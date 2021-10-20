package org.praisenter.javafx;

import org.praisenter.javafx.bible.BibleNavigationPane;
import org.praisenter.javafx.display.DisplayTarget;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.PlaceholderSlideComboBox;
import org.praisenter.javafx.slide.SlideMode;
import org.praisenter.javafx.slide.SingleSlidePreviewPane;
import org.praisenter.slide.Slide;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SlideDataPane extends BorderPane {
	private final PraisenterContext context;
	
	private SingleSlidePreviewPane slidePreviewPane;
	
	private PlaceholderSlideComboBox cmbTemplate;
	private BibleNavigationPane bibleNavigationPane;
	
	public SlideDataPane(PraisenterContext context) {
		this.getStyleClass().add("slide-data-pane");
		
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
		
		ComboBox<DisplayTarget> cmbTarget = new ComboBox<DisplayTarget>(context.getDisplayManager().getDisplayTargets());
		cmbTarget.getSelectionModel().select(0);
		Button btnShow = new Button("Show");
		
		editor.getChildren().add(this.cmbTemplate);
		editor.getChildren().add(new HBox(btnPlay, btnStop, cmbTarget, btnShow));
		editor.getChildren().add(this.bibleNavigationPane);

		
		this.setCenter(editor);
		
		btnPlay.setOnAction(e -> {
			//this.slidePreviewPane.stop();
			// FIXME pressing play in a row can mess things up because components get altered during the transition. we may need to just recreate the slide by changing the mode to make it work
			this.slidePreviewPane.setMode(SlideMode.PREVIEW_NO_AUDIO);
			this.slidePreviewPane.play();
		});
		
		btnStop.setOnAction(e -> {
			this.slidePreviewPane.stop();
			this.slidePreviewPane.setMode(SlideMode.PREVIEW);
		});
		
		btnShow.setOnAction(e -> {
			DisplayTarget target = cmbTarget.getValue();
			Slide slide = this.slidePreviewPane.getValue();
			if (target != null && slide != null) {
				ObservableSlide<?> os = new ObservableSlide<Slide>(slide.copy(), context, SlideMode.PRESENT);
				target.send(os);
			}
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
		
		this.bibleNavigationPane.valueProperty().addListener((obs, ov, nv) -> {
			if (this.slidePreviewPane.getValue() != null) {
				this.slidePreviewPane.setPlaceholderData(nv);
			} else {
				Slide slide = this.cmbTemplate.getValue();
				if (slide != null) {
					slide = slide.copy();
					slide.setPlaceholderData(nv);
					this.slidePreviewPane.setValue(slide);
				}
			}
		});
	}
}
