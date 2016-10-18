package org.praisenter.javafx;

import javafx.concurrent.Task;

public abstract class MonitoredTask<T> extends Task<T> {
	private final String name;
	private final boolean indeterminant;
	
	public MonitoredTask(String name, boolean indeterminant) {
		this.name = name;
		this.indeterminant = indeterminant;
	}
	
	@Override
	protected void scheduled() {
		super.scheduled();
		updateMessage("Scheduled");
	}
	
	@Override
	protected void running() {
		super.running();
		updateMessage("Running");
	}
	
	@Override
	protected void failed() {
		super.failed();
		updateMessage("Failed");
	}
	
	@Override
	protected void cancelled() {
		super.cancelled();
		updateMessage("Cancelled");
	}
	
	@Override
	protected void succeeded() {
		super.succeeded();
		updateMessage("Succeeded");
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public String getName() {
		return this.name;
	}

	public boolean isIndeterminant() {
		return this.indeterminant;
	}
}
