import { demoState } from "../core/state.js";
import { saveState } from "../core/state.js";
import {
  ordersTable,
  themeToggle,
  staffAccountsList,
  staffAccountsCount,
  userSearchInput,
  userRoleFilter,
  userStatusFilter,
  userDateSort,
  userResultsSummary,
  kpiCards
} from "../core/dom.js";
import { formatPeso, parseMoney, getLocale, isOrderPaymentComplete, isOrderReadyForStaff, isOrderQuotationConfirmed, getCustomerSelectedPaymentMethod } from "../core/utils.js";
import { renderInventory, renderInventoryFilters } from "./inventory.js";
import { renderSalesTrend } from "./sales.js";
import { renderPayments, renderPaymentMethodOptions, syncPaymentRequestStatuses, recordPaymentForQuote } from "./payments.js";
import { syncPaymentFormFromQuote } from "./payments.js";
import { renderQuoteSummary, exportCurrentQuotation, syncQuotationDraftDefaults } from "./quotes.js?v=20261104-required-email-1";
import { applyTheme, populateSalesSelectors, populateSalesViewControls, renderDashboardMetrics } from "./ui.js";
import { applyLocalizedCopy, getTranslation } from "../core/i18n.js";
import { appState, salesViewState, salesDayState } from "../core/runtime.js";
import { roomCaptureGrid, roomCaptureCount } from "../core/dom.js";
import { getStaffUsers, getRoleLabel } from "../core/permissions.js";
import { escapeHtml } from "../core/utils.js";

const backendBaseUrl = location.protocol === "file:" ? "http://localhost:3000" : "";

export function renderOrders() {
  const rows = (demoState.orders || []).filter(isOrderReadyForStaff).sort((left, right) => {
    const leftTime = Date.parse(String(left.createdAt || left.created_at || "")) || 0;
    const rightTime = Date.parse(String(right.createdAt || right.created_at || "")) || 0;
    return rightTime - leftTime;
  });

  if (!rows.length) {
    ordersTable.innerHTML = `<tr><td colspan="9" class="operations-empty">Orders appear here after the customer confirms the quotation and full payment is recorded. Review awaiting payments in Payments.</td></tr>`;
    renderDashboardMetrics();
    return;
  }

  ordersTable.innerHTML = rows.map(order => {
    const deliveryStatus = normalizeDeliveryStatus(order.deliveryStatus || order.delivery_status || order.status);
    const orderInvoice = escapeHtml(order.invoice || order.id || order.quoteNumber);
    const displayReference = escapeHtml(order.quoteNumber || order.invoice || order.id);
    return `<tr>
      <td>${displayReference}</td>
      <td>${escapeHtml(order.customer)}</td>
      <td>${escapeHtml(order.products)}</td>
      <td>${formatPeso(parseMoney(order.amount))}</td>
      <td>${escapeHtml(String(order.payment || "Pending"))}</td>
      <td>${escapeHtml(deliveryStatus === "Shipped" ? "Handed to courier" : deliveryStatus)}</td>
      <td>
        <span>${escapeHtml(order.courier || order.deliveryCourier || "Not selected by customer")}</span>
        <input class="order-courier-input" data-order-field="trackingNumber" data-order-invoice="${orderInvoice}" value="${escapeHtml(order.trackingNumber || order.tracking_number || "")}" placeholder="External tracking reference" aria-label="External tracking reference" />
      </td>
      <td>
        <select class="order-status-select" data-order-field="deliveryStatus" data-order-invoice="${orderInvoice}" aria-label="Delivery status">
          <option value="Preparing" disabled ${deliveryStatus === "Preparing" ? "selected" : ""}>Preparing (automatic)</option>
          <option value="Shipped" ${deliveryStatus === "Shipped" ? "selected" : ""}>Handed to courier</option>
          <option value="Delivered" ${deliveryStatus === "Delivered" ? "selected" : ""}>Delivered</option>
        </select>
        <button class="secondary-btn small order-update-btn" type="button" data-order-update="${orderInvoice}">Save update</button>
      </td>
      <td><button class="secondary-btn small order-print-quotation-btn" type="button" data-order-print-quotation="${orderInvoice}">Print Quotation</button></td>
    </tr>`;
  }).join("");
  renderDashboardMetrics();
}

