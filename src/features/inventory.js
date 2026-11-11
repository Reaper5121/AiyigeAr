import {
  demoState,
  categoryCatalog,
  firstGroup,
  getCatalogEntries,
  findCatalogEntry,
  resolveCatalogCategoryGroup,
  getProductDisplayCode,
  getProductDisplayName
} from "../core/state.js";
import {
  inventorySearch,
  inventoryCategoryFilter,
  inventoryTypeFilter,
  inventoryAvailabilityFilter,
  panelCategoryGroup,
  panelCategoryName,
  panelProductCode,
  panelProductName,
  panelLength,
  panelWidth,
  panelThickness,
  panelUnitsPerBox,
  panelPriceUnit,
  panelImagePreview,
  panelImageTitle,
  panelImageCaption,
  panelImageFile,
  panelImageReplaceBtn,
  panelModelPreview,
  panelModelFile,
  panelModelReplaceBtn,
  panelModelCaption,
  panelModelViewer,
  panelQuantity,
  panelAvailabilityDisplay,
  inventoryTable,
  quotePanel
} from "../core/dom.js";
import {
  formatPeso,
  normalizeAvailability,
  escapeHtml,
  availabilityClass,
  formatMeasurement,
  formatPanelDimensions,
  formatInventoryQuantity
} from "../core/utils.js";
import { appState } from "../core/runtime.js";
import { canManageInventory } from "../core/permissions.js";
import { updateAvailabilityDisplay } from "./ui.js";
import { renderDashboardMetrics } from "./ui.js";
import { updateQuotationItemPreview } from "./quotes.js";

export function categoryLabel(item) {
  if (!item.categoryGroup) return "Uncategorized";
  return item.categoryGroup;
}

export function categoryGroupLabel(item) {
  return item.categoryGroup || "Uncategorized";
}

export function productTypeLabel(item) {
  return item.categoryName || item.productType || "General";
}

export function getCategoryOptions(group) {
  return categoryCatalog[group] || [];
}

export function getProductTypeOptions(group) {
  return getCatalogEntries(group);
}

function compareInventoryItems(left, right) {
  const groupOrder = Object.keys(categoryCatalog);
  const leftGroupIndex = groupOrder.indexOf(resolveCatalogCategoryGroup(left.categoryGroup));
  const rightGroupIndex = groupOrder.indexOf(resolveCatalogCategoryGroup(right.categoryGroup));
  if (leftGroupIndex !== rightGroupIndex) {
    return (leftGroupIndex === -1 ? Number.MAX_SAFE_INTEGER : leftGroupIndex) - (rightGroupIndex === -1 ? Number.MAX_SAFE_INTEGER : rightGroupIndex);
  }

  const leftCode = getProductDisplayCode(left);
  const rightCode = getProductDisplayCode(right);
  return leftCode.localeCompare(rightCode, undefined, { numeric: true, sensitivity: "base" });
}

export function renderCategoryGroupOptions(selectedGroup = "") {
  const groups = Object.keys(categoryCatalog);
  panelCategoryGroup.innerHTML = [
    '<option value="" disabled>Select a category group</option>',
    ...groups.map(group => `
      <option value="${escapeHtml(group)}" ${group === selectedGroup ? "selected" : ""}>${escapeHtml(group)}</option>
    `)
  ].join("");
  panelCategoryGroup.value = selectedGroup || "";
  renderProductTypeOptions(selectedGroup);
}

export function renderProductTypeOptions(selectedGroup = panelCategoryGroup?.value || "", selectedType = "") {
  const entries = getProductTypeOptions(selectedGroup);
  const uniqueTypes = [...new Map(entries.map(entry => [entry.type, entry])).values()];
  panelCategoryName.innerHTML = [
    '<option value="" disabled>Select a panel type</option>',
    ...uniqueTypes.map(entry => `
      <option value="${escapeHtml(entry.type)}" data-code="${escapeHtml(entry.code)}" ${entry.type === selectedType ? "selected" : ""}>${escapeHtml(entry.type)}</option>
    `)
  ].join("");

  const selectedEntry = selectedType
    ? entries.find(entry => entry.type === selectedType || entry.code === selectedType)
    : null;
  if (selectedEntry) {
    panelCategoryName.value = selectedEntry.type;
  } else {
    panelCategoryName.value = "";
  }
}

