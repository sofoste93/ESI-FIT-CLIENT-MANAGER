$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$resource = Join-Path $root "src\main\resources\com\esifit\console"
New-Item -ItemType Directory -Path $resource -Force | Out-Null
$bitmap = New-Object System.Drawing.Bitmap 1024, 1024
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
$graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
$graphics.Clear([System.Drawing.Color]::FromArgb(5, 12, 9))
$frame = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(64, 100, 73)), 28
$lime = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(167, 255, 80)), 48
$cyan = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(81, 240, 255)), 25
$graphics.DrawRectangle($frame, 130, 130, 764, 764)
$points = [System.Drawing.Point[]]@(
  (New-Object System.Drawing.Point 210, 525),
  (New-Object System.Drawing.Point 350, 525),
  (New-Object System.Drawing.Point 425, 320),
  (New-Object System.Drawing.Point 570, 735),
  (New-Object System.Drawing.Point 675, 450),
  (New-Object System.Drawing.Point 750, 525),
  (New-Object System.Drawing.Point 820, 525)
)
$graphics.DrawLines($lime, $points)
$graphics.DrawLine($cyan, 190, 230, 830, 230)
$graphics.DrawLine($cyan, 190, 810, 830, 810)
$png = Join-Path $resource "icon.png"
$bitmap.Save($png, [System.Drawing.Imaging.ImageFormat]::Png)
$icon = [System.Drawing.Icon]::FromHandle($bitmap.GetHicon())
$stream = [System.IO.File]::Create((Join-Path $resource "app.ico"))
$icon.Save($stream)
$stream.Dispose(); $icon.Dispose(); $cyan.Dispose(); $lime.Dispose(); $frame.Dispose(); $graphics.Dispose(); $bitmap.Dispose()
