package org.praisenter.ui.undo;

import javafx.beans.property.Property;

final class PropertyEdit<T> implements Edit {
	private final String name;
	private final Property<T> property;
	private final T oldValue;
	private final T newValue;
	
	public PropertyEdit(String name, Property<T> property, T oldValue, T newValue) {
		this.name = name;
		this.property = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return name + "[" + this.oldValue + " => " + this.newValue + "]";
	}
	
	@Override
	public void undo() {
		this.property.setValue(this.oldValue);
	}
	
	@Override
	public void redo() {
		this.property.setValue(this.newValue);
	}
	
	@Override
	public boolean isMergeSupported(Edit edit) {
		return false;
	}
	
	@Override
	public Edit merge(Edit edit) {
		return null;
	}
}
