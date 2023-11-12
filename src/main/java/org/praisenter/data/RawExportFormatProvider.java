package org.praisenter.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.NotImplementedException;

public final class RawExportFormatProvider<T extends Persistable> implements ImportExportProvider<T> {
	public RawExportFormatProvider() {
		
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		return false;
	}
	
	@Override
	public boolean isSupported(Path path) {
		return false;
	}
	
	@Override
	public boolean isSupported(String name, InputStream stream) throws IOException {
		return false;
	}
	
	@Override
	public void exp(PersistAdapter<T> adapter, OutputStream stream, T data) throws IOException {
		PathResolver<T> pr = adapter.getPathResolver();
		Path path = pr.getRawPath(data);
		Files.copy(path, stream);
	}
	
	@Override
	public void exp(PersistAdapter<T> adapter, Path path, T data) throws IOException {
		PathResolver<T> pr = adapter.getPathResolver();
		Path sourcePath = pr.getRawPath(data);
		Files.copy(sourcePath, path, StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Override
	public void exp(PersistAdapter<T> adapter, ZipArchiveOutputStream stream, T data) throws IOException {
		PathResolver<T> pr = adapter.getPathResolver();
		Path targetPath = pr.getFriendlyExportPath(data);
		Path sourcePath = pr.getRawPath(data);
		ZipArchiveEntry entry = new ZipArchiveEntry(FilenameUtils.separatorsToUnix(targetPath.toString()));
		stream.putArchiveEntry(entry);
		Files.copy(sourcePath, stream);
		stream.closeArchiveEntry();
	}
	
	@Override
	public DataImportResult<T> imp(PersistAdapter<T> adapter, Path path) throws IOException {
		throw new NotImplementedException();
	}
}
