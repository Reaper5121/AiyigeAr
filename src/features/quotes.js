import { demoState } from "../core/state.js";
import { appState } from "../core/runtime.js";
import {
  quoteMeta,
  quoteTable,
  quoteSubtotal,
  quoteDiscountAmount,
  quoteVat,
  quoteTotal,
  quoteDeliveryTotal,
  quoteNumber,
  quoteDateIssued,
  quoteValidUntil,
  quoteSource,
  quoteDeliveryCost,
  quoteCourier,
  quoteTerms,
  quotePreparedBy,
  quoteNumberDisplay,
  quoteDateIssuedDisplay,
  quotePreparedByDisplay,
  quoteUnitPrice,
  quoteItemPreview,
  quoteCustomerWants,
  quoteSummaryName,
  quoteSummaryNumber,
  quoteSummaryContactNumber,
  quoteSummaryEmailAddress,
  quoteSummaryValidUntil,
  quoteSummaryPreparedBy,
  quoteRemarks,
  backQuotationBtn,
  quoteFirstName,
  quoteMiddleInitial,
  quoteLastName,
  quoteStreetAddress,
  quoteCityProvince,
  quotePostalCode,
  quoteContactNumber,
  quoteEmailAddress,
  quotePanel,
  quoteUom,
  quoteQty,
  quoteDiscount,
  saveQuotationBtn
} from "../core/dom.js";
import { formatPeso, escapeHtml, formatShortDate, calculatePanelQuoteTotal, parseMoney, getCustomerSelectedPaymentMethod, resolvePriceUnit } from "../core/utils.js";

function buildCustomerName() {
  const firstName = quoteFirstName ? quoteFirstName.value.trim() : "";
  const middleInitial = quoteMiddleInitial ? quoteMiddleInitial.value.trim() : "";
  const lastName = quoteLastName ? quoteLastName.value.trim() : "";
  return [firstName, middleInitial, lastName].filter(Boolean).join(" ").replace(/\s+/g, " ").trim();
}

function buildCustomerAddress() {
  const streetAddress = quoteStreetAddress ? quoteStreetAddress.value.trim() : "";
  const cityProvince = quoteCityProvince ? quoteCityProvince.value.trim() : "";
  const postalCode = quotePostalCode ? quotePostalCode.value.trim() : "";
  return {
    streetAddress,
    cityProvince,
    postalCode,
    customerAddress: [streetAddress, cityProvince, postalCode].filter(Boolean).join(", ")
  };
}

function splitCustomerAddress(address = "") {
  const parts = String(address)
    .split(",")
    .map(part => part.trim())
    .filter(Boolean);

  if (!parts.length) {
    return { streetAddress: "", cityProvince: "", postalCode: "" };
  }

  if (parts.length === 1) {
    return { streetAddress: parts[0], cityProvince: "", postalCode: "" };
  }

  if (parts.length === 2) {
    return { streetAddress: parts[0], cityProvince: parts[1], postalCode: "" };
  }

  return {
    streetAddress: parts[0],
    cityProvince: parts[1],
    postalCode: parts[2] || ""
  };
}

function splitCustomerName(fullName = "") {
  const parts = String(fullName).trim().split(/\s+/).filter(Boolean);
  if (!parts.length) {
    return { firstName: "", middleInitial: "", lastName: "" };
  }
  if (parts.length === 1) {
    return { firstName: parts[0], middleInitial: "", lastName: "" };
  }
  if (parts.length === 2) {
    return { firstName: parts[0], middleInitial: "", lastName: parts[1] };
  }
  return {
    firstName: parts[0],
    middleInitial: parts[1].replace(/\./g, ""),
    lastName: parts.slice(2).join(" ")
  };
}

function normalizeQuotationNotes(meta = {}) {
  const wants = String(meta.wants ?? meta.customerWants ?? "").trim();
  const remarks = String(meta.remarks || "").trim();

  if (wants && remarks && wants === remarks) {
    return {
      ...meta,
      wants: "",
      customerWants: "",
      remarks
    };
  }

  return meta;
}

function findCustomerRecord(customerName = "") {
  const needle = String(customerName || "").trim().toLowerCase();
  if (!needle) return null;

  return (demoState.customers || []).find(customer => (
    [customer.name, customer.customerName, customer.displayName, customer.fullName]
      .some(value => String(value || "").trim().toLowerCase() === needle)
  )) || null;
}

function getActiveCustomerProfile(customerName = "") {
  const currentUser = appState.currentUser;
  if (!currentUser || currentUser.role !== "customer") return null;

  const knownNames = [currentUser.name, currentUser.customerName, currentUser.displayName, currentUser.username]
    .map(value => String(value || "").trim().toLowerCase())
    .filter(Boolean);
  const requestedName = String(customerName || "").trim().toLowerCase();
  return !requestedName || knownNames.includes(requestedName) ? currentUser : null;
}

function resolveQuotationCustomerMeta(customerName = "") {
  const name = String(customerName || "").trim();
  const customer = findCustomerRecord(name);
  const activeProfile = getActiveCustomerProfile(name);
  const savedQuotation = getLatestSavedQuotationForCustomer(name);
  const savedMeta = savedQuotation?.meta || {};
  const latestOrder = getLatestOrderForCustomer(name);

  const customerAddress = savedMeta.customerAddress
    || [savedMeta.streetAddress, savedMeta.cityProvince, savedMeta.postalCode].filter(Boolean).join(", ")
    || customer?.address
    || "";

  const streetAddress = savedMeta.streetAddress
    || customer?.streetAddress
    || customer?.addressLine1
    || activeProfile?.streetAddress
    || activeProfile?.addressLine1
    || latestOrder?.streetAddress
    || "";
  const cityProvince = savedMeta.cityProvince
    || customer?.cityProvince
    || customer?.addressLine2
    || activeProfile?.cityProvince
    || activeProfile?.addressLine2
    || latestOrder?.cityProvince
    || "";
  const postalCode = savedMeta.postalCode
    || customer?.postalCode
    || customer?.postal_code
    || activeProfile?.postalCode
    || activeProfile?.postal_code
    || latestOrder?.postalCode
    || latestOrder?.postal_code
    || "";

  return normalizeQuotationNotes({
    customerName: customer?.name || customer?.customerName || customer?.displayName || activeProfile?.customerName || activeProfile?.displayName || savedMeta.customerName || name,
    streetAddress,
    cityProvince,
    postalCode,
    customerAddress: customerAddress || [streetAddress, cityProvince, postalCode].filter(Boolean).join(", "),
    contactNumber: customer?.contact
      || customer?.mobileNumber
      || customer?.mobile_number
      || customer?.contactNumber
      || customer?.phoneNumber
      || customer?.phone
      || activeProfile?.contact
      || activeProfile?.mobileNumber
      || activeProfile?.mobile_number
      || activeProfile?.contactNumber
      || activeProfile?.phoneNumber
      || activeProfile?.phone
      || savedMeta.contactNumber
      || latestOrder?.contactNumber
      || latestOrder?.customerPhone
      || "",
    emailAddress: customer?.email || activeProfile?.email || savedMeta.emailAddress || latestOrder?.emailAddress || "",
    quotationNumber: "",
    dateIssued: "",
    validUntil: "",
    preparedBy: "",
    wants: savedMeta.wants || savedMeta.customerWants || latestOrder?.wants || "",
    remarks: customer?.notes || savedMeta.remarks || ""
  });
}

function findInventoryItemByLabel(label = "") {
  const needle = String(label || "").trim().toLowerCase();
  if (!needle) return null;

  return (demoState.inventory || []).find(item => {
    const candidates = [
      item.productCode,
      item.code,
      item.productName,
      item.name,
      item.categoryName
    ].map(value => String(value || "").trim().toLowerCase());
    return candidates.includes(needle);
  }) || null;
}

function splitPurchaseSummary(products = "") {
  return String(products || "")
    .split(/[,|·]/)
    .map(part => part.trim())
    .filter(Boolean);
}

function parsePurchaseSummaryEntry(entry = "") {
  const text = String(entry || "").trim();
  if (!text) {
    return { label: "", qty: 1 };
  }

  const match = text.match(/^(.*?)(?:\s*[x×]\s*(\d+(?:\.\d+)?))$/i);
  if (!match) {
    return { label: text, qty: 1 };
  }

  return {
    label: match[1].trim(),
    qty: Math.max(1, Number(match[2]) || 1)
  };
}

