package org.praisenter.javafx.actions;

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
import org.praisenter.bible.Bible;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.async.EmptyExecutableTask;
import org.praisenter.javafx.async.EmptyPraisenterMultiTask;
import org.praisenter.javafx.async.EmptyPraisenterTask;
import org.praisenter.javafx.async.ExecutableTask;
import org.praisenter.javafx.async.PraisenterMultiTask;
import org.praisenter.javafx.async.PraisenterTask;
import org.praisenter.javafx.bible.ObservableBibleLibrary;
import org.praisenter.javafx.media.ObservableMediaLibrary;
import org.praisenter.media.Media;
import org.praisenter.resources.translations.Translations;
import org.praisenter.utility.MimeType;

import javafx.beans.InvalidationListener;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Window;

public final class Actions {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private Actions() {}
	
	// Media
	
	public static final PraisenterMultiTask<PraisenterTask<?, Path>> mediaImport(ObservableMediaLibrary library, Window owner, List<Path> paths) {
		// sanity check
		if (paths != null && !paths.isEmpty()) {
			// build a list of tasks to execute
			List<PraisenterTask<?, Path>> tasks = new ArrayList<PraisenterTask<?, Path>>();
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
			PraisenterMultiTask<PraisenterTask<?, Path>> task = new PraisenterMultiTask<PraisenterTask<?, Path>>("", tasks);
			// define a listener to be called when the wrapper task completes
			InvalidationListener listener = (obs) -> {
				// get the exceptions
				Exception[] exceptions = tasks
						.stream()
						.filter(t -> t.getException() != null)
						.map(t -> t.getException())
						.collect(Collectors.toList())
						.toArray(new Exception[0]);
				// did we have any exceptions?
				if (exceptions.length > 0) {
					// get the failed media
					String list = String.join(", ", tasks
							.stream()
							.filter(t -> t.getException() != null)
							.map(t -> t.getInput().getFileName().toString())
							.collect(Collectors.toList()));
					Alert alert = Alerts.exception(
							owner,
							null, 
							null, 
							MessageFormat.format(Translations.get("media.import.error"), list), 
							exceptions);
					alert.show();
				}
			};
			// on completion
			task.onFailedProperty().addListener(listener);
			task.onCancelledProperty().addListener(listener);
			task.onSucceededProperty().addListener(listener);
			// return the 
			return task;
		}
		return EmptyPraisenterMultiTask.create();
	}
	
	public static final PraisenterMultiTask<PraisenterTask<?, Path>> mediaPromptImport(ObservableMediaLibrary library, Window owner) {
		FileChooser chooser = new FileChooser();
    	chooser.setTitle(Translations.get("media.import.title"));
    	List<File> files = chooser.showOpenMultipleDialog(owner);
    	// null safe
    	if (files != null && files.size() > 0) {
    		List<Path> paths = files.stream().filter(f -> f != null).map(f -> f.toPath()).collect(Collectors.toList());
    		return mediaImport(library, owner, paths);
    	}
    	return EmptyPraisenterMultiTask.create();
	}

