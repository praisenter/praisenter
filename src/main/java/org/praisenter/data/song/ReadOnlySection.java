package org.praisenter.data.song;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadOnlySection extends Copyable {
	public String getName();
	public String getText();
	
	public ReadOnlyStringProperty nameProperty();
	public ReadOnlyStringProperty textProperty();
}
