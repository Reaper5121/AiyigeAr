const apiBaseUrl = location.protocol === "file:" ? "http://localhost:3000" : "";
const apiStateUrl = `${apiBaseUrl}/api/state`;
const localStateKey = "aiyige-admin-state";

export const defaultSettings = {
  requireStaffLogin: true,
  themeMode: "light",
  language: "English",
  currency: "PHP",
  dateFormat: "MM/DD/YYYY",
  unitOfMeasurement: "Meters",
  sessionTimeoutMinutes: 30,
  appVersion: "1.0.0",
  inventoryLastSyncedAt: null,
  quotationSequence: 1,
  paymentModes: ["Cash", "GCash", "Bank Transfer"]
};

export const brochureCatalog = {
  "UPVC Ceiling Panels": [
    { code: "F-00", type: "Seamless" },
    { code: "F-00B", type: "Seamless" },
    { code: "F-01A", type: "Seamless" },
    { code: "F-01B", type: "Seamless" },
    { code: "F-04", type: "Seamless" },
    { code: "F-05", type: "Seamless" },
    { code: "F-05B", type: "Seamless" },
    { code: "F-06", type: "Seamless" },
    { code: "F-07", type: "Seamless" },
    { code: "F-01", type: "Seamless" },
    { code: "F-02", type: "Seamless" },
    { code: "PG-00", type: "Groove" },
    { code: "PG-00B", type: "Groove" },
    { code: "PG-01", type: "Groove" },
    { code: "PG-05", type: "Groove" },
    { code: "PG-06", type: "Groove" },
    { code: "PG-07", type: "Groove" },
    { code: "PG-08", type: "Groove" },
    { code: "PG-203", type: "Groove" },
    { code: "PG-09", type: "Groove" },
    { code: "PG-10", type: "Groove" }
  ],
  "WPC Wall Cladding (Indoor)": [
    { code: "WCI-00", type: "3 in 1" },
    { code: "WCI-01", type: "4 in 1 (Fluted)" },
    { code: "WCI-02", type: "5 in 1" },
    { code: "150-00B", type: "5 in 1" },
    { code: "150-10", type: "5 in 1" },
    { code: "150-05B", type: "5 in 1" },
    { code: "160-23", type: "4 in 1 (Fluted)" },
    { code: "160-KB 01", type: "4 in 1 (Fluted)" },
    { code: "160-KB 02", type: "4 in 1 (Fluted)" },
    { code: "195-04", type: "3 in 1" },
    { code: "195-05", type: "3 in 1" },
    { code: "195-07", type: "3 in 1" }
  ],
  "Other Products": [
    { code: "OP-00", type: "SPC Flooring" },
    { code: "OP-01", type: "UV Board" },
    { code: "OP-02", type: "Wall Board" },
    { code: "01", type: "Wall Board" },
    { code: "84-25", type: "Wall Board" },
    { code: "423", type: "Wall Board" }
  ]
};

export const categoryCatalog = Object.fromEntries(
  Object.entries(brochureCatalog).map(([category, entries]) => [category, entries.map(entry => entry.type)])
);

export const productCatalog = brochureCatalog;

export const catalogCategoryAliases = {
  "UPVC CEILING PANEL": "UPVC Ceiling Panels",
  "UPVC CEILING PANELS": "UPVC Ceiling Panels",
  "WPC WALL CLADDING": "WPC Wall Cladding (Indoor)",
  "WPC WALL CLADDING (INDOOR)": "WPC Wall Cladding (Indoor)",
  "OTHER PRODUCTS": "Other Products",
  "SOLID": "Other Products"
};

export function resolveCatalogCategoryGroup(value = "") {
  const raw = String(value || "").trim();
  if (!raw) return "";
  if (productCatalog[raw]) return raw;
  const directMatch = Object.keys(productCatalog).find(group => group.toLowerCase() === raw.toLowerCase());
  if (directMatch) return directMatch;
  const aliasMatch = catalogCategoryAliases[raw.toUpperCase()];
  if (aliasMatch) return aliasMatch;
  return raw;
}

export function getCatalogEntries(categoryGroup = "") {
  const resolvedGroup = resolveCatalogCategoryGroup(categoryGroup);
  return productCatalog[resolvedGroup] || [];
}

export function findCatalogEntry(categoryGroup = "", value = "") {
  const entries = getCatalogEntries(categoryGroup);
  const needle = String(value || "").trim().toLowerCase();
  if (!needle) return null;
  return entries.find(entry => (
    String(entry.code || "").trim().toLowerCase() === needle ||
    String(entry.type || "").trim().toLowerCase() === needle
  )) || null;
}

export function getCatalogEntryByCode(code = "") {
  const needle = String(code || "").trim().toLowerCase();
  if (!needle) return null;
  return Object.entries(productCatalog).flatMap(([, entries]) => entries).find(entry => String(entry.code || "").trim().toLowerCase() === needle) || null;
}

