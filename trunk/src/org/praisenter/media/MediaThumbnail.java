package org.praisenter.media;

import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.xml.BufferedImageTypeAdapter;

/**
 * Represents a thumbnail for a {@link Media} item.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "MediaThumbnail")
public class MediaThumbnail implements Comparable<MediaThumbnail> {
	/** The file properties */
	@XmlElement(name = "File", required = true, nillable = false)
	protected MediaFile file;
	
	/** The thumbnail image */
	@XmlElement(name = "Image", nillable = true, required = false)
	@XmlJavaTypeAdapter(value = BufferedImageTypeAdapter.class)
	protected BufferedImage image;

	/** The media type */
	@XmlAttribute(name = "MediaType", required = true)
	protected MediaType mediaType;
	
	/**
	 * Default constructor.
	 */
	protected MediaThumbnail() {}
	
	/**
	 * Full constructor.
	 * @param file the file information
	 * @param image the thumbnail image
	 * @param mediaType the media type
	 */
	public MediaThumbnail(MediaFile file, BufferedImage image, MediaType mediaType) {
		super();
		this.file = file;
		this.image = image;
		this.mediaType = mediaType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MediaThumbnail o) {
		// compare on file name
		return this.file.getName().compareTo(o.getFile().getName());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof MediaThumbnail) {
			MediaThumbnail other = (MediaThumbnail)obj;
			if (other.mediaType == this.mediaType &&
				other.file.equals(this.file)) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.file.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MediaThumbnail[Type=").append(this.mediaType)
		  .append("|File=").append(this.file)
		  .append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the media type.
	 * @return {@link MediaType}
	 */
	public MediaType getMediaType() {
		return this.mediaType;
	}

	/**
	 * Returns the media item's file information.
	 * @return {@link MediaFile}
	 */
	public MediaFile getFile() {
		return this.file;
	}
	
	/**
	 * Returns the thumbnail of the media item.
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		return this.image;
	}
}
