package org.praisenter.ui.slide.controls;

import java.util.List;
import java.util.stream.Stream;

import org.praisenter.async.AsyncHelper;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.effects.SlideColorAdjust;
import org.praisenter.data.slide.graphics.ScaleType;
import org.praisenter.data.slide.media.MediaObject;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.controls.EditorField;
import org.praisenter.ui.controls.EditorFieldGroup;
import org.praisenter.ui.controls.WindowHelper;
import org.praisenter.ui.library.LibraryList;
import org.praisenter.ui.library.LibraryListType;
import org.praisenter.ui.translations.Translations;

import atlantafx.base.controls.ToggleSwitch;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class MediaObjectPicker extends EditorFieldGroup {
	private final ObjectProperty<MediaObject> value;
	
	private final ObjectProperty<Media> media;
	private final BooleanProperty loopEnabled;
	private final BooleanProperty mute;
	private final ObjectProperty<ScaleType> scaleType;
	private final ObjectProperty<SlideColorAdjust> colorAdjust;
	private final BooleanProperty repeatEnabled;
	
	private final ObservableList<Media> mediaList;
	
	private final BooleanBinding hasAudio;
	private final BooleanBinding hasVideo;
	private final BooleanBinding hasImage;
	
	public MediaObjectPicker(
			GlobalContext context,
			MediaType... allowedTypes) {
		this.value = new SimpleObjectProperty<>();
		
		this.media = new SimpleObjectProperty<>();
		this.loopEnabled = new SimpleBooleanProperty();
		this.mute = new SimpleBooleanProperty();
		this.scaleType = new SimpleObjectProperty<>();
		this.colorAdjust = new SimpleObjectProperty<>();
		this.repeatEnabled = new SimpleBooleanProperty();
		
		LibraryListType[] types = new LibraryListType[allowedTypes.length];
		int i = 0;
		for (MediaType t : allowedTypes) {
			switch (t) {
				case IMAGE: types[i++] = LibraryListType.IMAGE; break;
				case VIDEO: types[i++] = LibraryListType.VIDEO; break;
				case AUDIO: types[i++] = LibraryListType.AUDIO; break;
				default: break;
			}
		}
		
		LibraryList lstMedia = new LibraryList(context, Orientation.HORIZONTAL, types);
		lstMedia.setMultiSelectEnabled(false);
		
		FilteredList<Media> filtered = new FilteredList<>(context.getWorkspaceManager().getItemsUnmodifiable(Media.class));
		filtered.setPredicate(m -> {
			MediaType type = m.getMediaType();
			return Stream.of(allowedTypes).anyMatch(t -> t == type);
		});
		this.mediaList = filtered;
		Bindings.bindContent(lstMedia.getItems(), this.mediaList);
		
		Button btnCancel = new Button(Translations.get("cancel"));
		Button btnOk = new Button(Translations.get("ok"));
		btnOk.setDefaultButton(true);
		
		ButtonBar.setButtonData(btnCancel, ButtonData.CANCEL_CLOSE);
		ButtonBar.setButtonData(btnOk, ButtonData.OK_DONE);
		
		Stage dlgMedia = Dialogs.createStageDialog(
				context, 
				Translations.get("media"), 
				StageStyle.DECORATED, 
				Modality.WINDOW_MODAL, 
				lstMedia, 
				btnCancel, btnOk);
		
		dlgMedia.setResizable(true);
		dlgMedia.setWidth(1000);
		dlgMedia.setHeight(600);
		dlgMedia.setMinWidth(850);
		dlgMedia.setMinHeight(500);
		
		lstMedia.setOnDragOver(e -> {
			// only accept drag n drop from outside of the application
			if (e.getGestureSource() == null && e.getDragboard().hasFiles()) {
				e.acceptTransferModes(TransferMode.COPY);
			}
		});
		lstMedia.setOnDragDropped(e -> {
			Dragboard db = e.getDragboard();
			if (e.getGestureSource() == null && db.hasFiles()) {
				context.importFiles(db.getFiles()).thenAccept((items) -> {
					// nothing else to do with the items
				}).exceptionallyCompose(AsyncHelper.onJavaFXThreadAndWait((t) -> {
					Platform.runLater(() -> {
						Alert alert = Dialogs.exception(dlgMedia, t);
						alert.show();
					});
				}));
				e.setDropCompleted(true);
			}
		});
		
		btnCancel.setOnAction(e -> {
			dlgMedia.hide();
		});
		btnOk.setOnAction(e -> {
			dlgMedia.hide();
			List<?> selected = lstMedia.getSelectedItems();
			if (selected.size() > 0) {
				MediaObjectPicker.this.media.set((Media)selected.get(0));
			}
		});
		btnOk.disableProperty().bind(lstMedia.getSelectionModel().selectedItemProperty().isNull());
		
		Button btnMedia = new Button(Translations.get("slide.media.choose"));
		btnMedia.setAlignment(Pos.BASELINE_LEFT);
		btnMedia.setMaxWidth(Double.MAX_VALUE);
		btnMedia.setOnAction(e -> {
			dlgMedia.setWidth(1000);
			dlgMedia.setHeight(600);
			dlgMedia.setMaximized(false);
			WindowHelper.centerOnParent(this.getScene().getWindow(), dlgMedia);
			dlgMedia.show();
		});
		
//		CheckBox chkLoop = new CheckBox();
//		chkLoop.selectedProperty().bindBidirectional(this.loopEnabled);
		ToggleSwitch tglLoop = new ToggleSwitch();
		tglLoop.selectedProperty().bindBidirectional(this.loopEnabled);
		HBox boxLoop = new HBox(tglLoop);
		boxLoop.setAlignment(Pos.CENTER_RIGHT);
		
//		CheckBox chkMute = new CheckBox();
//		chkMute.selectedProperty().bindBidirectional(this.mute);
		ToggleSwitch tglMute = new ToggleSwitch();
		tglMute.selectedProperty().bindBidirectional(this.mute);
		HBox boxMute = new HBox(tglMute);
		boxMute.setAlignment(Pos.CENTER_RIGHT);
		
		ToggleSwitch tglRepeat = new ToggleSwitch();
		tglRepeat.selectedProperty().bindBidirectional(this.repeatEnabled);
		HBox boxRepeat = new HBox(tglRepeat);
		boxRepeat.setAlignment(Pos.CENTER_RIGHT);
		
		ObservableList<Option<ScaleType>> scaleTypes = FXCollections.observableArrayList();
		scaleTypes.add(new Option<>(Translations.get("slide.media.scale." + ScaleType.NONE), ScaleType.NONE));
		scaleTypes.add(new Option<>(Translations.get("slide.media.scale." + ScaleType.UNIFORM), ScaleType.UNIFORM));
		scaleTypes.add(new Option<>(Translations.get("slide.media.scale." + ScaleType.NONUNIFORM), ScaleType.NONUNIFORM));
		ChoiceBox<Option<ScaleType>> cbScaleType = new ChoiceBox<>(scaleTypes);
		cbScaleType.setMaxWidth(Double.MAX_VALUE);
		
		SlideColorAdjustPicker pkrColorAdjust = new SlideColorAdjustPicker();
		pkrColorAdjust.valueProperty().bindBidirectional(this.colorAdjust);

		EditorField fldMedia = new EditorField(Translations.get("media"), btnMedia);
		EditorField fldLoop = new EditorField(Translations.get("slide.media.loop"), boxLoop);
		EditorField fldMute = new EditorField(Translations.get("slide.media.mute"), boxMute);
		EditorField fldRepeat = new EditorField(Translations.get("slide.media.repeat"), boxRepeat);
		EditorField fldScaleType = new EditorField(Translations.get("slide.media.scale.type"), cbScaleType);
		EditorField fldColorAdjust = new EditorField(pkrColorAdjust);
		
		Label lblSelectedMedia = new Label();
		lblSelectedMedia.textProperty().bind(Bindings.createStringBinding(() -> {
			Media m = this.media.get();
			if (m == null) {
				return "No media selected";
			}
			return m.getName();
		}, this.media));
		EditorField fldMediaSelected = new EditorField(null, lblSelectedMedia);
		
		this.hasAudio = Bindings.createBooleanBinding(() -> {
			Media media = this.media.get();
			if (media == null) {
				return false;
			}
			
			MediaType type = media.getMediaType();
			return type == MediaType.VIDEO || type == MediaType.AUDIO;
		}, this.media);
		
		this.hasVideo = Bindings.createBooleanBinding(() -> {
			Media media = this.media.get();
			if (media == null) {
				return false;
			}
			
			MediaType type = media.getMediaType();
			return type == MediaType.VIDEO;
		}, this.media);
		
		this.hasImage = Bindings.createBooleanBinding(() -> {
			Media media = this.media.get();
			if (media == null) {
				return false;
			}
			
			MediaType type = media.getMediaType();
			return type == MediaType.IMAGE;
		}, this.media);
		
		fldLoop.visibleProperty().bind(this.hasAudio);
		fldLoop.managedProperty().bind(fldLoop.visibleProperty());
		fldMute.visibleProperty().bind(this.hasAudio);
		fldMute.managedProperty().bind(fldMute.visibleProperty());
		fldScaleType.visibleProperty().bind(this.hasVideo.or(this.hasImage.and(this.repeatEnabled.not())));
		fldScaleType.managedProperty().bind(fldScaleType.visibleProperty());
		fldColorAdjust.visibleProperty().bind(this.hasVideo.or(this.hasImage));
		fldColorAdjust.managedProperty().bind(fldColorAdjust.visibleProperty());
		fldRepeat.visibleProperty().bind(this.hasImage);
		fldRepeat.managedProperty().bind(fldRepeat.visibleProperty());
		
		this.getChildren().addAll(
				fldMedia,
				fldMediaSelected,
				fldLoop,
				fldMute,
				fldRepeat,
				fldScaleType,
				fldColorAdjust);
		
		BindingHelper.bindBidirectional(cbScaleType.valueProperty(), this.scaleType);
		BindingHelper.bindBidirectional(this.scaleType, this.value, new ObjectConverter<ScaleType, MediaObject>() {
			@Override
			public MediaObject convertFrom(ScaleType t) {
				return MediaObjectPicker.this.getControlValues();
			}
			@Override
			public ScaleType convertTo(MediaObject e) {
				if (e == null) return ScaleType.NONE;
				return e.getScaleType();
			}
		});
		
		BindingHelper.bindBidirectional(this.loopEnabled, this.value, new ObjectConverter<Boolean, MediaObject>() {
			@Override
			public MediaObject convertFrom(Boolean t) {
				return MediaObjectPicker.this.getControlValues();
			}
			@Override
			public Boolean convertTo(MediaObject e) {
				if (e == null) return false;
				return e.isLoopEnabled();
			}
		});
		
		BindingHelper.bindBidirectional(this.mute, this.value, new ObjectConverter<Boolean, MediaObject>() {
			@Override
			public MediaObject convertFrom(Boolean t) {
				return MediaObjectPicker.this.getControlValues();
			}
			@Override
			public Boolean convertTo(MediaObject e) {
				if (e == null) return false;
				return e.isMuted();
			}
		});
		
		BindingHelper.bindBidirectional(this.repeatEnabled, this.value, new ObjectConverter<Boolean, MediaObject>() {
			@Override
			public MediaObject convertFrom(Boolean t) {
				return MediaObjectPicker.this.getControlValues();
			}
			@Override
			public Boolean convertTo(MediaObject e) {
				if (e == null) return false;
				return e.isRepeatEnabled();
			}
		});
		
		BindingHelper.bindBidirectional(this.media, this.value, new ObjectConverter<Media, MediaObject>() {
			@Override
			public MediaObject convertFrom(Media t) {
				return MediaObjectPicker.this.getControlValues();
			}
			@Override
			public Media convertTo(MediaObject e) {
				if (e == null) return null;
				if (e.getMediaId() == null) return null;
				return context.getWorkspaceManager().getItem(Media.class, e.getMediaId());
			}
		});
		
		BindingHelper.bindBidirectional(this.colorAdjust, this.value, new ObjectConverter<SlideColorAdjust, MediaObject>() {
			@Override
			public MediaObject convertFrom(SlideColorAdjust t) {
				return MediaObjectPicker.this.getControlValues();
			}
			@Override
			public SlideColorAdjust convertTo(MediaObject e) {
				if (e == null) return null;
				return e.getColorAdjust();
			}
		});
	}
	
	private MediaObject getControlValues() {
		Media media = this.media.get();
		if (media != null) {
			MediaObject mo = new MediaObject();
			mo.setColorAdjust(this.colorAdjust.get());
			mo.setLoopEnabled(this.loopEnabled.get());
			mo.setMediaId(media.getId());
			mo.setMediaName(media.getName());
			mo.setMediaType(media.getMediaType());
			mo.setMuted(this.mute.get());
			mo.setRepeatEnabled(this.repeatEnabled.get());
			ScaleType st = this.scaleType.get();
			mo.setScaleType(st == null ? ScaleType.NONE : st);
			return mo;
		}
		return null;
	}
	
	public MediaObject getValue() {
		return this.value.get();
	}
	
	public void setValue(MediaObject media) {
		this.value.set(media);
	}
	
	public ObjectProperty<MediaObject> valueProperty() {
		return this.value;
	}
}
