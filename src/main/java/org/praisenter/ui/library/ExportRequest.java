package org.praisenter.ui.library;

import java.nio.file.Path;

import org.praisenter.data.Copyable;
import org.praisenter.data.ImportExportFormat;

final class ExportRequest implements Copyable {
	private final ImportExportFormat bibleFormat;
	private final ImportExportFormat slideFormat;
	private final ImportExportFormat mediaFormat;
	private final ImportExportFormat songFormat;
	private final Path path;
	
	public static final ExportRequest newDefaultRequest() {
		return new ExportRequest(
				ImportExportFormat.PRAISENTER3, 
				ImportExportFormat.PRAISENTER3, 
				ImportExportFormat.PRAISENTER3, 
				ImportExportFormat.PRAISENTER3, 
				null);
	}
	
	public ExportRequest(
			ImportExportFormat bibleFormat,
			ImportExportFormat slideFormat,
			ImportExportFormat mediaFormat,
			ImportExportFormat songFormat,
			Path path) {
		this.bibleFormat = bibleFormat;
		this.slideFormat = slideFormat;
		this.mediaFormat = mediaFormat;
		this.songFormat = songFormat;
		this.path = path;
	}
	
	public ImportExportFormat getBibleFormat() {
		return this.bibleFormat;
	}
	
	public ImportExportFormat getSlideFormat() {
		return this.slideFormat;
	}
	
	public ImportExportFormat getMediaFormat() {
		return this.mediaFormat;
	}
	
	public ImportExportFormat getSongFormat() {
		return this.songFormat;
	}
	
	public Path getPath() {
		return this.path;
	}
	
	@Override
	public Object copy() {
		return new ExportRequest(bibleFormat, slideFormat, mediaFormat, songFormat, path);
	}
}
