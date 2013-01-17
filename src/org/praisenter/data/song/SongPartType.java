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
package org.praisenter.data.song;

import org.praisenter.resources.Messages;

/**
 * Enumeration of the song part types.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum SongPartType {
	/** Verse part */
	VERSE(Messages.getString("song.part.type.verse")),
	
	/** Chorus part */
	CHORUS(Messages.getString("song.part.type.chorus")),
	
	/** Bridge part */
	BRIDGE(Messages.getString("song.part.type.bridge")),

	/** Tag part */
	TAG(Messages.getString("song.part.type.tag")),

	/** Vamp part */
	VAMP(Messages.getString("song.part.type.vamp")),
	
	/** End part */
	END(Messages.getString("song.part.type.end")),
	
	/** Other part */
	OTHER(Messages.getString("song.part.type.other"));
	
	/** The user friendly part name */
	private String name;
	
	/**
	 * Minimal constructor.
	 * @param name the user friendly part name
	 */
	private SongPartType(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the user friendly part name.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
}
