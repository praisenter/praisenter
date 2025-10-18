package org.praisenter.ui.display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.TriConsumer;
import org.praisenter.data.Persistable;
import org.praisenter.data.StringTextStore;
import org.praisenter.data.TextStore;
import org.praisenter.data.bible.BibleReferenceTextStore;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideReference;
import org.praisenter.data.song.SongReferenceTextStore;
import org.praisenter.data.workspace.DisplayConfiguration;
import org.praisenter.data.workspace.DisplayType;
import org.praisenter.data.workspace.ReadOnlyDisplayConfiguration;
import org.praisenter.ui.Action;
import org.praisenter.ui.ActionPane;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bible.BibleNavigationPane;
import org.praisenter.ui.bind.MappedList;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.slide.SlideList;
import org.praisenter.ui.slide.SlideMode;
import org.praisenter.ui.slide.SlideNavigationPane;
import org.praisenter.ui.slide.SlideTemplateComboBox;
import org.praisenter.ui.slide.SlideView;
import org.praisenter.ui.song.SongNavigationPane;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.StringManipulator;

import atlantafx.base.theme.Styles;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.robot.Robot;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.util.Duration;

public final class DisplayController extends BorderPane implements ActionPane {
	private static final String TAG_CSS = "p-tag";
	
	private static final String DISPLAY_CONTROLLER_CSS = "p-display-controller";
	private static final String DISPLAY_CONTROLLER_HEADER_CSS = "p-display-controller-header";
	private static final String DISPLAY_CONTROLLER_NAME_CSS = "p-display-controller-name";
	private static final String DISPLAY_CONTROLLER_LEFT_CSS = "p-display-controller-left";
	private static final String DISPLAY_CONTROLLER_RIGHT_CSS = "p-display-controller-right";
	private static final String DISPLAY_CONTROLLER_CHK_ROW_CSS = "p-display-controller-chk-row";
	private static final String DISPLAY_CONTROLLER_BTN_GRID_CSS = "p-display-controller-btn-grid";
	private static final String DISPLAY_CONTROLLER_TABS_CSS = "p-display-controller-tabs";
	private static final String DISPLAY_CONTROLLER_TAB_CSS = "p-display-controller-tab";
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	private final DisplayTarget target;
	
	private final DoubleBinding leftMaxWidth;
	private final DoubleBinding slidePreviewMaxHeight;
	
	private final ObservableList<Slide> slides;
	private final Slide WAS_REMOVED = new Slide();
	
	private final ObservableList<SlideReference> slideToSlideReferenceMapping;
	
	private final IntegerProperty lastTabIndex;
	
	private final SlideList slideList;
	
	private boolean selectingQueuedSlide = false;
	
	/** True if this display is linked (controlled by another) */
	private final StringProperty controllerDisplayName;
	private final BooleanProperty isControlled;
	
	private final ObservableList<DisplayTarget> watchableDisplays;
	
	/** The list of targets that this controller controls */
	private final ObservableList<DisplayTarget> controlledDisplays;
	
	/** The list of targets that could be controlled */
	private final ObservableList<DisplayTarget> controllableDisplays;
	private final ObservableList<MenuItem> displayMenuItems;
	
