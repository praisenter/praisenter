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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.FailedOperation;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleImporter;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.bible.FormatIdentifingBibleImporter;
import org.praisenter.bible.UnboundBibleImporter;
import org.praisenter.javafx.utility.Fx;

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
 */
public final class ObservableBibleLibrary {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The bible library */
	private final BibleLibrary library;
	
	/** The thread service */
	private final ExecutorService service;
	
	/** The observable list of bibles */
	private final ObservableList<BibleListItem> items = FXCollections.observableArrayList();
	
	/**
	 * Minimal constructor.
	 * @param library the bible library
	 * @param service the thread service
	 */
	public ObservableBibleLibrary(BibleLibrary library, ExecutorService service) {
		this.library = library;
		this.service = service;
		
		List<Bible> bibles = null;
		if (library != null) {
			try {
				bibles = library.getBibles();
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
		Task<List<Bible>> task = new Task<List<Bible>>() {
			@Override
			protected List<Bible> call() throws Exception {
				UnboundBibleImporter importer = new UnboundBibleImporter(library);
				return importer.execute(path);
			}
		};
		task.setOnSucceeded((e) -> {
			List<Bible> bibles = task.getValue();
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				items.remove(loading);
				for (Bible bible : bibles) {
					items.add(new BibleListItem(bible));
				}
				if (onSuccess != null) {
					onSuccess.accept(bibles);
				}
			});
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to import bible " + path.toAbsolutePath().toString(), ex);
			// changes to the list should be done on the FX UI Thread
			Fx.runOnFxThead(() -> {
				items.remove(loading);
				if (onError != null) {
					onError.accept(path, ex);
				}
			});
		});
		this.service.submit(task);
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
		List<BibleListItem> loadings = new ArrayList<BibleListItem>();
		for (Path path : paths) {
			loadings.add(new BibleListItem(path.getFileName().toString()));
		}
		
		// changes to the list should be done on the FX UI Thread
		Fx.runOnFxThead(() -> {
			// add it to the items list
			items.addAll(loadings);
		});
		
		List<Bible> successes = new ArrayList<Bible>();
		List<FailedOperation<Path>> failures = new ArrayList<FailedOperation<Path>>();
		
		BibleImporter importer = new FormatIdentifingBibleImporter(library);
		
		// execute the add on a different thread
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				for (Path path : paths) {
					try {
						List<Bible> bbls = importer.execute(path);
						successes.addAll(bbls);
						Fx.runOnFxThead(() -> {
							// remove the loading item
							items.remove(new BibleListItem(path.getFileName().toString()));
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
							items.remove(new BibleListItem(path.getFileName().toString()));
						});
					}
				}
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			Fx.runOnFxThead(() -> {
				// notify successes and failures
				if (onSuccess != null && successes.size() > 0) {
					onSuccess.accept(successes);
				}
				if (onError != null && failures.size() > 0) {
					onError.accept(failures);
				}
			});
		});
		task.setOnFailed((e) -> {
			// this shouldn't happen because we should catch all exceptions
			// inside the task, but lets put it here just in case
			Throwable ex = task.getException();
			LOGGER.error("Failed to complete bible import", ex);
			failures.add(new FailedOperation<Path>(null, ex));
			Fx.runOnFxThead(() -> {
				if (onError != null) {
					onError.accept(failures);
				}
			});
		});
		this.service.submit(task);
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
		Fx.runOnFxThead(() -> {
			items.remove(new BibleListItem(bible));
		});
		
		// execute the add on a different thread
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				library.deleteBible(bible);
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			Fx.runOnFxThead(() -> {
				if (onSuccess != null) {
					onSuccess.run();
				}
			});
		});
		task.setOnFailed((e) -> {
			Throwable ex = task.getException();
			LOGGER.error("Failed to remove bible " + bible.getName(), ex);
			Fx.runOnFxThead(() -> {
				// add it back
				items.add(new BibleListItem(bible));
				if (onError != null) {
					onError.accept(bible, ex);
				}
			});
		});
		this.service.submit(task);
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
		
		Fx.runOnFxThead(() -> {
			for (Bible bible : bibles) {
				// remove the item
				items.remove(new BibleListItem(bible));
			}
		});
		
		// execute the add on a different thread
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				for (Bible bible : bibles) {
					try {
						library.deleteBible(bible);
					} catch (Exception ex) {
						// add it back
						items.add(new BibleListItem(bible));
						LOGGER.error("Failed to remove bible " + bible.getName(), ex);
						failures.add(new FailedOperation<Bible>(bible, ex));
					}
				}
				return null;
			}
		};
		task.setOnSucceeded((e) -> {
			Fx.runOnFxThead(() -> {
				// notify any failures
				if (onError != null && failures.size() > 0) {
					onError.accept(failures);
				}
			});
		});
		task.setOnFailed((e) -> {
			// this shouldn't happen because we should catch all exceptions
			// inside the task, but lets put it here just in case
			Throwable ex = task.getException();
			LOGGER.error("Failed to complete removing bibles", ex);
			failures.add(new FailedOperation<Bible>(null, ex));
			Fx.runOnFxThead(() -> {
				if (onError != null) {
					onError.accept(failures);
				}
			});
		});
		this.service.submit(task);
	}
	
	// other
	
	
	
	/**
	 * Returns the observable list of bibles.
	 * @return ObservableList&lt;{@link BibleListItem}&gt;
	 */
	public ObservableList<BibleListItem> getItems() {
		return this.items;
	}
}
