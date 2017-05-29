package org.praisenter.javafx;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.configuration.Display;
import org.praisenter.javafx.configuration.ObservableConfiguration;
import org.praisenter.javafx.configuration.Setting;
import org.praisenter.javafx.configuration.SettingBatch;
import org.praisenter.javafx.media.JavaFXMediaImportFilter;
import org.praisenter.javafx.screen.ScreenView;
import org.praisenter.javafx.screen.ScreenViewDragDropManager;
import org.praisenter.javafx.themes.Styles;
import org.praisenter.javafx.themes.Theme;
import org.praisenter.resources.translations.Translations;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;

// TODO translate
// FEATURE option to export theme and translation to create new ones

public final class SetupPane extends BorderPane {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	public SetupPane(PraisenterContext context) {
		this.getStyleClass().add(Styles.SETUP_PANE);
		
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
		
		// GENERAL
		
		GridPane gridGeneral = new GridPane();
		gridGeneral.setHgap(5);
		gridGeneral.setVgap(5);
		
		// language
		Label lblLocale = new Label("Language");
		ComboBox<Option<Locale>> cmbLocale = new ComboBox<Option<Locale>>(FXCollections.observableArrayList(locales));
		cmbLocale.setValue(new Option<Locale>(null, locale));
		Button btnRefreshLocales = new Button("", ApplicationGlyphs.REFRESH.duplicate());
		gridGeneral.add(lblLocale, 0, 0);
		gridGeneral.add(cmbLocale, 1, 0);
		gridGeneral.add(btnRefreshLocales, 2, 0);
		cmbLocale.setMaxWidth(Double.MAX_VALUE);
		GridPane.setFillWidth(cmbLocale, true);
		
		// theme
		Label lblTheme = new Label("Theme");
		ComboBox<Theme> cmbTheme = new ComboBox<Theme>(FXCollections.observableArrayList(Theme.getAvailableThemes()));
		cmbTheme.setValue(theme);
		Button btnRefreshThemes = new Button("", ApplicationGlyphs.REFRESH.duplicate());
		gridGeneral.add(lblTheme, 0, 1);
		gridGeneral.add(cmbTheme, 1, 1);
		gridGeneral.add(btnRefreshThemes, 2, 1);
		cmbTheme.setMaxWidth(Double.MAX_VALUE);
		GridPane.setFillWidth(cmbTheme, true);
		
		// debug mode
		Label lblDebugMode = new Label("Debug Mode");
		CheckBox chkDebugMode = new CheckBox();
		chkDebugMode.setSelected(context.getConfiguration().getBoolean(Setting.APP_DEBUG_MODE, false));
		gridGeneral.add(lblDebugMode, 0, 2);
		gridGeneral.add(chkDebugMode, 1, 2);

		Label lblRestartWarning = new Label("Changing the Theme, Language, or Debug Mode requires the application to be restarted to take effect.", ApplicationGlyphs.INFO.duplicate());
		lblRestartWarning.setPadding(new Insets(0, 0, 5, 0));
		
		VBox vboxGeneral = new VBox(lblRestartWarning, gridGeneral);
		
		// MEDIA
		
		GridPane gridMedia = new GridPane();
		gridMedia.setHgap(5);
		gridMedia.setVgap(5);

		// transcoding
		Label lblTranscodeAudioVideo = new Label("Transcode");
		CheckBox chkTranscodeAudioVideo = new CheckBox();
		chkTranscodeAudioVideo.setSelected(context.getConfiguration().getBoolean(Setting.MEDIA_TRANSCODING_ENABLED, true));
		gridMedia.add(lblTranscodeAudioVideo, 0, 0);
		gridMedia.add(chkTranscodeAudioVideo, 1, 0);
		
		// transcoding video
		Label lblVideo = new Label("Video");
		TextField txtVideoExtension = new TextField(context.getConfiguration().getString(Setting.MEDIA_TRANSCODING_VIDEO_EXTENSION, JavaFXMediaImportFilter.DEFAULT_VIDEO_EXTENSION));
		TextField txtVideoCommand = new TextField(context.getConfiguration().getString(Setting.MEDIA_TRANSCODING_VIDEO_COMMAND, JavaFXMediaImportFilter.DEFAULT_COMMAND));
		txtVideoExtension.setPrefWidth(75);
		txtVideoCommand.setPrefWidth(600);
		gridMedia.add(lblVideo, 0, 1);
		gridMedia.add(txtVideoExtension, 1, 1);
		gridMedia.add(txtVideoCommand, 2, 1);

