@echo off
setlocal enabledelayedexpansion
title ISLA DIGITAL - SUITE DE COMPILACION PROFESIONAL v3.5

:: ========================================================
:: CONFIGURACION Y REQUISITOS
:: ========================================================
:INIT
set "LOG_FILE=compilacion_reporte.log"
set "APK_DEBUG=app\build\outputs\apk\debug\app-debug.apk"
set "APK_RELEASE=app\build\outputs\apk\release\app-release-unsigned.apk"
set "AAB_RELEASE=app\build\outputs\bundle\release\app-release.aab"
set "G_ARGS=--no-daemon --stacktrace --warning-mode all"

:MENU
cls
echo ========================================================
echo   ISLA DIGITAL - SISTEMA AVANZADO DE APK v3.5
echo   (VERSION SIN ICONOS - SOLO TEXTO)
echo ========================================================
echo.
echo [0]  SINCRONIZAR Y ACTUALIZAR DEPENDENCIAS (Forzar descarga)
echo [1]  REPARAR Y LIMPIAR PROYECTO (Regenerar carpetas y cache)
echo [2]  COMPILAR APK DEBUG   (Rapido, para pruebas)
echo [3]  COMPILAR APK RELEASE (Final, optimizado)
echo [4]  COMPILAR AAB BUNDLE  (Para Play Store)
echo [5]  COMPILAR TODO (Debug + Release + Bundle)
echo [6]  INSTALAR APK DEBUG   (Requiere ADB conectado)
echo [7]  CARPETAS DE SALIDA (Abrir explorador)
echo [8]  ANALISAR CODIGO (Lint / Checks)
echo [9]  EJECUTAR TESTS (Unit Tests)
echo [10] REPORTE DE DEPENDENCIAS
echo [11] GESTION DE ADB (Reiniciar y Listar)
echo [12] VER LOG DE COMPILACION (Notepad)
echo [13] DETENER GRADLE DAEMONS
echo [14] SALIR
echo.
echo ========================================================
if exist %LOG_FILE% (
    echo [TIP] Ultimo registro: %LOG_FILE%
)
echo.

set /p choice="ELIJA UNA OPCION (0-14): "

if "%choice%"=="0" set BUILD_TYPE=dependencies --refresh-dependencies && goto PROCESS_SIMPLE
if "%choice%"=="1" goto CLEAN
if "%choice%"=="2" set BUILD_TYPE=assembleDebug && set APK_TARGET=%APK_DEBUG% && goto PROCESS
if "%choice%"=="3" set BUILD_TYPE=assembleRelease && set APK_TARGET=%APK_RELEASE% && goto PROCESS
if "%choice%"=="4" set BUILD_TYPE=bundleRelease && set APK_TARGET=%AAB_RELEASE% && goto PROCESS
if "%choice%"=="5" set BUILD_TYPE=assembleDebug assembleRelease bundleRelease && set APK_TARGET=Multiples archivos && goto PROCESS
if "%choice%"=="6" goto INSTALL_ADB
if "%choice%"=="7" start "" "app\build\outputs\" && goto MENU
if "%choice%"=="8" set BUILD_TYPE=lint && goto PROCESS_SIMPLE
if "%choice%"=="9" set BUILD_TYPE=test && goto PROCESS_SIMPLE
if "%choice%"=="10" set BUILD_TYPE=dependencies && goto PROCESS_SIMPLE
if "%choice%"=="11" goto ADB_MENU
if "%choice%"=="12" if exist %LOG_FILE% (start notepad %LOG_FILE% & goto MENU) else (echo No hay log. & pause & goto MENU)
if "%choice%"=="13" call gradlew.bat --stop & pause & goto MENU
if "%choice%"=="14" exit
goto MENU

:ADB_MENU
cls
echo ========================================================
echo   GESTION DE DISPOSITIVOS (ADB)
echo ========================================================
echo.
adb devices
echo.
echo [1] Reiniciar ADB Server
echo [2] Volver al Menu Principal
echo.
set /p adb_choice="Opcion: "
if "%adb_choice%"=="1" (
    adb kill-server
    adb start-server
    adb devices
    pause
)
goto MENU

:CLEAN
echo.
echo ========================================================
echo   [ESTADO] INICIANDO MANTENIMIENTO INTEGRAL
echo ========================================================
echo.
echo [1/4] Borrando caches y archivos temporales...
if exist ".gradle" rd /s /q .gradle
if exist "app\build" rd /s /q app\build
if exist "build" rd /s /q build
if exist "app\src\main\assets" (echo [INFO] Carpeta Assets detectada.) else (md app\src\main\assets)

