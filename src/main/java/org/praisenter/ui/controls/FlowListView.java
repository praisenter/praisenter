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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.praisenter.ui.MappedList;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

// FEATURE (L-M) Allow reorder of multiple items instead of just one at a time

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
public class FlowListView<T> extends ScrollPane {
    /** The :hover CSS pseudo class for styling */
    private static final PseudoClass HOVER = PseudoClass.getPseudoClass("hover");
    
    // state
    
    /** The cell factory */
    private final Callback<T, ? extends FlowListCell<T>> cellFactory;
    
    /** The selection model */
	private final FlowListSelectionModel<T> selection;
	
	// nodes
	
	/** The layout pane */
	private final TilePane layout;
	
	/** The item nodes */
//	private final ListProperty<FlowListCell<T>> nodes;
	
	private final MappedList<FlowListCell<T>, T> mapping;
	
	/** The drag selection node */
	private final Rectangle dragRect;
	
	// drag selection
	
	/** The x coordinate of the start of the drag selection */
	private double dragSelectionStartX;
	
	/** The y coordinate of the start of the drag selection */
	private double dragSelectionStartY;
	
	// drag reorder

	/** A special data format for this instance of flow list only */
	private final DataFormat REORDER = new DataFormat("application/x-praisenter-" + UUID.randomUUID().toString().replaceAll("-", ""));
	
	/** The cell being dragged */
	private FlowListCell<T> dragReorderCell = null;

	// focus
	
	/** Keyboard shift-select */
	private FlowListCell<T> startCell = null;
	
	/** The current focused cell */
	private FlowListCell<T> currentCell = null;
	
	// properties
	
	/** The items themselves */
	private final ListProperty<T> items = new SimpleListProperty<T>(FXCollections.observableArrayList());

	/** True if drag selection is enabled */
	private final BooleanProperty dragSelectionEnabled = new SimpleBooleanProperty(true);
	
	/** True if drag reordering is enabled */
	private final BooleanProperty dragReorderEnabled = new SimpleBooleanProperty(false);
	
