#Requires -Version 5.1
# ISLA DIGITAL - Compilador Todos (Unificado)
# Menu para seleccionar plataforma a compilar

$ProjectPath = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$CompiladoresPath = Join-Path $ProjectPath "compiladores"

function Write-Status {
    param([string]$Message, [string]$Status = "INFO")
    $colors = @{ "OK" = "Green"; "ERROR" = "Red"; "INFO" = "Cyan"; "WARN" = "Yellow"; "MENU" = "Magenta" }
    Write-Host $Message -ForegroundColor $colors[$Status]
}

function Show-Menu {
    Clear-Host
    Write-Status "========================================" "MENU"
    Write-Status "    ISLA DIGITAL - COMPILADOR" "MENU"
    Write-Status "========================================" "MENU"
    Write-Status ""
    Write-Status "  [1] Android (APK)" "INFO"
    Write-Status "  [2] Android (App Bundle - Play Store)" "INFO"
    Write-Status "  [3] Windows (EXE)" "INFO"
    Write-Status "  [4] Linux" "INFO"
    Write-Status "  [5] TODAS las plataformas" "OK"
    Write-Status "  [0] Salir" "ERROR"
    Write-Status ""
    Write-Status "========================================" "MENU"
}

function Test-Flutter {
    if (-not (Get-Command flutter -ErrorAction SilentlyContinue)) {
        Write-Status "Flutter no encontrado en PATH" "ERROR"
        
        # Buscar en ubicaciones comunes
        $paths = @(
            "C:\flutter\bin",
            "C:\Program Files\flutter\bin",
            "$env:USERPROFILE\flutter\bin"
        )
        
        foreach ($p in $paths) {
            if (Test-Path "$p\flutter.bat") {
                $env:PATH = "$p;$env:PATH"
                Write-Status "Flutter encontrado en: $p" "OK"
                return $true
            }
        }
        
        Write-Status "No se encontro Flutter. Instala desde flutter.dev" "ERROR"
        return $false
    }
    return $true
}

function Test-AndroidSDK {
    if (-not $env:ANDROID_SDK_ROOT -and -not $env:ANDROID_HOME) {
        $paths = @(
            "$env:LOCALAPPDATA\Android\Sdk",
            "$env:USERPROFILE\AppData\Local\Android\Sdk",
            "C:\Android\Sdk"
        )
        
        foreach ($p in $paths) {
            if (Test-Path "$p\platform-tools\adb.exe") {
                $env:ANDROID_SDK_ROOT = $p
                Write-Status "Android SDK encontrado: $p" "OK"
                return $true
            }
        }
        
        Write-Status "Android SDK no encontrado" "ERROR"
        return $false
    }
    return $true
}

function Test-VisualStudio {
    $vswhere = "${env:ProgramFiles(x86)}\Microsoft Visual Studio\Installer\vswhere.exe"
    if (Test-Path $vswhere) {
        return $true
    }
    Write-Status "Visual Studio no encontrado (necesario para Windows)" "WARN"
    return $false
}

function Compile-Android {
    param([string]$Tipo = "apk")
    
    Write-Status "=== COMPILANDO ANDROID ===" "INFO"
    
    if (-not (Test-AndroidSDK)) {
        Write-Status "No se puede compilar sin Android SDK" "ERROR"
        return
    }
    
    $script = Join-Path $CompiladoresPath "compilador_android.ps1"
    if (Test-Path $script) {
        & $script -Tipo $Tipo
    } else {
        Write-Status "No se encontro: $script" "ERROR"
    }
}

function Compile-Windows {
    Write-Status "=== COMPILANDO WINDOWS ===" "INFO"
    
    if (-not (Test-VisualStudio)) {
        Write-Status "Visual Studio no esta instalado" "ERROR"
        Write-Status "Instala VS con 'Desktop development with C++'" "INFO"
        return
    }
    
    $script = Join-Path $CompiladoresPath "compilador_windows.ps1"
    if (Test-Path $script) {
        & $script
    } else {
        Write-Status "No se encontro: $script" "ERROR"
    }
}

function Compile-Linux {
    Write-Status "=== COMPILANDO LINUX ===" "INFO"
    
    $script = Join-Path $CompiladoresPath "compilador_linux.ps1"
    if (Test-Path $script) {
        & $script
    } else {
        Write-Status "No se encontro: $script" "ERROR"
    }
}

function Compile-All {
    Write-Status "=== COMPILANDO TODAS LAS PLATAFORMAS ===" "OK"
    
    # Android APK
    Write-Status "`n[1/4] Android APK..." "INFO"
    Compile-Android "apk"
    
    # Android AAB
    Write-Status "`n[2/4] Android AAB..." "INFO"
    Compile-Android "aab"
    
    # Windows
    Write-Status "`n[3/4] Windows..." "INFO"
    Compile-Windows
    
    # Linux
    Write-Status "`n[4/4] Linux..." "INFO"
    Compile-Linux
    
    Write-Status "`n=== TODAS LAS COMPILACIONES COMPLETADAS ===" "OK"
}

# ============================================
# SCRIPT PRINCIPAL
# ============================================

Clear-Host
Write-Status "ISLA DIGITAL - Iniciando Compilador..." "INFO"

# Verificar Flutter primero
if (-not (Test-Flutter)) {
    Write-Status "No se puede continuar sin Flutter" "ERROR"
    Read-Host "Presiona ENTER para salir"
    exit 1
}

# Mostrar menu
Show-Menu
$choice = Read-Host "Selecciona una opcion (0-5)"

switch ($choice) {
    "1" { Compile-Android "apk" }
    "2" { Compile-Android "aab" }
    "3" { Compile-Windows }
    "4" { Compile-Linux }
    "5" { Compile-All }
    "0" { 
        Write-Status "Saliendo..." "INFO"
        exit 0 
    }
    default { 
        Write-Status "Opcion no valida: $choice" "ERROR"
    }
}

Write-Status "`nProceso completado" "OK"
Read-Host "Presiona ENTER para salir"
