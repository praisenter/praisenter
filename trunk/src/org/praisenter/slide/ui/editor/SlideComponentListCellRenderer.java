package org.praisenter.slide.ui.editor;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.slide.SlideComponent;

/**
 * List cell renderer for a list of {@link SlideComponent} objects.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideComponentListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -2024884866598142560L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof SlideComponent) {
			SlideComponent component = (SlideComponent)value;
			this.setText(component.getName());
		}
		return this;
	}
}
