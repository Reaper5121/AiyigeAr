import {
  demoState,
  firstGroup,
  findCatalogEntry,
  findCatalogMatchForItem,
  findCatalogEntryForItem,
  resolveCatalogCategoryGroup,
  loadState,
  refreshStateFromBackend,
  saveState
} from "../core/state.js";
import {
  loginScreen,
  appShell,
  loginForm,
  firstAdminSetup,
  firstAdminSetupForm,
  logoutBtn,
  navItems,
  nav,
  navIndicator,
  contents,
  pageTitle,
  profileName,
  profileRole,
  inventoryTable,
  newItemBtn,
  salesTable,
  quotePanel,
  quoteFirstName,
  quoteMiddleInitial,
  quoteLastName,
  quoteStreetAddress,
  quoteCityProvince,
  quotePostalCode,
  quoteContactNumber,
  quoteEmailAddress,
  quoteCustomerWants,
  quoteNumber,
  quoteDateIssued,
  quoteValidUntil,
  quoteSource,
  quoteDeliveryCost,
  quoteCourier,
  quoteTerms,
  quoteUnitPrice,
  quoteUom,
  quotePreparedBy,
  quoteRemarks,
  quoteQty,
  quoteDiscount,
  quoteTable,
  addQuoteItemBtn,
  generateQuotePdfBtn,
  printQuoteBtn,
  saveQuotationBtn,
  clearQuotationBtn,
  backQuotationBtn,
  paymentForm,
  paymentQuoteNumber,
  paymentAccountName,
  paymentAmount,
  paymentMethod,
  paymentStatus,
  paymentDate,
  paymentReference,
  paymentNotes,
  markQuotePaidBtn,
  salesMonth,
  salesYear,
  salesViewMonthStrip,
  salesViewYear,
  salesTodayCard,
  closeSalesDayModalBtn,
  salesDayModal,
  salesDayPicker,
  inventorySearch,
  inventoryCategoryFilter,
  inventoryTypeFilter,
  inventoryAvailabilityFilter,
  roomCaptureGrid,
  roomCaptureRefreshBtn,
  roomCaptureDeleteAllBtn,
  staffManagementPanel,
  openStaffModalBtn,
  staffModal,
  staffModalTitle,
  staffModalCopy,
  staffModalSubmitBtn,
  closeStaffModalBtn,
  staffEditingIdInput,
  staffModeInput,
  panelCategoryGroup,
  panelCategoryName,
  panelProductCode,
  panelProductName,
  panelLength,
  panelWidth,
  panelThickness,
  panelUnitsPerBox,
  panelPriceUnit,
  panelImageFile,
  panelImageReplaceBtn,
  panelModelFile,
  panelModelReplaceBtn,
  panelQuantity,
  panelEditorModal,
  closePanelEditorBtn,
  paymentDonut,
  paymentLegendItems,
  themeToggle,
  languageSelect,
  currencySelect,
  dateFormatSelect,
  unitSelect,
  resetFiltersBtn,
  staffAccountForm,
  staffUsernameInput,
  staffDisplayNameInput,
  staffPasswordInput,
  staffEmailInput,
  staffStatusInput,
  staffRoleInput,
  userSearchInput,
  userRoleFilter,
  userStatusFilter,
  userDateSort,
  exportUsersBtn,
  deleteModal,
  deleteModalText,
  cancelDeleteBtn,
  confirmDeleteBtn,
  kpiCards
} from "../core/dom.js";
import { normalizeInventoryRecords, parsePanelDimensions, parseMoney, escapeHtml, getCustomerSelectedPaymentMethod } from "../core/utils.js";
import { appState, salesViewState, salesDayState } from "../core/runtime.js";
import {
  authenticateUser,
  canAccessView,
  canManageInventory,
  canManagePayments,
  canManageQuotations,
  canManageSettings,
  canManageRoomCaptures,
  createStaffAccount,
  updateStaffAccount,
  deleteStaffAccount,
  getDefaultViewForRole,
  getCurrentUserDisplayName,
  getRoleLabel,
  resolveUserSession,
  recordUserActivity
} from "../core/permissions.js";
import {
  positionNavIndicator,
  openPanelEditor,
  closePanelEditor,
  applyTheme,
  populateSalesSelectors,
  populateSalesViewControls,
  updateAvailabilityDisplay
} from "./ui.js";
import {
  renderSalesTrend,
  renderSalesPerformanceView,
  openSalesDayModal,
  closeSalesDayModal,
  renderSalesDayModal
} from "./sales.js";
import {
  renderInventory,
  renderCategoryGroupOptions,
  renderProductTypeOptions,
  renderInventoryFilters,
  categoryLabel,
  resetPanelEditor,
  setPanelImage,
  setPanelModel,
  getPanelImageSource
} from "./inventory.js";
import {
  renderPayments,
  renderPaymentMethodOptions,
  syncPaymentFormFromQuote,
  recordPaymentForQuote
} from "./payments.js";
import {
  renderQuoteSummary,
  getGeneratedQuotationPaymentState,
  canPrintGeneratedQuotation,
  exportCurrentQuotation,
  syncQuotationDraftDefaults,
  setCurrentQuotationMeta,
  getCurrentQuotationMeta,
  applyCustomerQuotationDefaults,
  saveQuotationDraft,
  restoreQuotationForCustomer,
  updateQuotationItemPreview,
  updateQuotationItem,
  removeQuotationItem,
  resetQuotationDraft
} from "./quotes.js?v=20261104-required-email-1";
import {
  renderOrders,
  renderCourierOrders,
  renderSalesManagerDashboard,
  renderPaymentOperations,
  renderSettingsPanel,
  renderAppSections,
  applySettings,
  renderInventoryStaffDashboard,
  exportUsersCsv
} from "./dashboard.js?v=20261104-required-email-1";
import { getDeleteMessage, getTranslation } from "../core/i18n.js";

let sessionTimeoutId = null;
let sharedStateRefreshTimer = null;
let sessionListenersBound = false;
let activeStaffEditId = null;
let toastHost = null;
let toastSequence = 0;

function updateConnectionStatus() {
  const status = document.getElementById("connectionStatus");
  const note = document.getElementById("connectionStatusNote");
  if (!status || !note) return;

  const online = navigator.onLine;
  status.textContent = online ? "Online" : "Offline";
  status.classList.toggle("success", online);
  status.classList.toggle("offline", !online);
  note.textContent = online ? "Network connection available." : "No network connection.";
}

function getCurrentRole() {
  return String(appState.currentUser?.role || "guest").trim().toLowerCase() || "guest";
}

function getToastHost() {
  if (toastHost) {
    return toastHost;
  }

  toastHost = document.createElement("div");
  toastHost.className = "toast-stack";
  toastHost.setAttribute("aria-live", "polite");
  toastHost.setAttribute("aria-atomic", "true");
  document.body.appendChild(toastHost);
  return toastHost;
}

function showToast(message, variant = "info") {
  const text = String(message || "").trim();
  if (!text) return;

  const host = getToastHost();
  const toast = document.createElement("div");
  const id = `toast-${++toastSequence}`;
  toast.className = `toast toast-${variant}`;
  toast.setAttribute("role", variant === "danger" ? "alert" : "status");
  toast.setAttribute("id", id);
  toast.innerHTML = `<span class="toast-message"></span>`;
  toast.querySelector(".toast-message").textContent = text;
  host.appendChild(toast);

  window.requestAnimationFrame(() => {
    toast.classList.add("toast-visible");
  });

  const dismiss = window.setTimeout(() => {
    toast.classList.remove("toast-visible");
    window.setTimeout(() => {
      toast.remove();
      if (host && !host.children.length) {
        host.remove();
        toastHost = null;
      }
    }, 220);
  }, variant === "danger" ? 4200 : 2800);

  toast.addEventListener("click", () => {
    window.clearTimeout(dismiss);
    toast.classList.remove("toast-visible");
    window.setTimeout(() => toast.remove(), 220);
  });
}

function showActionDialog(title, message) {
  let dialog = document.getElementById("actionInfoDialog");
  if (!dialog) {
    dialog = document.createElement("dialog");
    dialog.id = "actionInfoDialog";
    dialog.className = "action-info-dialog";
    dialog.innerHTML = `<h2></h2><p></p><button class="primary-btn" type="button">OK</button>`;
    dialog.querySelector("button").addEventListener("click", () => dialog.close());
    dialog.addEventListener("click", event => {
      if (event.target === dialog) dialog.close();
    });
    document.body.appendChild(dialog);
  }
  dialog.querySelector("h2").textContent = title;
  dialog.querySelector("p").textContent = message;
  if (!dialog.open) dialog.showModal();
}

function setupPersistenceLoadingIndicator() {
  const indicator = document.createElement("div");
  indicator.id = "globalLoadingIndicator";
  indicator.className = "global-loading-indicator";
  indicator.hidden = true;
  indicator.setAttribute("role", "status");
  indicator.setAttribute("aria-live", "polite");
  indicator.innerHTML = `<span class="loading-spinner" aria-hidden="true"></span><span class="loading-label"></span>`;
  document.body.appendChild(indicator);

  let pending = 0;
  let showTimer = 0;
  window.addEventListener("aiyige:persistence-loading", event => {
    const { phase, label } = event.detail || {};
    if (phase === "start") {
      pending += 1;
      indicator.querySelector(".loading-label").textContent = label || "Working…";
      if (pending === 1) {
        showTimer = window.setTimeout(() => { indicator.hidden = false; }, 160);
      }
      return;
    }
    pending = Math.max(0, pending - 1);
    if (pending === 0) {
      window.clearTimeout(showTimer);
      indicator.hidden = true;
    }
  });
}

