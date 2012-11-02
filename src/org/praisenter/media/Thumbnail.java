package org.praisenter.media;

import java.awt.image.BufferedImage;

public class Thumbnail {
	protected MediaType type;
	protected String fileName;
	protected BufferedImage image;
	
	public Thumbnail(MediaType type, String fileName, BufferedImage image) {
		super();
		this.type = type;
		this.fileName = fileName;
		this.image = image;
	}

	public MediaType getType() {
		return type;
	}

	public String getFileName() {
		return fileName;
	}

	public BufferedImage getImage() {
		return image;
	}
}
