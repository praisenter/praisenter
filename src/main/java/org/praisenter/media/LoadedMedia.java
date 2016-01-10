package org.praisenter.media;

import java.awt.image.BufferedImage;

public final class LoadedMedia {
	final Media media;
	final BufferedImage image;
	
	public LoadedMedia(Media media, BufferedImage image) {
		this.media = media;
		this.image = image;
	}

	public Media getMedia() {
		return media;
	}
	
	public BufferedImage getImage() {
		return image;
	}
}
