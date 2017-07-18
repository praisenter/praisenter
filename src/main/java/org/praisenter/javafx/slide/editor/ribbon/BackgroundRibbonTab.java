package org.praisenter.javafx.slide.editor.ribbon;

import org.controlsfx.control.SegmentedButton;
import org.praisenter.MediaType;
import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.command.ActionEditCommand;
import org.praisenter.javafx.media.MediaPicker;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.converters.PaintConverter;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.commands.BackgroundEditCommand;
import org.praisenter.javafx.slide.editor.controls.SlideGradientPicker;
import org.praisenter.media.Media;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.object.MediaObject;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

final class BackgroundRibbonTab extends ComponentEditorRibbonTab {
	private final ComboBox<Option<PaintType>> cmbTypes;
	private final ColorPicker pkrColor;
	private final SlideGradientPicker pkrGradient;
	private final MediaPicker pkrImage;
	private final MediaPicker pkrVideo;
	private final SegmentedButton segScaling;
	private final ToggleButton tglLoop;
	private final ToggleButton tglMute;

	private static final Color DEFAULT_PAINT = new Color(0, 0, 0, 1);
	private static final SlideLinearGradient DEFAULT_GRADIENT = new SlideLinearGradient(0, 0, 0, 1, SlideGradientCycleType.NONE, new SlideGradientStop(0, 0, 0, 0, 1), new SlideGradientStop(1, 0, 0, 0, 0.5));
	
