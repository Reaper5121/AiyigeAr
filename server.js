const http = require("http");
const fs = require("fs/promises");
const path = require("path");
const { URL } = require("url");
require("./scripts/load-env")();

const rootDir = __dirname;
const stateFile = path.join(rootDir, "data", "admin-state.json");
const assetsDir = path.join(rootDir, "data", "assets");
const logoSourceFile = path.join(rootDir, "client logo.jpg");
const logoAssetFile = path.join(assetsDir, "logo.jpg");
const port = Number(process.env.PORT || 3000);
const supabaseUrl = String(process.env.SUPABASE_URL || "").replace(/\/$/, "");
const supabaseSecretKey = String(process.env.SUPABASE_SECRET_KEY || process.env.SUPABASE_SERVICE_ROLE_KEY || "");
let pendingStateWrite = Promise.resolve();
let pendingOrderTransaction = Promise.resolve();
let pendingAdminBootstrap = Promise.resolve();
const syncedRoomCaptureIds = new Set();
const pendingRoomCaptureSyncIds = new Set();
const stateEventClients = new Set();
const sharedStateType = "app_state";
const sharedStateExternalId = "shared";
let lastSharedStateRevision = "";
let sharedStatePollAt = 0;
let sharedStatePollPromise = null;
let sharedStateRealtimeSocket = null;
let sharedStateRealtimeRetry = null;
let sharedStateRealtimeRetryMs = 1_000;
let sharedStateSyncReady = false;

function publishStateChange() {
  const event = `event: state-changed\ndata: ${JSON.stringify({ updatedAt: new Date().toISOString() })}\n\n`;
  for (const response of stateEventClients) {
    if (response.destroyed || response.writableEnded) {
      stateEventClients.delete(response);
      continue;
    }
    response.write(event);
  }
}

function isSupabaseSyncEnabled() {
  return Boolean(supabaseUrl && supabaseSecretKey);
}

if (!isSupabaseSyncEnabled()) {
  console.warn("Supabase sync is disabled. Set SUPABASE_URL and SUPABASE_SECRET_KEY in the root .env file.");
}

async function supabaseRequest(pathname, method, body) {
  if (!isSupabaseSyncEnabled()) return null;
  const headers = {
    apikey: supabaseSecretKey,
    "Content-Type": "application/json",
    Prefer: "resolution=merge-duplicates,return=representation"
  };
  // Current Supabase secret keys are API keys, not JWTs. Only legacy
  // service_role JWTs belong in Authorization: Bearer.
  if (!supabaseSecretKey.startsWith("sb_secret_")) {
    headers.Authorization = `Bearer ${supabaseSecretKey}`;
  }
  const response = await fetch(`${supabaseUrl}/rest/v1/${pathname}`, {
    method,
    headers,
    body: body == null ? undefined : JSON.stringify(body)
  });
  const text = await response.text();
  if (!response.ok) throw new Error(`Supabase ${method} ${pathname} failed (${response.status}): ${text.slice(0, 400)}`);
  return text ? JSON.parse(text) : null;
}

function sharedStatePath() {
  return `project_data?data_type=eq.${encodeURIComponent(sharedStateType)}&external_id=eq.${encodeURIComponent(sharedStateExternalId)}&select=id,data,updated_at&limit=1`;
}

async function fetchSharedStateRow() {
  if (!sharedStateSyncReady) return null;
  const rows = await supabaseRequest(sharedStatePath(), "GET");
  return Array.isArray(rows) ? rows[0] || null : null;
}

async function saveSharedState(state) {
  if (!isSupabaseSyncEnabled() || !sharedStateSyncReady) return;
  const rows = await supabaseRequest("project_data?on_conflict=data_type,external_id", "POST", [{
    data_type: sharedStateType,
    external_id: sharedStateExternalId,
    data: state,
    updated_at: new Date().toISOString()
  }]);
  const row = Array.isArray(rows) ? rows[0] : null;
  if (row?.updated_at) lastSharedStateRevision = String(row.updated_at);
}

async function applySharedStateRow(row, { publish = true } = {}) {
  if (!row?.data || typeof row.data !== "object" || Array.isArray(row.data)) return false;
  const revision = String(row.updated_at || "");
  if (revision && revision === lastSharedStateRevision) return false;
  const state = JSON.parse(JSON.stringify(row.data));
  if (["inventory", "orders", "payments", "customers"].some(key => !Array.isArray(state[key]))) {
    console.warn("Supabase shared state row is missing required collections; keeping the current state.");
    return false;
  }
  if (Array.isArray(state.users)) {
    state.users = state.users.filter(user => ["admin", "sales_staff", "sales_manager", "inventory_staff", "owner"].includes(String(user?.role || "").toLowerCase()));
  }
  const normalizedState = await normalizeInventoryAssets(state);
  await normalizeRoomCaptureAssets(normalizedState);
  await fs.mkdir(path.dirname(stateFile), { recursive: true });
  const temporaryStateFile = `${stateFile}.${process.pid}.remote.tmp`;
  await fs.writeFile(temporaryStateFile, JSON.stringify(normalizedState), "utf8");
  await fs.rename(temporaryStateFile, stateFile);
  if (revision) lastSharedStateRevision = revision;
  if (publish) publishStateChange();
  return true;
}

async function refreshSharedStateFromSupabase() {
  if (!isSupabaseSyncEnabled() || !sharedStateSyncReady) return false;
  if (sharedStatePollPromise) return sharedStatePollPromise;
  if (Date.now() - sharedStatePollAt < 2_000) return false;
  sharedStatePollAt = Date.now();
  sharedStatePollPromise = (async () => {
    try {
      const row = await fetchSharedStateRow();
      return row ? await applySharedStateRow(row) : false;
    } catch (error) {
      console.warn(`Supabase shared-state refresh failed: ${error.message}`);
      return false;
    } finally {
      sharedStatePollPromise = null;
    }
  })();
  return sharedStatePollPromise;
}

function connectSharedStateRealtime() {
  if (!isSupabaseSyncEnabled() || !sharedStateSyncReady || typeof WebSocket === "undefined" || sharedStateRealtimeSocket) return;
  const socketUrl = new URL(`${supabaseUrl.replace(/^https:/, "wss:")}/realtime/v1/websocket`);
  socketUrl.searchParams.set("apikey", supabaseSecretKey);
  socketUrl.searchParams.set("vsn", "1.0.0");
  let joined = false;
  let heartbeat;
  try {
    const socket = new WebSocket(socketUrl);
    sharedStateRealtimeSocket = socket;
    socket.addEventListener("open", () => {
      socket.send(JSON.stringify({
        topic: "realtime:public:project_data",
        event: "phx_join",
        ref: "1",
        payload: {
          config: {
            broadcast: { ack: false, self: false },
            presence: { key: "" },
            postgres_changes: [{
              event: "*",
              schema: "public",
              table: "project_data",
              filter: `data_type=eq.${sharedStateType}`,
              select: ["id", "updated_at"]
            }]
          }
        }
      }));
      heartbeat = setInterval(() => {
        if (socket.readyState === WebSocket.OPEN) {
          socket.send(JSON.stringify({ topic: "phoenix", event: "heartbeat", payload: {}, ref: String(Date.now()) }));
        }
      }, 25_000);
    });
    socket.addEventListener("message", event => {
      let message;
      try { message = JSON.parse(String(event.data)); } catch { return; }
      if (message.event === "phx_reply" && message.payload?.status === "ok") {
        if (!joined) {
          joined = true;
          sharedStateRealtimeRetryMs = 1_000;
          console.log("Supabase Realtime connected for shared app data.");
        }
        return;
      }
      if (message.event === "phx_reply" && message.payload?.status === "error") {
        console.warn(`Supabase Realtime subscription rejected: ${message.payload?.response?.reason || "check the Realtime publication migration"}`);
        return;
      }
      if (message.event === "postgres_changes" || message.event === "system") {
        void refreshSharedStateFromSupabase();
      }
    });
    const scheduleReconnect = () => {
      clearInterval(heartbeat);
      if (sharedStateRealtimeSocket === socket) sharedStateRealtimeSocket = null;
      if (sharedStateRealtimeRetry) return;
      const delay = sharedStateRealtimeRetryMs;
      sharedStateRealtimeRetryMs = Math.min(sharedStateRealtimeRetryMs * 2, 30_000);
      sharedStateRealtimeRetry = setTimeout(() => {
        sharedStateRealtimeRetry = null;
        connectSharedStateRealtime();
      }, delay);
      if (joined) console.warn("Supabase Realtime disconnected; reconnecting.");
    };
    socket.addEventListener("close", scheduleReconnect, { once: true });
    socket.addEventListener("error", () => socket.close(), { once: true });
  } catch (error) {
    sharedStateRealtimeSocket = null;
    console.warn(`Supabase Realtime could not start: ${error.message}`);
  }
}

