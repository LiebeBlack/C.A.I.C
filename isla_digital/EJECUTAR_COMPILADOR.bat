@echo off
:: ============================================
:: ISLA DIGITAL - Ejecutar Compilador
:: ============================================
:: Este archivo abre PowerShell y ejecuta el compilador unificado
:: Ubicacion: carpeta compiladores/
:: ============================================

cd /d "%~dp0"

echo ========================================
echo   ISLA DIGITAL - COMPILADOR
echo ========================================
echo.
echo Iniciando compilador...
echo.

:: Verificar que existe el script
if not exist "compiladores\compilador_todos.ps1" (
    echo [ERROR] No se encontro compiladores\compilador_todos.ps1
    echo.
    echo Verifica que la carpeta 'compiladores' existe con los scripts.
    pause
    exit /b 1
)

:: Ejecutar PowerShell con el compilador
powershell -ExecutionPolicy Bypass -File "compiladores\compilador_todos.ps1"

:: Pausa final (por si acaso)
echo.
echo ========================================
echo   Proceso terminado
echo ========================================
pause
