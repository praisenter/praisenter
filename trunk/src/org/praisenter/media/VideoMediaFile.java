package org.praisenter.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents general file properties of an image file.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "ImageMediaFile")
@XmlAccessorType(XmlAccessType.NONE)
public class VideoMediaFile extends MediaFile {
	/** The image width */
	@XmlAttribute(name = "Width")
	protected int width;
	
	/** The image height */
	@XmlAttribute(name = "Height")
	protected int height;
	
	protected long length;
	
	protected boolean audioPresent;
	
	/**
	 * Default constructor.
	 * <p>
	 * Should only be used by JAXB.
	 */
	protected VideoMediaFile() {}
	
	/**
	 * Full constructor.
	 * @param filePath the file name and path
	 * @param format the file format
	 * @param width the image width
	 * @param height the image height
	 */
	public VideoMediaFile(String filePath, String format, int width, int height) {
		super(filePath, format);
		this.width = width;
		this.height = height;
	}

	/**
	 * Returns the width of the image.
	 * @return int
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns the height of the image.
	 * @return int
	 */
	public int getHeight() {
		return this.height;
	}
}
