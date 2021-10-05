package org.praisenter.ui;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

// see https://gist.github.com/TomasMikula/8883719
public final class MappedList<E, F> extends TransformationList<E, F> {
	private static final Logger LOGGER = LogManager.getLogger();

	private final Function<F, E> mapper;
	private final Map<F, E> map;
//	private final boolean lazy;

	public MappedList(ObservableList<? extends F> source, Function<F, E> mapper) {
		super(source);
		this.mapper = mapper;
		this.map = new IdentityHashMap<>();
//		this.lazy = lazy;
		
//		if (!lazy) {
//			// convert any existing items in the source to 
//			for (F item : source) {
//				this.map.put(item, this.mapper.apply(item));
//			}
//		}
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
		// get the source value
		F f = getSource().get(index);
		
		E e = null;
//		if (this.lazy) {
			// get the mapped value
			e = this.map.computeIfAbsent(f, this.mapper);			
//		} else {
//			e = this.map.get(f);
//		}
//
//		// check for null
//		if (e == null) {
//			
//			// this should never happen - but let's log something just in case
//			LOGGER.fatal("MappedList map didn't contain a value for " + f + " at index: " + index);
//		}
		
		return e;
	}

	@Override
	public int size() {
		return getSource().size();
	}
	
	@Override
	protected void sourceChanged(Change<? extends F> c) {
//		if (!this.lazy) {
//			// map the items if something was added
//	        while (c.next()) {
//	        	if (c.wasAdded()) {
//	        		for (F value : c.getAddedSubList()) {
//	        			this.map.putIfAbsent(value, this.mapper.apply(value));
//	        		}
//	        	}
//	        }
//			
//	        // reset the changes
//	        c.reset();
//		}
		
		// fire to watchers of this list
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
				for (F f : c.getRemoved()) {
					E e = map.get(f);
					
					if (e == null) {
						// this should only happen if we're lazily mapping the source
						// to the target and the target hasn't been enumerated
						// in which case the removes shouldn't matter?
						LOGGER.warn("MappedList map didn't contain a value that was reported as removed.");
						continue;
					}
					
					res.add(e);
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
		
		// finally, reset and remove any map items 
		// for those items that were removed
		c.reset();
        while (c.next()) {
        	if (c.wasRemoved()) {
	            c.getRemoved().forEach(this.map::remove);
	        }
		}
	}
}