function normalizeQuotationItem(item = {}) {
  const qty = Math.max(1, Number(item.qty || 1) || 1);
  const discountPercent = Math.min(100, Math.max(0, Number(item.discountPercent || 0) || 0));
  const inventoryItem = findInventoryItemByLabel(item.name || item.productName || item.productCode || "");
  const pricePerMeter = Number(item.pricePerMeter ?? item.price ?? inventoryItem?.pricePerMeter ?? inventoryItem?.price ?? 0) || 0;
  const priceUnit = resolvePriceUnit(item.priceUnit ? item : (inventoryItem || item));
  const lengthMeters = Number(item.lengthMeters || 0) || calculatePanelQuoteTotal(inventoryItem || item, qty).lengthMeters || 0;
  // Always derive the line total from the current quantity, length, and unit
  // price. Saved line totals can be stale after an item is edited.
  const lineTotal = roundCurrency(pricePerMeter * (priceUnit === "meter" ? Math.max(lengthMeters, 1) : 1) * qty);

  return {
    ...item,
    name: item.name || inventoryItem?.name || item.productCode || item.productName || "Item",
    qty,
    uom: item.uom || (priceUnit === "meter" ? "m" : "pc"),
    price: pricePerMeter,
    pricePerMeter,
    priceUnit,
    lengthMeters,
    discountPercent,
    discountAmount: getDiscountAmount(lineTotal, discountPercent),
    lineTotal
  };
}

function buildQuotationItemFromInventoryItem(item = {}, qty = 1, discountPercent = 0) {
  const pricing = calculatePanelQuoteTotal(item, qty);
  const lineTotal = pricing.total;

  return normalizeQuotationItem({
    name: item.name,
    categoryGroup: item.categoryGroup,
    qty: pricing.boxes,
    uom: pricing.priceUnit === "meter" ? "m" : "pc",
    price: pricing.pricePerMeter,
    pricePerMeter: pricing.pricePerMeter,
    priceUnit: pricing.priceUnit,
    lengthMeters: pricing.lengthMeters,
    discountPercent,
    discountAmount: lineTotal * (discountPercent / 100),
    lineTotal
  });
}

function getQuotationItemsForCustomer(customerName = "") {
  const name = String(customerName || "").trim();
  if (!name) return [];

  const savedQuotation = getLatestSavedQuotationForCustomer(name);
  if (savedQuotation?.items?.length) {
    return savedQuotation.items.map(item => normalizeQuotationItem(item));
  }

  const latestOrder = getLatestOrderForCustomer(name);
  if (latestOrder?.products) {
    return splitPurchaseSummary(latestOrder.products)
      .map(parsePurchaseSummaryEntry)
      .map(entry => {
        const matchedItem = findInventoryItemByLabel(entry.label);
        return matchedItem ? buildQuotationItemFromInventoryItem(matchedItem, entry.qty, 0) : null;
      })
      .filter(Boolean);
  }

  const customer = findCustomerRecord(name);
  if (customer?.quote) {
    const matchedItem = findInventoryItemByLabel(customer.quote);
    if (matchedItem) {
      return [buildQuotationItemFromInventoryItem(matchedItem, 1, 0)];
    }
  }

  return [];
}

function syncQuotationPreviewFromItems(items = []) {
  const firstItem = items[0];
  const firstLabel = firstItem?.name || firstItem?.productName || firstItem?.productCode || "";
  const matchedItem = findInventoryItemByLabel(firstLabel);

  if (quotePanel) {
    quotePanel.value = matchedItem ? String(matchedItem.id) : "";
  }
  if (quoteQty) quoteQty.value = firstItem?.qty || 1;
  if (quoteDiscount) quoteDiscount.value = firstItem?.discountPercent || 0;
  if (quoteUom) quoteUom.value = firstItem?.uom || "box";
  if (quoteUnitPrice) {
    const previewPrice = Number(firstItem?.pricePerMeter ?? firstItem?.price ?? 0) || 0;
    quoteUnitPrice.value = firstItem ? String(previewPrice) : "";
    quoteUnitPrice.dataset.customPrice = previewPrice > 0 ? "true" : "false";
  }

  if (firstItem) {
    updateQuotationItemPreview(matchedItem || null);
  } else {
    updateQuotationItemPreview(null);
  }
}

function getLatestSavedQuotationForCustomer(customerName = "") {
  const needle = String(customerName || "").trim().toLowerCase();
  if (!needle) return null;
  const saved = [...(demoState.savedQuotations || [])].reverse();
  return saved.find(entry => String(entry.meta?.customerName || "").trim().toLowerCase() === needle) || null;
}

function getLatestIncomingQuotationForCustomer(customerName = "") {
  const needle = String(customerName || "").trim().toLowerCase();
  if (!needle) return null;
  return [...(demoState.paymentRequests || [])]
    .filter(request => String(request.customer_name || request.customerName || "").trim().toLowerCase() === needle)
    .sort((left, right) => Date.parse(right.created_at || right.createdAt || right.updated_at || right.updatedAt || "") - Date.parse(left.created_at || left.createdAt || left.updated_at || left.updatedAt || ""))[0] || null;
}

function getLatestOrderForCustomer(customerName = "") {
  const needle = String(customerName || "").trim().toLowerCase();
  if (!needle) return null;

  const paymentRequestOrders = (demoState.paymentRequests || []).map(request => ({
    invoice: request.quote_number || request.quoteNumber || "",
    quoteNumber: request.quote_number || request.quoteNumber || "",
    customer: request.customer_name || request.customerName || "",
    products: request.summary || request.item_name || request.itemName || "",
    amount: request.total_amount || request.totalAmount || 0,
    contactNumber: request.customer_phone || request.customerPhone || "",
    emailAddress: request.customer_email || request.customerEmail || "",
    streetAddress: request.street_address || request.streetAddress || "",
    cityProvince: request.city_province || request.cityProvince || "",
    postalCode: request.postal_code || request.postalCode || "",
    wants: request.notes || "",
    createdAt: request.created_at || request.createdAt || request.updated_at || request.updatedAt || ""
  }));

  return [...(demoState.orders || []), ...paymentRequestOrders]
    .filter(order => String(order.customer || "").trim().toLowerCase() === needle)
    .sort((left, right) => {
      const rightTime = Date.parse(String(right.createdAt || right.savedAt || right.updatedAt || "")) || 0;
      const leftTime = Date.parse(String(left.createdAt || left.savedAt || left.updatedAt || "")) || 0;
      return rightTime - leftTime;
    })[0] || null;
}

function getPreferredQuotationItemForCustomer(customerName = "") {
  const latestOrder = getLatestOrderForCustomer(customerName);
  const orderProductLabel = splitPurchaseSummary(latestOrder?.products || "")[0] || "";
  const orderItem = findInventoryItemByLabel(orderProductLabel);
  if (orderItem) return orderItem;

  const savedQuotation = getLatestSavedQuotationForCustomer(customerName);
  const savedItemLabel = savedQuotation?.items?.[0]?.name || savedQuotation?.items?.[0]?.productName || "";
  const savedItem = findInventoryItemByLabel(savedItemLabel);
  if (savedItem) return savedItem;

  const customer = findCustomerRecord(customerName);
  return customer ? findInventoryItemByLabel(customer.quote) : null;
}

function applyQuotationItemsForCustomer(customerName = "") {
  const items = getQuotationItemsForCustomer(customerName);
  if (!items.length) {
    return false;
  }

  demoState.quotation = items.map(item => ({ ...item }));
  syncQuotationPreviewFromItems(items);
  return true;
}

function getQuotationSequence() {
  const sequence = Number(demoState.settings?.quotationSequence || 1);
  return Number.isFinite(sequence) && sequence > 0 ? sequence : 1;
}

export function getQuotationNumber(sequence = getQuotationSequence()) {
  const year = new Date().getFullYear();
  const serial = String(sequence).padStart(3, "0");
  return `QT-${year}-${serial}`;
}

function normalizeDraftKey(customerName = "") {
  return String(customerName || "").trim().toLowerCase();
}

export function getQuotationDraft(customerName = appState.selectedQuotationCustomer || "") {
  const key = normalizeDraftKey(customerName);
  if (!key) return null;
  return demoState.quotationDrafts?.[key] || null;
}

export function saveQuotationDraft(customerName = appState.selectedQuotationCustomer || "") {
  const key = normalizeDraftKey(customerName);
  if (!key) return false;

  demoState.quotationDrafts = demoState.quotationDrafts || {};
  demoState.quotationDrafts[key] = {
    meta: getCurrentQuotationMeta(),
    items: (demoState.quotation || []).map(item => ({ ...item })),
    ui: {
      panelId: quotePanel?.value || "",
      qty: quoteQty ? quoteQty.value : "1",
      discount: quoteDiscount ? quoteDiscount.value : "0",
      uom: quoteUom ? quoteUom.value : "box"
    }
  };

  return true;
}

