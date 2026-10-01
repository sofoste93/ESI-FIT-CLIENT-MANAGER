param([ValidateSet("app-image", "exe", "msi")][string]$PackageType = "app-image")
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $root
mvn -B -DskipTests package
if ($LASTEXITCODE -ne 0) { throw "Maven package failed." }
$arguments = @(
  "--type", $PackageType,
  "--input", "target\package-input",
  "--main-jar", "esi-fit-console.jar",
  "--main-class", "com.esifit.console.Launcher",
  "--name", "ESI-FIT-Console",
  "--dest", "target\dist",
  "--app-version", "2.0.0",
  "--vendor", "Enrico, Islam and Stephane",
  "--description", "Keyboard-first fitness club manager",
  "--copyright", "Copyright 2026 ESI-FIT team",
  "--java-options", "-Dfile.encoding=UTF-8",
  "--icon", "src\main\resources\com\esifit\console\app.ico"
)
if ($PackageType -in @("exe", "msi")) {
  $arguments += @("--win-menu", "--win-shortcut", "--win-dir-chooser", "--win-menu-group", "ESI-FIT")
}
New-Item -ItemType Directory -Path "target\dist" -Force | Out-Null
jpackage @arguments
if ($LASTEXITCODE -ne 0) { throw "jpackage failed." }
if ($PackageType -eq "exe" -and $env:WINDOWS_CERTIFICATE_BASE64 -and $env:WINDOWS_CERTIFICATE_PASSWORD) {
  $certificate = Join-Path $env:RUNNER_TEMP "esi-fit-console.pfx"
  [IO.File]::WriteAllBytes($certificate, [Convert]::FromBase64String($env:WINDOWS_CERTIFICATE_BASE64))
  $installer = Get-ChildItem "target\dist\*.exe" | Select-Object -First 1
  & signtool sign /fd SHA256 /tr "http://timestamp.digicert.com" /td SHA256 /f $certificate /p $env:WINDOWS_CERTIFICATE_PASSWORD $installer.FullName
  if ($LASTEXITCODE -ne 0) { throw "Authenticode signing failed." }
  Remove-Item -LiteralPath $certificate -Force
}
