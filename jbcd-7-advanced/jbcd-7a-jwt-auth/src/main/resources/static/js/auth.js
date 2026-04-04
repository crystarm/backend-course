import {UserManager, WebStorageStateStore} from "https://cdn.jsdelivr.net/npm/oidc-client-ts@3.3.0/+esm";

const AuthApi = (() => {
    let userManagerPromise;
    let renewPromise = null;

    async function fetchOidcConfiguration() {
        const response = await fetch("/api/auth/config");
        const body = await response.json();
        return body.data;
    }

    async function getUserManager() {
        if (!userManagerPromise) {
            userManagerPromise = fetchOidcConfiguration().then((config) => {
                const userManager = new UserManager({
                    authority: config.issuerUri,
                    client_id: config.clientId,
                    redirect_uri: config.redirectUri,
                    response_type: "code",
                    scope: config.scope,
                    userStore: new WebStorageStateStore({store: window.sessionStorage}),
                    automaticSilentRenew: false
                });

                userManager.events.addAccessTokenExpiring(() => {
                    renewTokens(userManager).catch(() => userManager.removeUser());
                });
                userManager.events.addAccessTokenExpired(() => {
                    renewTokens(userManager).catch(() => userManager.removeUser());
                });

                return {userManager, config};
            });
        }

        return userManagerPromise;
    }

    async function renewTokens(userManager) {
        if (!renewPromise) {
            renewPromise = userManager.signinSilent().finally(() => {
                renewPromise = null;
            });
        }

        return renewPromise;
    }

    async function getActiveUser() {
        const {userManager, config} = await getUserManager();
        let user = await userManager.getUser();
        if (!user) {
            return null;
        }

        const expiresIn = typeof user.expires_in === "number" ? user.expires_in : null;
        const shouldRefresh = user.expired
            || (expiresIn !== null && expiresIn <= config.accessTokenExpiringNotificationSeconds);

        if (shouldRefresh) {
            try {
                user = await renewTokens(userManager);
            } catch (error) {
                await userManager.removeUser();
                return null;
            }
        }

        return user;
    }

    function getBearerToken(user) {
        return user?.id_token ?? user?.access_token ?? null;
    }

    async function request(url, options = {}) {
        const method = options.method ?? "GET";
        const headers = new Headers(options.headers ?? {});
        const user = await getActiveUser();
        const bearerToken = getBearerToken(user);

        if (bearerToken) {
            headers.set("Authorization", `Bearer ${bearerToken}`);
        }

        const response = await fetch(url, {
            ...options,
            method,
            headers
        });

        const isJson = response.headers.get("content-type")?.includes("application/json");
        const body = isJson ? await response.json() : null;
        return {response, body};
    }

    async function handleLoginCallbackIfNeeded() {
        const searchParams = new URLSearchParams(window.location.search);
        if (!searchParams.has("code") || !searchParams.has("state")) {
            return false;
        }

        const {userManager} = await getUserManager();
        await userManager.signinRedirectCallback();
        window.location.replace("/");
        return true;
    }

    async function login() {
        const {userManager} = await getUserManager();
        await userManager.signinRedirect();
    }

    async function logout() {
        const {userManager} = await getUserManager();
        await userManager.removeUser();
    }

    async function getCurrentUser() {
        const {response, body} = await request("/api/auth/me");
        return {
            status: response.status,
            body
        };
    }

    function extractDisplayName(claims = {}) {
        return claims.email ?? claims.name ?? claims.preferred_username ?? claims.sub ?? "unknown";
    }

    function showAlert(targetId, type, message) {
        const container = document.getElementById(targetId);
        if (!container) {
            return;
        }

        container.innerHTML = `
            <div class="alert ${type}">
                <span>${message}</span>
            </div>
        `;
    }

    return {
        getCurrentUser,
        handleLoginCallbackIfNeeded,
        login,
        logout,
        request,
        extractDisplayName,
        showAlert
    };
})();

window.AuthApi = AuthApi;
