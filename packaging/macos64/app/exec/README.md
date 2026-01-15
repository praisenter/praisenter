# Security Scoped Bookmarks
Deploying macOS applications requires them to be sandboxed.  Sandboxing an application ensures that malicious code has limited opportunity to affect the whole system.  Apple took this so far as to not allow access to folders outside the sandbox unless expressly selected by a user.  This poses a problem when an application is restarted and needs to access that same location.  To resolve that problem, Apple came up with a process of Bookmarking where an application can track that a user allowed a file/folder to be used by the application.  Those bookmarks can then be used on other executions to access those files/folders without the user selecting them again:

https://developer.apple.com/library/content/documentation/Security/Conceptual/AppSandboxDesignGuide/AppSandboxInDepth/AppSandboxInDepth.html

## JNI Library
https://github.com/plexteq/PQSsbJNIBridge

I had to make the following changes:
- I made the change suggested here https://github.com/plexteq/PQSsbJNIBridge/issues/2
- Change the MACOSX_DEPLOYMENT_TARGET to 10.15 in the project.pbxproj
- Added CODE_SIGNING_ALLOWED=NO argument to the build.sh

# FFmpeg / FFprobe
https://evermeet.cx/ffmpeg/

Mac OS X 10.12 and later

On macOS 10.15 (Catalina) the binary has to be removed from quarantine: xattr -dr com.apple.quarantine <path_to_binary>

The inclusion of these tools must be stored within the app bundle, must be marked as executables, and must be signed properly.

# NDI / Devolay
Version 2.1.1 was manually copied into this folder due to the loading style it won't work in the app store deployment