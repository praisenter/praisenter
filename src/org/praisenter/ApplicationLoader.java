package org.praisenter;

import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.praisenter.data.DataException;
import org.praisenter.data.bible.Bibles;
import org.praisenter.data.errors.Errors;
import org.praisenter.data.song.Songs;
import org.praisenter.dialog.ExceptionDialog;
import org.praisenter.resources.Messages;
import org.praisenter.utilities.FontManager;

/**
 * Dialog used to pre-load application resources like fonts.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ApplicationLoader {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(ApplicationLoader.class);
			
	/** The loading dialog */
	private JDialog dialog;
	
	/** Progress bar for loading */
	private JProgressBar barProgress;
	
	/** Label for the current group of resources being loaded */
	private JLabel lblLoading;
	
	/** Label for the specific resource being loaded */
	private JLabel lblLoadingText;
	
	/** The main application window */
	private Praisenter praisenter;
	
	/**
	 * Performs any loading that must be done prior to presenting
	 * the main GUI.
	 */
	protected static final void load() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ApplicationLoader();
			}
		});
	}
	
	/**
	 * Default constructor.
	 */
	private ApplicationLoader() {
		// create a new dialog
		this.dialog = new JDialog(null, Messages.getString("dialog.preload.title"), ModalityType.APPLICATION_MODAL);
		// make sure closing the modal doesn't work (since we can't remove the close button)
		this.dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		// create the progress bar
		this.barProgress = new JProgressBar(0, 100);
		this.barProgress.setStringPainted(true);
		this.barProgress.setMinimumSize(new Dimension(0, 50));
		
		this.lblLoading = new JLabel(Messages.getString("dialog.preload.title"));
		
		// create a label
		this.lblLoadingText = new JLabel(" ");
		this.lblLoadingText.setMinimumSize(new Dimension(0, 30));
		
		// layout the loading
		Container container = this.dialog.getContentPane();
		
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER)
				.addComponent(this.barProgress)
				.addComponent(this.lblLoading)
				.addComponent(this.lblLoadingText));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.barProgress)
				.addComponent(this.lblLoading)
				.addComponent(this.lblLoadingText));
		
		this.praisenter = null;
		
		// set the minimum size (so we can see all of the font)
		this.dialog.setMinimumSize(new Dimension(400, 0));
		
		// size the window
		this.dialog.pack();
		
		// start the background thread
		// this must be started before the modal dialog is
		// set to visible
		this.start();
		
		// make sure we are in the center of the parent window
		this.dialog.setLocationRelativeTo(null);
		
		// show the dialog
		this.dialog.setVisible(true);
	}
	
	/**
	 * Starts a new thread to perform the preloading tasks.
	 */
	private void start() {
		// we need to execute all the preloading on another
		// thread so that we don't block the EDT
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				runTasks();
			}
		}, "PreloadThread");
		// don't block the closing of the app by this thread
		thread.setDaemon(true);
		// start the preloading thread
		thread.start();
	}
	
	/**
	 * Closes this dialog and disposes any temporary resources.
	 * <p>
	 * This method will execute the close command on the EDT at 
	 * some time in the future.
	 */
	private void close() {
		// close the dialog on the EDT
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// close it
				dialog.setVisible(false);
				// then dispose of the resources
				dialog.dispose();
				// bring the main app window to the front
				if (praisenter != null) {
					praisenter.toFront();
				}
			}
		});
	}
	
	/**
	 * Runs all the preloading tasks.
	 */
	private void runTasks() {
		// wait a bit for the dialog to show
		sleep(500);
		
		// perform the tasks
		{
			// verify database connections
			try {
				verifyDataConnections();
			} catch (DataException ex) {
				// we need to stop immediately if we get an
				// error connection to the data stores
				close();
				// log the error
				LOGGER.error(ex);
				// show an error dialog
				ExceptionDialog.show(
						null, 
						Messages.getString("exception.startup.title"), 
						Messages.getString("exception.startup.text"), 
						ex);
				// don't continue any further
				System.exit(1);
			}
	
			// send saved errors
			sendSavedErrorReports();
			
			// load all fonts
			preloadFonts();
			
			// load the main application window
			try {
				preloadMainApplicationWindow();
			} catch (Exception ex) {
				// an error occurred trying to build
				// the main application window
				close();
				// log the error
				LOGGER.error(ex);
				// show an error dialog
				ExceptionDialog.show(
						null, 
						Messages.getString("exception.startup.title"), 
						Messages.getString("exception.startup.text"), 
						ex);
				// don't continue any further
				System.exit(1);
			}
		}
		
		// update the label to show completed
		updateProgress(true, 100, Messages.getString("dialog.preload.complete"), "");
		
		// wait a bit to allow the user to see
		// that the loading has completed
		sleep(500);
		// once the tasks are complete then
		// close the modal and resume normal
		// application flow
		close();
	}
	
	/**
	 * Verifies the database connections.
	 * @throws DataException if the connection to the data store(s) could not be made
	 */
	private void verifyDataConnections() throws DataException {
		// set the initial status
		updateProgress(true, 0, Messages.getString("dialog.preload.verifyData"), "");
		
		// check the bible data store
		Bibles.getBibleCount();
		updateProgress(true, 33);
		
		// check the songs data store
		Songs.getSong(0);
		updateProgress(true, 66);
		
		// check the songs data store
		Errors.getErrorMessageCount();
		updateProgress(true, 100);
	}
	
	/**
	 * Attempts to send any saved error reports.
	 */
	private void sendSavedErrorReports() {
		// set the initial status
		updateProgress(true, 0, Messages.getString("dialog.preload.errorReports"), "");
		Errors.sendErrorMessages();
		updateProgress(true, 100);
	}
	
	/**
	 * Preloads the main application window.
	 * @throws InterruptedException if the thread was interrupted before completing execution
	 * @throws InvocationTargetException if the thread encountered an error
	 */
	private void preloadMainApplicationWindow() throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				lblLoading.setText(Messages.getString("dialog.preload.app"));
				lblLoadingText.setText("");
				barProgress.setValue(0);
				
				// create the main app window
				// needs to be run on the EDT
				praisenter = new Praisenter();
				praisenter.setVisible(true);
				
				barProgress.setValue(100);
			}
		});
	}
	
	/**
	 * Method to preload the fonts.
	 * <p>
	 * Getting the fonts or font family names from the graphics environment does not load everything.
	 * This causes a slow down in the first window that displays the fonts.  To pre-load the fonts the
	 * font must be placed on a visible component and rendered.  This method will set the font of the
	 * {@link #lblLoadingText} label to each font on the system and will wait for the label to be
	 * repainted before continuing to the next font.
	 */
	private void preloadFonts() {
		updateProgress(true, MessageFormat.format(Messages.getString("dialog.preload.fonts.label"), FontManager.getFontFamilyNames().length));
		// this will load the font family names
		String[] families = FontManager.getFontFamilyNames();
		// get the default label font size
		Font defaultFont = FontManager.getDefaultFont();
		int size = defaultFont.getSize();
		// loop over the fonts and create them
		double max = families.length;
		double cur = 0.0;
		for (String family : families) {
			// get the font from the font manager
			Font font = FontManager.getFont(family, Font.PLAIN, size);
			cur++;
			// update the progress bar on the EDT
			updateProgress(true, (int)Math.floor(cur / max * 100), font);
		}
		updateProgress(true, 100, FontManager.getDefaultFont());
	}
	
	/**
	 * Sleeps the current thread for the given number of milliseconds.
	 * <p>
	 * Eats the exception if one occurs.
	 * @param millis the number of milliseconds to sleep
	 */
	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
			// just eat the exception if we get one
		} catch (InterruptedException e) {}
	}
	
	/**
	 * Updates the progress bar to 0 complete using the given task name
	 * as the new task (clearing the sub task).
	 * @param wait true to wait until the task finishes
	 * @param taskName the task name
	 */
	private void updateProgress(boolean wait, String taskName) {
		ProgressUpdate update = new ProgressUpdate(wait, 0, taskName, "");
		update.begin();
	}
	
	/**
	 * Updates the progress bar percent complete.  The current task names
	 * are retained.
	 * @param wait true to wait until the task finishes
	 * @param value the percent complete in the range [0, 100]
	 */
	private void updateProgress(boolean wait, int value) {
		ProgressUpdate update = new ProgressUpdate(wait, value, null, null);
		update.begin();
	}
	
	/**
	 * Updates the progress bar percent complete and sub task name.  The main
	 * task name is retained.
	 * @param wait true to wait until the task finishes
	 * @param value the percent complete in the range [0, 100]
	 * @param subTaskName the sub task name
	 */
	@SuppressWarnings("unused")
	private void updateProgress(boolean wait, int value, String subTaskName) {
		ProgressUpdate update = new ProgressUpdate(wait, value, null, subTaskName);
		update.begin();
	}
	
	/**
	 * Updates the progress bar percent complete, task name, and sub task name.
	 * @param wait true to wait until the task finishes
	 * @param value the percent complete in the range [0, 100]
	 * @param taskName the task name
	 * @param subTaskName the sub task name
	 */
	private void updateProgress(boolean wait, int value, String taskName, String subTaskName) {
		ProgressUpdate update = new ProgressUpdate(wait, value, taskName, subTaskName);
		update.begin();
	}
	
	/**
	 * Updates the progress bar percent complete and sub task name with the name of
	 * the given font.
	 * @param wait true to wait until the task finishes
	 * @param value the percent complete in the range [0, 100]
	 * @param font the font to use
	 */
	private void updateProgress(boolean wait, int value, Font font) {
		FontProgressUpdate update = new FontProgressUpdate(wait, value, font);
		update.begin();
	}
	
	/**
	 * Task used to update the progress bar and labels during loading.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class ProgressUpdate implements Runnable {
		/** True if the current thread should wait for this task to finish */
		protected boolean wait;
		
		/** The percent complete in the range [0, 100] */
		protected int value;
		
		/** The task name */
		protected String taskName;
		
		/** The sub task name */
		protected String subTaskName;
		
		/**
		 * Minimal constructor.
		 * @param wait true if the current thread should wait for this task to finish
		 * @param value the percent complete in the range of [0, 100]
		 * @param taskName the task name
		 * @param subTaskName the sub task name
		 */
		public ProgressUpdate(boolean wait, int value, String taskName, String subTaskName) {
			this.wait = wait;
			this.value = value;
			this.taskName = taskName;
			this.subTaskName = subTaskName;
		}
		
		/**
		 * Starts the update task.
		 */
		public void begin() {
			try {
				// see if we should wait or not
				if (this.wait) {
					SwingUtilities.invokeAndWait(this);
				} else {
					SwingUtilities.invokeLater(this);
				}
			// eat any exceptions
			} catch (Exception e) {}
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// set the progress bar value
			barProgress.setValue(this.value);
			// set the task name
			if (this.taskName != null) lblLoading.setText(this.taskName);
			// set the sub task name
			if (this.subTaskName != null) lblLoadingText.setText(this.subTaskName);
		}
	}
	
	/**
	 * Special update task for font loading.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class FontProgressUpdate extends ProgressUpdate {
		/** The font */
		protected Font font;
		
		/**
		 * Minimal constructor.
		 * @param wait true if the current thread should wait for this task to finish
		 * @param value the percent complete in the range of [0, 100]
		 * @param font the font
		 */
		public FontProgressUpdate(boolean wait, int value, Font font) {
			super(wait, value, null, null);
			this.font = font;
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.ApplicationLoader.ProgressUpdate#run()
		 */
		@Override
		public void run() {
			// set the progress bar's value
			barProgress.setValue(this.value);
			// get the family name
			String text = this.font.getFamily();
			// make sure the font can actually display the text
			if (this.font.canDisplayUpTo(text) < 0) {
				// set the font
				lblLoadingText.setFont(this.font);
			} else {
				lblLoadingText.setFont(FontManager.getDefaultFont());
			}
			// set the text to the font family name
			lblLoadingText.setText(text);
		}
	}
}
