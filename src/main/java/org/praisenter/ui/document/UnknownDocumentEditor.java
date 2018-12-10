package org.praisenter.ui.document;

import java.util.concurrent.CompletableFuture;

import org.praisenter.data.Persistable;
import org.praisenter.ui.Action;
import org.praisenter.ui.translations.Translations;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

final class UnknownDocumentEditor extends VBox implements DocumentEditor<Persistable> {
	private final DocumentContext<Persistable> documentContext;
	
	public UnknownDocumentEditor(DocumentContext<Persistable> documentContext) {
		this.documentContext = documentContext;
		
		Label lblMessage = new Label(Translations.get("error.editor.unavailable"));
		
		this.getChildren().addAll(lblMessage);
	}
	
	@Override
	public DocumentContext<Persistable> getDocumentContext() {
		return documentContext;
	}
	
	@Override
	public boolean isActionEnabled(Action action) {
		return false;
	}
	
	@Override
	public boolean isActionVisible(Action action) {
		return false;
	}
	
	@Override
	public CompletableFuture<Void> executeAction(Action action) {
		return CompletableFuture.completedFuture(null);
	}
	
	@Override
	public void setDefaultFocus() {
		// no-op
	}
}
