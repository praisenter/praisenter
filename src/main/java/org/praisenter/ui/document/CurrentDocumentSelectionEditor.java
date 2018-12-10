package org.praisenter.ui.document;

import java.util.HashMap;
import java.util.Map;

import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.MappedList;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
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
final class CurrentDocumentSelectionEditor extends VBox {
	private final GlobalContext context;
	
	// context panes
	
	private final ObservableList<DocumentSelectionEditor<?>> selectionEditors;
	private final Map<Class<?>, DocumentSelectionEditor<?>> selectionEditorMapping;
	private final MappedList<Node, DocumentSelectionEditor<?>> mapping;
	
	public CurrentDocumentSelectionEditor(GlobalContext context) {
		this.context = context;
		
		this.selectionEditors = FXCollections.observableArrayList();
		this.selectionEditorMapping = new HashMap<>();
		
//		Binder<Bible> bible = new Binder<>(Bible.class, context.currentDocumentProperty());
//		this.biblePropertiesPane.documentContextProperty().bind(bible.target);

		context.currentDocumentProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				// see if its a new type we haven't created a selection editor for
				Class<?> dc = nv.getDocumentClass();
				DocumentSelectionEditor<?> dse = selectionEditorMapping.get(dc);
				if (dse == null) {
					DocumentSelectionEditor<?> dse2 = EditorRegistrations.createSelectionEditor(context, context.currentDocumentProperty());
					
					Node node = (Node)dse2;
					node.visibleProperty().bind(dse2.documentContextProperty().isNotNull());
					
					selectionEditors.add(dse2);
					selectionEditorMapping.put(dc, dse2);
				}
			}
		});
		
		this.mapping = new MappedList<Node, DocumentSelectionEditor<?>>(this.selectionEditors, (dse) -> {
			return (Node)dse;
		});
		
		Bindings.bindContent(this.getChildren(), this.mapping);
	}
	
//	private <T> void bind(ObjectProperty<T> left, ObjectProperty<DocumentContext<? extends Persistable>> right) {
//		right.addListener((obs, ov, nv) -> {
//			if (nv != null) {
//				Class<?> docClass = nv.getDocumentClass();
//				
//			} else {
//				left.set(null);
//			}
//		});
//	}
	
//	private class Binder<T extends Persistable> {
//		private final Class<T> clazz;
//		private final ObjectProperty<DocumentContext<?>> source;
//		private final ObjectProperty<Object> document;
//		private final ObjectProperty<DocumentContext<T>> target;
//		
//		public Binder(Class<T> clazz, ObjectProperty<DocumentContext<?>> source) {
//			this.clazz = clazz;
//			this.source = source;
//			this.document = new SimpleObjectProperty<>();
//			this.target = new SimpleObjectProperty<>();
//			
//			this.source.addListener((obs, ov, nv) -> {
//				this.document.unbind();
//				this.document.set(null);
//				if (nv != null) this.document.bind(nv.documentProperty());
//			});
//			
//			this.document.addListener((obs, ov, nv) -> {
//				if (nv != null && nv.getClass() == clazz) {
//					this.target.set((DocumentContext<T>)this.source.get());
//				} else {
//					this.target.set(null);
//				}
//			});
//		}
//	}
}
