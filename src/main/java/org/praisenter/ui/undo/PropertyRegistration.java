package org.praisenter.ui.undo;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;

final class PropertyRegistration<T> implements Registration {
	private final Object target;
	private final Property<T> property;
	private final ChangeListener<T> listener;
	
	public PropertyRegistration(Object target, Property<T> property, ChangeListener<T> listener) {
		this.target = target;
		this.property = property;
		this.listener = listener;
	}
	
	@Override
	public List<?> getDependents() {
		List<T> dependents = new ArrayList<>();
		T value = this.property.getValue();
		if (value != null) {
			dependents.add(value);
		}
		return dependents;
	}
	
	@Override
	public void unbind() {
		this.property.removeListener(this.listener);
	}
	
	public Object getTarget() {
		return this.target;
	}
}
