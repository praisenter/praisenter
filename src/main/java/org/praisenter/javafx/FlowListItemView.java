package org.praisenter.javafx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

// FIXME these should use CSS classes so we can style them differently per theme (the dark theme for example)
public final class FlowListItemView<T> extends VBox implements EventTarget {
	private static final Paint BACKGROUND_COLOR_NORMAL = Color.TRANSPARENT;
    private static final Paint BACKGROUND_COLOR_HOVER = Color.rgb(153, 209, 255, 0.5);
    private static final Paint BACKGROUND_COLOR_SELECTED = Color.rgb(204, 232, 255);
    
    private static final Background BACKGROUND_NORMAL = new Background(new BackgroundFill(BACKGROUND_COLOR_NORMAL, null, null));
    private static final Background BACKGROUND_HOVER = new Background(new BackgroundFill(BACKGROUND_COLOR_HOVER, null, null));
    private static final Background BACKGROUND_SELECTED = new Background(new BackgroundFill(BACKGROUND_COLOR_SELECTED, null, null));
    
    private static final Paint BORDER_COLOR_NORMAL = Color.TRANSPARENT;
    private static final Paint BORDER_COLOR_SELECTED = Color.rgb(153, 209, 255);
    
    private static final Border BORDER_NORMAL = new Border(new BorderStroke(BORDER_COLOR_NORMAL, BorderStrokeStyle.SOLID, null, new BorderWidths(1)));
    private static final Border BORDER_SELECTED = new Border(new BorderStroke(BORDER_COLOR_SELECTED, BorderStrokeStyle.SOLID, null, new BorderWidths(1)));
	
	private final BooleanProperty selected;
	private final T data;
	
	public FlowListItemView(T data) {
		this.selected = new SimpleBooleanProperty(false);
		this.data = data;
		
		// this
		final FlowListItemView<T> cell = this;
		
		// change the colors of the cell based on the selected state
		this.selected.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ob, Boolean oldValue, Boolean newValue) {
				if (!oldValue) {
    				cell.setBorder(BORDER_SELECTED);
    				cell.setBackground(BACKGROUND_SELECTED);
    			} else {
    				cell.setBorder(BORDER_NORMAL);
    				cell.setBackground(BACKGROUND_NORMAL);
    			}
			}
		});
		
		// setup the cell
		cell.setPadding(new Insets(2, 2, 2, 2));
    	cell.setBorder(BORDER_NORMAL);
    	cell.setBackground(BACKGROUND_NORMAL);
    	cell.setAlignment(Pos.CENTER);
    	cell.setFocusTraversable(false);

    	// handle selection
    	cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
    		@Override
    		public void handle(MouseEvent event) {
    			if (event.getButton() == MouseButton.PRIMARY) {
	    			// are we being selected?
					boolean select = !selected.get();
					
					// toggle the selected flag
	    			selected.set(!selected.get());
	    			
	    			// build a select event to notify any node interested
	    			SelectionEvent selectEvent = null;
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
	    			
	    			// fire the event
	    			fireEvent(selectEvent);
    			}
    		}
    	});
    	
    	// handle hover
    	cell.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!selected.get()) {
					cell.setBackground(BACKGROUND_HOVER);
				}
			}
    	});
    	cell.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!selected.get()) {
					cell.setBackground(BACKGROUND_NORMAL);
				}
			}
    	});
	}
	
	public T getData() {
		return this.data;
	}
	
	public void setSelected(boolean flag) {
		this.selected.set(flag);
	}
	
	public boolean isSelected() {
		return this.selected.get();
	}
	
	public BooleanProperty selectedProperty() {
		return this.selected;
	}
}
