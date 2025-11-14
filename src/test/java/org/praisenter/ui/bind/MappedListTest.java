package org.praisenter.ui.bind;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
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
	
	@BeforeEach
	public void setupTest() {
		this.source = FXCollections.observableArrayList(ITEM1, ITEM2, ITEM3, ITEM4, ITEM5, ITEM6);
		this.mapped = new MappedList2<>(source, (o) -> {
			return o.toString();
		});
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
		
		source.remove(3);
		
		Assertions.assertEquals(source.size(), 4);
		Assertions.assertEquals(mapped.size(), 4);
		Assertions.assertEquals(mapped.items.size(), 4);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		
		source.remove(ITEM3);
		
		Assertions.assertEquals(source.size(), 3);
		Assertions.assertEquals(mapped.size(), 3);
		Assertions.assertEquals(mapped.items.size(), 3);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
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
		
		source.removeAll(ITEM2, ITEM6);
		Assertions.assertEquals(source.size(), 2);
		Assertions.assertEquals(mapped.size(), 2);
		Assertions.assertEquals(mapped.items.size(), 2);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
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
		
		source.removeLast();
		Assertions.assertEquals(source.size(), 4);
		Assertions.assertEquals(mapped.size(), 4);
		Assertions.assertEquals(mapped.items.size(), 4);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
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
	}
	
	@Test
	public void clear() {
		source.clear();
		
		Assertions.assertEquals(source.size(), 0);
		Assertions.assertEquals(mapped.size(), 0);
		Assertions.assertEquals(mapped.items.size(), 0);
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
		
		source.add(3, ITEM8);
		
		Assertions.assertEquals(source.size(), 8);
		Assertions.assertEquals(mapped.size(), 8);
		Assertions.assertEquals(mapped.items.size(), 8);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}

		source.addFirst(ITEM9);
		
		Assertions.assertEquals(source.size(), 9);
		Assertions.assertEquals(mapped.size(), 9);
		Assertions.assertEquals(mapped.items.size(), 9);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
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
		
		source.set(5, ITEM8);
		Assertions.assertEquals(source.size(), 6);
		Assertions.assertEquals(mapped.size(), 6);
		Assertions.assertEquals(mapped.items.size(), 6);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
		
		source.set(3, ITEM9);
		Assertions.assertEquals(source.size(), 6);
		Assertions.assertEquals(mapped.size(), 6);
		Assertions.assertEquals(mapped.items.size(), 6);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
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
		
		source.setAll(List.of(ITEM1, ITEM7, ITEM8));
		Assertions.assertEquals(source.size(), 3);
		Assertions.assertEquals(mapped.size(), 3);
		Assertions.assertEquals(mapped.items.size(), 3);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i).toString(), mapped.get(i));
		}
	}
	
	private class Mapped {
		String name;
		public Mapped(String s) {
			System.out.println("Creating mapped for " + s);
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
		
		Assertions.assertEquals(source.size(), 6);
		Assertions.assertEquals(mapped.size(), 6);
		Assertions.assertEquals(mapped.items.size(), 6);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i), mapped.get(i).name);
		}
		
		FXCollections.sort(source);
		
		Assertions.assertEquals(source.size(), 6);
		Assertions.assertEquals(mapped.size(), 6);
		Assertions.assertEquals(mapped.items.size(), 6);
		for (int i = 0; i < mapped.size(); i++) {
			Assertions.assertEquals(source.get(i), mapped.get(i).name);
		}
		
		
	}
}
