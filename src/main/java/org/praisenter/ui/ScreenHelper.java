package org.praisenter.ui;

import org.praisenter.data.workspace.Resolution;
import org.praisenter.utility.RuntimeProperties;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public final class ScreenHelper {
	private ScreenHelper() {}
	
	public static final Rectangle2D getScaledScreenBounds(Screen screen) {
		Rectangle2D bounds = screen.getBounds();
		// apparently for MacOS JavaFX decided to interpret the
		// scaling automatically without any additional work
		// by the developer
		if (RuntimeProperties.IS_MAC_OS) {
			return new Rectangle2D(
				bounds.getMinX(), 
				bounds.getMinY(), 
				bounds.getWidth(), 
				bounds.getHeight());
		} else {
			return new Rectangle2D(
				bounds.getMinX(), 
				bounds.getMinY(), 
				bounds.getWidth() * screen.getOutputScaleX(), 
				bounds.getHeight() * screen.getOutputScaleY());
		}
	}
	
	public static final Resolution getResolution(Screen screen) {
		Rectangle2D bounds = getScaledScreenBounds(screen);
		return new Resolution(
				(int)bounds.getWidth(),
				(int)bounds.getHeight());
	}
}
