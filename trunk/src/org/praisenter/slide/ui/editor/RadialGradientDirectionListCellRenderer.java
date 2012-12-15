package org.praisenter.slide.ui.editor;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.RadialGradientDirection;

/**
 * List cell renderer for a list of {@link RadialGradientDirection} objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class RadialGradientDirectionListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = 2033012064480468204L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value == RadialGradientDirection.CENTER) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.radial.center"));
		} else if (value == RadialGradientDirection.BOTTOM_LEFT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.radial.bottomLeft"));
		} else if (value == RadialGradientDirection.BOTTOM_RIGHT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.radial.bottomRight"));
		} else if (value == RadialGradientDirection.TOP_LEFT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.radial.topLeft"));
		} else if (value == RadialGradientDirection.TOP_RIGHT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.radial.topRight"));
		}
		return this;
	}
}