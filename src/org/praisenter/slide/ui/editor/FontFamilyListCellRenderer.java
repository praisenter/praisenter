package org.praisenter.slide.ui.editor;

import java.awt.Component;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.utilities.FontManager;

/**
 * List cell renderer for showing font families.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class FontFamilyListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -3747597107187786695L;

	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof String) {
			String family = (String)value;
			// create the font using the family name
			Font font = FontManager.getFont(family, Font.PLAIN, this.getFont().getSize());
			// fix some fonts showing all boxes
			if (font.canDisplayUpTo(family) < 0) {
				// hack to fix some fonts taking up way too much height-wise space
				Rectangle2D bounds = font.getMaxCharBounds(new FontRenderContext(new AffineTransform(), true, true));
				if (bounds.getHeight() <= 50) {
					this.setFont(font);
				}
			}
		}
		
		return this;
	}
}
