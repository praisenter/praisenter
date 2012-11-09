package org.praisenter.media;

import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.xml.FileProperties;
import org.praisenter.xml.Thumbnail;

/**
 * Represents a thumbnail for a {@link Media} item.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "MediaThumbnail")
public class MediaThumbnail extends Thumbnail {
	/** The media type */
	@XmlAttribute(name = "MediaType", required = true)
	protected MediaType mediaType;
	
	/**
	 * Default constructor.
	 */
	protected MediaThumbnail() {}
	
	/**
	 * Full constructor.
	 * @param fileProperties the file properties
	 * @param image the thumbnail image
	 * @param mediaType the media type
	 */
	public MediaThumbnail(FileProperties fileProperties, BufferedImage image, MediaType mediaType) {
		super();
		this.fileProperties = fileProperties;
		this.image = image;
		this.mediaType = mediaType;
	}
	
	/**
	 * Returns the media type.
	 * @return {@link MediaType}
	 */
	public MediaType getMediaType() {
		return this.mediaType;
	}
}
