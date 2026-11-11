const { spawn, spawnSync } = require("child_process");
const fs = require("fs");
const path = require("path");

const rootDir = path.resolve(__dirname, "..");
const processes = new Map();
let shuttingDown = false;
const sdkDir = readAndroidSdkDir();
const adbPath = sdkDir ? path.join(sdkDir, "platform-tools", "adb.exe") : null;
const javaHome = resolveJavaHome();
let primaryDeviceSerial = null;
let knownReadyDeviceSerials = new Set();
let deviceMonitorTimer = null;
const legacyCustomerPackage = "com.aiyige.customer";
const activeComposePackage = "com.aiyige.customer.compose";

function readAndroidSdkDir() {
  const localPropertiesPath = path.join(rootDir, "local.properties");
  try {
    const raw = fs.readFileSync(localPropertiesPath, "utf8");
    const match = raw.match(/^sdk\.dir=(.+)$/m);
    if (!match) return null;
    return match[1].trim().replace(/\\:/g, ":").replace(/\\\\/g, "\\");
  } catch {
    return null;
  }
}

function resolveJavaHome() {
  const envJavaHome = process.env.JAVA_HOME;
  if (envJavaHome && fs.existsSync(envJavaHome)) {
    return envJavaHome;
  }

  const candidates = [
    path.join("C:\\Program Files\\Android\\Android Studio", "jbr"),
    path.join("C:\\Program Files\\Android\\Android Studio", "jre"),
    path.join("C:\\Program Files\\Java", "jdk"),
    path.join("C:\\Program Files\\Java", "jre")
  ];

  for (const candidate of candidates) {
    if (fs.existsSync(candidate)) {
      return candidate;
    }
  }

  return null;
}

function withJavaEnv(extraEnv = {}) {
  if (!javaHome) {
    return { ...process.env, ...extraEnv };
  }

  return {
    ...process.env,
    JAVA_HOME: javaHome,
    PATH: `${path.join(javaHome, "bin")};${process.env.PATH || ""}`,
    ...extraEnv
  };
}

function launchAndroidApp(name, componentName, serial) {
  if (!adbPath || !fs.existsSync(adbPath)) {
    console.log(`[${name}] skipped launch because adb.exe was not found.`);
    return;
  }

  const backendUrl = "http://127.0.0.1:3000";
  const args = serial
    ? ["-s", serial, "shell", "am", "start", "-S", "-n", componentName, "--es", "backend_url", backendUrl]
    : ["shell", "am", "start", "-S", "-n", componentName, "--es", "backend_url", backendUrl];

  const result = spawnSync(adbPath, args, {
    cwd: rootDir,
    encoding: "utf8",
    shell: false,
    timeout: 15000
  });

  const output = `${result.stdout || ""}${result.stderr || ""}`.trim();
  if (result.status !== 0) {
    console.error(`[${name}] could not launch ${componentName}: ${output || "adb returned an error"}`);
    return;
  }
  console.log(`[${name}] launched ${componentName}${output ? `: ${output}` : ""}`);
}

function getConnectedDevices() {
  if (!adbPath || !fs.existsSync(adbPath)) return [];

  const result = spawnSync(adbPath, ["devices"], {
    cwd: rootDir,
    encoding: "utf8",
    shell: false
  });

  if (result.status !== 0 || !result.stdout) return [];

  return result.stdout
    .split(/\r?\n/)
    .slice(1)
    .map(line => line.trim().split(/\s+/))
    .filter(parts => parts.length >= 2 && parts[1] === "device")
    .map(parts => parts[0]);
}

function isDeviceReady(serial) {
  if (!adbPath || !fs.existsSync(adbPath)) return false;

  const stateResult = spawnSync(adbPath, ["-s", serial, "get-state"], {
    cwd: rootDir,
    encoding: "utf8",
    shell: false,
    timeout: 5000
  });

  if (stateResult.status !== 0 || !/device/i.test(stateResult.stdout || "")) {
    return false;
  }

  const bootResult = spawnSync(adbPath, ["-s", serial, "shell", "getprop", "sys.boot_completed"], {
    cwd: rootDir,
    encoding: "utf8",
    shell: false,
    timeout: 5000
  });

  return bootResult.status === 0 && /\b1\b/.test((bootResult.stdout || "").trim());
}

function getReadyDevices() {
  return getConnectedDevices().filter(isDeviceReady);
}

function reversePortOnDevice(serial, hostPort, devicePort) {
  if (!adbPath || !fs.existsSync(adbPath)) return false;

  const args = ["-s", serial, "reverse", `tcp:${devicePort}`, `tcp:${hostPort}`];
  const result = spawnSync(adbPath, args, {
    cwd: rootDir,
    encoding: "utf8",
    shell: false
  });

  if (result.status !== 0) {
    console.log(`[adb:${serial}] port reverse failed for ${devicePort}: ${result.stderr || result.stdout || "unknown error"}`.trim());
    return false;
  }

  return true;
}