export function renderInventoryFilters() {
  const groups = Object.keys(categoryCatalog);
  inventoryCategoryFilter.innerHTML = [
    '<option value="all">All Categories</option>',
    ...groups.map(group => `<option value="${escapeHtml(group)}">${escapeHtml(group)}</option>`)
  ].join("");
  const types = [...new Set(demoState.inventory
    .map(item => item.categoryName || item.productType || "")
    .map(type => String(type).trim())
    .filter(Boolean))]
    .sort((left, right) => left.localeCompare(right, undefined, { sensitivity: "base" }));
  const selectedType = inventoryTypeFilter.value;
  inventoryTypeFilter.innerHTML = [
    '<option value="all">All Types</option>',
    ...types.map(type => `<option value="${escapeHtml(type)}">${escapeHtml(type)}</option>`)
  ].join("");
  inventoryTypeFilter.value = types.includes(selectedType) ? selectedType : "all";
}

export function resolveAssetUrl(value = "") {
  const trimmed = String(value || "").trim();
  if (!trimmed) return "";
  if (trimmed.startsWith("data:")) return trimmed;
  if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("blob:")) return trimmed;
  if (trimmed.startsWith("/")) return trimmed;
  return `./${trimmed.replace(/^\.?\//, "")}`;
}

export function getPanelImageSource(item) {
  return resolveAssetUrl(item?.imageAsset || item?.imageData || item?.imageUrl || "");
}

export function updateImagePreview(url = "") {
  const trimmed = resolveAssetUrl(url);
  if (!trimmed) {
    panelImagePreview.style.backgroundImage = "none";
    panelImagePreview.classList.remove("has-image");
    if (panelImageTitle) panelImageTitle.textContent = "Product Image";
    if (panelImageCaption) panelImageCaption.textContent = "Preview holder";
    return;
  }

  panelImagePreview.style.backgroundImage = `url("${trimmed.replaceAll('"', '\\"')}")`;
  panelImagePreview.classList.add("has-image");
  if (panelImageTitle) panelImageTitle.textContent = "Product Image";
  if (panelImageCaption) panelImageCaption.textContent = "Loaded preview";
}

export function setPanelImage(dataUrl = "") {
  appState.currentPanelImageData = dataUrl || "";
  updateImagePreview(appState.currentPanelImageData);
  if (panelImageReplaceBtn) {
    panelImageReplaceBtn.textContent = appState.currentPanelImageData ? "Replace Image" : "Add Image";
  }
}

export function setPanelModel(data = "", fileName = "") {
  appState.currentPanelModelData = data || "";
  const hasModel = Boolean(data);
  if (panelModelPreview) panelModelPreview.classList.toggle("has-model", hasModel);
  if (panelModelViewer) {
    panelModelViewer.hidden = !hasModel;
    if (hasModel) panelModelViewer.setAttribute("src", resolveAssetUrl(data));
    else panelModelViewer.removeAttribute("src");
  }
  if (panelModelCaption) {
    panelModelCaption.textContent = hasModel
      ? (fileName || (String(data).startsWith("/assets/") ? "3D model connected" : "3D model ready to upload"))
      : "Optional .glb/.gltf for AR and 3D preview";
  }
  if (panelModelReplaceBtn) panelModelReplaceBtn.textContent = hasModel ? "Replace 3D Model" : "Add 3D Model";
}

export function escapeCssUrl(value) {
  return String(value).replaceAll("\\", "\\\\").replaceAll("'", "\\'");
}

export function resetPanelEditor(title = "Add Product") {
  document.getElementById("panelForm").reset();
  document.getElementById("panelId").value = "";
  renderCategoryGroupOptions("");
  document.getElementById("panelCategoryGroup").value = "";
  renderProductTypeOptions("", "");
  if (panelProductCode) panelProductCode.value = "";
  if (panelProductName) panelProductName.value = "";
  if (panelQuantity) panelQuantity.value = "";
  if (panelUnitsPerBox) panelUnitsPerBox.value = "";
  if (panelPriceUnit) panelPriceUnit.value = "piece";
  if (panelLength) panelLength.value = "";
  if (panelWidth) panelWidth.value = "";
  if (panelThickness) panelThickness.value = "";
  if (panelAvailabilityDisplay) {
    panelAvailabilityDisplay.textContent = "";
    panelAvailabilityDisplay.dataset.state = "";
    panelAvailabilityDisplay.classList.remove("in-stock", "low-stock", "out-stock");
  }
  if (panelImageFile) panelImageFile.value = "";
  if (panelModelFile) panelModelFile.value = "";
  setPanelImage("");
  setPanelModel("");
  document.getElementById("panelEditorTitle").textContent = title;
}

