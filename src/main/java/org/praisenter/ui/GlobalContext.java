package org.praisenter.ui;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.async.AsyncHelper;
import org.praisenter.async.BackgroundTask;
import org.praisenter.async.ReadOnlyBackgroundTask;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.DataManager;
import org.praisenter.data.Persistable;
import org.praisenter.data.UnknownFormatException;
import org.praisenter.data.configuration.Configuration;
import org.praisenter.data.configuration.Resolution;
import org.praisenter.data.media.Media;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideShow;
import org.praisenter.ui.controls.Alerts;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.events.ActionStateChangedEvent;
import org.praisenter.ui.translations.Translations;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Manages application level state.
 * @author William Bittle
 *
 */
public final class GlobalContext {
	private static final Logger LOGGER = LogManager.getLogger();
	
	final Application application;
	final Stage stage;
	final DataManager dataManager;
	final Configuration configuration;
	final ImageCache imageCache;
	
	private final BooleanProperty windowFocused;
	private final ObjectProperty<Scene> scene;
	private final ObjectProperty<Node> focusOwner;
	private final ObjectProperty<ActionPane> closestActionPane;
	
	private final StringProperty selectedText;
	private final BooleanProperty textSelected;
	
	private final ObjectProperty<DocumentContext<? extends Persistable>> currentDocument;
	private final ObservableList<DocumentContext<? extends Persistable>> openDocuments;
	private final ObservableList<DocumentContext<? extends Persistable>> openDocumentsReadOnly;
	
	private final Map<Action, BooleanProperty> isEnabled;
	private final Map<Action, BooleanProperty> isVisible;
	
	private final ObservableList<ReadOnlyBackgroundTask> tasks;
	private final ObservableList<ReadOnlyBackgroundTask> tasksReadOnly;
	private final BooleanProperty taskExecuting;
	private final BooleanProperty taskFailed;

