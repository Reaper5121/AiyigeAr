import {
  demoState,
  findCatalogMatchForItem,
  findCatalogEntryForItem,
  findCatalogEntry,
  getCatalogEntryByCode,
  getProductDisplayCode,
  getProductDisplayName,
  resolveCatalogCategoryGroup
} from "./state.js";

export const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
export const salesBaseValues = [18, 22, 26, 29, 33, 38, 35, 41, 44, 48, 52, 57];

export function getLocale() {
  return demoState.settings?.language === "Filipino" ? "fil-PH" : "en-PH";
}

export function getCurrencyCode() {
  const currency = String(demoState.settings?.currency || "PHP").toUpperCase();
  return ["PHP", "USD", "SGD"].includes(currency) ? currency : "PHP";
}

export function getMonthNames(locale = getLocale()) {
  return Array.from({ length: 12 }, (_, monthIndex) => new Intl.DateTimeFormat(locale, { month: "long" }).format(new Date(2024, monthIndex, 1)));
}

export function getShortMonthNames(locale = getLocale()) {
  return Array.from({ length: 12 }, (_, monthIndex) => new Intl.DateTimeFormat(locale, { month: "short" }).format(new Date(2024, monthIndex, 1)));
}

export function getMeasurementUnit() {
  return demoState.settings?.unitOfMeasurement === "Feet" ? "Feet" : "Meters";
}

function formatFeet(value) {
  const rounded = Math.round(value * 10) / 10;
  return Number.isInteger(rounded) ? String(rounded) : rounded.toFixed(1).replace(/\.0$/, "");
}

function metersToFeet(value) {
  return value * 3.28084;
}

export function formatPeso(value) {
  const locale = getLocale();
  const currency = getCurrencyCode();
  return new Intl.NumberFormat(locale, {
    style: "currency",
    currency,
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  }).format(value);
}

export function getCurrencySymbol() {
  return formatPeso(0).replace(/0$/, "");
}

export function formatCurrencyK(value) {
  return `${getCurrencySymbol()}${value}k`;
}

export function parseMoney(value) {
  return Number(String(value).replace(/[^\d.-]/g, "")) || 0;
}

export function getCustomerSelectedPaymentMethod(...values) {
  const invalidMethods = new Set(["quotation", "pending", "not selected"]);
  return values.map(value => String(value || "").trim())
    .find(value => value && !invalidMethods.has(value.toLowerCase())) || "";
}

export function isOrderPaymentComplete(order) {
  if (!order) return false;
  const settledStatuses = new Set(["paid", "completed", "settled"]);
  if ([order.paymentStatus, order.payment_status, order.status]
    .some(status => settledStatuses.has(String(status || "").trim().toLowerCase()))) return true;

  const references = new Set([order.id, order.invoice, order.quoteNumber, order.quote_number]
    .map(value => String(value || "").trim()).filter(Boolean));
  const total = parseMoney(order.amount || order.totalAmount || order.total_amount || 0);
  if (!references.size || total <= 0) return false;
  const paid = (demoState.payments || []).reduce((sum, payment) => {
    const status = String(payment.status || "").trim().toLowerCase();
    const paymentReferences = [payment.orderId, payment.order_id, payment.invoice, payment.quoteNumber, payment.quote_number]
      .map(value => String(value || "").trim());
    if (!settledStatuses.has(status) && status !== "partially paid") return sum;
    if (!paymentReferences.some(reference => references.has(reference))) return sum;
    return sum + parseMoney(payment.amount || 0);
  }, 0);
  return paid >= total;
}

export function isOrderReadyForStaff(order) {
  return isOrderQuotationConfirmed(order) && isOrderPaymentComplete(order);
}

export function isOrderQuotationConfirmed(order) {
  if (!order) return false;
  const orderReferences = new Set([order.id, order.invoice, order.quoteNumber, order.quote_number]
    .map(value => String(value || "").trim()).filter(Boolean));
  const quoteNumber = String(order.quoteNumber || order.quote_number || order.invoice || "").trim();
  return (demoState.quotations || []).some(quotation => {
    const linkedOrder = String(quotation.order_id || quotation.orderId || "").trim();
    const matchesQuotation = String(quotation.quotation_no || quotation.quotationNumber || "").trim() === quoteNumber;
    return Boolean(linkedOrder && orderReferences.has(linkedOrder) && matchesQuotation);
  });
}

