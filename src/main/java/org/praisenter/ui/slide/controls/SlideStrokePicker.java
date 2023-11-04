package org.praisenter.ui.slide.controls;

import java.util.List;

import org.praisenter.data.slide.graphics.DashPattern;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.graphics.SlideStroke;
import org.praisenter.data.slide.graphics.SlideStrokeCap;
import org.praisenter.data.slide.graphics.SlideStrokeJoin;
import org.praisenter.data.slide.graphics.SlideStrokeStyle;
import org.praisenter.data.slide.graphics.SlideStrokeType;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.EditorDivider;
import org.praisenter.ui.controls.EditorField;
import org.praisenter.ui.controls.EditorFieldGroup;
import org.praisenter.ui.controls.LastValueNumberStringConverter;
import org.praisenter.ui.translations.Translations;

import atlantafx.base.layout.InputGroup;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Callback;

public final class SlideStrokePicker extends EditorFieldGroup {
	private static final String SLIDE_STROKE_PICKER_CSS = "p-slide-stroke-picker";
	private static final String DASH_PATTERN_CSS = "p-slide-dash-pattern";
	
	private final ObjectProperty<SlideStroke> value;
	
	private final SlideStrokeType type;
	private final ObjectProperty<SlidePaint> paint;
	private final ObjectProperty<Double> radius;
	private final ObjectProperty<Double> width;
	private final ObjectProperty<SlideStrokeCap> cap;
	private final ObjectProperty<SlideStrokeJoin> join;
	private final ObjectProperty<DashPattern> dashes;
	
	private final BooleanBinding isValueSelected;
	
	public SlideStrokePicker(SlideStrokeType type, String label, boolean showRadius) {
		this.value = new SimpleObjectProperty<>();
		
		this.type = type;
		this.paint = new SimpleObjectProperty<>(null);
		this.radius = new SimpleObjectProperty<>(0.0);
		this.width = new SimpleObjectProperty<>(1.0);
		this.cap = new SimpleObjectProperty<>(SlideStrokeCap.BUTT);
		this.join = new SimpleObjectProperty<>(SlideStrokeJoin.MITER);
		this.dashes = new SimpleObjectProperty<>(DashPattern.SOLID);
		this.isValueSelected = this.paint.isNotNull();
		
		SlidePaintPicker pkrPaint = new SlidePaintPicker(null, true, true, true, false, false, label);
		pkrPaint.valueProperty().bindBidirectional(this.paint);
		
		TextField txtRadius = new TextField();
		TextFormatter<Double> tfRadius = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble());
		txtRadius.setTextFormatter(tfRadius);
		tfRadius.valueProperty().bindBidirectional(this.radius);

