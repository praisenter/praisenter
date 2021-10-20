package org.praisenter.ui.undo;

import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

final class ListRegistration<T> implements Registration {
	private final Object target;
	private final ObservableList<T> list;
	private final ListChangeListener<T> listener;
	
	public ListRegistration(Object target, ObservableList<T> list, ListChangeListener<T> listener) {
		this.target = target;
		this.list = list;
		this.listener = listener;
	}
	
	public ObservableList<T> getList() {
		return this.list;
	}
	
	@Override
	public List<?> getDependents() {
		return this.list;
	}
	
	@Override
	public void unbind() {
		this.list.removeListener(this.listener);
	}
	
	@Override
	public Object getTarget() {
		return this.target;
	}
}
