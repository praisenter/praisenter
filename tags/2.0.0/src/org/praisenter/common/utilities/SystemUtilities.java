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
package org.praisenter.common.utilities;

/**
 * Utility class used to get system related information.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SystemUtilities {
	/** Hidden default constructor */
	private SystemUtilities() {}
	
	/**
	 * Returns the current runtime version.
	 * <p>
	 * Returns null if an exception occurs.
	 * @return String
	 */
	public static final String getJavaVersion() {
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
	public static final String getJavaVendor() {
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
	public static final String getOperatingSystem() {
		try{
			return System.getProperty("os.name") + " " + System.getProperty("os.version");
		} catch (SecurityException e) {
			return null;
		}
	}
	
	/**
	 * Returns true if the operating system is Mac Os.
	 * @return boolean
	 */
	public static final boolean isMac() {
		try{
			String os = System.getProperty("os.name");
			if (os != null) {
				return os.toLowerCase().indexOf("mac") >= 0;
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
	public static final String getArchitecture() {
		try{
			return System.getProperty("os.arch");
		} catch (SecurityException e) {
			return null;
		}
	}
	
	/**
	 * Returns the user's home directory.
	 * <p>
	 * Returns <b>blank</b> if an exception occurs (rather than null like the other methods).
	 * @return String
	 */
	public static final String getUserHomeDirectory() {
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
	public static final String getJavaHomeDirectory() {
		try{
			return System.getProperty("java.home");
		} catch (SecurityException e) {
			return null;
		}
	}
}
