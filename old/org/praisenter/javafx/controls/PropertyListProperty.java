package org.praisenter.javafx.controls;

import java.util.function.Function;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

class PropertyListProperty<T> {
	private final String name;
	private final Function<T, StringProperty> value;
	
	public PropertyListProperty(String name, Function<T, StringProperty> value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Function<T, StringProperty> getValue() {
		return this.value;
	}
}
