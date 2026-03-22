@echo off
setlocal enabledelayedexpansion
title Preparacion de Android SDK e Isla Digital
echo ==============================================================
echo        INSTALACION DESATENDIDA DE ANDROID SDK Y COMPILACION
echo ==============================================================
echo.

set "PROJECT_DIR=%~dp0"
cd /d "%PROJECT_DIR%"

:: 1. Conectar Java Adoptium que ya tienes instalado
echo [INFO] Buscando instalacion de Eclipse Adoptium Java...
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk*") do (
    set "JAVA_HOME=%%D"
)

if not defined JAVA_HOME (
    echo [ERROR] No se pudo encontrar Eclipse Adoptium en Archivos de Programa.
    echo Asegurate de tenerlo bien instalado.
    pause
    exit /b 1
)

echo [INFO] JAVA_HOME configurado exitosamente a:
echo %JAVA_HOME%
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: 2. Variables para Android SDK
set "ANDROID_SDK_ROOT=%LOCALAPPDATA%\Android\Sdk"

if not exist "%ANDROID_SDK_ROOT%" (
    echo [ERROR] No se pudo encontrar el SDK de Android en %ANDROID_SDK_ROOT%.
    pause
    exit /b 1
)

echo [INFO] Android SDK detectado en %ANDROID_SDK_ROOT%.

:: 3. Escribimos el SDK Location en `local.properties` para que Gradle lo asimile directamente
echo sdk.dir=%ANDROID_SDK_ROOT:\=\\%> local.properties

:: 5. Ensamblar la app con Gradle usando Java y el nuevo SDK
echo.
echo ==============================================================
echo [INFO] Ensamblando Isla Digital con Gradle...
echo ==============================================================
call gradlew.bat clean assembleDebug

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Falló la compilación de la aplicacion. Revisa los errores.
    pause
    exit /b 1
)

echo.
echo ==============================================================
echo [EXITO] ¡El SDK fue preparado y tu App compilo perfectamente!
echo APK Disponible en:
echo %PROJECT_DIR%app\build\outputs\apk\debug\app-debug.apk
echo ==============================================================
echo.
pause > nul
