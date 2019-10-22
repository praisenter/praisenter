package org.praisenter.ui.slide;

import java.util.List;
import java.util.Objects;

import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bible.BibleNavigationPane;
import org.praisenter.ui.display.DisplayTarget;
import org.praisenter.ui.translations.Translations;

import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
		ComboBox<DisplayTarget> cmbTarget = new ComboBox<DisplayTarget>(context.getDisplayManager().getDisplayTargets());
		cmbTarget.getSelectionModel().select(0);
		Button btnShow = new Button(Translations.get("display.slide.show"));
		Button btnClear = new Button(Translations.get("display.slide.clear"));
		
		editor.getChildren().add(this.cmbTemplate);
		editor.getChildren().add(new HBox(cmbTarget, btnShow, btnClear));
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
		btnShow.setOnAction(e -> {
			DisplayTarget target = cmbTarget.getValue();
			
			Slide newSlide = this.slideView.getSlide();
			Slide oldSlide = target.getSlide();
			
			if (Objects.equals(oldSlide, newSlide)) {
				// do placeholders only
				target.display(this.bibleNavigationPane.getValue());
			} else {
				target.display(newSlide);
			}
		});
		
		btnClear.setOnAction(e -> {
			DisplayTarget target = cmbTarget.getValue();
			target.display((Slide)null);
		});
		
		this.cmbTemplate.getItems().addListener((Change<? extends Slide> c) -> {
			Slide cv = this.slideView.getSlide();
			// if the items change we need to examine if the change was the current slide we're on
			boolean removed = false;
			boolean added = false;
			while (c.next()) {
				List<? extends Slide> rs = c.getRemoved();
				for (Slide rm : rs) {
					if (rm.equals(cv)) {
						// then we need to clear the value
						removed = true;
						break;
					}
				}
				List<? extends Slide> as = c.getAddedSubList();
				for (Slide add : as) {
					if (add.equals(cv)) {
						// then we need to update the value
						added = true;
						break;
					}
				}	
			}
			
			// removed only?
			if (removed && !added) {
				this.slideView.setSlide(null);
			} else if (removed && added) {
				// replaced - was probably edited and saved
				// just reselect the value
				Slide slide = this.cmbTemplate.getSelectionModel().getSelectedItem();
				this.cmbTemplate.setValue(null);
				this.cmbTemplate.getSelectionModel().select(slide);
			}
        });
		
		this.cmbTemplate.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				Slide slide = nv.copy();
				slide.setPlaceholderData(this.bibleNavigationPane.getValue());
				// TODO add checkbox for transition slide
//				this.slideView.swapSlide(slide);
				// TODO add checkbox for transition placeholders
//				this.slideView.setSlide(slide);
				this.slideView.transitionSlide(slide);
			} else {
				this.slideView.setSlide(null);
			}
		});
		
		this.bibleNavigationPane.valueProperty().addListener((obs, ov, nv) -> {
			this.slideView.transitionPlaceholders(nv.copy());
		});
	}
}
