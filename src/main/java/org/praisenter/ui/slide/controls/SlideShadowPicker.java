package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.effects.ShadowType;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.EditorDivider;
import org.praisenter.ui.controls.EditorField;
import org.praisenter.ui.controls.EditorFieldGroup;
import org.praisenter.ui.controls.IntegerSliderField;
import org.praisenter.ui.controls.LastValueNumberStringConverter;
import org.praisenter.ui.controls.TextInputFieldEventFilter;
import org.praisenter.ui.slide.convert.PaintConverter;
import org.praisenter.ui.translations.Translations;

import atlantafx.base.layout.InputGroup;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public final class SlideShadowPicker extends EditorFieldGroup {
	private static final Color DEFAULT_COLOR = new Color(0.0, 0.0, 0.0, 0.8);
	
	private final ObjectProperty<SlideShadow> value;
	
	private final ObjectProperty<ShadowType> type;
	private final ObjectProperty<Color> color;
	private final ObjectProperty<Double> x;
	private final ObjectProperty<Double> y;
	private final DoubleProperty radius;
	private final DoubleProperty spread;
	
	private final BooleanBinding isSelected;
		
	public SlideShadowPicker(String label) {
		this.value = new SimpleObjectProperty<>();
		
		this.type = new SimpleObjectProperty<>(null);
		this.color = new SimpleObjectProperty<>(DEFAULT_COLOR);
		this.x = new SimpleObjectProperty<>(0.0);
		this.y = new SimpleObjectProperty<>(0.0);
		this.radius = new SimpleDoubleProperty(10.0);
		this.spread = new SimpleDoubleProperty(0.0);
		
		this.isSelected = this.value.isNotNull();
		
		ObservableList<Option<ShadowType>> types = FXCollections.observableArrayList();
		types.add(new Option<ShadowType>(Translations.get("slide.shadow.type.NONE"), null));
		types.add(new Option<ShadowType>(Translations.get("slide.shadow.type." + ShadowType.OUTER), ShadowType.OUTER));
		types.add(new Option<ShadowType>(Translations.get("slide.shadow.type." + ShadowType.INNER), ShadowType.INNER));
		
		ChoiceBox<Option<ShadowType>> cbType = new ChoiceBox<Option<ShadowType>>(types);
		cbType.setMaxWidth(Double.MAX_VALUE);
		cbType.setValue(types.get(0));
		BindingHelper.bindBidirectional(cbType.valueProperty(), this.type);
		
		ColorPicker pkrColor = new ColorPicker();
		pkrColor.setMaxWidth(Double.MAX_VALUE);
		pkrColor.valueProperty().bindBidirectional(this.color);
		
		TextField txtX = new TextField();
		TextFormatter<Double> tfX = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble());
		txtX.setTextFormatter(tfX);
		tfX.valueProperty().bindBidirectional(this.x);

		TextField txtY = new TextField();
		TextFormatter<Double> tfY = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble());
		txtY.setTextFormatter(tfY);
		tfY.valueProperty().bindBidirectional(this.y);
		
		// max-mins based on JavaFX max-min for DropShadow and InnerGlow
		IntegerSliderField sldRadius = new IntegerSliderField(0, 127, 10, 1);
		sldRadius.valueProperty().bindBidirectional(this.radius);
		
		IntegerSliderField sldSpread = new IntegerSliderField(0, 1, 0, 100);
		sldSpread.valueProperty().bindBidirectional(this.spread);

		// bindings
		
		BindingHelper.bindBidirectional(this.type, this.value, new ObjectConverter<ShadowType, SlideShadow>() {
			@Override
			public SlideShadow convertFrom(ShadowType t) {
				return SlideShadowPicker.this.getCurrentValue();
			}
			@Override
			public ShadowType convertTo(SlideShadow e) {
				if (e == null) return null;
				return e.getType();
			}
		});
		
		BindingHelper.bindBidirectional(this.color, this.value, new ObjectConverter<Color, SlideShadow>() {
			@Override
			public SlideShadow convertFrom(Color t) {
				return SlideShadowPicker.this.getCurrentValue();
			}
			@Override
			public Color convertTo(SlideShadow e) {
				if (e == null) return DEFAULT_COLOR;
				return PaintConverter.toJavaFX(e.getColor());
			}
		});
		
		BindingHelper.bindBidirectional(this.x, this.value, new ObjectConverter<Double, SlideShadow>() {
			@Override
			public SlideShadow convertFrom(Double t) {
				return SlideShadowPicker.this.getCurrentValue();
			}
			@Override
			public Double convertTo(SlideShadow e) {
				if (e == null) return 0.0;
				return e.getOffsetX();
			}
		});
		
		BindingHelper.bindBidirectional(this.y, this.value, new ObjectConverter<Double, SlideShadow>() {
			@Override
			public SlideShadow convertFrom(Double t) {
				return SlideShadowPicker.this.getCurrentValue();
			}
			@Override
			public Double convertTo(SlideShadow e) {
				if (e == null) return 0.0;
				return e.getOffsetY();
			}
		});
		
		BindingHelper.bindBidirectional(this.radius, this.value, new ObjectConverter<Number, SlideShadow>() {
			@Override
			public SlideShadow convertFrom(Number t) {
				return SlideShadowPicker.this.getCurrentValue();
			}
			@Override
			public Number convertTo(SlideShadow e) {
				if (e == null) return 10.0;
				return e.getRadius();
			}
		});
		
		BindingHelper.bindBidirectional(this.spread, this.value, new ObjectConverter<Number, SlideShadow>() {
			@Override
			public SlideShadow convertFrom(Number t) {
				return SlideShadowPicker.this.getCurrentValue();
			}
			@Override
			public Number convertTo(SlideShadow e) {
				if (e == null) return 0.0;
				return e.getSpread();
			}
		});
		
		// layout

		TextInputFieldEventFilter.applyTextInputFieldEventFilter(txtX, txtY);
		
		// layout
		
		Label lblXAdder = new Label(Translations.get("slide.measure.pixels.abbreviation"));
		lblXAdder.setAlignment(Pos.CENTER);
		Label lblYAdder = new Label(Translations.get("slide.measure.pixels.abbreviation"));
		lblYAdder.setAlignment(Pos.CENTER);
		InputGroup grpX = new InputGroup(txtX, lblXAdder);
		InputGroup grpY = new InputGroup(txtY, lblYAdder);
		
		HBox.setHgrow(txtX, Priority.ALWAYS);
		HBox.setHgrow(txtY, Priority.ALWAYS);
		
		EditorField fldType = new EditorField(Translations.get("slide.shadow.type"), cbType);
		EditorField fldColor = new EditorField(Translations.get("slide.shadow.color"), pkrColor);
		EditorDivider divOffset = new EditorDivider(Translations.get("slide.shadow.offset"));
		EditorField fldOffsetX = new EditorField(Translations.get("slide.shadow.offset.x"), grpX);
		EditorField fldOffsetY = new EditorField(Translations.get("slide.shadow.offset.y"), grpY);
		EditorDivider divBlur = new EditorDivider(Translations.get("slide.shadow.blur"));
		EditorField fldRadius = new EditorField(Translations.get("slide.shadow.radius"), sldRadius);
		EditorField fldSpread = new EditorField(Translations.get("slide.shadow.spread"), Translations.get("slide.shadow.spread.description"), sldSpread);
		
		fldColor.visibleProperty().bind(this.isSelected);
		fldColor.managedProperty().bind(fldColor.visibleProperty());
		divOffset.visibleProperty().bind(this.isSelected);
		divOffset.managedProperty().bind(divOffset.visibleProperty());
		fldOffsetX.visibleProperty().bind(this.isSelected);
		fldOffsetX.managedProperty().bind(fldOffsetX.visibleProperty());
		fldOffsetY.visibleProperty().bind(this.isSelected);
		fldOffsetY.managedProperty().bind(fldOffsetY.visibleProperty());
		divBlur.visibleProperty().bind(this.isSelected);
		divBlur.managedProperty().bind(divBlur.visibleProperty());
		fldRadius.visibleProperty().bind(this.isSelected);
		fldRadius.managedProperty().bind(fldRadius.visibleProperty());
		fldSpread.visibleProperty().bind(this.isSelected);
		fldSpread.managedProperty().bind(fldSpread.visibleProperty());
		
		this.getChildren().addAll(
				fldType,
				fldColor,
				divOffset,
				fldOffsetX,
				fldOffsetY,
				divBlur,
				fldRadius,
				fldSpread);
	}

	private SlideShadow getCurrentValue() {
		SlideShadow shadow = null;
		ShadowType type = this.type.getValue();
		if (type != null) {
			shadow = new SlideShadow();
			shadow.setType(type);
			shadow.setColor(PaintConverter.fromJavaFX(this.color.get()));
			shadow.setOffsetX(this.x.get());
			shadow.setOffsetY(this.y.get());
			shadow.setRadius(this.radius.get());
			shadow.setSpread(this.spread.get());
		}
		return shadow;
	}
	
	public ObjectProperty<SlideShadow> valueProperty() {
		return this.value;
	}
	
	public SlideShadow getValue() {
		return this.value.get();
	}
	
	public void setValue(SlideShadow shadow) {
		this.value.set(shadow);
	}
}
