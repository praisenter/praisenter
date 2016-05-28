package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.Scaling;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

class SlideRegionDraggedEventHandler implements EventHandler<MouseEvent> {
	private final ObservableSlideRegion<?> region;
	
	private Scene scene = null;
	private Cursor cursor = null;
	
	private double sx;
	private double sy;
	
	private int x;
	private int y;
	private int w;
	private int h;
	
	public SlideRegionDraggedEventHandler(ObservableSlideRegion<?> region) {
		this.region = region;
	}
	
	@Override
	public void handle(MouseEvent event) {
		// when the mouse is pressed, store some info so that
		// we are ready for a drag event
		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			// record the cursor at this time
			// so we can keep it the same regardless of where
			// the mouse goes
			if (event.getSource() instanceof Node) {
				scene = ((Node)event.getSource()).getScene();
				if (scene != null) {
					cursor = scene.getCursor();
				}
			}
			
			// record the scene coordinates of the start
			sx = event.getSceneX();
			sy = event.getSceneY();
			
			// record the original x,y coordinates of the slide region
			x = region.getX();
			y = region.getY();
			w = region.getWidth();
			h = region.getHeight();
		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			// make sure the cursor stays the same regardless of where
			// the mouse may move to
			if (scene != null && cursor != null) {
				// make sure the cursor stays the same
				scene.setCursor(cursor);
			}
			
			// compute the integer difference in position
			// of the mouse from the start and scale it
			// by the scale factor
			double nx = event.getSceneX();
			double ny = event.getSceneY();
			double dx = nx - sx;
			double dy = ny - sy;
			
			// get the scaled (transformed translation)
			Scaling scaling = region.getScaling();
			double sf = 1.0 / scaling.scale;
			int dxi = (int)Math.floor(dx * sf);
			int dyi = (int)Math.floor(dy * sf);
			
			// are we moving the node?
			if (cursor == Cursor.MOVE) {				
				// we SET the x/y for accuracy
				region.setX(x + dxi);
				region.setY(y + dyi);
			} else if (cursor == Cursor.E_RESIZE) {
				region.setWidth(w + dxi);
			} else if (cursor == Cursor.S_RESIZE) {
				region.setHeight(h + dyi);
			} else if (cursor == Cursor.N_RESIZE) {
				region.setY(y + dyi);
				region.setHeight(h - dyi);
			} else if (cursor == Cursor.W_RESIZE) {
				region.setX(x + dxi);
				region.setWidth(w - dxi);
			} else if (cursor == Cursor.SE_RESIZE) {
				region.setWidth(w + dxi);
				region.setHeight(h + dyi);
			} else if (cursor == Cursor.SW_RESIZE) {
				region.setX(x + dxi);
				region.setWidth(w - dxi);
				region.setHeight(h + dyi);
			} else if (cursor == Cursor.NE_RESIZE) {
				region.setWidth(w + dxi);
				region.setY(y + dyi);
				region.setHeight(h - dyi);
			} else if (cursor == Cursor.NW_RESIZE) {
				region.setX(x + dxi);
				region.setWidth(w - dxi);
				region.setY(y + dyi);
				region.setHeight(h - dyi);
			}
		} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			// when the mouse is released we need to go back to 
			// the default cursor
			
		}
	}
}
