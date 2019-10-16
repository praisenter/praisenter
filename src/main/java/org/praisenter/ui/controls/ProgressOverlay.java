package org.praisenter.ui.controls;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public final class ProgressOverlay extends StackPane {
	private final ProgressIndicator progress = new ProgressIndicator();
	
	public ProgressOverlay() {
		this.getStyleClass().add("progress-overlay");
		this.progress.getStyleClass().add("progress-overlay-indicator");
		
		this.getChildren().add(this.progress);
	}
	
	public ProgressIndicator getProgressIndicator() {
		return this.progress;
	}
}