	public DisplayController(GlobalContext context, DisplayTarget target) {
		this.getStyleClass().add(DISPLAY_CONTROLLER_CSS);
		
		this.context = context;
		this.target = target;
		
		this.slides = FXCollections.observableArrayList();
		this.lastTabIndex = new SimpleIntegerProperty(0);
		
		
		DisplayConfiguration configuration = target.getDisplayConfiguration();

		this.controllerDisplayName = new SimpleStringProperty();
		this.controllerDisplayName.bind(Bindings.createStringBinding(() -> {
			int id = configuration.getControllingDisplayId();
			if (id == ReadOnlyDisplayConfiguration.NOT_CONTROLLED) {
				return "";
			} else {
				DisplayTarget tgt = getDisplayTargetForId(id);
				if (tgt != null) {
					String name = "";
					DisplayConfiguration config = tgt.getDisplayConfiguration();
					if (StringManipulator.isNullOrEmpty(config.getName())) {
						name = config.getDefaultName();
					}
					name = config.getName();
					return Translations.get("display.controlledby", name);
				}
			}
			return "";
		}, configuration.controllingDisplayIdProperty()));
		
		this.watchableDisplays = FXCollections.observableArrayList(dt -> new ObservableValue[] {
			dt.getDisplayConfiguration().controllingDisplayIdProperty()
		});
		Bindings.bindContent(this.watchableDisplays, context.getDisplayManager().getDisplayTargets());
		
		this.isControlled = new SimpleBooleanProperty();
		this.isControlled.bind(Bindings.createBooleanBinding(() -> {
			return configuration.getControllingDisplayId() != ReadOnlyDisplayConfiguration.NOT_CONTROLLED;
		}, configuration.controllingDisplayIdProperty()));
		
		this.controlledDisplays = this.watchableDisplays.filtered(dt -> {
			return dt.getDisplayConfiguration().getControllingDisplayId() == configuration.getId();
		});
		
		this.controllableDisplays = this.watchableDisplays.filtered(dt -> {
			return dt != target;
		});
		this.displayMenuItems = new MappedList<>(this.controllableDisplays, (tgt) -> {
			var config = tgt.getDisplayConfiguration();
			
			CheckMenuItem itm = new CheckMenuItem();
			itm.setOnAction((ActionEvent e) -> {
				if (config.getControllingDisplayId() == ReadOnlyDisplayConfiguration.NOT_CONTROLLED) {
					config.setControllingDisplayId(configuration.getId());
				} else {
					config.setControllingDisplayId(ReadOnlyDisplayConfiguration.NOT_CONTROLLED);
				}
				
				try {
					context.saveConfiguration();
				} catch (Exception ex) {
					// just log it i guess
					LOGGER.error("Failed to save display configuration for linked display change", ex);
				}
			});
			itm.setSelected(config.getControllingDisplayId() == configuration.getId());
			
			BooleanBinding disable = Bindings.createBooleanBinding(() -> {
				// disable ALL linking if this display is controlled
				if (configuration.getControllingDisplayId() != ReadOnlyDisplayConfiguration.NOT_CONTROLLED) {
					return true;
				}
				
				// disable linking of this target if it's controlled and not by this display
				int controller = config.getControllingDisplayId();
				if (controller != ReadOnlyDisplayConfiguration.NOT_CONTROLLED && controller != configuration.getId()) {
					return true;
				}
				
				// finally, don't allow controlling another display that is controlling something else
				if (this.watchableDisplays.stream().anyMatch(tmp -> tmp.getDisplayConfiguration().getControllingDisplayId() == config.getId())) {
					return true;
				}
				
				return false;
			}, configuration.controllingDisplayIdProperty(), config.controllingDisplayIdProperty(), this.watchableDisplays);
			
			itm.disableProperty().bind(disable);
			itm.textProperty().bind(Bindings.createStringBinding(() -> {
				if (StringManipulator.isNullOrEmpty(config.getName())) {
					return config.getDefaultName();
				}
				return config.getName();
			}, config.nameProperty(), config.defaultNameProperty()));
			
			return itm;
		});
		
		// Action menu
		// set name
		// set role
		// identify
		// hide
		
		MenuItem mnuSetName = new MenuItem(Translations.get("display.rename"));
		mnuSetName.setOnAction(e -> {
			String oldName = configuration.getName();
	    	TextInputDialog prompt = Dialogs.textInput(
	    			this.context.getStage(), 
	    			Modality.WINDOW_MODAL, 
	    			oldName, 
	    			Translations.get("action.rename"), 
	    			Translations.get("action.rename.newname"), 
	    			Translations.get("action.rename.name"));
	    	Optional<String> result = prompt.showAndWait();
	    	if (result.isPresent()) {
	    		String newName = result.get();
	    		configuration.setName(newName);
	    	}
		});
		mnuSetName.setVisible(target.getDisplayConfiguration().getType() != DisplayType.NDI);
		
		final DisplayIdentifier identify = new DisplayIdentifier(configuration);
		MenuItem mnuIdentify = new MenuItem(Translations.get("display.identify"));
		mnuIdentify.setOnAction(e -> {
			// if so, we need to confirm with the user before we do it
			Alert alert = Dialogs.yesNoCancel(
					context.getStage(), 
					Modality.WINDOW_MODAL, 
					Translations.get("display.identify.title"), 
					Translations.get("display.identify.header"), 
					Translations.get("display.identify.text"));
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.YES) {
				identify.show();
				
				Transition tx = new PauseTransition(new Duration(5000));
				tx.setOnFinished(ev -> {
					identify.hide();
				});
				
				tx.play();
			}
		});
		mnuIdentify.setVisible(target.getDisplayConfiguration().getType() != DisplayType.NDI);
		
