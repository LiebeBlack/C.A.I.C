#Requires -Version 5.1
# ISLA DIGITAL - Compilador Android
# Compila APK y App Bundle para Android

param([string]$Tipo = "apk")

$ProjectPath = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)

function Write-Status {
    param([string]$Message, [string]$Status = "INFO")
    $colors = @{ "OK" = "Green"; "ERROR" = "Red"; "INFO" = "Cyan"; "WARN" = "Yellow" }
    Write-Host "[$Status] $Message" -ForegroundColor $colors[$Status]
}

Write-Status "=== COMPILADOR ANDROID ===" "INFO"
Write-Status "Proyecto: $ProjectPath" "INFO"
Write-Status "Tipo: $Tipo" "INFO"

# Verificar Flutter
if (-not (Get-Command flutter -ErrorAction SilentlyContinue)) {
    Write-Status "Flutter no encontrado" "ERROR"
    exit 1
}

# Verificar Android SDK
if (-not $env:ANDROID_SDK_ROOT -and -not $env:ANDROID_HOME) {
    Write-Status "Android SDK no configurado" "ERROR"
    exit 1
}

Set-Location $ProjectPath

# Instalar dependencias
Write-Status "Instalando dependencias..." "INFO"
flutter pub get
if ($LASTEXITCODE -ne 0) {
    Write-Status "Error en dependencias" "ERROR"
    exit 1
}

# Compilar según tipo
if ($Tipo -eq "apk" -or $Tipo -eq "all") {
    Write-Status "Compilando APK..." "INFO"
    flutter build apk --release
    if ($LASTEXITCODE -eq 0) {
        $apk = "build\app\outputs\flutter-apk\app-release.apk"
        if (Test-Path $apk) {
            $size = (Get-Item $apk).Length / 1MB
            Write-Status "APK Generado: $([math]::Round($size,2)) MB" "OK"
            Write-Status "Ubicacion: $apk" "INFO"
        }
    } else {
        Write-Status "Fallo APK" "ERROR"
    }
}

if ($Tipo -eq "aab" -or $Tipo -eq "all") {
    Write-Status "Compilando AAB..." "INFO"
    flutter build appbundle --release
    if ($LASTEXITCODE -eq 0) {
        $aab = "build\app\outputs\bundle\release\app-release.aab"
        if (Test-Path $aab) {
            $size = (Get-Item $aab).Length / 1MB
            Write-Status "AAB Generado: $([math]::Round($size,2)) MB" "OK"
            Write-Status "Ubicacion: $aab" "INFO"
        }
    } else {
        Write-Status "Fallo AAB" "ERROR"
    }
}

Write-Status "=== COMPILACION ANDROID COMPLETADA ===" "OK"
Write-Host "Presiona ENTER para salir..."
Read-Host
