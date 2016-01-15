package org.praisenter.javafx;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;

public class Testing {
	public static final Border border(Color color) {
		return new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, null, new BorderWidths(2)));
	}
}
