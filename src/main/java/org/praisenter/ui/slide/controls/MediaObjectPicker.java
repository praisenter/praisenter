package org.praisenter.ui.slide.controls;

import java.util.List;
import java.util.stream.Stream;

import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.effects.SlideColorAdjust;
import org.praisenter.data.slide.graphics.ScaleType;
import org.praisenter.data.slide.media.MediaObject;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.library.LibraryList;
import org.praisenter.ui.translations.Translations;

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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public final class MediaObjectPicker extends VBox {
	private final ObjectProperty<MediaObject> value;
	
	private final ObjectProperty<Media> media;
	private final BooleanProperty loopEnabled;
	private final BooleanProperty mute;
	private final ObjectProperty<ScaleType> scaleType;
	private final ObjectProperty<SlideColorAdjust> colorAdjust;
	
	public MediaObjectPicker(
			GlobalContext context,
			MediaType... allowedTypes) {
		this.value = new SimpleObjectProperty<>();
		
		this.media = new SimpleObjectProperty<>();
		this.loopEnabled = new SimpleBooleanProperty();
		this.mute = new SimpleBooleanProperty();
		this.scaleType = new SimpleObjectProperty<>();
		this.colorAdjust = new SimpleObjectProperty<>();
		
		LibraryList lstMedia = new LibraryList(context, Orientation.HORIZONTAL);
		lstMedia.setMultiSelectEnabled(false);
		FilteredList<Media> filtered = new FilteredList<>(context.getDataManager().getItemsUnmodifiable(Media.class));
		filtered.setPredicate(m -> {
			MediaType type = m.getMediaType();
			return Stream.of(allowedTypes).anyMatch(t -> t == type);
		});
		Bindings.bindContent(lstMedia.getItems(), filtered); 
		
		Dialog<Media> dlgMedia = new Dialog<>();
		dlgMedia.setTitle(Translations.get("media"));
		dlgMedia.getDialogPane().setContent(lstMedia);
		dlgMedia.setResultConverter((button) -> {
			List<?> selected = lstMedia.getSelectedItems();
			if (selected.size() > 0) {
				return (Media)selected.get(0);
			}
			return null;
		});
		dlgMedia.resultProperty().bindBidirectional(this.media);
		dlgMedia.initOwner(context.getStage());
		dlgMedia.initStyle(StageStyle.UTILITY);
		dlgMedia.initModality(Modality.WINDOW_MODAL);
		dlgMedia.setResizable(true);
	    dlgMedia.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
		
		Button btnMedia = new Button(Translations.get("slide.media.choose"));
		btnMedia.setOnAction(e -> {
			dlgMedia.show();
		});
		
		CheckBox chkLoop = new CheckBox(Translations.get("slide.media.loop"));
		chkLoop.selectedProperty().bindBidirectional(this.loopEnabled);
		CheckBox chkMute = new CheckBox(Translations.get("slide.media.mute"));
		chkMute.selectedProperty().bindBidirectional(this.mute);
		
		ObservableList<Option<ScaleType>> scaleTypes = FXCollections.observableArrayList();
		scaleTypes.add(new Option<>(Translations.get("slide.media.scale." + ScaleType.NONE), ScaleType.NONE));
		scaleTypes.add(new Option<>(Translations.get("slide.media.scale." + ScaleType.UNIFORM), ScaleType.UNIFORM));
		scaleTypes.add(new Option<>(Translations.get("slide.media.scale." + ScaleType.NONUNIFORM), ScaleType.NONUNIFORM));
		ChoiceBox<Option<ScaleType>> cbScaleType = new ChoiceBox<>(scaleTypes);
		
		SlideColorAdjustPicker pkrColorAdjust = new SlideColorAdjustPicker();
		pkrColorAdjust.valueProperty().bindBidirectional(this.colorAdjust);
		
		HBox loopMute = new HBox(chkLoop, chkMute);
		loopMute.visibleProperty().bind(this.createMediaTypeVisibleBinding(MediaType.AUDIO, MediaType.VIDEO));
		loopMute.managedProperty().bind(loopMute.visibleProperty());
		
		cbScaleType.visibleProperty().bind(this.createMediaTypeVisibleBinding(MediaType.IMAGE, MediaType.VIDEO));
		cbScaleType.managedProperty().bind(cbScaleType.visibleProperty());
		
		pkrColorAdjust.visibleProperty().bind(this.createMediaTypeVisibleBinding(MediaType.IMAGE, MediaType.VIDEO));
		pkrColorAdjust.managedProperty().bind(pkrColorAdjust.visibleProperty());
		
		this.getChildren().addAll(
				btnMedia,
				loopMute,
				cbScaleType,
				pkrColorAdjust);
		
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
		
		BindingHelper.bindBidirectional(this.media, this.value, new ObjectConverter<Media, MediaObject>() {
			@Override
			public MediaObject convertFrom(Media t) {
				return MediaObjectPicker.this.getControlValues();
			}
			@Override
			public Media convertTo(MediaObject e) {
				if (e == null) return null;
				if (e.getMediaId() == null) return null;
				return context.getDataManager().getItem(Media.class, e.getMediaId());
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
			mo.setScaleType(this.scaleType.get());
			return mo;
		}
		return null;
	}
	
	private BooleanBinding createMediaTypeVisibleBinding(MediaType... visibleWhen) {
		return Bindings.createBooleanBinding(() -> {
			Media media = this.media.get();
			if (media == null) return false;
			if (Stream.of(visibleWhen).anyMatch(t -> t == media.getMediaType())) return true;
			return false;
		}, this.media);
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
