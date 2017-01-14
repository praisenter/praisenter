package org.praisenter.javafx.actions;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.praisenter.FailedOperation;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.media.Media;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.stage.Window;

final class MediaImportAction implements AsyncAction {
	private final PraisenterContext context;
	private final Window owner;
	private final List<Path> paths;
	private final Consumer<List<Media>> onSuccess;
	private final Consumer<List<FailedOperation<Path>>> onError;
	
	
	public MediaImportAction(
			PraisenterContext context, 
			Window owner, 
			List<Path> paths,
			Consumer<List<Media>> onSuccess,
			Consumer<List<FailedOperation<Path>>> onError) {
		this.context = context;
		this.owner = owner;
		this.paths = paths;
		this.onSuccess = onSuccess;
		this.onError = onError;
	}

	@Override
	public Void call() {
		if (this.paths != null && !this.paths.isEmpty()) {
			// attempt to import them
			this.context.getMediaLibrary().add(
				this.paths, 
				this.onSuccess, 
				(List<FailedOperation<Path>> failures) -> {
					// get the exceptions
					Exception[] exceptions = failures.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
					// get the failed media
					String list = String.join(", ", failures.stream().map(f -> f.getData().getFileName().toString()).collect(Collectors.toList()));
					Alert alert = Alerts.exception(
							this.owner,
							null, 
							null, 
							MessageFormat.format(Translations.get("media.import.error"), list), 
							exceptions);
					alert.show();
				});
		}
		return null;
	}
}
