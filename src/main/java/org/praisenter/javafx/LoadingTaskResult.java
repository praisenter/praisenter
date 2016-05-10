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
package org.praisenter.javafx;

import org.praisenter.bible.BibleLibrary;
import org.praisenter.media.MediaLibrary;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.song.SongLibrary;

/**
 * Represents the result from the loading task.
 * @author William Bittle
 * @version 3.0.0
 */
final class LoadingTaskResult {
	/** The media library */
	private final MediaLibrary mediaLibrary;
	
	/** The song library */
	private final SongLibrary songLibrary;
	
	/** The bible library */
	private final BibleLibrary bibleLibrary;
	
	/** The slide library */
	private final SlideLibrary slideLibrary;

	/**
	 * Full constructor.
	 * @param media the media library
	 * @param songs the song library
	 * @param bibles the bible library
	 * @param slides the slide library
	 */
	public LoadingTaskResult(
			MediaLibrary media, 
			SongLibrary songs, 
			BibleLibrary bibles, 
			SlideLibrary slides) {
		this.mediaLibrary = media;
		this.songLibrary = songs;
		this.bibleLibrary = bibles;
		this.slideLibrary = slides;
	}

	/**
	 * Returns the media library.
	 * @return {@link MediaLibrary}
	 */
	public MediaLibrary getMediaLibrary() {
		return this.mediaLibrary;
	}

	/**
	 * Returns the song library.
	 * @return {@link SongLibrary}
	 */
	public SongLibrary getSongLibrary() {
		return this.songLibrary;
	}

	/**
	 * Returns the bible library.
	 * @return {@link BibleLibrary}
	 */
	public BibleLibrary getBibleLibrary() {
		return this.bibleLibrary;
	}
	
	/**
	 * Returns the slide library.
	 * @return {@link SlideLibrary}
	 */
	public SlideLibrary getSlideLibrary() {
		return this.slideLibrary;
	}
}
