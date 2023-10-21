package org.praisenter.ui.controls;

import atlantafx.base.theme.Styles;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class EditorField extends VBox {
	private static final double FIELD_WIDTH_RATIO = 0.65;
	
	public static final String LAYOUT_VERTICAL = "vertical";
	public static final String LAYOUT_HORIZONTAL = "horizontal";
	
	public EditorField(Region field) {
		this(null, null, field, LAYOUT_VERTICAL);
	}
	
	public EditorField(String name, Region field) {
		this(name, null, field, LAYOUT_HORIZONTAL);
	}
	
	public EditorField(String name, Region field, String layout) {
		this(name, null, field, layout);
	}
	
	public EditorField(String name, String description, Region field) {
		this(name, description, field, LAYOUT_HORIZONTAL);
	}
	
	public EditorField(String name, String description, Region field, String layout) {
		
		Node labelFieldLayout = null;
		if (name != null) {
			// label
			Label lblName = new Label(name);
			lblName.getStyleClass().add("p-editor-field-label");
			lblName.setMinWidth(0);
			
			if (layout == LAYOUT_HORIZONTAL) {
				HBox spacer = new HBox();
				spacer.setMaxWidth(Double.MAX_VALUE);
				HBox.setHgrow(spacer, Priority.ALWAYS);
				
				StackPane fieldWrapper = new StackPane(field);
				StackPane.setAlignment(field, Pos.CENTER_LEFT);
				
				// label-spacer-field layout
				HBox lf = new HBox(lblName, spacer, fieldWrapper);
				lf.setAlignment(Pos.TOP_LEFT);
				
				field.prefWidthProperty().bind(this.widthProperty().multiply(FIELD_WIDTH_RATIO));
				field.minWidthProperty().bind(this.widthProperty().multiply(FIELD_WIDTH_RATIO));
				field.maxWidthProperty().bind(this.widthProperty().multiply(FIELD_WIDTH_RATIO));
				
				labelFieldLayout = lf;
			} else {
				VBox lf = new VBox(lblName, field);
				labelFieldLayout = lf;
			}
		} else {
			labelFieldLayout = new VBox(field);
		}
		
		
		labelFieldLayout.getStyleClass().add("p-editor-field");
		
		Label lblNameDescription = new Label(description);
		lblNameDescription.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_SMALL);
		lblNameDescription.setWrapText(true);
		lblNameDescription.setMinWidth(0);
		lblNameDescription.getStyleClass().add("p-editor-field-description");
		
		if (description == null) {
			lblNameDescription.setVisible(false);
			lblNameDescription.setManaged(false);
		}
		
//		lblNameDescription.prefWidthProperty().bind(outer.widthProperty().multiply(pw));
//		lblNameDescription.minWidthProperty().bind(outer.widthProperty().multiply(pw));
//		lblNameDescription.maxWidthProperty().bind(outer.widthProperty().multiply(pw));
//		HBox spacer2 = new HBox();
//		spacer2.setMaxWidth(Double.MAX_VALUE);
//		HBox.setHgrow(spacer2, Priority.ALWAYS);
//		HBox desc = new HBox(spacer2, lblNameDescription);
		
		this.getStyleClass().add("p-editor-field-container");
		this.getChildren().addAll(labelFieldLayout, lblNameDescription);
	}
}