async function verifyMobileCustomer(req, requestedEmail) {
  if (!isSupabaseSyncEnabled()) return;
  const token = /^Bearer\s+(.+)$/i.exec(String(req.headers.authorization || ""))?.[1]?.trim();
  if (!token) {
    const error = new Error("Sign in again to verify your account before checkout.");
    error.statusCode = 401;
    throw error;
  }

  let response;
  try {
    response = await fetch(`${supabaseUrl}/auth/v1/user`, {
      headers: { apikey: supabaseSecretKey, Authorization: `Bearer ${token}` }
    });
  } catch {
    const error = new Error("Account verification is temporarily unavailable. Please try again.");
    error.statusCode = 503;
    throw error;
  }
  if (!response.ok) {
    const isUnauthorized = response.status === 401 || response.status === 403;
    const error = new Error(isUnauthorized
      ? "Your sign-in session expired. Sign in again before checkout."
      : "Account verification is temporarily unavailable. Please try again.");
    error.statusCode = isUnauthorized ? 401 : 503;
    throw error;
  }
  const user = await response.json().catch(() => null);
  if (!String(user?.email || "").trim().toLowerCase() ||
      String(user.email).trim().toLowerCase() !== String(requestedEmail || "").trim().toLowerCase()) {
    const error = new Error("The signed-in account does not match this customer account.");
    error.statusCode = 403;
    throw error;
  }
}

async function syncRoomCapturesToSupabase(roomCaptures = []) {
  if (!isSupabaseSyncEnabled()) return;
  for (const capture of Array.isArray(roomCaptures) ? roomCaptures : []) {
    const id = Number(capture.id);
    if (!Number.isFinite(id)) continue;
    await supabaseRequest("room_captures?on_conflict=id", "POST", [{
      id,
      customer_name: capture.customerName || "",
      product_id: capture.productId || null,
      product_name: capture.productName || "",
      finish: capture.finish || "",
      size: capture.size || "",
      quantity: Number(capture.quantity || 1),
      quote_number: capture.quoteNumber || null,
      quote_amount: Number(capture.quoteAmount || 0),
      captured_at: capture.capturedAt || new Date().toISOString(),
      image_asset: capture.imageAsset || capture.imageUrl || null,
      room_capture_url: capture.imageAsset || capture.imageUrl || null,
      uploaded_at: capture.imageAsset ? new Date().toISOString() : null,
      source_data: capture
    }]);
  }
}

async function syncOrderToSupabase({ order, quotation, payment }) {
  if (!isSupabaseSyncEnabled()) return;
  try {
    const remoteOrderRows = await supabaseRequest("orders?on_conflict=invoice", "POST", [{
      invoice: order.invoice,
      customer: order.customer,
      products: order.products,
      amount: order.amount,
      payment: order.payment,
      status: order.status,
      created_at: order.createdAt,
      room_capture_url: order.roomCaptureUrl || null,
      source_data: order
    }]);
    const remoteOrder = Array.isArray(remoteOrderRows) ? remoteOrderRows[0] : null;
    if (!remoteOrder?.id) throw new Error("Supabase did not return the order id.");

    const remoteQuotationRows = await supabaseRequest("quotations?on_conflict=quotation_number", "POST", [{
      quotation_number: quotation.quotation_no,
      quotation_no: quotation.quotation_no,
      order_id: remoteOrder.id,
      customer_name: quotation.customer.name,
      date_issued: quotation.created_at,
      prepared_by: "System",
      subtotal: quotation.subtotal,
      discount: 0,
      vat: quotation.vat,
      grand_total: quotation.total,
      items: quotation.items_snapshot,
      metadata: quotation.meta,
      source: quotation.source,
      items_snapshot: quotation.items_snapshot,
      delivery_fee: quotation.delivery_fee,
      total: quotation.total,
      created_at: quotation.created_at,
      payment_method: quotation.payment_method,
      payment_status: quotation.payment_status,
      courier: quotation.courier || null,
      room_capture_url: quotation.room_capture_url || null
    }]);
    const remoteQuotation = Array.isArray(remoteQuotationRows) ? remoteQuotationRows[0] : null;
    if (remoteQuotation?.id) {
      await supabaseRequest(`orders?id=eq.${encodeURIComponent(remoteOrder.id)}`, "PATCH", {
        quotation_id: remoteQuotation.id,
        room_capture_url: order.roomCaptureUrl || null
      });
    }

    const existingPayments = await supabaseRequest(`payments?invoice=eq.${encodeURIComponent(payment.invoice)}&select=id`, "GET");
    if (!Array.isArray(existingPayments) || !existingPayments.length) {
      await supabaseRequest("payments", "POST", [{
        invoice: payment.invoice,
        quote_number: payment.quoteNumber,
        customer: payment.customer,
        amount: payment.amount,
        method: payment.method,
        status: payment.status,
        payment_date: payment.date.slice(0, 10),
        reference: payment.reference,
        notes: "Automatic order payment record",
        source_data: payment
      }]);
    }
    console.log(`Supabase sync complete for ${quotation.quotation_no}.`);
  } catch (error) {
    console.warn(`Supabase sync skipped for ${quotation.quotation_no}: ${error.message}`);
  }
}

async function syncExistingStateToSupabase() {
  if (!isSupabaseSyncEnabled()) return;
  try {
    const state = await readState();
    await syncRoomCapturesToSupabase(state.roomCaptures);
    for (const capture of Array.isArray(state.roomCaptures) ? state.roomCaptures : []) {
      const id = Number(capture.id);
      if (Number.isFinite(id)) syncedRoomCaptureIds.add(id);
    }
    const orders = Array.isArray(state.orders) ? state.orders : [];
    for (const order of orders) {
      const quotation = (state.quotations || []).find(item => String(item.quotation_no) === String(order.quoteNumber || order.invoice));
      const payment = (state.payments || []).find(item => String(item.invoice) === String(order.invoice));
      if (quotation && payment) await syncOrderToSupabase({ order, quotation, payment });
    }
  } catch (error) {
    console.warn(`Initial Supabase sync skipped: ${error.message}`);
  }
}

