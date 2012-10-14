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
public class SongTableModel extends AbstractTableModel {
	/** The version id */
	private static final long serialVersionUID = -1023237320303369947L;
	
	/** The column names */
	protected final String[] columnNames = new String[] {
		Messages.getString("panel.songs.title")
	};
	
	/** The data (list of songs) */
	protected List<Song> songs;
	
	/** Default constructor */
	public SongTableModel() {}
	
	/**
	 * Full constructor.
	 * @param songs the list of songs
	 */
	public SongTableModel(List<Song> songs) {
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
			switch (columnIndex) {
				case 0:
					return song.getTitle();
				default:
					return "";
			}
		}
		return null;
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
