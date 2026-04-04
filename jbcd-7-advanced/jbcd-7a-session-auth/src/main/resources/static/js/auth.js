const AuthApi = (() => {
    const CSRF_COOKIE_NAME = "XSRF-TOKEN";
    const CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    function readCookie(name) {
        const encodedName = `${name}=`;
        return document.cookie
            .split(";")
            .map((item) => item.trim())
            .find((item) => item.startsWith(encodedName))
            ?.substring(encodedName.length) ?? null;
    }

    async function ensureCsrfCookie() {
        if (readCookie(CSRF_COOKIE_NAME)) {
            return;
        }

        await fetch("/api/auth/me", {
            method: "GET",
            credentials: "include"
        });
    }

    async function request(url, options = {}) {
        const method = options.method ?? "GET";
        const headers = new Headers(options.headers ?? {});

        if (!["GET", "HEAD", "OPTIONS"].includes(method.toUpperCase())) {
            await ensureCsrfCookie();
            const csrfToken = readCookie(CSRF_COOKIE_NAME);
            if (csrfToken) {
                headers.set(CSRF_HEADER_NAME, decodeURIComponent(csrfToken));
            }
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
        const {response, body} = await request("/api/auth/me");
        return {
            status: response.status,
            body
        };
    }

    async function register(email, password) {
        return request("/api/auth/registration", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({email, password})
        });
    }

    async function login(email, password) {
        const body = new URLSearchParams({email, password});
        return request("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
            body
        });
    }

    async function logout() {
        return request("/api/auth/logout", {
            method: "POST"
        });
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
        ensureCsrfCookie,
        getCurrentUser,
        register,
        login,
        logout,
        request,
        showAlert
    };
})();
