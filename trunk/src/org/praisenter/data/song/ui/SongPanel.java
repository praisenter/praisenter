package org.praisenter.data.song.ui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.praisenter.data.DataException;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.data.song.SongPartType;
import org.praisenter.data.song.Songs;
import org.praisenter.resources.Messages;
import org.praisenter.ui.SelectTextFocusListener;
import org.praisenter.ui.WaterMark;
import org.praisenter.utilities.StringUtilities;

/**
 * Panel used to edit a song.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongPanel extends JPanel implements ActionListener, DocumentListener, SongListener {
	/** The version id */
	private static final long serialVersionUID = -6559268693745087405L;
	
	/** The song being modified */
	private Song song;
	
	// controls
	
	/** The save button */
	private JButton btnSave;
	
	/** The text box for the song title */
	private JTextField txtTitle;
	
	/** The text box for the song notes */
	private JTextArea txtNotes;
	
	/** The table of song parts */
	private JTable tblSongParts;
	
	/** The add part button */
	private JButton btnAddPart;
	
	/** The delete parts button */
	private JButton btnDeleteSelectedParts;
	
	// song parts
	
	/** The song part edit panel */
	private SongPartPanel pnlSongPart;
	
	// editing
	
	/** True if the song has been changed but not saved */
	private boolean songChanged;

	private boolean notificationsDisabled;
	
	/**
	 * Full constructor.
	 * @param song the song to edit/show; can be null
	 */
	@SuppressWarnings("serial")
	public SongPanel(Song song) {
		this.song = song;
		this.songChanged = false;
		this.notificationsDisabled = true;
		
		// FIXME add save button
		
		this.btnSave = new JButton(Messages.getString("panel.song.save"));
		this.btnSave.setActionCommand("save");
		this.btnSave.addActionListener(this);
		
		JButton btnNew = new JButton(Messages.getString("panel.song.new"));
		btnNew.setActionCommand("new");
		btnNew.addActionListener(this);
		
		this.pnlSongPart = new SongPartPanel(song, null);
		this.pnlSongPart.addSongListener(this);
		
		String title = "";
		String notes = "";
		List<SongPart> parts = null;
		boolean edit = false;
		
		if (song != null) {
			title = song.getTitle();
			notes = song.getNotes();
			parts = song.getParts();
			edit = true;
		} else {
			parts = new ArrayList<SongPart>();
		}
		
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
		this.txtTitle.setEnabled(edit);
		this.txtTitle.getDocument().addDocumentListener(this);
		
		// FIXME notes needs to be done differently (like a popup or something)
		this.txtNotes = new JTextArea();
		this.txtNotes.setRows(5);
		this.txtNotes.setLineWrap(true);
		this.txtNotes.setWrapStyleWord(true);
		this.txtNotes.setText(notes);
		this.txtNotes.setEnabled(edit);
		JScrollPane pneNotes = new JScrollPane(this.txtNotes);
		
		this.tblSongParts = new JTable() {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = this.rowAtPoint(p);
				
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
					// get the data
					SongPartTableModel model = (SongPartTableModel)tblSongParts.getModel();
					SongPart part = model.getRow(row);
					// set the part panel up
					pnlSongPart.setSongPart(SongPanel.this.song, part);
				}
			}
		});
		this.tblSongParts.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		JScrollPane scrSongParts = new JScrollPane(this.tblSongParts);
		
		this.tblSongParts.setModel(new MutableSongPartTableModel(parts));
		this.setSongPartTableWidths();
		
		this.btnAddPart = new JButton(Messages.getString("panel.song.part.new"));
		this.btnAddPart.setActionCommand("addPart");
		this.btnAddPart.addActionListener(this);
		this.btnAddPart.setEnabled(edit);
		
		this.btnDeleteSelectedParts = new JButton(Messages.getString("panel.song.part.deleteSelected"));
		this.btnDeleteSelectedParts.setActionCommand("deleteSelectedParts");
		this.btnDeleteSelectedParts.addActionListener(this);
		this.btnDeleteSelectedParts.setEnabled(edit);
		
		this.notificationsDisabled = false;
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(this.txtTitle)
						.addComponent(btnNew)
						.addComponent(this.btnSave))
				.addGroup(layout.createSequentialGroup()
						.addComponent(scrSongParts, 200, 260, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup()
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.btnAddPart)
										.addComponent(this.btnDeleteSelectedParts))
								.addComponent(this.pnlSongPart))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNew)
						.addComponent(this.btnSave))
				.addGroup(layout.createParallelGroup()
						.addComponent(scrSongParts, 50, 150, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup()
										.addComponent(this.btnAddPart)
										.addComponent(this.btnDeleteSelectedParts))
								.addComponent(this.pnlSongPart))));
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
	
	@Override
	public void songAdded(Song song) {}
	
	@Override
	public void songChanged(Song song) {
		if (!this.notificationsDisabled) {
			this.setSongChanged(true);
			// we need to refresh the data in the song table
			int selectedRow = this.tblSongParts.getSelectedRow();
			AbstractTableModel model = (AbstractTableModel)this.tblSongParts.getModel();
			model.fireTableDataChanged();
			this.tblSongParts.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
		}
	}
	
	@Override
	public void songDeleted(Song song) {}
	
	@Override
	public void changedUpdate(DocumentEvent e) {
		this.updateSongTitle(this.txtTitle.getText());
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		this.updateSongTitle(this.txtTitle.getText());
	}
	
	@Override
	public void removeUpdate(DocumentEvent e) {
		this.updateSongTitle(this.txtTitle.getText());
	}
	
	private void updateSongTitle(String title) {
		if (!this.notificationsDisabled) {
			if (this.song != null) {
				this.song.setTitle(title);
				this.setSongChanged(true);
			}
		}
	}
	
	private void setSongChanged(boolean flag) {
		this.songChanged = flag;
		this.btnSave.setEnabled(flag);
	}
	
	public Song getSong() {
		return this.song;
	}
	
	/**
	 * Sets the current song.
	 * @param song the song; can be null
	 */
	public void setSong(Song song) {
		// check if the current song was changed first
		if (this.songChanged) {
			int choice = JOptionPane.showConfirmDialog(
					this, 
					MessageFormat.format(Messages.getString("panel.song.switch.confirm.message"), this.song.getTitle()), 
					Messages.getString("panel.song.switch.confirm.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				// then save the song
				try {
					boolean isNew = this.song.isNew();
					Songs.saveSong(this.song);
					this.notifySongListeners(isNew);
				} catch (DataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// set the new song
		this.song = song;
		this.setSongChanged(false);
		this.notificationsDisabled = true;
		
		String title = "";
		String notes = "";
		List<SongPart> parts = null;
		boolean edit = false;
		
		this.pnlSongPart.setSongPart(song, null);
		if (song != null) {
			title = song.getTitle();
			notes = song.getNotes();
			parts = song.getParts();
			edit = true;
		} else {
			parts = new ArrayList<SongPart>();
		}
		
		this.txtTitle.setText(title);
		this.txtNotes.setText(notes);
		this.txtNotes.setCaretPosition(0);
		
		this.tblSongParts.setModel(new MutableSongPartTableModel(parts));
		this.setSongPartTableWidths();
		
		this.txtTitle.setEnabled(edit);
		this.txtNotes.setEnabled(edit);
		this.btnAddPart.setEnabled(edit);
		this.btnDeleteSelectedParts.setEnabled(edit);
		
		this.notificationsDisabled = false;
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
				this.pnlSongPart.setSongPart(this.song, part);
			}
		} else if ("deleteSelectedParts".equals(command)) {
			if (this.song != null) {
				MutableSongPartTableModel model = (MutableSongPartTableModel)this.tblSongParts.getModel();
				// remove the selected parts (checked ones)
				List<SongPart> parts = model.removeSelectedRows();
				// remove the parts from the song
				Iterator<SongPart> it = this.song.getParts().iterator();
				boolean sDeleted = false;
				while (it.hasNext()) {
					SongPart cp = it.next();
					for (SongPart part : parts) {
						if (cp.equals(part)) {
							if (part.equals(this.pnlSongPart.getSongPart())) {
								sDeleted = true;
							}
							it.remove();
						}
					}
				}
				if (sDeleted) {
					// the selected line was deleted so null out the part panel
					this.pnlSongPart.setSongPart(this.song, null);
				}
				this.setSongChanged(true);
			}
		} else if ("new".equals(command)) {
			this.song = new Song();
			this.setSong(this.song);
			this.setSongChanged(true);
		} else if ("save".equals(command)) {
			try {
				boolean isNew = this.song.isNew();
				Songs.saveSong(this.song);
				this.notifySongListeners(isNew);
				this.setSongChanged(false);
			} catch (DataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	
	public void addSongListener(SongListener listener) {
		this.listenerList.add(SongListener.class, listener);
	}
}
