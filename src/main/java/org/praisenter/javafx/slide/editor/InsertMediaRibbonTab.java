package org.praisenter.javafx.slide.editor;

import java.util.Comparator;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.Tag;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.TagEvent;
import org.praisenter.javafx.TagListView;
import org.praisenter.javafx.configuration.Resolution;
import org.praisenter.javafx.slide.ObservableBasicTextComponent;
import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.SlideMode;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InsertMediaRibbonTab extends EditorRibbonTab<ObservableSlide<?>> {
	private static final GlyphFont OPEN_ICONIC = GlyphFontRegistry.font("Icons");
	
	/** The fontawesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	private final PraisenterContext context;
	
	public InsertMediaRibbonTab(PraisenterContext context) {
		super("Media");
		
		this.context = context;
		
		Button image = new Button("Image", FONT_AWESOME.create(FontAwesome.Glyph.IMAGE).size(40));
		Button video = new Button("Video", FONT_AWESOME.create(FontAwesome.Glyph.FILM).size(40));
		Button audio = new Button("Audio", FONT_AWESOME.create(FontAwesome.Glyph.MUSIC).size(40));
		
		image.setContentDisplay(ContentDisplay.TOP);
		video.setContentDisplay(ContentDisplay.TOP);
		audio.setContentDisplay(ContentDisplay.TOP);
		
		// layout
		
		HBox row1 = new HBox(2, image, video, audio);
		VBox layout = new VBox(2, row1);
		this.container.setCenter(layout);
	
		// events
		
		// TODO on the observable components, have then render some text when their primary content is empty during edit mode
		image.setOnAction((e) -> {
			MediaComponent mc = new MediaComponent();
			mc.setWidth(200);
			mc.setHeight(100);
			fireEvent(new SlideComponentAddEvent(image, InsertMediaRibbonTab.this, new ObservableMediaComponent(mc, context, SlideMode.EDIT)));
		});
		video.setOnAction((e) -> {
			MediaComponent mc = new MediaComponent();
			mc.setWidth(200);
			mc.setHeight(100);
			fireEvent(new SlideComponentAddEvent(video, InsertMediaRibbonTab.this, new ObservableMediaComponent(mc, context, SlideMode.EDIT)));
		});
		audio.setOnAction((e) -> {
			MediaComponent mc = new MediaComponent();
			mc.setWidth(200);
			mc.setHeight(100);
			fireEvent(new SlideComponentAddEvent(audio, InsertMediaRibbonTab.this, new ObservableMediaComponent(mc, context, SlideMode.EDIT)));
		});
	}
}