	public GlobalContext(
			Application application, 
			Stage stage,
			DataManager dataManager,
			Configuration configuration) {
		this.application = application;
		this.stage = stage;
		this.dataManager = dataManager;
		this.configuration = configuration;
		this.imageCache = new ImageCache();
		
		this.scene = new SimpleObjectProperty<>();
		
		this.focusOwner = new SimpleObjectProperty<>();
		this.closestActionPane = new SimpleObjectProperty<>();
		this.windowFocused = new SimpleBooleanProperty();
		
		this.selectedText = new SimpleStringProperty();
		this.textSelected = new SimpleBooleanProperty();
		
		this.currentDocument = new SimpleObjectProperty<>();
		this.openDocuments = FXCollections.observableArrayList();
		this.openDocumentsReadOnly = FXCollections.unmodifiableObservableList(this.openDocuments);
		
		this.isEnabled = new HashMap<>();
		this.isVisible = new HashMap<>();
		
		for (Action action : Action.values()) {
			this.isEnabled.put(action, new SimpleBooleanProperty());
			this.isVisible.put(action, new SimpleBooleanProperty());
		}
		
		// make sure update events get called on the sub properties
		this.tasks = FXCollections.observableArrayList(t -> {
			return new Observable[] {
				t.completeProperty()
			};
		});
		this.tasksReadOnly = FXCollections.unmodifiableObservableList(this.tasks);
		this.taskExecuting = new SimpleBooleanProperty();
		this.taskFailed = new SimpleBooleanProperty();
		
		// bindings
		
		this.scene.bind(this.stage.sceneProperty());
		this.windowFocused.bind(this.stage.focusedProperty());
		
		this.scene.addListener((obs, ov, nv) -> {
			this.focusOwner.unbind();
			if (nv != null) {
				this.focusOwner.bind(nv.focusOwnerProperty());
			}
		});
		
		this.focusOwner.addListener((obs, ov, nv) -> {
			// detach selection change event handler if text input
			this.selectedText.unbind();
			if (nv != null && nv instanceof TextInputControl) {
				this.selectedText.bind(((TextInputControl)nv).selectedTextProperty());
			}
			
			// find the closest action pane
			ActionPane ap = null;
			if (nv != null) {
				// walk up the tree from the focused node to the top
				// looking for an Application Pane
				Node node = nv;
				while (node != null) {
					if (node instanceof ActionPane) {
						ap = (ActionPane)node;
						break;
					}
					node = node.getParent();
				}
			}
			
			this.closestActionPane.set(ap);
			
			this.onActionStateChanged("FOCUS_CHANGED=" + (nv != null ? nv.getClass().getName() : "null"));
		});
		
		this.textSelected.bind(Bindings.createBooleanBinding(() -> {
			String selection = this.selectedText.get();
			return selection != null && !selection.isEmpty();
		}, this.selectedText));
		
		// NOTE: this was done rather than a class method because removal didn't work in that case
		// apparently Java will wrap the method reference call in an event handler so when you go
		// to remove it, its a whole new object and doesn't work
		EventHandler<ActionStateChangedEvent> eh = e -> {
			this.onActionStateChanged(e.getEventType().getName());
		};
		
		ListChangeListener<Object> lcl = (Change<? extends Object> c) -> {
			Object selectedItem = null;
			ActionPane ap = this.closestActionPane.get();
			if (ap != null) {
				List<?> selected = ap.getSelectedItems();
				if (selected != null && selected.size() > 0) {
					selectedItem = selected.get(0);
				}
			}
			this.onActionStateChanged("SELECTION_CHANGED=" + (selectedItem != null ? selectedItem.toString() : "null"));
		};
		
		this.closestActionPane.addListener((obs, ov, nv) -> {
			if (ov != null) {
				ov.removeEventHandler(ActionStateChangedEvent.ALL, eh);
				ov.getSelectedItems().removeListener(lcl);
			}
			if (nv != null) {
				nv.addEventHandler(ActionStateChangedEvent.ALL, eh);
				nv.getSelectedItems().addListener(lcl);
			}
		});
		
		this.currentDocument.addListener((obs, ov, nv) -> {
			this.onActionStateChanged("CURRENT_DOCUMENT_CHANGED=" + this.currentDocument.get());
		});
		
		// we need to re-evaluate the action states when:
		// 1. the focus between windows change (clipboard content may have changed)
		// 2. text is selected (for copy/cut/delete)
		// 3. when the last focused element changes
		this.windowFocused.addListener((obs, ov, nv) -> this.onActionStateChanged("WINDOW_FOCUS_CHANGED=" + nv));
		this.textSelected.addListener((obs, ov, nv) -> this.onActionStateChanged("TEXT_SELECTED=" + nv));
		
		this.taskExecuting.bind(Bindings.createBooleanBinding(() -> {
			boolean isTaskExecuting = this.tasks.stream().anyMatch(t -> !t.isComplete());
			return isTaskExecuting;
		}, this.tasks));
		
		this.taskFailed.bind(Bindings.createBooleanBinding(() -> {
			return this.tasks.stream().anyMatch(t -> t.getException() != null);
		}, this.tasks));

		// watch for screen resolution changes
		Screen.getScreens().addListener((Observable obs) -> {
			LOGGER.info("Screen change detected.");
			this.addMissingResolutionsBasedOnHost();
		});
		
		this.addMissingResolutionsBasedOnHost();
	}
	
	/**
	 * Adds any missing screen resolutions from the resolutions list based on the host
	 * environment's screen resolutions.
	 */
	private void addMissingResolutionsBasedOnHost() {
		for (Screen screen : Screen.getScreens()) {
			Rectangle2D bounds = screen.getBounds();
			Resolution res = new Resolution((int)bounds.getWidth(), (int)bounds.getHeight());
			boolean found = false;
			for (Resolution r : this.configuration.getResolutions()) {
				if (r.equals(res)) {
					found = true;
					break;
				}
			}
			if (!found) {
				this.configuration.getResolutions().add(res);
			}
		}
	}
	
