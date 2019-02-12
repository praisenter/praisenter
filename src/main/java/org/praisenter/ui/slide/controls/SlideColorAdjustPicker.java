package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.effects.SlideColorAdjust;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.translations.Translations;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

// JAVABUG (L) 08/09/17 [workaround] LabelFormatter doesn't give enough control of the labels https://bugs.openjdk.java.net/browse/JDK-8091345
// JAVABUG (H) 08/09/17 [workaround] The ticks aren't evenly spaced; setting the LabelFormatter seemed to fix it https://bugs.openjdk.java.net/browse/JDK-8164328

public final class SlideColorAdjustPicker extends VBox {
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
	
//	private static final StringConverter<Double> SLIDER_FORMATTER = new StringConverter<Double>() {
//		@Override
//		public String toString(Double object) {
//			if (Math.abs(object) <= 1e-10)
//				return "0.0";
//			return String.format("%2.1f", object);
//		}
//		@Override
//		public Double fromString(String string) {
//			return Double.parseDouble(string);
//		}
//	};
//	
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
		
		CheckBox enable = new CheckBox(Translations.get("slide.coloradjust.enabled"));
		enable.selectedProperty().bindBidirectional(this.enabled);
		
		Slider sldHue = this.buildSlider();
		Slider sldSaturation = this.buildSlider();
		Slider sldBrightness = this.buildSlider();
		Slider sldContrast = this.buildSlider();
		
//		Spinner<Double> spnHue = this.buildSpinner();
//		Spinner<Double> spnSaturation = this.buildSpinner();
//		Spinner<Double> spnBrightness = this.buildSpinner();
//		Spinner<Double> spnContrast = this.buildSpinner();
		
		GridPane layout = new GridPane();
		layout.setVgap(2);
		layout.setHgap(2);
		layout.add(new Label(Translations.get("slide.coloradjust.hue")), 0, 0);
		layout.add(sldHue, 1, 0);
		layout.add(new Label(Translations.get("slide.coloradjust.saturation")), 0, 1);
		layout.add(sldSaturation, 1, 1);
		layout.add(new Label(Translations.get("slide.coloradjust.brightness")), 0, 2);
		layout.add(sldBrightness, 1, 2);
		layout.add(new Label(Translations.get("slide.coloradjust.contrast")), 0, 3);
		layout.add(sldContrast, 1, 3);
		
		layout.visibleProperty().bind(this.enabled);
		layout.managedProperty().bind(layout.visibleProperty());
		
		this.setSpacing(2);
		this.getChildren().addAll(
				enable,
				layout);

//		spnHue.getValueFactory().valueProperty().bindBidirectional(this.hueAsObject);
		sldHue.valueProperty().bindBidirectional(this.hue);
		
//		spnSaturation.getValueFactory().valueProperty().bindBidirectional(this.saturationAsObject);
		sldSaturation.valueProperty().bindBidirectional(this.saturation);
		
//		spnBrightness.getValueFactory().valueProperty().bindBidirectional(this.brightnessAsObject);
		sldBrightness.valueProperty().bindBidirectional(this.brightness);
		
//		spnContrast.getValueFactory().valueProperty().bindBidirectional(this.contrastAsObject);
		sldContrast.valueProperty().bindBidirectional(this.contrast);
		
		BindingHelper.bindBidirectional(this.enabled, this.value, new ObjectConverter<Boolean, SlideColorAdjust>() {
			@Override
			public SlideColorAdjust convertFrom(Boolean t) {
				return SlideColorAdjustPicker.this.getControlValues();
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
				return SlideColorAdjustPicker.this.getControlValues();
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
				return SlideColorAdjustPicker.this.getControlValues();
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
				return SlideColorAdjustPicker.this.getControlValues();
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
				return SlideColorAdjustPicker.this.getControlValues();
			}
			@Override
			public Double convertTo(SlideColorAdjust e) {
				if (e == null) return 0.0;
				return e.getContrast();
			}
		});
	}
	
	private SlideColorAdjust getControlValues() {
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
		
//		slider.setMajorTickUnit(0.2);
//		slider.setMinorTickCount(0);
//		slider.setShowTickMarks(true);
//		slider.setShowTickLabels(true);
//		slider.setLabelFormatter(SLIDER_FORMATTER);
//		slider.setBlockIncrement(0.2);
//		slider.setMinWidth(300);
		
		return slider;
	}
	
//	private Spinner<Double> buildSpinner() {
//		Spinner<Double> spinner = new Spinner<Double>(-1.0, 1.0, 0, 0.1);
//		
//		spinner.setEditable(true);
//		spinner.setPrefWidth(75);
//		TextInputFieldEventFilter.applyTextInputFieldEventFilter(spinner);
//		
//		return spinner;
//	}
	
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