	public static final PraisenterTask<Void, List<Media>> mediaPromptExport(ObservableMediaLibrary library, Window owner, List<Media> media) {
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
	    		PraisenterTask<Void, List<Media>> task = library.exportMedia(
					path, 
					media);
	    		task.onFailedProperty().addListener(obs -> {
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
    	return EmptyPraisenterTask.create();
	}
	
	public static final PraisenterTask<Media, String> mediaPromptRename(ObservableMediaLibrary library, Window owner, Media media) {
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
		        	PraisenterTask<Media, String> task = library.rename(media, name);
		        	task.onFailedProperty().addListener((e) -> {
		        		Throwable ex = task.getException();
						// log the error
						LOGGER.error("Failed to rename media from '{}' to '{}': {}", media.getName(), name, ex.getMessage());
						// show an error to the user
						Alert alert = Alerts.exception(
								owner,
								null, 
								null, 
								MessageFormat.format(Translations.get("media.metadata.rename.error"), media.getName(), name), 
								ex);
						alert.show();
					});
		        	return task;
	    		}
	    		// didn't change
	    	}
	    	// user cancellation
		}
    	return EmptyPraisenterTask.create();
	}

	public static final PraisenterMultiTask<PraisenterTask<Void, Media>> mediaPromptDelete(ObservableMediaLibrary library, Window owner, List<Media> media) {
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
				List<PraisenterTask<Void, Media>> tasks = new ArrayList<PraisenterTask<Void, Media>>();
				for (Media m : media) {
					if (m != null) {
						tasks.add(library.remove(m));
					}
				}
				// wrap the tasks in a multi-task wait task
				PraisenterMultiTask<PraisenterTask<Void, Media>> task = new PraisenterMultiTask<PraisenterTask<Void, Media>>("", tasks);
				// define a listener to be called when the wrapper task completes
				InvalidationListener listener = (obs) -> {
					// get the exceptions
					Exception[] exceptions = tasks
							.stream()
							.filter(t -> t.getException() != null)
							.map(t -> t.getException())
							.collect(Collectors.toList())
							.toArray(new Exception[0]);
					// did we have any errors?
					if (exceptions.length > 0) {
						// get the failed media
						String list = String.join(", ", tasks
								.stream()
								.filter(t -> t.getException() != null)
								.map(t -> t.getInput().getName())
								.collect(Collectors.toList()));
						Alert alert = Alerts.exception(
								owner,
								null, 
								null, 
								MessageFormat.format(Translations.get("media.remove.error"), list), 
								exceptions);
						alert.show();
					}
				};
				// on completion
				task.onFailedProperty().addListener(listener);
				task.onCancelledProperty().addListener(listener);
				task.onSucceededProperty().addListener(listener);
				// return the 
				return task;
			}
		}
		return EmptyPraisenterMultiTask.create();
	}

	public static final ExecutableTask<Void> mediaAddTag(ObservableMediaLibrary library, Window owner, Media media, Tag tag) {
		// sanity check
		if (media != null && tag != null) {
			ExecutableTask<Void> task = library.addTag(media, tag);
			task.onFailedProperty().addListener(obs -> {
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
		return EmptyExecutableTask.create();
	}
	
	public static final ExecutableTask<Void> mediaDeleteTag(ObservableMediaLibrary library, Window owner, Media media, Tag tag) {
		// sanity check
		if (media != null && tag != null) {
			ExecutableTask<Void> task = library.addTag(media, tag);
			task.onFailedProperty().addListener(obs -> {
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
		return EmptyExecutableTask.create();
	}

	// Bibles
	
	public static final PraisenterMultiTask<PraisenterTask<List<Bible>, Path>> bibleImport(ObservableBibleLibrary library, Window owner, List<Path> paths) {
		// sanity check
		if (paths != null && !paths.isEmpty()) {
			// build a list of tasks to execute
			List<PraisenterTask<List<Bible>, Path>> tasks = new ArrayList<PraisenterTask<List<Bible>, Path>>();
			for (Path path : paths) {
				if (path != null) {
					tasks.add(library.add(path));
				}
			}
			// wrap the tasks in a multi-task wait task
			PraisenterMultiTask<PraisenterTask<List<Bible>, Path>> task = new PraisenterMultiTask<PraisenterTask<List<Bible>, Path>>("", tasks);
			// define a listener to be called when the wrapper task completes
			InvalidationListener listener = (obs) -> {
				// get the exceptions
				Exception[] exceptions = tasks
						.stream()
						.filter(t -> t.getException() != null)
						.map(t -> t.getException())
						.collect(Collectors.toList())
						.toArray(new Exception[0]);
				// did we have any errors?
				if (exceptions.length > 0) {
					// get the failed media
					String list = String.join(", ", tasks
							.stream()
							.filter(t -> t.getException() != null)
							.map(t -> t.getInput().getFileName().toString())
							.collect(Collectors.toList()));
					Alert alert = Alerts.exception(
							owner,
							null, 
							null, 
							MessageFormat.format(Translations.get("bible.import.error"), list), 
							exceptions);
					alert.show();
				} else {
					// get the warnings
					List<String> wFileNames = tasks
							.stream()
							.map(t -> t.getValue())
							.flatMap(List::stream)
							.filter(b -> b.hadImportWarning())
							.map(f -> f.getName())
							.collect(Collectors.toList());
					// did we have any warnings?
					if (wFileNames.size() > 0) {
						// show a dialog of the files that had warnings
						String list = String.join(", ", wFileNames);
						Alert alert = Alerts.info(
								owner, 
								Modality.WINDOW_MODAL,
								Translations.get("bible.import.info.title"), 
								Translations.get("bible.import.info.header"), 
								list);
						alert.show();
					}
				}
			};
			// on completion
			task.onFailedProperty().addListener(listener);
			task.onCancelledProperty().addListener(listener);
			task.onSucceededProperty().addListener(listener);
			// return the 
			return task;
		}
		return EmptyPraisenterMultiTask.create();
	}
	
	public static final PraisenterTask<Bible, Bible> bibleSave(ObservableBibleLibrary library, Window owner, Bible bible) {
		if (bible != null) {
	    	PraisenterTask<Bible, Bible> task = library.save(bible);
	    	task.onFailedProperty().addListener((e) -> {
	    		Throwable error = task.getException();
	    		LOGGER.error("Failed to save bible " + bible.getName() + " " + bible.getId() + " due to: " + error.getMessage(), error);
				Alert alert = Alerts.exception(
						owner,
						null, 
						null, 
						MessageFormat.format(Translations.get("bible.save.error.content"), bible.getName()), 
						error);
				alert.show();
			});
	    	return task;
		}
		return EmptyPraisenterTask.create();
	}
	
	public static final PraisenterTask<Bible, Bible> biblePromptRename(ObservableBibleLibrary library, Window owner, Bible bible) {
		if (bible != null) {
			String old = bible.getName();
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
		        	// update the bible's name
		    		bible.setName(name);
		        	PraisenterTask<Bible, Bible> task = library.save(MessageFormat.format(Translations.get("task.rename"), old, name), bible);
		        	task.onFailedProperty().addListener((e) -> {
		        		Throwable error = task.getException();
		        		bible.setName(old);
						// log the error
						LOGGER.error("Failed to rename bible from '{}' to '{}': {}", old, name, error.getMessage());
						// show an error to the user
						Alert alert = Alerts.exception(
								owner,
								null, 
								null, 
								MessageFormat.format(Translations.get("bible.metadata.rename.error"), old, name), 
								error);
						alert.show();
					});
		        	return task;
	        	}
	    		// names were the same
	    	}
	    	// user cancellation
		}
    	return EmptyPraisenterTask.create();
	}
	
	public static final PraisenterTask<Bible, Bible> bibleCopy(ObservableBibleLibrary library, Window owner, Bible bible) {
		if (bible != null) {
			// make a copy
			Bible copy = bible.copy(false);
			// set the name to something else
			copy.setName(MessageFormat.format(Translations.get("copyof"), bible.getName()));
			// save it
	    	PraisenterTask<Bible, Bible> task = library.save(copy);
	    	task.onFailedProperty().addListener((e) -> {
	    		Throwable error = task.getException();
	    		// log the error
				LOGGER.error("Failed to copy bible.", error);
				// present message to user
				Alert alert = Alerts.exception(
						owner,
						null, 
						null, 
						MessageFormat.format(Translations.get("bible.copy.error"), bible.getName()), 
						error);
				alert.show();
			});
	    	return task;
		}
		return EmptyPraisenterTask.create();
	}
	
	public static final PraisenterTask<Bible, Bible> biblePromptSaveAs(ObservableBibleLibrary library, Window owner, Bible bible) {
		if (bible != null) {
			String old = bible.getName();
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
	        	// create a copy of the current bible with new id
	    		Bible copy = bible.copy(false);
	    		// set the name
	    		copy.setName(name);
	    		// save it
		    	PraisenterTask<Bible, Bible> task = library.save(copy);
		    	task.onFailedProperty().addListener((e) -> {
		    		Throwable error = task.getException();
		    		// log the error
					LOGGER.error("Failed to save bible as " + name + ".", error);
					// present message to user
					Alert alert = Alerts.exception(
							owner,
							null, 
							null, 
							MessageFormat.format(Translations.get("bible.saveas.error"), bible.getName(), name), 
							error);
					alert.show();
				});
		    	return task;
	    	}
		}
		return EmptyPraisenterTask.create();
	}
	
	// Songs
	
	// Slides
}
