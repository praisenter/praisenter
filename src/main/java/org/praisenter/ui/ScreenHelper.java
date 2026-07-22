package org.praisenter.ui;

import org.praisenter.data.workspace.Resolution;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public final class ScreenHelper {
	private ScreenHelper() {}

	public static final Resolution getResolution(Screen screen) {
		Rectangle2D bounds = screen.getBounds();
		
		int w = (int)bounds.getWidth();
		int h = (int)bounds.getHeight();

		return new Resolution(w, h);
	}
}
