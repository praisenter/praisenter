package org.praisenter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
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
import org.praisenter.control.ZipFileFilter;
import org.praisenter.data.DataException;
import org.praisenter.data.DataImportException;
import org.praisenter.data.bible.UnboundBibleImporter;
import org.praisenter.data.errors.Errors;
import org.praisenter.data.song.SongImporter;
import org.praisenter.data.song.SongExporter;
import org.praisenter.data.song.Songs;
import org.praisenter.dialog.ExceptionDialog;
import org.praisenter.dialog.SetupDialog;
import org.praisenter.icons.Icons;
import org.praisenter.panel.bible.BiblePanel;
import org.praisenter.resources.Messages;
import org.praisenter.settings.SettingsListener;

/**
 * Main window for the Praisenter application.
 * @author William Bittle
 * @version 1.0.0
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
	
	/** The bible panel */
	private BiblePanel pnlBible;
	
	/**
	 * Default constructor.
	 */
	public Praisenter() {
		super(Messages.getString("praisenter"));
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		
		// TODO add a way to save a service; this could be used to store queued songs and verses
		
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

			// import menu
			{
				JMenu mnuImport = new JMenu(Messages.getString("menu.file.import"));
				mnuFile.add(mnuImport);
				
				JMenu mnuImportBible = new JMenu(Messages.getString("menu.file.import.bible"));
				mnuImport.add(mnuImportBible);
				
				JMenu mnuImportSongs = new JMenu(Messages.getString("menu.file.import.songs"));
				mnuImport.add(mnuImportSongs);
				
				JMenuItem mnuImportUBBible = new JMenuItem(Messages.getString("menu.file.import.bible.unbound"));
				mnuImportUBBible.setActionCommand("importUBBible");
				mnuImportUBBible.addActionListener(this);
				mnuImportBible.add(mnuImportUBBible);
				
				// TODO add option to import PraisenterSongs.xml file format
				JMenuItem mnuImportPraisenter = new JMenuItem(Messages.getString("menu.file.import.songs.praisenter"));
				mnuImportPraisenter.setActionCommand("importPraisenterSongs");
				mnuImportPraisenter.addActionListener(this);
				mnuImportSongs.add(mnuImportPraisenter);
				
				JMenuItem mnuImportCVSongs = new JMenuItem(Messages.getString("menu.file.import.songs.churchview"));
				mnuImportCVSongs.setActionCommand("importCVSongs");
				mnuImportCVSongs.addActionListener(this);
				mnuImportSongs.add(mnuImportCVSongs);
			}
			
			if (Main.isDebug()) {
				// look and feel menu
				this.mnuLookAndFeel = new JMenu(Messages.getString("menu.window.laf"));
				this.createLookAndFeelMenuItems(this.mnuLookAndFeel);
				mnuWindow.add(this.mnuLookAndFeel);
			}
			
			if (Main.isDebug()) {
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
		} else if ("exportSongs".equals(command)) {
			try {
				this.exportSongs();
			} catch (Exception e) {
				LOGGER.error("An error occurred while exporting the songs:", e);
				ExceptionDialog.show(
						this,
						Messages.getString("dialog.export.songs.error.title"), 
						Messages.getString("dialog.export.songs.error.text"), 
						e);
			}
		} else if ("importPraisenterSongs".equals(command)) {
			this.importPraisenterSongDatabase();
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
	 * Exports the entire song database to an xml file.
	 * @throws IOException thrown if an IO error occurs
	 * @throws DataException if an exception occurs getting the data
	 */
	@SuppressWarnings("serial")
	private void exportSongs() throws IOException, DataException {
		// see if we even need to export anything
		if (Songs.getSongCount() > 0) {
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
											MessageFormat.format(Messages.getString("dialog.export.songs.warning.text"), selectedFile.getName()),
											Messages.getString("dialog.export.songs.warning.title"),
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
			fileBrowser.setDialogTitle(Messages.getString("dialog.export.songs.title"));
			fileBrowser.setSelectedFile(new File(Messages.getString("dialog.export.songs.defaultFileName")));
			
			int option = fileBrowser.showSaveDialog(this);
			// check the option
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileBrowser.getSelectedFile();
				// export the errors
				String text = SongExporter.exportSongs();
				
				// see if its a new one or it already exists
				if (file.exists()) {
					// overwrite the file
					FileWriter fw = new FileWriter(file);
					fw.write(text);
					fw.close();
					JOptionPane.showMessageDialog(this, 
							Messages.getString("dialog.export.songs.success.text"), 
							Messages.getString("dialog.export.songs.success.title"), 
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					// create a new file
					if (file.createNewFile()) {
						FileWriter fw = new FileWriter(file);
						fw.write(text);
						fw.close();
						JOptionPane.showMessageDialog(this, 
								Messages.getString("dialog.export.songs.success.text"), 
								Messages.getString("dialog.export.songs.success.title"), 
								JOptionPane.INFORMATION_MESSAGE);
					}
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
	private void importPraisenterSongDatabase() {
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
								SongImporter.importPraisenterSongs(getFile());
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
								SongImporter.importChurchViewSongs(getFile());
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
}
