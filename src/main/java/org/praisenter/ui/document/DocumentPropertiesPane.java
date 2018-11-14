package org.praisenter.ui.document;

import org.praisenter.data.Persistable;
import org.praisenter.data.bible.Bible;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bible.SelectedBibleItemEditor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

// DOCUMENTS
// - Current document
// - Current selection within that document
//===================
// + slide show (document)
// + bible (document)
// + slide (document)
// + song (document)

// LISTS
// - selection of items may have contextual info
// ===================
// + slides list
// + media list
// + song list
// + bible list

// OTHER
// - no selection or document context needed?
//===================
// + About UI (modal)
// + Configuration (maybe modal?)


// TODO move to the document editor pane
public final class DocumentPropertiesPane extends VBox {
	private final GlobalContext context;
	
	// context panes
	
	private final SelectedBibleItemEditor biblePropertiesPane;
	
	public DocumentPropertiesPane(GlobalContext context) {
		this.context = context;
		
		this.biblePropertiesPane = new SelectedBibleItemEditor();
		
		Binder<Bible> bible = new Binder<>(Bible.class, context.currentDocumentProperty());
		this.biblePropertiesPane.documentContextProperty().bind(bible.target);//this.createObjectBinding(Bible.class, context.getApplicationState().currentDocumentProperty()));
		
		this.getChildren().addAll(
				new Label("Properties"),
				this.biblePropertiesPane);
	}
	
	private class Binder<T extends Persistable> {
		private final Class<T> clazz;
		private final ObjectProperty<DocumentContext<?>> source;
		private final ObjectProperty<Object> document;
		private final ObjectProperty<DocumentContext<T>> target;
		
		public Binder(Class<T> clazz, ObjectProperty<DocumentContext<?>> source) {
			this.clazz = clazz;
			this.source = source;
			this.document = new SimpleObjectProperty<>();
			this.target = new SimpleObjectProperty<>();
			
			this.source.addListener((obs, ov, nv) -> {
				this.document.unbind();
				this.document.set(null);
				if (nv != null) this.document.bind(nv.documentProperty());
			});
			
			this.document.addListener((obs, ov, nv) -> {
				if (nv != null && nv.getClass() == clazz) {
					this.target.set((DocumentContext<T>)this.source.get());
				} else {
					this.target.set(null);
				}
			});
		}
	}
}
