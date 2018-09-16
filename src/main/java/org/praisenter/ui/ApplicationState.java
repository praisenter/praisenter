package org.praisenter.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.async.AsyncHelper;
import org.praisenter.ui.events.ActionStateChangedEvent;

import javafx.application.Application;
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
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.stage.Stage;

public final class ApplicationState {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final Application application;
	private final Stage stage;
	private final ObjectProperty<Scene> scene;
	private final ObjectProperty<Node> focusOwner;
	private final ObjectProperty<ActionPane> focusedActionPane;
	private final BooleanProperty windowFocused;
	private final StringProperty selectedText;
	private final BooleanProperty textSelected;
	private final Map<Action, BooleanProperty> isEnabled;
	private final Map<Action, BooleanProperty> isVisible;
	
	public ApplicationState(Application application, Stage stage) {
		this.application = application;
		this.stage = stage;
		this.scene = new SimpleObjectProperty<>();
		this.focusOwner = new SimpleObjectProperty<>();
		this.focusedActionPane = new SimpleObjectProperty<>();
		this.windowFocused = new SimpleBooleanProperty();
		this.selectedText = new SimpleStringProperty();
		this.textSelected = new SimpleBooleanProperty();
		this.isEnabled = new HashMap<>();
		this.isVisible = new HashMap<>();
		
		for (Action action : Action.values()) {
			this.isEnabled.put(action, new SimpleBooleanProperty());
			this.isVisible.put(action, new SimpleBooleanProperty());
		}
		
		this.scene.bind(this.stage.sceneProperty());
		this.windowFocused.bind(this.stage.focusedProperty());
		
		this.scene.addListener((obs, ov, nv) -> {
			this.focusOwner.unbind();
			if (nv != null) {
				this.focusOwner.bind(nv.focusOwnerProperty());
			}
		});
		
		// NOTE: this was done rather than a class method because removal didn't work in that case
		// apparently Java will wrap the method reference call in an event handler so when you go
		// to remove it, its a whole new object and doesn't work
		EventHandler<ActionStateChangedEvent> eh = e -> {
			this.onActionStateChanged(e.getEventType().getName());
		};
		
		this.focusOwner.addListener((obs, ov, nv) -> {
			// detach selection change event handler if text input
			this.selectedText.unbind();
			
			// detach the handler on the previous focused action pane
			ActionPane ap = this.focusedActionPane.get();
			if (ap != null) {
				ap.removeEventHandler(ActionStateChangedEvent.ALL, eh);
			}
			this.focusedActionPane.set(null);
			
			if (nv != null) {
				if (nv instanceof TextInputControl) {
					this.selectedText.bind(((TextInputControl)nv).selectedTextProperty());
				}
				
				// walk up the tree from the focused node to the top
				// looking for an Application Pane
				Node node = nv;
				while (node != null) {
					if (node instanceof ActionPane) {
						ap = (ActionPane)node;
						ap.addEventHandler(ActionStateChangedEvent.ALL, eh);
						this.focusedActionPane.set(ap);
					}
					
					node = node.getParent();
				}
			}
			
			this.onActionStateChanged("FOCUS_CHANGED=" + (nv != null ? nv.getClass().getName() : "null"));
		});
		
		this.textSelected.bind(Bindings.createBooleanBinding(() -> {
			String selection = this.selectedText.get();
			return selection != null && !selection.isEmpty();
		}, this.selectedText));
		
		// we need to re-evaluate the action states when:
		// 1. the focus between windows change (clipboard content may have changed)
		// 2. text is selected (for copy/cut/delete)
		// 3. when the last focused element changes
		this.windowFocused.addListener((obs, ov, nv) -> this.onActionStateChanged("WINDOW_FOCUS_CHANGED=" + nv));
		this.textSelected.addListener((obs, ov, nv) -> this.onActionStateChanged("TEXT_SELECTED=" + nv));
	}
	
