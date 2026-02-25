@echo off
setlocal EnableDelayedExpansion

title Isla Digital - Compilador

:: ============================================
:: ISLA DIGITAL - Compilador Multiplataforma
:: ============================================
set "PROJECT_PATH=%~dp0"
set "PLATFORM=%~1"
if "%~1"=="" set "PLATFORM=menu"

echo.
echo ========================================
echo   ISLA DIGITAL - COMPILADOR
echo ========================================
echo.

:: Verificar Flutter
echo [*] Verificando Flutter...
where flutter >nul 2>nul
if %errorlevel% neq 0 (
    echo [!] Buscando Flutter...
    set "FOUND=0"
    for %%F in (
        "C:\flutter\bin\flutter.bat"
        "C:\Program Files\flutter\bin\flutter.bat"
        "%USERPROFILE%\flutter\bin\flutter.bat"
    ) do (
        if exist %%F (
            set "FPATH=%%~dpF"
            set "FOUND=1"
            set "PATH=!FPATH!;%PATH%"
            echo [OK] Flutter encontrado
            goto :foundflutter
        )
    )
    :foundflutter
    if !FOUND! equ 0 (
        echo [ERROR] Flutter no encontrado
        pause
        exit /b 1
    )
)
echo [OK] Flutter listo

:: Verificar Android SDK
echo.
echo [*] Verificando Android SDK...
if not defined ANDROID_SDK_ROOT (
    if not defined ANDROID_HOME (
        echo [!] Buscando Android SDK...
        for %%S in (
            "%LOCALAPPDATA%\Android\Sdk"
            "%USERPROFILE%\AppData\Local\Android\Sdk"
        ) do (
            if exist "%%S\platform-tools\adb.exe" (
                set "ANDROID_SDK_ROOT=%%S"
                echo [OK] Android SDK encontrado
                goto :foundsdk
            )
        )
        echo [ERROR] Android SDK no encontrado
        pause
        exit /b 1
    ) else (
        set "ANDROID_SDK_ROOT=%ANDROID_HOME%"
    )
)
:foundsdk
echo [OK] Android SDK listo

:: Instalar dependencias
echo.
echo [*] Instalando dependencias...
cd /d "%PROJECT_PATH%"
flutter pub get
if %errorlevel% neq 0 (
    echo [ERROR] Error en flutter pub get
    pause
    exit /b 1
)
echo [OK] Dependencias instaladas

:: Menu o plataforma directa
if /I "%PLATFORM%"=="menu" goto :showmenu
if /I "%PLATFORM%"=="apk" goto :buildapk
if /I "%PLATFORM%"=="aab" goto :buildaab
if /I "%PLATFORM%"=="windows" goto :buildwindows
if /I "%PLATFORM%"=="linux" goto :buildlinux
if /I "%PLATFORM%"=="all" goto :buildall

echo [ERROR] Plataforma no reconocida: %PLATFORM%
goto :end

:showmenu
echo.
echo ========================================
echo   SELECCIONA PLATAFORMA
echo ========================================
echo  [1] Android APK
echo  [2] Android App Bundle (Play Store)
echo  [3] Windows EXE
echo  [4] Linux
echo  [5] Todas
echo  [0] Salir
echo ========================================
set /p choice="Opcion: "
if "%choice%"=="1" goto :buildapk
if "%choice%"=="2" goto :buildaab
if "%choice%"=="3" goto :buildwindows
if "%choice%"=="4" goto :buildlinux
if "%choice%"=="5" goto :buildall
if "%choice%"=="0" goto :end
echo Opcion invalida
pause
goto :showmenu

:buildapk
echo.
echo [*] Compilando Android APK...
flutter build apk --release
if %errorlevel% neq 0 (
    echo [ERROR] Fallo compilacion
    pause
    exit /b 1
)
echo [OK] APK Generado: build\app\outputs\flutter-apk\app-release.apk
goto :end

:buildaab
echo.
echo [*] Compilando Android App Bundle...
flutter build appbundle --release
if %errorlevel% neq 0 (
    echo [ERROR] Fallo compilacion
    pause
    exit /b 1
)
echo [OK] AAB Generado: build\app\outputs\bundle\release\app-release.aab
goto :end

:buildwindows
echo.
echo [*] Compilando Windows...
flutter config --enable-windows-desktop 2>nul
flutter build windows --release
if %errorlevel% neq 0 (
    echo [ERROR] Fallo compilacion
    pause
    exit /b 1
)
echo [OK] EXE Generado: build\windows\x64\runner\Release\Isla Digital.exe
goto :end

:buildlinux
echo.
echo [*] Compilando Linux (requiere WSL/Linux)...
flutter config --enable-linux-desktop 2>nul
flutter build linux --release
if %errorlevel% neq 0 (
    echo [ERROR] Fallo compilacion - necesitas WSL o Linux
    pause
    exit /b 1
)
echo [OK] Linux compilado: build/linux/x64/release/bundle/
goto :end

:buildall
echo.
echo [*] Compilando todas las plataformas...
echo === Android APK ===
flutter build apk --release && echo [OK] APK || echo [X] APK fallo
echo === Android AAB ===
flutter build appbundle --release && echo [OK] AAB || echo [X] AAB fallo
echo === Windows ===
flutter config --enable-windows-desktop 2>nul
flutter build windows --release && echo [OK] Windows || echo [X] Windows fallo
echo === Linux ===
flutter config --enable-linux-desktop 2>nul
flutter build linux --release && echo [OK] Linux || echo [X] Linux fallo
goto :end

:end
echo.
echo ========================================
echo   COMPILACION COMPLETADA
echo ========================================
pause
exit /b 0
