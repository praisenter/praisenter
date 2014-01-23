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

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.praisenter.application.errors.ui.ExceptionDialog;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.ui.TaskProgressDialog;
import org.praisenter.common.threading.AbstractTask;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongExporter;
import org.praisenter.data.song.SongFormat;
import org.praisenter.data.song.SongImporter;
import org.praisenter.data.song.Songs;

/**
 * Dialog to present the user with the available import or export formats.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.1
 */
public class ImportExportSongsDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 6411282307597188424L;
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(ImportExportSongsDialog.class);
	
	// controls
	
	/** The format selector */
	private JComboBox<SongFormat> cmbFormats;
	
	/** The location of the file */
	private JTextField txtFileLocation;
	
	/** The import/export button */
	private JButton btnImportExport;
	
	// state
	
	/** True if the dialog should show the export options; false if the import options should be presented */
	private boolean export;
	
	/** The selected files */
	private File[] files;
	
	/** True if songs were imported */
	private boolean updated;
	
	/**
	 * Minimal constructor.
	 * @param owner the dialog owner
	 * @param export true if the dialog should show the export options; false if the import options should be presented
	 */
	private ImportExportSongsDialog(Window owner, boolean export) {
		super(owner, export ? Messages.getString("dialog.song.export.title") : Messages.getString("dialog.song.import.title"), ModalityType.APPLICATION_MODAL);
		this.export = export;
		this.updated = false;
		
		JLabel lblFormat = new JLabel(Messages.getString("dialog.song.importExport.format"));
		JLabel lblFile = new JLabel(Messages.getString("dialog.song.importExport.file"));
		
		SongFormat[] formats = null;
		if (export) {
			formats = SongFormat.getSupportedExportFormats();
		} else {
			formats = SongFormat.getSupportedImportFormats();
		}
		this.cmbFormats = new JComboBox<SongFormat>(formats);
		this.cmbFormats.setSelectedItem(SongFormat.PRAISENTER);
		this.cmbFormats.setRenderer(new SongFormatListCellRenderer());
		
		this.txtFileLocation = new JTextField();
		this.txtFileLocation.setEditable(false);
		this.txtFileLocation.setColumns(30);
		
		JButton btnBrowse = new JButton(Messages.getString("dialog.song.importExport.browse"));
		btnBrowse.addActionListener(this);
		btnBrowse.setActionCommand("browse");
		
		if (export) {
			this.btnImportExport = new JButton(Messages.getString("dialog.song.importExport.export"));
			this.btnImportExport.setActionCommand("export");
		} else {
			this.btnImportExport = new JButton(Messages.getString("dialog.song.importExport.import"));
			this.btnImportExport.setActionCommand("import");
		}
		this.btnImportExport.addActionListener(this);
		this.btnImportExport.setEnabled(false);
		
		JButton btnCancel = new JButton(Messages.getString("dialog.song.importExport.cancel"));
		btnCancel.setActionCommand("cancel");
		btnCancel.addActionListener(this);
		
		Container container = this.getContentPane();
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblFormat)
						.addComponent(lblFile))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(this.cmbFormats)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtFileLocation)
								.addComponent(btnBrowse))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.btnImportExport)
								.addComponent(btnCancel))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblFormat)
						.addComponent(this.cmbFormats))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblFile)
						.addComponent(this.txtFileLocation)
						.addComponent(btnBrowse))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.btnImportExport)
						.addComponent(btnCancel)));
		
		this.pack();
	}
	
	/**
	 * Shows a new Song import/export dialog.
	 * @param owner the owner of this dialog; can be null
	 * @param export true if the dialog should show the export options; false if the import options should be presented
	 * @return boolean true if the song library was changed
	 */
	public static final boolean show(Window owner, boolean export) {
		ImportExportSongsDialog dialog = new ImportExportSongsDialog(owner, export);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.updated;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		// get the currently selected format
		SongFormat format = (SongFormat)this.cmbFormats.getSelectedItem();
		if ("import".equals(command)) {
			this.importSongs(format);
		} else if ("export".equals(command)) {
			this.exportSongs(format);
		} else if ("cancel".equals(command)) {
			this.setVisible(false);
		} else if ("browse".equals(command)) {
			if (this.export) {
				JFileChooser fileBrowser = new JFileChooser();
				fileBrowser.setMultiSelectionEnabled(false);
				fileBrowser.setDialogTitle(Messages.getString("dialog.export.songs.title"));
				fileBrowser.setSelectedFile(new File(Messages.getString("dialog.export.songs.defaultFileName")));
				
				int option = fileBrowser.showSaveDialog(this);
				// check the option
				if (option == JFileChooser.APPROVE_OPTION) {
					File file = fileBrowser.getSelectedFile();
					this.files = new File[] { file };
					this.txtFileLocation.setText(file.getAbsolutePath());
					this.btnImportExport.setEnabled(true);
				}
			} else {
				JFileChooser fileBrowser = new JFileChooser();
				fileBrowser.setDialogTitle(Messages.getString("dialog.open.title"));
				if (format == SongFormat.OPENLYRICS) {
					fileBrowser.setMultiSelectionEnabled(true);
				} else {
					fileBrowser.setMultiSelectionEnabled(false);
				}
				int option = fileBrowser.showOpenDialog(this);
				// check the option
				if (option == JFileChooser.APPROVE_OPTION) {
					// get the selected file(s)
					String filePath = null;
					if (format == SongFormat.OPENLYRICS) {
						this.files = fileBrowser.getSelectedFiles();
						StringBuilder sb = new StringBuilder();
						for (File file : this.files) {
							sb.append(file.getAbsolutePath()).append(";");
						}
						filePath = sb.toString();
					} else {
						File file = fileBrowser.getSelectedFile();
						this.files = new File[] { file };
						filePath = file.getAbsolutePath();
					}
					this.txtFileLocation.setText(filePath);
					this.btnImportExport.setEnabled(true);
				}
			}
		}
	}
	
	/**
	 * Exports the entire song database to an xml file.
	 * @param format the song file format
	 */
	private void exportSongs(final SongFormat format) {
		if (this.files != null && this.files.length == 1) {
			final File file = this.files[0];
			if (file.exists()) {
				// see if the user is ok with it
				int response = JOptionPane.showConfirmDialog(
								this,
								MessageFormat.format(Messages.getString("save.replace.text"), file.getName()),
								Messages.getString("save.replace.title"),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE);
				if (response != JOptionPane.YES_OPTION)
					return;
			}
			
			// create a new file task
			AbstractTask task = new AbstractTask() {
				@Override
				public void run() {
					try {
						// load up the songs
						List<Song> songs = Songs.getSongs();
						// export them
						SongExporter.exportSongs(file.getAbsolutePath(), songs, format);
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
				this.setVisible(false);
			} else {
				LOGGER.error("An error occurred while exporting the songs:", task.getException());
				ExceptionDialog.show(
						this,
						Messages.getString("dialog.export.songs.error.title"), 
						Messages.getString("dialog.export.songs.error.text"), 
						task.getException());
			}
		}
	}
	
	/**
	 * Imports the user selected song file.
	 * @param format the song file format
	 */
	private void importSongs(final SongFormat format) {
		if (this.files != null && this.files.length > 0) {
			// make sure they are sure
			int option = JOptionPane.showConfirmDialog(this, 
					this.files.length == 1 ? Messages.getString("dialog.import.songs.prompt.text") : Messages.getString("dialog.import.songs.prompt.text.multiple"), 
					this.files.length == 1 ? MessageFormat.format(Messages.getString("dialog.import.songs.prompt.title"), this.files[0].getName()) : Messages.getString("dialog.import.songs.prompt.title.multiple"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			// check the user's choice
			if (option == JOptionPane.YES_OPTION) {
				final File[] files = this.files;
				// we need to execute this in a separate process
				// and show a progress monitor
				AbstractTask task = new AbstractTask() {
					@Override
					public void run() {
						try {
							// import the bible
							for (File file : files) {
								SongImporter.importSongs(file, format);
							}
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
					this.updated = true;
					// show a success message
					JOptionPane.showMessageDialog(this, 
							Messages.getString("dialog.import.songs.success.text"), 
							Messages.getString("dialog.import.songs.success.title"), 
							JOptionPane.INFORMATION_MESSAGE);
					this.setVisible(false);
				} else {
					Exception e = task.getException();
					// show an error message
					ExceptionDialog.show(
							this,
							Messages.getString("dialog.import.songs.failed.title"), 
							Messages.getString("dialog.import.songs.failed.text"), 
							e);
					LOGGER.error("An error occurred while importing the song file:", e);
				}
			}
		}
	}
}
