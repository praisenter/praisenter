package org.praisenter.javafx.actions;

import java.text.MessageFormat;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.bible.Bible;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.stage.Window;

final class BibleSaveAction implements AsyncAction {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final PraisenterContext context;
	private final Window owner;
	private final Bible bible;
	private final Consumer<Bible> onSuccess;
	private final BiConsumer<Bible, Throwable> onError;
	
	public BibleSaveAction(
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
			// save
			this.context.getBibleLibrary().save(
				this.bible, 
				this.onSuccess, 
				(failed, error) -> {
					LOGGER.error("Failed to save bible " + failed.getName() + " " + failed.getId() + " due to: " + error.getMessage(), error);
					Alert alert = Alerts.exception(
							this.owner,
							null, 
							null, 
							MessageFormat.format(Translations.get("bible.save.error.content"), bible.getName()), 
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
