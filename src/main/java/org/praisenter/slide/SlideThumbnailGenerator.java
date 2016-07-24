package org.praisenter.slide;

import java.awt.image.BufferedImage;

public abstract class SlideThumbnailGenerator {
	public <T extends Slide> BufferedImage generate(T slide) {
		return null;
	}
}
