![alt tag](https://github.com/wnbittle/praisenter/blob/master/site-logo.png)

[![Release Build](https://github.com/wnbittle/praisenter/actions/workflows/build-all.yml/badge.svg)](https://github.com/wnbittle/praisenter/actions/workflows/build-all.yml)
[![Pre-release Build](https://github.com/wnbittle/praisenter/actions/workflows/build-all-prerelease.yml/badge.svg)](https://github.com/wnbittle/praisenter/actions/workflows/build-all-prerelease.yml)

A free and open source presentation software package for Churches, providing display of Bible verses, songs, notifications, and custom slides to a secondary displays, typically a projector, monitor or television.

## Requirements:
* Windows 10 x64 (where most testing occurs)
* Ubuntu 22.04 x64 or higher
* Mac OS 11 x64 or higher
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
Workspace Selection

![Workspace Selection](https://github.com/wnbittle/praisenter/blob/master/images/workspace-selection.png)

Single Output

![Single Output](https://github.com/wnbittle/praisenter/blob/master/images/present-single-output.png)

Sample Output

![Sample Output](https://github.com/wnbittle/praisenter/blob/master/images/sample-output.png)

Relevance Searching

![Relevance Searching](https://github.com/wnbittle/praisenter/blob/master/images/relevance-searching.png)

Application Zoom

![Application Zoom](https://github.com/wnbittle/praisenter/blob/master/images/application-zoom.png)

Bible Editing

![Bible Editing](https://github.com/wnbittle/praisenter/blob/master/images/bible-editing.png)

Slide Editing

![Slide Editing](https://github.com/wnbittle/praisenter/blob/master/images/slide-editing.png)

Light Mode

![Light Mode](https://github.com/wnbittle/praisenter/blob/master/images/light-mode.png)

JavaFX Mode

![JavaFX Mode](https://github.com/wnbittle/praisenter/blob/master/images/javafx-mode.png)

## Building
Install WiX Toolset 3.x:
https://github.com/wixtoolset/wix3/releases

Run Maven build:
```shell
mvn clean install
```