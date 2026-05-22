#!/usr/bin/env bash
set -euo pipefail

# Script de build para Vercel: compila con Maven Wrapper y copia los assets estáticos a /public

echo "Starting vercel build script"

# Asegurar permisos en mvnw si se sube desde Windows
if [ -f ./mvnw ]; then
  chmod +x ./mvnw || true
fi

echo "Ejecutando build de Maven..."
./mvnw -ntp -DskipTests package

echo "Creando carpeta public y copiando assets..."
mkdir -p public
cp -r target/classes/static/. public/ || true

echo "Build completo. Archivos disponibles en ./public"

