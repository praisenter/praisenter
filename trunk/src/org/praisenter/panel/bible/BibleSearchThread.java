package org.praisenter.panel.bible;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.praisenter.data.DataException;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.BibleSearchType;
import org.praisenter.data.bible.Bibles;
import org.praisenter.data.bible.Verse;

/**
 * Thread used to search the bible.
 * <p>
 * Searching the bible can be a time consuming process and is best done on a separate thread
 * from the EDT.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BibleSearchThread extends Thread {
	/** The static logger */
	private static final Logger LOGGER = Logger.getLogger(BibleSearchThread.class);
	
	/** The blocking queue */
	private final BlockingQueue<BibleSearch> searchQueue = new ArrayBlockingQueue<BibleSearch>(10);
	
	/**
	 * Default constructor.
	 */
	public BibleSearchThread() {
		super("BibleSearchThread");
		// this thread should not stop shutdown
		this.setDaemon(true);
	}
	
	/**
	 * Queues a new search to be performed.
	 * @param search the search
	 */
	public void queueSearch(BibleSearch search) {
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
				BibleSearch search = this.searchQueue.poll(1000, TimeUnit.MILLISECONDS);
				// if no queued search then just continue
				if (search != null) {
					// if we have a queued search then execute it
					Bible bible = search.getBible();
					String text = search.getText();
					boolean ia = search.isApocryphaIncluded();
					BibleSearchType type = search.getType();
					Callback callback = search.getCallback();
					
					// assign the search
					callback.search = search;
					
					// get the verses
					List<Verse> verses = null;
					try {
						// search the verses
						verses = Bibles.searchVerses(bible, text, type, ia);
//						verses = bible.searchVerses(text, type);
//						verses = bible.searchVerses(1, text);
//						verses = bible.searchVerses(text, BibleSearchType.ANY_WORD);
//						verses = bible.searchVerses(testament, text);
//						verses = bible.searchVerses(bookId, chapter, text);
//						verses = bible.searchVerses(1, text, BibleSearchType.);
//						verses = bible.searchVerses(Testament.OLD, text, BibleSearchType.PHRASE);
//						verses = bible.searchVerses(1, 1, text, BibleSearchType.PHRASE);
//						verses = bible.searchVersesByLocation(text);
//						verses = bible.searchBooks(text);
						// assign the verses
						callback.result = verses;
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
						LOGGER.error("An error occurred while invoking the bible search callback: ", ex);
					} catch (InterruptedException ex) {
						// if the callback gets interrupted then just ignore it...
						LOGGER.error("The bible search callback invokation was interrupted: ", ex);
					}
				}
			} catch (InterruptedException ex) {
				// if the bible search thread is interupted then just stop it
				LOGGER.info("BibleSearchThread was interrupted. Stopping thread.", ex);
				break;
			}
		}
	}
	
	/**
	 * Represents a snip-it of code to be run after a bible search has completed.
	 * <p>
	 * This should be used to update the UI.  This will always be run on the EDT.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static abstract class Callback implements Runnable {
		/** The search */
		private BibleSearch search;
		
		/** The search result */
		private List<Verse> result;
		
		/** The exception */
		private Exception exception;

		/**
		 * The search performed.
		 * @return {@link BibleSearch}
		 */
		public BibleSearch getSearch() {
			return this.search;
		}
		
		/**
		 * The search result.
		 * @return List&lt;{@link Verse}&gt;
		 */
		public List<Verse> getResult() {
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
