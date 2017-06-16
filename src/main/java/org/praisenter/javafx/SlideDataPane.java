package org.praisenter.javafx;

import org.praisenter.javafx.bible.BibleNavigationPane;
import org.praisenter.javafx.slide.PlaceholderSlideComboBox;
import org.praisenter.javafx.slide.SlideMode;
import org.praisenter.javafx.slide.SingleSlidePreviewPane;
import org.praisenter.slide.Slide;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SlideDataPane extends BorderPane {
	private final PraisenterContext context;
	
	private SingleSlidePreviewPane slidePreviewPane;
	
	private PlaceholderSlideComboBox cmbTemplate;
	private BibleNavigationPane bibleNavigationPane;
	
	public SlideDataPane(PraisenterContext context) {
		this.context = context;
		
		this.slidePreviewPane = new SingleSlidePreviewPane(context, SlideMode.PREVIEW);
	
		this.cmbTemplate = new PlaceholderSlideComboBox(context);
		this.bibleNavigationPane = new BibleNavigationPane(context);
		
		this.slidePreviewPane.setMaxSize(400, 300);
		this.slidePreviewPane.setMinSize(400, 300);
		this.setTop(this.slidePreviewPane);
		
		VBox editor = new VBox();
		
		Button btnPlay = new Button("Play");
		Button btnStop = new Button("Stop");
		
		editor.getChildren().add(this.cmbTemplate);
		editor.getChildren().add(new HBox(btnPlay, btnStop));
		editor.getChildren().add(this.bibleNavigationPane);
		
		// TODO show button
		// TODO select-display combobox
		
		this.setCenter(editor);
		
		btnPlay.setOnAction(e -> {
			this.slidePreviewPane.setMode(SlideMode.PREVIEW_NO_AUDIO);
			this.slidePreviewPane.play();
		});
		
		btnStop.setOnAction(e -> {
			this.slidePreviewPane.stop();
			this.slidePreviewPane.setMode(SlideMode.PREVIEW);
		});
		
		this.cmbTemplate.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				Slide slide = nv.copy();
				slide.setPlaceholderData(this.bibleNavigationPane.getValue());
				this.slidePreviewPane.setValue(slide);
			} else {
				this.slidePreviewPane.setValue(nv);
			}
		});
		
		// TODO show prev/next
		this.bibleNavigationPane.valueProperty().addListener((obs, ov, nv) -> {
			Slide slide = this.cmbTemplate.getValue();
			if (slide != null) {
				slide = slide.copy();
				slide.setPlaceholderData(nv);
				this.slidePreviewPane.setValue(slide);
			}
		});
	}
}
