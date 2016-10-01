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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Represents an item in a {@link FlowListView}.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> the item type
 */
public final class FlowListItem<T> extends VBox implements EventTarget {
	/** The :selected CSS pseudo class for styling */
    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    
    /** The :hover CSS pseudo class for styling */
    private static final PseudoClass HOVER = PseudoClass.getPseudoClass("hover");
    
    /** The class name for this node for CSS styling */
    private static final String CLASS_NAME = "flow-list-view-item";
    
    /** True if this item is selected */
	private final BooleanProperty selected;
	
	/** The item's data */
	private final T data;
	
	/**
	 * Full constructor.
	 * @param data the data for this item
	 */
	public FlowListItem(T data) {
		this.selected = new SimpleBooleanProperty(false);
		this.data = data;
		
		final FlowListItem<T> cell = this;
		
		// change the colors of the cell based on the selected state
		this.selected.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ob, Boolean oldValue, Boolean newValue) {
				if (!oldValue) {
					cell.pseudoClassStateChanged(SELECTED, true);
    			} else {
    				cell.pseudoClassStateChanged(SELECTED, false);
    			}
			}
		});
		
		// setup the cell
		cell.getStyleClass().add(CLASS_NAME);
		cell.setPadding(new Insets(2, 2, 2, 2));
    	cell.setAlignment(Pos.CENTER);
    	cell.setFocusTraversable(false);

    	// handle selection
    	cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
    		@Override
    		public void handle(MouseEvent event) {
    			if (event.getButton() == MouseButton.PRIMARY) {
    				// build a select event to notify any node interested
	    			SelectionEvent selectEvent = null;
	    			
    				if (event.getClickCount() == 2) {
    					// double click
    					selectEvent = new SelectionEvent(cell, cell, SelectionEvent.DOUBLE_CLICK);
    				} else {
    					// single click
		    			// are we being selected?
						boolean select = !selected.get();
						
						// toggle the selected flag
		    			selected.set(!selected.get());
		    			
		    			if (event.isControlDown()) {
		    				// then its a multi-(de)select
		    				if (select) {
		    					selectEvent = new SelectionEvent(cell, cell, SelectionEvent.SELECT_MULTIPLE);
		    				} else {
		    					selectEvent = new SelectionEvent(cell, cell, SelectionEvent.DESELECT_MULTIPLE);
		    				}
		    			} else {
		    				// then its a single select
		    				if (select) {
		    					selectEvent = new SelectionEvent(cell, cell, SelectionEvent.SELECT);
		    				} else {
		    					selectEvent = new SelectionEvent(cell, cell, SelectionEvent.DESELECT);
		    				}
		    			}
    				}
	    			
	    			// fire the event
	    			fireEvent(selectEvent);
	    			
	    			event.consume();
    			}
    		}
    	});
    	
    	// handle hover
    	cell.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!selected.get()) {
					cell.pseudoClassStateChanged(HOVER, true);
				}
			}
    	});
    	cell.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!selected.get()) {
					cell.pseudoClassStateChanged(HOVER, false);
				}
			}
    	});
	}
	
	/**
	 * Returns the data for this item.
	 * @return T
	 */
	public T getData() {
		return this.data;
	}
	
	/**
	 * Sets the selected state of this item.
	 * @param flag true if this item should be selected
	 */
	public void setSelected(boolean flag) {
		this.selected.set(flag);
	}
	
	/**
	 * Returns true if this item is selected.
	 * @return boolean
	 */
	public boolean isSelected() {
		return this.selected.get();
	}
	
	/**
	 * Returns the selected property.
	 * @return BooleanProperty
	 */
	public BooleanProperty selectedProperty() {
		return this.selected;
	}
}
