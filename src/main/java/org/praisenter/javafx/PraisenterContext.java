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

import org.praisenter.Tag;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.javafx.async.AsyncTaskExecutor;
import org.praisenter.javafx.bible.ObservableBibleLibrary;
import org.praisenter.javafx.configuration.Configuration;
import org.praisenter.javafx.media.ObservableMediaLibrary;
import org.praisenter.javafx.screen.ScreenManager;
import org.praisenter.javafx.slide.JavaFXSlideThumbnailGenerator;
import org.praisenter.javafx.slide.ObservableSlideLibrary;
import org.praisenter.media.MediaLibrary;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.song.SongLibrary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * Represents the working state of Praisenter.
 * <p>
 * This class contains references to the Bible, Song, and Media libraries
 * and contains a caching mechanism for images.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PraisenterContext {
	/** The Java FX context */
	private final JavaFXContext javaFXContext;
	
	/** The application configuration */
	private final Configuration configuration;

	/** The screen manager */
	private final ScreenManager screenManager;
	
	/** The media library */
	private final ObservableMediaLibrary mediaLibrary;

	/** The observable bible library */
	private final ObservableBibleLibrary bibleLibrary;
	
	/** The slide library */
	private final ObservableSlideLibrary slideLibrary;
	
	/** The image cache */
	private final ImageCache imageCache;
	
	/** The global tag list */
	private final ObservableSet<Tag> tags;
	
	/** The worker thread pool */
	private final AsyncTaskExecutor executor;
	
	/**
	 * Full constructor.
	 * @param javaFxContext the Java FX context
	 * @param configuration the application configuration
	 * @param screenManager the display screen manager
	 * @param imageCache the image cache
	 * @param media the media library
	 * @param bibles the bible library
	 * @param songs the song library
	 * @param slides the slide library
	 */
	public PraisenterContext(
			JavaFXContext javaFxContext,
			Configuration configuration,
			ScreenManager screenManager,
			ImageCache imageCache,
			MediaLibrary media,
			BibleLibrary bibles,
			SongLibrary songs, 
			SlideLibrary slides) {
		this.javaFXContext = javaFxContext;
		this.screenManager = screenManager;
		this.imageCache = imageCache;
		this.configuration = configuration;

		// create a thread pool that we can reuse all over the app
		this.executor = new AsyncTaskExecutor();
		this.mediaLibrary = new ObservableMediaLibrary(media);
		this.bibleLibrary = new ObservableBibleLibrary(bibles);
		// FIXME move the thumbnail settings to the normal SlideLibrary
		JavaFXSlideThumbnailGenerator jfxThumbnailGenerator = new JavaFXSlideThumbnailGenerator(100, 100, this);
		this.slideLibrary = new ObservableSlideLibrary(slides, jfxThumbnailGenerator);
		
		Set<Tag> tags = new TreeSet<Tag>();
		// add all the tags to the main tag set
		if (slides != null) {
			tags.addAll(this.slideLibrary.getTags());
		}
		if (media != null) {
			tags.addAll(this.mediaLibrary.getTags());
		}
		
		this.tags = FXCollections.observableSet(tags);
	}

	/**
	 * Returns the application instance.
	 * @return Application
	 */
	public JavaFXContext getJavaFXContext() {
		return this.javaFXContext;
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
	 * Returns the bible library.
	 * @return {@link ObservableBibleLibrary}
	 */
	public ObservableBibleLibrary getBibleLibrary() {
		return this.bibleLibrary;
	}
	
	/**
	 * Returns the slide library.
	 * @return {@link ObservableSlideLibrary}
	 */
	public ObservableSlideLibrary getSlideLibrary() {
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
	public AsyncTaskExecutor getExecutorService() {
		return this.executor;
	}
}
