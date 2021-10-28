package org.praisenter.ui.controls;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public final class ProgressOverlay extends StackPane {
	private static final String PROGRESS_OVERLAY_CSS = "p-progress-overlay";
	private static final String PROGRESS_OVERLAY_INDICATOR_CSS = "p-progress-overlay-indicator";
	
	private final ProgressIndicator progress = new ProgressIndicator();
	
	public ProgressOverlay() {
		this.getStyleClass().add(PROGRESS_OVERLAY_CSS);
		this.progress.getStyleClass().add(PROGRESS_OVERLAY_INDICATOR_CSS);
		
		this.getChildren().add(this.progress);
	}
	
	public ProgressIndicator getProgressIndicator() {
		return this.progress;
	}
}
