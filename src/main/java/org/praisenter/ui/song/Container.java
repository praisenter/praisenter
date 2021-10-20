package org.praisenter.ui.song;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

class Container {
	private final StringProperty name;
	private final ObjectProperty<Class<?>> type;
	private final ObservableList<?> data;
	
	public Container(String name, Class<?> clazz, ObservableList<?> data) {
		this.name = new SimpleStringProperty(name);
		this.type = new SimpleObjectProperty<Class<?>>(clazz);
		this.data = data;
	}
	
	public String getName() {
		return this.name.get();
	}
	
	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}
	
	public ObservableList<?> getData() {
		return this.data;
	}
	
	public Class<?> getType() {
		return this.type.get();
	}
	
	public void setType(Class<?> clazz) {
		this.type.set(clazz);
	}
	
	public ObjectProperty<Class<?>> typeProperty() {
		return this.type;
	}
}
