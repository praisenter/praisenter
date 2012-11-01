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
	private static final long serialVersionUID = -1023237320303369947L;
	
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
