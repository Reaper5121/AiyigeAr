const loadLocalEnvironment = require("./load-env");

loadLocalEnvironment();

const projectUrl = String(process.env.SUPABASE_URL || "").replace(/\/$/, "");
const apiKey = String(process.env.SUPABASE_SECRET_KEY || process.env.SUPABASE_SERVICE_ROLE_KEY || "");

const requiredColumns = {
  project_data: ["data_type", "external_id", "data", "updated_at"],
  orders: ["invoice", "customer", "products", "amount", "payment", "status", "created_at", "room_capture_url", "source_data", "quotation_id"],
  quotations: ["quotation_number", "quotation_no", "order_id", "customer_name", "date_issued", "prepared_by", "subtotal", "discount", "vat", "grand_total", "items", "metadata", "source", "items_snapshot", "delivery_fee", "total", "created_at", "payment_method", "payment_status", "courier", "room_capture_url"],
  payments: ["invoice", "quote_number", "customer", "amount", "method", "status", "payment_date", "reference", "notes", "source_data"],
  room_captures: ["id", "customer_name", "product_id", "product_name", "finish", "size", "quantity", "quote_number", "quote_amount", "captured_at", "image_asset", "room_capture_url", "uploaded_at", "source_data"],
  payment_requests: ["quote_number", "customer_name", "customer_email", "customer_phone", "item_name", "panels", "unit_price", "subtotal", "vat", "total_amount", "summary", "street_address", "city_province", "postal_code", "payment_method", "delivery_method", "room_capture_url", "notes", "status", "source", "created_at", "updated_at"]
};

function apiHeaders() {
  const headers = { apikey: apiKey };
  if (!apiKey.startsWith("sb_secret_")) headers.Authorization = `Bearer ${apiKey}`;
  return headers;
}

function missingColumnFromError(body) {
  const message = String(body?.message || "");
  return message.match(/column (?:[\w]+\.)?([\w]+) does not exist/i)?.[1]
    || message.match(/could not find the '([\w]+)' column/i)?.[1]
    || null;
}

async function checkTable(table, columns) {
  const remaining = new Set(columns);
  const missing = [];
  for (let attempt = 0; attempt <= columns.length; attempt += 1) {
    if (!remaining.size) break;
    const endpoint = new URL(`/rest/v1/${table}`, projectUrl);
    endpoint.searchParams.set("select", [...remaining].join(","));
    endpoint.searchParams.set("limit", "0");
    let response;
    try {
      response = await fetch(endpoint, { headers: apiHeaders() });
    } catch {
      return { table, missing, error: "connection failed" };
    }
    if (response.ok) return { table, missing, ready: missing.length === 0 };
    const body = await response.json().catch(() => ({}));
    const absent = missingColumnFromError(body);
    if (absent && remaining.delete(absent)) {
      missing.push(absent);
      continue;
    }
    return { table, missing, error: body.code === "42P01" ? "table not found" : `HTTP ${response.status}` };
  }
  return {
    table,
    missing,
    ready: remaining.size === 0 && missing.length === 0,
    error: remaining.size ? "schema check incomplete" : undefined
  };
}

async function checkSharedRealtime() {
  const endpoint = new URL("/rest/v1/project_data", projectUrl);
  endpoint.searchParams.set("data_type", "eq.app_state_config");
  endpoint.searchParams.set("external_id", "eq.shared");
  endpoint.searchParams.set("select", "data");
  endpoint.searchParams.set("limit", "1");
  try {
    const response = await fetch(endpoint, { headers: apiHeaders() });
    if (!response.ok) return { ready: false, message: `HTTP ${response.status}` };
    const rows = await response.json();
    const marker = rows[0]?.data;
    return Number(marker?.schema_version) === 1 && marker?.private_state_row === true
      ? { ready: true }
      : { ready: false, message: "run 20261110_shared_realtime_state.sql" };
  } catch {
    return { ready: false, message: "connection failed" };
  }
}

async function main() {
  if (!/^https:\/\/[\w-]+\.supabase\.co$/i.test(projectUrl) || !apiKey) {
    console.error("Set a valid SUPABASE_URL and backend Secret key in the root .env.");
    process.exitCode = 1;
    return;
  }

  const checks = await Promise.all(Object.entries(requiredColumns).map(([table, columns]) => checkTable(table, columns)));
  for (const result of checks) {
    if (result.ready) console.log(`${result.table}: ready`);
    else {
      console.log(`${result.table}: ${result.error || "missing columns"}`);
      if (result.missing.length) console.log(`  Missing: ${result.missing.join(", ")}`);
    }
  }

  const realtime = await checkSharedRealtime();
  console.log(`Shared Realtime snapshot: ${realtime.ready ? "ready" : realtime.message}`);
  if (!realtime.ready) checks.push({ ready: false });

  try {
    const response = await fetch(`${projectUrl}/auth/v1/settings`, { headers: apiHeaders() });
    console.log(`Supabase Auth: HTTP ${response.status}`);
    if (!response.ok) checks.push({ ready: false });
  } catch {
    console.log("Supabase Auth: connection failed");
    checks.push({ ready: false });
  }

  if (checks.some(result => !result.ready)) process.exitCode = 1;
}

main().catch(error => {
  console.error(`Supabase configuration check failed: ${error.message}`);
  process.exitCode = 1;
});