function queueNewRoomCaptureSync(roomCaptures = []) {
  if (!isSupabaseSyncEnabled()) return;
  const pending = (Array.isArray(roomCaptures) ? roomCaptures : []).filter(capture => {
    const id = Number(capture.id);
    return Number.isFinite(id) && !syncedRoomCaptureIds.has(id) && !pendingRoomCaptureSyncIds.has(id);
  });
  if (!pending.length) return;
  pending.forEach(capture => pendingRoomCaptureSyncIds.add(Number(capture.id)));
  void syncRoomCapturesToSupabase(pending)
    .then(() => pending.forEach(capture => syncedRoomCaptureIds.add(Number(capture.id))))
    .catch(error => console.warn(`Room capture sync skipped: ${error.message}`))
    .finally(() => pending.forEach(capture => pendingRoomCaptureSyncIds.delete(Number(capture.id))));
}

const seedInventory = [
  {
    id: 1,
    name: "Seamless Ceiling Panel",
    productCode: "F-00",
    productName: "Seamless Ceiling Panel",
    categoryGroup: "UPVC Ceiling Panels",
    categoryName: "Seamless",
    productType: "Seamless",
    length: "2.4m",
    width: "1.2m",
    thickness: "",
    size: "2.4m x 1.2m",
    material: "UPVC",
    description: "Clean and modern ceiling finish for residential or commercial interiors.",
    quantity: 18,
    unitsPerBox: 20,
    pricePerMeter: 650,
    price: 650,
    availability: "In Stock",
    imageAsset: "/assets/panel-2.svg"
  },
  {
    id: 2,
    name: "Groove Ceiling Panel",
    productCode: "PG-05",
    productName: "Groove Ceiling Panel",
    categoryGroup: "UPVC Ceiling Panels",
    categoryName: "Groove",
    productType: "Groove",
    length: "2.4m",
    width: "1.2m",
    thickness: "",
    size: "2.4m x 1.2m",
    material: "UPVC",
    description: "Textured ceiling finish with a more defined panel line.",
    quantity: 14,
    unitsPerBox: 20,
    pricePerMeter: 680,
    price: 680,
    availability: "In Stock",
    imageAsset: "/assets/panel-3.svg"
  },
  {
    id: 3,
    name: "WPC Fluted Wall Cladding",
    productCode: "WCI-01",
    productName: "WPC Fluted Wall Cladding",
    categoryGroup: "WPC Wall Cladding (Indoor)",
    categoryName: "4 in 1 (Fluted)",
    productType: "4 in 1 (Fluted)",
    length: "2.9m",
    width: "0.2m",
    thickness: "",
    size: "2.9m x 0.2m",
    material: "WPC",
    description: "Premium fluted panel for feature walls and business-grade interiors.",
    quantity: 9,
    unitsPerBox: 20,
    pricePerMeter: 980,
    price: 980,
    availability: "In Stock",
    imageAsset: "/assets/panel-1781513382723-6fvny0.webp"
  },
  {
    id: 5,
    name: "Wall Board Accent Panel",
    productCode: "OP-02",
    productName: "Wall Board Accent Panel",
    categoryGroup: "Other Products",
    categoryName: "Wall Board",
    productType: "Wall Board",
    length: "2.4m",
    width: "1.2m",
    thickness: "",
    size: "2.4m x 1.2m",
    material: "Composite",
    description: "Versatile accent board for renovation and design upgrades.",
    quantity: 12,
    unitsPerBox: 20,
    pricePerMeter: 850,
    price: 850,
    availability: "In Stock",
    imageAsset: "/assets/panel-1782224742423-1782224742432-yvpsq0.png"
  }
];

const defaultState = {
  inventory: seedInventory,
  orders: [],
  sales: [],
  payments: [],
  quotations: [],
  notifications: [],
  paymentRequests: [],
  customers: [],
  users: [],
  quotation: [],
  roomCaptures: [],
  settings: {
    requireStaffLogin: true,
    themeMode: "light",
    paymentModes: ["Cash", "GCash", "Bank Transfer"],
    business: {
      name: "Aiyige Hardware & Construction",
      address: "",
      contact: "",
      terms: "Quotation is prepared before order confirmation. Delivery is arranged through the customer's selected courier."
    }
  }
};

function extFromMime(mimeType) {
  const map = {
    "image/jpeg": ".jpg",
    "image/jpg": ".jpg",
    "image/png": ".png",
    "image/gif": ".gif",
    "image/webp": ".webp",
    "image/svg+xml": ".svg",
    "model/gltf-binary": ".glb",
    "model/gltf+json": ".gltf",
    "application/octet-stream": ".glb"
  };
  return map[mimeType.toLowerCase()] || ".png";
}

function parseDataUrl(dataUrl) {
  const match = /^data:([^;]+);base64,(.+)$/.exec(String(dataUrl || ""));
  if (!match) return null;
  return {
    mimeType: match[1],
    buffer: Buffer.from(match[2], "base64")
  };
}

function slugify(value) {
  return String(value)
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "")
    .slice(0, 32) || "asset";
}

async function ensureAssets() {
  await fs.mkdir(assetsDir, { recursive: true });
  try {
    await fs.access(logoAssetFile);
  } catch {
    try {
      await fs.copyFile(logoSourceFile, logoAssetFile);
    } catch {
      // If the source logo is missing, we still continue; the app has alt text.
    }
  }
}

async function saveUploadedImage(dataUrl, hint) {
  const parsed = parseDataUrl(dataUrl);
  if (!parsed) return null;
  const extension = extFromMime(parsed.mimeType);
  const filename = `${slugify(hint)}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}${extension}`;
  const filePath = path.join(assetsDir, filename);
  await fs.mkdir(assetsDir, { recursive: true });
  await fs.writeFile(filePath, parsed.buffer);
  return `/assets/${filename}`;
}

async function saveUploadedModel(dataUrl, hint) {
  const parsed = parseDataUrl(dataUrl);
  if (!parsed) return null;
  const extension = parsed.mimeType.toLowerCase() === "model/gltf+json" ? ".gltf" : ".glb";
  const filename = `${slugify(hint)}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}${extension}`;
  const filePath = path.join(assetsDir, filename);
  await fs.mkdir(assetsDir, { recursive: true });
  await fs.writeFile(filePath, parsed.buffer);
  return `/assets/${filename}`;
}

async function normalizeRecordImage(record, hint) {
  const source = record.imageData || record.imageAsset || record.imageUrl || "";
  if (String(source).startsWith("data:")) {
    const assetPath = await saveUploadedImage(source, hint);
    if (assetPath) {
      record.imageAsset = assetPath;
    }
  } else if (String(source).startsWith("/assets/") || String(source).startsWith("http")) {
    record.imageAsset = source;
  }
  delete record.imageData;
  delete record.imageUrl;
}

async function normalizeRecordModel(record, hint) {
  const source = record.modelData || record.modelAsset || record.modelUrl || "";
  if (String(source).startsWith("data:")) {
    const assetPath = await saveUploadedModel(source, hint);
    if (assetPath) record.modelAsset = assetPath;
  } else if (String(source).startsWith("/assets/") || String(source).startsWith("http")) {
    record.modelAsset = source;
  }
  delete record.modelData;
  delete record.modelUrl;
}

async function normalizeInventoryAssets(state) {
  if (!state || !Array.isArray(state.inventory)) return state;
  await Promise.all(state.inventory.map(async item => {
    await normalizeRecordImage(item, `panel-${item.id || "item"}`);
    await normalizeRecordModel(item, `panel-model-${item.id || "item"}`);
  }));
  return state;
}

async function normalizeRoomCaptureAssets(state) {
  if (!state || !Array.isArray(state.roomCaptures)) return state;
  await Promise.all(state.roomCaptures.map(async capture => {
    await normalizeRecordImage(capture, `room-capture-${capture.id || "item"}`);
  }));
  return state;
}

