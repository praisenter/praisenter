package org.praisenter.slide;

import java.awt.image.BufferedImage;

public interface SlideThumbnailGenerator {
	public <T extends Slide> BufferedImage generate(T slide);
}