	/**
	 * Called in a number of scenarios so that the state of global actions can be updated.
	 * @param reason the reason for the update
	 */
	private void onActionStateChanged(String reason) {
		LOGGER.debug("Action State Updating: " + reason);

		for (Action action : Action.values()) {
			BooleanProperty isEnabled = this.isEnabled.get(action);
			isEnabled.set(this.isEnabled(action));
			
			// for visibility, we only want to hide certain ones
			BooleanProperty isVisible = this.isVisible.get(action);
			isVisible.set(this.isVisible(action));
		}
	}
	
	/**
	 * Determines whether the given action is enabled or not.
	 * @param action the action
	 * @return boolean
	 */
	private boolean isEnabled(Action action) {
		// always enabled actions
		switch (action) {
			case NEW:
			case IMPORT:
				return true;
			default:
				break;
		}
		
		// actions based on the current document or documents
		DocumentContext<?> document = this.currentDocument.get();
		switch (action) {
			case SAVE:
				return document != null && document.hasUnsavedChanges();
			case SAVE_ALL:
				for (DocumentContext<?> ctx : this.openDocuments) {
					if (ctx.hasUnsavedChanges()) return true;
				}
				return false;
			case REDO:
				return document != null && document.getUndoManager().isRedoAvailable();
			case UNDO:
				return document != null && document.getUndoManager().isUndoAvailable();
			default:
				break;
		}
		
		// actions based on textinput controls
		Node focused = this.getFocusOwner();
		boolean isTextInput = focused != null && focused instanceof TextInputControl;
		TextInputControl control = null;
		if (isTextInput) {
			control = (TextInputControl)focused;
		}
		
		if (isTextInput && this.isTextInputAction(action)) {
			switch(action) {
				case CUT:
				case DELETE:
				case COPY:
					String selection = control.getSelectedText();
					return selection != null && !selection.isEmpty();
				case SELECT_ALL:
				case SELECT_NONE:
					return true;
				case PASTE:
					Clipboard cb = Clipboard.getSystemClipboard();
					return cb.hasContent(DataFormat.PLAIN_TEXT);
				default:
					break;
			}
		}
		
		// actions based on the closest action pane
		ActionPane ap = this.closestActionPane.get();
		if (ap != null) {
			return ap.isActionEnabled(action);
		}
		
		// otherwise return false
		return false;
	}
	
	/**
	 * Determines whether the given action is visible or not.
	 * @param action the action
	 * @return boolean
	 */
	private boolean isVisible(Action action) {
		ActionPane ap = this.closestActionPane.get();
		
		switch (action) {
			case SAVE:
			case SAVE_ALL:
			case UNDO:
			case REDO:
				return this.openDocuments.size() > 0;
			case CUT:
				return ap != null && ap.isActionEnabled(action);
			case RENUMBER:
			case REORDER:
			case NEW_BOOK:
			case NEW_CHAPTER:
			case NEW_VERSE:
				return ap != null && ap.isActionVisible(action);
			default:
				return true;
		}
	}
	
	/**
	 * Will either execute the action directly or will delegate to the appropriate contextual component.
	 * @param action
	 * @return
	 */
	public CompletableFuture<Void> executeAction(Action action) {
		// handle global actions (i.e. no context related to the action)
		switch (action) {
			case IMPORT:
				return this.promptImport();
			case NEW:
				// TODO show a new dialog
				break;
			default:
				break;
		}
		
		// handle document based actions
		switch (action) {
			case SAVE:
				return this.save();
			case SAVE_ALL:
				return this.saveAll();
			case REDO:
				return this.redo();
			case UNDO:
				return this.undo();
			default:
				break;
		}
		
		// if the last focused thing was a TextInputControl, then send actions there first
		Node focused = this.getFocusOwner();
		boolean isTextInput = focused != null && focused instanceof TextInputControl;
		TextInputControl control = null;
		if (isTextInput) {
			control = (TextInputControl)focused;
		}
		
		CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
		if (isTextInput && this.isTextInputAction(action)) {
			// send commands to it
			this.handleTextInputControlAction(control, action);
			return CompletableFuture.completedFuture(null);
		}
		
		// lastly, send the actions to the closest action pane to the focused node
		ActionPane ap = this.closestActionPane.get();
		if (ap != null) {
			future = ap.executeAction(action);
		}
		
		// when we're done, always update the action states
		return future.thenApply((o) -> {
			AsyncHelper.onJavaFXThread(() -> {
				this.onActionStateChanged("ACTION_EXECUTED");
			});
			return null;
		});
	}
	
