package org.praisenter.javafx;

import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.bible.BibleLibraryPane;
import org.praisenter.javafx.media.MediaLibraryPane;
import org.praisenter.javafx.slide.SlideLibraryPane;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.layout.BorderPane;

public final class MainPane extends BorderPane {
	private final PraisenterContext context;
	
	private final MainMenu menu;
	
	private final SetupPane setupPane;
	private final BibleLibraryPane bibleLibraryPane;
	private final MediaLibraryPane mediaLibraryPane;
	private final SlideLibraryPane slideLibraryPane;
	
	private final ObjectProperty<ApplicationPane> focusedPane = new SimpleObjectProperty<ApplicationPane>();
	
	public MainPane(PraisenterContext context) {
		this.context = context;
		
		this.menu = new MainMenu(this);
		this.setTop(this.menu);
		
		this.setupPane = new SetupPane(context.getConfiguration());
		this.bibleLibraryPane = new BibleLibraryPane(context);
		this.mediaLibraryPane = new MediaLibraryPane(context, Orientation.HORIZONTAL);
		this.slideLibraryPane = new SlideLibraryPane(context);
		
		this.addEventHandler(ApplicationEvent.ALL, e -> {
			handleApplicationEvent(e.getAction());
		});
	}
	
	public void handleApplicationEvent(ApplicationAction action) {
		switch (action) {
			case PREFERENCES:
				setCenter(this.setupPane);
				break;
			case MANAGE_BIBLES:
				setCenter(this.bibleLibraryPane);
				break;
			case MANAGE_MEDIA:
				setCenter(this.mediaLibraryPane);
				break;
			case MANAGE_SLIDES:
				setCenter(this.slideLibraryPane);
				break;
			case EXIT:
				this.context.getJavaFXContext().getStage().close();
				break;
			default:
				// do nothing
				break;
		}
	}
}
