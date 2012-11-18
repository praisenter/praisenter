package org.praisenter.slide;

import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.xml.BufferedImageTypeAdapter;

/**
 * Represents a cached thumbnail on the file system.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "SlideThumbnail")
public class SlideThumbnail implements Comparable<SlideThumbnail> {
	/** The file properties */
	@XmlElement(name = "File", required = true, nillable = false)
	protected SlideFile file;
	
	/** The user designated name */
	@XmlElement(name = "Name")
	protected String name;
	
	/** The thumbnail image */
	@XmlElement(name = "Image", nillable = true, required = false)
	@XmlJavaTypeAdapter(value = BufferedImageTypeAdapter.class)
	protected BufferedImage image;
	
	/**
	 * Default constructor.
	 */
	protected SlideThumbnail() {}
	
	/**
	 * Full constructor.
	 * @param file the file information
	 * @param name the slide/template name
	 * @param image the thumbnail image
	 */
	public SlideThumbnail(SlideFile file, String name, BufferedImage image) {
		super();
		this.file = file;
		this.name = name;
		this.image = image;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SlideThumbnail o) {
		// compare on file name
		return this.file.getName().compareTo(o.getFile().getName());
	}
	
	/**
	 * Returns the media item's file information.
	 * @return {@link SlideFile}
	 */
	public SlideFile getFile() {
		return this.file;
	}
	
	/**
	 * Returns the thumbnail of the media item.
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		return this.image;
	}
	
	/**
	 * Returns the slide/template name.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
}
