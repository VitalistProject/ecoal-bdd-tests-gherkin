@echo off
setlocal

cd /d "%~dp0.."

echo ================================
echo VERIFICANDO JAVA...
echo ================================

if not defined JAVA_HOME (
    echo ERRO: JAVA_HOME nao esta configurado.
    echo Instale o JDK 21 e configure JAVA_HOME.
    pause
    exit /b 1
)

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERRO: Java nao encontrado em JAVA_HOME.
    pause
    exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"

java -version

echo.
echo ================================
echo EXECUTANDO TESTES BDD...
echo ================================

call mvn clean test

set "RESULTADO=%ERRORLEVEL%"

echo.
echo ================================
if "%RESULTADO%"=="0" (
    echo TESTES EXECUTADOS COM SUCESSO!
) else (
    echo FALHA NA EXECUCAO DOS TESTES.
)
echo ================================

pause
exit /b %RESULTADO%