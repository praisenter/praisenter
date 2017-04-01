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
package org.praisenter.javafx.bible;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.bible.BibleSearchCriteria;
import org.praisenter.bible.BibleSearchResult;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.async.AsyncTaskFactory;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents an Observable wrapper to the bible library.
 * <p>
 * This wrapper allows Java FX controls to bind to the list of bible items
 * to allow updating of the view from wherever the view is changed.
 * <p>
 * NOTE: modifications to the {@link #getItems()} and success and error handlers
 * will always be performed on the FX UI thread.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class ObservableBibleLibrary {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The bible library */
	private final BibleLibrary library;
	
	/** The observable list of bibles */
	private final ObservableList<BibleListItem> items;
	
	/**
	 * Minimal constructor.
	 * @param library the bible library
	 */
	public ObservableBibleLibrary(BibleLibrary library) {
		this.library = library;
		
		this.items = FXCollections.observableArrayList((bli) -> {
			// supply the list of properties that should trigger the list to do notification
			return new Observable[] {
				bli.bibleProperty(),
				bli.loadedProperty(),
				bli.nameProperty()
			};
		});

		List<Bible> bibles = null;
		if (library != null) {
			try {
				bibles = library.all();
			} catch (Exception ex) {
				LOGGER.error("Failed to load bibles.", ex);
			}
		}
		
		if (bibles != null) {
			// add all the current bibles to the observable list
			for (Bible bible : bibles) {
				this.items.add(new BibleListItem(bible));
	        }
		}
	}

	/**
	 * Returns the bible list item for the given bible or null if not found.
	 * @param bible the bible
	 * @return {@link BibleListItem}
	 */
	private BibleListItem getListItem(Bible bible) {
		if (bible == null) return null;
		BibleListItem bi = null;
    	for (int i = 0; i < this.items.size(); i++) {
    		BibleListItem item = this.items.get(i);
    		if (item != null &&
    			item.isLoaded() &&
    			item.getBible() != null &&
    			item.getBible().getId().equals(bible.getId())) {
    			bi = item;
    			break;
    		}
    	}
    	return bi;
	}
	
	/**
	 * Attempts to add the given path to the bible library.
	 * @param path the path to the bible
	 * @return {@link AsyncTask}&lt;List&lt;{@link Bible}&gt;&gt;
	 */
	public AsyncTask<List<Bible>> add(Path path) {
		if (path != null) {
			// create a "loading" item
			final BibleListItem loading = new BibleListItem(path.getFileName().toString());
			
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				// add it to the items list
				items.add(loading);
			});
			
			// execute the add on a different thread
			AsyncTask<List<Bible>> task = new AsyncTask<List<Bible>>(MessageFormat.format(Translations.get("task.import"), path.getFileName())) {
				@Override
				protected List<Bible> call() throws Exception {
					updateProgress(-1, 0);
					return library.importBibles(path);
				}
			};
			task.setOnSucceeded((e) -> {
				List<Bible> bibles = task.getValue();
				items.remove(loading);
				for (Bible bible : bibles) {
					items.add(new BibleListItem(bible));
				}
			});
			task.setOnFailed((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to import bible " + path.toAbsolutePath().toString(), ex);
				items.remove(loading);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}
	
	/**
	 * Attempts to save the given bible in this bible library.
	 * @param bible the bible
	 * @return {@link AsyncTask}&lt;{@link Bible}&gt;
	 */
	public AsyncTask<Bible> save(Bible bible) {
		return this.save(MessageFormat.format(Translations.get("task.save"), bible.getName()), bible);
	}
	
	/**
	 * Attempts to save the given bible in this bible library.
	 * @param action a simple string describing the save action if it's something more specific than "Save"
	 * @param bible the bible
	 * @return {@link AsyncTask}&lt;{@link Bible}&gt;
	 */
	public AsyncTask<Bible> save(String action, Bible bible) {
		if (bible != null) {
			// synchronously make a copy
			final Bible copy = bible.copy(true);
			// execute the add on a different thread
			AsyncTask<Bible> task = new AsyncTask<Bible>(action) {
				@Override
				protected Bible call() throws Exception {
					this.updateProgress(-1, 0);
					library.save(copy);
					return copy;
				}
			};
			task.setOnSucceeded((e) -> {
				// find the bible item
				BibleListItem bi = this.getListItem(bible);
				// check if new
				if (bi != null) {
					bi.setBible(copy);
					bi.setName(copy.getName());
				} else {
					this.items.add(new BibleListItem(copy));
				}
			});
			task.setOnFailed((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to save bible " + copy.getName(), ex);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}
	
	/**
	 * Attempts to remove the given bible from the bible library.
	 * @param bible the bible to remove
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> remove(Bible bible) {
		if (bible != null) {
			final BibleListItem bi = this.getListItem(bible);
			
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				// go ahead and remove it
				items.remove(bi);
			});
			
			// execute the add on a different thread
			AsyncTask<Void> task = new AsyncTask<Void>(MessageFormat.format(Translations.get("task.delete"), bible.getName())) {
				@Override
				protected Void call() throws Exception {
					this.updateProgress(-1, 0);
					library.remove(bible);
					return null;
				}
			};
			task.setOnFailed((e) -> {
				Throwable ex = task.getException();
				LOGGER.error("Failed to remove bible " + bible.getName(), ex);
				// add the item back
				items.add(bi);
			});
			return task;
		}
		return AsyncTaskFactory.single();
	}

	/**
	 * Exports the given bibles to the given file.
	 * @param path the file
	 * @param bibles the bibles to export
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> exportBibles(Path path, List<Bible> bibles) {
		AsyncTask<Void> task = new AsyncTask<Void>(MessageFormat.format(Translations.get("task.export"), path.getFileName())) {
			@Override
			protected Void call() throws Exception {
				this.updateProgress(-1, 0);
				library.exportBibles(path, bibles);
				return null;
			}
		};
		return task;
	}

	/**
	 * Exports the given bibles to the given file.
	 * @param stream the zip output stream
	 * @param fileName the file name to export to
	 * @param bibles the bibles to export
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> exportBibles(ZipOutputStream stream, String fileName, List<Bible> bibles) {
		AsyncTask<Void> task = new AsyncTask<Void>(MessageFormat.format(Translations.get("task.export"), fileName)) {
			@Override
			protected Void call() throws Exception {
				this.updateProgress(-1, 0);
				library.exportBibles(stream, bibles);
				return null;
			}
		};
		return task;
	}
	
	// searching
	
	/**
	 * Attempts to rebuild the bible searching index.
	 * <p>
	 * Re-indexing the bible library can take a significant amount of time.
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> reindex() {
		AsyncTask<Void> task = new AsyncTask<Void>(Translations.get("bible.reindex")) {
			@Override
			protected Void call() throws Exception {
				updateProgress(-1, 0);
				library.reindex();
				return null;
			}
		};
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to reindex bible library: ", ex);
		});
		return task;
	}
	
	/**
	 * Searches the given bible for the given text using the given search type.
	 * @param criteria the search criteria
	 * @return {@link AsyncTask}&lt;List&lt;{@link BibleSearchResult}&gt;&gt;
	 */
	public AsyncTask<List<BibleSearchResult>> search(BibleSearchCriteria criteria) {
		AsyncTask<List<BibleSearchResult>> task = new AsyncTask<List<BibleSearchResult>>() {
			@Override
			protected List<BibleSearchResult> call() throws Exception {
				updateProgress(-1, 0);
				return library.search(criteria);
			}
		};
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to search bible: " + criteria, ex);
		});
		return task;
	}

	// other

	/**
	 * Returns the bible for the given id.
	 * @param id the id
	 * @return {@link Bible}
	 */
	public Bible get(UUID id) {
		return this.library.get(id);
	}
	
	/**
	 * Returns the bible for the given id.
	 * @param id the id
	 * @return {@link BibleListItem}
	 */
	BibleListItem getListItem(UUID id) {
		Bible bible = this.library.get(id);
		return this.getListItem(bible);
	}
	
	/**
	 * Returns the observable list of bibles.
	 * @return ObservableList&lt;{@link BibleListItem}&gt;
	 */
	ObservableList<BibleListItem> getItems() {
		return this.items;
	}
}
