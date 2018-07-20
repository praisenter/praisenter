package org.praisenter.javafx.slide.editor.controls;

import org.praisenter.slide.effects.SlideColorAdjust;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

// JAVABUG (L) 08/09/17 [workaround] LabelFormatter doesn't give enough control of the labels https://bugs.openjdk.java.net/browse/JDK-8091345
// JAVABUG (H) 08/09/17 [workaround] The ticks aren't evenly spaced; setting the LabelFormatter seemed to fix it https://bugs.openjdk.java.net/browse/JDK-8164328

final class ColorAdjustPickerPane extends VBox {
	private final ObjectProperty<SlideColorAdjust> adjustment = new SimpleObjectProperty<SlideColorAdjust>();
	
	private final CheckBox enable;
	
	private final Slider sldHue;
	private final Slider sldSaturation;
	private final Slider sldBrightness;
	private final Slider sldContrast;
	
	private final Spinner<Double> spnHue;
	private final Spinner<Double> spnSaturation;
	private final Spinner<Double> spnBrightness;
	private final Spinner<Double> spnContrast;
	
	private final DoubleProperty hueProperty;
	private final DoubleProperty saturationProperty;
	private final DoubleProperty brightnessProperty;
	private final DoubleProperty contrastProperty;
	
	private boolean mutating = false;
	
	private static final StringConverter<Double> SLIDER_FORMATTER = new StringConverter<Double>() {
		@Override
		public String toString(Double object) {
			if (Math.abs(object) <= 1e-10)
				return "0.0";
			return String.format("%2.1f", object);
		}
		@Override
		public Double fromString(String string) {
			return Double.parseDouble(string);
		}
	};
	
	public ColorAdjustPickerPane() {
		this.enable = new CheckBox("Enabled");
		
		this.sldHue = this.buildSlider();
		this.sldSaturation = this.buildSlider();
		this.sldBrightness = this.buildSlider();
		this.sldContrast = this.buildSlider();
		
		this.spnHue = this.buildSpinner();
		this.spnSaturation = this.buildSpinner();
		this.spnBrightness = this.buildSpinner();
		this.spnContrast = this.buildSpinner();
		
		this.setSpacing(2);
		this.getChildren().addAll(
				this.enable,
				new Label("Hue"),
				new HBox(2, this.sldHue, this.spnHue), 
				new Label("Saturation"),
				new HBox(2, this.sldSaturation, this.spnSaturation), 
				new Label("Brightness"),
				new HBox(2, this.sldBrightness, this.spnBrightness), 
				new Label("Contrast"),
				new HBox(2, this.sldContrast, this.spnContrast));
		
		// have to keep references since they are weak
		this.hueProperty = DoubleProperty.doubleProperty(this.spnHue.getValueFactory().valueProperty());
		this.saturationProperty = DoubleProperty.doubleProperty(this.spnSaturation.getValueFactory().valueProperty());
		this.brightnessProperty = DoubleProperty.doubleProperty(this.spnBrightness.getValueFactory().valueProperty());
		this.contrastProperty = DoubleProperty.doubleProperty(this.spnContrast.getValueFactory().valueProperty());
		
		this.hueProperty.bindBidirectional(this.sldHue.valueProperty());
		this.saturationProperty.bindBidirectional(this.sldSaturation.valueProperty());
		this.brightnessProperty.bindBidirectional(this.sldBrightness.valueProperty());
		this.contrastProperty.bindBidirectional(this.sldContrast.valueProperty());
		
		InvalidationListener listener = obs -> {
			if (this.mutating) return;
			this.mutating = true;
			if (this.enable.isSelected()) {
				this.adjustment.set(new SlideColorAdjust(
						this.sldHue.getValue(), 
						this.sldSaturation.getValue(), 
						this.sldBrightness.getValue(), 
						this.sldContrast.getValue()));
			} else {
				this.adjustment.setValue(null);
			}
			this.mutating = false;
		};
		
		this.enable.selectedProperty().addListener((obs, ov, nv) -> {
			listener.invalidated(obs);
		});
		this.sldHue.valueProperty().addListener((obs, ov, nv) -> {
			if (!this.sldHue.isValueChanging()) {
				listener.invalidated(obs);
			}
		});
		this.sldSaturation.valueProperty().addListener((obs, ov, nv) -> {
			if (!this.sldSaturation.isValueChanging()) {
				listener.invalidated(obs);
			}
		});
		this.sldBrightness.valueProperty().addListener((obs, ov, nv) -> {
			if (!this.sldBrightness.isValueChanging()) {
				listener.invalidated(obs);
			}
		});
		this.sldContrast.valueProperty().addListener((obs, ov, nv) -> {
			if (!this.sldContrast.isValueChanging()) {
				listener.invalidated(obs);
			}
		});
		
		ChangeListener<Boolean> changing = (obs, ov, nv) -> {
			if (!nv) {
				listener.invalidated(obs);
			}
		};
		this.sldHue.valueChangingProperty().addListener(changing);
		this.sldSaturation.valueChangingProperty().addListener(changing);
		this.sldBrightness.valueChangingProperty().addListener(changing);
		this.sldContrast.valueChangingProperty().addListener(changing);
		
		this.sldHue.disableProperty().bind(this.enable.selectedProperty().not());
		this.sldSaturation.disableProperty().bind(this.enable.selectedProperty().not());
		this.sldBrightness.disableProperty().bind(this.enable.selectedProperty().not());
		this.sldContrast.disableProperty().bind(this.enable.selectedProperty().not());
		
		this.adjustment.addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			this.mutating = true;
			if (nv != null) {
				this.enable.setSelected(true);
				this.sldHue.setValue(nv.getHue());
				this.sldSaturation.setValue(nv.getSaturation());
				this.sldBrightness.setValue(nv.getBrightness());
				this.sldContrast.setValue(nv.getContrast());
			} else {
				this.enable.setSelected(false);
				this.sldHue.setValue(0);
				this.sldSaturation.setValue(0);
				this.sldBrightness.setValue(0);
				this.sldContrast.setValue(0);
			}
			this.mutating = false;
		});
	}
	
	private Slider buildSlider() {
		Slider slider = new Slider(-1, 1, 0);
		
		slider.setMajorTickUnit(0.2);
		slider.setMinorTickCount(0);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);
		slider.setLabelFormatter(SLIDER_FORMATTER);
		slider.setBlockIncrement(0.2);
		slider.setMinWidth(300);
		
		return slider;
	}
	
	private Spinner<Double> buildSpinner() {
		Spinner<Double> spinner = new Spinner<Double>(-1.0, 1.0, 0, 0.1);
		
		spinner.setEditable(true);
		spinner.setPrefWidth(75);
		
		return spinner;
	}
	
	public void setValue(SlideColorAdjust value) {
		this.adjustment.set(value);
	}
	
	public SlideColorAdjust getValue() {
		return this.adjustment.get();
	}
	
	public ObjectProperty<SlideColorAdjust> valueProperty() {
		return this.adjustment;
	}
}
