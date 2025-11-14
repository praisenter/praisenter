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
	public boolean isMergeSupported(Edit previous) {
		return false;
	}
	
	@Override
	public Edit merge(Edit previous) {
		return null;
	}
	
	@Override
	public void redo() {
		for (Change change : this.change.changes) {
			// handle permutation first
			if (change.permutation != null) {
				// remove all the values first
				this.list.remove(change.permutation.start, change.permutation.end);
				// then reinsert them at the start index
				this.list.addAll(change.permutation.start, change.permutation.newOrder);
			}
			
			// handle remove
			for (Removed removed: change.removed) {
				this.list.remove(removed.item);
			}
			
			// handle add
			for (Added added : change.added) {
				this.list.addAll(added.index, added.items);
			}
		}
	}
	
	@Override
	public void undo() {
		// must do the changes in reverse order
		for (int i = this.change.changes.size() - 1; i >= 0; i--) {
			Change change = this.change.changes.get(i);
			
			// remove adds first
			for (int j = 0; j < change.added.size(); j++) {
				this.list.removeAll(change.added.get(i).items);
			}
			
			// add back any removed (in reverse order)
			// getFrom will always be the correct index
			// if the removed items were consecutive then getFrom for all of them will be equal
			// otherwise they will be different items in the change (.next())
			for (int j = change.removed.size() - 1; j >= 0; j--) {
				Removed removed = change.removed.get(j);
				this.list.add(removed.index, removed.item);
			}
			
			// handle permutation
			if (change.permutation != null) {
				// remove all the values first
				this.list.remove(change.permutation.start, change.permutation.end);
				// then reinsert them at the start index
				this.list.addAll(change.permutation.start, change.permutation.oldOrder);
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
				Added a = new Added();
				a.index = change.getFrom();
				a.items = new ArrayList<T>(change.getAddedSubList());
				c.added.add(a);
				//c.added.addAll(change.getAddedSubList());
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
				Permutation p = new Permutation();
				p.newOrder = new ArrayList<>();
				p.oldOrder = new ArrayList<>();
				p.start = change.getFrom();
				p.end = change.getTo();
				for (int newIndex = change.getFrom(); newIndex < change.getTo(); newIndex++) {
					p.newOrder.add(change.getList().get(newIndex));
				}
				for (int oldIndex = change.getFrom(); oldIndex < change.getTo(); oldIndex++) {
					int newIndex = change.getPermutation(oldIndex);
					T item = change.getList().get(newIndex);
					p.oldOrder.add(item);
				}
				
				c.permutation = p;
			}
			
			changes.changes.add(c);
		}
		change.reset();
		
		return changes;
	}
	
	private class Changes {
		public List<Change> changes = new ArrayList<>();
	}
	
	private class Change {
		public List<Added> added = new ArrayList<>();
		public List<Removed> removed = new ArrayList<>();
		public Permutation permutation = null;
	}
	
	private class Added {
		public int index;
		public List<T> items;
	}
	
	private class Removed {
		public int index;
		public T item;
	}
	
	private class Permutation {
		public int start;
		public int end;
		public List<T> newOrder;
		public List<T> oldOrder;
	}
}
