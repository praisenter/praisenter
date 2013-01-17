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
package org.praisenter.data.song.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.praisenter.data.song.SongPart;
import org.praisenter.resources.Messages;

/**
 * Table model for the parts of a song.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongPartTableModel extends AbstractTableModel {
	/** The version id */
	private static final long serialVersionUID = 3244985783395704607L;

	/** The column names */
	protected final String[] columnNames = new String[] {
		Messages.getString("panel.song.part.name"),
		Messages.getString("panel.song.part.text")
	};
	
	/** The data (list of song parts) */
	protected List<SongPart> parts;
	
	/** Default constructor */
	public SongPartTableModel() {}
	
	/**
	 * Full constructor.
	 * @param parts the list of song parts
	 */
	public SongPartTableModel(List<SongPart> parts) {
		this.parts = new ArrayList<SongPart>();
		this.parts.addAll(parts);
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
		if (this.parts != null) {
			return this.parts.size();
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (this.parts != null && this.parts.size() > rowIndex) {
			SongPart part = this.parts.get(rowIndex);
			switch (columnIndex) {
				case 0:
					return part.getName();
				case 1:
					// replace any new lines with space + new line
					// this allows the text in the table to look normal at line breaks (with a space)
					// but also allows the on hover tooltip to break by line breaks
					return part.getText().replaceAll("((\\r\\n)|(\\r)|(\\n))", " $1");
				default:
					return "";
			}
		}
		return null;
	}
	
	/**
	 * Returns the row data for the given row index.
	 * @param rowIndex the row index
	 * @return {@link SongPart}
	 */
	public SongPart getRow(int rowIndex) {
		if (this.parts != null && this.parts.size() > rowIndex) {
			return this.parts.get(rowIndex);
		}
		return null;
	}
	
	/**
	 * Returns the row index for the given song part.
	 * <p>
	 * Returns -1 if the song part is not found.
	 * @param part the song part
	 * @return int
	 */
	public int getRowIndex(SongPart part) {
		for (int i = 0; i < this.parts.size(); i++) {
			SongPart p = this.parts.get(i);
			if (p.equals(part)) {
				return i;
			}
		}
		return -1;
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
