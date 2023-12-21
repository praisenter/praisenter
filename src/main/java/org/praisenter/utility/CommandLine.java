package org.praisenter.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;

public final class CommandLine {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private CommandLine() {}
	
	/**
	 * Executes the given command, waits for it to complete, and returns the console output.
	 * @param command the command
	 * @return String
	 * @throws IOException if an IO error occurs
	 * @throws InterruptedException if the waiting is interrupted
	 * @throws CommandLineExecutionException if the tool returned an exit code of something other than zero
	 */
	public static final String execute(List<String> command) throws IOException, InterruptedException, CommandLineExecutionException {
		// remove empty list elements because of 
		// https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8268939
		command.removeIf(s -> s == null || s.isBlank() || s.isEmpty());
		
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
    			throw new CommandLineExecutionException(exitCode, message);
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
