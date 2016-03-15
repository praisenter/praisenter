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

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.concurrent.Task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.data.Database;
import org.praisenter.javafx.media.JavaFXMediaImportFilter;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnailSettings;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.song.SongLibrary;
import org.praisenter.utility.ClasspathLoader;

/**
 * Task to perform the loading of resources for Praisenter including media,
 * slides, songs, bibles, etc.
 * @author William Bittle
 * @version 3.0.0
 */
final class ContextLoadingTask extends Task<PraisenterContext> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected PraisenterContext call() throws Exception {
		long t0 = 0;
		long t1 = 0;
		
		updateProgress(0, 4);
		
		// bible loading
		LOGGER.info("Loading bible library");
		t0 = System.nanoTime();
    	updateMessage(Translations.get("loading.library.bibles"));
		Database db = Database.open(Paths.get(Constants.DATABASE_ABSOLUTE_PATH));
		BibleLibrary bibles = new BibleLibrary(db);
		t1 = System.nanoTime();
		updateProgress(1, 4);
		LOGGER.info("Bible library loaded in {} seconds with {} bibles", (t1 - t0) / 1e9, bibles.getBibleCount());
		
		// song loading
		LOGGER.info("Loading song library");
		updateMessage(Translations.get("loading.library.songs"));
		t0 = System.nanoTime();
		SongLibrary songs = SongLibrary.open(Paths.get(Constants.SONGS_ABSOLUTE_PATH));
		t1 = System.nanoTime();
		updateProgress(2, 4);
		LOGGER.info("Song library loaded in {} seconds with {} songs", (t1 - t0) / 1e9, songs.size());

		// media loading
		LOGGER.info("Loading media library");
		updateMessage(Translations.get("loading.library.media"));
		t0 = System.nanoTime();
		Path mediaPath = Paths.get(Constants.MEDIA_ABSOLUTE_PATH);
		MediaThumbnailSettings settings = new MediaThumbnailSettings(
				Constants.MEDIA_THUMBNAIL_SIZE, 
				Constants.MEDIA_THUMBNAIL_SIZE,
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/image-default-thumbnail.png"),
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/music-default-thumbnail.png"),
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/video-default-thumbnail.png"));
    	MediaLibrary media = MediaLibrary.open(mediaPath, new JavaFXMediaImportFilter(mediaPath), settings);
    	t1 = System.nanoTime();
    	updateProgress(3, 4);
    	LOGGER.info("Media library loaded in {} seconds with {} media items", (t1 - t0) / 1e9, media.size());
		
    	// song loading
    	LOGGER.info("Loading slide library");
		updateMessage(Translations.get("loading.library.slides"));
		t0 = System.nanoTime();
		SlideLibrary slides = SlideLibrary.open(Paths.get(Constants.SLIDES_ABSOLUTE_PATH));
		t1 = System.nanoTime();
		updateProgress(4, 4);
		LOGGER.info("Slide library loaded in {} seconds with {} slides", (t1 - t0) / 1e9, slides.size());
    	
		PraisenterContext context = new PraisenterContext(media, songs, bibles, slides);
		LOGGER.info("Context created successfully");
		
		updateMessage(Translations.get("loading.complete"));
		
		return context;
	}
}