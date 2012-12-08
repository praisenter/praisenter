package org.praisenter.slide.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.resources.Messages;

/**
 * List cell renderer for a list of {@link TemplateType} enums.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class TemplateTypeListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -4814333313863405562L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value == TemplateType.BIBLE) {
			this.setText(Messages.getString("panel.slide.template.type.bible"));
		} else if (value == TemplateType.SONG) {
			this.setText(Messages.getString("panel.slide.template.type.song"));
		} else if (value == TemplateType.NOTIFICATION) {
			this.setText(Messages.getString("panel.slide.template.type.notification"));
		} else {
			this.setText(Messages.getString("panel.slide.template.type.slide"));
		}
		return this;
	}
}
