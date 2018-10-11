package org.praisenter.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface DocumentPane<T> extends ActionPane {
	public DocumentContext<T> getDocumentContext();
	public void setDocumentContext(DocumentContext<T> documentContext);
	public ObjectProperty<DocumentContext<T>> documentContextProperty();

	public String getDocumentName();
	public ReadOnlyStringProperty documentNameProperty();
	
	public boolean hasUnsavedChanges();
	public ReadOnlyBooleanProperty unsavedChangesProperty();
}
