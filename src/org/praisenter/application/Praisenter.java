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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;
import org.praisenter.application.bible.ui.BibleLibraryDialog;
import org.praisenter.application.bible.ui.BiblePanel;
import org.praisenter.application.errors.ui.ExceptionDialog;
import org.praisenter.application.icons.Icons;
import org.praisenter.application.media.ui.MediaLibraryDialog;
import org.praisenter.application.notification.ui.NotificationPanel;
import org.praisenter.application.preferences.ui.PreferencesDialog;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.slide.ui.SlideLibraryDialog;
import org.praisenter.application.slide.ui.SlidePanel;
import org.praisenter.application.song.ui.SongLibraryDialog;
import org.praisenter.application.song.ui.SongsPanel;
import org.praisenter.application.ui.AboutDialog;
import org.praisenter.application.ui.SystemDialog;
import org.praisenter.application.ui.TaskProgressDialog;
import org.praisenter.application.ui.ValidateFileChooser;
import org.praisenter.common.threading.AbstractTask;
import org.praisenter.common.xml.XmlIO;
import org.praisenter.data.DataException;
import org.praisenter.data.errors.ErrorMessage;
import org.praisenter.data.errors.Errors;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongImporter;
import org.praisenter.data.song.SongList;
import org.praisenter.data.song.Songs;
import org.praisenter.presentation.PresentationManager;

/**
 * Main window for the Praisenter application.
 * @author William Bittle
 * @version 2.0.1
 * @since 1.0.0
 */