function printGeneratedQuotation(order) {
  const quotation = (demoState.quotations || []).find(item => (
    String(item.order_id || "") === String(order.id || "") ||
    String(item.quotation_no || "") === String(order.quoteNumber || order.invoice || "")
  ));
  if (!quotation) {
    showActionDialog("Quotation not ready to print", "This order does not have a customer quotation yet. Wait for the customer to confirm the quotation in the mobile app.");
    return;
  }
  const linkedOrderId = String(quotation.order_id || "").trim();
  if (!linkedOrderId || ![order.id, order.invoice].some(reference => String(reference || "").trim() === linkedOrderId) || !quotation.customer_confirmed_at) {
    showActionDialog("Waiting for customer confirmation", "Please wait for the customer to confirm the order in the mobile app before printing this quotation.");
    return;
  }
  if (!getCustomerSelectedPaymentMethod(quotation.payment_method, order.payment, order.method, order.paymentMethod, order.payment_method)) {
    showActionDialog("Payment method not confirmed", "Please wait for the customer to select and confirm a payment method in the mobile app.");
    return;
  }
  const payment = getGeneratedQuotationPaymentState(quotation, order);
  if (!payment.complete) {
    showActionDialog("Payment still pending", "Printing for preparation is available after the customer's full payment has been received and recorded.");
    return;
  }
  const items = Array.isArray(quotation.items_snapshot) ? quotation.items_snapshot : [];
  const paymentStatus = String(order.paymentStatus || quotation.payment_status || "Payment pending");
  const savedBusinessName = String(quotation.business?.name || "").trim();
  const businessName = !savedBusinessName || savedBusinessName.toLowerCase() === "ar panels"
    ? "Aiyige Hardware & Construction"
    : savedBusinessName;
  const money = value => `\u20B1${Number(value || 0).toLocaleString("en-PH", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  const popup = window.open("", "ar-panels-quotation", "width=900,height=1100");
  if (!popup) {
    showToast("Allow pop-ups to print the quotation.", "danger");
    return;
  }
  popup.document.write(`<!doctype html><html><head><title>${escapeHtml(quotation.quotation_no)} - Preparation copy</title><style>
    @page{size:A4;margin:16mm}body{font-family:Arial,sans-serif;color:#202124;margin:0}header{border-bottom:3px solid #f57419;padding-bottom:14px;margin-bottom:22px}h1{margin:0 0 4px;color:#c65300}h2{margin:0 0 18px}.meta{display:grid;grid-template-columns:1fr 1fr;gap:8px 24px;margin-bottom:20px}.meta strong{display:block;color:#777;font-size:11px;text-transform:uppercase}.meta span{display:block;font-size:14px}table{width:100%;border-collapse:collapse;margin:18px 0}th,td{border-bottom:1px solid #ddd;padding:10px 6px;text-align:left}th{background:#fff1e3;color:#8b3b08}td:nth-child(n+3),th:nth-child(n+3){text-align:right}.totals{margin-left:auto;width:280px}.totals div{display:flex;justify-content:space-between;padding:5px 0}.grand{border-top:2px solid #202124;font-size:18px;font-weight:bold;padding-top:10px!important}.terms{display:none}.notes{display:none}footer{margin-top:40px;text-align:center;color:#777;font-size:11px}@media print{.no-print{display:none}}
  </style></head><body><header><h1>${escapeHtml(businessName)}</h1><div>${escapeHtml(quotation.business?.address || "")}</div><div>${escapeHtml(quotation.business?.contact || "")}</div></header><h2>Order preparation copy</h2><p>Quotation issued by the mobile app and confirmed by the customer.</p><div class="meta"><div><strong>Quotation no.</strong><span>${escapeHtml(quotation.quotation_no)}</span></div><div><strong>Date issued</strong><span>${escapeHtml(String(quotation.created_at || "").slice(0,10))}</span></div><div><strong>Customer</strong><span>${escapeHtml(quotation.customer?.name || "Customer")}</span></div><div><strong>Customer confirmation</strong><span>Confirmed</span></div><div><strong>Contact</strong><span>${escapeHtml(quotation.customer?.contact || "")}</span></div><div><strong>Payment status</strong><span>${escapeHtml(paymentStatus)}</span></div><div><strong>Delivery address</strong><span>${escapeHtml(quotation.customer?.deliveryAddress || "")}</span></div><div><strong>Courier</strong><span>${escapeHtml(quotation.courier || "Not selected by customer")}</span></div></div><table><thead><tr><th>Panel model</th><th>Size / finish</th><th>Qty</th><th>Unit price</th><th>Line total</th></tr></thead><tbody>${items.map(item => `<tr><td>${escapeHtml(item.panelModel)}</td><td>${escapeHtml([item.size,item.finish].filter(Boolean).join(" / "))}</td><td>${item.quantity}</td><td>${money(item.unitPrice)}</td><td>${money(item.lineTotal)}</td></tr>`).join("")}</tbody></table><div class="totals"><div><span>Subtotal</span><strong>${money(quotation.subtotal)}</strong></div><div class="grand"><span>GRAND TOTAL</span><strong>${money(quotation.total)}</strong></div></div><div class="terms">${escapeHtml(quotation.terms || "")}</div><footer>Printed for staff item preparation. The quotation was issued through the Aiyige mobile app.</footer><script>window.onload=()=>{window.print()}</script></body></html>`);
  popup.document.close();
  const printedAt = new Date().toISOString();
  quotation.printed_at = printedAt;
  order.quotationPrintedAt = printedAt;
  saveState();
  showToast("Order preparation copy printed.", "success");
}

function applyCurrentAccessRules() {
  const currentRole = getCurrentRole();
  appShell.classList.toggle("owner-readonly", currentRole === "owner");
  appShell.classList.toggle("admin-limited", currentRole === "admin");
  appShell.classList.toggle("sales-readonly", currentRole === "sales_staff");
  appShell.classList.toggle("inventory-staff-mode", currentRole === "inventory_staff");
  appShell.classList.toggle("admin-mode", currentRole === "admin");
  appShell.classList.toggle("sales-staff-mode", currentRole === "sales_staff");
  appShell.classList.toggle("sales-manager-mode", currentRole === "sales_manager");
  appShell.classList.toggle("owner-mode", currentRole === "owner");
  navItems.forEach(item => {
    const allowed = canAccessView(item.dataset.view);
    item.classList.toggle("hidden", !allowed);
    item.toggleAttribute("disabled", !allowed);
    item.setAttribute("aria-hidden", allowed ? "false" : "true");
    item.tabIndex = allowed ? 0 : -1;
  });

  if (newItemBtn) {
    newItemBtn.classList.toggle("hidden", !canManageInventory());
  }

  if (roomCaptureDeleteAllBtn) {
    roomCaptureDeleteAllBtn.classList.toggle("hidden", !canManageRoomCaptures());
  }

  if (staffManagementPanel) {
    staffManagementPanel.classList.toggle("hidden", !canManageSettings() && currentRole !== "owner");
  }
  if (openStaffModalBtn) openStaffModalBtn.classList.toggle("hidden", !canManageSettings());

  if (profileName) {
    profileName.textContent = getCurrentUserDisplayName();
  }
  if (profileRole) {
    profileRole.textContent = getRoleLabel(currentRole);
  }
}

function getSessionTimeoutMs() {
  const minutes = Number(demoState.settings.sessionTimeoutMinutes);
  return Math.max(1, Number.isFinite(minutes) ? minutes : 30) * 60_000;
}

async function startAuthenticatedSession(authenticatedUser) {
  appState.currentUser = authenticatedUser;
  appState.currentUser.lastActiveAt = new Date().toISOString();
  await recordUserActivity(authenticatedUser.id);
  loginScreen.classList.add("hidden");
  appShell.classList.remove("hidden");
  applyCurrentAccessRules();
  if (String(authenticatedUser.role || "").toLowerCase() === "customer") {
    syncCustomerSessionContext();
  } else {
    clearStaffQuotationSession();
  }
  showView(getDefaultViewForRole(authenticatedUser.role));
  renderAppSections();
  startSessionTimer();
}

function stopSessionTimer() {
  if (sessionTimeoutId) {
    clearTimeout(sessionTimeoutId);
    sessionTimeoutId = null;
  }
}

async function logoutCurrentUser() {
  if (appState.currentUser?.id != null) {
    await recordUserActivity(appState.currentUser.id);
  }
  stopSessionTimer();
  appState.currentUser = null;
  appState.activeView = "dashboard";
  appState.selectedQuotationCustomer = null;
  appState.selectedQuotationNumber = null;
  appState.quotationReturnView = null;
  resetQuotationDraft(Number(demoState.settings.quotationSequence || 1));
  saveState();
  appShell.classList.add("hidden");
  loginScreen.classList.remove("hidden");
  applyCurrentAccessRules();
}

function startSessionTimer() {
  stopSessionTimer();
  if (appShell.classList.contains("hidden") || !demoState.settings.requireStaffLogin) return;
  sessionTimeoutId = window.setTimeout(() => {
    logoutCurrentUser();
  }, getSessionTimeoutMs());
}

function registerSessionActivity() {
  if (sessionListenersBound) return;
  sessionListenersBound = true;
  const refresh = () => startSessionTimer();
  ["click", "keydown", "mousemove", "scroll", "touchstart"].forEach(eventName => {
    window.addEventListener(eventName, refresh, { passive: true });
  });
  window.addEventListener("online", () => renderSettingsPanel());
  window.addEventListener("offline", () => renderSettingsPanel());
}

function syncCustomerSessionContext() {
  const currentUser = appState.currentUser;
  if (currentUser?.role === "customer") {
    const customerName = currentUser.customerName || currentUser.displayName || currentUser.username;
    if (customerName) {
      const existingCustomer = (demoState.customers || []).find(customer => String(customer.name || "").trim().toLowerCase() === String(customerName).trim().toLowerCase());
      if (!existingCustomer) {
        demoState.customers = [
          {
            id: Date.now(),
            name: customerName,
            contact: currentUser.contact || "",
            email: currentUser.email || "",
            quote: "",
            notes: "Customer account"
          },
          ...(demoState.customers || [])
        ];
      }
      appState.selectedQuotationCustomer = customerName;
      appState.quotationReturnView = null;
      resetQuotationDraft(Number(demoState.settings.quotationSequence || 1));
      applyCustomerQuotationDefaults(customerName, { force: true });
    }
  }
}

function clearStaffQuotationSession() {
  appState.selectedQuotationCustomer = null;
  appState.selectedQuotationNumber = null;
  appState.quotationReturnView = null;
  resetQuotationDraft(Number(demoState.settings.quotationSequence || 1));
}

function captureActiveQuotationWorkspace() {
  const quotationsVisible = appState.activeView === "quotations"
    || Boolean(document.querySelector('[data-section="quotations"]:not(.hidden)'));
  if (!quotationsVisible) return null;

  const currentMeta = getCurrentQuotationMeta();
  const selectedCustomer = appState.selectedQuotationCustomer || currentMeta.customerName || null;

  const hasQuotationWork = Boolean(
    selectedCustomer
    || demoState.quotation?.length
    || quotePanel?.value
    || quoteUnitPrice?.value
    || quoteCustomerWants?.value
    || quoteRemarks?.value
  );
  if (!hasQuotationWork) return null;

  return {
    selectedQuotationCustomer: selectedCustomer,
    quotationReturnView: appState.quotationReturnView,
    quotation: (demoState.quotation || []).map(item => ({ ...item })),
    meta: {
      ...currentMeta,
      ...(selectedCustomer ? { customerName: selectedCustomer } : {})
    },
    ui: {
      panelId: quotePanel?.value || "",
      qty: quoteQty?.value || "1",
      discount: quoteDiscount?.value || "0",
      uom: quoteUom?.value || "box",
      unitPrice: quoteUnitPrice?.value || "",
      hasCustomUnitPrice: quoteUnitPrice?.dataset.customPrice === "true"
    }
  };
}

function restoreActiveQuotationWorkspace(snapshot = null) {
  if (!snapshot) return false;

  appState.selectedQuotationCustomer = snapshot.selectedQuotationCustomer || snapshot.meta?.customerName || null;
  appState.quotationReturnView = snapshot.quotationReturnView || null;
  demoState.quotation = (snapshot.quotation || []).map(item => ({ ...item }));

  setCurrentQuotationMeta({
    ...(snapshot.meta || {}),
    ...(appState.selectedQuotationCustomer ? { customerName: appState.selectedQuotationCustomer } : {})
  });
  if (quotePanel) quotePanel.value = snapshot.ui?.panelId || "";
  if (quoteQty) quoteQty.value = snapshot.ui?.qty || "1";
  if (quoteDiscount) quoteDiscount.value = snapshot.ui?.discount || "0";
  if (quoteUom) quoteUom.value = snapshot.ui?.uom || "box";
  if (quoteUnitPrice) {
    quoteUnitPrice.value = snapshot.ui?.unitPrice || "";
    if (snapshot.ui?.hasCustomUnitPrice) {
      quoteUnitPrice.dataset.customPrice = "true";
    } else {
      delete quoteUnitPrice.dataset.customPrice;
    }
  }

  const selectedItem = demoState.inventory.find(item => String(item.id) === String(quotePanel?.value));
  updateQuotationItemPreview(selectedItem || null);
  if (quoteUnitPrice && snapshot.ui?.unitPrice) {
    quoteUnitPrice.value = snapshot.ui.unitPrice;
  }
  renderQuoteSummary();
  return true;
}

function resetDashboardFilters() {
  inventorySearch.value = "";
  inventoryCategoryFilter.value = "all";
  inventoryTypeFilter.value = "all";
  inventoryAvailabilityFilter.value = "all";
  const today = new Date();
  if (salesMonth) salesMonth.value = String(today.getMonth());
  if (salesYear) salesYear.value = String(today.getFullYear());
  salesViewState.monthIndex = null;
  salesViewState.year = new Date().getFullYear();
  salesDayState.monthIndex = null;
  salesDayState.year = null;
  salesDayState.day = null;
  populateSalesViewControls();
  renderAppSections();
  renderInventoryFilters();
}

function showView(viewName) {
  const permittedView = canAccessView(viewName) ? viewName : getDefaultViewForRole();
  const previousView = appState.activeView;
  if (permittedView === "quotations" && previousView !== "quotations") {
    appState.selectedQuotationCustomer = null;
  }
  appState.activeView = permittedView;
  window.scrollTo(0, 0);
  if (permittedView !== "sales") {
    closeSalesDayModal();
  }

  navItems.forEach(item => item.classList.toggle("active", item.dataset.view === permittedView));
  contents.forEach(section => section.classList.toggle("hidden", section.dataset.section !== permittedView));
  positionNavIndicator(permittedView);

  const titles = {
    dashboard: "Overview",
    inventory: "Inventory Management",
    quotations: "Quotation Review",
    sales: "Sales Performance",
    payments: "Payment History",
    courier: "Courier Updates",
    staffs: "User Management",
    settings: "Admin Settings"
  };
  const roleDashboardTitles = {
    admin: "User Management Dashboard",
    sales_staff: "Sales Staff Dashboard",
    sales_manager: "Sales Manager Dashboard",
    inventory_staff: "Inventory Dashboard",
    owner: "Business Dashboard"
  };
  pageTitle.textContent = permittedView === "dashboard" && roleDashboardTitles[getCurrentRole()]
    ? roleDashboardTitles[getCurrentRole()]
    : (titles[permittedView] || "Overview");

  if (permittedView === "sales") {
    populateSalesViewControls();
    renderSalesPerformanceView();
  }

  if (permittedView === "payments") {
    renderPayments();
  renderPaymentOperations();
  }

  if (permittedView === "courier") {
    renderCourierOrders();
  }

  if (permittedView === "dashboard" && getCurrentRole() === "sales_manager") {
    renderSalesManagerDashboard();
  }

  if (permittedView === "quotations") {
    renderQuoteSummary();
  }

  applyCurrentAccessRules();
}

function openQuotationForCustomer(customerName, { resetDraft = true, skipDraft = false, freshDraft = false } = {}) {
  const nextCustomer = String(customerName || "").trim();
  if (!nextCustomer) return;

  appState.quotationReturnView = appState.activeView || "dashboard";

  const currentCustomer = String(appState.selectedQuotationCustomer || "").trim();
  if (currentCustomer && currentCustomer !== nextCustomer) {
    saveQuotationDraft(currentCustomer);
  }

  appState.selectedQuotationCustomer = nextCustomer;
  appState.selectedQuotationNumber = null;
  if (freshDraft) {
    resetQuotationDraft(Number(demoState.settings.quotationSequence || 1));
    if (saveQuotationBtn) saveQuotationBtn.dataset.quotationDirty = "false";
    applyCustomerQuotationDefaults(nextCustomer, { force: true });
    restoreQuotationForCustomer(nextCustomer, { skipDraft: true, freshQuoteNumber: true });
    // Re-assert the account context after restoring the fresh draft so the
    // summary can never fall back to the unassigned-quotation message.
    setCurrentQuotationMeta({
      ...getCurrentQuotationMeta(),
      customerName: nextCustomer
    });
    showView("quotations");
    renderQuoteSummary();
    window.scrollTo(0, 0);
    return;
  }
  const restored = restoreQuotationForCustomer(nextCustomer, { skipDraft });
  if (!restored && resetDraft) {
    resetQuotationDraft(Number(demoState.settings.quotationSequence || 1));
    applyCustomerQuotationDefaults(nextCustomer, { force: true });
  }
  setCurrentQuotationMeta({
    ...getCurrentQuotationMeta(),
    customerName: nextCustomer
  });
  if (saveQuotationBtn) saveQuotationBtn.dataset.quotationDirty = "false";
  showView("quotations");
  window.scrollTo(0, 0);
}

// Keep quotation actions available if a control is rendered by a legacy view.
document.addEventListener("click", event => {
  const actionButton = event.target.closest("#saveQuotationBtn, #printQuoteBtn, #recordQuotePaymentBtn, #clearQuotationBtn");
  if (actionButton) {
    if (actionButton.id === "saveQuotationBtn" && actionButton.dataset.quoteSaveHandler !== "true") {
      saveCurrentQuotation();
      return;
    }
    if (actionButton.id === "printQuoteBtn" && actionButton.dataset.quotePrintHandler !== "true") {
      const quotationNumber = String(appState.selectedQuotationNumber || "").trim();
      const quotation = (demoState.quotations || []).find(item => String(item.quotation_no || "").trim() === quotationNumber);
      const order = quotation
        ? (demoState.orders || []).find(item => String(item.id || item.invoice || "").trim() === String(quotation.order_id || "").trim())
        : null;
      if (quotation && order && canPrintGeneratedQuotation(quotation, order)) {
        printGeneratedQuotation(order);
      } else {
        showActionDialog("Waiting for customer confirmation", "Please wait for the customer to confirm the quotation and select a payment method. Printing for preparation becomes available after full payment is recorded.");
      }
      return;
    }
    if (actionButton.id === "clearQuotationBtn" && actionButton.dataset.quoteClearHandler !== "true") {
      resetQuotationDraft(Number(demoState.settings.quotationSequence || 1));
      saveState();
      const status = document.getElementById("exportStatus");
      if (status) status.textContent = "Quotation cleared.";
      return;
    }
    if (actionButton.id === "recordQuotePaymentBtn" && actionButton.dataset.quotePaymentHandler !== "true") {
      const meta = getCurrentQuotationMeta();
      const customerName = meta.customerName || appState.selectedQuotationCustomer || "";
      const quoteNo = meta.quotationNumber || quoteNumber?.value?.trim() || "";
      if (!quoteNo || !customerName) {
        showToast("Save the quotation from a customer account first.", "danger");
        return;
      }
      showView("payments");
      syncPaymentFormFromQuote({ number: quoteNo, meta });
    }
    return;
  }

  if (appState.activeView === "quotations") return;

  const createButton = event.target.closest('[data-action="create-quotation"]');
  if (createButton) {
    const customerName = String(createButton.dataset.customer || "").trim();
    if (customerName) openQuotationForCustomer(customerName, { freshDraft: true });
    return;
  }

});

function goBackFromQuotation() {
  const returnView = String(appState.quotationReturnView || "").trim();

  appState.quotationReturnView = null;

  if (!returnView) {
    showView("dashboard");
    return;
  }

  if (returnView === "payments") {
    showView("payments");
    return;
  }

  if (returnView === "customers") {
    showView("dashboard");
    return;
  }

  showView(returnView);
}

function openDashboardTarget(target) {
  if (target === "inventory") {
    inventoryAvailabilityFilter.value = "Low Stock";
    showView("inventory");
    renderInventory();
    return;
  }

  if (target === "quotations") {
    appState.quotationReturnView = appState.activeView || "dashboard";
    showView("quotations");
    return;
  }

  if (target === "payments") {
    showView("payments");
    return;
  }

  if (target === "sales") {
    showView("sales");
  }
}

function setDeleteModalContent({ eyebrow, title, message, actionLabel = "Delete" }) {
  const modalEyebrow = document.querySelector("#deleteModal .eyebrow");
  const modalTitle = document.querySelector("#deleteModal h3");
  if (modalEyebrow) modalEyebrow.textContent = eyebrow;
  if (modalTitle) modalTitle.textContent = title;
  if (deleteModalText) deleteModalText.textContent = message;
  if (confirmDeleteBtn) {
    confirmDeleteBtn.textContent = actionLabel;
    confirmDeleteBtn.classList.toggle("danger-btn", actionLabel === "Delete");
    confirmDeleteBtn.classList.toggle("secondary-btn", actionLabel !== "Delete");
  }
}

function openDeleteModal({ id, type, eyebrow, title, message, actionLabel = "Delete" }) {
  appState.pendingDeleteId = id;
  appState.pendingDeleteType = type;
  setDeleteModalContent({ eyebrow, title, message, actionLabel });
  deleteModal.classList.remove("hidden");
  deleteModal.setAttribute("aria-hidden", "false");
}

function closeDeleteModal() {
  appState.pendingDeleteId = null;
  appState.pendingDeleteType = null;
  if (confirmDeleteBtn) {
    confirmDeleteBtn.textContent = "Delete";
    confirmDeleteBtn.classList.add("danger-btn");
    confirmDeleteBtn.classList.remove("secondary-btn");
  }
  deleteModal.classList.add("hidden");
  deleteModal.setAttribute("aria-hidden", "true");
}

function getStaffRecordById(id) {
  const targetId = Number(id);
  if (!Number.isFinite(targetId)) return null;
  const users = Array.isArray(demoState.users) ? demoState.users : [];
  return users.find(user => Number(user.id) === targetId && ["sales_staff", "sales_manager", "inventory_staff", "owner"].includes(String(user.role || "").toLowerCase())) || null;
}

function closeStaffModal() {
  activeStaffEditId = null;
  staffModeInput.value = "create";
  staffEditingIdInput.value = "";
  staffAccountForm.reset();
  staffPasswordInput.type = "password";
  staffModal.classList.add("hidden");
  staffModal.setAttribute("aria-hidden", "true");
}

function openStaffModal(mode = "create", staff = null) {
  const t = getTranslation(demoState.settings.language);
  const isEdit = mode === "edit" && staff;
  activeStaffEditId = isEdit ? Number(staff.id) : null;
  staffModeInput.value = isEdit ? "edit" : "create";
  staffEditingIdInput.value = isEdit ? String(staff.id) : "";
  staffModalTitle.textContent = isEdit ? t.settings.staffModalTitleEdit : t.settings.staffModalTitleCreate;
  staffModalCopy.textContent = t.settings.staffModalCopy;
  staffModalSubmitBtn.textContent = isEdit ? t.settings.updateStaff : t.settings.addStaff;
  staffDisplayNameInput.value = isEdit ? String(staff.displayName || "") : "";
  staffUsernameInput.value = isEdit ? String(staff.username || "") : "";
  if (staffEmailInput) staffEmailInput.value = isEdit ? String(staff.email || "") : "";
  if (staffStatusInput) staffStatusInput.value = isEdit ? String(staff.status || "active").toLowerCase() : "active";
  if (staffRoleInput) staffRoleInput.value = isEdit ? String(staff.role || "sales_staff").toLowerCase() : "sales_staff";
  staffPasswordInput.value = "";
  staffPasswordInput.placeholder = isEdit ? "Leave blank to keep password" : "Create password";
  staffPasswordInput.type = "password";
  staffModal.classList.remove("hidden");
  staffModal.setAttribute("aria-hidden", "false");
  staffDisplayNameInput.focus();
}

function enhancePasswordFields() {
  document.querySelectorAll('input[type="password"]').forEach(input => {
    if (input.dataset.passwordToggleReady === "true") {
      return;
    }

    const label = input.closest("label");
    if (!label) {
      return;
    }

    const wrap = document.createElement("div");
    wrap.className = "password-field-wrap";
    input.parentNode.insertBefore(wrap, input);
    wrap.appendChild(input);

    const toggle = document.createElement("button");
    toggle.type = "button";
    toggle.className = "password-toggle-btn";
    toggle.innerHTML = `
      <svg class="password-toggle-icon password-toggle-icon-show" viewBox="0 0 24 24" aria-hidden="true">
        <path d="M12 5C6.5 5 2.1 8.4 1 12c1.1 3.6 5.5 7 11 7s9.9-3.4 11-7c-1.1-3.6-5.5-7-11-7zm0 11a4 4 0 1 1 0-8 4 4 0 0 1 0 8z"/>
      </svg>
      <svg class="password-toggle-icon password-toggle-icon-hide hidden" viewBox="0 0 24 24" aria-hidden="true">
        <path d="M3.3 2.3 21.7 20.7 20.3 22 17.8 19.5c-1.6.9-3.5 1.5-5.8 1.5-5.5 0-9.9-3.4-11-7 0-1.3.4-2.6 1-3.8L2 10.2A17.5 17.5 0 0 1 1 12c1.1 3.6 5.5 7 11 7 1.8 0 3.5-.3 5-.9l-2.3-2.3A4 4 0 0 1 8.2 9.6L5.9 7.3A15.8 15.8 0 0 0 1 12c.9-2.9 3.7-5.6 7.4-6.7L2 3.9 3.3 2.3zm8.7 4.4a4 4 0 0 1 4 4c0 .8-.2 1.5-.6 2.1l-5.5-5.5A4 4 0 0 1 12 6.7zm0-2c5.5 0 9.9 3.4 11 7-.4 1.1-1 2.3-2 3.4l-1.4-1.4c.5-.7.9-1.4 1.1-2C20.9 9.4 16.5 6 12 6c-.5 0-1 .1-1.5.1L9 4.6c1-.3 2-.5 3-.5z"/>
      </svg>
    `;
    toggle.setAttribute("aria-pressed", "false");
    toggle.setAttribute("aria-label", "Show password");

    toggle.addEventListener("click", () => {
      const showing = input.type === "password";
      input.type = showing ? "text" : "password";
      toggle.setAttribute("aria-pressed", showing ? "true" : "false");
      toggle.setAttribute("aria-label", showing ? "Hide password" : "Show password");
      toggle.querySelector(".password-toggle-icon-show")?.classList.toggle("hidden", showing);
      toggle.querySelector(".password-toggle-icon-hide")?.classList.toggle("hidden", !showing);
    });

    input.dataset.passwordToggleReady = "true";
    wrap.appendChild(toggle);
  });
}

function deleteRoomCaptureById(captureId) {
  const index = demoState.roomCaptures.findIndex(entry => entry.id === captureId);
  if (index < 0) return false;
  demoState.roomCaptures.splice(index, 1);
  renderAppSections();
  saveState();
  return true;
}

function openRoomCaptureDeletePrompt(captureId) {
  const capture = demoState.roomCaptures.find(entry => entry.id === captureId);
  if (!capture) return;
  openDeleteModal({
    id: captureId,
    type: "roomCapture",
    eyebrow: "Delete Capture",
    title: "Remove this room capture?",
    message: `Delete ${capture.customerName || "this customer"}'s room capture from the dashboard? This cannot be undone.`
  });
}

