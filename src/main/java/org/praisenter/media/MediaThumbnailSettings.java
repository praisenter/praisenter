package org.praisenter.media;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.praisenter.utility.ImageManipulator;

public final class MediaThumbnailSettings {
	final int width;
	final int height;
	
	final BufferedImage imageDefaultThumbnail;
	final BufferedImage audioDefaultThumbnail;
	final BufferedImage videoDefaultThumbnail;
	
	public MediaThumbnailSettings(
			int width, 
			int height,
			BufferedImage imageDefaultThumbnail, 
			BufferedImage audioDefaultThumbnail,
			BufferedImage videoDefaultThumbnail) {
		super();
		this.width = width;
		this.height = height;
		
		// make sure they are the right size
		this.imageDefaultThumbnail = ImageManipulator.getUniformScaledImage(imageDefaultThumbnail, width, height, AffineTransformOp.TYPE_BICUBIC);
		this.audioDefaultThumbnail = ImageManipulator.getUniformScaledImage(audioDefaultThumbnail, width, height, AffineTransformOp.TYPE_BICUBIC);
		this.videoDefaultThumbnail = ImageManipulator.getUniformScaledImage(videoDefaultThumbnail, width, height, AffineTransformOp.TYPE_BICUBIC);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public BufferedImage getImageDefaultThumbnail() {
		return imageDefaultThumbnail;
	}

	public BufferedImage getAudioDefaultThumbnail() {
		return audioDefaultThumbnail;
	}

	public BufferedImage getVideoDefaultThumbnail() {
		return videoDefaultThumbnail;
	}
}
