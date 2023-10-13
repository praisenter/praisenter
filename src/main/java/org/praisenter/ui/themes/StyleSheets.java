package org.praisenter.ui.themes;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;

import javafx.scene.Scene;

public final class StyleSheets {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String DEFAULT = fromFileSystem(Paths.get(Constants.STYLES_ABSOLUTE_PATH, Constants.STYLES_BASE_FILENAME), Constants.STYLES_BASE_ON_CLASSPATH);
	public static final String ICONS = fromFileSystem(Paths.get(Constants.STYLES_ABSOLUTE_PATH, Constants.STYLES_ICONS_FILENAME), Constants.STYLES_ICONS_ON_CLASSPATH);
	public static final String ACCENT = fromFileSystem(Paths.get(Constants.STYLES_ABSOLUTE_PATH, Constants.STYLES_ACCENT_FILENAME), Constants.STYLES_ACCENT_ON_CLASSPATH);

	public static final String[] STYLES = new String[] { 
		ICONS,
		DEFAULT,
		ACCENT
	};
	
	private StyleSheets() {}
	
	public static final void apply(Scene scene) {
		for (String style : STYLES) {
			scene.getStylesheets().add(style);
		}
	}
	
	public static final void reapply(Scene scene) {
		for (String style : STYLES) {
			scene.getStylesheets().remove(style);
		}
		for (String style : STYLES) {
			scene.getStylesheets().add(style);
		}
	}
	
	private static final String fromFileSystem(Path path, String fallbackClasspathPath) {
		try {
			return path.toUri().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			// log it and just use the classpath file
			LOGGER.error("Failed to resolve the path '" + path.toAbsolutePath() + "': " + e.getMessage(), e);
			return StyleSheets.class.getResource(fallbackClasspathPath).toExternalForm();
		}
	}
}
