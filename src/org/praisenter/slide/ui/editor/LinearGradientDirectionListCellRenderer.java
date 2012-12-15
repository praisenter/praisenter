package org.praisenter.slide.ui.editor;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.LinearGradientDirection;

/**
 * List cell renderer for a list of {@link LinearGradientDirection} objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class LinearGradientDirectionListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = 172531208527442471L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value == LinearGradientDirection.BOTTOM) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.bottom"));
		} else if (value == LinearGradientDirection.BOTTOM_LEFT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.bottomLeft"));
		} else if (value == LinearGradientDirection.BOTTOM_RIGHT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.bottomRight"));
		} else if (value == LinearGradientDirection.LEFT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.left"));
		} else if (value == LinearGradientDirection.RIGHT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.right"));
		} else if (value == LinearGradientDirection.TOP) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.top"));
		} else if (value == LinearGradientDirection.TOP_LEFT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.topLeft"));
		} else if (value == LinearGradientDirection.TOP_RIGHT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.topRight"));
		}
		return this;
	}
}