export function renderCourierOrders() {
  renderCourierOperations();
}
export function renderSalesManagerDashboard() {
  const payments = (demoState.payments || []).filter(payment => ["paid", "completed", "settled"].includes(String(payment.status || "").trim().toLowerCase()));
  const orders = getCourierOperationOrders();
  const awaitingPayments = (demoState.orders || []).filter(order => !isOrderPaymentComplete(order)).length;
  const pendingQuotes = (demoState.quotations || []).map(quotation => ({
    quote_number: quotation.quotation_no,
    customer_name: quotation.customer?.name || "Customer",
    total_amount: quotation.total,
    status: "Ready"
  }));
  const activeDeliveries = orders.filter(order => String(order.deliveryStatus || "").toLowerCase() !== "delivered");
  const setText = (id, value) => { const node = document.getElementById(id); if (node) node.textContent = String(value); };
  setText("salesManagerTotalSales", formatPeso(payments.reduce((sum, payment) => sum + parseMoney(payment.amount), 0)));
  setText("salesManagerPendingQuotes", pendingQuotes.length);
  setText("salesManagerAwaitingPayments", awaitingPayments);
  setText("salesManagerActiveDeliveries", activeDeliveries.length);

  const queue = document.getElementById("salesManagerQuoteQueue");
  if (queue) queue.innerHTML = pendingQuotes.slice(0, 5).map(request => `
    <div class="dashboard-queue-row"><div><strong>${escapeHtml(request.customer_name || request.customerName || "Customer")}</strong><small>${escapeHtml(request.quote_number || request.quoteNumber || "Quotation")} · ${escapeHtml(request.status || "Awaiting review")}</small></div><strong>${formatPeso(parseMoney(request.total_amount || request.totalAmount || 0))}</strong></div>
  `).join("") || `<p class="operations-muted">No generated quotations yet.</p>`;

  const courier = document.getElementById("salesManagerCourierSummary");
  if (courier) courier.innerHTML = orders.slice(0, 5).map(order => `
    <div class="dashboard-queue-row"><div><strong>${escapeHtml(order.displayReference || order.quoteNumber || order.invoice)}</strong><small>${escapeHtml(order.customer || "Customer")}</small></div><span class="status-pill ${String(order.deliveryStatus || order.status).toLowerCase() === "delivered" ? "success" : "warning"}">${escapeHtml(order.deliveryStatus || order.status || "Order placed")}</span></div>
  `).join("") || `<p class="operations-muted">No delivery records yet.</p>`;
}

let courierOperationsFilter = "All";

function showCourierMessage(message, kind = "success") {
  const existing = document.getElementById("courierActionMessage");
  if (existing) existing.remove();
  const host = document.querySelector('[data-section="courier"]') || document.body;
  const node = document.createElement("div");
  node.id = "courierActionMessage";
  node.className = `courier-action-message ${kind}`;
  node.textContent = message;
  host.prepend(node);
  window.setTimeout(() => node.remove(), 3200);
}

async function persistCourierChange(order, successMessage) {
  const saved = await saveState();
  if (!saved) {
    showCourierMessage("The courier update could not be saved. Please try again.", "error");
    return false;
  }
  renderOrders();
  renderCourierOperations();
  renderSalesManagerDashboard();
  showCourierMessage(successMessage);
  return true;
}

function getCourierOperationOrders() {
  return (demoState.orders || []).filter(order => isOrderReadyForStaff(order) && String(order.invoice || order.quoteNumber || "").trim()).map(order => {
    const quotation = (demoState.quotations || []).find(item => (
      String(item.quotation_no || "") === String(order.quoteNumber || order.invoice || "")
      || String(item.order_id || "") === String(order.id || order.invoice || "")
    ));
    const savedCourier = order.courier || order.deliveryCourier || order.deliveryMethod || quotation?.courier || "";
    const courier = ["J&T", "Lalamove"].find(option => String(savedCourier).trim().toLowerCase() === option.toLowerCase()) || "";
    return {
      ...order,
      invoice: order.invoice || order.quoteNumber,
      displayReference: order.quoteNumber || order.invoice,
      deliveryStatus: normalizeDeliveryStatus(order.deliveryStatus || order.delivery_status || order.status),
      courier
    };
  });
}

function normalizeDeliveryStatus(value = "") {
  const status = String(value || "Order placed").trim().toLowerCase().replace(/[_-]+/g, " ");
  if (status.includes("deliver")) return "Delivered";
  if (status.includes("handed") || status.includes("shipped") || status.includes("transit")) return "Shipped";
  return "Preparing";
}

function courierStageState(order, stage) {
  const status = normalizeDeliveryStatus(order.deliveryStatus || order.status).toLowerCase();
  const stages = ["preparing", "shipped", "delivered"];
  if (status === "delivered") return "done";
  const currentIndex = Math.max(0, stages.indexOf(status));
  const index = stages.indexOf(stage);
  return index < currentIndex ? "done" : index === currentIndex ? "current" : "pending";
}

