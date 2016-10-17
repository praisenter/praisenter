package org.praisenter.javafx;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.controlsfx.control.BreadCrumbBar;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;

public interface NavigationManager {

	public void push(String name, Node node);
	
	public void push(NavigationItem<Node> item);
	
	public NavigationItem<Node> pop();
	
	public NavigationItem<Node> peek();
	
	public void root();
}
