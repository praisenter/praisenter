package org.praisenter.ui.undo;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

final class SetRegistration<T> implements Registration {
	private final Object target;
	private final ObservableSet<T> set;
	private final SetChangeListener<T> listener;
	
	public SetRegistration(Object target, ObservableSet<T> set, SetChangeListener<T> listener) {
		this.target = target;
		this.set = set;
		this.listener = listener;
	}
	
	public ObservableSet<T> getList() {
		return this.set;
	}
	
	@Override
	public List<?> getDependents() {
		return new ArrayList<>(this.set);
	}
	
	@Override
	public void unbind() {
		this.set.removeListener(this.listener);
	}
	
	@Override
	public Object getTarget() {
		return this.target;
	}
}
