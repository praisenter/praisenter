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

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.praisenter.data.DataException;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.Songs;

/**
 * Thread used to search songs.
 * <p>
 * Searching the song data store can be a time consuming process and is best done on a separate thread
 * from the EDT.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public class SongSearchThread extends Thread {
	/** The static logger */
	private static final Logger LOGGER = Logger.getLogger(SongSearchThread.class);
	
	/** The blocking queue */
	private final BlockingQueue<SongSearch> searchQueue = new ArrayBlockingQueue<SongSearch>(10);
	
	/**
	 * Default constructor.
	 */
	public SongSearchThread() {
		super("SongSearchThread");
		// this thread should not stop shutdown
		this.setDaemon(true);
	}
	
	/**
	 * Queues a new search to be performed.
	 * @param search the search
	 */
	public void queueSearch(SongSearch search) {
		this.searchQueue.add(search);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// make the thread run always
		while (true) {
			try {
				// poll for any queued searches
				SongSearch search = this.searchQueue.poll(1000, TimeUnit.MILLISECONDS);
				// if no queued search then just continue
				if (search != null) {
					// if we have a queued search then execute it
					String text = search.getText();
					SongSearchCallback callback = search.getCallback();
					
					// assign the search
					callback.search = search;
					
					// get the matching songs
					List<Song> songs = null;
					try {
						// search the songs
						songs = Songs.searchSongsWithoutParts(text);
						// assign the songs
						callback.result = songs;
					} catch (DataException ex) {
						// assign the exception
						callback.exception = ex;
					}
					// attempt to call the callback
					try {
						// invoke the callback on the EDT
						SwingUtilities.invokeAndWait(callback);
					} catch (InvocationTargetException ex) {
						// this will happen if the callback throws an exception
						// the best we can do here is just log the error
						LOGGER.error("An error occurred while invoking the song search callback: ", ex);
					} catch (InterruptedException ex) {
						// if the callback gets interrupted then just ignore it...
						LOGGER.error("The song search callback invokation was interrupted: ", ex);
					}
				}
			} catch (InterruptedException ex) {
				// if the song search thread is interrupted then just stop it
				LOGGER.info("SongSearchThread was interrupted. Stopping thread.", ex);
				break;
			}
		}
	}
}
