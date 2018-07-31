package org.praisenter.ui;

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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;

public class ApplicationState {
	private final Application application;
	private final Stage stage;
	private final ObjectProperty<Scene> scene;
	private final ObjectProperty<Node> focusOwner;
	private final BooleanProperty windowFocused;
	private final StringProperty selectedText;
	private final BooleanProperty textSelected;
	private final ObjectProperty<ApplicationPane> applicationPane;
	private final ObjectProperty<DocumentPane> documentPane;
	
	public ApplicationState(Application application, Stage stage) {
		this.application = application;
		this.stage = stage;
		this.scene = new SimpleObjectProperty<>();
		this.focusOwner = new SimpleObjectProperty<>();
		this.windowFocused = new SimpleBooleanProperty();
		this.selectedText = new SimpleStringProperty();
		this.textSelected = new SimpleBooleanProperty();
		this.applicationPane = new SimpleObjectProperty<>();
		this.documentPane = new SimpleObjectProperty<>();
		
		this.scene.bind(this.stage.sceneProperty());
		this.windowFocused.bind(this.stage.focusedProperty());
		
		this.scene.addListener((obs, ov, nv) -> {
			this.focusOwner.unbind();
			if (nv != null) {
				this.focusOwner.bind(nv.focusOwnerProperty());
			}
		});
		
		this.focusOwner.addListener((obs, ov, nv) -> {
			// attach/detach selection change event handler if text input
			this.selectedText.unbind();
			this.applicationPane.setValue(null);
			if (nv != null) {
				if (nv instanceof TextInputControl) {
					this.selectedText.bind(((TextInputControl)nv).selectedTextProperty());
				}
				
				// walk up the tree from the focused node to the top
				// looking for an Application Pane
				Node node = nv;
				while (node != null) {
					if (node instanceof ApplicationPane) {
						this.applicationPane.set((ApplicationPane)node);
					}
					if (node instanceof DocumentPane) {
						this.documentPane.set((DocumentPane)node);
					}
					node = node.getParent();
				}
			}
		});
		
		this.textSelected.bind(Bindings.createBooleanBinding(() -> {
			return this.selectedText.get() != null;
		}, this.selectedText));
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
	
	public ApplicationPane getApplicationPane() {
		return this.applicationPane.get();
	}
	
	public ReadOnlyObjectProperty<ApplicationPane> applicationPaneProperty() {
		return this.applicationPane;
	}
	
	public DocumentPane getDocumentPane() {
		return this.documentPane.get();
	}
	
	public ReadOnlyObjectProperty<DocumentPane> documentPaneProperty() {
		return this.documentPane;
	}
}
