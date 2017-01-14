package org.praisenter.javafx.actions;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.praisenter.FailedOperation;
import org.praisenter.bible.Bible;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Window;

final class BiblePromptDeleteAction implements AsyncAction {
	private final PraisenterContext context;
	private final Window owner;
	private final List<Bible> bibles;
	private final Runnable onSuccess;
	private final Consumer<List<FailedOperation<Bible>>> onError;
	
	public BiblePromptDeleteAction(
			PraisenterContext context, 
			Window owner, 
			List<Bible> bibles,
			Runnable onSuccess,
			Consumer<List<FailedOperation<Bible>>> onError) {
		this.context = context;
		this.owner = owner;
		this.bibles = bibles;
		this.onSuccess = onSuccess;
		this.onError = onError;
	}

	@Override
	public Void call() {
		if (this.bibles != null && !this.bibles.isEmpty()) {
			// attempt to delete the selected bible
			Alert alert = Alerts.confirm(
					this.owner, 
					Modality.WINDOW_MODAL, 
					Translations.get("bible.delete.title"), 
					null, 
					Translations.get("bible.delete.content"));
			
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				// attempt to remove
				this.context.getBibleLibrary().remove(
					this.bibles, 
					this.onSuccess, 
					(List<FailedOperation<Bible>> failures) -> {
						// get the exceptions
						Exception[] exceptions = failures.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
						// get the failed bible
						String list = String.join(", ", failures.stream().map(f -> f.getData().getName()).collect(Collectors.toList()));
						Alert fAlert = Alerts.exception(
								this.owner,
								null, 
								null, 
								MessageFormat.format(Translations.get("bible.delete.error"), list), 
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
