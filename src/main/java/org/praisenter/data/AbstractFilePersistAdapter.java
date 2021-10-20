package org.praisenter.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.LockMap;
import org.praisenter.utility.MimeType;

public abstract class AbstractFilePersistAdapter<T extends Persistable, E extends PathResolver<T>> implements PersistAdapter<T> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final LockMap<UUID> locks;
	protected final E pathResolver;
	
	public AbstractFilePersistAdapter(E pathResolver) {
		this.locks = new LockMap<UUID>();
		this.pathResolver = pathResolver;
	}

	protected abstract T read(String mimeType, InputStream stream) throws IOException;
	protected abstract void write(Path path, T item) throws IOException;
	
	protected final Object getItemLock(T item) {
		return this.locks.get(item.getId());
	}
	
	@Override
	public void initialize() throws IOException {
		this.pathResolver.initialize();
	}
	
	@Override
	public List<T> load() throws IOException {
		List<T> items = new ArrayList<T>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.pathResolver.getBasePath())) {
			for (Path file : stream) {
				if (Files.isRegularFile(file)) {
					String mimeType = MimeType.get(file);
					try (InputStream is = Files.newInputStream(file)) {
						T m = this.read(mimeType, is);
						items.add(m);
					} catch (Exception ex) {
						LOGGER.error("Failed to load '" + file.toAbsolutePath() + "' due to: " + ex.getMessage(), ex);
					}
				}
			}
		}
		return items;
	}

	@Override
	public void create(T item) throws IOException {
		Path path = this.pathResolver.getPath(item);
		synchronized (this.getItemLock(item)) {
			if (Files.exists(path)) {
				throw new FileAlreadyExistsException(path.toAbsolutePath().toString());
			}
			this.write(path, item);
		}
	}

	@Override
	public void update(T item) throws IOException {
		Path path = this.pathResolver.getPath(item);
		synchronized (this.getItemLock(item)) {
			this.write(path, item);
		}
	}

	@Override
	public void delete(T item) throws IOException {
		Path path = this.pathResolver.getPath(item);
		synchronized (this.getItemLock(item)) {
			Files.deleteIfExists(path);
		}
	}
}
