package org.praisenter.data.song.ui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.praisenter.data.song.Song;
import org.praisenter.resources.Messages;

/**
 * Table model for the results of a song search.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongSearchTableModel extends AbstractTableModel {
	/** The version id */
	private static final long serialVersionUID = -1023237320303369947L;
	
	/** The column names */
	protected final String[] columnNames = new String[] {
		Messages.getString("panel.songs.songTitle"),
		Messages.getString("panel.songs.matchedText")
	};
	
	/** The data (list of songs) */
	protected List<Song> songs;
	
	/** Default constructor */
	public SongSearchTableModel() {}
	
	/**
	 * Full constructor.
	 * @param songs the list of songs
	 */
	public SongSearchTableModel(List<Song> songs) {
		this.songs = songs;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return this.columnNames.length;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		if (this.songs != null) {
			return this.songs.size();
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (this.songs != null && this.songs.size() > rowIndex) {
			Song song = this.songs.get(rowIndex);
			// determine if we need to "group" the results
			boolean groupHeader = this.isGroupHeader(rowIndex);
			switch (columnIndex) {
				case 0:
					return groupHeader ? song.getTitle() : "";
				case 1:
					// replace any new lines with space + new line
					// this allows the text in the table to look normal at line breaks (with a space)
					// but also allows the on hover tooltip to break by line breaks
					return song.getNotes().replaceAll("((\\r\\n)|(\\r)|(\\n))", " $1");
				default:
					return "";
			}
		}
		return null;
	}
	
	/**
	 * Returns true if the current song at the given row index is a
	 * group header or not.
	 * @param rowIndex the row index
	 * @return boolean
	 */
	protected boolean isGroupHeader(int rowIndex) {
		Song song = this.songs.get(rowIndex);
		// this only works because the results are sorted
		if (rowIndex > 0) {
			// get the previous song
			Song prev = this.songs.get(rowIndex - 1);
			// check if they are the same song
			if (prev.getId() == song.getId()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the row data for the given row index.
	 * @param rowIndex the row index
	 * @return {@link Song}
	 */
	public Song getRow(int rowIndex) {
		if (this.songs != null && this.songs.size() > rowIndex) {
			return this.songs.get(rowIndex);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return this.columnNames[column];
	}
}
