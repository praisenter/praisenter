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

/**
 * Class to store the passed command line arguments to the application.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class ApplicationArguments {
	/** The -debug argument */
	private static final String ARGUMENT_DEBUG = "-debug";
	
	/** The -installEmptyDB argument */
	private static final String ARGUMENT_INSTALL_EMPTY_DB = "-installEmptyDB";
	
	/** The -installSpecifiedDB="example database file name.zip" */
	private static final String ARGUMENT_INSTALL_SPECIFIED_DB = "-installSpecifiedDB=";
	
	// data
	
	/** True if the debug argument was passed */
	private boolean debugEnabled;
	
	/** True if the install empty db was passed */
	private boolean installEmptyDb;
	
	/** True if the install specified db was passed */
	private boolean installSpecifiedDb;
	
	/** The specified db file name */
	private String specifiedDb;
	
	/**
	 * Minimal constructor.
	 * @param args the command line arguments
	 */
	public ApplicationArguments(String[] args) {
		// set defaults
		this.debugEnabled = false;
		this.installEmptyDb = false;
		this.installSpecifiedDb = false;
		this.specifiedDb = "";
		
		// get the parameters from the command line
		if (args != null) {
			for (String arg : args) {
				// skip empty or null arguments
				if (arg == null || arg.length() == 0) {
					continue;
				}
				// check for the debug argument
				if (ARGUMENT_DEBUG.equals(arg)) {
					this.debugEnabled = true;
				}
				// check for the install empty db argument
				if (ARGUMENT_INSTALL_EMPTY_DB.equals(arg)) {
					this.installEmptyDb = true;
				}
				// check for the install specified db argument
				if (arg.startsWith(ARGUMENT_INSTALL_SPECIFIED_DB)) {
					// split by the = sign
					String[] items = arg.split("=");
					// make sure there is a second piece
					if (items.length > 1 && items[1].length() > 0) {
						this.installSpecifiedDb = true;
						// get the specified db file name/path
						this.specifiedDb = items[1].replace("\"", "").replace("'", "");
					}
				}
			}
		}
	}
	
	/**
	 * Returns true if debug is enabled.
	 * @return boolean
	 */
	public boolean isDebugEnabled() {
		return this.debugEnabled;
	}
	
	/**
	 * Returns true if we should install the empty db instead of the
	 * default KJV db.
	 * @return boolean
	 */
	public boolean isInstallEmptyDb() {
		return this.installEmptyDb;
	}
	
	/**
	 * Returns true if we should install the specified db instead of the
	 * default KJV db.
	 * @return boolean
	 */
	public boolean isInstallSpecifiedDb() {
		return this.installSpecifiedDb;
	}
	
	/**
	 * Returns the specified db file name.
	 * @return String
	 */
	public String getSpecifiedDb() {
		return this.specifiedDb;
	}
}
