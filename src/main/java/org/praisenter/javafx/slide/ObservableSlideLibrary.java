package org.praisenter.javafx.slide;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.FailedOperation;
import org.praisenter.Tag;
import org.praisenter.javafx.MonitoredTask;
import org.praisenter.javafx.MonitoredTaskResultStatus;
import org.praisenter.javafx.MonitoredThreadPoolExecutor;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideThumbnailGenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// TODO translate
// TODO Create importer classes
public final class ObservableSlideLibrary {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The slide library */
	private final SlideLibrary library;
	
	/** The thread service */
	private final MonitoredThreadPoolExecutor service;
	
	/** The observable list of slides */
	private final ObservableList<SlideListItem> items = FXCollections.observableArrayList();

	public ObservableSlideLibrary(SlideLibrary library, MonitoredThreadPoolExecutor service) {
		this.library = library;
		this.service = service;
		
		List<Slide> slides = null;
		if (library != null) {
			try {
				slides = library.all();
			} catch (Exception ex) {
				LOGGER.error("Failed to load slides.", ex);
			}
		}
		
		if (slides != null) {
			// add all the current media to the observable list
			for (Slide slide : slides) {
				this.items.add(new SlideListItem(slide));
	        }
		}
	}

	/**
	 * Attempts to add the given path to the slide library.
	 * <p>
	 * The onSuccess method will be called when the import is successful. The
	 * onError method will be called if an error occurs during import.
	 * <p>
	 * An item is added to the observable list to represent that the slide is
	 * being imported. This item will be removed when the import completes, whether
	 * its successful or not.
	 * <p>
	 * The observable list of slides will be updated if the slide is successfully
	 * added. Both the update of the observable list and the onSuccess and onError 
	 * methods will be performed on the Java FX UI thread.
	 * @param path the path to the slide
	 * @param onSuccess called when the slide is imported successfully
	 * @param onError called when the slide failed to be imported
	 */
	public void add(Path path, Consumer<Slide> onSuccess, BiConsumer<Path, Throwable> onError) {
		// create a "loading" item
		final SlideListItem loading = new SlideListItem(path.getFileName().toString());
		
		// changes to the list should be done on the FX UI Thread
		Fx.runOnFxThead(() -> {
			// add it to the items list
			items.add(loading);
		});
		
		// execute the add on a different thread
		MonitoredTask<Slide> task = new MonitoredTask<Slide>("Import '" + path.getFileName() + "'", true) {
			@Override
			protected Slide call() throws Exception {
				updateProgress(-1, 0);
				try {
					Slide slide = library.add(path);
					setResultStatus(MonitoredTaskResultStatus.SUCCESS);
					return slide;
				} catch (Exception ex) {
					setResultStatus(MonitoredTaskResultStatus.ERROR);
					throw ex;
				}
			}
		};
		task.setOnSucceeded((e) -> {
			Slide slide = task.getValue();
			SlideListItem success = new SlideListItem(slide);
			items.remove(loading);
			items.add(success);
			if (onSuccess != null) {
				onSuccess.accept(slide);
			}
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to import slide " + path.toAbsolutePath().toString(), ex);
			items.remove(loading);
			if (onError != null) {
				onError.accept(path, ex);
			}
		});
		this.service.execute(task);
	}

