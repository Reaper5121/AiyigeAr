const { spawnSync } = require("child_process");
const fs = require("fs");
const path = require("path");

const rootDir = path.resolve(__dirname, "..");
const gradleCommand = "gradlew.bat";

function readSdkDir() {
  try {
    const properties = fs.readFileSync(path.join(rootDir, "local.properties"), "utf8");
    const match = properties.match(/^sdk\.dir=(.+)$/m);
    return match ? match[1].trim().replace(/\\:/g, ":").replace(/\\\\/g, "\\") : null;
  } catch {
    return null;
  }
}

function getReadyDevices(adbPath) {
  if (!adbPath || !fs.existsSync(adbPath)) return [];
  const result = spawnSync(adbPath, ["devices"], { encoding: "utf8", windowsHide: true });
  if (result.status !== 0) return [];
  return (result.stdout || "")
    .split(/\r?\n/)
    .slice(1)
    .map(line => line.trim().split(/\s+/))
    .filter(parts => parts.length >= 2 && parts[1] === "device")
    .map(parts => parts[0]);
}

function runGradle(args) {
  return spawnSync("cmd.exe", ["/c", gradleCommand, ...args], {
    cwd: rootDir,
    stdio: "inherit",
    windowsHide: false,
    env: process.env
  });
}

const sdkDir = readSdkDir();
const adbPath = sdkDir ? path.join(sdkDir, "platform-tools", "adb.exe") : null;
const requestedSerial = process.env.ANDROID_DEVICE_SERIAL;
const devices = getReadyDevices(adbPath);
const serial = requestedSerial && devices.includes(requestedSerial) ? requestedSerial : devices[0];

if (!serial) {
  console.log("[mobile] no connected Android device/emulator found; building the APK without installing it.");
  process.exit(runGradle([":compose-app:assembleDebug"]).status || 0);
}

const install = runGradle([
  ":compose-app:installDebug",
  `-Pandroid.injected.device.serial=${serial}`
]);

if (install.status === 0) process.exit(0);

const remainingDevices = getReadyDevices(adbPath);
if (remainingDevices.length === 0) {
  console.log("[mobile] device disconnected during install; building the APK instead.");
  process.exit(runGradle([":compose-app:assembleDebug"]).status || 0);
}

process.exit(install.status || 1);
