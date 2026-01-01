![alt tag](https://github.com/wnbittle/praisenter/blob/master/site-logo.png)

[![Release Build](https://github.com/wnbittle/praisenter/actions/workflows/build-all.yml/badge.svg)](https://github.com/wnbittle/praisenter/actions/workflows/build-all.yml)
[![Pre-release Build](https://github.com/wnbittle/praisenter/actions/workflows/build-all-prerelease.yml/badge.svg)](https://github.com/wnbittle/praisenter/actions/workflows/build-all-prerelease.yml)

A free and open source presentation software package for Churches, providing display of Bible verses, songs, notifications, and custom slides to a secondary displays, typically a projector, monitor or television.

## Requirements:
* Windows 10 x64 22H2 or higher (most testing occurs on Windows 11)
* Ubuntu 22.04 x64 or higher
* Mac OS 11 x64 or higher (coming...)
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
Install WiX Toolset 6.x:
https://github.com/wixtoolset/wix/releases

### Maven
```shell
mvn clean install
```

### Microsoft Store
https://partner.microsoft.com/en-us/dashboard/products

### Ubuntu Snap
```shell
# to build the snap, navigate to the /snapcraft folder, copy the praisenter.deb there, then run
snapcraft pack
# to install a locally built snap
sudo snap install --devmode ./praisenter.snap
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

### FFmpeg Static Builds
https://github.com/BtbN/FFmpeg-Builds

### Git LFS
The FFmpeg binaries are too large for GitHub to track so they are stored in Git LFS.

To update the files, ensure that when you clone the repo that you initialize Git LFS:
```shell
git lfs install
```

To install Git LFS:
https://docs.github.com/en/repositories/working-with-files/managing-large-files/installing-git-large-file-storage

Current Git LFS tracked objects:
```shell
git lfs track src/main/resources/org/praisenter/data/media/tools/linux64/ffmpeg
git lfs track src/main/resources/org/praisenter/data/media/tools/linux64/ffprobe
git lfs track src/main/resources/org/praisenter/data/media/tools/macos64/ffmpeg
git lfs track src/main/resources/org/praisenter/data/media/tools/macos64/ffprobe
git lfs track src/main/resources/org/praisenter/data/media/tools/windows64/ffmpeg.exe
git lfs track src/main/resources/org/praisenter/data/media/tools/windows64/ffprobe.exe
```

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

### macOS updates needed for deployment
- provisioning profiles (if expired), download from https://developer.apple.com/account/resources/profiles/list
- install certificates (Developer ID Application, Developer ID Installer, Mac App Distribution, Mac Installer Distribution), download from https://developer.apple.com/account/resources/certificates/list
- /resources/Info.plist - version number
- /config/distribution.dist - version number

