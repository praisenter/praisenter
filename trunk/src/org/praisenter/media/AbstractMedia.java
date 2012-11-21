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
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof AbstractMedia) {
			AbstractMedia media = (AbstractMedia)obj;
			// their type and path must be equal
			if (media.type == this.type &&
				media.file.path.equals(this.file.path)) {
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
		return this.file.path.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Media[Type=").append(this.type)
		  .append("|Path=").append(this.file.path)
		  .append("]");
		return sb.toString();
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