		CheckMenuItem mnuPrimary = new CheckMenuItem(Translations.get("display.primary"));
		mnuPrimary.selectedProperty().bindBidirectional(configuration.primaryProperty());
		mnuPrimary.setOnAction(e -> {
			// clear the primary flag for all other configurations
			for (DisplayConfiguration conf : context.getWorkspaceConfiguration().getDisplayConfigurations()) {
				if (conf != configuration) conf.setPrimary(false);
			}
		});
		
		MenuItem mnuHide = new MenuItem(Translations.get("display.hide"));
		mnuHide.setOnAction(e -> {
			// if so, we need to confirm with the user before we do it
			Alert alert = Dialogs.yesNoCancel(
					context.getStage(), 
					Modality.WINDOW_MODAL, 
					Translations.get("display.hide.title"), 
					Translations.get("display.hide.header"), 
					Translations.get("display.hide.text"));
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.YES) {
				configuration.setActive(false);
			}
		});
		
		MenuItem mnuDelete = new MenuItem(Translations.get("display.remove"));
		mnuDelete.setOnAction(e -> {
			// if so, we need to confirm with the user before we do it
			Alert alert = Dialogs.yesNoCancel(
					context.getStage(), 
					Modality.WINDOW_MODAL, 
					Translations.get("display.remove.title"), 
					Translations.get("display.remove.header"), 
					Translations.get("display.remove.text"));
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.YES) {
				configuration.setActive(false);
				this.context.getDisplayManager().removeNDIDisplay(configuration);
			}
		});
		mnuDelete.setVisible(target.getDisplayConfiguration().getType() == DisplayType.NDI);
		
		Menu mnuLink = new Menu(Translations.get("display.link"));
		Bindings.bindContent(mnuLink.getItems(), this.displayMenuItems);
		
		MenuButton mnuActions = new MenuButton(Translations.get("display.actions"));
		mnuActions.getItems().addAll(
			mnuSetName,
			mnuPrimary,
			mnuLink,
			mnuIdentify,
			mnuHide,
			mnuDelete);
		
		Robot robot = new Robot();
		WritableImage image = robot.getScreenCapture(null, 
				configuration.getX(), 
				configuration.getY(), 
				configuration.getWidth(), 
				configuration.getHeight());
		
		VBox right = new VBox();
		right.getStyleClass().add(DISPLAY_CONTROLLER_RIGHT_CSS);
		
		this.leftMaxWidth = Bindings.createDoubleBinding(() -> {
			// NOTE: the "-1" here is to account for rounding errors when showing the image view
			return Math.floor(right.getMaxWidth()) - 1;
		}, right.maxWidthProperty());
