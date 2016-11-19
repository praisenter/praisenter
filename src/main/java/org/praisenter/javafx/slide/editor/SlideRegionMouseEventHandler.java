package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.Scaling;

import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

class SlideRegionMouseEventHandler implements EventHandler<MouseEvent> {

	private static final PseudoClass HOVER = PseudoClass.getPseudoClass("hover");
	private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
	
	
	private final ObservableSlideRegion<?> region;
	
	private Scene scene = null;
	private Cursor cursor = null;
	
	private double sx;
	private double sy;
	
	private int x;
	private int y;
	private int w;
	private int h;
	
	public SlideRegionMouseEventHandler(ObservableSlideRegion<?> region) {
		this.region = region;
	}
	
	public void entered(MouseEvent event) {
		region.getDisplayPane().pseudoClassStateChanged(HOVER, true);
		event.consume();
	}
	
	public void exited(MouseEvent event) {
		region.getDisplayPane().pseudoClassStateChanged(HOVER, false);
		Scene scene = region.getDisplayPane().getScene();
		if (scene != null) {
			scene.setCursor(Cursor.DEFAULT);
		}
		event.consume();
	}
	
	public void hover(MouseEvent event) {
		Region node = region.getDisplayPane();
		
		Cursor cursor = Cursor.DEFAULT;
		Scene scene = node.getScene();
		if (region instanceof ObservableSlideComponent) {
			double x = event.getX();
			double y = event.getY();
			double w = node.getWidth();
			double h = node.getHeight();
			
			cursor = CursorPosition.getCursorForPosition(x, y, w, h);
		}
		
		if (scene != null) {
			scene.setCursor(cursor);
		}
		
		event.consume();
	}
	
	public void pressed(MouseEvent event) {
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
		
		event.consume();
	}
	
	public void dragged(MouseEvent event) {
		// only components can be moved or resized
		if (region instanceof ObservableSlideComponent) {
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
			
			// FIXME only allow moving if the slide is a notification slide
			// are we moving the node?
			if (cursor == Cursor.MOVE) {				
				// we SET the x/y for accuracy
				region.setX(x + dxi);
				region.setY(y + dyi);
			}
			
			if (cursor == Cursor.E_RESIZE) {
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
		}
		
		// make sure the cursor stays the same regardless of where
		// the mouse may move to
		if (scene != null && cursor != null) {
			// make sure the cursor stays the same
			scene.setCursor(cursor);
		}
		
		event.consume();
	}
	
	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_PRESSED ||
			event.getEventType() == MouseEvent.MOUSE_RELEASED ||
			event.getEventType() == MouseEvent.MOUSE_CLICKED) {
			pressed(event);
		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			dragged(event);
		} else if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
			entered(event);
		} else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
			exited(event);
		} else if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
			hover(event);
		}
	}
}
