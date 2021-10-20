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
package org.praisenter.configuration;

/**
 * Represents a simple application setting.
 * @author William Bittle
 * @version 3.0.0
 */
public enum Setting {
	/** The application width */
	APP_WIDTH,
	
	/** The application height */
	APP_HEIGHT,
	
	/** The application x position */
	APP_X,
	
	/** The application y position */
	APP_Y,
	
	/** The application is maximized */
	APP_MAXIMIZED,
	
	/** The application theme */
	APP_THEME,
	
	/** The application language */
	APP_LANGUAGE,
	
	/** Debug mode; enhances logging and user feedback */
	APP_DEBUG_MODE,
	
	// bible
	
	/** The primary bible */
	BIBLE_PRIMARY,
	
	/** The secondary bible */
	BIBLE_SECONDARY,
	
	/** If the renumber warning should show */
	BIBLE_SHOW_RENUMBER_WARNING,
	
	/** If the reorder warning should show */
	BIBLE_SHOW_REORDER_WARNING,
	
	// media
	
	/** If media transcoding is enabled */
	MEDIA_TRANSCODING_ENABLED,
	
	/** The video media target extension (which will imply format) */
	MEDIA_TRANSCODING_VIDEO_EXTENSION,
	
	/** The audio media target extension (which will imply format) */
	MEDIA_TRANSCODING_AUDIO_EXTENSION,
	
	/** The video media FFmpeg command */
	MEDIA_TRANSCODING_VIDEO_COMMAND,
	
	/** The audio media FFmpeg command */
	MEDIA_TRANSCODING_AUDIO_COMMAND,
	
	/** The command used to extract frames from a video to use for thumbnails */
	MEDIA_VIDEO_FRAME_EXTRACT_COMMAND,
	
	// displays
	
	/** The number of displays */
	DISPLAY_COUNT,

	/** A mapping of alternate displays */
	DISPLAY_ASSIGNMENTS,
	
	/** The set of resolutions */
	DISPLAY_RESOLUTIONS,
	
	// other
	
	/** Determines if the application should wait until the current transition completes before showing the next slide */
	PRESENT_WAIT_FOR_TRANSITIONS_TO_COMPLETE,
}
