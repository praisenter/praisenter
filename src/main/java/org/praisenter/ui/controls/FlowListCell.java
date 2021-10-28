package org.praisenter.ui.controls;

import javafx.event.EventTarget;
import javafx.scene.layout.VBox;

public class FlowListCell<T> extends VBox implements EventTarget {
	private static final String FLOW_LIST_CELL_CSS = "p-flow-list-view-cell";
	
	private final T data;

	public FlowListCell(T data) {
		this.data = data;
		this.getStyleClass().add(FLOW_LIST_CELL_CSS);
		this.setFocusTraversable(false);
	}

	public T getData() {
		return this.data;
	}
}
