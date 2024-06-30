package org.praisenter.ui.library;

import java.io.File;
import java.nio.file.Path;

import org.praisenter.data.ImportExportFormat;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Icons;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.EditorField;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

final class LibraryExportPane extends BorderPane {
	private static final String LIBRARY_LIST_EXPORT_PANE_CSS = "p-library-list-export-pane";
	
	private final ObjectProperty<ImportExportFormat> exportFormat;
	private final ObjectProperty<Path> exportPath;
	
	private final ObjectProperty<ExportRequest> value;
	
	public LibraryExportPane(GlobalContext context) {
		this.exportFormat = new SimpleObjectProperty<ImportExportFormat>(ImportExportFormat.PRAISENTER3);
		this.exportPath = new SimpleObjectProperty<>();
		this.value = new SimpleObjectProperty<ExportRequest>();
		
		this.getStyleClass().add(LIBRARY_LIST_EXPORT_PANE_CSS);
		
		ObservableList<Option<ImportExportFormat>> formatOptions = FXCollections.observableArrayList();
		formatOptions.add(new Option<>(Translations.get("action.export.format." + ImportExportFormat.PRAISENTER3), ImportExportFormat.PRAISENTER3));
		formatOptions.add(new Option<>(Translations.get("action.export.format." + ImportExportFormat.RAW), ImportExportFormat.RAW));
		ChoiceBox<Option<ImportExportFormat>> cbExportFormat = new ChoiceBox<>(formatOptions);
		cbExportFormat.setMaxWidth(Double.MAX_VALUE);
		cbExportFormat.setValue(new Option<>("", ImportExportFormat.PRAISENTER3));
		BindingHelper.bindBidirectional(cbExportFormat.valueProperty(), this.exportFormat);
		
		TextField txtExportPath = new TextField();
		txtExportPath.textProperty().bind(Bindings.createStringBinding(() -> {
			Path path = this.exportPath.get();
			if (path == null)
				return null;
			return path.toAbsolutePath().toString();
		}, this.exportPath));
		
		Button btnBrowse = new Button(null, Icons.getIcon(Icons.FOLDER, Icons.COLOR_FOLDER));
		btnBrowse.setTooltip(new Tooltip(Translations.get("browse")));
		HBox selectorRow = new HBox(txtExportPath, btnBrowse);
		selectorRow.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(txtExportPath, Priority.ALWAYS);
		HBox.setHgrow(btnBrowse, Priority.NEVER);
		
		btnBrowse.setOnAction(e -> {
			FileChooser chooser = new FileChooser();
	    	chooser.setInitialFileName(Translations.get("action.export.filename") + ".zip");
	    	chooser.setTitle(Translations.get("action.export"));
	    	chooser.getExtensionFilters().add(new ExtensionFilter(Translations.get("action.export.filetype"), "*.zip"));
	    	File file = chooser.showSaveDialog(context.getStage());
	    	
	    	if (file != null) {
	    		this.exportPath.set(file.toPath());
	    	}
		});
		
		EditorField fldFormat = new EditorField(
				Translations.get("action.export.format"), 
				Translations.get("action.export.format.description"), 
				cbExportFormat, 
				EditorField.LAYOUT_VERTICAL);
		
		EditorField fldPath = new EditorField(
				Translations.get("action.export.path"), 
				Translations.get("action.export.path.description"), 
				selectorRow, 
				EditorField.LAYOUT_VERTICAL);
		
		VBox layout = new VBox(
				fldFormat,
				fldPath);
		
		this.setCenter(layout);
		
		BindingHelper.bindBidirectional(this.exportFormat, this.value, new ObjectConverter<ImportExportFormat, ExportRequest>() {
			@Override
			public ExportRequest convertFrom(ImportExportFormat t) {
				return LibraryExportPane.this.getCurrentValue();
			}
			@Override
			public ImportExportFormat convertTo(ExportRequest e) {
				if (e == null) return ImportExportFormat.PRAISENTER3;
				return e.getFormat();
			}
		});
		
		BindingHelper.bindBidirectional(this.exportPath, this.value, new ObjectConverter<Path, ExportRequest>() {
			@Override
			public ExportRequest convertFrom(Path t) {
				return LibraryExportPane.this.getCurrentValue();
			}
			@Override
			public Path convertTo(ExportRequest e) {
				if (e == null) return null;
				return e.getPath();
			}
		});
	}
	
	private ExportRequest getCurrentValue() {
		ImportExportFormat format = this.exportFormat.get();
		if (format == null)
			return null;
		
		Path path = this.exportPath.get();
		if (path == null)
			return null;
		
		return new ExportRequest(format, path);
	}

	public ExportRequest getValue() {
		return this.value.get();
	}
	
	public void setValue(ExportRequest value) {
		this.value.set(value);
	}
	
	public ObjectProperty<ExportRequest> valueProperty() {
		return this.value;
	}
}
