package org.praisenter.ui.controls;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;

public final class EditorTitledPane extends TitledPane {
	private static final String EDITOR_TITLED_PANE_CLASS = "p-editor-titled-pane";
	
	public EditorTitledPane(String title, Node content) {
		super(title, content);
		this.setAnimated(false);
		this.getStyleClass().add(EDITOR_TITLED_PANE_CLASS);
	}
}
