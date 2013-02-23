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
package org.praisenter.application.preferences.ui;

import java.awt.Graphics;
import java.text.NumberFormat;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.praisenter.application.preferences.ErrorReportingPreferences;
import org.praisenter.application.preferences.Preferences;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.ui.WaterMark;
import org.praisenter.common.utilities.ComponentUtilities;

/**
 * Panel used to setup {@link ErrorReportingPreferences}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ErrorReportingSettingsPanel extends JPanel implements PreferencesEditor, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = -4554732355783276924L;
	
	// controls

	/** The check box for enabling reporting */
	private JCheckBox chkReportingEnabled;
	
	/** The check box for enabling SMTP authentication */
	private JCheckBox chkAuthenticateEnabled;
	
	/** The text box for the SMTP host */
	private JTextField txtSmtpHost;
	
	/** The text box for the SMTP port */
	private JFormattedTextField txtSmtpPort;
	
	/** The check box for enabling Start TLS */
	private JCheckBox chkStartTlsEnabled;
	
	/** The text box for the SMTP username */
	private JTextField txtUsername;
	
	/** The text box for the user's email address */
	private JTextField txtEmail;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("serial")
	public ErrorReportingSettingsPanel() {
		Preferences preferences = Preferences.getInstance();
		ErrorReportingPreferences erp = preferences.getErrorReportingPreferences();
		
		boolean enabled = erp.isEnabled();
		
		JLabel lblReportingEnabled = new JLabel(Messages.getString("panel.error.preferences.reporting.enabled"));
		this.chkReportingEnabled = new JCheckBox();
		this.chkReportingEnabled.setToolTipText(Messages.getString("panel.error.preferences.reporting.enabled.tooltip"));
		this.chkReportingEnabled.setSelected(enabled);
		this.chkReportingEnabled.addChangeListener(this);
		JLabel lblReporting = new JLabel(Messages.getString("panel.error.preferences.reporting.description"));
		
		JLabel lblAuthenticateEnabled = new JLabel(Messages.getString("panel.error.preferences.reporting.smtp.auth"));
		this.chkAuthenticateEnabled = new JCheckBox();
		this.chkAuthenticateEnabled.setToolTipText(Messages.getString("panel.error.preferences.reporting.smtp.auth.tooltip"));
		this.chkAuthenticateEnabled.setSelected(erp.isAuthenticationEnabled());
		this.chkAuthenticateEnabled.setEnabled(enabled);
		
		JLabel lblSmtpHost = new JLabel(Messages.getString("panel.error.preferences.reporting.smtp.host"));
		this.txtSmtpHost = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.error.preferences.reporting.smtp.host.example"));
			}
		};
		this.txtSmtpHost.setToolTipText(Messages.getString("panel.error.preferences.reporting.smtp.host.tooltip"));
		this.txtSmtpHost.setText(erp.getSmtpHost());
		this.txtSmtpHost.setColumns(25);
		this.txtSmtpHost.setEnabled(enabled);
		
		JLabel lblSmtpPort = new JLabel(Messages.getString("panel.error.preferences.reporting.smtp.port"));
		this.txtSmtpPort = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtSmtpPort.setToolTipText(Messages.getString("panel.error.preferences.reporting.smtp.port.tooltip"));
		this.txtSmtpPort.setValue(erp.getSmtpPort());
		this.txtSmtpPort.setColumns(5);
		this.txtSmtpPort.setEnabled(enabled);
		
		JLabel lblStartTlsEnabled = new JLabel(Messages.getString("panel.error.preferences.reporting.smtp.tls"));
		this.chkStartTlsEnabled = new JCheckBox();
		this.chkStartTlsEnabled.setToolTipText(Messages.getString("panel.error.preferences.reporting.smtp.tls.tooltip"));
		this.chkStartTlsEnabled.setSelected(erp.isStartTlsEnabled());
		this.chkStartTlsEnabled.setEnabled(enabled);
		
		JLabel lblUsername = new JLabel(Messages.getString("panel.error.preferences.reporting.smtp.user"));
		this.txtUsername = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.error.preferences.reporting.smtp.user.example"));
			}
		};
		this.txtUsername.setToolTipText(Messages.getString("panel.error.preferences.reporting.smtp.user.tooltip"));
		this.txtUsername.setText(erp.getAccountUsername());
		this.txtUsername.setColumns(20);
		this.txtUsername.setEnabled(enabled);
		JLabel lblPassword = new JLabel(Messages.getString("panel.error.preferences.reporting.smtp.pass"));
		
		JLabel lblEmail = new JLabel(Messages.getString("panel.error.preferences.reporting.smtp.email"));
		this.txtEmail = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.error.preferences.reporting.smtp.email.example"));
			}
		};
		this.txtEmail.setToolTipText(Messages.getString("panel.error.preferences.reporting.smtp.email.tooltip"));
		this.txtEmail.setText(erp.getAccountEmail());
		this.txtEmail.setColumns(30);
		this.txtEmail.setEnabled(enabled);
		
		JPanel pnlGeneral = new JPanel();
		GroupLayout layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblReportingEnabled))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkReportingEnabled)
						.addComponent(lblReporting)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblReportingEnabled)
						.addComponent(this.chkReportingEnabled))
				.addComponent(lblReporting));
		
		JPanel pnlSmtp = new JPanel();
		layout = new GroupLayout(pnlSmtp);
		pnlSmtp.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblAuthenticateEnabled)
						.addComponent(lblSmtpHost)
						.addComponent(lblSmtpPort)
						.addComponent(lblStartTlsEnabled)
						.addComponent(lblUsername)
						.addComponent(lblEmail))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkAuthenticateEnabled)
						.addComponent(this.txtSmtpHost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtSmtpPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkStartTlsEnabled)
						.addComponent(this.txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPassword)
						.addComponent(this.txtEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblAuthenticateEnabled)
						.addComponent(this.chkAuthenticateEnabled))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSmtpHost)
						.addComponent(this.txtSmtpHost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSmtpPort)
						.addComponent(this.txtSmtpPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblStartTlsEnabled)
						.addComponent(this.chkStartTlsEnabled))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblUsername)
						.addComponent(this.txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(lblPassword)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblEmail)
						.addComponent(this.txtEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		ComponentUtilities.setMinimumSize(
				lblReportingEnabled,
				lblAuthenticateEnabled,
				lblEmail,
				lblSmtpHost,
				lblSmtpPort,
				lblStartTlsEnabled,
				lblUsername);
		
		JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(sep)
				.addComponent(pnlSmtp));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(sep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(pnlSmtp));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.preferences.ui.PreferencesEditor#applyPreferences()
	 */
	@Override
	public void applyPreferences() {
		Preferences preferences = Preferences.getInstance();
		ErrorReportingPreferences erPreferences = preferences.getErrorReportingPreferences();
		
		erPreferences.setAccountEmail(this.txtEmail.getText());
		erPreferences.setAccountUsername(this.txtUsername.getText());
		erPreferences.setEnabled(this.chkReportingEnabled.isSelected());
		erPreferences.setAuthenticationEnabled(this.chkAuthenticateEnabled.isSelected());
		erPreferences.setSmtpHost(this.txtSmtpHost.getText());
		erPreferences.setSmtpPort(((Number)this.txtSmtpPort.getValue()).intValue());
		erPreferences.setStartTlsEnabled(this.chkStartTlsEnabled.isSelected());
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == this.chkReportingEnabled) {
			// toggle the fields
			if (this.chkReportingEnabled.isSelected()) {
				this.chkAuthenticateEnabled.setEnabled(true);
				this.txtSmtpHost.setEnabled(true);
				this.txtSmtpPort.setEnabled(true);
				this.chkStartTlsEnabled.setEnabled(true);
				this.txtUsername.setEnabled(true);
				this.txtEmail.setEnabled(true);
			} else {
				this.chkAuthenticateEnabled.setEnabled(false);
				this.txtSmtpHost.setEnabled(false);
				this.txtSmtpPort.setEnabled(false);
				this.chkStartTlsEnabled.setEnabled(false);
				this.txtUsername.setEnabled(false);
				this.txtEmail.setEnabled(false);
			}
		}
	}
}
