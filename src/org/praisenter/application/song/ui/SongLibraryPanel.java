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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.praisenter.application.errors.ui.ExceptionDialog;
import org.praisenter.application.preferences.Preferences;
import org.praisenter.application.preferences.SongPreferences;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.slide.ui.SlideLibraryDialog;
import org.praisenter.application.slide.ui.SlideLibraryListener;
import org.praisenter.application.slide.ui.SlideThumbnailComboBoxRenderer;
import org.praisenter.application.ui.SelectTextFocusListener;
import org.praisenter.application.ui.WaterMark;
import org.praisenter.common.NotInitializedException;
import org.praisenter.common.utilities.StringUtilities;
import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.data.DataException;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.data.song.SongPartType;
import org.praisenter.data.song.Songs;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideLibraryException;
import org.praisenter.slide.SlideThumbnail;
import org.praisenter.slide.SongSlideTemplate;

/**
 * Panel used to manage the song library.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.1
 */
public class SongLibraryPanel extends JPanel implements ActionListener, ItemListener, SongPartListener, SlideLibraryListener {
	/** The version id */
	private static final long serialVersionUID = 9093357926870453088L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SongLibraryPanel.class);
	
	// song searching
	
	/** The song searching thread */
	private SongSearchThread songSearchThread;
	
	/** The song search text field */
	private JTextField txtSongSearch;
	
	/** The song search results table */
	private JTable tblSongSearchResults;
	
	/** The song search results table scroll pane */
	private JScrollPane scrSongSearchResults;
	
	/** The template combo box */
	private JComboBox<Object> cmbTemplates;
	
	// song editing
	
	/** The button to discard changes */
	private JButton btnDiscardChanges;
	
	/** The save button */
	private JButton btnSave;
	
	/** The text box for the song title */
	private JTextField txtTitle;
	
	/** The song id */
	private JLabel lblId;
	
	/** The text box for the song notes */
	private JTextArea txtNotes;
	
	/** The add part button */
	private JButton btnAddPart;
	
	/** The delete parts button */
	private JButton btnDeleteParts;
	
	// parts
	
	/** The panel containing the list of part edit panels */
	private JPanel pnlSongParts;
	
	/** The scroll pane for the song parts */
	private JScrollPane scrSongParts;

	// data
	
	/** The current song */
	private Song song;
	
	/** True if the current song has been changed */
	private boolean songChanged;
	
	/** True if notifications are disabled */
	private boolean notificationsDisabled;
	
	/** True if the song library has been changed */
	private boolean songLibraryChanged;
	
	/** This is used to store the previously selected template */
	private Object previouslySelectedTemplate;
	
	// preferences
	
	/** Local reference to the preferences */
	private Preferences preferences = Preferences.getInstance();
	
	/** Local reference to the song preferences */
	private SongPreferences sPreferences = preferences.getSongPreferences();
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("serial")
	public SongLibraryPanel() {
		this.notificationsDisabled = true;
		this.songLibraryChanged = false;
		
		// create and start the song search thread
		this.songSearchThread = new SongSearchThread();
		this.songSearchThread.start();
		
		this.txtSongSearch = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.song.search.watermark"));
			}
		};
		this.txtSongSearch.setActionCommand("search");
		this.txtSongSearch.addActionListener(this);
		this.txtSongSearch.addFocusListener(new SelectTextFocusListener(this.txtSongSearch));
		
		JButton btnSongSearch = new JButton(Messages.getString("panel.song.search"));
		btnSongSearch.setToolTipText(Messages.getString("panel.song.search.tooltip"));
		btnSongSearch.setActionCommand("search");
		btnSongSearch.addActionListener(this);
		
		List<Song> songs = null;
		try {
			songs = Songs.getSongs(false);
		} catch (DataException e) {
			songs = new ArrayList<Song>();
			LOGGER.error("An error occurred while trying to load all songs.", e);
		}
		this.tblSongSearchResults = new JTable(new MutableSongTableModel(songs)) {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = this.rowAtPoint(p);
				if (row < 0) return super.getToolTipText();
				// since sorting is allowed, we need to translate the view row index
				// into the model row index
				row = this.convertRowIndexToModel(row);
				
				// get the column value
				TableModel model = this.getModel();
				Object object = model.getValueAt(row, 1);
				if (object != null) {
					// get the song title
					String text = object.toString();
					// split the lines by 50 characters
					return StringUtilities.addLineBreaksAtInterval(text, 50, true);
				}
				
				return super.getToolTipText(event);
			}
		};
		this.tblSongSearchResults.setAutoCreateRowSorter(true);
		this.tblSongSearchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tblSongSearchResults.setColumnSelectionAllowed(false);
		this.tblSongSearchResults.setCellSelectionEnabled(false);
		this.tblSongSearchResults.setRowSelectionAllowed(true);
		this.tblSongSearchResults.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.tblSongSearchResults.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// make sure its a double click
				if (e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON1) {
					if (songChanged) {
						int choice = JOptionPane.showConfirmDialog(
								WindowUtilities.getParentWindow(SongLibraryPanel.this), 
								MessageFormat.format(Messages.getString("panel.song.switch.confirm.message"), song.getTitle()), 
								Messages.getString("panel.song.switch.confirm.title"), 
								JOptionPane.YES_NO_CANCEL_OPTION);
						if (choice == JOptionPane.YES_OPTION) {
							// then save the song
							try {
								Collections.sort(song.getParts());
								Songs.saveSong(song);
							} catch (DataException ex) {
								// if the song fails to be saved then show a message
								// and dont continue
								ExceptionDialog.show(
										SongLibraryPanel.this, 
										Messages.getString("panel.song.save.exception.title"), 
										MessageFormat.format(Messages.getString("panel.song.save.exception.text"), song.getTitle()), 
										ex);
								LOGGER.error("Failed to save song: ", ex);
								return;
							}
						} else if (choice == JOptionPane.CANCEL_OPTION) {
							// don't continue
							return;
						}
					}
					// assume single click
					// go to the data store and get the full song detail
					// get the selected row
					int row = tblSongSearchResults.rowAtPoint(e.getPoint());
					if (row < 0) return;
					// since sorting is allowed, we need to translate the view row index
					// into the model row index
					row = tblSongSearchResults.convertRowIndexToModel(row);
					
					// get the data
					SongTableModel model = (SongTableModel)tblSongSearchResults.getModel();
					Song song = model.getRow(row);
					// see if its the same song
					if (SongLibraryPanel.this.song == null || (song.getId() != SongLibraryPanel.this.song.getId())) {
						// if its not the same song then load it up
						try {
							// overwrite the song local variable
							song = Songs.getSong(song.getId());
							setSong(song);
						} catch (DataException ex) {
							// just log this exception because the user
							// should still be able to click on the row again
							LOGGER.error("An error occurred while selecting a song from the song search list: ", ex);
						}
					}
				}
			}
		});
		this.scrSongSearchResults = new JScrollPane(this.tblSongSearchResults);
		setSongTableWidths();
		
		SlideThumbnail[] thumbnails = this.getThumbnails();
		SlideThumbnail selected = this.getSelectedThumbnail(thumbnails);
		this.cmbTemplates = new JComboBox<Object>((Object[])thumbnails);
		// add the "manage templates" item
		this.cmbTemplates.addItem(Messages.getString("template.manage"));
		if (selected != null) {
			this.cmbTemplates.setSelectedItem(selected);
			this.previouslySelectedTemplate = selected;
		}
		this.cmbTemplates.setToolTipText(Messages.getString("panel.song.template"));
		this.cmbTemplates.setRenderer(new SlideThumbnailComboBoxRenderer());
		this.cmbTemplates.addItemListener(this);
		
		JButton btnImport = new JButton(Messages.getString("panel.song.import"));
		btnImport.setToolTipText(Messages.getString("panel.song.import.tooltip"));
		btnImport.setActionCommand("import");
		btnImport.addActionListener(this);
		
		JButton btnExport = new JButton(Messages.getString("panel.song.export"));
		btnExport.setToolTipText(Messages.getString("panel.song.export.tooltip"));
		btnExport.setActionCommand("export");
		btnExport.addActionListener(this);
		
		JButton btnNew = new JButton(Messages.getString("panel.song.newSong"));
		btnNew.setToolTipText(Messages.getString("panel.song.newSong.tooltip"));
		btnNew.setActionCommand("new");
		btnNew.addActionListener(this);
		
		JButton btnDeleteSelected = new JButton(Messages.getString("panel.song.deleteSelected"));
		btnDeleteSelected.setToolTipText(Messages.getString("panel.song.deleteSelected.tooltip"));
		btnDeleteSelected.setActionCommand("deleteSelectedSongs");
		btnDeleteSelected.addActionListener(this);
		
		this.txtTitle = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.song.title.watermark"));
			}
		};
		this.txtTitle.addFocusListener(new SelectTextFocusListener(this.txtTitle));
		this.txtTitle.setText("");
		this.txtTitle.setToolTipText(Messages.getString("panel.song.title.tooltip"));
		this.txtTitle.setEnabled(false);
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
		
		this.lblId = new JLabel(" ");
		this.lblId.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblId.setMinimumSize(new Dimension(40, 0));
		
		this.txtNotes = new JTextArea();
		this.txtNotes.setLineWrap(true);
		this.txtNotes.setWrapStyleWord(true);
		this.txtNotes.setEnabled(false);
		this.txtNotes.setToolTipText(Messages.getString("panel.song.library.notes.tooltip"));
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
		pneNotes.setMinimumSize(new Dimension(0, 75));
		
		this.btnAddPart = new JButton(Messages.getString("panel.song.part.newPart"));
		this.btnAddPart.setToolTipText(Messages.getString("panel.song.part.newPart.tooltip"));
		this.btnAddPart.setActionCommand("addPart");
		this.btnAddPart.addActionListener(this);
		this.btnAddPart.setEnabled(false);
		
		this.btnDeleteParts = new JButton(Messages.getString("panel.song.part.deleteParts"));
		this.btnDeleteParts.setToolTipText(Messages.getString("panel.song.part.deleteParts.tooltip"));
		this.btnDeleteParts.setActionCommand("deleteParts");
		this.btnDeleteParts.addActionListener(this);
		this.btnDeleteParts.setEnabled(false);
		
		this.btnDiscardChanges = new JButton(Messages.getString("panel.song.discard"));
		this.btnDiscardChanges.setToolTipText(Messages.getString("panel.song.discard.tooltip"));
		this.btnDiscardChanges.setActionCommand("discard");
		this.btnDiscardChanges.addActionListener(this);
		this.btnDiscardChanges.setEnabled(false);
		
		this.btnSave = new JButton(Messages.getString("panel.song.save"));
		this.btnSave.setToolTipText(Messages.getString("panel.song.save.tooltip"));
		this.btnSave.setActionCommand("save");
		this.btnSave.addActionListener(this);
		this.btnSave.setEnabled(false);
		
		this.notificationsDisabled = false;
		
		JPanel pnlImportExportDeleteAdd = new JPanel();
		pnlImportExportDeleteAdd.setLayout(new GridLayout(2, 2));
		pnlImportExportDeleteAdd.add(btnImport);
		pnlImportExportDeleteAdd.add(btnExport);
		pnlImportExportDeleteAdd.add(btnDeleteSelected);
		pnlImportExportDeleteAdd.add(btnNew);
		
		JPanel pnlDeleteAddDiscardSave = new JPanel();
		pnlDeleteAddDiscardSave.setLayout(new GridLayout(2, 2));
		pnlDeleteAddDiscardSave.add(this.btnDeleteParts);
		pnlDeleteAddDiscardSave.add(this.btnAddPart);
		pnlDeleteAddDiscardSave.add(this.btnDiscardChanges);
		pnlDeleteAddDiscardSave.add(this.btnSave);
		
		JPanel pnlLeft = new JPanel();
		GroupLayout leftLayout = new GroupLayout(pnlLeft);
		pnlLeft.setLayout(leftLayout);
		pnlLeft.setPreferredSize(new Dimension(250, 600));
		
		JSeparator sep = new JSeparator();
		
		leftLayout.setAutoCreateContainerGaps(true);
		leftLayout.setAutoCreateGaps(true);
		leftLayout.setHorizontalGroup(leftLayout.createParallelGroup()
				.addGroup(leftLayout.createSequentialGroup()
						.addComponent(this.txtSongSearch)
						.addComponent(btnSongSearch))
				.addComponent(this.scrSongSearchResults)
				.addComponent(this.cmbTemplates, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pnlImportExportDeleteAdd, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(sep)
				.addGroup(leftLayout.createSequentialGroup()
						.addComponent(this.txtTitle)
						.addComponent(this.lblId))
				.addComponent(pneNotes)
				.addComponent(pnlDeleteAddDiscardSave, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		leftLayout.setVerticalGroup(leftLayout.createSequentialGroup()
				.addGroup(leftLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.txtSongSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSongSearch))
				.addComponent(this.scrSongSearchResults)
				.addComponent(this.cmbTemplates)
				.addComponent(pnlImportExportDeleteAdd)
				.addComponent(sep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(leftLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.txtTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(pneNotes)
				.addComponent(pnlDeleteAddDiscardSave));
		
		this.pnlSongParts = new JPanel();
		this.pnlSongParts.setLayout(new GridLayout(0, 1));
		this.pnlSongParts.setMinimumSize(new Dimension(550, 0));
		this.scrSongParts = new JScrollPane(this.pnlSongParts, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.scrSongParts.setMinimumSize(new Dimension(620, 0));
		this.scrSongParts.setPreferredSize(new Dimension(620, 600));
		this.scrSongParts.getVerticalScrollBar().setUnitIncrement(20);
		this.scrSongParts.setBorder(BorderFactory.createEmptyBorder());
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlLeft, this.scrSongParts);
		split.setResizeWeight(0.5);
		split.setBorder(BorderFactory.createEmptyBorder());
		split.setOneTouchExpandable(true);
		
		this.setLayout(new BorderLayout());
		this.add(split, BorderLayout.CENTER);
	}
	
	/**
	 * Returns the selected template.
	 * @return {@link SongSlideTemplate}
	 */
	private SongSlideTemplate getSelectedTemplate() {
		// get the primary device size
		Dimension displaySize = this.preferences.getPrimaryOrDefaultDeviceResolution();
		
		// get the selected template
		SlideThumbnail thumb = (SlideThumbnail)this.cmbTemplates.getSelectedItem();
		SongSlideTemplate template = null;
		try {
			SlideLibrary library = SlideLibrary.getInstance();
			if (thumb == null) {
				// if thats null, then get the perferences template
				String templatePath = this.sPreferences.getTemplate();
				if (templatePath != null && templatePath.trim().length() > 0) {
					try {
						template = library.getTemplate(templatePath, SongSlideTemplate.class);
					} catch (SlideLibraryException e) {
						LOGGER.error("Unable to load preferences song template [" + templatePath + "]: ", e);
					}
				}
			} else {
				if (thumb.getFile() != SlideFile.NOT_STORED) {
					try {
						template = library.getTemplate(thumb.getFile(), SongSlideTemplate.class);
					} catch (SlideLibraryException e) {
						LOGGER.error("Unable to load selected song template [" + thumb.getFile().getRelativePath() + "]: ", e);
					}
				}
			}
		} catch (NotInitializedException e) {
			LOGGER.error(e);
		}
		// if we couldnt get a template then just use the default
		if (template == null) {
			// if its still null, then use the default template
			template = SongSlideTemplate.getDefaultTemplate(displaySize.width, displaySize.height);
		}
		
		// check the template size against the display size
		this.verifyTemplateDimensions(template, displaySize);
		
		return template;
	}
	
	/**
	 * Verifies the template is sized to the given size.
	 * <p>
	 * If not, the template is adjusted to fit.
	 * @param template the template
	 * @param size the size
	 */
	private void verifyTemplateDimensions(SongSlideTemplate template, Dimension size) {
		// check the template size against the display size
		if (template.getWidth() != size.width || template.getHeight() != size.height) {
			// log a message and modify the template to fit
			LOGGER.warn("Template is not sized correctly for the primary display. Adjusing template.");
			template.adjustSize(size.width, size.height);
		}
	}
	
	/**
	 * Returns an array of {@link SlideThumbnail}s for {@link SongSlideTemplate}s.
	 * @return {@link SlideThumbnail}[]
	 */
	private SlideThumbnail[] getThumbnails() {
		Dimension displaySize = this.preferences.getPrimaryOrDefaultDeviceResolution();
		
		List<SlideThumbnail> thumbs = null;
		try {
			thumbs = SlideLibrary.getInstance().getThumbnails(SongSlideTemplate.class);
		} catch (NotInitializedException e) {
			// just ignore the error
			thumbs = new ArrayList<SlideThumbnail>();
		}
		
		// add in the default template
		SongSlideTemplate dTemplate = SongSlideTemplate.getDefaultTemplate(displaySize.width, displaySize.height);
		BufferedImage image = dTemplate.getThumbnail(SlideLibrary.THUMBNAIL_SIZE);
		SlideThumbnail temp = new SlideThumbnail(SlideFile.NOT_STORED, dTemplate.getName(), image);
		thumbs.add(temp);
		// sort them
		Collections.sort(thumbs);
		
		return thumbs.toArray(new SlideThumbnail[0]);
	}
	
	/**
	 * Returns the selected thumbnail for the selected {@link SongSlideTemplate}
	 * given in the preferences.
	 * @param thumbnails the list of all slide thumbnails
	 * @return {@link SlideThumbnail}
	 */
	private SlideThumbnail getSelectedThumbnail(SlideThumbnail[] thumbnails) {
		for (SlideThumbnail thumb : thumbnails) {
			if (thumb.getFile() == SlideFile.NOT_STORED) {
				if (this.sPreferences.getTemplate() == null) {
					return thumb;
				}
			} else if (thumb.getFile().getRelativePath().equals(this.sPreferences.getTemplate())) {
				return thumb;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.SlideLibraryListener#slideLibraryChanged()
	 */
	@Override
	public void slideLibraryChanged() {
		// update the template listing and previews
		SlideThumbnail[] thumbnails = this.getThumbnails();
		
		// update the list of templates
		SlideThumbnail selected = (SlideThumbnail)this.cmbTemplates.getSelectedItem();
		if (selected == null) {
			selected = this.getSelectedThumbnail(thumbnails);
		}
		this.cmbTemplates.removeAllItems();
		for (SlideThumbnail thumb : thumbnails) {
			this.cmbTemplates.addItem(thumb);
		}
		// add the "manage templates" item
		this.cmbTemplates.addItem(Messages.getString("template.manage"));
		
		// set the selected one
		// selecting the item in the combo box will update the template
		// and the preview panel
		if (selected != null) {
			this.cmbTemplates.setSelectedItem(selected);
		} else {
			this.cmbTemplates.setSelectedIndex(0);
		}
		
		// update the parts preview
		SongSlideTemplate template = this.getSelectedTemplate();
		Component[] components = this.pnlSongParts.getComponents();
		for (Component component : components) {
			if (component instanceof EditSongPartPanel) {
				EditSongPartPanel spp = (EditSongPartPanel)component;
				spp.setTemplate(template);
			}
		}
		this.pnlSongParts.repaint();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		// on deselection of a template, set the previously selected
		// template.  This is useful for when the user selects the
		// "Manage Templates.." option.  We will swap back the selected
		// item to the previous (since the user cant actually "select"
		// the manage template item)
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			Object source = e.getSource();
			if (source == this.cmbTemplates) {
				this.previouslySelectedTemplate = e.getItem();
			}
		} else if (e.getStateChange() == ItemEvent.SELECTED) {
			Object source = e.getSource();
			if (source == this.cmbTemplates) {
				Object value = this.cmbTemplates.getSelectedItem();
				if (value instanceof SlideThumbnail) {
					SlideThumbnail thumbnail = (SlideThumbnail)value;
					if (thumbnail != null) {
						// refresh the song preview panels
						SongSlideTemplate template = this.getSelectedTemplate();
						Component[] components = this.pnlSongParts.getComponents();
						for (Component component : components) {
							if (component instanceof EditSongPartPanel) {
								EditSongPartPanel spp = (EditSongPartPanel)component;
								spp.setTemplate(template);
							}
						}
					}
				} else {
					// if the selected item is not a SlideThumbnail, then its the "Manage Templates..." item
					// hide the drop down popup
					this.cmbTemplates.hidePopup();
					// set the selected item back to the original template
					if (this.previouslySelectedTemplate == null) {
						// this shouldn't happen since there is a always a default template, but just in case
						this.previouslySelectedTemplate = this.getSelectedThumbnail(this.getThumbnails());
					}
					// no change to the preview needed since it was selected previously
					this.cmbTemplates.setSelectedItem(this.previouslySelectedTemplate);
					// open the Template Library
					boolean updated = SlideLibraryDialog.show(WindowUtilities.getParentWindow(this), SongSlideTemplate.class);
					if (updated) {
						// update the template dropdown and previews
						this.slideLibraryChanged();
						this.songLibraryChanged = true;
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.application.song.ui.SongPartListener#songPartAdded(org.praisenter.data.song.SongPart)
	 */
	@Override
	public void songPartAdded(SongPart part) {}
	
	/* (non-Javadoc)
	 * @see org.praisenter.application.song.ui.SongPartListener#songPartChanged(org.praisenter.data.song.SongPart)
	 */
	@Override
	public void songPartChanged(SongPart part) {
		this.setSongChanged(true);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.application.song.ui.SongPartListener#songPartDeleted(org.praisenter.data.song.SongPart)
	 */
	@Override
	public void songPartDeleted(SongPart part) {}
	
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
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if ("search".equals(command)) {
			this.searchSongsAction();
		} else if ("new".equals(command)) {
			this.newSongAction();
		} else if ("deleteSelectedSongs".equals(command)) {
			this.deleteSongsAction();
		} else if ("addPart".equals(command)) {
			this.addPartAction();
		} else if ("deleteParts".equals(command)) {
			this.deletePartsAction();
		} else if ("save".equals(command)) {
			this.saveAction();
		} else if ("import".equals(command)) {
			boolean updated = ImportExportSongsDialog.show(WindowUtilities.getParentWindow(this), false);
			if (updated) {
				this.clearSearch();
			}
		} else if ("export".equals(command)) {
			ImportExportSongsDialog.show(WindowUtilities.getParentWindow(this), true);
		} else if ("discard".equals(command)) {
			this.discardAction();
		}
	}
	
	/**
	 * Performs a search of the song data store using the text in the
	 * song search textbox.
	 */
	private void searchSongsAction() {
		// grab the text from the text box
		String text = this.txtSongSearch.getText();
		if (text != null && text.length() > 0) {
			// execute the search in another thread
			// its possible that the search thread was interrupted or stopped
			// so make sure its still running
			if (!this.songSearchThread.isAlive()) {
				// if the current thread is no longer alive (running) then
				// create another and start it
				this.songSearchThread = new SongSearchThread();
				this.songSearchThread.start();
			}
			
			// execute the search
			SongSearch search = new SongSearch(text, true, new SongsPanelSearchCallback());
			this.songSearchThread.queueSearch(search);
		} else {
			// show all
			List<Song> songs = null;
			try {
				songs = Songs.getSongs(false);
			} catch (DataException e) {
				songs = new ArrayList<Song>();
				LOGGER.error("An error occurred while trying to load all songs.", e);
			}
			this.tblSongSearchResults.setModel(new MutableSongTableModel(songs));
			this.setSongTableWidths();
		}
	}

	/**
	 * Clears the search text and shows all the songs.
	 */
	public void clearSearch() {
		this.txtSongSearch.setText("");
		this.searchSongsAction();
	}
	
	/**
	 * Creates a new song.
	 */
	private void newSongAction() {
		// check if the current song has changed first
		if (this.songChanged) {
			int choice = JOptionPane.showConfirmDialog(
					WindowUtilities.getParentWindow(this), 
					MessageFormat.format(Messages.getString("panel.song.switch.confirm.message"), this.song.getTitle()), 
					Messages.getString("panel.song.switch.confirm.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				// then save the song
				try {
					Collections.sort(this.song.getParts());
					Songs.saveSong(this.song);
				} catch (DataException e) {
					// if the song fails to be saved then show a message
					// and dont continue
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.song.save.exception.title"), 
							MessageFormat.format(Messages.getString("panel.song.save.exception.text"), this.song.getTitle()), 
							e);
					LOGGER.error("Failed to save song: ", e);
					return;
				}
			} else if (choice == JOptionPane.CANCEL_OPTION) {
				// don't continue
				return;
			}
		}
		// create a new song and setup the panel to edit it
		Song song = new Song();
		song.addSongPart(SongPartType.CHORUS, "");
		this.setSong(song);
		this.setSongChanged(true);
	}
	
	/**
	 * Deletes the selected songs in the song search table.
	 */
	private void deleteSongsAction() {
		MutableSongTableModel model = (MutableSongTableModel)this.tblSongSearchResults.getModel();
		// only prompt if there is a song selected
		if (model.isSongSelected()) {
			// verify the user wants to do it
			int choice = JOptionPane.showConfirmDialog(
					WindowUtilities.getParentWindow(this), 
					Messages.getString("panel.song.deleteSelected.message"),
					Messages.getString("panel.song.deleteSelected.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				// get the songs
				List<Song> songs = model.removeSelectedRows();
				// remove the song from the data store
				for (Song song : songs) {
					// see if the song is the one currently selected
					if (this.song != null && this.song.getId() == song.getId()) {
						// clear the current song
						this.setSong(null);
					}
					
					try {
						Songs.deleteSong(song.getId());
					} catch (DataException ex) {
						// show an exception dialog
						ExceptionDialog.show(
								this, 
								Messages.getString("panel.song.delete.exception.title"), 
								MessageFormat.format(Messages.getString("panel.song.delete.exception.text"), song.getTitle(), song.getId()), 
								ex);
						// log the error
						LOGGER.error("Error deleting song: ", ex);
					}
				}
				this.songLibraryChanged = true;
			}
		}
	}
	
	/**
	 * Adds a new part to the song.
	 */
	private void addPartAction() {
		if (this.song != null) {
			// this will add it to the song itself
			SongPart part = this.song.addSongPart(SongPartType.CHORUS, "");
			// flag that the song has changed
			this.setSongChanged(true);
			// update the song parts panel
			final EditSongPartPanel panel = new EditSongPartPanel(true, this.getSelectedTemplate());
			panel.setSongPart(this.song, part);
			panel.addSongPartListener(this);
			panel.setPreferredSize(new Dimension(550, 200));
			panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, panel.getBackground().darker()), BorderFactory.createEmptyBorder(5, 0, 5, 0)));
			this.pnlSongParts.add(panel);
			this.pnlSongParts.revalidate();
			// scroll to it
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					// jump to the new part
					scrSongParts.getVerticalScrollBar().setValue(scrSongParts.getVerticalScrollBar().getMaximum());
				}
			});
		}
	}
	
	/**
	 * Deletes the selected parts from the song.
	 */
	private void deletePartsAction() {
		if (this.song != null) {
			for (Component component : this.pnlSongParts.getComponents()) {
				if (component instanceof EditSongPartPanel) {
					EditSongPartPanel spp = (EditSongPartPanel)component;
					if (spp.isSelected()) {
						this.song.getParts().remove(spp.getSongPart());
						this.pnlSongParts.remove(component);
					}
				}
			}
			this.pnlSongParts.revalidate();
			this.pnlSongParts.repaint();
			this.setSongChanged(true);
		}
	}
	
	/**
	 * Saves the current song.
	 */
	private void saveAction() {
		try {
			Collections.sort(this.song.getParts());
			Songs.saveSong(this.song);
			// update the id label
			this.lblId.setText(String.valueOf(this.song.getId()));
			this.setSongChanged(false);
			this.songLibraryChanged = true;
			// update the song search table
			MutableSongTableModel model = (MutableSongTableModel)this.tblSongSearchResults.getModel();
			// loop over the rows to find the saved song, then update it
			for (int i = 0; i < model.getRowCount(); i++) {
				Song song = model.getRow(i);
				if (song.getId() == this.song.getId()) {
					// update the song
					model.songs.set(i, this.song);
					// notify of the update
					model.fireTableRowsUpdated(i, i);
					// shouldn't have anything else to do
					break;
				}
			}
		} catch (DataException e) {
			ExceptionDialog.show(
					this, 
					Messages.getString("panel.song.save.exception.title"), 
					MessageFormat.format(Messages.getString("panel.song.save.exception.text"), this.song.getTitle()), 
					e);
			LOGGER.error("Failed to save song: ", e);
		}
	}
	
	/**
	 * Discards any changes made.
	 */
	private void discardAction() {
		if (this.song != null) {
			int choice = JOptionPane.showConfirmDialog(
					WindowUtilities.getParentWindow(SongLibraryPanel.this), 
					MessageFormat.format(Messages.getString("panel.song.discard.confirm.message"), song.getTitle()), 
					Messages.getString("panel.song.discard.confirm.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				// if yes, then just reload the song, or if its a new song, just null it out
				if (this.song.isNew()) {
					this.setSong(null);
				} else {
					try {
						Song song = Songs.getSong(this.song.getId());
						this.setSong(song);
					} catch (DataException e) {
						// just log this exception
						LOGGER.error("An error occurred while loading the song [" + this.song.getId() + "|" + this.song.getTitle() + "]: ", e);
						// and null it out
						this.setSong(null);
					}
				}
			}
		}
	}
	
	/**
	 * This method will check if a song has not been saved and prompt the user
	 * if they want to save it or not.
	 * <p>
	 * If the user chooses yes then it is saved and true is returned.  If the
	 * user chooses no, it is not saved and true is returned.  If the user
	 * chooses cancel, it is not saved and false is returned.
	 * @return boolean true if the user chooses yes or no; false if they choose cancel
	 */
	public boolean checkForUnsavedWork() {
		// check if the current song has changed first
		if (this.songChanged) {
			int choice = JOptionPane.showConfirmDialog(
					WindowUtilities.getParentWindow(this), 
					MessageFormat.format(Messages.getString("panel.song.switch.confirm.message"), this.song.getTitle()), 
					Messages.getString("panel.song.switch.confirm.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				// then save the song
				try {
					Collections.sort(this.song.getParts());
					Songs.saveSong(this.song);
				} catch (DataException e) {
					// if the song fails to be saved then show a message
					// and dont continue
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.song.save.exception.title"), 
							MessageFormat.format(Messages.getString("panel.song.save.exception.text"), this.song.getTitle()), 
							e);
					LOGGER.error("Failed to save song: ", e);
				}
			} else if (choice == JOptionPane.CANCEL_OPTION) {
				// don't continue
				return false;
			}
		}
		return true;
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
			String id = "";
			String notes = "";
			boolean edit = false;
			if (song != null) {
				title = song.getTitle();
				notes = song.getNotes();
				if (!song.isNew()) {
					id = String.valueOf(song.getId());
				} else {
					id = Messages.getString("panel.song.newId");
				}
				edit = true;
			}
			
			this.txtTitle.setText(title);
			this.lblId.setText(id);
			this.txtNotes.setText(notes);
			this.txtNotes.setCaretPosition(0);
			
			this.txtTitle.setEnabled(edit);
			this.txtNotes.setEnabled(edit);
			this.btnAddPart.setEnabled(edit);
			this.btnDeleteParts.setEnabled(edit);
			
			// rebuild the parts panels
			this.pnlSongParts.removeAll();
			if (song != null) {
				SongSlideTemplate template = this.getSelectedTemplate();
				for (SongPart part : song.getParts()) {
					EditSongPartPanel partPanel = new EditSongPartPanel(true, template);
					partPanel.setSongPart(song, part);
					partPanel.addSongPartListener(this);
					partPanel.setPreferredSize(new Dimension(550, 200));
					partPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, partPanel.getBackground().darker()), BorderFactory.createEmptyBorder(5, 0, 5, 0)));
					this.pnlSongParts.add(partPanel);
				}
			}
			this.pnlSongParts.invalidate();
			this.pnlSongParts.repaint();
			this.scrSongParts.getVerticalScrollBar().setValue(0);
			
			this.notificationsDisabled = false;
		}
	}

	/**
	 * Flags that the song has been changed or not.
	 * @param flag true if the song has been changed
	 */
	private void setSongChanged(boolean flag) {
		this.songChanged = flag;
		this.btnSave.setEnabled(flag);
		this.btnDiscardChanges.setEnabled(flag);
	}
	
	/**
	 * Sets the table column widths for the song search results table.
	 */
	private void setSongTableWidths() {
		this.tblSongSearchResults.getColumnModel().getColumn(0).setMaxWidth(30);
		this.tblSongSearchResults.getColumnModel().getColumn(0).setPreferredWidth(30);
	}
	
	/**
	 * Returns true if the song library was changed.
	 * @return boolean
	 */
	public boolean isSongLibraryChanged() {
		return this.songLibraryChanged;
	}
	
	/**
	 * Callback for song searching to update the table and results text.
	 * @author William Bittle
	 * @version 2.0.1
	 * @since 2.0.1
	 */
	private class SongsPanelSearchCallback extends SongSearchCallback {
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			List<Song> songs = this.getResult();
			Exception ex = this.getException();
			SongSearch search = this.getSearch();
			if (ex == null) {
				if (songs != null && songs.size() > 0) {
					tblSongSearchResults.setModel(new MutableSongTableModel(songs));
				} else {
					tblSongSearchResults.setModel(new MutableSongTableModel());
				}
				// reset the scroll position
				scrSongSearchResults.getVerticalScrollBar().setValue(0);
				setSongTableWidths();
			} else {
				String message = MessageFormat.format(Messages.getString("panel.song.data.search.exception.text"), search.getText());
				ExceptionDialog.show(
						SongLibraryPanel.this, 
						Messages.getString("panel.song.data.search.exception.title"), 
						message, 
						ex);
				LOGGER.error(message, ex);
			}
		}
	}
}
