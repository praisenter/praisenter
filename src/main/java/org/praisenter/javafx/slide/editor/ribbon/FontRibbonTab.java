package org.praisenter.javafx.slide.editor.ribbon;

import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.PreventUndoRedoEventFilter;
import org.praisenter.javafx.command.ActionEditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.javafx.slide.converters.PaintConverter;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.commands.FontEditCommand;
import org.praisenter.javafx.slide.editor.commands.FontScalingEditCommand;
import org.praisenter.javafx.slide.editor.commands.LineSpacingEditCommand;
import org.praisenter.javafx.slide.editor.commands.TextPaintEditCommand;
import org.praisenter.javafx.slide.editor.controls.SlideFontPicker;
import org.praisenter.javafx.slide.editor.controls.SlideGradientPicker;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradient;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
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

class FontRibbonTab extends ComponentEditorRibbonTab {
	private final SlideFontPicker pkrFont;
	private final ComboBox<Option<FontScaleType>> cmbFontScaling;
	private final Spinner<Double> spnLineSpacing;
	private final ColorPicker pkrColor;
	private final SlideGradientPicker pkrGradient;

	private static final Color DEFAULT_PAINT = new Color(0, 0, 0, 1);
	private static final SlideLinearGradient DEFAULT_GRADIENT = new SlideLinearGradient(0, 0, 0, 1, SlideGradientCycleType.NONE, new SlideGradientStop(0, 0, 0, 0, 1), new SlideGradientStop(1, 0, 0, 0, 0.5));
	
