package org.praisenter.data;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.LockMap;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.RuntimeInterruptException;
import org.praisenter.utility.FileHelper;

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
				Path tempPath = Files.createTempFile(this.pathResolver.getTempPath(), path.getFileName().toString(), "tmp");
				try {
					try (FileOutputStream fos = new FileOutputStream(tempPath.toFile());
						BufferedOutputStream bos = new BufferedOutputStream(fos)) {
						LOGGER.debug("Exporting item '{}'", item.getName());
						provider.exp(this, bos, item);
					}
					FileHelper.moveFile(tempPath, path, FileHelper.FALLBACK_MOVE);
				} finally {
					try {
						Files.deleteIfExists(tempPath);
					} catch (IOException ex) {
						LOGGER.warn("Failed to delete temporary file: " + tempPath, ex);
					}
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
	public DataImportResult<T> importData(Path path) throws IOException, InterruptedException {
		Collection<ImportExportProvider<T>> providers = this.importExportProviders.values();
		return this.importFile(path, providers);
	}

	private DataImportResult<T> importFile(Path path, Collection<ImportExportProvider<T>> providers) throws IOException, InterruptedException {
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
				} catch (InterruptedException ex) {
					throw ex;
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
				Path tempPath = null;
				
				try {
					tempPath = Files.createTempDirectory(this.pathResolver.getTempPath(), "IMPORT");

					// extract the zip and lets try to read each file individually
					FileHelper.unzip(path, tempPath);
					LOGGER.debug("Unzip to '{}' completed successfully", tempPath.toAbsolutePath());
					
					// process the directory
					try (Stream<Path> fileStream = Files.walk(tempPath)) {
						fileStream.forEach(p -> {
							if (Files.isRegularFile(p)) {
								try {
									DataImportResult<T> res = this.importFile(p, providers);
									results.add(res);
								} catch (InterruptedException ex) {
									// throw a runtime exception - we'll check for this later
									throw new RuntimeInterruptException(ex);
								} catch (IOException ex) {
									LOGGER.debug("Failed to import file '" + p.toAbsolutePath() + "'", ex);
								}
							}
					    });
					} catch (RuntimeInterruptException ex) {
						// rethrow the cause as an InterruptedException
						throw ex.getCause();
					}
				} finally {
					// when done, clean up the temp directory
					FileHelper.deleteDirectory(tempPath, false);
				}
			}
		}
		
		return results;
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