	/**
	 * Full constructor.
	 * @param orientation the orientation of the items
	 * @param cellFactory the cell factory
	 */
	public FlowListView(Orientation orientation, Callback<T, ? extends FlowListCell<T>> cellFactory) {
		this.getStyleClass().add("flow-list-view");
		
		this.cellFactory = cellFactory;

		this.layout = new TilePane();
//		this.nodes = new SimpleListProperty<FlowListCell<T>>(FXCollections.observableArrayList());
		
		this.dragRect = new Rectangle();
		this.dragRect.getStyleClass().add("flow-list-view-drag-selection-area");
		this.dragRect.setManaged(false);
		this.dragRect.setVisible(false);
		this.dragRect.setMouseTransparent(true);
		this.dragRect.setFocusTraversable(false);
		
		this.selection = new FlowListSelectionModel<T>(this);
		
		this.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		if (orientation == Orientation.HORIZONTAL) {
			this.setFitToWidth(true);
        } else {
        	this.setFitToHeight(true);
        }
        this.setFocusTraversable(true);
        
        StackPane stack = new StackPane(this.layout, this.dragRect);
        this.setContent(stack);
        
        this.layout.getStyleClass().add("flow-list-view-tiles");
        this.layout.setOrientation(orientation);
        
        // make the min height of the listing pane the height of the split pane 
 		this.layout.minHeightProperty().bind(this.heightProperty().subtract(20));
        
 		this.mapping = new MappedList<>(this.items, (T item) -> {
 			return createCell(item);
 		});
 		
        // bind the children of this view to the tagNode list
 		Bindings.bindContent(this.layout.getChildren(), this.mapping);
        
 		
 		
// 		// add a change listener for the items property
// 		this.items.addListener(new ListChangeListener<T>() {
//			@Override
//			public void onChanged(ListChangeListener.Change<? extends T> changes) {
//				// iterate the changes
//				while (changes.next()) {
//		             if (changes.wasPermutated()) {
//                    	 // reorder
//		            	 int from = changes.getFrom();
//		            	 int to = changes.getTo();
//		            	 // re-order a sub list so we don't have duplicate nodes in the scene graph
//		            	 List<FlowListCell<T>> range = new ArrayList<FlowListCell<T>>(nodes.subList(from, to));
//	                     for (int i = from; i < to; ++i) {
//	                    	 int j = changes.getPermutation(i);
//	                    	 range.set(j - from, nodes.get(i));
//	                     }
//	                     // now replace this in the real list
//	                     nodes.subList(from, to).clear();
//	                     nodes.addAll(from, range);
//	                 } else if (changes.wasUpdated()) {
//	                	 // not sure what to do here
//	                 } else {
//	                     for (T remitem : changes.getRemoved()) {
//	                         nodes.removeIf(v -> v.getData().equals(remitem));
//	                     }
//                         // clear from selections
//	                     int i = changes.getFrom();
//	                     for (T additem : changes.getAddedSubList()) {
//	                    	 if (i >= 0) {
//	                    		 nodes.add(i++, createCell(additem));
//	                    	 } else {
//	                    		 nodes.add(createCell(additem));
//	                    	 }
//	                     }
//	                 }
//		         }
//			}
// 		});
 		
 		this.layout.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
 			if (!e.isConsumed() && (e.getButton() == MouseButton.PRIMARY || e.getButton() == MouseButton.SECONDARY) && e.isStillSincePress()) {
 				this.selection.clear();
 			}
 		});
 		
 		this.layout.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
 			// drag selection
 			if (this.dragSelectionEnabled.get() && this.isMultipleSelectionEnabled()) {
	 			this.dragSelectionStartX = e.getX();
	 			this.dragSelectionStartY = e.getY();
 			}
 		});
 		
 		this.layout.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
 			// drag selection
 			if (this.dragSelectionEnabled.get() && this.isMultipleSelectionEnabled()) {
	 			// drag scrolling
	 			if (!e.isConsumed()) {
	 				this.scrollAtEdge(e.getX(), e.getY());
	 			}
	 			
	 			// drag detected
	 			if (!e.isConsumed() && e.getSource() == this.layout && !e.isDragDetect()) {
	 				// then update the coordinates of the selection rect
	 				// and select any cells that intersect the rect
	 				double ex = e.getX();
	 				double ey = e.getY();
	 				double cx = this.dragSelectionStartX;
	 				double cy = this.dragSelectionStartY;
	 				
	 				double x, y, w, h;
	 				
	 				if (ex < cx) {
	 					x = ex;
	 					w = cx - ex;
	 				} else {
	 					x = cx;
	 					w = ex - cx;
	 				}
	 				if (ey < cy) {
	 					y = ey;
	 					h = cy - ey;
	 				} else {
	 					y = cy;
	 					h = ey - cy;
	 				}
	 				
	 				this.dragRect.setX(x);
	 				this.dragRect.setY(y);
	 				this.dragRect.setWidth(w);
	 				this.dragRect.setHeight(h);
	 				this.dragRect.setVisible(true);
	 				
	 				// select anything under the rect
	 				List<FlowListCell<T>> nodes = new ArrayList<FlowListCell<T>>();
	 				for (FlowListCell<T> node : this.mapping) {
	 					if (node.getBoundsInParent().intersects(x, y, w, h)) {
	 						nodes.add(node);
	 					}
	 				}
	 				
					// make sure the items selected have actually changed
	 				// so we don't flood the listeners
	 				List<T> items = nodes.stream().map(n -> n.getData()).collect(Collectors.toList());
	 				if (!items.containsAll(this.selection.getSelectedItems()) || 
	 					!this.selection.getSelectedItems().containsAll(items)) {
						this.selection.selectCellsOnly(nodes);
	 				}
	 			}
 			}
 		});
 		
 		this.layout.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
 			// drag selection
 			if (this.dragSelectionEnabled.get() && this.isMultipleSelectionEnabled()) {
	 			if (!e.isConsumed() && e.getSource() == this.layout) {
	 				// then update the coordinates of the selection rect
	 				// and select any cells that intersect the rect
	 				this.dragRect.setVisible(false);
	 				this.dragRect.setX(0);
	 				this.dragRect.setY(0);
	 				this.dragRect.setWidth(0);
	 				this.dragRect.setHeight(0);
	 				this.dragSelectionStartX = 0;
	 				this.dragSelectionStartY = 0;
	 			}
 			}
 		});
 		
 		// keyboard navigation
 		this.layout.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
 			if (!e.isConsumed()) {
 				this.handleKeyEvent(e);
 			}
 			e.consume();
 		});
 		
 		this.addEventFilter(KeyEvent.ANY, e -> {
 			KeyCode code = e.getCode();
 			if (code != KeyCode.RIGHT && code != KeyCode.LEFT &&
 				code != KeyCode.UP && code != KeyCode.DOWN) {
 				return;
 			}
 			if (e.getEventType() == KeyEvent.KEY_PRESSED) {
 				this.handleKeyEvent(e);
 			}
 			e.consume();
 		});
	}
	
	// methods
	
	/**
	 * Creates a cell using the given cell factory and wires up events.
	 * @param item the item
	 * @return {@link FlowListCell}
	 */
	protected FlowListCell<T> createCell(T item) {
		FlowListCell<T> cell = this.cellFactory.call(item);
		
		// wire up events
    	cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
    		@Override
    		public void handle(MouseEvent event) {
    			if (!event.isConsumed() && (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) && event.isStillSincePress()) {
    				selection.handle(cell, event);
    				currentCell = cell;
    			}
    		}
    	});
    	cell.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!selection.isCellSelected(cell)) {
					cell.pseudoClassStateChanged(HOVER, true);
				}
			}
    	});
    	cell.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!selection.isCellSelected(cell)) {
					cell.pseudoClassStateChanged(HOVER, false);
				}
			}
    	});
    	
    	cell.setOnDragDetected(e -> {
    		if (this.dragReorderEnabled.get()) {
	    		Dragboard db = startDragAndDrop(TransferMode.MOVE);
	    		ClipboardContent content = new ClipboardContent();
	    		content.put(REORDER, "");
	    		db.setContent(content);
	    		db.setDragView(cell.snapshot(null, null));
	    		e.consume();
	    		this.dragReorderCell = cell;
    		}
    	});
    	cell.setOnDragEntered(e -> {
    		if (this.dragReorderEnabled.get() && e.getGestureSource() != cell && e.getDragboard().hasContent(REORDER)) {
    			cell.setOpacity(0.8);
    		}
    	});
    	cell.setOnDragExited(e -> {
    		if (this.dragReorderEnabled.get() && e.getGestureSource() != cell && e.getDragboard().hasContent(REORDER)) {
    			cell.setOpacity(1);
    		}
    	});
    	cell.setOnDragOver(e -> {
    		if (this.dragReorderEnabled.get() && e.getGestureSource() != cell && e.getDragboard().hasContent(REORDER)) {
    			e.acceptTransferModes(TransferMode.MOVE);
    			e.consume();
    		}
    	});
    	cell.setOnDragDropped(e -> {
    		if (this.dragReorderEnabled.get() && e.getDragboard().hasContent(REORDER)) {
    			// NOTE: "cell" is where we dropped it
				FlowListCell<T> source = this.dragReorderCell;
    			T data = source.getData();
    			
    			int toIndex = this.items.indexOf(cell.getData());
    			this.items.remove(data);
    			this.items.add(toIndex, data);
	    		e.setDropCompleted(true);
	    		e.consume();
    		}
    	});
    	cell.setOnDragDone(e -> {
    		if (this.dragReorderEnabled.get()) {
    			this.dragReorderCell = null;
    		}
    	});
    	
    	return cell;
	}
	
	/**
	 * Returns the children of this view's content.
	 * @return ObservableList&lt;Node&gt;
	 */
	protected ObservableList<Node> getLayoutChildren() {
		return this.layout.getChildren();
	}
	
	/**
	 * Handles the key events for the flow list.
	 * <p>
	 * The flow list only handles key events for selection. This includes the right, left, up, and
	 * down keys along with the shift modifier.
	 * @param event the event
	 */
	@SuppressWarnings("unchecked")
	private void handleKeyEvent(KeyEvent event) {
		KeyCode code = event.getCode();
		Orientation orientation = this.layout.getOrientation();
		
		// handle the keypad (KP_) keys also by transforming them
		// before we get to the main part of the logic
		if (code == KeyCode.KP_RIGHT) {
			code = KeyCode.RIGHT;
		} else if (code == KeyCode.KP_DOWN) {
			code = KeyCode.DOWN;
		} else if (code == KeyCode.KP_UP) {
			code = KeyCode.UP;
		} else if (code == KeyCode.KP_LEFT) {
			code = KeyCode.LEFT;
		}

		// only work if the user used the arrow keys
		if (code != KeyCode.RIGHT && code != KeyCode.LEFT && code != KeyCode.UP && code != KeyCode.DOWN) {
			return;
		}

		// check for shift down, which indicates a range selection
		boolean rangeSelect = false;
		int startIndex = -1;
		if (event.isShiftDown()) {
			rangeSelect = true;
			if (this.startCell == null && this.currentCell != null) {
				this.startCell = this.currentCell;
			}
			if (this.startCell != null) {
				startIndex = this.getLayoutChildren().indexOf(this.startCell);
			}
		} else {
			this.startCell = null;
		}

		// get the number of items
		int size = this.getLayoutChildren().size();
		if (this.currentCell == null) {
			// no cell has been focused by the key events yet
			if (size > 0) {
				// there are cells to select, so select the first
				this.currentCell = (FlowListCell<T>) this.getLayoutChildren().get(0);
				this.selection.selectCell(this.currentCell);
			}
		} else {
			// otherwise, start from the last focused cell
			int index = this.getLayoutChildren().indexOf(this.currentCell);
			if ((orientation == Orientation.HORIZONTAL && code == KeyCode.RIGHT) || 
				(orientation == Orientation.VERTICAL && code == KeyCode.DOWN)) {
				// in either of these cases we just need to select the next item in the list
				index++;
				if (index < size) {
					this.currentCell = (FlowListCell<T>) this.getLayoutChildren().get(index);
					if (rangeSelect && startIndex >= 0) {
						this.selection.selectCellsOnly(this.getRange(startIndex, index));
					} else {
						this.selection.selectCellOnly(this.currentCell);
					}
				}
			} else if ((orientation == Orientation.HORIZONTAL && code == KeyCode.LEFT) || 
					   (orientation == Orientation.VERTICAL && code == KeyCode.UP)) {
				// in either of these case we just need to select the previous item in the list
				index--;
				if (index >= 0) {
					this.currentCell = (FlowListCell<T>) this.getLayoutChildren().get(index);
					if (rangeSelect && startIndex >= 0) {
						this.selection.selectCellsOnly(this.getRange(startIndex, index));
					} else {
						this.selection.selectCellOnly(this.currentCell);
					}
				}
			} else {
				// in this case we are jumping rows or columns
				int ind = this.getNextCellIndex(index, code);
				int tn = this.items.size();
				if (ind >= 0 && ind < tn) {
					this.currentCell = (FlowListCell<T>) this.getLayoutChildren().get(ind);
					if (rangeSelect && startIndex >= 0) {
						this.selection.selectCellsOnly(this.getRange(startIndex, ind));
					} else {
						this.selection.selectCellOnly(this.currentCell);
					}
				}
			}
		}

		if (this.currentCell != null) {
			this.scrollToCell(this.currentCell);
		}
	}
	
	/**
	 * Returns a sublist of this list's nodes.
	 * @param i index a
	 * @param j index b
	 * @return List&lt;{@link FlowListCell}&lt;T&gt;&gt;
	 */
	@SuppressWarnings("unchecked")
	private List<FlowListCell<T>> getRange(int i, int j) {
		return this.getLayoutChildren().subList(i < j ? i : j, (i < j ? j : i) + 1)
			.stream()
			.map(n -> (FlowListCell<T>)n)
			.collect(Collectors.toList());
	}
	
	// UI related helpers
	
	/**
	 * Scrolls the scrollpane appropriately when the given coordinates are in
	 * the scrollpane's scroll area.
	 * @param x the x coorindate
	 * @param y the y coorindate
	 */
	private void scrollAtEdge(double x, double y) {
		Bounds b = this.getViewportBounds();
		double sx = this.getHvalue();
		double sy = this.getVvalue();
		double h = this.layout.getBoundsInParent().getHeight();
		double w = this.layout.getBoundsInParent().getWidth();
		double dx = (w - b.getWidth()) * sx + 20;
		double dy = (h - b.getHeight()) * sy + 20;
		
		double dw = b.getWidth() - 20;
		double dh = b.getHeight() - 20;
		if (dw < x) {
			this.setHvalue(this.getHvalue() + 0.01);
		} else if (x < dx) {
			this.setHvalue(this.getHvalue() - 0.01);
		}
		if (dh < y) {
			this.setVvalue(this.getVvalue() + 0.01);
		} else if (y < dy) {
			this.setVvalue(this.getVvalue() - 0.01);
		}
	}
	
	/**
	 * Returns the index of the next cell based on the given key code.
	 * <p>
	 * The key code should be one of DOWN, LEFT, RIGHT, or UP.
	 * @param index the current item index
	 * @param code the key code representing the direction
	 * @return int
	 */
	private int getNextCellIndex(int index, KeyCode code) {
		Orientation orientation = this.layout.getOrientation();
		
		// determine the tile layout and compute the index of the desired item
		double th = this.layout.getTileHeight() + this.layout.getVgap();
		double tw = this.layout.getTileWidth() + this.layout.getHgap();
		double h = this.layout.getHeight();
		double w = this.layout.getWidth();

		int npr = (int) Math.floor(w / tw);
		int npc = (int) Math.floor(h / th);

		int ind = -1;
		if (orientation == Orientation.HORIZONTAL && code == KeyCode.DOWN) {
			ind = index + npr;
		} else if (orientation == Orientation.HORIZONTAL && code == KeyCode.UP) {
			ind = index - npr;
		} else if (orientation == Orientation.VERTICAL && code == KeyCode.RIGHT) {
			ind = index + npc;
		} else if (orientation == Orientation.VERTICAL && code == KeyCode.UP) {
			ind = index - npc;
		}

		return ind;
	}
	
	/**
	 * Scrolls the scrollpane to the given cell such that it is fully within the
	 * viewport.
	 * @param cell the cell to scroll to
	 */
	private void scrollToCell(FlowListCell<T> cell) {
		Bounds vb = this.getViewportBounds();
		double sx = this.getHvalue();
		double sy = this.getVvalue();
		double h = this.layout.getBoundsInParent().getHeight();
		double w = this.layout.getBoundsInParent().getWidth();
		
		// compute the current scroll position
		double dx = (w - vb.getWidth()) * sx;
		double dy = (h - vb.getHeight()) * sy;
		
		Bounds cb = this.layout.getLocalToParentTransform().transform(cell.getBoundsInParent());

		// if the view bounds is smaller than the tile size, then we want to scroll to the top/left of the tile
		// otherwise we want to scroll to the bottom/right of the tile
		double tw = this.layout.getTileWidth();
		double th = this.layout.getTileHeight();
		double targetX = tw > vb.getWidth() ? cb.getMinX() : cb.getMaxX();
		double targetY = th > vb.getHeight() ? cb.getMinY() : cb.getMaxY();
		
		// then we need to scroll
		if (cb.getMaxX() > dx + vb.getWidth()) {
			// scroll right
			double dsx = (targetX - (dx + vb.getWidth())) / (w - vb.getWidth());
			this.setHvalue(this.getHvalue() + dsx);
		} else if (cb.getMinX() < dx) {
			// scroll left
			double dsx = (dx - cb.getMinX()) / (w - vb.getWidth());
			this.setHvalue(this.getHvalue() - dsx);
		}
		
		if (cb.getMaxY() > dy + vb.getHeight()) {
			// scroll down
			double dsy = (targetY - (dy + vb.getHeight())) / (h - vb.getHeight());
			this.setVvalue(this.getVvalue() + dsy);
		} else if (cb.getMinY() < dy) {
			// scroll up
			double dsy = (dy - cb.getMinY()) / (h - vb.getHeight());
			this.setVvalue(this.getVvalue() - dsy);
		}
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
	
	// drag selection
	
	/**
	 * Returns true if click-drag selection is enabled.
	 * @return boolean
	 */
	public boolean isDragSelectionEnabled() {
		return this.dragSelectionEnabled.get();
	}
	
	/**
	 * Toggles click-drag selection.
	 * @param flag true to enable
	 */
	public void setDragSelectionEnabled(boolean flag) {
		this.dragSelectionEnabled.set(flag);
	}
	
	/**
	 * Returns the drag selection enabled property.
	 * @return BooleanProperty
	 */
	public BooleanProperty dragSelectionEnabledProperty() {
		return this.dragSelectionEnabled;
	}
	
	// drag reorder
	
	/**
	 * Returns true if click-drag reordering is enabled.
	 * @return boolean
	 */
	public boolean isDragReorderEnabled() {
		return this.dragReorderEnabled.get();
	}
	
	/**
	 * Toggles click-drag redordering.
	 * @param flag true to enable
	 */
	public void setDragReorderEnabled(boolean flag) {
		this.dragReorderEnabled.set(flag);
	}
	
	/**
	 * Returns the click-drag reorder enabled property.
	 * @return BooleanProperty
	 */
	public BooleanProperty dragReorderEnabledProperty() {
		return this.dragReorderEnabled;
	}
	
	// multi select
	
	/**
	 * Returns true if multi selection is enabled.
	 * @return boolean
	 */
	public boolean isMultipleSelectionEnabled() {
		return this.selection.multiselectProperty().get();
	}
	
	/**
	 * Toggles multiple selection.
	 * @param flag true to enable
	 */
	public void setMultipleSelectionEnabled(boolean flag) {
		this.selection.multiselectProperty().set(flag);
	}
	
	/**
	 * Returns the multiple selection property.
	 * @return BooleanProperty
	 */
	public BooleanProperty multipleSelectionProperty() {
		return this.selection.multiselectProperty();
	}
	
	/**
	 * Returns the selection model.
	 * @return {@link FlowListSelectionModel}
	 */
	public FlowListSelectionModel<T> getSelectionModel() {
		return this.selection;
	}
}
