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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

// FIXME Don't allow selection of "loading" items

/**
 * A selection model specifically for the {@link FlowListView}.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 * @param <T> the item type
 */
public final class FlowListSelectionModel<T> {
	/** The :selected CSS pseudo class for styling */
    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    
	// parent
	
    /** The view for this selection model */
	private final FlowListView<T> view;
	
	// properties
	
	/** The current selection when only one item is selected */
	private final ObjectProperty<T> selection = new SimpleObjectProperty<T>();
	
	/** The list of selected items */
	private final ListProperty<T> selections = new SimpleListProperty<T>();
	
	// state
	
	/** The selected nodes */
	private final ObservableSet<FlowListCell<T>> selected = FXCollections.observableSet(new HashSet<FlowListCell<T>>());
	
	/** The last selected node */
	private FlowListCell<T> last = null;
	
	/**
	 * Minimal constructor.
	 * @param view the view
	 */
	public FlowListSelectionModel(FlowListView<T> view) {
		this.view = view;
		this.selections.set(FXCollections.observableArrayList());
		
		// remove selections when the children are removed or replaced
		this.view.getChildren().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Node> changes) {
				// iterate the changes
				while (changes.next()) {
                    for (Node remitem : changes.getRemoved()) {
                        selected.remove(remitem);
                    }
		        }
			}
 		});
		
		// make sure we update the publicly facing selections when the 
		// internal selections are changed
		this.selected.addListener(new SetChangeListener<Node>() {
			@Override
			@SuppressWarnings("unchecked")
			public void onChanged(SetChangeListener.Change<? extends Node> changes) {
				if (changes.wasAdded()) {
					FlowListCell<T> cell = (FlowListCell<T>)changes.getElementAdded();
					selections.add(cell.getData());
				}
				if (changes.wasRemoved()) {
					FlowListCell<T> cell = (FlowListCell<T>)changes.getElementRemoved();
					selections.remove(cell.getData());
				}
			}
 		});
		
		// make sure the single selection property is updated when the
		// multi-selection property is changed
		this.selections.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (selections.size() == 1) {
					selection.set(selections.get(0));
				} else {
					selection.set(null);
				}
			}
		});
	}

	// PUBLIC INTERFACE
	
	/**
	 * Returns true if the given item is selected.
	 * @param item the item
	 * @return boolean
	 */
	public boolean isSelected(T item) {
		if (item != null) {
			for (FlowListCell<T> cell : this.selected) {
				if (item.equals(cell.getData())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Selects the given item. Other selections are retained.
	 * @param item the item to select
	 */
	@SuppressWarnings("unchecked")
	public void select(T item) {
		if (item != null) {
			for (Node node : this.view.getChildren()) {
				FlowListCell<T> cell = (FlowListCell<T>)node;
				if (item.equals(cell.getData())) {
					this.select(cell);
					break;
				}
			}
		}
	}
	
	/**
	 * Clears all selections and selects the given item only.
	 * @param item the item to select
	 */
	public void selectOnly(T item) {
		this.clear();
		this.select(item);
	}
	
	/**
	 * Deselects the given item. Other selections are retained.
	 * @param item the item to deselect
	 */
	public void deselect(T item) {
		if (item != null) {
			FlowListCell<T> cell = null;
			for (FlowListCell<T> test : this.selected) {
				if (item.equals(test.getData())) {
					cell = test;
					break;
				}
			}
			if (cell != null) {
				this.deselect(cell);
			}
		}
	}
	
	/**
	 * Selects all the given items. Other selections are retained.
	 * @param items the items to select
	 */
	@SuppressWarnings("unchecked")
	public void selectAll(Collection<T> items) {
		if (items != null && !items.isEmpty()) {
			for (T item : items) {
				for (Node node : this.view.getChildren()) {
					FlowListCell<T> cell = (FlowListCell<T>)node;
					if (item.equals(cell.getData())) {
						this.select(cell);
						break;
					}
				}
			}
			this.last = null;
		}
	}
	
	/**
	 * Clears all selections.
	 */
	public void clear() {
		for (Node node : this.selected) {
			node.pseudoClassStateChanged(SELECTED, false);
		}
		this.selected.clear();
		this.last = null;
	}
	
	/**
	 * Selects the given items in the list of nodes only.
	 * @param items the items
	 */
	@SuppressWarnings("unchecked")
	public void selectOnly(List<T> items) {
		this.clear();
		if (items != null && items.size() > 0) {
			for (Node node : this.view.getChildren()) {
				FlowListCell<T> cell = (FlowListCell<T>)node;
				if (items.contains(cell.getData())) {
					this.select(cell);
				}
			}
		}
	}
	
	/**
	 * Selects all items.
	 */
	@SuppressWarnings("unchecked")
	public void selectAll() {
		for (Node node : this.view.getChildren()) {
			FlowListCell<T> cell = (FlowListCell<T>)node;
			this.select(cell);
		}
	}
	
	/**
	 * Inverts the selection.
	 */
	@SuppressWarnings("unchecked")
	public void invert() {
		for (Node node : this.view.getChildren()) {
			FlowListCell<T> cell = (FlowListCell<T>)node;
			if (this.selected.contains(cell)) {
				this.deselect(cell);
			} else {
				this.select(cell);
			}
		}
	}
	
	// INTERNAL
	
	/**
	 * Returns true if the given cell is selected.
	 * @param cell the cell to test
	 * @return boolean
	 */
	boolean isSelected(FlowListCell<T> cell) {
		return this.selected.contains(cell);
	}
	
	/**
	 * Selects the given cell.
	 * @param cell the cell to select
	 */
	@SuppressWarnings("unchecked")
	void select(Node cell) {
		this.select((FlowListCell<T>)cell);
	}
	
	/**
	 * Selects the given cell.
	 * @param cell the cell to select
	 */
	void select(FlowListCell<T> cell) {
		if (cell != null) {
			cell.pseudoClassStateChanged(SELECTED, true);
			this.selected.add(cell);
			this.last = cell;
		}
	}
	
	/**
	 * Deselects the given cell.
	 * @param cell the cell to deselect
	 */
	void deselect(FlowListCell<T> cell) {
		if (cell != null) {
			cell.pseudoClassStateChanged(SELECTED, false);
			this.selected.remove(cell);
			if (this.last == cell) {
				this.last = null;
			}
		}
	}
	
	/**
	 * Event handler for the click event.
	 * @param cell the cell that was clicked
	 * @param event the mouse event
	 */
	final void handle(FlowListCell<T> cell, MouseEvent event) {
		boolean isPrimary = event.getButton() == MouseButton.PRIMARY;
		boolean isSecondary = event.getButton() == MouseButton.SECONDARY;
		if (isPrimary || isSecondary) {
			// make sure the mouse event doesn't propagate when a cell
			// is clicked; this is so that we can deselect when the user
			// clicks off of an item
			event.consume();

			// are we being selected?
			boolean selected = this.isSelected(cell);
			boolean select = !selected;
			
			// check for double click
			if (isPrimary && event.getClickCount() == 2) {
				// make sure this cell is selected
				// NOTE: this is the best we can do since we can't wait
				// on the first click to see if there will be a second
				// so just make sure its selected when we do get a double click
				select = true;
				// build a select event to notify any node interested
				SelectionEvent selectEvent = new SelectionEvent(cell, cell, SelectionEvent.DOUBLE_CLICK);
				cell.fireEvent(selectEvent);
			}
			
			// if this node is selected and the secondary
			// mouse button was used on this cell then don't
			// do anything, this should show the context menu
			// and keep this node selected
			if (event.isPopupTrigger() && selected) {
				return;
			}
			
			// if the short cut is no longer down but the last time we selected something
			// it was, then select this regardless if it was selected already
			if (!event.isShortcutDown() && this.selected.size() > 1) {
				select = true;
			}
			
			// CTRL + click
			if (isPrimary && event.isShortcutDown()) {
				// then its a multi-(de)select
				if (select) {
					select(cell);
				} else {
					deselect(cell);
				}
			// SHIFT + click
			} else if (isPrimary && event.isShiftDown()) {
				int start = 0;
				int end = this.view.getChildren().indexOf(cell);
				// select from the currently selected cell to the this cell
				if (this.last != null) {
					// select from last to this cell
					start = this.view.getChildren().indexOf(this.last);
				}
				if (end < start) {
					int temp = end;
					end = start;
					start = temp;
				}
				for (Node node : this.view.getChildren().subList(start, end + 1)) {
					this.select(node);
				}
			// just click
			} else {
				// then its a single select
				if (select) {
					this.clear();
					this.select(cell);
				} else {
					this.deselect(cell);
				}
    			
			}
		}
	}
	
	/**
	 * Returns the multi-selection property.
	 * <p>
	 * Do not modify this property or the contents of this property. Instead
	 * use the other methods of this class to set selection programatically.
	 * @return ListProperty&lt;T&gt;
	 */
	public ReadOnlyListProperty<T> selectionsProperty() {
		return this.selections;
	}
	
	/**
	 * Returns the single-selection property.
	 * @return ReadOnlyObjectProperty&lt;T&gt;
	 */
	public ReadOnlyObjectProperty<T> selectionProperty() {
		return this.selection;
	}
}
