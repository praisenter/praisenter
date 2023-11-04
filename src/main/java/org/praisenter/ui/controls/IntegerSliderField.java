package org.praisenter.ui.controls;

import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.utility.Numbers;

import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.theme.Styles;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public final class IntegerSliderField extends HBox {
	private static final String SLIDER_FIELD_CLASS = "p-slider-field";
	
	private final DoubleProperty value;
	private final DoubleProperty scaled;
	private final ObjectProperty<Double> scaledAsObject;
	
	public IntegerSliderField(double min, double max, double value, double scale) {
		this.value = new SimpleDoubleProperty();
		this.scaled = new SimpleDoubleProperty();
		this.scaledAsObject = this.scaled.asObject();
		
		BindingHelper.bindBidirectional(this.value, this.scaled, new ObjectConverter<Double, Double>() {
			public Double convertFrom(Double t) {
				return t.doubleValue() * scale;
			};
			public Double convertTo(Double e) {
				return e.doubleValue() / scale;
			};
		});
		
		double smin = min * scale;
		double smax = max * scale;
		double svalue = value * scale;
		
		int ticks = (int)(smax - smin) - 1;
		Slider sldOpacity = new Slider(smin, smax, svalue);
		sldOpacity.setSnapToTicks(true);
		sldOpacity.setMinorTickCount(ticks);
		sldOpacity.getStyleClass().add(Styles.SMALL);
		sldOpacity.setSkin(new ProgressSliderSkin(sldOpacity));
		sldOpacity.valueProperty().bindBidirectional(this.scaled);
		
		TextField txtField = new TextField();
		TextFormatter<Double> tf = new TextFormatter<>(new StringConverter<Double>() {
			@Override
			public Double fromString(String s) {
				if (s == null) return 0.0;
				try {
					double val = Double.parseDouble(s);
					return Numbers.clamp(val, smin, smax);
				} catch (Exception ex) {
					return 0.0;
				}
			}
			
			public String toString(Double d) {
				if (d == null) return "0";
				return String.valueOf((int)d.doubleValue());
			}
		});
		txtField.setTextFormatter(tf);
		tf.valueProperty().bindBidirectional(this.scaledAsObject);
		
		HBox.setHgrow(sldOpacity, Priority.ALWAYS);
		
		this.getChildren().addAll(sldOpacity, txtField);
		this.setAlignment(Pos.CENTER_LEFT);
		this.getStyleClass().add(SLIDER_FIELD_CLASS);
	}
	
	public double getValue() {
		return this.value.get();
	}
	
	public void setValue(double value) {
		this.value.set(value);
	}
	
	public DoubleProperty valueProperty() {
		return this.value;
	}
}