	public static boolean isNodeInFocusChain(Node focused, Node... nodes) {
		boolean isFocused = false;
		while (nodes != null && nodes.length > 0 && focused != null) {
			for (int i = 0; i < nodes.length; i++) {
				if (focused == nodes[i]) {
					isFocused = true;
					break;
				}
			}
			if (isFocused) {
				break;
			}
			focused = focused.getParent();
		}
		return isFocused;
	}

	private void onActionStateChanged(String reason) {
		LOGGER.debug("Action State Updating: " + reason);
		
		ActionPane ap = this.focusedActionPane.get();
		Node focused = this.getFocusOwner();
		
		boolean isTextInput = focused != null && focused instanceof TextInputControl;
		TextInputControl control = null;
		if (isTextInput) {
			control = (TextInputControl)focused;
		}
		
		for (Action action : Action.values()) {
			BooleanProperty isEnabled = this.isEnabled.get(action);
			if (action == Action.NEW || action == Action.IMPORT) {
				// new and import should always be available
				isEnabled.setValue(true);
			} else if (isTextInput && this.isTextInputAction(action)) {
				// if we are focused on a text input field, then determine the availability of
				// some actions based on it, others should go to the closest action pane
				isEnabled.setValue(this.isEnabledForTextInput(control, action));
			} else if (ap != null) {
				// determine based on the current action pane
				isEnabled.setValue(ap.isActionEnabled(action));
			} else {
				// otherwise the action is disabled
				isEnabled.setValue(false);
			}
			
			// for visibility, we only want to hide certain ones
			BooleanProperty isVisible = this.isVisible.get(action);
			switch (action) {
				case RENUMBER:
				case REORDER:
					isVisible.setValue(ap != null && ap.isActionVisible(action));
					break;
				default:
					isVisible.setValue(true);
					break;
			}
		}
	}
	
	/**
	 * Some actions should be available to normal Java FX controls, namely the text input
	 * controls for copy/cut/paste/delete/select all.
	 * @param focused the true focus owner
	 * @param action the action
	 * @return boolean
	 */
	private boolean isEnabledForTextInput(TextInputControl control, Action action) {
		switch (action) {
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
		return false;
	}
	
	public CompletableFuture<Node> executeAction(Action action) {
		boolean isUndoRedo = action == Action.UNDO || action == Action.REDO;
		
		ActionPane ap = this.focusedActionPane.get();
		Node focused = this.getFocusOwner();
		
		boolean isTextInput = focused != null && focused instanceof TextInputControl;
		TextInputControl control = null;
		if (isTextInput) {
			control = (TextInputControl)focused;
		}
		
		// if the last focused thing was a TextInputControl, then send actions
		// to it (other than undo/redo)
		CompletableFuture<Node> future = CompletableFuture.completedFuture(null);
		if (isTextInput && this.isTextInputAction(action)) {
			// send commands to it
			this.handleTextInputControlAction(control, action);
		} else if (ap != null) {
			future = ap.performAction(action);
		}
		
		return future.thenApply((node) -> {
			// when undo/redo is sent, update the action states after execution
			if (isUndoRedo) {
				AsyncHelper.onJavaFXThread(() -> {
					this.onActionStateChanged("UNDO_REDO_ACTION");
				});
			}
			
			// return the node
			return node;
		});
	}
	
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
	
	public Application getApplication() {
		return this.application;
	}
	
	public Stage getStage() {
		return this.stage;
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
	
	public ActionPane getFocusedActionPane() {
		return this.focusedActionPane.get();
	}
	
	public ReadOnlyObjectProperty<ActionPane> focusedActionPaneProperty() {
		return this.focusedActionPane;
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
	
	public ReadOnlyBooleanProperty getActionEnabledProperty(Action action) {
		return this.isEnabled.get(action);
	}
	
	public ReadOnlyBooleanProperty getActionVisibleProperty(Action action) {
		return this.isVisible.get(action);
	}
}
