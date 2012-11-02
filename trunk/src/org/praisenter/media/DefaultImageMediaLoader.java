package org.praisenter.media;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Default implementation for loading image media.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class DefaultImageMediaLoader implements ImageMediaLoader, MediaLoader<ImageMedia> {
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#load(java.lang.String)
	 */
	@Override
	public ImageMedia load(String fileName) throws MediaException {
		ImageMedia media = new ImageMedia();
		
		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			try {
				BufferedImage image = ImageIO.read(file);
				media.fileName = fileName;
				media.image = image;
			} catch (IOException e) {
				// FIXME translate?
				throw new MediaException(e);
			}
		} else {
			// FIXME translate?
			throw new MediaException("The path/file doesn't exist or is not a file.");
		}
		
		return media;
	}
}
