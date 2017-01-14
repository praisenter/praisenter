package org.praisenter.javafx.actions;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.praisenter.FailedOperation;
import org.praisenter.bible.Bible;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;

final class BiblePromptImportAction implements AsyncAction {
	private final PraisenterContext context;
	private final Window owner;
	private final Consumer<List<Bible>> onSuccess;
	private final Consumer<List<FailedOperation<Path>>> onError;
	
	public BiblePromptImportAction(
			PraisenterContext context, 
			Window owner, 
			Consumer<List<Bible>> onSuccess,
			Consumer<List<FailedOperation<Path>>> onError) {
		this.context = context;
		this.owner = owner;
		this.onSuccess = onSuccess;
		this.onError = onError;
	}

	@Override
	public Void call() {
		FileChooser chooser = new FileChooser();
    	chooser.setTitle(Translations.get("bible.import.title"));
    	List<File> files = chooser.showOpenMultipleDialog(this.owner);
    	if (files != null && files.size() > 0) {
			List<Path> paths = files.stream().map(f -> f.toPath()).collect(Collectors.toList());
			(new BibleImportAction(this.context, this.owner, paths, this.onSuccess, this.onError)).call();
    	}
		return null;
	}
}
