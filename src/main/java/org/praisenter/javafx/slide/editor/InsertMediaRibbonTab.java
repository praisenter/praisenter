package org.praisenter.javafx.slide.editor;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.SlideMode;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.graphics.SlideColor;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InsertMediaRibbonTab extends EditorRibbonTab<ObservableSlide<?>> {
	/** The font-awesome glyph-font pack */
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
		
		// FIXME either 1) force the user to select a media item before adding the component or 2) add the component with some default content
		image.setOnAction((e) -> {
			MediaComponent mc = new MediaComponent();
			mc.setWidth(200);
			mc.setHeight(100);
			mc.setBackground(randomColor());
			fireEvent(new SlideComponentAddEvent(image, InsertMediaRibbonTab.this, new ObservableMediaComponent(mc, context, SlideMode.EDIT)));
		});
		video.setOnAction((e) -> {
			MediaComponent mc = new MediaComponent();
			mc.setWidth(200);
			mc.setHeight(100);
			mc.setBackground(randomColor());
			fireEvent(new SlideComponentAddEvent(video, InsertMediaRibbonTab.this, new ObservableMediaComponent(mc, context, SlideMode.EDIT)));
		});
		audio.setOnAction((e) -> {
			MediaComponent mc = new MediaComponent();
			mc.setWidth(200);
			mc.setHeight(100);
			mc.setBackground(randomColor());
			fireEvent(new SlideComponentAddEvent(audio, InsertMediaRibbonTab.this, new ObservableMediaComponent(mc, context, SlideMode.EDIT)));
		});
	}
	
	private static SlideColor randomColor() {
		double r = Math.random();
		double g = Math.random();
		double b = Math.random();
		return new SlideColor(r, g, b, 1.0);
	}
}
