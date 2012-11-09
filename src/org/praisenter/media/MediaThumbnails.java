package org.praisenter.media;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a list of thumbnails for a thumbnail file.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "MediaThumbnails")
public class MediaThumbnails {
	/** The thumbnails */
	@XmlElement(name = "MediaThumbnail", required = true, nillable = false)
	protected List<MediaThumbnail> thumbnails;
	
	/** Default constructor */
	protected MediaThumbnails() {}
	
	/**
	 * Minimal constructor.
	 * @param thumbnails the thumbnails
	 */
	public MediaThumbnails(List<MediaThumbnail> thumbnails) {
		this.thumbnails = thumbnails;
	}
	
	/**
	 * Returns the thumbnails.
	 * @return List&lt;{@link MediaThumbnail}&gt;
	 */
	public List<MediaThumbnail> getThumbnails() {
		return thumbnails;
	}
}
