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
import java.util.Properties;

/**
 * Helper class for starting the main app without the need for java
 * to be on the PATH or equivalent environment variable.
 * @author William Bittle
 * @version 2.0.0
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
		String args = null;
		
		// attempt to use the Launch.properties file first
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(new File("Launcher.properties")));
			
			// set the configured properties
			java = properties.getProperty("java");
			args = properties.getProperty("args");
		} catch (Exception ex) {
			// ignore the error and continue
		}
		
		// check for null, empty, or an invalid path
		File file = new File(java);
		if (java == null || java.length() == 0 || !file.exists()) {
			String home = System.getProperty("java.home");
			String slash = System.getProperty("file.separator");
			
			// then attempt to build the path that launched this code
			java = home + slash + "bin" + slash + "java";
		}
		
		if (args == null || args.length() == 0) {
			args = "-Xms512m";
		}
		
		try {
			// if we are on Mac OS we need to set another jvm argument to set the
			// title bar name, otherwise its the name of the main class
			String os = System.getProperty("os.name");
			if (os != null) {
				if (os.toLowerCase().indexOf("mac") >= 0) {
					// add the dock name parameter
					args += " -Xdock:name=Praisenter";
				}
			}
		} catch (Exception e) {
			// ignore the exception and continue
		}
		
		try {
			// create the command line
			String command = java + " " + args + " -jar Praisenter.jar";
			
			// launch the real jar
			Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			System.err.println("Error executing command: ");
			e.printStackTrace();
		}
	}
}
