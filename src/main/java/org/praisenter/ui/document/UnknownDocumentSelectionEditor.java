package org.praisenter.ui.document;

import org.praisenter.data.Persistable;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.translations.Translations;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

final class UnknownDocumentSelectionEditor extends VBox implements DocumentSelectionEditor<Persistable> {
	private final ObjectProperty<DocumentContext<Persistable>> documentContext;
	
	public UnknownDocumentSelectionEditor(GlobalContext context) {
		this.documentContext = new SimpleObjectProperty<DocumentContext<Persistable>>();
		
		Label lblMessage = new Label(Translations.get("error.editor.unavailable"));
		
		this.getChildren().addAll(lblMessage);
	}
	
	@Override
	public DocumentContext<Persistable> getDocumentContext() {
		return this.documentContext.get();
	}

	@Override
	public void setDocumentContext(DocumentContext<Persistable> ctx) {
		this.documentContext.set(ctx);
	}

	@Override
	public ObjectProperty<DocumentContext<Persistable>> documentContextProperty() {
		return this.documentContext;
	}
}