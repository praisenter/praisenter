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

import java.io.IOException;
import java.nio.file.Files;
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

public final class Database {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final String CLASSPATH_DATABASE_FILE_NAME = "blank.zip";
	
	private final Path path;
	private DataSource dataSource;
	
	public static final Database open(Path path) throws ZipException, IOException, SQLException { 
		Database db = new Database(path);
		db.initialize();
		return db;
	}
	
	private Database(Path path) {
		this.path = path;
	}
	
	private void initialize() throws IOException, ZipException, SQLException {
		// make sure the driver is registered
		if (!isDriverRegistered()) {
			// register the driver
			DriverManager.registerDriver(new EmbeddedDriver());
		}
		
		// verify the database exists
		if (Files.isDirectory(this.path)) {
			LOGGER.debug("The database folder exists.");
		} else {
			LOGGER.debug("Database does not exist. Installing blank database.");
			
			// make sure the path exists
			Files.createDirectories(this.path);
			
			// copy the blank database zip from the classpath
			Path target = this.path.resolve(CLASSPATH_DATABASE_FILE_NAME);
			ClasspathLoader.copy("/org/praisenter/data/resources/" + CLASSPATH_DATABASE_FILE_NAME, target);
			
			// unzip the blank database zip into the full path
			Zip.unzip(target, this.path);
			
			// delete the zip to clean up
			try {
				Files.delete(target);
			} catch (IOException ex) {
				LOGGER.warn("Failed to delete the database zip: {}.", target.toAbsolutePath().toString());
			}
			
			LOGGER.debug("Blank database installed successfully.");
		}

        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory("jdbc:derby:" + this.path, null);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<PoolableConnection>(poolableConnectionFactory);
        // set the pool
        poolableConnectionFactory.setPool(connectionPool);
        this.dataSource = new PoolingDataSource<PoolableConnection>(connectionPool);
	}
	
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

	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}
}
