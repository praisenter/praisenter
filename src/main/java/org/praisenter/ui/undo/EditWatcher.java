package org.praisenter.ui.undo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Editable;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

final class EditWatcher implements Watcher {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final Consumer<Edit> onEdit;
	private final HashMap<Key, List<Registration>> map;
	
	private final class Key {
		private final int hash;
		private final Object target;
		public Key(Object target) {
			this.hash = System.identityHashCode(target);
			this.target = target;
		}
		
		@Override
		public final int hashCode() {
			return hash;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (obj == null) return false;
			if (obj == this) return true;
			if (obj instanceof Key) {
				return ((Key)obj).target == this.target;
			}
			return false;
		}
	}
	
	public EditWatcher(Consumer<Edit> onEdit) {
		this.onEdit = onEdit;
		this.map = new HashMap<Key, List<Registration>>();
	}
	
	public void register(Object target) {
		this.registerObjectProperties(target);
	}
	
	private void registerObjectProperties(Object target) {
		if (target == null) return;
		
		Class<?> clazz = target.getClass();
		if (clazz.isEnum() || clazz.isPrimitive()) {
			return;
		}
		
		List<Method> propertyMethods = this.getPropertyMethodsToWatch(clazz);
		for (Method method : propertyMethods) {
			try {
				String name = method.getAnnotation(Editable.class).value();
				Object returnValue = method.invoke(target);
				if (returnValue != null) {
					if (returnValue instanceof Property) {
						Property<?> prop = ((Property<?>)returnValue);
						this.register(name, target, prop);
					} else if (returnValue instanceof ObservableList) {
						ObservableList<?> list = ((ObservableList<?>)returnValue);
						this.register(name, target, list);
					} else if (returnValue instanceof ObservableSet) {
						ObservableSet<?> set = ((ObservableSet<?>)returnValue);
						this.register(name, target, set);
					} else {
						LOGGER.warn("Unknown return value type. Skipping.");
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.warn(e.getMessage(), e);
			}
		}
	}
	
	private List<Method> getPropertyMethodsToWatch(Class<?> objectClass) {
		List<Method> propertyMethods = new ArrayList<>();
		
		// we have to inspect all super classes for all the methods
		for (Class<?> c = objectClass; c != null; c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
            	// has to have the annotation
            	if (method.isAnnotationPresent(Editable.class) &&
            		Observable.class.isAssignableFrom(method.getReturnType())) {
            		propertyMethods.add(method);
            	}
            }
        }
		
		return propertyMethods;
	}
	
	/**
	 * Registers a property on the given target to be watched.
	 * <p>
	 * The value of the property will also be watched if it's an object.
	 */
	public <E, T> void register(String name, E target, Property<T> property) {
		ChangeListener<T> listener = (obs, ov, nv) -> {
			Edit edit = new PropertyEdit<T>(name, property, ov, nv);
			this.onEdit.accept(edit);
			
			if (ov != null) {
				// unregister the old value
				this.unregisterObjectRegistrations(ov);
			}
			if (nv != null) {
				// register the new value
				this.registerObjectProperties(nv);
			}
		};
		
		Registration registration = new PropertyRegistration<>(target, property, listener);
		Key key = new Key(target);
		
		List<Registration> registrations = this.map.get(key);
		if (registrations == null) {
			registrations = new ArrayList<>();
			this.map.put(key, registrations);
		}
		
		registrations.add(registration);
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
						this.unregisterObjectRegistrations(item);
					}
					// handle add
					for (T item : change.getAddedSubList()) {
						// register the sub properties
						this.registerObjectProperties(item);
					}
				}
			}
		};
		
		Registration registration = new ListRegistration<T>(target, list, listener);
		Key key = new Key(target);
		
		List<Registration> registrations = this.map.get(key);
		if (registrations == null) {
			registrations = new ArrayList<>();
			this.map.put(key, registrations);
		}
		
		registrations.add(registration);
		list.addListener(listener);
		
		// listen to all dependents
		for (T item : list) {
			this.registerObjectProperties(item);
		}
	}
	
	public <E, T> void register(String name, E target, ObservableSet<T> set) {
		SetChangeListener<T> listener = (change) -> {
			Edit edit = new SetEdit<T>(name, set, change);
			this.onEdit.accept(edit);
			
			if (change.wasAdded()) {
				this.registerObjectProperties(change.getElementAdded());
			}
			if (change.wasRemoved()) {
				this.unregisterObjectRegistrations(change.getElementRemoved());
			}
		};
		
		Registration registration = new SetRegistration<T>(target, set, listener);
		Key key = new Key(target);
		
		List<Registration> registrations = this.map.get(key);
		if (registrations == null) {
			registrations = new ArrayList<>();
			this.map.put(key, registrations);
		}
		
		registrations.add(registration);
		
		set.addListener(listener);
		
		// listen to all dependents
		for (T item : set) {
			this.registerObjectProperties(item);
		}
	}
	
	private <T> void unregisterObjectRegistrations(T target) {
		if (target == null) return;
		
		Key key = new Key(target);
		List<Registration> registrations = this.map.remove(key);
		
		if (registrations != null) {
			for (Registration registration : registrations) {
				registration.unbind();
				
				for (Object dependent : registration.getDependents()) {
					this.unregisterObjectRegistrations(dependent);
				}
			}
		}
	}
	
	public void unregister() {
		// unbind everything
		for (List<Registration> registrations : this.map.values()) {
			for (Registration registration : registrations) {
				registration.unbind();
			}
		}
		// then clear it
		this.map.clear();
	}
	
	public int count() {
		return this.map.values().stream().map(l -> l.size()).reduce((a, b) -> a + b).orElse(0);
	}
}
