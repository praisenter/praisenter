package org.praisenter.javafx.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.MonitoredTask;
import org.praisenter.javafx.MonitoredTaskResultStatus;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.media.Media;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

final class MediaPromptExportAction implements AsyncAction {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final PraisenterContext context;
	private final Window owner;
	private final List<Media> media;
	private final Consumer<Path> onSuccess;
	private final BiConsumer<Path, Throwable> onError;
	
	public MediaPromptExportAction(
			PraisenterContext context, 
			Window owner, 
			List<Media> media, 
			Consumer<Path> onSuccess,
			BiConsumer<Path, Throwable> onError) {
		this.context = context;
		this.owner = owner;
		this.media = media;
		this.onSuccess = onSuccess;
		this.onError = onError;
	}

	@Override
	public Void call() {
		if (this.media == null || this.media.isEmpty()) {
			return null;
		}
		
		String name = Translations.get("media.export.multiple.filename"); 
    	if (this.media.size() == 1) {
    		// make sure the file name doesn't have bad characters in it
    		name = this.media.get(0).getPath().getFileName().toString();
    	}
    	FileChooser chooser = new FileChooser();
    	chooser.setInitialFileName(name + ".zip");
    	chooser.setTitle(Translations.get("media.export.title"));
    	chooser.getExtensionFilters().add(new ExtensionFilter(Translations.get("export.zip.name"), Translations.get("export.zip.extension")));
    	File file = chooser.showSaveDialog(this.owner);
    	if (file != null) {
    		final Path path = file.toPath();
    		MonitoredTask<Void> task = new MonitoredTask<Void>(MessageFormat.format(Translations.get("task.export"), name)) {
				@Override
				protected Void call() throws Exception {
					this.updateProgress(-1, 0);
					// FIXME export metadata with it? would need another set of code to read this zip then
					byte[] buffer = new byte[1024];
					try {
						try (FileOutputStream fos = new FileOutputStream(path.toFile());
							 ZipOutputStream zos = new ZipOutputStream(fos)) {
							for (Media m : media) {
								try (FileInputStream fis = new FileInputStream(m.getPath().toFile())) {
									ZipEntry entry = new ZipEntry(m.getPath().getFileName().toString());
									zos.putNextEntry(entry);
									
									int length;
									while ((length = fis.read(buffer)) > 0) {
										zos.write(buffer, 0, length);
									}
									
									zos.closeEntry();
								}
							}
						} catch (Exception ex) {
							LOGGER.error("Failed to export bibles: " + ex.getMessage(), ex);
							throw ex;
						}
						setResultStatus(MonitoredTaskResultStatus.SUCCESS);
						return null;
					} catch (Exception ex) {
						LOGGER.error("Failed to export media.", ex);
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
						Translations.get("media.export.error"), 
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
