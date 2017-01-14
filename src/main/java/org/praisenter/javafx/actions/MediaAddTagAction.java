package org.praisenter.javafx.actions;

import java.text.MessageFormat;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Tag;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.media.Media;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.stage.Window;

final class MediaAddTagAction implements AsyncAction {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final PraisenterContext context;
	private final Window owner;
	private final Media media;
	private final Tag tag;
	private final Consumer<Tag> onSuccess;
	private final BiConsumer<Tag, Throwable> onError;
	
	public MediaAddTagAction(
			PraisenterContext context, 
			Window owner, 
			Media media, 
			Tag tag,
			Consumer<Tag> onSuccess, 
			BiConsumer<Tag, Throwable> onError) {
		this.context = context;
		this.owner = owner;
		this.media = media;
		this.tag = tag;
		this.onSuccess = onSuccess;
		this.onError = onError;
	}

	@Override
	public Void call() {
		if (this.media == null || this.tag == null) {
			return null;
		}
		
		this.context.getMediaLibrary().addTag(
			this.media, 
			this.tag, 
			(Tag addedTag) -> {
				// add the tag to the global list of tags
				this.context.getTags().add(addedTag);
				
				if (this.onSuccess != null) {
					this.onSuccess.accept(addedTag);
				}
			},
			(Tag failedTag, Throwable error) -> {
				// log the error
				LOGGER.error("Failed to add tag '{}' for '{}': {}", failedTag.getName(), this.media.getPath().toAbsolutePath().toString(), error.getMessage());
				// show an error to the user
				Alert alert = Alerts.exception(
						this.owner,
						null, 
						null, 
						MessageFormat.format(Translations.get("tags.add.error"), failedTag.getName()), 
						error);
				alert.show();
				
				if (this.onError != null) {
					this.onError.accept(failedTag, error);
				}
			});
    	return null;
	}
}
