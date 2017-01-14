package org.praisenter.javafx.actions;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.praisenter.bible.Bible;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Window;

final class BiblePromptSaveAsAction implements AsyncAction {
	private final PraisenterContext context;
	private final Window owner;
	private final Bible bible;
	private final Consumer<Bible> onSuccess;
	private final BiConsumer<Bible, Throwable> onError;
	
	public BiblePromptSaveAsAction(
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
		// make sure it's non-null
		if (this.bible != null) {
			String old = this.bible.getName();
			
	    	TextInputDialog prompt = new TextInputDialog(old);
	    	prompt.initOwner(this.owner);
	    	prompt.initModality(Modality.WINDOW_MODAL);
	    	prompt.setTitle(Translations.get("action.saveas"));
	    	prompt.setHeaderText(Translations.get("saveas.header"));
	    	prompt.setContentText(Translations.get("saveas.content"));
	    	Optional<String> result = prompt.showAndWait();
	    	
	    	// check for the "OK" button
	    	if (result.isPresent()) {
	    		String name = result.get();
	        	// create a copy of the current bible
	    		// with new id
	    		Bible copy = this.bible.copy(false);
	    		// set the name
	    		copy.setName(name);
	    		
	    		// then save
	    		(new BibleSaveAction(
    				this.context,
    				this.owner,
    				copy, 
    				this.onSuccess, 
    				this.onError))
	    		.call();
	    	}
		}
		return null;
	}
}
