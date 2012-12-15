package org.praisenter.slide.ui.editor;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.icons.Icons;
import org.praisenter.resources.Messages;
import org.praisenter.slide.text.FontScaleType;

/**
 * Renderer for showing font scale types.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class FontScaleTypeListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -3747597107187786695L;

	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof FontScaleType) {
			FontScaleType type = (FontScaleType)value;
			if (type == FontScaleType.NONE) {
				this.setText(Messages.getString("panel.slide.editor.text.font.scale.none"));
				this.setToolTipText(Messages.getString("panel.slide.editor.text.font.scale.none.tooltip"));
				this.setIcon(Icons.FONT_SIZE_NONE);
			} else if (type == FontScaleType.REDUCE_SIZE_ONLY) {
				this.setText(Messages.getString("panel.slide.editor.text.font.scale.reduceOnly"));
				this.setToolTipText(Messages.getString("panel.slide.editor.text.font.scale.reduceOnly.tooltip"));
				this.setIcon(Icons.FONT_SIZE_REDUCE_ONLY);
			} else if (type == FontScaleType.BEST_FIT) {
				this.setText(Messages.getString("panel.slide.editor.text.font.scale.bestFit"));
				this.setToolTipText(Messages.getString("panel.slide.editor.text.font.scale.bestFit.tooltip"));
				this.setIcon(Icons.FONT_SIZE_BEST_FIT);
			}
		}
		
		return this;
	}
}