export function findCatalogMatchForItem(item = {}) {
  const categoryGroup = resolveCatalogCategoryGroup(item.categoryGroup);
  const codeNeedle = String(item.productCode || item.code || item.name || "").trim().toLowerCase();
  if (codeNeedle) {
    for (const [group, entries] of Object.entries(productCatalog)) {
      const matchedEntry = entries.find(entry => String(entry.code || "").trim().toLowerCase() === codeNeedle);
      if (matchedEntry) {
        return { group, entry: matchedEntry };
      }
    }
  }

  const typeNeedle = String(item.categoryName || item.productType || item.productName || item.name || "").trim().toLowerCase();
  if (typeNeedle) {
    if (categoryGroup && productCatalog[categoryGroup]) {
      const matchedEntry = productCatalog[categoryGroup].find(entry => String(entry.type || "").trim().toLowerCase() === typeNeedle);
      if (matchedEntry) {
        return { group: categoryGroup, entry: matchedEntry };
      }
    }

    for (const [group, entries] of Object.entries(productCatalog)) {
      const matchedEntry = entries.find(entry => String(entry.type || "").trim().toLowerCase() === typeNeedle);
      if (matchedEntry) {
        return { group, entry: matchedEntry };
      }
    }
  }

  return { group: categoryGroup, entry: null };
}

export function findCatalogEntryForItem(item = {}) {
  return findCatalogMatchForItem(item).entry;
}

export function getProductDisplayCode(item = {}) {
  return String(item.productCode || item.code || item.name || "").trim();
}

export function getProductDisplayName(item = {}) {
  return String(item.productName || item.productType || item.categoryName || item.name || "").trim();
}

export const firstGroup = Object.keys(categoryCatalog)[0];

export function createEmptyState() {
  return {
    inventory: [],
    orders: [],
    sales: [],
    payments: [],
    quotations: [],
    notifications: [],
    paymentRequests: [],
    customers: [],
    users: [],
    quotation: [],
    quotationMeta: {},
    quotationDrafts: {},
    roomCaptures: [],
    savedQuotations: [],
    settings: { ...defaultSettings }
  };
}

export const demoState = createEmptyState();

export function cloneState(state) {
  return JSON.parse(JSON.stringify(state));
}

export function resetState() {
  Object.assign(demoState, createEmptyState());
}

export function applyLoadedState(saved) {
  if (!saved || typeof saved !== "object") return;
  const savedSettings = { ...(saved.settings || {}) };
  delete savedSettings.adminPassword;
  Object.assign(demoState, createEmptyState(), saved);
  demoState.settings = {
    ...defaultSettings,
    ...savedSettings
  };
  demoState.users = Array.isArray(saved.users)
    ? saved.users
      .filter(user => ["admin", "sales_staff", "sales_manager", "inventory_staff", "owner"].includes(String(user?.role || "").toLowerCase()))
      .map(user => ({ ...user }))
    : [];
}

export async function loadStateFromBackend() {
  try {
    const response = await fetch(apiStateUrl, {
      headers: {
        Accept: "application/json"
      }
    });
    if (!response.ok) return null;
    return await response.json();
  } catch {
    return null;
  }
}

export async function saveStateToBackend(state) {
  try {
    const response = await fetch(apiStateUrl, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(state)
    });
    return response.ok;
  } catch {
    return false;
  }
}

function canUseLocalStorage() {
  try {
    return typeof window !== "undefined" && Boolean(window.localStorage);
  } catch {
    return false;
  }
}

function loadStateFromLocalStorage() {
  if (!canUseLocalStorage()) return null;
  try {
    const raw = window.localStorage.getItem(localStateKey);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function saveStateToLocalStorage(state) {
  if (!canUseLocalStorage()) return false;
  try {
    window.localStorage.setItem(localStateKey, JSON.stringify(state));
    return true;
  } catch {
    return false;
  }
}

async function withPersistenceLoading(label, operation) {
  if (typeof window !== "undefined") {
    window.dispatchEvent(new CustomEvent("aiyige:persistence-loading", { detail: { phase: "start", label } }));
  }
  try {
    return await operation();
  } finally {
    if (typeof window !== "undefined") {
      window.dispatchEvent(new CustomEvent("aiyige:persistence-loading", { detail: { phase: "end", label } }));
    }
  }
}

export async function saveState() {
  return withPersistenceLoading("Saving changes...", async () => {
    const snapshot = cloneState(demoState);
    // The active quotation editor is session-scoped. Persist saved quotation
    // history, but never carry an unfinished editor into another session.
    snapshot.quotation = [];
    snapshot.quotationMeta = {};
    const backendSaved = await saveStateToBackend(snapshot);
    if (backendSaved) {
      saveStateToLocalStorage(snapshot);
      return true;
    }
    return saveStateToLocalStorage(snapshot);
  });
}

export async function loadState() {
  return withPersistenceLoading("Loading workspace...", async () => {
    const backendState = await loadStateFromBackend();
    if (backendState) {
      applyLoadedState(backendState);
      saveStateToLocalStorage(cloneState(demoState));
      return;
    }

    const localState = loadStateFromLocalStorage();
    if (localState) {
      applyLoadedState(localState);
    }
  });
}

export async function refreshStateFromBackend() {
  const backendState = await loadStateFromBackend();
  if (!backendState) return false;

  // Keep the active quotation editor and per-customer drafts in this browser
  // session while refreshing shared records from the backend.
  const activeQuotation = cloneState(demoState.quotation || []);
  const activeQuotationMeta = cloneState(demoState.quotationMeta || {});
  const quotationDrafts = cloneState(demoState.quotationDrafts || {});
  applyLoadedState(backendState);
  demoState.quotation = activeQuotation;
  demoState.quotationMeta = activeQuotationMeta;
  demoState.quotationDrafts = quotationDrafts;
  saveStateToLocalStorage(cloneState(demoState));
  return true;
}
