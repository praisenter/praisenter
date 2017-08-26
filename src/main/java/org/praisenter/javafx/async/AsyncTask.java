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

import java.util.function.Function;

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
	 * @return {@link AsyncTask}&lt;T&gt;
	 */
	public AsyncTask<T> execute(AsyncTaskExecutor service) {
		service.execute(this);
		return this;
	}
	
	/**
	 * Performs a mapping of this asynchronous task to another.
	 * <p>
	 * NOTE: The given mapping function will be executed on the Java FX UI thread so
	 * access to the task's state is available along with the value and exception.  This
	 * also means that the mapping function should be very simple and should not contain
	 * long running code.
	 * @param func the operation to perform the mapping
	 * @return {@link AsyncTask}&lt;E&gt;
	 */
	public <E> AsyncTask<E> map(Function<AsyncTask<T>, E> func) {
		// to make this work, we need to have the given func run on the Java FX thread
		// so that it can access this task's state and other properties. to do this
		// we need to have the returned task execute the original task, then on the
		// completion handler, run the mapping function since that will execute on the
		// Java FX thread. Then we execute the mapped task which simply returns the 
		// result from the function
		AsyncTask<T> self = this;
		AsyncTask<E> nt = new AsyncTask<E>() {
			E result = null;
			@Override
			protected E call() throws Exception {
				return result;
			}
			@Override
			public AsyncTask<E> execute(AsyncTaskExecutor service) {
				self.addCompletedHandler(e -> {
					result = func.apply(self);
					super.execute(service);
				});
				self.execute(service);
				return this;
			}
		};
		return nt;
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