function renderCourierOperations() {
  const orders = getCourierOperationOrders();
  const active = orders.filter(order => normalizeDeliveryStatus(order.deliveryStatus) !== "Delivered");
  const delivered = orders.filter(order => normalizeDeliveryStatus(order.deliveryStatus) === "Delivered");
  const metrics = document.getElementById("courierMetrics");
  if (metrics) metrics.innerHTML = [
    ["Active deliveries", active.length, "In progress"],
    ["Delivered today", delivered.length, delivered[0]?.displayReference || delivered[0]?.quoteNumber || "No deliveries"]
  ].map(([label, value, note]) => `<article class="operations-metric"><span>${label}</span><strong>${escapeHtml(String(value))}</strong><small>${escapeHtml(String(note))}</small></article>`).join("");

  const filters = document.getElementById("courierFilters");
  if (filters) filters.innerHTML = ["All", "In transit", "Delivered", "Pending"].map(label => `<button type="button" class="filter-chip${courierOperationsFilter === label ? " active" : ""}" data-courier-filter="${label}">${label}</button>`).join("");
  const visible = orders.filter(order => courierOperationsFilter === "All"
    || (courierOperationsFilter === "Delivered" && normalizeDeliveryStatus(order.deliveryStatus) === "Delivered")
    || (courierOperationsFilter === "Pending" && normalizeDeliveryStatus(order.deliveryStatus) === "Preparing")
    || (courierOperationsFilter === "In transit" && normalizeDeliveryStatus(order.deliveryStatus) === "Shipped"));
  const list = document.getElementById("courierTimelineList");
  if (list) list.innerHTML = visible.length ? visible.map(order => {
    const current = normalizeDeliveryStatus(order.deliveryStatus);
    const stages = [["preparing", "Preparing order"], ["shipped", "Handed to courier"], ["delivered", "Delivered"]];
    const nextStage = { preparing: "Shipped", shipped: "Delivered" }[current.toLowerCase()];
    const displayStatus = current.toLowerCase() === "preparing"
      ? "Pending"
      : current.toLowerCase() === "shipped"
        ? "Handed to courier"
        : current;
    return `<article class="courier-order-card">
      <div class="courier-order-head"><div><h4>${escapeHtml(order.displayReference || order.invoice)}</h4><p>${escapeHtml(order.customer || "Customer")}${order.cityProvince ? ` · ${escapeHtml(order.cityProvince)}` : ""}</p></div><div><span class="status-pill ${current.toLowerCase() === "delivered" ? "success" : "neutral"}">${escapeHtml(displayStatus)}</span>${order.courier ? `<span class="courier-badge">${escapeHtml(order.courier)}</span>` : `<span class="courier-badge">Courier choice not recorded</span>`}</div></div>
      <div class="courier-stages">${stages.map(([key, label]) => { const state = courierStageState(order, key); return `<div class="courier-stage ${state}"><span>${state === "done" ? "✓" : state === "current" ? "◷" : "○"}</span><div><strong>${label}</strong><small>${state === "done" ? "Completed" : state === "current" ? "In progress" : "Awaiting"}</small></div></div>`; }).join("")}</div>
      ${nextStage ? `<button type="button" class="secondary-btn courier-stage-btn" data-courier-stage="${escapeHtml(order.invoice)}" data-next-stage="${nextStage}">Mark ${nextStage === "Shipped" ? "handed to courier" : "delivered"}</button>` : ""}
    </article>`;
  }).join("") : `<div class="operations-empty">No orders match this filter.</div>`;

  const summary = document.getElementById("courierSummary");
  if (summary) summary.innerHTML = `<p class="eyebrow">Summary</p><h3>Delivery status</h3><div class="summary-line"><span>Pending</span><strong>${active.filter(order => normalizeDeliveryStatus(order.deliveryStatus) === "Preparing").length}</strong></div><div class="summary-line"><span>Handed to courier</span><strong>${active.filter(order => normalizeDeliveryStatus(order.deliveryStatus) === "Shipped").length}</strong></div><div class="summary-line"><span>Delivered</span><strong>${delivered.length}</strong></div>`;
}

document.addEventListener("click", async event => {
  const filter = event.target.closest("[data-courier-filter]");
  if (filter) {
    courierOperationsFilter = filter.dataset.courierFilter || "All";
    renderCourierOperations();
    return;
  }
  const stage = event.target.closest("[data-courier-stage]");
  if (!stage) return;
  const orders = getCourierOperationOrders();
  const targetSnapshot = orders.find(order => order.invoice === stage.dataset.courierStage);
  const target = (demoState.orders || []).find(order => String(order.invoice || order.quoteNumber || "") === String(targetSnapshot?.invoice || ""));
  if (!target) {
    showCourierMessage("This delivery update could not be matched to an order.", "error");
    return;
  }
  if (!isOrderReadyForStaff(target)) {
    showCourierMessage("This order stays out of delivery processing until the quotation is confirmed and full payment is recorded.", "error");
    return;
  }
  target.deliveryStatus = stage.dataset.nextStage || target.deliveryStatus;
  target.updatedAt = new Date().toISOString();
  await persistCourierChange(target, `Delivery status updated for ${target.invoice}.`);
});

let paymentOperationsFilter = "All";

function getPaymentOperationRecords() {
  const byQuote = new Map();
  const put = (record, source) => {
    const quote = String(record.quoteNumber || record.quote_number || record.invoice || "").trim();
    if (!quote) return;
    const existing = byQuote.get(quote.toLowerCase());
    if (!existing || source === "order") byQuote.set(quote.toLowerCase(), {
      ...record,
      invoice: quote,
      orderInvoice: record.invoice || record.id || "",
      quoteNumber: quote
    });
  };
  (demoState.orders || []).filter(isOrderQuotationConfirmed).forEach(order => put({
    ...order,
    quoteNumber: order.quoteNumber || order.quote_number || order.invoice,
    amount: order.amount || order.totalAmount || order.total_amount || 0,
    method: getCustomerSelectedPaymentMethod(order.payment, order.method, order.paymentMethod, order.payment_method),
    date: order.updatedAt || order.updated_at || order.createdAt || order.created_at || new Date().toISOString(),
    status: order.paymentStatus || order.payment_status || order.status || "Awaiting"
  }, "order"));
  return [...byQuote.values()].map(record => {
    if (!getCustomerSelectedPaymentMethod(record.method)) {
      const request = (demoState.paymentRequests || []).find(item => String(item.quote_number || item.quoteNumber || "") === String(record.quoteNumber));
      const quotation = (demoState.quotations || []).find(item => String(item.quotation_no || "") === String(record.quoteNumber));
      record.method = getCustomerSelectedPaymentMethod(request?.payment_method, request?.paymentMethod, quotation?.payment_method);
    }
    const normalized = String(record.status || "").toLowerCase();
    const settled = ["paid", "completed", "settled"].includes(normalized);
    const references = new Set([record.quoteNumber, record.orderInvoice, record.id]
      .map(value => String(value || "").trim()).filter(Boolean));
    const amountRecorded = (demoState.payments || []).reduce((sum, payment) => {
      const paymentStatus = String(payment.status || "").trim().toLowerCase();
      const matches = [payment.invoice, payment.quoteNumber, payment.quote_number, payment.orderId, payment.order_id]
        .some(reference => references.has(String(reference || "").trim()));
      return matches && ["paid", "completed", "settled", "partially paid"].includes(paymentStatus)
        ? sum + parseMoney(payment.amount)
        : sum;
    }, 0);
    const totalAmount = parseMoney(record.amount);
    const amountDue = settled ? 0 : Math.max(0, totalAmount - amountRecorded);
    const paid = settled || (totalAmount > 0 && amountDue === 0);
    return { ...record, totalAmount, amount: amountDue, paid, awaiting: !paid };
  }).sort((a, b) => (a.paid ? 1 : -1) || (Date.parse(b.date) || 0) - (Date.parse(a.date) || 0));
}

