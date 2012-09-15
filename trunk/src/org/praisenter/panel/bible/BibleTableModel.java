package org.praisenter.panel.bible;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.praisenter.data.bible.Verse;
import org.praisenter.resources.Messages;

/**
 * Table model for the results of a bible search.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BibleTableModel extends AbstractTableModel {
	/** The version id */
	private static final long serialVersionUID = -1023237320303369947L;
	
	/** The column names */
	protected final String[] columnNames = new String[] {
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
					return verse.getBook().getName();
				case 1:
					return verse.getChapter();
				case 2:
					return verse.getVerse();
				case 3:
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
