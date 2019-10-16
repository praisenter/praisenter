package org.praisenter.ui.slide;

import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bible.BibleNavigationPane;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public final class SlideDataPane extends BorderPane {
	private final GlobalContext context;
	
	private SlideView slideView;
	
	private SlideTemplateComboBox cmbTemplate;
	private BibleNavigationPane bibleNavigationPane;
	
	public SlideDataPane(GlobalContext context) {
		this.context = context;
		
		this.slideView = new SlideView(context);
		this.slideView.setViewMode(SlideMode.PREVIEW);
		this.slideView.setClipEnabled(true);
		this.slideView.setFitToWidthEnabled(true);
//		this.slideView.setViewScaleAlignCenter(flag);
	
		this.cmbTemplate = new SlideTemplateComboBox(context);
		this.bibleNavigationPane = new BibleNavigationPane(context);
		
		this.slideView.setMaxWidth(400);
//		this.slideView.setMinSize(400, 300);
		this.setTop(this.slideView);
		
		VBox editor = new VBox();
		
//		Button btnPlay = new Button("Play");
//		Button btnStop = new Button("Stop");
//		
//		ComboBox<DisplayTarget> cmbTarget = new ComboBox<DisplayTarget>(context.getDisplayManager().getDisplayTargets());
//		cmbTarget.getSelectionModel().select(0);
//		Button btnShow = new Button("Show");
		
		editor.getChildren().add(this.cmbTemplate);
//		editor.getChildren().add(new HBox(btnPlay, btnStop, cmbTarget, btnShow));
		editor.getChildren().add(this.bibleNavigationPane);

		
		this.setCenter(editor);
		
//		btnPlay.setOnAction(e -> {
//			//this.slidePreviewPane.stop();
//			// FIXME pressing play in a row can mess things up because components get altered during the transition. we may need to just recreate the slide by changing the mode to make it work
//			this.slidePreviewPane.setMode(SlideMode.PREVIEW_NO_AUDIO);
//			this.slidePreviewPane.play();
//		});
//		
//		btnStop.setOnAction(e -> {
//			this.slidePreviewPane.stop();
//			this.slidePreviewPane.setMode(SlideMode.PREVIEW);
//		});
//		
//		btnShow.setOnAction(e -> {
//			DisplayTarget target = cmbTarget.getValue();
//			Slide slide = this.slidePreviewPane.getValue();
//			if (target != null && slide != null) {
//				ObservableSlide<?> os = new ObservableSlide<Slide>(slide.copy(), context, SlideMode.PRESENT);
//				target.send(os);
//			}
//		});
		
		this.cmbTemplate.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				Slide slide = nv.copy();
				slide.setPlaceholderData(this.bibleNavigationPane.getValue());
				this.slideView.setSlide(slide);
			} else {
				this.slideView.setSlide(nv);
			}
		});
		
		this.bibleNavigationPane.valueProperty().addListener((obs, ov, nv) -> {
			this.slideView.updatePlaceholdersWithTransition(nv);
//			this.slideView.updatePlaceholders(nv);
//			} else {
//				Slide slide = this.cmbTemplate.getValue();
//				if (slide != null) {
//					slide = slide.copy();
//					slide.setPlaceholderData(nv);
//					this.slidePreviewPane.setValue(slide);
//				}
//			}
		});
	}
}