export function renderPaymentOperations() {
  const table = document.getElementById("paymentsOperationsTable");
  const metrics = document.getElementById("paymentOperationsMetrics");
  const filters = document.getElementById("paymentFilters");
  if (!table || !metrics || !filters) return;
  const records = getPaymentOperationRecords();
  const today = new Date().toISOString().slice(0, 10);
  const collectedToday = records.filter(record => record.paid && String(record.date).slice(0, 10) === today)
    .reduce((sum, record) => sum + parseMoney(record.amount), 0);
  const awaiting = records.filter(record => record.awaiting);
  const awaitingTotal = awaiting.reduce((sum, record) => sum + parseMoney(record.amount), 0);
  const methodCount = method => records.filter(record => String(record.method || "").toLowerCase().includes(method.toLowerCase())).length;
  metrics.innerHTML = [
    ["Total collected today", formatPeso(collectedToday), "Paid payments"],
    ["Awaiting payment", String(awaiting.length), `${formatPeso(awaitingTotal)} total`],
    ["GCash payments", String(methodCount("gcash")), "Recorded payment method"],
    ["Cash payments", String(methodCount("cash")), "Recorded payment method"]
  ].map(([label, value, note]) => `<article class="operations-metric"><span>${label}</span><strong>${value}</strong><small>${note}</small></article>`).join("");

  const labels = ["All", "Awaiting", "Paid", "GCash", "Cash", "Bank transfer"];
  filters.innerHTML = labels.map(label => `<button type="button" class="filter-chip${paymentOperationsFilter === label ? " active" : ""}" data-payment-filter="${label}">${label}</button>`).join("");
  const search = String(document.getElementById("paymentsSearch")?.value || "").trim().toLowerCase();
  const visible = records.filter(record => {
    const method = String(record.method || "").toLowerCase().replace(/_/g, " ");
    const matchesFilter = paymentOperationsFilter === "All"
      || (paymentOperationsFilter === "Awaiting" && record.awaiting)
      || (paymentOperationsFilter === "Paid" && record.paid)
      || method.includes(paymentOperationsFilter.toLowerCase());
    return matchesFilter && (!search || `${record.quoteNumber} ${record.customer}`.toLowerCase().includes(search));
  });
  table.innerHTML = visible.length ? visible.map(record => `
    <tr>
      <td><strong>${escapeHtml(record.quoteNumber)}</strong></td>
      <td>${escapeHtml(record.customer || "Customer")}</td>
      <td><span class="payment-method-mark">${record.method?.toLowerCase().includes("gcash") ? "●" : record.method?.toLowerCase().includes("cash") ? "₱" : "▣"}</span> ${escapeHtml(String(record.method || "Pending").replace(/_/g, " ").replace(/\b\w/g, char => char.toUpperCase()))}</td>
      <td><strong>${formatPeso(parseMoney(record.amount))}</strong></td>
      <td>${escapeHtml(String(record.date || "").slice(0, 10) || "--")}</td>
      <td><span class="status-pill ${record.paid ? "success" : "warning"}">${record.paid ? "Paid" : "Awaiting"}</span></td>
      <td><button type="button" class="secondary-btn small" data-payment-operation="${escapeHtml(record.quoteNumber)}" data-payment-action="${record.paid ? "receipt" : "record"}" ${!record.paid && !record.method ? "disabled title=\"Waiting for the customer to select a payment method\"" : ""}>${record.paid ? "Receipt" : record.method ? "Record" : "Awaiting method"}</button></td>
    </tr>`).join("") : `<tr><td colspan="7" class="operations-empty">No payments match the current filter.</td></tr>`;
}

