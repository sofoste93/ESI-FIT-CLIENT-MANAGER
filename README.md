# ESI-FIT Console Orbit

<p align="center">
  <img src="assets/esi-fit-console.svg" width="760" alt="ESI-FIT Console Orbit">
</p>

<p align="center">
  A fast, keyboard-first fitness club manager that runs locally and keeps member data offline.
</p>

<p align="center">
  <a href="https://github.com/sofoste93/ESI-FIT-CLIENT-MANAGER/releases/latest"><strong>Download the latest release</strong></a>
  ·
  <a href="#keyboard-controls">Controls</a>
  ·
  <a href="#data-and-privacy">Data</a>
</p>

## A real terminal command center

Version 2 replaces the old numbered German menu and fragile text-file storage with a full-screen terminal interface and a durable local database. It is designed for a front desk, a laptop or a remote SSH session.

- Live dashboard for active members, people in the club, today's visits and average session time
- Member creation, search, membership pause/resume and removal
- One-step check-in and check-out with duplicate-entry protection
- Visit archive, live sessions and CSV attendance exports
- Automatic recovery of the old `clients.txt` and `sessions.txt` format
- Local H2 database with no account, cloud service, tracking or network requirement
- Bundled Java runtime in every downloadable app
- Native Windows, Linux and macOS packages plus portable archives

## Install

Download the package for your system from [GitHub Releases](https://github.com/sofoste93/ESI-FIT-CLIENT-MANAGER/releases/latest). Java is included.

| System | Installer | Portable |
|---|---|---|
| Windows 10/11 x64 | `ESI-FIT-Console-Windows-x64.exe` | `ESI-FIT-Console-Windows-x64.zip` |
| Linux x64 (Debian/Ubuntu) | `ESI-FIT-Console-Linux-x64.deb` | `ESI-FIT-Console-Linux-x64.tar.gz` |
| macOS Intel | `ESI-FIT-Console-macOS-x64.dmg` | `ESI-FIT-Console-macOS-x64.tar.gz` |
| macOS Apple Silicon | `ESI-FIT-Console-macOS-arm64.dmg` | `ESI-FIT-Console-macOS-arm64.tar.gz` |

Portable builds can be extracted anywhere. On Linux, launch `bin/ESI-FIT-Console`; on Windows, launch `ESI-FIT-Console.exe`.

> Windows can show a SmartScreen warning until the release is signed with a publicly trusted code-signing certificate. The release workflow is already Authenticode-ready; see [SIGNING.md](SIGNING.md).

## Keyboard controls

| Key | Action |
|---|---|
| `Tab` / arrow keys | Move between operations and fields |
| `Enter` | Activate the selected operation |
| `Escape` | Close the current dialog |

The interface also works in an SSH terminal. A graphical terminal window is used automatically when the app is launched outside a console.

## Data and privacy

ESI-FIT stores everything on the current device:

| System | Data directory |
|---|---|
| Windows | `%LOCALAPPDATA%\ESI-FIT Console` |
| macOS | `~/Library/Application Support/ESI-FIT Console` |
| Linux | `$XDG_DATA_HOME/esi-fit-console` or `~/.local/share/esi-fit-console` |

CSV exports are written to the `exports` folder inside that directory. To migrate the original app, place its `clients.txt` and `sessions.txt` beside the executable before the first launch. The records are imported automatically.

## Build from source

Requirements: JDK 17+ and Maven 3.9+.

```bash
mvn clean verify
mvn exec:java -Dexec.mainClass=com.esifit.console.Launcher
```

Create a native package with the included scripts:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/package.ps1 -PackageType app-image
```

```bash
./scripts/package.sh app-image
```

Run the packaged-runtime health check with `ESI-FIT-Console --diagnostics`.

## Project crew

Created by Enrico Dück, Islam Nasif and Stephane Sob Fouodji. Console Orbit 2.0 restores the original group project while preserving its local, simple and practical spirit.

Released under the [MIT License](LICENSE).
