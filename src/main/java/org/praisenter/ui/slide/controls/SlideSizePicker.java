package org.praisenter.ui.slide.controls;

import org.praisenter.ui.Icons;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.LastValueNumberStringConverter;
import org.praisenter.ui.translations.Translations;

import atlantafx.base.theme.Styles;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public final class SlideSizePicker extends GridPane {
	private final DoubleProperty slideWidth;
	private final DoubleProperty slideHeight;
	
	private final ObjectProperty<Double> slideWidthAsObject;
	private final ObjectProperty<Double> slideHeightAsObject;
	
	public SlideSizePicker() {
		this.slideWidth = new SimpleDoubleProperty();
		this.slideHeight = new SimpleDoubleProperty();
		
		this.slideWidthAsObject = this.slideWidth.asObject();
		this.slideHeightAsObject = this.slideHeight.asObject();
		
		ObjectConverter<Integer, Double> intToDoubleConverter = new ObjectConverter<Integer, Double>() {
			@Override
			public Double convertFrom(Integer t) {
				if (t == null) return 0.0;
				return t.doubleValue();
			}
			@Override
			public Integer convertTo(Double e) {
				if (e == null) return 0;
				return e.intValue();
			}
		};
		
		TextField txtWidth = new TextField();
		TextFormatter<Integer> tfWidth = new TextFormatter<Integer>(LastValueNumberStringConverter.forInteger(originalValueText -> {
			Platform.runLater(() -> {
				txtWidth.setText(originalValueText);
			});
		}));
		txtWidth.setTextFormatter(tfWidth);
		BindingHelper.bindBidirectional(tfWidth.valueProperty(), this.slideWidthAsObject, intToDoubleConverter);

		TextField txtHeight = new TextField();
		TextFormatter<Integer> tfHeight = new TextFormatter<Integer>(LastValueNumberStringConverter.forInteger(originalValueText -> {
			Platform.runLater(() -> {
				txtHeight.setText(originalValueText);
			});
		}));
		txtHeight.setTextFormatter(tfHeight);
		BindingHelper.bindBidirectional(tfHeight.valueProperty(), this.slideHeightAsObject, intToDoubleConverter);
		
		Label lblW = new Label(Translations.get("slide.width.abbreviation")); 
		lblW.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_SMALL); 
		lblW.setAlignment(Pos.TOP_CENTER); 
		lblW.setMaxWidth(Double.MAX_VALUE);
		
		Label lblH = new Label(Translations.get("slide.height.abbreviation")); 
		lblH.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_SMALL); 
		lblH.setAlignment(Pos.TOP_CENTER); 
		lblH.setMaxWidth(Double.MAX_VALUE);
		
		Region icoX = Icons.getIcon(Icons.CLOSE);
		
//		GridPane gp = new GridPane();
		this.setHgap(4);
		
		this.add(txtWidth, 0, 0);
		this.add(icoX, 1, 0);
		this.add(txtHeight, 2, 0);
		
		this.add(lblW, 0, 1);
		this.add(lblH, 2, 1);
		
//		HBox boxSize = new HBox(2,
//				new VBox(txtWidth, lblW),
//				lblX,
//				new VBox(txtHeight, lblH));
//		boxSize.setAlignment(Pos.CENTER_LEFT);
	}
	
	public double getSlideWidth() {
		return this.slideWidth.get();
	}
	
	public void setSlideWidth(double width) {
		this.slideWidth.set(width);
	}
	
	public DoubleProperty slideWidthProperty() {
		return this.slideWidth;
	}

	public double getSlideHeight() {
		return this.slideHeight.get();
	}
	
	public void setSlideHeight(double height) {
		this.slideHeight.set(height);
	}
	
	public DoubleProperty slideHeightProperty() {
		return this.slideHeight;
	}
}
