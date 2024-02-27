package org.praisenter.data;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.LockMap;
//import org.praisenter.data.json.JsonIO;
import org.praisenter.utility.MimeType;

public abstract class AbstractPersistAdapter<T extends Persistable, E extends PathResolver<T>> implements PersistAdapter<T> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	protected final E pathResolver;
	protected final Map<ImportExportFormat, ImportExportProvider<T>> importExportProviders;
	
	private final LockMap<UUID> locks;
	private final Object exportLock;
	
	public AbstractPersistAdapter(E pathResolver) {
		this.pathResolver = pathResolver;
		this.locks = new LockMap<>();
		this.exportLock = new Object();
		this.importExportProviders = new LinkedHashMap<>();
	}
	
	protected abstract T load(Path path) throws IOException;
	protected abstract void create(Path path, T item) throws IOException;
	protected abstract void update(Path path, T item) throws IOException;
	protected abstract void delete(Path path, T item) throws IOException;
	
	@Override
	public void initialize() throws IOException {
		this.pathResolver.initialize();
	}
	
	@Override
	public List<T> load() throws IOException {
		final List<T> items = new ArrayList<T>();
		final Path basePath = this.pathResolver.getBasePath();
		LOGGER.trace("Loading data from '{}'", basePath.toAbsolutePath());
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(basePath)) {
			for (Path path : stream) {
				try {
					LOGGER.trace("Loading '{}'", path.toAbsolutePath());
					T item = this.load(path);
					if (item != null) {
						LOGGER.debug("Item '{}' loaded successfully", item.getName());
						items.add(item);
					}
				} catch (Exception ex) {
					LOGGER.warn("Failed to load '" + path.toAbsolutePath() + "'", ex);
				}
			}
		}
		return items;
	}
	
	@Override
	public void create(T item) throws IOException {
		LOGGER.trace("Getting lock for '{}'", item.getId());
		synchronized (this.locks.get(item.getId())) {
			LOGGER.trace("Lock for '{}' obtained", item.getId());
			Path path = this.pathResolver.getPath(item);
			LOGGER.trace("Checking if file exists at '{}'", path.toAbsolutePath());
			if (Files.exists(path)) {
				throw new FileAlreadyExistsException(path.toAbsolutePath().toString());
			}
			LOGGER.debug("Creating file '{}'", path.toAbsolutePath());
			this.create(path, item);
		}
	}
	
	@Override
	public void update(T item) throws IOException {
		LOGGER.trace("Getting lock for '{}'", item.getId());
		synchronized (this.locks.get(item.getId())) {
			LOGGER.trace("Lock for '{}' obtained", item.getId());
			Path path = this.pathResolver.getPath(item);
			LOGGER.debug("Updating file '{}'", path.toAbsolutePath());
			this.update(path, item);
		}
	}
	
	@Override
	public void delete(T item) throws IOException {
		LOGGER.trace("Getting export lock");
		synchronized (this.exportLock) {
			LOGGER.trace("Export lock obtained");
			LOGGER.trace("Getting lock for '{}'", item.getId());
			synchronized (this.locks.get(item.getId())) {
				LOGGER.trace("Lock for '{}' obtained", item.getId());
				Path path = this.pathResolver.getPath(item);
				LOGGER.debug("Deleting file '{}'", path.toAbsolutePath());
				this.delete(path, item);
			}
		}
	}
	
	@Override
	public boolean upsert(T item) throws IOException {
		LOGGER.trace("Getting lock for '{}'", item.getId());
		synchronized (this.locks.get(item.getId())) {
			LOGGER.trace("Lock for '{}' obtained", item.getId());
			Path path = this.pathResolver.getPath(item);
			boolean exists = Files.exists(path);
			LOGGER.debug("Upserting file '{}'", path.toAbsolutePath());
			if (exists) {
				this.update(item);
			} else {
				this.create(item);
			}
			return exists;
		}
	}
	
	@Override
	public void exportData(ImportExportFormat format, Path path, T item) throws IOException {
		LOGGER.trace("Getting export lock");
		synchronized (this.exportLock) {
			LOGGER.trace("Export lock obtained");
			LOGGER.trace("Getting export provider for format '{}'", format);
			ImportExportProvider<T> provider = this.importExportProviders.get(format);
			if (provider == null) {
				throw new UnknownFormatException(format.name());
			}
			LOGGER.trace("Format provider found '{}", provider.getClass().getName());
			LOGGER.trace("Getting lock for '{}'", item.getId());
			synchronized (this.locks.get(item.getId())) {
				LOGGER.trace("Lock for '{}' obtained", item.getId());
				try (FileOutputStream fos = new FileOutputStream(path.toFile());
					BufferedOutputStream bos = new BufferedOutputStream(fos)) {
					LOGGER.debug("Exporting item '{}'", item.getName());
					provider.exp(this, bos, item);
				}	
			}
		}
	}
	
	@Override
	public void exportData(ImportExportFormat format, ZipArchiveOutputStream destination, List<T> items) throws IOException {
		LOGGER.trace("Getting export provider for format '{}'", format);
		ImportExportProvider<T> provider = this.importExportProviders.get(format);
		if (provider == null) {
			throw new UnknownFormatException(format.name());
		}
		LOGGER.trace("Format provider found '{}", provider.getClass().getName());
		LOGGER.trace("Getting export lock");
		synchronized (this.exportLock) {
			LOGGER.trace("Export lock obtained");
			for (T item : items) {
				LOGGER.trace("Getting lock for '{}'", item.getId());
				synchronized (this.locks.get(item.getId())) {
					LOGGER.trace("Lock for '{}' obtained", item.getId());
					LOGGER.debug("Exporting item '{}'", item.getName());
					provider.exp(this, destination, item);
				}
			}
		}
	}

	@Override
	public DataImportResult<T> importData(Path path) throws IOException {
		Collection<ImportExportProvider<T>> providers = this.importExportProviders.values();
		return this.importFile(path, providers);
	}

	private DataImportResult<T> importFile(Path path, Collection<ImportExportProvider<T>> providers) throws IOException {
		DataImportResult<T> results = new DataImportResult<>();
		
		LOGGER.debug("Importing data from '{}'", path.toAbsolutePath());
		if (!Files.isRegularFile(path)) {
			throw new UnsupportedOperationException("Cannot import data from '" + path.toAbsolutePath() + "' because it's not a regular file");
		}
		
		// STEP 1: Attempt to import the file as-is
		for (ImportExportProvider<T> provider : providers) {
			LOGGER.trace("Testing provider '{}'", provider.getClass().getName());
			if (provider.isSupported(path)) {
				try {
					LOGGER.info("Attempting import of '{}' using provider '{}'", path.toAbsolutePath(), provider.getClass());
					DataImportResult<T> res = provider.imp(this, path);
					results.add(res);
					if (!res.isEmpty()) {
						break;
					}
				} catch (Exception ex) {
					LOGGER.warn("Failed to import '" + path.toAbsolutePath() + "' using provider '" + provider.getClass().getName() + "'", ex);
				}
			}
		}
		
		// did we get anything?
		if (results.isEmpty()) {
			LOGGER.info("No import provider was found to read '{}'", path.toAbsolutePath());
			// we failed to read the raw file with a format provider
			// check if the file is a zip and we'll try to import each
			// file as an item
			if (MimeType.ZIP.check(path)) {
				LOGGER.debug("The file '{}' is a zip file, attempting to extract to import contents individually", path.toAbsolutePath());
				// create a temp location to store the unzipped files
				Path importPath = this.pathResolver.getBasePath().resolve("temp");
				Files.createDirectories(importPath);
				Path tempPath = Files.createTempDirectory(importPath, "IMPORT");
				
				try {
					// extract the zip and lets try to read each file individually
					this.unzip(path, tempPath);
					LOGGER.debug("Unzip to '{}' completed successfully", tempPath.toAbsolutePath());
					
					// process the directory
					try (Stream<Path> fileStream = Files.walk(tempPath)) {
						fileStream.forEach(p -> {
							if (Files.isRegularFile(p)) {
								try {
									DataImportResult<T> res = this.importFile(p, providers);
									results.add(res);
								} catch (IOException e) {
									LOGGER.debug("Failed to import file '" + p.toAbsolutePath() + "'", e);
								}
							}
					    });
					}
				} finally {
					// when done, clean up the temp directory
					if (tempPath != null && Files.exists(tempPath)) {
						try(Stream<Path> fileStream = Files.walk(tempPath)) {
							fileStream
								.sorted((a, b) -> b.compareTo(a)) // reverse; files before dirs
								.forEach(p -> {
							        try { 
							        	Files.delete(p); 
							        } catch(IOException e) {
							        	LOGGER.warn("Failed to delete the temp file '" + p.toAbsolutePath().toString() + "'.", e);
							        }
							     });
						} catch (Exception ex) {
							LOGGER.warn("Failed to clean up temp directory '" + tempPath.toAbsolutePath() + "'.", ex);
						}
					}
				}
			}
		}
		
		return results;
	}
	
	private void unzip(Path zip, Path folder) throws IOException {
		// JAVABUG (L) 11/01/23 [workaround] Native java.util.zip package can't support zips 4GB or bigger or elements 2GB or bigger
        try (ZipFile zipFile = ZipFile.builder().setPath(zip).get()) {
        	Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        	while (entries.hasMoreElements()) {
        		ZipArchiveEntry entry = entries.nextElement();
        		
        		if (entry.isDirectory()) 
        			continue;
        			
        		if (zipFile.canReadEntryData(entry)) {
        			Path file = folder.resolve(entry.getName());
        			Files.createDirectories(file.getParent());
        			// NOTE: I tried using Files.copy here but it was super slow
        			// with large (> 1GB) files.  I'm guessing due to an internal 
        			// buffer size that's pretty small
        			this.copy(zipFile.getInputStream(entry), file);
        		}
        	}
        }
	}
	
	private void copy(InputStream stream, Path path) throws IOException {
		final int size = 1024 * 1024; // ~1MB
	    try (OutputStream outStream = new FileOutputStream(path.toFile(), false)) {
		    byte[] buffer = new byte[size];
		    int bytesRead;
		    while ((bytesRead = stream.read(buffer)) != -1) {
		        outStream.write(buffer, 0, bytesRead);
		    }
	    }
	}
	
	@Override
	public Path getFilePath(T item) {
		return this.pathResolver.getPath(item);
	}
	
	@Override
	public Object getLock(UUID id) {
		return this.locks.get(id);
	}
	
	@Override
	public PathResolver<T> getPathResolver() {
		return this.pathResolver;
	}
}
