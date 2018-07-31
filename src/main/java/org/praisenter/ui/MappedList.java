package org.praisenter.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

// see https://gist.github.com/TomasMikula/8883719
public final class MappedList<E, F> extends TransformationList<E, F> {

	private final BiFunction<Integer, F, E> mapper;

	public MappedList(ObservableList<? extends F> source, BiFunction<Integer, F, E> mapper) {
		super(source);
		this.mapper = mapper;
	}

	@Override
	public int getSourceIndex(int index) {
		return index;
	}

	@Override
	public E get(int index) {
		return this.mapper.apply(index, getSource().get(index));
	}

	@Override
	public int size() {
		return getSource().size();
	}
	
//	@Override
//	public int getViewIndex(int index) {
//		return index;
//	}

	@Override
	protected void sourceChanged(Change<? extends F> c) {
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
				ArrayList<E> res = new ArrayList<>(c.getRemovedSize());
				int i = c.getFrom();
				for (F e : c.getRemoved()) {
					res.add(mapper.apply(i++, e));
				}
				return res;
			}

			@Override
			public int getFrom() {
				return c.getFrom();
			}

			@Override
			public int getTo() {
				return c.getTo();
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
