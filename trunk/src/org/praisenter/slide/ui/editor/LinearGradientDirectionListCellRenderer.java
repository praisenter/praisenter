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
package org.praisenter.slide.ui.editor;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.LinearGradientDirection;

/**
 * List cell renderer for a list of {@link LinearGradientDirection} objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class LinearGradientDirectionListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = 172531208527442471L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value == LinearGradientDirection.BOTTOM) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.bottom"));
		} else if (value == LinearGradientDirection.BOTTOM_LEFT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.bottomLeft"));
		} else if (value == LinearGradientDirection.BOTTOM_RIGHT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.bottomRight"));
		} else if (value == LinearGradientDirection.LEFT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.left"));
		} else if (value == LinearGradientDirection.RIGHT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.right"));
		} else if (value == LinearGradientDirection.TOP) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.top"));
		} else if (value == LinearGradientDirection.TOP_LEFT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.topLeft"));
		} else if (value == LinearGradientDirection.TOP_RIGHT) {
			this.setText(Messages.getString("panel.slide.editor.fill.gradient.linear.topRight"));
		}
		return this;
	}
}
