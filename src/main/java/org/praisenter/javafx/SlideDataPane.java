package org.praisenter.javafx;

import org.praisenter.javafx.bible.BibleNavigationPane;
import org.praisenter.javafx.slide.SlideComboBox;
import org.praisenter.javafx.slide.SlideMode;
import org.praisenter.javafx.slide.SlidePreviewPane;
import org.praisenter.slide.Slide;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SlideDataPane extends BorderPane {
	private final PraisenterContext context;
	
	private SlidePreviewPane slidePreviewPane;
	
	private SlideComboBox cmbTemplate;
	private BibleNavigationPane bibleNavigationPane;
	
	public SlideDataPane(PraisenterContext context) {
		this.context = context;
		
		this.slidePreviewPane = new SlidePreviewPane(context, SlideMode.SNAPSHOT);
	
		this.cmbTemplate = new SlideComboBox(context);
		this.bibleNavigationPane = new BibleNavigationPane(context);
		
		this.slidePreviewPane.setMaxSize(400, 300);
		this.slidePreviewPane.setMinSize(400, 300);
		this.setTop(this.slidePreviewPane);
		
		VBox editor = new VBox();
		
		editor.getChildren().add(this.cmbTemplate);
		editor.getChildren().add(this.bibleNavigationPane);
		
		this.setCenter(editor);
		
		this.cmbTemplate.slideProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				Slide slide = nv.copy();
				slide.setPlaceholderData(this.bibleNavigationPane.getValue());
				this.slidePreviewPane.setValue(slide);
			} else {
				this.slidePreviewPane.setValue(nv);
			}
		});
		
		this.bibleNavigationPane.valueProperty().addListener((obs, ov, nv) -> {
			Slide slide = this.cmbTemplate.getSlide();
			if (slide != null) {
				slide = slide.copy();
				slide.setPlaceholderData(nv);
				this.slidePreviewPane.setValue(slide);
			}
		});
	}
}