	public BackgroundRibbonTab(SlideEditorContext context) {
		super(context, "Background");
		
		ObservableList<Option<PaintType>> paintTypes = FXCollections.observableArrayList();
		paintTypes.add(new Option<PaintType>("None", PaintType.NONE));
		paintTypes.add(new Option<PaintType>("Color", PaintType.COLOR));
		paintTypes.add(new Option<PaintType>("Gradient", PaintType.GRADIENT));
		paintTypes.add(new Option<PaintType>("Image", PaintType.IMAGE));
		paintTypes.add(new Option<PaintType>("Video", PaintType.VIDEO));
		
		this.cmbTypes = new ComboBox<Option<PaintType>>(paintTypes);
		this.cmbTypes.setMaxWidth(80);

		this.pkrColor = new ColorPicker();
		this.pkrColor.setValue(Color.WHITE);
		this.pkrColor.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
		this.pkrColor.managedProperty().bind(pkrColor.visibleProperty());
		this.pkrColor.setStyle("-fx-color-label-visible: false;");
		
		this.pkrGradient = new SlideGradientPicker();
		this.pkrGradient.setValue(new SlideLinearGradient());
		this.pkrGradient.managedProperty().bind(pkrGradient.visibleProperty());
		
		this.pkrImage = new MediaPicker(context.getPraisenterContext(), MediaType.IMAGE);
		this.pkrImage.setValue(null);
		this.pkrImage.managedProperty().bind(pkrImage.visibleProperty());
		
		this.pkrVideo = new MediaPicker(context.getPraisenterContext(), MediaType.VIDEO);
		this.pkrVideo.setValue(null);
		this.pkrVideo.managedProperty().bind(pkrVideo.visibleProperty());
		
		ToggleButton tglImageScaleNone = new ToggleButton("", ApplicationGlyphs.MEDIA_SCALE_NONE.duplicate());
		ToggleButton tglImageScaleNonUniform = new ToggleButton("", ApplicationGlyphs.MEDIA_SCALE_NONUNIFORM.duplicate());
		ToggleButton tglImageScaleUniform = new ToggleButton("", ApplicationGlyphs.MEDIA_SCALE_UNIFORM.duplicate());
		tglImageScaleNone.setSelected(true);
		tglImageScaleNone.setUserData(ScaleType.NONE);
		tglImageScaleNonUniform.setUserData(ScaleType.NONUNIFORM);
		tglImageScaleUniform.setUserData(ScaleType.UNIFORM);
		this.segScaling = new SegmentedButton(tglImageScaleNone, tglImageScaleNonUniform, tglImageScaleUniform);
		
		this.tglLoop = new ToggleButton("", ApplicationGlyphs.MEDIA_LOOP.duplicate());
		this.tglLoop.setSelected(false);
		
		this.tglMute = new ToggleButton("", ApplicationGlyphs.MEDIA_MUTE.duplicate());
		this.tglMute.setSelected(false);
		
		this.pkrColor.setVisible(false);
		this.pkrGradient.setVisible(false);
		this.pkrImage.setVisible(false);

		this.cmbTypes.setValue(new Option<PaintType>("", PaintType.NONE));
		
		togglePaintType(PaintType.NONE);
		
		// tooltips
		
		this.cmbTypes.setTooltip(new Tooltip("The backgound type"));
		this.pkrColor.setTooltip(new Tooltip("Choose the background color"));
		this.pkrGradient.setTooltip(new Tooltip("Choose the background gradient"));
		this.pkrImage.setTooltip(new Tooltip("Choose the background image"));
		this.pkrVideo.setTooltip(new Tooltip("Choose the background video"));
		tglImageScaleNone.setTooltip(new Tooltip("No scaling"));
		tglImageScaleUniform.setTooltip(new Tooltip("Keep aspect ratio"));
		tglImageScaleNonUniform.setTooltip(new Tooltip("Scale to fit"));
		this.tglLoop.setTooltip(new Tooltip("Toggles looping of the media"));
		this.tglMute.setTooltip(new Tooltip("Toggles muting the media"));
		
		// layout

		HBox row1 = new HBox(2, this.cmbTypes, this.pkrColor, this.pkrGradient, this.pkrImage, this.pkrVideo);
		HBox row2 = new HBox(2, this.segScaling, this.tglLoop, this.tglMute);
		//HBox row3 = new HBox(2, mnuPaintType, this.pkrColor, this.pkrGradient);
		VBox layout = new VBox(2, row1, row2);
		this.container.setCenter(layout);
	
		// events
		
		this.context.selectedProperty().addListener((obs, ov, nv) -> {
			this.mutating = true;
			if (nv != null) {
				setControlValues(nv.getBackground());
				setDisable(false);
			} else {
				setControlValues(null);
				setDisable(true);
			}
			this.mutating = false;
		});
		
		InvalidationListener listener = obs -> {
			if (this.mutating) return;
			ObservableSlideRegion<?> comp = context.getSelected();
			if (comp != null) {
				SlidePaint oldValue = comp.getBackground();
				SlidePaint newValue = getControlValues();
				this.applyCommand(
						new BackgroundEditCommand(oldValue, newValue, comp, context.selectedProperty(), this.cmbTypes,
						new ActionEditCommand(null, (self) -> { 
								setControlValues(oldValue); 
							}, (self) -> { 
								setControlValues(newValue); 
							})));
			}
		};
		
		this.cmbTypes.valueProperty().addListener(listener);
		this.pkrColor.valueProperty().addListener(listener);
		this.pkrGradient.valueProperty().addListener(listener);
		this.pkrImage.valueProperty().addListener(listener);
		this.pkrVideo.valueProperty().addListener(listener);
		this.segScaling.getToggleGroup().selectedToggleProperty().addListener(listener);
		this.tglLoop.selectedProperty().addListener(listener);
		this.tglMute.selectedProperty().addListener(listener);
		
		this.cmbTypes.valueProperty().addListener((obs, ov, nv) -> {
			togglePaintType(nv.getValue());
		});

	}
	
