package org.praisenter.ui;

import org.praisenter.data.workspace.Resolution;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public final class ScreenHelper {
	private ScreenHelper() {}
	
	public static final Rectangle2D getScaledScreenBounds(Screen screen) {
		Rectangle2D bounds = screen.getBounds();
		return new Rectangle2D(
				bounds.getMinX(), 
				bounds.getMinY(), 
				bounds.getWidth() * screen.getOutputScaleX(), 
				bounds.getHeight() * screen.getOutputScaleY());
	}
	
	public static final Resolution getResolution(Screen screen) {
		Rectangle2D bounds = getScaledScreenBounds(screen);
		return new Resolution(
				(int)bounds.getWidth(),
				(int)bounds.getHeight());
	}
}
