#Requires -Version 5.1
<#
.SYNOPSIS
    Isla Digital - Compilador Multiplataforma
.DESCRIPTION
    Script para compilar Isla Digital para Android, Windows y Linux
.NOTES
    Version: 3.0
#>

param(
    [string]$Plataforma = "",
    [string]$AndroidTipo = "apk",
    [switch]$SkipAnalyze,
    [switch]$Ayuda
)

# Configuracion
$script:ProjectPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$script:LogFile = Join-Path $script:ProjectPath ("compilacion_" + (Get-Date -Format 'yyyyMMdd_HHmmss') + ".log")

# Colores
$Colors = @{
    Success = "Green"
    Error = "Red"
    Warning = "Yellow"
    Info = "Cyan"
    Header = "Magenta"
    Menu = "Blue"
}

function Write-ColorOutput {
    param([string]$Message, [string]$Color = "White")
    Write-Host $Message -ForegroundColor $Colors[$Color]
}

function Write-Log {
    param([string]$Message, [string]$Level = "INFO")
    $timestamp = Get-Date -Format "HH:mm:ss"
    $logEntry = "[$timestamp] [$Level] $Message"
    Add-Content -Path $script:LogFile -Value $logEntry -ErrorAction SilentlyContinue
    
    $color = switch ($Level) {
        "SUCCESS" { "Success" }
        "WARNING" { "Warning" }
        "ERROR" { "Error" }
        default { "Info" }
    }
    Write-ColorOutput -Message $Message -Color $color
}

function Show-Header {
    Clear-Host
    Write-ColorOutput "========================================" "Header"
    Write-ColorOutput "    ISLA DIGITAL - COMPILADOR" "Header"
    Write-ColorOutput "      MULTIPLATAFORMA v3.0" "Header"
    Write-ColorOutput "========================================" "Header"
    Write-ColorOutput "" "White"
    Write-Log "Iniciando compilador" "INFO"
}

function Show-Menu {
    Write-ColorOutput "========================================" "Menu"
    Write-ColorOutput "    SELECCIONA PLATAFORMA" "Menu"
    Write-ColorOutput "========================================" "Menu"
    Write-ColorOutput "" "White"
    Write-ColorOutput "  [1] Android (APK)" "Info"
    Write-ColorOutput "  [2] Android (App Bundle - Play Store)" "Info"
    Write-ColorOutput "  [3] Android (Ambos)" "Info"
    Write-ColorOutput "  [4] Windows (EXE)" "Info"
    Write-ColorOutput "  [5] Linux" "Info"
    Write-ColorOutput "  [6] Todas las Plataformas" "Info"
    Write-ColorOutput "  [0] Salir" "Error"
    Write-ColorOutput "" "White"
    Write-ColorOutput "========================================" "Menu"
}

function Test-FlutterInstalled {
    Write-Log "Verificando Flutter..." "INFO"
    
    $flutterCmd = Get-Command "flutter" -ErrorAction SilentlyContinue
    
    if (-not $flutterCmd) {
        Write-Log "Flutter no encontrado en PATH, buscando..." "WARNING"
        
        $possiblePaths = @(
            "C:\flutter\bin\flutter.bat",
            "C:\Program Files\flutter\bin\flutter.bat",
            "$env:USERPROFILE\flutter\bin\flutter.bat",
            "$env:LOCALAPPDATA\flutter\bin\flutter.bat"
        )
        
        $found = $false
        foreach ($path in $possiblePaths) {
            if (Test-Path $path) {
                $dir = [System.IO.Path]::GetDirectoryName($path)
                $env:PATH = "$dir;$env:PATH"
                Write-Log "Flutter encontrado en: $path" "SUCCESS"
                $found = $true
                break
            }
        }
        
        if (-not $found) {
            Write-Log "Flutter no encontrado. Instala desde: https://flutter.dev" "ERROR"
            return $false
        }
    }
    
    $version = & flutter --version 2>&1 | Select-Object -First 1
    Write-Log "Flutter detectado: $version" "SUCCESS"
    return $true
}

function Test-AndroidSDK {
    Write-Log "Verificando Android SDK..." "INFO"
    
    if (-not $env:ANDROID_SDK_ROOT -and -not $env:ANDROID_HOME) {
        Write-Log "Buscando Android SDK..." "WARNING"
        
        $sdkPaths = @(
            "$env:LOCALAPPDATA\Android\Sdk",
            "$env:USERPROFILE\AppData\Local\Android\Sdk",
            "C:\Android\Sdk"
        )
        
        foreach ($path in $sdkPaths) {
            if (Test-Path "$path\platform-tools\adb.exe") {
                $env:ANDROID_SDK_ROOT = $path
                Write-Log "Android SDK encontrado: $path" "SUCCESS"
                return $true
            }
        }
        
        Write-Log "Android SDK no encontrado. Instala Android Studio." "ERROR"
        return $false
    }
    
    Write-Log "Android SDK: $($env:ANDROID_SDK_ROOT)" "SUCCESS"
    return $true
}

