package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.effects.ShadowType;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.TextInputFieldEventFilter;
import org.praisenter.ui.slide.convert.PaintConverter;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public final class SlideShadowPicker extends VBox {
	private final ObjectProperty<SlideShadow> value;
	
	private final ComboBox<Option<ShadowType>> cmbType;
	private final ColorPicker pkrColor;
	private final Spinner<Double> spnX;
	private final Spinner<Double> spnY;
	private final Slider sldRadius;
	private final Slider sldSpread;
	
	private final BooleanBinding isShadowSelected;
	
	public SlideShadowPicker() {
		this.value = new SimpleObjectProperty<>();
		
		ObservableList<Option<ShadowType>> types = FXCollections.observableArrayList();
		types.add(new Option<ShadowType>(Translations.get("slide.shadow.type.NONE"), null));
		types.add(new Option<ShadowType>(Translations.get("slide.shadow.type." + ShadowType.OUTER), ShadowType.OUTER));
		types.add(new Option<ShadowType>(Translations.get("slide.shadow.type." + ShadowType.INNER), ShadowType.INNER));
		
		this.cmbType = new ComboBox<Option<ShadowType>>(types);
		this.pkrColor = new ColorPicker();
		this.spnX = new Spinner<Double>(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 1.0);
		this.spnY = new Spinner<Double>(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 1.0);
		
		// max-mins based on JavaFX max-min for DropShadow and InnerGlow
		this.sldRadius = new Slider(0.0, 127.0, 10.0);
		this.sldSpread = new Slider(0.0, 1.0, 0.0);
		
		this.isShadowSelected = Bindings.createBooleanBinding(() -> {
			Option<ShadowType> value = this.cmbType.getValue();
			return value != null && value.getValue() != null;
		}, this.cmbType.valueProperty());
		
		// setup values
		this.cmbType.setValue(types.get(0));
		this.pkrColor.setValue(Color.BLACK);

		// setup look
		this.pkrColor.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
		this.pkrColor.setStyle("-fx-color-label-visible: false;");
		this.spnX.setEditable(true);
		this.spnY.setEditable(true);
		
		Label lblOffset = new Label(Translations.get("slide.shadow.offset"));
		Label lblRadius = new Label(Translations.get("slide.shadow.radius"));
		Label lblSpread = new Label(Translations.get("slide.shadow.spread"));
		
		// bindings
		
		BindingHelper.bindBidirectional(this.cmbType.valueProperty(), this.value, new ObjectConverter<Option<ShadowType>, SlideShadow>() {
			@Override
			public SlideShadow convertFrom(Option<ShadowType> t) {
				return SlideShadowPicker.this.getControlValues();
			}
			@Override
			public Option<ShadowType> convertTo(SlideShadow e) {
				if (e == null) return new Option<ShadowType>();
				return new Option<ShadowType>(null, e.getType());
			}
		});
		
		BindingHelper.bindBidirectional(this.pkrColor.valueProperty(), this.value, new ObjectConverter<Color, SlideShadow>() {
			@Override
			public SlideShadow convertFrom(Color t) {
				return SlideShadowPicker.this.getControlValues();
			}
			@Override
			public Color convertTo(SlideShadow e) {
				if (e == null) return Color.BLACK;
				return PaintConverter.toJavaFX(e.getColor());
			}
		});
		
		BindingHelper.bindBidirectional(this.spnX.getValueFactory().valueProperty(), this.value, new ObjectConverter<Double, SlideShadow>() {
			@Override
			public SlideShadow convertFrom(Double t) {
				return SlideShadowPicker.this.getControlValues();
			}
			@Override
			public Double convertTo(SlideShadow e) {
				if (e == null) return 0.0;
				return e.getOffsetX();
			}
		});
		
		BindingHelper.bindBidirectional(this.spnY.getValueFactory().valueProperty(), this.value, new ObjectConverter<Double, SlideShadow>() {
			@Override
			public SlideShadow convertFrom(Double t) {
				return SlideShadowPicker.this.getControlValues();
			}
			@Override
			public Double convertTo(SlideShadow e) {
				if (e == null) return 0.0;
				return e.getOffsetY();
			}
		});
		
		BindingHelper.bindBidirectional(this.sldRadius.valueProperty(), this.value, new ObjectConverter<Number, SlideShadow>() {
			@Override
			public SlideShadow convertFrom(Number t) {
				return SlideShadowPicker.this.getControlValues();
			}
			@Override
			public Number convertTo(SlideShadow e) {
				if (e == null) return 10.0;
				return e.getRadius();
			}
		});
		
		BindingHelper.bindBidirectional(this.sldSpread.valueProperty(), this.value, new ObjectConverter<Number, SlideShadow>() {
			@Override
			public SlideShadow convertFrom(Number t) {
				return SlideShadowPicker.this.getControlValues();
			}
			@Override
			public Number convertTo(SlideShadow e) {
				if (e == null) return 0.0;
				return e.getSpread();
			}
		});
		
		lblOffset.visibleProperty().bind(this.isShadowSelected);
		lblRadius.visibleProperty().bind(this.isShadowSelected);
		lblSpread.visibleProperty().bind(this.isShadowSelected);
		
		this.pkrColor.visibleProperty().bind(this.isShadowSelected);
		this.spnX.visibleProperty().bind(this.isShadowSelected);
		this.spnY.visibleProperty().bind(this.isShadowSelected);
		this.sldRadius.visibleProperty().bind(this.isShadowSelected);
		this.sldSpread.visibleProperty().bind(this.isShadowSelected);
		
		lblOffset.managedProperty().bind(lblOffset.visibleProperty());
		lblRadius.managedProperty().bind(lblRadius.visibleProperty());
		lblSpread.managedProperty().bind(lblSpread.visibleProperty());
		
		this.pkrColor.managedProperty().bind(this.pkrColor.visibleProperty());
		this.spnX.managedProperty().bind(this.spnX.visibleProperty());
		this.spnY.managedProperty().bind(this.spnY.visibleProperty());
		this.sldRadius.managedProperty().bind(this.sldRadius.visibleProperty());
		this.sldSpread.managedProperty().bind(this.sldSpread.visibleProperty());
		
		TextInputFieldEventFilter.applyTextInputFieldEventFilter(
				this.spnX.getEditor(),
				this.spnY.getEditor());
		
		// layout
		
		this.setSpacing(5);
		this.getChildren().addAll(
				new HBox(5, this.cmbType, this.pkrColor),
				lblOffset,
				new HBox(5, this.spnX, this.spnY),
				lblRadius,
				this.sldRadius,
				lblSpread,
				this.sldSpread);
		
	}

	private SlideShadow getControlValues() {
		SlideShadow shadow = null;
		Option<ShadowType> type = this.cmbType.getValue();
		if (type != null && type.getValue() != null) {
			shadow = new SlideShadow();
			shadow.setType(type.getValue());
			shadow.setColor(PaintConverter.fromJavaFX(this.pkrColor.getValue()));
			shadow.setOffsetX(this.spnX.getValue());
			shadow.setOffsetY(this.spnY.getValue());
			shadow.setRadius(this.sldRadius.getValue());
			shadow.setSpread(this.sldSpread.getValue());
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
