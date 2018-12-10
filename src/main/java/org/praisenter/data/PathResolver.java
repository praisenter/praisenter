package org.praisenter.data;

import java.io.IOException;
import java.nio.file.Path;

public interface PathResolver<T extends Identifiable> {
	public void initialize() throws IOException;
	public Path getFileName(T item);
	public Path getBasePath();
	public Path getRelativePath(T item);
	public Path getPath(T item);
	public Path getExportBasePath();
	public Path getExportPath(T item);
}
