package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.slide.JavaFXTypeConverter;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.graphics.ShadowType;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlideShadow;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class FontGlowRibbonTab extends EditorRibbonTab {

	boolean mutating = false;
	
	private final CheckBox chkEnabled;
	private final ToggleButton tglInner;
	private final ColorPicker pkrColor;
	private final Spinner<Double> spnX;
	private final Spinner<Double> spnY;
	private final Spinner<Double> spnRadius;
	private final Spinner<Double> spnSpread;

	public FontGlowRibbonTab() {
		super("Font Glow");

		this.chkEnabled = new CheckBox();
		this.tglInner = new ToggleButton("inner");
		this.pkrColor = new ColorPicker();
		this.spnX = new Spinner<Double>(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 1.0);
		this.spnY = new Spinner<Double>(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 1.0);
		this.spnRadius = new Spinner<Double>(0.0, 127.0, 10.0, 1.0);
		this.spnSpread = new Spinner<Double>(0.0, 1.0, 0.0, 0.05);
		
		// setup values
		this.pkrColor.setValue(Color.BLACK);
		this.tglInner.setSelected(false);

		// setup look
		this.pkrColor.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
		this.pkrColor.setStyle("-fx-color-label-visible: false;");
		this.spnX.setPrefWidth(60);
		this.spnY.setPrefWidth(60);
		this.spnRadius.setPrefWidth(60);
		this.spnSpread.setPrefWidth(60);
		this.spnX.setEditable(true);
		this.spnY.setEditable(true);
		this.spnRadius.setEditable(true);
		this.spnSpread.setEditable(true);
		
		this.chkEnabled.selectedProperty().addListener((obs, ov, nv) -> {
			tglInner.setDisable(!nv);
			pkrColor.setDisable(!nv);
			spnX.setDisable(!nv);
			spnY.setDisable(!nv);
			spnRadius.setDisable(!nv);
			spnSpread.setDisable(!nv);
		});
		
		this.tglInner.setDisable(true);
		this.pkrColor.setDisable(true);
		this.spnX.setDisable(true);
		this.spnY.setDisable(true);
		this.spnRadius.setDisable(true);
		this.spnSpread.setDisable(true);
				
		// by default
		this.chkEnabled.setSelected(false);
		
		// layout
		
		HBox row1 = new HBox(2, this.chkEnabled, this.tglInner, this.pkrColor);
		HBox row2 = new HBox(2, this.spnX, this.spnY);
		HBox row3 = new HBox(2, this.spnRadius, this.spnSpread);
		VBox layout = new VBox(2, row1, row2, row3);
		this.container.setCenter(layout);
	
		// events

		// when the control values change
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (mutating) return;
				// set the value
				ObservableSlideRegion<?> comp = component.get();
				if (comp != null && comp instanceof ObservableTextComponent) {
					ObservableTextComponent<?> tc =(ObservableTextComponent<?>)comp;
					tc.setTextGlow(getControlValues());
				}
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
		this.component.addListener((obs, ov, nv) -> {
			// update controls
			mutating = true;
			if (nv != null && nv instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc = (ObservableTextComponent<?>)nv;
				SlideShadow shadow = tc.getTextGlow();
				if (shadow != null) {
					chkEnabled.setSelected(true);
					tglInner.setSelected(shadow.getType() == ShadowType.INNER);
					pkrColor.setValue(JavaFXTypeConverter.toJavaFX(shadow.getColor()));
					spnX.getValueFactory().setValue(shadow.getOffsetX());
					spnY.getValueFactory().setValue(shadow.getOffsetY());
					spnRadius.getValueFactory().setValue(shadow.getRadius());
					spnSpread.getValueFactory().setValue(shadow.getSpread());
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
			}
			mutating = false;
		});
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
}
