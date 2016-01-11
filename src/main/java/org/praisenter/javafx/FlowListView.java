package org.praisenter.javafx;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.layout.TilePane;
import javafx.util.Callback;

public final class FlowListView<T> extends TilePane {
	final Callback<T, FlowListItemView<T>> cellFactory;
	
	final ListProperty<T> selections;
	final ObjectProperty<T> singleSelection;
	
	final ListProperty<FlowListItemView<T>> nodes;
	final ListProperty<T> items;
	
	public FlowListView(Callback<T, FlowListItemView<T>> cellFactory) {
		this.cellFactory = cellFactory;
		this.selections = new SimpleListProperty<T>(FXCollections.observableArrayList());
		this.singleSelection = new SimpleObjectProperty<T>(null);
		this.nodes = new SimpleListProperty<FlowListItemView<T>>(FXCollections.observableArrayList());
		this.items = new SimpleListProperty<T>(FXCollections.observableArrayList());
		
		this.setPadding(new Insets(5, 5, 5, 5));
		this.setVgap(5);
        this.setHgap(5);
        
        // bind the children of this view to the tagNode list
 		Bindings.bindContent(this.getChildren(), this.nodes);
        
 		this.items.addListener(new ListChangeListener<T>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends T> changes) {
				// iterate the changes
				while (changes.next()) {
		             if (changes.wasPermutated()) {
                    	 // reorder
		            	 int from = changes.getFrom();
		            	 int to = changes.getTo();
		            	 // re-order a sub list so we don't have duplicate nodes in the scene graph
		            	 List<FlowListItemView<T>> range = new ArrayList<FlowListItemView<T>>(nodes.subList(from, to));
	                     for (int i = from; i < to; ++i) {
	                    	 int j = changes.getPermutation(i);
	                    	 range.set(j - from, nodes.get(i));
	                     }
	                     // now replace this in the real list
	                     nodes.subList(from, to).clear();
	                     nodes.addAll(from, range);
	                 } else if (changes.wasUpdated()) {
	                	 // not sure what to do here
	                 } else {
	                     for (T remitem : changes.getRemoved()) {
	                         nodes.removeIf(v -> v.getData().equals(remitem));
	                         // clear it from selections
	                         T selectedItem = singleSelection.get();
	                         if (selectedItem != null && selectedItem.equals(remitem)) {
	                        	 singleSelection.set(null);
	                         }
	                         selections.remove(remitem);
	                     }
	                     for (T additem : changes.getAddedSubList()) {
	                         nodes.add(cellFactory.call(additem));
	                     }
	                 }
		         }
			}
 		});
 		
        this.addEventHandler(SelectionEvent.ALL, new EventHandler<SelectionEvent>() {
			@Override
			public void handle(SelectionEvent event) {
				int count = 0;
				
				@SuppressWarnings("unchecked")
				FlowListItemView<T> view = (FlowListItemView<T>)event.getTarget();
				
				T item = view.getData();
				
				if (event.getEventType() == SelectionEvent.SELECT ||
					event.getEventType() == SelectionEvent.DESELECT) {
					// unselect other cells
					for (FlowListItemView<T> cell : nodes) {
						if (cell != event.getTarget() && cell.isSelected()) {
							cell.setSelected(false);
						}
					}
				}
				// how many are selected?
				for (FlowListItemView<T> cell : nodes) {
					count += cell.isSelected() ? 1 : 0;
				}
				
				// set the single selection value
				if (count == 1) {
					singleSelection.set(item);
				} else {
					singleSelection.set(null);
				}
				
				if (event.getEventType() == SelectionEvent.SELECT) {
					List<T> items = new ArrayList<T>();
					items.add(item);
					selections.setAll(items);
				} else if (event.getEventType() == SelectionEvent.DESELECT) {
					selections.clear();
				} else if (event.getEventType() == SelectionEvent.SELECT_MULTIPLE) {
					selections.add(item);
				} else if (event.getEventType() == SelectionEvent.DESELECT_MULTIPLE) {
					selections.remove(item);
				}
				
				requestFocus();
				
				// stop propagation
				event.consume();
			}
        });
	}
	
	public ListProperty<T> itemsProperty() {
		return this.items;
	}
	
	public ObjectProperty<T> singleSelectionProperty() {
		return singleSelection;
	}
	
	public ListProperty<T> selectionProperty() {
		return selections;
	}
}
