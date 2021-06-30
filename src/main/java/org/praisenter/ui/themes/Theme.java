package org.praisenter.ui.themes;

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

public final class Theme implements Comparable<Theme> {
	private static final Logger LOGGER = LogManager.getLogger();

	private static final Pattern THEME_PATTERN = Pattern.compile("^(.+)\\.css$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Theme DEFAULT = new Theme("Default", Theme.class.getResource("/org/praisenter/themes/default.css").toExternalForm());

//	static {
//		Path path = Paths.get(Constants.THEMES_ABSOLUTE_FILE_PATH);
//		try {
//			Files.createDirectories(path);
//		} catch (Exception ex) {
//			LOGGER.warn("Failed to create themes folder.", ex);
//		}
//	}
	
	/**
	 * Returns an unmodifiable list of available themes.
	 * <p>
	 * This includes the default themes and any user-supplied themes placed in the
	 * themes directory.
	 * @return List&lt;{@link Theme}&gt;
	 */
	public static final List<Theme> getAvailableThemes() {
		Path path = Paths.get(Constants.THEMES_ABSOLUTE_FILE_PATH);
		
		// create a listing of all the themes
		List<Theme> themes = new ArrayList<Theme>();
//		
//		// add the default themes
//		themes.add(DEFAULT);
		
		// add any themes in the themes dir
		if (Files.exists(path) && Files.isDirectory(path)) {
			try (DirectoryStream<Path> paths = Files.newDirectoryStream(path)) {
				Iterator<Path> it = paths.iterator();
				while (it.hasNext()) {
					Path file = it.next();
					try {
						if (Files.isRegularFile(file)) {
							Matcher m = THEME_PATTERN.matcher(file.getFileName().toString());
							if (m.matches()) {
								String name = m.group(1);
								themes.add(new Theme(name, file.toUri().toURL().toExternalForm()));
							}
						}
					} catch (Exception ex) {
						LOGGER.warn("Failed to check type of path for path '" + path + "'.", ex);
					}
				}
			} catch (Exception e) {
				LOGGER.warn("Failed to iterate themes in '" + path + "'.", e);
			}
		}
		
		Collections.sort(themes);
		return Collections.unmodifiableList(themes);
	}
	
	/**
	 * Returns the theme with the given name or the {@link #DEFAULT} if it's not found.
	 * @param name the name
	 * @return {@link Theme}
	 */
	public static final Theme getTheme(String name) {
		if (StringManipulator.isNullOrEmpty(name)) {
			return Theme.DEFAULT;
		}
		for (Theme theme : getAvailableThemes()) {
			if (name.equalsIgnoreCase(theme.getName())) {
				return theme;
			}
		}
		return Theme.DEFAULT;
	}
	
	private final String name;
	private final String css;
	
	private Theme(String name, String css) {
		this.name = name;
		this.css = css;
	}

	@Override
	public int compareTo(Theme o) {
		return o.name.compareTo(this.name);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public String getName() {
		return this.name;
	}

	public String getCss() {
		return this.css;
	}
}