export function applyQuotationDraft(customerName = "", draft = null) {
  const activeDraft = draft || getQuotationDraft(customerName);
  if (!activeDraft) return false;

  demoState.quotation = (activeDraft.items || []).map(item => ({ ...item }));
  demoState.quotationMeta = {
    ...activeDraft.meta
  };

  setCurrentQuotationMeta(activeDraft.meta || {});

  if (quotePanel) quotePanel.value = activeDraft.ui?.panelId || "";
  if (quoteQty) quoteQty.value = activeDraft.ui?.qty ?? 1;
  if (quoteDiscount) quoteDiscount.value = activeDraft.ui?.discount ?? 0;
  if (quoteUom) quoteUom.value = activeDraft.ui?.uom || "box";

  const selectedItem = demoState.inventory.find(item => String(item.id) === String(quotePanel?.value));
  updateQuotationItemPreview(selectedItem || null);
  renderQuoteSummary();
  return true;
}

export function syncQuotationDraftDefaults() {
  const hasSelectedCustomer = Boolean(appState.selectedQuotationCustomer);

  if (hasSelectedCustomer) {
    applyCustomerQuotationDefaults(appState.selectedQuotationCustomer);
  }

  if (hasSelectedCustomer) {
    if (quoteNumber && !quoteNumber.value) {
      quoteNumber.value = getQuotationNumber();
    }

    if (quoteDateIssued && !quoteDateIssued.value) {
      quoteDateIssued.value = formatShortDate(new Date());
    }

    if (quoteValidUntil && !quoteValidUntil.value) {
      const validUntil = new Date();
      validUntil.setDate(validUntil.getDate() + 14);
      quoteValidUntil.value = validUntil.toISOString().slice(0, 10);
    }

    if (quotePreparedBy && !quotePreparedBy.value) {
      const preparedBy = document.querySelector(".profile-chip strong")?.textContent?.trim() || "Admin User";
      quotePreparedBy.value = preparedBy;
    }
  }

  if (quoteNumberDisplay && quoteNumber) quoteNumberDisplay.textContent = quoteNumber.value || "--";
  if (quoteDateIssuedDisplay && quoteDateIssued) quoteDateIssuedDisplay.textContent = quoteDateIssued.value || "--";
  if (quotePreparedByDisplay && quotePreparedBy) quotePreparedByDisplay.textContent = quotePreparedBy.value || "--";
}

export function getCurrentQuotationMeta() {
  const customerName = buildCustomerName()
    || String(appState.selectedQuotationCustomer || "").trim()
    || demoState.quotationMeta?.customerName
    || "";
  const address = buildCustomerAddress();
  const savedAddress = demoState.quotationMeta || {};
  const customerRecord = findCustomerRecord(customerName);
  const activeProfile = getActiveCustomerProfile(customerName);
  const latestOrder = getLatestOrderForCustomer(customerName);
  const fallbackAddress = savedAddress.customerAddress ? splitCustomerAddress(savedAddress.customerAddress) : null;
  const hasSelectedCustomer = Boolean(appState.selectedQuotationCustomer);
  return normalizeQuotationNotes({
    customerName,
    streetAddress: address.streetAddress || savedAddress.streetAddress || fallbackAddress?.streetAddress || customerRecord?.streetAddress || activeProfile?.streetAddress || activeProfile?.addressLine1 || latestOrder?.streetAddress || "",
    cityProvince: address.cityProvince || savedAddress.cityProvince || fallbackAddress?.cityProvince || customerRecord?.cityProvince || activeProfile?.cityProvince || activeProfile?.addressLine2 || latestOrder?.cityProvince || "",
    postalCode: address.postalCode || savedAddress.postalCode || fallbackAddress?.postalCode || customerRecord?.postalCode || customerRecord?.postal_code || activeProfile?.postalCode || activeProfile?.postal_code || latestOrder?.postalCode || latestOrder?.postal_code || "",
    customerAddress: address.customerAddress || savedAddress.customerAddress || "",
    contactNumber: quoteContactNumber?.value.trim()
      || savedAddress.contactNumber
      || customerRecord?.contact
      || customerRecord?.mobileNumber
      || customerRecord?.mobile_number
      || customerRecord?.contactNumber
      || customerRecord?.phoneNumber
      || customerRecord?.phone
      || activeProfile?.contact
      || activeProfile?.mobileNumber
      || activeProfile?.mobile_number
      || activeProfile?.contactNumber
      || activeProfile?.phoneNumber
      || activeProfile?.phone
      || latestOrder?.contactNumber
      || latestOrder?.customerPhone
      || "",
    emailAddress: quoteEmailAddress ? quoteEmailAddress.value.trim() || savedAddress.emailAddress || customerRecord?.email || activeProfile?.email || latestOrder?.emailAddress || latestOrder?.customerEmail || "" : (savedAddress.emailAddress || customerRecord?.email || activeProfile?.email || latestOrder?.emailAddress || latestOrder?.customerEmail || ""),
    quotationNumber: quoteNumber ? quoteNumber.value.trim() || (hasSelectedCustomer ? getQuotationNumber() : "") : (hasSelectedCustomer ? getQuotationNumber() : ""),
    dateIssued: quoteDateIssued ? quoteDateIssued.value : (savedAddress.dateIssued || (hasSelectedCustomer ? formatShortDate(new Date()) : "")),
    validUntil: quoteValidUntil ? quoteValidUntil.value : (savedAddress.validUntil || ""),
    source: quoteSource ? quoteSource.value : (savedAddress.source || "Catalog"),
    deliveryCost: quoteDeliveryCost ? Number(quoteDeliveryCost.value || 0) : Number(savedAddress.deliveryCost || 0),
    courier: quoteCourier ? quoteCourier.value : (savedAddress.courier || ""),
    terms: quoteTerms ? quoteTerms.value.trim() : (savedAddress.terms || ""),
    preparedBy: quotePreparedBy ? quotePreparedBy.value.trim() : (savedAddress.preparedBy || (hasSelectedCustomer ? "Admin User" : "")),
    wants: quoteCustomerWants ? quoteCustomerWants.value.trim() : (savedAddress.wants || savedAddress.customerWants || ""),
    remarks: quoteRemarks ? quoteRemarks.value.trim() : (savedAddress.remarks || "")
  });
}

export function applyCustomerQuotationDefaults(customerName = "", { force = false } = {}) {
  const currentMeta = getCurrentQuotationMeta();
  const nextMeta = resolveQuotationCustomerMeta(customerName || appState.selectedQuotationCustomer || currentMeta.customerName || "");
  if (!nextMeta.customerName) return false;

  const isDifferentCustomer = String(currentMeta.customerName || "").trim().toLowerCase() !== String(nextMeta.customerName || "").trim().toLowerCase();
  const shouldApply = force || isDifferentCustomer || !currentMeta.customerName;

  if (!shouldApply) {
    return true;
  }

  appState.selectedQuotationCustomer = nextMeta.customerName || customerName || null;
  setCurrentQuotationMeta(nextMeta);

  return true;
}

export function resetQuotationWorkspace(customerName = appState.selectedQuotationCustomer || "", { preserveDraft = false } = {}) {
  const key = normalizeDraftKey(customerName);
  if (preserveDraft && key) {
    saveQuotationDraft(customerName);
  }
  appState.selectedQuotationCustomer = customerName || null;
  resetQuotationDraft(Number(demoState.settings.quotationSequence || 1));
}

