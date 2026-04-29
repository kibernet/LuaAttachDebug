@echo off
setlocal EnableExtensions
set "ROOT_DIR=%~dp0"
set "ROOT_DIR=%ROOT_DIR:~0,-1%"
set "INTELLIJ_DIR=%ROOT_DIR%\intellij-plugin"
set "VSCODE_DIR=%ROOT_DIR%\vscode-plugin"
call :main
set "BUILD_CODE=%ERRORLEVEL%"
if not "%BUILD_CODE%"=="0" (
    echo.
    echo ========================================
    echo Build failed with exit code %BUILD_CODE%.
    echo ========================================
    pause
)
exit /b %BUILD_CODE%
:main
call :ensure_java || exit /b %ERRORLEVEL%
echo ========================================
echo Building intellij-plugin...
echo JAVA_HOME=%JAVA_HOME%
echo ========================================
pushd "%INTELLIJ_DIR%" || exit /b 1
if exist build\distributions\*.zip del /q build\distributions\*.zip
call gradlew.bat --no-daemon --console=plain clean buildPlugin
set "BUILD_CODE=%ERRORLEVEL%"
popd
if not "%BUILD_CODE%"=="0" exit /b %BUILD_CODE%
echo.
echo ========================================
echo Building vscode-plugin...
echo ========================================
pushd "%VSCODE_DIR%" || exit /b 1
if exist out rmdir /s /q out
if exist dist rmdir /s /q dist
if exist *.tsbuildinfo del /q *.tsbuildinfo
if exist *.vsix del /q *.vsix
call :ensure_node
if errorlevel 1 (set "BUILD_CODE=%ERRORLEVEL%" & popd & exit /b %BUILD_CODE%)
where npm >nul 2>nul
if errorlevel 1 (popd & echo [ERROR] npm was not found in PATH. & exit /b 1)
if exist package-lock.json (call npm ci --no-audit --fund=false --loglevel=error) else (call npm install --no-audit --fund=false --loglevel=error)
set "BUILD_CODE=%ERRORLEVEL%"
if not "%BUILD_CODE%"=="0" (popd & exit /b %BUILD_CODE%)
call npm run compile
set "BUILD_CODE=%ERRORLEVEL%"
if not "%BUILD_CODE%"=="0" (popd & exit /b %BUILD_CODE%)
set "OLD_NODE_OPTIONS=%NODE_OPTIONS%"
if defined NODE_OPTIONS (set "NODE_OPTIONS=--no-deprecation %NODE_OPTIONS%") else (set "NODE_OPTIONS=--no-deprecation")
call npm run package:vsix
set "BUILD_CODE=%ERRORLEVEL%"
set "NODE_OPTIONS=%OLD_NODE_OPTIONS%"
if not "%BUILD_CODE%"=="0" (popd & exit /b %BUILD_CODE%)
popd
echo.
echo ========================================
echo All builds completed successfully!
echo ========================================
pause
exit /b 0
:ensure_java
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        call :check_java "%JAVA_HOME%\bin\java.exe"
        if not errorlevel 1 exit /b 0
    )
)
for /d %%J in ("C:\Program Files\Java\jdk-21*" "C:\Program Files\Eclipse Adoptium\jdk-21*" "C:\Program Files\Microsoft\jdk-21*" "C:\Program Files\Amazon Corretto\jdk-21*" "C:\Program Files\Java\jdk-17*" "C:\Program Files\Eclipse Adoptium\jdk-17*" "C:\Program Files\Microsoft\jdk-17*" "C:\Program Files\Amazon Corretto\jdk-17*") do (
    if exist "%%~fJ\bin\java.exe" (
        call :check_java "%%~fJ\bin\java.exe"
        if not errorlevel 1 (set "JAVA_HOME=%%~fJ" & goto :java_found)
    )
)
echo [ERROR] No valid JDK 17+ installation was found.
echo [HINT] Install JDK 21 or JDK 17, or set JAVA_HOME to a valid JDK path.
exit /b 1
:java_found
set "PATH=%JAVA_HOME%\bin;%PATH%"
exit /b 0
:check_java
"%~1" -version 2>&1 | findstr /r /c:"version \"1[7-9]\." /c:"version \"[2-9][0-9]\." >nul
if errorlevel 1 exit /b 1
exit /b 0
:ensure_node
if defined LUAATTACHDEBUG_NODE_HOME (
    if exist "%LUAATTACHDEBUG_NODE_HOME%\npm.cmd" (set "PATH=%LUAATTACHDEBUG_NODE_HOME%;%PATH%" & exit /b 0)
)
if exist "%ROOT_DIR%\.tools\node\npm.cmd" (set "PATH=%ROOT_DIR%\.tools\node;%PATH%" & exit /b 0)
where npm >nul 2>nul
if not errorlevel 1 exit /b 0
echo [ERROR] npm was not found in PATH.
echo [HINT] Install Node.js LTS, set LUAATTACHDEBUG_NODE_HOME, or place portable Node at .tools\node.
exit /b 1
