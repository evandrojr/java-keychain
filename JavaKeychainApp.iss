; Inno Setup script for Java Keychain App
; Compile with Inno Setup Compiler (https://jrsoftware.org/isinfo.php)

[Setup]
AppName=Java Keychain App
AppVersion=1.0.0
DefaultDirName={pf}\JavaKeychainApp
DefaultGroupName=Java Keychain App
OutputDir=.
OutputBaseFilename=JavaKeychainApp-Setup
Compression=lzma
SolidCompression=yes

[Files]
Source: "build\libs\java-keychain-app-*.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "install-deps.ps1"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\Java Keychain App"; Filename: "{sys}\cmd.exe"; Parameters: "/c java -jar \"{app}\java-keychain-app-1.0.0.jar\""; WorkingDir: "{app}"
Name: "{userdesktop}\Java Keychain App"; Filename: "{sys}\cmd.exe"; Parameters: "/c java -jar \"{app}\java-keychain-app-1.0.0.jar\""; WorkingDir: "{app}"; Tasks: desktopicon

[Run]
Filename: "powershell.exe"; Parameters: "-ExecutionPolicy Bypass -File \"{app}\install-deps.ps1\""; StatusMsg: "Configurando dependências do Windows..."; Flags: runhidden

[Tasks]
Name: desktopicon; Description: "Criar atalho na área de trabalho"; GroupDescription: "Atalhos:"

[Code]
function InitializeSetup(): Boolean;
begin
  Result := True;
end;
