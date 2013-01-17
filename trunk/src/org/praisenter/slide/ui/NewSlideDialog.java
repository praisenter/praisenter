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
package org.praisenter.slide.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

import org.praisenter.preferences.Preferences;
import org.praisenter.resources.Messages;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.BasicSlideTemplate;
import org.praisenter.slide.BibleSlideTemplate;
import org.praisenter.slide.NotificationSlideTemplate;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SongSlideTemplate;
import org.praisenter.ui.BottomButtonPanel;

/**
 * Dialog to determine what type of slide or template the user wants to create.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class NewSlideDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 9101398260793943094L;
	
	// data
	
	/** A new instance of the selected slide/template type */
	private Slide slide;
	
	// controls
	
	/** The slide radio button */
	private JRadioButton rdoSlide;
	
	/** The template radio button */
	private JRadioButton rdoTemplate;
	
	/** The slide template radio button */
	private JRadioButton rdoSlideTemplate;
	
	/** The bible template radio button */
	private JRadioButton rdoBibleTemplate;
	
	/** The song template radio button */
	private JRadioButton rdoSongTemplate;
	
	/** The notification template radio button */
	private JRadioButton rdoNotificationTemplate;
	
	/**
	 * Minimal constructor.
	 * @param owner the owner of the this dialog; can be null
	 */
	protected NewSlideDialog(Window owner) {
		super(owner, Messages.getString("panel.slide.create"), ModalityType.APPLICATION_MODAL);
		
		JLabel lblDescription = new JLabel(Messages.getString("panel.slide.create.description"));
		
		this.rdoSlide = new JRadioButton(Messages.getString("panel.slide.create.type.slide"));
		this.rdoTemplate = new JRadioButton(Messages.getString("panel.slide.create.type.template"));
		
		ButtonGroup bgType = new ButtonGroup();
		bgType.add(this.rdoSlide);
		bgType.add(this.rdoTemplate);
		
		this.rdoSlideTemplate = new JRadioButton(Messages.getString("panel.slide.create.template.basic"));
		this.rdoBibleTemplate = new JRadioButton(Messages.getString("panel.slide.create.template.bible"));
		this.rdoSongTemplate = new JRadioButton(Messages.getString("panel.slide.create.template.song"));
		this.rdoNotificationTemplate = new JRadioButton(Messages.getString("panel.slide.create.template.notification"));
		
		ButtonGroup bgTemplateType = new ButtonGroup();
		bgTemplateType.add(this.rdoSlideTemplate);
		bgTemplateType.add(this.rdoBibleTemplate);
		bgTemplateType.add(this.rdoSongTemplate);
		bgTemplateType.add(this.rdoNotificationTemplate);
		
		JButton btnCreate = new JButton(Messages.getString("panel.slide.create.continue"));
		btnCreate.addActionListener(this);
		btnCreate.setActionCommand("continue");
		JButton btnCancel = new JButton(Messages.getString("panel.slide.create.cancel"));
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("cancel");
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.TRAILING));
		pnlButtons.add(btnCreate);
		pnlButtons.add(btnCancel);
		
		// default
		
		this.rdoSlide.setSelected(true);
		this.rdoSlideTemplate.setSelected(true);
		
		this.rdoSlideTemplate.setEnabled(false);
		this.rdoBibleTemplate.setEnabled(false);
		this.rdoSongTemplate.setEnabled(false);
		this.rdoNotificationTemplate.setEnabled(false);
		
		// events
		
		this.rdoSlide.addActionListener(this);
		this.rdoTemplate.addActionListener(this);
		
		Container container = this.getContentPane();
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		JSeparator sep = new JSeparator();
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(lblDescription)
				.addGroup(layout.createSequentialGroup()
						.addComponent(this.rdoSlide)
						.addComponent(this.rdoTemplate))
				.addComponent(sep)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(this.rdoSlideTemplate)
								.addComponent(this.rdoBibleTemplate))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.rdoSongTemplate)
								.addComponent(this.rdoNotificationTemplate)))
				.addComponent(pnlButtons));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(lblDescription)
				.addGroup(layout.createParallelGroup()
						.addComponent(this.rdoSlide)
						.addComponent(this.rdoTemplate))
				.addComponent(sep)
				.addGroup(layout.createParallelGroup()
						.addComponent(this.rdoSlideTemplate)
						.addComponent(this.rdoSongTemplate))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.rdoBibleTemplate)
						.addComponent(this.rdoNotificationTemplate))
				.addComponent(pnlButtons));
		
		this.pack();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Object source = e.getSource();
		if (source == this.rdoSlide) {
			this.rdoSlideTemplate.setEnabled(false);
			this.rdoBibleTemplate.setEnabled(false);
			this.rdoSongTemplate.setEnabled(false);
			this.rdoNotificationTemplate.setEnabled(false);
		} else if (source == this.rdoTemplate) {
			this.rdoSlideTemplate.setEnabled(true);
			this.rdoBibleTemplate.setEnabled(true);
			this.rdoSongTemplate.setEnabled(true);
			this.rdoNotificationTemplate.setEnabled(true);
		}
		
		if ("continue".equals(command)) {
			// get the target display size
			Dimension size = Preferences.getInstance().getPrimaryOrDefaultDeviceResolution();
			
			if (this.rdoSlide.isSelected()) {
				this.slide = new BasicSlide(Messages.getString("panel.slide.create.slide.name"), size.width, size.height);
			} else {
				// template is selected
				if (this.rdoSlideTemplate.isSelected()) {
					this.slide = new BasicSlideTemplate(Messages.getString("panel.slide.create.template.name"), size.width, size.height);
				} else if (this.rdoBibleTemplate.isSelected()) {
					this.slide = BibleSlideTemplate.getDefaultTemplate(size.width, size.height);
					this.slide.setName(Messages.getString("panel.slide.create.template.name"));
				} else if (this.rdoSongTemplate.isSelected()) {
					this.slide = SongSlideTemplate.getDefaultTemplate(size.width, size.height);
					this.slide.setName(Messages.getString("panel.slide.create.template.name"));
				} else if (this.rdoNotificationTemplate.isSelected()) {
					this.slide = NotificationSlideTemplate.getDefaultTemplate(size.width, size.height);
					this.slide.setName(Messages.getString("panel.slide.create.template.name"));
				}
			}
			
			this.setVisible(false);
		} else if ("cancel".equals(command)) {
			this.slide = null;
			this.setVisible(false);
		}
	}
	
	/**
	 * Shows a new create slide/template dialog and returns a new slide or template to modify.
	 * @param owner the owner of this dialog; can be null
	 * @return {@link Slide}
	 */
	public static final Slide show(Window owner) {
		NewSlideDialog dialog = new NewSlideDialog(owner);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		dialog.dispose();
		
		return dialog.slide;
	}
}