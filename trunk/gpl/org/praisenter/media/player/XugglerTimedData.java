/*
 * Praisenter: A free open source church presentation software.
 * Copyright (C) 2012-2013  William Bittle  http://www.praisenter.org/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.praisenter.media.player;

/**
 * Represents timed media stream data.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class XugglerTimedData implements Comparable<XugglerTimedData> {
	/** The data time stamp */
	protected long timestamp;
	
	/** The data */
	protected Object data;
	
	/**
	 * Full constructor.
	 * @param timestamp the timestamp
	 * @param data the data
	 */
	protected XugglerTimedData(long timestamp, Object data) {
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
	public Object getData() {
		return this.data;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// base equals off of time stamp
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof XugglerTimedData) {
			XugglerTimedData data = (XugglerTimedData)obj;
			return data.timestamp == this.timestamp;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (int)(this.timestamp);
		hash = hash * 31 + (int)(this.timestamp >> 8);
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("XugglerTimedData[Timestamp=")
		  .append(this.timestamp)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(XugglerTimedData o) {
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
