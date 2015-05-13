' Require first command line parameter
if WScript.Arguments.Count = 0 then
  MsgBox("Jar file name required.")
  WScript.Quit 1
end if

' Get the script location, the directorry where it's running
Set objShell = CreateObject("Wscript.Shell")

strPath = Wscript.ScriptFullName

Set objFSO = CreateObject("Scripting.FileSystemObject")

Set objFile = objFSO.GetFile(strPath)
strFolder = objFSO.GetParentFolderName(objFile) 
'MsgBox(strFolder)

' Create the object that serves as runnable something
Set UAC = CreateObject("Shell.Application") 
' Executing javaw directly never works.
'MsgBox("-jar "&strFolder&"\AutoClient.jar")
'MsgBox("-jar "&WScript.Arguments(0))
'UAC.ShellExecute "javaw.exe", "-jar "&strFolder&"\AutoClient.jar", strFolder, "runas", 1 
'UAC.ShellExecute "javaw", "-jar "&WScript.Arguments(0), strFolder, "runas", 1 
' So wee need this tiny helper file. 
' Args:
'   path to executable to run
'   command line parameters - first parameter of this file, which is the jar file name
'   working directory (this doesn't work but I use it nevertheless)
'   runas command which invokes elevation
'   0 means do not show the window. Normally, you show the window, but not this console window
'     which just blinks and disappears anyway
UAC.ShellExecute "run-normally.bat", WScript.Arguments(0), strFolder, "runas", 0 

WScript.Quit 0