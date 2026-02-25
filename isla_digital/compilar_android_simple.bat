@echo off
setlocal EnableDelayedExpansion

title Isla Digital - Compilador Android

:: ============================================
:: CONFIGURACION
:: ============================================
set "PROJECT_PATH=%~dp0"
set "BUILD_TYPE=%~1"
if "%~1"=="" set "BUILD_TYPE=apk"

echo.
echo ========================================
echo   ISLA DIGITAL - COMPILADOR ANDROID
echo ========================================
echo.

:: ============================================
:: PASO 1: Verificar Flutter
:: ============================================
echo [PASO 1/5] Verificando Flutter...

where flutter >nul 2>nul
if %errorlevel% neq 0 (
    echo [INFO] Flutter no en PATH, buscando...
    
    set "FLUTTER_FOUND=0"
    for %%F in (
        "C:\flutter\bin\flutter.bat"
        "C:\Program Files\flutter\bin\flutter.bat"
        "%USERPROFILE%\flutter\bin\flutter.bat"
    ) do (
        if exist %%F (
            set "FLUTTER_PATH=%%~dpF"
            set "FLUTTER_FOUND=1"
            set "PATH=!FLUTTER_PATH!;%PATH%"
            echo [OK] Flutter encontrado: %%~dpF
            goto :flutter_ok
        )
    )
    
    :flutter_ok
    if !FLUTTER_FOUND! equ 0 (
        echo [ERROR] Flutter no encontrado.
        echo Instala Flutter desde: https://flutter.dev
        pause
        exit /b 1
    )
)

for /f "tokens=*" %%a in ('flutter --version 2^>nul ^| findstr "Flutter"') do (
    echo [OK] %%a
)

:: ============================================
:: PASO 2: Verificar Android SDK
:: ============================================
echo.
echo [PASO 2/5] Verificando Android SDK...

if not defined ANDROID_SDK_ROOT (
    if not defined ANDROID_HOME (
        echo [INFO] Buscando Android SDK...
        
        for %%S in (
            "%LOCALAPPDATA%\Android\Sdk"
            "%USERPROFILE%\AppData\Local\Android\Sdk"
            "C:\Android\Sdk"
        ) do (
            if exist "%%S\platform-tools\adb.exe" (
                set "ANDROID_SDK_ROOT=%%S"
                echo [OK] Android SDK: %%S
                goto :sdk_ok
            )
        )
        
        echo [ERROR] Android SDK no encontrado.
        echo Instala Android Studio.
        pause
        exit /b 1
    ) else (
        set "ANDROID_SDK_ROOT=%ANDROID_HOME%"
    )
)

:sdk_ok
echo [OK] Android SDK configurado

:: ============================================
:: PASO 3: Dependencias
:: ============================================
echo.
echo [PASO 3/5] Instalando dependencias...

cd /d "%PROJECT_PATH%"

flutter pub get
if %errorlevel% neq 0 (
    echo [ERROR] Error en flutter pub get
    pause
    exit /b 1
)

echo [OK] Dependencias instaladas

:: ============================================
:: PASO 4: Assets
:: ============================================
echo.
echo [PASO 4/5] Verificando assets...

for %%D in ("assets\images" "assets\audio" "assets\animations" "assets\icons" "assets\fonts") do (
    if not exist "%%D" (
        mkdir "%%D" 2>nul
        echo [INFO] Creado: %%D
    )
)

echo [OK] Assets verificados

:: ============================================
:: PASO 5: Compilar
:: ============================================
echo.
echo [PASO 5/5] Compilando...
echo Esto puede tardar 5-15 minutos...
echo.

if /I "%BUILD_TYPE%"=="apk" (
    echo --- Compilando APK ---
    flutter build apk --release
    
    if %errorlevel% equ 0 (
        echo.
        echo ========================================
        echo [EXITO] APK Generado!
        echo ========================================
        echo Ubicacion:
        echo %PROJECT_PATH%build\app\outputs\flutter-apk\app-release.apk
        
        for %%F in ("%PROJECT_PATH%build\app\outputs\flutter-apk\app-release.apk") do (
            echo Tamano: %%~zF bytes
        )
    ) else (
        echo [ERROR] Fallo la compilacion
        pause
        exit /b 1
    )
    
) else if /I "%BUILD_TYPE%"=="aab" (
    echo --- Compilando App Bundle ---
    flutter build appbundle --release
    
    if %errorlevel% equ 0 (
        echo.
        echo ========================================
        echo [EXITO] AAB Generado!
        echo ========================================
        echo Ubicacion:
        echo %PROJECT_PATH%build\app\outputs\bundle\release\app-release.aab
    ) else (
        echo [ERROR] Fallo la compilacion
        pause
        exit /b 1
    )
    
) else if /I "%BUILD_TYPE%"=="all" (
    echo --- Compilando APK y AAB ---
    
    flutter build apk --release
    if %errorlevel% equ 0 (
        echo [OK] APK compilado
    ) else (
        echo [ERROR] Fallo APK
    )
    
    flutter build appbundle --release
    if %errorlevel% equ 0 (
        echo [OK] AAB compilado
    ) else (
        echo [ERROR] Fallo AAB
    )
    
) else (
    echo [ERROR] Tipo no reconocido: %BUILD_TYPE%
    echo Uso: %~nx0 [apk^|aab^|all]
    pause
    exit /b 1
)

echo.
echo ========================================
echo    COMPILACION COMPLETADA
echo ========================================
echo.

pause
exit /b 0
