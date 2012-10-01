package org.praisenter.data.errors.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;
import org.praisenter.data.errors.Errors;
import org.praisenter.icons.Icons;
import org.praisenter.resources.Messages;
import org.praisenter.settings.ErrorReportingSettings;
import org.praisenter.ui.BottomButtonPanel;

/**
 * Dialog used to obtain more information from the user
 * before sending an error report.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SendErrorReportDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 3484940802631864217L;
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SendErrorReportDialog.class);
	
	/** The contact textbox */
	private JTextField txtContact;
	
	/** The problem description text box */
	private JTextArea txtDescription;
	
	/** True if the error should be sent */
	private boolean send;
	
	/**
	 * Minimal constructor.
	 * @param owner the dialog owner
	 */
	private SendErrorReportDialog(Window owner) {
		super(owner, Messages.getString("dialog.error.report"), ModalityType.APPLICATION_MODAL);
		this.send = false;
		
		JLabel lblWarning = new JLabel(Messages.getString("dialog.error.report.warning"));
		lblWarning.setIcon(Icons.INFORMATION);
		lblWarning.setVerticalTextPosition(SwingConstants.TOP);
		lblWarning.setPreferredSize(new Dimension(300, 300));
		
		JLabel lblContact = new JLabel(Messages.getString("dialog.error.report.contact"));
		lblContact.setToolTipText(Messages.getString("dialog.error.report.contact.tooltip"));
		
		JLabel lblDescription = new JLabel(Messages.getString("dialog.error.report.description"));
		lblDescription.setToolTipText(Messages.getString("dialog.error.report.description.tooltip"));
		
		this.txtContact = new JTextField();
		this.txtContact.setColumns(20);
		
		this.txtDescription = new JTextArea();
		this.txtDescription.setLineWrap(true);
		this.txtDescription.setRows(8);
		
		JButton btnSend = new JButton(Messages.getString("dialog.error.report.send"));
		btnSend.setActionCommand("send");
		btnSend.addActionListener(this);
		
		JButton btnCancel = new JButton(Messages.getString("dialog.error.report.cancel"));
		btnCancel.setActionCommand("cancel");
		btnCancel.addActionListener(this);
		
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(lblWarning, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(lblContact, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtContact, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(lblDescription, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtDescription));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(lblWarning)
				.addComponent(lblContact)
				.addComponent(this.txtContact, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(lblDescription)
				.addComponent(this.txtDescription));
		
		JPanel pnlBottom = new BottomButtonPanel();
		pnlBottom.setLayout(new FlowLayout());
		pnlBottom.add(btnSend);
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
		if ("send".equals(e.getActionCommand())) {
			this.send = true;
		}
		this.setVisible(false);
	}
	
	/**
	 * Shows a dialog prompting the user for their information before sending the
	 * error report.
	 * @param owner the dialog owner
	 * @param message the message
	 * @param exception the exception
	 */
	public static final void show(Window owner, String message, Exception exception) {
		// show the dialog
		SendErrorReportDialog dialog = new SendErrorReportDialog(owner);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		if (dialog.send) {
			String password = null;
			if (ErrorReportingSettings.getInstance().isErrorReportingEnabled()) {
				// prompt the user for a password
				password = EnterPasswordDialog.show(null);
			}
			// start a new thread to send the email/add to the datastore
			Thread thread = new Thread(new SendErrorReportTask(
					password,
					message, 
					exception, 
					dialog.txtContact.getText(), 
					dialog.txtDescription.getText()), "SendErrorReportThread");
			thread.setDaemon(true);
			thread.setName("SendErrorReportTask");
			thread.start();
			
			// notify the user that it was sent/saved
			JOptionPane.showMessageDialog(
					dialog, 
					Messages.getString("dialog.error.report.success.text"),
					Messages.getString("dialog.error.report.success.title"),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * Task used to send a error reports.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private static class SendErrorReportTask implements Runnable {
		/** The smtp password */
		private String password;
		
		/** The message */
		private String message;
		
		/** The exception */
		private Exception exception;
		
		/** The contact person */
		private String contact;
		
		/** The description */
		private String description;
		
		/**
		 * Full constructor.
		 * @param password the SMTP password
		 * @param message the message
		 * @param exception the exception
		 * @param contact the contact person
		 * @param description the description of the problem
		 */
		private SendErrorReportTask(String password, String message, Exception exception, String contact, String description) {
			this.password = password;
			this.message = message;
			this.exception = exception;
			this.contact = contact;
			this.description = description;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			LOGGER.debug("Sending error message: " + this.message);
			boolean sent = Errors.sendErrorMessage(this.password, this.message, this.exception, this.contact, this.description);
			if (sent) {
				LOGGER.info("Error message sent.");
			} else {
				LOGGER.info("Error message saved to unsent messages.");
			}
		}
	}
}
