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
package org.praisenter;

import org.praisenter.utility.RuntimeProperties;

/**
 * Class containing Praisenter constants.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Constants {
	/** Hidden constructor */
	private Constants() {}
	
	/** The application name */
	public static final String NAME = "Praisenter";
	
	/** The new line character should be consistent to avoid issues cross-platform */
	public static final String NEW_LINE = "\n";

	/** The relative path to the root of all the Praisenter files */
	public static final String ROOT_RELATIVE_PATH = ".praisenter3" + RuntimeProperties.PATH_SEPARATOR;
	
	/** The absolute path to the root of all the Praisenter files */
	public static final String ROOT_PATH = RuntimeProperties.USER_HOME.isEmpty() ? 
			// Praisenter3/
			ROOT_RELATIVE_PATH :
			// /user/home/dir/Praisenter3/
			RuntimeProperties.USER_HOME + RuntimeProperties.PATH_SEPARATOR + ROOT_RELATIVE_PATH;
	
	public static final String WEBSITE = "https://github.com/wnbittle/praisenter";
	
	///////////////////////////
	// LOGGING
	
	/** The relative path to the logs directory */
	public static final String LOGS_RELATIVE_PATH = "logs" + RuntimeProperties.PATH_SEPARATOR;
	
	/** The absolute path to the logs directory */
	public static final String LOGS_ABSOLUTE_PATH = Constants.ROOT_PATH + Constants.LOGS_RELATIVE_PATH;
	
	/** The logging configuration file name */
	public static final String LOGS_CONFIGURATION_FILENAME = "log4j2.xml";
	
	/** The path to the logging configuration file on the classpath */
	public static final String LOGS_CONFIGURATION_ON_CLASSPATH = "/org/praisenter/config/" + LOGS_CONFIGURATION_FILENAME;

	
	
	///////////////////////////
	// UPGRADE
	
	/** The relative path to the upgrade directory */
	public static final String UPGRADE_RELATIVE_PATH = "upgrade" + RuntimeProperties.PATH_SEPARATOR;
	
	/** The absolute path to the upgrade directory */
	public static final String UPGRADE_ABSOLUTE_PATH = Constants.ROOT_PATH + Constants.UPGRADE_RELATIVE_PATH;
	
	/** The last runtime version of the application */
	public static final String UPGRADE_VERSION_FILENAME = ".version";
	
	/** The relative path to the upgrade/archive directory */
	public static final String UPGRADE_ARCHIVE_RELATIVE_PATH = Constants.UPGRADE_RELATIVE_PATH + "archive" + RuntimeProperties.PATH_SEPARATOR;
	
	/** The absolute path to the upgrade/archive directory */
	public static final String UPGRADE_ARCHIVE_ABSOLUTE_PATH = Constants.ROOT_PATH + Constants.UPGRADE_ARCHIVE_RELATIVE_PATH;
	
	/** The URL to check for a new released version of the application */
	public static final String UPGRADE_VERSION_CHECK_URL = "https://raw.githubusercontent.com/wnbittle/praisenter/master/release-version.txt";

	
	///////////////////////////
	// LOCALES
	
	// /locales
	/** The relative path to the locales directory */
	public static final String LOCALES_RELATIVE_PATH = "locales" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/locales
	/** The absolute path to the locales directory */
	public static final String LOCALES_ABSOLUTE_PATH = Constants.ROOT_PATH + LOCALES_RELATIVE_PATH;
	
	/** The default locale file name */
	public static final String LOCALES_DEFAULT_LOCALE_FILENAME = "messages.properties";
	
	/** The path to the default locale file on the classpath */
	public static final String LOCALES_DEFAULT_LOCALE_ON_CLASSPATH = "/org/praisenter/translations/" + Constants.LOCALES_DEFAULT_LOCALE_FILENAME;

	
	
	///////////////////////////
	// THEMES
	
	// /themes
	/** The relative path to the themes directory */
	public static final String THEMES_RELATIVE_PATH = "themes" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/themes
	/** The absolute path to the themes directory */
	public static final String THEMES_ABSOLUTE_PATH = Constants.ROOT_PATH + THEMES_RELATIVE_PATH;
	
	/** The default theme file name */
	public static final String THEMES_DEFAULT_THEME_FILENAME = "default.css";
	
	/** The path to the default theme file on the classpath */
	public static final String THEMES_DEFAULT_THEME_ON_CLASSPATH = "/org/praisenter/themes/" + Constants.THEMES_DEFAULT_THEME_FILENAME;
	
	/** The default theme file name */
	public static final String THEMES_FLAT_DARK_THEME_FILENAME = "flat-dark.css";
	
	/** The path to the default theme file on the classpath */
	public static final String THEMES_FLAT_DARK_THEME_ON_CLASSPATH = "/org/praisenter/themes/" + Constants.THEMES_FLAT_DARK_THEME_FILENAME;
	
	/** The default theme file name */
	public static final String THEMES_FLAT_LIGHT_THEME_FILENAME = "flat-light.css";
	
	/** The path to the default theme file on the classpath */
	public static final String THEMES_FLAT_LIGHT_THEME_ON_CLASSPATH = "/org/praisenter/themes/" + Constants.THEMES_FLAT_LIGHT_THEME_FILENAME;
	
	
	///////////////////////////
	// OTHER

	/** The thumbnail size */
	public static final int THUMBNAIL_SIZE = 100;
	
	/** Format name for use in any save-able format in the application to differentiate between other formats */
	public static final String FORMAT_NAME = "praisenter";

	/** The format property name in praisenter formats */
	public static final String FORMAT_PROPERTY_NAME = "@format";
	
	/** The version property name in praisenter formats */
	public static final String VERSION_PROPERTY_NAME = "@version";	
}