	private void togglePaintType(PaintType type) {
		switch (type) {
			case COLOR:
				this.pkrColor.setVisible(true);
				this.pkrColor.setDisable(false);
				this.pkrGradient.setVisible(false);
				this.pkrImage.setVisible(false);
				this.pkrVideo.setVisible(false);
				this.segScaling.setDisable(true);
				this.tglLoop.setDisable(true);
				this.tglMute.setDisable(true);
				break;
			case GRADIENT:
				this.pkrColor.setVisible(false);
				this.pkrGradient.setVisible(true);
				this.pkrImage.setVisible(false);
				this.pkrVideo.setVisible(false);
				this.segScaling.setDisable(true);
				this.tglLoop.setDisable(true);
				this.tglMute.setDisable(true);
				break;
			case IMAGE:
				this.pkrColor.setVisible(false);
				this.pkrGradient.setVisible(false);
				this.pkrImage.setVisible(true);
				this.pkrVideo.setVisible(false);
				this.segScaling.setDisable(false);
				this.tglLoop.setDisable(true);
				this.tglMute.setDisable(true);
				break;
			case VIDEO:
				this.pkrColor.setVisible(false);
				this.pkrGradient.setVisible(false);
				this.pkrImage.setVisible(false);
				this.pkrVideo.setVisible(true);
				this.segScaling.setDisable(false);
				this.tglLoop.setDisable(false);
				this.tglMute.setDisable(false);
				break;
			case NONE:
			default:
				// hide all the controls
				this.pkrColor.setVisible(true);
				this.pkrColor.setDisable(true);
				this.pkrGradient.setVisible(false);
				this.pkrImage.setVisible(false);
				this.pkrVideo.setVisible(false);
				this.segScaling.setDisable(true);
				this.tglLoop.setDisable(true);
				this.tglMute.setDisable(true);
				break;
		}
	}
	
	private SlidePaint getControlValues() {
		Toggle scaleToggle = segScaling.getToggleGroup().getSelectedToggle();
		ScaleType scaleType = scaleToggle != null && scaleToggle.getUserData() != null ? (ScaleType)scaleToggle.getUserData() : ScaleType.NONE;
		
		if (this.cmbTypes.getValue() == null) {
			return null;
		}
		
		switch (this.cmbTypes.getValue().getValue()) {
			case COLOR:
				Color color = this.pkrColor.getValue();
				return PaintConverter.fromJavaFX(color);
			case GRADIENT:
				return this.pkrGradient.getValue();
			case IMAGE:
				if (this.pkrImage.getValue() != null) {
					return new MediaObject(this.pkrImage.getValue().getId(), this.pkrImage.getValue().getName(), this.pkrImage.getValue().getType(), scaleType, false, false);
				}
				return null;
			case VIDEO:
				if (this.pkrVideo.getValue() != null) {
					return new MediaObject(this.pkrVideo.getValue().getId(), this.pkrVideo.getValue().getName(), this.pkrImage.getValue().getType(), scaleType, tglLoop.isSelected(), tglMute.isSelected());
				}
				return null;
			case AUDIO:
				return null;
			default:
				return null;
		}
	}
	
	private void setControlValues(SlidePaint paint) {
		if (paint == null) {
			cmbTypes.setValue(new Option<PaintType>("", PaintType.NONE));
		} else {
			if (paint instanceof MediaObject) {
				MediaObject mo = ((MediaObject)paint);
				Media media = this.context.getPraisenterContext().getMediaLibrary().get(mo.getId());
				// the media could have been removed, so check for null
				if (media == null) {
					cmbTypes.setValue(new Option<PaintType>("", PaintType.NONE));
				} else {
					if (media.getType() == MediaType.IMAGE) {
						cmbTypes.setValue(new Option<PaintType>("", PaintType.IMAGE));
						pkrImage.setValue(media);
					} else if (media.getType() == MediaType.VIDEO) {
						cmbTypes.setValue(new Option<PaintType>("", PaintType.VIDEO));
						pkrVideo.setValue(media);
					}
					tglLoop.setSelected(mo.isLoop());
					tglMute.setSelected(mo.isMute());
					for (Toggle toggle : segScaling.getButtons()) {
						if (toggle.getUserData() == mo.getScaling()) {
							toggle.setSelected(true);
							break;
						}
					}
				}
			} else if (paint instanceof SlideColor) {
				SlideColor sc = (SlideColor)paint;
				cmbTypes.setValue(new Option<PaintType>("", PaintType.COLOR));
				pkrColor.setValue(PaintConverter.toJavaFX(sc));
			} else if (paint instanceof SlideLinearGradient) {
				SlideLinearGradient lg = (SlideLinearGradient)paint;
				cmbTypes.setValue(new Option<PaintType>("", PaintType.GRADIENT));
				pkrGradient.setValue(lg);
			} else if (paint instanceof SlideRadialGradient) {
				SlideRadialGradient rg = (SlideRadialGradient)paint;
				cmbTypes.setValue(new Option<PaintType>("", PaintType.GRADIENT));
				pkrGradient.setValue(rg);
			}
		}
	}
}
