package org.praisenter.data.bible;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadOnlyVerse extends Copyable {
	public int getNumber();
	public String getText();
	
	public ReadOnlyIntegerProperty numberProperty();
	public ReadOnlyStringProperty textProperty();
}
