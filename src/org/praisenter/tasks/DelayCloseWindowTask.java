package org.praisenter.tasks;

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
