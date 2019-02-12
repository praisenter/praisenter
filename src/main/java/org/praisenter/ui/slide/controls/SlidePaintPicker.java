package org.praisenter.ui.slide.controls;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.graphics.SlideColor;
import org.praisenter.data.slide.graphics.SlideGradient;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.media.MediaObject;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.slide.convert.PaintConverter;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public final class SlidePaintPicker extends VBox {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final ObjectProperty<SlidePaint> value;
	
	private final ObjectProperty<PaintType> type;
	private final ObjectProperty<Color> color;
	private final ObjectProperty<SlideGradient> gradient;
	private final ObjectProperty<MediaObject> media;
	
	public SlidePaintPicker(GlobalContext context) {
		this(context, true, true, true, true, true);
	}
	
	public SlidePaintPicker(
			GlobalContext context,
			boolean allowNone,
			boolean allowColor,
			boolean allowGradient,
			boolean allowImage,
			boolean allowVideo) {
		this.value= new SimpleObjectProperty<>();
		
		this.type = new SimpleObjectProperty<>();
		this.color = new SimpleObjectProperty<>();
		this.gradient = new SimpleObjectProperty<>();
		this.media = new SimpleObjectProperty<>();
		
		ObservableList<Option<PaintType>> types = FXCollections.observableArrayList();
		if (allowNone) {
			types.add(new Option<PaintType>(Translations.get("slide.paint." + PaintType.NONE), PaintType.NONE));
		}
		types.add(new Option<PaintType>(Translations.get("slide.paint." + PaintType.COLOR), PaintType.COLOR));
		types.add(new Option<PaintType>(Translations.get("slide.paint." + PaintType.GRADIENT), PaintType.GRADIENT));
		if (allowImage || allowVideo) {
			types.add(new Option<PaintType>(Translations.get("slide.paint." + PaintType.MEDIA), PaintType.MEDIA));
		}
		ChoiceBox<Option<PaintType>> cbType = new ChoiceBox<>(types);
		BindingHelper.bindBidirectional(cbType.valueProperty(), this.type);
		
		// TODO maybe replace the color picker with a custom one?
		// https://stackoverflow.com/questions/27171885/display-custom-color-dialog-directly-javafx-colorpicker/27180647#27180647
		ColorPicker pkrColor = new ColorPicker();
		pkrColor.valueProperty().bindBidirectional(this.color);
		
		SlideGradientPicker pkrGradient = new SlideGradientPicker();
		pkrGradient.valueProperty().bindBidirectional(this.gradient);
		
		pkrColor.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
			PaintType type = this.type.get();
			return type == PaintType.COLOR;
		}, this.type));
		pkrColor.managedProperty().bind(pkrColor.visibleProperty());
		
		pkrGradient.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
			PaintType type = this.type.get();
			return type == PaintType.GRADIENT;
		}, this.type));
		pkrGradient.managedProperty().bind(pkrGradient.visibleProperty());
		
		this.getChildren().addAll(
				cbType,
				pkrColor,
				pkrGradient);

		MediaType[] allowed = null;
		if (allowImage && allowVideo) {
			allowed = new MediaType[] { MediaType.IMAGE, MediaType.VIDEO };
		} else if (allowImage) {
			allowed = new MediaType[] { MediaType.IMAGE };
		} else if (allowVideo) {
			allowed = new MediaType[] { MediaType.VIDEO };
		}
		
		if (allowed != null && allowed.length > 0) {
			MediaObjectPicker pkrMedia = new MediaObjectPicker(context, allowed);
			pkrMedia.valueProperty().bindBidirectional(this.media);
			
			pkrMedia.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
				PaintType type = this.type.get();
				return type == PaintType.MEDIA;
			}, this.type));
			pkrMedia.managedProperty().bind(pkrMedia.visibleProperty());
			
			this.getChildren().add(pkrMedia);
		}
		
		BindingHelper.bindBidirectional(this.type, this.value, new ObjectConverter<PaintType, SlidePaint>() {
			@Override
			public SlidePaint convertFrom(PaintType t) {
				return SlidePaintPicker.this.getControlValues();
			}
			@Override
			public PaintType convertTo(SlidePaint e) {
				if (e == null) return PaintType.NONE;
				if (e instanceof SlideColor) return PaintType.COLOR;
				if (e instanceof SlideGradient) return PaintType.GRADIENT;
				if (e instanceof MediaObject) return PaintType.MEDIA;
				LOGGER.warn("The SlidePaint of type '" + e.getClass().getName() + "' is unknown.");
				return PaintType.NONE;
			}
		});
		
		BindingHelper.bindBidirectional(this.color, this.value, new ObjectConverter<Color, SlidePaint>() {
			@Override
			public SlidePaint convertFrom(Color t) {
				return SlidePaintPicker.this.getControlValues();
			}
			@Override
			public Color convertTo(SlidePaint e) {
				// return the current value
				if (e == null || !(e instanceof SlideColor)) return color.get();
				return PaintConverter.toJavaFX((SlideColor)e);
			}
		});
		
		BindingHelper.bindBidirectional(this.gradient, this.value, new ObjectConverter<SlideGradient, SlidePaint>() {
			@Override
			public SlidePaint convertFrom(SlideGradient t) {
				return SlidePaintPicker.this.getControlValues();
			}
			@Override
			public SlideGradient convertTo(SlidePaint e) {
				if (e == null || !(e instanceof SlideGradient)) return gradient.get();
				return (SlideGradient)e;
			}
		});
		
		BindingHelper.bindBidirectional(this.media, this.value, new ObjectConverter<MediaObject, SlidePaint>() {
			@Override
			public SlidePaint convertFrom(MediaObject t) {
				return SlidePaintPicker.this.getControlValues();
			}
			@Override
			public MediaObject convertTo(SlidePaint e) {
				if (e == null || !(e instanceof MediaObject)) return media.get();
				return (MediaObject)e;
			}
		});
	}
	
	private SlidePaint getControlValues() {
		PaintType type = this.type.get();
		if (type == null) return null;
		switch (type) {
			case NONE:
				return null;
			case COLOR:
				return PaintConverter.fromJavaFX(this.color.get());
			case GRADIENT:
				return this.gradient.get();
			case MEDIA:
				return this.media.get();
			default:
				LOGGER.warn("Unknown paint type '" + type + "'.");
				return null;
		}
	}
	
	public SlidePaint getValue() {
		return this.value.get();
	}
	
	public void setValue(SlidePaint paint) {
		this.value.set(paint);
	}
	
	public ObjectProperty<SlidePaint> valueProperty() {
		return this.value;
	}
}
