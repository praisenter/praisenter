# Provisioning profiles
When packaging an application you must include the provisioning profile inside the app bundle.  The provisioning profile you use depends on the distribution method: app store or ad-hoc in the case of Praisenter.

Download the following provisioning profiles:
- Developer ID Application (associated to a Developer ID Application certificate) for ad-hoc distribution
- App Store (associated to a Mac App Distribution certificate) for App Store distribution

Place the provisioning profile files in this folder (packaging/macos64/app/content) and name them:
- DeveloperID.embedded.provisionprofile
- MacAppDistribution.embedded.provisionprofile

Running the correct `.sh` will select the correct provision profile for the build