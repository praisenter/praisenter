package org.praisenter.javafx.slide;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.javafx.GradientPicker;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.media.MediaPicker;
import org.praisenter.media.Media;
import org.praisenter.media.MediaType;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradient;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.object.MediaObject;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public final class SlidePaintPicker extends VBox {
	
//	final ObjectProperty<Color> color = new SimpleObjectProperty<Color>();
//	final ObjectProperty<Paint> gradient = new SimpleObjectProperty<Paint>();
//	final ObjectProperty<Media> image = new SimpleObjectProperty<Media>();
//	final ObjectProperty<Media> video = new SimpleObjectProperty<Media>();
//	final ObjectProperty<ScaleType> scaleType = new SimpleObjectProperty<ScaleType>();
//	final BooleanProperty mute = new SimpleBooleanProperty();
//	final BooleanProperty loop = new SimpleBooleanProperty();
	
	final ObjectProperty<SlidePaint> paint = new SimpleObjectProperty<SlidePaint>() {
		public void set(SlidePaint paint) {
			if (paint != null) {
				// FIXME set all the node values
				if (paint instanceof MediaObject) {
					MediaObject mo = ((MediaObject)paint);
					Media media = context.getObservableMediaLibrary().getMediaLibrary().get(mo.getId());
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
					pkrColor.setValue(new Color(sc.getRed(), sc.getGreen(), sc.getBlue(), sc.getAlpha()));
					cbTypes.setValue(PaintType.COLOR);
				} else if (paint instanceof SlideLinearGradient) {
					SlideLinearGradient lg = (SlideLinearGradient)paint;
					cbTypes.setValue(PaintType.GRADIENT);
					pkrGradient.setValue(new LinearGradient(
							lg.getStartX(), lg.getStartY(),
							lg.getEndX(), lg.getEndY(), 
							true, getCycleType(lg.getCycleType()), 
							getStops(lg.getStops())));
				} else if (paint instanceof SlideRadialGradient) {
					SlideRadialGradient lg = (SlideRadialGradient)paint;
					cbTypes.setValue(PaintType.GRADIENT);
					pkrGradient.setValue(new RadialGradient(
							0, 0, lg.getCenterX(), lg.getCenterY(),
							lg.getRadius(), 
							true, getCycleType(lg.getCycleType()), 
							getStops(lg.getStops())));
				}
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
	
	public SlidePaintPicker(PraisenterContext context) {
		this.context = context;
		
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				paint.set(null);
			}
		};
		
		Label lblBackground = new Label("Background");
		cbTypes = new ChoiceBox<PaintType>(FXCollections.observableArrayList(PaintType.values()));
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
//		grid.add(lblBackground, 0, 0);
//		grid.add(bg, 1, 0);
		
		Label lblScaling = new Label("Scaling");
		// TODO maybe convert to icons/buttons for the type
		cmbScaling = new ChoiceBox<ScaleType>(FXCollections.observableArrayList(ScaleType.values()));
		cmbScaling.valueProperty().addListener(listener);
		
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
//					grid.getChildren().removeAll(lblScaling, cmbScaling, lblLoop, chkLoop, lblMute, chkMute);
					break;
				case GRADIENT:
					pkrColor.setVisible(false);
					pkrGradient.setVisible(true);
					pkrImage.setVisible(false);
					pkrVideo.setVisible(false);
//					grid.getChildren().removeAll(lblScaling, cmbScaling, lblLoop, chkLoop, lblMute, chkMute);
					break;
				case IMAGE:
					pkrColor.setVisible(false);
					pkrGradient.setVisible(false);
					pkrImage.setVisible(true);
					pkrVideo.setVisible(false);
//					grid.getChildren().removeAll(lblScaling, cmbScaling, lblLoop, chkLoop, lblMute, chkMute);
//					grid.add(lblScaling, 0, 1);
//					grid.add(cmbScaling, 1, 1);
					break;
				case VIDEO:
					pkrColor.setVisible(false);
					pkrGradient.setVisible(false);
					pkrImage.setVisible(false);
					pkrVideo.setVisible(true);
//					grid.getChildren().removeAll(lblScaling, cmbScaling, lblLoop, chkLoop, lblMute, chkMute);
//					grid.add(lblScaling, 0, 1);
//					grid.add(cmbScaling, 1, 1);
//					grid.add(lblLoop, 0, 2);
//					grid.add(chkLoop, 1, 2);
//					grid.add(lblMute, 0, 3);
//					grid.add(chkMute, 1, 3);
					break;
				case NONE:
				default:
					// hide all the controls
					pkrColor.setVisible(false);
					pkrGradient.setVisible(false);
					pkrImage.setVisible(false);
					pkrVideo.setVisible(false);
//					grid.getChildren().removeAll(lblScaling, cmbScaling, lblLoop, chkLoop, lblMute, chkMute);
					break;
			}
		});
		
	}
	
	private SlidePaint createPaint() {
		switch (this.cbTypes.getValue()) {
			case COLOR:
				Color color = this.pkrColor.getValue();
				return new SlideColor(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
			case GRADIENT:
				Paint paint = this.pkrGradient.getValue();
				if (paint instanceof LinearGradient) {
					LinearGradient lg = (LinearGradient)paint;
					return new SlideLinearGradient(lg.getStartX(), lg.getStartY(), lg.getEndX(), lg.getEndY(), getCycleType(lg.getCycleMethod()), getSlideGradientStop(lg.getStops()));
				} else if (paint instanceof RadialGradient) {
					RadialGradient rg = (RadialGradient)paint;
					return new SlideRadialGradient(rg.getCenterX(), rg.getCenterY(), rg.getRadius(), getCycleType(rg.getCycleMethod()), getSlideGradientStop(rg.getStops()));
				} else {
					return null;
				}
			case IMAGE:
				return new MediaObject(this.pkrImage.getValue().getMetadata().getId(), cmbScaling.getValue(), chkLoop.isSelected(), chkMute.isSelected());
			case VIDEO:
				return new MediaObject(this.pkrVideo.getValue().getMetadata().getId(), cmbScaling.getValue(), chkLoop.isSelected(), chkMute.isSelected());
			default:
				return null;
		}
	}
	
	private static final SlideGradientCycleType getCycleType(CycleMethod method) {
		switch (method) {
			case REPEAT:
				return SlideGradientCycleType.REPEAT;
			case REFLECT:
				return SlideGradientCycleType.REFLECT;
			default:
				return SlideGradientCycleType.NONE;
		}
	}
	
	private static final CycleMethod getCycleType(SlideGradientCycleType method) {
		switch (method) {
			case REPEAT:
				return CycleMethod.REPEAT;
			case REFLECT:
				return CycleMethod.REFLECT;
			default:
				return CycleMethod.NO_CYCLE;
		}
	}
	
	private static final List<SlideGradientStop> getSlideGradientStop(List<Stop> stops) {
		List<SlideGradientStop> sps = new ArrayList<SlideGradientStop>();
		for (Stop stop : stops) {
			Color color = stop.getColor();
			sps.add(new SlideGradientStop(stop.getOffset(), new SlideColor(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity())));
		}
		return sps;
	}
	
	private static final List<Stop> getStops(List<SlideGradientStop> stops) {
		List<Stop> sps = new ArrayList<Stop>();
		for (SlideGradientStop stop : stops) {
			SlideColor color = stop.getColor();
			sps.add(new Stop(stop.getOffset(), new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())));
		}
		return sps;
	}
}
