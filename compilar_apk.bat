@echo off
setlocal enabledelayedexpansion
:: Asegurar que el directorio de trabajo sea el del script
cd /d "%~dp0"
@echo off
title ISLA DIGITAL - SUITE PROFESIONAL v3.6

:MENU
cls
echo ========================================================
echo   ISLA DIGITAL - SISTEMA AVANZADO v3.6
echo ========================================================
echo [1] LIMPIEZA PROFUNDA
echo [2] COMPILAR APK DEBUG
echo [3] COMPILAR APK RELEASE
echo [4] INSTALAR APK (ADB)
echo [5] SALIR
echo ========================================================
echo.
set /p "choice=ELIJA UNA OPCION: "

if "%choice%"=="1" goto CLEAN
if "%choice%"=="2" set "BT=assembleDebug" && goto PROCESS
if "%choice%"=="3" set "BT=assembleRelease" && goto PROCESS
if "%choice%"=="4" goto INSTALL
if "%choice%"=="5" exit
goto MENU

:CLEAN
echo [ESTADO] Limpiando...
call gradlew.bat clean
pause
goto MENU

:PROCESS
echo [ESTADO] Compilando %BT%...
:: Usamos call directo para evitar que el script se cierre tras Gradle
call gradlew.bat %BT% --stacktrace
if %ERRORLEVEL% equ 0 (
    echo.
    echo [OK] EXITO. Archivos en: app\build\outputs\apk\
) else (
    echo [ERROR] Revisa el codigo de error arriba.
)
pause
goto MENU

:INSTALL
echo [ESTADO] Buscando APK para instalar...
set "APK_RELEASE=app\build\outputs\apk\release\app-release.apk"
set "APK_DEBUG=app\build\outputs\apk\debug\app-debug.apk"

if exist "%APK_RELEASE%" (
    echo [INFO] APK Release encontrado. Instalando...
    adb install -r "%APK_RELEASE%"
) else if exist "%APK_DEBUG%" (
    echo [INFO] APK Debug encontrado. Instalando...
    adb install -r "%APK_DEBUG%"
) else (
    echo [ERROR] No se encontro ningun APK. Compila primero con opcion [2] o [3].
)
pause
goto MENU