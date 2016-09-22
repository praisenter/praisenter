package org.praisenter.javafx.slide.editor;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.javafx.Option;
import org.praisenter.javafx.slide.JavaFXTypeConverter;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.graphics.DashPattern;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradient;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.graphics.SlideStrokeCap;
import org.praisenter.slide.graphics.SlideStrokeJoin;
import org.praisenter.slide.graphics.SlideStrokeStyle;
import org.praisenter.slide.graphics.SlideStrokeType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.util.Callback;

public class FontBorderRibbonTab extends EditorRibbonTab {

	private static final Color DEFAULT_PAINT = new Color(0, 0, 0, 1);
	private static final SlideLinearGradient DEFAULT_GRADIENT = new SlideLinearGradient(0, 0, 0, 1, SlideGradientCycleType.NONE, new SlideGradientStop(0, 0, 0, 0, 1), new SlideGradientStop(1, 0, 0, 0, 0.5));
	
	final ColorPicker pkrColor;
	final SlideGradientPicker pkrGradient;
	private final ComboBox<Option<SlideStrokeJoin>> cbJoin;
	private final ComboBox<Option<SlideStrokeCap>> cbCap;
	private final ChoiceBox<Option<DashPattern>> cbDashes;
	private final Spinner<Double> spnWidth;
	private final Spinner<Double> spnRadius;
	
	public FontBorderRibbonTab() {
		super("Font Border");

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
		
		MenuItem itmNone = new MenuItem("None");
		MenuItem itmColor = new MenuItem("Color");
		MenuItem itmGradient = new MenuItem("Gradient");
		Pane pane = new Pane();
		Fx.setSize(pane, 10, 10);
		pane.setPadding(new Insets(0));
		List<Double> pattern = new ArrayList<>();
		pattern.add(1.0);
		pattern.add(3.0);
		pane.setBorder(new Border(new BorderStroke(Color.LIMEGREEN, new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1, 0, pattern), null, new BorderWidths(2, 2, 2, 2), new Insets(0))));
		MenuButton mnuPaintType = new MenuButton("", pane, itmNone, itmColor, itmGradient);
		pkrColor = new ColorPicker(DEFAULT_PAINT);
		pkrGradient = new SlideGradientPicker();
		pkrGradient.setValue(DEFAULT_GRADIENT);
		
		this.cbJoin = new ComboBox<Option<SlideStrokeJoin>>(joins);
		this.cbJoin.setCellFactory(new Callback<ListView<Option<SlideStrokeJoin>>, ListCell<Option<SlideStrokeJoin>>>() {
			@Override
			public ListCell<Option<SlideStrokeJoin>> call(ListView<Option<SlideStrokeJoin>> param) {
				return createJoinListCell();
			}
		});
		this.cbJoin.setButtonCell(createJoinListCell());
		this.cbJoin.setValue(joins.get(0));
		this.cbJoin.setMaxWidth(35.0);
		
		this.cbCap = new ComboBox<Option<SlideStrokeCap>>(caps);
		this.cbCap.setCellFactory(new Callback<ListView<Option<SlideStrokeCap>>, ListCell<Option<SlideStrokeCap>>>() {
			@Override
			public ListCell<Option<SlideStrokeCap>> call(ListView<Option<SlideStrokeCap>> param) {
				return createCapListCell();
			}
		});
		this.cbCap.setButtonCell(createCapListCell());
		this.cbCap.setValue(caps.get(0));
		this.cbCap.setMaxWidth(35.0);
		
		this.cbDashes = new ChoiceBox<Option<DashPattern>>(dashes);
		this.cbDashes.setValue(dashes.get(0));
		this.spnWidth = new Spinner<Double>(0, Double.MAX_VALUE, 1, 0.25);
		this.spnWidth.setMaxWidth(75);
		this.spnWidth.setEditable(true);
		this.spnRadius = new Spinner<Double>(0, Double.MAX_VALUE, 0, 0.25);
		this.spnRadius.setMaxWidth(75);
		this.spnRadius.setEditable(true);
		
		pkrColor.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
		pkrColor.setStyle("-fx-color-label-visible: false;");
		pkrColor.managedProperty().bind(pkrColor.visibleProperty());
		pkrGradient.managedProperty().bind(pkrGradient.visibleProperty());
		
		pkrGradient.setVisible(false);
		
		HBox row1 = new HBox(2, mnuPaintType, pkrColor, pkrGradient);
		HBox row2 = new HBox(2, cbJoin, cbCap, spnWidth);
		HBox row3 = new HBox(2, cbDashes, spnRadius);

		VBox layout = new VBox(2, row1, row2, row3);
		
		this.container.setCenter(layout);
	
