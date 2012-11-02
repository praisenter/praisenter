package org.praisenter.media;

/**
 * Abstract implementation of the {@link Media} interface.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractMedia implements Media {
	/** The media type */
	protected MediaType type;
	
	/** The file name */
	protected String fileName;
	
	/**
	 * Minimal constructor.
	 * @param type the media type
	 */
	public AbstractMedia(MediaType type) {
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getType()
	 */
	public MediaType getType() {
		return this.type;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getFileName()
	 */
	public String getFileName() {
		return this.fileName;
	}
}
