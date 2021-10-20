package org.praisenter.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

public final class EmptyItemList<E> extends TransformationList<E, E> {
	private final E emptyItem;
	
	public EmptyItemList(ObservableList<? extends E> source, E emptyItem) {
		super(source);
		this.emptyItem = emptyItem;
	}

	@Override
	public int getSourceIndex(int index) {
		return index - 1;
	}
	
	@Override
	public int getViewIndex(int index) {
		return index + 1;
	}

	@Override
	public E get(int index) {
		if (index == 0) {
			return emptyItem;
		}
		return getSource().get(index - 1);
	}

	@Override
	public int size() {
		return getSource().size() + 1;
	}
	
	@Override
	protected void sourceChanged(Change<? extends E> c) {
		fireChange(new Change<E>(this) {

			@Override
			public boolean wasAdded() {
				return c.wasAdded();
			}

			@Override
			public boolean wasRemoved() {
				return c.wasRemoved();
			}

			@Override
			public boolean wasReplaced() {
				return c.wasReplaced();
			}

			@Override
			public boolean wasUpdated() {
				return c.wasUpdated();
			}

			@Override
			public boolean wasPermutated() {
				return c.wasPermutated();
			}

			@Override
			public int getPermutation(int i) {
//				if (i == 0) return 0;
				return c.getPermutation(i);
			}

			@Override
			protected int[] getPermutation() {
				// This method is only called by the superclass methods
				// wasPermutated() and getPermutation(int), which are
				// both overriden by this class. There is no other way
				// this method can be called.
				throw new AssertionError("Unreachable code");
			}

			@Override
			public List<E> getRemoved() {
				return new ArrayList<>(c.getRemoved());
			}

			@Override
			public int getFrom() {
				return c.getFrom() + 1;
			}

			@Override
			public int getTo() {
				return c.getTo() + 1;
			}

			@Override
			public boolean next() {
				return c.next();
			}

			@Override
			public void reset() {
				c.reset();
			}
		});
	}
}
