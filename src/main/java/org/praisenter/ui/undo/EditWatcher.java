package org.praisenter.ui.undo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

final class EditWatcher implements Watcher {
	private final Consumer<Edit> onEdit;
	private final Map<Class<?>, WatchDefinition<?>> watchDefinitions;
	private final List<Registration> registrations;
	
	public EditWatcher(Consumer<Edit> onEdit) {
		this.onEdit = onEdit;
		this.watchDefinitions = new HashMap<>();
		this.registrations = new ArrayList<>();
	}
	
	public <T> void addWatchDefinition(WatchDefinition<T> definition) {
		this.watchDefinitions.put(definition.getClass(), definition);
	}
	
	public <E, T> void register(String name, E target, Property<T> property) {
		ChangeListener<T> listener = (obs, ov, nv) -> {
			Edit edit = new PropertyEdit<T>(name, property, ov, nv);
			this.onEdit.accept(edit);
			
			if (ov != null) {
				// unregister the old value
				this.unregister(ov);
			}
			if (nv != null) {
				// register the new value
				this.register(nv);
			}
		};
		this.registrations.add(new PropertyRegistration<>(target, property, listener));
		property.addListener(listener);
	}
	
	public <E, T> void register(String name, E target, ObservableList<T> list) {
		ListChangeListener<T> listener = (change) -> {
			Edit edit = new ListEdit<T>(name, list, change);
			this.onEdit.accept(edit);
			
			while (change.next()) {
				// handle permutation first
				if (change.wasPermutated()) {
					// no items need to be registered/unregistered in this case
				} else if (change.wasUpdated()) {
					// not needed as far as i can tell
				} else {
					// handling add/removed handles replaced
					// handle delete
					for (T item : change.getRemoved()) {
						// deregister the properties
						this.unregister(item);
					}
					// handle add
					for (T item : change.getAddedSubList()) {
						// register the sub properties
						this.register(item);
					}
				}
			}
		};
		this.registrations.add(new ListRegistration<T>(target, list, listener));
		list.addListener(listener);
	}
	
	public <E, T> void register(String name, E target, ObservableSet<T> set) {
		SetChangeListener<T> listener = (change) -> {
			Edit edit = new SetEdit<T>(name, set, change);
			this.onEdit.accept(edit);
			
			if (change.wasAdded()) {
				this.register(change.getElementAdded());
			}
			if (change.wasRemoved()) {
				this.unregister(change.getElementRemoved());
			}
		};
		this.registrations.add(new SetRegistration<T>(target, set, listener));
		set.addListener(listener);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void register(T target) {
		Class<?> clazz = target.getClass();
		if (clazz.isEnum() || clazz.isPrimitive()) {
			return;
		}
		WatchDefinition<T> def = (WatchDefinition<T>)this.watchDefinitions.get(clazz);
		if (def != null) {
			def.getRegistrar().accept(target, this);
		}
	}
	
	public <T> void unregister(T target) {
		// get the registrations for this object
		List<Registration> registrations = this.registrations
				.stream()
				.filter(r -> r.getTarget().equals(target))
				.collect(Collectors.toList());
		
		// remove the registrations
		this.registrations.removeAll(registrations);
		
		// iterate the registrations and unbind them
		for (Registration registration : registrations) {
			registration.unbind();
			
			// unregister dependents
			for (Object other : registration.getDependents()) {
				this.unregister(other);
			}
		}
	}
}
