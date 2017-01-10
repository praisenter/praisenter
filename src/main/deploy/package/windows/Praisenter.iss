;This file will be executed next to the application bundle image
;I.e. current directory will contain folder Praisenter with application files
[Setup]
AppId={{fxApplication}}
AppName=Praisenter
AppVersion=3.0.0
AppVerName=Praisenter 3.0.0
AppPublisher=Praisenter
AppComments=Praisenter
AppCopyright=Copyright (C) 2016
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={localappdata}\Praisenter
DisableStartupPrompt=Yes
DisableDirPage=Yes
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=Praisenter
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=Praisenter-3.0.0
SetupIconFile=Praisenter\Praisenter.ico
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
UninstallDisplayIcon={app}\Praisenter.ico
UninstallDisplayName=Praisenter
WizardImageStretch=No
WizardSmallImageFile=Praisenter-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "Praisenter\Praisenter.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "Praisenter\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\Praisenter"; Filename: "{app}\Praisenter.exe"; IconFilename: "{app}\Praisenter.ico"; Check: returnTrue()
Name: "{commondesktop}\Praisenter"; Filename: "{app}\Praisenter.exe";  IconFilename: "{app}\Praisenter.ico"; Check: returnTrue()


[Run]
Filename: "{app}\Praisenter.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\Praisenter.exe"; Description: "{cm:LaunchProgram,Praisenter}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\Praisenter.exe"; Parameters: "-install -svcName ""Praisenter"" -svcDesc ""Praisenter"" -mainExe ""Praisenter.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\Praisenter.exe "; Parameters: "-uninstall -svcName Praisenter -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
