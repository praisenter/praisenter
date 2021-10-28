package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.graphics.SlidePadding;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.FormFieldSection;
import org.praisenter.ui.controls.LastValueNumberStringConverter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

public final class SlidePaddingPicker extends FormFieldSection {
	private final ObjectProperty<SlidePadding> value;
	
	private final ObjectProperty<Double> top;
	private final ObjectProperty<Double> right;
	private final ObjectProperty<Double> bottom;
	private final ObjectProperty<Double> left;
	
	public SlidePaddingPicker() {
		this.value = new SimpleObjectProperty<>();
		
		this.top = new SimpleObjectProperty<>();
		this.right = new SimpleObjectProperty<>();
		this.bottom = new SimpleObjectProperty<>();
		this.left = new SimpleObjectProperty<>();
		
		TextField txtTop = new TextField();
		TextFormatter<Double> tfTop = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble());
		txtTop.setTextFormatter(tfTop);
		txtTop.setMaxWidth(50);
		tfTop.valueProperty().bindBidirectional(this.top);

		TextField txtRight = new TextField();
		TextFormatter<Double> tfRight = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble());
		txtRight.setTextFormatter(tfRight);
		txtRight.setMaxWidth(50);
		tfRight.valueProperty().bindBidirectional(this.right);
		
		TextField txtBottom = new TextField();
		TextFormatter<Double> tfBottom = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble());
		txtBottom.setTextFormatter(tfBottom);
		txtBottom.setMaxWidth(50);
		tfBottom.valueProperty().bindBidirectional(this.bottom);
		
		TextField txtLeft = new TextField();
		TextFormatter<Double> tfLeft = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble());
		txtLeft.setTextFormatter(tfLeft);
		txtLeft.setMaxWidth(50);
		tfLeft.valueProperty().bindBidirectional(this.left);
		
		Rectangle r = new Rectangle(0, 0, 50, 50);
		r.getStyleClass().add("p-padding-box");
		
		GridPane layout = new GridPane();
		layout.setVgap(10);
		layout.setHgap(10);
		layout.add(txtTop, 1, 0);
		layout.add(txtRight, 2, 1);
		layout.add(txtBottom, 1, 2);
		layout.add(txtLeft, 0, 1);
		layout.add(r, 1, 1);
		layout.setMaxWidth(170);
		
		GridPane.setHalignment(txtTop, HPos.CENTER);
		GridPane.setHalignment(txtBottom, HPos.CENTER);
		
		this.addField(layout);
		GridPane.setHalignment(layout, HPos.CENTER);

		BindingHelper.bindBidirectional(this.top, this.value, new ObjectConverter<Double, SlidePadding>() {
			@Override
			public SlidePadding convertFrom(Double t) {
				return SlidePaddingPicker.this.getCurrentValue();
			}
			@Override
			public Double convertTo(SlidePadding e) {
				if (e == null) return 0.0;
				return e.getTop();
			}
		});
		
		BindingHelper.bindBidirectional(this.right, this.value, new ObjectConverter<Double, SlidePadding>() {
			@Override
			public SlidePadding convertFrom(Double t) {
				return SlidePaddingPicker.this.getCurrentValue();
			}
			@Override
			public Double convertTo(SlidePadding e) {
				if (e == null) return 0.0;
				return e.getRight();
			}
		});
		
		BindingHelper.bindBidirectional(this.bottom, this.value, new ObjectConverter<Double, SlidePadding>() {
			@Override
			public SlidePadding convertFrom(Double t) {
				return SlidePaddingPicker.this.getCurrentValue();
			}
			@Override
			public Double convertTo(SlidePadding e) {
				if (e == null) return 0.0;
				return e.getBottom();
			}
		});
		
		BindingHelper.bindBidirectional(this.left, this.value, new ObjectConverter<Double, SlidePadding>() {
			@Override
			public SlidePadding convertFrom(Double t) {
				return SlidePaddingPicker.this.getCurrentValue();
			}
			@Override
			public Double convertTo(SlidePadding e) {
				if (e == null) return 0.0;
				return e.getLeft();
			}
		});
	}
	
	private SlidePadding getCurrentValue() {
		return new SlidePadding(
				this.getDoubleValue(this.top), 
				this.getDoubleValue(this.right), 
				this.getDoubleValue(this.bottom), 
				this.getDoubleValue(this.left));
	}
	
	private double getDoubleValue(ObjectProperty<Double> property) {
		Double value = property.get();
		if (value == null) return 0.0;
		return value.doubleValue();
	}
	
	public SlidePadding getValue() {
		return this.value.get();
	}
	
	public void setValue(SlidePadding padding) {
		this.value.set(padding);
	}
	
	public ObjectProperty<SlidePadding> valueProperty() {
		return this.value;
	}
}