echo [2/4] Regenerando estructura de directorios criticos (res/src)...
:: Verificacion y creacion de carpetas basicas si faltan
set "RES_PATH=app\src\main\res"
for %%d in (anim color drawable layout menu navigation raw values xml mipmap-anydpi mipmap-hdpi mipmap-mdpi mipmap-xhdpi mipmap-xxhdpi mipmap-xxxhdpi) do (
    if not exist "%RES_PATH%\%%d" (
        echo [REPARAR] Creando: %%d
        md "%RES_PATH%\%%d"
    )
)

:: Asegurar carpetas de codigo
if not exist "app\src\main\java" md app\src\main\java
if not exist "app\src\test\java" md app\src\test\java
if not exist "app\src\androidTest\java" md app\src\androidTest\java

echo [3/4] Ejecutando gradlew clean y purgado de cache...
call gradlew.bat clean %G_ARGS% > %LOG_FILE% 2>&1
call gradlew.bat --stop >nul 2>&1

if !ERRORLEVEL! equ 0 (
    echo [4/4] [OK] Sistema reparado y limpio.
) else (
    echo [ERROR] Fallo en la limpieza de Gradle. Revisar %LOG_FILE%
)
pause
goto MENU

:PROCESS_SIMPLE
echo.
echo [ESTADO] Ejecutando: !BUILD_TYPE!...
call gradlew.bat !BUILD_TYPE! %G_ARGS% > %LOG_FILE% 2>&1
if !ERRORLEVEL! equ 0 (
    echo [OK] Tarea finalizada.
) else (
    echo [ERROR] Tarea fallida. Revisar %LOG_FILE%
)
pause
goto MENU

:PROCESS
echo.
echo [ESTADO] Verificando entorno...
java -version >nul 2>&1
if !ERRORLEVEL! neq 0 (
    echo [ERROR] Java no detectado. Instale JDK 17 o superior.
    pause
    goto MENU
)

echo [ESTADO] Compilando: !BUILD_TYPE!...
echo [INFO] Por favor espere...
echo.

:: Corregimos el error de 'tee' usando PowerShell de forma robusta
:: Intentamos usar PowerShell de forma segura. Si falla, cae al comando normal sin 'tee'
powershell -Command "try { .\gradlew.bat !BUILD_TYPE! %G_ARGS% --info 2>&1 | Tee-Object -FilePath %LOG_FILE% } catch { throw }" 2>nul
if !ERRORLEVEL! neq 0 (
    echo [INFO] PowerShell not available, using standard output to log...
    call gradlew.bat !BUILD_TYPE! %G_ARGS% --info > %LOG_FILE% 2>&1
)

if !ERRORLEVEL! equ 0 (
    echo.
    echo ========================================================
    echo   [OK] PROCESO TERMINADO CON EXITO
    echo ========================================================
    echo DESTINO: !APK_TARGET!
    echo.
    echo ¿Abrir archivos generados? (S/N)
    set /p op_open="> "
    if /i "!op_open!"=="S" start "" "app\build\outputs\"
    goto MENU
) else (
    goto ERROR_DETALLADO
)

:INSTALL_ADB
echo.
echo [ESTADO] Verificando APK Debug...
if not exist %APK_DEBUG% (
    echo [ERROR] No se encuentra app-debug.apk. Compila primero (Opcion 2).
    pause
    goto MENU
)
echo [ESTADO] Instalando en el dispositivo...
adb install -r %APK_DEBUG%
if !ERRORLEVEL! equ 0 (
    echo [OK] Aplicacion instalada y actualizada.
) else (
    echo [ERROR] Error en la instalacion ADB.
)
pause
goto MENU

:ERROR_DETALLADO
echo.
echo ========================================================
echo   [ERROR] SE DETECTARON FALLOS EN LA COMPILACION
echo ========================================================
echo.
echo El reporte detallado esta en: %LOG_FILE%
echo.
echo Ultimos 20 lineas del registro:
echo --------------------------------------------------------
powershell -command "if (Test-Path %LOG_FILE%) { Get-Content %LOG_FILE% -Tail 20 } else { echo 'Archivo de log no encontrado' }" 2>nul
if !ERRORLEVEL! neq 0 (
    echo No PowerShell. Showing last lines with 'type':
    findstr /n "^" %LOG_FILE% | more
)
echo --------------------------------------------------------
echo.
echo Consejo: Ejecute la opcion [1] Limpieza Profunda si es un error de cache.
pause
goto MENU
