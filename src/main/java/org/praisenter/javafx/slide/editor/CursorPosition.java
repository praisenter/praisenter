package org.praisenter.javafx.slide.editor;

import javafx.scene.Cursor;

final class CursorPosition {
	private CursorPosition() {}
	
	private static final double RESIZE_WIDTH = 10;
	
	public static final Cursor getCursorForPosition(double x, double y, double w, double h) {
		if (x <= RESIZE_WIDTH && y <= RESIZE_WIDTH) { 
			// top left corner
			return Cursor.NW_RESIZE;
		} else if (x >= w - RESIZE_WIDTH && y >= h - RESIZE_WIDTH) {
			// bottom right corner
			return Cursor.SE_RESIZE;
		} else if (x <= RESIZE_WIDTH && y >= h - RESIZE_WIDTH) {
			// bottom left corner
			return Cursor.SW_RESIZE;
		} else if (x >= w - RESIZE_WIDTH &&	y <= RESIZE_WIDTH) {
			// top right corner
			return Cursor.NE_RESIZE;
		} else if (x <= RESIZE_WIDTH) {
			// left
			return Cursor.W_RESIZE;
		} else if (x >= w - RESIZE_WIDTH) {
			// right
			return Cursor.E_RESIZE;
		} else if (y <= RESIZE_WIDTH) {
			// top
			return Cursor.N_RESIZE;
		} else if (y >= h - RESIZE_WIDTH) {
			// bottom
			return Cursor.S_RESIZE;
		} else {
			return Cursor.MOVE;
		}
	}
}
