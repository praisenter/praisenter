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

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IAudioSamples.Format;

/**
 * Xuggler timed audio data.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public class XugglerAudioData extends XugglerTimedData {
	/** Specifies no audio sample conversion required */
	public static final int CONVERSION_NONE = 0;
	
	/** Specifies an audio sample conversion from more than 2 channels down to stereo (2 channels) */
	public static final int CONVERSION_TO_STEREO = 1;
	
	/** Specifies an audio sample conversion from a bit depth higher than 16 to 16 bit integer */
	public static final int CONVERSION_TO_BIT_DEPTH_16 = 2;
	
	/**
	 * Full constructor.
	 * @param timestamp the samples timestamp
	 * @param data the sample data
	 */
	public XugglerAudioData(long timestamp, byte[] data) {
		super(timestamp, data);
	}
	
	/**
	 * Returns the class type for the given format.
	 * @param format the format
	 * @return Class&lt;Number&gt;
	 * @since 2.0.1
	 */
	public static final Class<? extends Number> getDataTypeForFormat(IAudioSamples.Format format) {
		if (format == Format.FMT_DBL || format == Format.FMT_DBLP) {
			return Double.class;
		} else if (format == Format.FMT_FLT || format == Format.FMT_FLTP) {
			return Float.class;
		} else if (format == Format.FMT_S16 || format == Format.FMT_S16P) {
			return Short.class;
		} else if (format == Format.FMT_S32 || format == Format.FMT_S32P) {
			return Integer.class;
		} else if (format == Format.FMT_U8 || format == Format.FMT_U8P) {
			return Byte.class;
		}
		return null;
	}
}
