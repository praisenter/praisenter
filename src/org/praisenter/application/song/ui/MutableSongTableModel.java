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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.praisenter.data.song.Song;

/**
 * Table model for the Song listing.
 * @author William Bittle
 * @version 2.0.1
 * @since 1.0.0
 */
public class MutableSongTableModel extends SongTableModel {
	/** The version id */
	private static final long serialVersionUID = 4774945432718624517L;

	/** Remove column name */
	protected static final String SELECT = "";
	
	/** The list of selection status */
	protected List<Boolean> selectedItems = new ArrayList<Boolean>();
	
	/**
	 * Default constructor.
	 */
	public MutableSongTableModel() {
		super();
	}

	/**
	 * Full constructor.
	 * @param songs the list of songs
	 */
	public MutableSongTableModel(List<Song> songs) {
		super(songs);
		// add a selected boolean for each song
		for(int i = 0; i < songs.size(); i++) {
			this.selectedItems.add(false);
		}
	}

	/**
	 * Adds the given song to this table model.
	 * @param song the song
	 */
	public void addRow(Song song) {
		// make sure the songs array is not null
		if (this.songs == null) {
			// if it is, then create it
			this.songs = new ArrayList<Song>();
		}
		// add the song
		this.songs.add(song);
		// add a selection item
		this.selectedItems.add(false);
		// let the listeners know that the table had some
		// rows inserted
		int index = this.songs.size() - 1;
		this.fireTableRowsInserted(index, index);
	}
	
	/**
	 * Adds the given songs to this table model.
	 * @param songs the songs
	 */
	public void addRows(List<Song> songs) {
		if (songs != null && songs.size() > 0) {
			// make sure the songs array is not null
			if (this.songs == null) {
				// if it is, then create it
				this.songs = new ArrayList<Song>();
			}
			int size = this.songs.size();
			// add all the songs
			this.songs.addAll(songs);
			// create all the selected bools
			for (int i = 0; i < songs.size(); i++) {
				this.selectedItems.add(false);
			}
			// let the listener know about the added rows
			this.fireTableRowsInserted(size, this.songs.size() - 1);
		}
	}

	/**
	 * Removes the given row.
	 * @param rowIndex the row to remove
	 */
	public void removeRow(int rowIndex) {
		// make sure the row exists
		if (this.songs != null && this.songs.size() > rowIndex) {
			// remove the row
			this.songs.remove(rowIndex);
			// remove its corresponding selection row
			this.selectedItems.remove(rowIndex);
			// let the listeners know that the table had a row deleted
			this.fireTableRowsDeleted(rowIndex, rowIndex);
		}
	}

	/**
	 * Removes the given row.
	 * @param song the song row to remove
	 */
	public void removeRow(Song song) {
		// make sure the row exists
		if (this.songs != null) {
			int rowIndex = this.songs.indexOf(song);
			if (rowIndex >= 0) {
				// remove the row
				this.songs.remove(rowIndex);
				// remove its corresponding selection row
				this.selectedItems.remove(rowIndex);
				// let the listeners know that the table had a row deleted
				this.fireTableRowsDeleted(rowIndex, rowIndex);
			}
		}
	}
	
	/**
	 * Updates the given row.
	 * @param song the song row to update
	 */
	public void updateRow(Song song) {
		// make sure the row exists
		if (this.songs != null) {
			int rowIndex = this.songs.indexOf(song);
			if (rowIndex >= 0) {
				// remove the row
				this.songs.set(rowIndex, song);
				// let the listeners know that the table had a row deleted
				this.fireTableRowsUpdated(rowIndex, rowIndex);
			}
		}
	}
	
	/**
	 * Removes all the currently selected rows
	 * and returns them.
	 * @return List&lt;{@link Song}&gt;
	 */
	public List<Song> removeSelectedRows() {
		List<Song> songs = new ArrayList<Song>();
		// make sure there are some rows
		if (this.songs != null && this.songs.size() > 0) {
			// loop over the selected items list and store
			// all the indexes of those selected
			List<Integer> indices = new LinkedList<Integer>();
			int i = 0;
			for (Boolean bool : this.selectedItems) {
				// if its selected then add it to the list
				if (bool) {
					// we add them at the beginning of the
					// list because we need to remove them in
					// the order from last to first so that the
					// indexes remain valid
					indices.add(0, i);
				}
				i++;
			}
			// remove all the indexes that were selected
			for (int j : indices) {
				// remove the verse
				Song song = this.songs.remove(j);
				// remove its corresponding selection row
				this.selectedItems.remove(j);
				// add the song to the removed list
				songs.add(song);
			}
			// let the listeners know that we have changed the
			// table in some way
			this.fireTableDataChanged();
		}
		
		return songs;
	}

	/**
	 * Removes all the rows.
	 */
	public void removeAllRows() {
		// make sure there are some rows
		if (this.songs != null && this.songs.size() > 0) {
			// clear the verses
			this.songs.clear();
			// clear the selected items
			this.selectedItems.clear();
			// let the listeners know that we have changed the
			// table in some way
			this.fireTableDataChanged();
		}
	}
	
	/**
	 * Returns all the currently selected rows.
	 * @return List&lt;{@link Song}&gt;
	 */
	public List<Song> getSelectedRows() {
		List<Song> songs = new ArrayList<Song>();
		for (int i = 0; i < this.songs.size(); i++) {
			if (this.selectedItems.get(i)) {
				songs.add(this.songs.get(i));
			}
		}
		return songs;
	}

	/**
	 * Returns true if any song is selected.
	 * @return boolean
	 */
	public boolean isSongSelected() {
		for (Boolean bool : this.selectedItems) {
			if (bool) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * De-selects all selected rows.
	 */
	public void deselectAll() {
		for (int i = 0; i < this.selectedItems.size(); i++) {
			this.selectedItems.set(i, false);
		}
		this.fireTableDataChanged();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.panel.bible.BibleTableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		// the mutable table has one more column
		return super.getColumnCount() + 1;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.panel.bible.BibleTableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// the first column is a selection checkbox
		if (columnIndex == 0) {
			return this.selectedItems.get(rowIndex);
		}
		return super.getValueAt(rowIndex, columnIndex - 1);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// the first column is a selection checkbox
		if (columnIndex == 0) {
			return Boolean.class;
		}
		return super.getColumnClass(columnIndex - 1);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.panel.bible.BibleTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// the first column is a selection checkbox
		if (columnIndex == 0) {
			return true;
		}
		return super.isCellEditable(rowIndex, columnIndex - 1);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// the first column is a selection checkbox
		if (columnIndex == 0) {
			this.selectedItems.set(rowIndex, !this.selectedItems.get(rowIndex));
		}
		super.setValueAt(aValue, rowIndex, columnIndex - 1);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.panel.bible.BibleTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		// the first column is a selection checkbox
		if (column == 0) {
			return SELECT;
		}
		return super.getColumnName(column - 1);
	}
}
