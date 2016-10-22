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
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.util.Callback;

// FEATURE Add the ability to do mouse click+drag area selection

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

    /** The :hover CSS pseudo class for styling */
    private static final PseudoClass HOVER = PseudoClass.getPseudoClass("hover");
    
    // state
    
    /** The cell factory */
    private final Callback<T, FlowListCell<T>> cellFactory;
    
    /** The selection model */
	private final FlowListSelectionModel<T> selection;
	
	// nodes
	
	/** The item nodes */
	private final ListProperty<FlowListCell<T>> nodes;
	
	// data
	
	/** The items themselves */
	private final ListProperty<T> items;
	
	/**
	 * Full constructor.
	 * @param cellFactory the cellfactory
	 */
	public FlowListView(Callback<T, FlowListCell<T>> cellFactory) {
		this.cellFactory = cellFactory;
		this.selection = new FlowListSelectionModel<T>(this);
		this.nodes = new SimpleListProperty<FlowListCell<T>>(FXCollections.observableArrayList());
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
		            	 List<FlowListCell<T>> range = new ArrayList<FlowListCell<T>>(nodes.subList(from, to));
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
	                     }
                         // clear from selections
	                     for (T additem : changes.getAddedSubList()) {
	                         nodes.add(createCell(additem));
	                     }
	                 }
		         }
			}
 		});
	}
	
	// methods
	
	/**
	 * Creates a cell using the given cell factory and wires up events.
	 * @param item the item
	 * @return {@link FlowListCell}
	 */
	private FlowListCell<T> createCell(T item) {
		FlowListCell<T> cell = this.cellFactory.call(item);
		
		// wire up events
    	cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
    		@Override
    		public void handle(MouseEvent event) {
    			selection.handle(cell, event);
    		}
    	});
    	cell.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!selection.isSelected(cell)) {
					cell.pseudoClassStateChanged(HOVER, true);
				}
			}
    	});
    	cell.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!selection.isSelected(cell)) {
					cell.pseudoClassStateChanged(HOVER, false);
				}
			}
    	});
    	
    	return cell;
	}
	
	// properties
	
	
	/**
	 * Returns the items.
	 * @return ObservableList&lt;T&gt;
	 */
	public ObservableList<T> getItems() {
		return this.items.get();
	}
	
	/**
	 * Sets the items.
	 * @param items the items
	 */
	public void setItems(ObservableList<T> items) {
		this.items.set(items);
	}
	
	/**
	 * Returns the items property.
	 * @return ListProperty&lt;T&gt;
	 */
	public ListProperty<T> itemsProperty() {
		return this.items;
	}
	
	/**
	 * Returns the selection model.
	 * @return {@link FlowListSelectionModel}
	 */
	public FlowListSelectionModel<T> getSelectionModel() {
		return this.selection;
	}
}
