;NSIS Script For WinterMute

page license checkJRE
page directory
page instfiles


;Title Of Your Application
Name "WinterMute"

;Do A CRC Check
CRCCheck On

;Output File Name
OutFile "WinterMuteInstall.exe"

Function  checkJRE
   ClearErrors
   ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
   ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$1" "JavaHome"

   IfErrors 0 NoAbort
       MessageBox MB_OK "Couldn't find a suitable JRE installed. Sun JRE 1.4 or greater is required. Setup will exit now." 
       Quit        

   NoAbort:

   push $1
   push "1.4"
   Call VersionCheck
   pop $0
  ;  MessageBox MB_OK "vc: $0"
   IntCmp $0 2 TooOld
   Goto VersionOkay
   TooOld:
      MessageBox MB_OK "The JRE Version $1 which was found at $2 is too old. You must use at least JRE 1.4. Setup will exit now."
      Quit


   VersionOkay:

   
        
  
       DetailPrint "Found JRE $1 in path $2"  
   
FunctionEnd


;License Page Introduction
LicenseText "WinterMute License"

;License Data
LicenseData "c:\neo\wm\output\license.txt"

;The Default Installation Directory
InstallDir "$PROGRAMFILES\WinterMute"

;The text to prompt the user to enter a directory
DirText "Please select the folder below"

Section "Install"

  ;Install Files
  SetOutPath $INSTDIR
  SetCompress Auto
  SetOverwrite IfNewer
  File "C:\neo\wm\jars\xmltool.jar"
  File "C:\neo\wm\jars\db.jar"
  File "C:\neo\wm\jars\lucene-1.2.jar"
  File "C:\neo\wm\jars\mail.jar"
  File "C:\neo\wm\jars\wm.jar"
  File "C:\neo\wm\jars\activation.jar"
  File "C:\local\java\bdb\lib\win32\libdb_java41.dll"
  File "C:\local\java\bdb\lib\win32\libdb41.dll"

  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\WinterMute" "DisplayName" "WinterMute (remove only)"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\WinterMute" "UninstallString" "$INSTDIR\Uninst.exe"
WriteUninstaller "Uninst.exe"
SectionEnd

Section "Shortcuts"
  ; Desktop
  CreateShortCut "$DESKTOP\WinterMute.lnk" "$INSTDIR\wm.jar" ""
  ; Start Menu Items
  CreateDirectory "$SMPROGRAMS\WinterMute"
  CreateShortCut "$SMPROGRAMS\WinterMute\Uninstall.lnk" "$INSTDIR\Uninst.exe" "" "$INSTDIR\Uninst.exe" 0
  CreateShortCut "$SMPROGRAMS\WinterMute\WinterMute.lnk" "$INSTDIR\wm.jar" "" "$INSTDIR\wm.jar" 0

SectionEnd

UninstallText "This will uninstall WinterMute from your system"

Section Uninstall
  ;Delete Files
  Delete "$INSTDIR\xmltool.jar"
  Delete "$INSTDIR\db.jar"
  Delete "$INSTDIR\lucene-1.2.jar"
  Delete "$INSTDIR\mail.jar"
  Delete "$INSTDIR\wm.jar"
  Delete "$INSTDIR\activation.jar"
  Delete "$INSTDIR\libdb_java41.dll"
  Delete "$INSTDIR\libdb41.dll"

  ;Delete Uninstaller And Unistall Registry Entries
  Delete "$INSTDIR\Uninst.exe"
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\WinterMute"
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\WinterMute"
  RMDir "$INSTDIR"
  
  ;Delete Start Menu Shortcuts
  Delete "$DESKTOP\WinterMute.lnk"
  Delete "$SMPROGRAMS\WinterMute\*.*"
  RmDir  "$SMPROGRAMS\WinterMute"
SectionEnd

;  Push "3.5"
;  Push "3.5.1.4"
;  Call VersionCheck
;  Pop $0
;  MessageBox MB_OK "Number $0 was newer"
;  ; output 1 - if number 1 is newer
;  ;        2 - if number 2 is newer
;  ;        0 - if it is the same verion
;  ; output 2 in case above



Function VersionCheck
  Exch $0 ;second versionnumber
  Exch
  Exch $1 ;first versionnumber
  Push $R0 ;counter for $0
  Push $R1 ;counter for $1
  Push $3 ;temp char
  Push $4 ;temp string for $0
  Push $5 ;temp string for $1
  StrCpy $R0 "-1"
  StrCpy $R1 "-1"
  Start:
  StrCpy $4 ""
  DotLoop0:
  IntOp $R0 $R0 + 1
  StrCpy $3 $0 1 $R0
  StrCmp $3 "" DotFound0
  StrCmp $3 "." DotFound0
  StrCpy $4 $4$3
  Goto DotLoop0
  DotFound0:
  StrCpy $5 ""
  DotLoop1:
  IntOp $R1 $R1 + 1
  StrCpy $3 $1 1 $R1
  StrCmp $3 "" DotFound1
  StrCmp $3 "." DotFound1
  StrCmp $3 "_" DotFound1
  StrCpy $5 $5$3
  Goto DotLoop1
  DotFound1:
  Strcmp $4 "" 0 Not4
    StrCmp $5 "" Equal
    Goto Ver2Less
  Not4:
  StrCmp $5 "" Ver2More
  IntCmp $4 $5 Start Ver2Less Ver2More
  Equal:
  StrCpy $0 "0"
  Goto Finish
  Ver2Less:
  StrCpy $0 "1"
  Goto Finish
  Ver2More:
  StrCpy $0 "2"
  Finish:
  Pop $5
  Pop $4
  Pop $3
  Pop $R1
  Pop $R0
  Pop $1
  Exch $0
FunctionEnd
