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
	 * @param args command line arguments
	 */
	public static final void main(String[] args) {
		String home = null;
		String pathSeparator = null;
		
		try {
			// get the java home property
			home = System.getProperty("java.home");
			System.out.println("Java Home:" + home);
			
			// use the system's path separator			
			pathSeparator = System.getProperty("file.separator");
			System.out.println("Path Separator:" + home);
		} catch (Exception e) {
			System.err.println("Failed to get java.home or file.separtor properties.");
			e.printStackTrace();
			System.exit(1);
		}
		
		// set defaults
		String javadir = "bin";
		String java = "java";
		String jvmargs = "-Xms512m";
		String jar = "Praisenter.jar";
		
		// get the Launch.properties file
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(new File("Launcher.properties")));
			
			// set the configured properties
			javadir = properties.getProperty("javadir");
			java = properties.getProperty("java");
			jvmargs = properties.getProperty("jvmargs");
			jar = properties.getProperty("jar");
		} catch (Exception ex) {
			System.out.println("Launcher.properties file not found. Using default parameters.");
		}
		
		try {
			// if we are on Mac OS we need to set another jvm argument to set the
			// title bar name, otherwise its the name of the main class
			String os = System.getProperty("os.name");
			if (os != null) {
				if (os.toLowerCase().indexOf("mac") >= 0) {
					// add the dock name parameter
					jvmargs += " -Xdock:name=Praisenter";
				}
			}
		} catch (Exception e) {
			System.err.println("Failed to get property: os.name");
			e.printStackTrace();
		}
		
		try {
			// create the command line
			String command = home + pathSeparator + javadir + pathSeparator + java + " " + jvmargs + " -jar " + jar;
			System.out.println("Command:" + command);
			
			// launch the real jar
			Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			System.err.println("Error executing command: ");
			e.printStackTrace();
		}
	}
}
