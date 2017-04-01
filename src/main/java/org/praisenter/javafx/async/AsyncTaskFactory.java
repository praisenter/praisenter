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
import java.util.concurrent.Callable;

/**
 * Helper class to create various {@link AsyncTask}s.
 * @author William Bittle
 * @version 3.0.0
 */
public final class AsyncTaskFactory {
	/** Hidden constructor */
	private AsyncTaskFactory() {}
	
	/**
	 * Returns a task that is effectively a no-op.
	 * @return {@link AsyncTask}
	 */
	public static <T> AsyncTask<T> single() {
		return new CallableAsyncTask<T>(() -> { return null; });
	}
	
	/**
	 * Returns a task that executes the given lambda.
	 * @param lambda the code to execute in the task
	 * @return {@link AsyncTask}
	 */
	public static <T> AsyncTask<T> single(Callable<T> lambda) {
		return new CallableAsyncTask<T>(lambda);
	}
	
	/**
	 * Returns a task that executes the given lambda.
	 * @param name the name of the task
	 * @param lambda the code to execute in the task
	 * @return {@link AsyncTask}
	 */
	public static <T> AsyncTask<T> single(String name, Callable<T> lambda) {
		return new CallableAsyncTask<T>(name, lambda);
	}
	
	/**
	 * Returns a group task that is effectively a no-op.
	 * @return {@link AsyncGroupTask}
	 */
	public static <T extends AsyncTask<?>> AsyncGroupTask<T> group() {
		return new AsyncGroupTask<T>(null);
	}
	
	/**
	 * Returns a task that waits on the given set of tasks.
	 * @param tasks the tasks to wait on
	 * @return {@link AsyncGroupTask}
	 */
	public static <T extends AsyncTask<?>> AsyncGroupTask<T> group(List<T> tasks) {
		return new AsyncGroupTask<T>(tasks);
	}
	
	/**
	 * Returns a task that waits on the given set of tasks.
	 * @param name the name of the task
	 * @param tasks the tasks to wait on
	 * @return {@link AsyncGroupTask}
	 */
	public static <T extends AsyncTask<?>> AsyncGroupTask<T> group(String name, List<T> tasks) {
		return new AsyncGroupTask<T>(name, tasks);
	}

	/**
	 * Returns a chain task that is effectively a no-op.
	 * @return {@link AsyncChainedTask}
	 */
	public static <T extends AsyncTask<?>> AsyncChainedTask<T> chain() {
		return new AsyncChainedTask<T>(null);
	}
	
	/**
	 * Returns a task that waits on the given set of tasks to execute sequentially.
	 * @param tasks the tasks to wait on
	 * @return {@link AsyncChainedTask}
	 */
	public static <T extends AsyncTask<?>> AsyncChainedTask<T> chain(List<T> tasks) {
		return new AsyncChainedTask<T>(tasks);
	}
	
	/**
	 * Returns a task that waits on the given set of tasks to execute sequentially.
	 * @param name the name of the task
	 * @param tasks the tasks to wait on
	 * @return {@link AsyncChainedTask}
	 */
	public static <T extends AsyncTask<?>> AsyncChainedTask<T> chain(String name, List<T> tasks) {
		return new AsyncChainedTask<T>(name, tasks);
	}
}
