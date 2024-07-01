package org.praisenter.ui;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.SingleFileManager;
import org.praisenter.data.workspace.WorkspacePathResolver;
import org.praisenter.data.workspace.Workspaces;
import org.praisenter.ui.translations.Translations;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;

final class WorkspaceSelectionPane extends VBox {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String WORKSPACE_SELECTION_PANE_CLASS 					= "p-workspace-selection-pane";
	private static final String WORKSPACE_SELECTION_PANE_OVERVIEW_CLASS 		= "p-workspace-selection-pane-overview";
	private static final String WORKSPACE_SELECTION_PANE_TITLE_CLASS 			= "p-workspace-selection-pane-title";
	private static final String WORKSPACE_SELECTION_PANE_DESCRIPTION_CLASS 		= "p-workspace-selection-pane-description";
	private static final String WORKSPACE_SELECTION_PANE_SELECTION_CLASS 		= "p-workspace-selection-pane-selection";
	private static final String WORKSPACE_SELECTION_PANE_WARNING_CLASS 			= "p-workspace-selection-pane-warning";
	private static final String WORKSPACE_SELECTION_PANE_LAUNCH_BUTTON_CLASS 	= "p-workspace-selection-pane-launch-button";
	private static final String WORKSPACE_SELECTION_PANE_BUTTONS_CLASS 			= "p-workspace-selection-pane-buttons";
	
	private final Node VALID_ICON = Icons.getIcon(Icons.CHECK, Icons.COLOR_SUCCESS);
	private final Node ERROR_ICON = Icons.getIcon(Icons.ERROR, Icons.COLOR_DANGER);
	
	private final ObservableList<Path> workspacePaths;
	private final ObjectProperty<Optional<Path>> value;
	private final StringProperty statusText;
	private final ObjectProperty<Node> statusIcon;
	private final BooleanProperty pathValid;
	
	private final CompletableFuture<Optional<Path>> future;
	
