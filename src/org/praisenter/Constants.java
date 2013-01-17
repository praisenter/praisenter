/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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

import java.nio.file.FileSystems;

/**
 * Class containing various constants.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public final class Constants {
	/** Hidden default constructor */
	public Constants() {}
	
	/** File path separator */
	public static final String SEPARATOR = FileSystems.getDefault().getSeparator();
	
	/** The thumbnails file name */
	public static final String THUMBNAIL_FILE = "_thumbs.xml";
	
	// config
	
	/** The configuration file location */
	public static final String CONFIGURATION_FILE_LOCATION	= "config";
	
	/** The log4j configuration file name */
	public static final String LOG4J_FILE_NAME = "log4j.xml";
	
	/** The log4j configuration file name and path */
	public static final String LOG4J_FILE_PATH = Constants.CONFIGURATION_FILE_LOCATION + Constants.SEPARATOR + Constants.LOG4J_FILE_NAME;
	
	/** The log file folder */
	public static final String LOG_FILE_LOCATION = "logs";
	
	// database
	
	/** The database file location */
	public static final String DATABASE_FILE_LOCATION = "database";
	
	/** The database file name (foldername in the case of derby) */
	public static final String DATABASE_FILE_NAME = "praisenter";
	
	/** The database log file name and path */
	public static final String DATABASE_FILE_PATH = DATABASE_FILE_LOCATION + SEPARATOR + DATABASE_FILE_NAME;
	
	/** The database log file name and path */
	public static final String DATABASE_LOG_FILE_PATH = LOG_FILE_LOCATION + SEPARATOR + "derby.log";
	
	// media
	
	/** The media library path */
	public static final String MEDIA_LIBRARY_PATH = "media";
	
	/** The media library images path */
	public static final String MEDIA_LIBRARY_IMAGE_PATH = MEDIA_LIBRARY_PATH + SEPARATOR + "images";
	
	/** The media library videos path */
	public static final String MEDIA_LIBRARY_VIDEO_PATH = MEDIA_LIBRARY_PATH + SEPARATOR + "videos";
	
	/** The media library audio path */
	public static final String MEDIA_LIBRARY_AUDIO_PATH = MEDIA_LIBRARY_PATH + SEPARATOR + "audio";
	
	// slides/templates
	
	/** The slide library slide path */
	public static final String SLIDE_PATH = "slides";
	
	/** The slide library slide template path */
	public static final String TEMPLATE_PATH = "templates";
	
	/** The slide library bible template path */
	public static final String BIBLE_TEMPLATE_PATH = TEMPLATE_PATH + SEPARATOR + "bible";
	
	/** The slide library song template path */
	public static final String SONGS_TEMPLATE_PATH = TEMPLATE_PATH + SEPARATOR + "songs";
	
	/** The slide library notification template path */
	public static final String NOTIFICATIONS_TEMPLATE_PATH = TEMPLATE_PATH + SEPARATOR + "notifications";
}
