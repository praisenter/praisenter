package org.praisenter.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipOutputStream;

public interface PersistAdapter<T extends Persistable> {
	public void initialize() throws IOException;
	public List<T> load() throws IOException;
	public void create(T item) throws IOException;
	public void update(T item) throws IOException;
	public void delete(T item) throws IOException;
	public DataImportResult<T> importData(Path path) throws IOException;
	public void exportData(KnownFormat format, ZipOutputStream destination, List<T> items) throws IOException;
	public void exportData(KnownFormat format, Path path, T item) throws IOException;
}
