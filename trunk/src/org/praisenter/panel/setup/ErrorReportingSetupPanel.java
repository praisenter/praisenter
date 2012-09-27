package org.praisenter.panel.setup;

import java.beans.PropertyChangeEvent;
import java.text.NumberFormat;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.praisenter.settings.ErrorReportingSettings;
import org.praisenter.settings.SettingsException;

public class ErrorReportingSetupPanel extends JPanel implements SetupPanel {
	private ErrorReportingSettings settings;
	
	// controls
	
	private JCheckBox chkReportingEnabled;
	private JCheckBox chkAuthenticateEnabled;
	private JTextField txtSmtpHost;
	private JFormattedTextField txtSmtpPort;
	private JCheckBox chkStartTlsEnabled;
	private JTextField txtUsername;
	private JTextField txtEmail;
	
	public ErrorReportingSetupPanel(ErrorReportingSettings settings) {
		this.settings = settings;
		
		JLabel lblReportingEnabled = new JLabel();
		this.chkReportingEnabled = new JCheckBox();
		this.chkReportingEnabled.setSelected(settings.isErrorReportingEnabled());
		JLabel lblReporting = new JLabel();
		
		JLabel lblAuthenticateEnabled = new JLabel();
		this.chkAuthenticateEnabled = new JCheckBox();
		this.chkAuthenticateEnabled.setSelected(settings.isSmtpAuthenticateEnabled());
		
		JLabel lblSmtpHost = new JLabel();
		this.txtSmtpHost = new JTextField();
		this.txtSmtpHost.setText(settings.getSmtpHost());
		
		JLabel lblSmtpPort = new JLabel();
		this.txtSmtpPort = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtSmtpPort.setValue(settings.getSmtpPort());
		
		JLabel lblStartTlsEnabled = new JLabel();
		this.chkStartTlsEnabled = new JCheckBox();
		this.chkStartTlsEnabled.setSelected(settings.isSmtpStartTlsEnabled());
		
		JLabel lblUsername = new JLabel();
		this.txtUsername = new JTextField();
		this.txtUsername.setText(settings.getAccountUsername());
		
		JLabel lblEmail = new JLabel();
		this.txtEmail = new JTextField();
		this.txtEmail.setText(settings.getAccountEmail());
		
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {}
	
	/* (non-Javadoc)
	 * @see org.praisenter.panel.setup.SetupPanel#saveSettings()
	 */
	@Override
	public void saveSettings() throws SettingsException {
		this.settings.setAccountEmail(this.txtEmail.getText());
		this.settings.setAccountUsername(this.txtUsername.getText());
		this.settings.setErrorReportingEnabled(this.chkReportingEnabled.isSelected());
		this.settings.setSmtpAuthenticateEnabled(this.chkAuthenticateEnabled.isSelected());
		this.settings.setSmtpHost(this.txtSmtpHost.getText());
		this.settings.setSmtpPort(((Number)this.txtSmtpPort.getValue()).intValue());
		this.settings.setSmtpStartTlsEnabled(this.chkStartTlsEnabled.isSelected());
		
		this.settings.save();
	}
}