export function formatShortDate(value) {
  const date = value ? new Date(value) : new Date();
  if (Number.isNaN(date.getTime())) return "--";
  const year = String(date.getFullYear());
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const format = demoState.settings?.dateFormat === "DD/MM/YYYY" ? "DD/MM/YYYY" : "MM/DD/YYYY";
  return format === "DD/MM/YYYY" ? `${day}/${month}/${year}` : `${month}/${day}/${year}`;
}

export function addDays(value, days) {
  const date = value ? new Date(value) : new Date();
  if (Number.isNaN(date.getTime())) return new Date();
  const next = new Date(date);
  next.setDate(next.getDate() + days);
  return next;
}

export function pad2(value) {
  return String(value).padStart(2, "0");
}

export function getDaysInMonth(year, monthIndex) {
  return new Date(year, monthIndex + 1, 0).getDate();
}

export function getSalesValue(year, monthIndex) {
  const yearBoost = (year - 2024) * 4;
  const seasonalBoost = [0, 2, 1, 3, 2, 4, 3, 5, 4, 5, 6, 7][monthIndex] || 0;
  return Math.max(0, salesBaseValues[monthIndex] + yearBoost + seasonalBoost);
}

export function getSalesWindow(monthIndex, year, span = 6) {
  const values = [];
  for (let offset = span - 1; offset >= 0; offset -= 1) {
    const current = new Date(Number(year), Number(monthIndex), 1);
    current.setMonth(current.getMonth() - offset);
    const currentYear = current.getFullYear();
    const currentMonth = current.getMonth();
    const shortMonth = getShortMonthNames()[currentMonth];
    values.push({
      label: `${shortMonth} '${String(currentYear).slice(-2)}`,
      year: currentYear,
      value: getSalesValue(currentYear, currentMonth)
    });
  }
  return values;
}

export function formatMeasurement(value) {
  const text = String(value ?? "").trim();
  if (!text) return "--";
  if (getMeasurementUnit() !== "Feet") {
    return text;
  }

  return text.replace(/(\d+(?:\.\d+)?)\s*m\b/gi, (_, meters) => `${formatFeet(metersToFeet(Number(meters)))} ft`);
}

export function parsePanelDimensions(value = "") {
  const parts = String(value || "")
    .split(/[x\u00D7]/i)
    .map(part => part.trim())
    .filter(Boolean);

  return {
    length: parts[0] || "",
    width: parts[1] || "",
    thickness: parts[2] || ""
  };
}

export function formatPanelDimensions(item = {}) {
  const length = String(item.length || "").trim();
  const width = String(item.width || "").trim();
  const thickness = String(item.thickness || "").trim();
  const fallback = parsePanelDimensions(item.size);

  const resolvedLength = length || fallback.length;
  const resolvedWidth = width || fallback.width;
  const resolvedThickness = thickness || fallback.thickness;

  const parts = [];
  if (resolvedLength) parts.push(`Length: ${formatMeasurement(resolvedLength)}`);
  if (resolvedWidth) parts.push(`Width: ${formatMeasurement(resolvedWidth)}`);
  if (resolvedThickness) parts.push(`Thickness: ${formatMeasurement(resolvedThickness)}`);

  return parts.length ? parts.join(" · ") : formatMeasurement(item.size);
}

export function formatInventoryQuantity(item = {}) {
  const boxes = Number(item.quantity) || 0;
  const unitsPerBox = Number(item.unitsPerBox) || 0;
  const totalPieces = boxes * unitsPerBox;

  if (boxes <= 0) return "0 boxes";
  if (unitsPerBox > 0) {
    return `${boxes} box${boxes === 1 ? "" : "es"} · ${totalPieces} pcs`;
  }
  return `${boxes} box${boxes === 1 ? "" : "es"}`;
}

export function parseLengthInMeters(value = "") {
  const text = String(value || "").trim();
  if (!text) return 0;
  const match = text.match(/(\d+(?:\.\d+)?)/);
  if (!match) return 0;
  const numericLength = Number(match[1]);
  const afterFirstNumber = text.slice((match.index || 0) + match[1].length);
  const unit = afterFirstNumber.match(/^\s*(mm|cm|m|ft|feet|inches?|in)\b/i)?.[1]?.toLowerCase()
    || text.match(/\b(mm|cm|m|ft|feet|inches?|in)\b/i)?.[1]?.toLowerCase();
  const meters = unit === "mm" ? numericLength / 1000
    : unit === "cm" ? numericLength / 100
      : unit === "ft" || unit === "feet" ? numericLength * 0.3048
        : unit === "in" || unit === "inch" || unit === "inches" ? numericLength * 0.0254
          : numericLength;
  return Number.isFinite(meters) ? meters : 0;
}

