package org.praisenter.javafx.slide.editor.ribbon;

import java.time.LocalDateTime;

import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;
import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.javafx.slide.ObservableBasicTextComponent;
import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.ObservableDateTimeComponent;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;
import org.praisenter.javafx.slide.SlideMode;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.events.SlideComponentAddEvent;
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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InsertTextRibbonTab extends SlideRegionRibbonTab<ObservableSlide<?>> {
	
	public InsertTextRibbonTab(SlideEditorContext context) {
		super(context, "Text");
		
		Button text = new Button("Text", ApplicationGlyphs.BASIC_TEXT_COMPONENT.duplicate().size(40));
		Button countdown = new Button("Countdown", ApplicationGlyphs.COUNTDOWN_COMPONENT.duplicate().size(40));
		Button datetime = new Button("Date/Time", ApplicationGlyphs.DATETIME_COMPONENT.duplicate().size(40));
		Button placeholder = new Button("Placeholder", ApplicationGlyphs.PLACEHOLDER_COMPONENT.duplicate().size(40));
		
		text.setContentDisplay(ContentDisplay.TOP);
		countdown.setContentDisplay(ContentDisplay.TOP);
		datetime.setContentDisplay(ContentDisplay.TOP);
		placeholder.setContentDisplay(ContentDisplay.TOP);
		
		// tooltips
		text.setTooltip(new Tooltip("Add a new text box"));
		countdown.setTooltip(new Tooltip("Add a new component that counts\ndown to a given date and/or time"));
		datetime.setTooltip(new Tooltip("Add a new component that shows\nthe current date and/or time"));
		placeholder.setTooltip(new Tooltip("Add a new component that serves\nas a placeholder for bible, song, or notification\ntext"));
		
		// layout
		
		HBox row1 = new HBox(2, text, countdown, datetime, placeholder);
		VBox layout = new VBox(2, row1);
		this.container.setCenter(layout);
	
		// events

		text.setOnAction((e) -> {
			BasicTextComponent btc = new BasicTextComponent();
			btc.setWidth(400);
			btc.setHeight(300);
			btc.setText("New Text Component");
			btc.setTextPaint(new SlideColor(0, 0, 0, 1));
			btc.setFont(new SlideFont("Arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 50));
			fireEvent(new SlideComponentAddEvent(text, InsertTextRibbonTab.this, new ObservableBasicTextComponent(btc, this.context.getPraisenterContext(), SlideMode.EDIT)));
		});
		
		countdown.setOnAction((e) -> {
			CountdownComponent cdc = new CountdownComponent();
			cdc.setWidth(400);
			cdc.setHeight(300);
			cdc.setCountdownTarget(LocalDateTime.now().plusHours(1));
			cdc.setCountdownFormat("%4$02d:%5$02d:%6$02d");
			cdc.setTextPaint(new SlideColor(0, 0, 0, 1));
			cdc.setFont(new SlideFont("Arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 50));
			fireEvent(new SlideComponentAddEvent(countdown, InsertTextRibbonTab.this, new ObservableCountdownComponent(cdc, this.context.getPraisenterContext(), SlideMode.EDIT)));
		});
		
		datetime.setOnAction((e) -> {
			DateTimeComponent dtc = new DateTimeComponent();
			dtc.setWidth(400);
			dtc.setHeight(300);
			dtc.setTextPaint(new SlideColor(0, 0, 0, 1));
			dtc.setFont(new SlideFont("Arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 50));
			fireEvent(new SlideComponentAddEvent(datetime, InsertTextRibbonTab.this, new ObservableDateTimeComponent(dtc, this.context.getPraisenterContext(), SlideMode.EDIT)));
		});
		
		placeholder.setOnAction((e) -> {
			TextPlaceholderComponent phc = new TextPlaceholderComponent();
			phc.setWidth(400);
			phc.setHeight(300);
			phc.setPlaceholderType(TextType.TITLE);
			phc.setPlaceholderVariant(TextVariant.PRIMARY);
			phc.setTextPaint(new SlideColor(0, 0, 0, 1));
			phc.setFont(new SlideFont("Arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 50));
			fireEvent(new SlideComponentAddEvent(placeholder, InsertTextRibbonTab.this, new ObservableTextPlaceholderComponent(phc, this.context.getPraisenterContext(), SlideMode.EDIT)));
		});
	}
}
