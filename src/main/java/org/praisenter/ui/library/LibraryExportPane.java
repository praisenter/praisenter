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
	
	private final ObjectProperty<ImportExportFormat> bibleExportFormat;
	private final ObjectProperty<ImportExportFormat> songExportFormat;
	private final ObjectProperty<ImportExportFormat> slideExportFormat;
	private final ObjectProperty<ImportExportFormat> mediaExportFormat;
	
	private final ObjectProperty<Path> exportPath;
	
	private final ObjectProperty<ExportRequest> value;
	
	public LibraryExportPane(GlobalContext context) {
		this.bibleExportFormat = new SimpleObjectProperty<ImportExportFormat>(ImportExportFormat.PRAISENTER3);
		this.songExportFormat = new SimpleObjectProperty<ImportExportFormat>(ImportExportFormat.PRAISENTER3);
		this.slideExportFormat = new SimpleObjectProperty<ImportExportFormat>(ImportExportFormat.PRAISENTER3);
		this.mediaExportFormat = new SimpleObjectProperty<ImportExportFormat>(ImportExportFormat.PRAISENTER3);
		this.exportPath = new SimpleObjectProperty<>();
		this.value = new SimpleObjectProperty<ExportRequest>();
		
		this.getStyleClass().add(LIBRARY_LIST_EXPORT_PANE_CSS);
		
		ObservableList<Option<ImportExportFormat>> bibleFormatOptions = FXCollections.observableArrayList();
		bibleFormatOptions.add(new Option<>(Translations.get("action.export.format." + ImportExportFormat.PRAISENTER3), ImportExportFormat.PRAISENTER3));
		ChoiceBox<Option<ImportExportFormat>> cbBibleExportFormat = new ChoiceBox<>(bibleFormatOptions);
		cbBibleExportFormat.setMaxWidth(Double.MAX_VALUE);
		cbBibleExportFormat.setValue(new Option<>("", ImportExportFormat.PRAISENTER3));
		BindingHelper.bindBidirectional(cbBibleExportFormat.valueProperty(), this.bibleExportFormat);
		
		ObservableList<Option<ImportExportFormat>> slideFormatOptions = FXCollections.observableArrayList();
		slideFormatOptions.add(new Option<>(Translations.get("action.export.format." + ImportExportFormat.PRAISENTER3), ImportExportFormat.PRAISENTER3));
		ChoiceBox<Option<ImportExportFormat>> cbSlideExportFormat = new ChoiceBox<>(slideFormatOptions);
		cbSlideExportFormat.setMaxWidth(Double.MAX_VALUE);
		cbSlideExportFormat.setValue(new Option<>("", ImportExportFormat.PRAISENTER3));
		BindingHelper.bindBidirectional(cbSlideExportFormat.valueProperty(), this.slideExportFormat);
		
		ObservableList<Option<ImportExportFormat>> mediaFormatOptions = FXCollections.observableArrayList();
		mediaFormatOptions.add(new Option<>(Translations.get("action.export.format." + ImportExportFormat.PRAISENTER3), ImportExportFormat.PRAISENTER3));
		mediaFormatOptions.add(new Option<>(Translations.get("action.export.format." + ImportExportFormat.RAW), ImportExportFormat.RAW));
		ChoiceBox<Option<ImportExportFormat>> cbMediaExportFormat = new ChoiceBox<>(mediaFormatOptions);
		cbMediaExportFormat.setMaxWidth(Double.MAX_VALUE);
		cbMediaExportFormat.setValue(new Option<>("", ImportExportFormat.PRAISENTER3));
		BindingHelper.bindBidirectional(cbMediaExportFormat.valueProperty(), this.mediaExportFormat);
		
		ObservableList<Option<ImportExportFormat>> songFormatOptions = FXCollections.observableArrayList();
		songFormatOptions.add(new Option<>(Translations.get("action.export.format." + ImportExportFormat.PRAISENTER3), ImportExportFormat.PRAISENTER3));
		songFormatOptions.add(new Option<>(Translations.get("action.export.format." + ImportExportFormat.OPENLYRICSSONG), ImportExportFormat.OPENLYRICSSONG));
		songFormatOptions.add(new Option<>(Translations.get("action.export.format." + ImportExportFormat.CHORDPRO), ImportExportFormat.CHORDPRO));
		ChoiceBox<Option<ImportExportFormat>> cbSongExportFormat = new ChoiceBox<>(songFormatOptions);
		cbSongExportFormat.setMaxWidth(Double.MAX_VALUE);
		cbSongExportFormat.setValue(new Option<>("", ImportExportFormat.PRAISENTER3));
		BindingHelper.bindBidirectional(cbSongExportFormat.valueProperty(), this.songExportFormat);
		
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
		
		EditorField fldBibleFormat = new EditorField(
				Translations.get("action.export.format.bible"), 
				Translations.get("action.export.format.bible.description"), 
				cbBibleExportFormat, 
				EditorField.LAYOUT_VERTICAL);

		EditorField fldSlideFormat = new EditorField(
				Translations.get("action.export.format.slide"), 
				Translations.get("action.export.format.slide.description"), 
				cbSlideExportFormat, 
				EditorField.LAYOUT_VERTICAL);
		
		EditorField fldMediaFormat = new EditorField(
				Translations.get("action.export.format.media"), 
				Translations.get("action.export.format.media.description"), 
				cbMediaExportFormat, 
				EditorField.LAYOUT_VERTICAL);
		
		EditorField fldSongFormat = new EditorField(
				Translations.get("action.export.format.song"), 
				Translations.get("action.export.format.song.description"), 
				cbSongExportFormat, 
				EditorField.LAYOUT_VERTICAL);
		
		EditorField fldPath = new EditorField(
				Translations.get("action.export.path"), 
				Translations.get("action.export.path.description"), 
				selectorRow, 
				EditorField.LAYOUT_VERTICAL);
		
		VBox layout = new VBox(
				fldBibleFormat,
				fldSlideFormat,
				fldMediaFormat,
				fldSongFormat,
				fldPath);
		
		this.setCenter(layout);
		
		BindingHelper.bindBidirectional(this.bibleExportFormat, this.value, new ObjectConverter<ImportExportFormat, ExportRequest>() {
			@Override
			public ExportRequest convertFrom(ImportExportFormat t) {
				return LibraryExportPane.this.getCurrentValue();
			}
			@Override
			public ImportExportFormat convertTo(ExportRequest e) {
				if (e == null) return ImportExportFormat.PRAISENTER3;
				return e.getBibleFormat();
			}
		});
		
		BindingHelper.bindBidirectional(this.slideExportFormat, this.value, new ObjectConverter<ImportExportFormat, ExportRequest>() {
			@Override
			public ExportRequest convertFrom(ImportExportFormat t) {
				return LibraryExportPane.this.getCurrentValue();
			}
			@Override
			public ImportExportFormat convertTo(ExportRequest e) {
				if (e == null) return ImportExportFormat.PRAISENTER3;
				return e.getSlideFormat();
			}
		});
		
		BindingHelper.bindBidirectional(this.mediaExportFormat, this.value, new ObjectConverter<ImportExportFormat, ExportRequest>() {
			@Override
			public ExportRequest convertFrom(ImportExportFormat t) {
				return LibraryExportPane.this.getCurrentValue();
			}
			@Override
			public ImportExportFormat convertTo(ExportRequest e) {
				if (e == null) return ImportExportFormat.PRAISENTER3;
				return e.getMediaFormat();
			}
		});
		
		BindingHelper.bindBidirectional(this.songExportFormat, this.value, new ObjectConverter<ImportExportFormat, ExportRequest>() {
			@Override
			public ExportRequest convertFrom(ImportExportFormat t) {
				return LibraryExportPane.this.getCurrentValue();
			}
			@Override
			public ImportExportFormat convertTo(ExportRequest e) {
				if (e == null) return ImportExportFormat.PRAISENTER3;
				return e.getSongFormat();
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
		ImportExportFormat bibleFormat = this.bibleExportFormat.get();
		if (bibleFormat == null)
			return null;
		
		ImportExportFormat slideFormat = this.slideExportFormat.get();
		if (slideFormat == null)
			return null;
		
		ImportExportFormat mediaFormat = this.mediaExportFormat.get();
		if (mediaFormat == null)
			return null;
		
		ImportExportFormat songFormat = this.songExportFormat.get();
		if (songFormat == null)
			return null;
		
		Path path = this.exportPath.get();
		if (path == null)
			return null;
		
		return new ExportRequest(bibleFormat, slideFormat, mediaFormat, songFormat, path);
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
