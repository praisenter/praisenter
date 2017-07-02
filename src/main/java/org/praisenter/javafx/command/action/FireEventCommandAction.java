package org.praisenter.javafx.command.action;

import org.praisenter.javafx.command.operation.CommandOperation;

import javafx.event.Event;
import javafx.scene.Node;

public final class FireEventCommandAction<T extends Node, E extends CommandOperation> extends NodeCommandAction<T, E> {
	private final Event redoEvent;
	private final Event undoEvent;
	
	public FireEventCommandAction(T node, Event event) {
		this(node, event, event);
	}
	
	public FireEventCommandAction(T node, Event redoEvent, Event undoEvent) {
		super(node);
		this.redoEvent = redoEvent;
		this.undoEvent = undoEvent;
	}

	@Override
	public void undo(E operation) {
		this.node.fireEvent(this.undoEvent);
	}
	
	@Override
	public void redo(E operation) {
		this.node.fireEvent(this.redoEvent);
	}
}
