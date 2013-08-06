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

import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerFactory;
import org.praisenter.media.PlayableMedia;
import org.praisenter.media.XugglerPlayableMedia;

/**
 * Factory class for creating media player's for {@link XugglerPlayableMedia} media.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class XugglerMediaPlayerFactory implements MediaPlayerFactory<XugglerPlayableMedia> {
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#isTypeSupported(java.lang.Class)
	 */
	@Override
	public <T extends PlayableMedia> boolean isTypeSupported(Class<T> clazz) {
		if (XugglerPlayableMedia.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayerFactory#createMediaPlayer()
	 */
	@Override
	public MediaPlayer<XugglerPlayableMedia> createMediaPlayer() {
		return new XugglerMediaPlayer();
	}
}
