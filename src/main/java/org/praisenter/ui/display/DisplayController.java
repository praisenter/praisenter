package org.praisenter.ui.display;

import java.util.List;
import java.util.Objects;

import org.praisenter.data.StringTextStore;
import org.praisenter.data.TextStore;
import org.praisenter.data.configuration.DisplayRole;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.graphics.SlideStrokeCap;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Option;
import org.praisenter.ui.bible.BibleNavigationPane;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.slide.SlideMode;
import org.praisenter.ui.slide.SlideTemplateComboBox;
import org.praisenter.ui.slide.SlideView;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.robot.Robot;

public final class DisplayController extends BorderPane {
	private static final String REMOVED = "REMOVED";
	private static final String REPLACED = "REPLACED";
	
	private final GlobalContext context;
	private final DisplayTarget target;
	
	private final DoubleProperty maxWidth = new SimpleDoubleProperty(400);
	private final DoubleBinding maxHeight;
	
	public DisplayController(GlobalContext context, DisplayTarget target) {
		this.context = context;
		this.target = target;
		
		Robot robot = new Robot();
		WritableImage image = robot.getScreenCapture(null, 
				target.getDisplay().getX(), 
				target.getDisplay().getY(), 
				target.getDisplay().getWidth(), 
				target.getDisplay().getHeight());
		
		this.maxHeight = this.maxWidth.divide(target.getDisplay().widthProperty()).multiply(target.getDisplay().heightProperty());
		
		ObservableList<Option<DisplayRole>> displayRoleOptions = FXCollections.observableArrayList();
		displayRoleOptions.add(new Option<>(Translations.get("display.role." + DisplayRole.NONE), DisplayRole.NONE));
		displayRoleOptions.add(new Option<>(Translations.get("display.role." + DisplayRole.MAIN), DisplayRole.MAIN));
		displayRoleOptions.add(new Option<>(Translations.get("display.role." + DisplayRole.TELEPROMPT), DisplayRole.TELEPROMPT));
		displayRoleOptions.add(new Option<>(Translations.get("display.role." + DisplayRole.OTHER), DisplayRole.OTHER));
		ChoiceBox<Option<DisplayRole>> cbDisplayRole = new ChoiceBox<>(displayRoleOptions);
		
		ImageView screen = new ImageView(image);
		screen.fitWidthProperty().bind(this.maxWidth);
		screen.setPreserveRatio(true);
		
		SlideView slideView = new SlideView(context);
		slideView.setViewMode(SlideMode.PREVIEW);
		slideView.setClipEnabled(true);
		slideView.setFitToWidthEnabled(true);
		slideView.setCheckeredBackgroundEnabled(false);
		slideView.prefWidthProperty().bind(this.maxWidth);
		slideView.prefHeightProperty().bind(this.maxHeight);
	
		SlideView notificationView = new SlideView(context);
		notificationView.setViewMode(SlideMode.PREVIEW);
		notificationView.setClipEnabled(true);
		notificationView.setFitToWidthEnabled(true);
		notificationView.setCheckeredBackgroundEnabled(false);
		notificationView.setAutoHideEnabled(true);
		notificationView.prefWidthProperty().bind(this.maxWidth);
		notificationView.prefHeightProperty().bind(this.maxHeight);
		
		SlideTemplateComboBox cmbSlideTemplate = new SlideTemplateComboBox(context);
		SlideTemplateComboBox cmbNotificationTemplate = new SlideTemplateComboBox(context);
		BibleNavigationPane bibleNavigationPane = new BibleNavigationPane(context);
		
		VBox layout = new VBox();
		
//		Button btnPlay = new Button("Play");
//		Button btnStop = new Button("Stop");

		Button btnShowSlide = new Button(Translations.get("display.controller.show"));
		Button btnClearSlide = new Button(Translations.get("display.controller.hide"));
		
		TextField txtNotification = new TextField();
		txtNotification.setPromptText(Translations.get("display.controller.notification.text"));
		Button btnPreviewNotification = new Button(Translations.get("display.controller.preview"));
		Button btnShowNotification = new Button(Translations.get("display.controller.show"));
		Button btnClearNotification = new Button(Translations.get("display.controller.hide"));
		CheckBox chkAutoShow = new CheckBox(Translations.get("display.controller.autoshow"));
		CheckBox chkPreviewTransition = new CheckBox(Translations.get("display.controller.previewTransition"));
		
		layout.getChildren().add(new StackPane(screen, slideView, notificationView));
		layout.getChildren().add(chkPreviewTransition);
		layout.getChildren().add(cmbSlideTemplate);
		layout.getChildren().add(new HBox(chkAutoShow, btnShowSlide, btnClearSlide));
		layout.getChildren().add(bibleNavigationPane);
		layout.getChildren().add(cmbNotificationTemplate);
		layout.getChildren().add(txtNotification);
		layout.getChildren().add(new HBox(btnPreviewNotification, btnShowNotification, btnClearNotification));
		
		this.setTop(cbDisplayRole);
		this.setCenter(layout);
		
		layout.visibleProperty().bind(cbDisplayRole.valueProperty().isNotEqualTo(new Option<>(null, DisplayRole.NONE)));
		layout.managedProperty().bind(layout.visibleProperty());
		
		cbDisplayRole.setValue(new Option<>(null, target.getDisplay().getRole()));
		BindingHelper.bindBidirectional(cbDisplayRole.valueProperty(), target.getDisplay().roleProperty());
		
		cbDisplayRole.valueProperty().addListener((obs, ov, nv) -> {
			if (nv.getValue() == DisplayRole.NONE) {
				// clear the screen
				target.clear();
			}
		});
		
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
			Slide newSlide = cmbSlideTemplate.getValue();
			Slide oldSlide = target.getSlide();
			
			final TextStore data = bibleNavigationPane.getValue();
			if (this.isPlaceholderTransitionOnly(oldSlide, newSlide)) {
				// do placeholders only
				target.displaySlidePlaceholders(data);
			} else {
				target.displaySlide(newSlide, data);
			}
		});
		
		btnClearSlide.setOnAction(e -> {
			target.displaySlide(null, null);
		});
		
		btnPreviewNotification.setOnAction(e -> {
			boolean transition = chkPreviewTransition.isSelected();
			Slide nv = cmbNotificationTemplate.getValue();
			if (nv != null) {
				Slide slide = nv.copy();
				slide.setPlaceholderData(new StringTextStore(txtNotification.getText()));
				slide.fit(target.getDisplay().getWidth(), target.getDisplay().getHeight());
				if (transition) {
					notificationView.transitionSlide(slide);
				} else { 
					notificationView.swapSlide(slide);
				}
			} else {
				notificationView.swapSlide(null);
			}
		});
		
		btnShowNotification.setOnAction(e -> {
			Slide newSlide = cmbNotificationTemplate.getValue();
			if (newSlide != null) {
				target.displayNotification(newSlide, new StringTextStore(txtNotification.getText()));
			}
		});
		
		btnClearNotification.setOnAction(e -> {
			target.displayNotification(null, null);
		});
		
		// TODO how can we allow the user to select nothing?
		cmbSlideTemplate.getItems().addListener((Change<? extends Slide> c) -> {
			Slide cv = cmbSlideTemplate.getValue();
			String action = this.getChangeAction(c, cv);
			if (action == REMOVED) {
				cmbSlideTemplate.setValue(null);
			} else if (action == REPLACED) {
				cmbSlideTemplate.setValue(null);
				cmbSlideTemplate.setValue(cv);
			}
        });
		cmbNotificationTemplate.getItems().addListener((Change<? extends Slide> c) -> {
			Slide cv = cmbNotificationTemplate.getValue();
			String action = this.getChangeAction(c, cv);
			if (action == REMOVED) {
				cmbNotificationTemplate.setValue(null);
			} else if (action == REPLACED) {
				cmbNotificationTemplate.setValue(null);
				cmbNotificationTemplate.setValue(cv);
			}
        });
		
		cmbSlideTemplate.valueProperty().addListener((obs, ov, nv) -> {
			boolean transition = chkPreviewTransition.isSelected();
			if (nv != null) {
				Slide slide = nv.copy();
				slide.setPlaceholderData(bibleNavigationPane.getValue().copy());
				slide.fit(target.getDisplay().getWidth(), target.getDisplay().getHeight());
				// TODO add checkbox for transition slide
//				this.slideView.swapSlide(slide);
				// TODO add checkbox for transition placeholders
//				this.slideView.setSlide(slide);
				if (transition) {
					slideView.transitionSlide(slide);
				} else {
					slideView.swapSlide(slide);
				}
			} else {
				slideView.swapSlide(null);
			}
		});
		
		bibleNavigationPane.valueProperty().addListener((obs, ov, nv) -> {
			boolean transition = chkPreviewTransition.isSelected();
			if (transition) {
				slideView.transitionPlaceholders(nv.copy());
			} else {
				slideView.swapPlaceholders(nv.copy());
			}
			
			if (chkAutoShow.isSelected()) {
				Slide newSlide = cmbSlideTemplate.getValue();
				Slide oldSlide = target.getSlide();
				
				if (this.isPlaceholderTransitionOnly(oldSlide, newSlide)) {
					// do placeholders only
					target.displaySlidePlaceholders(nv);
				} else {
					target.displaySlide(newSlide, nv);
				}
			}
		});
	}
	
	private boolean isPlaceholderTransitionOnly(Slide oldSlide, Slide newSlide) {
		// they must be the same object instance
		if (Objects.equals(oldSlide, newSlide)) {
			// they must both be non-null
			if (oldSlide != null && newSlide != null) {
				// they must have the same modified date
				if (newSlide.getModifiedDate().equals(oldSlide.getModifiedDate())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String getChangeAction(Change<? extends Slide> c, Slide cv) {
		// if the items change we need to examine if the change was the current slide we're on
		while (c.next()) {
			// first check if the slide we currently have selected was
			// added (updated)
			List<? extends Slide> as = c.getAddedSubList();
			for (Slide add : as) {
				if (add.equals(cv)) {
					// then we need to update the value
					return REPLACED;
				}
			}
			
			// next check if the slide we currently have selected was
			// removed (deleted)
			List<? extends Slide> rs = c.getRemoved();
			for (Slide rm : rs) {
				if (rm.equals(cv)) {
					// then we need to clear the value
					return REMOVED;
				}
			}
		}
		
		// otherwise, keep the current value
		return null;
	}
}
