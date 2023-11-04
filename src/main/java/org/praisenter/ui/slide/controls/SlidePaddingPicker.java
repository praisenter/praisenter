package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.graphics.SlidePadding;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.EditorFieldGroup;
import org.praisenter.ui.controls.LastValueNumberStringConverter;
import org.praisenter.ui.translations.Translations;

import atlantafx.base.layout.InputGroup;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;

public final class SlidePaddingPicker extends EditorFieldGroup {
	private static final String SLIDE_PADDING_PICKER = "p-slide-padding-picker";
	private static final String SLIDE_PADDING_PICKER_TOP = "p-slide-padding-picker-top";
	private static final String SLIDE_PADDING_PICKER_RIGHT = "p-slide-padding-picker-right";
	private static final String SLIDE_PADDING_PICKER_BOTTOM = "p-slide-padding-picker-bottom";
	private static final String SLIDE_PADDING_PICKER_LEFT = "p-slide-padding-picker-left";
	private static final String SLIDE_PADDING_PICKER_PADDING_BOX = "p-padding-box";
	
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
		txtTop.setMaxWidth(Double.MAX_VALUE);
		tfTop.valueProperty().bindBidirectional(this.top);

		TextField txtRight = new TextField();
		TextFormatter<Double> tfRight = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble());
		txtRight.setTextFormatter(tfRight);
		txtRight.setMaxWidth(Double.MAX_VALUE);
		tfRight.valueProperty().bindBidirectional(this.right);
		
		TextField txtBottom = new TextField();
		TextFormatter<Double> tfBottom = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble());
		txtBottom.setTextFormatter(tfBottom);
		txtBottom.setMaxWidth(Double.MAX_VALUE);
		tfBottom.valueProperty().bindBidirectional(this.bottom);
		
		TextField txtLeft = new TextField();
		TextFormatter<Double> tfLeft = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble());
		txtLeft.setTextFormatter(tfLeft);
		txtLeft.setMaxWidth(Double.MAX_VALUE);
		tfLeft.valueProperty().bindBidirectional(this.left);
		
		Label lblTopAdder = new Label(Translations.get("slide.measure.pixels.abbreviation"));
		lblTopAdder.setAlignment(Pos.CENTER);
		Label lblBottomAdder = new Label(Translations.get("slide.measure.pixels.abbreviation"));
		lblBottomAdder.setAlignment(Pos.CENTER);
		Label lblLeftAdder = new Label(Translations.get("slide.measure.pixels.abbreviation"));
		lblLeftAdder.setAlignment(Pos.CENTER);
		Label lblRightAdder = new Label(Translations.get("slide.measure.pixels.abbreviation"));
		lblRightAdder.setAlignment(Pos.CENTER);
		InputGroup grpTop = new InputGroup(txtTop, lblTopAdder);
		InputGroup grpBottom = new InputGroup(txtBottom, lblBottomAdder);
		InputGroup grpLeft = new InputGroup(txtLeft, lblLeftAdder);
		InputGroup grpRight = new InputGroup(txtRight, lblRightAdder);
		
		grpTop.getStyleClass().add(SLIDE_PADDING_PICKER_TOP);
		grpRight.getStyleClass().add(SLIDE_PADDING_PICKER_RIGHT);
		grpBottom.getStyleClass().add(SLIDE_PADDING_PICKER_BOTTOM);
		grpLeft.getStyleClass().add(SLIDE_PADDING_PICKER_LEFT);
		
		HBox.setHgrow(txtTop, Priority.ALWAYS);
		HBox.setHgrow(txtBottom, Priority.ALWAYS);
		HBox.setHgrow(txtLeft, Priority.ALWAYS);
		HBox.setHgrow(txtRight, Priority.ALWAYS);
		
		Rectangle r = new Rectangle(0, 0, 50, 50);
		r.getStyleClass().add(SLIDE_PADDING_PICKER_PADDING_BOX);
		
		GridPane layout = new GridPane();
		layout.setVgap(10);
		layout.setHgap(10);
		layout.add(grpTop, 0, 0, 3, 1);
		layout.add(grpRight, 2, 1);
		layout.add(grpBottom, 0, 2, 3, 1);
		layout.add(grpLeft, 0, 1);
		layout.add(r, 1, 1);
		layout.setMaxWidth(Double.MAX_VALUE);
		layout.setAlignment(Pos.CENTER);

		GridPane.setHalignment(grpTop, HPos.CENTER);
		GridPane.setHalignment(grpBottom, HPos.CENTER);
		
		this.getChildren().add(layout);
		this.getStyleClass().add(SLIDE_PADDING_PICKER);

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