document.addEventListener("click", async event => {
  const filter = event.target.closest("[data-payment-filter]");
  if (filter) {
    paymentOperationsFilter = filter.dataset.paymentFilter || "All";
    renderPaymentOperations();
    return;
  }
  const action = event.target.closest("[data-payment-operation]");
  if (!action) return;
  const record = getPaymentOperationRecords().find(item => item.quoteNumber === action.dataset.paymentOperation);
  if (!record) return;
  if (action.dataset.paymentAction === "receipt") {
    window.dispatchEvent(new CustomEvent("aiyige:open-quotation", { detail: { quoteNumber: record.quoteNumber, customerName: record.customer } }));
    return;
  }
  const result = recordPaymentForQuote({
    quoteNumber: record.quoteNumber,
    customerName: record.customer,
    amount: record.amount,
    method: record.method,
    status: "Paid",
    paymentDateValue: new Date().toISOString().slice(0, 10),
    reference: "Payment recorded",
    notes: "Recorded from Payments page."
  });
  if (!result.ok) return;
  await saveState();
  renderPaymentOperations();
  renderPayments();
  renderOrders();
  renderCourierOperations();
  renderSalesManagerDashboard();
});

document.getElementById("paymentsSearch")?.addEventListener("input", renderPaymentOperations);

ordersTable?.addEventListener("click", async event => {
  const printButton = event.target.closest("[data-order-print-quotation]");
  if (printButton) {
    const invoice = printButton.dataset.orderPrintQuotation;
    const order = (demoState.orders || []).find(entry => String(entry.invoice || entry.quoteNumber || "") === String(invoice));
    if (order) window.dispatchEvent(new CustomEvent("aiyige:print-quotation", { detail: { order } }));
    return;
  }
  const button = event.target.closest("[data-order-update]");
  if (!button) return;
  const invoice = button.dataset.orderUpdate;
  const fields = [...ordersTable.querySelectorAll(`[data-order-invoice="${CSS.escape(invoice)}"]`)];
  const trackingNumber = fields.find(field => field.dataset.orderField === "trackingNumber")?.value.trim() || "";
  const deliveryStatus = fields.find(field => field.dataset.orderField === "deliveryStatus")?.value || "Order placed";
  const order = (demoState.orders || []).find(entry => String(entry.invoice || entry.quoteNumber || "") === String(invoice));
  if (!order || !isOrderReadyForStaff(order)) {
    showCourierMessage("This order stays out of delivery processing until the quotation is confirmed and full payment is recorded.", "error");
    return;
  }
  Object.assign(order, { trackingNumber, deliveryStatus, updatedAt: new Date().toISOString() });
  const saved = await persistCourierChange(order, `Courier update saved for ${invoice}.`);
  button.textContent = saved ? "Saved ✓" : "Save failed";
  setTimeout(() => { button.textContent = "Save update"; }, 1400);
});

export function renderInventoryStaffDashboard() {
  const inventory = Array.isArray(demoState.inventory) ? demoState.inventory : [];
  const getQuantity = item => Number(item.quantity ?? item.stock ?? 0) || 0;
  const lowStock = inventory.filter(item => getQuantity(item) > 0 && getQuantity(item) <= 5).length;
  const outOfStock = inventory.filter(item => getQuantity(item) <= 0).length;
  const inStock = Math.max(0, inventory.length - lowStock - outOfStock);
  const totalUnits = inventory.reduce((sum, item) => sum + getQuantity(item), 0);
  const values = {
    inventoryDashboardProductCount: inventory.length,
    inventoryDashboardLowStock: lowStock,
    inventoryDashboardOutOfStock: outOfStock,
    inventoryDashboardUnits: totalUnits,
    inventoryDashboardInStock: `${inStock} product${inStock === 1 ? "" : "s"}`,
    inventoryDashboardLowStockLabel: `${lowStock} product${lowStock === 1 ? "" : "s"}`,
    inventoryDashboardOutOfStockLabel: `${outOfStock} product${outOfStock === 1 ? "" : "s"}`
  };
  Object.entries(values).forEach(([id, value]) => {
    const element = document.getElementById(id);
    if (element) element.textContent = String(value);
  });

  const alerts = inventory
    .filter(item => getQuantity(item) <= 5)
    .sort((left, right) => getQuantity(left) - getQuantity(right))
    .slice(0, 6);
  const alertsElement = document.getElementById("inventoryDashboardAlerts");
  if (alertsElement) {
    alertsElement.innerHTML = alerts.length
      ? alerts.map(item => `<div class="inventory-alert-row"><span class="status-dot ${getQuantity(item) <= 0 ? "status-dot-danger" : "status-dot-warning"}"></span><strong>${escapeHtml(item.productCode || item.code || item.name || "Product")}</strong><span>${getQuantity(item) <= 0 ? "Out of stock" : `${getQuantity(item)} units left`}</span></div>`).join("")
      : '<p class="inventory-dashboard-empty">No stock alerts right now.</p>';
  }

  const categoryCounts = Object.entries(inventory.reduce((counts, item) => {
    const category = String(item.categoryGroup || item.category || "Uncategorized");
    counts[category] = (counts[category] || 0) + 1;
    return counts;
  }, {})).sort((left, right) => right[1] - left[1]).slice(0, 6);
  const categoriesElement = document.getElementById("inventoryDashboardCategories");
  if (categoriesElement) {
    categoriesElement.innerHTML = categoryCounts.length
      ? categoryCounts.map(([category, count]) => `<div class="inventory-category-row"><span>${escapeHtml(category)}</span><strong>${count}</strong></div>`).join("")
      : '<p class="inventory-dashboard-empty">No categories available.</p>';
  }
}

