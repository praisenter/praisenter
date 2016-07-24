package org.praisenter.javafx.slide;

import org.praisenter.javafx.ImageCache;
import org.praisenter.media.MediaLibrary;

public final class ObservableSlideContext {
	final MediaLibrary mediaLibrary;
	final ImageCache imageCache;
	
	public ObservableSlideContext(MediaLibrary mediaLibrary, ImageCache imageCache) {
		this.mediaLibrary = mediaLibrary;
		this.imageCache = imageCache;
	}
}
