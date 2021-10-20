package org.praisenter.ui.controls;

import javafx.event.EventTarget;
import javafx.scene.layout.VBox;

public class FlowListCell<T> extends VBox implements EventTarget {
	private final T data;

	public FlowListCell(T data) {
		this.data = data;
		this.getStyleClass().add("flow-list-view-cell");
		this.setFocusTraversable(false);
	}

	public T getData() {
		return this.data;
	}
}
