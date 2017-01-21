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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.praisenter.javafx.utility.Fx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;

/**
 * Represents a thread pool whose tasks can be monitored.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class PraisenterThreadPoolExecutor extends ThreadPoolExecutor {
	/** Keep the last X number of task results around */
	private static final int MAXIMUM_TASK_LIST_LENGTH = 25;
	
	/** The number of running tasks */
	private final IntegerProperty running = new SimpleIntegerProperty(0);
	
	/** True if there is currently a task running */
	private final BooleanProperty isRunning = new SimpleBooleanProperty(false);
	
	/** The tasks */
	private final ObservableList<PraisenterTask<?, ?>> tasks = FXCollections.observableList(new LinkedList<>());
	
	/**
	 * Constructor.
	 */
	public PraisenterThreadPoolExecutor() {
		super(2, 
			10, 
			1, 
			TimeUnit.MINUTES, 
			new LinkedBlockingQueue<Runnable>(), 
			new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread thread = new Thread(r);
					thread.setName("PraisenterWorkerThread");
					thread.setDaemon(true);
					return thread;
				}
		});
		
		isRunning.bind(running.greaterThan(0));
	}
	
	/**
	 * Executes the given task.
	 * @param task the task
	 */
	public void execute(PraisenterTask<?, ?> task) {
		super.execute(task);
		this.updateTaskList(task);
	}
	
	/**
	 * Adds the given task to the tasks list and trims it
	 * to fit within the maximum number of tasks.
	 * @param task
	 */
	private void updateTaskList(PraisenterTask<?, ?> task) {
		// make sure this runs on the FX thread
		Fx.runOnFxThead(() -> {
			// add the task
			tasks.add(0, task);
			
			// count the number of tasks running
			task.stateProperty().addListener((obs, ov, nv) -> {
				if (nv == Worker.State.RUNNING) {
					running.set(running.get() + 1);
				} else if (nv == Worker.State.FAILED ||
						   nv == Worker.State.CANCELLED ||
						   nv == Worker.State.SUCCEEDED) {
					running.set(running.get() - 1);
				}
			});
			
			// trim the list of completed tasks starting
			// from the head of the queue
			if (tasks.size() > MAXIMUM_TASK_LIST_LENGTH) {
				Iterator<PraisenterTask<?, ?>> it = tasks.iterator();
				while (it.hasNext()) {
					PraisenterTask<?, ?> t = it.next();
					// check if its done
					if (t.isDone()) {
						// if so remove it
						it.remove();
					}
					// check if we are under or at the maximum
					if (tasks.size() <= MAXIMUM_TASK_LIST_LENGTH) {
						// if so, break out
						break;
					}
				}
			}
		});
	}
	
	/**
	 * Returns the isRunning property.
	 * @return ReadOnlyBooleanProperty
	 */
	public ReadOnlyBooleanProperty isRunningProperty() {
		return this.isRunning;
	}
	
	/**
	 * Returns the running property.
	 * @return ReadOnlyIntegerProperty
	 */
	public ReadOnlyIntegerProperty runningProperty() {
		return this.running;
	}
	
	/**
	 * Returns a readonly list of tasks ordered by their execution.
	 * @return ObservableList&lt;{@link PraisenterTask}&gt;
	 */
	public ObservableList<PraisenterTask<?, ?>> tasksProperty() {
		return FXCollections.unmodifiableObservableList(this.tasks);
	}
}
