package org.praisenter.data.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.praisenter.data.BasicPathResolver;
import org.praisenter.data.PathResolver;

final class MediaPathResolver extends BasicPathResolver<Media> implements PathResolver<Media> {
	private static final String MEDIA_PATH = "media";
	private static final String THUMB_PATH = "thumb";
	private static final String IMAGE_PATH = "image";
	private static final String IMPORT_PATH = "import";
	
	private static final String IMAGE_EXTENSION = "jpg";
	private static final String THUMB_EXTENSION = "png";
	
	private static final String EXPORT_PATH = "media";
	
	private final Path mediaPath;
	private final Path imagePath;
	private final Path thumbPath;
	private final Path importPath;
	
	public MediaPathResolver(Path basePath, String extension) {
		super(basePath, extension);
		this.mediaPath = this.basePath.resolve(MEDIA_PATH);
		this.imagePath = this.basePath.resolve(IMAGE_PATH);
		this.thumbPath = this.basePath.resolve(THUMB_PATH);
		this.importPath = this.basePath.resolve(IMPORT_PATH);
	}

	public void initialize() throws IOException {
		Files.createDirectories(this.mediaPath);
		Files.createDirectories(this.imagePath);
		Files.createDirectories(this.thumbPath);
		Files.createDirectories(this.importPath);
	}
	
	// media
	
	public Path getMediaFileName(Media media) {
		return this.getFileName(media, media.getExtension());
	}
	
	public Path getRelativeMediaPath(Media media) {
		return Paths.get(MEDIA_PATH).resolve(this.getMediaFileName(media));
	}
	
	public Path getMediaPath(Media media) {
		return this.mediaPath.resolve(this.getFileName(media, media.getExtension()));
	}

	public Path getRelativeMediaPath() {
		return Paths.get(MEDIA_PATH);
	}

	public Path getMediaPath() {
		return this.mediaPath;
	}
	
	// image
	
	public Path getImageFileName(Media media) {
		return this.getFileName(media, IMAGE_EXTENSION);
	}
	
	public Path getRelativeImagePath(Media media) {
		return Paths.get(IMAGE_PATH).resolve(this.getImageFileName(media));
	}
	
	public Path getImagePath(Media media) {
		return this.imagePath.resolve(this.getFileName(media, IMAGE_EXTENSION));
	}

	public Path getRelativeImagePath() {
		return Paths.get(IMAGE_PATH);
	}

	public Path getImagePath() {
		return this.imagePath;
	}
	
	public String getImageExtension() {
		return IMAGE_EXTENSION;
	}
	
	// thumb
	
	public Path getThumbFileName(Media media) {
		return this.getFileName(media, THUMB_EXTENSION);
	}
	
	public Path getRelativeThumbPath(Media media) {
		return Paths.get(THUMB_PATH).resolve(this.getFileName(media));
	}
	
	public Path getThumbPath(Media media) {
		return this.thumbPath.resolve(this.getFileName(media, THUMB_EXTENSION));
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
	
	// import
	
	public Path getImportPath() {
		return this.importPath;
	}
	
	// export
	
	public Path getExportBasePath() {
		return Paths.get(EXPORT_PATH);
	}
	
	public Path getExportPath(Media media) {
		return this.getExportBasePath().resolve(this.getRelativePath(media));
	}
	
	public Path getExportImagePath(Media media) {
		return this.getExportBasePath().resolve(this.getRelativeImagePath(media));
	}
	
	public Path getExportMediaPath(Media media) {
		return this.getExportBasePath().resolve(this.getRelativeMediaPath(media));
	}
	
	public Path getExportThumbPath(Media media) {
		return this.getExportBasePath().resolve(this.getRelativeThumbPath(media));
	}
}