		// transcoding audio
		Label lblAudio = new Label("Audio");
		TextField txtAudioExtension = new TextField(context.getConfiguration().getString(Setting.MEDIA_TRANSCODING_AUDIO_EXTENSION, JavaFXMediaImportFilter.DEFAULT_AUDIO_EXTENSION));
		TextField txtAudioCommand = new TextField(context.getConfiguration().getString(Setting.MEDIA_TRANSCODING_AUDIO_COMMAND, JavaFXMediaImportFilter.DEFAULT_COMMAND));
		txtAudioExtension.setPrefWidth(75);
		txtAudioCommand.setPrefWidth(600);
		gridMedia.add(lblAudio, 0, 2);
		gridMedia.add(txtAudioExtension, 1, 2);
		gridMedia.add(txtAudioCommand, 2, 2);
		
		VBox vboxMedia = new VBox(gridMedia);
		
		// SCREENS
		
		Label lblScreenWarning = new Label("Changing the screen assignment will close any currently displayed slides or notifications.", ApplicationGlyphs.WARN.duplicate());
		Label lblScreenHowTo = new Label("Drag and drop the screen to assign its role.");
		lblScreenHowTo.setPadding(new Insets(0, 0, 10, 0));
		lblScreenWarning.setPadding(new Insets(0, 0, 10, 0));
		
		// get a screenshot of all the screens
		GridPane screenPane = new GridPane();
		screenPane.setHgap(10);
		screenPane.setVgap(10);
		
		Label lblOperatorScreen = new Label("Operator");
		Label lblPrimaryScreen = new Label("Primary");
		Label lblMusicianScreen = new Label("Musician");
		lblOperatorScreen.setFont(Font.font("System", FontWeight.BOLD, 15));
		lblPrimaryScreen.setFont(Font.font("System", FontWeight.BOLD, 15));
		lblMusicianScreen.setFont(Font.font("System", FontWeight.BOLD, 15));
		
		Label lblOperatorDescription = new Label("This is the screen that you will be operating from with the Praisenter application and any other tools. Typically your default desktop.");
		Label lblPrimaryDescription = new Label("This is the screen that will show the presentations and notifications.");
		Label lblMusicianDescription = new Label("This is the screen that will show specialized musician or song related information (optional).");
		
		lblOperatorDescription.setWrapText(true);
		lblOperatorDescription.setTextAlignment(TextAlignment.CENTER);
		GridPane.setHgrow(lblOperatorDescription, Priority.NEVER);
		GridPane.setValignment(lblOperatorDescription, VPos.TOP);
		
		lblPrimaryDescription.setWrapText(true);
		lblPrimaryDescription.setTextAlignment(TextAlignment.CENTER);
		GridPane.setHgrow(lblPrimaryDescription, Priority.NEVER);
		GridPane.setValignment(lblPrimaryDescription, VPos.TOP);

		lblMusicianDescription.setWrapText(true);
		lblMusicianDescription.setTextAlignment(TextAlignment.CENTER);
		GridPane.setHgrow(lblMusicianDescription, Priority.NEVER);
		GridPane.setValignment(lblMusicianDescription, VPos.TOP);
		
		screenPane.add(lblOperatorScreen, 0, 1);
		screenPane.add(lblPrimaryScreen, 1, 1);
		screenPane.add(lblMusicianScreen, 2, 1);
		screenPane.add(lblOperatorDescription, 0, 2);
		screenPane.add(lblPrimaryDescription, 1, 2);
		screenPane.add(lblMusicianDescription, 2, 2);
		
		GridPane.setHalignment(lblOperatorScreen, HPos.CENTER);
		GridPane.setHalignment(lblPrimaryScreen, HPos.CENTER);
		GridPane.setHalignment(lblMusicianScreen, HPos.CENTER);
		
		VBox vboxScreens = new VBox(lblScreenHowTo, lblScreenWarning, screenPane);

		// LAYOUT
		
		TitledPane ttlGeneral = new TitledPane("General", vboxGeneral);
		TitledPane ttlMedia = new TitledPane("Media", vboxMedia);
		TitledPane ttlScreens = new TitledPane("Screens", vboxScreens);
		
		ttlGeneral.setCollapsible(false);
		ttlMedia.setCollapsible(false);
		ttlScreens.setCollapsible(false);
		
		VBox layout = new VBox(ttlGeneral, ttlScreens, ttlMedia);
		ScrollPane scroller = new ScrollPane(layout);
		scroller.setFitToWidth(true);
		
		this.setCenter(scroller);
		
		// EVENTS

