#!/bin/bash

# Comando que queremos ejecutar dentro de la nueva terminal
COMMANDS="
echo '🧹 Limpiando el proyecto...';
/home/l/development/flutter/bin/flutter clean;
echo '📦 Descargando dependencias actualizadas...';
/home/l/development/flutter/bin/flutter pub get;
echo '📱 Construyendo el APK de Android...';
/home/l/development/flutter/bin/flutter build apk --release;
echo '✅ ¡Compilación completada!';
echo '📍 El APK se encuentra en: build/app/outputs/flutter-apk/app-release.apk';
exec bash"

# Llama a lxterminal y ejecuta los comandos
lxterminal -e "bash -c \"$COMMANDS\""