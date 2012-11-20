package org.praisenter.slide.ui;

import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingConstants;

import org.praisenter.slide.SlideThumbnail;

/**
 * Custom list cell renderer for {@link SlideThumbnail}s.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideThumbnailComboBoxRenderer extends DefaultListCellRenderer {	
	/** The version id */
	private static final long serialVersionUID = -8260540909617276091L;

	private static final BufferedImage test = new BufferedImage(64, 1, BufferedImage.TYPE_INT_ARGB);
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof SlideThumbnail) {
			SlideThumbnail t = (SlideThumbnail)value;
			this.setVerticalTextPosition(SwingConstants.CENTER);
			this.setText(t.getName());
			this.setToolTipText(t.getFile().getName());
			if (index >= 0) {
				this.setHorizontalTextPosition(SwingConstants.RIGHT);
				this.setIcon(new ImageIcon(t.getImage()));
			} else {
				this.setHorizontalTextPosition(SwingConstants.LEFT);
				this.setIcon(new ImageIcon(test));
			}
		}
		return this;
	}
}
