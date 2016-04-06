/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.zip.ZipException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.utility.ClasspathLoader;
import org.praisenter.utility.Zip;

/**
 * This class is effectively a connection factory for an embedded derby database.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Database {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The name of the database file */
	private static final String CLASSPATH_DATABASE_FILE_NAME = "blank.zip";
	
	/** The path to a blank version (as in no data, the schema should exist however) of the database */
	private static final String BLANK_DATABASE_CLASSPATH = "/org/praisenter/data/resources/";
	
	/** The path to house the database files */
	private final Path path;
	
	/** The datasource connection */
	private DataSource dataSource;
	
	/**
	 * Opens the database at the given path.
	 * @param path the path
	 * @return {@link Database}
	 * @throws ZipException if installing the blank database failed
	 * @throws IOException if an IO error occurs
	 * @throws SQLException if a connection could not be made
	 */
	public static final Database open(Path path) throws ZipException, IOException, SQLException { 
		Database db = new Database(path);
		db.initialize();
		return db;
	}
	
	/**
	 * Minimal constructor.
	 * @param path the path to the database.
	 */
	private Database(Path path) {
		this.path = path;
	}
	
	/**
	 * Initializes the database.
	 * @throws ZipException if installing the blank database failed
	 * @throws IOException if an IO error occurs
	 * @throws SQLException if a connection could not be made
	 */
	private void initialize() throws IOException, ZipException, SQLException {
		// make sure the driver is registered
		if (!isDriverRegistered()) {
			// register the driver
			DriverManager.registerDriver(new EmbeddedDriver());
		}
		
		// verify the database exists
		if (Files.exists(this.path)) {
			LOGGER.debug("The database path exists.");
			// make sure the path is a directory
			if (Files.isDirectory(this.path)) {
				LOGGER.debug("The database path is a directory.");
			} else {
				LOGGER.error("The given path {} is not a directory.", this.path.toAbsolutePath().toString());
				throw new NotDirectoryException(this.path.toAbsolutePath().toString());
			}
		} else {
			LOGGER.debug("Database does not exist. Installing blank database.");
			// if it doesn't exist then install the blank database
			installBlankDatabase(this.path);
		}

		// create a datasource for this path
		DataSource dataSource = createDataSource(this.path);
		
		try {
			// attempt to get a connection
			Connection connection = dataSource.getConnection();
			connection.isValid(5000);
		} catch (Exception ex) {
			LOGGER.error("Failed to connect to database.", ex);
			throw new ConnectException(ex);
		}
		
        this.dataSource = dataSource;
	}
	
	/**
	 * Returns true if the database driver has been registered.
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
	 * Installs the blank database in the given path.
	 * @param path the path
	 * @throws ZipException if installing the blank database failed
	 * @throws IOException if an IO error occurs
	 * @throws FileNotFoundException if the blank database is not found
	 */
	public static final void installBlankDatabase(Path path) throws FileNotFoundException, ZipException, IOException {
		// make sure the path exists
		Files.createDirectories(path);
		
		// copy the blank database zip from the classpath
		Path target = path.resolve(CLASSPATH_DATABASE_FILE_NAME);
		ClasspathLoader.copy(BLANK_DATABASE_CLASSPATH + CLASSPATH_DATABASE_FILE_NAME, target);
		
		// unzip the blank database zip into the full path
		Zip.unzip(target, path);
		
		// delete the zip to clean up
		try {
			Files.delete(target);
		} catch (IOException ex) {
			LOGGER.warn("Failed to delete the database zip: {}.", target.toAbsolutePath().toString());
		}
		
		LOGGER.debug("Blank database installed successfully.");
	}
	
	/**
	 * Creates a data source for the given path.
	 * @param path the path
	 * @return DataSource
	 */
	private static final DataSource createDataSource(Path path) {
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory("jdbc:derby:" + path, null);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<PoolableConnection>(poolableConnectionFactory);
        // set the pool
        poolableConnectionFactory.setPool(connectionPool);
        return new PoolingDataSource<PoolableConnection>(connectionPool);
	}
	
	/**
	 * Returns a connection to the database.
	 * @return Connection
	 * @throws SQLException if a database error occurs
	 */
	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}
}