		TextField txtWidth = new TextField();
		TextFormatter<Double> tfWidth = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble());
		txtWidth.setTextFormatter(tfWidth);
		tfWidth.valueProperty().bindBidirectional(this.width);
		
		ObservableList<Option<SlideStrokeCap>> capOptions = FXCollections.observableArrayList();
		capOptions.add(new Option<>(Translations.get("slide.stroke.cap." + SlideStrokeCap.BUTT), SlideStrokeCap.BUTT));
		capOptions.add(new Option<>(Translations.get("slide.stroke.cap." + SlideStrokeCap.SQUARE), SlideStrokeCap.SQUARE));
		capOptions.add(new Option<>(Translations.get("slide.stroke.cap." + SlideStrokeCap.ROUND), SlideStrokeCap.ROUND));
		ChoiceBox<Option<SlideStrokeCap>> cbCap = new ChoiceBox<>(capOptions);
		cbCap.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cbCap.valueProperty(), this.cap);

		ObservableList<Option<SlideStrokeJoin>> joinOptions = FXCollections.observableArrayList();
		joinOptions.add(new Option<>(Translations.get("slide.stroke.join." + SlideStrokeJoin.MITER), SlideStrokeJoin.MITER));
		joinOptions.add(new Option<>(Translations.get("slide.stroke.join." + SlideStrokeJoin.BEVEL), SlideStrokeJoin.BEVEL));
		joinOptions.add(new Option<>(Translations.get("slide.stroke.join." + SlideStrokeJoin.ROUND), SlideStrokeJoin.ROUND));
		ChoiceBox<Option<SlideStrokeJoin>> cbJoin = new ChoiceBox<>(joinOptions);
		cbJoin.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cbJoin.valueProperty(), this.join);
		
		ObservableList<DashPattern> dashOptions = FXCollections.observableArrayList();
		dashOptions.add(DashPattern.SOLID);
		dashOptions.add(DashPattern.DASH);
		dashOptions.add(DashPattern.DOT);
		dashOptions.add(DashPattern.DASH_DOT);
		dashOptions.add(DashPattern.LONG_DASH);
		dashOptions.add(DashPattern.LONG_DASH_DOT);
		dashOptions.add(DashPattern.LONG_DASH_DOT_DOT);
		ComboBox<DashPattern> cmbDash = new ComboBox<>(dashOptions);
		cmbDash.setCellFactory(new Callback<ListView<DashPattern>, ListCell<DashPattern>>() {
			@Override
			public ListCell<DashPattern> call(ListView<DashPattern> param) {
				return createPatternListCell();
			}
		});
		cmbDash.setButtonCell(createPatternListCell());
		cmbDash.setMaxWidth(Double.MAX_VALUE);
		cmbDash.valueProperty().bindBidirectional(this.dashes);
		
		// layout
		Label lblWidthAdder = new Label(Translations.get("slide.measure.pixels.abbreviation"));
		lblWidthAdder.setAlignment(Pos.CENTER);
		Label lblRadiusAdder = new Label(Translations.get("slide.measure.pixels.abbreviation"));
		lblRadiusAdder.setAlignment(Pos.CENTER);
		InputGroup grpWidth = new InputGroup(txtWidth, lblWidthAdder);
		InputGroup grpRadius = new InputGroup(txtRadius, lblRadiusAdder);
		
		HBox.setHgrow(txtWidth, Priority.ALWAYS);
		HBox.setHgrow(txtRadius, Priority.ALWAYS);
		
		EditorField fldPaint = new EditorField(pkrPaint);
		EditorDivider divSize = new EditorDivider(Translations.get("slide.border.size"));
		EditorField fldWidth = new EditorField(Translations.get("slide.border.width"), grpWidth);
		EditorField fldRadius = new EditorField(Translations.get("slide.border.radius"), grpRadius);
		EditorDivider divStyle = new EditorDivider(Translations.get("slide.border.style"));
		EditorField fldCap = new EditorField(Translations.get("slide.border.cap"), cbCap);
		EditorField fldJoin = new EditorField(Translations.get("slide.border.join"), cbJoin);
		EditorField fldDash = new EditorField(Translations.get("slide.border.dashes"), cmbDash);
		
		divSize.visibleProperty().bind(this.isValueSelected);
		divSize.managedProperty().bind(divSize.visibleProperty());
		fldWidth.visibleProperty().bind(this.isValueSelected);
		fldWidth.managedProperty().bind(fldWidth.visibleProperty());
		fldRadius.visibleProperty().bind(this.isValueSelected);
		fldRadius.managedProperty().bind(fldRadius.visibleProperty());
		divStyle.visibleProperty().bind(this.isValueSelected);
		divStyle.managedProperty().bind(divStyle.visibleProperty());
		fldCap.visibleProperty().bind(this.isValueSelected);
		fldCap.managedProperty().bind(fldCap.visibleProperty());
		fldJoin.visibleProperty().bind(this.isValueSelected);
		fldJoin.managedProperty().bind(fldJoin.visibleProperty());
		fldDash.visibleProperty().bind(this.isValueSelected);
		fldDash.managedProperty().bind(fldDash.visibleProperty());
		
		this.getChildren().addAll(
				fldPaint,
				divSize,
				fldWidth,
				fldRadius,
				divStyle,
				fldCap,
				fldJoin,
				fldDash);
		this.getStyleClass().add(SLIDE_STROKE_PICKER_CSS);
		
