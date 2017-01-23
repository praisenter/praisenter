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

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Represents a task that waits on a set of other tasks to complete.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 * @param <T> the task type
 */
public class AsyncGroupTask<T extends AsyncTask<?>> extends AsyncTask<List<T>> {
	/** The tasks */
	private final List<T> tasks;
	
	/** the latch for waiting */
	private final CountDownLatch latch;
	
	/**
	 * Minimal constructor.
	 * @param tasks the set of tasks to wait upon
	 */
	public AsyncGroupTask(List<T> tasks) {
		this(null, tasks);
	}
	
	/**
	 * Optional constructor.
	 * @param name the task name
	 * @param tasks the set of tasks to wait upon
	 */
	public AsyncGroupTask(String name, List<T> tasks) {
		super(name);
		this.tasks = tasks;
		this.latch = new CountDownLatch(tasks != null ? tasks.size() : 0);
		
		if (tasks != null) {
			// for each task, make sure we countdown the latch
			// when they finish
			for (AsyncTask<?> t : tasks) {
				t.addCompletedHandler((e) -> {
					latch.countDown();
				});
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected List<T> call() throws Exception {
		try {
			// don't allow this task to return until all the other
			// tasks are complete
			this.latch.await();
			return this.tasks;
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.async.AsyncTask#execute(org.praisenter.javafx.async.AsyncTaskExecutor)
	 */
	@Override
	public void execute(AsyncTaskExecutor service) {
		// execute the sub tasks
		if (this.tasks != null) {
			for (AsyncTask<?> t : this.tasks) {
				service.execute(t);
			}
		}
		// then execute this task
		service.execute(this);
	}
}
