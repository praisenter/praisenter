package org.praisenter.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.praisenter.control.BottomButtonPanel;
import org.praisenter.control.WaterMark;
import org.praisenter.resources.Messages;

/**
 * Dialog used to prompt the user for their SMTP password.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class EnterPasswordDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -4256639777511020517L;

	/** The contact textbox */
	private JPasswordField txtPassword;
	
	/**
	 * Minimal constructor.
	 * @param owner the dialog owner
	 */
	@SuppressWarnings("serial")
	private EnterPasswordDialog(Window owner) {
		super(owner, Messages.getString("dialog.pass.title"), ModalityType.APPLICATION_MODAL);
		
		JLabel lblDescription = new JLabel(Messages.getString("dialog.pass.description"));
		
		this.txtPassword = new JPasswordField(30) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("dialog.pass.input"));
			}
		};
		
		JButton btnOk = new JButton(Messages.getString("dialog.pass.button.ok"));
		btnOk.setActionCommand("ok");
		btnOk.addActionListener(this);
		
		JButton btnCancel = new JButton(Messages.getString("dialog.pass.button.cancel"));
		btnCancel.setActionCommand("cancel");
		btnCancel.addActionListener(this);
		
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(lblDescription)
				.addComponent(this.txtPassword));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(lblDescription)
				.addComponent(this.txtPassword));
		
		JPanel pnlBottom = new BottomButtonPanel();
		pnlBottom.setLayout(new FlowLayout());
		pnlBottom.add(btnOk);
		pnlBottom.add(btnCancel);
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(panel, BorderLayout.CENTER);
		container.add(pnlBottom, BorderLayout.PAGE_END);
		
		this.pack();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}
	
	/**
	 * Shows a dialog prompting the user for their password.
	 * @param owner the dialog owner
	 * @return String
	 */
	public static final String show(Window owner) {
		// show the dialog
		EnterPasswordDialog dialog = new EnterPasswordDialog(owner);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		char[] pass = dialog.txtPassword.getPassword();
		if (pass == null || pass.length == 0) {
			return null;
		}
		
		return String.valueOf(pass);
	}
}
