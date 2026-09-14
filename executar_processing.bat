@echo off
setlocal
cd /d "%~dp0"

where java >nul 2>nul
if errorlevel 1 (
  echo Java nao foi encontrado. Instale um JDK e tente novamente.
  pause
  exit /b 1
)
where javac >nul 2>nul
if errorlevel 1 (
  echo javac nao foi encontrado. E necessario um JDK, nao apenas o Java Runtime.
  pause
  exit /b 1
)

if not exist "lib\processing-core.jar" (
  call baixar_processing.bat
  if errorlevel 1 exit /b 1
)

if exist out rmdir /s /q out
mkdir out

echo Compilando o projeto...
javac -encoding UTF-8 -cp "lib\processing-core.jar" -d out *.java
if errorlevel 1 (
  echo.
  echo Houve um erro na compilacao.
  pause
  exit /b 1
)

echo Iniciando o jogo...
java -cp "out;lib\processing-core.jar" Main
if errorlevel 1 pause
