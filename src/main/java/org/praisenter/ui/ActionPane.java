package org.praisenter.ui;

import java.util.concurrent.CompletableFuture;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

public interface ActionPane {
	// copied from Node
	public <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler);
	public <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler);
	
	public ObservableList<?> getSelectedItems();
	
	public boolean isActionEnabled(Action action);
	public boolean isActionVisible(Action action);
	public CompletableFuture<Void> executeAction(Action action);
	
	public void setDefaultFocus();
}
