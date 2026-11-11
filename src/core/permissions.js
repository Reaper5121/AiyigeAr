import { demoState, saveState } from "./state.js";
import { appState } from "./runtime.js";

const roleConfig = {
  admin: {
    label: "Administrator",
    views: ["dashboard", "staffs"],
    homeView: "dashboard"
  },
  sales_staff: {
    label: "Sales Staff",
    views: ["dashboard", "inventory", "quotations", "payments", "courier"],
    homeView: "dashboard"
  },
  sales_manager: {
    label: "Sales Manager",
    views: ["dashboard", "quotations", "sales", "payments", "courier"],
    homeView: "dashboard"
  },
  inventory_staff: {
    label: "Inventory Staff",
    views: ["dashboard", "inventory"],
    homeView: "dashboard"
  },
  owner: {
    label: "Owner",
    views: ["dashboard", "inventory", "sales", "staffs"],
    homeView: "dashboard"
  },
  guest: {
    label: "Guest",
    views: ["dashboard"],
    homeView: "dashboard"
  }
};

function normalize(value = "") {
  return String(value || "").trim().toLowerCase();
}

function cloneUser(user) {
  if (!user) return null;
  const safeUser = { ...user };
  delete safeUser.password;
  return safeUser;
}

export function getCurrentUser() {
  return appState.currentUser ? cloneUser(appState.currentUser) : null;
}

export function getCurrentRole() {
  return normalize(appState.currentUser?.role) || "guest";
}

export function getRoleLabel(role = getCurrentRole()) {
  return roleConfig[normalize(role)]?.label || "Guest";
}

export function getAccessibleViews(role = getCurrentRole()) {
  const defaultViews = roleConfig[normalize(role)]?.views || roleConfig.guest.views;
  return [...defaultViews];
}

export function getDefaultViewForRole(role = getCurrentRole()) {
  return roleConfig[normalize(role)]?.homeView || roleConfig.guest.homeView;
}

export function canAccessView(viewName) {
  return getAccessibleViews().includes(String(viewName || "").trim());
}

export function canManageInventory() {
  return getCurrentRole() === "inventory_staff";
}

export function canManagePayments() {
  return ["sales_staff", "sales_manager"].includes(getCurrentRole());
}

export function canManageQuotations() {
  return ["sales_staff", "sales_manager"].includes(getCurrentRole());
}

export function canManageSettings() {
  return getCurrentRole() === "admin";
}

export function canManageRoomCaptures() {
  return ["sales_staff", "sales_manager"].includes(getCurrentRole());
}

export function getCurrentUserDisplayName() {
  const currentUser = getCurrentUser();
  if (!currentUser) return "Guest";
  return currentUser.displayName || currentUser.customerName || currentUser.username || "Guest";
}

export async function recordUserActivity(userId) {
  const targetId = Number(userId);
  if (!Number.isFinite(targetId)) return false;
  const users = Array.isArray(demoState.users) ? demoState.users : [];
  const user = users.find(entry => Number(entry.id) === targetId);
  if (!user) return false;
  user.lastActiveAt = new Date().toISOString();
  await saveState();
  return true;
}

function findUserMatch({ role, username, password }) {
  const usernameNeedle = normalize(username);
  const passwordNeedle = String(password || "");
  const users = Array.isArray(demoState.users)
    ? demoState.users.filter(user => ["admin", "sales_staff", "sales_manager", "inventory_staff", "owner"].includes(normalize(user.role)))
    : [];

  return users.find(user => (
    String(user.status || "active").toLowerCase() === "active" &&
    (normalize(user.username) === usernameNeedle || normalize(user.displayName) === usernameNeedle || normalize(user.customerName) === usernameNeedle) &&
    String(user.password || "") === passwordNeedle
  )) || null;
}

export function authenticateUser({ username, password }) {
  const matched = findUserMatch({ username, password });
  if (matched) {
    matched.lastActiveAt = new Date().toISOString();
    return cloneUser(matched);
  }

  return null;
}

export function resolveUserSession(role = "guest") {
  const normalizedRole = normalize(role);
  const users = Array.isArray(demoState.users)
    ? demoState.users.filter(user => ["admin", "sales_staff", "sales_manager", "inventory_staff", "owner"].includes(normalize(user.role)))
    : [];
  return cloneUser(users.find(user => normalize(user.role) === normalizedRole) || null);
}

export function getStaffUsers() {
  const users = Array.isArray(demoState.users) ? demoState.users : [];
  const validUsers = users.filter(user => ["admin", "sales_staff", "sales_manager", "inventory_staff", "owner"].includes(normalize(user.role)));
  if (getCurrentRole() === "admin") {
    return validUsers.filter(user => normalize(user.role) !== "owner");
  }
  if (getCurrentRole() === "owner") {
    // Owners can review the complete employee directory.  Previously this
    // returned only the currently signed-in owner, which made the employee
    // management page appear empty even though staff accounts existed.
    return validUsers;
  }
  return validUsers.filter(user => normalize(user.role) !== "owner");
}

