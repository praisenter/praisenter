package org.praisenter.javafx.actions;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.bible.Bible;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Window;

final class BiblePromptRenameAction implements AsyncAction {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final PraisenterContext context;
	private final Window owner;
	private final Bible bible;
	private final Consumer<Bible> onSuccess;
	private final BiConsumer<Bible, Throwable> onError;
	
	public BiblePromptRenameAction(
			PraisenterContext context, 
			Window owner, 
			Bible bible, 
			Consumer<Bible> onSuccess, 
			BiConsumer<Bible, Throwable> onError) {
		this.context = context;
		this.owner = owner;
		this.bible = bible;
		this.onSuccess = onSuccess;
		this.onError = onError;
	}

	@Override
	public Void call() {
		if (this.bible == null) {
			return null;
		}
		
		String old = this.bible.getName();
    	TextInputDialog prompt = new TextInputDialog(old);
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
        	// update the bible's name
    		this.bible.setName(name);
        	this.context.getBibleLibrary().save(
    			MessageFormat.format(Translations.get("task.rename"), old, name),
    			this.bible, 
    			this.onSuccess, 
    			(Bible failed, Throwable error) -> {
    				this.bible.setName(old);
    				// log the error
    				LOGGER.error("Failed to rename bible from '{}' to '{}': {}", old, name, error.getMessage());
    				// show an error to the user
    				Alert alert = Alerts.exception(
    						this.owner,
    						null, 
    						null, 
    						MessageFormat.format(Translations.get("bible.metadata.rename.error"), old, name), 
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