export function setCurrentQuotationMeta(meta = {}) {
  const cleanMeta = normalizeQuotationNotes(meta);
  demoState.quotationMeta = {
    ...getCurrentQuotationMeta(),
    ...cleanMeta
  };

  if (cleanMeta.customerName != null) {
    const split = splitCustomerName(cleanMeta.customerName);
    if (quoteFirstName) quoteFirstName.value = split.firstName;
    if (quoteMiddleInitial) quoteMiddleInitial.value = split.middleInitial;
    if (quoteLastName) quoteLastName.value = split.lastName;
  }
  const addressParts = cleanMeta.customerAddress != null ? splitCustomerAddress(cleanMeta.customerAddress) : null;
  if (quoteStreetAddress) quoteStreetAddress.value = cleanMeta.streetAddress ?? addressParts?.streetAddress ?? quoteStreetAddress.value;
  if (quoteCityProvince) quoteCityProvince.value = cleanMeta.cityProvince ?? addressParts?.cityProvince ?? quoteCityProvince.value;
  if (quotePostalCode) quotePostalCode.value = cleanMeta.postalCode ?? addressParts?.postalCode ?? quotePostalCode.value;
  if (cleanMeta.customerAddress != null && cleanMeta.streetAddress == null && cleanMeta.cityProvince == null) {
    demoState.quotationMeta.customerAddress = cleanMeta.customerAddress;
  }
  if (quoteContactNumber && cleanMeta.contactNumber != null) quoteContactNumber.value = cleanMeta.contactNumber;
  if (quoteEmailAddress && cleanMeta.emailAddress != null) quoteEmailAddress.value = cleanMeta.emailAddress;
  if (quoteNumber && cleanMeta.quotationNumber != null) quoteNumber.value = cleanMeta.quotationNumber;
  if (quoteDateIssued && cleanMeta.dateIssued != null) quoteDateIssued.value = cleanMeta.dateIssued;
  if (quoteValidUntil && cleanMeta.validUntil != null) quoteValidUntil.value = cleanMeta.validUntil;
  if (quoteSource && cleanMeta.source != null) quoteSource.value = cleanMeta.source;
  if (quoteDeliveryCost && cleanMeta.deliveryCost != null) quoteDeliveryCost.value = cleanMeta.deliveryCost;
  if (quoteCourier && cleanMeta.courier != null) quoteCourier.value = cleanMeta.courier;
  if (quoteTerms && cleanMeta.terms != null) quoteTerms.value = cleanMeta.terms;
  if (quotePreparedBy && cleanMeta.preparedBy != null) quotePreparedBy.value = cleanMeta.preparedBy;
  if (quoteCustomerWants && (cleanMeta.wants != null || cleanMeta.customerWants != null)) quoteCustomerWants.value = cleanMeta.wants ?? cleanMeta.customerWants ?? "";
  if (quoteRemarks && cleanMeta.remarks != null) quoteRemarks.value = cleanMeta.remarks;
  if (quoteNumberDisplay && quoteNumber) quoteNumberDisplay.textContent = quoteNumber.value || getQuotationNumber();
  if (quoteDateIssuedDisplay && quoteDateIssued) quoteDateIssuedDisplay.textContent = quoteDateIssued.value || formatShortDate(new Date());
  if (quotePreparedByDisplay && quotePreparedBy) quotePreparedByDisplay.textContent = quotePreparedBy.value || "Admin User";
}

function applySavedQuotationSnapshot(snapshot = null, { render = true } = {}) {
  if (!snapshot) return false;

  demoState.quotation = (snapshot.items || []).map(item => normalizeQuotationItem(item));
  demoState.quotationMeta = { ...(snapshot.meta || {}) };
  setCurrentQuotationMeta(snapshot.meta || {});

  syncQuotationPreviewFromItems(demoState.quotation);

  if (render) {
    renderQuoteSummary();
  }
  return true;
}

export function restoreQuotationForCustomer(customerName = "", { skipDraft = false, freshQuoteNumber = false } = {}) {
  const name = String(customerName || "").trim();
  if (!name) return false;

  appState.selectedQuotationCustomer = name;
  const freshMeta = freshQuoteNumber ? getCurrentQuotationMeta() : null;
  const draft = skipDraft ? null : getQuotationDraft(name);
  if (draft) {
    applyQuotationDraft(name, draft);
    return true;
  }

  const incoming = getLatestIncomingQuotationForCustomer(name);
  if (incoming) {
    const itemSummary = incoming.item_name || incoming.itemName || incoming.summary || "Payment request";
    const parsedItems = splitPurchaseSummary(itemSummary).map(parsePurchaseSummaryEntry).filter(entry => entry.label);
    const incomingItems = parsedItems.map(entry => {
      const matchedItem = findInventoryItemByLabel(entry.label);
      return matchedItem
        ? buildQuotationItemFromInventoryItem(matchedItem, entry.qty, 0)
        : normalizeQuotationItem({
          name: entry.label,
          qty: entry.qty,
          price: Number(incoming.unit_price || incoming.unitPrice || 0) || 0,
          pricePerMeter: Number(incoming.unit_price || incoming.unitPrice || 0) || 0,
          lengthMeters: 1,
          lineTotal: 0
        });
    });
    demoState.quotation = incomingItems.length ? incomingItems : [normalizeQuotationItem({
      name: itemSummary,
      qty: Math.max(1, Number(incoming.panels || 1) || 1),
      price: Number(incoming.unit_price || incoming.unitPrice || 0) || 0,
      pricePerMeter: Number(incoming.unit_price || incoming.unitPrice || 0) || 0,
      lengthMeters: 1,
      lineTotal: Number(incoming.total_amount || incoming.totalAmount || 0) || 0
    })];
    setCurrentQuotationMeta({
      customerName: incoming.customer_name || incoming.customerName || name,
      streetAddress: incoming.street_address || incoming.streetAddress || "",
      cityProvince: incoming.city_province || incoming.cityProvince || "",
      postalCode: incoming.postal_code || incoming.postalCode || "",
      customerAddress: [incoming.street_address || incoming.streetAddress, incoming.city_province || incoming.cityProvince, incoming.postal_code || incoming.postalCode].filter(Boolean).join(", "),
      contactNumber: incoming.customer_phone || incoming.customerPhone || "",
      emailAddress: incoming.customer_email || incoming.customerEmail || "",
      quotationNumber: incoming.quote_number || incoming.quoteNumber || getQuotationNumber(),
      dateIssued: formatShortDate(incoming.created_at || incoming.createdAt || new Date()),
      validUntil: "",
      preparedBy: "Customer App",
      wants: incoming.notes || "",
      remarks: incoming.notes || ""
    });
    syncQuotationPreviewFromItems(demoState.quotation);
    renderQuoteSummary();
    return true;
  }

  // A fresh estimate must not hydrate from the customer's previous saved
  // quotation or order. Those records are history, not the new workspace.
  const savedSnapshot = freshQuoteNumber ? null : getLatestSavedQuotationForCustomer(name);
  if (savedSnapshot) {
    applySavedQuotationSnapshot(savedSnapshot, { render: false });
    if (freshMeta) {
      setCurrentQuotationMeta({
        quotationNumber: freshMeta.quotationNumber || getQuotationNumber(),
        dateIssued: freshMeta.dateIssued || formatShortDate(new Date()),
        validUntil: freshMeta.validUntil || "",
        preparedBy: freshMeta.preparedBy || "Admin User"
      });
    }
    if (demoState.quotation.length) {
      renderQuoteSummary();
      return true;
    }
  }

  resetQuotationDraft(Number(demoState.settings.quotationSequence || 1));
  setCurrentQuotationMeta(resolveQuotationCustomerMeta(name));
  applyCustomerQuotationDefaults(name, { force: true });
  if (!freshQuoteNumber && applyQuotationItemsForCustomer(name)) {
    renderQuoteSummary();
    return true;
  }

  if (!freshQuoteNumber) {
    const preferredItem = getPreferredQuotationItemForCustomer(name);
    if (preferredItem) {
      demoState.quotation = [buildQuotationItemFromInventoryItem(preferredItem, 1, Number(quoteDiscount?.value || 0) || 0)];
      if (quotePanel) quotePanel.value = String(preferredItem.id);
      updateQuotationItemPreview(preferredItem);
    }
  }
  renderQuoteSummary();
  return true;
}

function getDiscountAmount(lineTotal, discountPercent = 0) {
  const safeDiscount = Math.min(100, Math.max(0, Number(discountPercent) || 0));
  return roundCurrency(lineTotal * (safeDiscount / 100));
}

function roundCurrency(value = 0) {
  return Math.round((Number(value) + Number.EPSILON) * 100) / 100;
}

export function calculateQuotationTotals(items = demoState.quotation || [], deliveryCost = 0) {
  const normalizedItems = items.map(item => recalculateQuotationItem(item));
  const subtotal = roundCurrency(normalizedItems.reduce((sum, item) => sum + item.lineTotal, 0));
  const discount = roundCurrency(normalizedItems.reduce((sum, item) => sum + getDiscountAmount(item.lineTotal, item.discountPercent || 0), 0));
  const taxableAmount = roundCurrency(Math.max(subtotal - discount, 0));
  const vat = 0;
  const grandTotal = roundCurrency(taxableAmount + (Number(deliveryCost) || 0));

  return { subtotal, discount, vat, grandTotal };
}

function getQuotationTotals() {
  const items = (demoState.quotation || []).map(item => recalculateQuotationItem(item));
  return calculateQuotationTotals(items, quoteDeliveryCost?.value || 0);
}

function updateQuotationTotalsDisplay() {
  const totals = getQuotationTotals();
  quoteSubtotal.textContent = formatPeso(totals.subtotal);
  quoteDiscountAmount.textContent = formatPeso(totals.discount);
  quoteVat.textContent = formatPeso(totals.vat);
  if (quoteDeliveryTotal) quoteDeliveryTotal.textContent = formatPeso(quoteDeliveryCost?.value || 0);
  quoteTotal.textContent = formatPeso(totals.grandTotal);
}