public class Praisenter extends JFrame implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 4204856340044399264L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(Praisenter.class);
	
	// menu
	
	/** Menu bar look and feel item */
	private JMenu mnuLookAndFeel;
	
	// main panels
	
	/** The slide panel */
	private SlidePanel pnlSlides;
	
	/** The bible panel */
	private BiblePanel pnlBible;
	
	/** The notification panel */
	private NotificationPanel pnlNotification;
	
	/** The song panel */
	private SongsPanel pnlSongs;
	
	/**
	 * Default constructor.
	 */
	public Praisenter() {
		super(Messages.getString("praisenter"));
		this.setIconImages(Icons.APPLICATION_ICON_LIST);
		
		// we may end up doing this later down the road
		//this.setMinimumSize(new Dimension(860, 680));
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		
		JTabbedPane tabs = new JTabbedPane();
		
		// create the bible panel
		this.pnlBible = new BiblePanel();
		tabs.addTab(Messages.getString("bible"), this.pnlBible);
		
		// create the songs panel
		this.pnlSongs = new SongsPanel();
		tabs.addTab(Messages.getString("songs"), this.pnlSongs);
		
		// create the slide panel
		this.pnlSlides = new SlidePanel();
		tabs.addTab(Messages.getString("slides"), this.pnlSlides);
		
		// create the notification panel
		this.pnlNotification = new NotificationPanel();
		
		container.add(this.pnlNotification, BorderLayout.PAGE_START);
		container.add(tabs, BorderLayout.CENTER);
		
		// create the main menu bar
		{
			JMenuBar barMenu = new JMenuBar();
			
			// file menu
			JMenu mnuFile = new JMenu(Messages.getString("menu.file"));
			barMenu.add(mnuFile);
			{
				// file->preferences menu
				JMenuItem mnuPreferences = new JMenuItem(Messages.getString("menu.file.preferences"));
				mnuPreferences.setActionCommand("preferences");
				mnuPreferences.addActionListener(this);
				mnuFile.add(mnuPreferences);
	
				mnuFile.addSeparator();
				
				// file->export menu
				{
					JMenu mnuExport = new JMenu(Messages.getString("menu.file.export"));
					mnuFile.add(mnuExport);
					
					JMenuItem mnuExportErrors = new JMenuItem(Messages.getString("menu.file.export.errors"));
					mnuExportErrors.setActionCommand("exportErrors");
					mnuExportErrors.addActionListener(this);
					mnuExport.add(mnuExportErrors);
					
					JMenuItem mnuExportSongs = new JMenuItem(Messages.getString("menu.file.export.songs"));
					mnuExportSongs.setActionCommand("exportSongs");
					mnuExportSongs.addActionListener(this);
					mnuExport.add(mnuExportSongs);
				}
	
				// file->import menu
				{
					JMenu mnuImport = new JMenu(Messages.getString("menu.file.import"));
					mnuFile.add(mnuImport);
					
					JMenu mnuImportSongs = new JMenu(Messages.getString("menu.file.import.songs"));
					mnuImport.add(mnuImportSongs);
					
					JMenuItem mnuImportPraisenter = new JMenuItem(Messages.getString("menu.file.import.songs.praisenter"));
					mnuImportPraisenter.setActionCommand("importPraisenterSongs");
					mnuImportPraisenter.setToolTipText(Messages.getString("menu.file.import.songs.praisenter.tooltip"));
					mnuImportPraisenter.addActionListener(this);
					mnuImportSongs.add(mnuImportPraisenter);
					
					JMenuItem mnuImportCVSongs = new JMenuItem(Messages.getString("menu.file.import.songs.churchview"));
					mnuImportCVSongs.setActionCommand("importCVSongs");
					mnuImportCVSongs.setToolTipText(Messages.getString("menu.file.import.songs.churchview.tooltip"));
					mnuImportCVSongs.addActionListener(this);
					mnuImportSongs.add(mnuImportCVSongs);
				}
				
				mnuFile.addSeparator();
				
				// file->exit menu
				JMenuItem mnuExit = new JMenuItem(Messages.getString("menu.file.exit"));
				mnuExit.setActionCommand("exit");
				mnuExit.addActionListener(this);
				mnuFile.add(mnuExit);
			}
			
			// libraries menu
			JMenu mnuLibraries = new JMenu(Messages.getString("menu.libraries"));
			barMenu.add(mnuLibraries);
			{
				// libraries->bible menu
				JMenuItem mnuBibleLibrary = new JMenuItem(Messages.getString("menu.libraries.bible"));
				mnuBibleLibrary.setActionCommand("bible");
				mnuBibleLibrary.addActionListener(this);
				mnuLibraries.add(mnuBibleLibrary);
				
				// libraries->song menu
				JMenuItem mnuSongLibrary = new JMenuItem(Messages.getString("menu.libraries.song"));
				mnuSongLibrary.setActionCommand("song");
				mnuSongLibrary.addActionListener(this);
				mnuLibraries.add(mnuSongLibrary);
				
				// libraries->media menu
				JMenuItem mnuMediaLibrary = new JMenuItem(Messages.getString("menu.libraries.media"));
				mnuMediaLibrary.setActionCommand("media");
				mnuMediaLibrary.addActionListener(this);
				mnuLibraries.add(mnuMediaLibrary);
				
				// libraries->slide/template menu
				JMenuItem mnuSlideLibrary = new JMenuItem(Messages.getString("menu.libraries.slide"));
				mnuSlideLibrary.setActionCommand("slide");
				mnuSlideLibrary.addActionListener(this);
				mnuLibraries.add(mnuSlideLibrary);
			}
			
			// debugging menu
			if (Main.isDebugEnabled()) {
				JMenu mnuWindow = new JMenu(Messages.getString("menu.window"));
				barMenu.add(mnuWindow);
				
				// show size
				JMenuItem mnuSize = new JMenuItem(Messages.getString("menu.window.size"));
				mnuSize.setActionCommand("size");
				mnuSize.addActionListener(this);
				mnuWindow.add(mnuSize);
				
				// look and feel menu
				this.mnuLookAndFeel = new JMenu(Messages.getString("menu.window.laf"));
				this.createLookAndFeelMenuItems(this.mnuLookAndFeel);
				mnuWindow.add(this.mnuLookAndFeel);
			}
			
			// help menu
			{
				JMenu mnuHelp = new JMenu(Messages.getString("menu.help"));
				barMenu.add(mnuHelp);
				
				// make sure the desktop is supported
				if (Desktop.isDesktopSupported()) {
					// about menu
					JMenuItem mnuLogs = new JMenuItem(Messages.getString("menu.help.logs"));
					mnuLogs.setActionCommand("logs");
					mnuLogs.addActionListener(this);
					mnuHelp.add(mnuLogs);
					
					mnuHelp.addSeparator();
				}
				
				// system menu
				JMenuItem mnuSystem = new JMenuItem(Messages.getString("menu.help.system"));
				mnuSystem.setActionCommand("system");
				mnuSystem.addActionListener(this);
				mnuHelp.add(mnuSystem);
				
				// about menu
				JMenuItem mnuAbout = new JMenuItem(Messages.getString("menu.help.about"));
				mnuAbout.setActionCommand("about");
				mnuAbout.addActionListener(this);
				mnuHelp.add(mnuAbout);
			}
			
			this.setJMenuBar(barMenu);
		}
		
		// exit the JVM on close of the app
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// dispose of the presentation manager
				PresentationManager.getInstance().dispose();
			}
			@Override
			public void windowClosed(WindowEvent e) {
				// this is called when dispose is called on this JFrame
				// kick off a daemon thread to shutdown the vm just in case
				
				// this thread will be axed if every other thread is a daemon
				// and there are no other displayable windows (this is the
				// requirement for exiting cleanly) since its a daemon thread
				// as well.
				
				// We need to do this to make sure the JVM shuts down. If its
				// left open, the only option for the user to do is kill the
				// process (which some users just can't be expected to know
				// how to do)
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							LOGGER.debug("The exit thread has started.");
							// wait 10 seconds before we forcefully shut down the JVM
							Thread.sleep(10000);
							LOGGER.warn("The exit thread waited 10 seconds. Manually exiting.");
							// shut her down...
							System.exit(0);
						} catch (InterruptedException e) {
							// I'm not really sure what to do here since this could happen in a normal
							// way (if the daemon threads are killed via interrupts). So for now we will
							// just log the error and hope the JVM shuts down on its own
							LOGGER.warn("The exit thread was interrupted. No System.exit(0) call made.");
						}
					}
				}, "ExitJVMThread");
				thread.setDaemon(true);
				thread.start();
				
				super.windowClosed(e);
			}
		});
		
		// size everything
		this.pack();
		
		// put the window in the middle of the primary display
		this.setLocationRelativeTo(null);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		// check for look and feel changes
		if (command.startsWith("laf+")) {
			this.changeLookAndFeel(command);
		} else if ("preferences".equals(command)) {
			// show the preferences dialog
			boolean changed = PreferencesDialog.show(this);
			if (changed) {
				this.pnlNotification.preferencesChanged();
				this.pnlSlides.preferencesChanged();
				this.pnlBible.preferencesChanged();
				this.pnlSongs.preferencesChanged();
			}
		} else if ("size".equals(command)) {
			this.showCurrentWindowSize();
		} else if ("exportErrors".equals(command)) {
			this.exportSavedErrorReports();
		} else if ("importCVSongs".equals(command)) {
			this.importChurchViewSongDatabase();
		} else if ("exportSongs".equals(command)) {
			this.exportSongs();
		} else if ("importPraisenterSongs".equals(command)) {
			this.importPraisenterSongDatabase();
		} else if ("bible".equals(command)) {
			boolean changed = BibleLibraryDialog.show(this);
			if (changed) {
				this.pnlBible.onBibleLibraryChanged();
			}
		} else if ("song".equals(command)) {
			SongLibraryDialog.show(this);
			this.pnlSongs.onReturnFromSongLibrary();
		} else if ("media".equals(command)) {
			MediaLibraryDialog.show(this);
		} else if ("slide".equals(command)) {
			boolean changed = SlideLibraryDialog.show(this, null);
			if (changed) {
				this.pnlNotification.slideLibraryChanged();
				this.pnlSlides.slideLibraryChanged();
				this.pnlBible.slideLibraryChanged();
				this.pnlSongs.slideLibraryChanged();
			}
		} else if ("logs".equals(command)) {
			File file = new File(Constants.LOG_FILE_LOCATION);
			try {
				Desktop.getDesktop().open(file);
			} catch (IOException e) {
				LOGGER.error("An error occurred while trying to open the log file location in the native system: ", e);
			}
		} else if ("system".equals(command)) {
			SystemDialog.show(this);
		} else if ("about".equals(command)) {
			AboutDialog.show(this);
		} else if ("exit".equals(command)) {
			this.setVisible(false);
			this.dispose();
		}
	}
	
	/**
	 * Adds menu items to the given menu for each look and feel
	 * installed in the running vm.
	 * @param menu the menu to add the items to
	 */
	private void createLookAndFeelMenuItems(JMenu menu) {
		LookAndFeel current = UIManager.getLookAndFeel();
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			JMenuItem mnuLaF = new JMenuItem(info.getName());
			if (current.getClass().getName().equals(info.getClassName())) {
				mnuLaF.setIcon(Icons.SELECTED);
			}
			mnuLaF.setActionCommand("laf+" + info.getClassName());
			mnuLaF.addActionListener(this);
			menu.add(mnuLaF);
		}
	}
	
	/**
	 * Changes the look and feel to the selected look and feel.
	 * @param command the command
	 */
	private void changeLookAndFeel(String command) {
		// make sure they are sure
		int choice = JOptionPane.showConfirmDialog(this, 
				Messages.getString("dialog.laf.warning.text"), 
				Messages.getString("dialog.laf.warning.title"), 
				JOptionPane.YES_NO_CANCEL_OPTION);
		// check the user's choice
		if (choice == JOptionPane.YES_OPTION) {
			// parse out the LAF class name
			String className = command.replace("laf+", "");
			try {
				// attempt to set the look and feel
				UIManager.setLookAndFeel(className);
				// get the current windows open by this application
				Window windows[] = Frame.getWindows();
				// update the ui
		        for(Window window : windows) {
		            SwingUtilities.updateComponentTreeUI(window);
		        }
		        // we need to pack since certain look and feels may have different component
		        // gaps which can cause stuff not to be shown
		        this.pack();
		        // find the item in the menu to set the current one
		        for (Component component : this.mnuLookAndFeel.getPopupMenu().getComponents()) {
		        	JMenuItem item = (JMenuItem)component;
		        	// set the newly selected LAF to have a checked icon
		        	// and the rest to have no icon
		        	if (item.getActionCommand().equalsIgnoreCase(command)) {
		        		item.setIcon(Icons.SELECTED);
		        	} else {
		        		item.setIcon(null);
		        	}
		        }
			} catch (Exception e) {
				ExceptionDialog.show(this, 
						Messages.getString("dialog.laf.error.title"), 
						Messages.getString("dialog.laf.error.text"), 
						e);
			}
		}
	}
	
	/**
	 * Shows a simple dialog with the current width and height of the main window.
	 */
	private void showCurrentWindowSize() {
		// show a dialog with size
		JOptionPane.showMessageDialog(
				this, 
				this.getSize().width + "x" + this.getSize().height, 
				Messages.getString("dialog.size.title"), 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Saves any saved error reports into the user selected file.
	 */
	private void exportSavedErrorReports() {
		// see if we even need to export anything
		int count = 0;
		try {
			count = Errors.getErrorMessageCount();
		} catch (DataException e) {
			// just log the error
			LOGGER.error(e);
		}
		if (count > 0) {
			// create a class to show a "are you sure" message when over writing an existing file
			JFileChooser fileBrowser = new ValidateFileChooser();
			fileBrowser.setMultiSelectionEnabled(false);
			fileBrowser.setDialogTitle(Messages.getString("dialog.export.errors.title"));
			fileBrowser.setSelectedFile(new File(Messages.getString("dialog.export.errors.defaultFileName")));
			
			int option = fileBrowser.showSaveDialog(this);
			// check the option
			if (option == JFileChooser.APPROVE_OPTION) {
				final File file = fileBrowser.getSelectedFile();
				// create a new file task
				AbstractTask task = new AbstractTask() {
					@Override
					public void run() {
						try {
							// export the error messages to a string
							List<ErrorMessage> errors = Errors.getErrorMessages();
							// build a string representation of the messages
							StringBuilder sb = new StringBuilder();
							for (ErrorMessage error : errors) {
								sb.append(error.toFormattedString()).append("\n\n");
							}
							// see if the file exists
							if (!file.exists()) {
								try {
									file.createNewFile();
								} catch (IOException ex) {
									this.handleException(ex);
									return;
								}
							}
							try (FileWriter fw = new FileWriter(file)) {
								fw.write(sb.toString());
							}
							// once the file has been written to, delete the messages
							// from the data store
							try {
								Errors.clearErrorMessages();
							} catch (Exception e) {
								// if an error happens here, just log it since its
								LOGGER.error("Failed to clear error messages: ", e);
							}
							this.setSuccessful(true);
						} catch (Exception ex) {
							this.handleException(ex);
						}
					}
				};
				
				// run the task
				TaskProgressDialog.show(this, Messages.getString("exporting"), task);
				
				// check the task result
				if (task.isSuccessful()) {
					JOptionPane.showMessageDialog(this, 
							Messages.getString("dialog.export.errors.success.text"), 
							Messages.getString("dialog.export.errors.success.title"), 
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					LOGGER.error("An error occurred while exporting the saved error reports:", task.getException());
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, 
					Messages.getString("dialog.export.errors.none.text"), 
					Messages.getString("dialog.export.errors.none.title"), 
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * Exports the entire song database to an xml file.
	 */
	private void exportSongs() {
		// get the song count
		int songCount = 0;
		try {
			songCount = Songs.getSongCount();
		} catch (DataException e) {}
		
		// see if we even need to export anything
		if (songCount > 0) {
			// create a class to show a "are you sure" message when over writing an existing file
			JFileChooser fileBrowser = new ValidateFileChooser();
			fileBrowser.setMultiSelectionEnabled(false);
			fileBrowser.setDialogTitle(Messages.getString("dialog.export.songs.title"));
			fileBrowser.setSelectedFile(new File(Messages.getString("dialog.export.songs.defaultFileName")));
			
			int option = fileBrowser.showSaveDialog(this);
			// check the option
			if (option == JFileChooser.APPROVE_OPTION) {
				final File file = fileBrowser.getSelectedFile();
				// create a new file task
				AbstractTask task = new AbstractTask() {
					@Override
					public void run() {
						try {
							// load up the songs
							List<Song> songs = Songs.getSongs();
							// create the object to save
							SongList list = new SongList(songs);
							XmlIO.save(file.getAbsolutePath(), list);
							this.setSuccessful(true);
						} catch (Exception ex) {
							this.handleException(ex);
						}
					}
				};
				
				// run the task
				TaskProgressDialog.show(this, Messages.getString("exporting"), task);
				
				// check the task result
				if (task.isSuccessful()) {
					JOptionPane.showMessageDialog(this, 
							Messages.getString("dialog.export.songs.success.text"), 
							Messages.getString("dialog.export.songs.success.title"), 
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					LOGGER.error("An error occurred while exporting the songs:", task.getException());
					ExceptionDialog.show(
							this,
							Messages.getString("dialog.export.songs.error.title"), 
							Messages.getString("dialog.export.songs.error.text"), 
							task.getException());
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, 
					Messages.getString("dialog.export.songs.none.text"), 
					Messages.getString("dialog.export.songs.none.title"), 
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * Attempts to import the user selected church view song file.
	 */
	private void importPraisenterSongDatabase() {
		JFileChooser fileBrowser = new JFileChooser();
		fileBrowser.setDialogTitle(Messages.getString("dialog.open.title"));
		fileBrowser.setMultiSelectionEnabled(false);
		int option = fileBrowser.showOpenDialog(this);
		// check the option
		if (option == JFileChooser.APPROVE_OPTION) {
			// get the selected file
			final File file = fileBrowser.getSelectedFile();
			// make sure it exists and its a file
			if (file.exists() && file.isFile()) {
				// make sure they are sure
				option = JOptionPane.showConfirmDialog(this, 
						Messages.getString("dialog.import.songs.prompt.text"), 
						MessageFormat.format(Messages.getString("dialog.import.songs.prompt.title"), file.getName()), 
						JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (option == JOptionPane.YES_OPTION) {
					// we need to execute this in a separate process
					// and show a progress monitor
					AbstractTask task = new AbstractTask() {
						@Override
						public void run() {
							try {
								// import the bible
								SongImporter.importPraisenterSongs(file);
								setSuccessful(true);
							} catch (Exception e) {
								// handle the exception
								handleException(e);
							}
						}
					};
					// show a task progress bar
					TaskProgressDialog.show(
							this, 
							Messages.getString("importing"), 
							task);
					// show a message either way
					if (task.isSuccessful()) {
						// show a success message
						JOptionPane.showMessageDialog(this, 
								Messages.getString("dialog.import.songs.success.text"), 
								Messages.getString("dialog.import.songs.success.title"), 
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						Exception e = task.getException();
						// show an error message
						ExceptionDialog.show(
								this,
								Messages.getString("dialog.import.songs.failed.title"), 
								Messages.getString("dialog.import.songs.failed.text"), 
								e);
						LOGGER.error("An error occurred while importing a ChurchView song database:", e);
					}
				}
			}
		}
	}
	
	/**
	 * Attempts to import the user selected church view song file.
	 */
	private void importChurchViewSongDatabase() {
		JFileChooser fileBrowser = new JFileChooser();
		fileBrowser.setDialogTitle(Messages.getString("dialog.open.title"));
		fileBrowser.setMultiSelectionEnabled(false);
		int option = fileBrowser.showOpenDialog(this);
		// check the option
		if (option == JFileChooser.APPROVE_OPTION) {
			// get the selected file
			final File file = fileBrowser.getSelectedFile();
			// make sure it exists and its a file
			if (file.exists() && file.isFile()) {
				// make sure they are sure
				option = JOptionPane.showConfirmDialog(this, 
						Messages.getString("dialog.import.songs.prompt.text"), 
						MessageFormat.format(Messages.getString("dialog.import.songs.prompt.title"), file.getName()), 
						JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (option == JOptionPane.YES_OPTION) {
					// we need to execute this in a separate process
					// and show a progress monitor
					AbstractTask task = new AbstractTask() {
						@Override
						public void run() {
							try {
								// import the bible
								SongImporter.importChurchViewSongs(file);
								setSuccessful(true);
							} catch (Exception e) {
								// handle the exception
								handleException(e);
							}
						}
					};
					// show a task progress bar
					TaskProgressDialog.show(
							this, 
							Messages.getString("importing"), 
							task);
					// show a message either way
					if (task.isSuccessful()) {
						// show a success message
						JOptionPane.showMessageDialog(this, 
								Messages.getString("dialog.import.songs.success.text"), 
								Messages.getString("dialog.import.songs.success.title"), 
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						Exception e = task.getException();
						// show an error message
						ExceptionDialog.show(
								this,
								Messages.getString("dialog.import.songs.failed.title"), 
								Messages.getString("dialog.import.songs.failed.text"), 
								e);
						LOGGER.error("An error occurred while importing a ChurchView song database:", e);
					}
				}
			}
		}
	}
}
