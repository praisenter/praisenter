package org.praisenter.slide.ui.editor;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.CapType;

/**
 * List cell renderer for a list of {@link CapType} objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class CapTypeListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -233621781959503272L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value == CapType.BUTT) {
			this.setText(Messages.getString("panel.slide.editor.line.cap.butt"));
		} else if (value == CapType.ROUND) {
			this.setText(Messages.getString("panel.slide.editor.line.cap.round"));
		} else if (value == CapType.SQUARE) {
			this.setText(Messages.getString("panel.slide.editor.line.cap.square"));
		}
		return this;
	}
}
