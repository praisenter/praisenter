package org.praisenter.javafx.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

final class PropertyListItem {
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty value;
	
	public PropertyListItem(String name, StringProperty value) {
		this.name.set(name);
		this.value = value;
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}
	
	public StringProperty valueProperty() {
		return this.value;
	}
}
