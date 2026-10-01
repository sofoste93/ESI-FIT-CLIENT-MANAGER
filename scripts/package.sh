#!/usr/bin/env bash
set -euo pipefail
TYPE="${1:-app-image}"
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"
mvn -B -DskipTests package
mkdir -p target/dist
ICON="src/main/resources/com/esifit/console/icon.png"
if [[ "$(uname -s)" == "Darwin" ]]; then
  ICONSET="target/AppIcon.iconset"
  rm -rf "$ICONSET"
  mkdir -p "$ICONSET"
  for size in 16 32 128 256 512; do
    sips -z "$size" "$size" "$ICON" --out "$ICONSET/icon_${size}x${size}.png" >/dev/null
    double=$((size * 2))
    sips -z "$double" "$double" "$ICON" --out "$ICONSET/icon_${size}x${size}@2x.png" >/dev/null
  done
  iconutil -c icns "$ICONSET" -o target/AppIcon.icns
  ICON="target/AppIcon.icns"
fi
args=(
  --type "$TYPE"
  --input target/package-input
  --main-jar esi-fit-console.jar
  --main-class com.esifit.console.Launcher
  --name ESI-FIT-Console
  --dest target/dist
  --app-version 2.0.0
  --vendor "Enrico, Islam and Stephane"
  --description "Keyboard-first fitness club manager"
  --copyright "Copyright 2026 ESI-FIT team"
  --java-options "-Dfile.encoding=UTF-8"
  --icon "$ICON"
)
if [[ "$TYPE" == "deb" ]]; then
  args+=(--linux-shortcut --linux-menu-group Office --linux-package-name esi-fit-console)
elif [[ "$TYPE" == "dmg" ]]; then
  args+=(--mac-package-identifier com.esifit.console)
fi
jpackage "${args[@]}"
