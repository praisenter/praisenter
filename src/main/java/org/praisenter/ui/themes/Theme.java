/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

/**
 * Represents a theme for the application using Java FX and css styling.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Theme implements Comparable<Theme> {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/** A regex for matching theme file names */
	private static final Pattern THEME_PATTERN = Pattern.compile("^(.+)\\.css$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	
	/** The default theme */
	public static final Theme DEFAULT = new Theme("Default", Theme.class.getResource("/org/praisenter/themes/default.css").toExternalForm());
	
	static {
		Path path = Paths.get(Constants.THEMES_ABSOLUTE_FILE_PATH);
		try {
			Files.createDirectories(path);
		} catch (Exception ex) {
			LOGGER.warn("Failed to create themes folder.", ex);
		}
	}
	
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
		
		// add the default themes
		themes.add(DEFAULT);
		
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
	
	/** The theme name */
	private final String name;
	
	/** The URI to the theme css */	
	private final String css;
	
	/**
	 * Full constructor.
	 * @param name the theme name
	 * @param css the URI to the theme
	 */
	private Theme(String name, String css) {
		this.name = name;
		this.css = css;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Theme o) {
		return o.name.compareTo(this.name);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/**
	 * Returns the name of the theme.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns a string URI to the css file for the theme.
	 * @return String
	 */
	public String getCss() {
		return this.css;
	}
}
