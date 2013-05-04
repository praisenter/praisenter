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
package org.praisenter.application.bible.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.praisenter.application.resources.Messages;
import org.praisenter.data.bible.Bible;

/**
 * Table model for the bible library.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public class MutableBibleTableModel extends AbstractTableModel {
	/** The version id */
	private static final long serialVersionUID = -4711169791842006078L;

	/** Remove column name */
	protected static final String SELECT = "";

	/** The column names */
	protected final String[] columnNames = new String[] {
		SELECT,
		Messages.getString("panel.bible.name"),
		Messages.getString("panel.bible.language"), 
		Messages.getString("panel.bible.source")
	};
	
	/** The data (list of bibles) */
	protected List<Bible> bibles;
	
	/** The list of selection status */
	protected List<Boolean> selectedItems;

	/**
	 * Default constructor.
	 */
	public MutableBibleTableModel() {
		this(new ArrayList<Bible>());
	}
	
	/**
	 * Full constructor.
	 * @param bibles the list of bibles
	 */
	public MutableBibleTableModel(List<Bible> bibles) {
		if (bibles == null) {
			bibles = new ArrayList<Bible>();
		}
		this.bibles = bibles;
		this.selectedItems = new ArrayList<Boolean>();
		for (int i = 0; i < bibles.size(); i++) {
			this.selectedItems.add(false);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		if (this.bibles != null) {
			return this.bibles.size();
		}
		return 0;
	}
	
	/**
	 * Returns the row data for the given row index.
	 * @param rowIndex the row index
	 * @return {@link Bible}
	 */
	public Bible getRow(int rowIndex) {
		if (this.bibles != null && this.bibles.size() > rowIndex) {
			return this.bibles.get(rowIndex);
		}
		return null;
	}

	/**
	 * Returns the row data for the table.
	 * @return List&lt;{@link Bible}&gt;
	 */
	public List<Bible> getRows() {
		List<Bible> bibles = new ArrayList<Bible>();
		if (this.bibles != null) {
			bibles.addAll(this.bibles);
		}
		return bibles;
	}
	
	/**
	 * Adds the given bible to this table model.
	 * @param bible the bible
	 */
	public void addRow(Bible bible) {
		// make sure the bibles array is not null
		if (this.bibles == null) {
			// if it is, then create it
			this.bibles = new ArrayList<Bible>();
		}
		// add the bible
		this.bibles.add(bible);
		// add a selection item
		this.selectedItems.add(false);
		// let the listeners know that the table had some
		// rows inserted
		int index = this.bibles.size() - 1;
		this.fireTableRowsInserted(index, index);
	}
	
	/**
	 * Adds all the given bibles to this table model.
	 * @param bibles the bibles to add
	 */
	public void addRows(List<Bible> bibles) {
		if (bibles == null) return;
		// make sure the bibles array is not null
		if (this.bibles == null) {
			// if it is, then create it
			this.bibles = new ArrayList<Bible>();
		}
		int index = this.bibles.size();
		// add the bibles
		for (Bible bible : bibles) {
			this.bibles.add(bible);
			this.selectedItems.add(false);
		}
		this.fireTableRowsInserted(index, index + bibles.size() - 1);
	}
	
	/**
	 * Removes the given row.
	 * @param rowIndex the row to remove
	 */
	public void removeRow(int rowIndex) {
		// make sure the row exists
		if (this.bibles != null && this.bibles.size() > rowIndex) {
			// remove the row
			this.bibles.remove(rowIndex);
			// remove its corresponding selection row
			this.selectedItems.remove(rowIndex);
			// let the listeners know that the table had a row deleted
			this.fireTableRowsDeleted(rowIndex, rowIndex);
		}
	}
	
	/**
	 * Removes all the currently selected rows.
	 * @return List&lt;{@link Bible}&gt;
	 */
	public List<Bible> removeSelectedRows() {
		List<Bible> bibles = new ArrayList<Bible>();
		// make sure there are some rows
		if (this.bibles != null && this.bibles.size() > 0) {
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
				Bible bible = this.bibles.remove(j);
				// remove its corresponding selection row
				this.selectedItems.remove(j);
				// add the song to the removed list
				bibles.add(bible);
			}
			// let the listeners know that we have changed the
			// table in some way
			this.fireTableDataChanged();
		}
		
		return bibles;
	}
	
	/**
	 * Removes all the rows.
	 */
	public void removeAllRows() {
		// make sure there are some rows
		if (this.bibles != null && this.bibles.size() > 0) {
			// clear the verses
			this.bibles.clear();
			// clear the selected items
			this.selectedItems.clear();
			// let the listeners know that we have changed the
			// table in some way
			this.fireTableDataChanged();
		}
	}

	/**
	 * Returns all the currently selected rows.
	 * @return List&lt;{@link Bible}&gt;
	 */
	public List<Bible> getSelectedRows() {
		List<Bible> bibles = new ArrayList<Bible>();
		if (this.bibles != null) {
			for (int i = 0; i < this.bibles.size(); i++) {
				if (this.selectedItems.get(i)) {
					bibles.add(this.bibles.get(i));
				}
			}
		}
		return bibles;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return this.columnNames.length;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (this.bibles != null && this.bibles.size() > rowIndex) {
			Bible bible = this.bibles.get(rowIndex);
			switch (columnIndex) {
				case 0:
					return this.selectedItems.get(rowIndex);
				case 1:
					return bible.getName();
				case 2:
					return bible.getLanguage().toUpperCase();
				case 3:
					return bible.getSource();
				default:
					return "";
			}
		}
		return null;
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
		return super.getColumnClass(columnIndex);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// the first column is a selection checkbox
		if (columnIndex == 0) {
			return true;
		}
		return false;
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
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return this.columnNames[column];
	}
}
