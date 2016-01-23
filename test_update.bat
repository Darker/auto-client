@echo off>NUL
set HOME_DIR=.
set AUTO_CLIENT_JAR=x.jar
set AUTO_CLIENT_JAR_PATH=%HOME_DIR%\%AUTO_CLIENT_JAR%
set UPDATE_DIR_NAME=target
set UPDATE_DIR=%HOME_DIR%\%UPDATE_DIR_NAME%
set BACKUP_DIR=%UPDATE_DIR%\backup
set UPDATE_NAME=.
set UPDATE_PATH=%UPDATE_DIR%
set NEW_AUTO_CLIENT_JAR_PATH=%UPDATE_PATH%\%AUTO_CLIENT_JAR%
set DEBUG_MODE=no
set TEST=TEST
rem ==END OF VARDEF==
if "%DEBUG_MODE%" == "yes" (
  echo DEBUG_MODE_SUCCESS>debugmode.txt
  goto exit
)



echo ==UPDATE INSTALLER FOR UPDATE %UPDATE_NAME%==
echo This script will copy new jar file and remove old one. It shouldn't
echo take more than 3 seconds.
echo If something goes wrong with the update, you will find your old version
echo in /update/backup folder. It is also possible to restore old versions 
echo from update manager if you don't like the new version.
echo.
echo The /update directory is free for you to delete or modify. Deleting it
echo should not cause any problems. The /update/backup folder will be automatically
echo deleted 7 days after you're using the new version.
echo.
echo Update info:
echo   Jar file: %AUTO_CLIENT_JAR_PATH%
echo ____________________________________________
echo.
echo Backing up old jar file:
echo on>NUL
xcopy "%AUTO_CLIENT_JAR_PATH%" "%BACKUP_DIR%\" /Q /Y
rem echo off>NUL
rem echo.
rem echo Deleting old version of the jar file at %AUTO_CLIENT_JAR_PATH%
rem echo  this might take a while - waiting until application isn't running.
rem :delete_start
rem echo on>NUL
rem erase "%AUTO_CLIENT_JAR_PATH%"
rem if exist "%AUTO_CLIENT_JAR_PATH%" (
rem     echo ... auto client is still running, waiting...
rem     rem timeout /T 1 /nobreak>NUL
rem     goto delete_start
rem ) else (
rem     echo Done, time to install new version
rem     goto install_update
rem )

:install_update
rem first make an empty file
rem echo test >%AUTO_CLIENT_JAR_PATH%
xcopy "%NEW_AUTO_CLIENT_JAR_PATH%" "%AUTO_CLIENT_JAR_PATH%" /Q /Y
echo Copy result: %errorlevel%
rem xcopy %UPDATE_PATH%\* %HOME_DIR% /Q 
:run_jar
pause

:exit