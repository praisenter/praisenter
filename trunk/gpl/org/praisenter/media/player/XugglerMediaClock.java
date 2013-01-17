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

import com.xuggle.xuggler.Global;

/**
 * Represents a clock used to synchronize play of media with the
 * timestamps of the media.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class XugglerMediaClock {
	/** The first time stamp in the media */
    protected long firstTimestamp;
    
    /** The clock start time in milliseconds */
    protected long startTime;

    /**
     * Default constructor.
     */
    public XugglerMediaClock() {
    	this.firstTimestamp = Global.NO_PTS;
    }
    
    /**
     * Resets the clock.
     */
    public void reset() {
        this.firstTimestamp = Global.NO_PTS;
    }

    /**
     * Returns the time to sleep in milliseconds.
     * @param timestamp the timestamp
     * @param isVideoStream true if the media we are sync-ing is a video stream
     * @return long
     */
    public long getSynchronizationTime(long timestamp, boolean isVideoStream) {
        if (this.firstTimestamp == Global.NO_PTS) {
        	// initialize the clock
        	this.firstTimestamp = timestamp;
        	this.startTime = System.currentTimeMillis();
        } else {
        	// compute the estimated sleep time for the current thread
            long systemClockCurrentTime = System.currentTimeMillis();
            long millisecondsClockTimeSinceStartofVideo = systemClockCurrentTime - startTime;
            long millisecondsStreamTimeSinceStartOfVideo = (long)Math.ceil((double)(timestamp - firstTimestamp) / 1000.0);
            // allow 50 milliseconds of tolerance
            final long millisecondsTolerance = isVideoStream ? 50 : 0;
            final long millisecondsToSleep = (millisecondsStreamTimeSinceStartOfVideo - (millisecondsClockTimeSinceStartofVideo + millisecondsTolerance));
            if (millisecondsToSleep > 0) {
            	return millisecondsToSleep;
            }
        }
        return 0;
    }
}