		// events
		itmNone.setOnAction((e) -> {
			this.pkrColor.setVisible(false);
			this.pkrGradient.setVisible(false);
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextBorder(null);
			}
		});
		itmColor.setOnAction((e) -> {
			this.pkrColor.setVisible(true);
			this.pkrGradient.setVisible(false);
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextPaint(JavaFXTypeConverter.fromJavaFX(this.pkrColor.getValue()));
			}
		});
		itmGradient.setOnAction((e) -> {
			this.pkrColor.setVisible(false);
			this.pkrGradient.setVisible(true);
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextPaint(pkrGradient.getValue());
			}
		});
		
		this.component.addListener((obs, ov, nv) -> {
			if (nv instanceof ObservableTextComponent) {
				ObservableTextComponent<?> otc = (ObservableTextComponent<?>)nv;
				setControlValues(otc.getTextBorder());
			}
		});
		
		this.pkrColor.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextBorder(this.getControlValues());
			}
		});
		
		this.pkrGradient.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextBorder(this.getControlValues());
			}
		});
		
		// other tabs
		// text border fill
		// text border style
		// paragraph
		// text shadow
		// text glow
	}
	
	private static ListCell<Option<SlideStrokeJoin>> createJoinListCell() {
		return new ListCell<Option<SlideStrokeJoin>>() {
			private final Path round;
			private final Path miter;
			private final Path bevel;
			
            {
                round = new Path();
                round.getElements().addAll(
                		new MoveTo(15, 0),
                		new LineTo(10, 0),
                		new ArcTo(10.0, 10.0, 0, 0, 10.0, false, false));
                round.setStroke(Color.BLACK);
                round.setStrokeLineJoin(StrokeLineJoin.MITER);
                round.setStrokeLineCap(StrokeLineCap.SQUARE);
                round.setStrokeWidth(3.5);
                round.setFill(null);
                
                miter = new Path();
                miter.getElements().addAll(
                		new MoveTo(15, 0),
                		new LineTo(0, 0),
                		new LineTo(0, 10));
                miter.setStroke(Color.BLACK);
                miter.setStrokeWidth(3.5);
                miter.setFill(null);
                miter.setStrokeLineJoin(StrokeLineJoin.MITER);
                miter.setStrokeLineCap(StrokeLineCap.SQUARE);
                
                bevel = new Path();
                bevel.getElements().addAll(
                		new MoveTo(15, 0),
                		new LineTo(7, 0),
                		new LineTo(0, 10));
                bevel.setStroke(Color.BLACK);
                bevel.setStrokeWidth(3.5);
                bevel.setFill(null);
                bevel.setStrokeLineJoin(StrokeLineJoin.MITER);
                bevel.setStrokeLineCap(StrokeLineCap.SQUARE);
                
                setText(null);
            }
            
			@Override
			protected void updateItem(Option<SlideStrokeJoin> item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null || item.getValue() == null) {
					setGraphic(null);
				} else {
					if (item.getValue() == SlideStrokeJoin.ROUND) {
						setGraphic(round);
					} else if (item.getValue() == SlideStrokeJoin.MITER) {
						setGraphic(miter);
					} else {
						setGraphic(bevel);
					}
				}
			}
		};
	}
	
	private static ListCell<Option<SlideStrokeCap>> createCapListCell() {
		return new ListCell<Option<SlideStrokeCap>>() {
			private final Line round;
			private final Line butt;
			private final Line square;
			
            {
                round = new Line(0, 0, 15, 0);
                round.setStroke(Color.BLACK);
                round.setStrokeLineJoin(StrokeLineJoin.MITER);
                round.setStrokeLineCap(StrokeLineCap.ROUND);
                round.setStrokeWidth(8);
                round.setFill(null);
                
                butt = new Line(0, 0, 15, 0);
                butt.setStroke(Color.BLACK);
                butt.setStrokeLineJoin(StrokeLineJoin.MITER);
                butt.setStrokeLineCap(StrokeLineCap.BUTT);
                butt.setStrokeWidth(8);
                butt.setFill(null);
                
                square = new Line(0, 0, 15, 0);
                square.setStroke(Color.BLACK);
                square.setStrokeLineJoin(StrokeLineJoin.MITER);
                square.setStrokeLineCap(StrokeLineCap.SQUARE);
                square.setStrokeWidth(8);
                square.setFill(null);
                
                setText(null);
            }
            
			@Override
			protected void updateItem(Option<SlideStrokeCap> item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null || item.getValue() == null) {
					setGraphic(null);
				} else {
					if (item.getValue() == SlideStrokeCap.ROUND) {
						setGraphic(round);
					} else if (item.getValue() == SlideStrokeCap.BUTT) {
						setGraphic(butt);
					} else {
						setGraphic(square);
					}
				}
			}
		};
	}
	
	private SlideStroke getControlValues() {
		SlidePaint paint = null;
		if (this.pkrColor.isVisible()) {
			paint = JavaFXTypeConverter.fromJavaFX(this.pkrColor.getValue());
		} else {
			paint = this.pkrGradient.getValue();
		}
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
		mutating = true;
		if (stroke != null) {
			SlidePaint paint = stroke.getPaint();
			if (paint == null) {
				pkrColor.setVisible(false);
				pkrGradient.setVisible(false);
			} else if (paint instanceof SlideColor) {
				pkrColor.setVisible(true);
				pkrGradient.setVisible(false);
				pkrColor.setValue(JavaFXTypeConverter.toJavaFX((SlideColor)paint));
			} else if (paint instanceof SlideGradient) {
				pkrColor.setVisible(false);
				pkrGradient.setVisible(true);
				pkrGradient.setValue((SlideGradient)paint);
			}
			SlideStrokeStyle style = stroke.getStyle();
			if (style != null) {
				this.cbJoin.setValue(new Option<SlideStrokeJoin>(null, style.getJoin()));
				this.cbCap.setValue(new Option<SlideStrokeCap>(null, style.getCap()));
				this.cbDashes.setValue(new Option<DashPattern>(null, DashPattern.getDashPattern(style.getDashes())));
			}
			this.spnWidth.getValueFactory().setValue(stroke.getWidth());
			this.spnRadius.getValueFactory().setValue(stroke.getRadius());
		} else {
			pkrColor.setVisible(false);
			pkrGradient.setVisible(false);
		}
		mutating = false;
	}
}
