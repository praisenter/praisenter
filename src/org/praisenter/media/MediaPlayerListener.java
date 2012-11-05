package org.praisenter.media;

import java.awt.image.BufferedImage;

public interface MediaPlayerListener {
	public void started();
	public void paused();
	public void stopped();
	public void seeked();
	
	public void updated(BufferedImage image);
}
