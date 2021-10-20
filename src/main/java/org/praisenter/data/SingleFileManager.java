package org.praisenter.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.json.JsonIO;

public final class SingleFileManager<T> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final Path path;
	private final T data;
	
	private SingleFileManager(Path path, T data) {
		this.path = path;
		this.data = data;
	}
	
	public static <T> SingleFileManager<T> open(
			Path path, 
			Class<T> clazz,
			T defaultValue) throws IOException {
		// setup the configuration
		LOGGER.info("Loading workspaces file...");
		T data = null;
		if (Files.exists(path)) {
			// read the file
			try {
				data = JsonIO.read(path, clazz);
			} catch (Exception ex) {
				LOGGER.error("Failed to read the file '" + path.toAbsolutePath() + "' as '" + clazz + "' due to: " + ex.getMessage(), ex);
				throw ex;
			}
		} else {
			// generate the file
			data = defaultValue;
			JsonIO.write(path, data);
		}
		
		return new SingleFileManager<T>(path, data);
	}
	
	public T getData() {
		return this.data;
	}
	
	public CompletableFuture<Void> saveData() {
		return CompletableFuture.runAsync(() -> {
			try {
				JsonIO.write(this.path, this.data);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}
}
