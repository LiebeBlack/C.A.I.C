#!/bin/bash
# Script para compilar Isla Digital en Linux
echo "Preparando compilación para Linux..."
/home/l/development/flutter/bin/flutter pub get
/home/l/development/flutter/bin/flutter clean
/home/l/development/flutter/bin/flutter pub get
echo "Compilando aplicación..."
/home/l/development/flutter/bin/flutter build linux --release
echo "¡Compilación completada! El binario se encuentra en build/linux/x64/release/bundle/"
