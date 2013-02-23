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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.apache.log4j.Logger;
import org.praisenter.common.CreateFileException;
import org.praisenter.common.InitializationException;
import org.praisenter.common.NotInitializedException;
import org.praisenter.common.utilities.FileUtilities;
import org.praisenter.common.utilities.ZipUtilities;

/**
 * Simple connection factory for obtaining connections to the various
 * data storage locations.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public final class ConnectionFactory {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class);
	
	/** The class path database file name */
	private static final String CLASSPATH_DATABASE_FILE_NAME = "praisenter-blank-db.zip";
	
	// members
	
	/** The full path to the current connection factory */
	private String fullPath;
	
	
	// static interface
	
	/** The current instance */
	private static ConnectionFactory instance;
	
	/**
	 * Initializes a connection to the given data store.
	 * <p>
	 * This method will attempt to verify the existence of the given path. If it doesn't exist
	 * it will attempt to install the default database at the given path.
	 * @param fullPath the full path to the data store
	 * @throws NullPointerException thrown if fullPath is null
	 * @throws InitializationException thrown if the {@link ConnectionFactory} fails to initialize
	 */
	public static final synchronized void initialize(String fullPath) throws InitializationException {
		// make sure its not null
		if (fullPath == null) {
			throw new InitializationException(new NullPointerException());
		}
		// verify the database is present or create it
		try {
			initializeDatabase(fullPath);
		} catch (Exception e) {
			throw new InitializationException(e);
		}
		// create a new connection factory
		ConnectionFactory factory = new ConnectionFactory(fullPath);
		// set the instance
		instance = factory;
	}
	
	/**
	 * Returns the current instance of the connection factory.
	 * @return {@link ConnectionFactory}
	 * @throws NotInitializedException thrown if {@link #initialize(String)} has not be called
	 */
	public static final synchronized ConnectionFactory getInstance() throws NotInitializedException {
		if (instance == null) {
			throw new NotInitializedException();
		}
		return instance;
	}

	/**
	 * Initializes the praisenter database if it does not exist.
	 * @param fullPath the full path to the database
	 * @throws ZipException thrown if the classpath zip file is not valid
	 * @throws CreateFileException thrown if the classpath zip file could not be created on the file system
	 * @throws FileNotFoundException thrown if the classpath zip or the copied zip could not be found
	 * @throws IllegalStateException thrown if the zip is closed while unzipping
	 * @throws SecurityException thrown if there isn't sufficient access to the file system
	 * @throws IOException thrown if an IO error occurs
	 */
	private static final void initializeDatabase(String fullPath) throws ZipException, CreateFileException, FileNotFoundException, IllegalStateException, SecurityException, IOException {
		LOGGER.debug("Verifying existence of database.");
		
		File file = new File(fullPath);
		if (file.isDirectory()) {
			LOGGER.debug("The database folder exists.");
		} else {
			LOGGER.debug("Database does not exist. Installing blank database.");
			
			// make sure the path exists
			if (!file.exists()) {
				// make the directories if they don't exist
				file.mkdirs();
			}
			
			// copy the blank database zip from the classpath
			FileUtilities.copyFileFromClasspath("/org/praisenter/data/resources/", CLASSPATH_DATABASE_FILE_NAME, fullPath);
			
			// unzip the blank database zip into the full path
			String zipPath = fullPath + FileUtilities.getSeparator() + CLASSPATH_DATABASE_FILE_NAME;
			ZipUtilities.unzip(zipPath, fullPath);
			
			// delete the zip to clean up
			File zip = new File(zipPath);
			if (!zip.delete()) {
				LOGGER.warn("Failed to delete the database zip: [" + zipPath + "].");
			}
			LOGGER.debug("Blank database installed successfully.");
		}
	}
	
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
	private static final Connection getConnectionByUrl(String url) throws SQLException {
		// if not, then make sure the driver is registered
		if (!isDriverRegistered()) {
			// register the driver
			DriverManager.registerDriver(new EmbeddedDriver());
		}
		
		// create the connection
		return DriverManager.getConnection(url);
	}
	
	/**
	 * Returns a connection for the given path.
	 * @param path the path to the database on the file system
	 * @return Connection
	 * @throws SQLException if an exception occurs when getting a connection
	 */
	private static final synchronized Connection getConnection(String path) throws SQLException {
		return getConnectionByUrl("jdbc:derby:" + path);
	}
	
	// members
	
	/**
	 * Full constructor.
	 * @param fullPath the full path to the database
	 */
	private ConnectionFactory(String fullPath) {
		this.fullPath = fullPath;
	}
	
	/**
	 * Returns a connection to the data store.
	 * @return Connection
	 * @throws SQLException if an exception occurs when getting a connection
	 */
	public Connection getConnection() throws SQLException {
		return getConnection(this.fullPath);
	}
	
}
