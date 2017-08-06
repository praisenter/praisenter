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
package org.praisenter.javafx.slide.editor;

import java.util.function.BiConsumer;

import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.graphics.Rectangle;
import org.praisenter.utility.Scaling;

import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/**
 * Event handler for mouse events in the slide editor.
 * @author William Bittle
 * @version 3.0.0
 */
final class SlideRegionMouseEventHandler implements EventHandler<MouseEvent> {
	/** The hover pseudo class */
	private static final PseudoClass HOVER = PseudoClass.getPseudoClass("edit-hover");
	
	/** The minimum size of a region */
	private static final int MIN_DIMENSION = 50;
	
	// context
	
	/** The region the handler is for */
	private final ObservableSlideRegion<?> region;
	
	/** The position changed handler */
	private final BiConsumer<ObservableSlideRegion<?>, Rectangle> positionChanged;
	
	/** The size changed handler */
	private final BiConsumer<ObservableSlideRegion<?>, Rectangle> sizeChanged;
	
	// cursor
	
	/** The scene to change the cursor */
	private Scene scene = null;
	
	/** The cursor */
	private Cursor cursor = null;
	
	// tracking
	
	/** The start x value of the mouse gesture */
	private double sx;
	
	/** The start y value of the mouse gesture */
	private double sy;
	
	/** The current x coordinate of the region */
	private double x;
	
	/** The current y coordinate of the region */
	private double y;
	
	/** The current width of the region */
	private double w;
	
	/** The current height of the region */
	private double h;
	
	/**
	 * Constructor.
	 * @param region the region
	 * @param positionChanged the position changed handler
	 * @param sizeChanged the size changed handler
	 */
	public SlideRegionMouseEventHandler(
			ObservableSlideRegion<?> region, 
			BiConsumer<ObservableSlideRegion<?>, Rectangle> positionChanged, 
			BiConsumer<ObservableSlideRegion<?>, Rectangle> sizeChanged) {
		this.region = region;
		this.positionChanged = positionChanged;
		this.sizeChanged = sizeChanged;
	}
	
	/**
	 * Called when the mouse has entered the region.
	 * @param event the event
	 */
	private void entered(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
			region.getEditBorderNode().pseudoClassStateChanged(HOVER, true);
		}
	}
	
	/**
	 * Called when the mouse has exited the region.
	 * @param event the event
	 */
	private void exited(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
			region.getEditBorderNode().pseudoClassStateChanged(HOVER, false);
			
			Scene scene = region.getDisplayPane().getScene();
			if (scene != null) {
				scene.setCursor(Cursor.DEFAULT);
			}
		}
	}
	
	/**
	 * Called when the is hovering over the region.
	 * @param event the event
	 */
	private void hover(MouseEvent event) {
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
			cursor = Fx.getCursorForPosition(x, y, w, h, 15);
		}
		
		if (scene != null) {
			scene.setCursor(cursor);
		}
		
		event.consume();
	}
	
	/**
	 * Called when a mouse button has been pressed on the region.
	 * @param event the event
	 */
	private void pressed(MouseEvent event) {
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
	
	/**
	 * Called when a mouse drag gesture involves the region.
	 * @param event the event
	 */
	private void dragged(MouseEvent event) {
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
				this.positionChanged(x + dxi, y + dyi);
			}
			
			if (cursor == Cursor.E_RESIZE) {
				dxi = clamp(w, dxi);
				this.sizeChanged(x, y, w + dxi, h);
			} else if (cursor == Cursor.S_RESIZE) {
				dyi = clamp(h, dyi);
				this.sizeChanged(x, y, w, h + dyi);
			} else if (cursor == Cursor.N_RESIZE) {
				dyi = -clamp(h, -dyi);
				this.sizeChanged(x, y + dyi, w, h - dyi);
			} else if (cursor == Cursor.W_RESIZE) {
				dxi = -clamp(w, -dxi);
				this.sizeChanged(x + dxi, y, w - dxi, h);
			} else if (cursor == Cursor.SE_RESIZE) {
				dxi = clamp(w, dxi);
				dyi = clamp(h, dyi);
				this.sizeChanged(x, y, w + dxi, h + dyi);
			} else if (cursor == Cursor.SW_RESIZE) {
				dxi = -clamp(w, -dxi);
				dyi = clamp(h, dyi);
				this.sizeChanged(x + dxi, y, w - dxi, h + dyi);
			} else if (cursor == Cursor.NE_RESIZE) {
				dxi = clamp(w, dxi);
				dyi = -clamp(h, -dyi);
				this.sizeChanged(x, y + dyi, w + dxi, h - dyi);
			} else if (cursor == Cursor.NW_RESIZE) {
				dxi = -clamp(w, -dxi);
				dyi = -clamp(h, -dyi);
				this.sizeChanged(x + dxi, y + dyi, w - dxi, h - dyi);
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
	
	/**
	 * Clamps the given dimension + value to no less than MIN_DIMENSION.
	 * @param dimension the dimension
	 * @param value the value
	 * @return double
	 */
	private static double clamp(double dimension, double value) {
		if (value < 0) {
			if (dimension + value < MIN_DIMENSION) {
				return -Math.floor(dimension - MIN_DIMENSION);
			}
		}
		return Math.floor(value);
	}
	
	/**
	 * Calls the position changed handler.
	 * @param x the new x coordinate
	 * @param y the new y coordinate
	 */
	private void positionChanged(double x, double y) {
		this.positionChanged.accept(this.region, new Rectangle(x, y, 0, 0));
	}
	
	/**
	 * Calls the size changed handler.
	 * @param x the new x coordinate
	 * @param y the new y coordinate
	 * @param w the new width
	 * @param h the new height
	 */
	private void sizeChanged(double x, double y, double w, double h) {
		this.sizeChanged.accept(this.region, new Rectangle(x, y, w, h));
	}
	
	/* (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
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
