package org.praisenter.data;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Primarily here to solve the issue where a user wants to delete a file from the application (ex. video)
 * but something in the application is still holding on to it (ex. Java FX media player).
 * @author William Bittle
 * 
 */
public final class DeleteFilesShutdownHook {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Set<Path> PATHS = new HashSet<>();
	private static boolean inProgress = false;
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(DeleteFilesShutdownHook::run));
	}
	
	private DeleteFilesShutdownHook() {}
	
	private static void run() {
		LOGGER.info("Delete Files Shutdown Hook is now running...");
		Set<Path> paths = lock();
		LOGGER.info("Found " + paths.size() + " files to delete.");
		for (Path path : paths) {
			try {
				LOGGER.info("Attempting to delete file '" + path.toAbsolutePath().toString() + "' at shutdown.");
				Files.deleteIfExists(path);
			} catch (Exception ex) {
				LOGGER.warn("Failed to delete file '" + path.toAbsolutePath().toString() + "' during shutdown due to: " + ex.getMessage(), ex);
			}
		}
	}
	
	private static synchronized HashSet<Path> lock() {
		inProgress = true;
		return new HashSet<>(PATHS);
	}
	
	public static synchronized void deleteOnShutdown(Path path) {
		if (inProgress) {
			throw new IllegalStateException("The delete files shutdown hook has already started.");
		}
		PATHS.add(path);
	}
}
