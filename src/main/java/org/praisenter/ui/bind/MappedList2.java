package org.praisenter.ui.bind;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

public final class MappedList2<E, F> extends TransformationList<E, F> {
	private final Function<F, E> mapper;
	protected final List<E> items;

	public MappedList2(ObservableList<? extends F> source, Function<F, E> mapper) {
		super(source);
		this.mapper = mapper;
		this.items = new ArrayList<>();
		for (F item : source) {
			this.items.add(this.mapper.apply(item));
		}
	}

	@Override
	public int getSourceIndex(int index) {
		return index;
	}
	
	@Override
	public int getViewIndex(int index) {
		return index;
	}

	@Override
	public E get(int index) {
		E e = this.items.get(index);
		return e;
	}

	@Override
	public int size() {
		return getSource().size();
	}
	
	@Override
	protected void sourceChanged(Change<? extends F> c) {
		this.beginChange();
		
		while (c.next()) {
            if (c.wasPermutated()) {
            	this.handlePermutation(c);
            } else if (c.wasUpdated()) {
            	this.handleUpdated(c);
            } else {
            	this.handleAddRemove(c);
            }
        }
		
		this.endChange();
	}
	
	private void handlePermutation(Change<? extends F> c) {
		final int from = c.getFrom();
		final int to = c.getTo();
		final int[] perm = new int[to - from];
		for (int i = from; i < to; i++) {
			perm[i] = c.getPermutation(i);
		}
		
		nextPermutation(from, to, perm);
		
		List<E> orig = new ArrayList<>(this.items);
		
		for (int oldIndex = c.getFrom(); oldIndex < c.getTo(); oldIndex++) {
			int newIndex = c.getPermutation(oldIndex);
			// swap out values
			E e = orig.get(oldIndex);
			this.items.set(newIndex, e);
		}
	}
	
	private void handleUpdated(Change<? extends F> c) {
		for (int idx = c.getFrom(); idx < c.getTo(); idx++) {
			nextUpdate(idx);
		}
	}
	
	private void handleAddRemove(Change<? extends F> c) {
		final int from = c.getFrom();
		final int to = c.getTo();
		
		// copy the removed items
		if (c.wasRemoved()) {
			List<E> removed = new ArrayList<>();
			for (int i = 0; i < c.getRemovedSize(); i++) {				
	            removed.add(this.items.get(from + i));
	        }
			nextRemove(from, removed);

			for (int i = 0; i < c.getRemovedSize(); i++) {				
	            this.items.remove(from);
	        }
		}
		
		if (c.wasAdded()) {				
            nextAdd(from, to);
			for (int i = 0; i < c.getAddedSize(); i++) {				
	            this.items.add(from + i, this.mapper.apply(c.getAddedSubList().get(i)));
	        }
		}
	}
}
