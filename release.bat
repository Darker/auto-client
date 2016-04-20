@echo off
set /p vid="Enter version id (vX.X.X-maybe): "
echo Building AutoClient in current dir
rem cd ..
echo Creating release directory
@del rd /S /Q release\last_release >NUL
@md release\last_release >NUL
@md release\last_release\images >NUL
echo  
echo Copying jar archive to release directory
xcopy target\autoclient-*-jar-with-dependencies.jar release\last_release\AutoClient.jar* /Y

echo  
echo Copying aditional files
copy release_aditional_files\*.* release\last_release
echo  
echo Copying external images
xcopy images\*.* release\last_release\images /S /Y /EXCLUDE:exclude.txt
cd release\last_release

echo Adding verison ID to the JAR archive
echo %vid%>version
jar uf AutoClient.jar version
del version
echo  
echo Packing
7z a -tzip -y -r "../AutoClient_%vid%.zip" "*.*"


rem cd ..
rem copy AutoClient_latest.zip AutoClient_latest.zip
rem cd ..
rem rmdir /S /Q last_release 
pause
