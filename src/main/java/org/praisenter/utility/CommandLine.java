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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.tools.ToolExecutionException;

/**
 * Helper class for dealing with command line tools.
 * @author William Bittle
 * @version 3.0.0
 */
public final class CommandLine {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** Hidden constructor */
	private CommandLine() {}
	
	/**
	 * Executes the given command, waits for it to complete, and returns the console output.
	 * @param command the command
	 * @return String
	 * @throws IOException if an IO error occurs
	 * @throws InterruptedException if the waiting is interrupted
	 * @throws ToolExecutionException if the tool returned an exit code of something other than zero
	 */
	public static final String execute(List<String> command) throws IOException, InterruptedException, ToolExecutionException {
		// run the command
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		Process process = null;
		
		try {
			LOGGER.info("Starting process with command: " + String.join(" ", command));
			process = pb.start();
			LOGGER.info("Waiting for process to complete...");
			
			// we must read the input streams otherwise they fill up
			// and the sub process will hang
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        String line = null;
	        StringBuilder s = new StringBuilder();
	        // readLine is a blocking call so this will pause us until the
	        // executable completes or encounters an error
	        while((line = input.readLine()) != null) {
	            s.append(line).append(Constants.NEW_LINE);
	        }
	        
        	// just in case i guess
        	int exitCode = process.waitFor();
        	LOGGER.info("Process completed with exitcode = " + exitCode);
        	
        	// attempt to close the input stream
        	try {
				input.close();
			} catch (IOException e) {
				LOGGER.warn("Failed to close process input stream.");
			}
        	
        	// check the exit code
        	if (exitCode != 0) {
        		String message = s.toString();
        		LOGGER.error(message);
    			throw new ToolExecutionException(message);
    		}
        	
        	return s.toString();
		} finally {
			// always try to clean up the process
			if (process != null) {
				process.destroy();
			}
		}
	}
}
