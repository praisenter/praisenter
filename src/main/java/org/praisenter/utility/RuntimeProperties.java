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
package org.praisenter.utility;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.List;

import org.praisenter.Constants;

/**
 * Class containing properties/configuration that is set at runtime.
 * @author William Bittle
 * @version 3.0.0
 */
public final class RuntimeProperties {
	/** The default file system */
	private static final FileSystem FILE_SYSTEM = FileSystems.getDefault();
	
	/** 
	 * The new line separator for the system
	 * <p>
	 * NOTE: Callers should instead use {@link Constants#NEW_LINE} when creating
	 * new content for the best portability between systems.
	 */
	public static final String NEW_LINE_SEPARATOR = getNewLineSeparator();
	
	/** The system path separator */
	public static final String PATH_SEPARATOR = FILE_SYSTEM.getSeparator();
	
	/** The java runtime version */
	public static final String JAVA_VERSION = getJavaVersion();
	
	/** The java vendor */
	public static final String JAVA_VENDOR = getJavaVendor();
	
	/** The operating system */
	public static final String OPERATING_SYSTEM = getOperatingSystem();
	
	/** True if the operating system is Windows */
	public static final boolean IS_WINDOWS_OS = isWindows();
	
	/** True if the operating system is Mac OS */
	public static final boolean IS_MAC_OS = isMac();
	
	/** True if the operating system is a Linux system */
	public static final boolean IS_LINUX_OS = isLinux();
	
	/** The system architecture */
	public static final String ARCHITECTURE = getArchitecture();
	
	/** True if the JVM is a 64 bit JVM */
	public static final boolean IS_64 = is64Bit();
	
	/** True if the JVM is a 32 bit JVM */
	public static final boolean IS_32 = is32Bit();
	
	/** 
	 * The path to the user's home directory
	 * <p>
	 * For example:<br>
	 * C:\Users\name\<br>
	 * /user/home/dir
	 */
	public static final String USER_HOME = getUserHomeDirectory();
	
	/** The path to where the JVM was started from */
	public static final String JAVA_HOME = getJavaHomeDirectory();
	
	/** Any JVM arguments */
	public static final String JVM_ARGUMENTS = getJvmArguments();
	
	/** Hidden default constructor */
	private RuntimeProperties() {}
	
	/**
	 * Returns the system new line separator.
	 * <p>
	 * Returns '\n' if an exception occurs.
	 * @return String
	 */
	private static final String getNewLineSeparator() {
		try {
			return System.getProperty("line.separator");
		} catch (SecurityException ex) {
			return "\n";
		}
	}
	
	/**
	 * Returns the current runtime version.
	 * <p>
	 * Returns null if an exception occurs.
	 * @return String
	 */
	private static final String getJavaVersion() {
		try{
			return System.getProperty("java.version");
		} catch (SecurityException e) {
			return null;
		}
	}
	
	/**
	 * Returns the current runtime vendor.
	 * <p>
	 * Returns null if an exception occurs.
	 * @return String
	 */
	private static final String getJavaVendor() {
		try{
			return System.getProperty("java.vendor");
		} catch (SecurityException e) {
			return null;
		}
	}
	
	/**
	 * Returns the current operating system name.
	 * <p>
	 * Returns null if an exception occurs.
	 * @return String
	 */
	private static final String getOperatingSystem() {
		try{
			return System.getProperty("os.name") + " " + System.getProperty("os.version");
		} catch (SecurityException e) {
			return null;
		}
	}
	
	/**
	 * Returns true if the operating system is Windows.
	 * @return boolean
	 */
	private static final boolean isWindows() {
		try{
			String os = System.getProperty("os.name");
			if (os != null) {
				return os.toLowerCase().startsWith("windows");
			}
		} catch (SecurityException e) {}
		return false;
	}
	
	/**
	 * Returns true if the operating system is Mac Os.
	 * @return boolean
	 */
	private static final boolean isMac() {
		try{
			String os = System.getProperty("os.name");
			if (os != null) {
				return os.toLowerCase().indexOf("mac") >= 0;
			}
		} catch (SecurityException e) {}
		return false;
	}
	
	/**
	 * Returns true if the operating system is Linux.
	 * @return boolean
	 */
	private static final boolean isLinux() {
		try{
			String os = System.getProperty("os.name");
			if (os != null) {
				return os.toLowerCase().indexOf("linux") >= 0;
			}
		} catch (SecurityException e) {}
		return false;
	}
	
	/**
	 * Returns the current architecture.
	 * <p>
	 * Returns null if an exception occurs.
	 * @return String
	 */
	private static final String getArchitecture() {
		try{
			return System.getProperty("os.arch");
		} catch (SecurityException e) {
			return "";
		}
	}
	
	/**
	 * Returns true if the JVM is a 64 bit JVM.
	 * @return boolean
	 */
	private static final boolean is64Bit() {
		try{
			String arch = System.getProperty("os.arch");
			if (arch.contains("x86_64") ||
				arch.contains("x64") ||
				arch.contains("amd64")) {
				return true;
			}
		} catch (SecurityException e) {
		}
		return false;
	}
	
	/**
	 * Returns true if the JVM is a 32 bit JVM.
	 * @return boolean
	 */
	private static final boolean is32Bit() {
		try{
			String arch = System.getProperty("os.arch");
			if (arch.contains("x86") ||
				arch.matches("i[3-6]86")) {
				return true;
			}
		} catch (SecurityException e) {
		}
		return false;
	}
	
	/**
	 * Returns the user's home directory.
	 * <p>
	 * Returns <b>blank</b> if an exception occurs (rather than null like the other methods).
	 * @return String
	 */
	private static final String getUserHomeDirectory() {
		try{
			return System.getProperty("user.home");
		} catch (SecurityException e) {
			return "";
		}
	}
	
	/**
	 * Returns the directory of the executing java process.
	 * <p>
	 * Returns null if an exception occurs.
	 * @return String
	 */
	private static final String getJavaHomeDirectory() {
		try{
			return System.getProperty("java.home");
		} catch (SecurityException e) {
			return "";
		}
	}
	
	/**
	 * Returns a string of all the JVM arguments used to start this VM.
	 * @return String
	 */
	private static final String getJvmArguments() {
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeBean.getInputArguments();
		StringBuilder sb = new StringBuilder();
		for (String argument : arguments) {
			sb.append(argument).append(" ");
		}
		return sb.toString();
	}
}