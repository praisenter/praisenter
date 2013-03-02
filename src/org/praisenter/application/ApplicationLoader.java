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
package org.praisenter.application;

import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;

import javax.mail.MessagingException;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.praisenter.application.errors.ui.EnterPasswordDialog;
import org.praisenter.application.errors.ui.ErrorMailer;
import org.praisenter.application.errors.ui.ExceptionDialog;
import org.praisenter.application.icons.Icons;
import org.praisenter.application.preferences.Preferences;
import org.praisenter.application.resources.Messages;
import org.praisenter.common.InitializationException;
import org.praisenter.common.utilities.FontManager;
import org.praisenter.data.ConnectionFactory;
import org.praisenter.data.DataException;
import org.praisenter.data.bible.Bibles;
import org.praisenter.data.errors.ErrorMessage;
import org.praisenter.data.errors.Errors;
import org.praisenter.data.song.Songs;
import org.praisenter.media.MediaLibrary;
import org.praisenter.slide.SlideLibrary;

/**
 * Dialog used to pre-load application resources like fonts.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public final class ApplicationLoader {
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
		this.dialog = new JDialog(null, Messages.getString("dialog.preload.title"), ModalityType.MODELESS);
		this.dialog.setIconImages(Icons.APPLICATION_ICON_LIST);
		// all the user to close it during startup
		this.dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.dialog.setResizable(false);
		
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
				// bring the main app window to the front
				if (praisenter != null) {
					praisenter.toFront();
				}
				// then dispose of the resources
				dialog.dispose();
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
			} catch (InitializationException | DataException ex) {
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
						ex,
						// if we can't get to the database then they
						// won't be able to send the error report
						false);
				// don't continue any further
				System.exit(1);
			}
	
			// send saved errors
			sendSavedErrorReports();
			
			// load all fonts
			preloadFonts();
			
			// load the media library
			loadMediaLibrary();
			
			// load the slide/template library
			loadSlideLibrary();
			
			try {
				// load the main application window
				preloadMainApplicationWindow();
			} catch (Exception ex) {
				// an error occurred trying to build
				// the main application window or the presentation windows
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
		
		// once the tasks are complete then
		// close the modal and resume normal
		// application flow
		close();
	}
	
	/**
	 * Verifies the database connections.
	 * @throws InitializationException thrown if the connection was not able to be initialized
	 * @throws DataException thrown if the connection to the data store(s) could not be made
	 */
	private void verifyDataConnections() throws InitializationException, DataException {
		// set the initial status
		updateProgress(true, 0, Messages.getString("dialog.preload.verifyData"), "");
		
		// initialize the connection to the database
		ConnectionFactory.initialize(Constants.DATABASE_FILE_PATH);
		
		// run a couple queries to ensure the tables exist
		// TODO [LOW] DATA later we may want to verify the structure of the tables
		
		// FIXME setup KJV if no bible present
		
		// check the bible data store
		Bibles.getBibleCount();
		updateProgress(true, 33);
		
		// check the songs data store
		Songs.getSongCount();
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
		// get any stored errors
		List<ErrorMessage> errors = null;
		try {
			errors = Errors.getErrorMessages();
		} catch (DataException e) {
			// just log it
			LOGGER.warn("Failed to get the stored error messages: ", e);
		}
		// make sure we have some errors
		if (errors != null && errors.size() > 0) {
			// if so then see if the reporting is enabled
			if (Preferences.getInstance().getErrorReportingPreferences().isEnabled()) {
				// if so, then ask the user for their smtp password
				String pass = EnterPasswordDialog.show(null);
				// see if they entered a password
				if (pass != null) {
					try {
						ErrorMailer.send(pass, errors.toArray(new ErrorMessage[0]));
						// if the send works, then try to delete the messages
						try {
							Errors.clearErrorMessages();
						} catch (DataException e) {
							// just log it
							LOGGER.warn("Failed to clear the messages that were sent: ", e);
						}
					} catch (MessagingException e) {
						// just log the error and don't delete the messages
						LOGGER.warn("Failed to automatically send error messages: ", e);
					}
				}
			}
		}
		updateProgress(true, 100);
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
	 * Loads the media library.
	 */
	private void loadMediaLibrary() {
		updateProgress(true, Messages.getString("dialog.preload.mediaLibrary.label"));
		MediaLibrary.initialize(Constants.BASE_PATH);
		updateProgress(true, 100);
	}
	
	/**
	 * Loads the slide/template library.
	 */
	private void loadSlideLibrary() {
		updateProgress(true, Messages.getString("dialog.preload.slideLibrary.label"));
		SlideLibrary.initialize(Constants.BASE_PATH);
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
