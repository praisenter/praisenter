package org.praisenter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.praisenter.settings.BibleSettings;
import org.praisenter.settings.ErrorReportingSettings;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.settings.NotificationSettings;
import org.praisenter.settings.SongSettings;
import org.praisenter.utilities.LookAndFeelUtilities;

// FIXME add license to all files
// FIXME the about dialog needs some work

/**
 * This class is the application entry point.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Main {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(Main.class);
	
	/** True if the -debug command line argument was passed in */
	private static boolean DEBUG = false;
	
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
	public static void main(String[] args) {
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
		// initialize folder structure
		initializeFolderStructure();
		
		// initialize log4j
		initializeLog4j();
		
		// initialize default look and feel
		initializeDefaultLookAndFeel();
		
		// initialize error reporting settings
		ErrorReportingSettings.getInstance();
		
		// initialize general settings
		GeneralSettings.getInstance();
		
		// initialize notification settings
		NotificationSettings.getInstance();
		
		// initialize bible settings
		BibleSettings.getInstance();
		
		// initialize song settings
		SongSettings.getInstance();
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
			String fileLocation = Constants.CONFIGURATION_FILE_LOCATION + "/" + Constants.LOG4J_FILE_NAME;
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