function recalculateQuotationItem(item = {}) {
  const qty = Math.max(1, Number(item.qty || 1) || 1);
  const pricePerMeter = Number(item.pricePerMeter ?? item.price ?? 0) || 0;
  const lengthMeters = Number(item.lengthMeters || 0) || calculatePanelQuoteTotal(item, 1).lengthMeters || 1;
  const discountPercent = Math.min(100, Math.max(0, Number(item.discountPercent || 0) || 0));
  const priceUnit = resolvePriceUnit(item);
  const lineTotal = roundCurrency(pricePerMeter * (priceUnit === "meter" ? lengthMeters : 1) * qty);

  return {
    ...item,
    qty,
    price: pricePerMeter,
    pricePerMeter,
    priceUnit,
    lengthMeters,
    discountPercent,
    discountAmount: getDiscountAmount(lineTotal, discountPercent),
    lineTotal
  };
}

export function updateQuotationItem(index, changes = {}, { render = true } = {}) {
  const item = demoState.quotation?.[index];
  if (!item) return false;

  demoState.quotation[index] = recalculateQuotationItem({
    ...item,
    ...changes
  });

  if (render) {
    renderQuoteSummary();
  } else {
    updateQuotationTotalsDisplay();
  }
  return true;
}

export function removeQuotationItem(index) {
  if (!demoState.quotation?.[index]) return false;
  demoState.quotation.splice(index, 1);
  renderQuoteSummary();
  return true;
}

export function updateQuotationItemPreview(item = null) {
  if (!quoteUnitPrice) return;

  if (!item) {
    quoteUnitPrice.value = "";
    delete quoteUnitPrice.dataset.customPrice;
    if (quoteItemPreview) {
      quoteItemPreview.textContent = "Select a product to see the price.";
    }
    return;
  }

  const quantity = Math.max(1, Number(quoteQty?.value || 1) || 1);
  const discountPercent = Math.min(100, Math.max(0, Number(quoteDiscount?.value || 0) || 0));
  const typedUnitPrice = parseMoney(quoteUnitPrice.value);
  const baseUnitPrice = Number(item.pricePerMeter ?? item.price ?? 0) || 0;
  const customUnitPrice = typedUnitPrice > 0 && quoteUnitPrice.dataset.customPrice === "true";
  const pricing = calculatePanelQuoteTotal({
    ...item,
    pricePerMeter: customUnitPrice ? typedUnitPrice : baseUnitPrice,
    price: customUnitPrice ? typedUnitPrice : baseUnitPrice
  }, quantity);
  const discountAmount = pricing.total * (discountPercent / 100);
  const estimatedTotal = Math.max(pricing.total - discountAmount, 0);
  const lengthLabel = pricing.lengthMeters > 0 ? `${pricing.lengthMeters} m` : String(item.length || item.size || "--");

  if (!customUnitPrice) {
    quoteUnitPrice.value = String(pricing.pricePerMeter || 0);
  }
  if (quoteItemPreview) {
    quoteItemPreview.innerHTML = `
      <strong>${escapeHtml(item.productCode || item.name || "Product")}</strong>
      <span>${quantity} ${pricing.priceUnit === "meter" ? "m" : "pc"}${quantity === 1 ? "" : "s"} | Line total ${formatPeso(estimatedTotal)}</span>
      <span>${escapeHtml(lengthLabel)} | ${formatPeso(pricing.unitPrice)} /${pricing.priceUnit === "meter" ? "m" : "pc"}</span>
    `;
  }
}

export function resetQuotationDraft(nextSequence = null) {
  demoState.quotation = [];
  demoState.quotationMeta = {};

  if (quoteFirstName) quoteFirstName.value = "";
  if (quoteMiddleInitial) quoteMiddleInitial.value = "";
  if (quoteLastName) quoteLastName.value = "";
  if (quoteStreetAddress) quoteStreetAddress.value = "";
  if (quoteCityProvince) quoteCityProvince.value = "";
  if (quotePostalCode) quotePostalCode.value = "";
  if (quoteContactNumber) quoteContactNumber.value = "";
  if (quoteEmailAddress) quoteEmailAddress.value = "";
  if (quoteCustomerWants) quoteCustomerWants.value = "";
  if (quoteRemarks) quoteRemarks.value = "";
  if (quoteUom) quoteUom.value = "box";
  if (quotePanel) quotePanel.value = "";
  if (quoteQty) quoteQty.value = 1;
  if (quoteDiscount) quoteDiscount.value = 0;
  if (quoteUnitPrice) {
    quoteUnitPrice.value = "";
    delete quoteUnitPrice.dataset.customPrice;
  }

  const sequence = nextSequence || getQuotationSequence();
  if (quoteNumber) quoteNumber.value = getQuotationNumber(sequence);
  if (quoteDateIssued) quoteDateIssued.value = formatShortDate(new Date());
  if (quoteValidUntil) {
    const validUntil = new Date();
    validUntil.setDate(validUntil.getDate() + 14);
    quoteValidUntil.value = validUntil.toISOString().slice(0, 10);
  }
  if (quoteSource && !quoteSource.value) quoteSource.value = "Catalog";
  if (quoteDeliveryCost) quoteDeliveryCost.value = "0";
  if (quoteCourier) quoteCourier.value = "";
  if (quoteTerms && !quoteTerms.value.trim()) quoteTerms.value = "Payment is due after quotation acceptance. Delivery is arranged through the selected courier.";
  if (quotePreparedBy) {
    quotePreparedBy.value = document.querySelector(".profile-chip strong")?.textContent?.trim() || "Admin User";
  }
  if (quoteNumberDisplay && quoteNumber) quoteNumberDisplay.textContent = quoteNumber.value || getQuotationNumber(sequence);
  if (quoteDateIssuedDisplay && quoteDateIssued) quoteDateIssuedDisplay.textContent = quoteDateIssued.value || formatShortDate(new Date());
  if (quotePreparedByDisplay && quotePreparedBy) quotePreparedByDisplay.textContent = quotePreparedBy.value || "Admin User";

  if (quotePanel) {
    const selectedItem = demoState.inventory.find(item => String(item.id) === String(quotePanel.value));
    updateQuotationItemPreview(selectedItem || null);
  } else {
    updateQuotationItemPreview(null);
  }
  renderQuoteSummary();
}

function getGeneratedQuotationForAdmin() {
  const quotations = Array.isArray(demoState.quotations) ? demoState.quotations : [];
  if (!quotations.length) return null;
  const selectedNumber = String(appState.selectedQuotationNumber || "").trim();
  if (selectedNumber) {
    const selected = quotations.find(quotation => String(quotation.quotation_no || "").trim() === selectedNumber);
    if (selected) return selected;
  }
  const selectedCustomer = String(appState.selectedQuotationCustomer || "").trim().toLowerCase();
  if (!selectedCustomer) return null;
  return quotations
    .filter(quotation => String(quotation.customer?.name || "").trim().toLowerCase() === selectedCustomer)
    .sort((left, right) => (Date.parse(right.created_at || "") || 0) - (Date.parse(left.created_at || "") || 0))[0] || null;
}

function findOrderForGeneratedQuotation(quotation) {
  const quotationNumber = String(quotation?.quotation_no || "").trim();
  const orderId = String(quotation?.order_id || "").trim();
  if (!orderId) return null;
  return (demoState.orders || []).find(order => (
    String(order.id || order.invoice || "").trim() === orderId
    && String(order.quoteNumber || order.invoice || "").trim() === quotationNumber
  )) || null;
}

export function getGeneratedQuotationPaymentState(quotation, order, matchedPayments = null) {
  const quotationNumber = String(quotation?.quotation_no || "").trim();
  const orderId = String(order?.id || order?.invoice || "").trim();
  const payments = matchedPayments || (demoState.payments || []).filter(payment => (
    String(payment.quoteNumber || "").trim() === quotationNumber
    || String(payment.invoice || payment.orderId || "").trim() === orderId
  ));
  const paidAmount = payments.reduce((sum, payment) => {
    const status = String(payment.status || "").trim().toLowerCase();
    return ["paid", "completed", "settled"].includes(status) ? sum + parseMoney(payment.amount || 0) : sum;
  }, 0);
  const total = Number(order?.amount || quotation?.total || 0);
  const status = String(order?.paymentStatus || payments[0]?.status || quotation?.payment_status || "Payment pending").trim();
  const complete = ["paid", "completed", "settled"].includes(status.toLowerCase()) || (total > 0 && paidAmount >= total);
  return { complete, status: complete ? "Payment complete" : status };
}

export function canPrintGeneratedQuotation(quotation, order = null) {
  const linkedOrder = findOrderForGeneratedQuotation(quotation);
  if (!linkedOrder || !quotation?.customer_confirmed_at) return false;
  if (order && String(order.id || order.invoice || "").trim() !== String(linkedOrder.id || linkedOrder.invoice || "").trim()) return false;
  const selectedPaymentMethod = getCustomerSelectedPaymentMethod(quotation.payment_method, linkedOrder.payment, linkedOrder.method, linkedOrder.paymentMethod, linkedOrder.payment_method);
  const hasCustomerSelectedMethod = Boolean(selectedPaymentMethod);
  return Boolean(hasCustomerSelectedMethod && getGeneratedQuotationPaymentState(quotation, linkedOrder).complete);
}

