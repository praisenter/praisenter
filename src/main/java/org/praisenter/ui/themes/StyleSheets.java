package org.praisenter.ui.themes;

import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.utility.StringManipulator;

import javafx.scene.Scene;

// TODO rename this to Layouts or PraisenterTheme
public final class StyleSheets {
	private static final Logger LOGGER = LogManager.getLogger();

//	private static final Pattern THEME_PATTERN = Pattern.compile("^(.+)\\.css$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
//	private static final Styles DEFAULT = new Styles("default", Styles.class.getResource("/org/praisenter/themes/default.css").toExternalForm());
	
	public static final String DEFAULT = fromFileSystem(Paths.get(Constants.STYLES_ABSOLUTE_PATH, Constants.STYLES_BASE_FILENAME), Constants.STYLES_BASE_ON_CLASSPATH);
	public static final String ICONS = fromFileSystem(Paths.get(Constants.STYLES_ABSOLUTE_PATH, Constants.STYLES_ICONS_FILENAME), Constants.STYLES_ICONS_ON_CLASSPATH);

	public static final String[] STYLES = new String[] { 
		ICONS,
		DEFAULT
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
	
//	/**
//	 * Returns an unmodifiable list of available themes.
//	 * <p>
//	 * This includes the default themes and any user-supplied themes placed in the
//	 * themes directory.
//	 * @return List&lt;{@link Styles}&gt;
//	 */
//	public static final List<Styles> getAvailableThemes() {
//		Path path = Paths.get(Constants.THEMES_ABSOLUTE_PATH);
//		
//		// create a listing of all the themes
//		List<Styles> themes = new ArrayList<Styles>();
//
//		// add any themes in the themes dir
//		if (Files.exists(path) && Files.isDirectory(path)) {
//			try (DirectoryStream<Path> paths = Files.newDirectoryStream(path)) {
//				Iterator<Path> it = paths.iterator();
//				while (it.hasNext()) {
//					Path file = it.next();
//					try {
//						if (Files.isRegularFile(file)) {
//							Matcher m = THEME_PATTERN.matcher(file.getFileName().toString());
//							if (m.matches()) {
//								String name = m.group(1);
//								themes.add(new Styles(name, file.toUri().toURL().toExternalForm()));
//							}
//						}
//					} catch (Exception ex) {
//						LOGGER.warn("Failed to check type of path for path '" + path + "'.", ex);
//					}
//				}
//			} catch (Exception e) {
//				LOGGER.warn("Failed to iterate themes in '" + path + "'.", e);
//			}
//		}
//		
//		Collections.sort(themes);
//		return Collections.unmodifiableList(themes);
//	}
//	
//	/**
//	 * Returns the theme with the given name or the {@link #DEFAULT} if it's not found.
//	 * @param name the name
//	 * @return {@link Styles}
//	 */
//	public static final Styles getTheme(String name) {
//		if (StringManipulator.isNullOrEmpty(name)) {
//			return Styles.DEFAULT;
//		}
//		for (Styles theme : getAvailableThemes()) {
//			if (name.equalsIgnoreCase(theme.getName())) {
//				return theme;
//			}
//		}
//		return Styles.DEFAULT;
//	}
//	
//	private final String name;
//	private final String css;
//	
//	private Styles(String name, String css) {
//		this.name = name;
//		this.css = css;
//	}
//
//	@Override
//	public int compareTo(Styles o) {
//		return o.name.compareTo(this.name);
//	}
//	
//	@Override
//	public String toString() {
//		return this.name;
//	}
//	
//	public String getName() {
//		return this.name;
//	}
//
//	public String getCss() {
//		return this.css;
//	}
}
