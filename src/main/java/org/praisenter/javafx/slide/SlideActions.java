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

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Tag;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.async.AsyncChainedTask;
import org.praisenter.javafx.async.AsyncGroupTask;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.async.AsyncTaskFactory;
import org.praisenter.javafx.controls.Alerts;
import org.praisenter.javafx.media.ObservableMediaLibrary;
import org.praisenter.media.Media;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideAssignment;
import org.praisenter.slide.SlideShow;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.StringManipulator;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * Wrapper class for user actions with the {@link ObservableSlideLibrary} that encapsulate
 * user related alerts and warnings.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideActions {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** Hidden constructor */
	private SlideActions() {}

	/**
	 * Returns a task that will import the given paths as slides.
	 * <p>
	 * Returns a list of the slides that were imported successfully.
	 * @param context the context
	 * @param owner the window owner
	 * @param paths the paths to import
	 * @return {@link AsyncGroupTask}&lt;{@link AsyncTask}&lt;List&lt;{@link Slide}&gt;&gt;&gt;
	 */
	public static final AsyncGroupTask<AsyncTask<?>> slideImport(PraisenterContext context, Window owner, List<Path> paths) {
		// sanity check
		if (paths != null && !paths.isEmpty()) {
			ObservableMediaLibrary mediaLibrary = context.getMediaLibrary();
			ObservableSlideLibrary library = context.getSlideLibrary();
			
			// build a list of tasks to execute
			List<AsyncTask<?>> tasks = new ArrayList<AsyncTask<?>>();
			for (Path path : paths) {
				if (path != null) {
					if (MimeType.ZIP.check(path)) {
						// import any media in the zip
						tasks.add(mediaLibrary.importMedia(path));
					}
					// import the slides
					tasks.add(library.importSlidesAndShows(path));
				}
			}
			// wrap the tasks in a multi-task wait task
			AsyncGroupTask<AsyncTask<?>> task = new AsyncGroupTask<AsyncTask<?>>(tasks);
			// define a listener to be called when the wrapper task completes
			task.addCompletedHandler((e) -> {
				// get the exceptions
				Exception[] exceptions = tasks
						.stream()
						.filter(t -> t.getException() != null)
						.map(t -> t.getException())
						.collect(Collectors.toList())
						.toArray(new Exception[0]);
				// did we have any errors?
				if (exceptions.length > 0) {
					Alert alert = Alerts.exception(
							owner,
							null, 
							null, 
							null, 
							exceptions);
					alert.show();
				}
			});
			// return the 
			return task;
		}
		AsyncGroupTask<AsyncTask<?>> task = AsyncTaskFactory.group();
		task.cancel();
		return task;
	}
	
	/**
	 * Returns a task that will save the given slide.
	 * <p>
	 * Returns the slide that was saved.
	 * @param library the library to save to
	 * @param owner the window owner
	 * @param slide the slide to save
	 * @return {@link AsyncTask}&lt;{@link Slide}&gt;
	 */
	public static final AsyncTask<Slide> slideSave(ObservableSlideLibrary library, Window owner, Slide slide) {
		// sanity check
		if (slide != null) {
			AsyncTask<Slide> task = library.save(slide, true);
	    	task.addCancelledOrFailedHandler((e) -> {
	    		Throwable error = task.getException();
	    		LOGGER.error("Failed to save slide " + slide.getName() + " " + slide.getId() + " due to: " + error.getMessage(), error);
				Alert alert = Alerts.exception(
						owner,
						null, 
						null, 
						MessageFormat.format(Translations.get("slide.save.error.content"), slide.getName()), 
						error);
				alert.show();
			});
	    	return task;
		}
		AsyncTask<Slide> task = AsyncTaskFactory.single();
		task.cancel();
		return task;
	}

	/**
	 * Returns a task that will prompt the user for a new name for the given slide and save it with
	 * that new name.
	 * <p>
	 * Returns the slide with the new name.
	 * @param library the library to save to
	 * @param owner the window owner
	 * @param slide the slide to rename
	 * @return {@link AsyncTask}&lt;{@link Slide}&gt;
	 */
	public static final AsyncTask<Slide> slidePromptRename(ObservableSlideLibrary library, Window owner, Slide slide) {
		// sanity check
		if (slide != null) {
			String old = slide.getName();
	    	TextInputDialog prompt = new TextInputDialog(old);
	    	prompt.initOwner(owner);
	    	prompt.initModality(Modality.WINDOW_MODAL);
	    	prompt.setTitle(Translations.get("rename"));
	    	prompt.setHeaderText(Translations.get("name.new"));
	    	prompt.setContentText(Translations.get("name"));
	    	Optional<String> result = prompt.showAndWait();
	    	// check for the "OK" button
	    	if (result.isPresent()) {
	    		// actually rename it?
	    		String name = result.get();
	    		if (!Objects.equals(old, name)) {
		        	// update the slide's name
		    		slide.setName(name);
		    		AsyncTask<Slide> task = library.save(MessageFormat.format(Translations.get("task.rename"), old, name), slide, false);
		        	task.addCancelledOrFailedHandler((e) -> {
		        		Throwable error = task.getException();
		        		slide.setName(old);
						// log the error
						LOGGER.error("Failed to rename slide from '{}' to '{}': {}", old, name, error.getMessage());
						// show an error to the user
						Alert alert = Alerts.exception(
								owner,
								null, 
								null, 
								MessageFormat.format(Translations.get("task.rename.error"), old, name), 
								error);
						alert.show();
					});
		        	return task;
	        	}
	    		// names were the same
	    	}
	    	// user cancellation
		}
		AsyncTask<Slide> task = AsyncTaskFactory.single();
		task.cancel();
		return task;
	}

	/**
	 * Returns a task that will prompt the user for a new name for the given slide show and save it with
	 * that new name.
	 * <p>
	 * Returns the slide with the new name.
	 * @param library the library to save to
	 * @param owner the window owner
	 * @param show the show to rename
	 * @return {@link AsyncTask}&lt;{@link SlideShow}&gt;
	 */
	public static final AsyncTask<SlideShow> showPromptRename(ObservableSlideLibrary library, Window owner, SlideShow show) {
		// sanity check
		if (show != null) {
			String old = show.getName();
	    	TextInputDialog prompt = new TextInputDialog(old);
	    	prompt.initOwner(owner);
	    	prompt.initModality(Modality.WINDOW_MODAL);
	    	prompt.setTitle(Translations.get("rename"));
	    	prompt.setHeaderText(Translations.get("name.new"));
	    	prompt.setContentText(Translations.get("name"));
	    	Optional<String> result = prompt.showAndWait();
	    	// check for the "OK" button
	    	if (result.isPresent()) {
	    		// actually rename it?
	    		String name = result.get();
	    		if (!Objects.equals(old, name)) {
		        	// update the slide's name
	    			show.setName(name);
		    		AsyncTask<SlideShow> task = library.save(MessageFormat.format(Translations.get("task.rename"), old, name), show);
		        	task.addCancelledOrFailedHandler((e) -> {
		        		Throwable error = task.getException();
		        		show.setName(old);
						// log the error
						LOGGER.error("Failed to rename slide from '{}' to '{}': {}", old, name, error.getMessage());
						// show an error to the user
						Alert alert = Alerts.exception(
								owner,
								null, 
								null, 
								MessageFormat.format(Translations.get("task.rename.error"), old, name), 
								error);
						alert.show();
					});
		        	return task;
	        	}
	    		// names were the same
	    	}
	    	// user cancellation
		}
		AsyncTask<SlideShow> task = AsyncTaskFactory.single();
		task.cancel();
		return task;
	}

	/**
	 * Returns a task that will prompt the user to select a file name to export the given slides to.
	 * @param context the context
	 * @param owner the window owner
	 * @param slides the slides to export; can be null
	 * @param shows the slide shows to export; can be null
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public static final AsyncChainedTask<AsyncTask<Void>> slidePromptExport(PraisenterContext context, Window owner, List<Slide> slides, List<SlideShow> shows) {
		// sanity check
		if ((slides != null && !slides.isEmpty()) ||
			(shows != null && !shows.isEmpty())) {
			String name = Translations.get("slide.export.multiple.filename"); 
	    	if (slides != null && slides.size() == 1) {
	    		Slide slide = slides.get(0);
	    		name = StringManipulator.toFileName(slide.getName(), slide.getId());
	    	} else if (shows != null && shows.size() == 1) {
	    		SlideShow show = shows.get(0);
	    		name = StringManipulator.toFileName(show.getName(), show.getId());
	    	}
	    	FileChooser chooser = new FileChooser();
	    	chooser.setInitialFileName(name + ".zip");
	    	chooser.setTitle(Translations.get("slide.export.title"));
	    	chooser.getExtensionFilters().add(new ExtensionFilter(Translations.get("export.zip.name"), Translations.get("export.zip.extension")));
	    	File file = chooser.showSaveDialog(owner);
	    	// check for cancellation
	    	if (file != null) {
	    		final Path path = file.toPath();
	    		
	    		final ObservableMediaLibrary mediaLibrary = context.getMediaLibrary();
	    		final ObservableSlideLibrary slideLibrary = context.getSlideLibrary();
	    		
	    		Map<UUID, Slide> added = new HashMap<UUID, Slide>();
	    		List<Slide> allSlides = new ArrayList<Slide>();
	    		
	    		// we need to make sure we don't have duplicate slides in the list
    			if (slides != null) {
    				for (Slide slide : slides) {
    					if (!added.containsKey(slide.getId())) {
    						allSlides.add(slide);
    						added.put(slide.getId(), slide);
    					}
    				}
    			}
	    		
	    		// get any slides for the slide shows
	    		if (shows != null && !shows.isEmpty()) {
	    			// we need to make sure all slides from the given shows are present
    				for (SlideShow show : shows) {
    					for (SlideAssignment assignment : show.getSlides()) {
    						Slide slide = slideLibrary.getSlide(assignment.getSlideId());
    						if (slide != null && !added.containsKey(slide.getId())) {
    							allSlides.add(slide);
    							added.put(slide.getId(), slide);
    						}
    					}
    				}
	    		}
	    		
	    		// get the dependent media
	    		final List<Media> media = new ArrayList<Media>();
	    		final Set<UUID> ids = new HashSet<UUID>();
	    		
	    		for (Slide slide : allSlides) {
	    			ids.addAll(slide.getReferencedMedia());
	    		}
	    		
	    		for (UUID id : ids) {
	    			if (id != null) {
	    				Media m = mediaLibrary.get(id);
	    				// check for missing media
	    				if (m != null) {
	    					media.add(m);
	    				} else {
	    					LOGGER.warn("Unable to export media '{}' because it no longer exists in the media library.", id);
	    				}
	    			}
	    		}
	    		
	    		try {
		    		final FileOutputStream fos = new FileOutputStream(path.toFile());
		    		final ZipOutputStream zos = new ZipOutputStream(fos);
		    		
		    		List<AsyncTask<Void>> tasks = new ArrayList<AsyncTask<Void>>();
		    		// export the slides
	    			tasks.add(slideLibrary.exportSlidesAndShows(zos, path.getFileName().toString(), allSlides, shows));
	    			// export the media
	    			if (!media.isEmpty()) {
	    				tasks.add(mediaLibrary.exportMedia(zos, media));
	    			}
	    			AsyncChainedTask<AsyncTask<Void>> task = new AsyncChainedTask<AsyncTask<Void>>(tasks);
	    			task.addCompletedHandler(e -> {
	    				if (zos != null) {
		    				try {
		    					zos.close();
		    				} catch (Exception ex) {
		    					LOGGER.warn("Failed to close ZipOutputStream after writing slides and media.", ex);
		    				}
	    				}
	    				if (fos != null) {
	    					try {
		    					fos.close();
		    				} catch (Exception ex) {
		    					LOGGER.warn("Failed to close FileOutputStream after writing slides and media.", ex);
		    				}
	    				}
	    			});
	    			// define a listener to be called when the wrapper task completes
					task.addCompletedHandler((e) -> {
						// get the exceptions
						Exception[] exceptions = tasks
								.stream()
								.filter(t -> t.getException() != null)
								.map(t -> t.getException())
								.collect(Collectors.toList())
								.toArray(new Exception[0]);
						// did we have any errors?
						if (exceptions.length > 0) {
							Alert fAlert = Alerts.exception(
									owner,
									null, 
									null, 
									Translations.get("slide.export.error"), 
									exceptions);
							fAlert.show();
						}
					});
					return task;
	    		} catch (Exception ex) {
	    			LOGGER.warn("Failed to export slides and media.", ex);
	    		}
	    	}
		}
		// user cancellation
		AsyncChainedTask<AsyncTask<Void>> task = AsyncTaskFactory.chain();
		task.cancel();
		return task;
	}

	/**
	 * Returns a task that will prompt the user for files to import as slides.
	 * <p>
	 * Returns a list of the slides that were imported successfully.
	 * @param context the context
	 * @param owner the window owner
	 * @return {@link AsyncGroupTask}&lt;{@link AsyncTask}&lt;List&lt;{@link Slide}&gt;&gt;&gt;
	 */
	public static final AsyncGroupTask<AsyncTask<?>> slidePromptImport(PraisenterContext context, Window owner) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(Translations.get("slide.import.title"));
		List<File> files = chooser.showOpenMultipleDialog(owner);
		if (files != null && files.size() > 0) {
			List<Path> paths = files
					.stream()
					.filter(f -> f != null && f.isFile())
					.map(f -> f.toPath())
					.collect(Collectors.toList());
			return slideImport(context, owner, paths);
		}
		AsyncGroupTask<AsyncTask<?>> task = AsyncTaskFactory.group();
		task.cancel();
		return task;
	}

	/**
	 * Returns a task that will make a copy of the given slide.
	 * <p>
	 * Returns the copy.
	 * @param library the library to save to
	 * @param owner the window owner
	 * @param slide the slide to copy
	 * @return {@link AsyncTask}&lt;{@link Slide}&gt;
	 */
	public static final AsyncTask<Slide> slideCopy(ObservableSlideLibrary library, Window owner, Slide slide) {
		if (slide != null) {
			// make a copy
			Slide copy = slide.copy(false);
			// set the name to something else
			copy.setName(MessageFormat.format(Translations.get("copyof"), slide.getName()));
			// save it
			AsyncTask<Slide> task = library.save(copy, false);
	    	task.addCancelledOrFailedHandler((e) -> {
	    		Throwable error = task.getException();
	    		// log the error
				LOGGER.error("Failed to copy slide.", error);
				// present message to user
				Alert alert = Alerts.exception(
						owner,
						null, 
						null, 
						MessageFormat.format(Translations.get("slide.copy.error"), slide.getName()), 
						error);
				alert.show();
			});
	    	return task;
		}
		AsyncTask<Slide> task = AsyncTaskFactory.single();
		task.cancel();
		return task;
	}

	/**
	 * Returns a task that will make a copy of the given slide show.
	 * <p>
	 * Returns the copy.
	 * @param library the library to save to
	 * @param owner the window owner
	 * @param show the slide show to copy
	 * @return {@link AsyncTask}&lt;{@link SlideShow}&gt;
	 */
	public static final AsyncTask<SlideShow> showCopy(ObservableSlideLibrary library, Window owner, SlideShow show) {
		if (show != null) {
			// make a copy
			SlideShow copy = show.copy(false);
			// set the name to something else
			copy.setName(MessageFormat.format(Translations.get("copyof"), show.getName()));
			// save it
			AsyncTask<SlideShow> task = library.save(copy);
	    	task.addCancelledOrFailedHandler((e) -> {
	    		Throwable error = task.getException();
	    		// log the error
				LOGGER.error("Failed to copy slide show.", error);
				// present message to user
				Alert alert = Alerts.exception(
						owner,
						null, 
						null, 
						MessageFormat.format(Translations.get("slide.copy.error"), show.getName()), 
						error);
				alert.show();
			});
	    	return task;
		}
		AsyncTask<SlideShow> task = AsyncTaskFactory.single();
		task.cancel();
		return task;
	}

	/**
	 * Returns a task that will prompt the user for a new name for the given slide and save a new
	 * slide with that new name.
	 * <p>
	 * Returns the new slide.
	 * @param library the library to save to
	 * @param owner the window owner
	 * @param slide the slide to rename
	 * @return {@link AsyncTask}&lt;{@link Slide}&gt;
	 */
	public static final AsyncTask<Slide> slidePromptSaveAs(ObservableSlideLibrary library, Window owner, Slide slide) {
		if (slide != null) {
			String old = slide.getName();
	    	TextInputDialog prompt = new TextInputDialog(old);
	    	prompt.initOwner(owner);
	    	prompt.initModality(Modality.WINDOW_MODAL);
	    	prompt.setTitle(Translations.get("action.saveas"));
	    	prompt.setHeaderText(Translations.get("saveas.header"));
	    	prompt.setContentText(Translations.get("saveas.content"));
	    	Optional<String> result = prompt.showAndWait();
	    	// check for the "OK" button
	    	if (result.isPresent()) {
	    		String name = result.get();
	        	// create a copy of the current slide with new id
	    		Slide copy = slide.copy(false);
	    		// set the name
	    		copy.setName(name);
	    		// save it
	    		AsyncTask<Slide> task = library.save(copy, true);
		    	task.addCancelledOrFailedHandler((e) -> {
		    		Throwable error = task.getException();
		    		// log the error
					LOGGER.error("Failed to save slide as " + name + ".", error);
					// present message to user
					Alert alert = Alerts.exception(
							owner,
							null, 
							null, 
							MessageFormat.format(Translations.get("slide.saveas.error"), slide.getName(), name), 
							error);
					alert.show();
				});
		    	return task;
	    	}
		}
		AsyncTask<Slide> task = AsyncTaskFactory.single();
		task.cancel();
		return task;
	}

	/**
	 * Returns a task that will prompt the user for for confirmation to delete the given slide shows.
	 * @param library the library to import into
	 * @param owner the window owner
	 * @param shows the slide shows to delete
	 * @return {@link AsyncGroupTask}&lt;{@link AsyncTask}&lt;Void&gt;&gt;
	 */
	public static final AsyncGroupTask<AsyncTask<Void>> showPromptDelete(ObservableSlideLibrary library, Window owner, List<SlideShow> shows) {
		if (shows != null) {
			// attempt to delete the selected slide
			Alert alert = Alerts.confirm(
					owner, 
					Modality.WINDOW_MODAL, 
					Translations.get("slide.delete.title"), 
					null, 
					Translations.get("slide.delete.content"));
			
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				List<AsyncTask<Void>> tasks = new ArrayList<AsyncTask<Void>>();
				for (SlideShow b : shows) {
					if (b != null) {
						tasks.add(library.remove(b));
					}
				}
				// wrap the tasks in a multi-task wait task
				AsyncGroupTask<AsyncTask<Void>> task = new AsyncGroupTask<AsyncTask<Void>>(tasks);
				// define a listener to be called when the wrapper task completes
				task.addCompletedHandler((e) -> {
					// get the exceptions
					Exception[] exceptions = tasks
							.stream()
							.filter(t -> t.getException() != null)
							.map(t -> t.getException())
							.collect(Collectors.toList())
							.toArray(new Exception[0]);
					// did we have any errors?
					if (exceptions.length > 0) {
						Alert fAlert = Alerts.exception(
								owner,
								null, 
								null, 
								null, 
								exceptions);
						fAlert.show();
					}
				});
				// return the 
				return task;
			}
		}
		AsyncGroupTask<AsyncTask<Void>> task = AsyncTaskFactory.group();
		task.cancel();
		return task;
	}
	
	/**
	 * Returns a task that will prompt the user for for confirmation to delete the given slides.
	 * @param library the library to import into
	 * @param owner the window owner
	 * @param slides the slides to delete
	 * @return {@link AsyncGroupTask}&lt;{@link AsyncTask}&lt;Void&gt;&gt;
	 */
	public static final AsyncGroupTask<AsyncTask<Void>> slidePromptDelete(ObservableSlideLibrary library, Window owner, List<Slide> slides) {
		if (slides != null) {
			// is it used in a show?
			boolean isReferenced = library.isSlideReferenced(slides.stream().map(s -> s.getId()).collect(Collectors.toList()));
			
			// attempt to delete the selected slide
			Alert alert = Alerts.confirm(
					owner, 
					Modality.WINDOW_MODAL, 
					Translations.get("slide.delete.title"), 
					null, 
					isReferenced ? Translations.get("slide.delete.referenced") : Translations.get("slide.delete.content"));
			
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				List<AsyncTask<Void>> tasks = new ArrayList<AsyncTask<Void>>();
				for (Slide b : slides) {
					if (b != null) {
						tasks.add(library.remove(b));
					}
				}
				// wrap the tasks in a multi-task wait task
				AsyncGroupTask<AsyncTask<Void>> task = new AsyncGroupTask<AsyncTask<Void>>(tasks);
				// define a listener to be called when the wrapper task completes
				task.addCompletedHandler((e) -> {
					// get the exceptions
					Exception[] exceptions = tasks
							.stream()
							.filter(t -> t.getException() != null)
							.map(t -> t.getException())
							.collect(Collectors.toList())
							.toArray(new Exception[0]);
					// did we have any errors?
					if (exceptions.length > 0) {
						Alert fAlert = Alerts.exception(
								owner,
								null, 
								null, 
								null, 
								exceptions);
						fAlert.show();
					}
				});
				// return the 
				return task;
			}
		}
		AsyncGroupTask<AsyncTask<Void>> task = AsyncTaskFactory.group();
		task.cancel();
		return task;
	}

	/**
	 * Returns a task that will add the given tag to the given slide.
	 * @param library the library the given slide is a part of
	 * @param owner the window owner
	 * @param slide the slide
	 * @param tag the tag to add
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public static final AsyncTask<Void> slideAddTag(ObservableSlideLibrary library, Window owner, Slide slide, Tag tag) {
		// sanity check
		if (slide != null && tag != null) {
			AsyncTask<Void> task = library.addTag(slide, tag);
			task.addCancelledOrFailedHandler((e) -> {
				Throwable error = task.getException();
				// log the error
				LOGGER.error("Failed to add tag '{}' to '{}': {}", tag.getName(), slide.getName(), error.getMessage());
				// show an error to the user
				Alert alert = Alerts.exception(
						owner,
						null, 
						null, 
						MessageFormat.format(Translations.get("tags.add.error"), tag.getName()), 
						error);
				alert.show();
			});
			return task;
		}
		AsyncTask<Void> task = AsyncTaskFactory.single();
		task.cancel();
		return task;
	}
	
	/**
	 * Returns a task that will remove the given tag from the given slide.
	 * @param library the library the slide is a part of
	 * @param owner the window owner
	 * @param slide the slide
	 * @param tag the tag to remove
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public static final AsyncTask<Void> slideRemoveTag(ObservableSlideLibrary library, Window owner, Slide slide, Tag tag) {
		// sanity check
		if (slide != null && tag != null) {
			AsyncTask<Void> task = library.removeTag(slide, tag);
			task.addCancelledOrFailedHandler((e) -> {
				Throwable error = task.getException();
				// log the error
				LOGGER.error("Failed to remove tag '{}' from '{}': {}", tag.getName(), slide.getName(), error.getMessage());
				// show an error to the user
				Alert alert = Alerts.exception(
						owner,
						null, 
						null, 
						MessageFormat.format(Translations.get("tags.remove.error"), tag.getName()), 
						error);
				alert.show();
			});
			return task;
		}
		AsyncTask<Void> task = AsyncTaskFactory.single();
		task.cancel();
		return task;
	}
}
