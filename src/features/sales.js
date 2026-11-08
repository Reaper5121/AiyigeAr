import { demoState } from "../core/state.js";
import {
  salesTable,
  salesChart,
  salesPeriodLabel,
  salesTodayValue,
  salesTodayMeta,
  salesWeekValue,
  salesWeekMeta,
  salesMonthValue,
  salesMonthMeta,
  salesDayModal,
  salesDayModalPeriod,
  salesDayLabel,
  salesDayCount,
  salesDayTotal,
  salesDayPicker,
  salesDayTransactions
} from "../core/dom.js";
import {
  getMonthNames,
  getShortMonthNames,
  formatPeso,
  formatCurrencyK,
  parseMoney,
  formatShortDate,
  addDays,
  pad2,
  getDaysInMonth,
  escapeHtml,
  paymentClass
} from "../core/utils.js";
import { salesViewState, salesDayState } from "../core/runtime.js";
import { getSalesViewPeriod, getSelectedSalesPeriod, renderDashboardMetrics } from "./ui.js";

function getPaymentsForPeriod(monthIndex, year) {
  const monthPrefix = `${year}-${pad2(monthIndex + 1)}-`;
  return demoState.payments.filter(payment => payment.date.startsWith(monthPrefix));
}

function getSalesTotalForPeriod(monthIndex, year) {
  return getPaymentsForPeriod(monthIndex, year).reduce((sum, payment) => sum + parseMoney(payment.amount), 0);
}

function getSalesTotalForDate(year, monthIndex, day) {
  const isoDate = `${year}-${pad2(monthIndex + 1)}-${pad2(day)}`;
  return demoState.payments
    .filter(payment => payment.date === isoDate)
    .reduce((sum, payment) => sum + parseMoney(payment.amount), 0);
}

function getSalesTotalForRange(startDate, endDate) {
  const start = startDate instanceof Date ? startDate : new Date(startDate);
  const end = endDate instanceof Date ? endDate : new Date(endDate);

  if (Number.isNaN(start.getTime()) || Number.isNaN(end.getTime())) {
    return 0;
  }

  const startIso = start.toISOString().slice(0, 10);
  const endIso = end.toISOString().slice(0, 10);

  return demoState.payments
    .filter(payment => payment.date >= startIso && payment.date <= endIso)
    .reduce((sum, payment) => sum + parseMoney(payment.amount), 0);
}

function getSalesTransactionsForRange(startDate, endDate) {
  const start = startDate instanceof Date ? startDate : new Date(startDate);
  const end = endDate instanceof Date ? endDate : new Date(endDate);

  if (Number.isNaN(start.getTime()) || Number.isNaN(end.getTime())) {
    return [];
  }

  const startIso = start.toISOString().slice(0, 10);
  const endIso = end.toISOString().slice(0, 10);
  return demoState.payments.filter(payment => payment.date >= startIso && payment.date <= endIso);
}

function getSalesWindow(monthIndex, year, span = 6) {
  const monthLabels = getShortMonthNames();
  const values = [];

  for (let offset = span - 1; offset >= 0; offset -= 1) {
    const current = new Date(Number(year), Number(monthIndex), 1);
    current.setMonth(current.getMonth() - offset);
    const currentYear = current.getFullYear();
    const currentMonth = current.getMonth();
    const amount = getSalesTotalForPeriod(currentMonth, currentYear);
    values.push({
      label: `${monthLabels[currentMonth]} '${String(currentYear).slice(-2)}`,
      year: currentYear,
      value: Math.round(amount / 1000)
    });
  }

  return values;
}

function getSelectedSalesDay(monthIndex, year) {
  const today = new Date();
  if (salesDayState.year === year && salesDayState.monthIndex === monthIndex && Number.isInteger(salesDayState.day)) {
    return salesDayState.day;
  }
  if (today.getFullYear() === year && today.getMonth() === monthIndex) {
    return today.getDate();
  }
  return 1;
}

export function getSalesTransactionsForPeriod(monthIndex, year) {
  const monthPrefix = `${year}-${pad2(monthIndex + 1)}-`;

  return demoState.payments
    .filter(payment => payment.date.startsWith(monthPrefix))
    .map(payment => {
      const order = demoState.orders.find(entry => entry.invoice === payment.invoice);
      return {
        date: payment.date,
        invoice: payment.invoice,
        customer: order?.customer || "Walk-in",
        service: order?.products || "Payment recorded",
        method: payment.method,
        amount: parseMoney(payment.amount),
        status: payment.status
      };
    })
    .sort((a, b) => b.date.localeCompare(a.date) || b.invoice.localeCompare(a.invoice));
}

