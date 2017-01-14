package org.praisenter.javafx.actions;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.media.Media;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Window;

final class MediaPromptRenameAction implements AsyncAction {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final PraisenterContext context;
	private final Window owner;
	private final Media media;
	private final Consumer<Media> onSuccess;
	private final BiConsumer<Media, Throwable> onError;
	
	public MediaPromptRenameAction(
			PraisenterContext context, 
			Window owner, 
			Media media, 
			Consumer<Media> onSuccess, 
			BiConsumer<Media, Throwable> onError) {
		this.context = context;
		this.owner = owner;
		this.media = media;
		this.onSuccess = onSuccess;
		this.onError = onError;
	}

	@Override
	public Void call() {
		if (this.media == null) {
			return null;
		}
		
		TextInputDialog prompt = new TextInputDialog(this.media.getName());
    	prompt.initOwner(this.owner);
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
        	this.context.getMediaLibrary().rename(
    			this.media,
    			name, 
    			this.onSuccess, 
    			(Media failed, Throwable error) -> {
    				// log the error
    				LOGGER.error("Failed to rename media from '{}' to '{}': {}", failed.getName(), name, error.getMessage());
    				// show an error to the user
    				Alert alert = Alerts.exception(
    						this.owner,
    						null, 
    						null, 
    						MessageFormat.format(Translations.get("media.metadata.rename.error"), failed.getName(), name), 
    						error);
    				alert.show();
    				
    				if (this.onError != null) {
    					this.onError.accept(failed, error);
    				}
    			});
    	}
    	return null;
	}
}
