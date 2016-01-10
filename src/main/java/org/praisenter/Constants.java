package org.praisenter;

import org.praisenter.utility.RuntimeProperties;

public final class Constants {
	
	public static final String ROOT_RELATIVE_PATH = "Praisenter3" + RuntimeProperties.PATH_SEPARATOR;
	public static final String ROOT_PATH = RuntimeProperties.USER_HOME.isEmpty() ? 
			// Praisenter3/
			ROOT_RELATIVE_PATH :
			// /user/home/dir/Praisenter3/
			RuntimeProperties.USER_HOME + RuntimeProperties.PATH_SEPARATOR + ROOT_RELATIVE_PATH;
	

	// /media
	public static final String MEDIA_RELATIVE_PATH = "media" + RuntimeProperties.PATH_SEPARATOR;
	
	// /user/home/dir/Praisenter3/media/
	public static final String MEDIA_ABSOLUTE_PATH = Constants.ROOT_PATH + MEDIA_RELATIVE_PATH;
	
	// /user/home/dir/Praisenter3/media/thumbs/
	public static final String MEDIA_THUMBNAILS_ABSOLUTE_PATH = MEDIA_ABSOLUTE_PATH + Constants.THUMBNAIL_PATH;
	
	
	// storage of thumbnails will be in a separate folder (and individual files)
	// _thumbs/
	public static final String THUMBNAIL_PATH = "_thumbs" + RuntimeProperties.PATH_SEPARATOR;
	public static final String THUMBNAIL_TYPE = "png";
	public static final String THUMBNAIL_NAME = "_thumb." + THUMBNAIL_TYPE;
	public static final int THUMBNAIL_SIZE = 100;
	
	//public static final Image PLACEHOLDER_IMAGE = ClasspathLoader.getImage("/org/praisenter/resources/placeholder.png");
	
	private Constants() {}
}
