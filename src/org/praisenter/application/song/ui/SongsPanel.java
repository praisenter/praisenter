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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.praisenter.animation.TransitionAnimator;
import org.praisenter.animation.easings.Easing;
import org.praisenter.animation.easings.Easings;
import org.praisenter.animation.transitions.Transition;
import org.praisenter.animation.transitions.TransitionType;
import org.praisenter.animation.transitions.Transitions;
import org.praisenter.application.Praisenter;
import org.praisenter.application.errors.ui.ExceptionDialog;
import org.praisenter.application.preferences.Preferences;
import org.praisenter.application.preferences.SongPreferences;
import org.praisenter.application.preferences.ui.PreferencesListener;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.slide.ui.SlideLibraryDialog;
import org.praisenter.application.slide.ui.SlideLibraryListener;
import org.praisenter.application.slide.ui.SlideThumbnailComboBoxRenderer;
import org.praisenter.application.slide.ui.TransitionListCellRenderer;
import org.praisenter.application.slide.ui.preview.ScrollableInlineSlidePreviewPanel;
import org.praisenter.application.ui.OpaquePanel;
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
import org.praisenter.presentation.ClearEvent;
import org.praisenter.presentation.PresentationEventConfiguration;
import org.praisenter.presentation.PresentationManager;
import org.praisenter.presentation.PresentationWindowType;
import org.praisenter.presentation.SendEvent;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideLibraryException;
import org.praisenter.slide.SlideThumbnail;
import org.praisenter.slide.SongSlide;
import org.praisenter.slide.SongSlideTemplate;

/**
 * Main ui for song manipulation and display.
 * @author William Bittle
 * @version 2.0.1
 * @since 1.0.0
 */
