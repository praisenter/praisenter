package org.praisenter.media.player;

import java.awt.image.BufferedImage;

/**
 * Xuggler timed video data.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XugglerVideoData extends XugglerTimedData {
	/**
	 * Full constructor.
	 * @param timestamp the video timestamp
	 * @param data the video data
	 */
	public XugglerVideoData(long timestamp, BufferedImage data) {
		super(timestamp, data);
	}
}
