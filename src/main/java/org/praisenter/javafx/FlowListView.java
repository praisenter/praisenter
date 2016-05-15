/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.layout.TilePane;
import javafx.util.Callback;

// FEATURE mouse click area selection
// FEATURE shift-click selection

/**
 * Represents a list view whose items are laid out vertically or horizontally
 * and wrap to a new row below when out of space.
 * <p>
 * This implementation does not use virtual items so performance can suffer with
 * many items in the list.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> the item type
 */
public final class FlowListView<T> extends TilePane {
	/** The class name for this node */
	private static final String CLASS_NAME = "flow-list-view";
	
	// selection
	
	/** The list of selected items */
	private final ListProperty<T> selections;
	
	/** The selected item; null if 0 or more than 1 item is selected */
	private final ObjectProperty<T> selection;
	
	// nodes
	
	/** The item nodes */
	private final ListProperty<FlowListItem<T>> nodes;
	
	// data
	
	/** The items themselves */
	private final ListProperty<T> items;
	
	// internal
	
	/** True if a selection is taking place */
	private boolean selecting = false;
	
	/**
	 * Full constructor.
	 * @param cellFactory the cellfactory
	 */
	public FlowListView(Callback<T, FlowListItem<T>> cellFactory) {
		this.selections = new SimpleListProperty<T>(FXCollections.observableArrayList());
		this.selection = new SimpleObjectProperty<T>(null);
		this.nodes = new SimpleListProperty<FlowListItem<T>>(FXCollections.observableArrayList());
		this.items = new SimpleListProperty<T>(FXCollections.observableArrayList());
		
		this.getStyleClass().add(CLASS_NAME);
		this.setPadding(new Insets(5, 5, 5, 5));
		this.setVgap(5);
        this.setHgap(5);
        
        // bind the children of this view to the tagNode list
 		Bindings.bindContent(this.getChildren(), this.nodes);
        
 		// add a change listener for the items property
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
		            	 List<FlowListItem<T>> range = new ArrayList<FlowListItem<T>>(nodes.subList(from, to));
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
	                	 selecting = true;
	                     for (T remitem : changes.getRemoved()) {
	                         nodes.removeIf(v -> v.getData().equals(remitem));
	                         // clear it from selections
	                         T selectedItem = selection.get();
	                         if (selectedItem != null && selectedItem.equals(remitem)) {
	                        	 selection.set(null);
	                         }
	                         selections.remove(remitem);
	                     }
	                     selecting = false;
	                     for (T additem : changes.getAddedSubList()) {
	                         nodes.add(cellFactory.call(additem));
	                     }
	                 }
		         }
			}
 		});
 		
 		// add an event handler for selection events
        this.addEventHandler(SelectionEvent.ALL, new EventHandler<SelectionEvent>() {
        	@SuppressWarnings("unchecked")
			@Override
			public void handle(SelectionEvent event) {
        		// get the data from the item node
				FlowListItem<T> view = (FlowListItem<T>)event.getTarget();
				T item = view.getData();
				
				if (selecting) return;
	        	selecting = true;
				FlowListView.this.handle(item, (EventType<SelectionEvent>)event.getEventType());
				selecting = false;
				
				requestFocus();
				
				// stop propagation
				event.consume();
			}
        });
        
        this.selection.addListener((obs, ov, nv) -> {
        	if (selecting) return;
        	selecting = true;
        	selectItem(nv);
        	handle(nv, SelectionEvent.SELECT);
        	selecting = false;
        });
        this.selections.addListener((obs, ov, nv) -> {
        	if (selecting) return;
        	selecting = true;
        	for (T nvi : nv) {
        		selectItem(nvi);
        		handle(nvi, SelectionEvent.SELECT_MULTIPLE);
        	}
        	selecting = false;
        });
	}
	
	/**
	 * Selects the given item in the list of nodes.
	 * @param item the item
	 */
	private void selectItem(T item) {
		for (FlowListItem<T> cell : nodes) {
			if (cell.getData().equals(item)) {
				cell.setSelected(true);
			}
		}
	}
	
	/**
	 * Called after an item node's selection property has changed.
	 * @param item the item
	 * @param type the selection type
	 */
	private void handle(T item, EventType<SelectionEvent> type) {
		int count = 0;
		if (type == SelectionEvent.SELECT ||
			type == SelectionEvent.DESELECT) {
			// unselect other cells
			for (FlowListItem<T> cell : nodes) {
				if (!cell.getData().equals(item) && cell.isSelected()) {
					cell.setSelected(false);
				}
			}
		}
		
		// how many are selected?
		for (FlowListItem<T> cell : nodes) {
			count += cell.isSelected() ? 1 : 0;
		}
		
		// set the single selection value
		if (count == 1) {
			selection.set(item);
		} else {
			selection.set(null);
		}
		
		// set the selections property
		if (type == SelectionEvent.SELECT) {
			List<T> items = new ArrayList<T>();
			items.add(item);
			selections.setAll(items);
		} else if (type == SelectionEvent.DESELECT) {
			selections.clear();
		} else if (type == SelectionEvent.SELECT_MULTIPLE) {
			selections.add(item);
		} else if (type == SelectionEvent.DESELECT_MULTIPLE) {
			selections.remove(item);
		}
	}
	
	/**
	 * Returns the items property.
	 * @return ListProperty&lt;T&gt;
	 */
	public ListProperty<T> itemsProperty() {
		return this.items;
	}
	
	/**
	 * Returns the single selection property.
	 * @return ObjectProperty&lt;T&gt;
	 */
	public ObjectProperty<T> selectionProperty() {
		return selection;
	}
	
	/**
	 * Returns the multi-selection property.
	 * @return ListProperty&lt;T&gt;
	 */
	public ListProperty<T> selectionsProperty() {
		return selections;
	}
}
