package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.Option;
import org.praisenter.javafx.slide.JavaFXTypeConverter;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.graphics.DashPattern;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradient;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePadding;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.graphics.SlideStrokeCap;
import org.praisenter.slide.graphics.SlideStrokeJoin;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.VerticalTextAlignment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
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
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FontBorderRibbonTab extends EditorRibbonTab {

	private static final Color DEFAULT_PAINT = new Color(0, 0, 0, 1);
	private static final SlideLinearGradient DEFAULT_GRADIENT = new SlideLinearGradient(0, 0, 0, 1, SlideGradientCycleType.NONE, new SlideGradientStop(0, 0, 0, 0, 1), new SlideGradientStop(1, 0, 0, 0, 0.5));
	
	final ColorPicker pkrColor;
	final SlideGradientPicker pkrGradient;
	private final ChoiceBox<Option<SlideStrokeJoin>> cbJoin;
	private final ChoiceBox<Option<SlideStrokeCap>> cbCap;
	private final ChoiceBox<Option<DashPattern>> cbDashes;
	private final Spinner<Double> spnWidth;
	private final Spinner<Double> spnRadius;
	
	public FontBorderRibbonTab() {
		super("Font Border");
		
		ObservableList<Option<PaintType>> paintTypes = FXCollections.observableArrayList();
		paintTypes.add(new Option<PaintType>("Color", PaintType.COLOR));
		paintTypes.add(new Option<PaintType>("Gradient", PaintType.GRADIENT));
		

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
		
		
		// controls

		pkrFont = new SlideFontPicker(FXCollections.observableArrayList(Font.getFamilies()));
		cbFontScaling = new ChoiceBox<>(fontScaleTypes);
		spnLineSpacing = new Spinner<Double>(-Double.MAX_VALUE, Double.MAX_VALUE, 0, 0.5);
		
		//ChoiceBox<Option<PaintType>> cbTypes = new ChoiceBox<Option<PaintType>>(paintTypes);
		MenuItem itmColor = new MenuItem("Color");
		MenuItem itmGradient = new MenuItem("Gradient");
		Text lblPaintGraphic = new Text("A");
		lblPaintGraphic.setFont(Font.font("System", 11));
		lblPaintGraphic.setLayoutY(10);
		Pane pane = new Pane(lblPaintGraphic);
		pane.setMaxSize(10, 14);
		pane.setPadding(new Insets(0));
		pane.setBorder(new Border(new BorderStroke(Color.RED, new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1, 0, null), null, new BorderWidths(0, 0, 3, 0), new Insets(0))));
		MenuButton mnuPaintType = new MenuButton("", pane, itmColor, itmGradient);
		pkrColor = new ColorPicker(DEFAULT_PAINT);
		pkrGradient = new SlideGradientPicker();
		pkrGradient.setValue(DEFAULT_GRADIENT);
		
		pkrFont.setMaxWidth(200);
		spnLineSpacing.setMaxWidth(75);
		pkrColor.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
		pkrColor.setStyle("-fx-color-label-visible: false;");
		spnLineSpacing.setEditable(true);
		pkrColor.managedProperty().bind(pkrColor.visibleProperty());
		pkrGradient.managedProperty().bind(pkrGradient.visibleProperty());
		
		pkrGradient.setVisible(false);
		
		HBox row1 = new HBox(2, pkrFont);
		HBox row2 = new HBox(2, cbFontScaling, spnLineSpacing);
		HBox row3 = new HBox(2, mnuPaintType, pkrColor, pkrGradient);

		VBox layout = new VBox(2, row1, row2, row3);
		
		this.container.setCenter(layout);
	
		// events
		
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
			mutating = true;
			if (nv instanceof ObservableTextComponent) {
				ObservableTextComponent<?> otc = (ObservableTextComponent<?>)nv;
				this.pkrFont.setFont(otc.getFont());
				SlidePaint paint = otc.getTextPaint();
				if (paint != null) {
					if (paint instanceof SlideColor) {
						this.pkrColor.setValue(JavaFXTypeConverter.toJavaFX((SlideColor)paint));
						this.pkrColor.setVisible(true);
						this.pkrGradient.setVisible(false);
					} else if (paint instanceof SlideGradient) {
						this.pkrGradient.setValue((SlideGradient)paint);
						this.pkrColor.setVisible(false);
						this.pkrGradient.setVisible(true);
					} else {
						this.pkrColor.setValue(DEFAULT_PAINT);
						this.pkrColor.setVisible(true);
						this.pkrGradient.setVisible(false);
					}
				} else {
					this.pkrColor.setValue(DEFAULT_PAINT);
					this.pkrColor.setVisible(true);
					this.pkrGradient.setVisible(false);
				}
				this.cbFontScaling.setValue(new Option<FontScaleType>(null, otc.getFontScaleType()));
				this.spnLineSpacing.getValueFactory().setValue(otc.getLineSpacing()); 
			}
			mutating = false;
		});
		
		this.pkrColor.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextPaint(JavaFXTypeConverter.fromJavaFX(nv));
			}
		});
		
		this.pkrGradient.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextPaint(nv);
			}
		});
		
		this.pkrFont.fontProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setFont(nv);
			}
		});
		
		this.cbFontScaling.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setFontScaleType(nv.getValue());
			}
		});
		
		this.spnLineSpacing.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setLineSpacing(nv);
			}
		});
		
		// other tabs
		// text border fill
		// text border style
		// paragraph
		// text shadow
		// text glow
	}
}
