package org.praisenter.ui.document;

import org.praisenter.data.Persistable;

import javafx.beans.property.ObjectProperty;

public interface DocumentSelectionEditor<T extends Persistable> {
	public DocumentContext<T> getDocumentContext();
	public void setDocumentContext(DocumentContext<T> ctx);
	public ObjectProperty<DocumentContext<T>> documentContextProperty();
}
