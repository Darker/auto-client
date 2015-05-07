REM Source: http://stackoverflow.com/a/12264592/607407

:::::::::::::::::::::::::::::::::::::::::
:: Automatically check & get admin rights
:::::::::::::::::::::::::::::::::::::::::
@echo off
CLS
ECHO.
ECHO =============================
ECHO Running Admin shell
ECHO =============================

:checkPrivileges
NET FILE 1>NUL 2>NUL
if '%errorlevel%' == '0' ( goto gotPrivileges ) else ( goto getPrivileges )

:getPrivileges
if '%1'=='ELEV' (shift & goto gotPrivileges)
ECHO.
ECHO **************************************
ECHO Invoking UAC for Privilege Escalation
ECHO **************************************

setlocal DisableDelayedExpansion
set "batchPath=%~0"
setlocal EnableDelayedExpansion
ECHO Set UAC = CreateObject^("Shell.Application"^) > "%temp%\OEgetPrivileges.vbs"
ECHO UAC.ShellExecute "!batchPath!", "ELEV", "", "runas", 1 >> "%temp%\OEgetPrivileges.vbs"
"%temp%\OEgetPrivileges.vbs"
exit /B

:gotPrivileges
::::::::::::::::::::::::::::
::START
::::::::::::::::::::::::::::
setlocal & pushd .

ECHO 
ECHO Starting the Java...
cd %~dp0
REM Everything below runs as admin:

IF NOT EXIST AutoClient.jar (
  rem color 4f
  echo AutoClient.jar not found in directory '%~dp0'!
  rem color
  echo Please check where did you put the batch file.
  pause >NUL
  exit 1
) ELSE (
  start javaw -jar "AutoClient.jar"
)




REM ECHO Program terminated. Press any key to close this window.
REM pause >NUL
