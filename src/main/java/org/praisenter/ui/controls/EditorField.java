package org.praisenter.ui.controls;

import atlantafx.base.theme.Styles;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
	
	private final StringProperty label;
	private final StringProperty description;
	private final ObjectProperty<Region> field;
	
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
		this.label = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(description);
		this.field = new SimpleObjectProperty<>(field);
		
		Label lblName = new Label();
		lblName.textProperty().bind(this.label);
		lblName.getStyleClass().add("p-editor-field-label");
		lblName.setMinWidth(0);
		lblName.visibleProperty().bind(this.label.isNotNull());
		lblName.managedProperty().bind(lblName.visibleProperty());
		
		Label lblDescription = new Label();
		lblDescription.textProperty().bind(this.description);
		lblDescription.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_SMALL);
		lblDescription.setWrapText(true);
		lblDescription.setMinWidth(0);
		lblDescription.getStyleClass().add("p-editor-field-description");
		lblDescription.visibleProperty().bind(this.description.isNotNull());
		lblDescription.managedProperty().bind(lblDescription.visibleProperty());
		
		Node labelFieldLayout = null;
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
			labelFieldLayout = new VBox(lblName, field);
		}
		
		labelFieldLayout.getStyleClass().add("p-editor-field");
		
		this.getStyleClass().add("p-editor-field-container");
		this.getChildren().addAll(labelFieldLayout, lblDescription);
	}
	
	public String getLabel() {
		return this.label.get();
	}
	
	public void setLabel(String label) {
		this.label.set(label);
	}
	
	public StringProperty labelProperty() {
		return this.label;
	}
	
	public String getDescription() {
		return this.description.get();
	}
	
	public void setDescription(String description) {
		this.description.set(description);
	}
	
	public StringProperty descriptionProperty() {
		return this.description;
	}
	
	public Region getField() {
		return this.field.get();
	}
	
	public void setRegion(Region field) {
		this.field.set(field);
	}
	
	public ObjectProperty<Region> fieldProperty() {
		return this.field;
	}
}
