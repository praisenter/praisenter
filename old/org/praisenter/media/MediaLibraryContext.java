/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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
package org.praisenter.media;

import org.praisenter.ThumbnailSettings;
import org.praisenter.configuration.Configuration;
import org.praisenter.data.media.tools.MediaTools;

/**
 * Represents contextual information for the media library.
 * @author William Bittle
 * @version 3.0.0
 */
public final class MediaLibraryContext {
	/** The configuration */
	private final Configuration configuration;
	
	/** The thumbnail settings */
	private final ThumbnailSettings thumbnailSettings;
	
	/** The tools */
	private final MediaTools tools;
	
	/**
	 * Minimal constructor.
	 * @param configuration the configuration
	 * @param thumbnailSettings the thumbnail settings
	 * @param tools the tools
	 */
	public MediaLibraryContext(Configuration configuration, ThumbnailSettings thumbnailSettings, MediaTools tools) {
		this.configuration = configuration;
		this.thumbnailSettings = thumbnailSettings;
		this.tools = tools;
	}

	/**
	 * Returns the configuration.
	 * @return {@link Configuration}
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}

	/**
	 * Returns the thumbnail settings.
	 * @return {@link ThumbnailSettings}
	 */
	public ThumbnailSettings getThumbnailSettings() {
		return this.thumbnailSettings;
	}
	
	/**
	 * Returns the tools.
	 * @return {@link MediaTools}
	 */
	public MediaTools getTools() {
		return this.tools;
	}
}
