import { demoState } from "../core/state.js";
import {
  paymentDonut,
  paymentDonutSegments,
  paymentLegendItems,
  paymentQuoteNumber,
  paymentAccountName,
  paymentAmount,
  paymentMethod,
  paymentStatus,
  paymentDate,
  paymentReference,
  paymentNotes
} from "../core/dom.js";
import {
  formatPeso,
  parseMoney,
  escapeHtml,
  isOrderQuotationConfirmed,
  getCustomerSelectedPaymentMethod
} from "../core/utils.js";
import { renderDashboardMetrics } from "./ui.js";
import { animateCount } from "./sales.js";

const settledPaymentStatuses = new Set(["paid", "completed", "settled"]);

function isSettledPayment(payment) {
  return settledPaymentStatuses.has(String(payment?.status || "").trim().toLowerCase());
}

export function getInvoicePayments(invoice) {
  const order = (demoState.orders || []).find(entry => [entry.id, entry.invoice, entry.quoteNumber, entry.quote_number]
    .some(reference => String(reference || "").trim() === String(invoice || "").trim()));
  const references = new Set([invoice, order?.id, order?.invoice, order?.quoteNumber, order?.quote_number]
    .map(reference => String(reference || "").trim()).filter(Boolean));
  return demoState.payments
    .filter(payment => [payment.invoice, payment.orderId, payment.order_id, payment.quoteNumber, payment.quote_number]
      .some(reference => references.has(String(reference || "").trim())))
    .sort((a, b) => new Date(a.date) - new Date(b.date));
}

export function getPaymentSummaryForQuote(quoteNumber) {
  const order = (demoState.orders || []).find(entry => [entry.invoice, entry.quoteNumber, entry.quote_number, entry.id]
    .some(reference => String(reference || "").trim() === String(quoteNumber || "").trim())) || null;
  const orderTotal = parseMoney(order?.amount || order?.totalAmount || order?.total_amount || 0);
  const payments = getInvoicePayments(quoteNumber);
  const paidAmount = payments.filter(isSettledPayment).reduce((sum, payment) => sum + parseMoney(payment.amount), 0);
  const isMarkedPaid = ["paid", "completed", "settled"].includes(String(order?.paymentStatus || order?.status || "").trim().toLowerCase());
  const effectivePaidAmount = isMarkedPaid ? orderTotal : paidAmount;
  const balanceDue = Math.max(orderTotal - effectivePaidAmount, 0);

  return {
    order,
    orderTotal,
    paidAmount: effectivePaidAmount,
    balanceDue,
    latestPayment: [...payments].reverse().find(isSettledPayment) || null
  };
}

export function syncPaymentFormFromQuote(snapshot = null) {
  const quotation = snapshot || demoState.savedQuotations?.[demoState.savedQuotations.length - 1] || null;
  if (!quotation) return;

  const quoteNumberValue = quotation.number || quotation.meta?.quotationNumber || "";
  const customerName = quotation.meta?.customerName || "";
  const summary = getPaymentSummaryForQuote(quoteNumberValue);

  if (paymentQuoteNumber) paymentQuoteNumber.value = quoteNumberValue;
  if (paymentAccountName) paymentAccountName.value = customerName;
  if (paymentAmount) paymentAmount.value = summary.balanceDue > 0 ? String(summary.balanceDue) : "0";
  if (paymentMethod) {
    const order = summary.order;
    const quotation = (demoState.quotations || []).find(item => String(item.quotation_no || "").trim() === String(quoteNumberValue).trim());
    paymentMethod.value = getCustomerSelectedPaymentMethod(quotation?.payment_method, order?.payment, order?.method, order?.paymentMethod, order?.payment_method);
  }
  if (paymentStatus) {
    paymentStatus.value = summary.balanceDue > 0 ? "Partially Paid" : "Paid";
  }
  if (paymentDate && !paymentDate.value) {
    paymentDate.value = new Date().toISOString().slice(0, 10);
  }
  if (paymentReference && !paymentReference.value) paymentReference.value = "";
  if (paymentNotes && !paymentNotes.value) paymentNotes.value = "";
}

