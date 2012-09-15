package org.praisenter.panel.bible;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.praisenter.data.bible.Verse;

/**
 * Table model for the SavedBibleVerses.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MutableBibleTableModel extends BibleTableModel {
	/** The version id */
	private static final long serialVersionUID = -8734004357778640711L;

	/** Remove column name */
	protected static final String SELECT = "";
	
	/** The list of selection status */
	protected List<Boolean> selectedItems = new ArrayList<Boolean>();
	
	/**
	 * Adds the given verse to this table model.
	 * @param verse the verse
	 */
	public void addRow(Verse verse) {
		// make sure the verses array is not null
		if (this.verses == null) {
			// if it is, then create it
			this.verses = new ArrayList<Verse>();
		}
		// add the verse
		this.verses.add(verse);
		// add a selection item
		this.selectedItems.add(false);
		// let the listeners know that the table had some
		// rows inserted
		int index = this.verses.size() - 1;
		this.fireTableRowsInserted(index, index);
	}
	
	/**
	 * Removes the given row.
	 * @param rowIndex the row to remove
	 */
	public void removeRow(int rowIndex) {
		// make sure the row exists
		if (this.verses != null && this.verses.size() > rowIndex) {
			// remove the row
			this.verses.remove(rowIndex);
			// remove its corresponding selection row
			this.selectedItems.remove(rowIndex);
			// let the listeners know that the table had a row deleted
			this.fireTableRowsDeleted(rowIndex, rowIndex);
		}
	}
	
	/**
	 * Removes all the currently selected rows.
	 */
	public void removeSelectedRows() {
		// make sure there are some rows
		if (this.verses != null && this.verses.size() > 0) {
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
				this.verses.remove(j);
				// remove its corresponding selection row
				this.selectedItems.remove(j);
			}
			// let the listeners know that we have changed the
			// table in some way
			this.fireTableDataChanged();
		}
	}
	
	/**
	 * Removes all the rows.
	 */
	public void removeAllRows() {
		// make sure there are some rows
		if (this.verses != null && this.verses.size() > 0) {
			// clear the verses
			this.verses.clear();
			// clear the selected items
			this.selectedItems.clear();
			// let the listeners know that we have changed the
			// table in some way
			this.fireTableDataChanged();
		}
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
