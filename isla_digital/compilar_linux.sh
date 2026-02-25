#!/bin/bash

# ============================================
# ISLA DIGITAL - Compilador Android (Linux)
# ============================================
# Uso: ./compilar_linux.sh [apk|aab|all]
# ============================================

set -e

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

PROJECT_PATH="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_TYPE="${1:-apk}"

print_header() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}  ISLA DIGITAL - COMPILADOR ANDROID${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo ""
}

print_success() { echo -e "${GREEN}[OK]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }
print_info() { echo -e "${BLUE}[INFO]${NC} $1"; }

# Verificar Flutter
verificar_flutter() {
    print_info "Verificando Flutter..."
    
    if ! command -v flutter &> /dev/null; then
        print_info "Buscando Flutter..."
        
        FLUTTER_PATHS=(
            "$HOME/flutter/bin"
            "/opt/flutter/bin"
            "/usr/local/flutter/bin"
            "/snap/bin"
        )
        
        for path in "${FLUTTER_PATHS[@]}"; do
            if [ -f "$path/flutter" ]; then
                export PATH="$path:$PATH"
                print_success "Flutter encontrado: $path"
                break
            fi
        done
    fi
    
    if ! command -v flutter &> /dev/null; then
        print_error "Flutter no encontrado"
        exit 1
    fi
    
    FLUTTER_VERSION=$(flutter --version 2>/dev/null | head -n 1)
    print_success "$FLUTTER_VERSION"
}

# Verificar Android SDK
verificar_android_sdk() {
    print_info "Verificando Android SDK..."
    
    if [ -z "$ANDROID_SDK_ROOT" ] && [ -z "$ANDROID_HOME" ]; then
        print_info "Buscando Android SDK..."
        
        SDK_PATHS=(
            "$HOME/Android/Sdk"
            "/opt/android-sdk"
            "/usr/lib/android-sdk"
        )
        
        for path in "${SDK_PATHS[@]}"; do
            if [ -d "$path/platform-tools" ]; then
                export ANDROID_SDK_ROOT="$path"
                print_success "Android SDK: $path"
                break
            fi
        done
    fi
    
    if [ -z "$ANDROID_SDK_ROOT" ] && [ -n "$ANDROID_HOME" ]; then
        export ANDROID_SDK_ROOT="$ANDROID_HOME"
    fi
    
    if [ -z "$ANDROID_SDK_ROOT" ]; then
        print_error "Android SDK no encontrado"
        exit 1
    fi
}

# Instalar dependencias
instalar_dependencias() {
    print_info "Instalando dependencias..."
    cd "$PROJECT_PATH"
    flutter pub get
    print_success "Dependencias instaladas"
}

# Verificar assets
verificar_assets() {
    print_info "Verificando assets..."
    for dir in "assets/images" "assets/audio" "assets/animations" "assets/icons" "assets/fonts"; do
        if [ ! -d "$dir" ]; then
            mkdir -p "$dir"
        fi
    done
    print_success "Assets verificados"
}

# Compilar APK
compilar_apk() {
    print_info "Compilando APK..."
    cd "$PROJECT_PATH"
    flutter build apk --release
    
    if [ -f "build/app/outputs/flutter-apk/app-release.apk" ]; then
        print_success "APK Generado!"
        echo "  Ubicacion: build/app/outputs/flutter-apk/app-release.apk"
        ls -lh build/app/outputs/flutter-apk/app-release.apk
    fi
}

# Compilar AAB
compilar_aab() {
    print_info "Compilando App Bundle..."
    cd "$PROJECT_PATH"
    flutter build appbundle --release
    
    if [ -f "build/app/outputs/bundle/release/app-release.aab" ]; then
        print_success "AAB Generado!"
        echo "  Ubicacion: build/app/outputs/bundle/release/app-release.aab"
    fi
}

# Main
main() {
    print_header
    
    case "$BUILD_TYPE" in
        apk|aab|all) ;;
        *)
            print_error "Tipo invalido: $BUILD_TYPE"
            echo "Uso: $0 [apk|aab|all]"
            exit 1
            ;;
    esac
    
    verificar_flutter
    verificar_android_sdk
    instalar_dependencias
    verificar_assets
    
    case "$BUILD_TYPE" in
        apk) compilar_apk ;;
        aab) compilar_aab ;;
        all)
            compilar_apk
            compilar_aab
            ;;
    esac
    
    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}   COMPILACION COMPLETADA${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
}

main "$@"
