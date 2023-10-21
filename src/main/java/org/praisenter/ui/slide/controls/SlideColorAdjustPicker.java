package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.effects.SlideColorAdjust;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.EditorField;
import org.praisenter.ui.controls.EditorFieldGroup;
import org.praisenter.ui.controls.IntegerSliderField;
import org.praisenter.ui.translations.Translations;

import atlantafx.base.controls.ToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

// JAVABUG (L) 08/09/17 [workaround] LabelFormatter doesn't give enough control of the labels https://bugs.openjdk.java.net/browse/JDK-8091345
// JAVABUG (H) 08/09/17 [workaround] The ticks aren't evenly spaced; setting the LabelFormatter seemed to fix it https://bugs.openjdk.java.net/browse/JDK-8164328

public final class SlideColorAdjustPicker extends EditorFieldGroup {
	private final ObjectProperty<SlideColorAdjust> value = new SimpleObjectProperty<SlideColorAdjust>();
	
	private final BooleanProperty enabled;
	private final DoubleProperty hue;
	private final DoubleProperty saturation;
	private final DoubleProperty brightness;
	private final DoubleProperty contrast;

	private final ObjectProperty<Double> hueAsObject;
	private final ObjectProperty<Double> saturationAsObject;
	private final ObjectProperty<Double> brightnessAsObject;
	private final ObjectProperty<Double> contrastAsObject;
	
	public SlideColorAdjustPicker() {
		this.enabled = new SimpleBooleanProperty();
		
		this.hue = new SimpleDoubleProperty();
		this.saturation = new SimpleDoubleProperty();
		this.brightness = new SimpleDoubleProperty();
		this.contrast = new SimpleDoubleProperty();
		
		this.hueAsObject = this.hue.asObject();
		this.saturationAsObject = this.saturation.asObject();
		this.brightnessAsObject = this.brightness.asObject();
		this.contrastAsObject = this.contrast.asObject();

		ToggleSwitch tglEnable = new ToggleSwitch();
		HBox boxEnable = new HBox(tglEnable);
		boxEnable.setAlignment(Pos.CENTER_RIGHT);
		
		IntegerSliderField sldHue = new IntegerSliderField(-1, 1, 0, 255);
		IntegerSliderField sldSaturation = new IntegerSliderField(-1, 1, 0, 255);
		IntegerSliderField sldBrightness = new IntegerSliderField(-1, 1, 0, 255);
		IntegerSliderField sldContrast = new IntegerSliderField(-1, 1, 0, 255);
		
		EditorField fldEnable = new EditorField(Translations.get("slide.coloradjust.enabled"), boxEnable);
		EditorField fldHue = new EditorField(Translations.get("slide.coloradjust.hue"), sldHue);
		EditorField fldSaturation = new EditorField(Translations.get("slide.coloradjust.saturation"), sldSaturation);
		EditorField fldBrightness = new EditorField(Translations.get("slide.coloradjust.brightness"), sldBrightness);
		EditorField fldContrast = new EditorField(Translations.get("slide.coloradjust.contrast"), sldContrast);
		
		fldHue.visibleProperty().bind(this.enabled);
		fldHue.managedProperty().bind(fldHue.visibleProperty());
		fldSaturation.visibleProperty().bind(this.enabled);
		fldSaturation.managedProperty().bind(fldSaturation.visibleProperty());
		fldBrightness.visibleProperty().bind(this.enabled);
		fldBrightness.managedProperty().bind(fldBrightness.visibleProperty());
		fldContrast.visibleProperty().bind(this.enabled);
		fldContrast.managedProperty().bind(fldContrast.visibleProperty());
		
		this.getChildren().addAll(
				fldEnable,
				fldHue,
				fldSaturation,
				fldBrightness,
				fldContrast);
		
		tglEnable.selectedProperty().bindBidirectional(this.enabled);
		sldHue.valueProperty().bindBidirectional(this.hue);
		sldSaturation.valueProperty().bindBidirectional(this.saturation);
		sldBrightness.valueProperty().bindBidirectional(this.brightness);
		sldContrast.valueProperty().bindBidirectional(this.contrast);
		
		BindingHelper.bindBidirectional(this.enabled, this.value, new ObjectConverter<Boolean, SlideColorAdjust>() {
			@Override
			public SlideColorAdjust convertFrom(Boolean t) {
				return SlideColorAdjustPicker.this.getCurrentValue();
			}
			@Override
			public Boolean convertTo(SlideColorAdjust e) {
				if (e == null) return false;
				return true;
			}
		});
		
		BindingHelper.bindBidirectional(this.hueAsObject, this.value, new ObjectConverter<Double, SlideColorAdjust>() {
			@Override
			public SlideColorAdjust convertFrom(Double t) {
				return SlideColorAdjustPicker.this.getCurrentValue();
			}
			@Override
			public Double convertTo(SlideColorAdjust e) {
				if (e == null) return 0.0;
				return e.getHue();
			}
		});
		
		BindingHelper.bindBidirectional(this.saturationAsObject, this.value, new ObjectConverter<Double, SlideColorAdjust>() {
			@Override
			public SlideColorAdjust convertFrom(Double t) {
				return SlideColorAdjustPicker.this.getCurrentValue();
			}
			@Override
			public Double convertTo(SlideColorAdjust e) {
				if (e == null) return 0.0;
				return e.getSaturation();
			}
		});
		
		BindingHelper.bindBidirectional(this.brightnessAsObject, this.value, new ObjectConverter<Double, SlideColorAdjust>() {
			@Override
			public SlideColorAdjust convertFrom(Double t) {
				return SlideColorAdjustPicker.this.getCurrentValue();
			}
			@Override
			public Double convertTo(SlideColorAdjust e) {
				if (e == null) return 0.0;
				return e.getBrightness();
			}
		});
		
		BindingHelper.bindBidirectional(this.contrastAsObject, this.value, new ObjectConverter<Double, SlideColorAdjust>() {
			@Override
			public SlideColorAdjust convertFrom(Double t) {
				return SlideColorAdjustPicker.this.getCurrentValue();
			}
			@Override
			public Double convertTo(SlideColorAdjust e) {
				if (e == null) return 0.0;
				return e.getContrast();
			}
		});
	}
	
	private SlideColorAdjust getCurrentValue() {
		if (this.enabled.get()) {
			SlideColorAdjust adjust = new SlideColorAdjust();
			adjust.setBrightness(this.brightness.get());
			adjust.setContrast(this.contrast.get());
			adjust.setHue(this.hue.get());
			adjust.setSaturation(this.saturation.get());
			return adjust;
		}
		return null;
	}
	
	public void setValue(SlideColorAdjust value) {
		this.value.set(value);
	}
	
	public SlideColorAdjust getValue() {
		return this.value.get();
	}
	
	public ObjectProperty<SlideColorAdjust> valueProperty() {
		return this.value;
	}
}
