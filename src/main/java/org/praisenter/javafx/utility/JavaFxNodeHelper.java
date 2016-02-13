package org.praisenter.javafx.utility;

import javafx.scene.layout.Region;

public final class JavaFxNodeHelper {
	private JavaFxNodeHelper() {}
	
	public static final void setSize(Region region, double width, double height) {
		region.setPrefSize(width, height);
		region.setMinSize(width, height);
		region.setMaxSize(width, height);
	}
}
