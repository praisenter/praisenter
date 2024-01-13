package org.praisenter.utility;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.ui.translations.Translations;

public final class DesktopLauncher {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private DesktopLauncher() {}
	
	public static final void browse(String url) {
		// https://forum.snapcraft.io/t/issue-with-java-desktop-browse-browse-action-is-not-supported/29525
		// https://forum.snapcraft.io/t/stric-confinement-application-must-open-firefox-web-browser/9789
		if (RuntimeProperties.IS_LINUX_OS) {
			// run xdg-open via command line instead due to SNAP isolation preventing whatever Java is doing (gnome_url_show?)
			// https://bugs.launchpad.net/ubuntu/+source/openjdk-8/+bug/1574879
			try {
				Runtime.getRuntime().exec(new String[] { "xdg-open", url });
				return;
			} catch (IOException ex) {
				// if xdg-open doesn't exist or we get an error, then just try the normal way
				LOGGER.error("Failed to open default browser for URL (using xdg-open): " + Translations.get("ndi.link"), ex);
			}
		}
		
		// Desktop must be used from the AWT EventQueue
		// https://stackoverflow.com/a/65863422
		EventQueue.invokeLater(() -> {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				try {
				    Desktop.getDesktop().browse(new URI(url));
				} catch (Exception ex) {
					LOGGER.error("Failed to open default browser for URL: " + Translations.get("ndi.link"), ex);
				}
			}
		});
	}
}