export async function createStaffAccount({ username, displayName, password, role = "sales_staff", permissions = [], mfaEnabled = false, mfaMethod = "Authenticator app", status = "active", email = "" }) {
  const cleanedUsername = String(username || "").trim();
  const cleanedDisplayName = String(displayName || "").trim() || cleanedUsername;
  const cleanedPassword = String(password || "").trim();
  const cleanedEmail = String(email || "").trim().toLowerCase();

  if (!cleanedUsername || !cleanedPassword || !cleanedEmail) {
    return { ok: false, reason: "Username, email, and password are required." };
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(cleanedEmail)) {
    return { ok: false, reason: "Enter a valid employee email address." };
  }

  const users = Array.isArray(demoState.users) ? demoState.users : (demoState.users = []);
  const duplicate = users.find(user => (
    normalize(user.username) === normalize(cleanedUsername)
  ));
  if (duplicate) {
    return { ok: false, reason: "That username already exists." };
  }

  const allowedRoles = ["sales_staff", "sales_manager", "inventory_staff"];
  const normalizedRole = allowedRoles.includes(normalize(role)) ? normalize(role) : "sales_staff";
  const defaultViews = roleConfig[normalizedRole].views;
  const selectedPermissions = Array.isArray(permissions) && permissions.length
    ? defaultViews.filter(view => permissions.includes(view))
    : [...defaultViews];
  const newStaff = {
    id: Date.now(),
    username: cleanedUsername,
    displayName: cleanedDisplayName,
    password: cleanedPassword,
    role: normalizedRole,
    email: cleanedEmail,
    status: ["active", "inactive", "suspended", "banned", "pending"].includes(normalize(status)) ? normalize(status) : "active",
    joinedAt: new Date().toISOString(),
    lastActiveAt: new Date().toISOString(),
    permissions: selectedPermissions,
    mfaEnabled: Boolean(mfaEnabled),
    mfaMethod: String(mfaMethod || "Authenticator app")
  };

  users.push(newStaff);
  await saveState();
  return { ok: true, user: cloneUser(newStaff) };
}

export async function updateStaffAccount({ id, username, displayName, password, role, permissions, mfaEnabled, mfaMethod, status, email }) {
  const targetId = Number(id);
  if (!Number.isFinite(targetId)) {
    return { ok: false, reason: "Select a staff account to update." };
  }

  const cleanedUsername = String(username || "").trim();
  const cleanedDisplayName = String(displayName || "").trim() || cleanedUsername;
  const cleanedPassword = String(password || "").trim();
  const cleanedEmail = String(email || "").trim().toLowerCase();
  if (!cleanedUsername) {
    return { ok: false, reason: "Username is required." };
  }
  if (!cleanedEmail) {
    return { ok: false, reason: "Email is required for every employee account." };
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(cleanedEmail)) {
    return { ok: false, reason: "Enter a valid employee email address." };
  }

  const users = Array.isArray(demoState.users) ? demoState.users : (demoState.users = []);
  const target = users.find(user => Number(user.id) === targetId && ["sales_staff", "sales_manager", "inventory_staff"].includes(normalize(user.role)));
  if (!target) {
    return { ok: false, reason: "That staff account could not be found." };
  }

  const duplicate = users.find(user => (
    Number(user.id) !== targetId &&
    normalize(user.username) === normalize(cleanedUsername)
  ));
  if (duplicate) {
    return { ok: false, reason: "That username already exists." };
  }

  target.username = cleanedUsername;
  target.displayName = cleanedDisplayName;
  target.email = cleanedEmail;
  const normalizedRole = ["sales_staff", "sales_manager", "inventory_staff"].includes(normalize(role)) ? normalize(role) : normalize(target.role);
  target.role = normalizedRole;
  target.status = ["active", "inactive", "suspended", "banned", "pending"].includes(normalize(status)) ? normalize(status) : String(target.status || "active").toLowerCase();
  const defaultViews = roleConfig[normalizedRole].views;
  target.permissions = Array.isArray(permissions) && permissions.length
    ? defaultViews.filter(view => permissions.includes(view))
    : [...defaultViews];
  target.mfaEnabled = Boolean(mfaEnabled);
  target.mfaMethod = String(mfaMethod || "Authenticator app");
  if (cleanedPassword) {
    target.password = cleanedPassword;
  }
  await saveState();
  return { ok: true, user: cloneUser(target) };
}

export async function deleteStaffAccount(id) {
  const targetId = Number(id);
  if (!Number.isFinite(targetId)) {
    return { ok: false, reason: "Select a staff account to delete." };
  }

  const users = Array.isArray(demoState.users) ? demoState.users : (demoState.users = []);
  const index = users.findIndex(user => Number(user.id) === targetId && ["sales_staff", "sales_manager", "inventory_staff"].includes(normalize(user.role)));
  if (index < 0) {
    return { ok: false, reason: "That staff account could not be found." };
  }

  users.splice(index, 1);
  await saveState();
  return { ok: true };
}
