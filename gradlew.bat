@echo off
setlocal

set GRADLE_VERSION=8.10.2
set WRAPPER_DIR=.gradle\wrapper-custom
set DIST_DIR=%WRAPPER_DIR%\gradle-%GRADLE_VERSION%
set ZIP_FILE=%WRAPPER_DIR%\gradle-%GRADLE_VERSION%-bin.zip
set GRADLE_BIN=%DIST_DIR%\gradle-%GRADLE_VERSION%\bin\gradle.bat

if not exist "%GRADLE_BIN%" (
  if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"
  if not exist "%ZIP_FILE%" (
    echo Downloading Gradle %GRADLE_VERSION%...
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
      "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ZIP_FILE%'"
  )
  if not exist "%GRADLE_BIN%" (
    echo Extracting Gradle %GRADLE_VERSION%...
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
      "Expand-Archive -Path '%ZIP_FILE%' -DestinationPath '%DIST_DIR%' -Force"
  )
)

"%GRADLE_BIN%" %*
