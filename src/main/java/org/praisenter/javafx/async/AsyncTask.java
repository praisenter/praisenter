package org.praisenter.javafx.async;

import java.util.concurrent.ExecutorService;

import org.praisenter.javafx.utility.Fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public abstract class AsyncTask<TOutput> extends Task<TOutput> {
	/** The task name/description */
	private final String name;
	
	public AsyncTask() {
		this(null);
	}
	
	public AsyncTask(String name) {
		this.name = name;
	}
	
	/**
	 * Executes this task on the given executor service.
	 * @param service the service
	 */
	public void execute(PraisenterThreadPoolExecutor service) {
		service.execute(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	public void addSuccessHandler(EventHandler<WorkerStateEvent> handler) {
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, handler);
	}
	
	public void addCancelledHandler(EventHandler<WorkerStateEvent> handler) {
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, handler);
	}
	
	public void addFailedHandler(EventHandler<WorkerStateEvent> handler) {
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, handler);
	}
	
	public void addCancelledOrFailedHandler(EventHandler<WorkerStateEvent> handler) {
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, handler);
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, handler);
	}
	
	public void addCompletedHandler(EventHandler<WorkerStateEvent> handler) {
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, handler);
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, handler);
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, handler);
	}
	
	/**
	 * Returns the task name/description.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
}
