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
package org.praisenter.media;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import org.praisenter.common.utilities.ImageUtilities;

/**
 * Represents an audio media type using the Xuggler library.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class XugglerAudioMedia extends AbstractAudioMedia implements XugglerPlayableMedia, PlayableMedia, Media, Serializable {
	/** The version id */
	private static final long serialVersionUID = 9254649876754018L;

	/** Sampled audio icon */
	public static final BufferedImage SAMPLED_AUDIO = ImageUtilities.getImageFromClassPathSuppressExceptions("/org/praisenter/media/resources/sampled-audio.png");
	
	/**
	 * Full constructor.
	 * @param file the file information
	 */
	public XugglerAudioMedia(AudioMediaFile file) {
		super(file);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getThumbnail(java.awt.Dimension)
	 */
	@Override
	public MediaThumbnail getThumbnail(Dimension size) {
		return new MediaThumbnail(this.file, SAMPLED_AUDIO, MediaType.AUDIO);
	}
}
