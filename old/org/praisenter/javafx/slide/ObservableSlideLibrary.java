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
package org.praisenter.javafx.slide;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.Tag;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.async.AsyncTaskFactory;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideImportResult;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideShow;
import org.praisenter.ui.translations.Translations;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// TODO Create importer classes for older praisenter versions

/**
 * Represents an Observable wrapper to the slide library.
 * <p>
 * This wrapper allows Java FX controls to bind to the list of slide items
 * to allow updating of the view from wherever the view is changed.
 * <p>
 * NOTE: modifications to the {@link #getSlideItems()} and success and error handlers
 * will always be performed on the FX UI thread.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class ObservableSlideLibrary {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The slide library */
	private final SlideLibrary library;

	/** The thumbnail generator */
	private final JavaFXSlideThumbnailGenerator thumbnailGenerator;
	
	/** The observable list of slides */
	private final ObservableList<SlideListItem> items;
	
	/** The observable list of slide shows */
	private final ObservableList<SlideShowListItem> shows;
	
	/**
	 * Minimal constructor.
	 * @param library the slide library
	 * @param thumbnailGenerator the slide thumbnail generator
	 */
	public ObservableSlideLibrary(SlideLibrary library, JavaFXSlideThumbnailGenerator thumbnailGenerator) {
		this.library = library;
		this.thumbnailGenerator = thumbnailGenerator;
		
		this.items = FXCollections.observableArrayList((sli) -> {
			return new Observable[] {
				sli.slideProperty(),
				sli.nameProperty(),
				sli.loadedProperty()
			};
		});
		
		this.shows = FXCollections.observableArrayList((ssli) -> {
			return new Observable[] {
				ssli.slideShowProperty(),
				ssli.nameProperty(),
				ssli.loadedProperty()
			};
		});
		
		List<Slide> slides = null;
		List<SlideShow> shows = null;
		if (library != null) {
			try {
				slides = library.allSlides();
			} catch (Exception ex) {
				LOGGER.error("Failed to load slides.", ex);
			}
			
			try {
				shows = library.allSlideShows();
			} catch (Exception ex) {
				LOGGER.error("Failed to load slide shows.", ex);
			}
		}
		
		if (slides != null) {
			for (Slide slide : slides) {
				this.items.add(new SlideListItem(slide));
	        }
		}
		
		if (shows != null) {
			for (SlideShow show : shows ) {
				this.shows.add(new SlideShowListItem(show));
			}
		}
		
	}

	/**
	 * Returns the slide list item for the given slide or null if not found.
	 * @param slide the slide
	 * @return {@link SlideListItem}
	 */
	private SlideListItem getSlideListItem(Slide slide) {
		if (slide == null) return null;
		SlideListItem si = null;
    	for (int i = 0; i < this.items.size(); i++) {
    		SlideListItem item = this.items.get(i);
    		if (item != null &&
    			item.isLoaded() &&
    			item.getSlide() != null &&
    			item.getSlide().getId().equals(slide.getId())) {
    			si = item;
    			break;
    		}
    	}
    	return si;
	}
	
	/**
	 * Returns the slide show list item for the given slide show or null if not found.
	 * @param show the slide show
	 * @return {@link SlideShowListItem}
	 */
	private SlideShowListItem getSlideShowListItem(SlideShow show) {
		if (show == null) return null;
		SlideShowListItem si = null;
    	for (int i = 0; i < this.shows.size(); i++) {
    		SlideShowListItem item = this.shows.get(i);
    		if (item != null &&
    			item.isLoaded() &&
    			item.getSlideShow() != null &&
    			item.getSlideShow().getId().equals(show.getId())) {
    			si = item;
    			break;
    		}
    	}
    	return si;
	}
	
	/**
	 * Attempts to add the given path to the slide library.
	 * @param path the path to the slide
	 * @return {@link AsyncTask}&lt;List&lt;{@link Slide}&gt;&gt;
	 */
	public AsyncTask<SlideImportResult> importSlidesAndShows(Path path) {
		if (path != null) {
			// create a "loading" item
			final SlideListItem loading = new SlideListItem(path.getFileName().toString());
			
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				// add it to the items list
				items.add(loading);
			});
			
			// execute the add on a different thread
			AsyncTask<SlideImportResult> task = new AsyncTask<SlideImportResult>(MessageFormat.format(Translations.get("task.import"), path.getFileName())) {
				@Override
				protected SlideImportResult call() throws Exception {
					updateProgress(-1, 0);
					return library.importSlidesAndShows(path);
				}
			};
			task.setOnSucceeded((e) -> {
				SlideImportResult result = task.getValue();
				items.remove(loading);
				for (Slide slide : result.getSlides()) {
					items.add(new SlideListItem(slide));
				}
				for (SlideShow show : result.getSlideShows()) {
					shows.add(new SlideShowListItem(show));
				}
			});
			task.setOnFailed((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to import slide " + path.toAbsolutePath().toString(), ex);
				items.remove(loading);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}

	/**
	 * Attempts to save the given slide in this slide library.
	 * @param slide the slide
	 * @param generateThumbnail true if a thumbnail of the slide should be generated
	 * @return {@link AsyncTask}&lt;{@link Slide}&gt;
	 */
	public AsyncTask<Slide> save(Slide slide, boolean generateThumbnail) {
		return this.save(MessageFormat.format(Translations.get("task.save"), slide.getName()), slide, generateThumbnail);
	}
	
	/**
	 * Attempts to save the given slide in this slide library.
	 * @param action a simple string describing the save action if it's something more specific than "Save"
	 * @param slide the slide
	 * @param generateThumbnail true if a thumbnail of the slide should be generated
	 * @return {@link AsyncTask}&lt;{@link Slide}&gt;
	 */
	public AsyncTask<Slide> save(String action, Slide slide, boolean generateThumbnail) {
		if (slide != null) {
			// synchronously make a copy
			final Slide copy = slide.copy(true);
			
			// execute the add on a different thread
			AsyncTask<Slide> task = new AsyncTask<Slide>(action) {
				@Override
				protected Slide call() throws Exception {
					this.updateProgress(-1, 0);
					// generate a thumbnail
					if (generateThumbnail) {
						final BufferedImage image = thumbnailGenerator.generate(slide);
						copy.setThumbnail(image);
					}
					library.saveSlide(copy);
					return copy;
				}
			};
			task.setOnSucceeded((e) -> {
				// find the item
				SlideListItem si = this.getSlideListItem(slide);
				// check if new
				if (si != null) {
					si.setSlide(copy);
					si.setName(slide.getName());
				} else {
					this.items.add(new SlideListItem(copy));
				}
			});
			task.setOnFailed((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to save slide " + copy.getName(), ex);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}
	
	/**
	 * Attempts to save the given slide show  in this slide library.
	 * @param show the show
	 * @return {@link AsyncTask}&lt;{@link SlideShow}&gt;
	 */
	public AsyncTask<SlideShow> save(SlideShow show) {
		return this.save(MessageFormat.format(Translations.get("task.save"), show.getName()), show);
	}
	
	/**
	 * Attempts to save the given slide show in this slide library.
	 * @param action a simple string describing the save action if it's something more specific than "Save"
	 * @param show the show
	 * @return {@link AsyncTask}&lt;{@link SlideShow}&gt;
	 */
	public AsyncTask<SlideShow> save(String action, SlideShow show) {
		if (show != null) {
			// synchronously make a copy
			final SlideShow copy = show.copy(true);
			
			// execute the add on a different thread
			AsyncTask<SlideShow> task = new AsyncTask<SlideShow>(action) {
				@Override
				protected SlideShow call() throws Exception {
					this.updateProgress(-1, 0);
					library.saveSlideShow(copy);
					return copy;
				}
			};
			task.setOnSucceeded((e) -> {
				// find the item
				SlideShowListItem ss = this.getSlideShowListItem(show);
				// check if new
				if (ss != null) {
					ss.setSlideShow(copy);
					ss.setName(copy.getName());
				} else {
					this.shows.add(new SlideShowListItem(copy));
				}
			});
			task.setOnFailed((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to save slide show " + copy.getName(), ex);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}
	
	/**
	 * Attempts to remove the given slide from the slide library.
	 * @param slide the slide to remove
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> remove(Slide slide) {
		if (slide != null) {
			final SlideListItem si = this.getSlideListItem(slide);
			
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				// go ahead and remove it
				items.remove(si);
			});
			
			// execute the add on a different thread
			AsyncTask<Void> task = new AsyncTask<Void>(MessageFormat.format(Translations.get("task.delete"), slide.getName())) {
				@Override
				protected Void call() throws Exception {
					this.updateProgress(-1, 0);
					library.removeSlide(slide);
					return null;
				}
			};
			task.setOnFailed((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to remove slide " + slide.getName(), ex);
				// add the item back
				items.add(si);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}

	/**
	 * Attempts to remove the given slide show from the slide library.
	 * @param show the show to remove
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> remove(SlideShow show) {
		if (show != null) {
			final SlideShowListItem ss = this.getSlideShowListItem(show);
			
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				// go ahead and remove it
				shows.remove(ss);
			});
			
			// execute the add on a different thread
			AsyncTask<Void> task = new AsyncTask<Void>(MessageFormat.format(Translations.get("task.delete"), show.getName())) {
				@Override
				protected Void call() throws Exception {
					this.updateProgress(-1, 0);
					library.removeSlideShow(show);
					return null;
				}
			};
			task.setOnFailed((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to remove slide show " + show.getName(), ex);
				// add the item back
				shows.add(ss);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}
	
	/**
	 * Attempts to add the given tag to the given slide.
	 * @param slide the slide
	 * @param tag the tag to add
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> addTag(Slide slide, Tag tag) {
		if (slide != null) {
			// execute the add on a different thread
			AsyncTask<Void> task = new AsyncTask<Void>() {
				@Override
				protected Void call() throws Exception {
					this.updateProgress(-1, 0);
					library.addTag(slide, tag);
					return null;
				}
			};
			task.setOnFailed((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to add tag " + tag.getName() + " to slide " + slide.getName(), ex);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}

	/**
	 * Attempts to remove the given tag from the given slide.
	 * @param slide the slide
	 * @param tag the tag to remove
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> removeTag(Slide slide, Tag tag) {
		if (slide != null) {
			// execute the add on a different thread
			AsyncTask<Void> task = new AsyncTask<Void>() {
				@Override
				protected Void call() throws Exception {
					this.updateProgress(-1, 0);
					library.removeTag(slide, tag);
					return null;
				}
			};
			task.setOnFailed((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to remove tag " + tag.getName() + " from slide " + slide.getName(), ex);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}

	/**
	 * Exports the given slides to the given file.
	 * @param path the file
	 * @param slides the slides to export; can be null
	 * @param shows the slide shows to export; can be null
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> exportSlidesAndShows(Path path, List<Slide> slides, List<SlideShow> shows) {
		AsyncTask<Void> task = new AsyncTask<Void>(MessageFormat.format(Translations.get("task.export"), path.getFileName())) {
			@Override
			protected Void call() throws Exception {
				this.updateProgress(-1, 0);
				library.exportSlidesAndShows(path, slides, shows);
				return null;
			}
		};
		return task;
	}

	/**
	 * Exports the given slides to the given file.
	 * @param stream the stream to export to
	 * @param fileName the file name to export to
	 * @param slides the slides to export; can be null
	 * @param shows the slide shows to export; can be null
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> exportSlidesAndShows(ZipOutputStream stream, String fileName, List<Slide> slides, List<SlideShow> shows) {
		AsyncTask<Void> task = new AsyncTask<Void>(MessageFormat.format(Translations.get("task.export"), fileName)) {
			@Override
			protected Void call() throws Exception {
				this.updateProgress(-1, 0);
				library.exportSlidesAndShows(stream, slides, shows);
				return null;
			}
		};
		return task;
	}
	
	/**
	 * Scans the entire library for slides without thumbnails and attempts
	 * to generate a thumbnail for each and save the slide with the thumbnail.
	 * <p>
	 * This method must be called from the Java FX UI thread.
	 */
	public void generateMissingThumbnails() {
		int generated = 0;
		LOGGER.debug("Checking for slides without thumbnails.");
		for (Slide slide : this.library.allSlides()) {
			if (slide.getThumbnail() == null) {
				try {
					LOGGER.debug("Generating thumbnail for '{}'.", slide.getName());
					BufferedImage image = this.thumbnailGenerator.generate(slide);
					// check if we were able to generate the image
					if (image != null) {
						// set the image
						slide.setThumbnail(image);
						try {
							LOGGER.debug("Saving slide '{}' with thumbnail.", slide.getName());
							// save it to disk
							library.saveSlide(slide);
							generated++;
						} catch (Exception e) {
							LOGGER.warn("Failed to save slide after generating thumbnail: " + e.getMessage(), e);
						}
					}
				} catch (Exception ex) {
					LOGGER.error("An error occurred while generating slide thumbnails.", ex);
				}
			}
		}
		LOGGER.info("Generated {} slide thumbnails.", generated);
	}
	
	/**
	 * Returns true if any of the given media is referenced.
	 * @param ids the media ids
	 * @return boolean
	 */
	public boolean isMediaReferenced(List<UUID> ids) {
		return this.library.isMediaReferenced(ids.toArray(new UUID[0]));
	}

	/**
	 * Returns true if any of the given slides are referenced in a show.
	 * @param ids the slide ids
	 * @return boolean
	 */
	public boolean isSlideReferenced(List<UUID> ids) {
		return this.library.isSlideReferenced(ids.toArray(new UUID[0]));
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
	public Slide getSlide(UUID id) {
		return this.library.getSlide(id);
	}

	/**
	 * Returns the slide for the given id.
	 * @param id the id
	 * @return {@link SlideListItem}
	 */
	SlideListItem getSlideListItem(UUID id) {
		Slide slide = this.library.getSlide(id);
		return this.getSlideListItem(slide);
	}
	
	/**
	 * Returns the observable list of slides.
	 * @return ObservableList&lt;{@link SlideListItem}&gt;
	 */
	ObservableList<SlideListItem> getSlideItems() {
		return this.items;
	}

	/**
	 * Returns the slide show for the given id.
	 * @param id the id
	 * @return {@link SlideShow}
	 */
	public SlideShow getSlideShow(UUID id) {
		return this.library.getSlideShow(id);
	}

	SlideShowListItem getSlideShowListItem(UUID id) {
		SlideShow show = this.library.getSlideShow(id);
		return this.getSlideShowListItem(show);
	}
	
	ObservableList<SlideShowListItem> getSlideShowItems() {
		return this.shows;
	}
}