		cmbLocale.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				context.getConfiguration()
					.setString(Setting.APP_LANGUAGE, nv.value.toLanguageTag())
					.execute(context.getExecutorService());
			}
		});
		
		btnRefreshLocales.setOnAction(e -> {
			List<Option<Locale>> locs = new ArrayList<Option<Locale>>();
			for (Locale loc : Translations.getAvailableLocales()) {
				locs.add(new Option<Locale>(loc.getDisplayName(), loc));
			}
			cmbLocale.setItems(FXCollections.observableArrayList(locs));
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

		// create a custom manager
		ScreenViewDragDropManager manager = new ScreenViewDragDropManager() {
			@Override
			public void swap(ScreenView view1, ScreenView view2) {
				int col1 = GridPane.getColumnIndex(view1);
				int row1 = GridPane.getRowIndex(view1);
				int col2 = GridPane.getColumnIndex(view2);
				int row2 = GridPane.getRowIndex(view2);
				screenPane.getChildren().removeAll(view1, view2);
				screenPane.add(view2, col1, row1);
				screenPane.add(view1, col2, row2);
				
				// record the changes (will save automatically)
				ObservableConfiguration conf = context.getConfiguration();
				SettingBatch<AsyncTask<Void>> batch = conf.createBatch();
				if (col1 == 0) {
					batch.setObject(Setting.DISPLAY_OPERATOR, view2.getDisplay());
					lblOperatorDescription.setPrefWidth(view2.getPrefWidth());
				} else if (col1 == 1) {
					batch.setObject(Setting.DISPLAY_MAIN, view2.getDisplay());
					lblPrimaryDescription.setPrefWidth(view2.getPrefWidth());
				} else if (col1 == 2) {
					batch.setObject(Setting.DISPLAY_MUSICIAN, view2.getDisplay());
					lblMusicianDescription.setPrefWidth(view2.getPrefWidth());
				}
				if (col2 == 0) {
					batch.setObject(Setting.DISPLAY_OPERATOR, view1.getDisplay());
					lblOperatorDescription.setPrefWidth(view1.getPrefWidth());
				} else if (col2 == 1) {
					batch.setObject(Setting.DISPLAY_MAIN, view1.getDisplay());
					lblPrimaryDescription.setPrefWidth(view1.getPrefWidth());
				} else if (col2 == 2) {
					batch.setObject(Setting.DISPLAY_MUSICIAN, view1.getDisplay());
					lblMusicianDescription.setPrefWidth(view1.getPrefWidth());
				}
				
				batch.commitBatch()
					.execute(context.getExecutorService());
			}
		};
		
		// listener for updating the screen views when the 
		// screens change or when the parent node changes
		InvalidationListener screenListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				ObservableConfiguration conf = context.getConfiguration();
				
				Display os = conf.getObject(Setting.DISPLAY_OPERATOR, Display.class, null);
				Display ms = conf.getObject(Setting.DISPLAY_MAIN, Display.class, null);
				Display cs = conf.getObject(Setting.DISPLAY_MUSICIAN, Display.class, null);
				
				Map<Integer, ScreenView> views = ScreenView.createScreenViews(manager);
				
				ScreenView operator = null;
				ScreenView main = null;
				ScreenView musician = null;
				if (os != null) {
					operator = views.remove(os.getId());
				}
				if (ms != null) {
					main = views.remove(ms.getId());
				}
				if (cs != null) {
					musician = views.remove(cs.getId());
				}
				
				if (operator == null) {
					operator = ScreenView.createUnassignedScreenView(manager);
				}
				if (main == null) {
					main = ScreenView.createUnassignedScreenView(manager);
				}
				if (musician == null) {
					musician = ScreenView.createUnassignedScreenView(manager);
				}
				
				screenPane.add(operator, 0, 0);
				screenPane.add(main, 1, 0);
				screenPane.add(musician, 2, 0);
				
				GridPane.setHalignment(operator, HPos.CENTER);
				GridPane.setHalignment(main, HPos.CENTER);
				GridPane.setHalignment(musician, HPos.CENTER);
				
				lblOperatorDescription.setPrefWidth(operator.getPrefWidth());
				lblPrimaryDescription.setPrefWidth(main.getPrefWidth());
				lblMusicianDescription.setPrefWidth(musician.getPrefWidth());
				
				int i = 0;
				int j = 3;
				for (ScreenView view : views.values()) {
					Label lblUnused = new Label("Unassigned");
					lblUnused.setFont(Font.font("System", FontWeight.BOLD, 15));
					screenPane.add(view, i, j);
					screenPane.add(lblUnused, i, j + 1);
					GridPane.setHalignment(lblUnused, HPos.CENTER);
					i++;
					if (i % 3 == 0) {
						i = 0;
						j += 2;
					}
				}
			}
		};
		
		// update when the parent changes
		parentProperty().addListener(screenListener);
		
		// update when the screens change
		Screen.getScreens().addListener(screenListener);
		
	}
}
