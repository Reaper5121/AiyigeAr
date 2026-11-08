export const appState = {
  pendingDeleteId: null,
  pendingDeleteType: null,
  currentPanelImageData: "",
  currentPanelModelData: "",
  activeView: "dashboard",
  currentUser: null,
  selectedQuotationCustomer: null,
  selectedQuotationNumber: null,
  quotationReturnView: null
};

export const salesViewState = {
  monthIndex: null,
  year: new Date().getFullYear()
};

export const salesDayState = {
  monthIndex: null,
  year: null,
  day: null
};
