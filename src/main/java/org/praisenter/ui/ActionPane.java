package org.praisenter.ui;

import java.util.concurrent.CompletableFuture;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

public interface ActionPane {
	public <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler);
	public <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler);
	
	public ObservableList<Object> getSelectedItems();
	
	public boolean isActionEnabled(Action action);
	public boolean isActionVisible(Action action);
	public CompletableFuture<Node> performAction(Action action);
	
	public void setDefaultFocus();
	public void cleanUp();
}
