package org.praisenter.javafx.bible;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

abstract class TreeData {
	final StringProperty label = new SimpleStringProperty();
	final ObjectProperty<Node> graphic = new SimpleObjectProperty<Node>();
	
	public abstract void update();
}
