package org.praisenter.data.slide;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.AbstractPersistAdapter;
import org.praisenter.data.ImportExportFormat;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.PraisenterFormatProvider;
import org.praisenter.data.RawExportFormatProvider;
import org.praisenter.data.json.JsonIO;
import org.praisenter.utility.FileHelper;
import org.praisenter.utility.MimeType;

public final class SlidePersistAdapter extends AbstractPersistAdapter<Slide, SlidePathResolver> implements PersistAdapter<Slide> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String EXTENSION = "json";
	
	private final SlideConfiguration configuration;
	private final SlideRenderer renderer;
	
	public SlidePersistAdapter(Path basePath, SlideRenderer renderer, SlideConfiguration configuration) {
		super(new SlidePathResolver(basePath, EXTENSION));
		this.configuration = configuration;
		this.renderer = renderer;
		
		this.importExportProviders.put(ImportExportFormat.PRAISENTER3, new PraisenterFormatProvider<>(Slide.class));
		this.importExportProviders.put(ImportExportFormat.RAW, new RawExportFormatProvider<Slide>());
	}
	
	@Override
	protected Slide load(Path path) throws IOException {
		if (Files.isRegularFile(path)) {
			String mimeType = MimeType.get(path);
			if (MimeType.JSON.is(mimeType)) {
				Slide s = JsonIO.read(path, Slide.class);
				s.setThumbnailPath(this.pathResolver.getThumbPath(s));
				return s;
			}
		}
		return null;
	}
	
	@Override
	protected void create(Path path, Slide item) throws IOException {
		Path thumbnailPath = this.pathResolver.getThumbPath(item);
		BufferedImage image = this.renderer.renderThumbnail(item, this.configuration.getThumbnailWidth(), this.configuration.getThumbnailHeight());
		
		// NOTE: need to set the thumbnail path because it won't be set
		item.setThumbnailPath(thumbnailPath);
		JsonIO.write(path, item, this.pathResolver.getTempPath());

		if (image != null) {
			// if this fails, it's fine - we've saved what was important
			// and if the user wants to retry, they can do so later
			try {
				writeThumbnail(image, thumbnailPath);
			} catch (IOException ex) {
				LOGGER.warn("Failed to write thumbnail image at path '{}'", thumbnailPath.toAbsolutePath(), ex);
			}
		}
	}
	
	@Override
	protected void update(Path path, Slide item) throws IOException {
		Path thumbnailPath = this.pathResolver.getThumbPath(item);
		BufferedImage image = this.renderer.renderThumbnail(item, this.configuration.getThumbnailWidth(), this.configuration.getThumbnailHeight());

		// NOTE: need to set the thumbnail path every time 
		// in the case of the slide being new where the field
		// will stay null until the editor is closed and re-opened
		item.setThumbnailPath(thumbnailPath);
		JsonIO.write(path, item, this.pathResolver.getTempPath());

		if (image != null) {
			// if this fails, it's fine - we've saved what was important
			// and if the user wants to retry, they can do so later
			try {
				writeThumbnail(image, thumbnailPath);
			} catch (IOException ex) {
				LOGGER.warn("Failed to write thumbnail image at path '{}'", thumbnailPath.toAbsolutePath(), ex);
			}
		}
	}
	
	@Override
	protected void delete(Path path, Slide item) throws IOException {
		Files.deleteIfExists(path);
		try {
			Files.deleteIfExists(this.pathResolver.getThumbPath(item));
		} catch (IOException ex) {
			LOGGER.warn("Failed to delete thumbnail at path '{}'", this.pathResolver.getThumbPath(item).toAbsolutePath(), ex);
		}
	}

	/**
	 * Writes the thumbnail image to the specified path safely by 
	 * first writing to a temporary file and then moving it to the 
	 * final location.
	 * @param image
	 * @param thumbnailPath
	 * @throws IOException
	 */
	private void writeThumbnail(BufferedImage image, Path thumbnailPath) throws IOException {
		// write to temporary file first
		Path tempDir = this.pathResolver.getThumbPath();
		Path tempFile = Files.createTempFile(this.pathResolver.getTempPath(), thumbnailPath.getFileName().toString(), "tmp");

		try {
			// then attempt a safe replace
			boolean success = ImageIO.write(image, this.pathResolver.getThumbExtension(), tempFile.toFile());
			if (!success) {
				throw new IOException("Failed to write thumbnail image to " + tempFile.toString());
			}
			FileHelper.moveFileSafe(tempFile, thumbnailPath, tempDir);
		} finally {
			try {
				Files.deleteIfExists(tempFile);
			} catch (IOException ex) {
				LOGGER.warn("Failed to delete temporary file at path '{}'", tempFile.toAbsolutePath(), ex);
			}
		}
	}
}
