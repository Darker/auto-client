echo Copying jar archive to release directory
rem xcopy target\autoclient-*-jar-with-dependencies.jar .\AutoClient.jar* /Y /L
copy /Y /B target\autoclient-*-jar-with-dependencies.jar .\AutoClient.jar
pause
