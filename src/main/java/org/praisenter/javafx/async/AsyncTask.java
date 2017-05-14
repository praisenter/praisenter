/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.javafx.async;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * Represents a named asynchronous task with various helpers.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> the task result type
 */
public abstract class AsyncTask<T> extends Task<T> {
	/** The task name/description */
	private final String name;
	
	/**
	 * Default constructor.
	 */
	public AsyncTask() {
		this(null);
	}
	
	/**
	 * Optional constructor.
	 * @param name the task name
	 */
	public AsyncTask(String name) {
		this.name = name;
	}
	
	/**
	 * Executes this task on the given executor service.
	 * @param service the service
	 */
	public void execute(AsyncTaskExecutor service) {
		service.execute(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/**
	 * Helper method for adding a success handler.
	 * @param handler the handler
	 * @return {@link AsyncTask}&lt;T&gt;
	 */
	public final AsyncTask<T> addSuccessHandler(EventHandler<WorkerStateEvent> handler) {
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, handler);
		return this;
	}
	
	/**
	 * Helper method for adding a cancelled handler.
	 * @param handler the handler
	 * @return {@link AsyncTask}&lt;T&gt;
	 */
	public final AsyncTask<T> addCancelledHandler(EventHandler<WorkerStateEvent> handler) {
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, handler);
		return this;
	}
	
	/**
	 * Helper method for adding a failed handler.
	 * @param handler the handler
	 * @return {@link AsyncTask}&lt;T&gt;
	 */
	public final AsyncTask<T> addFailedHandler(EventHandler<WorkerStateEvent> handler) {
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, handler);
		return this;
	}
	
	/**
	 * Helper method for adding a handler that gets called if the task is cancelled
	 * or if the task fails.
	 * @param handler the handler
	 * @return {@link AsyncTask}&lt;T&gt;
	 */
	public final AsyncTask<T> addCancelledOrFailedHandler(EventHandler<WorkerStateEvent> handler) {
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, handler);
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, handler);
		return this;
	}
	
	/**
	 * Helper method for adding a handler that gets called if the task is cancelled,
	 * has failed, or has succeeded.
	 * @param handler the handler
	 * @return {@link AsyncTask}&lt;T&gt;
	 */
	public final AsyncTask<T> addCompletedHandler(EventHandler<WorkerStateEvent> handler) {
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, handler);
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, handler);
		this.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, handler);
		return this;
	}
	
	/**
	 * Returns the task name/description.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
}
