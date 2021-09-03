package org.praisenter.ui.display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.TriConsumer;
import org.praisenter.data.Persistable;
import org.praisenter.data.StringTextStore;
import org.praisenter.data.TextStore;
import org.praisenter.data.bible.BibleReferenceTextStore;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.song.SongReferenceTextStore;
import org.praisenter.data.workspace.DisplayRole;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Option;
import org.praisenter.ui.bible.BibleNavigationPane;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.slide.SlideList;
import org.praisenter.ui.slide.SlideMode;
import org.praisenter.ui.slide.SlideNavigationPane;
import org.praisenter.ui.slide.SlideTemplateComboBox;
import org.praisenter.ui.slide.SlideView;
import org.praisenter.ui.song.SongNavigationPane;
import org.praisenter.ui.translations.Translations;

import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.robot.Robot;
import javafx.util.Duration;

public final class DisplayController extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	private final DisplayTarget target;
	
	private final DoubleProperty maxWidth = new SimpleDoubleProperty(400);
	private final DoubleBinding maxHeight;
	
	private final ObservableList<Slide> slides;
	private final Slide WAS_REMOVED = new Slide();
	
	
//	private final ObjectProperty<TextStore> placeholderData;
	
	public DisplayController(GlobalContext context, DisplayTarget target) {
		this.context = context;
		this.target = target;
		
		this.slides = FXCollections.observableArrayList();
		
//		this.placeholderData = new SimpleObjectProperty<TextStore>();
		
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
		
		final DisplayIdentifier identify = new DisplayIdentifier(target.getDisplay());
		Button btnIdentify = new Button(Translations.get("display.identify"));
		btnIdentify.setOnAction(e -> {
			identify.show();
			
			Transition tx = new PauseTransition(new Duration(5000));
			tx.setOnFinished(ev -> {
				identify.hide();
			});
			
			tx.play();
		});
		
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
		slideView.maxWidthProperty().bind(this.maxWidth);
		slideView.maxHeightProperty().bind(this.maxHeight);
	
		SlideView notificationView = new SlideView(context);
		notificationView.setViewMode(SlideMode.PREVIEW);
		notificationView.setClipEnabled(true);
		notificationView.setFitToWidthEnabled(true);
		notificationView.setCheckeredBackgroundEnabled(false);
		notificationView.setAutoHideEnabled(true);
		notificationView.prefWidthProperty().bind(this.maxWidth);
		notificationView.prefHeightProperty().bind(this.maxHeight);
		
		SlideTemplateComboBox cmbNotificationTemplate = new SlideTemplateComboBox(context);
		BibleNavigationPane bibleNavigationPane = new BibleNavigationPane(context);
		SongNavigationPane songNavigationPane = new SongNavigationPane(context);
		SlideNavigationPane slideNavigationPane = new SlideNavigationPane(context);
		
		VBox layout = new VBox();
		
		Button btnShowSlide = new Button(Translations.get("display.controller.show"));
		Button btnClearSlide = new Button(Translations.get("display.controller.hide"));
		Button btnAddSlide = new Button(Translations.get("display.controller.addSlide"));
		Button btnRemoveSlide = new Button(Translations.get("display.controller.removeSlide"));
		
		TextField txtNotification = new TextField();
		txtNotification.setPromptText(Translations.get("display.controller.notification.text"));
		Button btnPreviewNotification = new Button(Translations.get("display.controller.preview"));
		Button btnShowNotification = new Button(Translations.get("display.controller.show"));
		Button btnClearNotification = new Button(Translations.get("display.controller.hide"));
		CheckBox chkAutoShow = new CheckBox(Translations.get("display.controller.autoshow"));
		CheckBox chkWaitForTransition = new CheckBox(Translations.get("display.controller.waitForTransition"));
		chkWaitForTransition.setTooltip(new Tooltip(Translations.get("display.controller.waitForTransition.tooltip")));
		CheckBox chkPreviewTransition = new CheckBox(Translations.get("display.controller.previewTransition"));
		
		TabPane tabs = new TabPane();
		tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		SlideTemplateComboBox cmbBibleSlideTemplate = new SlideTemplateComboBox(context);
		SlideTemplateComboBox cmbSongSlideTemplate = new SlideTemplateComboBox(context);
		
		VBox bibleTab = new VBox(cmbBibleSlideTemplate, bibleNavigationPane);
		VBox songTab = new VBox(cmbSongSlideTemplate, songNavigationPane);
		
		tabs.getTabs().add(new Tab(Translations.get("bible"), bibleTab));
		tabs.getTabs().add(new Tab(Translations.get("song"), songTab));
		tabs.getTabs().add(new Tab(Translations.get("slide"), slideNavigationPane));
		
		layout.getChildren().add(new StackPane(screen, slideView, notificationView));
		layout.getChildren().add(new HBox(chkPreviewTransition, chkWaitForTransition));
		layout.getChildren().add(new HBox(chkAutoShow, btnShowSlide, btnClearSlide, btnAddSlide, btnRemoveSlide));
		layout.getChildren().add(tabs);
		layout.getChildren().add(cmbNotificationTemplate);
		layout.getChildren().add(txtNotification);
		layout.getChildren().add(new HBox(btnPreviewNotification, btnShowNotification, btnClearNotification));
		
		VBox.setVgrow(tabs, Priority.ALWAYS);
//		layout.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
		
		// TODO move the remove button to above/below slide list
		SlideList list = new SlideList(context, target.getDisplay().getWidth(), target.getDisplay().getHeight());
		Bindings.bindContent(list.getSlides(), this.slides);
		
		// listen for changes to the slides (remove and update)
		context.getWorkspaceManager().getItemsUnmodifiable().addListener((Change<? extends Persistable> c) -> {
			Map<Integer, Slide> toReplace = new HashMap<>();
			List<Slide> toRemove = new ArrayList<>();
			
			while (c.next()) {
				if (c.wasAdded()) {
					for (Persistable p : c.getAddedSubList()) {
						int i = 0;
						for (Slide s : this.slides) {
							if (p.getId().equals(s.getId())) {
								// then it was updated
								Slide s1 = (Slide)p;
								s1.setPlaceholderData(s.getPlaceholderData());
								toReplace.put(i, s1);
								break;
							}
							i++;
						}
					}
				}
				
				if (c.wasRemoved()) {
					for (Persistable p : c.getRemoved()) {
						for (Slide s : this.slides) {
							if (p.getId().equals(s.getId())) {
								// then it was removed
								toRemove.add(s);
								break;
							}
						}
					}
				}
			}
			
			for (int key : toReplace.keySet()) {
				this.slides.set(key, toReplace.get(key));
			}
			
			this.slides.removeAll(toRemove);
		});
		
		this.setTop(new HBox(5, cbDisplayRole, btnIdentify));
		this.setCenter(layout);
		this.setLeft(list);
		
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
		
		btnAddSlide.setOnAction(e -> {
			// get the current slide w/ placeholder data
			Slide slide = null;
			TextStore data = new StringTextStore("");
			
			int index = tabs.getSelectionModel().getSelectedIndex();
			if (index == 0) {
				slide = cmbBibleSlideTemplate.getValue();
				data = bibleNavigationPane.getValue();
			} else if (index == 1) {
				slide = cmbSongSlideTemplate.getValue();
				data = songNavigationPane.getValue();
			} else if (index == 2) {
				slide = slideNavigationPane.getValue();
			} else {
				LOGGER.warn("Tab index '" + index + "' is not supported.");
			}
			
			if (slide != null) {
				slide = slide.copy();
				slide.fit(target.getDisplay().getWidth(), target.getDisplay().getHeight());
				slide.setPlaceholderData(data);
				this.slides.add(slide);
			}
		});
		
		btnRemoveSlide.setOnAction(e -> {
			List<Slide> selected = new ArrayList<>(list.getSelected());
			for (Slide s1 : selected) {
				int index = -1;
				for (int i = 0; i < this.slides.size(); i++) {
					Slide s2 = this.slides.get(i);
					if (s1 == s2) {
						index = i;
						break;
					}
				}
				
				if (index >= 0) {
					LOGGER.debug("Found index {} to remove", index);
					this.slides.remove(index);
				}
			}
		});

		btnShowSlide.setOnAction(e -> {
			Slide slide = null;
			TextStore data = null;
			
			int index = tabs.getSelectionModel().getSelectedIndex();
			if (index == 0) {
				slide = cmbBibleSlideTemplate.getValue();
				data = bibleNavigationPane.getValue();
			} else if (index == 1) {
				slide = cmbSongSlideTemplate.getValue();
				data = songNavigationPane.getValue();
			} else if (index == 2) {
				slide = slideNavigationPane.getValue();
			} else {
				LOGGER.warn("Tab index '" + index + "' is not supported.");
			}
			
			boolean waitForTransition = chkWaitForTransition.isSelected();
			Slide oldSlide = target.getSlide();
			// we need to do this additional check to make sure the slide
			// hasn't changed since the last time it was displayed
			if (this.isPlaceholderTransitionOnly(oldSlide, slide)) {
				// do placeholders only
				target.displaySlidePlaceholders(data, waitForTransition);
			} else {
				target.displaySlide(slide, data, waitForTransition);
			}
		});
		
		btnClearSlide.setOnAction(e -> {
			target.displaySlide(null, null, false);
		});
		
		btnPreviewNotification.setOnAction(e -> {
			boolean transition = chkPreviewTransition.isSelected();
			Slide nv = cmbNotificationTemplate.getValue();
			if (nv != null) {
				Slide slide = nv.copy();
				// if the slide doesn't have a time defined, choose an
				// arbitrary one so that it auto-hides
				// this is only for the preview, for the real display
				// we'll keep it up forever until the user manually
				// hides it
				if (slide.getTime() == Slide.TIME_FOREVER ||
					slide.getTime() == 0) {
					slide.setTime(3);
				}
				slide.setPlaceholderData(new StringTextStore(txtNotification.getText()));
				slide.fit(target.getDisplay().getWidth(), target.getDisplay().getHeight());
				if (transition) {
					notificationView.transitionSlide(slide, chkWaitForTransition.isSelected());
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
				target.displayNotification(newSlide, new StringTextStore(txtNotification.getText()), chkWaitForTransition.isSelected());
			}
		});
		
		btnClearNotification.setOnAction(e -> {
			target.displayNotification(null, null, false);
		});
		
		cmbBibleSlideTemplate.getItems().addListener((Change<? extends Slide> c) -> {
			Slide cv = cmbBibleSlideTemplate.getValue();
			Slide replacement = this.getChangeAction(c, cv);
			if (replacement == WAS_REMOVED) {
				cmbBibleSlideTemplate.setValue(null);
			} else if (replacement != null) {
				cmbBibleSlideTemplate.setValue(null);
				cmbBibleSlideTemplate.setValue(replacement);
			}
        });
		
		cmbSongSlideTemplate.getItems().addListener((Change<? extends Slide> c) -> {
			Slide cv = cmbSongSlideTemplate.getValue();
			Slide replacement = this.getChangeAction(c, cv);
			if (replacement == WAS_REMOVED) {
				cmbSongSlideTemplate.setValue(null);
			} else if (replacement != null) {
				cmbSongSlideTemplate.setValue(null);
				cmbSongSlideTemplate.setValue(replacement);
			}
        });
		
		cmbNotificationTemplate.getItems().addListener((Change<? extends Slide> c) -> {
			Slide cv = cmbNotificationTemplate.getValue();
			Slide replacement = this.getChangeAction(c, cv);
			if (replacement == WAS_REMOVED) {
				cmbNotificationTemplate.setValue(null);
			} else if (replacement != null) {
				cmbNotificationTemplate.setValue(null);
				cmbNotificationTemplate.setValue(replacement);
			}
        });
		
		final TriConsumer<DisplayChange, Slide, TextStore> handleDisplayChange = (change, slide, data) -> {
			boolean transition = chkPreviewTransition.isSelected();
			boolean waitForTransition = chkWaitForTransition.isSelected();
			boolean autoShow = chkAutoShow.isSelected();
			
			double tw = target.getDisplay().getWidth();
			double th = target.getDisplay().getHeight();
			
			if (data == null) {
				data = new StringTextStore("");
			}
			
			// update the slide view
			if (change == DisplayChange.HIDE || slide == null) {
				if (transition) {
					slideView.transitionSlide(null, waitForTransition);
				} else {
					slideView.swapSlide(null);
				}
			} else if (change == DisplayChange.DATA) {
				if (transition) {
					slideView.transitionPlaceholders(data.copy(), waitForTransition);
				} else {
					slideView.swapPlaceholders(data.copy());
				}
			} else {
				Slide sld = slide.copy();
				sld.setPlaceholderData(data);
				sld.fit(tw, th);
				
				if (transition) {
					slideView.transitionSlide(sld, waitForTransition);
				} else {
					slideView.swapSlide(sld);
				}
			}
			
			// update the display (if auto-show enabled)
			if (autoShow && change == DisplayChange.DATA) {
				Slide oldSlide = target.getSlide();
				// we need to do this additional check to make sure the slide
				// hasn't changed since the last time it was displayed
				if (this.isPlaceholderTransitionOnly(oldSlide, slide)) {
					// do placeholders only
					target.displaySlidePlaceholders(data, waitForTransition);
				} else {
					target.displaySlide(slide, data, waitForTransition);
				}
			}
		};
		
		cmbBibleSlideTemplate.valueProperty().addListener((obs, ov, nv) -> {
			if (tabs.getSelectionModel().getSelectedIndex() != 0) {
				return;
			}
			
			TextStore data = bibleNavigationPane.getValue();
			handleDisplayChange.accept(DisplayChange.STANDARD, nv, data);
		});
		
		cmbSongSlideTemplate.valueProperty().addListener((obs, ov, nv) -> {
			if (tabs.getSelectionModel().getSelectedIndex() != 1) {
				return;
			}
			
			TextStore data = songNavigationPane.getValue();
			handleDisplayChange.accept(DisplayChange.STANDARD, nv, data);
		});
		
		slideNavigationPane.valueProperty().addListener((obs, ov, nv) -> {
			if (tabs.getSelectionModel().getSelectedIndex() != 2) {
				return;
			}
			
			handleDisplayChange.accept(DisplayChange.STANDARD, nv, null);
		});
		
		tabs.getSelectionModel().selectedIndexProperty().addListener((obs, ov, nv) -> {
			Slide slide = null;
			TextStore data = null;
			int index = nv.intValue();
			
			if (index == 0) {
				data = bibleNavigationPane.getValue();
				slide = cmbBibleSlideTemplate.getValue();
			} else if (index == 1) {
				data = songNavigationPane.getValue();
				slide = cmbSongSlideTemplate.getValue();
			} else if (index == 2) {
				slide = slideNavigationPane.getValue();
			} else {
				LOGGER.warn("Tab index '" + index + "' is not supported.");
			}
			
			handleDisplayChange.accept(DisplayChange.TAB, slide, data);
		});
		
		bibleNavigationPane.valueProperty().addListener((obs, ov, nv) -> {
			if (tabs.getSelectionModel().getSelectedIndex() != 0) {
				return;
			}
			
			Slide slide = cmbBibleSlideTemplate.getValue();
			handleDisplayChange.accept(DisplayChange.DATA, slide, nv);
		});
		
		songNavigationPane.valueProperty().addListener((obs, ov, nv) -> {
			if (tabs.getSelectionModel().getSelectedIndex() != 1) {
				return;
			}
			
			Slide slide = cmbSongSlideTemplate.getValue();
			handleDisplayChange.accept(DisplayChange.DATA, slide, nv);
		});
		
		list.selectionProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				// check for slide first
				if (!nv.hasPlaceholders()) {
					// then it's a slide
					tabs.getSelectionModel().select(2);
					handleDisplayChange.accept(DisplayChange.STANDARD, nv.copy(), null);
					return;
				}
				
				// otherwise it has placeholder data
				TextStore data = nv.getPlaceholderData();
				if (data instanceof BibleReferenceTextStore) {
					// set the tab index
					tabs.getSelectionModel().select(0);
					// set the template selector
					Optional<Slide> template = cmbBibleSlideTemplate.getItems().stream().filter(t -> t.getId().equals(nv.getId())).findFirst();
					if (template.isPresent()) {
						cmbBibleSlideTemplate.setValue(template.get());
					}
					// set the bible fields
					BibleReferenceTextStore brts = (BibleReferenceTextStore)data;
					bibleNavigationPane.setValue(brts);
				} else if (data instanceof SongReferenceTextStore) {
					// set the tab index
					tabs.getSelectionModel().select(1);
					// set the template selector
					Optional<Slide> template = cmbSongSlideTemplate.getItems().stream().filter(t -> t.getId().equals(nv.getId())).findFirst();
					if (template.isPresent()) {
						cmbSongSlideTemplate.setValue(template.get());
					}
					// set the bible fields
					SongReferenceTextStore srts = (SongReferenceTextStore)data;
					songNavigationPane.setValue(srts);
				}
			}
		});
	}
	
	private boolean isPlaceholderTransitionOnly(Slide oldSlide, Slide newSlide) {
		// they must be the same slide (by identity)
		if (oldSlide == null && newSlide != null) return false;
		if (oldSlide != null && newSlide == null) return false;
		if (oldSlide == null && newSlide == null) return false;
		
		if (oldSlide.identityEquals(newSlide)) {
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
	
	private Slide getChangeAction(Change<? extends Slide> c, Slide cv) {
		// if the items change we need to examine if the change was the current slide we're on
		while (c.next()) {
			// first check if the slide we currently have selected was
			// added (updated)
			List<? extends Slide> as = c.getAddedSubList();
			for (Slide add : as) {
				if (add.identityEquals(cv)) {
					// then we need to update the value
					return add;
				}
			}
			
			// next check if the slide we currently have selected was
			// removed (deleted)
			List<? extends Slide> rs = c.getRemoved();
			for (Slide rm : rs) {
				if (rm.identityEquals(cv)) {
					// then we need to clear the value
					return WAS_REMOVED;
				}
			}
		}
		
		// otherwise, keep the current value
		return null;
	}
}