export function renderSalesStaffDashboard() {
  const queue = document.getElementById("salesStaffDashboardQueue");
  const payments = document.getElementById("salesStaffDashboardPayments");
  if (!queue || !payments) return;
  const quotations = (demoState.quotations || []).map(quotation => ({
    quote_number: quotation.quotation_no,
    customer_name: quotation.customer?.name || "Customer",
    total_amount: quotation.total,
    source: "Generated",
    status: "Ready"
  }));
  queue.innerHTML = quotations.length ? quotations.slice(0, 5).map(request => {
    const number = request.quote_number || request.quoteNumber || "Quotation";
    return `<div class="dashboard-queue-row"><div><strong>${escapeHtml(request.customer_name || request.customerName || "Customer")}</strong><small>${escapeHtml(number)} · ${escapeHtml(request.source || "Mobile app")} · ${escapeHtml(request.status || "New")}</small></div><strong>${formatPeso(parseMoney(request.total_amount || request.totalAmount || 0))}</strong><button type="button" class="secondary-btn small" data-dashboard-quote-customer="${escapeHtml(request.customer_name || request.customerName || "")}">Print</button></div>`;
  }).join("") : `<p class="operations-muted">No generated quotations.</p>`;
  const pending = getPaymentOperationRecords().filter(record => record.awaiting).slice(0, 5);
  payments.innerHTML = pending.length ? pending.map(record => `<div class="dashboard-queue-row"><div><strong>${escapeHtml(record.customer || "Customer")}</strong><small>${escapeHtml(record.method || "Payment")} · ${escapeHtml(record.quoteNumber)}</small></div><strong>${formatPeso(parseMoney(record.amount))}</strong><span class="status-pill warning">Awaiting</span></div>`).join("") : `<p class="operations-muted">No pending payments.</p>`;
}

export function renderRoleDashboards() {
  const setValue = (id, value) => {
    const element = document.getElementById(id);
    if (element) element.textContent = String(value);
  };
  const users = Array.isArray(demoState.users) ? demoState.users : [];
  const managedUsers = users.filter(user => String(user.role || "").toLowerCase() !== "owner");
  setValue("adminDashboardUsers", managedUsers.length);
  setValue("adminDashboardActiveUsers", managedUsers.filter(user => String(user.status || "active").toLowerCase() === "active").length);
  setValue("adminDashboardSalesStaff", managedUsers.filter(user => String(user.role || "").toLowerCase() === "sales_staff").length);
  setValue("adminDashboardInventoryStaff", managedUsers.filter(user => String(user.role || "").toLowerCase() === "inventory_staff").length);

  const savedQuotes = Array.isArray(demoState.quotations) ? demoState.quotations : [];
  const orders = Array.isArray(demoState.orders) ? demoState.orders : [];
  const customers = Array.isArray(demoState.customers) ? demoState.customers : [];
  setValue("salesDashboardQuotes", savedQuotes.length);
  setValue("salesDashboardCustomers", customers.length);
  const paidOrders = orders.filter(isOrderReadyForStaff);
  setValue("salesDashboardOrders", paidOrders.length);
  setValue("salesDashboardProducts", Array.isArray(demoState.inventory) ? demoState.inventory.length : 0);
  setValue("salesDashboardQuotes", savedQuotes.length);
  renderSalesStaffDashboard();
  renderSalesManagerDashboard();

  const pendingQuotes = savedQuotes.length;
  const completedOrders = paidOrders.filter(order => ["completed", "paid", "closed", "delivered"].includes(String(order.status || "").toLowerCase())).length;
  const recentTransactions = Array.isArray(demoState.sales) ? demoState.sales.length : paidOrders.length;
  setValue("salesManagerQuotes", pendingQuotes);
  setValue("salesManagerOrders", paidOrders.length);
  setValue("salesManagerCompleted", completedOrders);
  setValue("salesManagerTransactions", recentTransactions);

  const inventory = Array.isArray(demoState.inventory) ? demoState.inventory : [];
  const quantity = item => Number(item.quantity ?? item.stock ?? 0) || 0;
  const salesTotal = paidOrders.reduce((sum, order) => sum + parseMoney(order.amount || order.totalAmount || 0), 0);
  setValue("ownerDashboardSales", formatPeso(salesTotal));
  setValue("ownerDashboardLowStock", inventory.filter(item => quantity(item) > 0 && quantity(item) <= 5).length);
  setValue("ownerDashboardProducts", inventory.length);
  setValue("ownerDashboardEmployees", managedUsers.filter(user => String(user.role || "").toLowerCase() !== "admin").length);
}

function resolveCaptureImageUrl(value) {
  const raw = String(value || "").trim();
  if (!raw) return null;
  if (raw.startsWith("http://") || raw.startsWith("https://") || raw.startsWith("data:")) return raw;
  if (raw.startsWith("/assets/")) return `${backendBaseUrl}${raw}`;
  if (raw.startsWith("assets/")) return `${backendBaseUrl}/${raw}`;
  return `${backendBaseUrl}/${raw}`;
}

