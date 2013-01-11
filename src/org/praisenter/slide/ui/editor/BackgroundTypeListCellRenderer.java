package org.praisenter.slide.ui.editor;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.resources.Messages;

/**
 * List cell renderer for a list of {@link BackgroundType} objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class BackgroundTypeListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -544122893593864480L;

	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value == BackgroundType.NONE) {
			this.setText(Messages.getString("panel.slide.editor.background.type.none"));
		} else if (value == BackgroundType.IMAGE) {
			this.setText(Messages.getString("panel.slide.editor.background.type.image"));
		} else if (value == BackgroundType.PAINT) {
			this.setText(Messages.getString("panel.slide.editor.background.type.paint"));
		} else if (value == BackgroundType.VIDEO) {
			this.setText(Messages.getString("panel.slide.editor.background.type.video"));
		}
		return this;
	}
}
