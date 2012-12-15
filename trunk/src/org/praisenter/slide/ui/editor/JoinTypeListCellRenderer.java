package org.praisenter.slide.ui.editor;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.JoinType;

/**
 * List cell renderer for a list of {@link JoinType} objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class JoinTypeListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -676675740581533554L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value == JoinType.BEVEL) {
			this.setText(Messages.getString("panel.slide.editor.line.join.bevel"));
		} else if (value == JoinType.ROUND) {
			this.setText(Messages.getString("panel.slide.editor.line.join.round"));
		} else if (value == JoinType.MITER) {
			this.setText(Messages.getString("panel.slide.editor.line.join.miter"));
		}
		return this;
	}
}
