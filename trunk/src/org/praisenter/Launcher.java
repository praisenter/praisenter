package org.praisenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Helper class for starting the main app without the need for java
 * to be on the PATH or equivalent environment variable.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Launcher {
	/**
	 * Main entry point for the launcher.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		// get the java home property
		String home = System.getProperty("java.home");
		System.out.println("Java Home:" + home);
		
		// use the system's path separator
		String pathSeparator = System.getProperty("file.separator");
		System.out.println("Path Separator:" + home);
		
		// set defaults
		String javadir = "bin";
		String java = "java";
		String jvmargs = "-Xms512m";
		String jar = "Praisenter.jar";
		
		// get the Launch.properties file
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(new File("Launch.properties")));
			
			// set the configured properties
			javadir = properties.getProperty("javadir");
			java = properties.getProperty("java");
			jvmargs = properties.getProperty("jvmargs");
			jar = properties.getProperty("jar");
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
		
		try {
			// create the command line
			String command = home + pathSeparator + javadir + pathSeparator + java + " " + jvmargs + " -jar " + jar;
			System.out.println("Command:" + command);
			
			// launch the real jar
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			System.out.println("Error executing command: ");
			e.printStackTrace();
		}
	}
}
