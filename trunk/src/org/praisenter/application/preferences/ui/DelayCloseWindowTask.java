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
package org.praisenter.application.preferences.ui;

import java.awt.Window;

import javax.swing.SwingUtilities;

/**
 * Class used to close {@link Window}(s) after a given wait period.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class DelayCloseWindowTask implements Runnable {
	/** The wait time */
	private int waitTime;
	
	/** The array of windows */
	private Window[] windows;
	
	/**
	 * Minimal constructor.
	 * @param waitTime the wait time in milliseconds
	 * @param windows the windows to close
	 */
	private DelayCloseWindowTask(int waitTime, Window... windows) {
		this.waitTime = waitTime;
		this.windows = windows;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// just return if we were not passed any windows
		if (this.windows == null || this.windows.length == 0) return;
		
		// wait first
		// dont bother waiting if the wait time is not greater than zero
		if (this.waitTime > 0) {
			try {
				Thread.sleep(this.waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// close the windows on the EDT
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// then close the windows
				for (int i = 0; i < windows.length; i++) {
					windows[i].setVisible(false);
					windows[i].dispose();
				}
			}
		});
	}
	
	/**
	 * Closes the given windows after the given wait time.
	 * @param waitTime the wait time in milliseconds
	 * @param windows the windows to close
	 */
	public static void execute(int waitTime, Window... windows) {
		DelayCloseWindowTask task = new DelayCloseWindowTask(waitTime, windows);
		Thread thread = new Thread(task, "DelayCloseWindowThread");
		thread.setDaemon(true);
		thread.start();
	}
}
