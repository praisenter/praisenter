package org.praisenter.ui;

import org.praisenter.data.bible.Bible;
import org.praisenter.ui.bible.BiblePropertiesPane;

import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
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

public final class ContextPropertiesPane extends VBox {
	private final ReadOnlyPraisenterContext context;
	
	// context panes
	
	private final BiblePropertiesPane biblePropertiesPane;
	
	public ContextPropertiesPane(ReadOnlyPraisenterContext context) {
		this.context = context;
		
		this.biblePropertiesPane = new BiblePropertiesPane();
		
		Binder<Bible> bible = new Binder<>(Bible.class, context.getApplicationState().currentDocumentProperty());
		this.biblePropertiesPane.documentContextProperty().bind(bible.target);//this.createObjectBinding(Bible.class, context.getApplicationState().currentDocumentProperty()));
		
		this.getChildren().addAll(
				new Label("Properties"),
				this.biblePropertiesPane);
	}
	
//	private <T> ObjectBinding<DocumentContext<T>> createObjectBinding(Class<T> clazz, ObservableValue<DocumentContext<?>> source) {
//		return Bindings.createObjectBinding(() -> {
//			DocumentContext<?> ctx = source.getValue();
//			Object value = ctx.getDocument();
//			if (value != null && value.getClass() == clazz) {
//				return (DocumentContext<T>)ctx;
//			}
//			return null;
//		}, source);
//	}
	
	private class Binder<T> {
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
	
	
//	public Object getDocument();
//	public void setDocument(Object object);
//	public ObjectProperty<Object> documentProperty();
//	
//	public Object getSelectedItem();
//	public void setSelectedItem(Object item);
//	public ObjectProperty<Object> selectedItemProperty();
//	
//	public ObservableList<Object> getSelectedItems();
}
