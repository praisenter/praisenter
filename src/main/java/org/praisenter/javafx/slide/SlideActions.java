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
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.async.AsyncGroupTask;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.async.AsyncTaskFactory;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideLibrary;

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
	 * @param library the library to import into
	 * @param owner the window owner
	 * @param paths the paths to import
	 * @return {@link AsyncGroupTask}&lt;{@link AsyncTask}&lt;List&lt;{@link Slide}&gt;&gt;&gt;
	 */
	public static final AsyncGroupTask<AsyncTask<List<Slide>>> slideImport(ObservableSlideLibrary library, Window owner, List<Path> paths) {
		// sanity check
		if (paths != null && !paths.isEmpty()) {
			// build a list of tasks to execute
			List<AsyncTask<List<Slide>>> tasks = new ArrayList<AsyncTask<List<Slide>>>();
			for (Path path : paths) {
				if (path != null) {
					// FIXME handle zip files
					tasks.add(library.add(path));
				}
			}
			// wrap the tasks in a multi-task wait task
			AsyncGroupTask<AsyncTask<List<Slide>>> task = new AsyncGroupTask<AsyncTask<List<Slide>>>(tasks);
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
		return AsyncTaskFactory.none();
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
			AsyncTask<Slide> task = library.save(slide);
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
		return AsyncTaskFactory.empty();
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
		    		AsyncTask<Slide> task = library.save(MessageFormat.format(Translations.get("task.rename"), old, name), slide);
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
		return AsyncTaskFactory.empty();
	}

	/**
	 * Returns a task that will prompt the user to select a file name to export the given slides to.
	 * @param library the library to save to
	 * @param owner the window owner
	 * @param slides the slides to export
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public static final AsyncTask<Void> slidePromptExport(ObservableSlideLibrary library, Window owner, List<Slide> slides) {
		// sanity check
		// TODO export media with it?
		if (slides != null && !slides.isEmpty()) {
			String name = Translations.get("slide.export.multiple.filename"); 
	    	if (slides.size() == 1) {
	    		// make sure the file name doesn't have bad characters in it
	    		name = SlideLibrary.createFileName(slides.get(0));
	    	}
	    	FileChooser chooser = new FileChooser();
	    	chooser.setInitialFileName(name + ".zip");
	    	chooser.setTitle(Translations.get("slide.export.title"));
	    	chooser.getExtensionFilters().add(new ExtensionFilter(Translations.get("export.zip.name"), Translations.get("export.zip.extension")));
	    	File file = chooser.showSaveDialog(owner);
	    	// check for cancellation
	    	if (file != null) {
	    		final Path path = file.toPath();
	    		AsyncTask<Void> task = library.exportSlides(path, slides);
				task.addCancelledOrFailedHandler((e) -> {
					// show an error to the user
					Alert alert = Alerts.exception(
							owner,
							null, 
							null, 
							Translations.get("slide.export.error"), 
							task.getException());
					alert.show();
				});
				return task;
	    	}
		}
		// user cancellation
		return AsyncTaskFactory.empty();
	}

	/**
	 * Returns a task that will prompt the user for files to import as slides.
	 * <p>
	 * Returns a list of the slides that were imported successfully.
	 * @param library the library to import into
	 * @param owner the window owner
	 * @return {@link AsyncGroupTask}&lt;{@link AsyncTask}&lt;List&lt;{@link Slide}&gt;&gt;&gt;
	 */
	public static final AsyncGroupTask<AsyncTask<List<Slide>>> slidePromptImport(ObservableSlideLibrary library, Window owner) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(Translations.get("slide.import.title"));
		List<File> files = chooser.showOpenMultipleDialog(owner);
		if (files != null && files.size() > 0) {
			List<Path> paths = files
					.stream()
					.filter(f -> f != null && f.isFile())
					.map(f -> f.toPath())
					.collect(Collectors.toList());
			return slideImport(library, owner, paths);
		}
		return AsyncTaskFactory.none();
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
			AsyncTask<Slide> task = library.save(copy);
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
		return AsyncTaskFactory.empty();
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
	    		AsyncTask<Slide> task = library.save(copy);
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
		return AsyncTaskFactory.empty();
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
		return AsyncTaskFactory.none();
	}
	
}
