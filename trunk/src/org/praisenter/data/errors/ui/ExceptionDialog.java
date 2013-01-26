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
package org.praisenter.data.errors.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.praisenter.preferences.Preferences;
import org.praisenter.resources.Messages;
import org.praisenter.utilities.ExceptionUtilities;
import org.praisenter.utilities.WindowUtilities;

/**
 * Dialog used to show exceptions.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExceptionDialog extends JDialog implements MouseListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 4223515214480697820L;
	
	// data
	
	/** The message */
	private String message;
	
	/** The exception */
	private Exception exception;
	
	// controls
	
	/** Text area to display the stack trace */
	private JTextArea txtStackTrace;

	/** The copy/select popup menu */
	private JPopupMenu copyMenu;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param title the dialog title
	 * @param message the message
	 * @param exception the exception
	 * @param sendEnabled true if the send error report button should be enabled
	 */
	private ExceptionDialog(Window owner, String title, String message, Exception exception, boolean sendEnabled) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		this.setIconImage(null);
		// set the size
		this.setPreferredSize(new Dimension(500, 400));
		this.setResizable(true);
		
		// assign the data
		this.message = message;
		this.exception = exception;
		
		// add the logo to the top
		JLabel lblMessage = new JLabel(message, UIManager.getIcon("OptionPane.errorIcon"), JLabel.LEFT);
		JLabel lblStackTrace = new JLabel(Messages.getString("dialog.exception.stacktrace"));
		
		// add the about text section with clickable links
		this.txtStackTrace = new JTextArea();
		this.txtStackTrace.setEditable(false);
		this.txtStackTrace.setTabSize(4);
		this.txtStackTrace.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		if (exception != null) {
			this.txtStackTrace.setText(ExceptionUtilities.getStackTrace(exception));
		}
		// set the cursor position to the top so that the scroller is at the top
		this.txtStackTrace.setCaretPosition(0);
		this.txtStackTrace.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
		this.txtStackTrace.addMouseListener(this);
		// wrap the text pane in a scroll pane just in case
		JScrollPane scroller = new JScrollPane(this.txtStackTrace);
		
		JButton btnOk = new JButton(Messages.getString("button.close"));
		btnOk.setActionCommand("close");
		btnOk.addActionListener(this);
		
		boolean errorReportingEnabled = Preferences.getInstance().getErrorReportingPreferences().isEnabled();
		
		JButton btnSendErrorReport = null;
		if (errorReportingEnabled) {
			btnSendErrorReport = new JButton(Messages.getString("dialog.exception.sendErrorReport"));
		} else {
			btnSendErrorReport = new JButton(Messages.getString("dialog.exception.saveErrorReport"));
		}
		btnSendErrorReport.setActionCommand("send");
		btnSendErrorReport.addActionListener(this);
		btnSendErrorReport.setEnabled(sendEnabled);
		
		Container container = this.getContentPane();
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(lblMessage)
				.addComponent(lblStackTrace)
				.addComponent(scroller)
				.addGroup(layout.createSequentialGroup()
						.addComponent(btnOk)
						.addComponent(btnSendErrorReport)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(lblMessage)
				.addComponent(lblStackTrace)
				.addComponent(scroller)
				.addGroup(layout.createParallelGroup()
						.addComponent(btnOk)
						.addComponent(btnSendErrorReport)));
		
		this.pack();
		
		// right click copy menu
		this.copyMenu = new JPopupMenu();
		
		JMenuItem mnuCopy = new JMenuItem(Messages.getString("menu.context.copy"));
		mnuCopy.setActionCommand("copy");
		mnuCopy.addActionListener(this);
		
		JMenuItem mnuSelectAll = new JMenuItem(Messages.getString("menu.context.selectAll"));
		mnuSelectAll.setActionCommand("selectall");
		mnuSelectAll.addActionListener(this);
		
		this.copyMenu.add(mnuCopy);
		this.copyMenu.add(mnuSelectAll);
	}

	/**
	 * Shows the popup menu wherever the user clicked if the user clicked
	 * the popup trigger mouse key.
	 * @param event the mouse event
	 */
	private void showPopup(MouseEvent event) {
		if (event.isPopupTrigger()) {
			this.copyMenu.show(this.txtStackTrace, event.getX(), event.getY());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if ("close".equals(event.getActionCommand())) {
			this.setVisible(false);
		} else if ("copy".equals(event.getActionCommand())) {
			// copy the selected text into the clipboard
			this.txtStackTrace.copy();
		} else if ("selectall".equals(event.getActionCommand())) {
			// select all
			this.txtStackTrace.selectAll();
		} else if ("send".equals(event.getActionCommand())) {
			// show a contact/description input dialog
			SendErrorReportDialog.show(this, this.message, this.exception);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		this.showPopup(e);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		this.showPopup(e);
	}
	
	// mouse events not used
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {}
	
	/**
	 * Shows a new exception dialog.
	 * @param owner the dialog owner
	 * @param title the dialog title
	 * @param message the message
	 * @param e the exception
	 */
	public static final void show(Component owner, String title, String message, Exception e) {
		ExceptionDialog.show(WindowUtilities.getParentWindow(owner), title, message, e);
	}
	
	/**
	 * Shows a new exception dialog.
	 * @param owner the dialog owner
	 * @param title the dialog title
	 * @param message the message
	 * @param e the exception
	 * @param sendEnabled true if the send error report button should be enabled
	 */
	public static final void show(Component owner, String title, String message, Exception e, boolean sendEnabled) {
		ExceptionDialog.show(WindowUtilities.getParentWindow(owner), title, message, e, sendEnabled);
	}
	
	/**
	 * Shows a new exception dialog.
	 * @param owner the dialog owner
	 * @param title the dialog title
	 * @param message the message
	 * @param e the exception
	 */
	public static final void show(Window owner, String title, String message, Exception e) {
		ExceptionDialog.show(owner, title, message, e, true);
	}
	
	/**
	 * Shows a new exception dialog.
	 * @param owner the dialog owner
	 * @param title the dialog title
	 * @param message the message
	 * @param e the exception
	 * @param sendEnabled true if the send error report button should be enabled
	 */
	public static final void show(Window owner, String title, String message, Exception e, boolean sendEnabled) {
		// create the dialog
		ExceptionDialog dialog = new ExceptionDialog(owner, title, message, e, sendEnabled);
		dialog.setLocationRelativeTo(owner);
		// show the dialog
		dialog.setVisible(true);
		dialog.dispose();
	}
}
