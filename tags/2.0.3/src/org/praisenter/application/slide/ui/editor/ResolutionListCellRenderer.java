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
package org.praisenter.application.slide.ui.editor;

import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.text.MessageFormat;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.application.preferences.Resolution;
import org.praisenter.application.resources.Messages;
import org.praisenter.common.utilities.WindowUtilities;

/**
 * List cell renderer for a list of {@link Resolution} objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ResolutionListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = 7195442835688788991L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		GraphicsDevice[] devices = WindowUtilities.getDevices();
		if (value instanceof Resolution) {
			Resolution r = (Resolution)value;
			for (GraphicsDevice device : devices) {
				DisplayMode mode = device.getDisplayMode();
				Resolution o = new Resolution(mode.getWidth(), mode.getHeight());
				if (r.equals(o)) {
					this.setText(MessageFormat.format(Messages.getString("resolution.format.native"), r.getWidth(), r.getHeight()));
					break;
				} else {
					this.setText(MessageFormat.format(Messages.getString("resolution.format"), r.getWidth(), r.getHeight()));
				}
			}
		}
		
		return this;
	}
}
