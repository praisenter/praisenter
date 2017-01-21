package org.praisenter.javafx.async;

import java.util.concurrent.ExecutorService;

import javafx.concurrent.Task;

public class EmptyExecutableTask<T> extends ExecutableTask<T> {

	public static final <T> EmptyExecutableTask<T> create() {
		return new EmptyExecutableTask<T>();
	}
	
	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected T call() throws Exception {
		return null;
	}
	
	/**
	 * Executes this task on the given executor service.
	 * @param service the service
	 */
	public void execute(ExecutorService service) {
		
	}
}
