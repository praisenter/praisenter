package org.praisenter.data.song.ui;

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
 * @version 1.0.0
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
					Callback callback = search.getCallback();
					
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
	
	/**
	 * Represents a snip-it of code to be run after a song search has completed.
	 * <p>
	 * This should be used to update the UI.  This will always be run on the EDT.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static abstract class Callback implements Runnable {
		/** The search */
		private SongSearch search;
		
		/** The search result */
		private List<Song> result;
		
		/** The exception */
		private Exception exception;

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
}