const contentTypes = {
  ".html": "text/html; charset=utf-8",
  ".css": "text/css; charset=utf-8",
  ".js": "application/javascript; charset=utf-8",
  ".json": "application/json; charset=utf-8",
  ".glb": "model/gltf-binary",
  ".gltf": "model/gltf+json",
  ".jpg": "image/jpeg",
  ".jpeg": "image/jpeg",
  ".png": "image/png",
  ".svg": "image/svg+xml",
  ".ico": "image/x-icon"
};

function sendJson(res, statusCode, payload) {
  res.writeHead(statusCode, {
    "Content-Type": "application/json; charset=utf-8",
    "Cache-Control": "no-store",
    "Access-Control-Allow-Origin": "*"
  });
  res.end(JSON.stringify(payload));
}

async function ensureStateFile() {
  await ensureAssets();
  try {
    await fs.access(stateFile);
  } catch {
    await fs.mkdir(path.dirname(stateFile), { recursive: true });
    await fs.writeFile(stateFile, JSON.stringify(defaultState, null, 2), "utf8");
  }
}

function hydrateGeneratedQuotations(state) {
  if (!state || !Array.isArray(state.quotations)) state.quotations = [];
  if (state.quotations.length || !Array.isArray(state.savedQuotations)) return state;
  state.savedQuotations.forEach(saved => {
    const meta = saved.meta || {};
    const quotationNo = String(saved.number || meta.quotationNumber || "").trim();
    if (!quotationNo) return;
    const order = (state.orders || []).find(item => [item.id, item.invoice, item.quoteNumber]
      .some(reference => String(reference || "").trim() === quotationNo));
    const paymentRequest = (state.paymentRequests || []).find(item => (
      String(item.quote_number || item.quoteNumber || "").trim() === quotationNo
    ));
    const savedPaymentMethod = String(order?.payment || order?.method || "").trim();
    const requestPaymentMethod = String(paymentRequest?.payment_method || paymentRequest?.paymentMethod || "").trim();
    const items = (saved.items || []).map(item => ({
      productId: item.productId || item.id || null,
      panelModel: item.name || item.productName || "Panel",
      size: item.size || (item.lengthMeters ? `${item.lengthMeters} m` : ""),
      finish: item.finish || item.colour || "",
      quantity: Math.max(1, Number(item.qty || item.quantity || 1)),
      unitPrice: money(item.pricePerMeter ?? item.price ?? 0),
      lineTotal: money(item.lineTotal || 0)
    }));
    const totals = saved.totals || {};
    const subtotal = money(totals.subtotal);
    const vat = money(totals.vat);
    const total = money(totals.grandTotal || (subtotal + vat));
    state.quotations.push({
      id: `LEGACY-${quotationNo}`,
      quotation_no: quotationNo,
      order_id: order ? (order.id || order.invoice || null) : null,
      customer_confirmed_at: order?.createdAt || order?.created_at || null,
      customer_id: meta.emailAddress || meta.customerName || null,
      source: String(meta.source || "CATALOG").toUpperCase() === "AR" ? "AR" : "CATALOG",
      items_snapshot: items,
      subtotal,
      delivery_fee: 0,
      vat,
      total,
      created_at: saved.savedAt || order?.createdAt || new Date().toISOString(),
      customer: {
        name: meta.customerName || order?.customer || "Customer",
        contact: meta.contactNumber || order?.customerPhone || "",
        email: meta.emailAddress || order?.customerEmail || "",
        deliveryAddress: meta.customerAddress || ""
      },
      courier: meta.courier || order?.courier || "",
      payment_method: ["", "quotation", "pending"].includes(savedPaymentMethod.toLowerCase())
        ? requestPaymentMethod
        : savedPaymentMethod,
      payment_status: order?.paymentStatus || "Payment pending",
      business: JSON.parse(JSON.stringify(getBusinessProfile(state))),
      terms: meta.terms || state.settings?.business?.terms || ""
    });
  });
  return state;
}

async function readState() {
  await ensureStateFile();
  await pendingStateWrite.catch(() => undefined);
  await refreshSharedStateFromSupabase();
  const raw = await fs.readFile(stateFile, "utf8");
  return hydrateGeneratedQuotations(JSON.parse(raw.replace(/^\uFEFF/, "")));
}

async function writeState(state) {
  const operation = pendingStateWrite.then(async () => {
    if (!state || typeof state !== "object" || Array.isArray(state)) {
      throw new Error("State payload must be an object.");
    }

    // Reject incomplete payloads instead of replacing a healthy state file
    // with `{}` after a client/network failure.
    const requiredCollections = ["inventory", "orders", "payments", "customers"];
    if (requiredCollections.some(key => !Array.isArray(state[key]))) {
      throw new Error("State payload is missing required collections.");
    }

    await fs.mkdir(path.dirname(stateFile), { recursive: true });
    const sanitizedState = JSON.parse(JSON.stringify(state));
    if (Array.isArray(sanitizedState.users)) {
      sanitizedState.users = sanitizedState.users.filter(user => ["admin", "sales_staff", "sales_manager", "inventory_staff", "owner"].includes(String(user?.role || "").toLowerCase()));
    }
    const normalizedState = await normalizeInventoryAssets(sanitizedState);
    await normalizeRoomCaptureAssets(normalizedState);
    const temporaryStateFile = `${stateFile}.${process.pid}.tmp`;

    // Write beside the real file, then replace it atomically. This prevents a
    // partially-written JSON file if the process or machine stops mid-save.
    await fs.writeFile(temporaryStateFile, JSON.stringify(normalizedState), "utf8");
    await fs.rename(temporaryStateFile, stateFile);
    try {
      await saveSharedState(normalizedState);
    } catch (error) {
      console.warn(`Supabase shared-state save failed; local changes were retained: ${error.message}`);
    }
    publishStateChange();
    queueNewRoomCaptureSync(normalizedState.roomCaptures);
    return normalizedState;
  });
  pendingStateWrite = operation.catch(() => undefined);
  return operation;
}

function getContentType(filePath) {
  return contentTypes[path.extname(filePath).toLowerCase()] || "application/octet-stream";
}

async function serveStatic(res, filePath) {
  try {
    const data = await fs.readFile(filePath);
    res.writeHead(200, {
      "Content-Type": getContentType(filePath),
      "Cache-Control": "no-store",
      // The Android WebView loads GLBs from the backend while the page itself
      // uses the appassets.androidplatform.net origin.
      "Access-Control-Allow-Origin": "*"
    });
    res.end(data);
  } catch {
    res.writeHead(404, { "Content-Type": "text/plain; charset=utf-8" });
    res.end("Not found");
  }
}

