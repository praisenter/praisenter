package org.praisenter.ui.display;

import java.util.List;
import java.util.Objects;

import org.praisenter.data.StringTextStore;
import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bible.BibleNavigationPane;
import org.praisenter.ui.slide.SlideMode;
import org.praisenter.ui.slide.SlideTemplateComboBox;
import org.praisenter.ui.slide.SlideView;
import org.praisenter.ui.translations.Translations;

import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class DisplayController extends BorderPane {
	private static final String REMOVED = "REMOVED";
	private static final String REPLACED = "REPLACED";
	
	private final GlobalContext context;
	
	public DisplayController(GlobalContext context) {
		this.context = context;
		
		SlideView slideView = new SlideView(context);
		slideView.setViewMode(SlideMode.PREVIEW);
		slideView.setClipEnabled(true);
		slideView.setFitToWidthEnabled(true);
		slideView.setMaxWidth(400);
	
		SlideView notificationView = new SlideView(context);
		notificationView.setViewMode(SlideMode.PREVIEW);
		notificationView.setClipEnabled(true);
		notificationView.setFitToWidthEnabled(true);
		notificationView.setMaxWidth(400);
		notificationView.setCheckeredBackgroundEnabled(false);
		notificationView.setAutoHideEnabled(true);
		
		SlideTemplateComboBox cmbSlideTemplate = new SlideTemplateComboBox(context);
		SlideTemplateComboBox cmbNotificationTemplate = new SlideTemplateComboBox(context);
		BibleNavigationPane bibleNavigationPane = new BibleNavigationPane(context);
		
		this.setTop(new StackPane(slideView, notificationView));
		
		VBox editor = new VBox();
		
//		Button btnPlay = new Button("Play");
//		Button btnStop = new Button("Stop");
//		
		ComboBox<DisplayTarget> cmbTarget = new ComboBox<DisplayTarget>(context.getDisplayManager().getDisplayTargets());
		cmbTarget.getSelectionModel().select(0);
		Button btnShowSlide = new Button(Translations.get("display.controller.show"));
		Button btnClearSlide = new Button(Translations.get("display.controller.hide"));
		
		TextField txtNotification = new TextField();
		txtNotification.setPromptText(Translations.get("display.controller.notification.text"));
		Button btnPreviewNotification = new Button(Translations.get("display.controller.preview"));
		Button btnShowNotification = new Button(Translations.get("display.controller.show"));
		Button btnClearNotification = new Button(Translations.get("display.controller.hide"));
		
		editor.getChildren().add(cmbSlideTemplate);
		editor.getChildren().add(new HBox(cmbTarget, btnShowSlide, btnClearSlide));
		editor.getChildren().add(bibleNavigationPane);
		editor.getChildren().add(cmbNotificationTemplate);
		editor.getChildren().add(txtNotification);
		editor.getChildren().add(new HBox(btnPreviewNotification, btnShowNotification, btnClearNotification));

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

		btnShowSlide.setOnAction(e -> {
			DisplayTarget target = cmbTarget.getValue();
			
			Slide newSlide = slideView.getSlide();
			Slide oldSlide = target.getSlide();
			
			if (Objects.equals(oldSlide, newSlide)) {
				// do placeholders only
				target.displaySlidePlaceholders(bibleNavigationPane.getValue());
			} else {
				target.displaySlide(newSlide);
			}
		});
		
		btnClearSlide.setOnAction(e -> {
			DisplayTarget target = cmbTarget.getValue();
			target.displaySlide(null);
		});
		
		btnPreviewNotification.setOnAction(e -> {
			Slide nv = cmbNotificationTemplate.getValue();
			if (nv != null) {
				Slide slide = nv.copy();
				slide.setPlaceholderData(new StringTextStore(txtNotification.getText()));
				notificationView.transitionSlide(slide);
			} else {
				notificationView.setSlide(null);
			}
		});
		
		btnShowNotification.setOnAction(e -> {
			DisplayTarget target = cmbTarget.getValue();
			
			Slide newSlide = cmbNotificationTemplate.getValue();
			if (newSlide != null) {
				newSlide = newSlide.copy();
				newSlide.setPlaceholderData(new StringTextStore(txtNotification.getText()));
				target.displayNotification(newSlide);
			}
		});
		
		btnClearNotification.setOnAction(e -> {
			DisplayTarget target = cmbTarget.getValue();
			target.displayNotification(null);
		});
		
		// TODO how can we allow the user to select nothing?
		cmbSlideTemplate.getItems().addListener((Change<? extends Slide> c) -> {
			Slide cv = cmbSlideTemplate.getValue();
			String result = this.getCurrentItemAction(c, cv);
			
			// removed only?
			if (result == REMOVED) {
				slideView.setSlide(null);
				cmbSlideTemplate.setValue(null);
			} else if (result == REPLACED) {
				// replaced - was probably edited and saved
				// just reselect the value
				Slide slide = cmbSlideTemplate.getSelectionModel().getSelectedItem();
				cmbSlideTemplate.setValue(null);
				cmbSlideTemplate.getSelectionModel().select(slide);
			}
        });
		cmbNotificationTemplate.getItems().addListener((Change<? extends Slide> c) -> {
			Slide cv = cmbNotificationTemplate.getValue();
			String result = this.getCurrentItemAction(c, cv);
			
			// removed only?
			if (result == REMOVED) {
				notificationView.setSlide(null);
				cmbNotificationTemplate.setValue(null);
			} else if (result == REPLACED) {
				// replaced - was probably edited and saved
				// just reselect the value
				Slide slide = cmbNotificationTemplate.getSelectionModel().getSelectedItem();
				cmbNotificationTemplate.setValue(null);
				cmbNotificationTemplate.getSelectionModel().select(slide);
			}
        });
		
		cmbSlideTemplate.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				Slide slide = nv.copy();
				slide.setPlaceholderData(bibleNavigationPane.getValue());
				// TODO add checkbox for transition slide
//				this.slideView.swapSlide(slide);
				// TODO add checkbox for transition placeholders
//				this.slideView.setSlide(slide);
				slideView.transitionSlide(slide);
			} else {
				slideView.setSlide(null);
			}
		});
		
		bibleNavigationPane.valueProperty().addListener((obs, ov, nv) -> {
			slideView.transitionPlaceholders(nv.copy());
		});
	}
	
	private String getCurrentItemAction(Change<? extends Slide> c, Slide cv) {
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
			return REMOVED;
		} else if (removed && added) {
			return REPLACED;
		}
		return null;
	}
}
