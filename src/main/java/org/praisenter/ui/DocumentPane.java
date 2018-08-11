package org.praisenter.ui;

import java.util.concurrent.CompletableFuture;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface DocumentPane extends ApplicationPane {
	public String getDocumentName();
	public ReadOnlyStringProperty documentNameProperty();
	
	public boolean hasUnsavedChanges();
	public ReadOnlyBooleanProperty unsavedChangesProperty();
	
	public CompletableFuture<Void> saveDocument();
	
	public void undo();
	public void redo();
}
