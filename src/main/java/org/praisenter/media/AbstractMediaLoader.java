package org.praisenter.media;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.praisenter.utility.ImageManipulator;

public abstract class AbstractMediaLoader implements MediaLoader {
	final MediaThumbnailSettings settings;
	
	public AbstractMediaLoader(MediaThumbnailSettings settings) {
		this.settings = settings;
	}
	
	protected final BufferedImage createThumbnail(BufferedImage image) {
		return ImageManipulator.getUniformScaledImage(
				image, 
				settings.width, 
				settings.height, 
				AffineTransformOp.TYPE_BICUBIC);
	}
	
	@Override
	public MediaThumbnailSettings getThumbnailSettings() {
		return settings;
	}
}
