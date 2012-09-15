package org.praisenter;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * Dialog used to show progress on a task.
 * <p>
 * The task is assumed to be indeterminate.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class TaskProgressDialog extends JDialog {
	/** The version id */
	private static final long serialVersionUID = 2197924276203282851L;

	/** Progress bar for loading */
	private JProgressBar barProgress;
	
	/** The task */
	private Runnable runnable;
	
	/**
	 * Shows an application modal dialog box that cannot be closed, 
	 * blocks the current application, and pre-loads resources.
	 * @param owner the owner of the dialog
	 * @param taskName the task name
	 * @param runnable the task
	 */
	public static final void show(Window owner, String taskName, Runnable runnable) {
		// creating an application modal dialog will block the
		// application but not the EDT
		new TaskProgressDialog(owner, taskName, runnable);
	}
	
	/**
	 * Minimal constructor.
	 * @param owner the owner of this dialog
	 * @param taskName the task name
	 * @param runnable the task
	 */
	private TaskProgressDialog(Window owner, String taskName, Runnable runnable) {
		super(owner, taskName, ModalityType.APPLICATION_MODAL);
		// make sure closing the modal doesn't work (since we can't remove the close button)
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.runnable = runnable;
		
		// create the progress bar
		this.barProgress = new JProgressBar(0, 100);
		this.barProgress.setMinimumSize(new Dimension(200, 50));
		
		// layout the loading
		Container container = this.getContentPane();
		
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER)
				.addComponent(this.barProgress));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.barProgress));
		
		// size the window
		this.pack();
		
		// start the background thread
		this.start();
		
		// make sure we are in the center of the parent window
		this.setLocationRelativeTo(owner);
		
		// show the dialog
		this.setVisible(true);
	}
	
	/**
	 * Starts a new thread to perform the task.
	 */
	private void start() {
		// we need to execute the task on another
		// thread so that we don't block the EDT
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// update the progress bar
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							barProgress.setIndeterminate(true);
						}
					});
				// just eat the exceptions
				} catch (Exception e) {}
				
				// begin the tasks
				runnable.run();
				
				// wait a bit to allow the user to see
				// that the task has completed
				try {
					Thread.sleep(500);
					// just eat the exception if we get one
				} catch (InterruptedException e) {}
				
				// once the task is complete then
				// close the modal and resume normal
				// application flow
				close();
			}
		}, "FileImportThread");
		// don't block the closing of the app by this thread
		thread.setDaemon(true);
		// start the task thread
		thread.start();
	}
	
	/**
	 * Closes this dialog and disposes any temporary resources.
	 * <p>
	 * This method will execute the close command on the EDT at 
	 * some time in the future.
	 */
	private void close() {
		// close the dialog later
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// close it
				TaskProgressDialog.this.setVisible(false);
				// then dispose of the resources
				TaskProgressDialog.this.dispose();
			}
		});
	}
}
