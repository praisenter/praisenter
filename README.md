![alt tag](https://github.com/wnbittle/praisenter/blob/master/site-logo.png)

[![Release Build](https://github.com/wnbittle/praisenter/actions/workflows/build-all.yml/badge.svg)](https://github.com/wnbittle/praisenter/actions/workflows/build-all.yml)
[![Pre-release Build](https://github.com/wnbittle/praisenter/actions/workflows/build-all-prerelease.yml/badge.svg)](https://github.com/wnbittle/praisenter/actions/workflows/build-all-prerelease.yml)

A free and open source presentation software package for Churches, providing display of Bible verses, songs, notifications, and custom slides to a secondary displays, typically a projector, monitor or television.

## Requirements:
* Windows 10 x64 (where most testing occurs)
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

** Media Support **
Praisenter can import hundreds of different media file formats. Upon importing, Praisenter will convert them to an optimized form. You can change (or turn off) this feature to fit your workflow.

![Media Formats](https://praisenter.org/assets/img/features/formats.png)

**Bulk Editing**
Bibles and songs lyrics support bulk editing for easy copy/paste from other sources or fast transcription.

![Bulk Editing](https://praisenter.org/assets/img/features/bulk-edit.png)

## Building
Install WiX Toolset 3.x:
https://github.com/wixtoolset/wix3/releases

### Maven
```shell
mvn clean install
```

### Microsoft Store
https://partner.microsoft.com/en-us/dashboard/products

### Ubuntu Snap
```shell
# to build the snap, navigate to the /snapcraft folder, copy the praisenter.deb there, then run
snapcraft
# to install a locally built snap
sudo snap install --devmode ./praisenter.snap
# to uninstall a snap
sudo snap remove praisenter
# to upload a new snap to the snapstore
snapcraft login
snapcraft upload ./praisenter_3.1.3_amd64.snap
```

