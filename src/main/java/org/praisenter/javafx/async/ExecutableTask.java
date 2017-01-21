package org.praisenter.javafx.async;

import java.util.concurrent.ExecutorService;

import javafx.concurrent.Task;

public abstract class ExecutableTask<T> extends Task<T> {
	/**
	 * Executes this task on the given executor service.
	 * @param service the service
	 */
	public void execute(ExecutorService service) {
		service.execute(this);
	}
	
}
