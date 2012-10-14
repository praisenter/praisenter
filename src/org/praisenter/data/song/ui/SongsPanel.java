package org.praisenter.data.song.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.praisenter.data.DataException;
import org.praisenter.data.errors.ui.ExceptionDialog;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.data.song.Songs;
import org.praisenter.display.DisplayFactory;
import org.praisenter.display.SongDisplay;
import org.praisenter.display.ui.ScrollableInlineDisplayPreviewPanel;
import org.praisenter.resources.Messages;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.settings.SongSettings;
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
	
	// need a song search
	// need a song queue
	// need song edit caps.
	// need song preview caps.
	// need song send caps.
	// think about fast sending (what should we do here? hot keys etc.)
	
	// current song
	
	private SongPanel pnlSong;
	
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
	
	private SongDisplayPreviewPanel pnlPreview;
//	private JScrollPane scrPreview;
	private ScrollableInlineDisplayPreviewPanel<SongDisplayPreviewPanel, SongDisplay> scrPreview;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("serial")
	public SongsPanel() {
		// FIXME add preview panel
		this.pnlPreview = new SongDisplayPreviewPanel();
		this.pnlPreview.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.scrPreview = new ScrollableInlineDisplayPreviewPanel<SongDisplayPreviewPanel, SongDisplay>(this.pnlPreview);
		
		// create current song section
		this.pnlSong = new SongPanel(null);
		this.pnlSong.addSongListener(this);
		
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
		this.txtSongSearch.addFocusListener(new SelectTextFocusListener(this.txtSongSearch));
		
		JButton btnSongSearch = new JButton(Messages.getString("panel.songs.search"));
		btnSongSearch.setActionCommand("search");
		btnSongSearch.addActionListener(this);
		
		this.tblSongSearchResults = new JTable(new MutableSongTableModel()) {
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
		this.tblSongSearchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tblSongSearchResults.setColumnSelectionAllowed(false);
		this.tblSongSearchResults.setCellSelectionEnabled(false);
		this.tblSongSearchResults.setRowSelectionAllowed(true);
		this.tblSongSearchResults.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// make sure its a double click
				if (e.getClickCount() > 0) {
					// assume single click
					// go to the data store and get the full song detail
					// get the selected row
					int row = tblSongSearchResults.rowAtPoint(e.getPoint());
					// get the data
					SongTableModel model = (SongTableModel)tblSongSearchResults.getModel();
					Song song = model.getRow(row);
					try {
						// overwrite the song local variable
						song = Songs.getSong(song.getId());
						setSong(song);
					} catch (DataException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
			}
		});
		this.tblSongSearchResults.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.setSongSearchTableWidths();
		JScrollPane scrSongsSearchResults = new JScrollPane(this.tblSongSearchResults);

		JButton btnDeleteSelected = new JButton(Messages.getString("panel.song.deleteSelected"));
		btnDeleteSelected.setActionCommand("delete");
		btnDeleteSelected.addActionListener(this);
		
		JButton btnAdd = new JButton(Messages.getString("panel.song.add"));
		btnAdd.setActionCommand("add");
		btnAdd.addActionListener(this);
		
		// search layout
		JPanel pnlSongSearch = new JPanel();
		GroupLayout ssLayout = new GroupLayout(pnlSongSearch);
		pnlSongSearch.setLayout(ssLayout);
		
		ssLayout.setAutoCreateContainerGaps(true);
		ssLayout.setAutoCreateGaps(true);
		ssLayout.setHorizontalGroup(ssLayout.createParallelGroup()
				.addGroup(ssLayout.createSequentialGroup()
						.addComponent(this.txtSongSearch)
						.addComponent(btnSongSearch))
				.addGroup(ssLayout.createSequentialGroup()
						.addComponent(btnDeleteSelected)
						.addComponent(btnAdd))
				.addComponent(scrSongsSearchResults, 100, 200, 200));
		ssLayout.setVerticalGroup(ssLayout.createSequentialGroup()
				.addGroup(ssLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.txtSongSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSongSearch))
				.addGroup(ssLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(btnDeleteSelected)
						.addComponent(btnAdd))
				.addComponent(scrSongsSearchResults, 100, 200, 200));
		
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
			public void mouseReleased(MouseEvent e) {
				// make sure its a double click
				if (e.getClickCount() > 0) {
					// assume single click
					// go to the data store and get the full song detail
					// get the selected row
					int row = tblSongQueue.rowAtPoint(e.getPoint());
					// get the data
					SongTableModel model = (SongTableModel)tblSongQueue.getModel();
					Song song = model.getRow(row);
					try {
						// overwrite the song local variable
						song = Songs.getSong(song.getId());
						setSong(song);
					} catch (DataException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
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
				.addComponent(scrSongQueueResults, 100, 200, 200));
		sqLayout.setVerticalGroup(sqLayout.createSequentialGroup()
				.addGroup(sqLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(btnRemoveSelected)
						.addComponent(btnRemoveAll))
				.addComponent(scrSongQueueResults, 100, 200, 200));
		
		// song search/queue tabs
		JTabbedPane tabSongQueue = new JTabbedPane();
		tabSongQueue.addTab(Messages.getString("panel.songs.tab.queue"), pnlSongQueue);
		JTabbedPane tabSongSearch = new JTabbedPane();
		tabSongSearch.addTab(Messages.getString("panel.songs.tab.search"), pnlSongSearch);
		
		JPanel bottomTabs = new JPanel();
		bottomTabs.setLayout(new GridLayout(1, 2));
		bottomTabs.add(tabSongQueue);
		bottomTabs.add(tabSongSearch);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(scrPreview)
				.addComponent(this.pnlSong)
				.addComponent(bottomTabs));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(scrPreview, 200, 300, Short.MAX_VALUE)
				.addComponent(this.pnlSong)
				.addComponent(bottomTabs));
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("search".equals(command)) {
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
		} else if ("delete".equals(command)) {
			MutableSongTableModel model = (MutableSongTableModel)this.tblSongSearchResults.getModel();
			// only prompt if there is a song selected
			if (model.isSongSelected()) {
				// verify the user wants to do it
				int choice = JOptionPane.showConfirmDialog(
						this, 
						Messages.getString("panel.songs.delete.message"),
						Messages.getString("panel.songs.delete.title"), 
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					// remove the rows from the table
					List<Song> songs = model.removeSelectedRows();
					// remove the songs from the data store
					for (Song song : songs) {
						try {
							Songs.deleteSong(song.getId());
							this.songDeleted(song);
						} catch (DataException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
							break;
						}
					}
				}
			}
		} else if ("remove".equals(command)) {
			MutableSongTableModel model = (MutableSongTableModel)this.tblSongQueue.getModel();
			model.removeSelectedRows();
		} else if ("removeAll".equals(command)) {
			MutableSongTableModel model = (MutableSongTableModel)this.tblSongQueue.getModel();
			model.removeAllRows();
		} else if ("add".equals(command)) {
			MutableSongTableModel sModel = (MutableSongTableModel)this.tblSongSearchResults.getModel();
			List<Song> selected = sModel.getSelectedRows();
			if (selected.size() > 0) {
				MutableSongTableModel qModel = (MutableSongTableModel)this.tblSongQueue.getModel();
				qModel.addRows(selected);
			}
			sModel.deselectAll();
		}
	}
	
	private void setSong(Song song) {
		// update the song panel
		pnlSong.setSong(song);
		// update the displays
		Dimension displaySize = GeneralSettings.getInstance().getPrimaryDisplaySize();
		SongSettings settings = SongSettings.getInstance();
		this.pnlPreview.removeDisplays();
		for (SongPart part : song.getParts()) {
			SongDisplay display = DisplayFactory.getDisplay(settings, displaySize);
			display.getTextComponent().setText(part.getText());
			display.getTextComponent().setTextFont(display.getTextComponent().getTextFont().deriveFont((float)part.getFontSize()));
			this.pnlPreview.addDisplay(display, part.getName());
		}
		this.scrPreview.updatePanelSize();
		this.scrPreview.revalidate();
		this.pnlPreview.revalidate();
		this.pnlPreview.repaint();
	}
	
	@Override
	public void songAdded(Song song) {
		MutableSongTableModel model = (MutableSongTableModel)this.tblSongQueue.getModel();
		model.addRow(song);
	}
	
	@Override
	public void songChanged(Song song) {
		// update the search/song queue tables (really only the title needs to be updated)
		MutableSongTableModel model = (MutableSongTableModel)this.tblSongSearchResults.getModel();
		model.updateRow(song);
		
		model = (MutableSongTableModel)this.tblSongQueue.getModel();
		model.updateRow(song);
	}
	
	@Override
	public void songDeleted(Song song) {
		// update the search/song queue tables
		MutableSongTableModel model = (MutableSongTableModel)this.tblSongSearchResults.getModel();
		model.removeRow(song);
		
		model = (MutableSongTableModel)this.tblSongQueue.getModel();
		model.removeRow(song);
		
		// see if the current song is the deleted one
		Song oSong = this.pnlSong.getSong();
		if (oSong != null && oSong.equals(song)) {
			this.pnlSong.setSong(null);
		}
	}
	
	/**
	 * Sets the table column widths for the song search results table.
	 */
	private void setSongSearchTableWidths() {
		this.tblSongSearchResults.getColumnModel().getColumn(0).setMaxWidth(35);
		this.tblSongSearchResults.getColumnModel().getColumn(0).setPreferredWidth(35);
		this.tblSongSearchResults.getColumnModel().getColumn(1).setPreferredWidth(100);
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
					tblSongSearchResults.setModel(new MutableSongTableModel(songs));
				} else {
					tblSongSearchResults.setModel(new MutableSongTableModel());
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
}
