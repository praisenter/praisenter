package org.praisenter.javafx;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.configuration.Display;
import org.praisenter.configuration.DisplayList;
import org.praisenter.configuration.DisplayRole;
import org.praisenter.configuration.Setting;
import org.praisenter.javafx.configuration.ObservableConfiguration;
import org.praisenter.javafx.controls.MessageLabel;
import org.praisenter.javafx.controls.SectionHeader;
import org.praisenter.javafx.controls.WellLabel;
import org.praisenter.javafx.display.DisplayView;
import org.praisenter.javafx.media.JavaFXMediaImportProcessor;
import org.praisenter.javafx.themes.Theme;
import org.praisenter.media.VideoMediaLoader;
import org.praisenter.ui.translations.Translations;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

// FEATURE (M-L) Add options to remove themes or translations
// FEATURE (L-M) Edit tags globally (like rename, remove)

public final class SetupPane extends BorderPane {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	private static final String ROOT = "root";
	private static final String GENERAL = "general";
	private static final String DISPLAYS = "displays";
	private static final String MEDIA = "media";
	
	// the layout
	
	private final TreeView<SetupTreeData> setupTree;
	
	// for identifying the displays
	
	/** The windows doing the identification */
	private List<Stage> identifiers = new ArrayList<Stage>();
	
	/** A timeline to play to make sure they close after x amount time */
	private Timeline identifierClose = null;
	
