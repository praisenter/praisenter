package org.praisenter.ui.slide.animation;

import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public abstract class CustomTransition extends Transition {
	protected ObjectProperty<Node> node;
	
	public CustomTransition() {
		this.node = new SimpleObjectProperty<Node>();
	}
	
	protected final boolean isInTransition() {
		return this.getRate() > 0;
	}
	
	public Node getNode() {
		return this.node.get();
	}

	public void setNode(Node node) {
		this.node.set(node);
	}
	
	public ObjectProperty<Node> nodeProperty() {
		return this.node;
	}
}
