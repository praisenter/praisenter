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
import org.praisenter.Tag;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.async.AsyncGroupTask;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.async.AsyncTaskFactory;
import org.praisenter.media.Media;
import org.praisenter.resources.translations.Translations;
import org.praisenter.utility.MimeType;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * Wrapper class for user actions with the {@link ObservableMediaLibrary} that encapsulate
 * user related alerts and warnings.
 * @author William Bittle
 * @version 3.0.0
 */
public final class MediaActions {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** Hidden constructor */
	private MediaActions() {}
	
	/**
	 * Returns a task that will import the given paths as media items.
	 * <p>
	 * Returns the list of completed tasks all of which will either have a single media item as
	 * the result or a list of media items.
	 * @param library the library to import into
	 * @param owner the window owner
	 * @param paths the paths to import
	 * @return {@link AsyncGroupTask}&lt;{@link AsyncTask}&lt;List&lt;?&gt;&gt;&gt;
	 */
	public static final AsyncGroupTask<AsyncTask<?>> mediaImport(ObservableMediaLibrary library, Window owner, List<Path> paths) {
		// sanity check
		if (paths != null && !paths.isEmpty()) {
			// build a list of tasks to execute
			List<AsyncTask<?>> tasks = new ArrayList<AsyncTask<?>>();
			for (Path path : paths) {
				if (path != null) {
					if (MimeType.ZIP.check(path)) {
						tasks.add(library.importMedia(path));
					} else {
						tasks.add(library.add(path));
					}
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
				// did we have any exceptions?
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
	 * Returns a task that will prompt the user for files to import as media items.
	 * <p>
	 * Returns the list of completed tasks all of which will either have a single media item as
	 * the result or a list of media items.
	 * @param library the library to import into
	 * @param owner the window owner
	 * @return {@link AsyncGroupTask}&lt;{@link AsyncTask}&lt;List&lt;?&gt;&gt;&gt;
	 */
	public static final AsyncGroupTask<AsyncTask<?>> mediaPromptImport(ObservableMediaLibrary library, Window owner) {
		FileChooser chooser = new FileChooser();
    	chooser.setTitle(Translations.get("media.import.title"));
    	List<File> files = chooser.showOpenMultipleDialog(owner);
    	// null safe
    	if (files != null && files.size() > 0) {
    		List<Path> paths = files.stream().filter(f -> f != null).map(f -> f.toPath()).collect(Collectors.toList());
    		return mediaImport(library, owner, paths);
    	}
    	return AsyncTaskFactory.none();
	}

	/**
	 * Returns a task that will prompt the user to select a file to export the given media to.
	 * @param library the library to import into
	 * @param owner the window owner
	 * @param media the media to export
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public static final AsyncTask<Void> mediaPromptExport(ObservableMediaLibrary library, Window owner, List<Media> media) {
		// sanity check
		if (media != null && !media.isEmpty()) {
			String name = Translations.get("media.export.multiple.filename"); 
	    	if (media.size() == 1) {
	    		// make sure the file name doesn't have bad characters in it
	    		name = media.get(0).getPath().getFileName().toString();
	    	}
	    	FileChooser chooser = new FileChooser();
	    	chooser.setInitialFileName(name + ".zip");
	    	chooser.setTitle(Translations.get("media.export.title"));
	    	chooser.getExtensionFilters().add(new ExtensionFilter(Translations.get("export.zip.name"), Translations.get("export.zip.extension")));
	    	File file = chooser.showSaveDialog(owner);
	    	if (file != null) {
	    		final Path path = file.toPath();
	    		AsyncTask<Void> task = library.exportMedia(
					path, 
					media);
	    		task.addCancelledOrFailedHandler((e) -> {
	    			// show an error to the user
					Alert alert = Alerts.exception(
							owner,
							null, 
							null, 
							Translations.get("media.export.error"), 
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
	 * Returns a task that will prompt the user to supply a new name for the given media and will rename the media.
	 * @param library the library to import into
	 * @param owner the window owner
	 * @param media the media to rename
	 * @return {@link AsyncTask}&lt;Media&gt;
	 */
	public static final AsyncTask<Media> mediaPromptRename(ObservableMediaLibrary library, Window owner, Media media) {
		// sanity check
		if (media != null) {
			String old = media.getName();
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
	    		if (!Objects.equals(name, old)) {
		        	// update the media's name
	    			AsyncTask<Media> task = library.rename(media, name);
	    			task.addCancelledOrFailedHandler((e) -> {
		        		Throwable ex = task.getException();
						// log the error
						LOGGER.error("Failed to rename media from '{}' to '{}': {}", media.getName(), name, ex.getMessage());
						// show an error to the user
						Alert alert = Alerts.exception(
								owner,
								null, 
								null, 
								MessageFormat.format(Translations.get("media.rename.error"), media.getName(), name), 
								ex);
						alert.show();
					});
		        	return task;
	    		}
	    		// didn't change
	    	}
	    	// user cancellation
		}
    	return AsyncTaskFactory.empty();
	}

	/**
	 * Returns a task that will prompt the user for confirmation for deleting the given media items.
	 * @param library the library to import into
	 * @param owner the window owner
	 * @param media the media to delete
	 * @return {@link AsyncGroupTask}&lt;{@link AsyncTask}&lt;Void&gt;&gt;
	 */
	public static final AsyncGroupTask<AsyncTask<Void>> mediaPromptDelete(ObservableMediaLibrary library, Window owner, List<Media> media) {
		if (media != null) {
			// make sure the user really wants to do this
			Alert confirm = Alerts.confirm(
					owner, 
					Modality.WINDOW_MODAL, 
					Translations.get("media.remove.title"), 
					null, 
					Translations.get("media.remove.content"));
			Optional<ButtonType> result = confirm.showAndWait();
			// for real?
			if (result.get() == ButtonType.OK) {
				List<AsyncTask<Void>> tasks = new ArrayList<AsyncTask<Void>>();
				for (Media m : media) {
					if (m != null) {
						tasks.add(library.remove(m));
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
		}
		return AsyncTaskFactory.none();
	}

	/**
	 * Returns a task that will add the given tag to the given media.
	 * @param library the library to import into
	 * @param owner the window owner
	 * @param media the media
	 * @param tag the tag to add
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public static final AsyncTask<Void> mediaAddTag(ObservableMediaLibrary library, Window owner, Media media, Tag tag) {
		// sanity check
		if (media != null && tag != null) {
			AsyncTask<Void> task = library.addTag(media, tag);
			task.addCancelledOrFailedHandler((e) -> {
				Throwable error = task.getException();
				// log the error
				LOGGER.error("Failed to add tag '{}' for '{}': {}", tag.getName(), media.getPath().toAbsolutePath().toString(), error.getMessage());
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
		return AsyncTaskFactory.empty();
	}
	
	/**
	 * Returns a task that will delete the given tag from the given media.
	 * @param library the library to import into
	 * @param owner the window owner
	 * @param media the media
	 * @param tag the tag to remove
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public static final AsyncTask<Void> mediaDeleteTag(ObservableMediaLibrary library, Window owner, Media media, Tag tag) {
		// sanity check
		if (media != null && tag != null) {
			AsyncTask<Void> task = library.removeTag(media, tag);
			task.addCancelledOrFailedHandler((e) -> {
				Throwable error = task.getException();
				// log the error
				LOGGER.error("Failed to remove tag '{}' for '{}': {}", tag.getName(), media.getPath().toAbsolutePath().toString(), error.getMessage());
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
		return AsyncTaskFactory.empty();
	}
}
