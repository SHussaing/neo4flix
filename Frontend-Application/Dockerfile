# syntax=docker/dockerfile:1

# --- Build stage ---
FROM node:20-alpine AS build
WORKDIR /app

COPY Frontend-Application/package.json Frontend-Application/package-lock.json* ./
RUN npm ci

COPY Frontend-Application/ .
RUN npm run build -- --configuration production


# --- Test stage (used by CI) ---
# Note: Angular 21 uses the new build system; `ng test` requires a browser environment.
# If your project isn't configured with a headless runner yet, this stage will fail.
# You can later adjust this to use a headless Chromium image + karma/jasmine setup.
FROM node:20-alpine AS test
WORKDIR /app

COPY Frontend-Application/package.json Frontend-Application/package-lock.json* ./
RUN npm ci

COPY Frontend-Application/ .
# "--watch=false" keeps it non-interactive in CI
RUN npm test -- --watch=false


# --- Runtime stage ---
FROM nginx:1.27-alpine AS runtime

# Install openssl and openjdk for keytool (needed to extract certs from p12)
RUN apk add --no-cache openssl openjdk17-jre-headless

# Nginx config is already in the repo; keep default if missing
COPY Frontend-Application/nginx.conf /etc/nginx/conf.d/default.conf

# Copy certs and generate SSL cert/key
COPY certs /certs
RUN mkdir -p /etc/nginx/certs && \
    keytool -exportcert -rfc \
      -alias shared \
      -keystore /certs/shared.p12 \
      -storetype PKCS12 \
      -storepass changeit \
      -file /etc/nginx/certs/frontend.crt && \
    openssl pkcs12 \
      -in /certs/shared.p12 \
      -passin pass:changeit \
      -nocerts -nodes \
      | openssl pkey -out /etc/nginx/certs/frontend.key && \
    chmod 600 /etc/nginx/certs/frontend.key

# Angular (builder: @angular/build:application) outputs to:
# dist/<projectName>/browser
COPY --from=build /app/dist/Frontend-Application/browser/ /usr/share/nginx/html/

EXPOSE 80 443
CMD ["nginx", "-g", "daemon off;"]
