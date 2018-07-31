package org.praisenter.ui;

import java.util.concurrent.CompletableFuture;

import javafx.beans.property.ObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;

public interface ApplicationPane {
	public boolean isActionEnabled(Action action);
	
	public EventHandler<Event> getOnActionStateChanged();
	public ObjectProperty<EventHandler<Event>> onActionStateChangedProperty();
	public void setOnActionStateChanged(EventHandler<Event> handler);
	
	public CompletableFuture<Void> performAction(Action action);
	
	public void setDefaultFocus();
	public void cleanUp();
}
