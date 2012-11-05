package org.praisenter.media;

import com.xuggle.xuggler.Global;

/**
 * Represents a clock used to synchronize play of media with the
 * timestamps of the media.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
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
            long millisecondsStreamTimeSinceStartOfVideo = (timestamp - firstTimestamp) / 1000;
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
