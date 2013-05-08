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

import java.util.List;

import org.praisenter.application.resources.Messages;
import org.praisenter.data.song.Song;

/**
 * Table model for the results of a song search.
 * @author William Bittle
 * @version 2.0.1
 * @since 1.0.0
 */
public class SongSearchTableModel extends MutableSongTableModel {
	/** The version id */
	private static final long serialVersionUID = -1023237320303369947L;
	
	/** The song search producing the results */
	private SongSearch search;
	
	/** Default constructor */
	public SongSearchTableModel() {}
	
	/**
	 * Full constructor.
	 * @param search the search
	 * @param songs the list of songs
	 */
	public SongSearchTableModel(SongSearch search, List<Song> songs) {
		super(songs);
		this.search = search;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (this.songs != null && this.songs.size() > rowIndex) {
			Song song = this.songs.get(rowIndex);
			// determine if we need to "group" the results
			switch (columnIndex) {
				case 0:
					return super.getValueAt(rowIndex, columnIndex);
				case 1:
					return song.getTitle();
				case 2:
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
		if (this.songs != null) {
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
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.application.song.ui.MutableSongTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (this.songs != null && this.songs.size() > rowIndex && columnIndex == 0) {
			// dont allow editing in rows that are not group headers
			// this makes it to where a checkbox will not show on click of the column
			return this.isGroupHeader(rowIndex);
		}
		return super.isCellEditable(rowIndex, columnIndex);
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
	 * @see org.praisenter.panel.bible.BibleTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		if (column < 2) {
			return super.getColumnName(column);
		} else {
			return Messages.getString("panel.song.matchedText");
		}
	}
	
	/**
	 * Returns the song search.
	 * @return {@link SongSearch}
	 */
	public SongSearch getSearch() {
		return this.search;
	}
}