async function serveAsset(res, requestPath) {
  const relativePath = requestPath.replace(/^\/assets\//, "");
  const filePath = path.normalize(path.join(assetsDir, relativePath));
  if (!filePath.startsWith(assetsDir)) {
    res.writeHead(403, { "Content-Type": "text/plain; charset=utf-8" });
    res.end("Forbidden");
    return true;
  }
  try {
    const data = await fs.readFile(filePath);
    res.writeHead(200, {
      "Content-Type": getContentType(filePath),
      "Cache-Control": "no-store"
    });
    res.end(data);
  } catch {
    res.writeHead(404, { "Content-Type": "text/plain; charset=utf-8" });
    res.end("Not found");
  }
  return true;
}

function readJsonBody(req) {
  return new Promise((resolve, reject) => {
    let body = "";
    req.on("data", chunk => {
      body += chunk;
      if (body.length > 10_000_000) {
        req.destroy();
        reject(new Error("Request body is too large."));
      }
    });
    req.on("end", () => {
      try {
        resolve(JSON.parse(body || "{}"));
      } catch {
        reject(new Error("Invalid JSON payload."));
      }
    });
    req.on("error", reject);
  });
}

function money(value) {
  const number = Number(value);
  return Number.isFinite(number) ? Math.round(number * 100) / 100 : 0;
}

function parsePanelLengthMeters(value) {
  const text = String(value || "").trim();
  const match = text.match(/\d+(?:\.\d+)?/);
  if (!match) return 1;
  const numericLength = Number(match[0]);
  const afterFirstNumber = text.slice((match.index || 0) + match[0].length);
  const unit = afterFirstNumber.match(/^\s*(mm|cm|m|ft|feet|inches?|in)\b/i)?.[1]?.toLowerCase()
    || text.match(/\b(mm|cm|m|ft|feet|inches?|in)\b/i)?.[1]?.toLowerCase();
  const meters = unit === "mm" ? numericLength / 1000
    : unit === "cm" ? numericLength / 100
      : unit === "ft" || unit === "feet" ? numericLength * 0.3048
        : unit === "in" || unit === "inch" || unit === "inches" ? numericLength * 0.0254
          : numericLength;
  return Number.isFinite(meters) && meters > 0 ? meters : 1;
}

function getBusinessProfile(state) {
  const business = state?.settings?.business || {};
  const savedName = String(business.name || "").trim();
  return {
    ...business,
    name: !savedName || savedName.toLowerCase() === "ar panels"
      ? "Aiyige Hardware & Construction"
      : savedName
  };
}

function makeQuotationNumber(state, year) {
  const settings = state.settings || (state.settings = {});
  const next = Math.max(1, Number(settings.quotationSequence || 1));
  settings.quotationSequence = next + 1;
  return `QT-${year}-${String(next).padStart(3, "0")}`;
}

function resolveInventoryPriceUnit(item = {}) {
  const explicit = String(item.priceUnit || "").trim().toLowerCase();
  if (explicit === "meter" || explicit === "piece") return explicit;
  const code = String(item.productCode || item.code || item.panelModel || item.name || "").trim().toUpperCase();
  return code.startsWith("LJS-") ? "meter" : "piece";
}

function normalizeOrderItems(items, inventory = [], { useSnapshotPrice = false } = {}) {
  const addOnPrices = { "Fixings Kit": 18, "Pro Adhesive": 12, "Edge Trim": 24 };
  return (Array.isArray(items) ? items : []).map(item => {
    const quantity = Number(item.quantity ?? item.qty ?? 1);
    if (!Number.isInteger(quantity) || quantity < 1 || quantity > 10_000) {
      throw new Error("Each item quantity must be between 1 and 10,000.");
    }
    const productId = item.productId ?? item.id ?? null;
    const product = useSnapshotPrice
      ? null
      : inventory.find(candidate => String(candidate.id) === String(productId));
    if (!useSnapshotPrice && !product) throw new Error("A selected product is no longer available. Refresh the catalog and try again.");
    const availableQuantity = Number(product?.quantity);
    if (Number.isFinite(availableQuantity) && quantity > availableQuantity) {
      throw new Error("There is not enough stock for one or more selected items. Update the cart and try again.");
    }

    const customization = item.customization && typeof item.customization === "object" ? item.customization : null;
    const addOns = customization?.addOns == null ? [] : customization.addOns;
    if (!Array.isArray(addOns) || addOns.some(name => !Object.prototype.hasOwnProperty.call(addOnPrices, String(name)))) {
      throw new Error("A selected customization add-on is not available.");
    }
    const size = String(customization?.size || item.size || product?.size || "").trim().slice(0, 100);
    const finish = String(customization?.finish ?? item.finish ?? product?.material ?? "").trim().slice(0, 80);
    const colour = String(customization?.colour ?? item.colour ?? "").trim().slice(0, 80);
    const notes = String(customization?.notes ?? "").trim().slice(0, 500);
    const unitPrice = useSnapshotPrice
      ? money(item.unitPrice ?? item.price ?? item.pricePerMeter)
      : money(product.pricePerMeter ?? product.price ?? 0) +
        (customization && size.includes("300") ? 12 : 0) +
        addOns.reduce((sum, name) => sum + addOnPrices[name], 0);
    const priceUnit = resolveInventoryPriceUnit(item.priceUnit ? item : (product || item));
    const lengthMeters = Number(item.lengthMeters) > 0
      ? money(item.lengthMeters)
      : parsePanelLengthMeters(size);
    const panelModel = String(product?.name ?? item.panelModel ?? item.name ?? item.productName ?? "Panel").trim().slice(0, 160);
    if (!panelModel || !Number.isFinite(unitPrice) || unitPrice < 0) {
      throw new Error("A selected product has invalid quotation details.");
    }
    return {
      productId,
      panelModel,
      size,
      finish,
      colour,
      customization: customization ? { size, finish, colour, addOns: [...addOns], notes } : null,
      quantity,
      unitPrice,
      priceUnit,
      lengthMeters,
      lineTotal: money(unitPrice * (priceUnit === "meter" ? lengthMeters : 1) * quantity)
    };
  });
}

async function createOrderTransaction(payload) {
  const state = await readState();
  const requestedQuotationNo = String(payload.quotationNumber || "").trim();
  if (!requestedQuotationNo) {
    throw new Error("Confirm a prepared quotation before submitting an order.");
  }
  const preparedQuotation = (state.quotations || []).find(item => String(item.quotation_no) === requestedQuotationNo);
  if (!preparedQuotation) {
    throw new Error("The quotation is no longer available. Please request a new quotation.");
  }
  const customer = payload.customer || {};
  if (String(preparedQuotation.customer?.email || "").toLowerCase() !== String(customer.email || "").toLowerCase()) {
    throw new Error("This quotation belongs to a different customer account.");
  }
  if (!String(preparedQuotation.customer?.deliveryAddress || "").trim()) {
    throw new Error("Add a delivery address before submitting the order.");
  }
  const requestedPaymentMethod = String(payload.paymentMethod || "").trim();
  const availablePaymentMethods = Array.isArray(state.settings?.paymentModes) && state.settings.paymentModes.length
    ? state.settings.paymentModes
    : ["Cash", "GCash", "Bank Transfer"];
  const paymentMethod = availablePaymentMethods.find(method => String(method).trim().toLowerCase() === requestedPaymentMethod.toLowerCase());
  if (!paymentMethod) throw new Error("Choose an available payment method before submitting the order.");
  const courier = String(payload.courier || "").trim();
  if (!["J&T", "Lalamove"].some(option => option.toLowerCase() === courier.toLowerCase())) {
    throw new Error("Choose J&T or Lalamove before submitting the order.");
  }
  if (preparedQuotation.order_id) {
    const existingOrder = (state.orders || []).find(item => String(item.id) === String(preparedQuotation.order_id));
    const existingPayment = (state.payments || []).find(item => String(item.orderId) === String(preparedQuotation.order_id));
    if (existingOrder && existingPayment) {
      return {
        order: existingOrder,
        quotation: preparedQuotation,
        payment: existingPayment,
        notification: (state.notifications || []).find(item => String(item.orderId) === String(preparedQuotation.order_id)) || null
      };
    }
    throw new Error("This quotation has already been submitted.");
  }
  const items = normalizeOrderItems(preparedQuotation.items_snapshot, [], { useSnapshotPrice: true });
  if (!items.length) throw new Error("At least one order item is required.");

  const now = new Date();
  const createdAt = now.toISOString();
  const quotationNo = preparedQuotation.quotation_no;
  const orderId = `ORD-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
  const subtotal = money(preparedQuotation.subtotal);
  const deliveryFee = 0;
  const vat = money(preparedQuotation.vat);
  const total = money(Math.max(0, money(preparedQuotation.total) - money(preparedQuotation.delivery_fee)));
  const paymentStatus = "Payment pending";
  const source = preparedQuotation.source;
  const roomCaptureUrl = String(preparedQuotation.room_capture_url || "").trim();
  const quotation = preparedQuotation;
  quotation.order_id = orderId;
  quotation.delivery_fee = deliveryFee;
  quotation.total = total;
  quotation.customer_confirmed_at = createdAt;
  quotation.courier = courier;
  quotation.payment_method = paymentMethod;
  quotation.payment_status = paymentStatus;
  quotation.status = "Awaiting payment";
  quotation.meta = {
    ...(quotation.meta || {}),
    courier,
    paymentMethod,
    paymentStatus
  };
  const order = {
    id: orderId,
    invoice: orderId,
    orderNumber: orderId,
    quoteNumber: quotationNo,
    customer: quotation.customer.name,
    customerEmail: quotation.customer.email,
    customerPhone: quotation.customer.contact,
    deliveryAddress: quotation.customer.deliveryAddress,
    deliveryRecipientName: quotation.customer.deliveryRecipientName || "",
    deliveryRecipientPhone: quotation.customer.deliveryRecipientPhone || "",
    products: items.map(item => `${item.panelModel} x${item.quantity}`).join(", "),
    quotationItems: quotation.items_snapshot,
    amount: total,
    subtotal,
    deliveryCost: deliveryFee,
    vat,
    payment: paymentMethod,
    method: paymentMethod,
    paymentStatus,
    status: "Awaiting payment",
    deliveryStatus: "Pending payment",
    courier: quotation.courier,
    trackingNumber: "",
    source,
    roomCaptureUrl,
    quotationId: quotation.id,
    createdAt
  };
  const payment = {
    id: `PAY-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
    orderId,
    invoice: orderId,
    quoteNumber: quotationNo,
    customer: quotation.customer.name,
    customerEmail: quotation.customer.email,
    amount: total,
    method: paymentMethod,
    status: paymentStatus,
    reference: String(payload.paymentReference || ""),
    screenshot: String(payload.paymentScreenshot || ""),
    date: createdAt,
    createdAt
  };
  const notification = {
    id: `NOT-${Date.now()}`,
    customerEmail: quotation.customer.email,
    title: "Payment awaiting confirmation",
    message: `Quotation ${quotationNo} is confirmed. Staff will prepare the order after full payment is recorded.`,
    quoteNumber: quotationNo,
    orderId,
    createdAt
  };

  state.orders = Array.isArray(state.orders) ? state.orders : [];
  state.payments = Array.isArray(state.payments) ? state.payments : [];
  state.quotations = Array.isArray(state.quotations) ? state.quotations : [];
  state.notifications = Array.isArray(state.notifications) ? state.notifications : [];
  delete quotation.customer_checkout_status;
  delete quotation.checkout_updated_at;
  state.notifications = state.notifications.filter(item => !(
    String(item.quoteNumber || item.quote_number || "").trim() === quotationNo &&
    String(item.customerEmail || item.customer_email || "").trim().toLowerCase() === String(quotation.customer.email || "").trim().toLowerCase() &&
    item.kind === "unfinished_quotation"
  ));
  state.orders.unshift(order);
  state.payments.unshift(payment);
  state.notifications.unshift(notification);
  await writeState(state);
  await syncOrderToSupabase({ order, quotation, payment });
  return { order, quotation, payment, notification };
}