	public WorkspaceSelectionPane(SingleFileManager<Workspaces> workspacesManager) {
		this.getStyleClass().add(WORKSPACE_SELECTION_PANE_CLASS);
		
		this.workspacePaths = FXCollections.observableArrayList();
		this.value = new SimpleObjectProperty<>();
		this.statusText = new SimpleStringProperty();
		this.statusIcon = new SimpleObjectProperty<>();
		this.pathValid = new SimpleBooleanProperty(false);
		this.future = new CompletableFuture<Optional<Path>>();
		
		List<Path> paths = workspacesManager.getData().getWorkspaces().stream().map(p -> {
			try {
				return Paths.get(p);
			} catch (Exception ex) {
				LOGGER.warn("Failed to parse path '" + p + "': " + ex.getMessage(), ex);
				return null;
			}
		}).filter(p -> p != null).sorted((a, b) -> { 
			return a.toAbsolutePath().toString().compareTo(b.toAbsolutePath().toString());
		}).collect(Collectors.toList());
		this.workspacePaths.addAll(paths);
		
		Path lastSelectedPath = null;
		try {
			String savedPath = workspacesManager.getData().getLastSelectedWorkspace();
			if (savedPath != null) {
				lastSelectedPath = Paths.get(savedPath);
			}
		} catch (Exception ex) {
			LOGGER.warn("Failed to parse path '" + workspacesManager.getData().getLastSelectedWorkspace() + "': " + ex.getMessage(), ex);
		}
		
		Label lblSelectAWorkspace = new Label(Translations.get("workspace.title"));
		lblSelectAWorkspace.getStyleClass().add(WORKSPACE_SELECTION_PANE_TITLE_CLASS);
		Label lblWorkspaceDescription = new Label(Translations.get("workspace.description"));
		lblWorkspaceDescription.getStyleClass().add(WORKSPACE_SELECTION_PANE_DESCRIPTION_CLASS);
		lblWorkspaceDescription.setWrapText(true);
		VBox description = new VBox(lblSelectAWorkspace, lblWorkspaceDescription);
		description.getStyleClass().add(WORKSPACE_SELECTION_PANE_OVERVIEW_CLASS);
		
		// drop down of workspaces
		Label lblWorkspace = new Label(Translations.get("workspace"));
		lblWorkspace.setAlignment(Pos.CENTER_LEFT);
		ComboBox<Path> cmbWorkspacePath = new ComboBox<Path>(this.workspacePaths);
		cmbWorkspacePath.setMaxWidth(Double.MAX_VALUE);
		cmbWorkspacePath.setMinWidth(0);
		cmbWorkspacePath.setEditable(true);
		cmbWorkspacePath.setConverter(new StringConverter<Path>() {
			@Override
			public String toString(Path object) {
				if (object == null) return null;
				return object.toAbsolutePath().toString();
			}
			
			@Override
			public Path fromString(String string) {
				if (string == null || string.isBlank()) return null;
				try {
					return Paths.get(string);
				} catch (Exception ex) {
					return null;
				}
			}
		});
		
		// status icon
		Label lblStatus = new Label();
		lblStatus.getStyleClass().add(WORKSPACE_SELECTION_PANE_WARNING_CLASS);
		lblStatus.graphicProperty().bind(this.statusIcon);
		Tooltip tipStatus = new Tooltip();
		tipStatus.setWrapText(true);
		tipStatus.setPrefWidth(300);
		tipStatus.textProperty().bind(this.statusText);
		lblStatus.setTooltip(tipStatus);
		
		// browse button 
		Button btnBrowse = new Button(null, Icons.getIcon(Icons.FOLDER, Icons.COLOR_FOLDER));
		btnBrowse.setTooltip(new Tooltip(Translations.get("browse")));
		HBox selectorRow = new HBox(lblStatus, cmbWorkspacePath, btnBrowse);
		selectorRow.getStyleClass().add(WORKSPACE_SELECTION_PANE_SELECTION_CLASS);
		selectorRow.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(lblStatus, Priority.NEVER);
		HBox.setHgrow(cmbWorkspacePath, Priority.ALWAYS);
		HBox.setHgrow(btnBrowse, Priority.NEVER);
		
		Button btnCancel = new Button(Translations.get("cancel"));
		Button btnLaunch = new Button(Translations.get("launch"));
		btnLaunch.getStyleClass().add(WORKSPACE_SELECTION_PANE_LAUNCH_BUTTON_CLASS);
		btnLaunch.disableProperty().bind(this.pathValid.not());
		btnLaunch.setDefaultButton(true);
		
		ButtonBar.setButtonData(btnCancel, ButtonData.CANCEL_CLOSE);
		ButtonBar.setButtonData(btnLaunch, ButtonData.OK_DONE);
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.getStyleClass().add(WORKSPACE_SELECTION_PANE_BUTTONS_CLASS);
		buttonBar.getButtons().addAll(btnCancel, btnLaunch);
		
		this.getChildren().addAll(
				description,
				selectorRow,
				buttonBar);
		
		cmbWorkspacePath.valueProperty().addListener((obs, ov, nv) -> {
			Path path = nv;
			
			this.pathValid.set(true);
			this.statusIcon.set(VALID_ICON);
			this.statusText.set(Translations.get("workspace.path.valid"));
			
			// check for a valid path
			if (path == null) {
				this.pathValid.set(false);
				this.statusIcon.set(ERROR_ICON);
				this.statusText.set(Translations.get("workspace.path.invalid"));
				return;
			}
			
			// does the path exist
			if (!Files.exists(path)) {
				return;
			}
			
			// is it a directory
			if (!Files.isDirectory(path)) {
				this.pathValid.set(false);
				this.statusIcon.set(ERROR_ICON);
				this.statusText.set(Translations.get("workspace.path.notDirectory"));
				return;
			}
			
			WorkspacePathResolver wpr = new WorkspacePathResolver(path);
			
			// is it an existing workspace?
			boolean hasWorkspaceConfigurationFile = Files.exists(wpr.getConfigurationFilePath().toAbsolutePath());
			if (hasWorkspaceConfigurationFile) {
				return;
			}
			
			// it's not so check if it's empty
			boolean isEmpty = false;
			try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
				isEmpty = !directory.iterator().hasNext();
	        } catch (Exception ex) {
	        	LOGGER.warn("Failed to check if the path is empty: " + ex.getMessage(), ex);
	        	this.pathValid.set(false);
	        	this.statusIcon.set(ERROR_ICON);
	        	this.statusText.set(Translations.get("workspace.path.error"));
	        	return;
	        }
			
			if (!isEmpty) {
				this.pathValid.set(false);
				this.statusIcon.set(ERROR_ICON);
				this.statusText.set(Translations.get("workspace.path.notEmpty"));
				return;
			}
		});
		
		if (lastSelectedPath != null) {
			cmbWorkspacePath.setValue(lastSelectedPath);
		}
		
		btnBrowse.setOnAction(e -> {
			DirectoryChooser dc = new DirectoryChooser();
			dc.setTitle(Translations.get("workspace.title"));
			File file = dc.showDialog(this.getScene().getWindow());
			
			if (file != null) {
				Path path = file.toPath();
				
				if (!this.workspacePaths.contains(path)) {
					this.workspacePaths.add(path);
				}
				cmbWorkspacePath.setValue(path);
			}
		});
		
		btnLaunch.setOnAction(e -> {
			Path ws = cmbWorkspacePath.getValue();
			LOGGER.debug("User requesting launch using workspace: '{}'", ws.toAbsolutePath());
			
			Optional<Path> value = Optional.of(ws);
			if (!this.future.isDone()) {
				this.value.set(value);
				this.future.complete(value);
			}
		});
		
		btnCancel.setOnAction(e -> {
			LOGGER.debug("User cancelled workspace selection");
			this.value.set(Optional.empty());
			this.future.complete(Optional.empty());
		});
		
		this.value.addListener((obs, ov, nv) -> {
			this.setDisable(true);
		});
	}
	
	public CompletableFuture<Optional<Path>> getSelectedWorkspace() {
		return this.future;
	}
}
