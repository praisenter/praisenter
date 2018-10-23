package org.praisenter.ui.document;

import org.praisenter.data.Persistable;
import org.praisenter.data.bible.Bible;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bible.BibleEditorPane;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Tab;

final class DocumentTab extends Tab {
	private final GlobalContext context;
	private final DocumentContext<? extends Persistable> document;
	private final DocumentEditor<?> editor;
	
	public DocumentTab(
			GlobalContext context,
			DocumentContext<? extends Persistable> document) {
		this.context = context;
		this.document = document;
		
		this.editor = this.createEditorForDocument();
		
		// check for unsaved changes on close of a tab
		this.setOnCloseRequest(e -> {
			if (document.hasUnsavedChanges()) {
				// TODO prompt to save first, then remove regardless of what the user chooses
			}
			
			context.closeDocument(document);
			e.consume();
		});
		
		// set the default focus when the tab is selected
		this.selectedProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				context.setCurrentDocument(document);
			}
		});
		
		// set the tab name based on the document and whether it's been changed
		this.textProperty().bind(Bindings.createStringBinding(() -> {
			if (document.hasUnsavedChanges()) {
				return "*" + document.getDocumentName();
			}
			return document.getDocumentName();
		}, document.documentNameProperty(), document.hasUnsavedChangesProperty()));
		
	}
	
	private DocumentEditor<?> createEditorForDocument() {
		Object document = this.document.getDocument();
		if (document != null) {
			// TODO don't really like this, but what else could we do?
			if (document.getClass() == Bible.class) {
				BibleEditorPane bep = new BibleEditorPane(this.context, (DocumentContext<Bible>)this.document);
				this.setContent(bep);
				return bep;
			}
		}
		
		return new UnknownDocumentEditor((DocumentContext<Persistable>) this.document);
	}
	
	public DocumentEditor<?> getDocumentEditor() {
		return this.editor;
	}
	
	public DocumentContext<?> getDocumentContext() {
		return this.document;
	}
}