export function getSalesTransactionsForDay(year, monthIndex, day) {
  const isoDate = `${year}-${pad2(monthIndex + 1)}-${pad2(day)}`;
  return demoState.payments
    .filter(payment => payment.date === isoDate)
    .map(payment => {
      const order = demoState.orders.find(entry => entry.invoice === payment.invoice);
      return {
        date: payment.date,
        invoice: payment.invoice,
        customer: order?.customer || "Walk-in",
        method: payment.method,
        amount: parseMoney(payment.amount),
        status: payment.status,
        details: order?.products || "Payment recorded"
      };
    })
    .sort((a, b) => a.invoice.localeCompare(b.invoice));
}

export function animateCount(element, target, formatter, duration = 1000, delay = 0) {
  if (!element) return;
  element.textContent = formatter(0);
  const startAt = performance.now() + delay;

  const tick = now => {
    if (now < startAt) {
      requestAnimationFrame(tick);
      return;
    }

    const elapsed = Math.min(1, (now - startAt) / duration);
    const eased = 1 - Math.pow(1 - elapsed, 3);
    element.textContent = formatter(Math.round(target * eased));

    if (elapsed < 1) {
      requestAnimationFrame(tick);
    }
  };

  requestAnimationFrame(tick);
}

export function renderSalesDayModal() {
  if (!salesDayModal || !salesDayPicker || !salesDayTransactions) return;

  const { monthIndex, year } = getSalesViewPeriod();
  const today = new Date();
  const daysInMonth = getDaysInMonth(year, monthIndex);
  const savedDay = salesDayState.year === year && salesDayState.monthIndex === monthIndex ? salesDayState.day : null;
  const fallbackDay = year === today.getFullYear() && monthIndex === today.getMonth() ? today.getDate() : 1;
  const day = Math.min(daysInMonth, Math.max(1, savedDay || fallbackDay));

  salesDayState.monthIndex = monthIndex;
  salesDayState.year = year;
  salesDayState.day = day;

  if (salesDayModalPeriod) {
    salesDayModalPeriod.textContent = `${getMonthNames()[monthIndex]} ${year}`;
  }
  if (salesDayLabel) {
    salesDayLabel.textContent = `Day ${day}`;
  }

  const transactions = getSalesTransactionsForDay(year, monthIndex, day);
  const total = transactions.reduce((sum, transaction) => sum + transaction.amount, 0);

  if (salesDayCount) {
    salesDayCount.textContent = String(transactions.length);
  }
  if (salesDayTotal) {
    salesDayTotal.textContent = formatPeso(total);
  }

  salesDayPicker.innerHTML = Array.from({ length: daysInMonth }, (_, index) => {
    const currentDay = index + 1;
    const active = currentDay === day ? " active" : "";
    const txCount = getSalesTransactionsForDay(year, monthIndex, currentDay).length;
    return `
      <button type="button" class="day-chip${active}" data-day="${currentDay}">
        <strong>${currentDay}</strong>
        <small>${txCount} tx</small>
      </button>
    `;
  }).join("");

  salesDayTransactions.innerHTML = transactions.length ? transactions.map(transaction => `
    <tr>
      <td>${formatShortDate(transaction.date)}</td>
      <td>${escapeHtml(transaction.invoice)}</td>
      <td>${escapeHtml(transaction.customer)}</td>
      <td>${escapeHtml(transaction.method)}</td>
      <td>${formatPeso(transaction.amount)}</td>
      <td><span class="pill ${paymentClass(transaction.status)}">${escapeHtml(transaction.status)}</span></td>
      <td>${escapeHtml(transaction.details)}</td>
    </tr>
  `).join("") : `
    <tr>
      <td colspan="7" class="empty-row">No transactions recorded for this day.</td>
    </tr>
  `;
}

export function openSalesDayModal() {
  if (!salesDayModal) return;
  renderSalesDayModal();
  salesDayModal.classList.remove("hidden");
  salesDayModal.setAttribute("aria-hidden", "false");
}

export function closeSalesDayModal() {
  if (!salesDayModal) return;
  salesDayModal.classList.add("hidden");
  salesDayModal.setAttribute("aria-hidden", "true");
}

