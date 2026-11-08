import { demoState } from "../core/state.js";
import {
  loginScreen,
  appShell,
  navItems,
  nav,
  navIndicator,
  panelEditorTitle,
  panelEditorModal,
  panelAvailabilityDisplay,
  salesMonth,
  salesYear,
  salesViewMonthStrip,
  salesViewYear
} from "../core/dom.js";
import { getMonthNames, getShortMonthNames, normalizeAvailability, availabilityClass, formatPeso, isOrderReadyForStaff } from "../core/utils.js";
import { salesViewState } from "../core/runtime.js";

export function positionNavIndicator(viewName) {
  if (!nav || !navIndicator) return;
  const activeItem = navItems.find(item => item.dataset.view === viewName) || navItems[0];
  if (!activeItem) return;

  navIndicator.style.opacity = "1";
  navIndicator.style.height = `${activeItem.offsetHeight}px`;
  navIndicator.style.transform = `translateY(${activeItem.offsetTop}px)`;
}

export function openPanelEditor(title) {
  panelEditorTitle.textContent = title;
  panelEditorModal.classList.remove("hidden");
  panelEditorModal.setAttribute("aria-hidden", "false");
}

export function closePanelEditor() {
  panelEditorModal.classList.add("hidden");
  panelEditorModal.setAttribute("aria-hidden", "true");
}

export function applyTheme(theme = demoState.settings.themeMode || "light") {
  const resolvedTheme = theme === "dark" ? "dark" : "light";
  document.body.dataset.theme = resolvedTheme;
  demoState.settings.themeMode = resolvedTheme;
}

export function populateSalesSelectors() {
  const currentYear = new Date().getFullYear();
  const monthLabels = getMonthNames();
  const selectedMonth = salesMonth?.value ?? "";
  const selectedYear = salesYear?.value ?? "";

  if (salesMonth) {
    salesMonth.innerHTML = monthLabels.map((month, index) => `
      <option value="${index}">${month}</option>
    `).join("");
    if (selectedMonth !== "" && salesMonth.querySelector(`option[value="${selectedMonth}"]`)) {
      salesMonth.value = selectedMonth;
    }
  }

  if (salesYear) {
    const years = Array.from({ length: 5 }, (_, index) => currentYear - 2 + index);
    salesYear.innerHTML = years.map(year => `
      <option value="${year}">${year}</option>
    `).join("");
    if (selectedYear !== "" && salesYear.querySelector(`option[value="${selectedYear}"]`)) {
      salesYear.value = selectedYear;
    }
  }
}

export function populateSalesViewControls() {
  const today = new Date();
  const currentYear = today.getFullYear();
  const monthLabels = getShortMonthNames();
  const selectedMonthIndex = Number.isInteger(salesViewState.monthIndex) ? salesViewState.monthIndex : null;
  const selectedYear = Number.isInteger(salesViewState.year) ? salesViewState.year : currentYear;

  if (salesViewMonthStrip) {
    salesViewMonthStrip.innerHTML = monthLabels.map((month, index) => `
      <button
        type="button"
        class="month-chip${selectedMonthIndex === index ? " active" : ""}"
        data-month-index="${index}"
        aria-pressed="${selectedMonthIndex === index ? "true" : "false"}"
      >
        ${month.slice(0, 3)}
      </button>
    `).join("");
  }

  if (salesViewYear) {
    const years = Array.from({ length: 5 }, (_, index) => currentYear - 2 + index);
    salesViewYear.innerHTML = [
      '<option value="">All Years</option>',
      ...years.map(year => `
        <option value="${year}" ${year === selectedYear ? "selected" : ""}>${year}</option>
      `)
    ].join("");
    salesViewYear.value = Number.isInteger(salesViewState.year) ? String(salesViewState.year) : "";
  }
}

export function getSelectedSalesPeriod() {
  const today = new Date();
  const monthIndex = salesMonth ? Number(salesMonth.value) : today.getMonth();
  const year = salesYear ? Number(salesYear.value) : today.getFullYear();

  return {
    monthIndex: Number.isFinite(monthIndex) ? monthIndex : today.getMonth(),
    year: Number.isFinite(year) ? year : today.getFullYear()
  };
}

export function getSalesViewPeriod() {
  const today = new Date();
  return {
    monthIndex: Number.isInteger(salesViewState.monthIndex) ? salesViewState.monthIndex : today.getMonth(),
    year: Number.isInteger(salesViewState.year) ? salesViewState.year : today.getFullYear()
  };
}

export function updateAvailabilityDisplay(quantity) {
  if (!panelAvailabilityDisplay) return;

  const availability = normalizeAvailability(quantity);
  panelAvailabilityDisplay.textContent = availability;
  panelAvailabilityDisplay.dataset.state = availability.toLowerCase().replaceAll(" ", "-");
  panelAvailabilityDisplay.classList.remove("in-stock", "low-stock", "out-stock");
  panelAvailabilityDisplay.classList.add(availabilityClass(availability));
}

export function renderDashboardMetrics(selectedSalesTotal = null) {
  const totalSales = selectedSalesTotal == null
    ? demoState.payments
      .filter(payment => ["paid", "completed", "settled"].includes(String(payment.status || "").trim().toLowerCase()))
      .reduce((sum, payment) => sum + Number(String(payment.amount).replace(/[^\d.-]/g, "")), 0)
    : selectedSalesTotal;
  const paidOrders = demoState.orders.filter(isOrderReadyForStaff).length;
  const pendingSavedQuotes = (demoState.savedQuotations || []).filter(savedQuote => {
    const quoteNumber = String(savedQuote.number || savedQuote.meta?.quotationNumber || "").trim();
    const order = (demoState.orders || []).find(entry => (
      String(entry.invoice || entry.quoteNumber || "").trim() === quoteNumber
    ));
    return !order || !["paid", "closed won"].includes(String(order.status || "").trim().toLowerCase());
  }).length;
  const pendingQuotes = demoState.sales.filter(sale => sale.stage !== "Closed Won").length + pendingSavedQuotes;
  const lowStock = demoState.inventory.filter(item => Number(item.quantity) <= 5).length;

  const totalSalesNode = document.getElementById("totalSales");
  const paidOrdersNode = document.getElementById("paidOrders");
  const pendingQuotesNode = document.getElementById("pendingQuotes");
  const lowStockNode = document.getElementById("lowStock");

  if (totalSalesNode) totalSalesNode.textContent = formatPeso(totalSales);
  if (paidOrdersNode) paidOrdersNode.textContent = String(paidOrders);
  if (pendingQuotesNode) pendingQuotesNode.textContent = String(pendingQuotes);
  if (lowStockNode) lowStockNode.textContent = String(lowStock);
}
