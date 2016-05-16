package org.praisenter.javafx;

public class PraisenterThread extends Thread {
	public PraisenterThread(Runnable task) {
		super(task, "PraisenterWorkerThread");
		this.setDaemon(true);
	}
}