//		this.showRowsOnly(fIndex);
//		
//		this.isValueSelected.addListener((obs, ov, nv) -> {
//			if (nv) {
//				if (showRadius) {
//					this.showRowsOnly(fIndex, fIndex + 1, fIndex + 2, fIndex + 3, fIndex + 4, fIndex + 5);
//				} else {
//					this.showRowsOnly(fIndex, fIndex + 1, fIndex + 3, fIndex + 4, fIndex + 5);
//				}
//			}
//			else this.showRowsOnly(fIndex);
//		});
		
		BindingHelper.bindBidirectional(this.paint, this.value, new ObjectConverter<SlidePaint, SlideStroke>() {
			@Override
			public SlideStroke convertFrom(SlidePaint t) {
				return SlideStrokePicker.this.getCurrentValue();
			}
			@Override
			public SlidePaint convertTo(SlideStroke e) {
				if (e == null) return null;
				return e.getPaint();
			}
		});
		
		BindingHelper.bindBidirectional(this.radius, this.value, new ObjectConverter<Double, SlideStroke>() {
			@Override
			public SlideStroke convertFrom(Double t) {
				return SlideStrokePicker.this.getCurrentValue();
			}
			@Override
			public Double convertTo(SlideStroke e) {
				if (e == null) return 0.0;
				return e.getRadius();
			}
		});
		
		BindingHelper.bindBidirectional(this.width, this.value, new ObjectConverter<Double, SlideStroke>() {
			@Override
			public SlideStroke convertFrom(Double t) {
				return SlideStrokePicker.this.getCurrentValue();
			}
			@Override
			public Double convertTo(SlideStroke e) {
				if (e == null) return 1.0;
				return e.getWidth();
			}
		});
		
		BindingHelper.bindBidirectional(this.cap, this.value, new ObjectConverter<SlideStrokeCap, SlideStroke>() {
			@Override
			public SlideStroke convertFrom(SlideStrokeCap t) {
				return SlideStrokePicker.this.getCurrentValue();
			}
			@Override
			public SlideStrokeCap convertTo(SlideStroke e) {
				if (e == null) return SlideStrokeCap.BUTT;
				SlideStrokeStyle style = e.getStyle();
				if (style == null) return SlideStrokeCap.BUTT;
				return style.getCap();
			}
		});
		
		BindingHelper.bindBidirectional(this.join, this.value, new ObjectConverter<SlideStrokeJoin, SlideStroke>() {
			@Override
			public SlideStroke convertFrom(SlideStrokeJoin t) {
				return SlideStrokePicker.this.getCurrentValue();
			}
			@Override
			public SlideStrokeJoin convertTo(SlideStroke e) {
				if (e == null) return SlideStrokeJoin.MITER;
				SlideStrokeStyle style = e.getStyle();
				if (style == null) return SlideStrokeJoin.MITER;
				return style.getJoin();
			}
		});
		
		BindingHelper.bindBidirectional(this.dashes, this.value, new ObjectConverter<DashPattern, SlideStroke>() {
			@Override
			public SlideStroke convertFrom(DashPattern t) {
				return SlideStrokePicker.this.getCurrentValue();
			}
			@Override
			public DashPattern convertTo(SlideStroke e) {
				if (e == null) return DashPattern.SOLID;
				SlideStrokeStyle style = e.getStyle();
				if (style == null) return DashPattern.SOLID;
				return DashPattern.getDashPattern(style.getDashes());
			}
		});
	}
	
	private SlideStroke getCurrentValue() {
		SlidePaint paint = this.paint.get();
		if (paint != null) {
			SlideStroke ss = new SlideStroke();
			SlideStrokeStyle style = new SlideStrokeStyle();
			DashPattern dp = this.dashes.get();
			style.setCap(this.cap.get());
			style.setDashes(dp != null ? dp.getDashes() : DashPattern.SOLID.getDashes());
			style.setJoin(this.join.get());
			// for performance we force centered
			style.setType(this.type);
			ss.setPaint(paint);
			ss.setRadius(this.radius.get());
			ss.setStyle(style);
			ss.setWidth(this.width.get());
			return ss;
		}
		return null;
	}
	
	private static ListCell<DashPattern> createPatternListCell() {
		return new ListCell<DashPattern>() {
			private final Line line;
			
            {
            	line = new Line(0, 0, 50, 0);
            	line.getStyleClass().add(DASH_PATTERN_CSS);
            	line.setStrokeLineJoin(StrokeLineJoin.MITER);
            	line.setStrokeLineCap(StrokeLineCap.ROUND);
            	line.setStrokeWidth(2);
            	line.setFill(null);
                
                setText(null);
            }
            
			@Override
			protected void updateItem(DashPattern item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					List<Double> dashes = item.getScaledDashPattern(2);
					line.getStrokeDashArray().clear();
					line.getStrokeDashArray().addAll(dashes);
					setGraphic(line);
				}
			}
		};
	}
	
	public SlideStroke getValue() {
		return this.value.get();
	}
	
	public void setValue(SlideStroke stroke) {
		this.value.set(stroke);
	}
	
	public ObjectProperty<SlideStroke> valueProperty() {
		return this.value;
	}
}
