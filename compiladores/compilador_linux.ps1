#Requires -Version 5.1
# ISLA DIGITAL - Compilador Linux
# Compila para Linux (requiere WSL o Linux)

$ProjectPath = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)

function Write-Status {
    param([string]$Message, [string]$Status = "INFO")
    $colors = @{ "OK" = "Green"; "ERROR" = "Red"; "INFO" = "Cyan"; "WARN" = "Yellow" }
    Write-Host "[$Status] $Message" -ForegroundColor $colors[$Status]
}

Write-Status "=== COMPILADOR LINUX ===" "INFO"
Write-Status "Proyecto: $ProjectPath" "INFO"

# Verificar si estamos en Linux o WSL
$isLinux = $false
try {
    $uname = & uname -r 2>$null
    if ($uname -match "microsoft" -or $uname -match "WSL" -or $uname) {
        $isLinux = $true
    }
} catch {
    Write-Status "No se detecto Linux/WSL" "ERROR"
    Write-Status "Para compilar Linux en Windows, usa WSL" "INFO"
    exit 1
}

if (-not $isLinux) {
    Write-Status "Este script debe ejecutarse en Linux o WSL" "ERROR"
    exit 1
}

# Verificar Flutter
if (-not (Get-Command flutter -ErrorAction SilentlyContinue)) {
    Write-Status "Flutter no encontrado" "ERROR"
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

# Compilar Linux
Write-Status "Compilando Linux..." "INFO"
flutter config --enable-linux-desktop | Out-Null
flutter build linux --release

if ($LASTEXITCODE -eq 0) {
    Write-Status "Linux compilado exitosamente" "OK"
    Write-Status "Ubicacion: build/linux/x64/release/bundle/" "INFO"
} else {
    Write-Status "Fallo compilacion Linux" "ERROR"
}

Write-Status "=== COMPILACION LINUX COMPLETADA ===" "OK"
Write-Host "Presiona ENTER para salir..."
Read-Host