function renderGeneratedQuotation() {
  const quotation = getGeneratedQuotationForAdmin();
  if (!quotation || !quoteTable) return false;
  const quotationModal = document.getElementById("quotationDetailModal");
  if (quotationModal) quotationModal.hidden = false;
  const customer = quotation.customer || {};
  const order = findOrderForGeneratedQuotation(quotation);
  const payment = getGeneratedQuotationPaymentState(quotation, order);
  const items = Array.isArray(quotation.items_snapshot) ? quotation.items_snapshot : [];
  const money = value => formatPeso(Number(value || 0));
  const issuedDate = String(quotation.created_at || "").slice(0, 10);
  const customerContact = customer.contact || customer.phone || customer.contactNumber || "--";
  const customerEmail = customer.email || customer.emailAddress || customer.email_address || "--";
  const customerAddress = customer.deliveryAddress || customer.address || customer.delivery_address || "--";
  const documentLogo = document.getElementById("quotationDocumentLogo");
  if (documentLogo) documentLogo.src = new URL("../../client%20logo.jpg", import.meta.url).href;
  quoteMeta.innerHTML = `
    <div class="quotation-document-meta">
      <div><span>Quotation No.</span><strong>${escapeHtml(quotation.quotation_no || "--")}</strong></div>
      <div><span>Date Issued</span><strong>${escapeHtml(issuedDate ? formatShortDate(issuedDate) : "--")}</strong></div>
    </div>
    <div class="quotation-customer-details">
      <h4>Customer</h4>
      <strong class="quotation-customer-name">${escapeHtml(customer.name || "Customer")}</strong>
      <div class="quotation-customer-fields">
        <div><span>Contact No.</span><strong>${escapeHtml(customerContact)}</strong></div>
        <div><span>Email</span><strong>${escapeHtml(customerEmail)}</strong></div>
        <div class="address"><span>Address</span><strong>${escapeHtml(customerAddress)}</strong></div>
      </div>
    </div>`;
  const customerStatus = document.getElementById("quoteCustomerStatus");
  if (customerStatus) {
    const customerConfirmed = Boolean(order && quotation.customer_confirmed_at);
    const paymentMethod = String(quotation.payment_method || order?.payment || order?.method || "").trim();
    const methodSelected = Boolean(paymentMethod && !["quotation", "pending", "not selected"].includes(paymentMethod.toLowerCase()));
    customerStatus.innerHTML = `<span class="status-pill ${customerConfirmed ? "success" : "warning"}">${customerConfirmed ? "Customer confirmed" : "Awaiting confirmation"}</span><span class="status-pill ${methodSelected ? "success" : "warning"}">${methodSelected ? `Payment method: ${escapeHtml(paymentMethod)}` : "Payment method not selected"}</span><span class="status-pill ${payment.complete ? "success" : "warning"}">${escapeHtml(payment.status)}</span>`;
  }
  quoteTable.innerHTML = items.map(item => `
    <tr>
      <td><strong>${escapeHtml(item.panelModel || "Panel")}</strong><br /><small>${escapeHtml([item.size, item.finish].filter(Boolean).join(" | ") || "")}</small></td>
      <td><strong>${escapeHtml(String(item.quantity || 1))}</strong></td>
      <td><strong>${money(item.unitPrice)}</strong></td>
      <td><strong>${money(item.lineTotal)}</strong></td>
    </tr>
  `).join("");
  quoteSubtotal.textContent = money(quotation.subtotal);
  quoteDiscountAmount.textContent = money(0);
  quoteVat.textContent = money(quotation.vat);
  quoteTotal.textContent = money(quotation.total);
  if (quoteNumber) quoteNumber.value = quotation.quotation_no || "";
  if (quoteDateIssued) quoteDateIssued.value = issuedDate;
  if (quoteNumberDisplay) quoteNumberDisplay.textContent = quotation.quotation_no || "--";
  if (quoteDateIssuedDisplay) quoteDateIssuedDisplay.textContent = issuedDate || "--";
  if (quotePreparedByDisplay) quotePreparedByDisplay.textContent = "System generated";
  if (quoteSummaryName) quoteSummaryName.textContent = customer.name || "--";
  if (quoteSummaryNumber) quoteSummaryNumber.textContent = quotation.quotation_no || "--";
  if (quoteSummaryContactNumber) quoteSummaryContactNumber.textContent = customer.contact || "--";
  if (quoteSummaryEmailAddress) quoteSummaryEmailAddress.textContent = customer.email || "--";
  if (quoteSummaryValidUntil) quoteSummaryValidUntil.textContent = "No expiry";
  if (quoteSummaryPreparedBy) quoteSummaryPreparedBy.textContent = "System generated";
  if (quoteDeliveryCost) quoteDeliveryCost.value = String(quotation.delivery_fee || 0);
  if (quoteCourier) quoteCourier.value = quotation.courier || "";
  if (quoteTerms) quoteTerms.value = quotation.terms || "";
  // Keep this clickable for pending orders so staff receive an explanation.
  if (printQuoteBtn) printQuoteBtn.disabled = !quotation;
  if (saveQuotationBtn) saveQuotationBtn.hidden = true;
  if (clearQuotationBtn) clearQuotationBtn.hidden = true;
  if (recordQuotePaymentBtn) recordQuotePaymentBtn.hidden = true;
  if (markQuotePaidBtn) markQuotePaidBtn.hidden = true;
  syncQuotationWorkflow({ customerName: customer.name, itemCount: items.length, meta: { quotationNumber: quotation.quotation_no } });
  return true;
}