function Test-VisualStudio {
    Write-Log "Verificando Visual Studio..." "INFO"
    
    $vsWhere = "${env:ProgramFiles(x86)}\Microsoft Visual Studio\Installer\vswhere.exe"
    
    if (Test-Path $vsWhere) {
        try {
            $vsPath = & $vsWhere -latest -products * -requires Microsoft.VisualStudio.Component.VC.Tools.x86.x64 -property installationPath 2>$null
            if ($vsPath) {
                Write-Log "Visual Studio encontrado: $vsPath" "SUCCESS"
                return $true
            }
        } catch {
            Write-Log "Error al buscar Visual Studio" "WARNING"
        }
    }
    
    Write-Log "Visual Studio no encontrado. Necesario para Windows." "WARNING"
    Write-Log "Descarga desde: https://visualstudio.microsoft.com/downloads/" "INFO"
    return $false
}

function Install-Dependencies {
    Write-Log "=== Instalando Dependencias ===" "INFO"
    
    Set-Location $script:ProjectPath
    
    $result = & flutter pub get 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Log "Error en flutter pub get" "ERROR"
        return $false
    }
    
    Write-Log "Dependencias instaladas" "SUCCESS"
    return $true
}

function Test-Assets {
    Write-Log "=== Verificando Assets ===" "INFO"
    
    $assetDirs = @("assets\images", "assets\audio", "assets\animations", "assets\icons", "assets\fonts")
    
    foreach ($dir in $assetDirs) {
        $fullPath = Join-Path $script:ProjectPath $dir
        if (-not (Test-Path $fullPath)) {
            New-Item -ItemType Directory -Path $fullPath -Force | Out-Null
            Write-Log "Creado: $dir" "WARNING"
        }
    }
    
    Write-Log "Assets verificados" "SUCCESS"
}

function Test-CodeQuality {
    if ($SkipAnalyze) {
        Write-Log "Analisis omitido" "INFO"
        return $true
    }
    
    Write-Log "=== Analisis de Codigo ===" "INFO"
    
    $result = & flutter analyze --suppress-analytics 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Log "Analisis completado sin errores" "SUCCESS"
    } else {
        Write-Log "Analisis encontro problemas (continuando...)" "WARNING"
    }
    return $true
}

function Build-AndroidAPK {
    Write-Log "=== Compilando Android APK ===" "INFO"
    Write-Log "Esto puede tardar 5-15 minutos..." "INFO"
    
    if (-not (Test-AndroidSDK)) {
        Write-Log "Android SDK no disponible" "ERROR"
        return $false
    }
    
    Set-Location $script:ProjectPath
    
    & flutter build apk --release
    if ($LASTEXITCODE -ne 0) {
        Write-Log "Error al compilar APK" "ERROR"
        return $false
    }
    
    $apkPath = Join-Path $script:ProjectPath "build\app\outputs\flutter-apk\app-release.apk"
    if (Test-Path $apkPath) {
        $size = (Get-Item $apkPath).Length / 1MB
        Write-Log "APK Generado!" "SUCCESS"
        Write-Log "  Ubicacion: $apkPath" "INFO"
        Write-Log ("  Tamano: " + ([math]::Round($size, 2)) + " MB") "INFO"
        return $true
    }
    return $false
}

function Build-AndroidAAB {
    Write-Log "=== Compilando Android App Bundle ===" "INFO"
    Write-Log "Esto puede tardar 5-15 minutos..." "INFO"
    
    if (-not (Test-AndroidSDK)) {
        Write-Log "Android SDK no disponible" "ERROR"
        return $false
    }
    
    Set-Location $script:ProjectPath
    
    & flutter build appbundle --release
    if ($LASTEXITCODE -ne 0) {
        Write-Log "Error al compilar AAB" "ERROR"
        return $false
    }
    
    $aabPath = Join-Path $script:ProjectPath "build\app\outputs\bundle\release\app-release.aab"
    if (Test-Path $aabPath) {
        $size = (Get-Item $aabPath).Length / 1MB
        Write-Log "AAB Generado!" "SUCCESS"
        Write-Log "  Ubicacion: $aabPath" "INFO"
        Write-Log ("  Tamano: " + ([math]::Round($size, 2)) + " MB") "INFO"
        return $true
    }
    return $false
}

function Build-Windows {
    Write-Log "=== Compilando Windows ===" "INFO"
    
    if (-not (Test-VisualStudio)) {
        Write-Log "Visual Studio no encontrado. Saltando Windows." "WARNING"
        return $false
    }
    
    Set-Location $script:ProjectPath
    
    & flutter config --enable-windows-desktop | Out-Null
    
    & flutter build windows --release
    if ($LASTEXITCODE -ne 0) {
        Write-Log "Error al compilar Windows" "ERROR"
        return $false
    }
    
    $exePath = Join-Path $script:ProjectPath "build\windows\x64\runner\Release\Isla Digital.exe"
    if (Test-Path $exePath) {
        $size = (Get-Item $exePath).Length / 1MB
        Write-Log "Windows EXE Generado!" "SUCCESS"
        Write-Log "  Ubicacion: $exePath" "INFO"
        Write-Log ("  Tamano: " + ([math]::Round($size, 2)) + " MB") "INFO"
        return $true
    }
    return $false
}

