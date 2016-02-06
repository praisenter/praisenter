package org.praisenter;

import org.praisenter.utility.RuntimeProperties;

public final class Constants {
	public static final String VERSION = "3.0.0";
	
	public static final String NAME = "Praisenter";
	
	/** The new line character should be consistent to avoid issues cross-platform */
	public static final String NEW_LINE = "\n";
	
	public static final String ROOT_RELATIVE_PATH = "Praisenter3" + RuntimeProperties.PATH_SEPARATOR;
	public static final String ROOT_PATH = RuntimeProperties.USER_HOME.isEmpty() ? 
			// Praisenter3/
			ROOT_RELATIVE_PATH :
			// /user/home/dir/Praisenter3/
			RuntimeProperties.USER_HOME + RuntimeProperties.PATH_SEPARATOR + ROOT_RELATIVE_PATH;
	
	///////////////////////////
	// MEDIA

	// /media
	public static final String MEDIA_RELATIVE_PATH = "media" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/media/
	public static final String MEDIA_ABSOLUTE_PATH = Constants.ROOT_PATH + MEDIA_RELATIVE_PATH;
	
	///////////////////////////
	// SONGS
	
	// /songs
	public static final String SONGS_RELATIVE_PATH = "songs" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/songs
	public static final String SONGS_ABSOLUTE_PATH = Constants.ROOT_PATH + SONGS_RELATIVE_PATH;

	///////////////////////////
	// SLIDES
	
	// /slides
	public static final String SLIDES_RELATIVE_PATH = "slides" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/slides
	public static final String SLIDES_ABSOLUTE_PATH = Constants.ROOT_PATH + SLIDES_RELATIVE_PATH;
	
	private Constants() {}
}