export function getFilteredInventory() {
  const search = inventorySearch.value.trim().toLowerCase();
  const category = inventoryCategoryFilter.value;
  const type = inventoryTypeFilter.value;
  const availability = inventoryAvailabilityFilter.value;

  return demoState.inventory.filter(item => {
    const haystack = [
      getProductDisplayCode(item),
      getProductDisplayName(item),
      item.categoryGroup,
      item.categoryName,
      item.size,
      item.length,
      item.width,
      item.thickness,
      item.unitsPerBox,
      item.material,
      item.description,
      item.quantity,
      normalizeAvailability(item.quantity)
    ]
      .join(" ")
      .toLowerCase();

    const matchesSearch = !search || haystack.includes(search);
    const matchesCategory = category === "all" || item.categoryGroup === category;
    const itemType = item.categoryName || item.productType || "";
    const matchesType = type === "all" || itemType === type;
    const matchesAvailability = availability === "all" || normalizeAvailability(item.quantity) === availability;
    return matchesSearch && matchesCategory && matchesType && matchesAvailability;
  });
}

export function renderInventory(list = getFilteredInventory()) {
  const sortedList = [...list].sort(compareInventoryItems);
  const canEditInventory = canManageInventory();
  const actionHeader = document.getElementById("inventoryActionHeader");
  if (actionHeader) actionHeader.hidden = !canEditInventory;
  // Rebuilding the inventory table also rebuilds the quotation product select.
  // Keep the active selection whenever the table is explicitly rerendered.
  const selectedQuotePanelId = quotePanel?.value || "";

  inventoryTable.innerHTML = sortedList.map(item => `
    <tr>
      <td>
        <div class="panel-row-media">
          <div class="panel-thumb ${getPanelImageSource(item) ? "has-image" : "empty"}"${getPanelImageSource(item) ? ` style="background-image:url('${escapeCssUrl(getPanelImageSource(item))}')"` : ""}>
            ${getPanelImageSource(item) ? "" : "<span>Image</span>"}
          </div>
          <div class="panel-product-stack">
            <strong class="panel-product-code">${escapeHtml(getProductDisplayCode(item) || item.name)}</strong>
            <span class="panel-product-name">${escapeHtml(getProductDisplayName(item) || item.name)}</span>
            <small>${escapeHtml(item.description)}</small>
          </div>
        </div>
      </td>
      <td>
        <div class="category-badges">
          <span class="badge group">${escapeHtml(categoryGroupLabel(item))}</span>
        </div>
      </td>
      <td>${escapeHtml(productTypeLabel(item))}</td>
      <td>${escapeHtml(formatPanelDimensions(item))}</td>
      <td>${escapeHtml(item.material)}</td>
      <td>${escapeHtml(formatInventoryQuantity(item))}</td>
      <td>${formatPeso(item.pricePerMeter ?? item.price ?? 0)} /${item.priceUnit === "meter" ? "m" : "pc"}</td>
      <td><span class="pill ${availabilityClass(normalizeAvailability(item.quantity))}">${escapeHtml(normalizeAvailability(item.quantity))}</span></td>
      ${canEditInventory ? `<td class="inventory-action-cell">
        <div class="row-actions">
          <button type="button" class="action-btn action-btn-edit" data-edit="${item.id}">
            <span class="action-btn-icon" aria-hidden="true">✎</span>
            <span>Edit</span>
          </button>
          <button type="button" class="action-btn action-btn-delete" data-delete="${item.id}">
            <span class="action-btn-icon" aria-hidden="true"><svg class="row-action-icon" viewBox="0 0 24 24"><path d="M4 7h16M10 11v6m4-6v6M5 7l1 14h12l1-14M9 7V4h6v3"/></svg></span>
            <span>Delete</span>
          </button>
        </div>
      </td>` : ""}
    </tr>
  `).join("");

  quotePanel.innerHTML = [
    '<option value="">Select a product</option>',
    ...[...demoState.inventory].sort(compareInventoryItems).map(item => `
    <option value="${item.id}">${escapeHtml(getProductDisplayCode(item) || item.name)} - ${escapeHtml(getProductDisplayName(item) || item.name)} (${escapeHtml(categoryLabel(item))})</option>
    `)
  ].join("");
  if (quotePanel) {
    quotePanel.value = selectedQuotePanelId;
    const selectedItem = demoState.inventory.find(item => String(item.id) === String(quotePanel.value));
    updateQuotationItemPreview(selectedItem || null);
  }
  renderDashboardMetrics();
}
