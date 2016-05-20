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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.FailedOperation;
import org.praisenter.Tag;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;

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
	
	/** The thread service */
	private final ExecutorService service;
	
	/** The initial tags */
	private final Set<Tag> tags;
	
	/** The observable list of media items */
	private final ObservableList<MediaListItem> items = FXCollections.observableArrayList();
	
	/**
	 * Minimal constructor.
	 * @param library the media library
	 * @param service the thread service
	 */
	public ObservableMediaLibrary(MediaLibrary library, ExecutorService service) {
		this.library = library;
		this.service = service;
		this.tags = new TreeSet<Tag>();
		
		// add all the current media to the observable list
		if (library != null) {
			for (Media media : library.all()) {
				this.items.add(new MediaListItem(media));
				this.tags.addAll(media.getMetadata().getTags());
	        }
		}
	}
	
	/**
	 * Attempts to add the given path to the media library.
	 * <p>
	 * The onSuccess method will be called when the import is successful. The
	 * onError method will be called if an error occurs during import.
	 * <p>
	 * An item is added to the observable list to represent that the media is
	 * being imported. This item will be removed when the import completes, whether
	 * its successful or not.
	 * <p>
	 * The observable list of media will be updated if the media is successfully
	 * added to the MediaLibrary. Both the update of the observable list and
	 * the onSuccess and onError methods will be performed on the Java FX UI
	 * thread.
	 * @param path the path to the media
	 * @param onSuccess called when the media is imported successfully
	 * @param onError called when the media failed to be imported
	 */
	public void add(Path path, Consumer<Media> onSuccess, BiConsumer<Path, Throwable> onError) {
		// create a "loading" item
		final MediaListItem loading = new MediaListItem(path.getFileName().toString());
		
		// changes to the list should be done on the FX UI Thread
		Fx.runOnFxThead(() -> {
			// add it to the items list
			items.add(loading);
		});
		
		// execute the add on a different thread
		Task<Media> task = new Task<Media>() {
			@Override
			protected Media call() throws Exception {
				return library.add(path);
			}
		};
		task.setOnSucceeded((e) -> {
			Media media = task.getValue();
			MediaListItem success = new MediaListItem(media);
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				items.remove(loading);
				items.add(success);
				if (onSuccess != null) {
					onSuccess.accept(media);
				}
			});
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to import media " + path.toAbsolutePath().toString(), ex);
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				items.remove(loading);
				if (onError != null) {
					onError.accept(path, ex);
				}
			});
		});
		this.service.submit(task);
	}
	
	/**
	 * Attempts to add the given paths to the media library.
	 * <p>
	 * The onSuccess method will be called with all the successful imports. The
	 * onError method will be called with all the failed imports. The onSuccess
	 * and onError methods will only be called if there's at least one successful
	 * or one failed import respectively.
	 * <p>
	 * Items are added to the observable list to represent that the media is
	 * being imported. These items will be removed when the import completes, whether
	 * they are successful or not.
	 * <p>
	 * The observable list of media will be updated if the media is successfully
	 * added to the MediaLibrary. Both the update of the observable list and
	 * the onSuccess and onError methods will be performed on the Java FX UI
	 * thread.
	 * @param paths the paths to the media
	 * @param onSuccess called with the media that was imported successfully
	 * @param onError called with the media that failed to be imported
	 */
	public void add(List<Path> paths, Consumer<List<Media>> onSuccess, Consumer<List<FailedOperation<Path>>> onError) {
		// create the "loading" items
		List<MediaListItem> loadings = new ArrayList<MediaListItem>();
		for (Path path : paths) {
			loadings.add(new MediaListItem(path.getFileName().toString()));
		}
		
		// changes to the list should be done on the FX UI Thread
		Fx.runOnFxThead(() -> {
			// add it to the items list
			items.addAll(loadings);
		});
		
		List<Media> successes = new ArrayList<Media>();
		List<FailedOperation<Path>> failures = new ArrayList<FailedOperation<Path>>();
		
		// execute the add on a different thread
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				for (Path path : paths) {
					try {
						Media media = library.add(path);
						successes.add(media);
						Fx.runOnFxThead(() -> {
							// remove the loading item
							items.remove(new MediaListItem(path.getFileName().toString()));
							// add the real item
							items.add(new MediaListItem(media));
						});
					} catch (Exception ex) {
						LOGGER.error("Failed to import media " + path.toAbsolutePath().toString(), ex);
						failures.add(new FailedOperation<Path>(path, ex));
						Fx.runOnFxThead(() -> {
							// remove the loading item
							items.remove(new MediaListItem(path.getFileName().toString()));
						});
					}
				}
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			Fx.runOnFxThead(() -> {
				// notify successes and failures
				if (onSuccess != null && successes.size() > 0) {
					onSuccess.accept(successes);
				}
				if (onError != null && failures.size() > 0) {
					onError.accept(failures);
				}
			});
		});
		task.setOnFailed((e) -> {
			// this shouldn't happen because we should catch all exceptions
			// inside the task, but lets put it here just in case
			Throwable ex = task.getException();
			LOGGER.error("Failed to finish import", ex);
			failures.add(new FailedOperation<Path>(null, ex));
			Fx.runOnFxThead(() -> {
				if (onError != null) {
					onError.accept(failures);
				}
			});
		});
		this.service.submit(task);
	}
	
	/**
	 * Attempts to remove the given media from the media library.
	 * <p>
	 * The onSuccess method will be called on success. The onError method will be 
	 * called if an error occurs during removal (like it being in use).
	 * <p>
	 * Removing a media item is typically a fast operation so no loading items are
	 * added.
	 * <p>
	 * The observable list of media will be updated if the media is successfully
	 * removed from the MediaLibrary. Both the update of the observable list and
	 * the onSuccess and onError methods will be performed on the Java FX UI
	 * thread.
	 * @param media the media to remove
	 * @param onSuccess called when the media is successfully removed
	 * @param onError called when the media failed to be removed
	 */
	public void remove(Media media, Runnable onSuccess, BiConsumer<Media, Throwable> onError) {
		// execute the add on a different thread
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				library.remove(media);
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			MediaListItem success = new MediaListItem(media);
			Fx.runOnFxThead(() -> {
				items.remove(success);
				if (onSuccess != null) {
					onSuccess.run();
				}
			});
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to remove media " + media.getMetadata().getName(), ex);
			Fx.runOnFxThead(() -> {
				if (onError != null) {
					onError.accept(media, ex);
				}
			});
		});
		this.service.submit(task);
	}
	
	/**
	 * Attempts to remove all the given media from the media library.
	 * <p>
	 * The onSuccess method will be called on success. The onError method will be 
	 * called if an error occurs during removal (like it being in use).
	 * <p>
	 * Removing a media item is typically a fast operation so no loading items are
	 * added.
	 * <p>
	 * The observable list of media will be updated if the media is successfully
	 * removed from the MediaLibrary. Both the update of the observable list and
	 * the onSuccess and onError methods will be performed on the Java FX UI
	 * thread.
	 * @param media the media to remove
	 * @param onSuccess called when the media is successfully removed
	 * @param onError called when the media failed to be removed
	 */
	public void remove(List<Media> media, Runnable onSuccess, Consumer<List<FailedOperation<Media>>> onError) {
		List<FailedOperation<Media>> failures = new ArrayList<FailedOperation<Media>>();
		
		// execute the add on a different thread
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				for (Media m : media) {
					try {
						library.remove(m);
						Fx.runOnFxThead(() -> {
							// remove the item
							items.remove(new MediaListItem(m));
						});
					} catch (Exception ex) {
						LOGGER.error("Failed to remove media " + m.getMetadata().getName(), ex);
						failures.add(new FailedOperation<Media>(m, ex));
					}
				}
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			Fx.runOnFxThead(() -> {
				// notify any failures
				if (onError != null && failures.size() > 0) {
					onError.accept(failures);
				}
			});
		});
		task.setOnFailed((e) -> {
			// this shouldn't happen because we should catch all exceptions
			// inside the task, but lets put it here just in case
			Throwable ex = task.getException();
			LOGGER.error("Failed to complete removal", ex);
			failures.add(new FailedOperation<Media>(null, ex));
			Fx.runOnFxThead(() -> {
				if (onError != null) {
					onError.accept(failures);
				}
			});
		});
		this.service.submit(task);
	}
	
	/**
	 * Attempts to rename the given media.
	 * <p>
	 * The onSuccess method will be called on success. The onError method will be 
	 * called if an error occurs during renaming (like it being in use).
	 * <p>
	 * Renaming a media item is typically a fast operation so no loading items are
	 * added.
	 * <p>
	 * The observable list of media will be updated if the media is successfully
	 * renamed from the MediaLibrary. Both the update of the observable list and
	 * the onSuccess and onError methods will be performed on the Java FX UI
	 * thread.
	 * @param media the media to rename
	 * @param name the new name
	 * @param onSuccess called when the media is successfully renamed
	 * @param onError called when the media failed to be renamed
	 */
	public void rename(Media media, String name, Consumer<Media> onSuccess, BiConsumer<Media, Throwable> onError) {
		final Media m0 = media;
		// execute the add on a different thread
		Task<Media> task = new Task<Media>() {
			@Override
			protected Media call() throws Exception {
				return library.rename(m0, name);
			}
		};
		task.setOnSucceeded((e) -> {
			final Media m1 = task.getValue();
			// changes to the list should be done on the FX UI Thread
			Platform.runLater(() -> {
				// update the list item
				int index = -1;
		    	for (int i = 0; i < items.size(); i++) {
		    		MediaListItem item = items.get(i);
		    		if (item.media.getMetadata().getId() == m0.getMetadata().getId()) {
		    			index = i;
		    			break;
		    		}
		    	}
		    	if (index >= 0) {
		    		items.set(index, new MediaListItem(m1));
		    	} else {
		    		items.add(new MediaListItem(m1));
		    	}
		    	
		    	if (onSuccess != null) {
					onSuccess.accept(m1);
				}
			});
		});
		task.setOnFailed((e) -> {
			final Throwable ex = task.getException();
			LOGGER.error("Failed to rename media " + media.getMetadata().getName(), ex);
			// run everything on the FX UI Thread
			Platform.runLater(() -> {
				if (onError != null) {
					onError.accept(m0, ex);
				}
			});
		});
		this.service.submit(task);
	}
	
	// mutators
	
	/**
	 * Returns an unmodifiable set of the initial tags
	 * at the time of loading the media library.
	 * @return Set&lt;{@link Tag}&gt;
	 */
	public Set<Tag> getTags() {
		return Collections.unmodifiableSet(this.tags);
	}
	
	/**
	 * Returns the observable list of media.
	 * @return ObservableList&lt;{@link MediaListItem}&gt;
	 */
	public ObservableList<MediaListItem> getItems() {
		return this.items;
	}
}
