package org.praisenter.javafx.slide.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

abstract class EditorRibbonTab<T> extends HBox {

	final ObjectProperty<T> component = new SimpleObjectProperty<T>();
	final BorderPane container;
	
	boolean mutating = false;
	
	public EditorRibbonTab(String name) {
		container = new BorderPane();
		
		// bottom is the label
		Label lblName = new Label(name);
		lblName.setAlignment(Pos.BASELINE_CENTER);
		container.setBottom(lblName);
		lblName.setMaxWidth(Double.MAX_VALUE);
		lblName.setPadding(new Insets(3, 0, 0, 0));
		lblName.setFont(Font.font("System", FontWeight.BOLD, 10));
		
		// right is the separator
		this.getChildren().addAll(container, new Separator(Orientation.VERTICAL));
		this.setPadding(new Insets(3, 0, 3, 0));
		this.setSpacing(4);
		
		this.container.setMinWidth(USE_PREF_SIZE);
	}
	
	public T getComponent() {
		return this.component.get();
	}
	
	public void setComponent(T component) {
		this.component.set(component);
	}
	
	public ObjectProperty<T> componentProperty() {
		return this.component;
	}
}
