package org.praisenter.slide;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a list of thumbnails for a thumbnail file.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "SlideThumbnails")
@XmlAccessorType(XmlAccessType.NONE)
public class SlideThumbnails {
	/** The thumbnails */
	@XmlElement(name = "Thumbnail", required = true, nillable = false)
	protected List<SlideThumbnail> thumbnails;
	
	/** Default constructor */
	protected SlideThumbnails() {}
	
	/**
	 * Minimal constructor.
	 * @param thumbnails the thumbnails
	 */
	public SlideThumbnails(List<SlideThumbnail> thumbnails) {
		this.thumbnails = thumbnails;
	}
	
	/**
	 * Returns the thumbnails.
	 * @return List&lt;{@link SlideThumbnail}&gt;
	 */
	public List<SlideThumbnail> getThumbnails() {
		return thumbnails;
	}
}
