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

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.praisenter.application.errors.ui.ExceptionDialog;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.ui.SelectTextFocusListener;
import org.praisenter.application.ui.WaterMark;
import org.praisenter.common.utilities.StringUtilities;
import org.praisenter.data.DataException;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.data.song.SongPartType;
import org.praisenter.data.song.Songs;
import org.praisenter.slide.SongSlideTemplate;

/**
 * Panel used to edit a song.
 * @author William Bittle
 * @version 2.0.1
 * @since 1.0.0
 */
public class EditSongPanel extends JPanel implements ActionListener, SongPartListener {
	/** The version id */
	private static final long serialVersionUID = -6559268693745087405L;
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(EditSongPanel.class);
	
	/** The song parts card name */
	private static final String CARD_PARTS = "Parts";
	
	/** The song notes card name */
	private static final String CARD_NOTES = "Notes";
	
	// data
	
	/** The song being modified */
	private Song song;
	
	// controls
	
	/** The save button */
	private JButton btnSave;
	
	/** The text box for the song title */
	private JTextField txtTitle;
	
	/** The song id */
	private JLabel lblId;
	
	/** The text box for the song notes */
	private JTextArea txtNotes;
	
	/** The table of song parts */
	private JTable tblSongParts;
	
	/** The add part button */
	private JButton btnAddPart;
	
	/** The delete parts button */
	private JButton btnDeleteSelectedParts;
	
	/** The button to toggle editing of the song notes */
	private JToggleButton btnNotes;
	
	// song parts
	
	/** The song part edit panel */
	private EditSongPartPanel pnlEditPart;
	
	// editing
	
	/** The panel for the notes/song parts cards */
	private JPanel pnlCards;
	
	/** True if the song has been changed but not saved */
	private boolean songChanged;

	/** True if notifications are disabled */
	private boolean notificationsDisabled;
	
