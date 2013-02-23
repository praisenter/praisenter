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
package org.praisenter.application;

import org.praisenter.common.utilities.FileUtilities;
import org.praisenter.common.utilities.SystemUtilities;

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
	public static final String SEPARATOR = FileUtilities.getSeparator();
	
	/** The user's home directory */
	public static final String USER_HOME = SystemUtilities.getUserHomeDirectory();
	
	/** The application base path (user/home/dir/Praisenter2 or Praisenter2) */
	public static final String BASE_PATH = (!USER_HOME.isEmpty() ? USER_HOME + SEPARATOR : "") + "Praisenter2";

	/** The thumbnails file name */
	public static final String THUMBNAIL_FILE = "_thumbs.xml";
	
	// config
	
	/** The configuration file location */
	public static final String CONFIGURATION_FILE_LOCATION	= BASE_PATH + SEPARATOR + "config";
	
	/** The log4j configuration file name */
	public static final String LOG4J_FILE_NAME = "log4j.xml";
	
	/** The log4j configuration file name and path */
	public static final String LOG4J_FILE_PATH = CONFIGURATION_FILE_LOCATION + SEPARATOR + LOG4J_FILE_NAME;
	
	/** The log file folder */
	public static final String LOG_FILE_LOCATION = BASE_PATH + SEPARATOR + "logs";
	
	// database
	
	/** The database file location */
	public static final String DATABASE_FILE_LOCATION = BASE_PATH + SEPARATOR + "database";
	
	/** The database file name (foldername in the case of derby) */
	public static final String DATABASE_FILE_NAME = "praisenter";
	
	/** The database file name and path */
	public static final String DATABASE_FILE_PATH = DATABASE_FILE_LOCATION + SEPARATOR + DATABASE_FILE_NAME;
	
	/** The database log file name and path */
	public static final String DATABASE_LOG_FILE_PATH = LOG_FILE_LOCATION + SEPARATOR + "derby.log";
}
