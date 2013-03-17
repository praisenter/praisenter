/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.common.threading;

/**
 * Represents a Thread that runs forever, that can be paused, resumed, and stopped.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class PausableThread extends Thread {
	/** True if this thread is waiting to be unpaused */
	private boolean paused;
	
	/** True if this thread has been stopped */
	private boolean stopped;
	
	/** The lock used to pause this thread */
	private Object pausedLock = new Object();

	/**
	 * Optional constructor.
	 * @param name the thread name
	 */
	public PausableThread(String name) {
		super(name);
		this.initialize();
	}

	/**
	 * Optional constructor.
	 * @param target the code to run
	 * @param name the thread name
	 */
	public PausableThread(Runnable target, String name) {
		super(target, name);
		this.initialize();
	}
	
	/**
	 * Initializes this thread.
	 */
	private void initialize() {
		this.setDaemon(true);
		this.paused = true;
		this.stopped = false;
	}

	/**
	 * Returns true if this thread is paused (and running).
	 * @return boolean
	 */
	public boolean isPaused() {
		return this.paused && this.isAlive();
	}
	
	/**
	 * Returns true if this thread has been stopped.
	 * @return boolean
	 */
	public boolean isStopped() {
		return this.stopped && !this.isAlive();
	}
	
	/**
	 * Pauses execution of this thread.
	 * @param flag true if the thread should be paused
	 */
	public void setPaused(boolean flag) {
		// check if the states are the same
		if (this.paused != flag) {
			this.paused = flag;
			// check if we are pausing the thread
			if (!flag) {
				// we are unpausing the thread
				synchronized (this.pausedLock) {
					this.pausedLock.notify();
				}
			}
		}
	}
	
	/**
	 * Stops execution of this thread.
	 */
	public void end() {
		// set the stopped flag
		this.stopped = true;
		// make sure we are paused
		this.paused = false;
		// notify just in case this thread is waiting
		// to be unpaused
		synchronized (this.pausedLock) {
			this.pausedLock.notify();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// run forever, until stopped
		while (true) {
			// see if we have been stopped
			if (this.stopped) {
				// call the stopped code
				this.onThreadStopped();
				// break from the loop
				break;
			}
			
			// check the paused state
			if (this.paused) {
				// wait on the paused flag
				while (this.paused) {
					synchronized (this.pausedLock) {
						try {
							// call the paused method before waiting
							this.onThreadPaused();
							// wait until we are unpaused
							this.pausedLock.wait();
						} catch (InterruptedException e) {
							// notify of the interrupted event
							this.onInterrupted();
							// make sure we aren't stopped
							if (this.stopped) {
								// break from this loop
								break;
							}
						}
					}
				}
				// check for stopped again
				if (this.stopped) {
					// if we are, then call the stopped method
					this.onThreadStopped();
					break;
				}
			}
			
			// if we aren't stopped or paused, then execute
			// the task (whether it be the given runnable or
			// the overridden executeTask method
			this.executeTask();
			super.run();
		}
	}
	
	/**
	 * Called when this thread is has not been paused and has not been stopped.
	 * <p>
	 * This method will be called in a tight loop with no throttling.  This method should
	 * perform some throttling of it's own to avoid high CPU costs.
	 * <p>
	 * This method is permitted to block for other reasons. However, it is recommended
	 * that the {@link #setPaused(boolean)} and {@link #end()} methods be overridden to allow 
	 * notification of the paused or stopped state changes.
	 */
	protected void executeTask() {}
	
	/**
	 * Called when this thread is interrupted.
	 * <p>
	 * If the thread has been stopped this method will still be called. The thread
	 * will be stopped after this method returns.
	 */
	protected void onInterrupted() {}
	
	/**
	 * Called right before this thread ceases excecution due to being paused.
	 */
	protected void onThreadPaused() {}
	
	/**
	 * Called right before this thread stops execution.
	 */
	protected void onThreadStopped() {}
}