	/**
	 * Full constructor.
	 * @param template the initial song slide template
	 */
	@SuppressWarnings("serial")
	public EditSongPanel(SongSlideTemplate template) {
		this.song = null;
		this.songChanged = false;
		this.notificationsDisabled = true;
		
		String title = "";
		String notes = "";
		List<SongPart> parts = new ArrayList<SongPart>();;
		String id = "";
		boolean edit = false;
		
		this.pnlEditPart = new EditSongPartPanel(template);
		this.pnlEditPart.addSongPartListener(this);
		
		this.btnSave = new JButton(Messages.getString("panel.song.save"));
		this.btnSave.setToolTipText(Messages.getString("panel.song.save.tooltip"));
		this.btnSave.setActionCommand("save");
		this.btnSave.addActionListener(this);
		this.btnSave.setEnabled(edit);
		
		this.txtTitle = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.song.title.watermark"));
			}
		};
		this.txtTitle.addFocusListener(new SelectTextFocusListener(this.txtTitle));
		this.txtTitle.setText(title);
		this.txtTitle.setToolTipText(Messages.getString("panel.song.title.tooltip"));
		this.txtTitle.setEnabled(edit);
		this.txtTitle.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateSongTitle(txtTitle.getText());
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateSongTitle(txtTitle.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateSongTitle(txtTitle.getText());
			}
		});
		
		this.lblId = new JLabel(id);
		this.lblId.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblId.setMinimumSize(new Dimension(40, 0));
		
		this.tblSongParts = new JTable() {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = this.rowAtPoint(p);
				// since sorting is allowed, we need to translate the view row index
				// into the model row index
				row = this.convertRowIndexToModel(row);
				
				// get the column value
				TableModel model = this.getModel();
				Object object = model.getValueAt(row, 2);
				if (object != null) {
					// get the song title
					String text = object.toString();
					if (text != null && text.length() > 0) {
						// split the lines by 50 characters
						return StringUtilities.addLineBreaksAtInterval(text, 50, true);
					}
				}
				
				return super.getToolTipText(event);
			}
		};
		this.tblSongParts.setAutoCreateRowSorter(true);
		this.tblSongParts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tblSongParts.setColumnSelectionAllowed(false);
		this.tblSongParts.setCellSelectionEnabled(false);
		this.tblSongParts.setRowSelectionAllowed(true);
		this.tblSongParts.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() > 0) {
					// double clicked
					// get the selected row
					int row = tblSongParts.rowAtPoint(e.getPoint());
					// since sorting is allowed, we need to translate the view row index
					// into the model row index
					row = tblSongParts.convertRowIndexToModel(row);
					
					// get the data
					SongPartTableModel model = (SongPartTableModel)tblSongParts.getModel();
					SongPart part = model.getRow(row);
					// set the part panel up
					pnlEditPart.setSongPart(EditSongPanel.this.song, part);
					// switch back to the parts card
					btnNotes.setSelected(false);
					CardLayout layout = (CardLayout)pnlCards.getLayout();
					layout.show(pnlCards, CARD_PARTS);
					btnNotes.setText(Messages.getString("panel.songs.notes"));
				}
			}
		});
		this.tblSongParts.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		// disable the default F2 functionality of the JTable
		this.tblSongParts.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "none");
		JScrollPane scrSongParts = new JScrollPane(this.tblSongParts);
		
		this.tblSongParts.setModel(new MutableSongPartTableModel(parts));
		this.setSongPartTableWidths();
		
		this.btnAddPart = new JButton(Messages.getString("panel.song.part.new"));
		this.btnAddPart.setToolTipText(Messages.getString("panel.song.part.new.tooltip"));
		this.btnAddPart.setActionCommand("addPart");
		this.btnAddPart.addActionListener(this);
		this.btnAddPart.setEnabled(edit);
		
		this.btnDeleteSelectedParts = new JButton(Messages.getString("panel.song.part.deleteSelected"));
		this.btnDeleteSelectedParts.setToolTipText(Messages.getString("panel.song.part.deleteSelected.tooltip"));
		this.btnDeleteSelectedParts.setActionCommand("deleteSelectedParts");
		this.btnDeleteSelectedParts.addActionListener(this);
		this.btnDeleteSelectedParts.setEnabled(edit);
		
		this.btnNotes = new JToggleButton(Messages.getString("panel.songs.notes"));
		this.btnNotes.setToolTipText(Messages.getString("panel.songs.notes.tooltip"));
		this.btnNotes.setActionCommand("notes");
		this.btnNotes.addActionListener(this);
		this.btnNotes.setEnabled(edit);
		
		this.txtNotes = new JTextArea();
		this.txtNotes.setLineWrap(true);
		this.txtNotes.setWrapStyleWord(true);
		this.txtNotes.setText(notes);
		this.txtNotes.setEnabled(edit);
		this.txtNotes.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateSongNotes(txtNotes.getText());
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateSongNotes(txtNotes.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateSongNotes(txtNotes.getText());
			}
		});
		JScrollPane pneNotes = new JScrollPane(this.txtNotes);
		
		this.pnlCards = new JPanel();
		this.pnlCards.setLayout(new CardLayout());
		
		this.pnlCards.add(this.pnlEditPart, CARD_PARTS);
		this.pnlCards.add(pneNotes, CARD_NOTES);
		
		this.notificationsDisabled = false;
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(this.txtTitle)
						.addComponent(this.lblId)
						.addComponent(this.btnSave))
				.addGroup(layout.createSequentialGroup()
						.addComponent(scrSongParts, 400, 400, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup()
								.addComponent(this.btnAddPart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(this.btnDeleteSelectedParts, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(this.btnNotes, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(this.pnlCards)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.txtTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblId)
						.addComponent(this.btnSave))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(scrSongParts, 0, 75, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.btnAddPart)
								.addComponent(this.btnDeleteSelectedParts)
								.addComponent(this.btnNotes))
						.addComponent(this.pnlCards)));
	}
	
	/**
	 * Sets the table column widths for the song search results table.
	 */
	private void setSongPartTableWidths() {
		this.tblSongParts.getColumnModel().getColumn(0).setMaxWidth(35);
		this.tblSongParts.getColumnModel().getColumn(0).setPreferredWidth(35);
		this.tblSongParts.getColumnModel().getColumn(1).setMaxWidth(75);
		this.tblSongParts.getColumnModel().getColumn(1).setPreferredWidth(75);
		this.tblSongParts.getColumnModel().getColumn(2).setPreferredWidth(100);
	}
	
	// song part changes
	
	/* (non-Javadoc)
	 * @see org.praisenter.data.song.ui.SongPartListener#songPartAdded(org.praisenter.data.song.SongPart)
	 */
	@Override
	public void songPartAdded(SongPart part) {}
	
	/* (non-Javadoc)
	 * @see org.praisenter.data.song.ui.SongPartListener#songPartChanged(org.praisenter.data.song.SongPart)
	 */
	@Override
	public void songPartChanged(SongPart part) {
		if (!this.notificationsDisabled) {
			this.setSongChanged(true);
			// we need to refresh the data in the song part table
			int selectedRow = this.tblSongParts.getSelectedRow();
			AbstractTableModel model = (AbstractTableModel)this.tblSongParts.getModel();
			model.fireTableDataChanged();
			this.tblSongParts.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.data.song.ui.SongPartListener#songPartDeleted(org.praisenter.data.song.SongPart)
	 */
	@Override
	public void songPartDeleted(SongPart part) {}

	/**
	 * Updates the song title when the song title text field is changed.
	 * @param title the new title
	 */
	private void updateSongTitle(String title) {
		if (!this.notificationsDisabled) {
			if (this.song != null) {
				this.song.setTitle(title);
				this.setSongChanged(true);
			}
		}
	}

	/**
	 * Updates the song notes when the song notes text area is changed.
	 * @param notes the new notes
	 */
	private void updateSongNotes(String notes) {
		if (!this.notificationsDisabled) {
			if (this.song != null) {
				this.song.setNotes(notes);
				this.setSongChanged(true);
			}
		}
	}
	
	/**
	 * Flags that the song has been changed or not.
	 * @param flag true if the song has been changed
	 */
	private void setSongChanged(boolean flag) {
		this.songChanged = flag;
		this.btnSave.setEnabled(flag);
	}
	
	/**
	 * Returns the song being edited.
	 * @return {@link Song}
	 */
	public Song getSong() {
		return this.song;
	}
	
	/**
	 * Returns true if the song has changed.
	 * @return boolean
	 */
	public boolean isSongChanged() {
		return this.songChanged;
	}
	
	/**
	 * Sets the current song.
	 * @param song the song; can be null
	 */
	public void setSong(Song song) {
		// only do this if the song is a different song (reference wise)
		if (this.song != song) {
			// set the new song
			this.song = song;
			this.setSongChanged(false);
			this.notificationsDisabled = true;
			
			String title = "";
			String notes = "";
			List<SongPart> parts = null;
			String id = "";
			boolean edit = false;
			
			this.pnlEditPart.setSongPart(null, null);
			if (song != null) {
				title = song.getTitle();
				notes = song.getNotes();
				parts = song.getParts();
				if (!song.isNew()) {
					id = String.valueOf(song.getId());
				} else {
					id = Messages.getString("panel.song.newId");
				}
				edit = true;
			} else {
				parts = new ArrayList<SongPart>();
			}
			
			this.txtTitle.setText(title);
			this.lblId.setText(id);
			this.txtNotes.setText(notes);
			this.txtNotes.setCaretPosition(0);
			
			this.tblSongParts.setModel(new MutableSongPartTableModel(parts));
			this.setSongPartTableWidths();
			
			this.txtTitle.setEnabled(edit);
			this.txtNotes.setEnabled(edit);
			this.btnAddPart.setEnabled(edit);
			this.btnDeleteSelectedParts.setEnabled(edit);
			this.btnNotes.setEnabled(edit);
			
			// flip back to the parts layout
			this.btnNotes.setSelected(false);
			CardLayout layout = (CardLayout)this.pnlCards.getLayout();
			layout.show(this.pnlCards, CARD_PARTS);
			this.btnNotes.setText(Messages.getString("panel.songs.notes"));
			
			this.notificationsDisabled = false;
		}
	}
	
	/**
	 * Sets the template used for the song part preview.
	 * @param template the template
	 */
	public void setTemplate(SongSlideTemplate template) {
		this.pnlEditPart.setTemplate(template);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if ("addPart".equals(command)) {
			if (this.song != null) {
				// this will add it to the song itself
				SongPart part = this.song.addSongPart(SongPartType.CHORUS, "");
				// flag that the song has changed
				this.setSongChanged(true);
				// update the table
				MutableSongPartTableModel model = (MutableSongPartTableModel)this.tblSongParts.getModel();
				model.addRow(part);
				int index = this.tblSongParts.getRowCount() - 1;
				this.tblSongParts.getSelectionModel().setSelectionInterval(index, index);
				// set the editing part to the new part
				this.pnlEditPart.setSongPart(this.song, part);
			}
		} else if ("deleteSelectedParts".equals(command)) {
			if (this.song != null) {
				MutableSongPartTableModel model = (MutableSongPartTableModel)this.tblSongParts.getModel();
				// remove the selected parts (checked ones)
				List<SongPart> parts = model.removeSelectedRows();
				// remove the parts from the song
				Iterator<SongPart> it = this.song.getParts().iterator();
				boolean sDeleted = false;
				SongPart ePart = this.pnlEditPart.getSongPart();
				while (it.hasNext()) {
					SongPart cp = it.next();
					for (SongPart part : parts) {
						// here we need to compare on type & index to make sure we delete the right ones
						if (cp.getType() == part.getType() && cp.getIndex() == part.getIndex()) {
							// see if we are editing the deleted one
							if (ePart != null && part.getType() == ePart.getType() && part.getIndex() == ePart.getIndex()) {
								sDeleted = true;
							}
							it.remove();
						}
					}
				}
				if (sDeleted) {
					// the selected line was deleted so null out the part panel
					this.pnlEditPart.setSongPart(null, null);
				} else if (ePart != null) {
					// the current part we are on was not deleted, but it needs to be
					// updated with the new taken indices
					this.pnlEditPart.setSongPart(this.song, ePart);
					// re-select the row
					int row = model.getRowIndex(ePart);
					this.tblSongParts.getSelectionModel().setSelectionInterval(row, row);
				}
				// only set the song has changed if we really deleted a part
				if (parts.size() > 0) {
					this.setSongChanged(true);
				}
			}
		} else if ("save".equals(command)) {
			try {
				boolean isNew = this.song.isNew();
				Collections.sort(this.song.getParts());
				Songs.saveSong(this.song);
				// update the id label
				this.lblId.setText(String.valueOf(this.song.getId()));
				this.setSongChanged(false);
				this.notifySongListeners(isNew);
			} catch (DataException e) {
				ExceptionDialog.show(
						this, 
						Messages.getString("panel.songs.save.exception.title"), 
						MessageFormat.format(Messages.getString("panel.songs.save.exception.text"), this.song.getTitle()), 
						e);
				LOGGER.error("Failed to save song: ", e);
			}
		} else if ("notes".equals(command)) {
			CardLayout layout = (CardLayout)this.pnlCards.getLayout();
			if (this.btnNotes.isSelected()) {
				layout.show(this.pnlCards, CARD_NOTES);
				this.btnNotes.setText(Messages.getString("panel.songs.parts"));
			} else {
				layout.show(this.pnlCards, CARD_PARTS);
				this.btnNotes.setText(Messages.getString("panel.songs.notes"));
			}
		}
	}

	/**
	 * Called when the song is saved.
	 * @param isNew true if the song was a new song
	 */
	private void notifySongListeners(boolean isNew) {
		if (!this.notificationsDisabled) {
			SongListener[] listeners = this.getListeners(SongListener.class);
			for (SongListener listener : listeners) {
				if (isNew) {
					listener.songAdded(this.song);
				} else {
					listener.songChanged(this.song);
				}
			}
		}
	}
	
	/**
	 * Adds a new song listener to this edit song panel.
	 * @param listener the song listener
	 */
	public void addSongListener(SongListener listener) {
		this.listenerList.add(SongListener.class, listener);
	}
}
