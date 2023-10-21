package org.praisenter.ui.bind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

public final class AdditionalItemsList<E> extends TransformationList<E, E> {
	private final List<E> first;
	private final List<E> last;
	
	public AdditionalItemsList(ObservableList<? extends E> source, E first, E last) {
		this(source, 
			first != null ? Arrays.asList(first) : null, 
			last != null ? Arrays.asList(last) : null);
	}
	
	public AdditionalItemsList(ObservableList<? extends E> source, List<E> first, List<E> last) {
		super(source);
		this.first = first;
		this.last = last;
	}

	@Override
	public int getSourceIndex(int index) {
		return index - (this.first != null ? this.first.size() : 0);
	}
	
	@Override
	public int getViewIndex(int index) {
		return index + (this.first != null ? this.first.size() : 0);
	}

	@Override
	public E get(int index) {
		if (this.first != null && index < this.first.size()) {
			return this.first.get(index);
		}
		int si = index - (this.first != null ? this.first.size() : 0);
		if (this.last != null && si >= getSource().size()) {
			return this.last.get(si - getSource().size());
		}
		return getSource().get(si);
	}

	@Override
	public int size() {
		return getSource().size() + (this.first != null ? this.first.size() : 0) + (this.last != null ? this.last.size() : 0);
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
				return c.getFrom() + (first != null ? first.size() : 0);
			}

			@Override
			public int getTo() {
				return c.getTo() + (first != null ? first.size() : 0);
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
