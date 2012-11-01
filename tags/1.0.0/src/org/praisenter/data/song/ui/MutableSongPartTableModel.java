package org.praisenter.data.song.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.praisenter.data.song.SongPart;

/**
 * Editable table model for the song parts.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MutableSongPartTableModel extends SongPartTableModel {
	/** The version id */
	private static final long serialVersionUID = -8734004357778640711L;

	/** Remove column name */
	protected static final String SELECT = "";
	
	/** The list of selection status */
	protected List<Boolean> selectedItems = new ArrayList<Boolean>();
	
	/**
	 * Default constructor.
	 */
	public MutableSongPartTableModel() {
		super();
	}

	/**
	 * Full constructor.
	 * @param parts the song parts
	 */
	public MutableSongPartTableModel(List<SongPart> parts) {
		super(parts);
		for(int i = 0; i < parts.size(); i++) {
			this.selectedItems.add(false);
		}
	}

	/**
	 * Adds the given song part to this table model.
	 * @param part the song part
	 */
	public void addRow(SongPart part) {
		// make sure the songs array is not null
		if (this.parts == null) {
			// if it is, then create it
			this.parts = new ArrayList<SongPart>();
		}
		// add the song
		this.parts.add(part);
		// add a selection item
		this.selectedItems.add(false);
		// let the listeners know that the table had some
		// rows inserted
		int index = this.parts.size() - 1;
		this.fireTableRowsInserted(index, index);
	}
	
	/**
	 * Removes the given row.
	 * @param rowIndex the row to remove
	 */
	public void removeRow(int rowIndex) {
		// make sure the row exists
		if (this.parts != null && this.parts.size() > rowIndex) {
			// remove the row
			this.parts.remove(rowIndex);
			// remove its corresponding selection row
			this.selectedItems.remove(rowIndex);
			// let the listeners know that the table had a row deleted
			this.fireTableRowsDeleted(rowIndex, rowIndex);
		}
	}
	
	/**
	 * Removes all the currently selected rows.
	 * @return List&lt;{@link SongPart}&gt;
	 */
	public List<SongPart> removeSelectedRows() {
		List<SongPart> parts = new ArrayList<SongPart>();
		// make sure there are some rows
		if (this.parts != null && this.parts.size() > 0) {
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
				SongPart part = this.parts.remove(j);
				// remove its corresponding selection row
				this.selectedItems.remove(j);
				// add the part to the removed list
				parts.add(part);
			}
			// let the listeners know that we have changed the
			// table in some way
			this.fireTableDataChanged();
		}
		
		return parts;
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
