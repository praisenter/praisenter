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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.praisenter.resources.Messages;
import org.praisenter.slide.Resolution;
import org.praisenter.slide.Resolutions;
import org.praisenter.ui.BottomButtonPanel;
import org.praisenter.ui.WaterMark;
import org.praisenter.utilities.WindowUtilities;

/**
 * Dialog used to add a new resolution.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class AddResolutionDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 7219182229540375050L;

	/** The resolution width */
	private JFormattedTextField txtWidth;
	
	/** The resolution height */
	private JFormattedTextField txtHeight;
	
	/** The resolution */
	private Resolution resolution;
	
	/**
	 * Default constructor.
	 * @param owner the owner of this dialog
	 */
	@SuppressWarnings("serial")
	private AddResolutionDialog(Window owner) {
		super(owner, Messages.getString("panel.slide.editor.resolution.new.title"), ModalityType.APPLICATION_MODAL);
		
		this.txtWidth = new JFormattedTextField(new DecimalFormat("0")) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.slide.editor.width"));
			}
		};
		this.txtWidth.setColumns(8);
		
		this.txtHeight = new JFormattedTextField(new DecimalFormat("0")) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.slide.editor.height"));
			}
		};
		this.txtHeight.setColumns(8);
		
		JLabel lblX = new JLabel(Messages.getString("panel.slide.editor.resolution.by"));
		
		JButton btnOk = new JButton(Messages.getString("panel.slide.editor.ok"));
		btnOk.addActionListener(this);
		btnOk.setActionCommand("ok");
		
		JButton btnCancel = new JButton(Messages.getString("panel.slide.editor.cancel"));
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("cancel");
		
		JPanel pnlResolution = new JPanel();
		GroupLayout layout = new GroupLayout(pnlResolution);
		pnlResolution.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(this.txtWidth)
				.addComponent(lblX)
				.addComponent(this.txtHeight));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(this.txtWidth)
				.addComponent(lblX)
				.addComponent(this.txtHeight));

		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.TRAILING));
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);
		
		container.add(pnlResolution, BorderLayout.CENTER);
		container.add(pnlButtons, BorderLayout.PAGE_END);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("ok".equals(command)) {
			// validate the resolution
			int w = 0;
			int h = 0;
			
			Object ow = this.txtWidth.getValue();
			Object oh = this.txtHeight.getValue();
			
			if (ow != null && ow instanceof Number) {
				w = ((Number)ow).intValue();
			}
			if (oh != null && oh instanceof Number) {
				h = ((Number)oh).intValue();
			}
			
			// make sure the width and height are greater than zero
			if (w > 0 && h > 0) {
				// make sure the resolution doesn't already exist
				List<Resolution> resolutions = Resolutions.getResolutions();
				for (Resolution resolution : resolutions) {
					if (resolution.getWidth() == w && resolution.getHeight() == h) {
						JOptionPane.showMessageDialog(
								WindowUtilities.getParentWindow(this), 
								Messages.getString("panel.slide.editor.resolution.exists.message"), 
								Messages.getString("panel.slide.editor.resolution.invalid.title"), 
								JOptionPane.ERROR_MESSAGE);
						this.resolution = null;
						return;
					}
				}
				// if we make it here the resolution is valid
				this.resolution = new Resolution(w, h);
				this.setVisible(false);
			} else {
				JOptionPane.showMessageDialog(
						WindowUtilities.getParentWindow(this), 
						Messages.getString("panel.slide.editor.resolution.invalid.message"), 
						Messages.getString("panel.slide.editor.resolution.invalid.title"), 
						JOptionPane.ERROR_MESSAGE);
				this.resolution = null;
				return;
			}
		} else if ("cancel".equals(command)) {
			this.resolution = null;
			this.setVisible(false);
		}
	}
	
	/**
	 * Shows a {@link AddResolutionDialog} and returns the configured resolution.
	 * <p>
	 * Returns null if the user clicks cancel.
	 * @param owner the owner of the dialog
	 * @return {@link Resolution}
	 */
	public static final Resolution show(Window owner) {
		AddResolutionDialog dialog = new AddResolutionDialog(owner);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		Resolution resolution = dialog.resolution;
		dialog.dispose();
		
		return resolution;
	}
}