function Build-Linux {
    Write-Log "=== Compilando Linux ===" "INFO"
    Write-Log "Nota: Requiere Linux o WSL" "WARNING"
    
    $isLinux = $false
    
    try {
        $uname = & uname -r 2>$null
        if ($uname -match "microsoft" -or $uname -match "WSL" -or $uname) {
            $isLinux = $true
        }
    } catch {
        # No estamos en Linux
    }
    
    if (-not $isLinux) {
        Write-Log "No estas en Linux o WSL. No se puede compilar para Linux." "ERROR"
        Write-Log "Opciones: Usar WSL o compilar en maquina Linux" "INFO"
        return $false
    }
    
    Set-Location $script:ProjectPath
    
    & flutter config --enable-linux-desktop | Out-Null
    
    & flutter build linux --release
    if ($LASTEXITCODE -ne 0) {
        Write-Log "Error al compilar Linux" "ERROR"
        return $false
    }
    
    Write-Log "Linux compilado exitosamente!" "SUCCESS"
    Write-Log "  Ubicacion: build/linux/x64/release/bundle/" "INFO"
    return $true
}

function Build-All {
    Write-Log "=== Compilando TODAS las Plataformas ===" "INFO"
    
    $apkOk = Build-AndroidAPK
    $aabOk = Build-AndroidAAB
    $winOk = Build-Windows
    $linOk = Build-Linux
    
    Write-Log "=== RESUMEN ===" "INFO"
    Write-Log ("Android APK: " + (&{if($apkOk){"OK"}else{"FALLO"}})) (&{if($apkOk){"SUCCESS"}else{"ERROR"}})
    Write-Log ("Android AAB: " + (&{if($aabOk){"OK"}else{"FALLO"}})) (&{if($aabOk){"SUCCESS"}else{"ERROR"}})
    Write-Log ("Windows: " + (&{if($winOk){"OK"}else{"FALLO"}})) (&{if($winOk){"SUCCESS"}else{"ERROR"}})
    Write-Log ("Linux: " + (&{if($linOk){"OK"}else{"FALLO"}})) (&{if($linOk){"SUCCESS"}else{"ERROR"}})
}

# ============================================
# SCRIPT PRINCIPAL
# ============================================

if ($Ayuda) {
    Write-Host @"
USO: .\compilar_isla_digital.ps1 [opciones]

OPCIONES:
    -Plataforma <android|windows|linux|all>    Seleccionar plataforma
    -AndroidTipo <apk|aab|all>                Tipo Android
    -SkipAnalyze                               Omitir analisis
    -Ayuda                                     Mostrar ayuda

EJEMPLOS:
    .\compilar_isla_digital.ps1                           # Menu interactivo
    .\compilar_isla_digital.ps1 -Plataforma android       # APK Android
    .\compilar_isla_digital.ps1 -Plataforma windows       # Windows EXE
    .\compilar_isla_digital.ps1 -Plataforma all           # Todas
"@
    exit 0
}

Show-Header

if (-not (Test-FlutterInstalled)) {
    Write-Log "Flutter no esta instalado correctamente. Abortando." "ERROR"
    exit 1
}

# Determinar opcion
if ($Plataforma) {
    switch ($Plataforma.ToLower()) {
        "android" { 
            if ($AndroidTipo -eq "aab") { $choice = "2" }
            elseif ($AndroidTipo -eq "all") { $choice = "3" }
            else { $choice = "1" }
        }
        "windows" { $choice = "4" }
        "linux" { $choice = "5" }
        "all" { $choice = "6" }
        default { $choice = $null }
    }
} else {
    Show-Menu
    $choice = Read-Host "Selecciona una opcion (0-6)"
}

# Instalar dependencias
Install-Dependencies | Out-Null
Test-Assets
Test-CodeQuality | Out-Null

# Procesar seleccion
switch ($choice) {
    "1" { Build-AndroidAPK }
    "2" { Build-AndroidAAB }
    "3" { 
        Build-AndroidAPK
        Build-AndroidAAB
    }
    "4" { Build-Windows }
    "5" { Build-Linux }
    "6" { Build-All }
    "0" { 
        Write-Log "Saliendo..." "INFO"
        exit 0 
    }
    default { 
        Write-Log "Opcion no valida: $choice" "ERROR"
        exit 1 
    }
}

Write-ColorOutput "" "White"
Write-ColorOutput "========================================" "Success"
Write-ColorOutput "    COMPILACION COMPLETADA" "Success"
Write-ColorOutput "========================================" "Success"
Write-ColorOutput ("Log guardado en: " + $script:LogFile) "Info"
Write-ColorOutput "" "White"

if (-not $Plataforma) {
    Read-Host "Presiona ENTER para salir"
}