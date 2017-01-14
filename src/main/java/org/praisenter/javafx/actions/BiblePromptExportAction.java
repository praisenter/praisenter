package org.praisenter.javafx.actions;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.bible.PraisenterBibleExporter;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.MonitoredTask;
import org.praisenter.javafx.MonitoredTaskResultStatus;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

final class BiblePromptExportAction implements AsyncAction {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final PraisenterContext context;
	private final Window owner;
	private final List<Bible> bibles;
	private final Consumer<Path> onSuccess;
	private final BiConsumer<Path, Throwable> onError;
	
	public BiblePromptExportAction(
			PraisenterContext context, 
			Window owner, 
			List<Bible> bibles, 
			Consumer<Path> onSuccess,
			BiConsumer<Path, Throwable> onError) {
		this.context = context;
		this.owner = owner;
		this.bibles = bibles;
		this.onSuccess = onSuccess;
		this.onError = onError;
	}

	@Override
	public Void call() {
		if (this.bibles == null || this.bibles.isEmpty()) {
			return null;
		}
		
		String name = Translations.get("bible.export.multiple.filename"); 
    	if (this.bibles.size() == 1) {
    		// make sure the file name doesn't have bad characters in it
    		name = BibleLibrary.createFileName(this.bibles.get(0));
    	}
    	FileChooser chooser = new FileChooser();
    	chooser.setInitialFileName(name + ".zip");
    	chooser.setTitle(Translations.get("bible.export.title"));
    	chooser.getExtensionFilters().add(new ExtensionFilter(Translations.get("export.zip.name"), Translations.get("export.zip.extension")));
    	File file = chooser.showSaveDialog(this.owner);
    	if (file != null) {
    		final Path path = file.toPath();
    		final PraisenterBibleExporter exporter = new PraisenterBibleExporter();
    		MonitoredTask<Void> task = new MonitoredTask<Void>(MessageFormat.format(Translations.get("task.export"), name)) {
				@Override
				protected Void call() throws Exception {
					this.updateProgress(-1, 0);
					try {
						exporter.execute(path, bibles);
						setResultStatus(MonitoredTaskResultStatus.SUCCESS);
						return null;
					} catch (Exception ex) {
						LOGGER.error("Failed to export bible(s).", ex);
						setResultStatus(MonitoredTaskResultStatus.ERROR);
						throw ex;
					}
				}
			};
			task.setOnSucceeded((e) -> {
				if (this.onSuccess != null) {
					this.onSuccess.accept(path);
				}
			});
			task.setOnFailed((e) -> {
				// show an error to the user
				Alert alert = Alerts.exception(
						this.owner,
						null, 
						null, 
						Translations.get("bible.export.error"), 
						task.getException());
				alert.show();
				
				if (this.onError != null) {
					this.onError.accept(path, task.getException());
				}
			});
    		this.context.getExecutorService().execute(task);
    	}
		return null;
	}
}
