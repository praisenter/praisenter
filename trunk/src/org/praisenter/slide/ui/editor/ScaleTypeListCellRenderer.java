package org.praisenter.slide.ui.editor;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.ScaleType;

/**
 * List cell renderer for a list of {@link ScaleType} objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ScaleTypeListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -4804000683571079432L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value == ScaleType.UNIFORM) {
			this.setText(Messages.getString("panel.slide.editor.scale.uniform"));
			this.setToolTipText(Messages.getString("panel.slide.editor.scale.uniform.tooltip"));
		} else if (value == ScaleType.NONUNIFORM) {
			this.setText(Messages.getString("panel.slide.editor.scale.nonuniform"));
			this.setToolTipText(Messages.getString("panel.slide.editor.scale.nonuniform.tooltip"));
		} else if (value == ScaleType.NONE) {
			this.setText(Messages.getString("panel.slide.editor.scale.none"));
			this.setToolTipText(Messages.getString("panel.slide.editor.scale.none.tooltip"));
		}
		return this;
	}
}
