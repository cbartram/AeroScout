#!/usr/bin/env sh

echo "[INFO] Creating Directory Structure..."
mkdir -p ./lib

echo "[INFO] Downloading OSBot Jar File..."
curl -o ./lib/osbot.jar https://osbot.org/mvc/get > /dev/null 2>&1
echo "[INFO] Downloading Lombok Jar File..."
curl -o ./lib/lombok.jar https://projectlombok.org/downloads/lombok.jar > /dev/null 2>&1