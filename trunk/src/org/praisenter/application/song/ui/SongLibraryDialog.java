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
package org.praisenter.application.song.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.praisenter.application.errors.ui.ExceptionDialog;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.ui.TaskProgressDialog;
import org.praisenter.application.ui.ValidateFileChooser;
import org.praisenter.common.threading.AbstractTask;
import org.praisenter.common.xml.XmlIO;
import org.praisenter.data.DataException;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongImporter;
import org.praisenter.data.song.SongList;
import org.praisenter.data.song.Songs;

/**
 * Simple dialog to display the Song Library.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.1
 */
public class SongLibraryDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 6827271875643932106L;
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SongLibraryDialog.class);
	
	/** The song library panel */
	private SongLibraryPanel pnlSongLibrary;
	
	/**
	 * Minimal constructor.
	 * @param owner the owner of the this dialog; can be null
	 */
	private SongLibraryDialog(Window owner) {
		super(owner, Messages.getString("dialog.song.title"), ModalityType.APPLICATION_MODAL);
		
		// create the main menu bar
		{
			JMenuBar barMenu = new JMenuBar();
			
			// import menu
			JMenu mnuImport = new JMenu(Messages.getString("dialog.song.menu.import"));
			barMenu.add(mnuImport);
			{
				JMenuItem mnuImportPraisenter = new JMenuItem(Messages.getString("dialog.song.menu.import.praisenter"));
				mnuImportPraisenter.setActionCommand("importPraisenterSongs");
				mnuImportPraisenter.setToolTipText(Messages.getString("dialog.song.menu.import.praisenter.tooltip"));
				mnuImportPraisenter.addActionListener(this);
				mnuImport.add(mnuImportPraisenter);
				
				JMenuItem mnuImportCVSongs = new JMenuItem(Messages.getString("dialog.song.menu.import.churchview"));
				mnuImportCVSongs.setActionCommand("importCVSongs");
				mnuImportCVSongs.setToolTipText(Messages.getString("dialog.song.menu.import.churchview.tooltip"));
				mnuImportCVSongs.addActionListener(this);
				mnuImport.add(mnuImportCVSongs);
			}
			
			// export menu
			JMenu mnuExport = new JMenu(Messages.getString("dialog.song.menu.export"));
			barMenu.add(mnuExport);
			{
				JMenuItem mnuExportSongs = new JMenuItem(Messages.getString("dialog.song.menu.export.praisenter"));
				mnuExportSongs.setToolTipText(Messages.getString("dialog.song.menu.export.praisenter.tooltip"));
				mnuExportSongs.setActionCommand("exportPraisenterSongs");
				mnuExportSongs.addActionListener(this);
				mnuExport.add(mnuExportSongs);
			}
			
			this.setJMenuBar(barMenu);
		}
		
		this.pnlSongLibrary = new SongLibraryPanel();
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.pnlSongLibrary, BorderLayout.CENTER);
		
		this.pack();
	}
	
	/**
	 * Shows a new Song Library dialog.
	 * @param owner the owner of this dialog; can be null
	 * @return boolean true if the song library was changed
	 */
	public static final boolean show(Window owner) {
		SongLibraryDialog dialog = new SongLibraryDialog(owner);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.pnlSongLibrary.isSongLibraryChanged();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("importCVSongs".equals(command)) {
			this.importChurchViewSongDatabase();
			this.pnlSongLibrary.clearSearch();
		} else if ("importPraisenterSongs".equals(command)) {
			this.importPraisenterSongDatabase();
			this.pnlSongLibrary.clearSearch();
		} else if ("exportPraisenterSongs".equals(command)) {
			this.exportSongs();
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
