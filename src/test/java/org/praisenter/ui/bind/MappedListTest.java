package org.praisenter.ui.bind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

public class MappedListTest {
	
	private final Object ITEM1 = new Object();
	private final Object ITEM2 = new Object();
	private final Object ITEM3 = new Object();
	private final Object ITEM4 = new Object();
	private final Object ITEM5 = new Object();
	private final Object ITEM6 = new Object();
	
	private final Object ITEM7 = new Object();
	private final Object ITEM8 = new Object();
	private final Object ITEM9 = new Object();
	
	private ObservableList<Object> source;
	private MappedList2<String, Object> mapped;
	private TrackingListChangeListener<Object> listener;
	
	private class TrackedChange<T> {
		public final int from;
		public final int to;
		public final Map<Integer, Integer> permutations;
		public final List<T> added;
		public final List<T> removed;
		public final boolean wasAdded;
		public final boolean wasRemoved;
		public final boolean wasReplaced;
		public final boolean wasPermutated;
		public final boolean wasUpdated;
		
		public TrackedChange(Change<? extends T> change) {
			this.from = change.getFrom();
			this.to = change.getTo();
			this.added = new ArrayList<>(change.getAddedSubList());
			this.removed = new ArrayList<>(change.getRemoved());
			this.wasAdded = change.wasAdded();
			this.wasRemoved = change.wasRemoved();
			this.wasReplaced = change.wasReplaced();
			this.wasPermutated = change.wasPermutated();
			this.wasUpdated = change.wasUpdated();
			
			this.permutations = new HashMap<>();
			if (change.wasPermutated()) {
				for (int i = this.from; i < this.to; i++) {
					this.permutations.put(i, change.getPermutation(i));
				}
			}
		}
	}
	
	private class TrackingListChangeListener<T> implements ListChangeListener<T> {
		public final List<TrackedChange<T>> changes = new ArrayList<>(); 

	    @Override
	    public void onChanged(Change<? extends T> c) {
	        while (c.next()) {
	        	changes.add(new TrackedChange<>(c));
	        }
	    }
	    
	    public void reset() {
	    	this.changes.clear();
	    }
	}
	
	@BeforeEach
	public void setupTest() {
		this.source = FXCollections.observableArrayList(ITEM1, ITEM2, ITEM3, ITEM4, ITEM5, ITEM6);
		this.mapped = new MappedList2<>(source, (o) -> {
			return o.toString();
		});
		this.listener = new TrackingListChangeListener<>();
		this.mapped.addListener(this.listener);
	}
	
	@Test
	public void remove() {
		source.remove(0);
		
		Assertions.assertEquals(source.size(), 5);
		Assertions.assertEquals(mapped.size(), 5);
		Assertions.assertEquals(mapped.items.size(), 5);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(0, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM1.toString(), change.removed.get(0));
		this.listener.reset();
		
		source.remove(3);
		
		Assertions.assertEquals(source.size(), 4);
		Assertions.assertEquals(mapped.size(), 4);
		Assertions.assertEquals(mapped.items.size(), 4);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		change = this.listener.changes.get(0);
		Assertions.assertEquals(3, change.from);
		Assertions.assertEquals(3, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM5.toString(), change.removed.get(0));
		this.listener.reset();
		
		source.remove(ITEM3);
		
		Assertions.assertEquals(source.size(), 3);
		Assertions.assertEquals(mapped.size(), 3);
		Assertions.assertEquals(mapped.items.size(), 3);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		change = this.listener.changes.get(0);
		Assertions.assertEquals(1, change.from);
		Assertions.assertEquals(1, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM3.toString(), change.removed.get(0));
		this.listener.reset();
	}
	
