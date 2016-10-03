package org.praisenter.javafx.slide;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.FailedOperation;
import org.praisenter.Tag;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideThumbnailGenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public final class ObservableSlideLibrary {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The bible library */
	private final SlideLibrary library;
	
	/** The thread service */
	private final ExecutorService service;
	
	/** The observable list of bibles */
	private final ObservableList<SlideListItem> items = FXCollections.observableArrayList();

	public ObservableSlideLibrary(SlideLibrary library, ExecutorService service) {
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
		Task<Slide> task = new Task<Slide>() {
			@Override
			protected Slide call() throws Exception {
				return library.add(path);
			}
		};
		task.setOnSucceeded((e) -> {
			Slide slide = task.getValue();
			SlideListItem success = new SlideListItem(slide);
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				items.remove(loading);
				items.add(success);
				if (onSuccess != null) {
					onSuccess.accept(slide);
				}
			});
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to import slide " + path.toAbsolutePath().toString(), ex);
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				items.remove(loading);
				if (onError != null) {
					onError.accept(path, ex);
				}
			});
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
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				library.save(slide);
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
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
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
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
	public void remove(List<Slide> slides, Runnable onSuccess, Consumer<List<FailedOperation<Slide>>> onError) {
		List<FailedOperation<Slide>> failures = new ArrayList<FailedOperation<Slide>>();
		
		// execute the add on a different thread
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
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
					}
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
