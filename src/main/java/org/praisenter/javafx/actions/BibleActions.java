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
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.bible.PraisenterBibleExporter;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.async.AsyncGroupTask;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.async.AsyncTaskFactory;
import org.praisenter.javafx.bible.ObservableBibleLibrary;
import org.praisenter.resources.translations.Translations;

import javafx.beans.InvalidationListener;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Window;

public final class BibleActions {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private BibleActions() {}

	public static final AsyncGroupTask<AsyncTask<List<Bible>>> bibleImport(ObservableBibleLibrary library, Window owner, List<Path> paths) {
		// sanity check
		if (paths != null && !paths.isEmpty()) {
			// build a list of tasks to execute
			List<AsyncTask<List<Bible>>> tasks = new ArrayList<AsyncTask<List<Bible>>>();
			for (Path path : paths) {
				if (path != null) {
					tasks.add(library.add(path));
				}
			}
			// wrap the tasks in a multi-task wait task
			AsyncGroupTask<AsyncTask<List<Bible>>> task = new AsyncGroupTask<AsyncTask<List<Bible>>>(tasks);
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
			});
			// return the 
			return task;
		}
		return AsyncTaskFactory.none();
	}
	
	public static final AsyncTask<Bible> bibleSave(ObservableBibleLibrary library, Window owner, Bible bible) {
		// sanity check
		if (bible != null) {
			AsyncTask<Bible> task = library.save(bible);
	    	task.addCancelledOrFailedHandler((e) -> {
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
		return AsyncTaskFactory.empty();
	}

	public static final AsyncTask<Bible> biblePromptRename(ObservableBibleLibrary library, Window owner, Bible bible) {
		// sanity check
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
		    		AsyncTask<Bible> task = library.save(MessageFormat.format(Translations.get("task.rename"), old, name), bible);
		        	task.addCancelledOrFailedHandler((e) -> {
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
		return AsyncTaskFactory.empty();
	}

	public static final AsyncTask<Void> biblePromptExport(ObservableBibleLibrary library, Window owner, List<Bible> bibles) {
		// sanity check
		if (bibles != null && !bibles.isEmpty()) {
			String name = Translations.get("bible.export.multiple.filename"); 
	    	if (bibles.size() == 1) {
	    		// make sure the file name doesn't have bad characters in it
	    		name = BibleLibrary.createFileName(bibles.get(0));
	    	}
	    	FileChooser chooser = new FileChooser();
	    	chooser.setInitialFileName(name + ".zip");
	    	chooser.setTitle(Translations.get("bible.export.title"));
	    	chooser.getExtensionFilters().add(new ExtensionFilter(Translations.get("export.zip.name"), Translations.get("export.zip.extension")));
	    	File file = chooser.showSaveDialog(owner);
	    	// check for cancellation
	    	if (file != null) {
	    		final Path path = file.toPath();
	    		final PraisenterBibleExporter exporter = new PraisenterBibleExporter();
	    		// TODO this should be in the ObservableBibleLibrary
	    		AsyncTask<Void> task = new AsyncTask<Void>(MessageFormat.format(Translations.get("task.export"), name)) {
					@Override
					protected Void call() throws Exception {
						this.updateProgress(-1, 0);
						exporter.execute(path, bibles);
						return null;
					}
				};
				task.addCancelledOrFailedHandler((e) -> {
					// show an error to the user
					Alert alert = Alerts.exception(
							owner,
							null, 
							null, 
							Translations.get("bible.export.error"), 
							task.getException());
					alert.show();
				});
				return task;
	    	}
		}
		// user cancellation
		return AsyncTaskFactory.empty();
	}

	public static final AsyncGroupTask<AsyncTask<List<Bible>>> biblePromptImport(ObservableBibleLibrary library, Window owner) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(Translations.get("bible.import.title"));
		List<File> files = chooser.showOpenMultipleDialog(owner);
		if (files != null && files.size() > 0) {
			List<Path> paths = files
					.stream()
					.filter(f -> f != null && f.isFile())
					.map(f -> f.toPath())
					.collect(Collectors.toList());
			return bibleImport(library, owner, paths);
		}
		return AsyncTaskFactory.none();
	}

	public static final AsyncTask<Bible> bibleCopy(ObservableBibleLibrary library, Window owner, Bible bible) {
		if (bible != null) {
			// make a copy
			Bible copy = bible.copy(false);
			// set the name to something else
			copy.setName(MessageFormat.format(Translations.get("copyof"), bible.getName()));
			// save it
			AsyncTask<Bible> task = library.save(copy);
	    	task.addCancelledOrFailedHandler((e) -> {
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
		return AsyncTaskFactory.empty();
	}

	public static final AsyncTask<Bible> biblePromptSaveAs(ObservableBibleLibrary library, Window owner, Bible bible) {
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
	    		AsyncTask<Bible> task = library.save(copy);
		    	task.addCancelledOrFailedHandler((e) -> {
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
		return AsyncTaskFactory.empty();
	}

	public static final AsyncGroupTask<AsyncTask<Void>> biblePromptDelete(ObservableBibleLibrary library, Window owner, List<Bible> bibles) {
		if (bibles != null) {
			// attempt to delete the selected bible
			Alert alert = Alerts.confirm(
					owner, 
					Modality.WINDOW_MODAL, 
					Translations.get("bible.delete.title"), 
					null, 
					Translations.get("bible.delete.content"));
			
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				List<AsyncTask<Void>> tasks = new ArrayList<AsyncTask<Void>>();
				for (Bible b : bibles) {
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