	public SetupPane(PraisenterContext context) {
		this.getStyleClass().add("setup-pane");
		
		TreeItem<SetupTreeData> root = new TreeItem<SetupTreeData>(new SetupTreeData(ROOT, "Preferences"));
		TreeItem<SetupTreeData> general = new TreeItem<SetupTreeData>(new SetupTreeData(GENERAL, "General"));
		TreeItem<SetupTreeData> displays = new TreeItem<SetupTreeData>(new SetupTreeData(DISPLAYS, "Displays"));
		TreeItem<SetupTreeData> media = new TreeItem<SetupTreeData>(new SetupTreeData(MEDIA, "Media"));
		root.getChildren().add(general);
		root.getChildren().add(displays);
		root.getChildren().add(media);
		
		this.setupTree = new TreeView<SetupTreeData>(root);
		this.setupTree.setShowRoot(false);
		this.setupTree.getSelectionModel().select(general);
		this.setupTree.setMaxHeight(Double.MAX_VALUE);
		this.setupTree.getStyleClass().add("setup-pane-list");
		
		// GENERAL

		List<Option<Locale>> locales = new ArrayList<Option<Locale>>();
		for (Locale locale : Translations.getAvailableLocales()) {
			locales.add(new Option<Locale>(locale.getDisplayName(), locale));
		}
		
		Locale locale = context.getConfiguration().getLanguage();
		if (locale == null) {
			locale = Locale.getDefault();
		}
		
		Theme theme = context.getConfiguration().getTheme();
		if (theme == null) {
			theme = Theme.DEFAULT;
		}
		
		GridPane gridLangTheme = new GridPane();
		gridLangTheme.setHgap(5);
		gridLangTheme.setVgap(5);
		
		// language
		Label lblLocale = new Label("Language");
		ComboBox<Option<Locale>> cmbLocale = new ComboBox<Option<Locale>>(FXCollections.observableArrayList(locales));
		cmbLocale.setValue(new Option<Locale>(null, locale));
		Button btnRefreshLocales = new Button("", ApplicationGlyphs.REFRESH.duplicate());
		Button btnDownloadLocale = new Button("", ApplicationGlyphs.EXPORT.duplicate());
		Button btnUploadLocale = new Button("", ApplicationGlyphs.IMPORT.duplicate());
		gridLangTheme.add(lblLocale, 0, 0);
		gridLangTheme.add(cmbLocale, 1, 0);
		gridLangTheme.add(btnRefreshLocales, 2, 0);
		gridLangTheme.add(btnDownloadLocale, 3, 0);
		gridLangTheme.add(btnUploadLocale, 4, 0);
		cmbLocale.setMaxWidth(Double.MAX_VALUE);
		GridPane.setFillWidth(cmbLocale, true);
		
		// theme
		Label lblTheme = new Label("Theme");
		ComboBox<Theme> cmbTheme = new ComboBox<Theme>(FXCollections.observableArrayList(Theme.getAvailableThemes()));
		cmbTheme.setValue(theme);
		Button btnRefreshThemes = new Button("", ApplicationGlyphs.REFRESH.duplicate());
		Button btnDownloadTheme = new Button("", ApplicationGlyphs.EXPORT.duplicate());
		Button btnUploadTheme = new Button("", ApplicationGlyphs.IMPORT.duplicate());
		gridLangTheme.add(lblTheme, 0, 1);
		gridLangTheme.add(cmbTheme, 1, 1);
		gridLangTheme.add(btnRefreshThemes, 2, 1);
		gridLangTheme.add(btnDownloadTheme, 3, 1);
		gridLangTheme.add(btnUploadTheme, 4, 1);
		cmbTheme.setMaxWidth(Double.MAX_VALUE);
		GridPane.setFillWidth(cmbTheme, true);

		GridPane gridOther = new GridPane();
		gridOther.setHgap(5);
		gridOther.setVgap(5);
		
		// wait for transitions
		Label lblWaitTransitions = new Label("Wait for transitions to complete");
		CheckBox chkWaitTransitions = new CheckBox();
		chkWaitTransitions.setSelected(context.getConfiguration().getBoolean(Setting.PRESENT_WAIT_FOR_TRANSITIONS_TO_COMPLETE, false));
		gridOther.add(lblWaitTransitions, 0, 0);
		gridOther.add(chkWaitTransitions, 1, 0);
		
		// debug mode
		Label lblDebugMode = new Label("Debug Mode");
		CheckBox chkDebugMode = new CheckBox();
		chkDebugMode.setSelected(context.getConfiguration().getBoolean(Setting.APP_DEBUG_MODE, false));
		gridOther.add(lblDebugMode, 0, 1);
		gridOther.add(chkDebugMode, 1, 1);
		
		VBox vboxGeneral = new VBox(
				new SectionHeader("Language & Theme"),
				new MessageLabel("Changing the Theme or Language requires the application to be restarted to take effect.", AlertType.INFORMATION, false),
				gridLangTheme,
				new SectionHeader("Other"),
				gridOther);
		vboxGeneral.setPadding(new Insets(10));
		vboxGeneral.setSpacing(10);
		
		// MEDIA
		
		GridPane gridMedia = new GridPane();
		gridMedia.setHgap(5);
		gridMedia.setVgap(7);

		// transcoding
		Label lblTranscodeAudioVideo = new Label("Transcode");
		CheckBox chkTranscodeAudioVideo = new CheckBox();
		chkTranscodeAudioVideo.setSelected(context.getConfiguration().getBoolean(Setting.MEDIA_TRANSCODING_ENABLED, true));
		gridMedia.add(lblTranscodeAudioVideo, 0, 0);
		gridMedia.add(chkTranscodeAudioVideo, 1, 0);
		
		// transcoding video
		Label lblVideo = new Label("Video");
		TextField txtVideoExtension = new TextField(context.getConfiguration().getString(Setting.MEDIA_TRANSCODING_VIDEO_EXTENSION, JavaFXMediaImportProcessor.DEFAULT_VIDEO_EXTENSION));
		TextField txtVideoCommand = new TextField(context.getConfiguration().getString(Setting.MEDIA_TRANSCODING_VIDEO_COMMAND, JavaFXMediaImportProcessor.DEFAULT_TRANSCODE_COMMAND));
		txtVideoExtension.setPrefWidth(75);
		txtVideoCommand.setPrefWidth(600);
		gridMedia.add(lblVideo, 0, 1);
		gridMedia.add(txtVideoExtension, 1, 1);
		gridMedia.add(txtVideoCommand, 2, 1);

		// transcoding audio
		Label lblAudio = new Label("Audio");
		TextField txtAudioExtension = new TextField(context.getConfiguration().getString(Setting.MEDIA_TRANSCODING_AUDIO_EXTENSION, JavaFXMediaImportProcessor.DEFAULT_AUDIO_EXTENSION));
		TextField txtAudioCommand = new TextField(context.getConfiguration().getString(Setting.MEDIA_TRANSCODING_AUDIO_COMMAND, JavaFXMediaImportProcessor.DEFAULT_TRANSCODE_COMMAND));
		txtAudioExtension.setPrefWidth(75);
		txtAudioCommand.setPrefWidth(600);
		gridMedia.add(lblAudio, 0, 2);
		gridMedia.add(txtAudioExtension, 1, 2);
		gridMedia.add(txtAudioCommand, 2, 2);
		
		GridPane gridFrameExtraction = new GridPane();
		gridFrameExtraction.setHgap(5);
		gridFrameExtraction.setVgap(7);
		
		Label lblFrameExtraction = new Label("Command");
		TextField txtFrameExtraction = new TextField(context.getConfiguration().getString(Setting.MEDIA_VIDEO_FRAME_EXTRACT_COMMAND, VideoMediaLoader.DEFAULT_VIDEO_FRAME_EXTRACT_COMMAND));
		txtFrameExtraction.setPrefWidth(600);
		gridFrameExtraction.add(lblFrameExtraction, 0, 0);
		gridFrameExtraction.add(txtFrameExtraction, 1, 0);
		
		VBox vboxMedia = new VBox(
				new SectionHeader("Transcoding", "Audio and video media is transcoded into a common format that is supported by Praisenter. Transcoding is the process of converting one media file format to another. You can turn off transcoding if your media is already in the set of supported media formats."),
				new MessageLabel("The commands below are used to perform transcoding audio and video media. The {ffmpeg}, {source}, and {target} tokens should not be changed, but all other command text can. The command should be a valid FFmpeg command line command.", AlertType.INFORMATION, true),
				gridMedia,
				new SectionHeader("Video Thumbnails", "This controls the extraction of a frames from video media. The frames are used to generate thumbnails and during editing of slides. The frame selected is determined by the frame's Log Average Luminance that's closest to 0.5."),
				new MessageLabel("The command below is used to extract individual frames from a video. The {ffmpeg}, {media}, and {frame} tokens should not be changed, but all other command text can. The command should be a valid FFmpeg command line command.", AlertType.INFORMATION, true),
				gridFrameExtraction);
		vboxMedia.setPadding(new Insets(10));
		vboxMedia.setSpacing(10);
		
		// SCREENS
		
		Button btnIdentify = new Button("Identify Displays");
		
		TilePane screenPane = new TilePane(Orientation.HORIZONTAL);
		screenPane.setHgap(5);
		screenPane.setVgap(5);
		
		VBox vboxScreens = new VBox(
				new SectionHeader("Display Setup", "Praisenter allows the use of multiple displays. Each display can be assigned a role and name. The role defines the type of content that will show there, while the name help you know which screen to send content to."), 
				new WellLabel("The display assignment will be automatically set when Praisenter opens for the first time. Afterwhich the screens are detected to for changes continuously. You will be notified if the screens change in such a way that you need to reassign their role/name."), 
				new MessageLabel("Clicking this button will show a number on each screen.", AlertType.WARNING, false),
				btnIdentify, 
				new MessageLabel("Changing the screen assignment will close any currently displayed slides or notifications.", AlertType.WARNING, false), 
				screenPane);
		vboxScreens.setPadding(new Insets(10));
		vboxScreens.setSpacing(10);

		// LAYOUT
		
		BorderPane right = new BorderPane();
		right.setCenter(vboxGeneral);
		
		SplitPane split = new SplitPane(this.setupTree, right);
		split.setOrientation(Orientation.HORIZONTAL);
		split.setDividerPositions(0.25);
		SplitPane.setResizableWithParent(this.setupTree, false);
		
		this.setCenter(split);
		
		// EVENTS

		cmbLocale.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				context.getConfiguration()
					.setString(Setting.APP_LANGUAGE, nv.value.toLanguageTag())
					.execute(context.getExecutorService());
			}
		});
		
		btnRefreshLocales.setOnAction(e -> {
			cmbLocale.setItems(refreshLocales());
		});
		
		btnDownloadLocale.setOnAction(e -> {
			Path path = null;
	    	Option<Locale> selected = cmbLocale.getValue();
	    	if (selected != null) {
	    		Locale loc = selected.getValue();
	    		if (!loc.equals(Locale.ENGLISH)) {
		    		String tag = loc.toLanguageTag();
		    		path = Paths.get(Constants.LOCALES_ABSOLUTE_FILE_PATH).resolve("messages_" + tag + ".properties");
	    		}
	    	}
			
			FileChooser chooser = new FileChooser();
	    	chooser.setInitialFileName(path != null ? path.getFileName().toString() : "messages.properties");
	    	chooser.setTitle("Export Translation");
	    	chooser.getExtensionFilters().add(new ExtensionFilter("Java Translations", "*.properties"));
	    	File file = chooser.showSaveDialog(getScene().getWindow());
	    	
	    	if (file != null) {
		    	if (path != null) {
			    	try {
			    		Files.copy(path, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			    	} catch (Exception ex) {
			    		LOGGER.error("Failed to export translation", ex);
			    	}
		    	} else {
		    		try (InputStream def = SetupPane.class.getResourceAsStream("/org/praisenter/resources/translations/messages.properties")) {
		    			Files.copy(def, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		    		} catch (Exception ex) {
		    			LOGGER.error("Failed to export default translation", ex);
		    		}
		    	}
	    	}
		});
		
		btnUploadLocale.setOnAction(e -> {
			FileChooser chooser = new FileChooser();
	    	chooser.setTitle("Import Translation");
	    	chooser.getExtensionFilters().add(new ExtensionFilter("Java Translations", "*.properties"));
	    	File file = chooser.showOpenDialog(getScene().getWindow());
	    	if (file != null) {
	    		try {
	    			Path path = Paths.get(Constants.LOCALES_ABSOLUTE_FILE_PATH).resolve(file.getName());
		    		Files.copy(file.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
		    	} catch (Exception ex) {
		    		LOGGER.error("Failed to import translation", ex);
		    	}
	    		cmbLocale.setItems(refreshLocales());
	    	}
		});
		
		cmbTheme.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				context.getConfiguration()
					.setString(Setting.APP_THEME, nv.getName())
					.execute(context.getExecutorService());
			}
		});
		
		btnRefreshThemes.setOnAction(e -> {
			cmbTheme.setItems(FXCollections.observableArrayList(Theme.getAvailableThemes()));
		});
		
		btnDownloadTheme.setOnAction(e -> {
			Path path = null;
	    	Theme selected = cmbTheme.getValue();
	    	if (selected != null && !selected.equals(Theme.DEFAULT) && !selected.equals(Theme.DARK)) {
	    		path = Paths.get(Constants.THEMES_ABSOLUTE_FILE_PATH).resolve(selected.getName() + ".css");
	    	}
			
			FileChooser chooser = new FileChooser();
	    	chooser.setInitialFileName(path != null ? path.getFileName().toString() : "default.css");
	    	chooser.setTitle("Export Theme");
	    	chooser.getExtensionFilters().add(new ExtensionFilter("Cascading Style Sheet", "*.css"));
	    	File file = chooser.showSaveDialog(getScene().getWindow());
	    	
	    	if (path != null) {
		    	try {
		    		Files.copy(path, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		    	} catch (Exception ex) {
		    		LOGGER.error("Failed to export theme", ex);
		    	}
	    	} else {
	    		try (InputStream def = SetupPane.class.getResourceAsStream("/org/praisenter/javafx/themes/" + selected.getName() + ".css")) {
	    			Files.copy(def, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	    		} catch (Exception ex) {
	    			LOGGER.error("Failed to export default theme", ex);
	    		}
	    	}
		});
		
		btnUploadTheme.setOnAction(e -> {
			FileChooser chooser = new FileChooser();
	    	chooser.setTitle("Import Theme");
	    	chooser.getExtensionFilters().add(new ExtensionFilter("Cascading Style Sheet", "*.css"));
	    	File file = chooser.showOpenDialog(getScene().getWindow());
	    	if (file != null) {
	    		try {
	    			Path path = Paths.get(Constants.THEMES_ABSOLUTE_FILE_PATH).resolve(file.getName());
		    		Files.copy(file.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
		    	} catch (Exception ex) {
		    		LOGGER.error("Failed to import theme", ex);
		    	}
	    		cmbTheme.setItems(FXCollections.observableArrayList(Theme.getAvailableThemes()));
	    	}
		});
		
		chkDebugMode.selectedProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				context.getConfiguration()
					.setBoolean(Setting.APP_DEBUG_MODE, true)
					.execute(context.getExecutorService());
			} else {
				context.getConfiguration()
					.remove(Setting.APP_DEBUG_MODE)
					.execute(context.getExecutorService());
			}
		});
		
		chkWaitTransitions.selectedProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				context.getConfiguration()
					.setBoolean(Setting.PRESENT_WAIT_FOR_TRANSITIONS_TO_COMPLETE, true)
					.execute(context.getExecutorService());
			} else {
				context.getConfiguration()
					.remove(Setting.PRESENT_WAIT_FOR_TRANSITIONS_TO_COMPLETE)
					.execute(context.getExecutorService());
			}
		});
		
		btnIdentify.setOnAction(e -> {
			// show a window for each screen with a number on it
			List<Screen> screens = Screen.getScreens();
			
			if (identifierClose != null) {
				identifierClose.stop();
			}
			for (Stage stage : this.identifiers) {
				stage.close();
			}
			identifiers.clear();
			identifierClose = new Timeline(new KeyFrame(Duration.seconds(3), 
					ae -> {
						for (Stage stage : this.identifiers) {
							stage.close();
						}
					}));
			
			int i = 1;
			for (Screen screen : screens) {
				Stage stage = new Stage(StageStyle.TRANSPARENT);
				stage.initOwner(getScene().getWindow());
				stage.initModality(Modality.NONE);
				stage.setTitle("IDENTIFY-" + i);
				stage.setAlwaysOnTop(true);
				stage.setResizable(false);
				// position and size
				Rectangle2D bounds = screen.getBounds();
				stage.setX(bounds.getMinX());
				stage.setY(bounds.getMinY());
				stage.setWidth(bounds.getWidth());
				stage.setHeight(bounds.getHeight());
				// content
				Pane container = new Pane();
				container.setBackground(null);
				StackPane block = new StackPane();
				block.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
				block.setPadding(new Insets(50));
				block.setTranslateX(50);
				block.setTranslateY(50);
				Text text = new Text(String.valueOf(i));
				text.setFill(Color.WHITE);
				text.setFont(Font.font(text.getFont().getName(), 80));
				block.getChildren().add(text);
				container.getChildren().add(block);
				
				stage.setScene(new Scene(container, null));
				identifiers.add(stage);
				
				stage.show();
				i++;
			}
			
			identifierClose.play();
		});
		
		chkTranscodeAudioVideo.selectedProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				context.getConfiguration()
					.setBoolean(Setting.MEDIA_TRANSCODING_ENABLED, true)
					.execute(context.getExecutorService());
			} else {
				context.getConfiguration()
					.remove(Setting.MEDIA_TRANSCODING_ENABLED)
					.execute(context.getExecutorService());
			}
		});
		
		txtAudioExtension.textProperty().addListener((obs, ov, nv) -> {
			context.getConfiguration().setString(Setting.MEDIA_TRANSCODING_AUDIO_EXTENSION, nv)
				.execute(context.getExecutorService());
		});
		
		txtAudioCommand.textProperty().addListener((obs, ov, nv) -> {
			context.getConfiguration().setString(Setting.MEDIA_TRANSCODING_AUDIO_COMMAND, nv)
				.execute(context.getExecutorService());
		});
		
		txtVideoExtension.textProperty().addListener((obs, ov, nv) -> {
			context.getConfiguration().setString(Setting.MEDIA_TRANSCODING_VIDEO_EXTENSION, nv)
				.execute(context.getExecutorService());
		});
		
		txtVideoCommand.textProperty().addListener((obs, ov, nv) -> {
			context.getConfiguration().setString(Setting.MEDIA_TRANSCODING_VIDEO_COMMAND, nv)
				.execute(context.getExecutorService());
		});
		
		txtFrameExtraction.textProperty().addListener((obs, ov, nv) -> {
			context.getConfiguration().setString(Setting.MEDIA_VIDEO_FRAME_EXTRACT_COMMAND, nv)
				.execute(context.getExecutorService());
		});
		
		// listener for updating the screen views when the 
		// screens change or when the parent node changes
		InvalidationListener screenListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				ObservableConfiguration conf = context.getConfiguration();
				screenPane.getChildren().clear();
				
				// snapshot at this moment
				List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
				int n = screens.size();
				
				// get the configuration
				List<Display> assignments = conf.getDisplays();
				
				// setup displays
				Map<Integer, DisplayView> views = new HashMap<Integer, DisplayView>();
				for (int i = 0; i < assignments.size(); i++) {
					Display display = assignments.get(i);
					if (!views.containsKey(display.getId()) && display.getId() < n) {
						views.put(display.getId(), new DisplayView(display));
					}
				}
				
				// fill in gaps
				for (int i = 0; i < n; i++) {
					if (!views.containsKey(i)) {
						Rectangle2D bounds = screens.get(i).getBounds();
						views.put(i, new DisplayView(new Display(i, DisplayRole.NONE, "Unassigned", (int)bounds.getMinX(), (int)bounds.getMinY(), (int)bounds.getWidth(), (int)bounds.getHeight())));
					}
				}
				
				for (DisplayView view : views.values()) {
					view.displayProperty().addListener((obs, ov, nv) -> {
						DisplayList displays = context.getConfiguration().getObject(Setting.DISPLAY_ASSIGNMENTS, DisplayList.class, new DisplayList());
						
						if (ov != null) {
							displays.remove(ov);
						}
						
						if (nv != null) {
							// add it
							displays.add(nv);
							// save it
							context.getConfiguration().setObject(Setting.DISPLAY_ASSIGNMENTS, displays)
								.execute(context.getExecutorService());
						}
					});
				}
				
				int i = 0;
				for (DisplayView view : views.values()) {
					screenPane.getChildren().add(view);
					i++;
					if (i % 3 == 0) {
						i = 0;
					}
				}
			}
		};

		this.setupTree.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				switch (nv.getValue().getName()) {
				case GENERAL:
					right.setCenter(vboxGeneral);
					break;
				case DISPLAYS:
					right.setCenter(vboxScreens);
					screenListener.invalidated(obs);
					break;
				case MEDIA:
					right.setCenter(vboxMedia);
					break;
				default:
					break;
				}
			}
		});
		
		// only attach the screen listener when this pane is attached to the scene
		parentProperty().addListener((obs, ov, nv) -> {
			if (nv == null) {
				Screen.getScreens().removeListener(screenListener);
			} else if (nv != null) {
				Screen.getScreens().addListener(screenListener);
			}
		});
	}
	
	private ObservableList<Option<Locale>> refreshLocales() {
		List<Option<Locale>> locs = new ArrayList<Option<Locale>>();
		for (Locale loc : Translations.getAvailableLocales()) {
			locs.add(new Option<Locale>(loc.getDisplayName(), loc));
		}
		return FXCollections.observableArrayList(locs);
	}
}
