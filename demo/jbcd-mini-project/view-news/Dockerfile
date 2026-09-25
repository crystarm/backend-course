# --- build stage -----------------------------------------------------------
FROM node:22-alpine AS builder
WORKDIR /app

# Устанавливаем зависимости отдельным слоем для кэша.
COPY package.json package-lock.json* ./
RUN if [ -f package-lock.json ]; then npm ci; else npm install; fi

COPY . .
RUN npm run build

# --- runtime stage ---------------------------------------------------------
FROM nginx:stable-alpine AS runtime

# Статика SPA.
COPY --from=builder /app/dist /usr/share/nginx/html

# Собственный конфиг с SPA fallback.
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=10s --timeout=5s --retries=5 --start-period=5s \
  CMD wget -q --spider http://127.0.0.1/ || exit 1
