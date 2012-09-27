package org.praisenter.settings;

import java.util.Properties;

import org.praisenter.Constants;

/**
 * Error reporting settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ErrorReportingSettings extends RootSettings<ErrorReportingSettings> {
	/** The file name */
	private static final String FILE_NAME = "ErrorReportingSettings.properties";
	
	/** The settings file location and name */
	private static final String FILE_NAME_LOCATION = Constants.CONFIGURATION_FILE_LOCATION + "/" + FILE_NAME;
	
	/** The instance of the settings */
	private static final ErrorReportingSettings instance = ErrorReportingSettings.loadSettings();
	
	// settings keys
	
	/** Property key for enabling/disabling error reporting */
	private static final String KEY_REPORTING_ENABLED = "Reporting.Enabled";
	
	/** Property key for the Report to email address */
	private static final String KEY_REPORT_TO_EMAIL = "Reporting.To.Email";
	
	/** Property key for the from email address */
	private static final String KEY_ACCOUNT_EMAIL = "Account.Email";
	
	/** Property key for the from username */
	private static final String KEY_ACCOUNT_USERNAME = "Account.Username";

	/** Property key for the SMTP host name */
	private static final String KEY_SMTP_HOST = "Smtp.Host";
	
	/** Property key for the SMTP host port */
	private static final String KEY_SMTP_PORT = "Smtp.Port";
	
	/** Property key for the SMTP authenticate flag */
	private static final String KEY_SMTP_AUTHENTICATE_ENABLED = "Smtp.Authenticate.Enabled";
	
	/** Property key for the SMTP start tls flag */
	private static final String KEY_SMTP_STARTTLS_ENABLED = "Smtp.StartTLS.Enabled";
	
	/**
	 * Returns the instance of the {@link ErrorReportingSettings}.
	 * @return {@link ErrorReportingSettings}
	 */
	public static final ErrorReportingSettings getInstance() {
		return ErrorReportingSettings.instance;
	}
	
	/**
	 * Loads the {@link ErrorReportingSettings}.
	 * @return {@link ErrorReportingSettings}
	 */
	private static final ErrorReportingSettings loadSettings() {
		// create a new settings instance
		ErrorReportingSettings settings = new ErrorReportingSettings();
		// load the saved settings or default settings
		settings.load();
		// return the default settings
		return settings;
	}
	
	/**
	 * Default constructor.
	 */
	private ErrorReportingSettings() {}
	
	/**
	 * Full constructor.
	 * @param properties the initial properties
	 */
	private ErrorReportingSettings(Properties properties) {
		super(properties);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#getFileNameLocation()
	 */
	@Override
	protected String getFileNameLocation() {
		return ErrorReportingSettings.FILE_NAME_LOCATION;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#getNewInstance()
	 */
	@Override
	protected ErrorReportingSettings getNewInstance() {
		return new ErrorReportingSettings();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#getSingletonInstance()
	 */
	@Override
	protected ErrorReportingSettings getSingletonInstance() {
		return ErrorReportingSettings.getInstance();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#setDefaultSettings()
	 */
	@Override
	public void setDefaultSettings() throws SettingsException {
		this.setErrorReportingEnabled(false);
		this.setReportToEmail("errors@praisenter.org");
		this.setAccountEmail("");
		this.setAccountUsername("");
		this.setSmtpAuthenticateEnabled(true);
		this.setSmtpHost("");
		this.setSmtpPort(587);
		this.setSmtpStartTlsEnabled(true);
	}
	
	// settings methods
	
	/**
	 * Returns true if error reporting is enabled.
	 * <p>
	 * This doesn't guarantee that the reporting settings are valid.
	 * @return boolean
	 */
	public boolean isErrorReportingEnabled() {
		return getBooleanSetting(KEY_REPORTING_ENABLED);
	}
	
	/**
	 * Toggles error reporting.
	 * @param flag true if error reporting should be enabled
	 * @throws SettingsException if an exception occurs while updating the setting
	 */
	public void setErrorReportingEnabled(boolean flag) throws SettingsException {
		setSetting(KEY_REPORTING_ENABLED, flag);
	}
	
	/**
	 * Returns the report to email address.
	 * @return String
	 */
	public String getReportToEmail() {
		return getStringSetting(KEY_REPORT_TO_EMAIL);
	}
	
	/**
	 * Sets the report to email address.
	 * @param email the email address
	 * @throws SettingsException if an exception occurs while updating the setting
	 */
	public void setReportToEmail(String email) throws SettingsException {
		setSetting(KEY_REPORT_TO_EMAIL, email);
	}
	
	/**
	 * Returns the account email address (from email address).
	 * @return String
	 */
	public String getAccountEmail() {
		return getStringSetting(KEY_ACCOUNT_EMAIL);
	}
	
	/**
	 * Sets the account email address (from email address).
	 * @param email the email address
	 * @throws SettingsException if an exception occurs while updating the setting
	 */
	public void setAccountEmail(String email) throws SettingsException {
		setSetting(KEY_ACCOUNT_EMAIL, email);
	}
	
	/**
	 * Returns the username used to authenticate with the STMP server.
	 * @return String
	 */
	public String getAccountUsername() {
		return getStringSetting(KEY_ACCOUNT_USERNAME);
	}
	
	/**
	 * Sets the username used to authenticate with the STMP server.
	 * @param username the username
	 * @throws SettingsException if an exception occurs while updating the setting
	 */
	public void setAccountUsername(String username) throws SettingsException {
		setSetting(KEY_ACCOUNT_USERNAME, username);
	}
	
	/**
	 * Returns the SMTP host name.
	 * @return String
	 */
	public String getSmtpHost() {
		return getStringSetting(KEY_SMTP_HOST);
	}
	
	/**
	 * Sets the SMTP host name.
	 * @param host the host name
	 * @throws SettingsException if an exception occurs while updating the setting
	 */
	public void setSmtpHost(String host) throws SettingsException {
		setSetting(KEY_SMTP_HOST, host);
	}
	
	/**
	 * Returns the SMTP host port.
	 * @return int
	 */
	public int getSmtpPort() {
		return getIntegerSetting(KEY_SMTP_PORT);
	}
	
	/**
	 * Sets the SMTP host port.
	 * @param port the port
	 * @throws SettingsException if an exception occurs while updating the setting
	 */
	public void setSmtpPort(int port) throws SettingsException {
		setSetting(KEY_SMTP_PORT, port);
	}
	
	/**
	 * Returns true if SMTP authentication should be used.
	 * @return boolean
	 */
	public boolean isSmtpAuthenticateEnabled() {
		return getBooleanSetting(KEY_SMTP_AUTHENTICATE_ENABLED);
	}
	
	/**
	 * Toggles SMTP authentication.
	 * @param flag true if SMTP authentication should be enabled
	 * @throws SettingsException if an exception occurs while updating the setting
	 */
	public void setSmtpAuthenticateEnabled(boolean flag) throws SettingsException {
		setSetting(KEY_SMTP_AUTHENTICATE_ENABLED, flag);
	}

	/**
	 * Returns true if Start TLS should be used.
	 * @return boolean
	 */
	public boolean isSmtpStartTlsEnabled() {
		return getBooleanSetting(KEY_SMTP_STARTTLS_ENABLED);
	}
	
	/**
	 * Toggles Start TLS.
	 * @param flag true if Start TLS should be used
	 * @throws SettingsException if an exception occurs while updating the setting
	 */
	public void setSmtpStartTlsEnabled(boolean flag) throws SettingsException {
		setSetting(KEY_SMTP_STARTTLS_ENABLED, flag);
	}
}
