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

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.praisenter.preferences.Preferences;
import org.praisenter.utilities.LookAndFeelUtilities;

// TODO allow play/stop/pause/seek controls for playable media

/**
 * This class is the application entry point.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public final class Main {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(Main.class);
	
	/** True if the -debug command line argument was passed in */
	private static boolean DEBUG = false;
	
	/** Hidden default constructor. */
	private Main() {}
	
	/**
	 * Returns true if the -debug argument was passed to startup.
	 * @return boolean
	 */
	public static final boolean isDebugEnabled() {
		return DEBUG;
	}
	
	/**
	 * Entry point into the app.
	 * <p>
	 * Command line arguments:
	 * <ul>
	 * <li>-debug</li>
	 * </ul>
	 * @param args the command line arguments
	 */
	public static final void main(String[] args) {
		// interpret command line
		if (args != null) {
			for (String arg : args) {
				if ("-debug".equals(arg)) {
					DEBUG = true;
				}
			}
		}
		
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
		System.setProperty("derby.stream.error.file", Constants.DATABASE_LOG_FILE_PATH);
		
		// initialize folder structure
		initializeFolderStructure();
		
		// initialize log4j
		initializeLog4j();
		
		// initialize default look and feel
		initializeDefaultLookAndFeel();
		
		// initialize preferences
		Preferences.getInstance();
	}
	
	/**
	 * Initializes the folder structure for the application.
	 * <p>
	 * This really only needs to happen on the first startup of the
	 * application, however, if the folders were deleted for any reason
	 * we need to make sure they exist on each start up.
	 */
	private static final void initializeFolderStructure() {
		// verify the existence of the /config directory
		initializeFolder(Constants.CONFIGURATION_FILE_LOCATION);
		
		// verify the log file folder
		initializeFolder(Constants.LOG_FILE_LOCATION);
		
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
		LOGGER.info("Configuring Log4j.");
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
					DOMConfigurator.configure(Main.class.getResource("/" + log4j));
					LOGGER.warn("Log4j configuration file not found at [" + file.getAbsolutePath() + "]. Using classpath configuration file.");
					try {
						LOGGER.info("Copying classpath " + log4j + " to " + file.getAbsolutePath() + " directory.");
						// attempt to make a copy and place it in the root directory
						if (file.createNewFile()) {
							// copy the contents of the file
							try {
								BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
								BufferedInputStream bis = new BufferedInputStream(Main.class.getResourceAsStream("/" + log4j));
								int r = 0;
								byte[] buffer = new byte[1000];
								while ((r = bis.read(buffer, 0, buffer.length)) > 0) {
									bos.write(buffer, 0, r);
								}
								bos.flush();
								bos.close();
								bis.close();
								LOGGER.info(log4j + " file copied successfully.");
							} catch (FileNotFoundException ex) {
								// just log the error if we can't copy the file							
								LOGGER.warn("File not found: [" + file.getAbsolutePath() + "].", ex);
							}
						} else {
							// just log the error if we can't copy the file							
							LOGGER.warn("Could not create file: [" + file.getAbsolutePath() + "].");
						}
					} catch (IOException ex) {
						// just log the error if we can't copy or write to the file
						LOGGER.warn("An IO error occurred while creating or writing the log file to the root directory.", ex);
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
}
