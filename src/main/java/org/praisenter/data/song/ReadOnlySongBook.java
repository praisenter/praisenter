package org.praisenter.data.song;

import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadOnlySongBook {
	public String getName();
	public String getEntry();
	
	public ReadOnlyStringProperty nameProperty();
	public ReadOnlyStringProperty entryProperty();
}
