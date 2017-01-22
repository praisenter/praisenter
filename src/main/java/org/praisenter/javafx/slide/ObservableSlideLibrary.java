package org.praisenter.javafx.slide;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Tag;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.async.PraisenterThreadPoolExecutor;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideLibrary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// TODO translate
// TODO Create importer classes
public final class ObservableSlideLibrary {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The slide library */
	private final SlideLibrary library;

	private final JavaFXSlideThumbnailGenerator thumbnailGenerator;
	
	/** The thread service */
	private final PraisenterThreadPoolExecutor service;
	
	/** The observable list of slides */
	private final ObservableList<SlideListItem> items = FXCollections.observableArrayList();

	public ObservableSlideLibrary(SlideLibrary library, JavaFXSlideThumbnailGenerator thumbnailGenerator, PraisenterThreadPoolExecutor service) {
		this.library = library;
		this.thumbnailGenerator = thumbnailGenerator;
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
		AsyncTask<Slide> task = new AsyncTask<Slide>("Import '" + path.getFileName() + "'") {
			@Override
			protected Slide call() throws Exception {
				updateProgress(-1, 0);
				return library.add(path);
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
	public void add(List<Path> paths, Consumer<List<Slide>> onSuccess) {
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
		
		// execute the add on a different thread
		AsyncTask<Void> task = new AsyncTask<Void>(paths.size() > 1 ? "Import " + paths.size() + " slides" : "Import '" + paths.get(0).getFileName() + "'") {
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
						Fx.runOnFxThead(() -> {
							// remove the loading item
							items.remove(new SlideListItem(path.getFileName().toString()));
						});
						errorCount++;
					}
					this.updateProgress(i++, paths.size());
				}
				
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			// notify successes and failures
			if (onSuccess != null && successes.size() > 0) {
				onSuccess.accept(successes);
			}
		});
		task.setOnFailed((e) -> {
			// this shouldn't happen because we should catch all exceptions
			// inside the task, but lets put it here just in case
			Throwable ex = task.getException();
			LOGGER.error("Failed to complete slide import", ex);
			
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
		
		// generate a thumbnail on the FX thread
		BufferedImage image = this.thumbnailGenerator.generate(slide);
		slide.setThumbnail(image);

		Slide copy = slide.copy(true);
		AsyncTask<Void> task = new AsyncTask<Void>("Save '" + slide.getName() + "'") {
			@Override
			protected Void call() throws Exception {
				updateProgress(-1, 0);
				library.save(copy);
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			// find the slide item
			SlideListItem si = null;
			for (SlideListItem item : this.items) {
				if (item.getSlide().getId().equals(copy.getId())) {
					si = item;
					break;
				}
			}
			// check if new
			if (si == null) {
				// then add one
				this.items.add(new SlideListItem(copy));
			} else {
				// then update it
				si.setName(copy.getName());
				// we set it to a copy because it could
				// still be edited after being saved
				// and we only want to change it if saved
				si.setSlide(copy);
			}
			// check if name changed
			if (onSuccess != null) {
				// return the original
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
		AsyncTask<Void> task = new AsyncTask<Void>("Remove '" + slide.getName() + "'") {
			@Override
			protected Void call() throws Exception {
				updateProgress(-1, 0);
				library.remove(slide);
				return null;
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
	public void remove(List<Slide> slides, Runnable onSuccess) {
		// execute the add on a different thread
		AsyncTask<Void> task = new AsyncTask<Void>(slides.size() > 1 ? "Remove " + slides.size() + " slides" : "Remove '" + slides.get(0).getName() + "'") {
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
						errorCount++;
					}
					this.updateProgress(i++, slides.size());
				}
				
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			// notify any failures
			
		});
		task.setOnFailed((e) -> {
			// this shouldn't happen because we should catch all exceptions
			// inside the task, but lets put it here just in case
			Throwable ex = task.getException();
			LOGGER.error("Failed to complete removal", ex);
		});
		this.service.execute(task);
	}
	
	/**
	 * Scans the entire library for slides without thumbnails and attempts
	 * to generate a thumbnail for each and save the slide with the thumbnail.
	 * <p>
	 * This method must be called from the Java FX UI thread.
	 */
	public void generateMissingThumbnails() {
		int generated = 0;
		for (Slide slide : library.all()) {
			if (slide.getThumbnail() == null) {
				BufferedImage image = this.thumbnailGenerator.generate(slide);
				// check if we were able to generate the image
				if (image != null) {
					// set the image
					slide.setThumbnail(image);
					try {
						// save it to disk
						library.save(slide);
						generated++;
					} catch (Exception e) {
						LOGGER.warn("Failed to save slide after generating thumbnail: " + e.getMessage(), e);
					}
				}
			}
		}
		LOGGER.info("Generated {} slide thumbnails.", generated);
	}
	
	// mutators

	/**
	 * Returns a set of all the tags in this library.
	 * @return Set&lt;{@link Tag}&gt;
	 */
	public Set<Tag> getTags() {
		Set<Tag> tags = new TreeSet<Tag>();
		for (SlideListItem item : items) {
			if (item.isLoaded()) {
				tags.addAll(item.getSlide().getTags());
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
