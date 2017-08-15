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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.ThumbnailSettings;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.configuration.Configuration;
import org.praisenter.javafx.configuration.ObservableConfiguration;
import org.praisenter.javafx.media.JavaFXMediaImportProcessor;
import org.praisenter.media.MediaImportProcessor;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaLibraryContext;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.song.SongLibrary;
import org.praisenter.tools.Tools;

import javafx.concurrent.Task;

/**
 * Task to perform the loading of resources for Praisenter including media,
 * slides, songs, bibles, etc.
 * @author William Bittle
 * @version 3.0.0
 */
final class LoadingTask extends Task<PraisenterContext> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The JavaFX context */
	private final JavaFXContext javaFXContext;
	
	/** The configuration */
	private final Configuration configuration;
	
	/**
	 * Minimal constructor.
	 * @param javaFXContext the JavaFX context information
	 * @param configuration the configuration
	 */
	public LoadingTask(JavaFXContext javaFXContext, Configuration configuration) {
		this.javaFXContext = javaFXContext;
		this.configuration = configuration;
	}
	
	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected PraisenterContext call() throws Exception {
		long t0 = 0;
		long t1 = 0;
		int n = 5;
		int i = 0;
		
		updateProgress(i++, n);
		
		// build the observable configuration
		ObservableConfiguration configuration = new ObservableConfiguration(this.configuration);
		
		// setup the thumbnail settings
		ThumbnailSettings thumbnailSettings = new ThumbnailSettings(
				Constants.THUMBNAIL_SIZE, 
				Constants.THUMBNAIL_SIZE);
		
		// tools
		LOGGER.info("Loading tools");
		updateMessage(Translations.get("loading.tools"));
		t0 = System.nanoTime();
		Tools tools = Tools.open(Paths.get(Constants.TOOLS_ABSOLUTE_PATH));
		t1 = System.nanoTime();
		updateProgress(i++, n);
		LOGGER.info("Tools loaded in {} seconds", (t1 - t0) / 1e9);
		
		// build the media context and import processor
		MediaLibraryContext mediaLibraryContext = new MediaLibraryContext(this.configuration, thumbnailSettings, tools);
		MediaImportProcessor mediaImportProcessor = new JavaFXMediaImportProcessor(mediaLibraryContext);
		
		// bible loading
		LOGGER.info("Loading bible library");
		t0 = System.nanoTime();
    	updateMessage(Translations.get("loading.library.bibles"));
		BibleLibrary bibles = BibleLibrary.open(Paths.get(Constants.BIBLES_ABSOLUTE_PATH));
		t1 = System.nanoTime();
		updateProgress(i++, n);
		LOGGER.info("Bible library loaded in {} seconds with {} bibles", (t1 - t0) / 1e9, bibles.size());
		
		// song loading
		LOGGER.info("Loading song library");
		updateMessage(Translations.get("loading.library.songs"));
		t0 = System.nanoTime();
		SongLibrary songs = SongLibrary.open(Paths.get(Constants.SONGS_ABSOLUTE_PATH));
		t1 = System.nanoTime();
		updateProgress(i++, n);
		LOGGER.info("Song library loaded in {} seconds with {} songs", (t1 - t0) / 1e9, songs.size());

		// media loading
		LOGGER.info("Loading media library");
		updateMessage(Translations.get("loading.library.media"));
		t0 = System.nanoTime();
		Path mediaPath = Paths.get(Constants.MEDIA_ABSOLUTE_PATH);
    	MediaLibrary media = MediaLibrary.open(mediaPath, mediaLibraryContext, mediaImportProcessor);
    	t1 = System.nanoTime();
    	updateProgress(i++, n);
    	LOGGER.info("Media library loaded in {} seconds with {} media items", (t1 - t0) / 1e9, media.size());
		
    	// song loading (this is dependent on the media library and the image cache)
    	LOGGER.info("Loading slide library");
		updateMessage(Translations.get("loading.library.slides"));
		t0 = System.nanoTime();
		SlideLibrary slides = SlideLibrary.open(Paths.get(Constants.SLIDES_ABSOLUTE_PATH));
		t1 = System.nanoTime();
		updateProgress(i++, n);
		LOGGER.info("Slide library loaded in {} seconds with {} slides", (t1 - t0) / 1e9, slides.size());
		
		LOGGER.info("Building the application context.");
		// build the context
		PraisenterContext context = new PraisenterContext(
				this.javaFXContext,
				configuration,
				tools,
				media,
				bibles,
				songs,
				slides);

		updateMessage(Translations.get("loading.complete"));
		
		return context;
	}
}
