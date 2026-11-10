![alt tag](https://github.com/wnbittle/praisenter/blob/master/site-logo.png)

[![Release Build](https://github.com/wnbittle/praisenter/actions/workflows/build-all.yml/badge.svg)](https://github.com/wnbittle/praisenter/actions/workflows/build-all.yml)
[![Pre-release Build](https://github.com/wnbittle/praisenter/actions/workflows/build-all-prerelease.yml/badge.svg)](https://github.com/wnbittle/praisenter/actions/workflows/build-all-prerelease.yml)

A free and open source presentation software package for Churches, providing display of Bible verses, songs, notifications, and custom slides to a secondary displays, typically a projector, monitor or television.

<a href="https://get.microsoft.com/installer/download/9phhwb94w800?referrer=appbadge" target="_self"><img src="https://get.microsoft.com/images/en-us%20light.svg" width="210"/></a>
&nbsp;
<a href="https://snapcraft.io/praisenter"><img alt="Get it from the Snap Store" src=https://snapcraft.io/en/light/install.svg width="180"/></a>
&nbsp;
<a href="https://apps.apple.com/us/app/praisenter/id6756894623?mt=12&itscg=30200&itsct=apps_box_badge&mttnsubad=6756894623"><img src="https://toolbox.marketingtools.apple.com/api/v2/badges/download-on-the-app-store/white/en-us?releaseDate=1769472000" alt="Download on the App Store" width="172" /></a>
    
## Requirements:
* Windows 10 x64 22H2 or higher (most testing occurs on Windows 11)
* Ubuntu 22.04 x64 or higher
* Mac OS 13.0 to 27 (when Rosetta support is ended)
* Plenty of RAM (4 or more)
* A decent/recent multicore CPU
* At least 2 video outputs

## Features:
* Free, no cost, no ads, nothing.
* Multiple screen support (limited only by the number of physically connected displays)
* Display Bible verses
* Display song lyrics
* Display custom slides
* Fully customizable templates for displaying Bible verses, song lyrics, and notifications
* Support for huge number of picture, video, audio formats
* Support for transition animations between slides, Bible verses, song lyrics, etc.
* Support for creating and editing Bibles, songs, and slides
* Support for displaying two languages side-by-side when presenting Bible verses and song lyrics
* Support for displaying notifications/alerts
* Support for "workspaces" where you can split your assets between different services
* Support for export/import of content (build at home, export, import at church)
* Support for adding slides to a service queue (no auto-play yet)
* Support for bulk editing Bible verses/song lyrics
* NDI (video only) output
* And more!

## Screenshots:

**Workspace Selection**
Workspaces allow you to collect all your assests (Bibles, song lyrics, videos, pictures, etc.) in a single location. You could create one workspace for Bible study nights and separate workspace for worship services or a third workspace for youth services or special events.

**Multiple Outputs**
Praisenter allows you to send content to each display independently. The number of displays is dependent on the number of video outputs your system supports.

![Multiple Output](https://praisenter.org/assets/img/features/multiple-display.png)

**Sample Output**
Display Bible verses, song lyrics, pictures, videos, custom slides, and notifications.

![Sample Output](https://praisenter.org/assets/img/features/sample-output.png)

**Two-Language Display**
Present side-by-side two languages (or two versions) when presenting Bible verses or song lyrics.

![Two Language Display](https://praisenter.org/assets/img/features/dual-language.png)

**Editing**
With Praisenter you can create Bibles, song lyrics, and slides that include pictures, videos, and audio. Slides can have backgrounds, static text, dynamic text, the current date/time, count downs, pictures, animations, and more.

![Editing](https://praisenter.org/assets/img/features/editing.png)

**Export/Import**
Since Praisenter is free, you can download it anywhere, and build content. After building that content you can export it and import it 
on another computer. This is a great way to prepare content ahead of time.

![Export and Import](https://praisenter.org/assets/img/features/import.png)

**Relevance Searching**
Other features include Bible searching, edit safety, notification overlays, slide queuing, text scaling, and more.

![Relevance Searching](https://praisenter.org/assets/img/features/searching.png)

**Media Support**
Praisenter can import hundreds of different media file formats. Upon importing, Praisenter will convert them to an optimized form. You can change (or turn off) this feature to fit your workflow.

![Media Formats](https://praisenter.org/assets/img/features/formats.png)

**Bulk Editing**
Bibles and songs lyrics support bulk editing for easy copy/paste from other sources or fast transcription.

![Bulk Editing](https://praisenter.org/assets/img/features/bulk-edit.png)

## Building:

### General Setup
Install a **JDK 25 or higher** for your operating system and CPU architecture. The project and CI use **Azul Zulu 25 with JavaFX** (`jdk+fx`); for example, the Windows x64 build uses the Zulu FX JDK 25.0.2. Download the JDK (not a JRE) from [Azul Zulu Downloads](https://www.azul.com/downloads/?package=jdk-fx#zulu) and select Java 25, the JDK FX package, and your platform/architecture. JavaFX dependencies are also declared in `pom.xml` and Maven will resolve them during the build.

### VS Code Setup

1. Install [Visual Studio Code](https://code.visualstudio.com/).

2. Install the **Extension Pack for Java** (`vscjava.vscode-java-pack`) and **Maven for Java** (`vscjava.vscode-maven`). The Java pack includes the Java language support, debugger, test runner, and project manager extensions.

3. Set `JAVA_HOME` to the JDK installation directory (the directory containing `bin`), and add its `bin` directory to `PATH` so `java` and `javac` are available. For example, on Windows:

   ```text
   JAVA_HOME=C:\Program Files\Zulu\zulu-25
   PATH=%JAVA_HOME%\bin;...
   ```

   On macOS or Linux, add `export JAVA_HOME=/path/to/jdk-25` and `export PATH="$JAVA_HOME/bin:$PATH"` to your shell profile. Restart VS Code after changing environment variables. The workspace `.vscode/settings.json` also sets `JAVA_HOME` for Maven terminals; update its `maven.terminal.customEnv` value to your JDK path, or remove that override if you rely on the environment inherited by VS Code.

4. Install [Apache Maven](https://maven.apache.org/download.cgi) (3.9 or newer); this repository does not include the Maven Wrapper. Set `MAVEN_HOME` or `M2_HOME` if required by your installation and add Maven's `bin` directory to `PATH`. Confirm the tools are visible in a new VS Code terminal:

   ```shell
   java -version
   javac -version
   mvn -version
   ```

5. Open the repository folder in VS Code and allow the Java and Maven extensions to import the Maven project. Build and test with:

   ```shell
   mvn clean install
   ```

### Maven
```shell
mvn clean install
```

### Windows
https://partner.microsoft.com/en-us/dashboard/products

On Windows, the full Maven build also creates an installer using `jpackage`. Install the [.NET SDK](https://dotnet.microsoft.com/download) and [WiX Toolset 6.x](https://github.com/wixtoolset/wix/releases), including the WiX Util and UI extensions, and ensure the `wix` command is on `PATH`. WiX is not needed for Java editing or running the application.

Install WiX Toolset 6.x:
https://github.com/wixtoolset/wix/releases


### Ubuntu
The project produces two artifacts for Ubuntu: A Debian package (.deb) and a Snapcraft Snap (.snap).  The `.snap` is the preferred installation method since it's built using `core22` and supports multiple versions of Ubuntu.  The `.deb` is an alternative installation method, but only supports the version of Ubuntu it was build on - in this case whatever the GitHub runner `ubuntu-latest` is on.

```shell
# to build the snap, navigate to the /snapcraft folder, copy the praisenter.deb there, then run
snapcraft pack
# to install a locally built snap
sudo snap install --devmode ./praisenter.snap
# to install the github produced snap in sandbox mode
sudo snap install --dangerous ./praisenter.snap
# to uninstall a snap
sudo snap remove praisenter

# to list interfaces
snap connections praisenter
# to connect interfaces
snap connect praisenter:avahi-control
# to disconnect interfaces
snap disconnect praisenter:avahi-control

# to upload a new snap to the snapstore
snapcraft login
snapcraft upload ./praisenter_3.1.3_amd64.snap
```

To test NDI on Ubuntu you need to:
```shell
# Install OBS Studio
sudo snap install obs-studio
# Install NDI runtime for obs studio
sudo snap install ndi-runtime-obs-studio
# Install DistroAV for obs studio
sudo snap install distroav-obs-studio

# connect required plugs
sudo snap connect obs-studio:obs-plugins ndi-runtime-obs-studio:obs-plugins
sudo snap connect obs-studio:obs-plugins distroav-obs-studio:obs-plugins
sudo snap connect obs-studio:avahi-control

# if using the .snap version of praisenter
sudo snap connect praisenter:avahi-control
```

### macOS deployment
The bash scripts used below recreate _some_ of the `jpackage` steps but are different due to additional native code used on MacOS, embedded executables, and custom packaging/signing to meet the App Store requirements.

- Make sure provisioning profiles are stored in packaging/macos64/app/content (or replace if expired), download from https://developer.apple.com/account/resources/profiles/list
- Make sure certificates (Developer ID Application, Developer ID Installer, Mac App Distribution, Mac Installer Distribution) are installed (or replace if expired), download from https://developer.apple.com/account/resources/certificates/list
- Run maven build from Eclipse to produce an unsigned "app-image" (the Praisenter.app file)
- Run either the `package-store.sh` or `package-adhoc.sh` to generate an installer pkg file
- Use the pkg generated by the `package-adhoc.sh` script to test locally:
  - Delete the Praisenter.app from /Applications
  - Delete the Praisenter.app from the target folder
  - Delete the pkg-work folder in the target folder
  - Double click the pkg file to install
  - Test by running from /Applications
- Use the Transporter app to upload the pkg file to App Store Connect
  - NOTE: this pkg cannot be tested locally
  - Once uploaded to App Store Connect, you can use TestFlight to test these builds
  
### macOS Marketing Tool
https://toolbox.marketingtools.apple.com/en-us/app-store/us/app/6756894623
  
### macOS Icon Composer
Icons for the application were created using [Icon Composer](https://developer.apple.com/icon-composer/).  This tool creates the newer version of icons in the Assets.car and the legacy .icns file for older OSes.

The editable file is /packaging/macos64/Praisenter.icon. The creation of the icon involved splitting the standard SVG icon into layers and tweaking the settings in Icon Composer.  From there, I created a dummy XCode project, added the icon to it and built it.  In the output directory for the dummy project contained the Assets.car file that I manually add to the app package and reference from the Info.plist.

### macOS .icns file
Follow the process outlined [here](https://gist.github.com/jamieweavis/b4c394607641e1280d447deed5fc85fc) and repeated below, just in case that gist is removed.

#### Create images
 Name | Dimensions |
| ---- | ---------- |
| `icon_16x16.png` | `16x16` |
| `icon_16x16@2x.png` | `32x32` |
| `icon_32x32.png` | `32x32` |
| `icon_32x32@2x.png` | `64x64` |
| `icon_128x128.png` | `128x128` |
| `icon_128x128@2x.png` | `256x256` |
| `icon_256x256.png` | `256x256` |
| `icon_256x256@2x.png` | `512x512` |
| `icon_512x512.png` | `512x512` |
| `icon_512x512@2x.png` | `1024x1024` |

#### Creating an `.iconset`

1. Move all of the images into a new folder
2. Rename the folder to: `icon.iconset`
3. Confirm the file extension when prompted

This will convert the folder of images into an iconset, this can be verified by quick looking with the spacebar - a resizable preview of your icon should now appear.

#### Converting to `.icns`

1. Navigate to the directory containing your `icon.iconset` in the terminal
2. Run `iconutil` with the following command: `iconutil -c icns icon.iconset`
3. Your `icon.icns` will be generated in the current directory
