package org.praisenter;

import java.nio.file.FileSystems;

/**
 * Class containing various constants.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Constants {
	/** File path separator */
	public static final String SEPARATOR = FileSystems.getDefault().getSeparator();
	
	// config
	
	/** The configuration file location */
	public static final String CONFIGURATION_FILE_LOCATION	= "config";
	
	/** The log4j file name */
	public static final String LOG4J_FILE_NAME = "log4j.xml";
	
	// database
	
	/** The database file location */
	public static final String DATABASE_FILE_LOCATION = "database";
	
	/** The database file name (foldername in this case) */
	public static final String DATABASE_FILE_NAME = "praisenter";
	
	// media
	
	public static final String MEDIA_LIBRARY_PATH = "media";
	
	public static final String MEDIA_LIBRARY_IMAGE_PATH = MEDIA_LIBRARY_PATH + SEPARATOR + "images";
	public static final String MEDIA_LIBRARY_VIDEO_PATH = MEDIA_LIBRARY_PATH + SEPARATOR + "videos";
	public static final String MEDIA_LIBRARY_AUDIO_PATH = MEDIA_LIBRARY_PATH + SEPARATOR + "audio";
	
	// slides/templates
	
	public static final String SLIDE_PATH = "slides";
	public static final String TEMPLATE_PATH = "templates";
	public static final String BIBLE_TEMPLATE_PATH = TEMPLATE_PATH + SEPARATOR + "bible";
	public static final String SONGS_TEMPLATE_PATH = TEMPLATE_PATH + SEPARATOR + "songs";
	public static final String NOTIFICATIONS_TEMPLATE_PATH = TEMPLATE_PATH + SEPARATOR + "notifications";
}
