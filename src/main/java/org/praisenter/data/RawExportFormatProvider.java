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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.utility.FileHelper;

public final class RawExportFormatProvider<T extends Persistable> implements ImportExportProvider<T> {
	private static final Logger LOGGER = LogManager.getLogger();

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
		Path tempPath = Files.createTempFile(pr.getTempPath(), sourcePath.getFileName().toString(), "tmp");
		try {
			Files.copy(sourcePath, tempPath, StandardCopyOption.REPLACE_EXISTING);
			FileHelper.moveFile(tempPath, path, FileHelper.FALLBACK_MOVE);
		} finally {
			try {
				Files.deleteIfExists(tempPath);
			} catch (IOException ex) {
				LOGGER.warn("Failed to delete temporary file: " + tempPath, ex);
			}
		}
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
