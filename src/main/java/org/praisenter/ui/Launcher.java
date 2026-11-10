package org.praisenter.ui;

// required unless doing all modules (which we can't right now)
// https://github.com/javafxports/openjdk-jfx/issues/236#issuecomment-426583174
public class Launcher {
	public static void main(String[] args) {
        // fix for Snap package user home directory issue
        // preventing the save of preferences allowed when
        // starting the app (like what screens to share)
        String snapHome = System.getenv("SNAP_USER_COMMON");
        if (snapHome != null && !snapHome.isBlank()) {
            // Java FX when using Robot (and other situations?) will
            // use user.home as the place to store the preferences made
            // by the user.  We need to set this property ASAP before
            // Java FX initializes
            System.setProperty("user.home", snapHome);
        }
        
        // There's another issue on the Linux side with screen access where the restore token is
        // not a GUID
        // JAVABUG [Linux] Robot: screencast restore token is wrongly assumed to be a UUID https://bugs.openjdk.org/browse/JDK-8396109

        Praisenter.main(args);
    }
}
