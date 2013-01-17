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
package org.praisenter.media.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;
import org.praisenter.threading.AbstractTask;


/**
 * Custom task for adding media to the media library.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class AddMediaTask extends AbstractTask {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(AddMediaTask.class);
	
	/** The file system paths */
	private String[] paths;
	
	/** The loaded media */
	private List<Media> media;
	
	/** The failed paths */
	private List<String> failed;
	
	/**
	 * Minimal constructor.
	 * @param paths the file system paths
	 */
	public AddMediaTask(String[] paths) {
		this.paths = paths;
		this.media = new ArrayList<Media>();
		this.failed = new ArrayList<String>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		for (String path : this.paths) {
			try {
				this.media.add(MediaLibrary.addMedia(path));
				this.setSuccessful(true);
			} catch (Exception e) {
				this.failed.add(path);
				LOGGER.error("An error occurred while attempting to add [" + path + "] to the media library: ", e);
				this.handleException(e);
			}
		}
	}
	
	/**
	 * Returns a list of all the media file name/paths that
	 * failed to be imported.
	 * @return List&lt;String&gt;
	 */
	public List<String> getFailed() {
		return this.failed;
	}
	
	/**
	 * Returns the list of all the media that were successfully
	 * added to the media library.
	 * @return List&lt;{@link Media}&gt;
	 */
	public List<Media> getMedia() {
		return this.media;
	}
}