public class SongsPanel extends OpaquePanel implements ActionListener, SongListener, ItemListener, PreferencesListener, SlideLibraryListener {
	/** The version id */
	private static final long serialVersionUID = 2646140774751357022L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SongsPanel.class);
	
	// need song edit caps.
	
	// current song
	
	/** The currently selected song */
	private Song song;
	
	/** The edit song panel */
	private EditSongPanel pnlEditSong;
	
	// song searching
	
	/** The song searching thread */
	private SongSearchThread songSearchThread;
	
	/** The song search text field */
	private JTextField txtSongSearch;
	
	/** The song search results table */
	private JTable tblSongSearchResults;

	/** The song search results table scroll pane */
	private JScrollPane scrSongSearchResults;
	
	/** The song queue */
	private JTable tblSongQueue;
	
	// preview
	
	/** The song preview thread */
	private SongPreivewThread previewThread;
	
	/** The song preview panel */
	private SongSlidePreviewPanel pnlPreview;
	
	/** The preview panel scroller */
	private ScrollableInlineSlidePreviewPanel scrPreview;
	
	// display
	
	/** The song title and notes label */
	private JLabel lblSongTitle;
	
	/** The quick send panel for the standard song parts */
	private SongQuickSendPanel pnlQuickSend;
	
	/** The combo box of all song parts */
	private JComboBox<SongPart> cmbParts;

	/** The template combo box */
	private JComboBox<Object> cmbTemplates;
	
	/** The send button for the selected song part */
	private JButton btnSend;
	
	/** The clear button */
	private JButton btnClear;
	
	/** The combo box of transitions for sending */
	private JComboBox<Transition> cmbSendTransitions;
	
	/** The text box of send transition duration */
	private JFormattedTextField txtSendTransitions;
	
	/** The combo box of transitions for clearing */
	private JComboBox<Transition> cmbClearTransitions;
	
	/** The text box of clear transition duration */
	private JFormattedTextField txtClearTransitions;
	
	// state
	
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
	public SongsPanel() {
		// get the primary device
		GraphicsDevice device = this.preferences.getPrimaryOrDefaultDevice();
		
		// song preview
		this.pnlPreview = new SongSlidePreviewPanel();
		this.pnlPreview.setBorder(BorderFactory.createEmptyBorder(15, 15, 20, 15));
		
		this.scrPreview = new ScrollableInlineSlidePreviewPanel(this.pnlPreview);
		this.scrPreview.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
		this.scrPreview.getHorizontalScrollBar().setUnitIncrement(20);
		this.scrPreview.setOpaque(false);
		this.scrPreview.getViewport().setOpaque(false);
		this.scrPreview.setViewportBorder(null);
		
		// song preview thread
		this.previewThread = new SongPreivewThread();
		this.previewThread.start();
		
		// current song and sending
		
		this.lblSongTitle = new JLabel(MessageFormat.format(Messages.getString("panel.song.current.pattern"), Messages.getString("panel.song.default.title"), ""));
		this.lblSongTitle.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		this.pnlQuickSend = new SongQuickSendPanel(this);
		this.pnlQuickSend.setOpaque(false);
		
		JLabel lblParts = new JLabel(Messages.getString("panel.song.parts.select"));
		lblParts.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
		this.cmbParts = new JComboBox<SongPart>(new SongPart[] { new SongPart() });
		this.cmbParts.setRenderer(new SongPartCellRenderer());
		this.cmbParts.setEnabled(false);
		this.cmbParts.setToolTipText(Messages.getString("panel.song.part.list"));

		SlideThumbnail[] thumbnails = this.getThumbnails();
		SlideThumbnail selected = this.getSelectedThumbnail(thumbnails);
		this.cmbTemplates = new JComboBox<Object>((Object[])thumbnails);
		// add the "manage templates" item
		this.cmbTemplates.addItem(Messages.getString("template.manage"));
		if (selected != null) {
			this.cmbTemplates.setSelectedItem(selected);
			this.previouslySelectedTemplate = selected;
		}
		this.cmbTemplates.setToolTipText(Messages.getString("panel.template"));
		this.cmbTemplates.setRenderer(new SlideThumbnailComboBoxRenderer());
		this.cmbTemplates.addItemListener(this);
		
		this.btnSend = new JButton(Messages.getString("panel.song.send"));
		this.btnSend.setToolTipText(Messages.getString("panel.song.send.tooltip"));
		this.btnSend.addActionListener(this);
		this.btnSend.setActionCommand("send");
		this.btnSend.setFont(this.btnSend.getFont().deriveFont(Font.BOLD, this.btnSend.getFont().getSize2D() + 3.0f));
		
		this.btnClear = new JButton(Messages.getString("panel.song.clear"));
		this.btnClear.setToolTipText(Messages.getString("panel.song.clear.tooltip"));
		this.btnClear.addActionListener(this);
		this.btnClear.setActionCommand("clear");
		
		JButton btnBlank = new JButton(Messages.getString("panel.song.blank"));
		btnBlank.setToolTipText(Messages.getString("panel.song.blank.tooltip"));
		btnBlank.addActionListener(this);
		btnBlank.setActionCommand("blank");
		
		// setup the transition lists
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(device);
		
		this.cmbSendTransitions = new JComboBox<Transition>(Transitions.IN);
		this.cmbSendTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbSendTransitions.setSelectedItem(Transitions.getTransitionForId(this.sPreferences.getSendTransitionId(), TransitionType.IN));
		this.txtSendTransitions = new JFormattedTextField(new DecimalFormat("0"));
		this.txtSendTransitions.addFocusListener(new SelectTextFocusListener(this.txtSendTransitions));
		this.txtSendTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtSendTransitions.setValue(this.sPreferences.getSendTransitionDuration());
		this.txtSendTransitions.setColumns(3);
		
		this.cmbClearTransitions = new JComboBox<Transition>(Transitions.OUT);
		this.cmbClearTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbClearTransitions.setSelectedItem(Transitions.getTransitionForId(this.sPreferences.getClearTransitionId(), TransitionType.OUT));
		this.txtClearTransitions = new JFormattedTextField(new DecimalFormat("0"));
		this.txtClearTransitions.addFocusListener(new SelectTextFocusListener(this.txtClearTransitions));
		this.txtClearTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtClearTransitions.setValue(this.sPreferences.getClearTransitionDuration());
		this.txtClearTransitions.setColumns(3);
		
		if (!transitionsSupported) {
			this.cmbSendTransitions.setEnabled(false);
			this.txtSendTransitions.setEnabled(false);
			this.cmbClearTransitions.setEnabled(false);
			this.txtClearTransitions.setEnabled(false);
		}
		
		JPanel pnlSending = new OpaquePanel();
		GroupLayout sendLayout = new GroupLayout(pnlSending);
		pnlSending.setLayout(sendLayout);
		
		sendLayout.setAutoCreateContainerGaps(true);
		sendLayout.setAutoCreateGaps(true);
		sendLayout.setHorizontalGroup(sendLayout.createSequentialGroup()
				.addGroup(sendLayout.createParallelGroup()
						.addComponent(this.lblSongTitle)
						.addComponent(this.pnlQuickSend)
						.addGroup(sendLayout.createSequentialGroup()
								.addComponent(lblParts)
								.addComponent(this.cmbParts)
								.addComponent(btnBlank)))
				.addGroup(sendLayout.createParallelGroup()
						.addComponent(this.cmbTemplates)
						.addGroup(sendLayout.createSequentialGroup()
								.addGroup(sendLayout.createParallelGroup()
										.addGroup(sendLayout.createSequentialGroup()
												.addComponent(this.cmbSendTransitions)
												.addComponent(this.txtSendTransitions))
										.addComponent(this.btnSend, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(sendLayout.createParallelGroup()
									.addGroup(sendLayout.createSequentialGroup()
											.addComponent(this.cmbClearTransitions)
											.addComponent(this.txtClearTransitions))
									.addComponent(this.btnClear, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))));
		
		sendLayout.setVerticalGroup(sendLayout.createSequentialGroup()
				.addGroup(sendLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblSongTitle)
						.addComponent(this.cmbTemplates, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(sendLayout.createParallelGroup()
						.addComponent(this.pnlQuickSend)
						.addGroup(sendLayout.createSequentialGroup()
								.addGroup(sendLayout.createParallelGroup()
										.addComponent(this.cmbSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(this.txtSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(this.btnSend, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(sendLayout.createSequentialGroup()
								.addGroup(sendLayout.createParallelGroup()
										.addComponent(this.cmbClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(this.txtClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(this.btnClear, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
				.addGroup(sendLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblParts)
						.addComponent(this.cmbParts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBlank)));
		
		// searching
		
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
		
		JButton btnAdd = new JButton(Messages.getString("panel.song.add"));
		btnAdd.setToolTipText(Messages.getString("panel.song.add.tooltip"));
		btnAdd.setActionCommand("add");
		btnAdd.addActionListener(this);
		
		JButton btnSongLibrary = new JButton(Messages.getString("panel.song.library"));
		btnSongLibrary.setToolTipText(Messages.getString("panel.song.library.tooltip"));
		btnSongLibrary.setActionCommand("library");
		btnSongLibrary.addActionListener(this);
		
		this.tblSongSearchResults = new JTable(new SongSearchTableModel()) {
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
				Object object = model.getValueAt(row, 2);
				if (object != null) {
					// get the song title
					String text = object.toString();
					// split the lines by 50 characters
					return StringUtilities.addLineBreaksAtInterval(text, 50, true);
				}
				
				return super.getToolTipText(event);
			}
		};
		this.tblSongSearchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tblSongSearchResults.setColumnSelectionAllowed(false);
		this.tblSongSearchResults.setCellSelectionEnabled(false);
		this.tblSongSearchResults.setRowSelectionAllowed(true);
		this.tblSongSearchResults.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// make sure its a double click
				if (e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON1) {
					// assume single click
					// go to the data store and get the full song detail
					// get the selected row
					int row = tblSongSearchResults.rowAtPoint(e.getPoint());
					if (row < 0) return;
					// since sorting is allowed, we need to translate the view row index
					// into the model row index
					row = tblSongSearchResults.convertRowIndexToModel(row);
					
					// get the data
					SongSearchTableModel model = (SongSearchTableModel)tblSongSearchResults.getModel();
					Song song = model.getRow(row);
					// see if its the same song
					if (SongsPanel.this.song == null || (song.getId() != SongsPanel.this.song.getId())) {
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
		this.tblSongSearchResults.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		// disable the default F2 functionality of the JTable
		this.tblSongSearchResults.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "none");
		this.setSongSearchTableWidths();
		this.scrSongSearchResults = new JScrollPane(this.tblSongSearchResults);
		
		// search layout
		JPanel pnlSongSearch = new OpaquePanel();
		GroupLayout ssLayout = new GroupLayout(pnlSongSearch);
		pnlSongSearch.setLayout(ssLayout);
		
		ssLayout.setAutoCreateContainerGaps(true);
		ssLayout.setAutoCreateGaps(true);
		ssLayout.setHorizontalGroup(ssLayout.createParallelGroup()
				.addGroup(ssLayout.createSequentialGroup()
						.addComponent(this.txtSongSearch)
						.addComponent(btnSongSearch)
						.addComponent(btnAdd)
						.addComponent(btnSongLibrary))
				.addComponent(this.scrSongSearchResults, 0, 400, Short.MAX_VALUE));
		
		ssLayout.setVerticalGroup(ssLayout.createSequentialGroup()
				.addGroup(ssLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.txtSongSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSongSearch)
						.addComponent(btnAdd)
						.addComponent(btnSongLibrary))
				.addComponent(this.scrSongSearchResults, 0, 75, Short.MAX_VALUE));
		
		// the song queue
		this.tblSongQueue = new JTable(new MutableSongTableModel()) {
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
					return StringUtilities.addLineBreaksAtInterval(text, 50);
				}
				
				return super.getToolTipText(event);
			}
		};
		this.tblSongQueue.setAutoCreateRowSorter(true);
		this.tblSongQueue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tblSongQueue.setColumnSelectionAllowed(false);
		this.tblSongQueue.setCellSelectionEnabled(false);
		this.tblSongQueue.setRowSelectionAllowed(true);
		this.tblSongQueue.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// make sure its a double click
				if (e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON1) {
					// assume single click
					// go to the data store and get the full song detail
					// get the selected row
					int row = tblSongQueue.rowAtPoint(e.getPoint());
					if (row < 0) return;
					// since sorting is allowed, we need to translate the view row index
					// into the model row index
					row = tblSongQueue.convertRowIndexToModel(row);
					
					// get the data
					SongTableModel model = (SongTableModel)tblSongQueue.getModel();
					Song song = model.getRow(row);
					// see if its the same song
					if (SongsPanel.this.song == null || (song.getId() != SongsPanel.this.song.getId())) {
						try {
							// overwrite the song local variable
							song = Songs.getSong(song.getId());
							setSong(song);
						} catch (DataException ex) {
							// just log this exception because the user
							// should still be able to click on the row again
							LOGGER.error("An error occurred while selecting a song from the song queue list: ", ex);
						}
					}
				}
			}
		});
		this.tblSongQueue.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		// disable the default F2 functionality of the JTable
		this.tblSongQueue.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "none");
		this.setSongQueueTableWidths();
		JScrollPane scrSongQueueResults = new JScrollPane(this.tblSongQueue);
		
		JButton btnRemoveSelected = new JButton(Messages.getString("panel.song.queue.remove"));
		btnRemoveSelected.setToolTipText(Messages.getString("panel.song.queue.remove.tooltip"));
		btnRemoveSelected.setActionCommand("remove");
		btnRemoveSelected.addActionListener(this);
		
		JButton btnRemoveAll = new JButton(Messages.getString("panel.song.queue.removeAll"));
		btnRemoveAll.setToolTipText(Messages.getString("panel.song.queue.removeAll.tooltip"));
		btnRemoveAll.setActionCommand("removeAll");
		btnRemoveAll.addActionListener(this);
		
		// queue layout
		JPanel pnlSongQueue = new OpaquePanel();
		GroupLayout sqLayout = new GroupLayout(pnlSongQueue);
		pnlSongQueue.setLayout(sqLayout);
		
		sqLayout.setAutoCreateContainerGaps(true);
		sqLayout.setAutoCreateGaps(true);
		sqLayout.setHorizontalGroup(sqLayout.createParallelGroup()
				.addGroup(sqLayout.createSequentialGroup()
						.addComponent(btnRemoveSelected)
						.addComponent(btnRemoveAll))
				.addComponent(scrSongQueueResults, 0, 400, Short.MAX_VALUE));
		
		sqLayout.setVerticalGroup(sqLayout.createSequentialGroup()
				.addGroup(sqLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(btnRemoveSelected)
						.addComponent(btnRemoveAll))
				.addComponent(scrSongQueueResults, 0, 75, Short.MAX_VALUE));
		
		// edit panel
		this.pnlEditSong = new EditSongPanel(getSelectedTemplate());
		this.pnlEditSong.addSongListener(this);
		
		// song search/queue tabs
		JTabbedPane tabBottom = new JTabbedPane();
		tabBottom.addTab(Messages.getString("panel.song.tab.search"), pnlSongSearch);
		tabBottom.addTab(Messages.getString("panel.song.tab.queue"), pnlSongQueue);
		tabBottom.addTab(Messages.getString("panel.song.tab.editSong"), this.pnlEditSong);
		
		JPanel pnlTop = new OpaquePanel();
		GroupLayout layout = new GroupLayout(pnlTop);
		pnlTop.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.scrPreview, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pnlSending, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.scrPreview, 0, 100, Short.MAX_VALUE)
				.addComponent(pnlSending, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(pnlTop);
		split.setBottomComponent(tabBottom);
		split.setResizeWeight(0.9);
		split.setOneTouchExpandable(true);
		split.setBorder(null);
		
		// create the layout
		this.setLayout(new BorderLayout());
		this.add(split, BorderLayout.CENTER);
		
		// setup hot keys for quick sending
		SongQuickSendAction[] actions = SongQuickSendPanel.getQuickSendActions(this);
		for (int i = 0; i < actions.length; i++) {
			SongQuickSendAction action = actions[i];
			String name = "QuickSend_" + i;
			this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(action.keyStroke, name);
			this.getActionMap().put(name, action);
		}
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
			LOGGER.warn("Template [" + template.getName() + "] is not sized correctly for the primary display. Adjusing template.");
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
	 * @see javax.swing.JPanel#updateUI()
	 */
	@Override
	public void updateUI() {
		// before we update the ui, clear the current song
		if (this.scrPreview != null && this.pnlPreview != null) {
			this.pnlPreview.clearSlides();
			this.scrPreview.updatePanelSize();
		}
		// call the super method
		super.updateUI();
		// after the ui is updated, re-generate the song layout
		if (this.scrPreview != null && this.pnlPreview != null && this.song != null) {
			// set the loading status
			this.scrPreview.setLoading(true);
			this.queueSongPreview(this.song);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.preferences.ui.PreferencesListener#preferencesChanged()
	 */
	@Override
	public void preferencesChanged() {
		this.onPreferencesOrSlideLibraryChanged();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.SlideLibraryListener#slideLibraryChanged()
	 */
	@Override
	public void slideLibraryChanged() {
		this.onPreferencesOrSlideLibraryChanged();
	}

	/**
	 * Called when the preferences or the slide library changes.
	 */
	private void onPreferencesOrSlideLibraryChanged() {
		// if the preferences or slide library changes we only want to make
		// sure that we are using the latest template (so if it was edited
		// we need to update the preview) and that we are using the latest
		// listing of other templates
		
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
		
		// redraw the preview panel
		this.pnlPreview.repaint();
	}
	
	/**
	 * Called when the song library is closed.
	 */
	public void onReturnFromSongLibrary() {
		// verify the currently selected song
		if (this.song != null) {
			// clear any edit state from the edit song panel
			this.pnlEditSong.setSong(null);
			try {
				// re-lookup the song
				Song song = Songs.getSong(this.song.getId());
				this.setSong(song);
			} catch (DataException ex) {
				LOGGER.error("An error occurred while trying to lookup song id " + this.song.getId(), ex);
			}
		}
		
		// we need to verify the song search by removing songs that have been deleted
		// and updating songs (just the title) that have been changed
		// since we have to verify the existence of each song, its best to just re-run the search
		// there shouldn't be anything to do if the song search is null
		// trying to validate each song via the database was far too slow
		SongSearchTableModel searchModel = (SongSearchTableModel)this.tblSongSearchResults.getModel();
		SongSearch search = searchModel.getSearch();
		if (search != null) {
			this.songSearchThread.queueSearch(search);
		}
//		SongSearchTableModel searchModel = (SongSearchTableModel)this.tblSongSearchResults.getModel();
//		List<Integer> remove = new ArrayList<Integer>();
//		for (int i = 0; i < searchModel.getRowCount(); i++) {
//			Song song = searchModel.getRow(i);
//			try {
//				// re-lookup the song
//				Song songTemp = Songs.getSong(song.getId());
//				if (songTemp == null) {
//					remove.add(i);
//				} else {
//					// only update the title
//					// since the notes field contains the matched text
//					song.setTitle(songTemp.getTitle());
//				}
//			} catch (DataException ex) {
//				LOGGER.error("An error occurred while trying to lookup song id " + this.song.getId(), ex);
//			}
//		}
//		// we need to go backwards through the deletions since we are doing it by row index
//		for (int i = remove.size() - 1; i >= 0; i--) {
//			int row = (int)remove.get(i);
//			searchModel.removeRow(row);
//		}
//		searchModel.fireTableDataChanged();
		
		// we need to verify the song queue by removing songs that have been deleted
		// and updating songs that have been changed
		MutableSongTableModel queueModel = (MutableSongTableModel)this.tblSongQueue.getModel();
		List<Integer> remove = new ArrayList<Integer>();
		for (int i = 0; i < queueModel.getRowCount(); i++) {
			Song song = queueModel.getRow(i);
			try {
				// re-lookup the song
				Song songTemp = Songs.getSong(song.getId());
				if (songTemp == null) {
					remove.add(i);
				} else {
					queueModel.songs.set(i, songTemp);
				}
			} catch (DataException ex) {
				LOGGER.error("An error occurred while trying to lookup song id " + this.song.getId(), ex);
			}
		}
		// we need to go backwards through the deletions since we are doing it by row index
		for (int i = remove.size() - 1; i >= 0; i--) {
			int row = (int)remove.get(i);
			queueModel.removeRow(row);
		}
		queueModel.fireTableDataChanged();
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
						// refresh the song preview panel
						if (this.song != null) {
							// set the loading status
							this.scrPreview.setLoading(true);
							this.queueSongPreview(this.song);
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
						// we need to notify all the panels that the slide/template library has been changed
						// since the user could change other slides/templates other than the ones displayed
						// initially by the class type
						firePropertyChange(Praisenter.PROPERTY_SLIDE_TEMPLATE_LIBRARY_CHANGED, null, null);
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("search".equals(command)) {
			this.searchSongsAction();
		} else if ("library".equals(command)) {
			boolean updated = SongLibraryDialog.show(WindowUtilities.getParentWindow(this));
			if (updated) {
				this.onReturnFromSongLibrary();
				this.firePropertyChange(Praisenter.PROPERTY_SLIDE_TEMPLATE_LIBRARY_CHANGED, null, null);
			}
		} else if ("remove".equals(command)) {
			MutableSongTableModel model = (MutableSongTableModel)this.tblSongQueue.getModel();
			model.removeSelectedRows();
		} else if ("removeAll".equals(command)) {
			MutableSongTableModel model = (MutableSongTableModel)this.tblSongQueue.getModel();
			model.removeAllRows();
		} else if ("add".equals(command)) {
			this.addSongsToSongQueueAction();
		} else if (command.startsWith("quickSend=")) {
			this.quickSendAction(command);
		} else if ("send".equals(command)) {
			this.sendAction();
		} else if ("clear".equals(command)) {
			this.clearAction();
		} else if ("blank".equals(command)) {
			this.blankAction();
		}
	}
	
	/**
	 * Performs a search of the song data store using the text in the
	 * song search textbox.
	 */
	private void searchSongsAction() {
		// grab the text from the text box
		String text = this.txtSongSearch.getText();
		if (text != null && text.trim().length() > 0) {
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
			SongSearch search = new SongSearch(text, new SongsPanelSearchCallback());
			this.songSearchThread.queueSearch(search);
		}
	}
	
	/**
	 * Adds the selected song in the song search table to the song queue table.
	 */
	private void addSongsToSongQueueAction() {
		SongSearchTableModel model = (SongSearchTableModel)this.tblSongSearchResults.getModel();
		List<Song> songs = model.getSelectedRows();

		MutableSongTableModel qModel = (MutableSongTableModel)this.tblSongQueue.getModel();
		qModel.addRows(songs);
		
		model.deselectAll();
	}
	
	/**
	 * Determines the display to send and sends it to the primary display.
	 * @param command the command
	 */
	private void quickSendAction(String command) {
		String[] parts = command.split("=");
		parts = parts[1].split("\\|");
		
		SongPartType type = SongPartType.valueOf(parts[0]);
		int index = Integer.parseInt(parts[1]);
		SongPartKey key = new SongPartKey(type, index);
		
		// set the currently selected item in the parts combo box
		int n = this.cmbParts.getItemCount();
		for (int i = 0; i < n; i++) {
			SongPart part = this.cmbParts.getItemAt(i);
			if (part.getType() == type && part.getIndex() == index) {
				this.cmbParts.setSelectedItem(part);
				break;
			}
		}
		
		SongSlide slide = this.pnlPreview.getSlide(key);
		if (slide != null) {
			this.sendSlide(slide);
		}
	}
	
	/**
	 * Sends the currently selected song part to the primary display.
	 */
	private void sendAction() {
		SongPart part = (SongPart)this.cmbParts.getSelectedItem();
		if (part != null) {
			SongSlide slide = this.pnlPreview.getSlide(part);
			if (slide != null) {
				this.sendSlide(slide);
			}
		}
	}
	
	/**
	 * Sends a blank song part to the primary display.
	 * @since 2.0.1
	 */
	private void blankAction() {
		SongSlide slide = this.getSelectedTemplate().createSlide();
		if (slide != null) {
			slide.setName("");
			slide.getTextComponent().setText("");
			this.sendSlide(slide);
		}
	}
	
	
	/**
	 * Sends the given {@link SongSlide} to the primary display.
	 * <p>
	 * This method uses the current values of the send transition combo box and
	 * textbox.
	 * @param slide the slide to send
	 */
	private void sendSlide(SongSlide slide) {
		SongPreferences preferences = Preferences.getInstance().getSongPreferences();
		// copy the slide
		slide = slide.copy();
		// get the transition
		Transition transition = (Transition)this.cmbSendTransitions.getSelectedItem();
		int duration = ((Number)this.txtSendTransitions.getValue()).intValue();
		int delay = this.preferences.getTransitionDelay();
		Easing easing = Easings.getEasingForId(preferences.getSendTransitionEasingId());
		TransitionAnimator animator = new TransitionAnimator(transition, duration, delay, easing);
		// get the configuration
		PresentationEventConfiguration configuration = this.preferences.getPresentationEventConfiguration(PresentationWindowType.FULLSCREEN);
		// execute a new send event to the primary fullscreen display
		PresentationManager.getInstance().execute(new SendEvent(configuration, animator, slide));
	}
	
	/**
	 * Clears the primary display.
	 */
	private void clearAction() {
		SongPreferences preferences = Preferences.getInstance().getSongPreferences();
		// get the transition
		Transition transition = (Transition)this.cmbClearTransitions.getSelectedItem();
		int duration = ((Number)this.txtClearTransitions.getValue()).intValue();
		int delay = this.preferences.getTransitionDelay();
		Easing easing = Easings.getEasingForId(preferences.getClearTransitionEasingId());
		TransitionAnimator animator = new TransitionAnimator(transition, duration, delay, easing);
		// get the configuration
		PresentationEventConfiguration configuration = this.preferences.getPresentationEventConfiguration(PresentationWindowType.FULLSCREEN);
		// execute a new clear event to the primary fullscreen display
		PresentationManager.getInstance().execute(new ClearEvent(configuration, animator));
	}
	
	/**
	 * Sets the current song.
	 * @param song the song
	 */
	private void setSong(Song song) {
		// check if the current song was changed first
		if (this.pnlEditSong.isSongChanged()) {
			// get the song from the panel
			Song temp = this.pnlEditSong.getSong();
			int choice = JOptionPane.showConfirmDialog(
					WindowUtilities.getParentWindow(this), 
					MessageFormat.format(Messages.getString("panel.song.switch.confirm.message"), temp.getTitle()), 
					Messages.getString("panel.song.switch.confirm.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				// then save the song
				try {
					boolean isNew = temp.isNew();
					Collections.sort(temp.getParts());
					Songs.saveSong(temp);
					if (isNew) {
						this.songAdded(temp);
					} else {
						this.songChanged(temp);
					}
				} catch (DataException e) {
					// if the song fails to be saved then show a message
					// and dont continue
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.song.save.exception.title"), 
							MessageFormat.format(Messages.getString("panel.song.save.exception.text"), temp.getTitle()), 
							e);
					LOGGER.error("Failed to save song: ", e);
					return;
				}
			} else if (choice == JOptionPane.CANCEL_OPTION) {
				// don't continue
				return;
			}
		}
		
		// assign the song
		this.song = song;
		
		this.cmbParts.setEnabled(false);
		this.btnSend.setEnabled(false);
		this.pnlQuickSend.setButtonsEnabled(false);
		
		// update the part combo box
		this.cmbParts.removeAllItems();
		
		if (song != null) {
			// update the labels
			String notes = song.getNotes();
			final int maxLength = 25;
			if (notes != null && notes.length() > 0) {
				// replace new lines with spaces
				notes = notes.replaceAll("(\\r\\n)|(\\r)|(\\n)", " ");
				// truncate if too long
				if (notes.length() > maxLength) {
					notes = notes.substring(0, maxLength - 1);
					notes += "...";
				}
			}
			// set the song label text
			this.lblSongTitle.setText(MessageFormat.format(Messages.getString("panel.song.current.pattern"), song.getTitle(), notes));
			// set the song label tooltip
			if (song.getNotes() != null && song.getNotes().length() > 0) {
				this.lblSongTitle.setToolTipText(StringUtilities.addLineBreaksAtInterval(song.getNotes(), 50, true));
			} else {
				this.lblSongTitle.setToolTipText(null);
			}
			for (SongPart part : song.getParts()) {
				this.cmbParts.addItem(part);
			}
			// set the loading status
			this.scrPreview.setLoading(true);
			// update the current song
			this.queueSongPreview(this.song);
		} else {
			// set the song label text
			this.lblSongTitle.setText(MessageFormat.format(Messages.getString("panel.song.current.pattern"), Messages.getString("panel.song.default.title"), ""));
			this.lblSongTitle.setToolTipText(null);
			// add one dummy item to the parts combo box
			this.cmbParts.addItem(new SongPart());
			// clear the preview
			this.pnlPreview.clearSlides();
			this.scrPreview.updatePanelSize();
			this.pnlPreview.repaint();
			// clear the edit song panel
			this.pnlEditSong.setSong(null);
		}
	}
	
	/**
	 * Queues a new song to be previewed.
	 * @param song the song
	 */
	private void queueSongPreview(Song song) {
		// its possible that the preview thread was interrupted or stopped
		// so make sure its still running
		if (!this.previewThread.isAlive()) {
			// if the current thread is no longer alive (running) then
			// create another and start it
			this.previewThread = new SongPreivewThread();
			this.previewThread.start();
		}
		
		// execute the preview update
		this.previewThread.queueSong(song);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.data.song.ui.SongListener#songAdded(org.praisenter.data.song.Song)
	 */
	@Override
	public void songAdded(Song song) {
		MutableSongTableModel model = (MutableSongTableModel)this.tblSongQueue.getModel();
		model.addRow(song);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.data.song.ui.SongListener#songChanged(org.praisenter.data.song.Song)
	 */
	@Override
	public void songChanged(Song song) {
		// update the song queue table (really only the title needs to be updated)
		MutableSongTableModel model = (MutableSongTableModel)this.tblSongQueue.getModel();
		model.updateRow(song);
		
		// update the preview panel
		this.setSong(song);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.data.song.ui.SongListener#songDeleted(org.praisenter.data.song.Song)
	 */
	@Override
	public void songDeleted(Song song) {
		// update the song queue table
		MutableSongTableModel model = (MutableSongTableModel)this.tblSongQueue.getModel();
		model.removeRow(song);
		
		// clear the preview and edit panels
		if (this.song != null && this.song.getId() == song.getId()) {
			this.setSong(null);
		} else {
			// it could be that the song we deleted was in the edit song panel only
			// which happens when its a new song
			Song eSong = this.pnlEditSong.getSong();
			if (eSong != null && eSong.getId() == song.getId()) {
				this.pnlEditSong.setSong(null);
			}
		}
	}
	
	/**
	 * Sets the table column widths for the song search results table.
	 */
	private void setSongSearchTableWidths() {
		this.tblSongSearchResults.getColumnModel().getColumn(0).setMaxWidth(35);
		this.tblSongSearchResults.getColumnModel().getColumn(0).setPreferredWidth(35);
		this.tblSongSearchResults.getColumnModel().getColumn(1).setMaxWidth(200);
		this.tblSongSearchResults.getColumnModel().getColumn(1).setPreferredWidth(200);
	}
	
	/**
	 * Sets the table column widths for the song queue table.
	 */
	private void setSongQueueTableWidths() {
		this.tblSongQueue.getColumnModel().getColumn(0).setMaxWidth(35);
		this.tblSongQueue.getColumnModel().getColumn(0).setPreferredWidth(35);
		this.tblSongQueue.getColumnModel().getColumn(1).setPreferredWidth(100);
	}
	
	/**
	 * Callback for song searching to update the table and results text.
	 * @author William Bittle
	 * @version 2.0.0
	 * @since 1.0.0
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
				SongSearchTableModel model = null;
				if (songs != null && songs.size() > 0) {
					model = new SongSearchTableModel(search, songs);
				} else {
					model = new SongSearchTableModel();
				}
				tblSongSearchResults.setModel(model);
				// reset the scrollbar position
				scrSongSearchResults.getVerticalScrollBar().setValue(0);
				setSongSearchTableWidths();
				tblSongSearchResults.getColumnModel().getColumn(0).setCellRenderer(new SongSearchCheckTableCellRenderer(tblSongSearchResults.getDefaultRenderer(Boolean.class)));
				tblSongSearchResults.getColumnModel().getColumn(1).setCellRenderer(new SongSearchTitleTableCellRenderer());
				tblSongSearchResults.getColumnModel().getColumn(2).setCellRenderer(new SongSearchMatchTableCellRenderer(search));
				// disable sorting any column other than the title column
				// it doesn't make sense to sort the matched text or selection column
				TableRowSorter<SongSearchTableModel> sorter = new TableRowSorter<SongSearchTableModel>(model);
				sorter.setSortable(0, false);
				sorter.setSortable(2, false);
				tblSongSearchResults.setRowSorter(sorter);
			} else {
				String message = MessageFormat.format(Messages.getString("panel.song.data.search.exception.text"), search.getText());
				ExceptionDialog.show(
						SongsPanel.this, 
						Messages.getString("panel.song.data.search.exception.title"), 
						message, 
						ex);
				LOGGER.error(message, ex);
			}
		}
	}
	
	/**
	 * Represents a task to update the song preview panel and song controls.
	 * <p>
	 * Rendering of songs may take some time depending on the number of parts they have.  This
	 * thread will pre-generate the previews and then update the preview panel when complete.
	 * <p>
	 * We need to wait on the generated previews before we can send any of the slides.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class SongPreivewThread extends Thread {
		/** The blocking queue */
		private final BlockingQueue<Song> songQueue;
		
		/**
		 * Default constructor.
		 */
		public SongPreivewThread() {
			super("SongPreviewThread");
			this.setDaemon(true);
			this.songQueue = new ArrayBlockingQueue<Song>(10);
		}

		/**
		 * Queues a new song to be shown.
		 * @param song the song
		 */
		public void queueSong(Song song) {
			this.songQueue.add(song);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// make the thread run always
			while (true) {
				try {
					// end when this panel is no longer displayable
					if (!isDisplayable()) {
						return;
					}
					// poll for any queued searches
					final Song song = this.songQueue.poll(1000, TimeUnit.MILLISECONDS);
					// if no queued search then just continue
					if (song != null) {
						final SongSlideTemplate template = getSelectedTemplate();
						
						// update the slides (this method should not
						// do anything that should normally be done on the EDT)
						pnlPreview.setSong(song, template);
						
						// update the preview panel
						try {
							
							// we need to block this thread until this code
							// runs since we will get problems if another song
							// is in the queue
							SwingUtilities.invokeAndWait(new Runnable() {
								@Override
								public void run() {
									scrPreview.setLoading(false);
									
									// update the state of the send controls
									if (song.getParts().size() > 0) {
										cmbParts.setEnabled(true);
										btnSend.setEnabled(true);
									}
									
									// update the quick send panel
									pnlQuickSend.setButtonsEnabled(song);
									
									// update the song edit panel
									pnlEditSong.setSong(song);
									
									pnlEditSong.setTemplate(template);
								}
							});
						} catch (InvocationTargetException e) {
							// in this case just continue
							LOGGER.warn("An error occurred while updating the song preview on the EDT: ", e);
						}
					}
				} catch (InterruptedException ex) {
					// if the song search thread is interrupted then just stop it
					LOGGER.info("SongPreviewThread was interrupted. Stopping thread.", ex);
					break;
				}
			}
		}
	}
}
