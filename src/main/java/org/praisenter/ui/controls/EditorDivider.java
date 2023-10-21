package org.praisenter.ui.controls;

import atlantafx.base.theme.Styles;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public final class EditorDivider extends HBox {
	private static final String EDITOR_DIVIDER_CLASS = "p-editor-divider";
	
	public EditorDivider(String label) {
		Label lbl = new Label(label);
		lbl.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_SMALL);

		Separator sepl = new Separator(Orientation.HORIZONTAL);
		sepl.setMaxWidth(Double.MAX_VALUE);
		
		Separator sepr = new Separator(Orientation.HORIZONTAL);
		sepr.setMaxWidth(Double.MAX_VALUE);
		
		this.getChildren().addAll(sepl, lbl, sepr);
		this.setAlignment(Pos.CENTER_LEFT);
		this.getStyleClass().add(EDITOR_DIVIDER_CLASS);
		
		HBox.setHgrow(sepl, Priority.ALWAYS);
		HBox.setHgrow(sepr, Priority.ALWAYS);
	}
}
