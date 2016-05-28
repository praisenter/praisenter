package org.praisenter.javafx.slide.editor;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;

import org.controlsfx.control.SegmentedButton;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.media.MediaPicker;
import org.praisenter.javafx.slide.JavaFXTypeConverter;
import org.praisenter.media.Media;
import org.praisenter.media.MediaType;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.object.MediaObject;

// TODO translate

public final class SlidePaintPicker extends VBox {
	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");

	boolean settingValues = false;
	
	final ObjectProperty<SlidePaint> value = new SimpleObjectProperty<SlidePaint>() {
		public void set(SlidePaint paint) {
			if (paint != null && !settingValues) {
				settingValues = true;
				if (paint instanceof MediaObject) {
					MediaObject mo = ((MediaObject)paint);
					Media media = context.getMediaLibrary().get(mo.getId());
					if (media.getMetadata().getType() == MediaType.IMAGE) {
						cbTypes.setValue(PaintType.IMAGE);
						pkrImage.setValue(media);
					} else if (media.getMetadata().getType() == MediaType.VIDEO) {
						cbTypes.setValue(PaintType.VIDEO);
						pkrVideo.setValue(media);
					} else if (media.getMetadata().getType() == MediaType.AUDIO) {
						cbTypes.setValue(PaintType.AUDIO);
						pkrAudio.setValue(media);
					}
					tglLoop.setSelected(mo.isLoop());
					tglMute.setSelected(mo.isMute());
					for (Toggle toggle : segScaling.getToggleGroup().getToggles()) {
						if (toggle.getUserData() == mo.getScaling()) {
							toggle.setSelected(true);
							break;
						}
					}
				} else if (paint instanceof SlideColor) {
					SlideColor sc = (SlideColor)paint;
					cbTypes.setValue(PaintType.COLOR);
					pkrColor.setValue(JavaFXTypeConverter.toJavaFX(sc));
				} else if (paint instanceof SlideLinearGradient) {
					SlideLinearGradient lg = (SlideLinearGradient)paint;
					cbTypes.setValue(PaintType.GRADIENT);
					pkrGradient.setValue(JavaFXTypeConverter.toJavaFX(lg));
				} else if (paint instanceof SlideRadialGradient) {
					SlideRadialGradient rg = (SlideRadialGradient)paint;
					cbTypes.setValue(PaintType.GRADIENT);
					pkrGradient.setValue(JavaFXTypeConverter.toJavaFX(rg));
				}
				settingValues = false;
			}
			
			// doing this will mean that setting it to null or any other type of paint will do nothing
			// since it will just use the current observables to generate a new paint
			
			// this has the added effect of allowing us to update the paint property without having
			// it go through the conversion process above by calling: set(null);
			super.set(createPaint());
		}
		public void setValue(SlidePaint paint) {
			set(paint);
		}
	};
	
	final PraisenterContext context;
	
	// nodes
	
	final ChoiceBox<PaintType> cbTypes;
	final ColorPicker pkrColor;
	final GradientPicker pkrGradient;
	final MediaPicker pkrImage;
	final MediaPicker pkrVideo;
	final MediaPicker pkrAudio;
	final SegmentedButton segScaling;
	final ToggleButton tglLoop;
	final ToggleButton tglMute;
	
