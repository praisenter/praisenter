package org.praisenter.data.song.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.praisenter.data.DataException;
import org.praisenter.data.errors.ui.ExceptionDialog;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.data.song.SongPartType;
import org.praisenter.data.song.Songs;
import org.praisenter.display.SongDisplay;
import org.praisenter.display.ui.DisplayWindows;
import org.praisenter.display.ui.ScrollableInlineDisplayPreviewPanel;
import org.praisenter.display.ui.StandardDisplayWindow;
import org.praisenter.resources.Messages;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.settings.SongSettings;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.TransitionAnimator;
import org.praisenter.transitions.Transitions;
import org.praisenter.transitions.ui.TransitionListCellRenderer;
import org.praisenter.ui.SelectTextFocusListener;
import org.praisenter.ui.WaterMark;
import org.praisenter.utilities.StringUtilities;

/**
 * Main ui for song manipulation and display.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongsPanel extends JPanel implements ActionListener, SongListener {
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
	// FIXME we need to make sure that song parts are unique using song part type and index
	
	// song searching
	
	/** The song searching thread */
	private SongSearchThread songSearchThread;
	
	/** The song search text field */
	private JTextField txtSongSearch;
	
	/** The song search results table */
	private JTable tblSongSearchResults;
	
	/** The song queue */
	private JTable tblSongQueue;
	
	// preview
	
	/** The song preview panel */
	private SongDisplayPreviewPanel pnlPreview;
	
	/** The preview panel scroller */
	private ScrollableInlineDisplayPreviewPanel<SongDisplayPreviewPanel, SongDisplay> scrPreview;
	
	// display
	
	/** The song title and notes label */
	private JLabel lblSongTitle;
	
	/** The quick send panel for the standard song parts */
	private SongQuickSendPanel pnlQuickSend;
	
	/** The combo box of all song parts */
	private JComboBox<SongPart> cmbParts;
	
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
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("serial")
	public SongsPanel() {
		GeneralSettings gSettings = GeneralSettings.getInstance();
		SongSettings sSettings = SongSettings.getInstance();
		
		// song preview
		
		this.pnlPreview = new SongDisplayPreviewPanel();
		this.pnlPreview.setBorder(BorderFactory.createEmptyBorder(15, 15, 20, 15));
		this.scrPreview = new ScrollableInlineDisplayPreviewPanel<SongDisplayPreviewPanel, SongDisplay>(this.pnlPreview);
		this.scrPreview.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
		
		// current song and sending
		
		this.lblSongTitle = new JLabel(MessageFormat.format(Messages.getString("panel.songs.current.pattern"), Messages.getString("panel.songs.default.title"), ""));
		this.lblSongTitle.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		this.pnlQuickSend = new SongQuickSendPanel(this);
		
		this.cmbParts = new JComboBox<SongPart>(new SongPart[] { new SongPart() });
		this.cmbParts.setRenderer(new SongPartCellRenderer());
		this.cmbParts.setEnabled(false);
		
		this.btnSend = new JButton(Messages.getString("panel.songs.send"));
		this.btnSend.setToolTipText(Messages.getString("panel.songs.send.tooltip"));
		this.btnSend.addActionListener(this);
		this.btnSend.setActionCommand("send");
		this.btnSend.setFont(this.btnSend.getFont().deriveFont(Font.BOLD, this.btnSend.getFont().getSize2D() + 3.0f));
		
		this.btnClear = new JButton(Messages.getString("panel.songs.clear"));
		this.btnClear.setToolTipText(Messages.getString("panel.songs.clear.tooltip"));
		this.btnClear.addActionListener(this);
		this.btnClear.setActionCommand("clear");
		
		// setup the transition lists
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(gSettings.getPrimaryOrDefaultDisplay());
		
		this.cmbSendTransitions = new JComboBox<Transition>(Transitions.IN);
		this.cmbSendTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbSendTransitions.setSelectedItem(Transitions.getTransitionForId(sSettings.getDefaultSendTransition(), Transition.Type.IN));
		this.txtSendTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtSendTransitions.addFocusListener(new SelectTextFocusListener(this.txtSendTransitions));
		this.txtSendTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtSendTransitions.setValue(sSettings.getDefaultSendTransitionDuration());
		this.txtSendTransitions.setColumns(3);
		
		this.cmbClearTransitions = new JComboBox<Transition>(Transitions.OUT);
		this.cmbClearTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbClearTransitions.setSelectedItem(Transitions.getTransitionForId(sSettings.getDefaultClearTransition(), Transition.Type.OUT));
		this.txtClearTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtClearTransitions.addFocusListener(new SelectTextFocusListener(this.txtClearTransitions));
		this.txtClearTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtClearTransitions.setValue(sSettings.getDefaultClearTransitionDuration());
		this.txtClearTransitions.setColumns(3);
		
		if (!transitionsSupported) {
			this.cmbSendTransitions.setEnabled(false);
			this.txtSendTransitions.setEnabled(false);
			this.cmbClearTransitions.setEnabled(false);
			this.txtClearTransitions.setEnabled(false);
		}
		
		JPanel pnlSending = new JPanel();
		GroupLayout sendLayout = new GroupLayout(pnlSending);
		pnlSending.setLayout(sendLayout);
		sendLayout.setAutoCreateContainerGaps(true);
		sendLayout.setAutoCreateGaps(true);
		sendLayout.setHorizontalGroup(sendLayout.createSequentialGroup()
				.addGroup(sendLayout.createParallelGroup()
						.addComponent(this.lblSongTitle)
						.addComponent(this.pnlQuickSend))
				.addGroup(sendLayout.createParallelGroup()
						.addComponent(this.cmbParts, 155, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
						.addComponent(this.cmbParts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
								.addComponent(this.btnClear, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
		
		// searching
		
		// create and start the song search thread
		this.songSearchThread = new SongSearchThread();
		this.songSearchThread.start();
		
		this.txtSongSearch = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.songs.search.watermark"));
			}
		};
		this.txtSongSearch.setActionCommand("search");
		this.txtSongSearch.addActionListener(this);
		this.txtSongSearch.addFocusListener(new SelectTextFocusListener(this.txtSongSearch));
		
		JButton btnSongSearch = new JButton(Messages.getString("panel.songs.search"));
		btnSongSearch.setActionCommand("search");
		btnSongSearch.addActionListener(this);
		
		JButton btnAdd = new JButton(Messages.getString("panel.song.add"));
		btnAdd.setToolTipText(Messages.getString("panel.song.add.tooltip"));
		btnAdd.setActionCommand("add");
		btnAdd.addActionListener(this);
		
		JButton btnDelete = new JButton(Messages.getString("panel.song.delete"));
		btnDelete.setToolTipText(Messages.getString("panel.song.delete.tooltip"));
		btnDelete.setActionCommand("delete");
		btnDelete.addActionListener(this);
		
		this.tblSongSearchResults = new JTable(new SongSearchTableModel()) {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = this.rowAtPoint(p);
				
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
		this.setSongSearchTableWidths();
		JScrollPane scrSongsSearchResults = new JScrollPane(this.tblSongSearchResults);
		
		// search layout
		JPanel pnlSongSearch = new JPanel();
		GroupLayout ssLayout = new GroupLayout(pnlSongSearch);
		pnlSongSearch.setLayout(ssLayout);
		
		ssLayout.setAutoCreateContainerGaps(true);
		ssLayout.setAutoCreateGaps(true);
		ssLayout.setHorizontalGroup(ssLayout.createParallelGroup()
				.addGroup(ssLayout.createSequentialGroup()
						.addComponent(this.txtSongSearch)
						.addComponent(btnSongSearch)
						.addComponent(btnAdd)
						.addComponent(btnDelete))
				.addComponent(scrSongsSearchResults, 400, 400, Short.MAX_VALUE));
		ssLayout.setVerticalGroup(ssLayout.createSequentialGroup()
				.addGroup(ssLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.txtSongSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSongSearch)
						.addComponent(btnAdd)
						.addComponent(btnDelete))
				.addComponent(scrSongsSearchResults, 200, 200, Short.MAX_VALUE));
		
		// the song queue
		this.tblSongQueue = new JTable(new MutableSongTableModel()) {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = this.rowAtPoint(p);
				
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
		this.setSongQueueTableWidths();
		JScrollPane scrSongQueueResults = new JScrollPane(this.tblSongQueue);
		
		JButton btnRemoveSelected = new JButton(Messages.getString("panel.song.queue.remove"));
		btnRemoveSelected.setActionCommand("remove");
		btnRemoveSelected.addActionListener(this);
		
		JButton btnRemoveAll = new JButton(Messages.getString("panel.song.queue.removeAll"));
		btnRemoveAll.setActionCommand("removeAll");
		btnRemoveAll.addActionListener(this);
		
		// queue layout
		JPanel pnlSongQueue = new JPanel();
		GroupLayout sqLayout = new GroupLayout(pnlSongQueue);
		pnlSongQueue.setLayout(sqLayout);
		
		sqLayout.setAutoCreateContainerGaps(true);
		sqLayout.setAutoCreateGaps(true);
		sqLayout.setHorizontalGroup(sqLayout.createParallelGroup()
				.addGroup(sqLayout.createSequentialGroup()
						.addComponent(btnRemoveSelected)
						.addComponent(btnRemoveAll))
				.addComponent(scrSongQueueResults, 400, 400, Short.MAX_VALUE));
		sqLayout.setVerticalGroup(sqLayout.createSequentialGroup()
				.addGroup(sqLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(btnRemoveSelected)
						.addComponent(btnRemoveAll))
				.addComponent(scrSongQueueResults, 200, 200, Short.MAX_VALUE));
		
		// edit panel
		this.pnlEditSong = new EditSongPanel(null);
		this.pnlEditSong.addSongListener(this);
		
		// song search/queue tabs
		JTabbedPane tabBottom = new JTabbedPane();
		tabBottom.addTab(Messages.getString("panel.songs.tab.search"), pnlSongSearch);
		tabBottom.addTab(Messages.getString("panel.songs.tab.queue"), pnlSongQueue);
		tabBottom.addTab(Messages.getString("panel.songs.tab.editSong"), this.pnlEditSong);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.scrPreview, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pnlSending, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(tabBottom));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.scrPreview, 200, 300, Short.MAX_VALUE)
				.addComponent(pnlSending, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(tabBottom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("search".equals(command)) {
			this.searchSongsAction();
		} else if ("delete".equals(command)) {
			this.deleteSongAction();
		} else if ("remove".equals(command)) {
			MutableSongTableModel model = (MutableSongTableModel)this.tblSongQueue.getModel();
			model.removeSelectedRows();
		} else if ("removeAll".equals(command)) {
			MutableSongTableModel model = (MutableSongTableModel)this.tblSongQueue.getModel();
			model.removeAllRows();
		} else if ("add".equals(command)) {
			this.addSongToSongQueueAction();
		} else if (command.startsWith("quickSend=")) {
			this.quickSendAction(command);
		} else if ("send".equals(command)) {
			this.sendAction();
		} else if ("clear".equals(command)) {
			this.clearAction();
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
			SongSearch search = new SongSearch(text, new SongSearchCallback());
			this.songSearchThread.queueSearch(search);
		}
	}
	
	/**
	 * Deletes the selected song in the song search table.
	 */
	private void deleteSongAction() {
		SongSearchTableModel model = (SongSearchTableModel)this.tblSongSearchResults.getModel();
		int row = this.tblSongSearchResults.getSelectedRow();
		// only prompt if there is a song selected
		if (row >= 0) {
			// get the song
			Song song = model.getRow(row);
			// verify the user wants to do it
			int choice = JOptionPane.showConfirmDialog(
					this, 
					MessageFormat.format(Messages.getString("panel.song.delete.message"), song.getTitle(), song.getId()),
					Messages.getString("panel.song.delete.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				// remove the row(s) from the search results table
				model.remove(song);
				// remove the song from the data store
				try {
					Songs.deleteSong(song.getId());
					// clear the current song preview (if needed)
					// clear the song from the song queue (if there)
					this.songDeleted(song);
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
		}
	}
	
	/**
	 * Adds the selected song in the song search table to the song queue table.
	 */
	private void addSongToSongQueueAction() {
		SongSearchTableModel model = (SongSearchTableModel)this.tblSongSearchResults.getModel();
		int row = this.tblSongSearchResults.getSelectedRow();
		// only prompt if there is a song selected
		if (row >= 0) {
			Song song = model.getRow(row);
			MutableSongTableModel qModel = (MutableSongTableModel)this.tblSongQueue.getModel();
			qModel.addRow(song);
		}
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
		
		SongDisplay display = this.pnlPreview.getDisplay(key);
		if (display != null) {
			this.sendDisplay(display);
		}
	}
	
	/**
	 * Sends the currently selected song part to the primary display.
	 */
	private void sendAction() {
		SongPart part = (SongPart)this.cmbParts.getSelectedItem();
		if (part != null) {
			SongDisplay display = this.pnlPreview.getDisplay(part);
			if (display != null) {
				this.sendDisplay(display);
			}
		}
	}
	
	/**
	 * Sends the given {@link SongDisplay} to the primary display.
	 * <p>
	 * This method uses the current values of the send transition combo box and
	 * textbox.
	 * @param display the display to send
	 */
	private void sendDisplay(SongDisplay display) {
		// get the transition
		Transition transition = (Transition)this.cmbSendTransitions.getSelectedItem();
		int duration = ((Number)this.txtSendTransitions.getValue()).intValue();
		TransitionAnimator ta = new TransitionAnimator(transition, duration);
		StandardDisplayWindow primary = DisplayWindows.getPrimaryDisplayWindow();
		if (primary != null) {
			primary.send(display, ta);
		} else {
			// the device is no longer available
			LOGGER.warn("The primary display doesn't exist.");
			JOptionPane.showMessageDialog(
					this, 
					Messages.getString("dialog.device.primary.missing.text"), 
					Messages.getString("dialog.device.primary.missing.title"), 
					JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Clears the primary display.
	 */
	private void clearAction() {
		// get the transition
		Transition transition = (Transition)this.cmbClearTransitions.getSelectedItem();
		int duration = ((Number)this.txtClearTransitions.getValue()).intValue();
		TransitionAnimator ta = new TransitionAnimator(transition, duration);
		StandardDisplayWindow primary = DisplayWindows.getPrimaryDisplayWindow();
		if (primary != null) {
			primary.clear(ta);
		}
	}
	
	/**
	 * Sets the current song.
	 * @param song the song
	 */
	private void setSong(Song song) {
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
			this.lblSongTitle.setText(MessageFormat.format(Messages.getString("panel.songs.current.pattern"), song.getTitle(), notes));
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
			Thread thread = new Thread(new UpdateCurrentSongTask(song));
			thread.setName("RenderPreviewThread");
			thread.setDaemon(true);
			thread.start();
		} else {
			// set the song label text
			this.lblSongTitle.setText(MessageFormat.format(Messages.getString("panel.songs.current.pattern"), Messages.getString("panel.songs.default.title"), ""));
			this.lblSongTitle.setToolTipText(null);
			// add one dummy item to the parts combo box
			this.cmbParts.addItem(new SongPart());
			// clear the preview
			this.pnlPreview.setSong(null);
			this.scrPreview.updatePanelSize();
			this.pnlPreview.repaint();
			// clear the edit song panel
			this.pnlEditSong.setSong(null);
		}
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
			if (this.pnlEditSong.getSong().getId() == song.getId()) {
				this.pnlEditSong.setSong(null);
			}
		}
	}
	
	/**
	 * Sets the table column widths for the song search results table.
	 */
	private void setSongSearchTableWidths() {
		this.tblSongSearchResults.getColumnModel().getColumn(0).setMaxWidth(200);
		this.tblSongSearchResults.getColumnModel().getColumn(0).setPreferredWidth(200);
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
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class SongSearchCallback extends SongSearchThread.Callback {
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
					tblSongSearchResults.setModel(new SongSearchTableModel(songs));
				} else {
					tblSongSearchResults.setModel(new SongSearchTableModel());
				}
				setSongSearchTableWidths();
			} else {
				String message = MessageFormat.format(Messages.getString("panel.bible.data.search.exception.text"), search.getText());
				ExceptionDialog.show(
						SongsPanel.this, 
						Messages.getString("panel.bible.data.search.exception.title"), 
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
	 * task will pre-generate the previews and then update the preview panel when complete.
	 * <p>
	 * We need to wait on the generated previews before we can send any of the displays too.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class UpdateCurrentSongTask implements Runnable {
		/** The song to render */
		private Song song;
		
		/**
		 * Minimal constructor.
		 * @param song the song to render
		 */
		public UpdateCurrentSongTask(Song song) {
			this.song = song;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// update the displays
			pnlPreview.setSong(this.song);
			
			// update the preview panel
			SwingUtilities.invokeLater(new Runnable() {
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
				}
			});
		}
	}
}
