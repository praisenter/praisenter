package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.SlideRegion;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public abstract class EditorRibbonTab extends HBox {

	final ObjectProperty<ObservableSlideRegion<?>> component = new SimpleObjectProperty<ObservableSlideRegion<?>>();
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
		
		// right is the separator
		this.getChildren().addAll(container, new Separator(Orientation.VERTICAL));
		this.setPadding(new Insets(3));
		this.setSpacing(6);
	}
	
	public ObservableSlideRegion<?> getComponent() {
		return this.component.get();
	}
	
	public void setComponent(ObservableSlideRegion<?> component) {
		this.component.set(component);
	}
	
	public ObjectProperty<ObservableSlideRegion<?>> componentProperty() {
		return this.component;
	}
}
