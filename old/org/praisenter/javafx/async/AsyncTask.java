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

import java.util.function.Consumer;
import java.util.function.Function;

import org.praisenter.ThrowableFunction;

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
	 * Executes the given operation on the Java FX UI thread using the result from this {@link AsyncTask}.
	 * @param operation the operation
	 * @return {@link AsyncTask}
	 */
	public <E> AsyncTask<E> thenOnJavaFXThread(ThrowableFunction<T, E> operation) {
		return this.then(operation, false);
	}
	
	/**
	 * Executes the given operation on a background thread using the result from this {@link AsyncTask}.
	 * @param operation the operation
	 * @return {@link AsyncTask}
	 */
	public <E> AsyncTask<E> thenOnBackgroundThread(ThrowableFunction<T, E> operation) {
		return this.then(operation, true);
	}
	
	/**
	 * Executes the given task after this task has completed.
	 * @param task the task
	 * @return {@link AsyncTask}
	 */
	public <E> AsyncTask<E> then(AsyncTask<E> task) {
		AsyncTask<T> source = this;
		return new AsyncTask<E>() {
			// chain state
			private Throwable sourceException;
			private boolean sourceIsCancelled;
			private E result;
			
			@Override
			protected E call() throws Exception {
				// handle input exceptions
				Throwable ex = this.sourceException;
				if (ex != null) {
					if (ex instanceof Exception) {
						throw (Exception)ex;
					} else if (ex instanceof Error) {
						throw (Error)ex;
					} else {
						throw new Exception(ex.getMessage(), ex);
					}
				}
				
				// handle input cancellation
				if (this.sourceIsCancelled) {
					this.cancel();
				}
				
				return this.result;
			}
			
			@Override
			public AsyncTask<E> execute(AsyncTaskExecutor service) {
				source.addCompletedHandler(e -> {
					if (e.getEventType() == WorkerStateEvent.WORKER_STATE_FAILED) {
						sourceException = source.getException();
					} else if (e.getEventType() == WorkerStateEvent.WORKER_STATE_CANCELLED) {
						sourceIsCancelled = true;
					} else {
						task.addCompletedHandler(se -> {
							if (se.getEventType() == WorkerStateEvent.WORKER_STATE_FAILED) {
								sourceException = task.getException();
							} else if (se.getEventType() == WorkerStateEvent.WORKER_STATE_CANCELLED) {
								sourceIsCancelled = true;
							} else {
								result = task.getValue();
							}
							super.execute(service);
						});
						task.execute(service);
						return;
					}
					
					super.execute(service);
				});
				source.execute(service);
				return this;
			}
		};
	}
	
	/**
	 * Executes the given operation on the Java FX UI thread to produce a new task. The task returned
	 * by the given operation is then run on a background thread.
	 * @param operation the operation to produce a task
	 * @return {@link AsyncTask}
	 */
	public <E> AsyncTask<E> then(Function<T, AsyncTask<E>> operation) {
		AsyncTask<T> source = this;
		return new AsyncTask<E>() {
			// chain state
			private Throwable sourceException;
			private boolean sourceIsCancelled;
			private AsyncTask<E> task;
			private E result;
			
			@Override
			protected E call() throws Exception {
				// handle input exceptions
				Throwable ex = this.sourceException;
				if (ex != null) {
					if (ex instanceof Exception) {
						throw (Exception)ex;
					} else if (ex instanceof Error) {
						throw (Error)ex;
					} else {
						throw new Exception(ex.getMessage(), ex);
					}
				}
				
				// handle input cancellation
				if (this.sourceIsCancelled) {
					this.cancel();
				}
				
				return this.result;
			}
			
			@Override
			public AsyncTask<E> execute(AsyncTaskExecutor service) {
				source.addCompletedHandler(e -> {
					if (e.getEventType() == WorkerStateEvent.WORKER_STATE_FAILED) {
						sourceException = source.getException();
					} else if (e.getEventType() == WorkerStateEvent.WORKER_STATE_CANCELLED) {
						sourceIsCancelled = true;
					} else {
						T sourceResult = source.getValue();
						task = operation.apply(sourceResult);
						task.addCompletedHandler(se -> {
							if (se.getEventType() == WorkerStateEvent.WORKER_STATE_FAILED) {
								sourceException = task.getException();
							} else if (se.getEventType() == WorkerStateEvent.WORKER_STATE_CANCELLED) {
								sourceIsCancelled = true;
							} else {
								result = task.getValue();
							}
							super.execute(service);
						});
						task.execute(service);
						return;
					}

					super.execute(service);
				});
				source.execute(service);
				return this;
			}
		};
	}
	
	/**
	 * Captures any throwable and executes the given operation on the Java FX UI thread.
	 * @param operation the operation
	 * @return {@link AsyncTask}
	 */
	public AsyncTask<T> errorOnJavaFXThread(Consumer<Throwable> operation) {
		return this.error(operation, false);
	}
	
	/**
	 * Captures any throwable and executes the given operation on a background thread.
	 * @param operation the operation
	 * @return {@link AsyncTask}
	 */
	public AsyncTask<T> errorOnBackgroundThread(Consumer<Throwable> operation) {
		return this.error(operation, false);
	}
	
	/**
	 * Wraps this task in a named task that waits for this task to complete. 
	 * @param name the name
	 * @return {@link AsyncTask}
	 */
	public AsyncTask<T> wrap(String name) {
		return AsyncTaskFactory.group(name, this).thenOnJavaFXThread((tasks) -> {
			return this.getValue();
		});
	}

	// internal
	
	private <E> AsyncTask<E> then(ThrowableFunction<T, E> operation, boolean background) {
		AsyncTask<T> source = this;
		return new AsyncTask<E>() {
			// chain state
			private T sourceResult;
			private Throwable sourceException;
			private boolean sourceIsCancelled;
			private E result;
			
			@Override
			protected E call() throws Exception {
				// handle input exceptions
				Throwable ex = this.sourceException;
				if (ex != null) {
					if (ex instanceof Exception) {
						throw (Exception)ex;
					} else if (ex instanceof Error) {
						throw (Error)ex;
					} else {
						throw new Exception(ex.getMessage(), ex);
					}
				}
				
				// handle input cancellation
				if (this.sourceIsCancelled) {
					this.cancel();
				}

				// execute in the background?
				if (background) {
					// otherwise perform the next operation
					this.result = operation.apply(this.sourceResult);
				}
				
				return this.result;
			}
			
			@Override
			public AsyncTask<E> execute(AsyncTaskExecutor service) {
				source.addCompletedHandler(e -> {
					if (e.getEventType() == WorkerStateEvent.WORKER_STATE_FAILED) {
						sourceException = source.getException();
					} else if (e.getEventType() == WorkerStateEvent.WORKER_STATE_CANCELLED) {
						sourceIsCancelled = true;
					} else {
						sourceResult = source.getValue();
						// execute on the UI thread?
						if (!background) {
							try {
								result = operation.apply(sourceResult);
							} catch (Exception ex) {
								sourceException = ex;
							}
						}
					}
					super.execute(service);
				});
				source.execute(service);
				return this;
			}
		};
	}
	
	private AsyncTask<T> error(Consumer<Throwable> operation, boolean background) {
		AsyncTask<T> source = this;
		return new AsyncTask<T>() {
			// chain state
			private T sourceResult;
			private Throwable sourceException;
			private boolean sourceIsCancelled;
			
			@Override
			protected T call() throws Exception {
				// handle input exceptions
				Throwable ex = this.sourceException;
				if (ex != null) {
					if (background) {
						operation.accept(ex);
					}
				}
				
				// handle input cancellation
				if (this.sourceIsCancelled) {
					this.cancel();
				}

				return this.sourceResult;
			}
			
			@Override
			public AsyncTask<T> execute(AsyncTaskExecutor service) {
				source.addCompletedHandler(e -> {
					if (e.getEventType() == WorkerStateEvent.WORKER_STATE_FAILED) {
						sourceException = source.getException();
						if (!background) {
							operation.accept(sourceException);
						}
					} else if (e.getEventType() == WorkerStateEvent.WORKER_STATE_CANCELLED) {
						sourceIsCancelled = true;
					} else {
						sourceResult = source.getValue();
					}
					super.execute(service);
				});
				source.execute(service);
				return this;
			}
		};
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
