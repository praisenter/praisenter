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
package org.praisenter.ui.controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.praisenter.ui.MappedList;
import org.praisenter.ui.events.FlowListViewSelectionEvent;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

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
	
	/** The mapping for the selected data from the selected cells */
	private final MappedList<T, FlowListCell<T>> mapping;
	
	/** The list of selected items */
	private final ObservableList<T> selections = FXCollections.observableArrayList();
	
	/** The readonly selections */
	private final ObservableList<T> selectionsReadOnly = FXCollections.unmodifiableObservableList(this.selections);
	
	/** True if multi-selection is enabled */
	private final BooleanProperty multiselect = new SimpleBooleanProperty(true);
	
	// internal state
	
	/** The selected nodes */
	private final ObservableList<FlowListCell<T>> selected = FXCollections.observableArrayList();
	
	/** The last selected node */
	private FlowListCell<T> last = null;
	
	/** The first selected node in a shift select */
	private FlowListCell<T> first = null;
	
	/**
	 * Minimal constructor.
	 * @param view the view
	 */
	public FlowListSelectionModel(FlowListView<T> view) {
		this.view = view;
		
		// remove selections when the children are removed or replaced
		this.view.getLayoutChildren().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Node> changes) {
				// record what was selected before
				List<T> old = selected.stream().map(n -> ((FlowListCell<T>)n).getData()).collect(Collectors.toList());
				// now reselect what we can
				if (old.size() > 0) {
					// NOTE: we need to do this later so that the change to the source can
					// be applied
					Platform.runLater(() -> {
						selectOnly(old);
					});
				}
			}
 		});
		
		// make sure we update the publicly facing selections when the 
		// internal selections are changed
		this.mapping = new MappedList<>(this.selected, (item) -> {
			return item.getData();
		});
		
		// bind the selections to the mapping
		Bindings.bindContent(this.selections, this.mapping);
		
		// make sure the single selection property is updated when the
		// multi-selection property is changed
		this.selection.bind(Bindings.createObjectBinding(() -> {
			if (this.selections.size() == 1) {
				return this.selections.get(0);
			}
			return null;
		}, this.selections));
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
		if (!this.multiselect.get() ) {
			this.selectOnly(item);
			return;
		}
		
		if (item != null) {
			List<FlowListCell<T>> cells = new ArrayList<FlowListCell<T>>();
			for (Node node : this.view.getLayoutChildren()) {
				FlowListCell<T> cell = (FlowListCell<T>)node;
				if (item.equals(cell.getData())) {
					cells.add(cell);
				}
			}
			if (cells.size() > 0) {
				this.selectCells(cells);
			}
		}
	}
	
	/**
	 * Clears all selections and selects the given item only.
	 * @param item the item to select
	 */
	@SuppressWarnings("unchecked")
	public void selectOnly(T item) {
		if (item != null) {
			List<FlowListCell<T>> cells = new ArrayList<FlowListCell<T>>();
			for (Node node : this.view.getLayoutChildren()) {
				FlowListCell<T> cell = (FlowListCell<T>)node;
				if (item.equals(cell.getData())) {
					cells.add(cell);
				}
			}
			if (cells.size() > 0) {
				this.selectCellsOnly(cells);
				return;
			}
		}
		this.clear();
	}
	
	/**
	 * Deselects the given item. Other selections are retained.
	 * @param item the item to deselect
	 */
	public void deselect(T item) {
		if (item != null) {
			List<FlowListCell<T>> cells = new ArrayList<FlowListCell<T>>();
			for (FlowListCell<T> cell : this.selected) {
				if (item.equals(cell.getData())) {
					cells.add(cell);
				}
			}
			if (cells.size() > 0) {
				this.deselectCells(cells);
			}
		}
	}
	
	/**
	 * Selects all the given items. Other selections are retained.
	 * @param items the items to select
	 */
	@SuppressWarnings("unchecked")
	public void select(Collection<T> items) {
		if (items != null && !items.isEmpty()) {
			if (!this.multiselect.get()) {
				this.selectOnly(items.stream().findFirst().get());
				return;
			}
			List<FlowListCell<T>> cells = new ArrayList<FlowListCell<T>>();
			for (T item : items) {
				for (Node node : this.view.getLayoutChildren()) {
					FlowListCell<T> cell = (FlowListCell<T>)node;
					if (item.equals(cell.getData())) {
						cells.add(cell);
					}
				}
			}
			this.selectCells(cells);
		}
	}

	/**
	 * Selects the given items in the list of nodes only.
	 * @param items the items
	 */
	@SuppressWarnings("unchecked")
	public void selectOnly(Collection<T> items) {
		if (items != null && !items.isEmpty()) {
			if (!this.multiselect.get()) {
				this.selectOnly(items.stream().findFirst().get());
				return;
			}
			List<FlowListCell<T>> cells = new ArrayList<FlowListCell<T>>();
			for (Node node : this.view.getLayoutChildren()) {
				FlowListCell<T> cell = (FlowListCell<T>)node;
				if (items.contains(cell.getData())) {
					cells.add(cell);
				}
			}
			this.selectCellsOnly(cells);
		} else {
			this.clear();
		}
	}
	
	/**
	 * Selects all items.
	 */
	@SuppressWarnings("unchecked")
	public void selectAll() {
		if (!this.multiselect.get()) {
			return;
		}
		List<FlowListCell<T>> cells = new ArrayList<FlowListCell<T>>();
		for (Node node : this.view.getLayoutChildren()) {
			FlowListCell<T> cell = (FlowListCell<T>)node;
			cells.add(cell);
		}
		this.selectCells(cells);
	}
	
	/**
	 * Inverts the selection.
	 */
	@SuppressWarnings("unchecked")
	public void invert() {
		if (!this.multiselect.get()) {
			return;
		}
		List<FlowListCell<T>> cells = new ArrayList<FlowListCell<T>>();
		for (Node node : this.view.getLayoutChildren()) {
			FlowListCell<T> cell = (FlowListCell<T>)node;
			if (!this.selected.contains(cell)) {
				cells.add(cell);
			}
		}
		this.selectCellsOnly(cells);
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
	
	// INTERNAL
	
	/**
	 * Returns true if the given cell is selected.
	 * @param cell the cell to test
	 * @return boolean
	 */
	boolean isCellSelected(FlowListCell<T> cell) {
		return this.selected.contains(cell);
	}
	
	/**
	 * Selects the given cell.
	 * @param cell the cell to select
	 */
	void selectCell(FlowListCell<T> cell) {
		if (!this.multiselect.get()) {
			this.selectCellOnly(cell);
			return;
		}
		if (cell != null) {
			cell.pseudoClassStateChanged(SELECTED, true);
			this.selected.add(cell);
			this.last = cell;
		}
	}
	
	/**
	 * Selects the given cell.
	 * @param cell the cell to select
	 */
	@SuppressWarnings("unchecked")
	void selectCellOnly(FlowListCell<T> cell) {
		// avoid clear+select by checking if it's already selected first
		if (this.selected.size() == 1 && this.selected.contains(cell)) return;
		
		for (FlowListCell<T> c : this.selected) {
			c.pseudoClassStateChanged(SELECTED, false);
		}
		if (cell != null) {
			cell.pseudoClassStateChanged(SELECTED, true);
			this.selected.setAll(cell);
			this.last = cell;
		} else {
			this.selected.clear();
			this.last = null;
		}
	}
	
	/**
	 * Selects all the given cells.
	 * @param cells the cells to select
	 */
	void selectCells(Collection<FlowListCell<T>> cells) {
		if (cells != null && !cells.isEmpty()) {
			if (!this.multiselect.get()) {
				this.selectCellOnly(cells.stream().findFirst().get());
				return;
			}
			List<FlowListCell<T>> toAdd = new ArrayList<FlowListCell<T>>();
			for (FlowListCell<T> cell : cells) {
				cell.pseudoClassStateChanged(SELECTED, true);
				if (!this.selected.contains(cell)) {
					toAdd.add(cell);
				}
			}
			this.selected.addAll(toAdd);
		}
	}
	
	/**
	 * Selects only the given cells.
	 * @param cells the cells to select
	 */
	void selectCellsOnly(Collection<FlowListCell<T>> cells) {
		if (cells != null && !cells.isEmpty()) {
			if (!this.multiselect.get()) {
				this.selectCellOnly(cells.stream().findFirst().get());
				return;
			}
			for (FlowListCell<T> cell : this.selected) {
				cell.pseudoClassStateChanged(SELECTED, false);
			}
			for (FlowListCell<T> cell : cells) {
				cell.pseudoClassStateChanged(SELECTED, true);
			}
			this.selected.setAll(cells);
		} else {
			this.clear();
		}
	}
	
	/**
	 * Deselects the given cell.
	 * @param cell the cell to deselect
	 */
	void deselectCell(FlowListCell<T> cell) {
		if (cell != null) {
			cell.pseudoClassStateChanged(SELECTED, false);
			this.selected.remove(cell);
			if (this.last == cell) {
				this.last = null;
			}
		}
	}

	/**
	 * Deselects the given cells.
	 * @param cells the cells to deselect
	 */
	void deselectCells(Collection<FlowListCell<T>> cells) {
		if (cells != null && !cells.isEmpty()) {
			for (FlowListCell<T> cell : this.selected) {
				cell.pseudoClassStateChanged(SELECTED, false);
			}
			this.selected.removeAll(cells);
		}
	}
	
	/**
	 * Event handler for the click event.
	 * @param cell the cell that was clicked
	 * @param event the mouse event
	 */
	@SuppressWarnings("unchecked")
	final void handle(FlowListCell<T> cell, MouseEvent event) {
		boolean isPrimary = event.getButton() == MouseButton.PRIMARY;
		boolean isSecondary = event.getButton() == MouseButton.SECONDARY;
		if (isPrimary || isSecondary) {
			// make sure the mouse event doesn't propagate when a cell
			// is clicked; this is so that we can deselect when the user
			// clicks off of an item
			event.consume();

			// are we being selected?
			boolean selected = this.isCellSelected(cell);
			boolean select = !selected;
			
			// check for double click
			if (isPrimary && event.getClickCount() == 2 && !event.isShiftDown() && !event.isShortcutDown()) {
				// make sure this cell is selected
				// NOTE: this is the best we can do since we can't wait
				// on the first click to see if there will be a second
				// so just make sure its selected when we do get a double click
				select = true;
				// build a select event to notify any node interested
				FlowListViewSelectionEvent selectEvent = new FlowListViewSelectionEvent(cell, cell, FlowListViewSelectionEvent.DOUBLE_CLICK);
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
			
			// record what the first item selected was so that
			// we can do a shift select properly
			if (!event.isShiftDown()) {
				// shift isn't down, so clear the first selected item
				this.first = null;
			} else if (this.first == null && this.last != null) {
				// shift is down and first is null, so set it to the last item selected
				this.first = this.last;
			}
			
			// CTRL + click
			if (this.multiselect.get() && isPrimary && event.isShortcutDown()) {
				// then its a multi-(de)select
				if (select) {
					selectCell(cell);
				} else {
					deselectCell(cell);
				}
			// SHIFT + click
			} else if (this.multiselect.get() && isPrimary && event.isShiftDown()) {
				int start = 0;
				int end = this.view.getLayoutChildren().indexOf(cell);
				// select from the currently selected cell to the this cell
				if (this.first != null) {
					// select from last to this cell
					start = this.view.getLayoutChildren().indexOf(this.first);
				}
				if (end < start) {
					int temp = end;
					end = start;
					start = temp;
				}
				List<FlowListCell<T>> cells = new ArrayList<FlowListCell<T>>();
				for (Node node : this.view.getLayoutChildren().subList(start, end + 1)) {
					cells.add((FlowListCell<T>)node);
				}
				this.selectCellsOnly(cells);
			// just click
			} else {
				// then its a single select
				if (select) {
					this.selectCellOnly(cell);
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
	public ObservableList<T> getSelectedItems() {
		return this.selectionsReadOnly;
	}
	
	/**
	 * Returns the selected item when only one item is selected.
	 * @return T
	 */
	public T getSelectedItem() {
		return this.selection.get();
	}
	
	/**
	 * Returns the single-selection property.
	 * @return ReadOnlyObjectProperty&lt;T&gt;
	 */
	public ReadOnlyObjectProperty<T> selectedItemProperty() {
		return this.selection;
	}
	
	/**
	 * Returns true if multiselection is enabled.
	 * @return boolean
	 */
	public boolean isMultiSelect() {
		return this.multiselect.get();
	}
	
	/**
	 * Returns the multiselect property.
	 * @return BooleanProperty
	 */
	public BooleanProperty multiselectProperty() {
		return this.multiselect;
	}
}
