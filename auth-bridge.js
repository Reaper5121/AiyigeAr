(() => {
  const host = window.location.hostname;
  const isLocalhost = host === "localhost" || host === "127.0.0.1" || host === "::1";
  if (!isLocalhost) return;

  const authSearch = window.location.search || "";
  const authHash = window.location.hash || "";
  const authPayload = `${authSearch}${authHash}`;
  if (!authPayload) return;

  const params = new URLSearchParams(authSearch.replace(/^\?/, ""));
  const hashParams = new URLSearchParams(authHash.replace(/^#/, ""));
  const hasAuthData =
    ["access_token", "refresh_token", "error", "code", "token", "token_hash", "type"].some(key => params.has(key) || hashParams.has(key));

  if (!hasAuthData) return;

  const mergedParams = new URLSearchParams();
  for (const [key, value] of params.entries()) mergedParams.set(key, value);
  for (const [key, value] of hashParams.entries()) mergedParams.set(key, value);

  const serialized = mergedParams.toString();
  const querySuffix = serialized ? `?${serialized}` : "";
  const intentTarget = `intent://auth-callback${querySuffix}#Intent;scheme=aiyigecustomer;package=com.aiyige.customer.compose;end`;
  const customSchemeTarget = `aiyigecustomer://auth-callback${querySuffix}`;

  const attemptOpen = target => {
    const anchor = document.createElement("a");
    anchor.href = target;
    anchor.rel = "noreferrer noopener";
    anchor.style.display = "none";
    document.body.appendChild(anchor);
    anchor.click();
    anchor.remove();
    window.location.href = target;
  };

  attemptOpen(intentTarget);
  setTimeout(() => attemptOpen(customSchemeTarget), 150);
})();
