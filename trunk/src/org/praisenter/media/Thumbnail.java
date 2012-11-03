package org.praisenter.media;

import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.xml.BufferedImageTypeAdapter;

/**
 * Represents a thumbnail of a {@link Media} item.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "Thumbnail")
public class Thumbnail implements Comparable<Thumbnail> {
	/** The file properties */
	@XmlElement(name = "FileProperties", required = true, nillable = false)
	protected FileProperties fileProperties;
	
	/** The media type */
	@XmlAttribute(name = "Type", required = true)
	protected MediaType type;
	
	/** The thumbnail image */
	@XmlElement(name = "Image", nillable = true, required = false)
	@XmlJavaTypeAdapter(value = BufferedImageTypeAdapter.class)
	protected BufferedImage image;
	
	/**
	 * Default constructor.
	 */
	protected Thumbnail() {}
	
	/**
	 * Full constructor.
	 * @param fileProperties the file properties
	 * @param type the media type
	 * @param image the thumbnail image
	 */
	public Thumbnail(FileProperties fileProperties, MediaType type, BufferedImage image) {
		super();
		this.fileProperties = fileProperties;
		this.type = type;
		this.image = image;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Thumbnail o) {
		// compare on file name
		return this.fileProperties.getFileName().compareTo(o.getFileProperties().getFileName());
	}
	
	/**
	 * Returns the media item's file properties.
	 * @return {@link FileProperties}
	 */
	public FileProperties getFileProperties() {
		return this.fileProperties;
	}
	
	/**
	 * Returns the media item's media type.
	 * @return {@link MediaType}
	 */
	public MediaType getType() {
		return this.type;
	}
	
	/**
	 * Returns the thumbnail of the media item.
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		return this.image;
	}
}
