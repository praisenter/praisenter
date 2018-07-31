package org.praisenter.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.input.KeyCombination;

final class ActionDefinition<T, E> {
	private final Action action;
	private final StringProperty label;
	private final ObjectProperty<Node> graphic;
	private final ObjectProperty<KeyCombination> accelerator;
	
	public ActionDefinition(Action action) {
		this.action = action;
		
		this.label = new SimpleStringProperty();
		this.graphic = new SimpleObjectProperty<>();
		this.accelerator = new SimpleObjectProperty<>();
	}
	
	public Action getAction() {
		return this.action;
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
	
	public Node getGraphic() {
		return this.graphic.get();
	}
	
	public void setGraphic(Node graphic) {
		this.graphic.set(graphic);
	}
	
	public ObjectProperty<Node> graphicProperty() {
		return this.graphic;
	}
	
	public KeyCombination getAccelerator() {
		return this.accelerator.get();
	}
	
	public void setAccelerator(KeyCombination accelerator) {
		this.accelerator.set(accelerator);
	}
	
	public ObjectProperty<KeyCombination> acceleratorProperty() {
		return this.accelerator;
	}
}
