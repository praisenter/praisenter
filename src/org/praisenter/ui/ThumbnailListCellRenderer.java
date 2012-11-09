package org.praisenter.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingConstants;

import org.praisenter.xml.Thumbnail;

/**
 * Custom list cell renderer for {@link Thumbnail}s.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ThumbnailListCellRenderer extends DefaultListCellRenderer {	
	/** The version id */
	private static final long serialVersionUID = -8260540909617276091L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof Thumbnail) {
			Thumbnail t = (Thumbnail)value;
			this.setIcon(new ImageIcon(t.getImage()));
			this.setHorizontalTextPosition(SwingConstants.CENTER);
			this.setVerticalTextPosition(SwingConstants.BOTTOM);
			this.setText(t.getFileProperties().getFileName());
			this.setToolTipText(t.getFileProperties().getFileName());
			this.setHorizontalAlignment(CENTER);
		}
		return this;
	}
}
