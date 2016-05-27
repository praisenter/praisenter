package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.GradientPicker;
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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;

// TODO translate

public final class SlidePaintPicker extends GridPane {
	
//	final ObjectProperty<Color> color = new SimpleObjectProperty<Color>();
//	final ObjectProperty<Paint> gradient = new SimpleObjectProperty<Paint>();
//	final ObjectProperty<Media> image = new SimpleObjectProperty<Media>();
//	final ObjectProperty<Media> video = new SimpleObjectProperty<Media>();
//	final ObjectProperty<ScaleType> scaleType = new SimpleObjectProperty<ScaleType>();
//	final BooleanProperty mute = new SimpleBooleanProperty();
//	final BooleanProperty loop = new SimpleBooleanProperty();
	boolean settingValues = false;
	final ObjectProperty<SlidePaint> value = new SimpleObjectProperty<SlidePaint>() {
		public void set(SlidePaint paint) {
			if (paint != null && !settingValues) {
				settingValues = true;
				// FIXME set all the node values
				if (paint instanceof MediaObject) {
					MediaObject mo = ((MediaObject)paint);
					Media media = context.getMediaLibrary().get(mo.getId());
					if (media.getMetadata().getType() == MediaType.IMAGE) {
						pkrImage.setValue(media);
						cbTypes.setValue(PaintType.IMAGE);
					} else if (media.getMetadata().getType() == MediaType.VIDEO) {
						pkrVideo.setValue(media);
						cbTypes.setValue(PaintType.VIDEO);
					}
					chkLoop.setSelected(mo.isLoop());
					chkMute.setSelected(mo.isMute());
					cmbScaling.setValue(mo.getScaling());
				} else if (paint instanceof SlideColor) {
					SlideColor sc = (SlideColor)paint;
					pkrColor.setValue(JavaFXTypeConverter.toJavaFX(sc));
					cbTypes.setValue(PaintType.COLOR);
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
	final ChoiceBox<ScaleType> cmbScaling;
	final CheckBox chkLoop;
	final CheckBox chkMute;
	
	public SlidePaintPicker(PraisenterContext context, PaintType... types) {
		this.context = context;
		
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				value.set(null);
			}
		};
		
		Label lblBackground = new Label("Background");
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
		
		HBox bg = new HBox();
		bg.setSpacing(2);
		bg.getChildren().addAll(cbTypes, pkrColor, pkrGradient, pkrImage, pkrVideo);
		this.add(lblBackground, 0, 0);
		this.add(bg, 1, 0);
		
		Label lblScaling = new Label("Scaling");
		// TODO maybe convert to icons/buttons for the type
		cmbScaling = new ChoiceBox<ScaleType>(FXCollections.observableArrayList(ScaleType.values()));
		cmbScaling.valueProperty().addListener(listener);
		
		
		// TODO convert to toggle buttons
		Label lblLoop = new Label("Loop");
		chkLoop = new CheckBox();
		chkLoop.selectedProperty().addListener(listener);
		
		Label lblMute = new Label("Mute");
		chkMute = new CheckBox();
		chkMute.selectedProperty().addListener(listener);
		
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
					this.getChildren().removeAll(lblScaling, cmbScaling, lblLoop, chkLoop, lblMute, chkMute);
					break;
				case GRADIENT:
					pkrColor.setVisible(false);
					pkrGradient.setVisible(true);
					pkrImage.setVisible(false);
					pkrVideo.setVisible(false);
					this.getChildren().removeAll(lblScaling, cmbScaling, lblLoop, chkLoop, lblMute, chkMute);
					break;
				case IMAGE:
					pkrColor.setVisible(false);
					pkrGradient.setVisible(false);
					pkrImage.setVisible(true);
					pkrVideo.setVisible(false);
					this.getChildren().removeAll(lblScaling, cmbScaling, lblLoop, chkLoop, lblMute, chkMute);
					this.add(lblScaling, 0, 1);
					this.add(cmbScaling, 1, 1);
					break;
				case VIDEO:
					pkrColor.setVisible(false);
					pkrGradient.setVisible(false);
					pkrImage.setVisible(false);
					pkrVideo.setVisible(true);
					this.getChildren().removeAll(lblScaling, cmbScaling, lblLoop, chkLoop, lblMute, chkMute);
					this.add(lblScaling, 0, 1);
					this.add(cmbScaling, 1, 1);
					this.add(lblLoop, 0, 2);
					this.add(chkLoop, 1, 2);
					this.add(lblMute, 0, 3);
					this.add(chkMute, 1, 3);
					break;
				case NONE:
				default:
					// hide all the controls
					pkrColor.setVisible(false);
					pkrGradient.setVisible(false);
					pkrImage.setVisible(false);
					pkrVideo.setVisible(false);
					this.getChildren().removeAll(lblScaling, cmbScaling, lblLoop, chkLoop, lblMute, chkMute);
					break;
			}
		});
		
	}
	
	private SlidePaint createPaint() {
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
					return new MediaObject(this.pkrImage.getValue().getMetadata().getId(), cmbScaling.getValue(), chkLoop.isSelected(), chkMute.isSelected());
				}
				return null;
			case VIDEO:
				if (this.pkrVideo.getValue() != null) {
					return new MediaObject(this.pkrVideo.getValue().getMetadata().getId(), cmbScaling.getValue(), chkLoop.isSelected(), chkMute.isSelected());
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