function setupPortReverses() {
  const devices = getReadyDevices();
  if (devices.length === 0) {
    console.log("[mobile] no ready Android device/emulator found, skipping adb reverse.");
    return;
  }

  primaryDeviceSerial = devices[0];
  if (devices.length > 1) {
    console.log(`[mobile] multiple Android devices found, using ${primaryDeviceSerial} for launch commands.`);
  }

  for (const serial of devices) {
    reversePortOnDevice(serial, 3000, 3000);
  }
}

function monitorAndroidReconnects() {
  if (!adbPath || !fs.existsSync(adbPath)) return;
  knownReadyDeviceSerials = new Set(getReadyDevices());
  deviceMonitorTimer = setInterval(() => {
    const readyDevices = getReadyDevices();
    const currentSerials = new Set(readyDevices);

    for (const serial of knownReadyDeviceSerials) {
      if (!currentSerials.has(serial)) {
        console.log(`[adb:${serial}] disconnected; the backend will keep running.`);
      }
    }

    for (const serial of currentSerials) {
      if (knownReadyDeviceSerials.has(serial)) continue;
      if (reversePortOnDevice(serial, 3000, 3000)) {
        console.log(`[adb:${serial}] reconnected; restored backend forwarding without restarting the server.`);
      }
      if (!primaryDeviceSerial || !currentSerials.has(primaryDeviceSerial)) {
        primaryDeviceSerial = serial;
      }
    }

    knownReadyDeviceSerials = currentSerials;
  }, 3_000);
}

function removeLegacyMobileApp(serial = primaryDeviceSerial) {
  if (!adbPath || !fs.existsSync(adbPath) || !serial) return;

  // The old package can remain in Android's recent-task list and reopen its
  // legacy drawer UI. Stop it before launching the current Compose app. We do
  // not uninstall it, so its old local data remains recoverable.
  const result = spawnSync(adbPath, [
    "-s", serial, "shell", "am", "force-stop", legacyCustomerPackage
  ], {
    cwd: rootDir,
    encoding: "utf8",
    shell: false,
    timeout: 5000
  });

  if (result.status !== 0) {
    console.log(`[adb:${serial}] could not stop legacy mobile app: ${result.stderr || result.stdout || "unknown error"}`.trim());
  }
}

function hasConnectedAndroidDevice() {
  return getReadyDevices().length > 0;
}

function start(name, command, args, options = {}) {
  const child = spawn(command, args, {
    cwd: rootDir,
    stdio: "inherit",
    shell: false,
    env: options.env || process.env
  });

  child.on("exit", (code, signal) => {
    processes.delete(name);
    if (signal) {
      console.log(`[${name}] exited with signal ${signal}`);
    } else {
      console.log(`[${name}] exited with code ${code}`);
    }

    if (name === "server") {
      // server.js exits cleanly when port 3000 is already owned by the
      // backend from an earlier run. Keep the mobile workflow alive in that
      // case instead of shutting down the APK install/launch process.
      if (code === 0 && !shuttingDown) {
        processes.delete(name);
        return;
      }
      shutdown(code ?? 0);
      return;
    }

    if (name === "mobile" && code === 2) {
      console.log(`[${name}] install skipped; backend server will keep running.`);
      return;
    }

    if (code && code !== 0) {
      console.log(`[${name}] finished with an error, but the backend server will keep running.`);
    } else {
      if (options.launchComponent) {
        launchAndroidApp(name, options.launchComponent, primaryDeviceSerial);
      }
      if (processes.size === 0) {
        shutdown(0);
      }
    }
  });

  child.on("error", error => {
    console.error(`[${name}] failed to start:`, error.message);
    shutdown(1);
  });

  processes.set(name, child);
}

function shutdown(exitCode) {
  if (shuttingDown) return;
  shuttingDown = true;
  clearInterval(deviceMonitorTimer);
  for (const child of processes.values()) {
    if (!child.killed) {
      child.kill();
    }
  }
  process.exit(exitCode);
}

process.on("SIGINT", () => shutdown(0));
process.on("SIGTERM", () => shutdown(0));

// Keep the backend process alive and restart it automatically when server-side
// files change. Static web assets are read per request, so refreshing the
// browser is enough to pick up HTML/CSS/JS edits without restarting npm.
start("server", process.execPath, ["--watch", "server.js"]);
if (!javaHome) {
  console.log("[mobile] JAVA_HOME was not found, so Android tasks were skipped.");
} else if (!hasConnectedAndroidDevice()) {
  console.log("[mobile] no ready Android device/emulator found, skipping Android install.");
} else {
  setupPortReverses();
  // Gradle otherwise attempts to install on every device returned by adb,
  // including devices that disconnect between discovery and installation.
  // Pin the install to the device selected above.
  removeLegacyMobileApp(primaryDeviceSerial);
  start("mobile", process.execPath, ["scripts/install-mobile.js"], {
    env: withJavaEnv({ ANDROID_DEVICE_SERIAL: primaryDeviceSerial }),
    launchComponent: `${activeComposePackage}/.MainActivity`
  });
}

monitorAndroidReconnects();
