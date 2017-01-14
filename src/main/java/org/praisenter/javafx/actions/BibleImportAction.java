package org.praisenter.javafx.actions;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.praisenter.FailedOperation;
import org.praisenter.bible.Bible;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Window;

final class BibleImportAction implements AsyncAction {
	private final PraisenterContext context;
	private final Window owner;
	private final List<Path> paths;
	private final Consumer<List<Bible>> onSuccess;
	private final Consumer<List<FailedOperation<Path>>> onError;
	
	
	public BibleImportAction(
			PraisenterContext context, 
			Window owner, 
			List<Path> paths,
			Consumer<List<Bible>> onSuccess,
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
			this.context.getBibleLibrary().add(
				this.paths, 
				(List<Bible> bibles) -> {
					// get the warning files
					List<String> wFileNames = bibles.stream().filter(b -> b.hadImportWarning()).map(f -> f.getName()).collect(Collectors.toList());
					if (wFileNames.size() > 0) {
						// show a dialog of the files that had warnings
						String list = String.join(", ", wFileNames);
						Alert alert = Alerts.info(
								this.owner, 
								Modality.WINDOW_MODAL,
								Translations.get("bible.import.info.title"), 
								Translations.get("bible.import.info.header"), 
								list);
						alert.show();
					}
					
					if (this.onSuccess != null) {
						this.onSuccess.accept(bibles);
					}
				},
				(List<FailedOperation<Path>> failures) -> {
					// get the exceptions
					Exception[] exceptions = failures.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
					// get the failed bibles
					String list = String.join(", ", failures.stream().map(f -> f.getData().toAbsolutePath().toString()).collect(Collectors.toList()));
					Alert alert = Alerts.exception(
							this.owner,
							null, 
							null,
							MessageFormat.format(Translations.get("bible.import.error"), list), 
							exceptions);
					alert.show();
					
					if (this.onError != null) {
						this.onError.accept(failures);
					}
				});
		}
		return null;
	}
}
