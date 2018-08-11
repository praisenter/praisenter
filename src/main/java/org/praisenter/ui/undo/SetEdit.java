package org.praisenter.ui.undo;

import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

final class SetEdit<T> implements Edit {
	private final String name;
	private final ObservableSet<T> set;
	private final Change change;
	
	public SetEdit(String name, ObservableSet<T> set, SetChangeListener.Change<? extends T> change) {
		this.name = name;
		this.set = set;
		this.change = this.process(change);
	}

	@Override
	public String toString() {
		return this.name + "[C=" + String.join("|", this.change.added != null ? "Added" : null, this.change.removed != null ? "Removed" : null) + "]";
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean isMergeSupported(Edit previous) {
		return false;
	}
	
	@Override
	public Edit merge(Edit previous) {
		return null;
	}
	
	@Override
	public void redo() {
		T added = this.change.added;
		T removed = this.change.removed;
		
		if (removed != null) {
			this.set.remove(removed);
		}
		if (added != null) {
			this.set.add(added);
		}
	}
	
	@Override
	public void undo() {
		T added = this.change.added;
		T removed = this.change.removed;
		
		if (added != null) {
			this.set.remove(added);
		}
		if (removed != null) {
			this.set.add(removed);
		}
	}
	
	private Change process(SetChangeListener.Change<? extends T> change) {
		Change c = new Change();
		c.added = change.getElementAdded();
		c.removed = change.getElementRemoved();
		return c;
	}
	
	private class Change {
		public T added;
		public T removed;
	}
}
