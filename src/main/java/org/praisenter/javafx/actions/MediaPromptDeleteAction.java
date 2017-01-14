package org.praisenter.javafx.actions;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.praisenter.FailedOperation;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.media.Media;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Window;

final class MediaPromptDeleteAction implements AsyncAction {
	private final PraisenterContext context;
	private final Window owner;
	private final List<Media> media;
	private final Runnable onSuccess;
	private final Consumer<List<FailedOperation<Media>>> onError;
	
	public MediaPromptDeleteAction(
			PraisenterContext context, 
			Window owner, 
			List<Media> media,
			Runnable onSuccess,
			Consumer<List<FailedOperation<Media>>> onError) {
		this.context = context;
		this.owner = owner;
		this.media = media;
		this.onSuccess = onSuccess;
		this.onError = onError;
	}

	@Override
	public Void call() {
		// are there any items selected?
		if (this.media != null && this.media.size() > 0) {
			// make sure the user really wants to do this
			Alert alert = Alerts.confirm(
					this.owner, 
					Modality.WINDOW_MODAL, 
					Translations.get("media.remove.title"), 
					null, 
					Translations.get("media.remove.content"));
			Optional<ButtonType> result = alert.showAndWait();
			
			if (result.get() == ButtonType.OK) {
				// attempt to delete the selected media
				this.context.getMediaLibrary().remove(
					this.media, 
					this.onSuccess, 
						(List<FailedOperation<Media>> failures) -> {
						// on failure we should notify the user
						// get the exceptions
						Exception[] exceptions = failures.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
						// get the failed media
						String list = String.join(", ", failures.stream().map(f -> f.getData().getName()).collect(Collectors.toList()));
						Alert fAlert = Alerts.exception(
								this.owner,
								null, 
								null, 
								MessageFormat.format(Translations.get("media.remove.error"), list), 
								exceptions);
						fAlert.show();
						
						if (this.onError != null) {
							this.onError.accept(failures);
						}
					});
			}
		}
		return null;
	}
}