export function renderQuoteSummary() {
  if (renderGeneratedQuotation()) {
    [quotePanel, quoteQty, quoteUnitPrice, quoteDiscount, quoteUom, quoteDeliveryCost, quoteCourier, quoteSource, quoteTerms, quoteCustomerWants, quoteRemarks]
      .filter(Boolean)
      .forEach(field => { field.disabled = true; field.setAttribute("aria-readonly", "true"); });
    if (saveQuotationBtn) saveQuotationBtn.hidden = true;
    renderQuotationQueue();
    return;
  }
  if (!String(appState.selectedQuotationCustomer || "").trim()) {
    const quotationModal = document.getElementById("quotationDetailModal");
    if (quotationModal) quotationModal.hidden = true;
    demoState.quotation = [];
    if (quoteMeta) quoteMeta.textContent = "Select a customer from the quotation list.";
    if (quoteTable) quoteTable.innerHTML = "";
    if (quoteSubtotal) quoteSubtotal.textContent = formatPeso(0);
    if (quoteVat) quoteVat.textContent = formatPeso(0);
    if (quoteTotal) quoteTotal.textContent = formatPeso(0);
    if (quoteDeliveryTotal) quoteDeliveryTotal.textContent = formatPeso(0);
    const customerStatus = document.getElementById("quoteCustomerStatus");
    if (customerStatus) customerStatus.innerHTML = "";
    if (quoteNumber) quoteNumber.value = "";
    if (quoteNumberDisplay) quoteNumberDisplay.textContent = "--";
    if (quoteSummaryName) quoteSummaryName.textContent = "--";
    if (quoteSummaryNumber) quoteSummaryNumber.textContent = "--";
    if (quoteSummaryContactNumber) quoteSummaryContactNumber.textContent = "--";
    if (quoteSummaryEmailAddress) quoteSummaryEmailAddress.textContent = "--";
    if (quoteSummaryPreparedBy) quoteSummaryPreparedBy.textContent = "--";
    syncQuotationWorkflow({});
    renderQuotationQueue();
    return;
  }
  // Keep totals and displayed line amounts in sync even for drafts loaded from
  // older saved state that contains an outdated lineTotal.
  demoState.quotation = (demoState.quotation || []).map(item => recalculateQuotationItem(item));
  syncQuotationDraftDefaults();

  const meta = getCurrentQuotationMeta();
  if (
    quoteCustomerWants
    && quoteRemarks
    && quoteCustomerWants.value.trim()
    && quoteCustomerWants.value.trim() === quoteRemarks.value.trim()
  ) {
    quoteCustomerWants.value = "";
    demoState.quotationMeta = {
      ...(demoState.quotationMeta || {}),
      wants: "",
      customerWants: ""
    };
  }
  const validUntilText = meta.validUntil ? formatShortDate(meta.validUntil) : "";
  const items = demoState.quotation || [];
  const customerName = String(appState.selectedQuotationCustomer || meta.customerName || "").trim();

  if (!items.length) {
    const customerBits = [
      meta.customerName ? `Customer: ${meta.customerName}` : "",
      meta.contactNumber ? `Contact: ${meta.contactNumber}` : "",
      meta.emailAddress ? `Email: ${meta.emailAddress}` : "",
      meta.customerAddress ? `Address: ${meta.customerAddress}` : "",
      meta.wants ? `Wants: ${meta.wants}` : ""
    ].filter(Boolean);
    quoteMeta.textContent = customerBits.length
      ? `${customerBits.join(" | ")}. Add a product to start.`
      : "Start from Accounts, then add a product.";
    quoteTable.innerHTML = "";
    updateQuotationTotalsDisplay();
  } else {
    const customerBits = [
      meta.customerName ? `Customer: ${meta.customerName}` : "",
      meta.contactNumber ? `Contact: ${meta.contactNumber}` : "",
      meta.emailAddress ? `Email: ${meta.emailAddress}` : "",
      meta.customerAddress ? `Address: ${meta.customerAddress}` : "",
      meta.wants ? `Wants: ${meta.wants}` : ""
    ].filter(Boolean);
    quoteMeta.textContent = customerBits.length
      ? `${customerBits.join(" | ")}. Quotation preview.`
      : "Quotation preview.";
    quoteTable.innerHTML = items.map((item, index) => {
      const lineTotal = Number(item.lineTotal || 0);
      const discountAmount = getDiscountAmount(lineTotal, item.discountPercent || 0);
      const displayTotal = Math.max(lineTotal - discountAmount, 0);
      const discountLabel = item.discountPercent ? `${item.discountPercent}% discount` : "Standard rate";
      const unitPrice = Number(item.pricePerMeter ?? item.price ?? 0);
      return `
        <tr data-quote-row="${index}">
          <td>
            <strong>${escapeHtml(item.name)}</strong>
            <br />
            <small>${escapeHtml(item.lengthMeters ? `${item.lengthMeters} m` : item.uom || "pc")} | ${discountLabel}</small>
          </td>
          <td>
            <strong>${escapeHtml(String(item.qty || 1))}</strong>
          </td>
          <td>
            <strong>${formatPeso(unitPrice)}</strong>
            <small>/${item.priceUnit === "meter" ? "m" : "pc"}</small>
          </td>
          <td>
            <div class="quote-total-cell">
              <small>${discountLabel}</small>
              <strong>${formatPeso(displayTotal)}</strong>
            </div>
          </td>
        </tr>
      `;
    }).join("");
    updateQuotationTotalsDisplay();
  }

  if (quoteSummaryName) quoteSummaryName.textContent = meta.customerName || "--";
  if (quoteSummaryNumber) quoteSummaryNumber.textContent = meta.quotationNumber || "--";
  if (quoteSummaryContactNumber) quoteSummaryContactNumber.textContent = meta.contactNumber || "--";
  if (quoteSummaryEmailAddress) quoteSummaryEmailAddress.textContent = meta.emailAddress || "--";
  if (quoteSummaryValidUntil) quoteSummaryValidUntil.textContent = validUntilText || "--";
  if (quoteSummaryPreparedBy) quoteSummaryPreparedBy.textContent = meta.preparedBy || "--";

  if (backQuotationBtn) {
    const returnView = String(appState.quotationReturnView || "").trim();
    const hasReturnTarget = Boolean(returnView);
    backQuotationBtn.disabled = !hasReturnTarget;
    if (!hasReturnTarget) {
      backQuotationBtn.title = "No return location available";
    } else if (returnView === "payments") {
      backQuotationBtn.title = "Return to Payments";
    } else if (returnView === "customers") {
      backQuotationBtn.title = "Return to Customers";
    } else if (returnView === "dashboard") {
      backQuotationBtn.title = "Return to Dashboard";
    } else {
      backQuotationBtn.title = `Return to ${returnView}`;
    }
  }

  if (quoteCustomerWants && !quoteCustomerWants.value && meta.wants) {
    quoteCustomerWants.value = meta.wants;
  }

  if (quoteRemarks && !quoteRemarks.value && meta.remarks) {
    quoteRemarks.value = meta.remarks;
  }

  renderQuotationQueue();
  syncQuotationWorkflow({ customerName, itemCount: items.length, meta });

  // Staff quotations are generated snapshots. Keep all editor controls locked
  // even when an older cached DOM or legacy saved draft is present.
  [quotePanel, quoteQty, quoteUnitPrice, quoteDiscount, quoteUom, quoteDeliveryCost, quoteCourier, quoteSource, quoteTerms, quoteCustomerWants, quoteRemarks]
    .filter(Boolean)
    .forEach(field => {
      field.disabled = true;
      field.setAttribute("aria-readonly", "true");
    });
  if (addQuoteItemBtn) addQuoteItemBtn.hidden = true;
  if (saveQuotationBtn) saveQuotationBtn.hidden = true;
  if (clearQuotationBtn) clearQuotationBtn.hidden = true;
}

function syncQuotationWorkflow({ customerName = "", itemCount = 0, meta = {} } = {}) {
  const steps = [...document.querySelectorAll("[data-quote-step]")];
  const status = document.getElementById("quoteWorkflowStatus");
  const hint = document.getElementById("quoteWorkflowHint");
  if (!steps.length) return;

  const hasCustomer = Boolean(String(customerName || "").trim());
  const hasItems = Number(itemCount) > 0;
  const isSaved = Boolean(
    quoteNumber?.value
    && saveQuotationBtn?.dataset.quotationDirty !== "true"
    && (demoState.savedQuotations || []).some(entry => String(entry.number || entry.meta?.quotationNumber || "") === String(quoteNumber.value))
  );
  steps.forEach(step => step.classList.remove("active", "complete"));

  if (status) {
    status.textContent = isSaved || hasItems ? "Read-only quotation" : "No order selected";
    status.classList.toggle("success", isSaved || hasItems);
    status.classList.toggle("warning", !(isSaved || hasItems));
  }
  if (hint) {
    hint.textContent = hasItems
      ? `${meta.quotationNumber || quoteNumber?.value || "Quotation"} selected`
      : "Select an order";
  }
}

export function renderQuotationQueue() {
  const queue = document.getElementById("quotationQueue");
  if (!queue) return;

  const generated = Array.isArray(demoState.quotations) ? demoState.quotations : [];
  const orderByReference = new Map();
  for (const order of demoState.orders || []) {
    for (const reference of [order.id, order.invoice, order.quoteNumber]) {
      const key = String(reference || "").trim();
      if (key && !orderByReference.has(key)) orderByReference.set(key, order);
    }
  }
  const paymentsByReference = new Map();
  for (const payment of demoState.payments || []) {
    for (const reference of [payment.quoteNumber, payment.invoice, payment.orderId]) {
      const key = String(reference || "").trim();
      if (!key) continue;
      if (!paymentsByReference.has(key)) paymentsByReference.set(key, []);
      paymentsByReference.get(key).push(payment);
    }
  }
  const quotationRecords = generated.slice(0, 20).map(quotation => {
    const orderId = String(quotation?.order_id || "").trim();
    const quotationNo = String(quotation?.quotation_no || "").trim();
    const order = orderId ? orderByReference.get(orderId) || null : null;
    const paymentReferences = new Set([quotationNo, orderId, String(order?.id || "").trim(), String(order?.invoice || "").trim()]);
    const matchedPayments = [...paymentReferences].flatMap(reference => paymentsByReference.get(reference) || []);
    return { quotation, order, matchedPayments: [...new Set(matchedPayments)] };
  });
  if (!quotationRecords.length) {
    queue.innerHTML = `<div class="quotation-queue-empty">No mobile quotations have been issued yet.</div>`;
    return;
  }
  queue.innerHTML = quotationRecords.map(({ quotation, order, matchedPayments }) => {
    const payment = getGeneratedQuotationPaymentState(quotation, order, matchedPayments);
    const customerConfirmed = Boolean(order && quotation.customer_confirmed_at);
    const selectedMethod = getCustomerSelectedPaymentMethod(quotation.payment_method, order?.payment, order?.method, order?.paymentMethod, order?.payment_method);
    const paymentMethodSelected = Boolean(selectedMethod);
    const canPrint = canPrintGeneratedQuotation(quotation, order);
    const queueStatus = !customerConfirmed
      ? "Awaiting confirmation"
      : !paymentMethodSelected
        ? "Payment method not selected"
        : payment.complete
          ? "Paid · Ready for preparation"
          : "Awaiting payment";
    const queueStatusClass = canPrint ? "success" : "warning";
    const pendingMessage = !customerConfirmed
      ? "Wait for the customer to confirm the quotation."
      : !paymentMethodSelected
        ? "Wait for the customer to select a payment method."
        : "Printing is available after full payment is recorded.";
    return `
      <article class="quotation-queue-card" data-quotation-queue-select="${escapeHtml(quotation.quotation_no || "")}" tabindex="0" role="button" aria-label="Open quotation for ${escapeHtml(quotation.customer?.name || "customer")}">
        <div class="quotation-queue-head"><strong>${escapeHtml(quotation.customer?.name || "Customer")}</strong><span class="status-pill ${queueStatusClass}">${escapeHtml(queueStatus)}</span></div>
        <div class="quotation-queue-number">${escapeHtml(quotation.quotation_no || "Quotation")} · ${formatPeso(quotation.total || 0)}</div>
        <div class="quotation-queue-detail">${escapeHtml(payment.status)}${order ? ` · ${escapeHtml(quotation.courier || "Customer-selected courier")} · ${escapeHtml(order.deliveryStatus || order.status || "Order submitted")}` : ""}</div>
        ${canPrint ? `<button type="button" class="quotation-queue-action" data-generated-quotation="${escapeHtml(quotation.quotation_no || "")}">Print for preparation</button>` : `<div class="quotation-queue-detail">${pendingMessage}</div>`}
      </article>
    `;
  }).join("");
}

