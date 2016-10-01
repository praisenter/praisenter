package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.slide.JavaFXTypeConverter;
import org.praisenter.javafx.slide.ObservableSlideRegion;
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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Callback;

class BorderRibbonTab extends ComponentEditorRibbonTab {

	private static final Color DEFAULT_PAINT = new Color(0, 0, 0, 1);
	private static final SlideLinearGradient DEFAULT_GRADIENT = new SlideLinearGradient(0, 0, 0, 1, SlideGradientCycleType.NONE, new SlideGradientStop(0, 0, 0, 0, 1), new SlideGradientStop(1, 0, 0, 0, 0.5));
	
	private final ColorPicker pkrColor;
	private final SlideGradientPicker pkrGradient;
	private final ComboBox<SlideStrokeJoin> cbJoin;
	private final ComboBox<SlideStrokeCap> cbCap;
	private final ComboBox<DashPattern> cbDashes;
	private final Spinner<Double> spnWidth;
	private final Spinner<Double> spnRadius;
	
	public BorderRibbonTab() {
		super("Border");

		MenuItem itmNone = new MenuItem("None");
		MenuItem itmColor = new MenuItem("Color");
		MenuItem itmGradient = new MenuItem("Gradient");
		
		Rectangle rect = new Rectangle(0, 0, 14, 13);
		rect.setStroke(Color.BLACK);
		rect.setStrokeWidth(1.0);
		rect.getStrokeDashArray().addAll(1.0, 2.0);
		rect.setStrokeLineJoin(StrokeLineJoin.MITER);
		rect.setStrokeLineCap(StrokeLineCap.SQUARE);
		rect.setStrokeMiterLimit(100);
		rect.setFill(null);
		
		MenuButton mnuPaintType = new MenuButton("", rect, itmNone, itmColor, itmGradient);
		this.pkrColor = new ColorPicker(DEFAULT_PAINT);
		this.pkrGradient = new SlideGradientPicker();
		this.pkrGradient.setValue(DEFAULT_GRADIENT);
		
		this.cbJoin = new ComboBox<SlideStrokeJoin>(FXCollections.observableArrayList(SlideStrokeJoin.values()));
		this.cbJoin.setCellFactory(new Callback<ListView<SlideStrokeJoin>, ListCell<SlideStrokeJoin>>() {
			@Override
			public ListCell<SlideStrokeJoin> call(ListView<SlideStrokeJoin> param) {
				return createJoinListCell();
			}
		});
		this.cbJoin.setButtonCell(createJoinListCell());
		this.cbJoin.setValue(SlideStrokeJoin.MITER);
		this.cbJoin.setMaxWidth(35.0);
		
		this.cbCap = new ComboBox<SlideStrokeCap>(FXCollections.observableArrayList(SlideStrokeCap.values()));
		this.cbCap.setCellFactory(new Callback<ListView<SlideStrokeCap>, ListCell<SlideStrokeCap>>() {
			@Override
			public ListCell<SlideStrokeCap> call(ListView<SlideStrokeCap> param) {
				return createCapListCell();
			}
		});
		this.cbCap.setButtonCell(createCapListCell());
		this.cbCap.setValue(SlideStrokeCap.SQUARE);
		this.cbCap.setMaxWidth(35.0);
		
		this.cbDashes = new ComboBox<DashPattern>(FXCollections.observableArrayList(DashPattern.values()));
		this.cbDashes.setCellFactory(new Callback<ListView<DashPattern>, ListCell<DashPattern>>() {
			@Override
			public ListCell<DashPattern> call(ListView<DashPattern> param) {
				return createPatternListCell();
			}
		});
		this.cbDashes.setButtonCell(createPatternListCell());
		this.cbDashes.setValue(DashPattern.SOLID);
		
		this.spnWidth = new Spinner<Double>(0, Double.MAX_VALUE, 1, 0.25);
		this.spnWidth.setMaxWidth(55);
		this.spnWidth.setEditable(true);
		this.spnRadius = new Spinner<Double>(0, Double.MAX_VALUE, 0, 0.25);
		this.spnRadius.setMaxWidth(55);
		this.spnRadius.setEditable(true);
		
		this.pkrColor.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
		this.pkrColor.setStyle("-fx-color-label-visible: false;");
		this.pkrColor.managedProperty().bind(pkrColor.visibleProperty());
		this.pkrGradient.managedProperty().bind(pkrGradient.visibleProperty());
		
		toggleMode(true, true);
		
		HBox row1 = new HBox(2, mnuPaintType, pkrColor, pkrGradient, spnWidth);
		HBox row2 = new HBox(2, cbJoin, cbCap);
		HBox row3 = new HBox(2, cbDashes, spnRadius);

		VBox layout = new VBox(2, row1, row2, row3);
		
		this.container.setCenter(layout);
		
		// events
		itmNone.setOnAction((e) -> {
			toggleMode(true, true);
		});
		itmColor.setOnAction((e) -> {
			toggleMode(false, true);
		});
		itmGradient.setOnAction((e) -> {
			toggleMode(false, false);
		});
		
		this.component.addListener((obs, ov, nv) -> {
			mutating = true;
			if (nv != null) {
				setControlValues(nv.getBorder());
				setDisable(false);
			} else {
				setControlValues(null);
				setDisable(true);
			}
			mutating = false;
		});
		
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (mutating) return;
				ObservableSlideRegion<?> comp = component.get();
				if (comp != null) {
					comp.setBorder(getControlValues());
				}
			}
		};
		
		this.pkrColor.valueProperty().addListener(listener);
		this.pkrGradient.valueProperty().addListener(listener);
		this.cbJoin.valueProperty().addListener(listener);
		this.cbCap.valueProperty().addListener(listener);
		this.cbDashes.valueProperty().addListener(listener);
		this.spnWidth.valueProperty().addListener(listener);
		this.spnRadius.valueProperty().addListener(listener);
	}
	
	// List Cell generation methods
	
	private static ListCell<SlideStrokeJoin> createJoinListCell() {
		return new ListCell<SlideStrokeJoin>() {
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
			protected void updateItem(SlideStrokeJoin item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					if (item == SlideStrokeJoin.ROUND) {
						setGraphic(round);
					} else if (item == SlideStrokeJoin.MITER) {
						setGraphic(miter);
					} else {
						setGraphic(bevel);
					}
				}
			}
		};
	}
	
	private static ListCell<SlideStrokeCap> createCapListCell() {
		return new ListCell<SlideStrokeCap>() {
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
			protected void updateItem(SlideStrokeCap item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					if (item == SlideStrokeCap.ROUND) {
						setGraphic(round);
					} else if (item == SlideStrokeCap.BUTT) {
						setGraphic(butt);
					} else {
						setGraphic(square);
					}
				}
			}
		};
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
	
	// values
	
	private void toggleMode(boolean off, boolean color) {
		this.pkrColor.setVisible(color);
		this.pkrGradient.setVisible(!color);
		this.pkrColor.setDisable(off);
		this.pkrGradient.setDisable(off);
		this.cbJoin.setDisable(off);
		this.cbCap.setDisable(off);
		this.cbDashes.setDisable(off);
		this.spnRadius.setDisable(off);
		this.spnWidth.setDisable(off);
		if (mutating) return;
		ObservableSlideRegion<?> component = this.component.get();
		if (component != null) {
			component.setBorder(off ? null : getControlValues());
		}
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
						this.cbJoin.getValue(), 
						this.cbCap.getValue(), 
						this.cbDashes.getValue().getDashes()), 
				this.spnWidth.getValue(), 
				this.spnRadius.getValue());
	}
	
	private void setControlValues(SlideStroke stroke) {
		if (stroke != null) {
			SlidePaint paint = stroke.getPaint();
			if (paint == null) {
				toggleMode(false, true);
			} else if (paint instanceof SlideColor) {
				toggleMode(false, true);
				this.pkrColor.setValue(JavaFXTypeConverter.toJavaFX((SlideColor)paint));
			} else if (paint instanceof SlideGradient) {
				toggleMode(false, false);
				this.pkrGradient.setValue((SlideGradient)paint);
			}
			SlideStrokeStyle style = stroke.getStyle();
			if (style != null) {
				this.cbJoin.setValue(style.getJoin());
				this.cbCap.setValue(style.getCap());
				this.cbDashes.setValue(DashPattern.getDashPattern(style.getDashes()));
			}
			this.spnWidth.getValueFactory().setValue(stroke.getWidth());
			this.spnRadius.getValueFactory().setValue(stroke.getRadius());
		} else {
			toggleMode(true, true);
		}
	}
}