async function prepareQuotationTransaction(payload) {
  const state = await readState();
  const requestId = String(payload.requestId || "").trim();
  const customer = payload.customer || {};
  const customerEmail = String(customer.email || "").trim().toLowerCase();
  if (!customerEmail) throw new Error("A customer email is required to prepare a quotation.");

  const existing = requestId
    ? (state.quotations || []).find(item => item.request_id === requestId && String(item.customer?.email || "").toLowerCase() === customerEmail)
    : null;
  if (existing) return { quotation: existing };

  const items = normalizeOrderItems(payload.items, state.inventory || []);
  if (!items.length) throw new Error("At least one quotation item is required.");

  const createdAt = new Date().toISOString();
  const subtotal = money(items.reduce((sum, item) => sum + item.lineTotal, 0));
  // Courier booking is handled by the customer outside this system, so no
  // external delivery charge is included in the customer quotation.
  const deliveryFee = 0;
  const vat = 0;
  const total = money(subtotal + deliveryFee + vat);
  const quotationNo = makeQuotationNumber(state, new Date(createdAt).getUTCFullYear());
  const source = String(payload.source || "CATALOG").toUpperCase() === "AR" ? "AR" : "CATALOG";
  const customerSnapshot = {
    name: String(customer.name || "Customer").trim(),
    contact: String(customer.contact || customer.contactNumber || "").trim(),
    email: customerEmail,
    deliveryAddress: String(customer.deliveryAddress || customer.address || "").trim(),
    deliveryRecipientName: String(customer.deliveryRecipientName || "").trim().slice(0, 160),
    deliveryRecipientPhone: String(customer.deliveryRecipientPhone || "").trim().slice(0, 40)
  };
  const terms = String(state.settings?.business?.terms || "");
  const business = getBusinessProfile(state);
  const quotation = {
    id: `QUO-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
    request_id: requestId,
    quotation_no: quotationNo,
    order_id: null,
    customer_id: customer.id || customerEmail || customerSnapshot.name || null,
    source,
    items_snapshot: JSON.parse(JSON.stringify(items)),
    subtotal,
    delivery_fee: deliveryFee,
    vat,
    total,
    created_at: createdAt,
    customer: customerSnapshot,
    courier: "",
    payment_method: "",
    payment_status: "Not submitted",
    business: JSON.parse(JSON.stringify(business)),
    terms,
    room_capture_url: String(payload.roomCaptureUrl || "").trim(),
    status: "Quotation ready",
    number: quotationNo,
    savedAt: createdAt,
    items: JSON.parse(JSON.stringify(items)),
    totals: { subtotal, deliveryFee, vat, grandTotal: total },
    meta: {
      customerName: customerSnapshot.name,
      contactNumber: customerSnapshot.contact,
      customerAddress: customerSnapshot.deliveryAddress,
      emailAddress: customerSnapshot.email,
      quotationNumber: quotationNo,
      dateIssued: createdAt.slice(0, 10),
      source,
      terms
    }
  };
  const notification = {
    id: `NOT-${Date.now()}`,
    customerEmail,
    title: "Quotation ready",
    message: `Your quotation ${quotationNo} is ready to review in the app.`,
    quoteNumber: quotationNo,
    createdAt
  };

  state.quotations = Array.isArray(state.quotations) ? state.quotations : [];
  state.notifications = Array.isArray(state.notifications) ? state.notifications : [];
  state.quotations.unshift(quotation);
  state.notifications.unshift(notification);
  await writeState(state);
  return { quotation, notification };
}

const server = http.createServer(async (req, res) => {
  const requestUrl = new URL(req.url, `http://${req.headers.host || "localhost"}`);

  if (requestUrl.pathname === "/api/events" && req.method === "GET") {
    res.writeHead(200, {
      "Content-Type": "text/event-stream; charset=utf-8",
      "Cache-Control": "no-cache, no-transform",
      Connection: "keep-alive",
      "Access-Control-Allow-Origin": "*",
      "X-Accel-Buffering": "no"
    });
    res.write(`event: connected\ndata: ${JSON.stringify({ connectedAt: new Date().toISOString() })}\n\n`);
    stateEventClients.add(res);
    const heartbeat = setInterval(() => {
      if (res.destroyed || res.writableEnded) {
        clearInterval(heartbeat);
        stateEventClients.delete(res);
        return;
      }
      res.write(": keep-alive\n\n");
    }, 25_000);
    req.on("close", () => {
      clearInterval(heartbeat);
      stateEventClients.delete(res);
    });
    return;
  }

  if (requestUrl.pathname === "/api/setup/first-admin" && req.method === "POST") {
    const operation = pendingAdminBootstrap.then(async () => {
      const payload = await readJsonBody(req);
      const state = await readState();
      if (Array.isArray(state.users) && state.users.length > 0) {
        return { status: 409, body: { error: "An administrator account is already configured." } };
      }

      const username = String(payload.username || "").trim();
      const displayName = String(payload.displayName || "").trim();
      const password = String(payload.password || "");
      if (!username || !displayName || password.length < 12) {
        return { status: 400, body: { error: "Enter a name, username, and password with at least 12 characters." } };
      }

      const admin = {
        id: Date.now(),
        username,
        password,
        role: "admin",
        displayName,
        reference: "ADM-001",
        status: "active"
      };
      state.users = [admin];
      await writeState(state);
      const { password: _password, ...safeAdmin } = admin;
      return { status: 201, body: { user: safeAdmin } };
    });
    pendingAdminBootstrap = operation.catch(() => undefined);
    try {
      const result = await operation;
      sendJson(res, result.status, result.body);
    } catch (error) {
      sendJson(res, 400, { error: error.message || "Unable to create the administrator account." });
    }
    return;
  }

  if (requestUrl.pathname === "/api/orders" && req.method === "POST") {
    try {
      const payload = await readJsonBody(req);
      await verifyMobileCustomer(req, payload.customer?.email);
      const operation = pendingOrderTransaction.then(() => createOrderTransaction(payload));
      pendingOrderTransaction = operation.catch(() => undefined);
      const result = await operation;
      sendJson(res, 201, result);
    } catch (error) {
      sendJson(res, error.statusCode || 400, { error: error.message || "Unable to create order." });
    }
    return;
  }

  if (requestUrl.pathname === "/api/quotations/prepare" && req.method === "POST") {
    try {
      const payload = await readJsonBody(req);
      await verifyMobileCustomer(req, payload.customer?.email);
      const operation = pendingOrderTransaction.then(() => prepareQuotationTransaction(payload));
      pendingOrderTransaction = operation.catch(() => undefined);
      const result = await operation;
      sendJson(res, 201, result);
    } catch (error) {
      sendJson(res, error.statusCode || 400, { error: error.message || "Unable to prepare quotation." });
    }
    return;
  }

  const quotationCheckoutMatch = requestUrl.pathname.match(/^\/api\/quotations\/([^/]+)\/checkout-state$/);
  if (quotationCheckoutMatch && req.method === "POST") {
    try {
      const payload = await readJsonBody(req);
      const quotationNo = decodeURIComponent(quotationCheckoutMatch[1]);
      const customerEmail = String(payload.email || "").trim().toLowerCase();
      await verifyMobileCustomer(req, customerEmail);
      const requestedStatus = String(payload.status || "").trim();
      if (!["unfinished", "in_progress"].includes(requestedStatus)) {
        sendJson(res, 400, { error: "Unsupported quotation checkout status." });
        return;
      }

      const operation = pendingOrderTransaction.then(async () => {
        const state = await readState();
        const quotation = (state.quotations || []).find(item => (
          String(item.quotation_no || "").trim() === quotationNo &&
          String(item.customer?.email || "").trim().toLowerCase() === customerEmail
        ));
        if (!quotation) {
          const error = new Error("Quotation not found for this customer.");
          error.statusCode = 404;
          throw error;
        }
        if (quotation.order_id || quotation.customer_confirmed_at) {
          const error = new Error("This quotation has already moved into order processing.");
          error.statusCode = 409;
          throw error;
        }

        quotation.customer_checkout_status = requestedStatus;
        quotation.checkout_updated_at = new Date().toISOString();
        state.notifications = Array.isArray(state.notifications) ? state.notifications : [];
        state.notifications = state.notifications.filter(item => !(
          String(item.quoteNumber || item.quote_number || "").trim() === quotationNo &&
          String(item.customerEmail || item.customer_email || "").trim().toLowerCase() === customerEmail &&
          item.kind === "unfinished_quotation"
        ));
        if (requestedStatus === "unfinished") {
          state.notifications.unshift({
            id: `NOT-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
            customerEmail,
            title: "Unfinished quotation",
            message: `Your quotation ${quotationNo} is unfinished. Return to your menu to continue checkout.`,
            quoteNumber: quotationNo,
            kind: "unfinished_quotation",
            createdAt: quotation.checkout_updated_at
          });
        }
        await writeState(state);
        return quotation;
      });
      pendingOrderTransaction = operation.catch(() => undefined);
      const quotation = await operation;
      sendJson(res, 200, { quotation });
    } catch (error) {
      sendJson(res, error.statusCode || 400, { error: error.message || "Unable to update quotation checkout status." });
    }
    return;
  }

  const customerQuotationMatch = requestUrl.pathname.match(/^\/api\/quotations\/([^/]+)$/);
  if (customerQuotationMatch && req.method === "DELETE") {
    try {
      const payload = await readJsonBody(req);
      const quotationNo = decodeURIComponent(customerQuotationMatch[1]);
      const customerEmail = String(payload.email || "").trim().toLowerCase();
      await verifyMobileCustomer(req, customerEmail);
      const operation = pendingOrderTransaction.then(async () => {
        const state = await readState();
        const quotationIndex = (state.quotations || []).findIndex(item => (
          String(item.quotation_no || "").trim() === quotationNo &&
          String(item.customer?.email || "").trim().toLowerCase() === customerEmail
        ));
        if (quotationIndex < 0) {
          const error = new Error("Quotation not found for this customer.");
          error.statusCode = 404;
          throw error;
        }
        const quotation = state.quotations[quotationIndex];
        if (quotation.customer_checkout_status !== "unfinished" || quotation.order_id || quotation.customer_confirmed_at) {
          const error = new Error("Only an unfinished quotation without an order can be deleted.");
          error.statusCode = 409;
          throw error;
        }
        state.quotations.splice(quotationIndex, 1);
        if (Array.isArray(state.savedQuotations)) {
          state.savedQuotations = state.savedQuotations.filter(item => (
            String(item.number || item.meta?.quotationNumber || "").trim() !== quotationNo
          ));
        }
        const notifications = Array.isArray(state.notifications) ? state.notifications : [];
        state.notifications = notifications.filter(item => (
          String(item.quoteNumber || item.quote_number || "").trim() !== quotationNo ||
          String(item.customerEmail || item.customer_email || "").trim().toLowerCase() !== customerEmail
        ));
        await writeState(state);
        return { quotationNumber: quotationNo };
      });
      pendingOrderTransaction = operation.catch(() => undefined);
      const result = await operation;
      sendJson(res, 200, result);
    } catch (error) {
      sendJson(res, error.statusCode || 400, { error: error.message || "Unable to delete unfinished quotation." });
    }
    return;
  }

  const quotationMatch = requestUrl.pathname.match(/^\/api\/orders\/([^/]+)\/quotation$/);
  if (quotationMatch && req.method === "GET") {
    try {
      const state = await readState();
      const quotation = (state.quotations || []).find(item => (
        String(item.order_id) === decodeURIComponent(quotationMatch[1]) ||
        String(item.quotation_no) === decodeURIComponent(quotationMatch[1])
      ));
      if (!quotation) {
        sendJson(res, 404, { error: "Quotation not found." });
        return;
      }
      sendJson(res, 200, quotation);
    } catch (error) {
      sendJson(res, 500, { error: "Unable to read quotation." });
    }
    return;
  }

  if (requestUrl.pathname === "/api/mobile/sync" && req.method === "GET") {
    try {
      const email = String(requestUrl.searchParams.get("email") || "").trim().toLowerCase();
      if (email) await verifyMobileCustomer(req, email);
      const state = await readState();
      const matchesEmail = record => String(record?.customerEmail || record?.customer_email || record?.customer?.email || "").trim().toLowerCase() === email;
      const customer = email
        ? (state.customers || []).find(record => String(record?.email || "").trim().toLowerCase() === email) || null
        : null;
      sendJson(res, 200, {
        updatedAt: (await fs.stat(stateFile)).mtime.toISOString(),
        inventory: state.inventory || [],
        paymentModes: state.settings?.paymentModes || [],
        customer,
        orders: email ? (state.orders || []).filter(matchesEmail) : [],
        quotations: email ? (state.quotations || []).filter(record => String(record.customer?.email || "").trim().toLowerCase() === email) : []
      });
    } catch (error) {
      sendJson(res, error.statusCode || 500, { error: error.message || "Unable to sync mobile data." });
    }
    return;
  }

  if (requestUrl.pathname === "/api/state") {
    if (req.method === "GET") {
      try {
        sendJson(res, 200, await readState());
      } catch (error) {
        sendJson(res, 500, { error: "Unable to read admin state." });
      }
      return;
    }

    if (req.method === "PUT") {
      try {
        const bodyChunks = [];
        let bodySize = 0;
        let bodyTooLarge = false;
        req.on("data", chunk => {
          bodySize += chunk.length;
          if (bodySize > 10_000_000) {
            bodyTooLarge = true;
            bodyChunks.length = 0;
            return;
          }
          if (!bodyTooLarge) bodyChunks.push(chunk);
        });
        req.on("end", async () => {
          if (bodyTooLarge) {
            sendJson(res, 413, { error: "State payload exceeds the 10 MB limit." });
            return;
          }
          try {
            const parsed = JSON.parse(Buffer.concat(bodyChunks).toString("utf8") || "{}");
            await writeState(parsed);
            sendJson(res, 200, { ok: true });
          } catch {
            sendJson(res, 400, { error: "Invalid state payload." });
          }
        });
      } catch {
        sendJson(res, 400, { error: "Invalid request." });
      }
      return;
    }

    if (req.method === "OPTIONS") {
      res.writeHead(204, {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "GET,PUT,OPTIONS",
        "Access-Control-Allow-Headers": "Content-Type"
      });
      res.end();
      return;
    }

    sendJson(res, 405, { error: "Method not allowed." });
    return;
  }

  if (requestUrl.pathname.startsWith("/assets/")) {
    await serveAsset(res, requestUrl.pathname);
    return;
  }

  if (requestUrl.pathname === "/model-viewer") {
    const model = requestUrl.searchParams.get("model") || "";
    const safeModel = model.startsWith("/") || model.startsWith("http://") || model.startsWith("https://")
      ? model
      : "";
    const escapedModel = safeModel
      .replace(/&/g, "&amp;")
      .replace(/"/g, "&quot;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;");
    res.writeHead(200, {
      "Content-Type": "text/html; charset=utf-8",
      "Cache-Control": "no-store",
      "Access-Control-Allow-Origin": "*"
    });
    res.end(`<!doctype html>
<html><head><meta name="viewport" content="width=device-width, initial-scale=1" />
<script type="module" src="/assets/model-viewer.min.js"></script>
<style>html,body{margin:0;width:100%;height:100%;overflow:hidden;background:transparent}model-viewer{width:100%;height:100%;background:transparent;--poster-color:transparent}</style>
</head><body><model-viewer id="panel-viewer" camera-controls auto-rotate rotation-per-second="18deg" shadow-intensity="0.8" exposure="1.05" interaction-prompt="none" reveal="eager"></model-viewer>
<script>
const viewer=document.getElementById("panel-viewer");
const modelSource="${escapedModel}";
viewer.addEventListener("load",()=>document.body.dataset.modelLoaded="true");
viewer.addEventListener("error",event=>{document.body.dataset.modelError="true";console.error("GLB load failed",event)});
customElements.whenDefined("model-viewer").then(()=>{
  viewer.removeAttribute("src");
  setTimeout(()=>viewer.setAttribute("src", modelSource), 250);
});
</script>
</body></html>`);
    return;
  }

  if (requestUrl.pathname === "/auth-callback") {
    res.writeHead(200, {
      "Content-Type": "text/html; charset=utf-8",
      "Cache-Control": "no-store"
    });
    res.end(`<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Returning to app</title>
  <script src="/auth-bridge.js" defer></script>
  <style>
    body {
      margin: 0;
      min-height: 100vh;
      display: grid;
      place-items: center;
      font-family: Arial, sans-serif;
      background: linear-gradient(180deg, #fff7eb, #f4efe7);
      color: #121826;
    }
    .card {
      background: rgba(255, 255, 255, 0.88);
      border-radius: 24px;
      padding: 24px 28px;
      box-shadow: 0 24px 60px rgba(0, 0, 0, 0.08);
      text-align: center;
      max-width: 320px;
    }
    .subtle {
      color: #667085;
      font-size: 14px;
      line-height: 1.5;
    }
  </style>
</head>
<body>
  <div class="card">
    <h1>Returning to the mobile app</h1>
    <p class="subtle">If the app does not open automatically, check that the customer app is installed and try the reset link again.</p>
  </div>
</body>
</html>`);
    return;
  }

  const pathname = requestUrl.pathname === "/" || requestUrl.pathname === "/admin"
    ? "/index.html"
    : requestUrl.pathname;
  const decodedPath = decodeURIComponent(pathname);
  const safePath = path.normalize(path.join(rootDir, decodedPath));

  if (!safePath.startsWith(rootDir)) {
    res.writeHead(403, { "Content-Type": "text/plain; charset=utf-8" });
    res.end("Forbidden");
    return;
  }

  await serveStatic(res, safePath);
});

server.on("error", error => {
  if (error.code === "EADDRINUSE") {
    console.log(`Admin backend is already running at http://localhost:${port}`);
    // The existing backend is already serving requests; do not start a
    // periodic keep-alive or refresh loop in this process.
    return;
  }
  throw error;
});

async function initializeSharedState() {
  if (!isSupabaseSyncEnabled()) return;
  try {
    await ensureStateFile();
    const readinessRows = await supabaseRequest(
      `project_data?data_type=eq.app_state_config&external_id=eq.shared&select=data&limit=1`,
      "GET"
    );
    const readiness = Array.isArray(readinessRows) ? readinessRows[0]?.data : null;
    if (Number(readiness?.schema_version) !== 1 || readiness?.private_state_row !== true) {
      console.warn("Shared Supabase sync is paused. Run supabase/20261110_shared_realtime_state.sql, then restart the backend.");
      return;
    }
    sharedStateSyncReady = true;
    const row = await fetchSharedStateRow();
    if (row) {
      await applySharedStateRow(row, { publish: false });
      console.log("Loaded shared app data from Supabase.");
    } else {
      const raw = await fs.readFile(stateFile, "utf8");
      const state = hydrateGeneratedQuotations(JSON.parse(raw.replace(/^\uFEFF/, "")));
      await saveSharedState(state);
      console.log("Initialized shared app data in Supabase from the current local state.");
    }
  } catch (error) {
    console.warn(`Supabase shared-state initialization failed: ${error.message}`);
  }
  connectSharedStateRealtime();
  setInterval(() => { void refreshSharedStateFromSupabase(); }, 10_000).unref();
}

server.listen(port, "0.0.0.0", () => {
  console.log(`Admin backend running at http://localhost:${port}`);
  void initializeSharedState().then(() => syncExistingStateToSupabase());
});
