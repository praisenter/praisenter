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
	public static final String ROOT_RELATIVE_PATH = "Praisenter3" + RuntimeProperties.PATH_SEPARATOR;
	
	/** The absolute path to the root of all the Praisenter files */
	public static final String ROOT_PATH = RuntimeProperties.USER_HOME.isEmpty() ? 
			// Praisenter3/
			ROOT_RELATIVE_PATH :
			// /user/home/dir/Praisenter3/
			RuntimeProperties.USER_HOME + RuntimeProperties.PATH_SEPARATOR + ROOT_RELATIVE_PATH;
	
	///////////////////////////
	// LOGGING
	
	/** The relative path to the logs directory */
	public static final String LOGS_RELATIVE_PATH = "logs" + RuntimeProperties.PATH_SEPARATOR;
	
	/** The absolute path to the logs directory */
	public static final String LOGS_ABSOLUTE_PATH = Constants.ROOT_PATH + "logs";

	///////////////////////////
	// SEARCH INDEX
	
	// /_index
	/** The relative path to the search index directory */
	public static final String SEARCH_INDEX_RELATIVE_PATH = "_index";
	
	// /user/home/dir/Praisenter3/_index
	/** The absolute path to the search index directory */
	public static final String SEARCH_INDEX_ABSOLUTE_PATH = Constants.ROOT_PATH + SEARCH_INDEX_RELATIVE_PATH;
	
	///////////////////////////
	// CONFIGURATION
	
	// /config
	/** The relative path to the configuration file */
	public static final String CONFIG_RELATIVE_PATH = "config";
	
	// /user/home/dir/Praisenter3/config
	/** The absolute path to the configuration file */
	public static final String CONFIG_ABSOLUTE_PATH = Constants.ROOT_PATH + CONFIG_RELATIVE_PATH;

	// /locales
	/** The relative path to the locales directory */
	public static final String LOCALES_RELATIVE_FILE_PATH = "locales" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/locales
	/** The absolute path to the locales directory */
	public static final String LOCALES_ABSOLUTE_FILE_PATH = Constants.ROOT_PATH + LOCALES_RELATIVE_FILE_PATH;

	// /themes
	/** The relative path to the themes directory */
	public static final String THEMES_RELATIVE_FILE_PATH = "themes" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/themes
	/** The absolute path to the themes directory */
	public static final String THEMES_ABSOLUTE_FILE_PATH = Constants.ROOT_PATH + THEMES_RELATIVE_FILE_PATH;
	
	///////////////////////////
	// MEDIA

	// /media
	/** The relative path to the media folder */
	public static final String MEDIA_RELATIVE_PATH = "media" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/media/
	/** The absolute path to the media folder */
	public static final String MEDIA_ABSOLUTE_PATH = Constants.ROOT_PATH + MEDIA_RELATIVE_PATH;
	
	///////////////////////////
	// SONGS
	
	// /songs
	/** The relative path to the songs folder */
	public static final String SONGS_RELATIVE_PATH = "songs" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/songs
	/** The absolute path to the songs folder */
	public static final String SONGS_ABSOLUTE_PATH = Constants.ROOT_PATH + SONGS_RELATIVE_PATH;

	///////////////////////////
	// SLIDES
	
	// /slides
	/** The relative path to the slides folder */
	public static final String SLIDES_RELATIVE_PATH = "slides" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/slides
	/** The absolute path to the slides folder */
	public static final String SLIDES_ABSOLUTE_PATH = Constants.ROOT_PATH + SLIDES_RELATIVE_PATH;
	
	///////////////////////////
	// SLIDESHOWS
	
	// /slides
	/** The relative path to the slides folder */
	public static final String SLIDESHOWS_RELATIVE_PATH = "slideshows" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/slides
	/** The absolute path to the slides folder */
	public static final String SLIDESHOWS_ABSOLUTE_PATH = Constants.ROOT_PATH + SLIDESHOWS_RELATIVE_PATH;
	
	///////////////////////////
	// BIBLES
	
	// /bibles
	/** The relative path to the database */
	public static final String BIBLES_RELATIVE_PATH = "bibles" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/bibles
	/** The absolute path to the database */
	public static final String BIBLES_ABSOLUTE_PATH = Constants.ROOT_PATH + BIBLES_RELATIVE_PATH;
	
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
