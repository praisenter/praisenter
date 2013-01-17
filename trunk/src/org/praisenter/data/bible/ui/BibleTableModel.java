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
package org.praisenter.data.bible.ui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.praisenter.data.bible.Verse;
import org.praisenter.resources.Messages;

/**
 * Table model for the results of a bible search.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public class BibleTableModel extends AbstractTableModel {
	/** The version id */
	private static final long serialVersionUID = 8519101677353181222L;

	/** The column names */
	protected final String[] columnNames = new String[] {
		Messages.getString("panel.bible.version"),
		Messages.getString("panel.bible.book"), 
		Messages.getString("panel.bible.chapter"), 
		Messages.getString("panel.bible.verse"),
		Messages.getString("panel.bible.text")
	};
	
	/** The data (list of verses) */
	protected List<Verse> verses;
	
	/** Default constructor */
	public BibleTableModel() {}
	
	/**
	 * Full constructor.
	 * @param verses the list of verses
	 */
	public BibleTableModel(List<Verse> verses) {
		this.verses = verses;
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
		if (this.verses != null) {
			return this.verses.size();
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (this.verses != null && this.verses.size() > rowIndex) {
			Verse verse = this.verses.get(rowIndex);
			switch (columnIndex) {
				case 0:
					return verse.getBible().getName();
				case 1:
					return verse.getBook().getName();
				case 2:
					return verse.getChapter();
				case 3:
					return verse.getVerse();
				case 4:
					return verse.getText();
				default:
					return "";
			}
		}
		return null;
	}
	
	/**
	 * Returns the row data for the given row index.
	 * @param rowIndex the row index
	 * @return {@link Verse}
	 */
	public Verse getRow(int rowIndex) {
		if (this.verses != null && this.verses.size() > rowIndex) {
			return this.verses.get(rowIndex);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == this.getChapterColumn() || columnIndex == this.getVerseColumn()) {
			return Number.class;
		}
		return super.getColumnClass(columnIndex);
	}
	
	/**
	 * Returns the chapter column number.
	 * @return int
	 */
	protected int getChapterColumn() {
		return 2;
	}
	
	/**
	 * Returns the verse column number.
	 * @return int
	 */
	protected int getVerseColumn() {
		return 3;
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
