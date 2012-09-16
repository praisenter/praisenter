package org.praisenter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.praisenter.control.ZipFileFilter;
import org.praisenter.data.DataException;
import org.praisenter.data.DataImportException;
import org.praisenter.data.bible.Bibles;
import org.praisenter.data.bible.UnboundBibleImporter;
import org.praisenter.data.errors.Errors;
import org.praisenter.data.song.ChurchViewSongImporter;
import org.praisenter.data.song.Songs;
import org.praisenter.dialog.ExceptionDialog;
import org.praisenter.dialog.SetupDialog;
import org.praisenter.icons.Icons;
import org.praisenter.panel.bible.BiblePanel;
import org.praisenter.resources.Messages;
import org.praisenter.settings.SettingsListener;
import org.praisenter.utilities.FontManager;

/**
 * Main class for the application.
 * <p>
 * This class represents the main window and links all the functionality of the
 * application together.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Praisenter extends JFrame implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 4204856340044399264L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(Praisenter.class);
	
	/** The log4j file name */
	private static final String LOG4J_FILE_NAME = "log4j.xml";
	
	/** The log4j file location */
	private static final String LOG4J_FILE_LOCATION	= "config/";
	
	// menu
	
	/** Menu bar look and feel item */
	private JMenu mnuLookAndFeel;
	
	// main panels
	
	/** The bible panel */
	private BiblePanel pnlBible;
	
	/**
	 * Default constructor.
	 */
	private Praisenter() {
		super(Messages.getString("praisenter"));
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		
		// TODO add a way to save a service; the service type will be used to store queued songs and verses
		
		// TODO add menu option to export bibles and songs (probably xml format)
		
		// create the bible panel
		this.pnlBible = new BiblePanel();
		this.pnlBible.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(Messages.getString("bible"), this.pnlBible);
		
		container.add(tabs, BorderLayout.CENTER);
		
		// create the main menu bar
		{
			JMenuBar barMenu = new JMenuBar();
			
			// main menus
			JMenu mnuFile = new JMenu(Messages.getString("menu.file"));
			JMenu mnuWindow = new JMenu(Messages.getString("menu.window"));
			
			// preferences menu
			JMenuItem mnuPreferences = new JMenuItem(Messages.getString("menu.window.preferences"));
			mnuPreferences.setActionCommand("preferences");
			mnuPreferences.addActionListener(this);
			mnuWindow.add(mnuPreferences);

			// export menu
			JMenu mnuExport = new JMenu(Messages.getString("menu.file.export"));
			mnuFile.add(mnuExport);
			
			JMenuItem mnuExportErrors = new JMenuItem(Messages.getString("menu.file.export.errors"));
			mnuExportErrors.setActionCommand("exportErrors");
			mnuExportErrors.addActionListener(this);
			mnuExport.add(mnuExportErrors);
			
			// import menu
			
			JMenu mnuImport = new JMenu(Messages.getString("menu.file.import"));
			mnuFile.add(mnuImport);
			
			JMenuItem mnuImportUBBible = new JMenuItem(Messages.getString("menu.file.import.bible.unbound"));
			mnuImportUBBible.setActionCommand("importUBBible");
			mnuImportUBBible.addActionListener(this);
			mnuImport.add(mnuImportUBBible);
			
			JMenuItem mnuImportCVSongs = new JMenuItem(Messages.getString("menu.file.import.songs.churchview"));
			mnuImportCVSongs.setActionCommand("importCVSongs");
			mnuImportCVSongs.addActionListener(this);
			mnuImport.add(mnuImportCVSongs);
			
			if (Praisenter.isDebug()) {
				// look and feel menu
				this.mnuLookAndFeel = new JMenu(Messages.getString("menu.window.laf"));
				this.createLookAndFeelMenuItems(this.mnuLookAndFeel);
				mnuWindow.add(this.mnuLookAndFeel);
			}
			
			if (Praisenter.isDebug()) {
				// show size
				JMenuItem mnuSize = new JMenuItem(Messages.getString("menu.window.size"));
				mnuSize.setActionCommand("size");
				mnuSize.addActionListener(this);
				mnuWindow.add(mnuSize);
			}
			
			barMenu.add(mnuFile);
			barMenu.add(mnuWindow);
			this.setJMenuBar(barMenu);
		}
		
		// exit the JVM on close of the app
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
			SetupDialog.show(this, new SettingsListener[] { this.pnlBible });
		} else if ("size".equals(command)) {
			this.showCurrentWindowSize();
		} else if ("exportErrors".equals(command)) {
			// show a file save dialog
			try {
				this.exportSavedErrorReports();
			} catch (Exception e) {
				LOGGER.error("An error occurred while exporting the saved error reports:", e);
			}
		} else if ("importUBBible".equals(command)) {
			this.importUnboundBible();
		} else if ("importCVSongs".equals(command)) {
			this.importChurchViewSongDatabase();
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
	 * @throws IOException thrown if an IO error occurs
	 */
	@SuppressWarnings("serial")
	private void exportSavedErrorReports() throws IOException {
		// see if we even need to export anything
		if (Errors.getErrorMessageCount() > 0) {
			// create a class to show a "are you sure" message when over writing an existing file
			JFileChooser fileBrowser = new JFileChooser() {
				/* (non-Javadoc)
				 * @see javax.swing.JFileChooser#approveSelection()
				 */
				@Override
				public void approveSelection() {
					if (getDialogType() == SAVE_DIALOG) {
						File selectedFile = getSelectedFile();
						if ((selectedFile != null) && selectedFile.exists()) {
							int response = JOptionPane.showConfirmDialog(
											this,
											MessageFormat.format(Messages.getString("dialog.export.errors.warning.text"), selectedFile.getName()),
											Messages.getString("dialog.export.errors.warning.title"),
											JOptionPane.YES_NO_OPTION,
											JOptionPane.WARNING_MESSAGE);
							if (response != JOptionPane.YES_OPTION)
								return;
						}
					}
	
					super.approveSelection();
				}
			};
			fileBrowser.setMultiSelectionEnabled(false);
			fileBrowser.setDialogTitle(Messages.getString("dialog.export.errors.title"));
			fileBrowser.setSelectedFile(new File(Messages.getString("dialog.export.errors.defaultFileName")));
			
			int option = fileBrowser.showSaveDialog(this);
			// check the option
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileBrowser.getSelectedFile();
				// export the errors
				String text = Errors.exportErrorMessages();
				
				// see if its a new one or it already exists
				if (file.exists()) {
					// overwrite the file
					FileWriter fw = new FileWriter(file);
					fw.write(text);
					fw.close();
					JOptionPane.showMessageDialog(this, 
							Messages.getString("dialog.export.errors.success.text"), 
							Messages.getString("dialog.export.errors.success.title"), 
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					// create a new file
					if (file.createNewFile()) {
						FileWriter fw = new FileWriter(file);
						fw.write(text);
						fw.close();
						JOptionPane.showMessageDialog(this, 
								Messages.getString("dialog.export.errors.success.text"), 
								Messages.getString("dialog.export.errors.success.title"), 
								JOptionPane.INFORMATION_MESSAGE);
					}
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
	 * Attempts to import the user selected Unbound Bible .zip file.
	 */
	private void importUnboundBible() {
		JFileChooser fileBrowser = new JFileChooser();
		fileBrowser.setDialogTitle(Messages.getString("dialog.open.title"));
		fileBrowser.setMultiSelectionEnabled(false);
		fileBrowser.setAcceptAllFileFilterUsed(false);
		fileBrowser.setFileFilter(new ZipFileFilter());
		int option = fileBrowser.showOpenDialog(this);
		// check the option
		if (option == JFileChooser.APPROVE_OPTION) {
			// get the selected file
			File file = fileBrowser.getSelectedFile();
			// make sure it exists and its a file
			if (file.exists() && file.isFile()) {
				// make sure they are sure
				option = JOptionPane.showConfirmDialog(this, 
						Messages.getString("dialog.import.bible.prompt.text"), 
						MessageFormat.format(Messages.getString("dialog.import.bible.prompt.title"), file.getName()), 
						JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (option == JOptionPane.YES_OPTION) {
					// we need to execute this in a separate process
					// and show a progress monitor
					ImportFileTask task = new ImportFileTask(file) {
						@Override
						public void run() {
							try {
								// import the bible
								UnboundBibleImporter.importBible(getFile());
								setSuccessful(true);
							} catch (DataImportException e) {
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
								Messages.getString("dialog.import.bible.success.text"), 
								Messages.getString("dialog.import.bible.success.title"), 
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						Exception e = task.getException();
						// show an error message
						ExceptionDialog.show(
								this,
								Messages.getString("dialog.import.bible.failed.title"), 
								Messages.getString("dialog.import.bible.failed.text"), 
								e);
						LOGGER.error("An error occurred while importing an Unbound Bible bible:", e);
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
			File file = fileBrowser.getSelectedFile();
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
					ImportFileTask task = new ImportFileTask(file) {
						@Override
						public void run() {
							try {
								// import the bible
								ChurchViewSongImporter.importSongs(getFile());
								setSuccessful(true);
							} catch (DataImportException e) {
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
	
	// application entry point
	
	/** True if the -debug command line argument was passed in */
	private static boolean DEBUG = false;
	
	/**
	 * Main entry point into the app.
	 * <p>
	 * Command line arguments:
	 * <ul>
	 * <li>-debug</li>
	 * </ul>
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		// interpret command line
		if (args != null) {
			for (String arg : args) {
				if ("-debug".equals(arg)) {
					DEBUG = true;
				}
			}
		}
		
		// configure log4j
		Praisenter.initializeLog4j();
		
		// attempt to use the nimbus look and feel
		Praisenter.initializeDefaultLookAndFeel();
		
		// start the swing app on the EDT
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					// preload and get the praisenter instance
					Praisenter praisenter = PreloadDialog.preload();
					if (praisenter == null) {
						// this will only happen if the praisenter
						// was not created on the EDT in the pre-loader
						// which could be possible if the waiting thread
						// was interrupted
						praisenter = new Praisenter();
					}
					// TODO break the preloading code up into more classes
					// show the main app
					praisenter.setVisible(true);
					praisenter.toFront();
				} catch (Exception e) {
					LOGGER.error(e);
					ExceptionDialog.show(
							null, 
							Messages.getString("exception.startup.title"), 
							Messages.getString("exception.startup.text"), 
							e);
					// this relies on the fact that the ExceptionDialog is
					// application modal (i.e. blocks)
					System.exit(0);
				}
			}
		});
	}
	
	/**
	 * Returns true if the -debug argument was passed to startup.
	 * @return boolean
	 */
	public static final boolean isDebug() {
		return DEBUG;
	}
	
	/**
	 * Performs the setup of log4j.
	 * <p>
	 * This method will first check for the log4j.xml file in the root directory of the
	 * application.  If this file is not found, the log4j.xml file in the classpath is used.
	 * If this file is not found then the basic configuration is used.
	 * <p>
	 * If the log4j.xml file in the root directory of the application is not found and the 
	 * classpath log4j.xml file is used, this method will attempt to copy the classpath file
	 * to the root directory.
	 * <p>
	 * Unfortunately there is no way to verify whether the log4j.xml if valid and no way to
	 * verify that the configuration using a file has worked.
	 */
	private static final void initializeLog4j() {
		LOGGER.info("Configuring Log4j.");
		try {
			// see if the config file exists
			File file = new File(Praisenter.LOG4J_FILE_LOCATION + Praisenter.LOG4J_FILE_NAME);
			if (file.exists()) {
				DOMConfigurator.configure(Praisenter.LOG4J_FILE_LOCATION + Praisenter.LOG4J_FILE_NAME);
			} else {
				// the file didn't exist so load the classpath one
				try {
					// configure using the classpath xml file
					DOMConfigurator.configure(Praisenter.class.getResource("/" + Praisenter.LOG4J_FILE_NAME));
					LOGGER.warn("Log4j configuration file not found at [" + file.getAbsolutePath() + "]. Using classpath configuration file.");
					try {
						LOGGER.info("Copying classpath " + Praisenter.LOG4J_FILE_NAME + " to " + Praisenter.LOG4J_FILE_LOCATION + " directory.");
						// attempt to make a copy and place it in the root directory
						if (file.createNewFile()) {
							// copy the contents of the file
							try {
								BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
								BufferedInputStream bis = new BufferedInputStream(Praisenter.class.getResourceAsStream("/" + Praisenter.LOG4J_FILE_NAME));
								int r = 0;
								byte[] buffer = new byte[1000];
								while ((r = bis.read(buffer, 0, buffer.length)) > 0) {
									bos.write(buffer, 0, r);
								}
								bos.flush();
								bos.close();
								bis.close();
								LOGGER.info(Praisenter.LOG4J_FILE_NAME + " file copied successfully.");
							} catch (FileNotFoundException ex) {
								// just log the error if we can't copy the file							
								LOGGER.warn("File not found: [" + file.getAbsolutePath() + "].", ex);
							}
						} else {
							// just log the error if we can't copy the file							
							LOGGER.warn("Could not create file: [" + file.getAbsolutePath() + "].");
						}
					} catch (IOException ex) {
						// just log the error if we can't copy or write to the file
						LOGGER.warn("An IO error occurred while creating or writing the log file to the root directory.", ex);
					}
				} catch (Exception ex) {
					// just use the default configuration
					// this can happen if the class path one doesn't exist or any other error occurs
					BasicConfigurator.configure();
					LOGGER.warn("An error occurred while configuring log4j using the classpath " + Praisenter.LOG4J_FILE_NAME + ". Using default configuration instead.", ex);
				}
			}
			LOGGER.info("Log4j initialized successfully.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Initializes the default look and feel.
	 * <p>
	 * This method will attempt to change the application look and feel to the
	 * Nimbus look and feel.  If its not found, then the default look and
	 * feel is used.
	 */
	private static final void initializeDefaultLookAndFeel() {
		LOGGER.info("Defaulting look and feel.");
		
		// get the default look and feel
		String defaultLookAndFeelClassName = null;
		try {
			defaultLookAndFeelClassName = System.getProperty("swing.defaultlaf");
		} catch (Exception ex) {
			LOGGER.warn("Could not obtain default look and feel class name: ", ex);
		}
		
		String defaultLookAndFeelName = defaultLookAndFeelClassName;
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		        	LOGGER.info("Nimbus look and feel found.");
		            UIManager.setLookAndFeel(info.getClassName());
		            return;
		        }
		        if (info.getClassName().equals(defaultLookAndFeelClassName)) {
		        	defaultLookAndFeelName = info.getName();
		        }
		    }
		} catch (Exception ex) {
			// completely ignore the error and just use the default look and feel
			LOGGER.info("Failed to change the look and feel to Nimbus. Continuing wity default look and feel.", ex);
		}
		
		if (defaultLookAndFeelName == null || defaultLookAndFeelName.isEmpty()) {
			// if the default look and feel has not been set
			LOGGER.info("Nimbus look and feel not found. Using default look and feel: Metal");
		} else {
			LOGGER.info("Nimbus look and feel not found. Using default look and feel: " + defaultLookAndFeelName);
		}
	}
	
	/**
	 * Dialog used to pre-load application resources like fonts.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private static class PreloadDialog extends JDialog {
		/** The version id */
		private static final long serialVersionUID = -944344035062861836L;

		/** Progress bar for loading */
		private JProgressBar barProgress;
		
		/** Label for the current group of resources being loaded */
		private JLabel lblLoading;
		
		/** Label for the specific resource being loaded */
		private JLabel lblLoadingText;
		
		/** The main application window */
		private Praisenter praisenter;
		
		/** The exception */
		private Exception exception;
		
		/**
		 * Shows the preload dialog and creates the instance of Praisenter.
		 * <p>
		 * This method will throw an exception only in the case that the application
		 * cannot continue.
		 * @return {@link Praisenter}
		 * @throws Exception a unrecoverable error
		 */
		private static final Praisenter preload() throws Exception {
			PreloadDialog dialog = new PreloadDialog();
			
			if (dialog.exception != null) {
				throw dialog.exception;
			}
			
			return dialog.praisenter;
		}
		
		/**
		 * Default constructor.
		 */
		private PreloadDialog() {
			super(null, Messages.getString("dialog.preload.title"), ModalityType.APPLICATION_MODAL);
			// make sure closing the modal doesn't work (since we can't remove the close button)
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			
			// create the progress bar
			this.barProgress = new JProgressBar(0, 100);
			this.barProgress.setStringPainted(true);
			this.barProgress.setMinimumSize(new Dimension(0, 50));
			
			this.lblLoading = new JLabel(Messages.getString("dialog.preload.title"));
			
			// create a label
			this.lblLoadingText = new JLabel(" ");
			this.lblLoadingText.setMinimumSize(new Dimension(0, 30));
			
			// layout the loading
			Container container = this.getContentPane();
			
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
			
			// set the minimum size (so we can see all of the font)
			this.setMinimumSize(new Dimension(400, 0));
			
			// size the window
			this.pack();
			
			// start the background thread
			this.start();
			
			// make sure we are in the center of the parent window
			this.setLocationRelativeTo(null);
			
			// show the dialog
			this.setVisible(true);
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
					// wait a bit for the dialog to show
					try {
						Thread.sleep(500);
						// just eat the exception if we get one
					} catch (InterruptedException e) {}
					
					// begin the tasks
					try {
						verifyDataConnections();
					} catch (DataException ex) {
						// we need to stop immediately if we get an
						// error connection to the data stores
						exception = ex;
						close();
						return;
					}
					preloadFonts();
					preloadMainApplicationWindow();
					// TODO attempt to send saved errors
					
					// PLACE OTHER PRELOADING TASKS HERE
					
					// update the label to show completed
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								lblLoading.setText(Messages.getString("dialog.preload.complete"));
								lblLoadingText.setText("");
							}
						});
					// just eat the exceptions
					} catch (Exception e) {}
					
					// wait a bit to allow the user to see
					// that the preloading has completed
					try {
						Thread.sleep(500);
						// just eat the exception if we get one
					} catch (InterruptedException e) {}
					
					// once the tasks are complete then
					// close the modal and resume normal
					// application flow
					close();
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
			// close the dialog later
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					// close it
					PreloadDialog.this.setVisible(false);
					// then dispose of the resources
					PreloadDialog.this.dispose();
				}
			});
		}
		
		// database
		
		/**
		 * Verifies the database connections.
		 * @throws DataException if the connection to the data store(s) could not be made
		 */
		private void verifyDataConnections() throws DataException {
			// set the initial status
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						lblLoading.setText(Messages.getString("dialog.preload.verifyData"));
						barProgress.setValue(0);
					}
				});
			} catch (Exception e) {}
			
			// check the bible data store
			Bibles.getBibleCount();
			
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						barProgress.setValue(33);
					}
				});
			} catch (Exception e) {}
			
			// check the songs data store
			Songs.getSong(0);
			
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						barProgress.setValue(66);
					}
				});
			} catch (Exception e) {}
			
			// check the songs data store
			Errors.getErrorMessageCount();
			
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						barProgress.setValue(100);
					}
				});
			} catch (Exception e) {}
		}
		
		// main app
		
		/**
		 * Preloads the main application window.
		 */
		private void preloadMainApplicationWindow() {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						lblLoading.setText(Messages.getString("dialog.preload.app"));
						lblLoadingText.setText("");
						lblLoadingText.setFont(FontManager.getDefaultFont());
						barProgress.setValue(0);
						
						// create the main app window
						// needs to be run on the EDT
						praisenter = new Praisenter();
						
						barProgress.setValue(100);
					}
				});
			} catch (Exception e) {}
		}
		
		// font loading
		
		/** A reusable font update task */
		private FontProgressUpdateTask fontUpdateTask = new FontProgressUpdateTask();
		
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
			try {
				SwingUtilities.invokeAndWait(this.fontUpdateTask.set(null, 0));
			} catch (Exception e) {}
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
				try {
					SwingUtilities.invokeAndWait(this.fontUpdateTask.set(font, (int)Math.floor(cur / max * 100)));
				} catch (Exception e) {}
			}
		}
		
		/**
		 * Represents a task to update the progress on loading fonts.
		 * @author William Bittle
		 * @version 1.0.0
		 * @since 1.0.0
		 */
		private class FontProgressUpdateTask implements Runnable {
			/** The font */
			private Font font;
			
			/** The current progress (0-100) */
			private int value;
			
			/**
			 * Updates this task's values and returns this task.
			 * <p>
			 * Use this method to reuse this task.
			 * @param font the new font
			 * @param value the current progress
			 * @return {@link FontProgressUpdateTask}
			 */
			public FontProgressUpdateTask set(Font font, int value) {
				this.font = font;
				this.value = value;
				return this;
			}
			
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				if (this.font != null) {
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
					// set the progress bar's value
					barProgress.setValue(this.value);
				} else {
					lblLoading.setText(MessageFormat.format(Messages.getString("dialog.preload.fonts.label"), FontManager.getFontFamilyNames().length));
					barProgress.setValue(this.value);
				}
			}
		}
	}
}
