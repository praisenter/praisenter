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
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.utility.ClasspathLoader;
import org.praisenter.utility.Zip;

public final class ConnectionFactory {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final String CLASSPATH_DATABASE_FILE_NAME = "praisenter-blank-db.zip";
	
	private final Path path;
	
	public static final ConnectionFactory open(Path path) throws FileNotFoundException, ZipException, IOException { 
		ConnectionFactory factory = new ConnectionFactory(path);
		factory.initialize();
		return factory;
	}
	
	private ConnectionFactory(Path path) {
		this.path = path;
	}
	
	private void initialize() throws IOException, FileNotFoundException, ZipException {
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

	private static final Connection getConnectionByUrl(String url) throws SQLException {
		// if not, then make sure the driver is registered
		if (!isDriverRegistered()) {
			// register the driver
			DriverManager.registerDriver(new EmbeddedDriver());
		}
		
		// create the connection
		return DriverManager.getConnection(url);
	}
	
	private static final synchronized Connection getConnection(String path) throws SQLException {
		return getConnectionByUrl("jdbc:derby:" + path);
	}


	public Connection getConnection() throws SQLException {
		return getConnection(this.path.toAbsolutePath().toString());
	}
}