//		this.leftMaxWidth = right.maxWidthProperty();
		this.slidePreviewMaxHeight = this.leftMaxWidth.divide(configuration.widthProperty()).multiply(configuration.heightProperty());
		
		ImageView screen = new ImageView(image);
		screen.fitWidthProperty().bind(this.leftMaxWidth);
		screen.fitHeightProperty().bind(this.slidePreviewMaxHeight);
		screen.setPreserveRatio(true);
		
		SlideView slideView = new SlideView(context);
		slideView.setViewMode(SlideMode.PREVIEW);
		slideView.setClipEnabled(true);
		slideView.setFitToWidthEnabled(true);
		slideView.setCheckeredBackgroundEnabled(false);
		slideView.prefWidthProperty().bind(this.leftMaxWidth);
		slideView.prefHeightProperty().bind(this.slidePreviewMaxHeight);
		slideView.maxWidthProperty().bind(this.leftMaxWidth);
		slideView.maxHeightProperty().bind(this.slidePreviewMaxHeight);
	
		SlideView notificationView = new SlideView(context);
		notificationView.setViewMode(SlideMode.PREVIEW);
		notificationView.setClipEnabled(true);
		notificationView.setFitToWidthEnabled(true);
		notificationView.setCheckeredBackgroundEnabled(false);
		notificationView.setAutoHideEnabled(true);
		notificationView.prefWidthProperty().bind(this.leftMaxWidth);
		notificationView.prefHeightProperty().bind(this.slidePreviewMaxHeight);
		
		BibleNavigationPane bibleNavigationPane = new BibleNavigationPane(context, configuration);
		SongNavigationPane songNavigationPane = new SongNavigationPane(context);
		SlideNavigationPane slideNavigationPane = new SlideNavigationPane(context);
		
		Button btnShow = new Button(Translations.get("display.controller.show"));
		Button btnClear = new Button(Translations.get("display.controller.hide"));
		Button btnQueueAdd = new Button(Translations.get("display.controller.queue.add"));
		Button btnQueueRemoveSelected = new Button(Translations.get("display.controller.queue.removeSelected"));
		Button btnQueueRemoveAll = new Button(Translations.get("display.controller.queue.removeAll"));
		
		btnShow.setMaxWidth(Double.MAX_VALUE);
		btnShow.setMaxHeight(Double.MAX_VALUE);
		btnClear.setMaxWidth(Double.MAX_VALUE);
		btnClear.setMaxHeight(Double.MAX_VALUE);
		
		CheckBox chkAutoShow = new CheckBox(Translations.get("display.controller.autoshow"));
		chkAutoShow.setSelected(configuration.isAutoShowEnabled());
		CheckBox chkPreviewTransition = new CheckBox(Translations.get("display.controller.previewTransition"));
		chkPreviewTransition.setSelected(configuration.isPreviewTransitionEnabled());
		
		chkAutoShow.selectedProperty().bindBidirectional(configuration.autoShowEnabledProperty());
		chkPreviewTransition.selectedProperty().bindBidirectional(configuration.previewTransitionEnabledProperty());
		
		TextField txtNotification = new TextField();
		txtNotification.setPromptText(Translations.get("display.controller.notification.text"));
		Button btnPreviewNotification = new Button(Translations.get("display.controller.preview"));
		Button btnShowNotification = new Button(Translations.get("display.controller.show"));
		Button btnClearNotification = new Button(Translations.get("display.controller.hide"));
		
		SlideTemplateComboBox cmbBibleSlideTemplate = new SlideTemplateComboBox(context);
		SlideTemplateComboBox cmbSongSlideTemplate = new SlideTemplateComboBox(context);
		SlideTemplateComboBox cmbNotificationTemplate = new SlideTemplateComboBox(context);
		
		cmbBibleSlideTemplate.setMaxWidth(Double.MAX_VALUE);
		cmbSongSlideTemplate.setMaxWidth(Double.MAX_VALUE);
		cmbNotificationTemplate.setMaxWidth(Double.MAX_VALUE);
		
		cmbBibleSlideTemplate.setPromptText(Translations.get("display.controller.template"));
		cmbSongSlideTemplate.setPromptText(Translations.get("display.controller.template"));
		cmbNotificationTemplate.setPromptText(Translations.get("display.controller.template"));
		
		VBox bibleTab = new VBox(cmbBibleSlideTemplate, bibleNavigationPane);
		VBox songTab = new VBox(cmbSongSlideTemplate, songNavigationPane);
		VBox notificationTab = new VBox(cmbNotificationTemplate, txtNotification, new HBox(5, btnPreviewNotification, btnShowNotification, btnClearNotification));
		
		VBox.setVgrow(bibleNavigationPane, Priority.ALWAYS);
		VBox.setVgrow(songNavigationPane, Priority.ALWAYS);
		
		bibleTab.getStyleClass().add(DISPLAY_CONTROLLER_TAB_CSS);
		songTab.getStyleClass().add(DISPLAY_CONTROLLER_TAB_CSS);
		notificationTab.getStyleClass().add(DISPLAY_CONTROLLER_TAB_CSS);

		TabPane tabs = new TabPane();
		tabs.getStyleClass().add(DISPLAY_CONTROLLER_TABS_CSS);
		tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		tabs.getTabs().add(new Tab(Translations.get("bible"), bibleTab));
		tabs.getTabs().add(new Tab(Translations.get("song"), songTab));
		tabs.getTabs().add(new Tab(Translations.get("slide"), slideNavigationPane));
		tabs.getTabs().add(new Tab(Translations.get("notification"), notificationTab));
		
		for (SlideReference sr : configuration.getQueuedSlidesUnmodifiable()) {
			if (sr.getSlideId() != null) {
				Persistable item = context.getWorkspaceManager().getPersistableById(sr.getSlideId());
				if (item != null && item instanceof Slide) {
					Slide slide = (Slide)item;
					slide = slide.copy();
					slide.setPlaceholderData(sr.getPlaceholderData());
					slide.fit(configuration.getWidth(), configuration.getHeight());
					this.slides.add(slide);
				}
			}
		}
		
		this.slideToSlideReferenceMapping = new MappedList<>(this.slides, (s) -> {
			SlideReference sr = new SlideReference();
			sr.setSlideId(s.getId());
			sr.setPlaceholderData(s.getPlaceholderData());
			return sr;
		});
		Bindings.bindContent(configuration.getQueuedSlides(), this.slideToSlideReferenceMapping);
		
		// NOTE: we have to do a bidirectional binding here because the SlideList itself
		// allows delete, copy, paste, and dnd
		SlideList lstSlideQueue = new SlideList(context);
		Label lblEmptySlideList = new Label(Translations.get("display.controller.slide.queue.empty"));
		lblEmptySlideList.setWrapText(true);
		lblEmptySlideList.setTextAlignment(TextAlignment.CENTER);
		lstSlideQueue.setPlaceholder(lblEmptySlideList);
		Bindings.bindContentBidirectional(lstSlideQueue.getItems(), this.slides);
		this.slideList = lstSlideQueue;
		
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
								Slide s1 = (Slide)p.copy();
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

		StackPane stkSlideView = new StackPane(screen, slideView, notificationView);
		stkSlideView.setAlignment(Pos.TOP_LEFT);
		
		GridPane layout = new GridPane();
		layout.getStyleClass().add(DISPLAY_CONTROLLER_BTN_GRID_CSS);
		
		btnQueueAdd.setMaxWidth(Double.MAX_VALUE);
		btnQueueRemoveSelected.setMaxWidth(Double.MAX_VALUE);
		btnQueueRemoveAll.setMaxWidth(Double.MAX_VALUE);
		GridPane.setFillWidth(btnQueueAdd, true);
		
		int row = 0;
		HBox checkRow = new HBox(chkPreviewTransition, chkAutoShow);
		checkRow.getStyleClass().add(DISPLAY_CONTROLLER_CHK_ROW_CSS);
		
		layout.add(checkRow, 0, row, 3, 1);
		layout.add(btnShow, 3, row, 1, 2);
		layout.add(btnClear, 4, row, 1, 2);
		
		row++;
		layout.add(btnQueueAdd, 0, row, 2, 1);
		
		for (int i = 0; i < 5; i++) {
			ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(20);
			layout.getColumnConstraints().add(c1);
		}
		
		right.getChildren().addAll(stkSlideView, layout, tabs);
		
		VBox left = new VBox(lstSlideQueue, btnQueueRemoveSelected, btnQueueRemoveAll);
		left.getStyleClass().add(DISPLAY_CONTROLLER_LEFT_CSS);
		
		BorderPane body = new BorderPane();
		body.setCenter(right);
		body.setLeft(left);
		
		VBox.setVgrow(lstSlideQueue, Priority.ALWAYS);
		VBox.setVgrow(tabs, Priority.ALWAYS);
		
		// HEADER layout
		
		Label lblHeader = new Label();
		lblHeader.textProperty().bind(Bindings.createStringBinding(() -> {
			String name = configuration.getName();
			String defaultName = configuration.getDefaultName();
			if (name == null || name.isBlank()) {
				return defaultName;
			} else {
				return "# " + name;				
			}
		}, configuration.nameProperty(), configuration.defaultNameProperty()));
		lblHeader.getStyleClass().addAll(Styles.TITLE_3, DISPLAY_CONTROLLER_NAME_CSS);
		
		Label lblDefaultName = new Label();
		lblDefaultName.textProperty().bind(Bindings.createStringBinding(() -> {
			String name = configuration.getName();
			String defaultName = configuration.getDefaultName();
			if (name == null || name.isBlank()) {
				return "";
			}
			return defaultName;
		}, configuration.nameProperty(), configuration.defaultNameProperty()));
		
		final Button btn = new Button();
		btn.textProperty().bind(this.controllerDisplayName);
		btn.getStyleClass().add(TAG_CSS);
		btn.setDisable(true);
		btn.getStyleClass().addAll(Styles.ROUNDED, Styles.SMALL);
		btn.visibleProperty().bind(this.controllerDisplayName.isNotNull());
		btn.managedProperty().bind(btn.visibleProperty());
		
		HBox spacer = new HBox();
		spacer.setMaxWidth(Double.MAX_VALUE);
		HBox header = new HBox(lblHeader, lblDefaultName, btn, spacer, mnuActions);
		header.setAlignment(Pos.CENTER_LEFT);
		header.getStyleClass().add(DISPLAY_CONTROLLER_HEADER_CSS);
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		this.setTop(header);
		this.setCenter(body);

		configuration.activeProperty().addListener((obs, ov, nv) -> {
			if (!nv) {
				// clear the screen
				clearTarget();
			}
		});
		
		btnQueueAdd.setOnAction(e -> {
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
			} else if (index == 3) {
				// just ignore template tab
				return;
			} else {
				LOGGER.warn("Tab index '" + index + "' is not supported.");
			}
			
			if (slide != null) {
				slide = slide.copy();
				slide.fit(configuration.getWidth(), configuration.getHeight());
				slide.setPlaceholderData(data);
				this.slides.add(slide);
			}
		});
		
		btnQueueRemoveSelected.setOnAction(e -> {
			Slide selected = lstSlideQueue.getSelectionModel().getSelectedItem();
			int index = -1;
			for (int i = 0; i < this.slides.size(); i++) {
				Slide s2 = this.slides.get(i);
				if (selected == s2) {
					index = i;
					break;
				}
			}
			
			if (index >= 0) {
				LOGGER.debug("Found index {} to remove", index);
				this.slides.remove(index);
			}
		});
		
		btnQueueRemoveAll.setOnAction(e -> {
			this.slides.clear();
		});

		btnShow.setOnAction(e -> {
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
			} else if (index == 3) {
				return;
			} else {
				LOGGER.warn("Tab index '" + index + "' is not supported.");
			}
			
			displaySlideOnTarget(slide, data);
		});
		
		btnClear.setOnAction(e -> {
			displaySlideOnTarget(null, null);
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
				slide.fit(configuration.getWidth(), configuration.getHeight());

				notificationView.render(slide, slide.getPlaceholderData(), transition);
			} else {
				notificationView.render(null, null, false);
			}
		});
		
		btnShowNotification.setOnAction(e -> {
			Slide newSlide = cmbNotificationTemplate.getValue();
			String message = txtNotification.getText();
			if (newSlide != null && !StringManipulator.isNullOrEmpty(message)) {
				displayNotificationOnTarget(newSlide, new StringTextStore(message));
			}
		});
		
		btnClearNotification.setOnAction(e -> {
			displayNotificationOnTarget(null, null);
		});
		
		// disable some things when the user is on the notification tab
		btnShow.disableProperty().bind(tabs.getSelectionModel().selectedIndexProperty().isEqualTo(3));
		btnQueueAdd.disableProperty().bind(tabs.getSelectionModel().selectedIndexProperty().isEqualTo(3));
		chkAutoShow.disableProperty().bind(tabs.getSelectionModel().selectedIndexProperty().isEqualTo(3));
		chkPreviewTransition.disableProperty().bind(tabs.getSelectionModel().selectedIndexProperty().isEqualTo(3));
		
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
			boolean autoShow = chkAutoShow.isSelected();
			
			double tw = configuration.getWidth();
			double th = configuration.getHeight();
			
			if (!this.selectingQueuedSlide) {
				lstSlideQueue.getSelectionModel().clearSelection();
			}
			
			TextStore dataCopy = null;
			if (data != null) {
				dataCopy = data.copy();
			}
			
			Slide slideCopy = null;
			if (slide != null) {
				slideCopy = slide.copy();
				slideCopy.setPlaceholderData(dataCopy);
				slideCopy.fit(tw, th);
			}
			
			// update the slide view
			if (change == DisplayChange.HIDE || slide == null) {
				slideView.render(null, null, transition);
			} else { 
				slideView.render(slideCopy, dataCopy, transition);
			} 
			
			// update the display (if auto-show enabled)
			if (autoShow && change == DisplayChange.DATA) {
				displaySlideOnTarget(slide, data);
			}
		};
		
		cmbBibleSlideTemplate.valueProperty().addListener((obs, ov, nv) -> {
			if (tabs.getSelectionModel().getSelectedIndex() != 0) {
				return;
			}
			
			TextStore data = bibleNavigationPane.getValue();
			handleDisplayChange.accept(DisplayChange.STANDARD, nv, data);
			if (nv != null) {
				configuration.setBibleTemplateId(nv.getId());
			}
		});
		
		cmbSongSlideTemplate.valueProperty().addListener((obs, ov, nv) -> {
			if (tabs.getSelectionModel().getSelectedIndex() != 1) {
				return;
			}
			
			TextStore data = songNavigationPane.getValue();
			handleDisplayChange.accept(DisplayChange.STANDARD, nv, data);
			if (nv != null) {
				configuration.setSongTemplateId(nv.getId());
			}
		});
		
		cmbNotificationTemplate.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				configuration.setNotificationTemplateId(nv.getId());
			}
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
			} else if (index == 3) {
				// ignore notification
				// don't record we went here so that
				// we don't do a superfluous transition
				// when preview transition is enabled
				return;
			} else {
				LOGGER.warn("Tab index '" + index + "' is not supported.");
			}

			int lastTabIndex = this.lastTabIndex.get();
			this.lastTabIndex.set(index);

			// if we're coming from the notification tab, check what the
			// last "real" tab we were on was and compare it to where we're
			// going.  If they are the same, then there's no need to do a
			// display change because there's nothing the user can do to 
			// change what's there from the notification tab.  The user could
			// modify the slide template in the editor, but that should be
			// handled already
			if (ov.intValue() == 3 && lastTabIndex == nv.intValue()) {
				return;
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
		
		lstSlideQueue.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
			LOGGER.debug("{} was selected", nv);
			
			if (nv != null) {
				this.selectingQueuedSlide = true;
				// check for slide first
				if (!nv.hasPlaceholders()) {
					// then it's a slide
					tabs.getSelectionModel().select(2);
					Slide slide = context.getWorkspaceManager().getItem(Slide.class, nv.getId());
					if (slide != null) {
						slideNavigationPane.setValue(slide);
					}
					
					this.selectingQueuedSlide = false;
					return;
				}
				
				// otherwise it has placeholder data
				TextStore data = nv.getPlaceholderData();
				if (data instanceof BibleReferenceTextStore) {
					// set the bible fields
					BibleReferenceTextStore brts = (BibleReferenceTextStore)data;
					bibleNavigationPane.setValue(brts);
					// set the tab index
					tabs.getSelectionModel().select(0);
					// set the template selector
					Optional<Slide> template = cmbBibleSlideTemplate.getItems().stream().filter(t -> t.getId().equals(nv.getId())).findFirst();
					if (template.isPresent()) {
						cmbBibleSlideTemplate.setValue(template.get());
					}
				} else if (data instanceof SongReferenceTextStore) {
					// set the bible fields
					SongReferenceTextStore srts = (SongReferenceTextStore)data;
					songNavigationPane.setValue(srts);
					// set the tab index
					tabs.getSelectionModel().select(1);
					// set the template selector
					Optional<Slide> template = cmbSongSlideTemplate.getItems().stream().filter(t -> t.getId().equals(nv.getId())).findFirst();
					if (template.isPresent()) {
						cmbSongSlideTemplate.setValue(template.get());
					}
				}
				
				this.selectingQueuedSlide = false;
			}
		});
		
		UUID bibleTemplateId = configuration.getBibleTemplateId();
		if (bibleTemplateId != null) {
			for (Slide slide : cmbBibleSlideTemplate.getItems()) {
				if (slide.getId().equals(bibleTemplateId)) {
					cmbBibleSlideTemplate.setValue(slide);
					break;
				}
			}
		}
		
		UUID songTemplateId = configuration.getSongTemplateId();
		if (songTemplateId != null) {
			for (Slide slide : cmbSongSlideTemplate.getItems()) {
				if (slide.getId().equals(songTemplateId)) {
					cmbSongSlideTemplate.setValue(slide);
					break;
				}
			}
		}
		
		UUID notificationTemplateId = configuration.getNotificationTemplateId();
		if (notificationTemplateId != null) {
			for (Slide slide : cmbNotificationTemplate.getItems()) {
				if (slide.getId().equals(notificationTemplateId)) {
					cmbNotificationTemplate.setValue(slide);
					break;
				}
			}
		}
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
	
	private void displaySlideOnTarget(final Slide slide, final TextStore data) {
		this.target.displaySlide(slide, data);
		for (DisplayTarget target : this.controlledDisplays) {
			target.displaySlide(slide, data);
		}
	}
	
	@SuppressWarnings("unused")
	private void displaySlideOnTarget(final Slide slide, final TextStore data, boolean transition) {
		this.target.displaySlide(slide, data, transition);
		for (DisplayTarget target : this.controlledDisplays) {
			target.displaySlide(slide, data, transition);
		}
	}
	
	private void clearTarget() {
		this.target.clear();
		for (DisplayTarget target : this.controlledDisplays) {
			target.clear();
		}
	}
	
	@SuppressWarnings("unused")
	private void clearTarget(boolean transition) {
		this.target.clear(transition);
		for (DisplayTarget target : this.controlledDisplays) {
			target.clear(transition);
		}
	}
	
	private void displayNotificationOnTarget(final Slide slide, final TextStore data) {
		this.target.displayNotification(slide, data);
		for (DisplayTarget target : this.controlledDisplays) {
			target.displayNotification(slide, data);
		}
	}
	
	@SuppressWarnings("unused")
	private void displayNotificationOnTarget(final Slide slide, final TextStore data, boolean transition) {
		this.target.displayNotification(slide, data, transition);
		for (DisplayTarget target : this.controlledDisplays) {
			target.displayNotification(slide, data, transition);
		}
	}
	
	private DisplayTarget getDisplayTargetForId(Integer id) {
		var targets = new ArrayList<>(this.context.getDisplayManager().getDisplayTargets());
		for (var target : targets) {
			if (target.getDisplayConfiguration().getId() == id) {
				return target;
			}
		}
		return null;
	}
	
	@Override
	public CompletableFuture<Void> executeAction(Action action) {
		return this.slideList.executeAction(action);
	}
	
	@Override
	public ObservableList<?> getSelectedItems() {
		return this.slideList.getSelectedItems();
	}
	
	@Override
	public boolean isActionEnabled(Action action) {
		return this.slideList.isActionEnabled(action);
	}
	
	@Override
	public boolean isActionVisible(Action action) {
		return this.slideList.isActionVisible(action);
	}
	
	@Override
	public void setDefaultFocus() {
		this.slideList.setDefaultFocus();
	}
}
