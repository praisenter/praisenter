package org.praisenter.ui;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface DocumentPane extends ApplicationPane {
	public String getDocumentName();
	public ReadOnlyStringProperty documentNameProperty();
	
	public boolean hasUnsavedChanges();
	public ReadOnlyBooleanProperty unsavedChangesProperty();
}
