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

// TODO verify logging configuration
// TODO setProperty("derby.stream.error.file", Constants.DATABASE_LOG_FILE_PATH);
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
	
	/** Hidden constructor */
	private Constants() {}
}
