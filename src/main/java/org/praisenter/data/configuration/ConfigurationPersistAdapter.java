package org.praisenter.data.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.BasicPathResolver;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.KnownFormat;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.json.JsonIO;
import org.praisenter.utility.MimeType;

public final class ConfigurationPersistAdapter implements PersistAdapter<Configuration> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String EXTENSION = "json";

	private final BasicPathResolver<Configuration> pathResolver;
	
	private final Object lock;
	
	public ConfigurationPersistAdapter(Path path) {
		this.pathResolver = new BasicPathResolver<>(path, "config", EXTENSION);
		this.lock = new Object();
	}

	@Override
	public void initialize() throws IOException {
		this.pathResolver.initialize();
	}
	
	@Override
	public List<Configuration> load() throws IOException {
		List<Configuration> configs = new ArrayList<Configuration>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.pathResolver.getBasePath())) {
			for (Path file : stream) {
				if (Files.isRegularFile(file)) {
					if (MimeType.JSON.check(file)) {
						try (InputStream is = Files.newInputStream(file)) {
							Configuration config = JsonIO.read(is, Configuration.class);
							configs.add(config);
						} catch (Exception ex) {
							LOGGER.warn("Failed to load configuration '" + file.toAbsolutePath().toString() + "'", ex);
						}
					}
				}
			}
		}
		return configs;
	}
	
	@Override
	public void create(Configuration item) throws IOException {
		Path path = this.pathResolver.getPath(item);
		synchronized (this.lock) {
			if (Files.exists(path)) {
				throw new FileAlreadyExistsException(path.toAbsolutePath().toString());
			}
			JsonIO.write(path, item);
		}
	}
	
	@Override
	public void update(Configuration item) throws IOException {
		synchronized (this.lock) {
			Path path = this.pathResolver.getPath(item);
			JsonIO.write(path, item);
		}
	}
	
	@Override
	public void delete(Configuration item) throws IOException {
		// no-op
	}
	
	@Override
	public void exportData(KnownFormat format, Path path, Configuration item) throws IOException {
		// no-op
	}
	
	@Override
	public void exportData(KnownFormat format, ZipOutputStream destination, List<Configuration> items) throws IOException {
		// no-op
	}
	
	@Override
	public DataImportResult<Configuration> importData(Path path) throws IOException {
		// no-op
		return new DataImportResult<>();
	}
	
	@Override
	public Path getFilePath(Configuration item) {
		return this.pathResolver.getPath(item);
	}
}
