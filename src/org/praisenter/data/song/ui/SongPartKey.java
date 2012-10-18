package org.praisenter.data.song.ui;

import org.praisenter.data.song.SongPartType;

/**
 * Map key for a quick send button.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongPartKey implements Comparable<SongPartKey> {
	/** The song part type */
	protected SongPartType type;
	
	/** The song part index */
	protected int index;
	
	/**
	 * Full constructor.
	 * @param type the song part type
	 * @param index the song part index
	 */
	public SongPartKey(SongPartType type, int index) {
		this.type = type;
		this.index = index;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = this.type.hashCode();
		result = 37 * result + this.index;
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SongPartKey) {
			SongPartKey key = (SongPartKey)obj;
			if (key.type == this.type && key.index == this.index) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("QuickSendButtonKey[Type=").append(this.type)
		  .append("|Index=").append(this.index)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SongPartKey o) {
		int diff = this.type.compareTo(o.type);
		if (diff == 0) {
			diff = this.index - o.index;
		}
		return diff;
	}

	/**
	 * Returns the key's song part type.
	 * @return {@link SongPartType}
	 */
	public SongPartType getType() {
		return this.type;
	}

	/**
	 * Returns the key's song part index.
	 * @return int
	 */
	public int getIndex() {
		return this.index;
	}
}
