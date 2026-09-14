@echo off
setlocal
cd /d "%~dp0"
if not exist lib mkdir lib
if exist "lib\processing-core.jar" (
  echo Processing Core ja esta instalado.
  exit /b 0
)
echo Baixando Processing Core 4.3.1...
powershell -NoProfile -ExecutionPolicy Bypass -Command "try { Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/processing/core/4.3.1/core-4.3.1.jar' -OutFile 'lib\processing-core.jar' -UseBasicParsing } catch { Write-Host $_.Exception.Message; exit 1 }"
if errorlevel 1 (
  echo.
  echo Nao foi possivel baixar a biblioteca Processing.
  echo Verifique sua internet e tente novamente.
  pause
  exit /b 1
)
echo Processing instalado com sucesso.
