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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.FailedOperation;
import org.praisenter.SearchType;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleImporter;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.bible.BibleSearchResult;
import org.praisenter.bible.FormatIdentifingBibleImporter;
import org.praisenter.javafx.MonitoredTask;
import org.praisenter.javafx.MonitoredTaskResultStatus;
import org.praisenter.javafx.MonitoredThreadPoolExecutor;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

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
	
	/** The thread service */
	private final MonitoredThreadPoolExecutor service;
	
	/** The observable list of bibles */
	private final ObservableList<BibleListItem> items = FXCollections.observableArrayList();
	
	/**
	 * Minimal constructor.
	 * @param library the bible library
	 * @param service the thread service
	 */
	public ObservableBibleLibrary(BibleLibrary library, MonitoredThreadPoolExecutor service) {
		this.library = library;
		this.service = service;
		
		List<Bible> bibles = null;
		if (library != null) {
			try {
				bibles = library.all();
			} catch (Exception ex) {
				LOGGER.error("Failed to load bibles.", ex);
			}
		}
		
		if (bibles != null) {
			// add all the current media to the observable list
			for (Bible bible : bibles) {
				this.items.add(new BibleListItem(bible));
	        }
		}
	}

	/**
	 * Attempts to add the given path to the bible library.
	 * <p>
	 * The onSuccess method will be called when the import is successful. The
	 * onError method will be called if an error occurs during import.
	 * <p>
	 * An item is added to the observable list to represent that the bible is
	 * being imported. This item will be removed when the import completes, whether
	 * its successful or not.
	 * <p>
	 * The observable list of media will be updated if the bible is successfully
	 * added to the BibleLibrary. Both the update of the observable list and
	 * the onSuccess and onError methods will be performed on the Java FX UI
	 * thread.
	 * @param path the path to the bible
	 * @param onSuccess called when the bible is imported successfully
	 * @param onError called when the bible failed to be imported
	 */
	public void add(Path path, Consumer<List<Bible>> onSuccess, BiConsumer<Path, Throwable> onError) {
		// create a "loading" item
		final BibleListItem loading = new BibleListItem(path.getFileName().toString());
		
		// changes to the list should be done on the FX UI Thread
		Fx.runOnFxThead(() -> {
			// add it to the items list
			items.add(loading);
		});
		
		// execute the add on a different thread
		MonitoredTask<List<Bible>> task = new MonitoredTask<List<Bible>>(MessageFormat.format(Translations.get("task.import"), path.getFileName())) {
			@Override
			protected List<Bible> call() throws Exception {
				updateProgress(-1, 0);
				try {
					FormatIdentifingBibleImporter importer = new FormatIdentifingBibleImporter();
					List<Bible> bibles = importer.execute(path);
					for (Bible bible : bibles) {
						library.save(bible);
					}
					setResultStatus(MonitoredTaskResultStatus.SUCCESS);
					return bibles;
				} catch (Exception ex) {
					setResultStatus(MonitoredTaskResultStatus.ERROR);
					throw ex;
				}
			}
		};
		task.setOnSucceeded((e) -> {
			List<Bible> bibles = task.getValue();
			items.remove(loading);
			for (Bible bible : bibles) {
				items.add(new BibleListItem(bible));
			}
			if (onSuccess != null) {
				onSuccess.accept(bibles);
			}
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to import bible " + path.toAbsolutePath().toString(), ex);
			items.remove(loading);
			if (onError != null) {
				onError.accept(path, ex);
			}
		});
		this.service.execute(task);
	}
	
	/**
	 * Attempts to add the given paths to the bible library.
	 * <p>
	 * The onSuccess method will be called with all the successful imports. The
	 * onError method will be called with all the failed imports. The onSuccess
	 * and onError methods will only be called if there's at least one successful
	 * or one failed import respectively.
	 * <p>
	 * Items are added to the observable list to represent that the bible is
	 * being imported. These items will be removed when the import completes, whether
	 * they are successful or not.
	 * <p>
	 * The observable list of bibles will be updated if some bibles are successfully
	 * added to the BibleLibrary. Both the update of the observable list and
	 * the onSuccess and onError methods will be performed on the Java FX UI
	 * thread.
	 * @param paths the paths to the bibles
	 * @param onSuccess called with the bibles that were imported successfully
	 * @param onError called with the bibles that failed to be imported
	 */
	public void add(List<Path> paths, Consumer<List<Bible>> onSuccess, Consumer<List<FailedOperation<Path>>> onError) {
		// create the "loading" items
		Map<Path, BibleListItem> loadings = new HashMap<Path, BibleListItem>();
		for (Path path : paths) {
			loadings.put(path, new BibleListItem(path.getFileName().toString()));
		}
		
		// changes to the list should be done on the FX UI Thread
		Fx.runOnFxThead(() -> {
			// add it to the items list
			items.addAll(loadings.values());
		});
		
		List<Bible> successes = new ArrayList<Bible>();
		List<FailedOperation<Path>> failures = new ArrayList<FailedOperation<Path>>();
		
		BibleImporter importer = new FormatIdentifingBibleImporter();
		
		// execute the add on a different thread
		MonitoredTask<Void> task = new MonitoredTask<Void>(
				paths.size() > 1 
					? MessageFormat.format(Translations.get("bible.task.import"), paths.size())
					: MessageFormat.format(Translations.get("task.import"), paths.get(0).getFileName())) {
			@Override
			protected Void call() throws Exception {
				updateProgress(-1, 0);
				int errorCount = 0;
				for (Path path : paths) {
					BibleListItem loading = loadings.get(path);
					try {
						List<Bible> bbls = importer.execute(path);
						for (Bible bible : bbls) {
							library.save(bible);
						}
						successes.addAll(bbls);
						Fx.runOnFxThead(() -> {
							// remove the loading item
							items.remove(loading);
							// add the real item
							for (Bible bible : bbls) {
								items.add(new BibleListItem(bible));
							}
						});
					} catch (Exception ex) {
						LOGGER.error("Failed to import bible(s) " + path.toAbsolutePath().toString(), ex);
						failures.add(new FailedOperation<Path>(path, ex));
						Fx.runOnFxThead(() -> {
							// remove the loading item
							items.remove(loading);
						});
						errorCount++;
					}
				}
				
				// set the result status based on the number of errors we got
				if (errorCount == 0) {
					this.setResultStatus(MonitoredTaskResultStatus.SUCCESS);
				} else if (errorCount == paths.size()) {
					this.setResultStatus(MonitoredTaskResultStatus.ERROR);
				} else {
					this.setResultStatus(MonitoredTaskResultStatus.WARNING);
				}
				
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			// notify successes and failures
			if (onSuccess != null && successes.size() > 0) {
				onSuccess.accept(successes);
			}
			if (onError != null && failures.size() > 0) {
				onError.accept(failures);
			}
		});
		task.setOnFailed((e) -> {
			// this shouldn't happen because we should catch all exceptions
			// inside the task, but lets put it here just in case
			Throwable ex = task.getException();
			LOGGER.error("Failed to complete bible import", ex);
			failures.add(new FailedOperation<Path>(null, ex));
			if (onError != null) {
				onError.accept(failures);
			}
		});
		this.service.execute(task);
	}

	/**
	 * Attempts to save the given bible in this bible library.
	 * <p>
	 * The onSuccess method will be called when the save is successful. The
	 * onError method will be called if an error occurs during the save.
	 * @param bible the bible
	 * @param onSuccess called when the bible is saved successfully
	 * @param onError called when the bible failed to be saved
	 */
	public void save(Bible bible, Consumer<Bible> onSuccess, BiConsumer<Bible, Throwable> onError) {
		this.save(MessageFormat.format(Translations.get("task.save"), bible.getName()), bible, onSuccess, onError);
	}
	
	/**
	 * Attempts to save the given bible in this bible library.
	 * <p>
	 * The onSuccess method will be called when the save is successful. The
	 * onError method will be called if an error occurs during the save.
	 * @param action a simple string describing the save action if it's something more specific than "Save"
	 * @param bible the bible
	 * @param onSuccess called when the bible is saved successfully
	 * @param onError called when the bible failed to be saved
	 */
	public void save(String action, Bible bible, Consumer<Bible> onSuccess, BiConsumer<Bible, Throwable> onError) {
		// execute the add on a different thread
		Bible copy = bible.copy(true);
		MonitoredTask<Void> task = new MonitoredTask<Void>(action) {
			@Override
			protected Void call() throws Exception {
				this.updateProgress(-1, 0);
				try {
					library.save(copy);
					setResultStatus(MonitoredTaskResultStatus.SUCCESS);
					return null;
				} catch (Exception ex) {
					setResultStatus(MonitoredTaskResultStatus.ERROR);
					throw ex;
				}
			}
		};
		task.setOnSucceeded((e) -> {
			// find the bible item
			BibleListItem bi = this.getListItem(copy);
			// check if new
			if (bi == null) {
				// then add one
				this.items.add(new BibleListItem(copy));
			} else {
				// then update it
				bi.setName(copy.getName());
				// we set it to a copy because it could
				// still be edited after being saved
				// and we only want to change it if saved
				bi.setBible(copy);
			}
			// check if name changed
			if (onSuccess != null) {
				// return the original
				onSuccess.accept(bible);
			}
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to save bible " + copy.getName(), ex);
			if (onError != null) {
				// on error return the original
				onError.accept(bible, ex);
			}
		});
		this.service.execute(task);
	}
	
	/**
	 * Attempts to remove the given bible from the bible library.
	 * <p>
	 * The onSuccess method will be called on success. The onError method will be 
	 * called if an error occurs during removal (like it being in use).
	 * <p>
	 * Removing a bible can be a long operation, but the bible will be removed
	 * from the list immediately
	 * <p>
	 * The observable list of bibles will be updated if the bible is successfully
	 * removed from the BibleLibrary. Both the update of the observable list and
	 * the onSuccess and onError methods will be performed on the Java FX UI
	 * thread.
	 * @param bible the bible to remove
	 * @param onSuccess called when the bible is successfully removed
	 * @param onError called when the bible failed to be removed
	 */
	public void remove(Bible bible, Runnable onSuccess, BiConsumer<Bible, Throwable> onError) {
		// execute the add on a different thread
		MonitoredTask<Void> task = new MonitoredTask<Void>(MessageFormat.format(Translations.get("task.delete"), bible.getName())) {
			@Override
			protected Void call() throws Exception {
				this.updateProgress(-1, 0);
				try {
					library.remove(bible);
					setResultStatus(MonitoredTaskResultStatus.SUCCESS);
					return null;
				} catch (Exception ex) {
					setResultStatus(MonitoredTaskResultStatus.ERROR);
					throw ex;
				}
			}
		};
		task.setOnSucceeded((e) -> {
			BibleListItem bi = this.getListItem(bible);
			items.remove(bi);
			if (onSuccess != null) {
				onSuccess.run();
			}
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to remove bible " + bible.getName(), ex);
			if (onError != null) {
				onError.accept(bible, ex);
			}
		});
		this.service.execute(task);
	}

	/**
	 * Attempts to remove all the given bibles from the bible library.
	 * <p>
	 * The onSuccess method will be called on success. The onError method will be 
	 * called if an error occurs during removal.
	 * <p>
	 * Removing a bible can be a long operation, but the bible will be removed
	 * from the list immediately
	 * <p>
	 * The observable list of bibles will be updated if a bible is successfully
	 * removed from the BibleLibrary. Both the update of the observable list and
	 * the onSuccess and onError methods will be performed on the Java FX UI
	 * thread.
	 * @param bibles the bibles to remove
	 * @param onSuccess called for the bibles that were successfully removed
	 * @param onError called for the bibles that failed to be removed
	 */
	public void remove(List<Bible> bibles, Runnable onSuccess, Consumer<List<FailedOperation<Bible>>> onError) {
		List<FailedOperation<Bible>> failures = new ArrayList<FailedOperation<Bible>>();
		
		// execute the add on a different thread
		MonitoredTask<Void> task = new MonitoredTask<Void>(
				bibles.size() > 1 
					? MessageFormat.format(Translations.get("bible.task.delete"), bibles.size())
					: MessageFormat.format(Translations.get("task.delete"), bibles.get(0).getName())) {
			@Override
			protected Void call() throws Exception {
				updateProgress(-1, 0);
				long i = 1;
				int errorCount = 0;
				for (Bible b : bibles) {
					try {
						library.remove(b);
						Fx.runOnFxThead(() -> {
							// remove the item
							items.remove(getListItem(b));
						});
					} catch (Exception ex) {
						LOGGER.error("Failed to remove bible " + b.getName(), ex);
						failures.add(new FailedOperation<Bible>(b, ex));
						errorCount++;
					}
					updateProgress(i, bibles.size());
				}

				// set the result status based on the number of errors we got
				if (errorCount == 0) {
					this.setResultStatus(MonitoredTaskResultStatus.SUCCESS);
				} else if (errorCount == bibles.size()) {
					this.setResultStatus(MonitoredTaskResultStatus.ERROR);
				} else {
					this.setResultStatus(MonitoredTaskResultStatus.WARNING);
				}
				
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			// notify any failures
			if (onError != null && failures.size() > 0) {
				onError.accept(failures);
			}
		});
		task.setOnFailed((e) -> {
			// this shouldn't happen because we should catch all exceptions
			// inside the task, but lets put it here just in case
			Throwable ex = task.getException();
			LOGGER.error("Failed to complete removing bibles", ex);
			failures.add(new FailedOperation<Bible>(null, ex));
			if (onError != null) {
				onError.accept(failures);
			}
		});
		this.service.execute(task);
	}
	
	// searching
	
	/**
	 * Attempts to rebuild the bible searching index.
	 * <p>
	 * The onSuccess method will be called on success. The onError method will be 
	 * called if an error occurs.
	 * <p>
	 * Re-indexing the bible library can take a significant amount of time.
	 * @param onSuccess called for the bibles that were successfully removed
	 * @param onError called for the bibles that failed to be removed
	 */
	public void reindex(Runnable onSuccess, Consumer<Throwable> onError) {
		MonitoredTask<Void> task = new MonitoredTask<Void>(Translations.get("bible.reindex")) {
			@Override
			protected Void call() throws Exception {
				updateProgress(-1, 0);
				try {
					library.reindex();
					setResultStatus(MonitoredTaskResultStatus.SUCCESS);
				} catch (Exception ex) {
					setResultStatus(MonitoredTaskResultStatus.ERROR);
					throw ex;
				}
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			if (onSuccess != null) {
				onSuccess.run();
			}
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to rebuild the bible index.", ex);
			if (onError != null) {
				onError.accept(ex);
			}
		});
		this.service.execute(task);
	}
	
	/**
	 * Searches all bibles for the given text using the given search type.
	 * @param text the text to search for
	 * @param searchType the search type
	 * @param onSuccess called when the search is complete
	 * @param onError called when the search failed
	 */
	public void search(String text, SearchType searchType, Consumer<List<BibleSearchResult>> onSuccess, Consumer<Throwable> onError) {
		this.search(null, null, text, searchType, onSuccess, onError);
	}

	/**
	 * Searches all bibles for the given text using the given search type.
	 * @param bibleId the id of the bible to search
	 * @param text the text to search for
	 * @param searchType the search type
	 * @param onSuccess called when the search is complete
	 * @param onError called when the search failed
	 */
	public void search(UUID bibleId, String text, SearchType searchType, Consumer<List<BibleSearchResult>> onSuccess, Consumer<Throwable> onError) {
		this.search(bibleId, null, text, searchType, onSuccess, onError);
	}
	
	/**
	 * Searches the given bible for the given text using the given search type.
	 * @param bibleId the id of the bible to search
	 * @param bookNumber the book number of the book to search
	 * @param text the text to search for
	 * @param searchType the search type
	 * @param onSuccess called when the search is complete
	 * @param onError called when the search failed
	 */
	public void search(UUID bibleId, Short bookNumber, String text, SearchType searchType, Consumer<List<BibleSearchResult>> onSuccess, Consumer<Throwable> onError) {
		Task<List<BibleSearchResult>> task = new Task<List<BibleSearchResult>>() {
			@Override
			protected List<BibleSearchResult> call() throws Exception {
				updateProgress(-1, 0);
				try {
					return library.search(bibleId, bookNumber, text, searchType);
				} catch (Exception ex) {
					throw ex;
				}
			}
		};
		task.setOnSucceeded((e) -> {
			if (onSuccess != null) {
				onSuccess.accept(task.getValue());
			}
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to search. BibleId: " + bibleId + " Text: '" + text + "' Type: " + searchType, ex);
			if (onError != null) {
				onError.accept(ex);
			}
		});
		this.service.execute(task);
	}

	/**
	 * Returns the bible list item for the given bible or null if not found.
	 * @param bible the bible
	 * @return {@link BibleListItem}
	 */
	private BibleListItem getListItem(Bible bible) {
		if (bible == null) return null;
		BibleListItem bi = null;
    	for (int i = 0; i < items.size(); i++) {
    		BibleListItem item = items.get(i);
    		if (item.getBible().getId().equals(bible.getId())) {
    			bi = item;
    			break;
    		}
    	}
    	return bi;
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
	public BibleListItem getListItem(UUID id) {
		Bible bible = this.library.get(id);
		return this.getListItem(bible);
	}
	
	/**
	 * Returns the observable list of bibles.
	 * @return ObservableList&lt;{@link BibleListItem}&gt;
	 */
	public ObservableList<BibleListItem> getItems() {
		return this.items;
	}
}
