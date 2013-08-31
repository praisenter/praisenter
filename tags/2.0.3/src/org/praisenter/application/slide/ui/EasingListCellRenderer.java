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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.apache.log4j.Logger;
import org.praisenter.animation.easings.BackEasing;
import org.praisenter.animation.easings.CircularEasing;
import org.praisenter.animation.easings.CubicEasing;
import org.praisenter.animation.easings.Easing;
import org.praisenter.animation.easings.ExponentialEasing;
import org.praisenter.animation.easings.LinearEasing;
import org.praisenter.animation.easings.QuadraticEasing;
import org.praisenter.animation.easings.QuarticEasing;
import org.praisenter.animation.easings.QuinticEasing;
import org.praisenter.animation.easings.SinusoidalEasing;
import org.praisenter.application.resources.Messages;

/**
 * List cell renderer for a list of {@link Easing} objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class EasingListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -1463086080761109418L;
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(EasingListCellRenderer.class);
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof Easing) {
			Easing easing = (Easing)value;
			int id = easing.getId();
			
			if (id == BackEasing.ID) {
				this.setText(Messages.getString("easing.back"));
			} else if (id == CircularEasing.ID) {
				this.setText(Messages.getString("easing.circular"));
			} else if (id == CubicEasing.ID) {
				this.setText(Messages.getString("easing.cubic"));
			} else if (id == ExponentialEasing.ID) {
				this.setText(Messages.getString("easing.exponential"));
			} else if (id == QuadraticEasing.ID) {
				this.setText(Messages.getString("easing.quadratic"));
			} else if (id == QuarticEasing.ID) {
				this.setText(Messages.getString("easing.quartic"));
			} else if (id == QuinticEasing.ID) {
				this.setText(Messages.getString("easing.quintic"));
			} else if (id == SinusoidalEasing.ID) {
				this.setText(Messages.getString("easing.sinusoidal"));
			} else if (id == LinearEasing.ID) {
				this.setText(Messages.getString("easing.linear"));
			} else {
				LOGGER.warn("Unknown easing: " + easing.getClass().getName());
				this.setText(easing.getClass().getSimpleName());
			}
		}
		return this;
	}
}
