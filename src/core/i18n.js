import { demoState } from "./state.js";
import { appState } from "./runtime.js";
import { getCurrentRole, getCurrentUserDisplayName, getRoleLabel } from "./permissions.js";

const translations = {
  English: {
    htmlLang: "en",
    appTitle: "Aiyige Admin Dashboard",
    login: {
      eyebrow: "Access Portal",
      title: "Aiyige Access",
      username: "Username",
      password: "Password",
      signIn: "Sign In",
    },
    sidebar: {
      brand: "Aiyige",
      subbrand: "Hardware & Construction",
      dashboard: "Dashboard",
      inventory: "Inventory",
      quotations: "Quotations",
      sales: "Sales",
      payments: "Payments",
      customers: "Accounts",
      staffs: "Employee Information",
      settings: "Settings",
      statusTitle: "System Status",
      statusNote: "Inventory synced 2 minutes ago."
    },
    topbar: {
      eyebrow: "Admin Dashboard",
      staffEyebrow: "Staff Dashboard",
      salesStaffEyebrow: "Sales Staff Dashboard",
      inventoryStaffEyebrow: "Inventory Staff Dashboard",
      ownerEyebrow: "Owner Dashboard",
      title: "Overview",
      profileRole: "Authorized Staff",
      logout: "Logout"
    },
    dashboard: {
      totalSales: "Total Sales",
      paidOrders: "Paid Orders",
      pendingQuotes: "Pending Quotes",
      lowStock: "Low Stock Items",
      revenueEyebrow: "Revenue",
      revenueTitle: "Monthly Sales Trend",
      paymentsEyebrow: "Payments",
      paymentsTitle: "Payment Breakdown",
      recentEyebrow: "Recent Activity",
      recentTitle: "Orders and Payment Updates",
      chartMonth: "Month",
      chartYear: "Year",
      collected: "Collected",
      noSales: "No sales yet",
      noCompleted: "No completed orders",
      waiting: "Waiting for activity",
      noLowStock: "No low stock items"
    },
    inventory: {
      eyebrow: "Inventory",
      title: "Product Records",
      addPanel: "Add Product",
      searchLabel: "Search Product",
      searchPlaceholder: "Search by code, name, or category",
      categoryLabel: "Category Filter",
      typeLabel: "Type Filter",
      availabilityLabel: "Availability",
      allCategories: "All Categories",
      allAvailability: "All Availability",
      panel: "Panel",
      category: "Category",
      size: "Size",
      material: "Material",
      quantity: "Boxes",
      price: "Price / Meter",
      availability: "Availability",
      action: "Action"
    },
    quotations: {
      queueEyebrow: "Mobile quotations",
      queueTitle: "Customers with quotations",
      back: "Back",
      customerName: "Customer Name (First, Last, MI)",
      streetAddress: "Street Address",
      cityProvince: "City / Province",
      contactNumber: "Contact Number",
      emailAddress: "Email Address",
      quotationNumber: "Quotation Number",
      dateIssued: "Date Issued",
      validUntil: "Valid Until",
      preparedBy: "Prepared by",
      panelItem: "Product",
      quantity: "Qty",
      unitOfMeasurement: "Unit of Measurement",
      unitPrice: "Unit Price",
      discount: "Discount %",
      add: "Add Item",
      customerWants: "Customer Wants / Request",
      customerWantsPlaceholder: "Describe what the customer wants here.",
      notes: "Notes / Remarks",
      notesPlaceholder: "Add any notes for the customer here.",
      summaryEyebrow: "Summary",
      summaryTitle: "Quotation Preview",
      generatePdf: "Generate PDF",
      print: "Print Quotation",
      saveQuotation: "Save Quote",
      moreActions: "More actions",
      clear: "Reset Draft",
      noQuote: "Select an order to view its generated quotation.",
      subtotal: "Subtotal",
      discountLabel: "Discount amount",
      vat: "VAT (12%)",
      total: "Grand Total"
    },
    sales: {
      eyebrow: "Sales",
      title: "Sales Performance",
      hot: "Hot",
      leadConversion: "Lead Conversion",
      close: "Close",
      periodView: "Period View",
      periodNote: "Switch the month and year to refresh the sales cards and recent sales list.",
      today: "Today",
      thisWeek: "This Week",
      thisMonth: "This Month",
      noActivityWeek: "No activity this week",
      noSalesMonth: "No sales recorded",
      noTransactions: "0 transactions",
      selectDay: "Select day",
      salesDrilldown: "Sales Drilldown",
      pickDay: "Pick a day from the selected month and year to review detailed transactions.",
      selectedDay: "Selected Day",
      totalTransactions: "Total Transactions",
      totalSales: "Total Sales",
      noDaySales: "No transactions recorded for this day.",
      noPeriodSales: "No sales recorded for this period.",
      date: "Date",
      invoice: "Invoice No.",
      customer: "Account",
      service: "Service",
      method: "Method",
      amount: "Amount",
      status: "Status",
      details: "Details"
    },
    panels: {
      editorEyebrow: "Product Editor",
      editorTitle: "Add / Edit Product",
      editorCopy: "Use the brochure category and panel type so the official code and product name stay aligned with the catalog.",
      close: "Close",
      addImage: "Add Image",
      replaceImage: "Replace Image",
      savePanel: "Save Product",
      clear: "Clear",
      deleteEyebrow: "Delete Product",
      deleteTitle: "Remove this product?",
      deleteCopy: "This product will be removed from the inventory list.",
      cancel: "Cancel",
      delete: "Delete"
    },
    payments: {
      eyebrow: "Financial Operations",
      title: "Payment History"
    },
    settings: {
      eyebrow: "Configuration Center",
      title: "Admin Settings",
      copy: "Manage appearance, localization, system status, and account preferences from one organized control panel.",
      appearance: "Appearance",
      localization: "Localization",
      team: "Team Access",
      system: "System",
      account: "Account",
      appearanceValue: "Dark Mode",
      localizationValue: "\u20B1 / MM/DD / Meters",
      systemValue: "Online",
      displayTitle: "Display Preferences",
      displayCopy: "Set the dashboard theme and visual defaults used across the system.",
      regionalTitle: "Regional Preferences",
      themeMode: "Theme Mode",
      darkMode: "Dark mode",
      visualSummary: "Visual Summary",
      languageTitle: "Language Preference",
      languageCopy: "Select the language label used throughout the dashboard.",
      languageField: "Language",
      currencyTitle: "Default Currency",
      currencyCopy: "Set the main money format used for summaries and payments.",
      currencyField: "Currency",
      dateTitle: "Date Format",
      dateCopy: "Choose how dates are displayed in the interface.",
      dateField: "Format",
      unitTitle: "Unit of Measurement",
      unitCopy: "Pick the measurement unit used for inventory and panels.",
      unitField: "Unit",
      systemTitle: "Health and Maintenance",
      systemCopy: "Review app version, sync details, and reset interface state when needed.",
      appVersion: "App Version",
      inventorySynced: "Inventory Last Synced",
      resetFilters: "Clear Cache / Reset Filters",
      systemStatus: "System Status",
      staffTitle: "Employee Information",
      staffCopy: "Manage all users in one place. Control access, assign roles, and monitor activity across your platform.",
      createStaff: "Create Employee",
      openStaff: "Add Employee",
      editStaff: "Edit",
      deleteStaff: "Delete",
      updateStaff: "Update Employee",
      staffModalTitleCreate: "Create Employee",
      staffModalTitleEdit: "Update Employee",
      staffModalCopy: "Add an employee account or update access settings.",
      displayNameField: "Display name",
      usernameField: "Username",
      passwordField: "Password",
      addStaff: "Add Employee",
      currentStaff: "Current Employees",
      activePreferences: "Active Preferences",
      systemSnapshot: "System Snapshot",
      summaryAppearance: "Theme updates apply across the whole interface.",
      summaryLocalization: "Currency, date, and unit choices are stored with the system.",
      summaryAccount: "Staff access is managed through employee accounts."
    }
  },
  Filipino: {
    htmlLang: "fil",
    appTitle: "Aiyige Admin Dashboard",
    login: {
      eyebrow: "Portal ng Pagpasok",
      title: "Aiyige Access",
      username: "Pangalan ng User",
      password: "Password",
      signIn: "Mag-sign In",
      note: "Gamitin ang iyong nakatalagang username at password. Awtomatikong ilalagay ng system ang iyong papel."
    },
    sidebar: {
      brand: "Aiyige",
      subbrand: "Hardware at Konstruksyon",
      dashboard: "Dashboard",
      inventory: "Imbentaryo",
      quotations: "Mga Quotation",
      sales: "Benta",
      payments: "Mga Bayad",
      customers: "Mga Account",
      staffs: "Impormasyon ng mga Empleyado",
      settings: "Mga Setting",
      statusTitle: "Kalagayan ng Sistema",
      statusNote: "Na-sync ang imbentaryo 2 minuto ang nakalipas."
    },
    topbar: {
      eyebrow: "Admin Dashboard",
      staffEyebrow: "Dashboard ng Kawani",
      salesStaffEyebrow: "Dashboard ng Sales Staff",
      inventoryStaffEyebrow: "Dashboard ng Inventory Staff",
      ownerEyebrow: "Dashboard ng May-ari",
      title: "Pangkalahatang-ideya",
      profileRole: "Awtorisadong Kawani",
      logout: "Mag-logout"
    },
    dashboard: {
      totalSales: "Kabuuang Benta",
      paidOrders: "Mga Bayad na Order",
      pendingQuotes: "Mga Naka-pending na Quotation",
      lowStock: "Mga Item na Kulang ang Stock",
      revenueEyebrow: "Kita",
      revenueTitle: "Buwanang Trend ng Benta",
      paymentsEyebrow: "Mga Bayad",
      paymentsTitle: "Hati ng Bayad",
      recentEyebrow: "Pinakabagong Aktibidad",
      recentTitle: "Mga Order at Update sa Bayad",
      chartMonth: "Buwan",
      chartYear: "Taon",
      collected: "Nakolekta",
      noSales: "Wala pang benta",
      noCompleted: "Wala pang natapos na order",
      waiting: "Naghihintay ng aktibidad",
      noLowStock: "Walang item na mababa ang stock"
    },
    inventory: {
      eyebrow: "Imbentaryo",
      title: "Mga Rekord ng Produkto",
      addPanel: "Magdagdag ng Produkto",
      searchLabel: "Maghanap ng Produkto",
      searchPlaceholder: "Maghanap ayon sa code, pangalan, o kategorya",
      categoryLabel: "Salain Ayon sa Kategorya",
      typeLabel: "Salain Ayon sa Uri",
      availabilityLabel: "Kasalukuyang Dami",
      allCategories: "Lahat ng Kategorya",
      allAvailability: "Lahat ng Estado",
      panel: "Panel",
      category: "Kategorya",
      size: "Sukat",
      material: "Materyal",
      quantity: "Mga Kahon",
      price: "Presyo bawat Metro",
      availability: "Kasalukuyang Dami",
      action: "Aksyon"
    },
    quotations: {
      queueEyebrow: "Mga quotation sa mobile app",
      queueTitle: "Mga customer na may quotation",
      back: "Bumalik",
      customerName: "Pangalan ng Customer (Unang Pangalan, Apelyido, MI)",
      streetAddress: "Kalye / Address",
      cityProvince: "Lungsod / Probinsya",
      contactNumber: "Numero ng Contact",
      emailAddress: "Email Address",
      quotationNumber: "Numero ng Quotation",
      dateIssued: "Petsa ng Pag-isyu",
      validUntil: "Balid hanggang",
      preparedBy: "Inihanda ni",
      panelItem: "Item ng Produkto",
      quantity: "Dami",
      unitOfMeasurement: "Yunit ng Sukat",
      unitPrice: "Presyo bawat Metro",
      discount: "Discount %",
      add: "Idagdag na Line Item",
      customerWants: "Hiling ng Customer / Request",
      customerWantsPlaceholder: "Ilarawan dito ang gusto ng customer.",
      notes: "Mga Tala / Remarks",
      notesPlaceholder: "Magdagdag ng anumang tala para sa customer dito.",
      summaryEyebrow: "Buod",
      summaryTitle: "Preview ng Quotation",
      generatePdf: "Gumawa ng PDF",
      print: "I-print",
      saveQuotation: "I-save ang Estimate",
      moreActions: "Iba pang aksyon",
      clear: "I-reset ang Draft",
      noQuote: "Pumili ng order para makita ang awtomatikong quotation.",
      subtotal: "Subtotal",
      discountLabel: "Halaga ng diskwento",
      vat: "VAT (12%)",
      total: "Kabuuang Halaga"
    },
    sales: {
      eyebrow: "Benta",
      title: "Performance ng Benta",
      hot: "Mainit",
      leadConversion: "Conversion ng Leads",
      close: "Isara",
      periodView: "Pananaw ng Panahon",
      periodNote: "Palitan ang buwan at taon para i-refresh ang mga sales card at listahan ng benta.",
      today: "Ngayon",
      thisWeek: "Ngayong Linggo",
      thisMonth: "Ngayong Buwan",
      noActivityWeek: "Walang aktibidad ngayong linggo",
      noSalesMonth: "Walang naitalang benta",
      noTransactions: "0 transaksyon",
      selectDay: "Pumili ng araw",
      salesDrilldown: "Mas Detalyadong Benta",
      pickDay: "Pumili ng araw mula sa napiling buwan at taon para tingnan ang mga transaksyon.",
      selectedDay: "Napiling Araw",
      totalTransactions: "Kabuuang Transaksyon",
      totalSales: "Kabuuang Benta",
      noDaySales: "Walang naitalang transaksyon sa araw na ito.",
      noPeriodSales: "Walang naitalang benta para sa panahong ito.",
      date: "Petsa",
      invoice: "Invoice No.",
      customer: "Account",
      service: "Serbisyo",
      method: "Paraan",
      amount: "Halaga",
      status: "Katayuan",
      details: "Detalye"
    },
    panels: {
      editorEyebrow: "Product Editor",
      editorTitle: "Magdagdag / Mag-edit ng Produkto",
      editorCopy: "Gamitin ang brochure category at panel type para manatiling tugma ang official code at product name sa catalog.",
      close: "Isara",
      addImage: "Magdagdag ng Larawan",
      replaceImage: "Palitan ang Larawan",
      savePanel: "I-save ang Produkto",
      clear: "I-clear",
      deleteEyebrow: "Burahin ang Produkto",
      deleteTitle: "Tanggalin ang produktong ito?",
      deleteCopy: "Mawawala ang produktong ito sa listahan ng imbentaryo.",
      cancel: "Kanselahin",
      delete: "Burahin"
    },
    payments: {
      eyebrow: "Operasyong Pinansyal",
      title: "Kasaysayan ng Bayad"
    },
    settings: {
      eyebrow: "Sentro ng Konpigurasyon",
      title: "Mga Setting ng Admin",
      copy: "Pamahalaan ang itsura, lokal na setting, kalagayan ng sistema, at mga preference ng account mula sa isang control panel.",
      appearance: "Itsura",
      localization: "Lokalisasyon",
      team: "Pag-access ng Koponan",
      system: "Sistema",
      account: "Account",
      appearanceValue: "Dark Mode",
      localizationValue: "\u20B1 / MM/DD / Meters",
      systemValue: "Online",
      displayTitle: "Mga Preference sa Display",
      displayCopy: "Itakda ang tema at visual defaults na gagamitin sa buong sistema.",
      regionalTitle: "Mga Kagustuhang Pangrehiyon",
      themeMode: "Mode ng Tema",
      darkMode: "Dark mode",
      visualSummary: "Buod ng Itsura",
      languageTitle: "Wika",
      languageCopy: "Piliin ang wikang ipapakita sa dashboard.",
      languageField: "Wika",
      currencyTitle: "Default na Currency",
      currencyCopy: "Itakda ang pangunahing format ng pera para sa mga buod at bayad.",
      currencyField: "Currency",
      dateTitle: "Format ng Petsa",
      dateCopy: "Piliin kung paano ipapakita ang mga petsa sa interface.",
      dateField: "Format",
      unitTitle: "Yunit ng Sukat",
      unitCopy: "Piliin ang yunit na gagamitin para sa imbentaryo at panel.",
      unitField: "Yunit",
      systemTitle: "Kalusugan at Maintenance",
      systemCopy: "Tingnan ang app version, sync details, at i-reset ang interface kapag kailangan.",
      appVersion: "Bersyon ng App",
      inventorySynced: "Huling Sync ng Imbentaryo",
      resetFilters: "I-clear ang Cache / I-reset ang Filters",
      systemStatus: "Kalagayan ng Sistema",
      staffTitle: "Impormasyon ng mga Empleyado",
      staffCopy: "Pamahalaan ang lahat ng user sa isang lugar. Kontrolin ang access, magtalaga ng roles, at subaybayan ang aktibidad.",
      createStaff: "Gumawa ng Kawani",
      openStaff: "Magdagdag ng Kawani",
      editStaff: "I-edit",
      deleteStaff: "Burahin",
      updateStaff: "I-update ang Kawani",
      staffModalTitleCreate: "Gumawa ng Kawani",
      staffModalTitleEdit: "I-update ang Kawani",
      staffModalCopy: "Magdagdag ng account ng kawani o mag-update ng umiiral na account.",
      displayNameField: "Pangalan na ipapakita",
      usernameField: "Username",
      passwordField: "Password",
      addStaff: "Magdagdag ng Kawani",
      currentStaff: "Kasalukuyang Kawani",
      activePreferences: "Aktibong Preferences",
      systemSnapshot: "Snapshot ng Sistema",
      summaryAppearance: "Aplikado ang tema sa buong interface.",
      summaryLocalization: "Naka-store sa sistema ang currency, petsa, at yunit.",
      summaryAccount: "Pinamamahalaan ang staff access sa mga account ng empleyado."
    }
  }
};

