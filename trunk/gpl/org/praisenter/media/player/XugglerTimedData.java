package org.praisenter.media.player;

/**
 * Represents timed media stream data.
 * @param <E> The data type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XugglerTimedData<E> implements Comparable<XugglerTimedData<?>> {
	/** The data time stamp */
	protected long timestamp;
	
	/** The data */
	protected E data;
	
	/**
	 * Full constructor.
	 * @param timestamp the timestamp
	 * @param data the data
	 */
	public XugglerTimedData(long timestamp, E data) {
		this.timestamp = timestamp;
		this.data = data;
	}

	/**
	 * Returns the time stamp.
	 * @return long
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Returns the data.
	 * @return E
	 */
	public E getData() {
		return this.data;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(XugglerTimedData<?> o) {
		long diff = this.timestamp - o.timestamp;
		if (diff > 0) {
			return 1;
		} else if (diff < 0) {
			return -1;
		} else {
			return 0;
		}
	}
}
