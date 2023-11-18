package org.praisenter.ui.library;

import java.nio.file.Path;

import org.praisenter.data.ImportExportFormat;

final class ExportRequest {
	private final ImportExportFormat format;
	private final Path path;
	
	public ExportRequest(ImportExportFormat format, Path path) {
		this.format = format;
		this.path = path;
	}
	
	public ImportExportFormat getFormat() {
		return format;
	}
	
	public Path getPath() {
		return path;
	}
}
