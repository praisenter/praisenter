package org.praisenter.ui.display;

import javafx.scene.image.WritableImage;

final class NDIVideoFrame implements Comparable<NDIVideoFrame> {
	private final WritableImage image;
	private final long frameNumber;
	
	public NDIVideoFrame(WritableImage image, long frameNumber) {
		super();
		this.image = image;
		this.frameNumber = frameNumber;
	}
	
	@Override
	public int compareTo(NDIVideoFrame o) {
		return Long.compare(this.frameNumber, o.frameNumber);
	}
	
	public WritableImage getImage() {
		return image;
	}
	
	public long getFrameNumber() {
		return frameNumber;
	}
	
}
