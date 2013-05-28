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

import javax.swing.JList;

import org.apache.log4j.Logger;
import org.praisenter.animation.transitions.CircularCollapse;
import org.praisenter.animation.transitions.CircularExpand;
import org.praisenter.animation.transitions.Fade;
import org.praisenter.animation.transitions.HorizontalSplitCollapse;
import org.praisenter.animation.transitions.HorizontalSplitExpand;
import org.praisenter.animation.transitions.PushDown;
import org.praisenter.animation.transitions.PushLeft;
import org.praisenter.animation.transitions.PushRight;
import org.praisenter.animation.transitions.PushUp;
import org.praisenter.animation.transitions.Swap;
import org.praisenter.animation.transitions.SwipeDown;
import org.praisenter.animation.transitions.SwipeLeft;
import org.praisenter.animation.transitions.SwipeRight;
import org.praisenter.animation.transitions.SwipeUp;
import org.praisenter.animation.transitions.Transition;
import org.praisenter.animation.transitions.VerticalSplitCollapse;
import org.praisenter.animation.transitions.VerticalSplitExpand;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.ui.PraisenterListCellRenderer;

/**
 * List cell renderer for a list of {@link Transition} objects.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public class TransitionListCellRenderer extends PraisenterListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = 8489812432376046994L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(TransitionListCellRenderer.class);
	
	/**
	 * Minimal constructor.
	 * @param component the component this renderer is for
	 */
	public TransitionListCellRenderer(Component component) {
		super(component);
	}

	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof Transition) {
			Transition transition = (Transition)value;
			int id = transition.getId();
			
			if (id == Swap.ID) {
				this.setText(Messages.getString("transition.swap"));
			} else if (id == CircularCollapse.ID) {
				this.setText(Messages.getString("transition.circularCollapse"));
			} else if (id == CircularExpand.ID) {
				this.setText(Messages.getString("transition.circularExpand"));
			} else if (id == Fade.ID) {
				this.setText(Messages.getString("transition.fade"));
			} else if (id == HorizontalSplitCollapse.ID) {
				this.setText(Messages.getString("transition.horizontalSplitCollapse"));
			} else if (id == HorizontalSplitExpand.ID) {
				this.setText(Messages.getString("transition.horizontalSplitExpand"));
			} else if (id == PushDown.ID) {
				this.setText(Messages.getString("transition.pushDown"));
			} else if (id == PushLeft.ID) {
				this.setText(Messages.getString("transition.pushLeft"));
			} else if (id == PushRight.ID) {
				this.setText(Messages.getString("transition.pushRight"));
			} else if (id == PushUp.ID) {
				this.setText(Messages.getString("transition.pushUp"));
			} else if (id == SwipeDown.ID) {
				this.setText(Messages.getString("transition.swipeDown"));
			} else if (id == SwipeLeft.ID) {
				this.setText(Messages.getString("transition.swipeLeft"));
			} else if (id == SwipeRight.ID) {
				this.setText(Messages.getString("transition.swipeRight"));
			} else if (id == SwipeUp.ID) {
				this.setText(Messages.getString("transition.swipeUp"));
			} else if (id == VerticalSplitCollapse.ID) {
				this.setText(Messages.getString("transition.verticalSplitCollapse"));
			} else if (id == VerticalSplitExpand.ID) {
				this.setText(Messages.getString("transition.verticalSplitExpand"));
			} else {
				LOGGER.warn("Unknown transition: " + transition.getClass().getName());
				this.setText(transition.getClass().getSimpleName());
			}
		}
		return this;
	}
}