export function renderRoomCaptures() {
  if (!roomCaptureGrid) return;

  const captures = [...(demoState.roomCaptures || [])].sort((a, b) => {
    const left = Date.parse(String(b.capturedAt || "").replace(" ", "T")) || 0;
    const right = Date.parse(String(a.capturedAt || "").replace(" ", "T")) || 0;
    return left - right;
  });
  const visibleCaptures = captures.slice(0, 6);

  if (roomCaptureCount) {
    roomCaptureCount.textContent = `${captures.length} capture${captures.length === 1 ? "" : "s"}`;
  }

  if (!visibleCaptures.length) {
    roomCaptureGrid.innerHTML = `
      <div class="room-capture-empty">
        <strong>No room captures yet.</strong>
        <p>Customer room photos will appear here once the mobile app saves a capture.</p>
      </div>
    `;
    return;
  }

  roomCaptureGrid.innerHTML = visibleCaptures.map(capture => {
    const imageUrl = resolveCaptureImageUrl(capture.imageAsset || capture.imageUrl || capture.imageData);
    return `
      <article class="room-capture-card">
        <div class="room-capture-thumb${imageUrl ? " has-image" : ""}">
          ${imageUrl ? `<img class="room-capture-image" src="${escapeHtml(imageUrl)}" alt="${escapeHtml(capture.customerName || "Room capture")}" />` : "<span>Capture</span>"}
        </div>
        <div class="room-capture-copy">
          <div class="room-capture-head">
            <strong>${escapeHtml(capture.customerName || "Unknown customer")}</strong>
            <span>${escapeHtml(capture.capturedAt || "--")}</span>
          </div>
          <p>${escapeHtml(capture.productName || "Room capture")}${capture.quoteNumber ? ` · ${escapeHtml(capture.quoteNumber)}` : ""}</p>
          <small>${capture.finish ? `Finish: ${escapeHtml(capture.finish)}` : "Finish: --"}${capture.size ? ` · Size: ${escapeHtml(capture.size)}` : ""}</small>
          <small>${capture.quantity != null ? `Panels: ${escapeHtml(String(capture.quantity))}` : "Panels: --"}${capture.quoteAmount != null ? ` · Total: ${formatPeso(parseMoney(capture.quoteAmount))}` : ""}${capture.status ? ` · ${escapeHtml(capture.status)}` : ""}</small>
        </div>
        <div class="room-capture-actions">
          <button
            type="button"
            class="action-btn action-btn-delete action-btn-small"
            data-delete-room-capture="${escapeHtml(String(capture.id))}"
          >
            <span class="action-btn-icon" aria-hidden="true">×</span>
            Delete
          </button>
        </div>
      </article>
    `;
  }).join("");
}

export function renderSettingsPanel() {
  applyLocalizedCopy();
  const t = getTranslation(demoState.settings.language);
  if (themeToggle) {
    themeToggle.checked = demoState.settings.themeMode === "dark";
  }

  const themeSummaryValue = document.getElementById("themeSummaryValue");
  const appearanceSummaryValue = document.getElementById("appearanceSummaryValue");
  const localizationSummaryValue = document.getElementById("localizationSummaryValue");
  const languageSelect = document.getElementById("languageSelect");
  const currencySelect = document.getElementById("currencySelect");
  const dateFormatSelect = document.getElementById("dateFormatSelect");
  const unitSelect = document.getElementById("unitSelect");
  const appVersionValue = document.getElementById("appVersionValue");
  const inventorySyncValue = document.getElementById("inventorySyncValue");
  const systemStatusValue = document.getElementById("systemStatusValue");
  const systemStatusBadge = document.getElementById("systemStatusBadge");

  if (themeSummaryValue) {
    themeSummaryValue.textContent = demoState.settings.themeMode === "dark" ? "Dark" : "Light";
  }
  if (appearanceSummaryValue) {
    appearanceSummaryValue.textContent = demoState.settings.themeMode === "dark" ? "Dark Mode" : "Light Mode";
  }
  if (languageSelect) languageSelect.value = demoState.settings.language || "English";
  if (currencySelect) currencySelect.value = demoState.settings.currency || "PHP";
  if (dateFormatSelect) dateFormatSelect.value = demoState.settings.dateFormat || "MM/DD/YYYY";
  if (unitSelect) unitSelect.value = demoState.settings.unitOfMeasurement || "Meters";
  if (localizationSummaryValue) {
    localizationSummaryValue.textContent = `${demoState.settings.currency || "PHP"} / ${demoState.settings.dateFormat || "MM/DD/YYYY"} / ${demoState.settings.unitOfMeasurement || "Meters"}`;
  }
  if (appVersionValue) appVersionValue.textContent = `v${demoState.settings.appVersion || "1.0.0"}`;
  if (inventorySyncValue) {
    inventorySyncValue.textContent = demoState.settings.inventoryLastSyncedAt
      ? new Intl.DateTimeFormat(getLocale(), {
        month: "short",
        day: "numeric",
        hour: "numeric",
        minute: "2-digit"
      }).format(new Date(demoState.settings.inventoryLastSyncedAt))
      : "Not synced yet";
  }
  const isOnline = navigator.onLine;
  const statusLabel = isOnline ? "Online" : "Offline";
  if (systemStatusValue) systemStatusValue.textContent = statusLabel;
  if (systemStatusBadge) systemStatusBadge.textContent = statusLabel;

  const search = String(userSearchInput?.value || "").trim().toLowerCase();
  const roleFilter = String(userRoleFilter?.value || "all").toLowerCase();
  const statusFilter = String(userStatusFilter?.value || "all").toLowerCase();
  const dateSort = String(userDateSort?.value || "recent").toLowerCase();
  const staffUsers = getStaffUsers()
    .filter(user => {
      const haystack = [user.displayName, user.email, user.username, user.role].map(value => String(value || "").toLowerCase()).join(" ");
      const status = String(user.status || "active").toLowerCase();
      return (!search || haystack.includes(search))
        && (roleFilter === "all" || String(user.role || "").toLowerCase() === roleFilter)
        && (statusFilter === "all" || status === statusFilter);
    })
    .sort((left, right) => {
      if (dateSort === "name") return String(left.displayName || left.username || "").localeCompare(String(right.displayName || right.username || ""));
      const leftDate = dateSort === "joined" ? left.joinedAt : left.lastActiveAt;
      const rightDate = dateSort === "joined" ? right.joinedAt : right.lastActiveAt;
      return (Date.parse(String(rightDate || "")) || 0) - (Date.parse(String(leftDate || "")) || 0);
    });
  if (staffAccountsCount) {
    const totalUsers = getStaffUsers().length;
    staffAccountsCount.textContent = `${totalUsers} user${totalUsers === 1 ? "" : "s"}`;
  }
  if (userResultsSummary) {
    userResultsSummary.textContent = `${staffUsers.length} of ${getStaffUsers().length} users`;
  }
  if (staffAccountsList) {
    staffAccountsList.innerHTML = staffUsers.length ? staffUsers.map(user => `
      <tr>
        <td><input type="checkbox" class="user-row-checkbox" data-user-select="${escapeHtml(String(user.id))}" aria-label="Select ${escapeHtml(user.displayName || user.username || "user")}" /></td>
        <td><strong>${escapeHtml(user.displayName || user.username || "User")}</strong></td>
        <td>${escapeHtml(user.email || "--")}</td>
        <td>${escapeHtml(user.username || "--")}</td>
        <td><span class="user-status-badge status-${escapeHtml(String(user.status || "active").toLowerCase())}">${escapeHtml(String(user.status || "Active").replace(/^./, character => character.toUpperCase()))}</span></td>
        <td>${escapeHtml(getRoleLabel(user.role))}</td>
        <td>${escapeHtml(formatUserDate(user.joinedAt))}</td>
        <td>${escapeHtml(formatRelativeUserDate(user.lastActiveAt))}</td>
        <td>
          <div class="user-row-actions">
            ${String(user.role || "").toLowerCase() === "admin" ? "" : `<button type="button" class="ghost-btn small" data-edit-staff="${escapeHtml(String(user.id))}" aria-label="Edit user">✎</button>`}
            ${Number(user.id) === Number(appState.currentUser?.id) ? "" : `<button type="button" class="action-btn action-btn-delete action-btn-small" data-delete-staff="${escapeHtml(String(user.id))}" aria-label="Delete user">♧</button>`}
          </div>
        </td>
      </tr>
    `).join("") : `
      <tr><td colspan="9" class="user-table-empty">No users match the current filters.</td></tr>
    `;
  }
}

