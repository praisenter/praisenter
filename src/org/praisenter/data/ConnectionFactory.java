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
package org.praisenter.data;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.praisenter.Constants;

/**
 * Simple connection factory for obtaining connections to the various
 * data storage locations.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public class ConnectionFactory {
	/** Hidden default constructor */
	private ConnectionFactory() {}
	
	/**
	 * Returns true if the Derby driver has been registered.
	 * @return boolean
	 */
	private static final boolean isDriverRegistered() {
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (driver instanceof EmbeddedDriver) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns a connection for the given url.
	 * @param url the url
	 * @return Connection
	 * @throws SQLException if an exception occurs when getting a connection
	 */
	public static final Connection getConnection(String url) throws SQLException {
		// if not, then make sure the driver is registered
		if (!isDriverRegistered()) {
			// register the driver
			DriverManager.registerDriver(new EmbeddedDriver());
		}
		
		// create the connection
		return DriverManager.getConnection(url);
	}
	
	/**
	 * Returns a connection to the bible data store.
	 * @return Connection
	 * @throws SQLException if an exception occurs when getting a connection
	 */
	public static final Connection getBibleConnection() throws SQLException {
		return getConnection("jdbc:derby:" + Constants.DATABASE_FILE_PATH);
	}
	
	/**
	 * Returns a connection to the songs data store.
	 * @return Connection
	 * @throws SQLException if an exception occurs when getting a connection
	 */
	public static final Connection getSongsConnection() throws SQLException {
		return getConnection("jdbc:derby:" + Constants.DATABASE_FILE_PATH);
	}

	/**
	 * Returns a connection to the errors data store.
	 * @return Connection
	 * @throws SQLException if an exception occurs when getting a connection
	 */
	public static final Connection getErrorsConnection() throws SQLException {
		return getConnection("jdbc:derby:" + Constants.DATABASE_FILE_PATH);
	}
}
