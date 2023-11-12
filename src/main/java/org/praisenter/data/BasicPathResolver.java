package org.praisenter.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.praisenter.utility.StringManipulator;

public class BasicPathResolver<T extends Persistable> implements PathResolver<T> {
	protected final Path basePath;
	protected final String exportBasePath;
	protected final String extension;
	
	public BasicPathResolver(Path basePath, String exportBasePath, String extension) {
		this.basePath = basePath;
		this.exportBasePath = exportBasePath;
		this.extension = extension;
	}
	
	public Path getFileName(T item, String extension) {
		return this.getFileName(item.getId(), extension);
	}
	
	public Path getFileName(UUID id, String extension) {
		String ext = "";
		if (!StringManipulator.isNullOrEmpty(extension)) {
			ext = "." + extension;
		}
		return Paths.get(id.toString().replaceAll("-", "") + ext);
	}
	
	@Override
	public void initialize() throws IOException {
		Files.createDirectories(this.basePath);
	}
	
	@Override
	public Path getBasePath() {
		return this.basePath;
	}
	
	@Override
	public Path getFileName(T item) {
		return this.getFileName(item, this.extension);
	}
	
	@Override
	public Path getPath(T item) {
		return this.basePath.resolve(this.getFileName(item, this.extension));
	}
	
	@Override
	public Path getRelativePath(T item) {
		return this.getFileName(item);
	}
	
	@Override
	public Path getExportBasePath() {
		return Paths.get(this.exportBasePath);
	}
	
	@Override
	public Path getExportPath(T item) {
		return this.getExportBasePath().resolve(this.getRelativePath(item));
	}

	// raw

	@Override
	public Path getRawPath(T item) {
		return this.getPath(item);
	}
	
	// friendly
	
	public Path getFriendlyFileName(T item, String extension) {
		String cleansedFileName = StringManipulator.toFileName(item.getName(), "");
		String ext = "";
		if (!StringManipulator.isNullOrEmpty(extension)) {
			ext = "." + extension;
		}
		return Paths.get(cleansedFileName + ext);
	}

	@Override
	public Path getFriendlyFileName(T item) {
		return this.getFriendlyFileName(item, this.extension);
	}
	
	@Override
	public Path getFriendlyRelativePath(T item) {
		return this.getFriendlyFileName(item);
	}
	
	@Override
	public Path getFriendlyExportPath(T item) {
		return this.getExportBasePath().resolve(this.getFriendlyRelativePath(item));
	}
}