	/**
	 * Attempts to add the given paths to the slide library.
	 * <p>
	 * The onSuccess method will be called with all the successful imports. The
	 * onError method will be called with all the failed imports. The onSuccess
	 * and onError methods will only be called if there's at least one successful
	 * or one failed import respectively.
	 * <p>
	 * Items are added to the observable list to represent that the slide is
	 * being imported. These items will be removed when the import completes, whether
	 * they are successful or not.
	 * <p>
	 * The observable list of slides will be updated if some slides are successfully
	 * added to the SlideLibrary. Both the update of the observable list and
	 * the onSuccess and onError methods will be performed on the Java FX UI
	 * thread.
	 * @param paths the paths to the slides
	 * @param onSuccess called with the slides that were imported successfully
	 * @param onError called with the slides that failed to be imported
	 */
	public void add(List<Path> paths, Consumer<List<Slide>> onSuccess, Consumer<List<FailedOperation<Path>>> onError) {
		// create the "loading" items
		List<SlideListItem> loadings = new ArrayList<SlideListItem>();
		for (Path path : paths) {
			loadings.add(new SlideListItem(path.getFileName().toString()));
		}
		
		// changes to the list should be done on the FX UI Thread
		Fx.runOnFxThead(() -> {
			// add it to the items list
			items.addAll(loadings);
		});
		
		List<Slide> successes = new ArrayList<Slide>();
		List<FailedOperation<Path>> failures = new ArrayList<FailedOperation<Path>>();
		
		// execute the add on a different thread
		MonitoredTask<Void> task = new MonitoredTask<Void>(paths.size() > 1 ? "Import " + paths.size() + " slides" : "Import '" + paths.get(0).getFileName() + "'", false) {
			@Override
			protected Void call() throws Exception {
				updateProgress(0, paths.size());
				
				long i = 1;
				int errorCount = 0;
				for (Path path : paths) {
					try {
						Slide slide = library.add(path);
						successes.add(slide);
						Fx.runOnFxThead(() -> {
							// remove the loading item
							items.remove(new SlideListItem(path.getFileName().toString()));
							// add the real item
							items.add(new SlideListItem(slide));
						});
					} catch (Exception ex) {
						LOGGER.error("Failed to import slide(s) " + path.toAbsolutePath().toString(), ex);
						failures.add(new FailedOperation<Path>(path, ex));
						Fx.runOnFxThead(() -> {
							// remove the loading item
							items.remove(new SlideListItem(path.getFileName().toString()));
						});
						errorCount++;
					}
					this.updateProgress(i++, paths.size());
				}
				
				// set the result status based on the number of errors we got
				if (errorCount == 0) {
					this.setResultStatus(MonitoredTaskResultStatus.SUCCESS);
				} else if (errorCount == paths.size()) {
					this.setResultStatus(MonitoredTaskResultStatus.ERROR);
				} else {
					this.setResultStatus(MonitoredTaskResultStatus.WARNING);
				}
				
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			// notify successes and failures
			if (onSuccess != null && successes.size() > 0) {
				onSuccess.accept(successes);
			}
			if (onError != null && failures.size() > 0) {
				onError.accept(failures);
			}
		});
		task.setOnFailed((e) -> {
			// this shouldn't happen because we should catch all exceptions
			// inside the task, but lets put it here just in case
			Throwable ex = task.getException();
			LOGGER.error("Failed to complete slide import", ex);
			failures.add(new FailedOperation<Path>(null, ex));
			if (onError != null) {
				onError.accept(failures);
			}
		});
		this.service.execute(task);
	}

	/**
	 * Attempts to save the given slide to this slide library.
	 * <p>
	 * The onSuccess method will be called when the save is successful. The
	 * onError method will be called if an error occurs during the save.
	 * @param slide the slide
	 * @param onSuccess called when the slide is imported successfully
	 * @param onError called when the slide failed to be imported
	 */
	public void save(Slide slide, Consumer<Slide> onSuccess, BiConsumer<Slide, Throwable> onError) {
		// execute the add on a different thread
		MonitoredTask<Void> task = new MonitoredTask<Void>("Save '" + slide.getName() + "'", true) {
			@Override
			protected Void call() throws Exception {
				updateProgress(-1, 0);
				try {
					library.save(slide);
					setResultStatus(MonitoredTaskResultStatus.SUCCESS);
					return null;
				} catch (Exception ex) {
					setResultStatus(MonitoredTaskResultStatus.ERROR);
					throw ex;
				}
			}
		};
		task.setOnSucceeded((e) -> {
			// FIXME handle rename
			if (onSuccess != null) {
				onSuccess.accept(slide);
			}
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to save slide " + slide.getName(), ex);
			if (onError != null) {
				onError.accept(slide, ex);
			}
		});
		this.service.execute(task);
	}

	/**
	 * Attempts to remove the given slide from the slide library.
	 * <p>
	 * The onSuccess method will be called on success. The onError method will be 
	 * called if an error occurs during removal.
	 * <p>
	 * Removing a slide item is typically a fast operation so no loading items are
	 * added.
	 * <p>
	 * The observable list of slides will be updated if the slide is successfully
	 * removed. Both the update of the observable list and the onSuccess and onError 
	 * methods will be performed on the Java FX UI thread.
	 * @param slide the slide to remove
	 * @param onSuccess called when the slide is successfully removed
	 * @param onError called when the slide failed to be removed
	 */
	public void remove(Slide slide, Runnable onSuccess, BiConsumer<Slide, Throwable> onError) {
		// execute the add on a different thread
		MonitoredTask<Void> task = new MonitoredTask<Void>("Remove '" + slide.getName() + "'", true) {
			@Override
			protected Void call() throws Exception {
				updateProgress(-1, 0);
				try {
					library.remove(slide);
					setResultStatus(MonitoredTaskResultStatus.SUCCESS);
					return null;
				} catch (Exception ex) {
					setResultStatus(MonitoredTaskResultStatus.ERROR);
					throw ex;
				}
			}
		};
		task.setOnSucceeded((e) -> {
			SlideListItem success = new SlideListItem(slide);
			items.remove(success);
			if (onSuccess != null) {
				onSuccess.run();
			}
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to remove media " + slide.getName(), ex);
			if (onError != null) {
				onError.accept(slide, ex);
			}
		});
		this.service.execute(task);
	}
	
