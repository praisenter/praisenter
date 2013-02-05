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
package org.praisenter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.praisenter.preferences.Preferences;
import org.praisenter.slide.Resolutions;
import org.praisenter.utilities.LookAndFeelUtilities;
import org.praisenter.utilities.SystemUtilities;
import org.praisenter.utilities.ZipUtilities;

// TODO [MEDIUM] MEDIA allow play/stop/pause/seek controls for playable media

/**
 * This class is the application entry point.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public final class Main {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(Main.class);
	
	/** The application arguments */
	private static ApplicationArguments ARGUMENTS;
	
	/** Hidden default constructor. */
	private Main() {}
	
	/**
	 * Returns true if the -debug argument was passed to startup.
	 * @return boolean
	 */
	public static final ApplicationArguments getApplicationArguments() {
		return ARGUMENTS;
	}
	
	/**
	 * Entry point into the app.
	 * <p>
	 * Command line arguments:
	 * <ul>
	 * <li>-debug</li>
	 * <li>-installEmptyDB</li>
	 * <li>-installSpecifiedDB="database zip file name.zip"</li>
	 * </ul>
	 * @param args the command line arguments
	 */
	public static final void main(String[] args) {
		// interpret command line
		ARGUMENTS = new ApplicationArguments(args);
		
		// initialize configuration files
		Main.initializeConfiguration();
		
		// load the application
		ApplicationLoader.load();
	}
	
	/**
	 * Initializes all configuration files.
	 */
	private static final void initializeConfiguration() {
		// set system properties
		// setup derby (the database) log file location
		setProperty("derby.stream.error.file", Constants.DATABASE_LOG_FILE_PATH);
		// setup mac os title bar as the menu (does nothing on other OSes)
		setProperty("apple.laf.useScreenMenuBar", "true");
		// setup mac os quit command to close all windows
		setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
		
		// set the praisenter.home property for log4j file appender paths
		setProperty("praisenter.home", Constants.BASE_PATH);
		
		try {
			// initialize folder structure
			initializeFolderStructure();
		} catch (Exception e) {
			// if anything in here fails, the app cannot continue
			System.err.println("Failed to initialize directory structure: ");
			e.printStackTrace();
			// we must quit
			System.exit(1);
		}
		
		// initialize log4j
		initializeLog4j();
		
		// initialize default look and feel
		initializeDefaultLookAndFeel();
		
		// initialize the database
		initializeDatabase();
		
		// initialize preferences
		Preferences.getInstance();
		
		// initialize resolutions
		Resolutions.getResolutions();
	}
	
	/**
	 * Sets the given property.
	 * <p>
	 * This method will catch and output any exceptions generated but will
	 * not terminate the application.
	 * @param name the property name
	 * @param value the property value
	 */
	private static final void setProperty(String name, String value) {
		try {
			System.setProperty(name, value);
		} catch (Exception e) {
			System.err.println("Failed to set property: [" + name + "] = [" + value + "]");
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the folder structure for the application.
	 * <p>
	 * This really only needs to happen on the first startup of the
	 * application, however, if the folders were deleted for any reason
	 * we need to make sure they exist on each start up.
	 */
	private static final void initializeFolderStructure() {
		// verify the base folder location
		initializeFolder(Constants.BASE_PATH);
		
		// verify the existence of the /config directory
		initializeFolder(Constants.CONFIGURATION_FILE_LOCATION);
		
		// verify the log file folder
		initializeFolder(Constants.LOG_FILE_LOCATION);
		
		// verify the database folder
		initializeFolder(Constants.DATABASE_FILE_LOCATION);
		
		// verify the existence of the /media and sub directories
		initializeFolder(Constants.MEDIA_LIBRARY_PATH);
		initializeFolder(Constants.MEDIA_LIBRARY_IMAGE_PATH);
		initializeFolder(Constants.MEDIA_LIBRARY_VIDEO_PATH);
		initializeFolder(Constants.MEDIA_LIBRARY_AUDIO_PATH);
		
		// verify the existence of the /slides path
		initializeFolder(Constants.SLIDE_PATH);
		
		// verify the existence of the /templates path and sub directories
		initializeFolder(Constants.TEMPLATE_PATH);
		initializeFolder(Constants.BIBLE_TEMPLATE_PATH);
		initializeFolder(Constants.SONGS_TEMPLATE_PATH);
		initializeFolder(Constants.NOTIFICATIONS_TEMPLATE_PATH);
	}
	
	/**
	 * Verifies the existence of the given folder and creates it if its not present.
	 * @param folder the folder
	 */
	private static final void initializeFolder(String folder) {
		File file = new File(folder);
		if (!file.exists()) {
			// create the directory if it doesn't exist
			file.mkdir();
		}
	}
	
	/**
	 * Performs the setup of log4j.
	 * <p>
	 * This method will first check for the log4j.xml file in the root directory of the
	 * application.  If this file is not found, the log4j.xml file in the classpath is used.
	 * If this file is not found then the basic configuration is used.
	 * <p>
	 * If the log4j.xml file in the root directory of the application is not found and the 
	 * classpath log4j.xml file is used, this method will attempt to copy the classpath file
	 * to the root directory.
	 * <p>
	 * Unfortunately there is no way to verify whether the log4j.xml if valid and no way to
	 * verify that the configuration using a file has worked.
	 */
	private static final void initializeLog4j() {
		try {
			String fileLocation = Constants.LOG4J_FILE_PATH;
			String log4j = Constants.LOG4J_FILE_NAME;
			// see if the config file exists
			File file = new File(fileLocation);
			if (file.exists()) {
				DOMConfigurator.configure(fileLocation);
			} else {
				// the file didn't exist so load the classpath one
				try {
					// configure using the classpath xml file
					String classpath = "/org/praisenter/resources/";
					DOMConfigurator.configure(Main.class.getResource(classpath + log4j));
					// we have to wait until log4j is configured before we can actually make any log statement
					LOGGER.warn("Log4j configuration file not found at [" + file.getAbsolutePath() + "]. Using classpath configuration file.");
					
					// copy the file
					LOGGER.info("Copying classpath [" + classpath + log4j + "] to [" + file.getAbsolutePath() + "].");
					boolean copied = copyFileFromClasspath(classpath, log4j, Constants.CONFIGURATION_FILE_LOCATION);
					if (copied) {
						LOGGER.info("Log4j configuration file copied successfully.");
					} else {
						LOGGER.warn("Failed to copy the log4j configuration file from the classpath.");
					}
				} catch (Exception ex) {
					// just use the default configuration
					// this can happen if the class path one doesn't exist or any other error occurs
					BasicConfigurator.configure();
					LOGGER.warn("An error occurred while configuring log4j using the classpath " + log4j + ". Using default configuration instead.", ex);
				}
			}
			LOGGER.info("Log4j initialized successfully.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Initializes the default look and feel.
	 * <p>
	 * This method will attempt to change the application look and feel to the
	 * Nimbus look and feel.  If its not found, then the default look and
	 * feel is used.
	 */
	private static final void initializeDefaultLookAndFeel() {
		LOGGER.info("Defaulting look and feel.");
		
		// get the default look and feel
		String defaultLookAndFeelClassName = null;
		try {
			defaultLookAndFeelClassName = System.getProperty("swing.defaultlaf");
		} catch (Exception ex) {
			LOGGER.warn("Could not obtain default look and feel class name: ", ex);
		}
		
		String defaultLookAndFeelName = defaultLookAndFeelClassName;
		try {
			// check the OS
			if (SystemUtilities.isMac()) {
				// on a mac we want to use the native look and feel
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} else {
			    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			        if (LookAndFeelUtilities.NIMBUS.equalsIgnoreCase(info.getName())) {
			        	LOGGER.info("Nimbus look and feel found and applied.");
			            UIManager.setLookAndFeel(info.getClassName());
			            // fix the nimbus disabled tooltip coloring
			        	UIManager.put("ToolTip[Disabled].backgroundPainter", UIManager.get("ToolTip[Enabled].backgroundPainter"));
			            return;
			        }
			        if (info.getClassName().equals(defaultLookAndFeelClassName)) {
			        	defaultLookAndFeelName = info.getName();
			        }
			    }
			}
		} catch (Exception ex) {
			// completely ignore the error and just use the default look and feel
			LOGGER.info("Failed to change the look and feel to Nimbus. Continuing with default look and feel.", ex);
		}
		
		if (defaultLookAndFeelName == null || defaultLookAndFeelName.isEmpty()) {
			// if the default look and feel has not been set
			LOGGER.info("Nimbus look and feel not found. Using default look and feel: Metal");
		} else {
			LOGGER.info("Nimbus look and feel not found. Using default look and feel: " + defaultLookAndFeelName);
		}
	}
	
	/**
	 * Initializes the praisenter database if it does not exist.
	 */
	private static final void initializeDatabase() {
		LOGGER.debug("Verifying existence of database.");
		
		File file = new File(Constants.DATABASE_FILE_PATH);
		if (file.isDirectory()) {
			LOGGER.debug("The database folder exists. Database connections will be verified later.");
		} else {
			LOGGER.info("Database does not exist. Creating default database.");
			
			// copy the zip files from the resources directory on the classpath
			// we do this all the time so that we make sure we always get the latest
			// non-corrupted zip file for the database
			
			// copy the empty database zip
			boolean copied = copyFileFromClasspath("/org/praisenter/resources/", "praisenter-blank-db.zip", Constants.DATABASE_FILE_LOCATION);
			if (!copied) {
				return;
			} else {
				LOGGER.info("Empty database zip file copied successfully.");
			}
			
			// copy the kjv database zip
			copied = copyFileFromClasspath("/org/praisenter/resources/", "praisenter-kjv-db.zip", Constants.DATABASE_FILE_LOCATION);
			if (!copied) {
				return;
			} else {
				LOGGER.info("KJV database zip file copied successfully.");
			}
			
			// determine what database we should unzip
			try {
				if (ARGUMENTS.isInstallEmptyDb()) {
					LOGGER.info("Installing empty database.");
					ZipUtilities.unzip(Constants.DATABASE_FILE_LOCATION + Constants.SEPARATOR + "praisenter-blank-db.zip", Constants.DATABASE_FILE_LOCATION);
				} else if (ARGUMENTS.isInstallSpecifiedDb()) {
					LOGGER.info("Installing specified database.");
					ZipUtilities.unzip(Constants.DATABASE_FILE_LOCATION + Constants.SEPARATOR + ARGUMENTS.getSpecifiedDb(), Constants.DATABASE_FILE_LOCATION);
				} else {
					LOGGER.info("Installing KJV database.");
					ZipUtilities.unzip(Constants.DATABASE_FILE_LOCATION + Constants.SEPARATOR + "praisenter-kjv-db.zip", Constants.DATABASE_FILE_LOCATION);
				}
				LOGGER.info("Database zip unzipped successfully.");
			} catch (ZipException e) {
				LOGGER.fatal("A zip exception occurred while attempting to unzip the database zip file.", e);
			} catch (FileNotFoundException e) {
				LOGGER.fatal("The database zip file was not found.", e);
			} catch (IOException e) {
				LOGGER.fatal("An IO error occurred while attempting to unzip the database zip file.", e);
			} catch (IllegalStateException e) {
				LOGGER.fatal("The database zip file was closed before completing.", e);
			} catch (SecurityException e) {
				LOGGER.fatal("You do not have permissions to unzip this file.", e);
			} catch (Exception e) {
				LOGGER.fatal("An error occurred while unzipping the database zip file.", e);
			}
		}
	}
	
	/**
	 * Copies the given file on the classpath to the given location using the same name.
	 * <p>
	 * Returns true if the copy was successful.
	 * @param classpath the path in the classpath to the file
	 * @param filename the file name of the file in the classpath
	 * @param folder the destination folder
	 * @return boolean
	 */
	private static final boolean copyFileFromClasspath(String classpath, String filename, String folder) {
		// get the classpath resource
		try (InputStream is = Main.class.getResourceAsStream(classpath + filename)) {
			// see if we found the classpath resource
			if (is == null) {
				LOGGER.warn("File [" + classpath + filename + "] was not found.");
				return false;
			}
			
			// create the file for the resource to go into
			File file = new File(folder + Constants.SEPARATOR + filename);
			
			// see if the file exists
			if (!file.exists()) {
				// attempt to create it
				if (!file.createNewFile()) {
					LOGGER.warn("Could not create file [" + file.getAbsolutePath() + "].");
					return false;
				}
			}
			
			// copy the contents of the file
			try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				 BufferedInputStream bis = new BufferedInputStream(is);)	{
				int bytesRead = 0;
				byte[] buffer = new byte[1024];
				while ((bytesRead = bis.read(buffer, 0, buffer.length)) > 0) {
					bos.write(buffer, 0, bytesRead);
				}
				bos.flush();
				LOGGER.info("File [" + filename + "] copied successfully.");
				return true;
			} catch (FileNotFoundException ex) {
				// just log the error if we can't copy the file
				LOGGER.warn("File [" + file.getAbsolutePath() + "] was not found: ", ex);
			} catch (IOException ex) {
				// if an IO error occurs then we need to quit
				LOGGER.warn("An IO error occurred while copying the file [" + filename + "]:", ex);
			}
		} catch (Exception e) {
			// just log the error
			LOGGER.warn("An error occurred while copying the file [" + filename + "]: ", e);
		}
		return false;
	}
}
