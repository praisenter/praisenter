package org.praisenter.data.errors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.praisenter.data.ConnectionFactory;
import org.praisenter.data.DataException;

/**
 * Class used for error reporting.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Errors {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(Errors.class);
	
	/** The insert message sql */
	private static final String INSERT_MESSAGE_SQL = "INSERT INTO errors (java_version,java_vendor,os,architecture,message,stacktrace,contact,description,added_date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	/** The smtp properties */
	private static final Properties SMTP_SETTINGS = loadSmtpSettings();
	
	/** True if smtp is enabled */
	private static final boolean SMTP_ENABLED = SMTP_SETTINGS != null ? Boolean.parseBoolean(SMTP_SETTINGS.getProperty("smtp.enabled")) : false;
	
	/** The authenticator for the SMTP session */
	private static final Authenticator AUTHENTICATOR = new Authenticator() {
		/* (non-Javadoc)
		 * @see javax.mail.Authenticator#getPasswordAuthentication()
		 */
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			if (SMTP_SETTINGS != null) {
				return new PasswordAuthentication(
						SMTP_SETTINGS.getProperty("smtp.username"),
						SMTP_SETTINGS.getProperty("smtp.password"));
			} else {
				LOGGER.info("SMTP settings not found. Cannot send error reports (they will be saved instead).");
			}
			return super.getPasswordAuthentication();
		}
	};
	
	/**
	 * Loads the smtp settings.
	 * @return Properties
	 */
	private static final Properties loadSmtpSettings() {
		try {
			Properties properties = new Properties();
			properties.load(new FileReader(new File("config/ErrorReportSettings.properties")));
			return properties;
		} catch (FileNotFoundException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return null;
	}
	
	/**
	 * Returns the list of stored error messages that still require sending.
	 * <p>
	 * These were likely saved when the user didn't have internet access or their smtp settings
	 * were not configured or not configured properly.
	 * @return List&lt;{@link ErrorMessage}&gt;
	 */
	public static final List<ErrorMessage> getErrorMessages() {
		String sql = "SELECT id, java_version, java_vendor, os, architecture, message, stacktrace, contact, description, added_date FROM errors ORDER BY added_date";
		try (Connection connection = ConnectionFactory.getErrorsConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql)) {
			
			List<ErrorMessage> errors = new ArrayList<ErrorMessage>();
			while (result.next()) {
				ErrorMessage error = new ErrorMessage(
						result.getInt("id"), 
						result.getString("java_version"), 
						result.getString("java_vendor"), 
						result.getString("os"), 
						result.getString("architecture"), 
						result.getString("message"), 
						result.getString("stacktrace"), 
						result.getString("contact"), 
						result.getString("description"), 
						new Date(result.getTimestamp("added_date").getTime()));
				errors.add(error);
			}
			
			return errors;
		} catch (SQLException e) {
			LOGGER.info("An error occurred while getting the stored error messages:", e);
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * Returns the number of stored error messages.
	 * @return int 
	 */
	public static final int getErrorMessageCount() {
		String sql = "SELECT COUNT(*) FROM errors";
		try (Connection connection = ConnectionFactory.getErrorsConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql)) {
			
			if (result.next()) {
				return result.getInt(1);
			}
			
			return 0;
		} catch (SQLException e) {
			LOGGER.info("An error occurred while getting the stored error message count:", e);
			return 0;
		}
	}
	
	/**
	 * Sends the given error message to the default recipient.
	 * <p>
	 * If access to the mail server is not available, the message will instead
	 * be stored in the errors datastore.
	 * <p>
	 * Returns true if the message was sent or false if the message was
	 * stored.
	 * @param message the message text
	 * @param exception the exception
	 * @param contact the contact
	 * @param description the contact's description of the problem
	 * @return boolean
	 */
	public static final boolean sendErrorMessage(String message, Exception exception, String contact, String description) {
		// create the error message
		ErrorMessage em = new ErrorMessage(message, exception, contact, description);
		// see if reporting is enabled
		if (SMTP_ENABLED) {
			// if so, attempt to email the message
			try {
				emailErrorMessages(em);
				return true;
			} catch (Exception ex) {
				// just log the error
				LOGGER.error(ex);
			}
		}
		// attempt to save it to the database
		try {
			saveErrorMessage(em);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return false;
	}
	
	/**
	 * Saves the given error message to the data store.
	 * @param message the error message to save
	 * @throws DataException if any error occurs while saving
	 */
	private static final void saveErrorMessage(ErrorMessage message) throws DataException {
		try (Connection connection = ConnectionFactory.getErrorsConnection();
			 PreparedStatement statement = connection.prepareStatement(INSERT_MESSAGE_SQL)) {
			
			statement.setString(1, message.javaVersion);
			statement.setString(2, message.javaVendor);
			statement.setString(3, message.os);
			statement.setString(4, message.architecture);
			statement.setString(5, message.message);
			statement.setString(6, message.stacktrace);
			statement.setString(7, message.contact);
			statement.setString(8, message.description);
			statement.setTimestamp(9, new Timestamp(message.timestamp.getTime()));
			
			int n = statement.executeUpdate();
			if (n <= 0) {
				throw new DataException("The error message not saved: " + message);
			}
		} catch (SQLException e) {
			throw new DataException(e);
		}
	}
	
	/**
	 * Attempts to send the stored error messages.
	 */
	public static final void sendErrorMessages() {
		try {
			// get all the stored messages
			List<ErrorMessage> messages = getErrorMessages();
			ErrorMessage[] array = messages.toArray(new ErrorMessage[0]);
			// email the messages
			emailErrorMessages(array);
			// clear the messages
			clearErrorMessages();
		} catch (Exception e) {
			// just log the error
			LOGGER.error(e);
		}
	}
	
	/**
	 * Emails the given error messages.
	 * @param messages the error messages
	 * @throws InvalidConfigurationException if the smtp configuration is invalid
	 * @throws MessagingException if an error occurs in the mail api
	 */
	private static final void emailErrorMessages(ErrorMessage... messages) throws InvalidConfigurationException, MessagingException {
		// setup all the properties
		String host = SMTP_SETTINGS.getProperty("smtp.host");
		String port = SMTP_SETTINGS.getProperty("smtp.port");
	    String auth = SMTP_SETTINGS.getProperty("smtp.authenticate");
	    String user = SMTP_SETTINGS.getProperty("smtp.username");
	    String pass = SMTP_SETTINGS.getProperty("smtp.password");
	    String tls = SMTP_SETTINGS.getProperty("smtp.starttls.enabled");
	    String fromEmail = SMTP_SETTINGS.getProperty("smtp.email");
	    String toEmail = SMTP_SETTINGS.getProperty("smtp.to");
	    
	    Properties props = System.getProperties();
	    props.put("mail.smtp.starttls.enable", tls);
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.user", user);
	    props.put("mail.smtp.password", pass);
	    props.put("mail.smtp.port", port);
	    props.put("mail.smtp.auth", auth);
	    
	    // get a mail session
	    Session session = Session.getDefaultInstance(props, AUTHENTICATOR);
	    
	    MimeMessage message = new MimeMessage(session);
	    // set the from
	    InternetAddress from = null;
	    try {
	    	from = new InternetAddress(fromEmail);
	    } catch (AddressException e) {
	    	throw new InvalidConfigurationException(e);
	    }
		message.setFrom(from);

	    // set the to
		InternetAddress to = null;
	    try {
	    	to = new InternetAddress(toEmail);
	    } catch (AddressException e) {
	    	throw new InvalidConfigurationException(e);
	    }
	    message.addRecipient(RecipientType.TO, to);
	    
	    // if we've made it this far, then lets combine the messages
	    
	    // set the subject
	    message.setSubject("Praisenter Error Report");
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append("<h2 style='border-bottom: 1px solid #000000;'>Praisenter Error Report</h2>")
	      .append("<p>Below is the listing of errors reported by a user.</p>");
	    for (ErrorMessage error : messages) {
	    	// being error message
	    	sb.append("<h3 style='border-bottom: 1px solid #000000;'>").append(error.message).append("</h3>")
	    	  .append("<table style='border-collapse: collapse; border: 1px solid #000000;'>")
		    	  .append("<tr><td style='background-color: #00FF66; border: 1px solid #000000; padding: 5px;'>Contact</td><td style='border: 1px solid #000000; padding: 5px;'>").append(error.contact).append("</td></tr>")
		    	  .append("<tr><td style='background-color: #00FF66; border: 1px solid #000000; padding: 5px;'>Java Version</td><td style='border: 1px solid #000000; padding: 5px;'>").append(error.javaVersion).append("</td></tr>")
		    	  .append("<tr><td style='background-color: #00FF66; border: 1px solid #000000; padding: 5px;'>Java Vendor</td><td style='border: 1px solid #000000; padding: 5px;'>").append(error.javaVendor).append("</td></tr>")
		    	  .append("<tr><td style='background-color: #00FF66; border: 1px solid #000000; padding: 5px;'>Operating System</td><td style='border: 1px solid #000000; padding: 5px;'>").append(error.os).append("</td></tr>")
		    	  .append("<tr><td style='background-color: #00FF66; border: 1px solid #000000; padding: 5px;'>Architecture</td><td style='border: 1px solid #000000; padding: 5px;'>").append(error.architecture).append("</td></tr>")
		    	  .append("<tr><td style='background-color: #00FF66; border: 1px solid #000000; padding: 5px;'>Timestamp</td><td style='border: 1px solid #000000; padding: 5px;'>").append(error.timestamp).append("</td></tr>")
	    	  .append("</table>")
	    	  .append("<p>").append(error.description).append("<p>")
	    	  .append("<p>").append(error.stacktrace.replaceAll("\\n\\r?", "<br />")).append("<p>");
	    }
	    
	    // set the content
	    message.setContent(sb.toString(), "text/html");
	    
	    // send the email
	    Transport transport = session.getTransport("smtp");
	    transport.connect(host, user, pass);
	    transport.sendMessage(message, message.getAllRecipients());
	    try {
	    	// close the transport and just eat the exceptions
	    	transport.close();
	    } catch (MessagingException e) {}
	}
	
	/**
	 * Exports the saved {@link ErrorMessage}s to a string (to be saved in a file).
	 * @return String
	 */
	public static final String exportErrorMessages() {
		StringBuilder sb = new StringBuilder();
		
		try {
			// get all the stored messages
			List<ErrorMessage> messages = getErrorMessages();
			for (ErrorMessage message : messages) {
				sb.append(message.id).append(" ")
				  .append(message.timestamp).append(" ")
				  .append(message.message).append("\n")
				  // details
				  .append(message.javaVersion).append(" [")
				  .append(message.javaVendor).append("] ")
				  .append(message.os).append(" ")
				  .append(message.architecture).append("\n")
				  // user info
				  .append(message.contact).append(" ")
				  .append(message.description).append("\n")
				  // stacktrace
				  .append(message.stacktrace).append("\n")
				  .append("\n")
				  .append("\n");
			}
			
			clearErrorMessages();
		} catch (Exception e) {
			// just log the error
			LOGGER.error(e);
		}
		
		return sb.toString();
	}
	
	/**
	 * Clears all the stored error messages.
	 */
	private static final void clearErrorMessages() {
		try (Connection connection = ConnectionFactory.getErrorsConnection();
			 Statement statement = connection.createStatement();) {
				
			statement.execute("DELETE FROM errors");
				
		} catch (SQLException e) {
			LOGGER.error(e);
		}
	}
}
