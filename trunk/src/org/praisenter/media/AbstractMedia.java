package org.praisenter.media;

import org.praisenter.xml.FileProperties;

/**
 * Abstract implementation of the {@link Media} interface.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractMedia implements Media {
	/** The media type */
	protected MediaType type;
	
	/** The file properties */
	protected FileProperties fileProperties;
	
	/**
	 * Minimal constructor.
	 * @param fileProperties the file properties
	 * @param type the media type
	 */
	public AbstractMedia(FileProperties fileProperties, MediaType type) {
		this.type = type;
		this.fileProperties = fileProperties;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getType()
	 */
	@Override
	public MediaType getType() {
		return this.type;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getFileProperties()
	 */
	@Override
	public FileProperties getFileProperties() {
		return this.fileProperties;
	}
}
