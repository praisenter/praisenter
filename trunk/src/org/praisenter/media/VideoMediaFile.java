package org.praisenter.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents general file properties of an video file.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "VideoMediaFile")
@XmlAccessorType(XmlAccessType.NONE)
public class VideoMediaFile extends MediaFile {
	/** The image width */
	@XmlAttribute(name = "Width")
	protected int width;
	
	/** The image height */
	@XmlAttribute(name = "Height")
	protected int height;
	
	/** The video length in seconds */
	@XmlAttribute(name = "Length")
	protected long length;
	
	/** True if audio is present in the video */
	@XmlAttribute(name = "AudioPresent")
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
	 * @param length the video length in seconds
	 * @param audioPresent true if the video contains audio
	 */
	public VideoMediaFile(
			String filePath, String format, 
			int width, int height,
			long length, boolean audioPresent) {
		super(filePath, format);
		this.width = width;
		this.height = height;
		this.length = length;
		this.audioPresent = audioPresent;
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

	/**
	 * Returns the length of this video in seconds
	 * @return long
	 */
	public long getLength() {
		return this.length;
	}

	/**
	 * Returns true if this video contains audio.
	 * @return boolean
	 */
	public boolean isAudioPresent() {
		return this.audioPresent;
	}
}
