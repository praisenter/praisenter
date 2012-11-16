package org.praisenter.slide.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.slide.transitions.easing.Easing;

/**
 * List cell renderer for a list of {@link Easing} objects.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class EasingListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -1463086080761109418L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof Easing) {
			Easing easing = (Easing)value;
			this.setText(easing.getName());
		}
		return this;
	}
}
