package org.praisenter.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents general file properties of an audio file.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "AudioMediaFile")
@XmlAccessorType(XmlAccessType.NONE)
public class AudioMediaFile extends MediaFile {
	/** The video length in seconds */
	@XmlAttribute(name = "Length")
	protected long length;
	
	/**
	 * Default constructor.
	 * <p>
	 * Should only be used by JAXB.
	 */
	protected AudioMediaFile() {}
	
	/**
	 * Full constructor.
	 * @param filePath the file name and path
	 * @param format the file format
	 * @param length the audio length in seconds
	 */
	public AudioMediaFile(String filePath, String format, long length) {
		super(filePath, format);
		this.length = length;
	}
	
	/**
	 * Returns the length of this audio in seconds
	 * @return long
	 */
	public long getLength() {
		return this.length;
	}
}
