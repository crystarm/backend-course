const AuthApi = (() => {
    async function request(url, options = {}) {
        const method = options.method ?? "GET";
        const headers = new Headers(options.headers ?? {});
        if (!headers.has("Accept")) {
            headers.set("Accept", "application/json");
        }

        const response = await fetch(url, {
            ...options,
            method,
            headers,
            credentials: "include"
        });

        const isJson = response.headers.get("content-type")?.includes("application/json");
        const body = isJson ? await response.json() : null;
        return {response, body};
    }

    async function getCurrentUser() {
        const {response, body} = await request("/api/me");
        return {
            status: response.status,
            body
        };
    }

    function login(returnTo = "/") {
        window.location.assign(`/oauth2/start?rd=${encodeURIComponent(returnTo)}`);
    }

    function logout(returnTo = "/login?logout") {
        window.location.assign(`/oauth2/sign_out?rd=${encodeURIComponent(returnTo)}`);
    }

    function extractDisplayName(userInfo = {}) {
        return userInfo.email ?? userInfo.preferred_username ?? userInfo.user ?? "unknown";
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
        login,
        logout,
        request,
        extractDisplayName,
        showAlert
    };
})();

window.AuthApi = AuthApi;