export function getTranslation(language = demoState.settings.language || "English") {
  return translations[language] || translations.English;
}

export function getDeleteMessage(itemName, categoryName) {
  const language = demoState.settings.language || "English";
  if (language === "Filipino") {
    return `Burahin ang ${itemName} mula sa ${categoryName}? Hindi na ito maibabalik.`;
  }
  return `Delete ${itemName} from ${categoryName}? This cannot be undone.`;
}

function setText(node, value) {
  if (node && value != null) {
    node.textContent = value;
  }
}

function setLeadingText(selector, value) {
  const node = document.querySelector(selector);
  if (!node || value == null) return;
  const textNode = [...node.childNodes].find(child => child.nodeType === Node.TEXT_NODE);
  if (textNode) {
    textNode.nodeValue = ` ${value} `;
    return;
  }
  node.insertBefore(document.createTextNode(` ${value} `), node.firstChild);
}

function setPlaceholder(selector, value) {
  const node = document.querySelector(selector);
  if (node && value != null) {
    node.setAttribute("placeholder", value);
  }
}

export function applyLocalizedCopy() {
  const language = demoState.settings.language || "English";
  const t = getTranslation(language);

  document.documentElement.lang = t.htmlLang;
  document.title = t.appTitle;

  setText(document.querySelector("#loginScreen .eyebrow"), t.login.eyebrow);
  setText(document.querySelector("#loginScreen h1"), t.login.title);
  setLeadingText("#loginScreen label:nth-of-type(1)", t.login.username);
  setLeadingText("#loginScreen label:nth-of-type(2)", t.login.password);
  setText(document.querySelector("#loginScreen .primary-btn"), t.login.signIn);

  setText(document.querySelector(".sidebar-brand small"), t.sidebar.subbrand);
  setText(document.querySelector(".sidebar-card .sidebar-title"), t.sidebar.statusTitle);
  setText(document.querySelector(".sidebar-card .sidebar-note"), t.sidebar.statusNote);
  navButtons("dashboard", t.sidebar.dashboard);
  navButtons("inventory", t.sidebar.inventory);
  navButtons("quotations", t.sidebar.quotations);
  navButtons("sales", t.sidebar.sales);
  navButtons("payments", t.sidebar.payments);
  navButtons("staffs", t.sidebar.staffs);
  navButtons("settings", t.sidebar.settings);

  const currentRole = getCurrentRole();
  const topbarLabel = currentRole === "sales_staff"
      ? t.topbar.salesStaffEyebrow
      : currentRole === "sales_manager"
        ? "Sales Manager"
      : currentRole === "inventory_staff"
        ? t.topbar.inventoryStaffEyebrow
        : currentRole === "owner"
          ? t.topbar.ownerEyebrow
          : t.topbar.eyebrow;
  setText(document.querySelector(".topbar .eyebrow"), topbarLabel);
  const roleDashboardTitles = {
    admin: "User Management Dashboard",
    sales_staff: "Sales Staff Dashboard",
    sales_manager: "Sales Manager Dashboard",
    inventory_staff: "Inventory Dashboard",
    owner: "Business Dashboard"
  };
  setText(document.getElementById("pageTitle"), appState.activeView === "dashboard" && roleDashboardTitles[currentRole]
    ? roleDashboardTitles[currentRole]
    : t.topbar.title);
  setText(document.getElementById("profileName"), getCurrentUserDisplayName());
  setText(document.getElementById("profileRole"), getRoleLabel());
  setText(document.getElementById("logoutBtn"), t.topbar.logout);

  setText(document.querySelector('[data-kpi-target="sales"] span'), t.dashboard.totalSales);
  setText(document.querySelector('[data-kpi-target="sales"] small'), t.dashboard.noSales);
  setText(document.querySelector('[data-kpi-target="payments"] span'), t.dashboard.paidOrders);
  setText(document.querySelector('[data-kpi-target="payments"] small'), t.dashboard.noCompleted);
  setText(document.querySelector('[data-kpi-target="quotations"] span'), t.dashboard.pendingQuotes);
  setText(document.querySelector('[data-kpi-target="quotations"] small'), t.dashboard.waiting);
  setText(document.querySelector('[data-kpi-target="inventory"] span'), t.dashboard.lowStock);
  setText(document.querySelector('[data-kpi-target="inventory"] small'), t.dashboard.noLowStock);
  setText(document.querySelector(".chart-panel .eyebrow"), t.dashboard.revenueEyebrow);
  setText(document.querySelector(".chart-panel h3"), t.dashboard.revenueTitle);
  setLeadingText(".sales-period-controls label:first-child", t.dashboard.chartMonth);
  setLeadingText(".sales-period-controls label:last-child", t.dashboard.chartYear);
  setText(document.querySelector(".donut-center small"), t.dashboard.collected);
  setText(document.querySelector(".panel:nth-of-type(2) .eyebrow"), t.dashboard.paymentsEyebrow);
  setText(document.querySelector(".panel:nth-of-type(2) h3"), t.dashboard.paymentsTitle);
  setText(document.querySelector(".panel:nth-of-type(3) .eyebrow"), t.dashboard.recentEyebrow);
  setText(document.querySelector(".panel:nth-of-type(3) h3"), t.dashboard.recentTitle);
  setText(document.querySelector('[data-section="staffs"] .section-hero .eyebrow'), t.settings.team);
  setText(document.querySelector('[data-section="staffs"] .section-hero h2'), t.settings.staffTitle);
  setText(document.querySelector('[data-section="staffs"] .section-hero .section-copy'), t.settings.staffCopy);
  setText(document.querySelector('[data-section="staffs"] .staff-management-panel .eyebrow'), t.settings.team);
  setText(document.querySelector('[data-section="staffs"] .staff-management-panel h3'), t.settings.staffTitle);
  setText(document.querySelector('[data-section="staffs"] .staff-management-panel .panel-copy'), t.settings.copy);
  setText(document.querySelector('[data-section="staffs"] .staff-management-panel .setting-card h4'), t.settings.createStaff);
  setText(document.getElementById("openStaffModalBtn"), t.settings.openStaff);
  const employeeFormIsEditing = document.getElementById("staffMode")?.value === "edit";
  setText(document.getElementById("staffModalTitle"), employeeFormIsEditing ? t.settings.staffModalTitleEdit : t.settings.staffModalTitleCreate);
  setText(document.getElementById("staffModalCopy"), t.settings.staffModalCopy);
  setText(document.getElementById("staffModalSubmitBtn"), employeeFormIsEditing ? t.settings.updateStaff : t.settings.addStaff);
  setLeadingText("#staffAccountForm label:nth-of-type(1)", t.settings.displayNameField);
  setLeadingText("#staffAccountForm label:nth-of-type(2)", t.settings.usernameField);
  setLeadingText("#staffAccountForm label:nth-of-type(5)", t.settings.passwordField);
  setText(document.querySelector(".staff-list-head strong"), t.settings.currentStaff);

  setText(document.querySelector('[data-section="inventory"] .eyebrow'), t.inventory.eyebrow);
  setText(document.querySelector('[data-section="inventory"] h3'), t.inventory.title);
  setText(document.getElementById("newItemBtn"), t.inventory.addPanel);
  setLeadingText('[data-section="inventory"] .inventory-tools label:nth-child(1)', t.inventory.searchLabel);
  setPlaceholder("#inventorySearch", t.inventory.searchPlaceholder);
  setLeadingText('[data-section="inventory"] .inventory-tools label:nth-child(2)', t.inventory.categoryLabel);
  setLeadingText('[data-section="inventory"] .inventory-tools label:nth-child(3)', t.inventory.typeLabel);
  setLeadingText('[data-section="inventory"] .inventory-tools label:nth-child(4)', t.inventory.availabilityLabel);

  setText(document.querySelector('[data-section="quotations"] .quote-summary-panel .panel-head .eyebrow'), t.quotations.queueEyebrow);
  setText(document.querySelector('[data-section="quotations"] .quote-summary-panel .panel-head h3'), t.quotations.queueTitle);
  setText(document.getElementById("quoteStreetAddressLabel"), t.quotations.streetAddress);
  setText(document.getElementById("quoteCityProvinceLabel"), t.quotations.cityProvince);
  setText(document.getElementById("quoteContactNumberLabel"), t.quotations.contactNumber);
  setText(document.getElementById("quoteEmailAddressLabel"), t.quotations.emailAddress);
  setText(document.getElementById("quoteNumberLabel"), t.quotations.quotationNumber);
  setText(document.getElementById("quoteDateIssuedLabel"), t.quotations.dateIssued);
  setText(document.getElementById("quoteValidUntilLabel"), t.quotations.validUntil);
  setText(document.getElementById("quotePreparedByLabel"), t.quotations.preparedBy);
  setText(document.getElementById("quotePanelLabel"), t.quotations.panelItem);
  setText(document.getElementById("quoteQtyLabel"), t.quotations.quantity);
  setText(document.getElementById("quoteUomLabel"), t.quotations.unitOfMeasurement);
  setText(document.getElementById("quoteUnitPriceLabel"), t.quotations.unitPrice);
  setText(document.getElementById("quoteDiscountLabel"), t.quotations.discount);
  setText(document.getElementById("addQuoteItemBtn"), t.quotations.add);
  setText(document.querySelector("[data-section='quotations'] .quote-summary-panel .eyebrow"), t.quotations.summaryEyebrow);
  setText(document.querySelector("[data-section='quotations'] .quote-summary-panel h3"), t.quotations.summaryTitle);
  setText(document.getElementById("backQuotationBtn"), t.quotations.back);
  setText(document.getElementById("generateQuotePdfBtn"), t.quotations.generatePdf);
  setText(document.getElementById("printQuoteBtn"), t.quotations.print);
  setText(document.getElementById("saveQuotationBtn"), t.quotations.saveQuotation);
  setText(document.getElementById("moreQuoteActionsBtn"), t.quotations.moreActions);
  setText(document.getElementById("clearQuotationBtn"), t.quotations.clear);
  setText(document.getElementById("quoteMeta"), t.quotations.noQuote);
  setText(document.getElementById("quoteCustomerWantsLabel"), t.quotations.customerWants);
  setPlaceholder("#quoteCustomerWants", t.quotations.customerWantsPlaceholder);
  setText(document.getElementById("quoteRemarksLabel"), t.quotations.notes);
  setPlaceholder("#quoteRemarks", t.quotations.notesPlaceholder);

  setText(document.querySelector('[data-section="sales"] .eyebrow'), t.sales.eyebrow);
  setText(document.querySelector('[data-section="sales"] h3'), t.sales.title);
  setText(document.querySelector(".chip-row .success"), t.sales.hot);
  setText(document.querySelector(".chip-row .warning"), `${t.sales.leadConversion} 0%`);
  setText(document.querySelector(".sales-period-copy .eyebrow"), t.sales.periodView);
  setText(document.querySelector(".sales-period-note"), t.sales.periodNote);
  setText(document.querySelector(".sales-day-modal-toolbar .eyebrow"), t.sales.salesDrilldown);
  setText(document.querySelector(".sales-day-modal-toolbar .editor-copy"), t.sales.pickDay);
  setText(document.querySelector(".sales-day-summary div:nth-child(1) span"), t.sales.selectedDay);
  setText(document.querySelector(".sales-day-summary div:nth-child(2) span"), t.sales.totalTransactions);
  setText(document.querySelector(".sales-day-summary div:nth-child(3) span"), t.sales.totalSales);
  setText(document.getElementById("closeSalesDayModalBtn"), t.sales.close);
  setText(document.querySelector(".sales-today-card span"), t.sales.today);
  setText(document.querySelector(".sales-grid .mini-card:nth-child(2) span"), t.sales.thisWeek);
  setText(document.querySelector(".sales-grid .mini-card:nth-child(3) span"), t.sales.thisMonth);
  setText(document.querySelector("#salesTodayMeta"), t.sales.noTransactions);
  setText(document.querySelector("#salesWeekMeta"), t.sales.noActivityWeek);
  setText(document.querySelector("#salesMonthMeta"), t.sales.noSalesMonth);

  setText(document.querySelector('[data-section="payments"] .eyebrow'), t.payments.eyebrow);
  setText(document.querySelector('[data-section="payments"] h2'), t.payments.title);

  setText(document.querySelector('[data-section="settings"] .section-hero .eyebrow'), t.settings.eyebrow);
  setText(document.querySelector('[data-section="settings"] .section-hero h2'), t.settings.title);
  setText(document.querySelector('[data-section="settings"] .section-hero .section-copy'), t.settings.copy);
  setText(document.querySelector(".settings-stack .settings-panel:nth-of-type(1) .eyebrow"), t.settings.appearance);
  setText(document.querySelector(".settings-stack .settings-panel:nth-of-type(1) h3"), t.settings.displayTitle);
  setText(document.querySelector(".settings-stack .settings-panel:nth-of-type(1) .panel-copy"), t.settings.displayCopy);
  setText(document.querySelector(".settings-stack .settings-panel:nth-of-type(1) .setting-card:nth-of-type(1) h4"), t.settings.themeMode);
  setText(document.querySelector(".settings-stack .settings-panel:nth-of-type(1) .setting-card:nth-of-type(1) p"), t.settings.displayCopy);
  setText(document.querySelector(".settings-stack .settings-panel:nth-of-type(1) .setting-switch-copy strong"), t.settings.darkMode);
  setText(document.querySelector(".settings-stack .settings-panel:nth-of-type(1) .setting-switch-copy small"), t.settings.displayCopy);
  setText(document.querySelector(".settings-stack .settings-panel:nth-of-type(2) .eyebrow"), t.settings.localization);
  setText(document.querySelector(".settings-aside .eyebrow"), t.settings.systemSnapshot);
  setText(document.querySelector(".settings-aside h3"), t.settings.activePreferences);
  setText(document.querySelector(".settings-aside .panel-copy"), t.settings.copy);
  const asideNotes = document.querySelectorAll(".settings-aside .settings-notes div");
  if (asideNotes[0]) setText(asideNotes[0].querySelector("span"), t.settings.appearance);
  if (asideNotes[0]) setText(asideNotes[0].querySelector("strong"), t.settings.summaryAppearance);
  if (asideNotes[1]) setText(asideNotes[1].querySelector("span"), t.settings.localization);
  if (asideNotes[1]) setText(asideNotes[1].querySelector("strong"), t.settings.summaryLocalization);
  if (asideNotes[2]) setText(asideNotes[2].querySelector("span"), t.settings.account);
  if (asideNotes[2]) setText(asideNotes[2].querySelector("strong"), t.settings.summaryAccount);

  setText(document.querySelector("#panelEditorModal .eyebrow"), t.panels.editorEyebrow);
  setText(document.getElementById("panelEditorTitle"), t.panels.editorTitle);
  setText(document.querySelector("#panelEditorModal .editor-copy"), t.panels.editorCopy);
  setText(document.getElementById("closePanelEditorBtn"), t.panels.close);
  setText(document.getElementById("panelImageReplaceBtn"), appState.currentPanelImageData ? t.panels.replaceImage : t.panels.addImage);
  setText(document.querySelector("#panelForm .primary-btn"), t.panels.savePanel);
  setText(document.getElementById("clearPanelBtn"), t.panels.clear);
  setText(document.querySelector("#deleteModal .eyebrow"), t.panels.deleteEyebrow);
  setText(document.querySelector("#deleteModal h3"), t.panels.deleteTitle);
  setText(document.getElementById("deleteModalText"), t.panels.deleteCopy);
  setText(document.getElementById("cancelDeleteBtn"), t.panels.cancel);
  setText(document.getElementById("confirmDeleteBtn"), t.panels.delete);
}

function navButtons(view, label) {
  const button = document.querySelector(`.nav-item[data-view="${view}"]`);
  if (button) button.textContent = label;
}

