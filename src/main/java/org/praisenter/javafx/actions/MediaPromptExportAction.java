package org.praisenter.javafx.actions;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.Alerts;
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
    		
    		context.getMediaLibrary().exportMedia(
				path, 
				this.media, 
				() -> {
					if (this.onSuccess != null) {
						this.onSuccess.accept(path);
					}
				}, 
				(error) -> {
					// show an error to the user
					Alert alert = Alerts.exception(
							this.owner,
							null, 
							null, 
							Translations.get("media.export.error"), 
							error);
					alert.show();
					
					if (this.onError != null) {
						this.onError.accept(path, error);
					}
				});
    	}
		return null;
	}
}
