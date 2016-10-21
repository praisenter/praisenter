package org.praisenter.javafx.media;

import org.praisenter.media.MediaThumbnailSettings;

import javafx.scene.image.Image;

final class DefaultThumbnails {

	/** The default image thumbnail */
	private final Image defaultImageThumbnail;
	
	/** The default video thumbnail */
	private final Image defaultVideoThumbnail;
	
	/** The default audio thumbnail */
	private final Image defaultAudioThumbnail;
	
	/**
	 * Creates a new cell factory for media items.
	 * @param settings the thumbnail settings
	 */
	public DefaultThumbnails(MediaThumbnailSettings settings) {
		int w = settings.getWidth();
		int h = settings.getHeight();
		this.defaultImageThumbnail = new Image("/org/praisenter/resources/image-default-thumbnail.png", w, h, true, true, false);
		this.defaultVideoThumbnail = new Image("/org/praisenter/resources/video-default-thumbnail.png", w, h, true, true, false);
		this.defaultAudioThumbnail = new Image("/org/praisenter/resources/music-default-thumbnail.png", w, h, true, true, false);
	}
	
	public Image getDefaultImageThumbnail() {
		return this.defaultImageThumbnail;
	}
	
	public Image getDefaultVideoThumbnail() {
		return this.defaultVideoThumbnail;
	}
	
	public Image getDefaultAudioThumbnail() {
		return this.defaultAudioThumbnail;
	}
}
