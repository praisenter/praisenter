package org.praisenter.ui.slide.controls;

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
import org.praisenter.ui.controls.LastValueDoubleStringConverter;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Callback;

public final class SlideStrokePicker extends VBox {
	private final ObjectProperty<SlideStroke> value;
	
	private final SlideStrokeType type;
	private final ObjectProperty<SlidePaint> paint;
	private final ObjectProperty<Double> radius;
	private final ObjectProperty<Double> width;
	private final ObjectProperty<SlideStrokeCap> cap;
	private final ObjectProperty<SlideStrokeJoin> join;
	private final ObjectProperty<DashPattern> dashes;
	
	private final BooleanBinding isValueSelected;
	
	public SlideStrokePicker(SlideStrokeType type) {
		this.value = new SimpleObjectProperty<>();
		
		this.type = type;
		this.paint = new SimpleObjectProperty<>(null);
		this.radius = new SimpleObjectProperty<>(0.0);
		this.width = new SimpleObjectProperty<>(1.0);
		this.cap = new SimpleObjectProperty<>(SlideStrokeCap.BUTT);
		this.join = new SimpleObjectProperty<>(SlideStrokeJoin.MITER);
		this.dashes = new SimpleObjectProperty<>(DashPattern.SOLID);
		this.isValueSelected = this.paint.isNotNull();
		
		SlidePaintPicker pkrPaint = new SlidePaintPicker(null, true, true, true, false, false);
		pkrPaint.valueProperty().bindBidirectional(this.paint);
		
		Label lblRadius = new Label(Translations.get("slide.border.radius"));
		TextField txtRadius = new TextField();
		TextFormatter<Double> tfRadius = new TextFormatter<Double>(new LastValueDoubleStringConverter());
		txtRadius.setTextFormatter(tfRadius);
		txtRadius.setPrefWidth(50);
		tfRadius.valueProperty().bindBidirectional(this.radius);

		Label lblWidth = new Label(Translations.get("slide.border.width"));
		TextField txtWidth = new TextField();
		TextFormatter<Double> tfWidth = new TextFormatter<Double>(new LastValueDoubleStringConverter());
		txtWidth.setTextFormatter(tfWidth);
		txtWidth.setPrefWidth(50);
		tfWidth.valueProperty().bindBidirectional(this.width);
		
		Label lblCap = new Label(Translations.get("slide.border.cap"));
		ObservableList<Option<SlideStrokeCap>> capOptions = FXCollections.observableArrayList();
		capOptions.add(new Option<>(Translations.get("slide.stroke.cap." + SlideStrokeCap.BUTT), SlideStrokeCap.BUTT));
		capOptions.add(new Option<>(Translations.get("slide.stroke.cap." + SlideStrokeCap.SQUARE), SlideStrokeCap.SQUARE));
		capOptions.add(new Option<>(Translations.get("slide.stroke.cap." + SlideStrokeCap.ROUND), SlideStrokeCap.ROUND));
		ChoiceBox<Option<SlideStrokeCap>> cbCap = new ChoiceBox<>(capOptions);
		BindingHelper.bindBidirectional(cbCap.valueProperty(), this.cap);

		Label lblJoin = new Label(Translations.get("slide.border.join"));
		ObservableList<Option<SlideStrokeJoin>> joinOptions = FXCollections.observableArrayList();
		joinOptions.add(new Option<>(Translations.get("slide.stroke.join." + SlideStrokeJoin.MITER), SlideStrokeJoin.MITER));
		joinOptions.add(new Option<>(Translations.get("slide.stroke.join." + SlideStrokeJoin.BEVEL), SlideStrokeJoin.BEVEL));
		joinOptions.add(new Option<>(Translations.get("slide.stroke.join." + SlideStrokeJoin.ROUND), SlideStrokeJoin.ROUND));
		ChoiceBox<Option<SlideStrokeJoin>> cbJoin = new ChoiceBox<>(joinOptions);
		BindingHelper.bindBidirectional(cbJoin.valueProperty(), this.join);
		
		Label lbldashes = new Label(Translations.get("slide.border.dashes"));
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
		cmbDash.valueProperty().bindBidirectional(this.dashes);
		
		this.getChildren().addAll(
			pkrPaint,
			lblWidth, txtWidth,
			lblRadius, txtRadius,
			lblCap, cbCap,
			lblJoin, cbJoin,
			lbldashes, cmbDash);
		
		txtWidth.visibleProperty().bind(this.isValueSelected);
		txtWidth.managedProperty().bind(txtWidth.visibleProperty());
		txtRadius.visibleProperty().bind(this.isValueSelected);
		txtRadius.managedProperty().bind(txtRadius.visibleProperty());
		cbCap.visibleProperty().bind(this.isValueSelected);
		cbCap.managedProperty().bind(cbCap.visibleProperty());
		cbJoin.visibleProperty().bind(this.isValueSelected);
		cbJoin.managedProperty().bind(cbJoin.visibleProperty());
		cmbDash.visibleProperty().bind(this.isValueSelected);
		cmbDash.managedProperty().bind(cmbDash.visibleProperty());
		
		BindingHelper.bindBidirectional(this.paint, this.value, new ObjectConverter<SlidePaint, SlideStroke>() {
			@Override
			public SlideStroke convertFrom(SlidePaint t) {
				return SlideStrokePicker.this.getControlValues();
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
				return SlideStrokePicker.this.getControlValues();
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
				return SlideStrokePicker.this.getControlValues();
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
				return SlideStrokePicker.this.getControlValues();
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
				return SlideStrokePicker.this.getControlValues();
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
				return SlideStrokePicker.this.getControlValues();
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
	
	private SlideStroke getControlValues() {
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
            	line.setStroke(Color.BLACK);
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
					Double[] dashes = item.getScaledDashPattern(2);
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
