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

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.praisenter.bible.BibleLibrary;
import org.praisenter.media.MediaLibrary;
import org.praisenter.song.SongLibrary;

/**
 * Represents the working state of Praisenter.
 * <p>
 * This class contains references to the Bible, Song, and Media libraries
 * and contains a caching mechanism for images.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PraisenterContext {
	/** The media library */
	private final MediaLibrary mediaLibrary;
	
	/** The song library */
	private final SongLibrary songLibrary;
	
	/** The bible library */
	private final BibleLibrary bibleLibrary;
	
	// TODO slide library
	
	/** The image cache */
	private final ImageCache imageCache;
	
	/**
	 * Full constructor.
	 * @param ml the media library
	 * @param sl the song library
	 * @param bl the bible library
	 */
	public PraisenterContext(MediaLibrary ml, SongLibrary sl, BibleLibrary bl) {
		this.mediaLibrary = ml;
		this.songLibrary = sl;
		this.bibleLibrary = bl;
		this.imageCache = new ImageCache();
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
	 * Returns the image cache.
	 * @return {@link ImageCache}
	 */
	public ImageCache getImageCache() {
		return this.imageCache;
	}
}
