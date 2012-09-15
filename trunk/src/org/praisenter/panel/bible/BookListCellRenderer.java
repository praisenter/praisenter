package org.praisenter.panel.bible;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.data.bible.Book;

/**
 * List cell renderer for a list of {@link Book} objects.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BookListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -7278859139143556108L;

	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof Book) {
			Book book = (Book)value;
			this.setText(book.getName());
		}
		return this;
	}
}
