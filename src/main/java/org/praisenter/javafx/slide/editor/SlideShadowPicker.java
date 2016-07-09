package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.slide.JavaFXTypeConverter;
import org.praisenter.slide.graphics.ShadowType;
import org.praisenter.slide.graphics.SlideShadow;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

final class SlideShadowPicker extends VBox {
	
	final ObjectProperty<SlideShadow> value = new SimpleObjectProperty<SlideShadow>();
	
	boolean mutating = false;
	
	final CheckBox chkEnabled;
	final ToggleButton tglInner;
	final ColorPicker pkrColor;
	final Spinner<Double> spnX;
	final Spinner<Double> spnY;
	final Spinner<Double> spnRadius;
	final Spinner<Double> spnSpread;
	
	public SlideShadowPicker() {
		
		this.chkEnabled = new CheckBox();
		this.tglInner = new ToggleButton("inner");
		this.pkrColor = new ColorPicker();
		this.spnX = new Spinner<Double>(-Double.MAX_VALUE, Double.MAX_VALUE, 10.0, 1.0);
		this.spnY = new Spinner<Double>(-Double.MAX_VALUE, Double.MAX_VALUE, 10.0, 1.0);
		this.spnRadius = new Spinner<Double>(0.0, 127.0, 10.0, 1.0);
		this.spnSpread = new Spinner<Double>(0.0, 1.0, 0.0, 0.05);
		
		// setup values
		this.pkrColor.setValue(Color.BLACK);
		this.tglInner.setSelected(false);

		// setup look
		this.pkrColor.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
		this.pkrColor.setStyle("-fx-color-label-visible: false;");
		this.spnX.setPrefWidth(75);
		this.spnY.setPrefWidth(75);
		this.spnRadius.setPrefWidth(75);
		this.spnSpread.setPrefWidth(75);
		this.spnX.setEditable(true);
		this.spnY.setEditable(true);
		this.spnRadius.setEditable(true);
		this.spnSpread.setEditable(true);
		
		// when the check is checked/unchecked, hide/show the other controls
		this.tglInner.managedProperty().bind(this.tglInner.visibleProperty());
		this.pkrColor.managedProperty().bind(this.pkrColor.visibleProperty());
		this.spnX.managedProperty().bind(this.spnX.visibleProperty());
		this.spnY.managedProperty().bind(this.spnY.visibleProperty());
		this.spnRadius.managedProperty().bind(this.spnRadius.visibleProperty());
		this.spnSpread.managedProperty().bind(this.spnSpread.visibleProperty());
		this.chkEnabled.selectedProperty().addListener((obs, ov, nv) -> {
			tglInner.setVisible(nv);
			pkrColor.setVisible(nv);
			spnX.setVisible(nv);
			spnY.setVisible(nv);
			spnRadius.setVisible(nv);
			spnSpread.setVisible(nv);
		});
		
		// by default, hide
		this.chkEnabled.setSelected(false);
		this.tglInner.setVisible(false);
		this.pkrColor.setVisible(false);
		this.spnX.setVisible(false);
		this.spnY.setVisible(false);
		this.spnRadius.setVisible(false);
		this.spnSpread.setVisible(false);
		
		// when the control values change
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (mutating) return;
				mutating = true;
				// set the value
				value.set(getControlValues());
				mutating = false;
			}
		};
		
		this.chkEnabled.selectedProperty().addListener(listener);
		this.tglInner.selectedProperty().addListener(listener);
		this.pkrColor.valueProperty().addListener(listener);
		this.spnX.valueProperty().addListener(listener);
		this.spnY.valueProperty().addListener(listener);
		this.spnRadius.valueProperty().addListener(listener);
		this.spnSpread.valueProperty().addListener(listener);
		
		// when the value is changed externally
		this.value.addListener((obs, ov, nv) -> {
			// update controls
			if (mutating) return;
			mutating = true;
			if (nv != null) {
				chkEnabled.setSelected(true);
				tglInner.setSelected(nv.getType() == ShadowType.INNER);
				pkrColor.setValue(JavaFXTypeConverter.toJavaFX(nv.getColor()));
				spnX.getValueFactory().setValue(nv.getOffsetX());
				spnY.getValueFactory().setValue(nv.getOffsetY());
				spnRadius.getValueFactory().setValue(nv.getRadius());
				spnSpread.getValueFactory().setValue(nv.getSpread());
			} else {
				chkEnabled.setSelected(false);
				// just go back to defaults
				tglInner.setSelected(false);
				pkrColor.setValue(Color.BLACK);
				spnX.getValueFactory().setValue(0.0);
				spnY.getValueFactory().setValue(0.0);
				spnRadius.getValueFactory().setValue(10.0);
				spnSpread.getValueFactory().setValue(0.0);
			}
			mutating = false;
		});
		
		HBox color = new HBox(2, this.chkEnabled, this.tglInner, this.pkrColor);
		HBox offset = new HBox(2, this.spnX, this.spnY);
		HBox size = new HBox(2, this.spnRadius, this.spnSpread);
		
		this.setSpacing(2);
		this.getChildren().addAll(color, offset, size);
	}
	
	private SlideShadow getControlValues() {
		if (this.chkEnabled.isSelected()) {
			return new SlideShadow(
					this.tglInner.isSelected() ? ShadowType.INNER : ShadowType.OUTER,
					JavaFXTypeConverter.fromJavaFX(this.pkrColor.getValue()), 
					this.spnX.getValue(), 
					this.spnY.getValue(), 
					this.spnRadius.getValue(),
					this.spnSpread.getValue());
		} else {
			return null;
		}
	}
	
	public SlideShadow getValue() {
		return this.value.get();
	}
	
	public void setValue(SlideShadow shadow) {
		this.value.set(shadow);
	}
	
	public ObjectProperty<SlideShadow> valueProperty() {
		return this.value;
	}
}
