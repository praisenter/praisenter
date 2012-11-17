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
	
	/** The file information */
	protected MediaFile file;
	
	/**
	 * Minimal constructor.
	 * @param file the file information
	 * @param type the media type
	 */
	public AbstractMedia(MediaFile file, MediaType type) {
		this.type = type;
		this.file = file;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getType()
	 */
	@Override
	public MediaType getType() {
		return this.type;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getFile()
	 */
	@Override
	public MediaFile getFile() {
		return this.file;
	}
}
