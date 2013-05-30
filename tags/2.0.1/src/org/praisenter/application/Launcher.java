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
package org.praisenter.application;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 * Helper class for starting the main app without the need for java
 * to be on the PATH or equivalent environment variable.
 * @author William Bittle
 * @version 2.0.1
 * @since 1.0.0
 */
public final class Launcher {
	/** Hidden default constructor */
	private Launcher() {}
	
	/**
	 * Main entry point for the launcher.
	 * @param params command line arguments
	 */
	public static final void main(String[] params) {
		String java = null;
		String jvmArgs = null;
		String appArgs = null;
		
		String sep = System.getProperty("file.separator");
		
		// determine if the working directory is the application directory
		// this can happen depending on how the jar is launched
		// if the working directory is not the jar's directory it won't be
		// able to find the Laucher.properties or the Praisenter.jar files
		String currentDir = System.getProperty("user.dir");
		String execDir = currentDir;
		boolean isExecDir = true;
		
		try {
			execDir = getExecutionPath();
			isExecDir = currentDir.equals(execDir);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Unable to get execution directory, trying current directory:\n[" + currentDir + "].", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		// attempt to use the Launch.properties file first
		String launcherProperties = isExecDir ? "Launcher.properties" : execDir + sep + "Launcher.properties";
		try {
			File file = new File(launcherProperties);
			Properties properties = new Properties();
			properties.load(new FileInputStream(file));
			
			// set the configured properties
			java = properties.getProperty("java");
			jvmArgs = properties.getProperty("jvmargs");
			appArgs = properties.getProperty("appargs");
		} catch (Exception ex) {
			// ignore the error and continue
			JOptionPane.showMessageDialog(null, "Unable to find [" + launcherProperties + "]. Using defaults.", "Warning", JOptionPane.WARNING_MESSAGE);
		}
		
		// check for null, empty, or an invalid path
		if (java == null || java.length() == 0 || !(new File(java)).exists()) {
			// use the java executable that launched this code
			String home = System.getProperty("java.home");
			
			// then attempt to build the path that launched this code
			java = home + sep + "bin" + sep + "java";
		}
		
		// put all the command line stuff into a string list
		List<String> command = new ArrayList<String>();
		
		// add the java executable command
		command.add(java);
		
		// add any JVM arguments from the properties file
		if (jvmArgs != null && jvmArgs.length() > 0) {
			// handle multiple arguments
			for (String arg : jvmArgs.split(",")) {
				// dont add empty arguments
				if (arg != null && !arg.isEmpty()) {
					command.add(arg);
				}
			}
		}
		
		try {
			// if we are on Mac OS we need to set another jvm argument to set the
			// title bar name, otherwise its the name of the main class
			String os = System.getProperty("os.name");
			if (os != null) {
				if (os.toLowerCase().indexOf("mac") >= 0) {
					// add the dock name parameter
					command.add("-Xdock:name=Praisenter");
					// add the dock icon parameter
					command.add("-Xdock:icon=icon.png");
				}
			}
		} catch (Exception e) {
			// ignore the exception and continue
		}
		
		try {
			// let the java executable know we are using a Jar file
			command.add("-jar");
			// tell it which jar to execute
			command.add((isExecDir ? "" : execDir + sep) + "Praisenter.jar");
			
			// add any application arguments from the properties file
			if (appArgs != null && appArgs.length() > 0) {
				// handle multiple arguments
				for (String arg : appArgs.split(",")) {
					// dont add empty arguments
					if (arg != null && !arg.isEmpty()) {
						command.add(arg);
					}
				}
			}
			
			// create a log file for Praisenter to catch any stderr logging
			String launcherLog = (isExecDir ? "" : execDir + sep) + "Launcher.log";
			File file = new File(launcherLog);
			file.createNewFile();
			
			// launch the real jar
			ProcessBuilder builder = new ProcessBuilder(command);
			// redirect stderr of the subprocess
			builder.redirectError(file);
			// start Praisenter
			builder.start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Returns the execution path of the Launcher.jar.
	 * <p>
	 * This may be different than the current working directory.
	 * @return String
	 * @throws URISyntaxException thrown if the URL cannot be converted
	 * @since 2.0.1
	 */
	private static final String getExecutionPath() throws URISyntaxException {
		// get the path without the file:// prefix
		String path = new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
		// strip the Launcher.jar from the path
		return path.substring(0, path.lastIndexOf(System.getProperty("file.separator")));
	}
}