	/**
	 * Handles the set of actions that a TextInputControl should be able to perform.
	 * @param control the control
	 * @param action the action
	 */
	private void handleTextInputControlAction(TextInputControl control, Action action) {
		IndexRange selection = control.getSelection();
		switch (action) {
			case COPY:
				control.copy();
				break;
			case CUT:
				// JAVABUG (L) 11/09/16 [workaround] for java.lang.StringIndexOutOfBoundsException when only using control.cut();
				control.copy();
				control.deselect();
				control.deleteText(selection);
				break;
			case PASTE:
				control.paste();
				break;
			case DELETE:
				// JAVABUG (L) 11/09/16 [workaround] workaround for java.lang.StringIndexOutOfBoundsException when only using control.deleteText(control.getSelection());
				control.deselect();
				control.deleteText(selection);
				break;
			case SELECT_ALL:
				control.selectAll();
				break;
			case SELECT_NONE:
				control.deselect();
				break;
			default:
				break;
		}
		
		this.onActionStateChanged("TEXT_INPUT_CONTROL_ACTION");
	}
	
	/**
	 * Returns true if the given action is in the set of actions applicable to TextInputControls.
	 * @param action the action
	 * @return boolean
	 */
	private boolean isTextInputAction(Action action) {
		switch(action) {
			case COPY:
			case PASTE:
			case CUT:
			case DELETE:
			case SELECT_ALL:
			case SELECT_NONE:
				return true;
			default:
				return false;
		}
	}

	// actions
	
	private CompletableFuture<Void> undo() {
		DocumentContext<?> ctx = this.currentDocument.get();
		if (ctx != null) {
			ctx.getUndoManager().undo();
		}
		return AsyncHelper.nil();
	}
	
	private CompletableFuture<Void> redo() {
		DocumentContext<?> ctx = this.currentDocument.get();
		if (ctx != null) {
			ctx.getUndoManager().redo();
		}
		return AsyncHelper.nil();
	}
	
	// TODO i don't really like methods that show UI elements in this class
	