	/**
	 * Attempts to remove all the given slides from the slide library.
	 * <p>
	 * The onSuccess method will be called on success. The onError method will be 
	 * called if an error occurs during removal.
	 * <p>
	 * Removing a slide item is typically a fast operation so no loading items are
	 * added.
	 * <p>
	 * The observable list of slides will be updated if the slide is successfully
	 * removed. Both the update of the observable list and the onSuccess and onError 
	 * methods will be performed on the Java FX UI thread.
	 * @param slides the slides to remove
	 * @param onSuccess called when a slide is successfully removed
	 * @param onError called when a slide failed to be removed
	 */
	public void remove(List<Slide> slides, Runnable onSuccess, Consumer<List<FailedOperation<Slide>>> onError) {
		List<FailedOperation<Slide>> failures = new ArrayList<FailedOperation<Slide>>();
		
		// execute the add on a different thread
		MonitoredTask<Void> task = new MonitoredTask<Void>(slides.size() > 1 ? "Remove " + slides.size() + " slides" : "Remove '" + slides.get(0).getName() + "'", false) {
			@Override
			protected Void call() throws Exception {
				updateProgress(0, slides.size());
				
				long i = 1;
				int errorCount = 0;
				for (Slide slide : slides) {
					try {
						library.remove(slide);
						Fx.runOnFxThead(() -> {
							// remove the item
							items.remove(new SlideListItem(slide));
						});
					} catch (Exception ex) {
						LOGGER.error("Failed to remove media " + slide.getName(), ex);
						failures.add(new FailedOperation<Slide>(slide, ex));
						errorCount++;
					}
					this.updateProgress(i++, slides.size());
				}
				
				// set the result status based on the number of errors we got
				if (errorCount == 0) {
					this.setResultStatus(MonitoredTaskResultStatus.SUCCESS);
				} else if (errorCount == slides.size()) {
					this.setResultStatus(MonitoredTaskResultStatus.ERROR);
				} else {
					this.setResultStatus(MonitoredTaskResultStatus.WARNING);
				}
				
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			// notify any failures
			if (onError != null && failures.size() > 0) {
				onError.accept(failures);
			}
		});
		task.setOnFailed((e) -> {
			// this shouldn't happen because we should catch all exceptions
			// inside the task, but lets put it here just in case
			Throwable ex = task.getException();
			LOGGER.error("Failed to complete removal", ex);
			failures.add(new FailedOperation<Slide>(null, ex));
			if (onError != null) {
				onError.accept(failures);
			}
		});
		this.service.execute(task);
	}
	
	/**
	 * Scans the entire library for slides without thumbnails and attempts
	 * to generate a thumbnail for each and save the slide with the thumbnail.
	 * <p>
	 * This method must be called from the Java FX UI thread.
	 * @param generator the thumbnail generator
	 */
	public void generateMissingThumbnails(SlideThumbnailGenerator generator) {
		for (Slide slide : library.all()) {
			if (slide.getThumbnail() == null) {
				BufferedImage image = generator.generate(slide);
				// check if we were able to generate the image
				if (image != null) {
					// set the image
					slide.setThumbnail(image);
					try {
						// save it to disk
						library.save(slide);
					} catch (JAXBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	// mutators

	/**
	 * Returns a set of all the tags in this library.
	 * @return Set&lt;{@link Tag}&gt;
	 */
	public Set<Tag> getTags() {
		Set<Tag> tags = new TreeSet<Tag>();
		for (SlideListItem item : items) {
			if (item.loaded) {
				tags.addAll(item.slide.getTags());
			}
		}
		return tags;
	}
	
	/**
	 * Returns the slide for the given id.
	 * @param id the id
	 * @return {@link Slide}
	 */
	public Slide get(UUID id) {
		return this.library.get(id);
	}
	
	/**
	 * Returns the observable list of slides.
	 * @return ObservableList&lt;{@link SlideListItem}&gt;
	 */
	public ObservableList<SlideListItem> getItems() {
		return this.items;
	}
}