export function renderSalesPerformanceView() {
  const { monthIndex, year } = getSalesViewPeriod();
  const allYearsMode = !Number.isInteger(salesViewState.year);
  if (salesPeriodLabel) {
    salesPeriodLabel.textContent = allYearsMode
      ? `${salesViewState.monthIndex == null ? "All Months" : getMonthNames()[monthIndex]} | All Years`
      : `${getMonthNames()[monthIndex]} ${year}`;
  }

  const selectedDay = getSelectedSalesDay(monthIndex, year);
  const selectedDate = new Date(year, monthIndex, selectedDay);
  const weekStart = addDays(selectedDate, -6);
  const previousWeekStart = addDays(selectedDate, -13);
  const previousWeekEnd = addDays(selectedDate, -7);
  const todayValue = getSalesTotalForDate(year, monthIndex, selectedDay);
  const weekValue = getSalesTotalForRange(weekStart, selectedDate);
  const previousWeekValue = getSalesTotalForRange(previousWeekStart, previousWeekEnd);
  const monthValue = getSalesTotalForPeriod(monthIndex, year);
  const todayTransactions = getSalesTransactionsForDay(year, monthIndex, selectedDay).length;
  const weekTransactions = getSalesTransactionsForRange(weekStart, selectedDate).length;
  const monthTransactions = getSalesTransactionsForPeriod(monthIndex, year).length;
  const leadRate = monthTransactions > 0
    ? Math.round((todayTransactions / monthTransactions) * 100)
    : 0;
  const weekChange = previousWeekValue > 0
    ? Math.round(((weekValue - previousWeekValue) / previousWeekValue) * 100)
    : (weekValue > 0 ? 100 : 0);

  if (salesTodayValue) salesTodayValue.textContent = formatPeso(todayValue);
  if (salesTodayMeta) salesTodayMeta.textContent = `${todayTransactions} transactions`;
  if (salesWeekValue) salesWeekValue.textContent = formatPeso(weekValue);
  if (salesWeekMeta) {
    salesWeekMeta.textContent = weekTransactions
      ? `${weekChange >= 0 ? "Up" : "Down"} ${Math.abs(weekChange)}% from last week`
      : "No activity this week";
  }
  if (salesMonthValue) salesMonthValue.textContent = formatPeso(monthValue);
  if (salesMonthMeta) {
    salesMonthMeta.textContent = monthTransactions
      ? `${monthTransactions} transactions this month`
      : "No sales recorded";
  }

  const leadConversionNode = document.querySelector(".chip-row .warning");
  if (leadConversionNode) {
    leadConversionNode.textContent = `Lead Conversion ${leadRate}%`;
  }

  renderSalesActivityTable(monthIndex, year);

  if (salesDayModal && !salesDayModal.classList.contains("hidden")) {
    renderSalesDayModal();
  }
}

export function renderSalesActivityTable(monthIndex, year) {
  if (!salesTable) return;

  const transactions = getSalesTransactionsForPeriod(monthIndex, year);

  salesTable.innerHTML = transactions.length ? transactions.map(transaction => {
    const customerCell = transaction.customer === "Walk-in"
      ? "Walk-in"
      : `<button type="button" class="table-link sales-customer-link" data-customer="${escapeHtml(transaction.customer)}">${escapeHtml(transaction.customer)}</button>`;

    return `
      <tr>
        <td>${formatShortDate(transaction.date)}</td>
        <td>${escapeHtml(transaction.invoice)}</td>
        <td>${customerCell}</td>
        <td>${escapeHtml(transaction.service)}</td>
        <td>${escapeHtml(transaction.method)}</td>
        <td>${formatPeso(transaction.amount)}</td>
        <td><span class="pill ${paymentClass(transaction.status)}">${escapeHtml(transaction.status)}</span></td>
      </tr>
    `;
  }).join("") : `
    <tr>
      <td colspan="7" class="empty-row">No sales recorded for this period.</td>
    </tr>
  `;
}

export function renderSalesTrend() {
  const { monthIndex, year } = getSelectedSalesPeriod();
  const data = getSalesWindow(monthIndex, year, 6);

  if (salesChart) {
    salesChart.classList.remove("sales-chart-ready");
    void salesChart.offsetWidth;
    salesChart.innerHTML = data.map((point, index) => `
        <div class="bar" style="--delay:${index * 90}ms">
        <div class="bar-value" data-target="${point.value}">${formatCurrencyK(0)}</div>
        <div class="bar-track">
          <div class="bar-fill" style="--target-height:${point.value > 0 ? Math.max(18, Math.min(240, point.value * 3.8)) : 0}px; --delay:${index * 90}ms"></div>
        </div>
        <div class="bar-label">${point.label}</div>
      </div>
    `).join("");

    requestAnimationFrame(() => {
      salesChart.classList.add("sales-chart-ready");
      salesChart.querySelectorAll(".bar-value").forEach((label, index) => {
        const target = Number(label.dataset.target) || 0;
        animateCount(label, target, value => formatCurrencyK(value), 980, index * 90);
      });
    });
  }

  renderDashboardMetrics();
}