function saveCurrentQuotation() {
  showToast("Quotations are generated automatically when an order is placed.", "info");
}

function syncQuotationActionButtons() {
  if (!saveQuotationBtn) return;

  const quoteNo = String(quoteNumber?.value || "").trim();
  const customerName = String(appState.selectedQuotationCustomer || getCurrentQuotationMeta().customerName || "")
    .trim()
    .toLowerCase();
  const dirty = saveQuotationBtn.dataset.quotationDirty === "true";
  const savedQuotation = !dirty && quoteNo && customerName
    ? (demoState.savedQuotations || []).find(savedQuote => (
      String(savedQuote.number || savedQuote.meta?.quotationNumber || "").trim() === quoteNo
      && String(savedQuote.meta?.customerName || "").trim().toLowerCase() === customerName
    ))
    : null;
  const hasSavedItems = Boolean(savedQuotation?.items?.length);
  const generatedQuotation = (demoState.quotations || []).find(item => String(item.quotation_no || "").trim() === quoteNo);
  const order = generatedQuotation
    ? (demoState.orders || []).find(entry => String(entry.id || entry.invoice || "").trim() === String(generatedQuotation.order_id || "").trim())
    : savedQuotation
      ? (demoState.orders || []).find(entry => String(entry.invoice || entry.quoteNumber || "").trim() === quoteNo)
      : null;
  const paidAmount = order
    ? (demoState.payments || [])
      .filter(payment => String(payment.invoice || payment.quoteNumber || "").trim() === quoteNo)
      .reduce((sum, payment) => sum + Number(String(payment.amount || 0).replace(/[^\d.-]/g, "")), 0)
    : 0;
  const grandTotal = Number(savedQuotation?.totals?.grandTotal || order?.amount || 0);
  const fullyPaid = hasSavedItems && grandTotal > 0 && paidAmount >= grandTotal;

  saveQuotationBtn.disabled = false;
  if (printQuoteBtn) printQuoteBtn.disabled = !generatedQuotation;
  const recordButton = document.getElementById("recordQuotePaymentBtn");
  if (recordButton) recordButton.disabled = !hasSavedItems || fullyPaid;
  if (markQuotePaidBtn) markQuotePaidBtn.disabled = !hasSavedItems || fullyPaid;
}

