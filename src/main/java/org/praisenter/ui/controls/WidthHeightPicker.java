package org.praisenter.ui.controls;

import org.praisenter.ui.Icons;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
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

public final class WidthHeightPicker extends GridPane {
	private final DoubleProperty selectedWidth;
	private final DoubleProperty selectedHeight;
	
	private final ObjectProperty<Double> selectedWidthAsObject;
	private final ObjectProperty<Double> selectedHeightAsObject;
	
	public WidthHeightPicker() {
		this.selectedWidth = new SimpleDoubleProperty();
		this.selectedHeight = new SimpleDoubleProperty();
		
		this.selectedWidthAsObject = this.selectedWidth.asObject();
		this.selectedHeightAsObject = this.selectedHeight.asObject();
		
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
		BindingHelper.bindBidirectional(tfWidth.valueProperty(), this.selectedWidthAsObject, intToDoubleConverter);

		TextField txtHeight = new TextField();
		TextFormatter<Integer> tfHeight = new TextFormatter<Integer>(LastValueNumberStringConverter.forInteger(originalValueText -> {
			Platform.runLater(() -> {
				txtHeight.setText(originalValueText);
			});
		}));
		txtHeight.setTextFormatter(tfHeight);
		BindingHelper.bindBidirectional(tfHeight.valueProperty(), this.selectedHeightAsObject, intToDoubleConverter);
		
		Label lblW = new Label(Translations.get("width.abbreviation")); 
		lblW.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_SMALL); 
		lblW.setAlignment(Pos.TOP_CENTER); 
		lblW.setMaxWidth(Double.MAX_VALUE);
		
		Label lblH = new Label(Translations.get("height.abbreviation")); 
		lblH.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_SMALL); 
		lblH.setAlignment(Pos.TOP_CENTER); 
		lblH.setMaxWidth(Double.MAX_VALUE);
		
		Region icoX = Icons.getIcon(Icons.CLOSE);
		
		this.setHgap(4);
		
		this.add(txtWidth, 0, 0);
		this.add(icoX, 1, 0);
		this.add(txtHeight, 2, 0);
		
		this.add(lblW, 0, 1);
		this.add(lblH, 2, 1);
	}
	
	public double getSelectedWidth() {
		return this.selectedWidth.get();
	}
	
	public void setSelectedWidth(double width) {
		this.selectedWidth.set(width);
	}
	
	public DoubleProperty selectedWidthProperty() {
		return this.selectedWidth;
	}

	public double getSelectedHeight() {
		return this.selectedHeight.get();
	}
	
	public void setSelectedHeight(double height) {
		this.selectedHeight.set(height);
	}
	
	public DoubleProperty selectedHeightProperty() {
		return this.selectedHeight;
	}
}
