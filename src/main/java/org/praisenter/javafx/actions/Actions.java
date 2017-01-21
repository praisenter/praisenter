package org.praisenter.javafx.actions;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.parser.EmptyParser;
import org.praisenter.Tag;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.async.EmptyPraisenterMultiTask;
import org.praisenter.javafx.async.EmptyPraisenterTask;
import org.praisenter.javafx.async.ExecutableTask;
import org.praisenter.javafx.async.PraisenterMultiTask;
import org.praisenter.javafx.async.PraisenterTask;
import org.praisenter.javafx.media.ObservableMediaLibrary;
import org.praisenter.media.Media;
import org.praisenter.resources.translations.Translations;

import javafx.beans.InvalidationListener;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;

public final class Actions {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private Actions() {}
	
	public static final PraisenterMultiTask<Media, Path> mediaImport(ObservableMediaLibrary library, Window owner, List<Path> paths) {
		List<PraisenterTask<Media, Path>> tasks = new ArrayList<PraisenterTask<Media, Path>>();
		for (Path path : paths) {
			tasks.add(library.add(path));
		}
		// wrap the tasks in a multi-task wait task
		PraisenterMultiTask<Media, Path> task = new PraisenterMultiTask<Media, Path>("", tasks);
		// define a listener to be called when the wrapper task completes
		InvalidationListener listener = (obs) -> {
			// get the exceptions
			Exception[] exceptions = tasks.stream().map(t -> t.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
			// get the failed media
			String list = String.join(", ", tasks.stream().map(t -> t.getInput().getFileName().toString()).collect(Collectors.toList()));
			Alert alert = Alerts.exception(
					owner,
					null, 
					null, 
					MessageFormat.format(Translations.get("media.import.error"), list), 
					exceptions);
			alert.show();
		};
		// on completion
		task.onFailedProperty().addListener(listener);
		task.onCancelledProperty().addListener(listener);
		task.onSucceededProperty().addListener(listener);
		// return the 
		return task;
	}
	
	public static final PraisenterMultiTask<Media, Path> mediaPromptImport(ObservableMediaLibrary library, Window owner) {
		FileChooser chooser = new FileChooser();
    	chooser.setTitle(Translations.get("media.import.title"));
    	List<File> files = chooser.showOpenMultipleDialog(owner);
    	// null safe
    	if (files != null && files.size() > 0) {
    		List<Path> paths = files.stream().map(f -> f.toPath()).collect(Collectors.toList());
    		return mediaImport(library, owner, paths);
    	}
    	return EmptyPraisenterMultiTask.create();
	}

	public static final PraisenterTask<Media, String> mediaPromptRename(ObservableMediaLibrary library, Window owner, Media media) {
		TextInputDialog prompt = new TextInputDialog(media.getName());
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
    	// user cancellation
    	return EmptyPraisenterTask.create();
	}

	public static final PraisenterMultiTask<Void, Media> mediaPromptDelete(ObservableMediaLibrary library, Window owner, List<Media> media) {
		// make sure the user really wants to do this
		Alert confirm = Alerts.confirm(
				owner, 
				Modality.WINDOW_MODAL, 
				Translations.get("media.remove.title"), 
				null, 
				Translations.get("media.remove.content"));
		Optional<ButtonType> result = confirm.showAndWait();
		
		if (result.get() == ButtonType.OK) {
			List<PraisenterTask<Void, Media>> tasks = new ArrayList<PraisenterTask<Void, Media>>();
			for (Media m : media) {
				tasks.add(library.remove(m));
			}
			// wrap the tasks in a multi-task wait task
			PraisenterMultiTask<Void, Media> task = new PraisenterMultiTask<Void, Media>("", tasks);
			// define a listener to be called when the wrapper task completes
			InvalidationListener listener = (obs) -> {
				// get the exceptions
				Exception[] exceptions = tasks.stream().map(t -> t.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
				// get the failed media
				String list = String.join(", ", tasks.stream().map(t -> t.getInput().getName()).collect(Collectors.toList()));
				Alert alert = Alerts.exception(
						owner,
						null, 
						null, 
						MessageFormat.format(Translations.get("media.remove.error"), list), 
						exceptions);
				alert.show();
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

	public static final ExecutableTask<Void> mediaAddTag(ObservableMediaLibrary library, Window owner, Media media, Tag tag) {
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
	
	public static final ExecutableTask<Void> mediaDeleteTag(ObservableMediaLibrary library, Window owner, Media media, Tag tag) {
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
}
