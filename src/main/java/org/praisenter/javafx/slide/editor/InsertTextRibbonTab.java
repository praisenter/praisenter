package org.praisenter.javafx.slide.editor;

import java.time.LocalDateTime;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.TextType;
import org.praisenter.TextVariant;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.ObservableBasicTextComponent;
import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.ObservableDateTimeComponent;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;
import org.praisenter.javafx.slide.SlideMode;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.CountdownComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;
import org.praisenter.slide.text.TextPlaceholderComponent;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InsertTextRibbonTab extends EditorRibbonTab<ObservableSlide<?>> {
	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	private final PraisenterContext context;
	
	public InsertTextRibbonTab(PraisenterContext context) {
		super("Text");
		
		this.context = context;
		
		Button text = new Button("Text", FONT_AWESOME.create(FontAwesome.Glyph.FONT).size(40));
		Button countdown = new Button("Countdown", FONT_AWESOME.create(FontAwesome.Glyph.CLOCK_ALT).size(40));
		Button datetime = new Button("Date/Time", FONT_AWESOME.create(FontAwesome.Glyph.CALENDAR_ALT).size(40));
		Button placeholder = new Button("Placeholder", FONT_AWESOME.create(FontAwesome.Glyph.TERMINAL).size(40));
		
		text.setContentDisplay(ContentDisplay.TOP);
		countdown.setContentDisplay(ContentDisplay.TOP);
		datetime.setContentDisplay(ContentDisplay.TOP);
		placeholder.setContentDisplay(ContentDisplay.TOP);
		
		// layout
		
		HBox row1 = new HBox(2, text, countdown, datetime, placeholder);
		VBox layout = new VBox(2, row1);
		this.container.setCenter(layout);
	
		// events

		text.setOnAction((e) -> {
			BasicTextComponent btc = new BasicTextComponent();
			btc.setWidth(200);
			btc.setHeight(100);
			btc.setText("New Text Component");
			btc.setTextPaint(new SlideColor(0, 0, 0, 1));
			btc.setFont(new SlideFont("Arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 20));
			fireEvent(new SlideComponentAddEvent(text, InsertTextRibbonTab.this, new ObservableBasicTextComponent<>(btc, context, SlideMode.EDIT)));
		});
		
		countdown.setOnAction((e) -> {
			CountdownComponent cdc = new CountdownComponent();
			cdc.setWidth(200);
			cdc.setHeight(100);
			cdc.setCountdownTarget(LocalDateTime.now().plusHours(1));
			cdc.setCountdownFormat("%4$02d:%5$02d:%6$02d");
			cdc.setTextPaint(new SlideColor(0, 0, 0, 1));
			cdc.setFont(new SlideFont("Arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 20));
			fireEvent(new SlideComponentAddEvent(countdown, InsertTextRibbonTab.this, new ObservableCountdownComponent(cdc, context, SlideMode.EDIT)));
		});
		
		datetime.setOnAction((e) -> {
			DateTimeComponent dtc = new DateTimeComponent();
			dtc.setWidth(200);
			dtc.setHeight(100);
			dtc.setTextPaint(new SlideColor(0, 0, 0, 1));
			dtc.setFont(new SlideFont("Arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 20));
			fireEvent(new SlideComponentAddEvent(datetime, InsertTextRibbonTab.this, new ObservableDateTimeComponent(dtc, context, SlideMode.EDIT)));
		});
		
		placeholder.setOnAction((e) -> {
			TextPlaceholderComponent phc = new TextPlaceholderComponent();
			phc.setWidth(200);
			phc.setHeight(100);
			phc.setPlaceholderType(TextType.TITLE);
			phc.setPlaceholderVariant(TextVariant.PRIMARY);
			phc.setTextPaint(new SlideColor(0, 0, 0, 1));
			phc.setFont(new SlideFont("Arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 20));
			fireEvent(new SlideComponentAddEvent(placeholder, InsertTextRibbonTab.this, new ObservableTextPlaceholderComponent(phc, context, SlideMode.EDIT)));
		});
	}
}
