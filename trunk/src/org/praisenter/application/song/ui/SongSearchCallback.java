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

import java.util.List;

import org.praisenter.data.song.Song;

/**
 * Represents a snip-it of code to be run after a song search has completed.
 * <p>
 * This should be used to update the UI.  This will always be run on the EDT.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class SongSearchCallback implements Runnable {
	/** The search */
	protected SongSearch search;
	
	/** The search result */
	protected List<Song> result;
	
	/** The exception */
	protected Exception exception;

	/**
	 * The search performed.
	 * @return {@link SongSearch}
	 */
	public SongSearch getSearch() {
		return this.search;
	}
	
	/**
	 * The search result.
	 * @return List&lt;{@link Song}&gt;
	 */
	public List<Song> getResult() {
		return this.result;
	}
	
	/**
	 * The exception.
	 * <p>
	 * This will be null if the search was successful.
	 * @return Exception
	 */
	public Exception getException() {
		return this.exception;
	}
}
