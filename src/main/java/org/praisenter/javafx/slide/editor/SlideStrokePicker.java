package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.Option;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.graphics.DashPattern;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.graphics.SlideStrokeCap;
import org.praisenter.slide.graphics.SlideStrokeJoin;
import org.praisenter.slide.graphics.SlideStrokeStyle;
import org.praisenter.slide.graphics.SlideStrokeType;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// FEATURE Allow more dash patterns

class SlideStrokePicker extends VBox {

	private final ObjectProperty<SlideStroke> value = new SimpleObjectProperty<SlideStroke>();
	
	private boolean mutating = false;
	
	// controls
	
	private final SlidePaintPicker pkrPaint;
	
	private final ChoiceBox<Option<SlideStrokeJoin>> cbJoin;
	
	private final ChoiceBox<Option<SlideStrokeCap>> cbCap;
	
	private final ChoiceBox<Option<DashPattern>> cbDashes;
	
	private final Spinner<Double> spnWidth;
	
	private final Spinner<Double> spnRadius;
	
	public SlideStrokePicker(PraisenterContext context, boolean showRadius) {
		this.setSpacing(2);
		
		ObservableList<Option<SlideStrokeJoin>> joins = FXCollections.observableArrayList();
		joins.add(new Option<SlideStrokeJoin>(Translations.get("stroke.join.round"), SlideStrokeJoin.ROUND));
		joins.add(new Option<SlideStrokeJoin>(Translations.get("stroke.join.miter"), SlideStrokeJoin.MITER));
		joins.add(new Option<SlideStrokeJoin>(Translations.get("stroke.join.bevel"), SlideStrokeJoin.BEVEL));
		
		ObservableList<Option<SlideStrokeCap>> caps = FXCollections.observableArrayList();
		caps.add(new Option<SlideStrokeCap>(Translations.get("stroke.cap.round"), SlideStrokeCap.ROUND));
		caps.add(new Option<SlideStrokeCap>(Translations.get("stroke.cap.butt"), SlideStrokeCap.BUTT));
		caps.add(new Option<SlideStrokeCap>(Translations.get("stroke.cap.square"), SlideStrokeCap.SQUARE));
		
		ObservableList<Option<DashPattern>> dashes = FXCollections.observableArrayList();
		dashes.add(new Option<DashPattern>(Translations.get("stroke.pattern.solid"), DashPattern.SOLID));
		dashes.add(new Option<DashPattern>(Translations.get("stroke.pattern.dash"), DashPattern.DASH));
		dashes.add(new Option<DashPattern>(Translations.get("stroke.pattern.dashdot"), DashPattern.DASH_DOT));
		dashes.add(new Option<DashPattern>(Translations.get("stroke.pattern.dot"), DashPattern.DOT));
		dashes.add(new Option<DashPattern>(Translations.get("stroke.pattern.longdash"), DashPattern.LONG_DASH));
		dashes.add(new Option<DashPattern>(Translations.get("stroke.pattern.longdashdot"), DashPattern.LONG_DASH_DOT));
		dashes.add(new Option<DashPattern>(Translations.get("stroke.pattern.longdashdotdot"), DashPattern.LONG_DASH_DOT_DOT));
		
		this.pkrPaint = new SlidePaintPicker(context,
				PaintType.NONE,
				PaintType.COLOR, 
				PaintType.GRADIENT);
		
		this.cbJoin = new ChoiceBox<Option<SlideStrokeJoin>>(joins);
		this.cbJoin.setValue(joins.get(0));
		this.cbCap = new ChoiceBox<Option<SlideStrokeCap>>(caps);
		this.cbCap.setValue(caps.get(0));
		this.cbDashes = new ChoiceBox<Option<DashPattern>>(dashes);
		this.cbDashes.setValue(dashes.get(0));
		this.spnWidth = new Spinner<Double>(0, Double.MAX_VALUE, 1, 0.25);
		this.spnWidth.setMaxWidth(75);
		this.spnWidth.setEditable(true);
		this.spnRadius = new Spinner<Double>(0, Double.MAX_VALUE, 0, 0.25);
		this.spnRadius.setMaxWidth(75);
		this.spnRadius.setEditable(true);
		
		HBox h1 = new HBox(2, this.cbJoin, this.cbCap, this.spnWidth);
		HBox h2 = new HBox(2, this.cbDashes);
		if (showRadius) {
			h2.getChildren().add(this.spnRadius);
		}
		
		h1.managedProperty().bind(h1.visibleProperty());
		h2.managedProperty().bind(h2.visibleProperty());
		
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (mutating) return;
				mutating = true;
				value.set(getControlValues());
				mutating = false;
			}
		};
		
		this.pkrPaint.valueProperty().addListener(listener);
		this.cbJoin.valueProperty().addListener(listener);
		this.cbCap.valueProperty().addListener(listener);
		this.cbDashes.valueProperty().addListener(listener);
		this.spnWidth.valueProperty().addListener(listener);
		this.spnRadius.valueProperty().addListener(listener);
		
		h1.setVisible(false);
		h2.setVisible(false);
		
		this.value.addListener((obs, ov, nv) -> {
			if (nv == null) {
				h1.setVisible(false);
				h2.setVisible(false);
			} else {
				h1.setVisible(true);
				h2.setVisible(true);
			}
			if (mutating) return;
			mutating = true;
			setControlValues(nv);
			mutating = false;
		});
		
		this.getChildren().addAll(this.pkrPaint, h1, h2);
	}
	
	private SlideStroke getControlValues() {
		SlidePaint paint = this.pkrPaint.getValue();
		// if the slide paint is null then this means
		// the user selected NONE
		if (paint == null) {
			return null;
		}
		return new SlideStroke(
				paint, 
				new SlideStrokeStyle(
						SlideStrokeType.CENTERED, 
						this.cbJoin.getValue().getValue(), 
						this.cbCap.getValue().getValue(), 
						this.cbDashes.getValue().getValue().getDashes()), 
				this.spnWidth.getValue(), 
				this.spnRadius.getValue());
	}
	
	private void setControlValues(SlideStroke stroke) {
		if (stroke == null) {
			this.pkrPaint.setValue(null);
		} else {		
			this.pkrPaint.setValue(stroke.getPaint());
			SlideStrokeStyle style = stroke.getStyle();
			if (style != null) {
				this.cbJoin.setValue(new Option<SlideStrokeJoin>(null, style.getJoin()));
				this.cbCap.setValue(new Option<SlideStrokeCap>(null, style.getCap()));
				this.cbDashes.setValue(new Option<DashPattern>(null, DashPattern.getDashPattern(style.getDashes())));
			}
			this.spnWidth.getValueFactory().setValue(stroke.getWidth());
			this.spnRadius.getValueFactory().setValue(stroke.getRadius());
		}
	}
	
	public ObjectProperty<SlideStroke> valueProperty() {
		return this.value;
	}
	
	public SlideStroke getValue() {
		return this.value.get();
	}
	
	public void setValue(SlideStroke stroke) {
		this.value.set(stroke);
	}
}