function markQuotationDirty() {
  if (saveQuotationBtn) saveQuotationBtn.dataset.quotationDirty = "true";
  syncQuotationActionButtons();
}

function bindEvents() {
  renderCategoryGroupOptions("");
  renderInventoryFilters();

  document.querySelectorAll("[data-quote-step]").forEach(step => {
    step.addEventListener("click", () => {
      const target = step.dataset.quoteStep === "1"
        ? document.querySelector(".quote-builder-panel")
        : step.dataset.quoteStep === "2"
          ? document.querySelector(".quote-item-card")
          : document.querySelector(".quote-summary-panel");
      target?.scrollIntoView({ behavior: "smooth", block: "start" });
    });
  });

  document.querySelectorAll("[data-staff-action]").forEach(action => {
    action.addEventListener("click", () => {
      const targetView = String(action.dataset.staffAction || "").trim();
      if (targetView) showView(targetView);
    });
  });

  window.addEventListener("aiyige:open-quotation", event => {
    const quoteNumber = String(event.detail?.quoteNumber || "").trim();
    const order = (demoState.orders || []).find(entry => [entry.invoice, entry.quoteNumber, entry.id]
      .some(reference => String(reference || "").trim() === quoteNumber));
    if (order) {
      window.dispatchEvent(new CustomEvent("aiyige:print-quotation", { detail: { order } }));
      return;
    }
    showToast("No generated quotation is linked to this order yet.", "info");
  });

  [userSearchInput, userRoleFilter, userStatusFilter, userDateSort].forEach(control => {
    control?.addEventListener("input", () => renderSettingsPanel());
    control?.addEventListener("change", () => renderSettingsPanel());
  });

  exportUsersBtn?.addEventListener("click", () => {
    if (!canManageSettings()) return;
    exportUsersCsv();
  });

  document.getElementById("inventoryDashboardAlertsBtn")?.addEventListener("click", () => {
    showView("inventory");
    if (inventoryAvailabilityFilter) inventoryAvailabilityFilter.value = "Low Stock";
    renderInventoryFilters();
    renderInventory();
  });

  document.getElementById("adminDashboardUsersBtn")?.addEventListener("click", () => showView("staffs"));
  document.getElementById("salesDashboardOpenQuotesBtn")?.addEventListener("click", () => {
    appState.selectedQuotationNumber = null;
    showView("quotations");
  });
  document.getElementById("salesDashboardOpenPaymentsBtn")?.addEventListener("click", () => showView("payments"));
  document.getElementById("salesStaffDashboardQueue")?.addEventListener("click", event => {
    const button = event.target.closest("[data-dashboard-quote-customer]");
    if (!button) return;
    const customerName = String(button.dataset.dashboardQuoteCustomer || "").trim().toLowerCase();
    const quotation = (demoState.quotations || []).find(item => String(item.customer?.name || "").trim().toLowerCase() === customerName);
    if (quotation) {
      showView("quotations");
      appState.selectedQuotationCustomer = quotation.customer?.name || "";
      appState.selectedQuotationNumber = quotation.quotation_no || "";
      renderQuoteSummary();
    }
  });
  document.getElementById("salesDashboardDeliveryBtn")?.addEventListener("click", () => {
    showView("courier");
  });
  document.getElementById("salesManagerQuotesBtn")?.addEventListener("click", () => {
    appState.selectedQuotationNumber = null;
    showView("quotations");
  });
  document.getElementById("salesManagerReportsBtn")?.addEventListener("click", () => showView("sales"));
  document.getElementById("salesManagerOpenQuotesBtn")?.addEventListener("click", () => {
    appState.selectedQuotationNumber = null;
    showView("quotations");
  });
  document.getElementById("salesManagerOpenCourierBtn")?.addEventListener("click", () => showView("courier"));
  document.getElementById("ownerDashboardInventoryBtn")?.addEventListener("click", () => showView("inventory"));
  document.getElementById("ownerDashboardSalesBtn")?.addEventListener("click", () => showView("sales"));

  loginForm.addEventListener("submit", async event => {
    event.preventDefault();
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;
    const authenticatedUser = authenticateUser({ username, password });
    if (!authenticatedUser) {
      showToast("Invalid login.", "danger");
      return;
    }
    await startAuthenticatedSession(authenticatedUser);
  });

  firstAdminSetupForm?.addEventListener("submit", async event => {
    event.preventDefault();
    const formData = new FormData(firstAdminSetupForm);
    const password = String(formData.get("password") || "");
    if (password !== String(formData.get("confirmPassword") || "")) {
      showToast("The passwords do not match.", "danger");
      return;
    }

    const submitButton = firstAdminSetupForm.querySelector('[type="submit"]');
    if (submitButton) submitButton.disabled = true;
    try {
      const response = await fetch("/api/setup/first-admin", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          displayName: formData.get("displayName"),
          username: formData.get("username"),
          password
        })
      });
      const result = await response.json();
      if (!response.ok) throw new Error(result.error || "Unable to create the administrator account.");
      await loadState();
      const authenticatedUser = authenticateUser({ username: formData.get("username"), password });
      if (!authenticatedUser) throw new Error("Account created. Please sign in with your new credentials.");
      firstAdminSetupForm.reset();
      await startAuthenticatedSession(authenticatedUser);
    } catch (error) {
      showToast(error.message || "Unable to create the administrator account.", "danger");
    } finally {
      if (submitButton) submitButton.disabled = false;
    }
  });

  logoutBtn.addEventListener("click", () => {
    logoutCurrentUser();
  });

  if (backQuotationBtn) {
    backQuotationBtn.addEventListener("click", () => {
      goBackFromQuotation();
    });
  }

  navItems.forEach(item => {
    item.addEventListener("click", () => {
      if (!canAccessView(item.dataset.view)) return;
      if (item.dataset.view === "quotations") {
        appState.quotationReturnView = appState.activeView || "dashboard";
      }
      showView(item.dataset.view);
    });
  });

  kpiCards.forEach(card => {
    const activate = () => openDashboardTarget(card.dataset.kpiTarget);
    card.addEventListener("click", activate);
    card.addEventListener("keydown", event => {
      if (event.key === "Enter" || event.key === " ") {
        event.preventDefault();
        activate();
      }
    });
  });

  newItemBtn?.addEventListener("click", () => {
    if (!canManageInventory()) return;
    resetPanelEditor("Add Product");
    openPanelEditor("Add Product");
  });

  document.getElementById("clearPanelBtn").addEventListener("click", () => {
    resetPanelEditor("Add Product");
  });

  panelCategoryGroup.addEventListener("change", () => {
    renderProductTypeOptions(panelCategoryGroup.value, "");
  });

  document.getElementById("panelForm").addEventListener("submit", event => {
    event.preventDefault();
    if (!canManageInventory()) {
      showToast("Only Inventory Staff accounts can save inventory changes.", "danger");
      return;
    }
    const id = document.getElementById("panelId").value;
    const categoryGroup = document.getElementById("panelCategoryGroup").value;
    const categoryCode = document.getElementById("panelCategoryName").value;
    const catalogEntry = findCatalogEntry(categoryGroup, categoryCode) || findCatalogEntryForItem({
      categoryGroup,
      categoryName: categoryCode,
      productType: categoryCode,
      productCode: document.getElementById("panelProductCode").value.trim(),
      productName: document.getElementById("panelProductName").value.trim()
    });
    const productCode = document.getElementById("panelProductCode").value.trim() || catalogEntry?.code || "";
    const enteredProductName = document.getElementById("panelProductName").value.trim();
    const productName = enteredProductName || catalogEntry?.type || catalogEntry?.code || productCode || "";
    const length = document.getElementById("panelLength").value.trim();
    const width = document.getElementById("panelWidth").value.trim();
    const thickness = document.getElementById("panelThickness").value.trim();
    const unitsPerBox = Math.max(1, Number(document.getElementById("panelUnitsPerBox").value) || 20);
    const unitPrice = Number(document.getElementById("panelPrice").value);
    const priceUnit = panelPriceUnit?.value === "meter" ? "meter" : "piece";
    const payload = {
      id: id ? Number(id) : Date.now(),
      name: productCode || productName,
      productCode,
      productName,
      categoryGroup,
      categoryName: catalogEntry?.type || "",
      productType: catalogEntry?.type || "",
      length,
      width,
      thickness,
      size: [length, width, thickness].filter(Boolean).join(" x "),
      material: document.getElementById("panelMaterial").value.trim(),
      description: document.getElementById("panelDescription").value.trim(),
      quantity: Math.max(0, Number(document.getElementById("panelQuantity").value) || 0),
      unitsPerBox,
      priceUnit,
      pricePerMeter: unitPrice,
      price: unitPrice,
      availability: document.getElementById("panelAvailabilityDisplay").textContent,
      imageData: appState.currentPanelImageData || "",
      modelData: appState.currentPanelModelData || ""
    };

    const index = demoState.inventory.findIndex(item => item.id === payload.id);
    if (index >= 0) {
      demoState.inventory[index] = payload;
    } else {
      demoState.inventory.unshift(payload);
    }
    renderInventory();
    resetPanelEditor("Add Product");
    closePanelEditor();
    saveState();
  });

  inventoryTable.addEventListener("click", event => {
    const actionButton = event.target.closest("[data-edit], [data-delete]");
    if (!actionButton) return;
    if (!canManageInventory()) return;

    const editId = actionButton.dataset.edit;
    const deleteId = actionButton.dataset.delete;
    if (deleteId) {
      const index = demoState.inventory.findIndex(entry => entry.id === Number(deleteId));
      if (index >= 0) {
        appState.pendingDeleteId = Number(deleteId);
        appState.pendingDeleteType = "inventory";
        const item = demoState.inventory[index];
        setDeleteModalContent({
          eyebrow: "Delete Product",
          title: "Remove this product?",
          message: getDeleteMessage(item.productCode || item.name, categoryLabel(item))
        });
        deleteModal.classList.remove("hidden");
        deleteModal.setAttribute("aria-hidden", "false");
      }
      return;
    }
    if (!editId) return;
    const item = demoState.inventory.find(entry => entry.id === Number(editId));
    if (!item) return;
    document.getElementById("panelId").value = item.id;
    const resolvedMatch = findCatalogMatchForItem(item);
    const resolvedGroup = resolvedMatch.group || resolveCatalogCategoryGroup(item.categoryGroup) || firstGroup;
    const resolvedType = item.categoryName || item.productType || resolvedMatch.entry?.type || findCatalogEntryForItem(item)?.type || "";
    renderCategoryGroupOptions(resolvedGroup);
    renderProductTypeOptions(resolvedGroup, resolvedType);
    document.getElementById("panelProductCode").value = item.productCode || item.code || item.name;
    document.getElementById("panelProductName").value = item.productName || item.productType || item.categoryName || item.productCode || item.name || resolvedType;
    document.getElementById("panelCategoryGroup").value = resolvedGroup;
    document.getElementById("panelCategoryName").value = resolvedType;
    const parsedDimensions = parsePanelDimensions(item.size);
    if (panelLength) panelLength.value = item.length || parsedDimensions.length || "";
    if (panelWidth) panelWidth.value = item.width || parsedDimensions.width || "";
    if (panelThickness) panelThickness.value = item.thickness || parsedDimensions.thickness || "";
    if (panelUnitsPerBox) panelUnitsPerBox.value = item.unitsPerBox || 20;
    document.getElementById("panelMaterial").value = item.material;
    document.getElementById("panelDescription").value = item.description;
    document.getElementById("panelQuantity").value = item.quantity ?? 0;
    document.getElementById("panelPrice").value = item.pricePerMeter ?? item.price ?? 0;
    if (panelPriceUnit) panelPriceUnit.value = item.priceUnit || (String(item.productCode || item.name || "").toUpperCase().startsWith("LJS-") ? "meter" : "piece");
    updateAvailabilityDisplay(item.quantity ?? 0);
    setPanelImage(getPanelImageSource(item));
    setPanelModel(item.modelAsset || item.modelUrl || "");
    showView("inventory");
    openPanelEditor("Edit Product");
  });

  if (roomCaptureGrid) {
    roomCaptureGrid.addEventListener("click", event => {
      const deleteButton = event.target.closest("[data-delete-room-capture]");
      if (!deleteButton) return;
      const captureId = Number(deleteButton.dataset.deleteRoomCapture);
      openRoomCaptureDeletePrompt(captureId);
    });
  }

  cancelDeleteBtn.addEventListener("click", () => {
    closeDeleteModal();
  });

  confirmDeleteBtn.addEventListener("click", async () => {
    if (appState.pendingDeleteId == null) return;

    if (appState.pendingDeleteType === "quotationDraft") {
      discardUnsavedQuotationDraft();
      closeDeleteModal();
      showToast("Quotation draft discarded.", "success");
      return;
    }

    if (appState.pendingDeleteType === "roomCapture") {
      deleteRoomCaptureById(appState.pendingDeleteId);
      closeDeleteModal();
      return;
    }

    if (appState.pendingDeleteType === "roomCaptureAll") {
      demoState.roomCaptures = [];
      renderAppSections();
      saveState();
      closeDeleteModal();
      return;
    }

    if (appState.pendingDeleteType === "inventory") {
      const index = demoState.inventory.findIndex(entry => entry.id === appState.pendingDeleteId);
      if (index >= 0) {
        demoState.inventory.splice(index, 1);
        renderInventory();
        renderQuoteSummary();
        saveState();
      }
      closeDeleteModal();
      return;
    }

    if (appState.pendingDeleteType === "staff") {
      const result = await deleteStaffAccount(appState.pendingDeleteId);
      if (!result.ok) {
        showToast(result.reason || "Unable to delete employee account.", "danger");
        return;
      }
      renderAppSections();
      closeDeleteModal();
      showToast("Staff account deleted.", "success");
      return;
    }
    closeDeleteModal();
  });

  deleteModal.addEventListener("click", event => {
    if (event.target === deleteModal) {
      closeDeleteModal();
    }
  });

  closePanelEditorBtn.addEventListener("click", closePanelEditor);
  panelImageFile.addEventListener("change", event => {
    const file = event.target.files && event.target.files[0];
    if (!file) {
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      setPanelImage(String(reader.result || ""));
    };
    reader.readAsDataURL(file);
  });

  panelImageReplaceBtn.addEventListener("click", () => {
    panelImageFile.value = "";
    panelImageFile.click();
  });

  panelModelFile?.addEventListener("change", event => {
    const file = event.target.files && event.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => setPanelModel(String(reader.result || ""), file.name);
    reader.readAsDataURL(file);
  });

  panelModelReplaceBtn?.addEventListener("click", () => {
    panelModelFile.value = "";
    panelModelFile.click();
  });

  panelQuantity.addEventListener("input", event => {
    updateAvailabilityDisplay(event.target.value);
  });

  document.getElementById("quoteForm").addEventListener("submit", event => {
    event.preventDefault();
    showToast("Staff cannot create quotations manually. Place an order from the customer app.", "info");
  });

  if (quotePanel) {
    quotePanel.addEventListener("change", event => {
      const item = demoState.inventory.find(entry => entry.id === Number(event.target.value));
      if (quoteUnitPrice) {
        delete quoteUnitPrice.dataset.customPrice;
      }
      updateQuotationItemPreview(item || null);
      markQuotationDirty();
    });
  }

  if (quoteUnitPrice) {
    quoteUnitPrice.addEventListener("input", () => {
      quoteUnitPrice.dataset.customPrice = "true";
      const item = demoState.inventory.find(entry => Number(entry.id) === Number(quotePanel?.value));
      updateQuotationItemPreview(item || null);
      markQuotationDirty();
    });
  }

  [quoteQty, quoteDiscount, quoteUom].forEach(field => {
    if (!field) return;
    const eventName = field === quoteUom ? "change" : "input";
    field.addEventListener(eventName, () => {
      const item = demoState.inventory.find(entry => Number(entry.id) === Number(quotePanel?.value));
      updateQuotationItemPreview(item || null);
      markQuotationDirty();
    });
  });

  [quoteDeliveryCost, quoteCourier, quoteSource, quoteTerms].forEach(field => {
    if (!field) return;
    field.addEventListener(field.tagName === "SELECT" ? "change" : "input", () => {
      renderQuoteSummary();
      markQuotationDirty();
    });
  });

  if (quoteTable) {
    quoteTable.addEventListener("input", event => {
      const input = event.target.closest("[data-quote-edit]");
      if (!input) return;

      const index = Number(input.dataset.quoteEdit);
      const field = input.dataset.field;
      const value = field === "pricePerMeter" ? parseMoney(input.value) : Number(input.value);
      if (!["qty", "pricePerMeter", "discountPercent"].includes(field)) return;

      updateQuotationItem(index, { [field]: value }, { render: false });
      markQuotationDirty();
    });

    quoteTable.addEventListener("change", event => {
      const input = event.target.closest("[data-quote-edit]");
      if (!input) return;

      const index = Number(input.dataset.quoteEdit);
      const field = input.dataset.field;
      const value = field === "pricePerMeter" ? parseMoney(input.value) : Number(input.value);
      if (!["qty", "pricePerMeter", "discountPercent"].includes(field)) return;

      updateQuotationItem(index, { [field]: value });
      markQuotationDirty();
      saveQuotationDraft();
      saveState();
    });

    quoteTable.addEventListener("click", event => {
      const removeButton = event.target.closest("[data-quote-remove]");
      if (!removeButton) return;

      removeQuotationItem(Number(removeButton.dataset.quoteRemove));
      markQuotationDirty();
      saveQuotationDraft();
      saveState();
    });
  }

  const quoteMetaFields = [
    quoteFirstName,
    quoteMiddleInitial,
    quoteLastName,
    quoteStreetAddress,
    quoteCityProvince,
    quoteContactNumber,
    quoteEmailAddress,
    quoteCustomerWants,
    quoteValidUntil,
    quoteRemarks
  ];
  quoteMetaFields.forEach(field => {
    if (!field) return;
    field.addEventListener("input", () => {
      renderQuoteSummary();
      markQuotationDirty();
    });
  });

  if (generateQuotePdfBtn) {
    generateQuotePdfBtn.addEventListener("click", exportCurrentQuotation);
  }

  const quotationQueue = document.getElementById("quotationQueue");
  const quotationDetailModal = document.getElementById("quotationDetailModal");
  // Keep the fixed dialog outside transformed/scrolling section containers so
  // it is positioned against the viewport and remains above the sticky header.
  if (quotationDetailModal && quotationDetailModal.parentElement !== document.body) {
    document.body.append(quotationDetailModal);
  }
  const closeQuotationDetails = () => {
    if (quotationDetailModal) quotationDetailModal.hidden = true;
    appState.selectedQuotationNumber = null;
    appState.selectedQuotationCustomer = null;
    renderQuoteSummary();
  };
  document.getElementById("closeQuotationDetails")?.addEventListener("click", closeQuotationDetails);
  quotationDetailModal?.addEventListener("click", event => {
    if (event.target === quotationDetailModal) closeQuotationDetails();
  });
  document.addEventListener("keydown", event => {
    if (event.key === "Escape" && quotationDetailModal && !quotationDetailModal.hidden) {
      closeQuotationDetails();
    }
  });
  if (quotationQueue) {
    quotationQueue.addEventListener("click", event => {
      const quotationCard = event.target.closest("[data-quotation-queue-select]");
      if (quotationCard && !event.target.closest("button")) {
        const quotationNumber = String(quotationCard.dataset.quotationQueueSelect || "");
        const quotation = (demoState.quotations || []).find(item => String(item.quotation_no || "") === quotationNumber);
        if (quotation) {
          appState.selectedQuotationCustomer = quotation.customer?.name || "";
          appState.selectedQuotationNumber = quotationNumber;
          renderQuoteSummary();
        }
        return;
      }
      const generatedButton = event.target.closest("[data-generated-quotation]");
      if (generatedButton) {
        const quotationNumber = String(generatedButton.dataset.generatedQuotation || "");
        const quotation = (demoState.quotations || []).find(item => String(item.quotation_no || "") === quotationNumber);
        if (quotation) {
          const order = (demoState.orders || []).find(item => (
            String(item.id || item.invoice || "") === String(quotation.order_id || "")
            || String(item.quoteNumber || item.invoice || "") === quotationNumber
          ));
          if (order) window.dispatchEvent(new CustomEvent("aiyige:print-quotation", { detail: { order } }));
        }
        return;
      }
      const action = event.target.closest("[data-quotation-queue-action]");
      if (!action) return;
      const customerName = action.dataset.customer?.trim();
      if (!customerName) return;
      const order = [...(demoState.orders || [])]
        .filter(entry => String(entry.customer || "").trim().toLowerCase() === customerName.toLowerCase())
        .sort((left, right) => (Date.parse(right.createdAt || "") || 0) - (Date.parse(left.createdAt || "") || 0))[0];
      if (order) window.dispatchEvent(new CustomEvent("aiyige:print-quotation", { detail: { order } }));
      else showToast("No generated quotation is linked to this customer order yet.", "info");
    });
    quotationQueue.addEventListener("keydown", event => {
      if (event.key !== "Enter" && event.key !== " ") return;
      const card = event.target.closest("[data-quotation-queue-select]");
      if (!card || event.target.closest("button")) return;
      event.preventDefault();
      card.click();
    });
  }

  if (saveQuotationBtn) {
    saveQuotationBtn.dataset.quoteSaveHandler = "true";
    saveQuotationBtn.addEventListener("click", saveCurrentQuotation);
  }

  const discardUnsavedQuotationDraft = () => {
    const customerName = String(appState.selectedQuotationCustomer || "").trim();
    resetQuotationDraft(Number(demoState.settings.quotationSequence || 1));
    if (customerName) {
      applyCustomerQuotationDefaults(customerName, { force: true });
      setCurrentQuotationMeta({ ...getCurrentQuotationMeta(), customerName });
    }
    if (saveQuotationBtn) saveQuotationBtn.dataset.quotationDirty = "false";
    syncQuotationActionButtons();
    saveState();
    const status = document.getElementById("exportStatus");
    if (status) status.textContent = "Quotation cleared.";
  };

  if (clearQuotationBtn) {
    clearQuotationBtn.dataset.quoteClearHandler = "true";
    clearQuotationBtn.addEventListener("click", () => {
      if (!canManageQuotations()) return;
      if (demoState.quotation.length) {
        openDeleteModal({
          id: "quotation-draft",
          type: "quotationDraft",
          eyebrow: "Quotation draft",
          title: "Discard this unsaved draft?",
          message: "Your current quotation items and details will be cleared.",
          actionLabel: "Discard draft"
        });
        return;
      }
      discardUnsavedQuotationDraft();
    });
  }

  const recordQuotePaymentBtn = document.getElementById("recordQuotePaymentBtn");
  if (recordQuotePaymentBtn) {
    recordQuotePaymentBtn.dataset.quotePaymentHandler = "true";
    recordQuotePaymentBtn.addEventListener("click", () => {
      const meta = getCurrentQuotationMeta();
      const quoteNo = meta.quotationNumber || quoteNumber?.value?.trim() || "";
      const customerName = meta.customerName || appState.selectedQuotationCustomer || "";
      if (!quoteNo || !customerName) {
        showToast("Save the quotation from a customer account first.", "danger");
        return;
      }

      appState.quotationReturnView = "quotations";
      showView("payments");
      const savedQuote = (demoState.savedQuotations || []).find(saved => (
        String(saved.number || saved.meta?.quotationNumber || "").trim() === String(quoteNo).trim()
      ));
      syncPaymentFormFromQuote({
        number: quoteNo,
        meta,
        totals: {
          grandTotal: savedQuote?.totals?.grandTotal
            || demoState.orders.find(order => order.invoice === quoteNo)?.amount
            || 0
        }
      });
    });
  }

  if (markQuotePaidBtn) {
    markQuotePaidBtn.dataset.quotePaidHandler = "true";
    markQuotePaidBtn.addEventListener("click", () => {
      if (!canManagePayments()) {
        showToast("Only Sales Staff and Sales Manager accounts can update payment status.", "danger");
        return;
      }

      const meta = getCurrentQuotationMeta();
      const quoteNo = meta.quotationNumber || quoteNumber?.value?.trim() || "";
      const customerName = meta.customerName || appState.selectedQuotationCustomer || "";
      if (!quoteNo || !customerName) {
        showToast("Save the quotation from a customer account first.", "danger");
        return;
      }

      const order = demoState.orders.find(entry => entry.invoice === quoteNo || entry.quoteNumber === quoteNo);
      if (!order) {
        showToast("Save the quotation before marking it as paid.", "danger");
        return;
      }
      const quotation = (demoState.quotations || []).find(entry => String(entry.quotation_no || "").trim() === quoteNo);

      if (String(order.paymentStatus || order.status || "").toLowerCase() === "paid") {
        showToast("This quotation is already marked as paid.", "info");
        return;
      }

      const orderTotal = parseMoney(order.amount || order.total || 0);
      const amountPaid = demoState.payments
        .filter(payment => [payment.invoice, payment.quoteNumber, payment.quote_number, payment.orderId, payment.order_id]
          .some(reference => [quoteNo, order.id, order.invoice].some(orderReference => String(reference || "") === String(orderReference || ""))))
        .filter(payment => ["paid", "completed", "settled", "partially paid"].includes(String(payment.status || "").trim().toLowerCase()))
        .reduce((sum, payment) => sum + parseMoney(payment.amount), 0);
      const remaining = Math.max(orderTotal - amountPaid, 0);
      if (remaining <= 0) {
        showToast("This quotation has no remaining balance.", "info");
        return;
      }

      const result = recordPaymentForQuote({
        quoteNumber: quoteNo,
        customerName,
        amount: remaining,
        method: getCustomerSelectedPaymentMethod(quotation.payment_method, order.payment, order.method, order.paymentMethod, order.payment_method),
        status: "Paid",
        paymentDateValue: new Date().toISOString().slice(0, 10),
        reference: "Marked as paid",
        notes: "Payment status updated from quotation workspace."
      });

      if (!result.ok) {
        showToast(result.reason || "Unable to mark quotation as paid.", "danger");
        return;
      }

      saveState();
      renderAppSections();
      showToast(`${quoteNo} marked as paid and added to Sales.`, "success");
    });
  }

  if (paymentForm) {
    paymentForm.addEventListener("submit", event => {
      event.preventDefault();
      if (!canManagePayments()) {
        showToast("Only Sales Staff and Sales Manager accounts can record payments.", "danger");
        return;
      }
      const quoteNo = paymentQuoteNumber?.value.trim();
      if (!quoteNo) {
        showToast("Save a quotation first before recording payment.", "danger");
        return;
      }

      const result = recordPaymentForQuote({
        quoteNumber: quoteNo,
        customerName: paymentAccountName?.value.trim(),
        amount: paymentAmount?.value,
        method: paymentMethod?.value || "",
        status: paymentStatus?.value || "Paid",
        paymentDateValue: paymentDate?.value || new Date().toISOString().slice(0, 10),
        reference: paymentReference?.value.trim(),
        notes: paymentNotes?.value.trim()
      });

      if (!result.ok) {
        showToast(result.reason || "Unable to record payment.", "danger");
        return;
      }

      saveState();
      renderAppSections();
      const savedQuote = (demoState.savedQuotations || []).find(saved => (
        String(saved.number || saved.meta?.quotationNumber || "").trim() === String(quoteNo).trim()
      ));
      syncPaymentFormFromQuote(savedQuote || null);
      const status = document.getElementById("exportStatus");
      if (status) {
        status.textContent = result.remaining > 0
          ? `Payment recorded. Remaining balance: ${result.remaining.toLocaleString()}`
          : "Payment recorded and quotation fully settled.";
      }
    });
  }

  if (printQuoteBtn) {
    printQuoteBtn.dataset.quotePrintHandler = "true";
    printQuoteBtn.addEventListener("click", () => {
      const quotationNumber = String(appState.selectedQuotationNumber || "").trim();
      const quotation = (demoState.quotations || []).find(item => String(item.quotation_no || "").trim() === quotationNumber);
      if (!quotation) {
        showActionDialog("Quotation not ready to print", "Please wait for the customer to confirm the quotation and select a payment method. Printing for preparation becomes available after full payment is recorded.");
        return;
      }
      const order = (demoState.orders || []).find(item => String(item.id || item.invoice || "").trim() === String(quotation.order_id || "").trim());
      if (!order) {
        showActionDialog("Waiting for customer confirmation", "Please wait for the customer to confirm the order in the mobile app before printing this quotation.");
        return;
      }
      if (!canPrintGeneratedQuotation(quotation, order)) {
        printGeneratedQuotation(order);
        return;
      }
      printGeneratedQuotation(order);
    });
  }

  window.addEventListener("aiyige:print-quotation", event => {
    if (event.detail?.order) printGeneratedQuotation(event.detail.order);
  });

  if (salesMonth) {
    salesMonth.addEventListener("change", renderSalesTrend);
  }

  if (salesYear) {
    salesYear.addEventListener("change", renderSalesTrend);
  }

  if (salesViewMonthStrip) {
    salesViewMonthStrip.addEventListener("click", event => {
      const button = event.target.closest(".month-chip");
      if (!button) return;
      const nextMonthIndex = Number(button.dataset.monthIndex);
      salesViewState.monthIndex = salesViewState.monthIndex === nextMonthIndex ? null : nextMonthIndex;
      populateSalesViewControls();
      renderSalesPerformanceView();
    });
  }

  if (salesViewYear) {
    salesViewYear.addEventListener("change", event => {
      salesViewState.year = event.target.value === "" ? null : Number(event.target.value);
      populateSalesViewControls();
      renderSalesPerformanceView();
    });
  }

  if (salesTodayCard) {
    const openSalesDay = () => openSalesDayModal();
    salesTodayCard.addEventListener("click", openSalesDay);
    salesTodayCard.addEventListener("keydown", event => {
      if (event.key === "Enter" || event.key === " ") {
        event.preventDefault();
        openSalesDay();
      }
    });
  }

  if (closeSalesDayModalBtn) {
    closeSalesDayModalBtn.addEventListener("click", closeSalesDayModal);
  }

  if (salesDayModal) {
    salesDayModal.addEventListener("click", event => {
      if (event.target === salesDayModal) {
        closeSalesDayModal();
      }
    });
  }

  if (salesDayPicker) {
    salesDayPicker.addEventListener("click", event => {
      const button = event.target.closest("[data-day]");
      if (!button) return;
      const day = Number(button.dataset.day);
      if (Number.isFinite(day)) {
        salesDayState.day = day;
      }
      renderSalesDayModal();
    });
  }

  if (salesTable) {
    salesTable.addEventListener("click", event => {
      const button = event.target.closest("[data-customer]");
      if (!button) return;
      const search = document.getElementById("paymentsSearch");
      if (search) search.value = button.dataset.customer || "";
      showView("payments");
      renderPaymentOperations();
    });
  }

  if (themeToggle) {
    themeToggle.addEventListener("change", event => {
      demoState.settings.themeMode = event.target.checked ? "dark" : "light";
      applyTheme(demoState.settings.themeMode);
      renderAppSections();
      saveState();
    });
  }

  if (languageSelect) {
    languageSelect.addEventListener("change", event => {
      demoState.settings.language = event.target.value;
      renderAppSections();
      saveState();
    });
  }

  if (currencySelect) {
    currencySelect.addEventListener("change", event => {
      demoState.settings.currency = event.target.value;
      renderAppSections();
      saveState();
    });
  }

  if (dateFormatSelect) {
    dateFormatSelect.addEventListener("change", event => {
      demoState.settings.dateFormat = event.target.value;
      renderAppSections();
      saveState();
    });
  }

  if (unitSelect) {
    unitSelect.addEventListener("change", event => {
      demoState.settings.unitOfMeasurement = event.target.value;
      renderAppSections();
      saveState();
    });
  }

  if (resetFiltersBtn) {
    resetFiltersBtn.addEventListener("click", () => {
      resetDashboardFilters();
    });
  }

  if (staffAccountForm) {
    staffAccountForm.addEventListener("submit", async event => {
      event.preventDefault();
      try {
        if (!canManageSettings()) {
          showToast("Only admin accounts can manage employee access.", "danger");
          return;
        }

        const mode = String(staffModeInput?.value || "create").trim();
        const result = mode === "edit"
          ? await updateStaffAccount({
            id: staffEditingIdInput?.value,
            displayName: staffDisplayNameInput?.value,
            username: staffUsernameInput?.value,
            email: staffEmailInput?.value,
            password: staffPasswordInput?.value,
            role: staffRoleInput?.value,
            status: staffStatusInput?.value
          })
          : await createStaffAccount({
            displayName: staffDisplayNameInput?.value,
            username: staffUsernameInput?.value,
            email: staffEmailInput?.value,
            password: staffPasswordInput?.value,
            role: staffRoleInput?.value,
            status: staffStatusInput?.value
          });

        if (!result.ok) {
        showToast(result.reason || "Unable to save employee account.", "danger");
          return;
        }

        closeStaffModal();
        renderAppSections();
        showToast(mode === "edit" ? "Staff account updated." : "Staff account created.", "success");
      } catch (error) {
        console.warn("Failed to save staff account.", error);
        showToast("Unable to save the employee account right now.", "danger");
      }
    });
  }

  openStaffModalBtn?.addEventListener("click", () => {
    if (!canManageSettings()) return;
    openStaffModal("create");
  });

  closeStaffModalBtn?.addEventListener("click", closeStaffModal);

  staffModal?.addEventListener("click", event => {
    if (event.target === staffModal) {
      closeStaffModal();
    }
  });

  staffAccountsList?.addEventListener("click", event => {
    const editButton = event.target.closest("[data-edit-staff]");
    const deleteButton = event.target.closest("[data-delete-staff]");
    if (editButton) {
      if (!canManageSettings()) return;
      const staff = getStaffRecordById(editButton.dataset.editStaff);
      if (!staff) {
        showToast("That employee account could not be found.", "danger");
        return;
      }
      openStaffModal("edit", staff);
      return;
    }

    if (deleteButton) {
      if (!canManageSettings()) return;
      const staff = getStaffRecordById(deleteButton.dataset.deleteStaff);
      if (!staff) {
        showToast("That employee account could not be found.", "danger");
        return;
      }
      openDeleteModal({
        id: Number(staff.id),
        type: "staff",
        eyebrow: "Delete Employee",
        title: "Remove this employee account?",
        message: `Delete ${staff.displayName || staff.username || "this employee account"} permanently? This cannot be undone.`
      });
    }
  });


  inventorySearch.addEventListener("input", () => renderInventory());
  inventoryCategoryFilter.addEventListener("change", () => renderInventory());
  inventoryTypeFilter.addEventListener("change", () => renderInventory());
  inventoryAvailabilityFilter.addEventListener("change", () => renderInventory());
  roomCaptureRefreshBtn?.addEventListener("click", async () => {
    const quotationWorkspace = captureActiveQuotationWorkspace();
    await loadState();
    normalizeInventoryRecords(demoState);
    if (!restoreActiveQuotationWorkspace(quotationWorkspace)) {
      if (appState.currentUser?.role === "customer") {
        syncCustomerSessionContext();
      } else if (!appState.selectedQuotationCustomer && !appState.quotationReturnView) {
        clearStaffQuotationSession();
      }
    }
    renderAppSections();
  });
  roomCaptureDeleteAllBtn?.addEventListener("click", () => {
    if (!canManageRoomCaptures()) return;
    if (!demoState.roomCaptures.length) return;
    openDeleteModal({
      id: -1,
      type: "roomCaptureAll",
      eyebrow: "Delete All Captures",
      title: "Remove all room captures?",
      message: `Delete all ${demoState.roomCaptures.length} saved room captures from the dashboard? This cannot be undone.`
    });
  });
}

