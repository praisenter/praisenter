/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.application.slide.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.praisenter.application.resources.Messages;
import org.praisenter.slide.SlideThumbnail;

/**
 * Custom list cell renderer for {@link SlideThumbnail} combo boxes.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public class SlideThumbnailComboBoxRenderer extends DefaultListCellRenderer {	
	/** The version id */
	private static final long serialVersionUID = -8260540909617276091L;

	/** A transparent icon used to fill the space of the selected item */
	private static final BufferedImage SELECTED_ICON = new BufferedImage(64, 1, BufferedImage.TYPE_INT_ARGB);
	
	/** A border to add some padding for string elements */
	private static final Border STRING_ITEM_BORDER = BorderFactory.createEmptyBorder(10, 10, 10, 10);
	
	/** The original JLabel border */
	private Border originalBorder = null;
	
	/** The original JLabel font */
	private Font originalFont = null;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof SlideThumbnail) {
			SlideThumbnail t = (SlideThumbnail)value;
			this.setHorizontalAlignment(SwingConstants.LEFT);
			this.setVerticalTextPosition(SwingConstants.CENTER);
			this.setText(t.getName());
			this.setToolTipText(t.getFile().getName());
			if (index >= 0) {
				this.setHorizontalTextPosition(SwingConstants.RIGHT);
				this.setIcon(new ImageIcon(t.getImage()));
			} else {
				this.setHorizontalTextPosition(SwingConstants.LEFT);
				this.setIcon(new ImageIcon(SELECTED_ICON));
			}
			// use the old border if we have overridden it
			// (originalBorder will be assigned if we override the border)
			if (this.originalBorder != null) {
				this.setBorder(this.originalBorder);
				this.setFont(this.originalFont);
			}
		} else if (value instanceof String) {
			if (index >= 0) {
				// save the old border if we haven't already
				if (this.originalBorder == null) {
					this.originalBorder = this.getBorder();
					this.originalFont = this.getFont();
				}
				this.setToolTipText(Messages.getString("template.manage.tooltip"));
				this.setHorizontalAlignment(SwingConstants.CENTER);
				this.setBorder(STRING_ITEM_BORDER);
				this.setFont(this.originalFont.deriveFont(Font.BOLD, this.originalFont.getSize2D() * 1.2f));
			}
		}
		return this;
	}
}
