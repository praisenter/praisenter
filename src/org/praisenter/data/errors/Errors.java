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
package org.praisenter.data.errors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.praisenter.data.ConnectionFactory;
import org.praisenter.data.DataException;

/**
 * Class used for error reporting.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public final class Errors {
	/** The insert message sql */
	private static final String INSERT_MESSAGE_SQL = "INSERT INTO errors (java_version,java_vendor,os,architecture,message,stacktrace,contact,description,added_date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	/** Hidden default constructor */
	private Errors() {}
	
	/**
	 * Returns the list of stored error messages.
	 * @return List&lt;{@link ErrorMessage}&gt;
	 * @throws DataException thrown if an error occurs while getting the saved error messages
	 */
	public static final List<ErrorMessage> getErrorMessages() throws DataException {
		String sql = "SELECT id, java_version, java_vendor, os, architecture, message, stacktrace, contact, description, added_date FROM errors ORDER BY added_date";
		try (Connection connection = ConnectionFactory.getInstance().getConnection();
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
		} catch (Exception e) {
			throw new DataException(e);
		}
	}
	
	/**
	 * Returns the number of saved error messages.
	 * @return int 
	 * @throws DataException thrown if an error occurs while getting the number of saved error messages
	 */
	public static final int getErrorMessageCount() throws DataException {
		String sql = "SELECT COUNT(*) FROM errors";
		try (Connection connection = ConnectionFactory.getInstance().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql)) {
			
			if (result.next()) {
				return result.getInt(1);
			}
			
			return 0;
		} catch (Exception e) {
			throw new DataException(e);
		}
	}
	
	/**
	 * Saves the error message to the data store.
	 * @param smtpPassword the SMTP password
	 * @param message the message text
	 * @param exception the exception
	 * @param contact the contact
	 * @param description the contact's description of the problem
	 * @throws DataException thrown if an error occurs while saving the error message
	 */
	public static final void saveErrorMessage(String smtpPassword, String message, Exception exception, String contact, String description) throws DataException {
		// create the error message
		ErrorMessage em = new ErrorMessage(message, exception, contact, description);
		// attempt to save it to the database
		saveErrorMessage(em);
	}
	
	/**
	 * Saves the given error message to the data store.
	 * @param message the error message to save
	 * @throws DataException if any error occurs while saving
	 */
	public static final void saveErrorMessage(ErrorMessage message) throws DataException {
		try (Connection connection = ConnectionFactory.getInstance().getConnection();
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
		} catch (Exception e) {
			throw new DataException(e);
		}
	}
	
	/**
	 * Clears all the stored error messages.
	 * @throws DataException thrown if an error occurs while clearing the saved messages
	 */
	public static final void clearErrorMessages() throws DataException {
		try (Connection connection = ConnectionFactory.getInstance().getConnection();
			 Statement statement = connection.createStatement();) {
				
			statement.execute("DELETE FROM errors");
				
		} catch (Exception e) {
			throw new DataException(e);
		}
	}
}
