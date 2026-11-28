package org.praisenter.utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public final class FileHelper {
    private static final Logger LOGGER = LogManager.getLogger();

	public static final String FALLBACK_MOVE = "FM";
	public static final String FALLBACK_COPY = "FC";

	/**
	 * Unzips the given file to the specified folder.
	 * @param zip
	 * @param folder
	 * @throws IOException
	 */
	public static void unzip(Path zip, Path folder) throws IOException {
		unzip(zip, folder, null);
	}

	/**
	 * Unzips the given file to the specified folder.
	 * <p>
	 * If a mapper is supplied, it will be used to determine the extract path
	 * for each entry.  Returning null from the mapper will skip extracting that
	 * entry.
	 * <p>
	 * The mapper sends the ZipArchiveEntry and the default extraction path for 
	 * that entry.
	 * @param zip
	 * @param folder
	 * @param mapper
	 * @throws IOException
	 */
	public static void unzip(Path zip, Path folder, BiFunction<ZipArchiveEntry, Path, Path> mapper) throws IOException {
		// JAVABUG (L) 11/01/23 [workaround] Native java.util.zip package can't support zips 4GB or bigger or elements 2GB or bigger
		Path extractionRoot = folder.toAbsolutePath().normalize();
        try (ZipFile zipFile = ZipFile.builder().setPath(zip).get()) {
        	Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        	while (entries.hasMoreElements()) {
        		ZipArchiveEntry entry = entries.nextElement();
        		
        		if (entry.isDirectory()) 
        			continue;
        		
        		if (zipFile.canReadEntryData(entry)) {
					Path file = extractionRoot.resolve(entry.getName()).normalize();
					if (mapper != null) 
						file = mapper.apply(entry, file);

					if (file == null)
						continue;

					file = file.toAbsolutePath().normalize();
					if (!file.startsWith(extractionRoot)) {
                        LOGGER.warn("Skipping ZIP entry '{}' as it escapes the extraction directory", entry.getName());
						continue;
					}

        			Files.createDirectories(file.getParent());
        			// NOTE: I tried using Files.copy here but it was super slow
        			// with large (> 1GB) files.  I'm guessing due to an internal 
        			// buffer size that's pretty small
					try (InputStream stream = zipFile.getInputStream(entry)) {
        				copy(stream, file);
					}
        		}
        	}
        }
	}
	
	/**
	 * Copies the contents of the given InputStream to the specified file path.
	 * @param stream
	 * @param path
	 * @throws IOException
	 */
	private static void copy(InputStream stream, Path path) throws IOException {
		final int size = 1024 * 1024; // ~1MB
	    try (OutputStream outStream = new FileOutputStream(path.toFile(), false)) {
		    byte[] buffer = new byte[size];
		    int bytesRead;
		    while ((bytesRead = stream.read(buffer)) != -1) {
		        outStream.write(buffer, 0, bytesRead);
		    }
	    }
	}

	/**
	 * Moves a file from the source to the target using an atomic move if 
	 * supported, otherwise falls back to a non-atomic move.  Both operations
	 * will replace any existing file at the target location.
	 * @param source
	 * @param target
	 * @return
	 * @throws IOException
	 * @see java.nio.file.Files#move(Path, Path, java.nio.file.CopyOption...)
	 */
	public static final Path moveFile(Path source, Path target, String fallbackMethod) throws IOException {
		try {
			return Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (AtomicMoveNotSupportedException ex) {
			if (FALLBACK_MOVE.equals(fallbackMethod)) {
				LOGGER.warn("Failed to move file atomically from '{}' to '{}', falling back to non-atomic move", source.toAbsolutePath(), target.toAbsolutePath(), ex);
				return Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
			} else if (FALLBACK_COPY.equals(fallbackMethod)) {
				LOGGER.warn("Failed to move file atomically from '{}' to '{}', falling back to copy to retain source as-is", source.toAbsolutePath(), target.toAbsolutePath(), ex);
				return Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
			} else {
				LOGGER.error("Failed to move file atomically from '{}' to '{}', no fallback specified", source.toAbsolutePath(), target.toAbsolutePath(), ex);
				throw ex;
			}
		}
	}

	/**
	 * Attempts to move the source file to the target location atomically. If atomic
	 * move isn't supported, it falls back to a non-atomic move, backing up the
	 * existing target file to tempDir.  Upon success, everything is cleaned up
	 * and on failure, recovery is attempted using the backup file.  If recovery
	 * fails, the backup file can be used to restore the target to its original
	 * state manually.
	 * @param source
	 * @param target
	 * @param tempDir
	 * @return
	 * @throws IOException
	 */
	public static final Path moveFileSafe(Path source, Path target, Path tempDir) throws IOException {
		LOGGER.debug("Creating temporary backup file in directory '{}'", tempDir.toAbsolutePath());
		Path backFile = Files.createTempFile(tempDir, target.getFileName().toString(), "bak");
		boolean retainBackup = false;

		try {
			// attempt automic move first
			LOGGER.debug("Attempting atomic move of file '{}' to '{}'", source.toAbsolutePath(), target.toAbsolutePath());
			return Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
		} catch (AtomicMoveNotSupportedException ex) {
			LOGGER.warn("Attempting atomic move of file '{}' to '{}' failed.", source.toAbsolutePath(), target.toAbsolutePath(), ex);
			// automic move isn't supported so we'll do 
			// a backup manual move/copy restore process
			boolean backedUp = false;
			if (Files.exists(target)) {
				LOGGER.debug("Backing up existing file '{}' to '{}'", target.toAbsolutePath(), backFile.toAbsolutePath());
				Files.copy(target, backFile, StandardCopyOption.REPLACE_EXISTING);
				backedUp = true;
			}

			try {
				// try the non-atomic move
				LOGGER.debug("Attempting non-atomic move of file '{}' to '{}'", source.toAbsolutePath(), target.toAbsolutePath());
				return Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				LOGGER.error("Failed to move file non-atomically from '{}' to '{}'", source.toAbsolutePath(), target.toAbsolutePath(), e);
				// attempt to restore backup if available
				if (backedUp) {
					try {
						LOGGER.debug("Restoring backup file from '{}' to '{}'", backFile.toAbsolutePath(), target.toAbsolutePath());
						FileHelper.moveFile(backFile, target, FileHelper.FALLBACK_COPY); 
					} catch (IOException ex2) {
						LOGGER.error("Failed to restore backup file from '{}' to '{}'. You will need to restore it manually.", backFile.toAbsolutePath(), target.toAbsolutePath(), ex2);
						retainBackup = true;
					}
				} else {
					// remove anything we attempted to move to the target
					try {
						Files.deleteIfExists(target);
					} catch (IOException ex3) {
						LOGGER.error("Failed to delete target file '" + target.toAbsolutePath().toString() + "'. You will need to delete it manually.", ex3);
					}
				}

				throw e;
			}
		} finally {
			try {
				if (!retainBackup) {
					Files.deleteIfExists(backFile); 
				}
			} catch (IOException e) {
				LOGGER.warn("Failed to delete backup file '" + backFile.toAbsolutePath().toString() + "'.", e);
			}
		}
	}

	/**
	 * Deletes the specified file or directory and all its contents.
	 * @param path
	 * @throws IOException
	 */
	public static final void deleteDirectory(Path path, boolean failOnError) throws IOException {
		if (path != null && Files.exists(path)) {
			try(Stream<Path> fileStream = Files.walk(path)) {
				Iterator<Path> it = fileStream
					.sorted((a, b) -> b.compareTo(a))
					.iterator(); // reverse; files before dirs
				while (it.hasNext()) {
					Path p = it.next();
					try {
						Files.delete(p);
					} catch (IOException e) {
						LOGGER.warn("Failed to delete the file '" + p.toAbsolutePath().toString() + "'.", e);
						if (failOnError) {
							throw e;
						}
					}
				}	
			} catch (Exception ex) {
				LOGGER.warn("Failed to delete directory '" + path.toAbsolutePath() + "'.", ex);
				throw ex;
			}
		}
	}
}
