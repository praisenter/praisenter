package org.praisenter.ui.slide.controls;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.graphics.SlideColor;
import org.praisenter.data.slide.graphics.SlideGradient;
import org.praisenter.data.slide.graphics.SlideGradientCycleType;
import org.praisenter.data.slide.graphics.SlideGradientStop;
import org.praisenter.data.slide.graphics.SlideGradientType;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.media.MediaObject;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.EditorField;
import org.praisenter.ui.controls.EditorFieldGroup;
import org.praisenter.ui.slide.convert.PaintConverter;
import org.praisenter.ui.translations.Translations;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public final class SlidePaintPicker extends EditorFieldGroup {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final ObjectProperty<SlidePaint> value;
	
	private final ObjectProperty<PaintType> type;
	private final ObjectProperty<Color> color;
	private final ObjectProperty<SlideGradient> gradient;
	private final ObjectProperty<MediaObject> media;
	
	public SlidePaintPicker(
			GlobalContext context,
			boolean allowNone,
			boolean allowColor,
			boolean allowGradient,
			boolean allowImage,
			boolean allowVideo,
			String label) {
		this.value = new SimpleObjectProperty<>();
		
		this.type = new SimpleObjectProperty<>(PaintType.NONE);
		this.color = new SimpleObjectProperty<>(Color.BLACK);
		this.gradient = new SimpleObjectProperty<>(new SlideGradient(SlideGradientType.LINEAR, 0, 0, 1, 1, SlideGradientCycleType.NONE, new SlideGradientStop(0.0, 0.0, 0.0, 0.0, 1.0), new SlideGradientStop(1.0, 1.0, 1.0, 1.0, 1.0)));
		this.media = new SimpleObjectProperty<>();
		
		ObservableList<Option<PaintType>> types = FXCollections.observableArrayList();
		if (allowNone) types.add(new Option<PaintType>(Translations.get("slide.paint." + PaintType.NONE), PaintType.NONE));
		if (allowColor) types.add(new Option<PaintType>(Translations.get("slide.paint." + PaintType.COLOR), PaintType.COLOR));
		if (allowGradient) types.add(new Option<PaintType>(Translations.get("slide.paint." + PaintType.GRADIENT), PaintType.GRADIENT));
		if (allowImage || allowVideo) {
			types.add(new Option<PaintType>(Translations.get("slide.paint." + PaintType.MEDIA), PaintType.MEDIA));
		}
		
		ChoiceBox<Option<PaintType>> cbType = new ChoiceBox<>(types);
		cbType.setMaxWidth(Double.MAX_VALUE);
		if (types.size() > 0)
			cbType.setValue(types.get(0));
		BindingHelper.bindBidirectional(cbType.valueProperty(), this.type);
		
		// JAVABUG (H) 06/07/2024 Using custom color minimizes wrong stage https://bugs.openjdk.org/browse/JDK-8260024
		ColorPicker pkrColor = new ColorPicker();
		pkrColor.valueProperty().bindBidirectional(this.color);
		pkrColor.setMaxWidth(Double.MAX_VALUE);
		// this is a workaround for the javafx bug above 
		pkrColor.setOnAction(e -> {
			Platform.runLater(() -> {
				context.getStage().toFront();	
			});
			
		});
		
		SlideGradientPicker pkrGradient = new SlideGradientPicker();
		pkrGradient.valueProperty().bindBidirectional(this.gradient);
		
		MediaType[] allowed = null;
		if (allowImage && allowVideo) {
			allowed = new MediaType[] { MediaType.IMAGE, MediaType.VIDEO };
		} else if (allowImage) {
			allowed = new MediaType[] { MediaType.IMAGE };
		} else if (allowVideo) {
			allowed = new MediaType[] { MediaType.VIDEO };
		}
		
		EditorField fldType = new EditorField(label, cbType);
		
		EditorField fldColor = new EditorField(Translations.get("slide.paint." + PaintType.COLOR), pkrColor);
		fldColor.visibleProperty().bind(cbType.valueProperty().isEqualTo(new Option<PaintType>(null, PaintType.COLOR)));
		fldColor.managedProperty().bind(fldColor.visibleProperty());
		
		EditorField fldGradient = new EditorField(pkrGradient);
		fldGradient.visibleProperty().bind(cbType.valueProperty().isEqualTo(new Option<PaintType>(null, PaintType.GRADIENT)));
		fldGradient.managedProperty().bind(fldGradient.visibleProperty());
		
		if (allowImage || allowVideo) {
			MediaObjectPicker pkrMedia = new MediaObjectPicker(context, allowed);
			pkrMedia.valueProperty().bindBidirectional(this.media);
			
			EditorField fldMedia = new EditorField(pkrMedia);
			fldMedia.visibleProperty().bind(cbType.valueProperty().isEqualTo(new Option<PaintType>(null, PaintType.MEDIA)));
			fldMedia.managedProperty().bind(fldMedia.visibleProperty());

			this.getChildren().addAll(
					fldType,
					fldColor,
					fldGradient,
					fldMedia);
			
		} else {
			this.getChildren().addAll(
					fldType,
					fldColor,
					fldGradient);
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
