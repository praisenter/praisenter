package org.praisenter.javafx.slide.editor;

import org.controlsfx.control.SegmentedButton;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.slide.JavaFXTypeConverter;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.resources.OpenIconic;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradient;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePadding;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;
import org.praisenter.slide.text.VerticalTextAlignment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
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
import javafx.stage.Popup;

public class ParagraphRibbonTab extends EditorRibbonTab {

	/** The openiconic glyph-font pack */
	private static final GlyphFont FONT_ICONIC	= GlyphFontRegistry.font("Icons");
	
	/** The fontawesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	private final SegmentedButton segHorizontalAlignment;
	private final SegmentedButton segVerticalAlignment;
	private final ToggleButton tglTextWrapping;
	private final Spinner<Double> spnPadding;
	
	public ParagraphRibbonTab() {
		super("Paragraph");

		// h-align
		ToggleButton tglLeft = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_LEFT));
		ToggleButton tglRight = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_RIGHT));
		ToggleButton tglCenter = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_CENTER));
		ToggleButton tglJustify = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_JUSTIFY));
		tglLeft.setSelected(true);
		tglLeft.setUserData(HorizontalTextAlignment.LEFT);
		tglRight.setUserData(HorizontalTextAlignment.RIGHT);
		tglCenter.setUserData(HorizontalTextAlignment.CENTER);
		tglJustify.setUserData(HorizontalTextAlignment.JUSTIFY);
		this.segHorizontalAlignment = new SegmentedButton(tglLeft, tglCenter, tglRight, tglJustify);
		// v-align
		ToggleButton tglTop = new ToggleButton("", FONT_ICONIC.create(OpenIconic.Glyph.VERTICAL_ALIGN_TOP));
		ToggleButton tglMiddle = new ToggleButton("", FONT_ICONIC.create(OpenIconic.Glyph.VERTICAL_ALIGN_CENTER));
		ToggleButton tglBottom = new ToggleButton("", FONT_ICONIC.create(OpenIconic.Glyph.VERTICAL_ALIGN_BOTTOM));
		tglTop.setSelected(true);
		tglTop.setUserData(VerticalTextAlignment.TOP);
		tglMiddle.setUserData(VerticalTextAlignment.CENTER);
		tglBottom.setUserData(VerticalTextAlignment.BOTTOM);
		this.segVerticalAlignment = new SegmentedButton(tglTop, tglMiddle, tglBottom);
		
		// text wrapping
		this.tglTextWrapping = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.PARAGRAPH));
		this.tglTextWrapping.setSelected(true);
		
		this.spnPadding = new Spinner<Double>(0.0, Double.MAX_VALUE, 0.0, 1.0);
		this.spnPadding.setPrefWidth(70);
		this.spnPadding.setEditable(true);
		
		// layout
		
		HBox row1 = new HBox(2, this.segHorizontalAlignment);
		HBox row2 = new HBox(2, this.segVerticalAlignment);
		HBox row3 = new HBox(2, this.tglTextWrapping, this.spnPadding);
		VBox layout = new VBox(2, row1, row2, row3);
		this.container.setCenter(layout);
	
		// events

		this.component.addListener((obs, ov, nv) -> {
			mutating = true;
			if (nv != null && nv instanceof ObservableTextComponent) {
				this.setDisable(false);
				ObservableTextComponent<?> otc = (ObservableTextComponent<?>)nv;
				switch (otc.getHorizontalTextAlignment()) {
					case LEFT:
						tglLeft.setSelected(true);
						break;
					case CENTER:
						tglCenter.setSelected(true);
						break;
					case RIGHT:
						tglRight.setSelected(true);
						break;
					case JUSTIFY:
						tglJustify.setSelected(true);
						break;
				}
				switch (otc.getVerticalTextAlignment()) {
					case TOP:
						tglTop.setSelected(true);
						break;
					case CENTER:
						tglMiddle.setSelected(true);
						break;
					case BOTTOM:
						tglBottom.setSelected(true);
						break;
				}
				this.spnPadding.getValueFactory().setValue(otc.getPadding().getTop());
				this.tglTextWrapping.setSelected(otc.isTextWrapping()); 
			} else {
				this.setDisable(true);
				tglLeft.setSelected(true);
				tglTop.setSelected(true);
				this.spnPadding.getValueFactory().setValue(0.0);
				this.tglTextWrapping.setSelected(true); 
			}
			mutating = false;
		});

		this.segHorizontalAlignment.getToggleGroup().selectedToggleProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				Object value = nv != null ? nv.getUserData() : null;
				if (value == null) {
					// default to left
					value = HorizontalTextAlignment.LEFT;
				}
				if (value instanceof HorizontalTextAlignment) {
					tc.setHorizontalTextAlignment((HorizontalTextAlignment)value);
				} else {
					tc.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
				}
			}
		});
		
		this.segVerticalAlignment.getToggleGroup().selectedToggleProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				Object value = nv != null ? nv.getUserData() : null;
				if (value == null) {
					// default to left
					value = VerticalTextAlignment.TOP;
				}
				if (value instanceof VerticalTextAlignment) {
					tc.setVerticalTextAlignment((VerticalTextAlignment)value);
				} else {
					tc.setVerticalTextAlignment(VerticalTextAlignment.TOP);
				}
			}
		});
		
		this.spnPadding.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setPadding(new SlidePadding(nv.doubleValue()));
			}
		});
		
		this.tglTextWrapping.selectedProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextWrapping(nv);
			}
		});
		
		// other tabs
		// text shadow
		// text glow
	}
}