export function recordPaymentForQuote({
  quoteNumber,
  customerName,
  amount,
  method,
  status,
  paymentDateValue,
  reference,
  notes
}) {
  const order = (demoState.orders || []).find(entry => [entry.invoice, entry.quoteNumber, entry.quote_number, entry.id]
    .some(reference => String(reference || "").trim() === String(quoteNumber || "").trim()));
  if (!order) {
    return { ok: false, reason: "No saved order was found for that quote." };
  }
  if (!isOrderQuotationConfirmed(order)) {
    return { ok: false, reason: "The customer must confirm the quotation before staff can record payment." };
  }
  const quotation = (demoState.quotations || []).find(item => (
    String(item.quotation_no || "").trim() === String(order.quoteNumber || order.invoice || "").trim()
  ));
  const customerSelectedMethod = getCustomerSelectedPaymentMethod(quotation?.payment_method, order.payment, order.method, order.paymentMethod, order.payment_method);
  if (!customerSelectedMethod) {
    return { ok: false, reason: "Wait for the customer to select a payment method before recording payment." };
  }

  const paymentAmountValue = Math.max(0, parseMoney(amount));
  if (paymentAmountValue <= 0) {
    return { ok: false, reason: "Enter a payment amount greater than zero." };
  }
  const existingPayments = getInvoicePayments(quoteNumber);
  const totalPaidBefore = existingPayments.reduce((sum, payment) => {
    const paymentStatus = String(payment.status || "").trim().toLowerCase();
    return ["paid", "completed", "settled", "partially paid"].includes(paymentStatus)
      ? sum + parseMoney(payment.amount)
      : sum;
  }, 0);
  const orderTotal = parseMoney(order.amount || order.totalAmount || order.total_amount || 0);
  if (orderTotal <= 0 || totalPaidBefore + paymentAmountValue > orderTotal) {
    return { ok: false, reason: "The payment amount exceeds the remaining balance." };
  }
  const totalPaidAfter = totalPaidBefore + paymentAmountValue;
  const remaining = Math.max(orderTotal - totalPaidAfter, 0);
  const resolvedStatus = remaining <= 0
    ? "Paid"
    : "Partially Paid";

  const recordedMethod = getCustomerSelectedPaymentMethod(method, customerSelectedMethod);
  if (String(recordedMethod).toLowerCase() !== String(customerSelectedMethod).toLowerCase()) {
    return { ok: false, reason: "The recorded payment method must match the method selected by the customer." };
  }
  order.payment = customerSelectedMethod;
  order.status = resolvedStatus;
  order.paymentStatus = resolvedStatus;
  demoState.quotations = (demoState.quotations || []).map(quotation => (
    String(quotation.quotation_no || "").trim() === String(order.quoteNumber || order.invoice || "").trim()
      ? { ...quotation, payment_status: resolvedStatus, status: resolvedStatus }
      : quotation
  ));
  const deliveryStatus = String(order.deliveryStatus || "").trim().toLowerCase();
  if (resolvedStatus === "Paid" && ["", "pending payment", "order placed"].includes(deliveryStatus)) {
    order.deliveryStatus = "Preparing";
  } else if (resolvedStatus !== "Paid" && !["shipped", "delivered"].includes(deliveryStatus)) {
    order.deliveryStatus = "Pending payment";
  }
  order.updatedAt = new Date().toISOString();

  // Keep the customer app's payment-request history synchronized with the
  // sales order. The two records share the quotation number but are stored in
  // separate collections, so updating only the order leaves the customer
  // seeing "Pending Review" after staff marks it paid.
  const paymentRequestUpdatedAt = new Date().toISOString();
  demoState.paymentRequests = (demoState.paymentRequests || []).map(request => {
    const requestQuoteNumber = request.quote_number || request.quoteNumber || "";
    if (String(requestQuoteNumber).trim() !== String(quoteNumber).trim()) return request;
    return {
      ...request,
      status: resolvedStatus,
      updated_at: paymentRequestUpdatedAt,
      updatedAt: paymentRequestUpdatedAt
    };
  });

  const paymentRecord = {
    invoice: quoteNumber,
    quoteNumber,
    customer: customerName || order.customer || "Customer",
    amount: paymentAmountValue,
    method: customerSelectedMethod,
    status: resolvedStatus,
    date: paymentDateValue || new Date().toISOString().slice(0, 10),
    reference: reference || "",
    notes: notes || ""
  };

  demoState.payments.unshift(paymentRecord);
  return {
    ok: true,
    paymentRecord,
    remaining
  };
}

