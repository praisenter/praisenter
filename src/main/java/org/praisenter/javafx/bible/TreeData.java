package org.praisenter.javafx.bible;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

abstract class TreeData {
	final StringProperty label = new SimpleStringProperty();
	final StringProperty list = new SimpleStringProperty();
	
	public abstract void update();
}
