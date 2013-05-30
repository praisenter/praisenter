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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.application.icons.Icons;
import org.praisenter.application.resources.Messages;
import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.slide.RenderableComponent;
import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.Fill;

/**
 * Abstact editor panel for {@link RenderableComponent}s.
 * @param <E> the {@link RenderableComponent} type
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class RenderableComponentEditorPanel<E extends RenderableComponent> extends SlideComponentEditorPanel<E> implements ChangeListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = -8456563715108565220L;
	
	/** The checkbox for background paint visibility */
	protected JCheckBox chkBackgroundVisible;
	
	/** The background fill label */
	protected JLabel lblBackground;
	
	/** The background fill button */
	protected JButton btnBackgroundFill;
	
	/**
	 * Default constructor.
	 */
	protected RenderableComponentEditorPanel() {
		// background
		this.lblBackground = new JLabel(Messages.getString("panel.slide.editor.background"));
		
		this.btnBackgroundFill = new JButton(Icons.FILL);
		this.btnBackgroundFill.addActionListener(this);
		this.btnBackgroundFill.setActionCommand("bg-fill");
		
		this.chkBackgroundVisible = new JCheckBox(Messages.getString("panel.slide.editor.visible"));
		this.chkBackgroundVisible.addChangeListener(this);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == this.chkBackgroundVisible) {
			if (this.slideComponent != null) {
				boolean flag = this.chkBackgroundVisible.isSelected();
				this.slideComponent.setBackgroundVisible(flag);
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("bg-fill".equals(command)) {
			Fill fill = new ColorFill();
			if (this.slideComponent != null) {
				fill = this.slideComponent.getBackgroundFill();
			}
			fill = FillEditorDialog.show(WindowUtilities.getParentWindow(this), fill);
			if (fill != null) {
				if (this.slideComponent != null) {
					this.slideComponent.setBackgroundFill(fill);
					this.notifyEditorListeners();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.SlideComponentEditorPanel#setSlideComponent(org.praisenter.slide.SlideComponent, boolean)
	 */
	public void setSlideComponent(E slideComponent, boolean isStatic) {
		super.setSlideComponent(slideComponent, isStatic);
		
		this.disableNotification();
		if (slideComponent != null) {
			this.chkBackgroundVisible.setSelected(slideComponent.isBackgroundVisible());
		} else {
			this.chkBackgroundVisible.setSelected(false);
		}
		this.enableNotification();
	}
}