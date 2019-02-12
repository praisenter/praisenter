package org.praisenter.ui.undo;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.Property;

class PropertyEdit<T> implements Edit {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final Instant timestamp;
	private final String name;
	private final Property<T> property;
	private final T oldValue;
	private final T newValue;
	
	public PropertyEdit(String name, Property<T> property, T oldValue, T newValue) {
		this.timestamp = Instant.now();
		this.name = name;
		this.property = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public Instant getTimestamp() {
		return this.timestamp;
	}
	
	@Override
	public String toString() {
		return name + "[" + this.oldValue + " => " + this.newValue + "]";
	}
	
	@Override
	public void undo() {
		if (this.property.isBound()) {
			LOGGER.warn("The property '" + this.name + "' is bound, so it cannot be set.");
			return;
		}
		this.property.setValue(this.oldValue);
	}
	
	@Override
	public void redo() {
		if (this.property.isBound()) {
			LOGGER.warn("The property '" + this.name + "' is bound, so it cannot be set.");
			return;
		}
		this.property.setValue(this.newValue);
	}
	
	@Override
	public boolean isMergeSupported(Edit previous) {
		if (previous == null) return false;
		if (previous == this) return true;
		if (previous instanceof PropertyEdit) {
			PropertyEdit<?> prev = (PropertyEdit<?>)previous;
			if (this.property == prev.property) {
				if (Math.abs(ChronoUnit.SECONDS.between(this.getTimestamp(), prev.getTimestamp())) < 2) {
					return true;
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Edit merge(Edit previous) {
		if (previous == null) return this;
		if (previous == this) return this;
		if (previous instanceof PropertyEdit) {
			PropertyEdit<?> prev = (PropertyEdit<?>)previous;
			if (this.property == prev.property) {
				return new PropertyEdit<T>(this.name, this.property, (T)prev.oldValue, this.newValue);
			}
		}
		return null;
	}
}