export function calculatePanelQuoteTotal(item = {}, boxes = 1) {
  const safeBoxes = Math.max(1, Number(boxes) || 1);
  const priceUnit = resolvePriceUnit(item);
  const unitPrice = Number(item.unitPrice ?? item.pricePerMeter ?? item.price ?? 0) || 0;
  const lengthMeters = parseLengthInMeters(item.length || item.size);
  const total = unitPrice * (priceUnit === "meter" ? lengthMeters : 1) * safeBoxes;
  return {
    pricePerMeter: unitPrice,
    unitPrice,
    priceUnit,
    lengthMeters,
    boxes: safeBoxes,
    total
  };
}

export function resolvePriceUnit(item = {}) {
  const explicit = String(item.priceUnit || "").trim().toLowerCase();
  if (["piece", "meter"].includes(explicit)) return explicit;
  const code = String(item.productCode || item.code || item.panelModel || item.name || "").trim().toUpperCase();
  return code.startsWith("LJS-") ? "meter" : "piece";
}

export function normalizeAvailability(quantity) {
  const value = Number(quantity) || 0;
  if (value <= 0) return "Out of Stock";
  if (value <= 5) return "Low Stock";
  return "In Stock";
}

export function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;");
}

export function availabilityClass(value) {
  const map = {
    "In Stock": "in-stock",
    "Low Stock": "low-stock",
    "Out of Stock": "out-stock"
  };
  return map[value] || "quote";
}

export function paymentClass(value) {
  const map = {
    Paid: "paid",
    "Partially Paid": "partial",
    Pending: "pending"
  };
  return map[value] || "quote";
}

export function stageClass(value) {
  const map = {
    "Closed Won": "completed",
    "Proposal Sent": "quote",
    Negotiation: "processing"
  };
  return map[value] || "quote";
}

export function normalizeInventoryRecords(demoState) {
  demoState.inventory = (demoState.inventory || []).map(item => {
    const fallbackQuantity =
      item.availability === "Out of Stock" ? 0 :
        item.availability === "Low Stock" ? 5 : 12;
    const quantity = Number.isFinite(Number(item.quantity)) ? Number(item.quantity) : fallbackQuantity;
    const unitsPerBox = Number.isFinite(Number(item.unitsPerBox)) ? Number(item.unitsPerBox) : 20;
    const parsedDimensions = parsePanelDimensions(item.size);
    const catalogMatch = findCatalogMatchForItem(item);
    const categoryGroup = catalogMatch.group || resolveCatalogCategoryGroup(item.categoryGroup) || "Uncategorized";
    const legacyName = String(item.name || "").trim();
    const legacyType = String(item.categoryName || item.productType || "").trim();
    const catalogEntry = catalogMatch.entry || findCatalogEntryForItem(item) || findCatalogEntry(categoryGroup, item.categoryName || item.productType || item.productName || item.name) || getCatalogEntryByCode(item.productCode || item.code || item.name);
    const productCode = getProductDisplayCode({
      ...item,
      productCode: item.productCode || item.code || catalogEntry?.code || item.name || ""
    });
    const productName = getProductDisplayName({
      ...item,
      productName: productCode || item.productName || catalogEntry?.type || (legacyType && !/^\d+$/.test(legacyType) ? legacyType : "") || legacyName || ""
    });
    const pricePerMeter = Number.isFinite(Number(item.pricePerMeter)) ? Number(item.pricePerMeter) : Number(item.price || 0);
    const priceUnit = resolvePriceUnit(item);
    return {
      ...item,
      categoryGroup,
      categoryName: String(item.categoryName || item.productType || catalogEntry?.type || "").trim(),
      productType: String(item.productType || item.categoryName || catalogEntry?.type || "").trim(),
      productCode,
      productName,
      name: productCode || productName || item.name || "",
      length: String(item.length || parsedDimensions.length || "").trim(),
      width: String(item.width || parsedDimensions.width || "").trim(),
      thickness: String(item.thickness || parsedDimensions.thickness || "").trim(),
      size: String(item.size || [item.length || parsedDimensions.length || "", item.width || parsedDimensions.width || "", item.thickness || parsedDimensions.thickness || ""].filter(Boolean).join(" x ")).trim(),
      unitsPerBox,
      quantity,
      priceUnit,
      pricePerMeter,
      price: pricePerMeter,
      availability: normalizeAvailability(quantity)
    };
  });
}



