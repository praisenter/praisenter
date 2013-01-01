package org.praisenter.slide;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.resources.Messages;

/**
 * Represents a resolution.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "Resolution")
@XmlAccessorType(XmlAccessType.NONE)
public class Resolution implements Comparable<Resolution> {
	/** The width in pixels */
	@XmlAttribute(name = "Width")
	protected int width;
	
	/** The height in pixels */
	@XmlAttribute(name = "Height")
	protected int height;
	
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should be used with JAXB only.
	 */
	protected Resolution() {}
	
	/**
	 * Creates a new resolution with the given height and width.
	 * @param width the width
	 * @param height the height
	 */
	public Resolution(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Resolution) {
			Resolution r = (Resolution)obj;
			if (r.width == this.width && r.height == this.height) {
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
		int hash = 31;
		hash = hash * 39 + this.width;
		hash = hash * 39 + this.height;
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return MessageFormat.format(Messages.getString("resolution.format"), this.width, this.height);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Resolution o) {
		if (o == null) return 1;
		// order by width first
		int diff = this.width - o.width;
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		} else {
			// order by height next
			diff = this.height - o.height;
			if (diff < 0) {
				return -1;
			} else if (diff > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * Returns the width of the resolution in pixels.
	 * @return int
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the height of the resolution in pixels.
	 * @return int
	 */
	public int getHeight() {
		return this.height;
	}
}
