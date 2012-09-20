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
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConnectionFactory {
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
		return getConnection("jdbc:derby:" + Constants.DATABASE_FILE_LOCATION + "/" + Constants.DATABASE_FILE_NAME);
	}
	
	/**
	 * Returns a connection to the songs data store.
	 * @return Connection
	 * @throws SQLException if an exception occurs when getting a connection
	 */
	public static final Connection getSongsConnection() throws SQLException {
		return getConnection("jdbc:derby:" + Constants.DATABASE_FILE_LOCATION + "/" + Constants.DATABASE_FILE_NAME);
	}

	/**
	 * Returns a connection to the errors data store.
	 * @return Connection
	 * @throws SQLException if an exception occurs when getting a connection
	 */
	public static final Connection getErrorsConnection() throws SQLException {
		return getConnection("jdbc:derby:" + Constants.DATABASE_FILE_LOCATION + "/" + Constants.DATABASE_FILE_NAME);
	}
}