	@Test
	public void removeRange() {
		source.remove(1, 4);
		
		Assertions.assertEquals(source.size(), 3);
		Assertions.assertEquals(mapped.size(), 3);
		Assertions.assertEquals(mapped.items.size(), 3);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(1, change.from);
		Assertions.assertEquals(1, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(3, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM2.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM3.toString(), change.removed.get(1));
		Assertions.assertEquals(ITEM4.toString(), change.removed.get(2));
		this.listener.reset();
	}
	
	@Test
	public void removeAll() {
		source.removeAll(List.of(ITEM1, ITEM4));
		
		Assertions.assertEquals(source.size(), 4);
		Assertions.assertEquals(mapped.size(), 4);
		Assertions.assertEquals(mapped.items.size(), 4);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(2, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(0, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM1.toString(), change.removed.get(0));
		change = this.listener.changes.get(1);
		Assertions.assertEquals(2, change.from);
		Assertions.assertEquals(2, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM4.toString(), change.removed.get(0));
		this.listener.reset();
		
		source.removeAll(ITEM2, ITEM6);
		Assertions.assertEquals(source.size(), 2);
		Assertions.assertEquals(mapped.size(), 2);
		Assertions.assertEquals(mapped.items.size(), 2);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(2, this.listener.changes.size());
		change = this.listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(0, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM2.toString(), change.removed.get(0));
		change = this.listener.changes.get(1);
		Assertions.assertEquals(2, change.from);
		Assertions.assertEquals(2, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM6.toString(), change.removed.get(0));
		this.listener.reset();
	}
	
	@Test
	public void removeFirstLast() {
		source.removeFirst();
		
		Assertions.assertEquals(source.size(), 5);
		Assertions.assertEquals(mapped.size(), 5);
		Assertions.assertEquals(mapped.items.size(), 5);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(0, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM1.toString(), change.removed.get(0));
		this.listener.reset();
		
		source.removeLast();
		Assertions.assertEquals(source.size(), 4);
		Assertions.assertEquals(mapped.size(), 4);
		Assertions.assertEquals(mapped.items.size(), 4);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		change = this.listener.changes.get(0);
		Assertions.assertEquals(4, change.from);
		Assertions.assertEquals(4, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM6.toString(), change.removed.get(0));
		this.listener.reset();
	}
	
	@Test
	public void removePredicate() {
		source.removeIf(o -> {
			return o == ITEM2 || o == ITEM5;
		});
		
		Assertions.assertEquals(source.size(), 4);
		Assertions.assertEquals(mapped.size(), 4);
		Assertions.assertEquals(mapped.items.size(), 4);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(2, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(1, change.from);
		Assertions.assertEquals(1, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM2.toString(), change.removed.get(0));
		change = this.listener.changes.get(1);
		Assertions.assertEquals(3, change.from);
		Assertions.assertEquals(3, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM5.toString(), change.removed.get(0));
		this.listener.reset();
	}
	
	@Test
	public void clear() {
		source.clear();
		
		Assertions.assertEquals(source.size(), 0);
		Assertions.assertEquals(mapped.size(), 0);
		Assertions.assertEquals(mapped.items.size(), 0);
		
		Assertions.assertEquals(1, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(0, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(6, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM1.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM2.toString(), change.removed.get(1));
		Assertions.assertEquals(ITEM3.toString(), change.removed.get(2));
		Assertions.assertEquals(ITEM4.toString(), change.removed.get(3));
		Assertions.assertEquals(ITEM5.toString(), change.removed.get(4));
		Assertions.assertEquals(ITEM6.toString(), change.removed.get(5));
		this.listener.reset();
	}
	
	@Test
	public void add() {
		source.add(ITEM7);
		
		Assertions.assertEquals(source.size(), 7);
		Assertions.assertEquals(mapped.size(), 7);
		Assertions.assertEquals(mapped.items.size(), 7);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(6, change.from);
		Assertions.assertEquals(7, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(false, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(1, change.added.size());
		Assertions.assertEquals(0, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM7.toString(), change.added.get(0));
		this.listener.reset();
		
		source.add(3, ITEM8);
		
		Assertions.assertEquals(source.size(), 8);
		Assertions.assertEquals(mapped.size(), 8);
		Assertions.assertEquals(mapped.items.size(), 8);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		change = this.listener.changes.get(0);
		Assertions.assertEquals(3, change.from);
		Assertions.assertEquals(4, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(false, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(1, change.added.size());
		Assertions.assertEquals(0, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM8.toString(), change.added.get(0));
		this.listener.reset();

		source.addFirst(ITEM9);
		
		Assertions.assertEquals(source.size(), 9);
		Assertions.assertEquals(mapped.size(), 9);
		Assertions.assertEquals(mapped.items.size(), 9);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		change = this.listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(1, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(false, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(1, change.added.size());
		Assertions.assertEquals(0, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM9.toString(), change.added.get(0));
		this.listener.reset();
	}
	
	@Test
	public void addAllCollection() {
		source.addAll(List.of(ITEM7, ITEM8));
		
		Assertions.assertEquals(source.size(), 8);
		Assertions.assertEquals(mapped.size(), 8);
		Assertions.assertEquals(mapped.items.size(), 8);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(6, change.from);
		Assertions.assertEquals(8, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(false, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(2, change.added.size());
		Assertions.assertEquals(0, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM7.toString(), change.added.get(0));
		Assertions.assertEquals(ITEM8.toString(), change.added.get(1));
		this.listener.reset();
	}
	
	@Test
	public void addAllItems() {
		source.addAll(ITEM7, ITEM8);
		
		Assertions.assertEquals(source.size(), 8);
		Assertions.assertEquals(mapped.size(), 8);
		Assertions.assertEquals(mapped.items.size(), 8);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(6, change.from);
		Assertions.assertEquals(8, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(false, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(2, change.added.size());
		Assertions.assertEquals(0, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM7.toString(), change.added.get(0));
		Assertions.assertEquals(ITEM8.toString(), change.added.get(1));
		this.listener.reset();
	}
	
	@Test
	public void addAllAt() {
		source.addAll(3, List.of(ITEM7, ITEM8));
		
		Assertions.assertEquals(source.size(), 8);
		Assertions.assertEquals(mapped.size(), 8);
		Assertions.assertEquals(mapped.items.size(), 8);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(3, change.from);
		Assertions.assertEquals(5, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(false, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(2, change.added.size());
		Assertions.assertEquals(0, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM7.toString(), change.added.get(0));
		Assertions.assertEquals(ITEM8.toString(), change.added.get(1));
		this.listener.reset();
	}
	
	@Test
	public void retainAll() {
		source.retainAll(List.of(ITEM3, ITEM2));
		
		Assertions.assertEquals(source.size(), 2);
		Assertions.assertEquals(mapped.size(), 2);
		Assertions.assertEquals(mapped.items.size(), 2);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(2, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(0, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM1.toString(), change.removed.get(0));
		change = this.listener.changes.get(1);
		Assertions.assertEquals(2, change.from);
		Assertions.assertEquals(2, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(3, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM4.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM5.toString(), change.removed.get(1));
		Assertions.assertEquals(ITEM6.toString(), change.removed.get(2));
		this.listener.reset();
	}
	
	@Test
	public void retainAll2() {
		source.retainAll(ITEM1, ITEM6);
		
		Assertions.assertEquals(source.size(), 2);
		Assertions.assertEquals(mapped.size(), 2);
		Assertions.assertEquals(mapped.items.size(), 2);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(1, change.from);
		Assertions.assertEquals(1, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(4, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM2.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM3.toString(), change.removed.get(1));
		Assertions.assertEquals(ITEM4.toString(), change.removed.get(2));
		Assertions.assertEquals(ITEM5.toString(), change.removed.get(3));
		this.listener.reset();
	}
	
	@Test
	public void replaceAll() {
		source.replaceAll(o -> {
			return ITEM7;
		});
		
		Assertions.assertEquals(source.size(), 6);
		Assertions.assertEquals(mapped.size(), 6);
		Assertions.assertEquals(mapped.items.size(), 6);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(6, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(1, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(true, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(1, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM1.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM7.toString(), change.added.get(0));
		change = this.listener.changes.get(1);
		Assertions.assertEquals(1, change.from);
		Assertions.assertEquals(2, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(true, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(1, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM2.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM7.toString(), change.added.get(0));
		change = this.listener.changes.get(2);
		Assertions.assertEquals(2, change.from);
		Assertions.assertEquals(3, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(true, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(1, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM3.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM7.toString(), change.added.get(0));
		change = this.listener.changes.get(3);
		Assertions.assertEquals(3, change.from);
		Assertions.assertEquals(4, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(true, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(1, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM4.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM7.toString(), change.added.get(0));
		change = this.listener.changes.get(4);
		Assertions.assertEquals(4, change.from);
		Assertions.assertEquals(5, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(true, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(1, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM5.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM7.toString(), change.added.get(0));
		change = this.listener.changes.get(5);
		Assertions.assertEquals(5, change.from);
		Assertions.assertEquals(6, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(true, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(1, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM6.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM7.toString(), change.added.get(0));
		this.listener.reset();
	}
	
	@Test
	public void set() {
		source.set(0, ITEM7);
		Assertions.assertEquals(source.size(), 6);
		Assertions.assertEquals(mapped.size(), 6);
		Assertions.assertEquals(mapped.items.size(), 6);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(1, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(true, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(1, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM1.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM7.toString(), change.added.get(0));
		this.listener.reset();
		
		source.set(5, ITEM8);
		Assertions.assertEquals(source.size(), 6);
		Assertions.assertEquals(mapped.size(), 6);
		Assertions.assertEquals(mapped.items.size(), 6);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		change = this.listener.changes.get(0);
		Assertions.assertEquals(5, change.from);
		Assertions.assertEquals(6, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(true, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(1, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM6.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM8.toString(), change.added.get(0));
		this.listener.reset();
		
		source.set(3, ITEM9);
		Assertions.assertEquals(source.size(), 6);
		Assertions.assertEquals(mapped.size(), 6);
		Assertions.assertEquals(mapped.items.size(), 6);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		change = this.listener.changes.get(0);
		Assertions.assertEquals(3, change.from);
		Assertions.assertEquals(4, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(true, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(1, change.added.size());
		Assertions.assertEquals(1, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM4.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM9.toString(), change.added.get(0));
		this.listener.reset();
	}
	
	@Test
	public void setAll() {
		source.setAll(List.of(ITEM1, ITEM5, ITEM3));
		Assertions.assertEquals(source.size(), 3);
		Assertions.assertEquals(mapped.size(), 3);
		Assertions.assertEquals(mapped.items.size(), 3);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		TrackedChange<Object> change = this.listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(3, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(true, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(3, change.added.size());
		Assertions.assertEquals(6, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM1.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM2.toString(), change.removed.get(1));
		Assertions.assertEquals(ITEM3.toString(), change.removed.get(2));
		Assertions.assertEquals(ITEM4.toString(), change.removed.get(3));
		Assertions.assertEquals(ITEM5.toString(), change.removed.get(4));
		Assertions.assertEquals(ITEM6.toString(), change.removed.get(5));
		Assertions.assertEquals(ITEM1.toString(), change.added.get(0));
		Assertions.assertEquals(ITEM5.toString(), change.added.get(1));
		Assertions.assertEquals(ITEM3.toString(), change.added.get(2));
		this.listener.reset();
		
		source.setAll(List.of(ITEM1, ITEM7, ITEM8));
		Assertions.assertEquals(source.size(), 3);
		Assertions.assertEquals(mapped.size(), 3);
		Assertions.assertEquals(mapped.items.size(), 3);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		Assertions.assertEquals(1, this.listener.changes.size());
		change = this.listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(3, change.to);
		Assertions.assertEquals(true, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(true, change.wasRemoved);
		Assertions.assertEquals(true, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(3, change.added.size());
		Assertions.assertEquals(3, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		Assertions.assertEquals(ITEM1.toString(), change.removed.get(0));
		Assertions.assertEquals(ITEM5.toString(), change.removed.get(1));
		Assertions.assertEquals(ITEM3.toString(), change.removed.get(2));
		Assertions.assertEquals(ITEM1.toString(), change.added.get(0));
		Assertions.assertEquals(ITEM7.toString(), change.added.get(1));
		Assertions.assertEquals(ITEM8.toString(), change.added.get(2));
		this.listener.reset();
	}
	
	private class Mapped {
		String name;
		public Mapped(String s) {
			name = s;
		}
		@Override
		public String toString() {
			return name;
		}
	}

	@Test
	public void sort() {
		ObservableList<String> source = FXCollections.observableArrayList("TEST", "SORT", "THIS", "LIST", "FAIL", "PLAN");
		MappedList2<Mapped, String> mapped = new MappedList2<Mapped, String>(source, (o) -> {
			return new Mapped(o);
		});
		TrackingListChangeListener<Mapped> listener = new TrackingListChangeListener<>();
		mapped.addListener(listener);
		
		Assertions.assertEquals(source.size(), 6);
		Assertions.assertEquals(mapped.size(), 6);
		Assertions.assertEquals(mapped.items.size(), 6);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i), mapped.get(i).name);
		}
		
		List<Mapped> refs = new ArrayList<>(mapped);
		
		FXCollections.sort(source);
		
		Assertions.assertEquals(source.size(), 6);
		Assertions.assertEquals(mapped.size(), 6);
		Assertions.assertEquals(mapped.items.size(), 6);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i), mapped.get(i).name);
		}
		// ensure sorting didn't create new objects
		Assertions.assertSame(refs.get(0), mapped.get(4));
		Assertions.assertSame(refs.get(1), mapped.get(3));
		Assertions.assertSame(refs.get(2), mapped.get(5));
		Assertions.assertSame(refs.get(3), mapped.get(1));
		Assertions.assertSame(refs.get(4), mapped.get(0));
		Assertions.assertSame(refs.get(5), mapped.get(2));
		
		Assertions.assertEquals(1, listener.changes.size());
		TrackedChange<Mapped> change = listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(6, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(true, change.wasPermutated);
		Assertions.assertEquals(false, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(false, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(0, change.removed.size());
		Assertions.assertEquals(6, change.permutations.size());
		Assertions.assertEquals(0, change.permutations.get(4));
		Assertions.assertEquals(1, change.permutations.get(3));
		Assertions.assertEquals(2, change.permutations.get(5));
		Assertions.assertEquals(3, change.permutations.get(1));
		Assertions.assertEquals(4, change.permutations.get(0));
		Assertions.assertEquals(5, change.permutations.get(2));

		this.listener.reset();
		
	}
	
	private class Mapped2 { 
		private StringProperty name;
		public Mapped2(String name) {
			this.name = new SimpleStringProperty(name);
		}
		public StringProperty nameProperty() {
			return this.name;
		}
	}
	
	@Test
	public void update() {
		// setup a OOB property watched observable list
		ObservableList<Mapped2> watcher = FXCollections.observableArrayList((i) -> {
			return new Observable[] {
				i.nameProperty()
			};
		});
		
		// add some items to it
		watcher.add(new Mapped2("TEST"));
		watcher.add(new Mapped2("SORT"));
		watcher.add(new Mapped2("THIS"));
		watcher.add(new Mapped2("LIST"));
		watcher.add(new Mapped2("FAIL"));
		watcher.add(new Mapped2("PLAN"));
		
		// now create a mapped list off of the OOB list
		// that doesn't generate it's own updates so that
		// we can see if updates are passed through the
		// mapped list
		MappedList2<Mapped, Mapped2> mapped = new MappedList2<Mapped, Mapped2>(watcher, (o) -> {
			return new Mapped(o.name.get());
		});
		
		TrackingListChangeListener<Mapped> listener = new TrackingListChangeListener<>();
		mapped.addListener(listener);
		
		watcher.get(0).name.set("TEST2");
		
		Assertions.assertEquals(1, listener.changes.size());
		TrackedChange<Mapped> change = listener.changes.get(0);
		Assertions.assertEquals(0, change.from);
		Assertions.assertEquals(1, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(false, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(true, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(0, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		listener.reset();
		
		watcher.get(4).name.set("SUCCESS");
		
		Assertions.assertEquals(1, listener.changes.size());
		change = listener.changes.get(0);
		Assertions.assertEquals(4, change.from);
		Assertions.assertEquals(5, change.to);
		Assertions.assertEquals(false, change.wasAdded);
		Assertions.assertEquals(false, change.wasPermutated);
		Assertions.assertEquals(false, change.wasRemoved);
		Assertions.assertEquals(false, change.wasReplaced);
		Assertions.assertEquals(true, change.wasUpdated);
		Assertions.assertEquals(0, change.added.size());
		Assertions.assertEquals(0, change.removed.size());
		Assertions.assertEquals(0, change.permutations.size());
		listener.reset();
	}
}