export function exportCurrentQuotation({ receipt = false } = {}) {
  const items = demoState.quotation || [];
  if (!items.length) {
    const status = document.getElementById("exportStatus");
    if (status) status.textContent = "No quotation to export yet.";
    return;
  }

  const meta = getCurrentQuotationMeta();
  const validUntilText = meta.validUntil ? formatShortDate(meta.validUntil) : "--";
  const totals = getQuotationTotals();
  const logoUrl = new URL("../../client%20logo.jpg", import.meta.url).href;
  const rows = items.map(item => {
    const discountAmount = getDiscountAmount(item.lineTotal || 0, item.discountPercent || 0);
    const lineTotal = Math.max((item.lineTotal || 0) - discountAmount, 0);
    const unitPrice = Number(item.pricePerMeter ?? item.price ?? 0);
    return `
      <tr>
        <td>${escapeHtml(item.name)}</td>
        <td>${escapeHtml(String(item.qty || 1))}</td>
        <td>${escapeHtml(item.uom || "box")}</td>
        <td>${formatPeso(unitPrice)} /${item.priceUnit === "meter" ? "m" : "pc"}</td>
        <td>${formatPeso(lineTotal)}</td>
      </tr>
    `;
  }).join("");

  const printWindow = window.open("", "_blank", "width=1100,height=900");
  if (!printWindow) {
    const status = document.getElementById("exportStatus");
    if (status) status.textContent = "Popup blocked. Please allow popups to generate the PDF.";
    return;
  }

  printWindow.document.write(`
    <!doctype html>
    <html>
      <head>
        <meta charset="utf-8" />
        <title>${escapeHtml(receipt ? `Receipt ${meta.quotationNumber || ""}` : (meta.quotationNumber || "Quotation"))}</title>
        <style>
          @page { size: A4; margin: 18mm; }
          * { box-sizing: border-box; }
          body {
            font-family: Arial, Helvetica, sans-serif;
            color: #0f172a;
            margin: 0;
            background: #fff;
          }
          .sheet {
            max-width: 760px;
            margin: 0 auto;
            padding: 0;
          }
          .header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            padding-bottom: 16px;
            border-bottom: 2px solid #e2e8f0;
            margin-bottom: 18px;
          }
          .brand {
            display: flex;
            align-items: center;
            gap: 14px;
          }
          .brand img {
            width: 58px;
            height: 58px;
            border-radius: 16px;
            object-fit: cover;
            border: 1px solid #e2e8f0;
          }
          .brand h1 {
            margin: 0;
            font-size: 20px;
            line-height: 1.15;
          }
          .brand p {
            margin: 4px 0 0;
            color: #64748b;
            font-size: 12px;
          }
          .doc-meta {
            text-align: right;
            font-size: 12px;
            color: #475569;
            line-height: 1.55;
          }
          .title {
            margin: 22px 0 10px;
            font-size: 26px;
            color: #111827;
            letter-spacing: -0.02em;
          }
          .details {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 10px 18px;
            padding: 14px 16px;
            border: 1px solid #e2e8f0;
            border-radius: 16px;
            background: #f8fafc;
            margin-bottom: 20px;
          }
          .details div {
            font-size: 12px;
            line-height: 1.5;
          }
          .details strong {
            display: inline-block;
            min-width: 92px;
            color: #0f172a;
          }
          .details .wide {
            grid-column: 1 / -1;
          }
          .details .wide strong {
            min-width: 112px;
          }
          table {
            width: 100%;
            border-collapse: collapse;
            margin: 0 0 18px;
          }
          th, td {
            border: 1px solid #cbd5e1;
            padding: 10px 12px;
            text-align: left;
            vertical-align: top;
            font-size: 12px;
          }
          th {
            background: #0f172a;
            color: #fff;
            font-weight: 700;
            letter-spacing: 0.03em;
            text-transform: uppercase;
          }
          tbody tr:nth-child(even) td {
            background: #f8fafc;
          }
          .totals {
            margin-left: auto;
            width: min(100%, 330px);
            border: 1px solid #e2e8f0;
            border-radius: 16px;
            overflow: hidden;
          }
          .totals div {
            display: flex;
            justify-content: space-between;
            padding: 10px 14px;
            border-bottom: 1px solid #e2e8f0;
            font-size: 12px;
            background: #fff;
          }
          .totals div:last-child { border-bottom: 0; }
          .totals .grand {
            background: linear-gradient(135deg, #ff8a3d, #ff6f2c);
            color: #fff;
            font-weight: 700;
            font-size: 13px;
          }
          .notes {
            margin-top: 18px;
            padding: 14px 16px;
            border-radius: 16px;
            background: #fff7ef;
            border: 1px solid #fed7aa;
            color: #7c2d12;
            font-size: 12px;
            line-height: 1.55;
          }
          .footer {
            margin-top: 26px;
            padding-top: 12px;
            border-top: 1px solid #e2e8f0;
            display: flex;
            justify-content: space-between;
            gap: 12px;
            font-size: 11px;
            color: #64748b;
          }
        </style>
      </head>
      <body>
        <div class="sheet">
          <div class="header">
            <div class="brand">
              <img src="${logoUrl}" alt="Aiyige logo" />
              <div>
                <h1>Aiyige Hardware & Construction</h1>
                <p>Quotation and materials supply</p>
              </div>
            </div>
            <div class="doc-meta">
              <div><strong>Quotation No.</strong> ${escapeHtml(meta.quotationNumber || "")}</div>
              <div><strong>Date Issued</strong> ${escapeHtml(meta.dateIssued || "--")}</div>
              <div><strong>Valid Until</strong> ${escapeHtml(validUntilText)}</div>
            </div>
          </div>

          <h2 class="title">${receipt ? "Quotation Receipt" : "Official Quotation"}</h2>
          ${!receipt ? `<div class="notes"><strong>Generated automatically:</strong> This quotation is an immutable order snapshot.</div>` : ""}
          <div class="details">
            <div><strong>Customer</strong> ${escapeHtml(meta.customerName || "--")}</div>
            <div><strong>Prepared By</strong> ${escapeHtml(meta.preparedBy || "--")}</div>
            <div class="wide"><strong>Street Address</strong> ${escapeHtml(meta.streetAddress || "--")}</div>
            <div><strong>City / Province</strong> ${escapeHtml(meta.cityProvince || "--")}</div>
            <div><strong>Postal Code</strong> ${escapeHtml(meta.postalCode || "--")}</div>
            <div><strong>Contact</strong> ${escapeHtml(meta.contactNumber || "--")}</div>
            <div><strong>Email</strong> ${escapeHtml(meta.emailAddress || "--")}</div>
            <div><strong>Quote Ref</strong> ${escapeHtml(meta.quotationNumber || "--")}</div>
          </div>
        <table>
          <thead>
            <tr>
              <th>Item</th>
              <th>Qty</th>
              <th>UOM</th>
              <th>Unit Price</th>
              <th>Total</th>
            </tr>
          </thead>
          <tbody>${rows}</tbody>
        </table>
          <div class="totals">
            <div><span>Subtotal</span><strong>${formatPeso(totals.subtotal)}</strong></div>
            <div><span>Discount amount</span><strong>${formatPeso(totals.discount)}</strong></div>
            <div hidden><span>VAT (12%)</span><strong>${formatPeso(0)}</strong></div>
            <div class="grand"><span>Grand Total</span><strong>${formatPeso(totals.grandTotal)}</strong></div>
          </div>
          <div class="footer">
            <div>Prepared by ${escapeHtml(meta.preparedBy || "Admin User")}</div>
            <div>Thank you for your business.</div>
          </div>
        </div>
        <script>
          window.onload = () => {
            window.print();
          };
        </script>
      </body>
    </html>
  `);
  printWindow.document.close();

  const status = document.getElementById("exportStatus");
  if (status) status.textContent = "Quotation opened for PDF export.";
}


