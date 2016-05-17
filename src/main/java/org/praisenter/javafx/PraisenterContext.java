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

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.stage.Stage;

import org.praisenter.Tag;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.javafx.bible.ObservableBibleLibrary;
import org.praisenter.javafx.configuration.Configuration;
import org.praisenter.javafx.media.ObservableMediaLibrary;
import org.praisenter.javafx.screen.ScreenManager;
import org.praisenter.media.MediaLibrary;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.song.Song;
import org.praisenter.song.SongLibrary;

// TODO add an executor service so that at shutdown we can wait for any pending tasks

/**
 * Represents the working state of Praisenter.
 * <p>
 * This class contains references to the Bible, Song, and Media libraries
 * and contains a caching mechanism for images.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PraisenterContext {
	/** The Java FX application instance */
	private final Application application;
	
	/** The Java FX main stage */
	private final Stage stage;
	
	/** The application configuration */
	private final Configuration configuration;

	/** The screen manager */
	private final ScreenManager screenManager;
	
	/** The media library */
	private final ObservableMediaLibrary mediaLibrary;

	/** The bible library */
	private final ObservableBibleLibrary bibleLibrary;
	
	/** The song library */
	private final SongLibrary songLibrary;
	
	/** The slide library */
	private final SlideLibrary slideLibrary;
	
	/** The image cache */
	private final ImageCache imageCache;
	
	/** The global tag list */
	private final ObservableSet<Tag> tags;
	
	/** The worker thread pool */
	private final ThreadPoolExecutor workers;
	
	/**
	 * Full constructor.
	 * @param application the Java FX application instance
	 * @param stage the Java FX main stage
	 * @param configuration the application configuration
	 * @param screenManager the display screen manager
	 * @param media the media library
	 * @param bibles the bible library
	 * @param songs the song library
	 * @param slides the slide library
	 */
	public PraisenterContext(
			Application application,
			Stage stage,
			Configuration configuration,
			ScreenManager screenManager,
			MediaLibrary media,
			BibleLibrary bibles,
			SongLibrary songs, 
			SlideLibrary slides) {
		this.application = application;
		this.stage = stage;
		this.configuration = configuration;
		this.screenManager = screenManager;
		this.imageCache = new ImageCache();
		
		this.workers = new ThreadPoolExecutor(
				2, 
				10, 
				1, 
				TimeUnit.MINUTES, 
				new LinkedBlockingQueue<Runnable>(), 
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						return new PraisenterThread(r);
					}
				});
		
		this.mediaLibrary = new ObservableMediaLibrary(media, workers);
		this.bibleLibrary = new ObservableBibleLibrary(bibles, workers);
		this.songLibrary = songs;
		this.slideLibrary = slides;
		
		Set<Tag> tags = new TreeSet<Tag>();
		
		// add all the tags to the main tag set
		if (this.songLibrary != null) {
			for (Song song : this.songLibrary.all()) {
				tags.addAll(song.getTags());
			}
		}
		if (this.slideLibrary != null) {
			for (Slide slide : this.slideLibrary.all()) {
				tags.addAll(slide.getTags());
			}
		}
		if (this.mediaLibrary != null) {
			tags.addAll(this.mediaLibrary.getTags());
		}
		
		
		this.tags = FXCollections.observableSet(tags);
	}

	/**
	 * Returns the application instance.
	 * @return Application
	 */
	public Application getApplication() {
		return this.application;
	}
	
	/**
	 * Returns the main stage for the application.
	 * @return Stage
	 */
	public Stage getStage() {
		return this.stage;
	}
	
	/**
	 * Returns the application configuration.
	 * @return {@link Configuration}
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}

	/**
	 * Returns the screen manager.
	 * @return {@link ScreenManager}
	 */
	public ScreenManager getScreenManager() {
		return this.screenManager;
	}
	
	/**
	 * Returns the media library.
	 * @return {@link ObservableMediaLibrary}
	 */
	public ObservableMediaLibrary getMediaLibrary() {
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
	 * @return {@link ObservableBibleLibrary}
	 */
	public ObservableBibleLibrary getBibleLibrary() {
		return this.bibleLibrary;
	}
	
	/**
	 * Returns the slide library.
	 * @return {@link SlideLibrary}
	 */
	public SlideLibrary getSlideLibrary() {
		return this.slideLibrary;
	}
	
	/**
	 * Returns the image cache.
	 * @return {@link ImageCache}
	 */
	public ImageCache getImageCache() {
		return this.imageCache;
	}
	
	/**
	 * Returns the global set of tags.
	 * @return ObservableSet&lt;{@link Tag}&gt;
	 */
	public ObservableSet<Tag> getTags() {
		return this.tags;
	}
	
	/**
	 * Returns the worker thread pool.
	 * @return ThreadPoolExecutor
	 */
	public ThreadPoolExecutor getWorkers() {
		return this.workers;
	}
}
