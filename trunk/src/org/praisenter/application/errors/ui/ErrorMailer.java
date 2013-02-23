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
package org.praisenter.application.errors.ui;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.praisenter.application.preferences.ErrorReportingPreferences;
import org.praisenter.application.preferences.Preferences;
import org.praisenter.data.errors.ErrorMessage;

/**
 * Helper class for reporting error messages.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class ErrorMailer {
	/** Hidden default constructor. */
	private ErrorMailer() {}
	
	/**
	 * Emails the given error messages.
	 * @param pass the smtp password
	 * @param messages the error messages
	 * @throws MessagingException if an error occurs in the mail api
	 */
	public static final void send(String pass, ErrorMessage... messages) throws MessagingException {
		// get the preferences
		ErrorReportingPreferences preferences = Preferences.getInstance().getErrorReportingPreferences();
		// setup all the properties
		String host = preferences.getSmtpHost();
		int port = preferences.getSmtpPort();
	    boolean auth = preferences.isAuthenticationEnabled();
	    String user = preferences.getAccountUsername();
	    boolean tls = preferences.isStartTlsEnabled();
	    String fromEmail = preferences.getAccountEmail();
	    String toEmail = preferences.getReportToEmail();
	    
	    Properties props = System.getProperties();
	    
	    if (tls) props.put("mail.smtp.starttls.enable", tls);
	    if (auth) props.put("mail.smtp.auth", auth);
	    
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.port", port);
	    
	    Authenticator authenticator = null;
	    if (auth) {
	    	authenticator = new SmtpAuthenticator(user, pass);
	    }
	    
	    // get a mail session
	    Session session = Session.getDefaultInstance(props, authenticator);
	    
	    MimeMessage message = new MimeMessage(session);
	    
	    // set the from
	    InternetAddress from = new InternetAddress(fromEmail);
		message.setFrom(from);

	    // set the to
		InternetAddress to = new InternetAddress(toEmail);
	    message.addRecipient(RecipientType.TO, to);
	    
	    // if we've made it this far, then lets combine the messages
	    
	    // set the subject
	    message.setSubject("Praisenter Error Report");
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append("<h2 style='border-bottom: 1px solid #000000;'>Praisenter Error Report</h2>")
	      .append("<p>Below is the listing of errors reported by a user.</p>");
	    for (ErrorMessage error : messages) {
	    	// being error message
	    	sb.append("<h3 style='border-bottom: 1px solid #000000;'>").append(error.getMessage()).append("</h3>")
	    	  .append("<table style='border-collapse: collapse; border: 1px solid #000000;'>")
		    	  .append("<tr><td style='background-color: #00FF66; border: 1px solid #000000; padding: 5px;'>Contact</td><td style='border: 1px solid #000000; padding: 5px;'>").append(error.getContact()).append("</td></tr>")
		    	  .append("<tr><td style='background-color: #00FF66; border: 1px solid #000000; padding: 5px;'>Java Version</td><td style='border: 1px solid #000000; padding: 5px;'>").append(error.getJavaVersion()).append("</td></tr>")
		    	  .append("<tr><td style='background-color: #00FF66; border: 1px solid #000000; padding: 5px;'>Java Vendor</td><td style='border: 1px solid #000000; padding: 5px;'>").append(error.getJavaVendor()).append("</td></tr>")
		    	  .append("<tr><td style='background-color: #00FF66; border: 1px solid #000000; padding: 5px;'>Operating System</td><td style='border: 1px solid #000000; padding: 5px;'>").append(error.getOs()).append("</td></tr>")
		    	  .append("<tr><td style='background-color: #00FF66; border: 1px solid #000000; padding: 5px;'>Architecture</td><td style='border: 1px solid #000000; padding: 5px;'>").append(error.getArchitecture()).append("</td></tr>")
		    	  .append("<tr><td style='background-color: #00FF66; border: 1px solid #000000; padding: 5px;'>Timestamp</td><td style='border: 1px solid #000000; padding: 5px;'>").append(error.getTimestamp()).append("</td></tr>")
	    	  .append("</table>")
	    	  .append("<p>").append(error.getDescription()).append("<p>")
	    	  .append("<p>").append(error.getStacktrace().replaceAll("(\\r\\n)|(\\r)|(\\n)", "<br />")).append("<p>");
	    }
	    
	    // set the content
	    message.setContent(sb.toString(), "text/html");
	    
	    // send the email
	    Transport.send(message);
	}
	
	/**
	 * Custom authenticator for STMP settings.
	 * @author William Bittle
	 * @version 2.0.0
	 * @since 2.0.0
	 */
	private static class SmtpAuthenticator extends Authenticator {
		/** The username */
		private String username;
		
		/** The password */
		private String password;
		
		/**
		 * Full constructor.
		 * @param username the username
		 * @param password the password
		 */
		public SmtpAuthenticator(String username, String password) {
			this.username = username;
			this.password = password;
		}
		
		/* (non-Javadoc)
		 * @see javax.mail.Authenticator#getPasswordAuthentication()
		 */
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(this.username, this.password);
		}
	}
}
