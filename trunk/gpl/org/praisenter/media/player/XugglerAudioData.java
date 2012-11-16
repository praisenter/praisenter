package org.praisenter.media.player;

/**
 * Xuggler timed audio data.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XugglerAudioData extends XugglerTimedData {
	/**
	 * Full constructor.
	 * @param timestamp the samples timestamp
	 * @param data the sample data
	 */
	public XugglerAudioData(long timestamp, byte[] data) {
		super(timestamp, data);
	}
}