	public SlidePaintPicker(PraisenterContext context, PaintType... types) {
		this.context = context;
		
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				value.set(null);
			}
		};
		
		cbTypes = new ChoiceBox<PaintType>(FXCollections.observableArrayList(types == null ? PaintType.values() : types));
		pkrColor = new ColorPicker();
		pkrColor.managedProperty().bind(pkrColor.visibleProperty());
		pkrColor.valueProperty().addListener(listener);
		
		pkrGradient = new GradientPicker(null);
		pkrGradient.managedProperty().bind(pkrGradient.visibleProperty());
		pkrGradient.valueProperty().addListener(listener);
		
		pkrImage = new MediaPicker(context, MediaType.IMAGE);
		pkrImage.managedProperty().bind(pkrImage.visibleProperty());
		pkrImage.valueProperty().addListener(listener);
		
		pkrVideo = new MediaPicker(context, MediaType.VIDEO);
		pkrVideo.managedProperty().bind(pkrVideo.visibleProperty());
		pkrVideo.valueProperty().addListener(listener);
		
		pkrAudio = new MediaPicker(context, MediaType.AUDIO);
		pkrAudio.managedProperty().bind(pkrAudio.visibleProperty());
		pkrAudio.valueProperty().addListener(listener);
		
		HBox bg = new HBox();
		bg.setSpacing(2);
		bg.getChildren().addAll(cbTypes, pkrColor, pkrGradient, pkrImage, pkrVideo, pkrAudio);
		this.getChildren().add(bg);
		
		ToggleButton tglImageScaleNone = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.IMAGE));
		ToggleButton tglImageScaleNonUniform = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ARROWS_ALT));
		ToggleButton tglImageScaleUniform = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ARROWS));
		tglImageScaleNone.setUserData(ScaleType.NONE);
		tglImageScaleNonUniform.setUserData(ScaleType.NONUNIFORM);
		tglImageScaleUniform.setUserData(ScaleType.UNIFORM);
		this.segScaling = new SegmentedButton(tglImageScaleNone, tglImageScaleNonUniform, tglImageScaleUniform);
		
		this.tglLoop = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.REPEAT));
		this.tglLoop.selectedProperty().addListener(listener);
		
		this.tglMute = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_OFF));
		this.tglMute.selectedProperty().addListener(listener);
		
		HBox loopMute = new HBox();
		loopMute.setSpacing(2);
		loopMute.getChildren().addAll(this.tglLoop, this.tglMute);
		
		pkrColor.setVisible(false);
		pkrGradient.setVisible(false);
		pkrImage.setVisible(false);
		pkrVideo.setVisible(false);
		cbTypes.valueProperty().addListener((obs, ov, nv) -> {
			switch (nv) {
				case COLOR:
					pkrColor.setVisible(true);
					pkrGradient.setVisible(false);
					pkrImage.setVisible(false);
					pkrVideo.setVisible(false);
					pkrAudio.setVisible(false);
					this.getChildren().removeAll(this.segScaling, loopMute);
					break;
				case GRADIENT:
					pkrColor.setVisible(false);
					pkrGradient.setVisible(true);
					pkrImage.setVisible(false);
					pkrVideo.setVisible(false);
					pkrAudio.setVisible(false);
					this.getChildren().removeAll(this.segScaling, loopMute);
					break;
				case IMAGE:
					pkrColor.setVisible(false);
					pkrGradient.setVisible(false);
					pkrImage.setVisible(true);
					pkrVideo.setVisible(false);
					pkrAudio.setVisible(false);
					this.getChildren().removeAll(this.segScaling, loopMute);
					this.getChildren().add(this.segScaling);
					break;
				case AUDIO:
					pkrColor.setVisible(false);
					pkrGradient.setVisible(false);
					pkrImage.setVisible(false);
					pkrVideo.setVisible(false);
					pkrAudio.setVisible(true);
					this.getChildren().removeAll(this.segScaling, loopMute);
					this.getChildren().add(loopMute);
					break;
				case VIDEO:
					pkrColor.setVisible(false);
					pkrGradient.setVisible(false);
					pkrImage.setVisible(false);
					pkrVideo.setVisible(true);
					pkrAudio.setVisible(false);
					this.getChildren().removeAll(this.segScaling, loopMute);
					this.getChildren().addAll(this.segScaling, loopMute);
					break;
				case NONE:
				default:
					// hide all the controls
					pkrColor.setVisible(false);
					pkrGradient.setVisible(false);
					pkrImage.setVisible(false);
					pkrVideo.setVisible(false);
					pkrAudio.setVisible(false);
					this.getChildren().removeAll(this.segScaling, loopMute);
					break;
			}
		});
		
	}
	
	private SlidePaint createPaint() {
		Toggle scaleToggle = segScaling.getToggleGroup().getSelectedToggle();
		ScaleType scaleType = scaleToggle != null && scaleToggle.getUserData() != null ? (ScaleType)scaleToggle.getUserData() : ScaleType.NONE;
		switch (this.cbTypes.getValue()) {
			case COLOR:
				Color color = this.pkrColor.getValue();
				return JavaFXTypeConverter.fromJavaFX(color);
			case GRADIENT:
				Paint paint = this.pkrGradient.getValue();
				if (paint instanceof LinearGradient) {
					LinearGradient lg = (LinearGradient)paint;
					return JavaFXTypeConverter.fromJavaFX(lg);
				} else if (paint instanceof RadialGradient) {
					RadialGradient rg = (RadialGradient)paint;
					return JavaFXTypeConverter.fromJavaFX(rg);
				} else {
					return null;
				}
			case IMAGE:
				if (this.pkrImage.getValue() != null) {
					return new MediaObject(this.pkrImage.getValue().getMetadata().getId(), scaleType, false, false);
				}
				return null;
			case VIDEO:
				if (this.pkrVideo.getValue() != null) {
					return new MediaObject(this.pkrVideo.getValue().getMetadata().getId(), scaleType, tglLoop.isSelected(), tglMute.isSelected());
				}
				return null;
			case AUDIO:
				if (this.pkrAudio.getValue() != null) {
					return new MediaObject(this.pkrAudio.getValue().getMetadata().getId(), ScaleType.NONE, tglLoop.isSelected(), tglMute.isSelected());
				}
				return null;
			default:
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
