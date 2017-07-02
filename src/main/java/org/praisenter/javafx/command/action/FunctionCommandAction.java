package org.praisenter.javafx.command.action;

import java.util.function.Consumer;

import org.praisenter.javafx.command.operation.CommandOperation;

public final class FunctionCommandAction<T extends CommandOperation> implements CommandAction<T>{
	private final Consumer<T> undo;
	private final Consumer<T> redo;
	
	public FunctionCommandAction(Consumer<T> action) {
		this(action, action);
	}
	
	public FunctionCommandAction(Consumer<T> undo, Consumer<T> redo) {
		this.undo = undo;
		this.redo = redo;
	}
	
	@Override
	public void undo(T operation) {
		if (this.undo != null) this.undo(operation);
	}
	
	@Override
	public void redo(T operation) {
		if (this.redo != null) this.redo(operation);
	}
}
