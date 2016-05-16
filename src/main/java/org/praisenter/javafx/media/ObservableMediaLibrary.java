package org.praisenter.javafx.media;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.xml.bind.JAXBException;

import org.praisenter.FailedOperation;
import org.praisenter.Tag;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnailSettings;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

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
	private final MediaLibrary library;
	private final ExecutorService service;
	private final Set<Tag> tags;
	
	private final ObservableList<MediaListItem> items = FXCollections.observableArrayList();
	
	public ObservableMediaLibrary(MediaLibrary library, ExecutorService service) {
		this.library = library;
		this.service = service;
		this.tags = new TreeSet<Tag>();
		// add all the current media to the observable list
		for (Media media : library.all()) {
			this.items.add(new MediaListItem(media));
			this.tags.addAll(media.getMetadata().getTags());
        }
	}
	
	public void add(Path path, Consumer<Media> onSuccess, BiConsumer<Path, Throwable> onError) {
		// create a "loading" item
		final MediaListItem loading = new MediaListItem(path.getFileName().toString());
		
		// changes to the list should be done on the FX UI Thread
		runOnFxThead(() -> {
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
			runOnFxThead(() -> {
				items.remove(loading);
				items.add(success);
				if (onSuccess != null) {
					onSuccess.accept(media);
				}
			});
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			// changes to the list should be done on the FX UI Thread
			runOnFxThead(() -> {
				items.remove(loading);
				if (onError != null) {
					onError.accept(path, ex);
				}
			});
		});
		task.setOnCancelled((e) -> {
			// TODO implement
		});
		this.service.submit(task);
	}
	
	public void addAll(List<Path> paths, Consumer<List<Media>> onSuccess, Consumer<List<FailedOperation<Path>>> onError) {
		// create the "loading" items
		List<MediaListItem> loadings = new ArrayList<MediaListItem>();
		for (Path path : paths) {
			loadings.add(new MediaListItem(path.getFileName().toString()));
		}
		
		// changes to the list should be done on the FX UI Thread
		runOnFxThead(() -> {
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
						runOnFxThead(() -> {
							// remove the loading item
							items.remove(new MediaListItem(path.getFileName().toString()));
							// add the real item
							items.add(new MediaListItem(media));
						});
					} catch (Exception ex) {
						// TODO logging
						failures.add(new FailedOperation<Path>(path, ex));
						runOnFxThead(() -> {
							// remove the loading item
							items.remove(new MediaListItem(path.getFileName().toString()));
						});
					}
				}
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			runOnFxThead(() -> {
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
			failures.add(new FailedOperation<Path>(null, ex));
			runOnFxThead(() -> {
				if (onError != null) {
					onError.accept(failures);
				}
			});
		});
		task.setOnCancelled((e) -> {
			// TODO implement
		});
		this.service.submit(task);
	}
	
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
			runOnFxThead(() -> {
				items.remove(success);
				if (onSuccess != null) {
					onSuccess.run();
				}
			});
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			runOnFxThead(() -> {
				if (onError != null) {
					onError.accept(media, ex);
				}
			});
		});
		task.setOnCancelled((e) -> {
			// TODO implement
		});
		this.service.submit(task);
	}
	
	public void removeAll(List<Media> media, Runnable onSuccess, Consumer<List<FailedOperation<Media>>> onError) {
		List<FailedOperation<Media>> failures = new ArrayList<FailedOperation<Media>>();
		
		// execute the add on a different thread
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				for (Media m : media) {
					try {
						library.remove(m);
						runOnFxThead(() -> {
							// remove the item
							items.remove(new MediaListItem(m));
						});
					} catch (Exception ex) {
						// TODO logging
						failures.add(new FailedOperation<Media>(m, ex));
					}
				}
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			runOnFxThead(() -> {
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
			failures.add(new FailedOperation<Media>(null, ex));
			runOnFxThead(() -> {
				if (onError != null) {
					onError.accept(failures);
				}
			});
		});
		task.setOnCancelled((e) -> {
			// TODO implement
		});
		this.service.submit(task);
	}
	
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
			// run everything on the FX UI Thread
			Platform.runLater(() -> {
				if (onError != null) {
					onError.accept(m0, ex);
				}
			});
		});
		task.setOnCancelled((e) -> {
			// TODO implement
		});
		this.service.submit(task);

		
		
		
		
	}
	
	public void addTag(Media media, Tag tag) throws JAXBException, IOException {
		library.addTag(media, tag);
	}
	
	public void removeTag(Media media, Tag tag) throws JAXBException, IOException {
		library.removeTag(media, tag);
	}
	
	private static final void runOnFxThead(Runnable r) {
		if (Platform.isFxApplicationThread()) {
			r.run();
		} else {
			Platform.runLater(r);
		}
	}
	
	// mutators
	
	public Set<Tag> getTags() {
		return Collections.unmodifiableSet(this.tags);
	}
	
	public ObservableList<MediaListItem> getItems() {
		return this.items;
	}
	
	public MediaThumbnailSettings getThumbnailSettings() {
		return this.library.getThumbnailSettings();
	}
}
