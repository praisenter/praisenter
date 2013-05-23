/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.application.song.ui;

import java.text.MessageFormat;

import org.praisenter.application.resources.Messages;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.data.song.SongPartType;

/**
 * Small helper class for {@link Song}s and {@link SongPart}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class SongHelper {
	/** Hidden default constructor. */
	private SongHelper() {}
	
	/**
	 * Returns the name of the given part.
	 * @param part the part
	 * @return String
	 */
	public static final String getPartName(SongPart part) {
		return MessageFormat.format(Messages.getString("song.part.name.pattern"), getPartTypeName(part.getType()), part.getIndex());
	}
	
	/**
	 * Returns the name of a song part given the type and index.
	 * @param type the song part type
	 * @param index the song part index
	 * @return String
	 */
	public static final String getPartName(SongPartType type, int index) {
		return MessageFormat.format(Messages.getString("song.part.name.pattern"), getPartTypeName(type), index);
	}
	
	/**
	 * Returns the name of the given part type.
	 * @param type the part type
	 * @return String
	 */
	public static final String getPartTypeName(SongPartType type) {
		if (type == SongPartType.VERSE) {
			return Messages.getString("song.part.type.verse");
		} else if (type == SongPartType.PRECHORUS) {
			return Messages.getString("song.part.type.prechorus");
		} else if (type == SongPartType.CHORUS) {
			return Messages.getString("song.part.type.chorus");
		} else if (type == SongPartType.BRIDGE) {
			return Messages.getString("song.part.type.bridge");
		} else if (type == SongPartType.TAG) {
			return Messages.getString("song.part.type.tag");
		} else if (type == SongPartType.VAMP) {
			return Messages.getString("song.part.type.vamp");
		} else if (type == SongPartType.END) {
			return Messages.getString("song.part.type.end");
		} else {
			return Messages.getString("song.part.type.other");
		}
	}
}