export function syncPaymentRequestStatuses() {
  const statusByQuote = new Map();
  (demoState.orders || []).forEach(order => {
    const status = String(order.paymentStatus || order.status || "").trim();
    if (!["paid", "partially paid", "completed", "settled"].includes(status.toLowerCase())) return;
    [order.quoteNumber, order.invoice, order.id].forEach(reference => {
      const quoteNumber = String(reference || "").trim();
      if (quoteNumber) statusByQuote.set(quoteNumber, status);
    });
  });
  let changed = false;
  demoState.paymentRequests = (demoState.paymentRequests || []).map(request => {
    const quoteNumber = String(request.quote_number || request.quoteNumber || "").trim();
    const resolvedStatus = statusByQuote.get(quoteNumber);
    if (!resolvedStatus || request.status === resolvedStatus) return request;
    changed = true;
    return { ...request, status: resolvedStatus, updated_at: new Date().toISOString() };
  });
  return changed;
}

export function renderPaymentBreakdown() {
  const methods = ["Cash", "GCash", "Bank Transfer"];
  const totals = Object.fromEntries(methods.map(method => [method, 0]));
  const grandTotal = demoState.payments.filter(isSettledPayment).reduce((sum, payment) => {
    const amount = parseMoney(payment.amount);
    totals[payment.method] = (totals[payment.method] || 0) + amount;
    return sum + amount;
  }, 0);
  const neutralSlice = document.body.dataset.theme === "dark" ? "#8fa3bf" : "#0f172a";

  const percentages = grandTotal > 0
    ? methods.map(method => Math.round((totals[method] / grandTotal) * 100))
    : [0, 0, 0];
  const sliceAngles = grandTotal > 0
    ? methods.map(method => (totals[method] / grandTotal) * 360)
    : [0, 0, 0];
  const colors = ["#ff8a3d", "#35b9ff", neutralSlice];
  const segments = [];
  let currentAngle = -90;

  sliceAngles.forEach((angle, index) => {
    const size = Math.max(0, angle);
    if (grandTotal <= 0 && index > 0) return;
    const start = currentAngle;
    segments.push({
      color: colors[index],
      start,
      size,
      delay: index * 140
    });
    currentAngle += size;
  });

  if (paymentDonut) {
    paymentDonut.classList.remove("donut-ready");
    void paymentDonut.offsetWidth;
    const donutValue = paymentDonut.querySelector(".donut-center strong");
    if (paymentDonutSegments) {
      paymentDonutSegments.innerHTML = segments.length ? segments.map((segment, index) => `
        <span
          class="donut-segment"
          style="--segment-color:${segment.color}; --segment-start:${segment.start}deg; --segment-size:${segment.size}deg; --segment-delay:${segment.delay}ms; --segment-tilt:${index % 2 === 0 ? -2 : 2}deg"
        ></span>
      `).join("") : "";
    }
    if (donutValue) {
      animateCount(donutValue, grandTotal, formatPeso, 1200, 120);
    }
    requestAnimationFrame(() => {
      paymentDonut.classList.add("donut-ready");
    });
  }

  paymentLegendItems.forEach((item, index) => {
    const label = item.querySelector(".legend-label");
    const strong = item.querySelector("strong");
    if (label) label.textContent = methods[index];
    if (strong) strong.textContent = `${percentages[index] || 0}%`;
  });
}

export function renderPaymentMethodOptions() {
  const modes = demoState.settings.paymentModes.length ? demoState.settings.paymentModes : ["Cash"];
  const paymentMethodSelect = document.getElementById("paymentMethod");
  if (!paymentMethodSelect) return;
  const current = paymentMethodSelect.value || modes[0];
  paymentMethodSelect.innerHTML = modes.map(mode => `<option value="${escapeHtml(mode)}">${escapeHtml(mode)}</option>`).join("");
  paymentMethodSelect.value = modes.includes(current) ? current : modes[0];
}

export function renderPayments() {
  renderDashboardMetrics();
  renderPaymentBreakdown();
}


