package org.praisenter.preferences;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class used to store error reporting preferences.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "ErrorReportingPreferences")
@XmlAccessorType(XmlAccessType.NONE)
public class ErrorReportingPreferences {
	/** The email address to report errors to */
	public static final String DEFAULT_REPORT_TO_EMAIL = "errors@praisenter.org";
	
	/** True if error reporting is enabled */
	@XmlElement(name = "Enabled", required = true, nillable = false)
	protected boolean enabled;
	
	/** The smtp port */
	@XmlElement(name = "SmtpPort", required = true, nillable = false)
	protected int smtpPort;
	
	/** The smtp host */
	@XmlElement(name = "SmtpHost", required = true, nillable = false)
	protected String smtpHost;
	
	/** True if authentication is enabled */
	@XmlElement(name = "AuthenticationEnabled", required = true, nillable = false)
	protected boolean authenticationEnabled;
	
	/** True if start TLS is enabled */
	@XmlElement(name = "StartTlsEnabled", required = true, nillable = false)
	protected boolean startTlsEnabled;
	
	/** The account email address */
	@XmlElement(name = "AccountEmail", required = true, nillable = false)
	protected String accountEmail;
	
	/** The account user name */
	@XmlElement(name = "AccountUsername", required = true, nillable = false)
	protected String accountUsername;
	
	/** The report to account email address */
	@XmlElement(name = "ReportToEmail", required = true, nillable = false)
	protected String reportToEmail;
	
	/** Default constructor. */
	protected ErrorReportingPreferences() {
		this.enabled = false;
		this.smtpPort = 587;
		this.smtpHost = "";
		this.authenticationEnabled = true;
		this.startTlsEnabled = true;
		this.accountEmail = "";
		this.accountUsername = "";
		this.reportToEmail = DEFAULT_REPORT_TO_EMAIL;
	}

	/**
	 * Returns true if error reporting is enabled.
	 * @return boolean
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Toggles error reporting.
	 * @param enabled true if error reporting should be enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Returns the SMTP port the sending SMTP server.
	 * @return int
	 */
	public int getSmtpPort() {
		return this.smtpPort;
	}

	/**
	 * Sets the SMTP port of the sending SMTP server.
	 * @param smtpPort the port number
	 */
	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	/**
	 * Returns the SMTP host of the sending SMTP server.
	 * @return String
	 */
	public String getSmtpHost() {
		return this.smtpHost;
	}

	/**
	 * Sets the SMTP host of the sending SMTP server.
	 * @param smtpHost the host name
	 */
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	/**
	 * Returns true if SMTP authentication is being used.
	 * @return boolean
	 */
	public boolean isAuthenticationEnabled() {
		return this.authenticationEnabled;
	}

	/**
	 * Toggles SMTP authentication.
	 * <p>
	 * This should rarely be false.
	 * @param authenticationEnabled true if authentication should be used
	 */
	public void setAuthenticationEnabled(boolean authenticationEnabled) {
		this.authenticationEnabled = authenticationEnabled;
	}

	/**
	 * Returns true if StartTLS is enabled.
	 * @return boolean
	 */
	public boolean isStartTlsEnabled() {
		return this.startTlsEnabled;
	}

	/**
	 * Toggles StartTLS.
	 * <p>
	 * This is required for some SMTP hosts.
	 * @param startTlsEnabled true if StartTLS should be enabled
	 */
	public void setStartTlsEnabled(boolean startTlsEnabled) {
		this.startTlsEnabled = startTlsEnabled;
	}

	/**
	 * Returns the sending SMTP account email address.
	 * @return String
	 */
	public String getAccountEmail() {
		return this.accountEmail;
	}

	/**
	 * Sets the sending SMTP account email address.
	 * @param accountEmail the sending SMTP account email address
	 */
	public void setAccountEmail(String accountEmail) {
		this.accountEmail = accountEmail;
	}

	/**
	 * Returns the sending SMTP account username.
	 * @return String
	 */
	public String getAccountUsername() {
		return this.accountUsername;
	}

	/**
	 * Sets the sending SMTP account username.
	 * @param accountUsername the sending SMTP account username
	 */
	public void setAccountUsername(String accountUsername) {
		this.accountUsername = accountUsername;
	}

	/**
	 * Returns the receiving SMTP account email address.
	 * @return String
	 */
	public String getReportToEmail() {
		return this.reportToEmail;
	}

	/**
	 * Sets the receiving SMTP account email address.
	 * @param reportToEmail the receiving email address
	 */
	public void setReportToEmail(String reportToEmail) {
		this.reportToEmail = reportToEmail;
	}
}
