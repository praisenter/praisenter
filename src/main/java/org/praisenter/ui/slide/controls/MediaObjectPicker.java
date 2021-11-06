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
import org.praisenter.ui.controls.FormFieldSection;
import org.praisenter.ui.controls.WindowHelper;
import org.praisenter.ui.library.LibraryList;
import org.praisenter.ui.library.LibraryListType;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public final class MediaObjectPicker extends FormFieldSection {
	private static final String MEDIA_DIALOG_CSS = "p-media-dialog";
	
	private final ObjectProperty<MediaObject> value;
	
	private final ObjectProperty<Media> media;
	private final BooleanProperty loopEnabled;
	private final BooleanProperty mute;
	private final ObjectProperty<ScaleType> scaleType;
	private final ObjectProperty<SlideColorAdjust> colorAdjust;
	
	private final ObservableList<Media> mediaList;
	
	private Media originalMedia;
	
	public MediaObjectPicker(
			GlobalContext context,
			MediaType... allowedTypes) {
		this(null, context, allowedTypes);
	}
	
	public MediaObjectPicker(
			String label,
			GlobalContext context,
			MediaType... allowedTypes) {
		super(label);
		
		this.value = new SimpleObjectProperty<>();
		
		this.media = new SimpleObjectProperty<>();
		this.loopEnabled = new SimpleBooleanProperty();
		this.mute = new SimpleBooleanProperty();
		this.scaleType = new SimpleObjectProperty<>();
		this.colorAdjust = new SimpleObjectProperty<>();
		
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
		
		Dialog<Media> dlgMedia = new Dialog<>();
		dlgMedia.setTitle(Translations.get("media"));
		dlgMedia.getDialogPane().setContent(lstMedia);
		dlgMedia.setResultConverter((button) -> {
			if (button == ButtonType.OK) {
				List<?> selected = lstMedia.getSelectedItems();
				if (selected.size() > 0) {
					return (Media)selected.get(0);
				}
			}
			// return the original value
			return this.originalMedia;
		});
		dlgMedia.resultProperty().bindBidirectional(this.media);
		dlgMedia.initOwner(context.getStage());
		dlgMedia.initStyle(StageStyle.DECORATED);
		dlgMedia.initModality(Modality.WINDOW_MODAL);
		dlgMedia.setResizable(true);
		dlgMedia.getDialogPane().getStyleClass().add(MEDIA_DIALOG_CSS);
	    dlgMedia.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
		
		Button btnMedia = new Button(Translations.get("slide.media.choose"));
		btnMedia.setAlignment(Pos.BASELINE_LEFT);
		btnMedia.setMaxWidth(Double.MAX_VALUE);
		btnMedia.setOnAction(e -> {
			this.originalMedia = this.media.get();
			dlgMedia.show();
			WindowHelper.centerOnParent(this.getScene().getWindow(), dlgMedia);
		});
		
		CheckBox chkLoop = new CheckBox();
		chkLoop.selectedProperty().bindBidirectional(this.loopEnabled);
		CheckBox chkMute = new CheckBox();
		chkMute.selectedProperty().bindBidirectional(this.mute);
		
		ObservableList<Option<ScaleType>> scaleTypes = FXCollections.observableArrayList();
		scaleTypes.add(new Option<>(Translations.get("slide.media.scale." + ScaleType.NONE), ScaleType.NONE));
		scaleTypes.add(new Option<>(Translations.get("slide.media.scale." + ScaleType.UNIFORM), ScaleType.UNIFORM));
		scaleTypes.add(new Option<>(Translations.get("slide.media.scale." + ScaleType.NONUNIFORM), ScaleType.NONUNIFORM));
		ChoiceBox<Option<ScaleType>> cbScaleType = new ChoiceBox<>(scaleTypes);
		cbScaleType.setMaxWidth(Double.MAX_VALUE);
		
		SlideColorAdjustPicker pkrColorAdjust = new SlideColorAdjustPicker();
		pkrColorAdjust.valueProperty().bindBidirectional(this.colorAdjust);
		
		int fIndex = this.addField(Translations.get("media"), btnMedia);
		this.addField(Translations.get("slide.media.loop"), chkLoop);
		this.addField(Translations.get("slide.media.mute"), chkMute);
		this.addField(Translations.get("slide.media.scale.type"), cbScaleType);
		this.addSubSection(pkrColorAdjust);
		this.showRowsOnly(fIndex);
		
		this.media.addListener((obs, ov, nv) -> {
			if (nv == null) {
				this.showRowsOnly(fIndex);
			} else if (nv.getMediaType() == MediaType.AUDIO) {
				this.showRowsOnly(fIndex, fIndex + 1, fIndex + 2);
			} else if (nv.getMediaType() == MediaType.IMAGE) {
				this.showRowsOnly(fIndex, fIndex + 3, fIndex + 4);
			} else if (nv.getMediaType() == MediaType.VIDEO) {
				this.showRowsOnly(fIndex, fIndex + 1, fIndex + 2, fIndex + 3, fIndex + 4);
			}
		});
		
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