function formatUserDate(value) {
  const date = new Date(value);
  if (!value || Number.isNaN(date.getTime())) return "--";
  return new Intl.DateTimeFormat(undefined, { month: "long", day: "numeric", year: "numeric" }).format(date);
}

function formatRelativeUserDate(value) {
  const date = new Date(value);
  if (!value || Number.isNaN(date.getTime())) return "Never";
  const seconds = Math.max(0, Math.floor((Date.now() - date.getTime()) / 1000));
  if (seconds < 60) return `${seconds || 1} minute${seconds === 1 ? "" : "s"} ago`;
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return `${minutes} minute${minutes === 1 ? "" : "s"} ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours} hour${hours === 1 ? "" : "s"} ago`;
  const days = Math.floor(hours / 24);
  if (days < 30) return `${days} day${days === 1 ? "" : "s"} ago`;
  return `${Math.floor(days / 30)} month${Math.floor(days / 30) === 1 ? "" : "s"} ago`;
}

export function exportUsersCsv() {
  const users = getStaffUsers();
  const headers = ["Full Name", "Email", "Username", "Status", "Role", "Joined Date", "Last Active"];
  const rows = users.map(user => [
    user.displayName || "",
    user.email || "",
    user.username || "",
    user.status || "active",
    user.role || "",
    user.joinedAt || "",
    user.lastActiveAt || ""
  ]);
  const csv = [headers, ...rows].map(row => row.map(value => `"${String(value).replace(/"/g, '""')}"`).join(",")).join("\n");
  const blob = new Blob([csv], { type: "text/csv;charset=utf-8" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = "user-management.csv";
  link.click();
  URL.revokeObjectURL(url);
}

export function applySettings() {
  applyTheme(demoState.settings.themeMode);
  renderAppSections();
}

export function renderAppSections() {
  const paymentRequestStatusesChanged = syncPaymentRequestStatuses();
  if (paymentRequestStatusesChanged) saveState();
  applyLocalizedCopy();
  renderRoleDashboards();
  renderInventoryStaffDashboard();
  populateSalesSelectors();
  populateSalesViewControls();
  renderInventory();
  renderOrders();
  renderPayments();
  renderRoomCaptures();
  syncQuotationDraftDefaults();
  renderQuoteSummary();
  syncPaymentFormFromQuote();
  renderSalesTrend();
  renderPaymentMethodOptions();
  renderSettingsPanel();
}
