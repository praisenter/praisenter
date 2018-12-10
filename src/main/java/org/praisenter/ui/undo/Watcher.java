package org.praisenter.ui.undo;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public interface Watcher {
	public <E, T> void register(String name, E target, Property<T> property);
	public <E, T> void register(String name, E target, ObservableList<T> list);
	public <E, T> void register(String name, E target, ObservableSet<T> set);
}
