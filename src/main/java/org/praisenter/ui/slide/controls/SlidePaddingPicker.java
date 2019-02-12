package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.graphics.SlidePadding;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.LastValueDoubleStringConverter;
import org.praisenter.ui.translations.Translations;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public final class SlidePaddingPicker extends VBox {
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
		TextFormatter<Double> tfTop = new TextFormatter<Double>(new LastValueDoubleStringConverter());
		txtTop.setTextFormatter(tfTop);
		txtTop.setPrefWidth(50);
		tfTop.valueProperty().bindBidirectional(this.top);

		TextField txtRight = new TextField();
		TextFormatter<Double> tfRight = new TextFormatter<Double>(new LastValueDoubleStringConverter());
		txtRight.setTextFormatter(tfRight);
		txtRight.setPrefWidth(50);
		tfRight.valueProperty().bindBidirectional(this.right);
		
		TextField txtBottom = new TextField();
		TextFormatter<Double> tfBottom = new TextFormatter<Double>(new LastValueDoubleStringConverter());
		txtBottom.setTextFormatter(tfBottom);
		txtBottom.setPrefWidth(50);
		tfBottom.valueProperty().bindBidirectional(this.bottom);
		
		TextField txtLeft = new TextField();
		TextFormatter<Double> tfLeft = new TextFormatter<Double>(new LastValueDoubleStringConverter());
		txtLeft.setTextFormatter(tfLeft);
		txtLeft.setPrefWidth(50);
		tfLeft.valueProperty().bindBidirectional(this.left);
		
		Label lblTop = new Label(Translations.get("slide.padding.top"));
		Label lblRight = new Label(Translations.get("slide.padding.right"));
		Label lblBottom = new Label(Translations.get("slide.padding.bottom"));
		Label lblLeft = new Label(Translations.get("slide.padding.left"));
		
		GridPane layout = new GridPane();
		layout.add(lblTop, 0, 0);
		layout.add(lblRight, 1, 0);
		layout.add(lblBottom, 2, 0);
		layout.add(lblLeft, 3, 0);
		layout.add(txtTop, 0, 1);
		layout.add(txtRight, 1, 1);
		layout.add(txtBottom, 2, 1);
		layout.add(txtLeft, 3, 1);
		
		this.getChildren().addAll(layout);
		
		BindingHelper.bindBidirectional(this.top, this.value, new ObjectConverter<Double, SlidePadding>() {
			@Override
			public SlidePadding convertFrom(Double t) {
				return SlidePaddingPicker.this.getControlValues();
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
				return SlidePaddingPicker.this.getControlValues();
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
				return SlidePaddingPicker.this.getControlValues();
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
				return SlidePaddingPicker.this.getControlValues();
			}
			@Override
			public Double convertTo(SlidePadding e) {
				if (e == null) return 0.0;
				return e.getLeft();
			}
		});
	}
	
	private SlidePadding getControlValues() {
		return new SlidePadding(
			this.top.get(), 
			this.right.get(), 
			this.bottom.get(), 
			this.left.get());
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
