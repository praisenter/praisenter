package org.praisenter.data.song;

import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadOnlyAuthor {
	public String getName();
	public String getType();
	
	public ReadOnlyStringProperty nameProperty();
	public ReadOnlyStringProperty typeProperty();
}
