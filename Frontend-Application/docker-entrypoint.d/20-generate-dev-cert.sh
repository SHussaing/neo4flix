#!/bin/sh
set -eu

CERT_DIR="/etc/nginx/certs"
CRT_FILE="$CERT_DIR/frontend.crt"
KEY_FILE="$CERT_DIR/frontend.key"

KEYSTORE_PATH="${SHARED_KEYSTORE_PATH:-/certs/shared.p12}"
KEYSTORE_PASS="${SHARED_KEYSTORE_PASSWORD:-changeit}"
KEYSTORE_ALIAS="${SHARED_KEYSTORE_ALIAS:-shared}"

mkdir -p "$CERT_DIR"

if [ -s "$CRT_FILE" ] && [ -s "$KEY_FILE" ]; then
  echo "[cert] Using existing TLS cert/key in $CERT_DIR"
  exit 0
fi

if [ ! -f "$KEYSTORE_PATH" ]; then
  echo "[cert] ERROR: shared keystore not found at $KEYSTORE_PATH"
  echo "[cert] Mount the repo ./certs folder into /certs in docker-compose."
  exit 1
fi

echo "[cert] Exporting TLS cert/key from shared keystore: $KEYSTORE_PATH (alias=$KEYSTORE_ALIAS)"

# Export certificate
keytool -exportcert -rfc \
  -alias "$KEYSTORE_ALIAS" \
  -keystore "$KEYSTORE_PATH" \
  -storetype PKCS12 \
  -storepass "$KEYSTORE_PASS" \
  -file "$CRT_FILE" >/dev/null

# Export private key: keystore -> PKCS8 key using openssl
# Step 1: convert PKCS12 to an unencrypted PKCS8 key file.
openssl pkcs12 \
  -in "$KEYSTORE_PATH" \
  -passin pass:"$KEYSTORE_PASS" \
  -nocerts -nodes \
  | openssl pkey -out "$KEY_FILE"

chmod 600 "$KEY_FILE"
