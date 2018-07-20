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
package org.praisenter.javafx.media;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.ThumbnailSettings;
import org.praisenter.data.Tag;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.async.AsyncTaskFactory;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;
import org.praisenter.ui.translations.Translations;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents an Observable wrapper to the media library.
 * <p>
 * This wrapper allows Java FX controls to bind to the list of media items
 * to allow updating of the view from wherever the view is changed.
 * <p>
 * NOTE: modifications to the {@link #getItems()} and success and error handlers
 * will always be performed on the FX UI thread.
 * @author William Bittle
 * @version 3.0.0
 */
public final class ObservableMediaLibrary {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The media library */
	private final MediaLibrary library;
	
	/** The observable list of media items */
	private final ObservableList<MediaListItem> items;
	
	/**
	 * Minimal constructor.
	 * @param library the media library
	 */
	public ObservableMediaLibrary(MediaLibrary library) {
		this.library = library;
		this.items = FXCollections.observableArrayList((mli) -> {
			// supply the list of properties that should trigger the list to do notification
			return new Observable[] {
				mli.mediaProperty(),
				mli.loadedProperty(),
				mli.nameProperty()
			};
		});
		
		// initialize the observable list
		for (Media media : library.all()) {
			this.items.add(new MediaListItem(media));
        }
	}

	/**
	 * Returns the media list item for the given media or null if not found.
	 * @param media the media
	 * @return {@link MediaListItem}
	 */
	private MediaListItem getListItem(Media media) {
		if (media == null) {
			return null;
		}
		MediaListItem mi = null;
    	for (int i = 0; i < items.size(); i++) {
    		MediaListItem item = items.get(i);
    		if (item != null && 
    			item.isLoaded() && 
    			item.getMedia() != null && 
    			item.getMedia().equals(media)) {
    			mi = item;
    			break;
    		}
    	}
    	return mi;
	}
	
	/**
	 * Attempts to add the given path to the media library.
	 * @param path the path to the media
	 * @return {@link AsyncTask}&lt;{@link Media}&gt;
	 */
	public AsyncTask<Media> add(Path path) {
		// sanity check
		if (path != null) {
			// create a "loading" item
			final MediaListItem loading = new MediaListItem(path.getFileName().toString());
			
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				// add it to the items list
				items.add(loading);
			});
			
			// execute the add on a different thread
			AsyncTask<Media> task = new AsyncTask<Media>(MessageFormat.format(Translations.get("task.import"), path.getFileName())) {
				@Override
				protected Media call() throws Exception {
					updateProgress(-1, 0);
					return library.add(path);
				}
			};
			task.addSuccessHandler((e) -> {
				Media media = task.getValue();
				loading.setMedia(media);
				loading.setName(media.getName());
				loading.setLoaded(true);
			});
			task.addCancelledOrFailedHandler((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to import media " + path.toAbsolutePath().toString(), ex);
				items.remove(loading);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}

	/**
	 * Attempts to remove the given media from the media library.
	 * @param media the media to remove
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> remove(Media media) {
		// sanity check
		if (media != null) {
			final MediaListItem item = this.getListItem(media);

			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				// remove the item
				if (item != null) {
					items.remove(item);
				}
			});
			
			// execute the add on a different thread
			AsyncTask<Void> task = new AsyncTask<Void>(MessageFormat.format(Translations.get("task.delete"), media.getName())) {
				@Override
				protected Void call() throws Exception {
					updateProgress(-1, 0);
					library.remove(media);
					return null;
				}
			};
			task.addCancelledOrFailedHandler((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to remove media " + media.getName(), ex);
				// add it back
				items.add(item);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}

	/**
	 * Attempts to rename the given media.
	 * @param media the media to rename
	 * @param name the new name
	 * @return {@link AsyncTask}&lt;{@link Media}&gt;
	 */
	public AsyncTask<Media> rename(Media media, String name) {
		// sanity check
		if (media != null && name != null && name.length() > 0) {
			final MediaListItem item = this.getListItem(media);

			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				// remove the item
				if (item != null) {
					items.remove(item);
				}
			});
			
			// execute the add on a different thread
			AsyncTask<Media> task = new AsyncTask<Media>(MessageFormat.format(Translations.get("task.rename"), media.getName(), name)) {
				@Override
				protected Media call() throws Exception {
					updateProgress(-1, 0);
					return library.rename(media, name);
				}
			};
			task.addSuccessHandler((e) -> {
				// add a new item for the renamed media
				final Media m1 = task.getValue();
		    	items.add(new MediaListItem(m1));
			});
			task.addCancelledOrFailedHandler((e) -> {
				final Throwable ex = task.getException();
				LOGGER.error("Failed to rename media " + media.getName(), ex);
				// add it back
				items.add(item);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}

	// tags
	
	/**
	 * Attempts to add the given tag to the given media.
	 * @param media the media
	 * @param tag the tag to add
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> addTag(Media media, Tag tag) {
		// sanity check
		if (media != null && tag != null) {
			// NOTE: don't bother monitoring tag-add
			AsyncTask<Void> task = new AsyncTask<Void>() {
				@Override
				protected Void call() throws Exception {
					library.addTag(media, tag);
					return null;
				}
			};
			task.addCancelledOrFailedHandler((e) -> {
				final Throwable ex = task.getException();
				LOGGER.error("Failed to add tag " + tag.getName() + " to media " + media.getName(), ex);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}
	
	/**
	 * Attempts to remove the given tag from the given media.
	 * @param media the media
	 * @param tag the tag to remove
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> removeTag(Media media, Tag tag) {
		// sanity check
		if (media != null && tag != null) {
			// execute the add on a different thread
			// NOTE: don't bother monitoring tag-remove
			AsyncTask<Void> task = new AsyncTask<Void>() {
				@Override
				protected Void call() throws Exception {
					library.removeTag(media, tag);
					return null;
				}
			};
			task.addCancelledOrFailedHandler((e) -> {
				final Throwable ex = task.getException();
				LOGGER.error("Failed to remove tag " + tag.getName() + " from media " + media.getName(), ex);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}
	
	// import/export

	/**
	 * Exports the given media to the given path.
	 * @param path the path to export to (zip file)
	 * @param media the media to export
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> exportMedia(Path path, List<Media> media) {
		// sanity check
		if (path != null && media != null && media.size() > 0) {
			// execute the add on a different thread
			AsyncTask<Void> task = new AsyncTask<Void>(
					media.size() > 1 
					? MessageFormat.format(Translations.get("task.export.multiple.media"), media.size())
					: MessageFormat.format(Translations.get("task.export"), media.get(0).getName())) {
				@Override
				protected Void call() throws Exception {
					updateProgress(-1, 0);
					library.exportMedia(path, media);
					return null;
				}
			};
			task.addCancelledOrFailedHandler((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to complete export", ex);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}
	
	/**
	 * Exports the given media to the given path.
	 * @param stream the zip stream to write to
	 * @param media the media to export
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> exportMedia(ZipOutputStream stream, List<Media> media) {
		// sanity check
		if (stream != null && media != null && media.size() > 0) {
			// execute the add on a different thread
			AsyncTask<Void> task = new AsyncTask<Void>(
					media.size() > 1 
					? MessageFormat.format(Translations.get("task.export.multiple.media"), media.size())
					: MessageFormat.format(Translations.get("task.export"), media.get(0).getName())) {
				@Override
				protected Void call() throws Exception {
					updateProgress(-1, 0);
					library.exportMedia(stream, media);
					return null;
				}
			};
			task.addCancelledOrFailedHandler((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to complete export", ex);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}

	/**
	 * Imports the given media.
	 * @param path the path to import
	 * @return {@link AsyncTask}&lt;List&lt;{@link Media}&gt;&gt;
	 */
	public AsyncTask<List<Media>> importMedia(Path path) {
		// sanity check
		if (path != null) {
			// create a "loading" item
			final MediaListItem loading = new MediaListItem(path.getFileName().toString());
			
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				// add it to the items list
				items.add(loading);
			});
			
			// execute the add on a different thread
			AsyncTask<List<Media>> task = new AsyncTask<List<Media>>(MessageFormat.format(Translations.get("task.import"), path.getFileName())) {
				@Override
				protected List<Media> call() throws Exception {
					updateProgress(-1, 0);
					return library.importMedia(path);
				}
			};
			task.addSuccessHandler((e) -> {
				items.remove(loading);
				for (Media media : task.getValue()) {
					items.add(new MediaListItem(media));
				}
			});
			task.addCancelledOrFailedHandler((e) -> {
				items.remove(loading);
				Throwable ex = task.getException();
				LOGGER.error("Failed to import media: " + path.toString(), ex);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}
	
	// other
	
	/**
	 * Returns the media for the given id.
	 * @param id the id
	 * @return {@link Media}
	 */
	public Media get(UUID id) {
		return this.library.get(id);
	}

	/**
	 * Returns the media for the given id.
	 * @param id the id
	 * @return {@link MediaListItem}
	 */
	MediaListItem getListItem(UUID id) {
		Media media = this.library.get(id);
		return this.getListItem(media);
	}
	
	/**
	 * Returns a set of all the tags on all media items.
	 * @return Set&lt;{@link Tag}&gt;
	 */
	public Set<Tag> getTags() {
		Set<Tag> tags = new TreeSet<Tag>();
		for (Media media : library.all()) {
			tags.addAll(media.getTags());
        }
		return tags;
	}
	
	/**
	 * Returns the observable list of media.
	 * @return ObservableList&lt;{@link MediaListItem}&gt;
	 */
	ObservableList<MediaListItem> getItems() {
		return this.items;
	}

	/**
	 * Returns the thumbnail settings.
	 * @return {@link ThumbnailSettings}
	 */
	public ThumbnailSettings getThumbnailSettings() {
		return this.library.getThumbnailSettings();
	}
}
