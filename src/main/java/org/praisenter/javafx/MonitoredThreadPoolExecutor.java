package org.praisenter.javafx;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.praisenter.javafx.utility.Fx;

import com.sun.corba.se.spi.orbutil.threadpool.Work;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;

public final class MonitoredThreadPoolExecutor extends ThreadPoolExecutor {
	private static final int MAXIMUM_TASK_LIST_LENGTH = 100;
	
	private final IntegerProperty running = new SimpleIntegerProperty(0);
	private final BooleanProperty isRunning = new SimpleBooleanProperty(false);
	private final ObservableList<MonitoredTask<?>> tasks = FXCollections.observableList(new LinkedList<>());
	
	public MonitoredThreadPoolExecutor() {
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
	
	public void submit(MonitoredTask<?> task) {
		super.submit(task);
		this.updateTaskList(task);
	}
	
	public void execute(MonitoredTask<?> task) {
		super.execute(task);
		this.updateTaskList(task);
	}
	
	private void updateTaskList(MonitoredTask<?> task) {
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
				Iterator<MonitoredTask<?>> it = tasks.iterator();
				while (it.hasNext()) {
					MonitoredTask<?> t = it.next();
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
	
	public ReadOnlyBooleanProperty isRunningProperty() {
		return this.isRunning;
	}
	
	public ReadOnlyIntegerProperty runningProperty() {
		return this.running;
	}
	
	/**
	 * Returns a readonly list of tasks ordered by their execution.
	 * @return
	 */
	public ObservableList<MonitoredTask<?>> tasksProperty() {
		return FXCollections.unmodifiableObservableList(this.tasks);
	}
}
