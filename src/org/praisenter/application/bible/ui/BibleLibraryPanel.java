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
package org.praisenter.application.bible.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;
import org.praisenter.application.errors.ui.ExceptionDialog;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.ui.OpenUrlHyperlinkListener;
import org.praisenter.application.ui.TaskProgressDialog;
import org.praisenter.application.ui.ZipFileFilter;
import org.praisenter.common.threading.AbstractTask;
import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.data.DataException;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Bibles;
import org.praisenter.data.bible.UnboundBibleImporter;

/**
 * Panel used to maintain bibles.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class BibleLibraryPanel extends JPanel implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -5206697345242295524L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(BibleLibraryPanel.class);
	
	/** The table of bibles */
	private JTable tblBibles;
	
	// state
	
	/** True if the bible library was updated */
	private boolean bibleLibraryUpdated;
	
	/**
	 * Default constructor.
	 */
	public BibleLibraryPanel() {
		this.bibleLibraryUpdated = false;
		
		// get the bibles
		List<Bible> bibles;
		try {
			bibles = Bibles.getBibles();
		} catch (DataException e) {
			LOGGER.error("An error occurred while reading the bibles from the database: ", e);
			bibles = new ArrayList<Bible>();
		}
		
		// create the bible table
		this.tblBibles = new JTable(new MutableBibleTableModel(bibles));
		this.tblBibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tblBibles.setColumnSelectionAllowed(false);
		this.tblBibles.setCellSelectionEnabled(false);
		this.tblBibles.setRowSelectionAllowed(true);
		this.tblBibles.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setBibleTableWidths();
		
		// wrap the bible table in a scroll pane
		JScrollPane scrBibles = new JScrollPane(this.tblBibles);

		JButton btnImport = new JButton(Messages.getString("panel.bible.import.unbound"));
		btnImport.setToolTipText(Messages.getString("panel.bible.import.unbound.tooltip"));
		btnImport.addActionListener(this);
		btnImport.setActionCommand("import");
		btnImport.setMinimumSize(new Dimension(0, 50));
		Font font = btnImport.getFont();
		btnImport.setFont(font.deriveFont(Font.BOLD, font.getSize2D() + 2.0f));
		
		// add the about text section with clickable links
		JTextPane importNotes = new JTextPane();
		importNotes.setEditable(false);
		importNotes.setContentType("text/html");
		importNotes.setText(Messages.getString("panel.bible.import.unbound.notes"));
		// add a hyperlink listener to open links in the default browser
		importNotes.addHyperlinkListener(new OpenUrlHyperlinkListener());
		JScrollPane scrImportNotes = new JScrollPane(importNotes);
		
		JButton btnRemoveSelected = new JButton(Messages.getString("panel.bible.removeSelected"));
		btnRemoveSelected.setToolTipText(Messages.getString("panel.bible.removeSelected.tooltip"));
		btnRemoveSelected.addActionListener(this);
		btnRemoveSelected.setActionCommand("remove-selected");
		
		JButton btnRemoveAll = new JButton(Messages.getString("panel.bible.removeAll"));
		btnRemoveAll.setToolTipText(Messages.getString("panel.bible.removeAll.tooltip"));
		btnRemoveAll.addActionListener(this);
		btnRemoveAll.setActionCommand("remove-all");
		
		JPanel pnlRight = new JPanel();
		GroupLayout rLayout = new GroupLayout(pnlRight);
		pnlRight.setLayout(rLayout);
		
		rLayout.setAutoCreateContainerGaps(true);
		rLayout.setAutoCreateGaps(true);
		rLayout.setHorizontalGroup(rLayout.createParallelGroup()
				.addComponent(btnImport, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnRemoveSelected, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnRemoveAll, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(scrImportNotes, 150, 200, Short.MAX_VALUE));
		rLayout.setVerticalGroup(rLayout.createSequentialGroup()
				.addComponent(btnImport)
				.addComponent(btnRemoveSelected)
				.addComponent(btnRemoveAll)
				.addComponent(scrImportNotes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		
		JSplitPane pneSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrBibles, pnlRight);
		pneSplit.setOneTouchExpandable(true);
		pneSplit.setResizeWeight(1.0);
		
		this.setLayout(new BorderLayout());
		this.add(pneSplit, BorderLayout.CENTER);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("remove-selected".equals(command)) {
			this.removeSelectedBibles();
		} else if ("remove-all".equals(command)) {
			this.removeAllBibles();
		} else if ("import".equals(command)) {
			this.importUnboundBible();
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
			final File file = fileBrowser.getSelectedFile();
			// make sure it exists and its a file
			if (file.exists() && file.isFile()) {
				// make sure they are sure
				option = JOptionPane.showConfirmDialog(this, 
						Messages.getString("panel.bible.import.prompt.text"), 
						MessageFormat.format(Messages.getString("panel.bible.import.prompt.title"), file.getName()), 
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
								UnboundBibleImporter.importBible(file);
								setSuccessful(true);
							} catch (Exception e) {
								// handle the exception
								handleException(e);
							}
						}
					};
					// show a task progress bar
					TaskProgressDialog.show(
							WindowUtilities.getParentWindow(this), 
							Messages.getString("importing"), 
							task);
					// show a message either way
					if (task.isSuccessful()) {
						this.bibleLibraryUpdated = true;
						
						// show a success message
						JOptionPane.showMessageDialog(this, 
								Messages.getString("panel.bible.import.success.text"), 
								Messages.getString("panel.bible.import.success.title"), 
								JOptionPane.INFORMATION_MESSAGE);
						
						// update the bible table
						// get the bibles
						List<Bible> bibles;
						try {
							bibles = Bibles.getBibles();
						} catch (DataException e) {
							LOGGER.error("An error occurred while reading the bibles from the database: ", e);
							bibles = new ArrayList<Bible>();
						}
						
						MutableBibleTableModel model = (MutableBibleTableModel)this.tblBibles.getModel();
						model.removeAllRows();
						model.addRows(bibles);
					} else {
						Exception e = task.getException();
						// show an error message
						ExceptionDialog.show(
								this,
								Messages.getString("panel.bible.import.failed.title"), 
								Messages.getString("panel.bible.import.failed.text"), 
								e);
						LOGGER.error("An error occurred while importing an Unbound Bible bible:", e);
					}
				}
			}
		}
	}
	
	/**
	 * Attempts to remove the selected bibles.
	 */
	private void removeSelectedBibles() {
		MutableBibleTableModel model = (MutableBibleTableModel)this.tblBibles.getModel();
		final List<Bible> bibles = model.getSelectedRows();
		
		// make sure they selected at least one bible to remove
		if (bibles.size() > 0) {
			// make sure they are sure they want to do this
			int option = JOptionPane.showConfirmDialog(this, 
					Messages.getString("panel.bible.remove.selected.prompt.text"), 
					Messages.getString("panel.bible.remove.selected.prompt.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			// check the user's choice
			if (option == JOptionPane.YES_OPTION) {
				// remove the bibles in a separate task
				AbstractTask task = new AbstractTask() {
					@Override
					public void run() {
						try {
							// import the bible
							for (Bible bible : bibles) {
								Bibles.deleteBible(bible);
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
						WindowUtilities.getParentWindow(this), 
						Messages.getString("removing"), 
						task);
				// show a message either way
				if (task.isSuccessful()) {
					this.bibleLibraryUpdated = true;
					// remove the items from the table
					model.removeSelectedRows();
				} else {
					Exception e = task.getException();
					// show an error message
					ExceptionDialog.show(
							this,
							Messages.getString("panel.bible.remove.failed.title"), 
							Messages.getString("panel.bible.remove.failed.text"), 
							e);
					LOGGER.error("An error occurred while removing a bible:", e);
				}
			}
		}
	}
	
	/**
	 * Attempts to remove all the bibles.
	 */
	private void removeAllBibles() {
		MutableBibleTableModel model = (MutableBibleTableModel)this.tblBibles.getModel();
		final List<Bible> bibles = model.getRows();
		
		if (bibles.size() > 0) {
			// make sure they are sure they want to do this
			int option = JOptionPane.showConfirmDialog(this, 
					Messages.getString("panel.bible.remove.all.prompt.text"), 
					Messages.getString("panel.bible.remove.all.prompt.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			// check the user's choice
			if (option == JOptionPane.YES_OPTION) {
				// remove the bibles in a separate task
				AbstractTask task = new AbstractTask() {
					@Override
					public void run() {
						try {
							// import the bible
							for (Bible bible : bibles) {
								Bibles.deleteBible(bible);
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
						WindowUtilities.getParentWindow(this), 
						Messages.getString("removing"), 
						task);
				// show a message either way
				if (task.isSuccessful()) {
					this.bibleLibraryUpdated = true;
					// remove the items from the table
					model.removeSelectedRows();
				} else {
					Exception e = task.getException();
					// show an error message
					ExceptionDialog.show(
							this,
							Messages.getString("panel.bible.remove.failed.title"), 
							Messages.getString("panel.bible.remove.failed.text"), 
							e);
					LOGGER.error("An error occurred while removing a bible:", e);
				}
			}
		}
	}
	
	/**
	 * Sets the table column widths for the bible table.
	 */
	private void setBibleTableWidths() {
		this.tblBibles.getColumnModel().getColumn(0).setMaxWidth(35);
		this.tblBibles.getColumnModel().getColumn(0).setPreferredWidth(35);
		this.tblBibles.getColumnModel().getColumn(1).setPreferredWidth(200);
		this.tblBibles.getColumnModel().getColumn(2).setPreferredWidth(50);
		this.tblBibles.getColumnModel().getColumn(3).setPreferredWidth(250);
	}
	
	/**
	 * Returns true if the bible library was updated.
	 * @return boolean
	 */
	public boolean isBibleLibraryUpdated() {
		return this.bibleLibraryUpdated;
	}
}
