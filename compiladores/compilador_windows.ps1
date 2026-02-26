#Requires -Version 5.1
# ISLA DIGITAL - Compilador Windows
# Compila EXE para Windows

$ProjectPath = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)

function Write-Status {
    param([string]$Message, [string]$Status = "INFO")
    $colors = @{ "OK" = "Green"; "ERROR" = "Red"; "INFO" = "Cyan"; "WARN" = "Yellow" }
    Write-Host "[$Status] $Message" -ForegroundColor $colors[$Status]
}

Write-Status "=== COMPILADOR WINDOWS ===" "INFO"
Write-Status "Proyecto: $ProjectPath" "INFO"

# Verificar Flutter
if (-not (Get-Command flutter -ErrorAction SilentlyContinue)) {
    Write-Status "Flutter no encontrado" "ERROR"
    exit 1
}

# Verificar Visual Studio
$vsWhere = "${env:ProgramFiles(x86)}\Microsoft Visual Studio\Installer\vswhere.exe"
if (-not (Test-Path $vsWhere)) {
    Write-Status "Visual Studio no encontrado. Instala VS con 'Desktop development with C++'" "ERROR"
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

# Compilar Windows
Write-Status "Compilando Windows..." "INFO"
flutter config --enable-windows-desktop | Out-Null
flutter build windows --release

if ($LASTEXITCODE -eq 0) {
    $exe = "build\windows\x64\runner\Release\Isla Digital.exe"
    if (Test-Path $exe) {
        $size = (Get-Item $exe).Length / 1MB
        Write-Status "EXE Generado: $([math]::Round($size,2)) MB" "OK"
        Write-Status "Ubicacion: $exe" "INFO"
    }
} else {
    Write-Status "Fallo compilacion Windows" "ERROR"
}

Write-Status "=== COMPILACION WINDOWS COMPLETADA ===" "OK"
Write-Host "Presiona ENTER para salir..."
Read-Host
