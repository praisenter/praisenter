package org.praisenter.media;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.praisenter.xml.FileProperties;

/**
 * Default implementation for loading image media.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class DefaultImageMediaLoader implements ImageMediaLoader, MediaLoader<ImageMedia> {
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#isSupported(java.lang.String)
	 */
	@Override
	public boolean isSupported(String mimeType) {
		String[] supportedTypes = ImageIO.getReaderMIMETypes();
		for (String type : supportedTypes) {
			if (type.equalsIgnoreCase(mimeType)) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#getMediaType()
	 */
	@Override
	public Class<ImageMedia> getMediaType() {
		return ImageMedia.class;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#load(java.lang.String)
	 */
	@Override
	public ImageMedia load(String filePath) throws MediaException {
		Path path = FileSystems.getDefault().getPath(filePath);
		
		if (Files.exists(path) && Files.isRegularFile(path)) {
			try {
				BufferedImage image = ImageIO.read(path.toFile());
				FileProperties props = FileProperties.getFileProperties(filePath);
				ImageMedia media = new ImageMedia(props, image);
				return media;
			} catch (IOException e) {
				// FIXME translate?
				throw new MediaException(e);
			}
		} else {
			// FIXME translate?
			throw new MediaException("The path/file doesn't exist or is not a file.");
		}
	}
}
