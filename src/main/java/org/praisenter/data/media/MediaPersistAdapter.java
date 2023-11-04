package org.praisenter.data.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.AbstractPersistAdapter;
import org.praisenter.data.DeleteFilesShutdownHook;
import org.praisenter.data.ImportExportFormat;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.media.tools.MediaTools;
import org.praisenter.utility.MimeType;

public final class MediaPersistAdapter extends AbstractPersistAdapter<Media, MediaPathResolver> implements PersistAdapter<Media> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String EXTENSION = "json";

	private final MediaConfiguration configuration;
	private final MediaTools tools;
	
	public MediaPersistAdapter(Path path, MediaConfiguration configuration) {
		super(new MediaPathResolver(path, EXTENSION));
		this.configuration = configuration;
		this.tools = new MediaTools(this.pathResolver.getBasePath());
		
		// praisenter zip format (allows us to skip import steps)
		this.importExportProviders.put(ImportExportFormat.PRAISENTER3, new PraisenterMediaFormatProvider());
		
		// raw audio/image/video
		this.importExportProviders.put(ImportExportFormat.RAW_IMAGE, new RawImageMediaFormatProvider(this.configuration, this.tools));
		this.importExportProviders.put(ImportExportFormat.RAW_VIDEO, new RawVideoMediaFormatProvider(this.configuration, this.tools));
		this.importExportProviders.put(ImportExportFormat.RAW_AUDIO, new RawAudioMediaFormatProvider(this.configuration, this.tools));
	}

	@Override
	public void initialize() throws IOException {
		super.initialize();
		this.tools.initialize();
	}
	
	@Override
	protected Media load(Path path) throws IOException {
		if (Files.isRegularFile(path)) {
			String mimeType = MimeType.get(path);
			if (MimeType.JSON.is(mimeType)) {
				Media m = JsonIO.read(path, Media.class);
				m.setMediaPath(this.pathResolver.getMediaPath(m));
				if (m.getMediaType() == MediaType.IMAGE) {
					m.setMediaImagePath(this.pathResolver.getMediaPath(m));
				} else if (m.getMediaType() == MediaType.AUDIO) {
					m.setMediaImagePath(this.pathResolver.getThumbPath(m));
				} else {
					m.setMediaImagePath(this.pathResolver.getImagePath(m));
				}
				m.setMediaThumbnailPath(this.pathResolver.getThumbPath(m));
				return m;
			}
		}
		return null;
	}
	
	@Override
	protected void create(Path path, Media item) throws IOException {
		JsonIO.write(path, item);
	}
	
	@Override
	protected void update(Path path, Media item) throws IOException {
		JsonIO.write(path, item);
	}
	
	@Override
	protected void delete(Path path, Media item) throws IOException {
		// delete the item data first - this ensures that it doesn't show
		// up in the UI any more
		Files.deleteIfExists(path);
		
		// now, it's possible that media could be playing at the time this code
		// executes, so the best thing we can do is to try to delete when the
		// app shutsdown - deleting this stuff is really only for clean up anyway
		this.deleteWithShutdownFallback(this.pathResolver.getMediaPath(item));
		this.deleteWithShutdownFallback(this.pathResolver.getImagePath(item));
		this.deleteWithShutdownFallback(this.pathResolver.getThumbPath(item));
	}
	
	private void deleteWithShutdownFallback(Path path) {
		try {
			Files.deleteIfExists(path);
		} catch (Exception ex) {
			LOGGER.warn("Failed to delete path '" + path.toAbsolutePath().toString() + "' due to: " + ex.getMessage() + ". Will try again at shutdown.");
			DeleteFilesShutdownHook.deleteOnShutdown(path);
		}
	}
}
