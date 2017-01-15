package org.praisenter.javafx.actions;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.FailedOperation;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.PraisenterMultiTask;
import org.praisenter.javafx.PraisenterTask;
import org.praisenter.javafx.media.ObservableMediaLibrary;
import org.praisenter.media.Media;
import org.praisenter.resources.translations.Translations;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;

public final class Actions {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private Actions() {}
	
//	public static final PraisenterMultiTask<List<Media>, Path> mediaImport(ObservableMediaLibrary library, Window owner, List<Path> paths) {
//		// attempt to import them
//		PraisenterMultiTask<List<Media>, Path> task = library.add(paths);
//		task.onFailedProperty().addListener((e) -> {
//			List<FailedOperation<Path>> failures = task.getResultFailures();
//			// get the exceptions
//			Exception[] exceptions = failures.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
//			// get the failed media
//			String list = String.join(", ", failures.stream().map(f -> f.getData().getFileName().toString()).collect(Collectors.toList()));
//			Alert alert = Alerts.exception(
//					owner,
//					null, 
//					null, 
//					MessageFormat.format(Translations.get("media.import.error"), list), 
//					exceptions);
//			alert.show();
//		});
//		return task;
//	}
	
	public static final PraisenterMultiTask<List<Media>, Path> mediaImport(ObservableMediaLibrary library, Window owner, List<Path> paths) {
		// TODO return a task that waits on the cyclic barrier to complete
		CyclicBarrier barrier = new CyclicBarrier(paths.size() + 1);
		
		// attempt to import them
		PraisenterMultiTask<List<Media>, Path> task = library.add(paths);
		task.onFailedProperty().addListener((e) -> {
			List<FailedOperation<Path>> failures = task.getResultFailures();
			// get the exceptions
			Exception[] exceptions = failures.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
			// get the failed media
			String list = String.join(", ", failures.stream().map(f -> f.getData().getFileName().toString()).collect(Collectors.toList()));
			Alert alert = Alerts.exception(
					owner,
					null, 
					null, 
					MessageFormat.format(Translations.get("media.import.error"), list), 
					exceptions);
			alert.show();
		});
		return task;
	}
	
	public static final PraisenterTask<Media> mediaImport(ObservableMediaLibrary library, Window owner, Path path) {
		// attempt to import them
		PraisenterTask<Media> task = library.add(path);
		task.onFailedProperty().addListener((e) -> {
			// get the failed media
			Alert alert = Alerts.exception(
					owner,
					null, 
					null, 
					MessageFormat.format(Translations.get("media.import.error"), path.getFileName().toString()), 
					task.getException());
			alert.show();
		});
		
		return task;
	}
	
	public static final PraisenterMultiTask<List<Media>, Path> mediaPromptImport(ObservableMediaLibrary library, Window owner) {
		FileChooser chooser = new FileChooser();
    	chooser.setTitle(Translations.get("media.import.title"));
    	List<File> files = chooser.showOpenMultipleDialog(owner);
    	// null safe
    	if (files != null && files.size() > 0) {
			files = new ArrayList<File>();
    	}
    	List<Path> paths = files.stream().map(f -> f.toPath()).collect(Collectors.toList());
		return mediaImport(library, owner, paths);
	}

	public static final PraisenterTask<Media> mediaPromptRename(ObservableMediaLibrary library, Window owner, Media media) {
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
        	PraisenterTask<Media> task = library.rename(media, name);
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
    	return null;
	}
}
