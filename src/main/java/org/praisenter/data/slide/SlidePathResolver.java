package org.praisenter.data.slide;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.praisenter.data.BasicPathResolver;

final class SlidePathResolver extends BasicPathResolver<Slide> {
	private static final String THUMB_PATH = "thumb";
	private static final String THUMB_EXTENSION = "png";
	
	private final Path thumbPath;
	
	public SlidePathResolver(Path basePath, String extension) {
		super(basePath, "slides", extension);
		
		this.thumbPath = basePath.resolve(THUMB_PATH);
	}
	
	@Override
	public void initialize() throws IOException {
		super.initialize();
		Files.createDirectories(this.thumbPath);
	}
	
	public Path getThumbFileName(Slide slide) {
		return this.getFileName(slide, THUMB_EXTENSION);
	}
	
	public Path getRelativeThumbPath(Slide slide) {
		return Paths.get(THUMB_PATH).resolve(this.getFileName(slide));
	}
	
	public Path getThumbPath(Slide slide) {
		return this.thumbPath.resolve(this.getFileName(slide, THUMB_EXTENSION));
	}

	public Path getRelativeThumbPath() {
		return Paths.get(THUMB_PATH);
	}

	public Path getThumbPath() {
		return this.thumbPath;
	}
	
	public String getThumbExtension() {
		return THUMB_EXTENSION;
	}
	
}
