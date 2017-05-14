package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.utility.Scaling;

import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

class SlideRegionMouseEventHandler implements EventHandler<MouseEvent> {

	private static final PseudoClass HOVER = PseudoClass.getPseudoClass("edit-hover");
	private static final int MIN_DIMENSION = 50;
	
	private final ObservableSlide<?> slide;
	private final ObservableSlideRegion<?> region;
	
	private Scene scene = null;
	private Cursor cursor = null;
	
	private double sx;
	private double sy;
	
	private double x;
	private double y;
	private double w;
	private double h;
	
	public SlideRegionMouseEventHandler(ObservableSlide<?> slide, ObservableSlideRegion<?> region) {
		this.slide = slide;
		this.region = region;
	}
	
	public void entered(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
			region.getEditBorderNode().pseudoClassStateChanged(HOVER, true);
		}
	}
	
	public void exited(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
			region.getEditBorderNode().pseudoClassStateChanged(HOVER, false);
			
			Scene scene = region.getDisplayPane().getScene();
			if (scene != null) {
				scene.setCursor(Cursor.DEFAULT);
			}
		}
	}
	
	public void hover(MouseEvent event) {
		double x = event.getX();
		double y = event.getY();
		double w = 0;
		double h = 0;
		
		Node node = region.getDisplayPane();
		if (node instanceof Region) {
			Region region = (Region)node;
			w = region.getWidth();
			h = region.getHeight();
		}
		
		Cursor cursor = Cursor.DEFAULT;
		Scene scene = node.getScene();
		if (region instanceof ObservableSlideComponent) {
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
			double sf = 1.0 / scaling.factor;
			double dxi = dx * sf;
			double dyi = dy * sf;
			
			// are we moving the node?
			if (cursor == Cursor.MOVE) {				
				// we SET the x/y for accuracy
				region.setX(x + dxi);
				region.setY(y + dyi);
			}
			
			if (cursor == Cursor.E_RESIZE) {
				dxi = clamp(w, dxi);
				region.setWidth(w + dxi);
			} else if (cursor == Cursor.S_RESIZE) {
				dyi = clamp(h, dyi);
				region.setHeight(h + dyi);
			} else if (cursor == Cursor.N_RESIZE) {
				dyi = -clamp(h, -dyi);
				region.setY(y + dyi);
				region.setHeight(h - dyi);
			} else if (cursor == Cursor.W_RESIZE) {
				dxi = -clamp(w, -dxi);
				region.setX(x + dxi);
				region.setWidth(w - dxi);
			} else if (cursor == Cursor.SE_RESIZE) {
				dxi = clamp(w, dxi);
				dyi = clamp(h, dyi);
				region.setWidth(w + dxi);
				region.setHeight(h + dyi);
			} else if (cursor == Cursor.SW_RESIZE) {
				dxi = -clamp(w, -dxi);
				dyi = clamp(h, dyi);
				region.setX(x + dxi);
				region.setWidth(w - dxi);
				region.setHeight(h + dyi);
			} else if (cursor == Cursor.NE_RESIZE) {
				dxi = clamp(w, dxi);
				dyi = -clamp(h, -dyi);
				region.setWidth(w + dxi);
				region.setY(y + dyi);
				region.setHeight(h - dyi);
			} else if (cursor == Cursor.NW_RESIZE) {
				dxi = -clamp(w, -dxi);
				dyi = -clamp(h, -dyi);
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
	
	private static double clamp(double dimension, double value) {
		if (value < 0) {
			if (dimension + value < MIN_DIMENSION) {
				return -Math.floor(dimension - MIN_DIMENSION);
			}
		}
		return Math.floor(value);
	}
	
	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_PRESSED ||
			event.getEventType() == MouseEvent.MOUSE_RELEASED ||
			event.getEventType() == MouseEvent.MOUSE_CLICKED) {
			pressed(event);
		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			dragged(event);
		} else if (event.getEventType() == MouseEvent.MOUSE_ENTERED || event.getEventType() == MouseEvent.MOUSE_ENTERED_TARGET) {
			entered(event);
		} else if (event.getEventType() == MouseEvent.MOUSE_EXITED || event.getEventType() == MouseEvent.MOUSE_EXITED_TARGET) {
			exited(event);
		} else if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
			hover(event);
		}
	}
}
