package org.praisenter.ui.controls;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class EditorFieldGroup extends VBox {
	private static final String EDITOR_FIELD_GROUP_CLASS = "p-editor-field-group";
	
	public EditorFieldGroup(Node... children) {
		this();
		this.getChildren().addAll(children);
	}
	
	public EditorFieldGroup() {
		this.getStyleClass().add(EDITOR_FIELD_GROUP_CLASS);
	}
}