	public FontRibbonTab(SlideEditorContext context) {
		super(context, "Font");
		
		ObservableList<Option<FontScaleType>> fontScaleTypes = FXCollections.observableArrayList();
		fontScaleTypes.add(new Option<FontScaleType>("None", FontScaleType.NONE));
		fontScaleTypes.add(new Option<FontScaleType>("Reduce Size Only", FontScaleType.REDUCE_SIZE_ONLY));
		fontScaleTypes.add(new Option<FontScaleType>("Fill", FontScaleType.BEST_FIT));
		
		// controls

		this.pkrFont = new SlideFontPicker(FXCollections.observableArrayList(Font.getFamilies()));
		this.pkrFont.setFont(new SlideFont("Arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 50));
		this.pkrFont.setMaxWidth(200);
		
		this.cmbFontScaling = new ComboBox<>(fontScaleTypes);
		this.cmbFontScaling.setMaxWidth(120);
		this.cmbFontScaling.setValue(new Option<FontScaleType>(null, FontScaleType.NONE));
		this.cmbFontScaling.setButtonCell(new ListCell<Option<FontScaleType>>(){
			@Override
			protected void updateItem(Option<FontScaleType> item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(ApplicationGlyphs.FONT_SCALING.duplicate().color((Color)getTextFill()));
				if (item != null) {
					setText(item.getName());
				}
			}
		});
		
		this.spnLineSpacing = new Spinner<Double>(-Double.MAX_VALUE, Double.MAX_VALUE, 0, 0.5);
		this.spnLineSpacing.setEditable(true);
		this.spnLineSpacing.setMaxWidth(60);
		this.spnLineSpacing.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
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
		this.pkrColor = new ColorPicker(DEFAULT_PAINT);
		this.pkrGradient = new SlideGradientPicker();
		this.pkrGradient.setValue(DEFAULT_GRADIENT);
		
		this.pkrColor.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
		this.pkrColor.setStyle("-fx-color-label-visible: false;");
		this.pkrColor.managedProperty().bind(this.pkrColor.visibleProperty());
		
		this.pkrGradient.managedProperty().bind(this.pkrGradient.visibleProperty());
		this.pkrGradient.setVisible(false);
		
		// tooltips
		this.cmbFontScaling.setTooltip(new Tooltip("The font resizing method"));
		this.spnLineSpacing.setTooltip(new Tooltip("The spacing between each line of text"));
		mnuPaintType.setTooltip(new Tooltip("The font color"));
		this.pkrColor.setTooltip(new Tooltip("The font color"));
		this.pkrGradient.setTooltip(new Tooltip("The font gradient"));
		
		// layout
		
		HBox row1 = new HBox(2, this.pkrFont);
		HBox row2 = new HBox(2, this.cmbFontScaling, this.spnLineSpacing);
		HBox row3 = new HBox(2, mnuPaintType, this.pkrColor, this.pkrGradient);
		VBox layout = new VBox(2, row1, row2, row3);
		this.container.setCenter(layout);
	
		// events
		
		this.context.selectedProperty().addListener((obs, ov, nv) -> {
			this.mutating = true;
			if (nv != null && nv instanceof ObservableTextComponent) {
				this.setDisable(false);
				ObservableTextComponent<?> otc = (ObservableTextComponent<?>)nv;
				this.pkrFont.setFont(otc.getFont());
				SlidePaint paint = otc.getTextPaint();
				this.setPaintValues(paint);
				this.cmbFontScaling.setValue(new Option<FontScaleType>(null, otc.getFontScaleType()));
				this.spnLineSpacing.getValueFactory().setValue(otc.getLineSpacing()); 
			} else {
				this.pkrFont.setFont(new SlideFont());
				this.pkrColor.setValue(DEFAULT_PAINT);
				this.pkrColor.setVisible(true);
				this.pkrGradient.setVisible(false);
				this.cmbFontScaling.setValue(new Option<FontScaleType>(null, FontScaleType.NONE));
				this.spnLineSpacing.getValueFactory().setValue(0.0); 
				this.setDisable(true);
			}
			this.mutating = false;
		});
		
		itmColor.setOnAction((e) -> {
			this.pkrColor.setVisible(true);
			this.pkrGradient.setVisible(false);
			ObservableSlideRegion<?> component = this.context.getSelected();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;

				SlidePaint oldValue = tc.getTextPaint();
				SlidePaint newValue = PaintConverter.fromJavaFX(this.pkrColor.getValue());
				this.applyCommand(new TextPaintEditCommand(oldValue, newValue, tc, this.context.selectedProperty(), this.pkrColor,
									new ActionEditCommand(null, self -> {
										this.setPaintValues(oldValue);
									}, self -> {
										this.setPaintValues(newValue);
									})));
			}
		});
		itmGradient.setOnAction((e) -> {
			this.pkrColor.setVisible(false);
			this.pkrGradient.setVisible(true);
			ObservableSlideRegion<?> component = this.context.getSelected();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				
				SlidePaint oldValue = tc.getTextPaint();
				SlidePaint newValue = this.pkrGradient.getValue();
				this.applyCommand(new TextPaintEditCommand(oldValue, newValue, tc, this.context.selectedProperty(), this.pkrGradient,
									new ActionEditCommand(null, self -> {
										this.setPaintValues(oldValue);
									}, self -> {
										this.setPaintValues(newValue);
									})));
			}
		});
		
		this.pkrColor.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			ObservableSlideRegion<?> component = this.context.getSelected();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				
				SlidePaint oldValue = tc.getTextPaint();
				SlidePaint newValue = PaintConverter.fromJavaFX(nv);
				this.applyCommand(new TextPaintEditCommand(oldValue, newValue, tc, this.context.selectedProperty(), this.pkrColor,
									new ActionEditCommand(null, self -> {
										this.setPaintValues(oldValue);
									}, self -> {
										this.setPaintValues(newValue);
									})));
			}
		});
		
		this.pkrGradient.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			ObservableSlideRegion<?> component = this.context.getSelected();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;

				SlidePaint oldValue = tc.getTextPaint();
				SlidePaint newValue = nv;
				this.applyCommand(new TextPaintEditCommand(oldValue, newValue, tc, this.context.selectedProperty(), this.pkrGradient,
									new ActionEditCommand(null, self -> {
										this.setPaintValues(oldValue);
									}, self -> {
										this.setPaintValues(newValue);
									})));
			}
		});
		
		this.pkrFont.fontProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			ObservableSlideRegion<?> component = this.context.getSelected();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				this.applyCommand(new FontEditCommand(ov, nv, tc, this.context.selectedProperty(), this.pkrFont));
			}
		});
		
		this.cmbFontScaling.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			ObservableSlideRegion<?> component = this.context.getSelected();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				this.applyCommand(new FontScalingEditCommand(ov, nv, tc, this.context.selectedProperty(), this.cmbFontScaling));
			}
		});
		
		this.spnLineSpacing.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			ObservableSlideRegion<?> component = this.context.getSelected();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				this.applyCommand(new LineSpacingEditCommand(ov, nv, tc, this.context.selectedProperty(), this.spnLineSpacing));
			}
		});
	}
	
	private void setPaintValues(SlidePaint paint) {
		if (paint != null) {
			if (paint instanceof SlideColor) {
				this.pkrColor.setValue(PaintConverter.toJavaFX((SlideColor)paint));
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
	};
}
