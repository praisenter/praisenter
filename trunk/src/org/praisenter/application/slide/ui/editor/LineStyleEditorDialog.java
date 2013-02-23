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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.praisenter.application.resources.Messages;
import org.praisenter.application.ui.BottomButtonPanel;
import org.praisenter.slide.graphics.LineStyle;

/**
 * Dialog for choosing a line style.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class LineStyleEditorDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -4898946623099190356L;

	/** True if the action was cancelled */
	private boolean isCancel;
	
	/** The fill editor panel */
	private LineStyleEditorPanel pnlLineEditor;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param style the line style
	 */
	private LineStyleEditorDialog(Window owner, LineStyle style) {
		super(owner, Messages.getString("panel.slide.editor.line"), ModalityType.APPLICATION_MODAL);
		
		this.isCancel = false;
		this.pnlLineEditor = new LineStyleEditorPanel(style);
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.pnlLineEditor, BorderLayout.CENTER);
		
		JButton btnOk = new JButton(Messages.getString("panel.slide.editor.ok"));
		btnOk.addActionListener(this);
		btnOk.setActionCommand("ok");
		
		JButton btnCancel = new JButton(Messages.getString("panel.slide.editor.cancel"));
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("cancel");
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.TRAILING));
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);
		
		container.add(this.pnlLineEditor, BorderLayout.CENTER);
		container.add(pnlButtons, BorderLayout.PAGE_END);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("ok".equals(command)) {
			this.setVisible(false);
		} else if ("cancel".equals(command)) {
			this.isCancel = true;
			this.setVisible(false);
		}
	}
	
	/**
	 * Shows a new {@link LineStyleEditorDialog} using the given initial line style.
	 * <p>
	 * Returns the user configured line style when the user clicks the accept button
	 * and returns null if the user clicks the cancel button.
	 * @param owner the dialog owner
	 * @param style the initial line style
	 * @return {@link LineStyle}
	 */
	public static final LineStyle show(Window owner, LineStyle style) {
		LineStyleEditorDialog dialog = new LineStyleEditorDialog(owner, style);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		if (dialog.isCancel) {
			style = null;
		} else {
			style = dialog.pnlLineEditor.getLineStyle();
		}
		
		dialog.dispose();
		
		return style;
	}
}
