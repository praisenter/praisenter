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
import org.controlsfx.glyphfont.Glyph;
import org.praisenter.data.SingleFileManager;
import org.praisenter.data.workspace.WorkspacePathResolver;
import org.praisenter.data.workspace.Workspaces;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

final class WorkspaceSelectorPane extends VBox {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final ObservableList<Path> workspacePaths;
	private final ObjectProperty<Optional<Path>> value;
	
	private final CompletableFuture<Optional<Path>> future;
	
	public WorkspaceSelectorPane(SingleFileManager<Workspaces> workspacesManager) {
		this.getStyleClass().add("workspace-selector");
		
		this.workspacePaths = FXCollections.observableArrayList();
		this.value = new SimpleObjectProperty<>();
		this.future = new CompletableFuture<Optional<Path>>();
		
		List<Path> paths = workspacesManager.getData().getWorkspaces().stream().map(p -> {
			try {
				return Paths.get(p);
			} catch (Exception ex) {
				LOGGER.warn("Failed to parse path '" + p + "': " + ex.getMessage(), ex);
				return null;
			}
		}).filter(p -> p != null).collect(Collectors.toList());
		this.workspacePaths.addAll(paths);
		
		Path lastSelectedPath = null;
		try {
			lastSelectedPath = Paths.get(workspacesManager.getData().getLastSelectedWorkspace());
		} catch (Exception ex) {
			LOGGER.warn("Failed to parse path '" + workspacesManager.getData().getLastSelectedWorkspace() + "': " + ex.getMessage(), ex);
		}
		
		Label lblSelectAWorkspace = new Label(Translations.get("workspace.title"));
		lblSelectAWorkspace.getStyleClass().add("workspace-title");
		Label lblWorkspaceDescription = new Label(Translations.get("workspace.description"));
		lblWorkspaceDescription.getStyleClass().add("workspace-description");
		lblWorkspaceDescription.setWrapText(true);
		VBox description = new VBox(lblSelectAWorkspace, lblWorkspaceDescription);
		description.getStyleClass().add("workspace-overview");
		
		// drop down of workspaces
		Label lblWorkspace = new Label(Translations.get("workspace"));
		lblWorkspace.setAlignment(Pos.CENTER_LEFT);
		ComboBox<Path> cmbWorkspacePath = new ComboBox<Path>(this.workspacePaths);
		cmbWorkspacePath.setValue(lastSelectedPath);
		cmbWorkspacePath.setMaxWidth(Double.MAX_VALUE);
		// browse button
		Button btnBrowse = new Button(Translations.get("browse"));
		HBox selectorRow = new HBox(lblWorkspace, cmbWorkspacePath, btnBrowse);
		selectorRow.getStyleClass().add("workspace-selection");
		selectorRow.setFillHeight(true);
		selectorRow.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(cmbWorkspacePath, Priority.ALWAYS);
		HBox.setHgrow(lblWorkspace, Priority.NEVER);
		HBox.setHgrow(btnBrowse, Priority.NEVER);
		
		// The selected folder isn't empty and it doesn't look like an existing workspace. If you are creating a new workspace you should choose an empty folder.
		Label lblWorkspaceWarning = new Label(Translations.get("workspace.notEmptyWarning"));
		lblWorkspaceWarning.setWrapText(true);
		Glyph warnIcon = Glyphs.WARN.duplicate();
		warnIcon.setMinWidth(USE_COMPUTED_SIZE);
		warnIcon.setTextOverrun(OverrunStyle.CLIP);
		HBox pathWarning = new HBox(warnIcon, lblWorkspaceWarning);
		pathWarning.getStyleClass().add("workspace-not-empty-warning");
		HBox.setHgrow(warnIcon, Priority.NEVER);
		HBox.setHgrow(lblWorkspaceWarning, Priority.ALWAYS);
		pathWarning.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
			Path path = cmbWorkspacePath.getValue();
			
			WorkspacePathResolver wpr = new WorkspacePathResolver(path);
			boolean isEmpty = false;
			boolean hasWorkspaceConfigurationFile = Files.exists(wpr.getConfigurationFilePath().toAbsolutePath());
			
			try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
				isEmpty = !directory.iterator().hasNext();
	        } catch (Exception ex) {
	        	LOGGER.warn("Failed to check if the path is empty: " + ex.getMessage(), ex);
	        }
			
			if (!isEmpty && !hasWorkspaceConfigurationFile) {
				return true;
			}
			
			return false;
		}, cmbWorkspacePath.valueProperty()));
		
		Button btnCancel = new Button(Translations.get("cancel"));
		Button btnLaunch = new Button(Translations.get("launch"));
		btnLaunch.getStyleClass().add("workspace-launch-button");
		HBox buttonRow = new HBox(btnLaunch, btnCancel);
		buttonRow.getStyleClass().add("workspace-buttons");
		buttonRow.setAlignment(Pos.CENTER_RIGHT);
		
		this.getChildren().addAll(
				description,
				new Separator(),
				selectorRow,
				pathWarning,
				buttonRow);
		
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
			this.value.set(Optional.of(cmbWorkspacePath.getValue()));
			this.future.complete(Optional.of(cmbWorkspacePath.getValue()));
		});
		
		btnCancel.setOnAction(e -> {
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