async function init() {
  await loadState();
  const needsFirstAdmin = !Array.isArray(demoState.users) || demoState.users.length === 0;
  loginForm.classList.toggle("hidden", needsFirstAdmin);
  firstAdminSetup?.classList.toggle("hidden", !needsFirstAdmin);
  // `quotation` is the transient active workspace, not the customer history.
  // Do not reopen the last staff member's unfinished quote on application
  // startup; customer-specific drafts remain in quotationDrafts.
  if (!appState.selectedQuotationCustomer) {
    resetQuotationDraft(Number(demoState.settings.quotationSequence || 1));
    // Clean up active quotation data written by older builds so it cannot be
    // reopened by the next staff/admin session.
    saveState();
  }
  setupPersistenceLoadingIndicator();
  updateConnectionStatus();
  window.addEventListener("online", updateConnectionStatus);
  window.addEventListener("offline", updateConnectionStatus);
  bindEvents();
  subscribeToSharedState();
  normalizeInventoryRecords(demoState);
  applyTheme(demoState.settings.themeMode);
  renderCategoryGroupOptions("");
  renderInventoryFilters();
  renderAppSections();
  enhancePasswordFields();
  syncQuotationDraftDefaults();
  if (saveQuotationBtn) saveQuotationBtn.dataset.quotationDirty = "false";
  syncQuotationActionButtons();

  populateSalesSelectors();
  const today = new Date();
  if (salesMonth) salesMonth.value = String(today.getMonth());
  if (salesYear) salesYear.value = String(today.getFullYear());
  salesViewState.monthIndex = null;
  salesViewState.year = today.getFullYear();
  populateSalesViewControls();
  renderSalesTrend();
  renderSalesPerformanceView();

  if (!demoState.settings.requireStaffLogin) {
    appState.currentUser = resolveUserSession("admin");
    applyCurrentAccessRules();
    loginScreen.classList.add("hidden");
    appShell.classList.remove("hidden");
    syncCustomerSessionContext();
    renderAppSections();
    showView(getDefaultViewForRole(appState.currentUser?.role));
  }

  positionNavIndicator(navItems.find(item => item.classList.contains("active"))?.dataset.view || "dashboard");
  window.addEventListener("resize", () => {
    const activeView = navItems.find(item => item.classList.contains("active"))?.dataset.view || "dashboard";
    positionNavIndicator(activeView);
  });

  registerSessionActivity();
}

function subscribeToSharedState() {
  if (typeof EventSource === "undefined") return;

  const refresh = () => {
    if (document.visibilityState === "hidden") return;
    clearTimeout(sharedStateRefreshTimer);
    sharedStateRefreshTimer = setTimeout(async () => {
      const refreshed = await refreshStateFromBackend();
      if (!refreshed) return;

      normalizeInventoryRecords(demoState);
      applyTheme(demoState.settings.themeMode);
      renderAppSections();

      const currentUsername = String(appState.currentUser?.username || "").toLowerCase();
      if (currentUsername) {
        const currentUser = (demoState.users || []).find(user => (
          String(user.username || "").toLowerCase() === currentUsername &&
          String(user.status || "active").toLowerCase() === "active"
        ));
        if (!currentUser) {
          logoutCurrentUser();
          return;
        }
        appState.currentUser = currentUser;
      }
      applyCurrentAccessRules();
    }, 250);
  };

  const events = new EventSource("/api/events");
  events.addEventListener("state-changed", refresh);
  document.addEventListener("visibilitychange", () => {
    if (document.visibilityState === "visible") refresh();
  });
}

init().catch(error => {
  console.warn("Failed to initialize admin dashboard.", error);
});


