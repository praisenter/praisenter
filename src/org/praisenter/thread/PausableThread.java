package org.praisenter.thread;

/**
 * Represents a Thread that runs forever, that can be paused, resumed, and stopped.
 * @author USWIBIT
 *
 */
public class PausableThread extends Thread {
	private boolean paused;
	private boolean stop;
	private boolean running;
	
	private Object pausedLock = new Object();
	private Object runningLock = new Object();
	private Object stoppingLock = new Object();

	public PausableThread(String name) {
		super(name);
		this.initialize();
	}

	public PausableThread(Runnable target, String name) {
		super(target, name);
		this.initialize();
	}
	
	private void initialize() {
		this.setDaemon(true);
		this.paused = true;
		this.stop = false;
		this.running = false;
	}

	public boolean isPaused() {
		return this.paused && !this.running;
	}
	
	public boolean isRunning() {
		return !this.paused && this.running; 
	}
	
	/**
	 * Pauses execution of this thread.
	 * <p>
	 * This method will block until the thread is paused.
	 * @param flag true if the thread should be paused
	 */
	public void setPaused(boolean flag) {
		// check if the states are the same
		if (this.paused != flag) {
			this.paused = flag;
			// check if we are pausing the thread
			if (flag) {
				// we are pausing the thread
				// so wait until the running state is changed
				while (running) {
					synchronized (this.runningLock) {
						try {
							this.runningLock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} else {
				// we are unpausing the thread
				synchronized (this.pausedLock) {
					this.pausedLock.notify();
				}
			}
		}
	}
	
	/**
	 * Stops execution of this thread.
	 * <p>
	 * This method will block until the thread is stopped.
	 */
	public void end() {
		this.stop = true;
		while (this.isPaused() || this.isRunning()) {
			synchronized (this.stoppingLock) {
				try {
					this.stoppingLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void run() {
		this.running = true;
		
		while (true) {
			// see if we have been ended
			if (this.stop) {
				break;
			}
			
			// first check the paused state
			if (this.paused) {
				// set the running flag
				this.running = false;
				// notify that we paused
				synchronized (this.runningLock) {
					this.runningLock.notify();
				}
				// wait on the paused flag
				while (this.paused) {
					synchronized (this.pausedLock) {
						try {
							this.pausedLock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
			this.executeTask();
			super.run();
		}
		
		this.paused = false;
		this.running = false;
		
		// notify that we have stopped
		synchronized (this.stoppingLock) {
			this.stoppingLock.notify();
		}
	}
	
	protected void executeTask() {
		
	}
}
