package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.effects.SlideColorAdjust;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.FormFieldSection;
import org.praisenter.ui.translations.Translations;

import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.theme.Styles;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;

// JAVABUG (L) 08/09/17 [workaround] LabelFormatter doesn't give enough control of the labels https://bugs.openjdk.java.net/browse/JDK-8091345
// JAVABUG (H) 08/09/17 [workaround] The ticks aren't evenly spaced; setting the LabelFormatter seemed to fix it https://bugs.openjdk.java.net/browse/JDK-8164328

public final class SlideColorAdjustPicker extends FormFieldSection {
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
		this(null);
	}
	
	public SlideColorAdjustPicker(String label) {
		super(label);
		
		this.enabled = new SimpleBooleanProperty();
		
		this.hue = new SimpleDoubleProperty();
		this.saturation = new SimpleDoubleProperty();
		this.brightness = new SimpleDoubleProperty();
		this.contrast = new SimpleDoubleProperty();
		
		this.hueAsObject = this.hue.asObject();
		this.saturationAsObject = this.saturation.asObject();
		this.brightnessAsObject = this.brightness.asObject();
		this.contrastAsObject = this.contrast.asObject();
		
		CheckBox enable = new CheckBox();
		enable.selectedProperty().bindBidirectional(this.enabled);
		
		Slider sldHue = this.buildSlider();
		Slider sldSaturation = this.buildSlider();
		Slider sldBrightness = this.buildSlider();
		Slider sldContrast = this.buildSlider();
		
		int fIndex = this.addField(Translations.get("slide.coloradjust.enabled"), enable);
		this.addField(Translations.get("slide.coloradjust.hue"), sldHue);
		this.addField(Translations.get("slide.coloradjust.saturation"), sldSaturation);
		this.addField(Translations.get("slide.coloradjust.brightness"), sldBrightness);
		this.addField(Translations.get("slide.coloradjust.contrast"), sldContrast);
		
		this.showRowsOnly(fIndex);
		this.enabled.addListener((obs, ov, nv) -> {
			if (nv) {
				this.showRows(fIndex, fIndex + 1, fIndex + 2, fIndex + 3, fIndex + 4);
			} else {
				this.showRowsOnly(fIndex);
			}
		});
		
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
	
	private Slider buildSlider() {
		Slider slider = new Slider(-1, 1, 0);
		slider.getStyleClass().add(Styles.SMALL);
		slider.setSkin(new ProgressSliderSkin(slider));
//		slider.setMajorTickUnit(0.2);
//		slider.setMinorTickCount(0);
//		slider.setShowTickMarks(true);
//		slider.setShowTickLabels(true);
//		slider.setLabelFormatter(SLIDER_FORMATTER);
//		slider.setBlockIncrement(0.2);
//		slider.setMinWidth(300);
		
		return slider;
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
