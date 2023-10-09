package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.effects.ShadowType;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.FormFieldSection;
import org.praisenter.ui.controls.LastValueNumberStringConverter;
import org.praisenter.ui.controls.TextInputFieldEventFilter;
import org.praisenter.ui.slide.convert.PaintConverter;
import org.praisenter.ui.translations.Translations;

import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.theme.Styles;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;

// FIXME replace color pickers with custom one
public final class SlideShadowPicker extends FormFieldSection {
	private static final Color DEFAULT_COLOR = new Color(0.0, 0.0, 0.0, 0.8);
	
	private final ObjectProperty<SlideShadow> value;
	
	private final ObjectProperty<ShadowType> type;
	private final ObjectProperty<Color> color;
	private final ObjectProperty<Double> x;
	private final ObjectProperty<Double> y;
	private final DoubleProperty radius;
	private final DoubleProperty spread;
		
	public SlideShadowPicker(String label) {
		this.value = new SimpleObjectProperty<>();
		
		this.type = new SimpleObjectProperty<>(null);
		this.color = new SimpleObjectProperty<>(DEFAULT_COLOR);
		this.x = new SimpleObjectProperty<>(0.0);
		this.y = new SimpleObjectProperty<>(0.0);
		this.radius = new SimpleDoubleProperty(5.0);
		this.spread = new SimpleDoubleProperty(0.8);
		
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
		Slider sldRadius = new Slider(0.0, 127.0, 10.0);
		sldRadius.getStyleClass().add(Styles.SMALL);
		sldRadius.setSkin(new ProgressSliderSkin(sldRadius));
		sldRadius.valueProperty().bindBidirectional(this.radius);
		
		Slider sldSpread = new Slider(0.0, 1.0, 0.0);
		sldSpread.getStyleClass().add(Styles.SMALL);
		sldSpread.setSkin(new ProgressSliderSkin(sldSpread));
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
		
		int fIndex = this.addField(Translations.get("slide.shadow.type"), cbType);
		this.addField(Translations.get("slide.shadow.color"), pkrColor);
		this.addField(Translations.get("slide.shadow.offset.x"), txtX);
		this.addField(Translations.get("slide.shadow.offset.y"), txtY);
		this.addField(Translations.get("slide.shadow.radius"), sldRadius);
		this.addField(Translations.get("slide.shadow.spread"), sldSpread);
		
		this.showRowsOnly(fIndex);
		
		this.type.addListener((obs, ov, nv) -> {
			if (nv == null) {
				this.showRowsOnly(fIndex);
			} else {
				this.showRowsOnly(fIndex, fIndex + 1, fIndex + 2, fIndex + 3, fIndex + 4, fIndex + 5);
			}
		});
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
