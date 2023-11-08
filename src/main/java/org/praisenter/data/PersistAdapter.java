package org.praisenter.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public interface PersistAdapter<T extends Persistable> {
	public void initialize() throws IOException;
	public List<T> load() throws IOException;
	public void create(T item) throws IOException;
	public void update(T item) throws IOException;
	public void delete(T item) throws IOException;
	public boolean upsert(T item) throws IOException;
	public Object getLock(UUID id);
	public DataImportResult<T> importData(Path path) throws IOException;
	public void exportData(ImportExportFormat format, ZipArchiveOutputStream destination, List<T> items) throws IOException;
	public void exportData(ImportExportFormat format, Path path, T item) throws IOException;
	public Path getFilePath(T item);
	public PathResolver<T> getPathResolver();
}
