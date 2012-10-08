package org.praisenter.data.song.ui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import org.praisenter.data.DataException;
import org.praisenter.data.bible.Verse;
import org.praisenter.data.bible.ui.BibleTableModel;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.Songs;
import org.praisenter.utilities.StringUtilities;

public class SongsPanel extends JPanel {
	// need a song search
	// need a song queue
	// need song edit caps.
	// need song preview caps.
	// need song send caps.
	// think about fast sending (what should we do here? hot keys etc.)
	
	private JTable tblSongSearchResults;
	
	@SuppressWarnings("serial")
	public SongsPanel() {
		List<Song> songs;
		try {
			songs = Songs.searchSongsWithoutParts("majesty");
		} catch (DataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			songs = new ArrayList<Song>();
		}
		
		this.tblSongSearchResults = new JTable(new MutableSongTableModel(songs)) {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = this.rowAtPoint(p);
				
				// get the column value
				TableModel model = this.getModel();
				Object object = model.getValueAt(row, 0);
				if (object != null) {
					// get the song title
					String text = object.toString();
					// split the lines by 50 characters
					return text;
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
				if (e.getClickCount() == 2) {
//					// get the selected row
//					int row = tblBibleSearchResults.rowAtPoint(e.getPoint());
//					// get the data
//					BibleTableModel model = (BibleTableModel)tblBibleSearchResults.getModel();
//					Verse verse = model.getRow(row);
//					// set the selection
//					cmbBooks.setSelectedItem(verse.getBook());
//					// set the numbers
//					txtChapter.setValue(verse.getChapter());
//					txtVerse.setValue(verse.getVerse());
//					// update the labels
//					updateLabels();
//					// update the displays
//					try {
//						updateVerseDisplays(verse);
//					} catch (DataException ex) {
//						// just log this exception because the user
//						// should still be able to click the preview button
//						LOGGER.error("An error occurred while updating the verse displays from a search result: ", ex);
//					}
				}
			}
		});
		this.tblSongSearchResults.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.setSongSearchTableWidths();
		
		JScrollPane scrSongsSearchResults = new JScrollPane(this.tblSongSearchResults);
		
		this.setLayout(new BorderLayout());
		this.add(scrSongsSearchResults, BorderLayout.CENTER);
	}
	
	/**
	 * Sets the table column widths for the song search results table.
	 */
	private void setSongSearchTableWidths() {
		this.tblSongSearchResults.getColumnModel().getColumn(0).setMaxWidth(35);
		this.tblSongSearchResults.getColumnModel().getColumn(0).setPreferredWidth(35);
		this.tblSongSearchResults.getColumnModel().getColumn(1).setPreferredWidth(170);
	}
}
