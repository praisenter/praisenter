package org.praisenter.ui.undo;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

final class ListEdit<T> implements Edit {
	private final String name;
	private final ObservableList<T> list;
	private final Changes change;
	
	public ListEdit(String name, ObservableList<T> list, ListChangeListener.Change<? extends T> change) {
		this.name = name;
		this.list = list;
		this.change = this.process(change);
	}

	@Override
	public String toString() {
		return this.name + "[C=" + change.changes.size() + "]";
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean isMergeSupported(Edit edit) {
		return false;
	}
	
	@Override
	public Edit merge(Edit edit) {
		return null;
	}
	
	@Override
	public void redo() {
		for (Change change : this.change.changes) {
			// handle permutation first
			for (Moved moved : change.moved) {
				this.list.set(moved.newIndex, moved.item);
			}
			
			// handle remove
			for (Removed removed: change.removed) {
				this.list.remove(removed.item);
			}
			
			// handle add
			for (T added : change.added) {
				this.list.add(added);
			}
		}
	}
	
	@Override
	public void undo() {
		// must do the changes in reverse order
		for (int i = this.change.changes.size() - 1; i >= 0; i--) {
			Change change = this.change.changes.get(i);
			
			// remove adds first
			this.list.removeAll(change.added);
			
			// add back any removed (in reverse order)
			// getFrom will always be the correct index
			// if the removed items were consecutive then getFrom for all of them will be equal
			// otherwise they will be different items in the change (.next())
			for (int j = change.removed.size() - 1; j >= 0; j--) {
				Removed removed = change.removed.get(j);
				this.list.add(removed.index, removed.item);
			}
			
			// handle permutation
			for (int j = change.moved.size() - 1; j >= 0; j--) {
				Moved moved = change.moved.get(j);
				this.list.set(moved.oldIndex, moved.item);
			}
		}
	}
	
	private Changes process(ListChangeListener.Change<? extends T> change) {
		Changes changes = new Changes();
		
		change.reset();
		while (change.next()) {
			Change c = new Change();
			
			// copy the added items
			if (change.wasAdded()) {
				c.added.addAll(change.getAddedSubList());
			}

			// copy the removed items
			if (change.wasRemoved()) {
				for (T removed : change.getRemoved()) {
					Removed r = new Removed();
					r.index = change.getFrom();
					r.item = removed;
					c.removed.add(r);
				}
			}
			
			// copy the permutated items
			if (change.wasPermutated()) {
				for (int oldIndex = change.getFrom(); oldIndex < change.getTo(); oldIndex++) {
					int newIndex = change.getPermutation(oldIndex);
					T item = change.getList().get(newIndex);
					Moved m = new Moved();
					m.item = item;
					m.newIndex = newIndex;
					m.oldIndex = oldIndex;
					c.moved.add(m);
				}
			}
			
			changes.changes.add(c);
		}
		return changes;
	}
	
	private class Changes {
		public List<Change> changes = new ArrayList<>();
	}
	
	private class Change {
		public List<T> added = new ArrayList<>();
		public List<Removed> removed = new ArrayList<>();
		public List<Moved> moved = new ArrayList<>();
	}
	
	private class Removed {
		public int index;
		public T item;
	}
	
	private class Moved {
		public int oldIndex;
		public int newIndex;
		public T item;
	}
}