	private CompletableFuture<Void> save() {
		DocumentContext<?> ctx = this.currentDocument.get();
		if (ctx != null) {
			return this.save(ctx).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
				this.onActionStateChanged("SAVE_COMPLETE");
			})).exceptionally(t -> {
				Platform.runLater(() -> {
					Alert alert = Alerts.exception(this.stage, null, null, null, t);
					alert.show();
				});
				return null;
			});
		} else {
			return CompletableFuture.completedFuture(null);
		}
	}
	
	private CompletableFuture<Void> saveAll() {
		List<DocumentContext<?>> ctxs = new ArrayList<>(this.openDocuments);
		
		CompletableFuture<?>[] futures = ctxs.stream()
			.map(ctx -> this.save(ctx))
			.collect(Collectors.toList())
			.toArray(new CompletableFuture[0]);
		
		return CompletableFuture.allOf(futures).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.onActionStateChanged("SAVE_COMPLETE");
		})).exceptionally(t -> {
			// get the root exceptions from the futures
			List<Throwable> exceptions = AsyncHelper.getExceptions(futures);
			
			Platform.runLater(() -> {
				Alert alert = Alerts.exception(this.stage, null, null, null, exceptions);
				alert.show();
			});
			
			return null;
		});
	}
	
	public CompletableFuture<Void> save(DocumentContext<?> context) {
		// make sure there are changes to save first
		if (!context.hasUnsavedChanges()) {
			return CompletableFuture.completedFuture(null);
		}
		
		// if there are, then attempt to save
		Persistable data = context.getDocument();
		if (data != null) {
			// update the modified on
			data.setModifiedDate(Instant.now());
			// now create a copy to be saved
			final Persistable copy = data.copy();
			final Object position = context.getUndoManager().storePosition();
			
			return context.getSaveExecutionManager().execute((o) -> {
				BackgroundTask task = new BackgroundTask();
				task.setName(Translations.get("task.saving", copy.getName()));
				task.setMessage(Translations.get("task.saving", copy.getName()));
				this.addBackgroundTask(task);
				return this.getDataManager().update(copy).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
					context.getUndoManager().markPosition(position);
					task.setProgress(1);
				})).handle((ob, t) -> {
					return t;
				}).thenCompose(AsyncHelper.onJavaFXThreadAndWait((t) -> {
					if (t != null) {
						LOGGER.error("Failed to save '" + copy.getName() + "'", t);
						context.getUndoManager().clearPosition(position);
						task.setException(t);
					}
					return t;
				})).thenAccept((t) -> {
					if (t != null) {
						throw new CompletionException(t);
					}
				});
			});
		}
		return AsyncHelper.nil();
	}
	
	private CompletableFuture<Void> promptImport() {
		FileChooser fc = new FileChooser();
		fc.setTitle(Translations.get("action.import"));
		List<File> files = fc.showOpenMultipleDialog(this.stage);
		return this.importFiles(files);
	}
	
	public CompletableFuture<Void> importFiles(List<File> files) {
		if (files == null || files.isEmpty()) return CompletableFuture.completedFuture(null);
		
		final BackgroundTask bt = new BackgroundTask();
		bt.setName(Translations.get("action.import"));
		bt.setMessage(Translations.get("action.import"));
		
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		
		for (File file : files) {
			System.out.println("Starting load of " + file);
			CompletableFuture<Void> future = this.dataManager.importData(file.toPath(), Bible.class, Slide.class, SlideShow.class, Media.class).exceptionally(t -> {
				LOGGER.error("Failed to import file '" + file.toPath().toAbsolutePath().toString() + "' due to: " + t.getMessage(), t);
				if (t instanceof CompletionException) throw (CompletionException)t; 
				throw new CompletionException(t);
			});
			futures.add(future);
		}
		
		this.addBackgroundTask(bt);
		
		CompletableFuture<?>[] farray = futures.toArray(new CompletableFuture[0]);
		return CompletableFuture.allOf(farray).thenRun(() -> {
			bt.setProgress(1.0);
		}).exceptionally(t -> {
			List<Throwable> exceptions = AsyncHelper.getExceptions(farray);
			
			Platform.runLater(() -> {
				Alert alert = Alerts.exception(this.stage, null, null, null, exceptions);
				alert.show();
			});

			bt.setException(exceptions.get(0));
			
			return null;
		});
	}
	
	public Application getApplication() {
		return this.application;
	}
	
	public Stage getStage() {
		return this.stage;
	}
	
	public DataManager getDataManager() {
		return this.dataManager;
	}
	
	public Configuration getConfiguration() {
		return this.configuration;
	}
	
	public ImageCache getImageCache() {
		return this.imageCache;
	}
	
	public CompletableFuture<Void> saveConfiguration() {
		return this.dataManager.update(this.configuration);
	}
	
	public Scene getScene() {
		return this.scene.get();
	}
	
	public ReadOnlyObjectProperty<Scene> sceneProperty() {
		return this.scene;
	}
	
	public Node getFocusOwner() {
		return this.focusOwner.get();
	}
	
	public ReadOnlyObjectProperty<Node> focusOwnerProperty() {
		return this.focusOwner;
	}
	
	public boolean isWindowFocused() {
		return this.windowFocused.get();
	}
	
	public ReadOnlyBooleanProperty windowFocusedProperty() {
		return this.windowFocused;
	}
	
	public String getSelectedText() {
		return this.selectedText.get();
	}
	
	public ReadOnlyStringProperty selectedTextProperty() {
		return this.selectedText;
	}
	
	public boolean isTextSelected() {
		return this.textSelected.get();
	}
	
	public ReadOnlyBooleanProperty textSelectedProperty() {
		return this.textSelected;
	}
	
	/**
	 * Returns the current document.
	 * @return {@link DocumentContext}
	 */
	public DocumentContext<? extends Persistable> getCurrentDocument() {
		return this.currentDocument.get();
	}
	
	/**
	 * Sets the current document.
	 * @param document the document
	 */
	public void setCurrentDocument(DocumentContext<? extends Persistable> document) {
		this.currentDocument.set(document);
	}
	
	/**
	 * Returns the current document property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<DocumentContext<? extends Persistable>> currentDocumentProperty() {
		return this.currentDocument;
	}
	
	/**
	 * Opens the given document.
	 * @param document the document
	 */
	public <T extends Persistable> void openDocument(T document) {
		// is the document already open?
		DocumentContext<? extends Persistable> context = null;
		for (DocumentContext<? extends Persistable> ctx : this.openDocuments) {
			if (Objects.equals(ctx.getDocument(), document)) {
				context = ctx;
				break;
			}
		}
		
		if (context == null) {
			// then open it
			context = new DocumentContext<>(document);
			context.getUndoManager().undoCountProperty().addListener((obs, ov, nv) -> this.onActionStateChanged("UNDO_REDO"));
			context.getUndoManager().redoCountProperty().addListener((obs, ov, nv) -> this.onActionStateChanged("UNDO_REDO"));
			this.openDocuments.add(context);
		}
		
		// set it to the current document
		final DocumentContext<? extends Persistable> ctx = context;
		this.currentDocument.set(ctx);
	}
	
	/**
	 * Closes the given document.
	 * @param document the document to close
	 */
	public void closeDocument(DocumentContext<? extends Persistable> document) {
		this.openDocuments.removeIf(d -> Objects.equals(d, document));
		if (Objects.equals(document, this.currentDocument.get())) {
			this.currentDocument.set(null);
		}
	}
	
	/**
	 * Returns an unmodifiable list of the currently open documents.
	 * @return ObservableList
	 */
	public ObservableList<DocumentContext<? extends Persistable>> getOpenDocumentsUnmodifiable() {
		return this.openDocumentsReadOnly;
	}
	
	/**
	 * Returns the enabled property for the given action.
	 * @param action the action
	 * @return ReadOnlyBooleanProperty
	 */
	public ReadOnlyBooleanProperty getActionEnabledProperty(Action action) {
		return this.isEnabled.get(action);
	}
	
	/**
	 * Returns the visible property for the given action.
	 * @param action the action
	 * @return ReadOnlyBooleanProperty
	 */
	public ReadOnlyBooleanProperty getActionVisibleProperty(Action action) {
		return this.isVisible.get(action);
	}
	
	public boolean isTaskExecuting() {
		return this.taskExecuting.get();
	}
	
	public ReadOnlyBooleanProperty taskExecutingProperty() {
		return this.taskExecuting;
	}

	public boolean isTaskFailed() {
		return this.taskFailed.get();
	}
	
	public ReadOnlyBooleanProperty taskFailedProperty() {
		return this.taskFailed;
	}
	
	/**
	 * Returns the list of all background tasks.
	 * @return
	 */
	public ObservableList<ReadOnlyBackgroundTask> getBackgroundTasksUnmodifiable() {
		return this.tasksReadOnly;
	}
	
	/**
	 * Adds a new background task that the user can monitor.
	 * @param task the task
	 */
	public void addBackgroundTask(ReadOnlyBackgroundTask task) {
		// check for tasks we should clean up
		this.tasks.removeIf(t -> t.isComplete() && t.getException() == null);
		this.tasks.add(task);
	}
	
	/**
	 * Clears all completed background tasks, even those that completed with an exception.
	 */
	public void clearCompletedTasks() {
		this.tasks.removeIf(t -> t.isComplete());
	}
}
