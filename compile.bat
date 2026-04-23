@echo off
REM ╔══════════════════════════════════════════════════╗
REM   compile.bat — Script de Compilação (Windows)
REM   BST Visualizer — Árvore Binária de Busca
REM ╚══════════════════════════════════════════════════╝

echo.
echo Compilando BST Visualizer...
echo.

if exist out rmdir /s /q out
mkdir out

REM Coleta todos os arquivos .java e compila
for /r src %%f in (*.java) do echo %%f >> sources.txt
javac -sourcepath src -d out @sources.txt
del sources.txt

if %errorlevel% == 0 (
    echo.
    echo [OK] Compilacao concluida com sucesso!
    echo      Execute: run.bat
) else (
    echo.
    echo [ERRO] Falha na compilacao. Verifique os logs acima.
)
