package org.praisenter.slide.ui.editor;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.DashPattern;

/**
 * List cell renderer for a list of {@link DashPattern} objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class DashPatternListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -4163946563211402422L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value == DashPattern.SOLID) {
			this.setText(Messages.getString("panel.slide.editor.line.pattern.solid"));
		} else if (value == DashPattern.DOT) {
			this.setText(Messages.getString("panel.slide.editor.line.pattern.dot"));
		} else if (value == DashPattern.DASH) {
			this.setText(Messages.getString("panel.slide.editor.line.pattern.dash"));
		} else if (value == DashPattern.DASH_DOT) {
			this.setText(Messages.getString("panel.slide.editor.line.pattern.dashDot"));
		} else if (value == DashPattern.LONG_DASH) {
			this.setText(Messages.getString("panel.slide.editor.line.pattern.longDash"));
		} else if (value == DashPattern.LONG_DASH_DOT) {
			this.setText(Messages.getString("panel.slide.editor.line.pattern.longDashDot"));
		} else if (value == DashPattern.LONG_DASH_DOT_DOT) {
			this.setText(Messages.getString("panel.slide.editor.line.pattern.longDashDotDot"));
		}
		return this;
	}
}
