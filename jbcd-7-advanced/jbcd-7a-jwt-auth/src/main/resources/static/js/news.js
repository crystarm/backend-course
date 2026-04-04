const NewsApi = (() => {
    async function getAllNews() {
        return AuthApi.request("/api/news");
    }

    async function createNews(title, message) {
        return AuthApi.request("/api/news", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({title, message})
        });
    }

    function renderNewsList(targetId, events) {
        const container = document.getElementById(targetId);
        if (!container) {
            return;
        }

        if (!events || events.length === 0) {
            container.innerHTML = `
                <div class="card bg-base-100 shadow-sm">
                    <div class="card-body">
                        <p class="text-base-content/70">Пока новостей нет.</p>
                    </div>
                </div>
            `;
            return;
        }

        container.innerHTML = events.map((event) => `
            <article class="card bg-base-100 shadow-sm">
                <div class="card-body">
                    <h3 class="card-title">${event.title}</h3>
                    <p>${event.message}</p>
                </div>
            </article>
        `).join("");
    }

    return {
        getAllNews,
        createNews,
        renderNewsList
    };
})();

window.NewsApi = NewsApi;
