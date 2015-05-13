rem Used as a helper for the elevating VBS script. Runs the jar file
rem given as 1st argument as if the file was double clicked.

rem Make sure we're on our CURRENT directory
cd /d %~dp0
rem Run java, expecting the jar file to be given as the 1st argument
javaw -jar %1