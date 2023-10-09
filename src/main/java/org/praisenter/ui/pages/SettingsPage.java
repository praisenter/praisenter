package org.praisenter.ui.pages;

import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.workspace.PlaceholderTransitionBehavior;
import org.praisenter.data.workspace.WorkspaceConfiguration;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Option;
import org.praisenter.ui.controls.FastScrollPane;
import org.praisenter.ui.controls.LastValueNumberStringConverter;
import org.praisenter.ui.themes.StyleSheets;
import org.praisenter.ui.themes.ThemeListCell;
import org.praisenter.ui.themes.Themes;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.StringManipulator;

import atlantafx.base.controls.Tile;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Theme;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.Stylesheet;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SettingsPage extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String SETTINGS_PAGE_CLASS = "p-settings-page";
	private static final String SETTINGS_PAGE_SCROLL_CLASS = "p-settings-page-scroll";
	private static final String SETTINGS_PAGE_GROUP_CLASS = "p-settings-page-group";
	private static final String SETTINGS_PAGE_FLOW_CLASS = "p-settings-page-flow";
	
	private final ObservableList<Theme> themes = FXCollections.observableArrayList(Themes.THEMES);
	private final ObservableList<Locale> locales;
	
	public SettingsPage(GlobalContext context) {
		WorkspaceConfiguration configuration = context.getWorkspaceConfiguration();
		
		// theme
		ComboBox<Theme> cmbTheme = new ComboBox<>();
		cmbTheme.setCellFactory((view) -> {
			return new ThemeListCell();
		});
		cmbTheme.setButtonCell(new ThemeListCell());
		cmbTheme.setValue(Themes.getTheme(configuration.getThemeName()));
		Bindings.bindContent(cmbTheme.getItems(), this.themes);
		
		cmbTheme.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				configuration.setThemeName(nv.getUserAgentStylesheet());
				Application.setUserAgentStylesheet(nv.getUserAgentStylesheet());
			}
		});
		
		Button btnReloadCss = new Button(Translations.get("settings.theme.reload"));
		btnReloadCss.setOnAction(e -> {
			StyleSheets.reapply(context.getStage().getScene());
//			String url = org.praisenter.ui.themes.Styles.getTheme("default").getCss();
//			context.getStage().getScene().getStylesheets().remove(url);
//			context.getStage().getScene().getStylesheets().add(url);
		});
		
		// language
		List<Locale> locales = Translations.getAvailableLocales();
		Locale locale = Locale.getDefault();
		String languageTag = configuration.getLanguageTag();
		if (!StringManipulator.isNullOrEmpty(languageTag)) {
			try {
				locale = Locale.forLanguageTag(languageTag);
			} catch (Exception ex) {
				LOGGER.warn("Failed to find locale for language tag {}", languageTag);
			}
		}
		locale = Translations.getClosestMatch(locale, locales);
		this.locales = FXCollections.observableArrayList(locales);
		ComboBox<Locale> cmbLocales = new ComboBox<>();
		Bindings.bindContent(cmbLocales.getItems(), this.locales);
		cmbLocales.setValue(locale);
		
		Button btnRefreshLocales = new Button(Translations.get("refresh"));
		btnRefreshLocales.setOnAction(e -> {
			this.locales.setAll(Translations.getAvailableLocales());
		});
		
		cmbLocales.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				configuration.setLanguageTag(nv.toLanguageTag());
			}
		});

		// placeholder transition behavior
		ObservableList<Option<PlaceholderTransitionBehavior>> behaviors = FXCollections.observableArrayList();
		behaviors.add(new Option<PlaceholderTransitionBehavior>(Translations.get("settings.slide.placeholderTransitionBehavior." + PlaceholderTransitionBehavior.PLACEHOLDERS), PlaceholderTransitionBehavior.PLACEHOLDERS));
		behaviors.add(new Option<PlaceholderTransitionBehavior>(Translations.get("settings.slide.placeholderTransitionBehavior." + PlaceholderTransitionBehavior.CONTENT), PlaceholderTransitionBehavior.CONTENT));
		behaviors.add(new Option<PlaceholderTransitionBehavior>(Translations.get("settings.slide.placeholderTransitionBehavior." + PlaceholderTransitionBehavior.SLIDE), PlaceholderTransitionBehavior.SLIDE));
		ComboBox<Option<PlaceholderTransitionBehavior>> cmbPlaceholderTransitionBehavior = new ComboBox<Option<PlaceholderTransitionBehavior>>(behaviors);
		cmbPlaceholderTransitionBehavior.setValue(new Option<>(null, configuration.getPlaceholderTransitionBehavior()));
		
		cmbPlaceholderTransitionBehavior.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				configuration.setPlaceholderTransitionBehavior(nv.getValue());
			}
		});
		
		// wait for transition flag
		ToggleSwitch tglWaitForTransition = new ToggleSwitch();
		tglWaitForTransition.setSelected(configuration.isWaitForTransitionsToCompleteEnabled());
		tglWaitForTransition.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setWaitForTransitionsToCompleteEnabled(nv);
		});
		
		// audio transcode enabled
		ToggleSwitch tglTranscodeAudio = new ToggleSwitch();
		tglTranscodeAudio.setSelected(configuration.isAudioTranscodingEnabled());
		tglTranscodeAudio.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setAudioTranscodingEnabled(nv);
		});
		
		// video transcode enabled
		ToggleSwitch tglTranscodeVideo = new ToggleSwitch();
		tglTranscodeVideo.setSelected(configuration.isVideoTranscodingEnabled());
		tglTranscodeVideo.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setVideoTranscodingEnabled(nv);
		});
		
		// volume adjust enabled
		ToggleSwitch tglAdjustVolume = new ToggleSwitch();
		tglAdjustVolume.setSelected(configuration.isVolumeAdjustmentEnabled());
		tglAdjustVolume.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setVolumeAdjustmentEnabled(nv);
		});
		
		// audio transcode extension
		TextField txtAudioTranscodeExtension = new TextField(configuration.getAudioTranscodeExtension());
		txtAudioTranscodeExtension.setMaxWidth(100);
		txtAudioTranscodeExtension.textProperty().addListener((obs, ov, nv) -> {
			configuration.setAudioTranscodeExtension(nv);
		});
		
		// video transcode extension
		TextField txtVideoTranscodeExtension = new TextField(configuration.getVideoTranscodeExtension());
		txtVideoTranscodeExtension.setMaxWidth(100);
		txtVideoTranscodeExtension.textProperty().addListener((obs, ov, nv) -> {
			configuration.setVideoTranscodeExtension(nv);
		});
		
		// audio transcode command
		TextField txtAudioTranscodeCommand = new TextField(configuration.getAudioTranscodeCommand());
		txtAudioTranscodeCommand.setMaxWidth(Double.MAX_VALUE);
		txtAudioTranscodeCommand.textProperty().addListener((obs, ov, nv) -> {
			configuration.setAudioTranscodeCommand(nv);
		});
		
		// video transcode command
		TextField txtVideoTranscodeCommand = new TextField(configuration.getVideoTranscodeCommand());
		txtVideoTranscodeCommand.setMaxWidth(Double.MAX_VALUE);
		txtVideoTranscodeCommand.textProperty().addListener((obs, ov, nv) -> {
			configuration.setVideoTranscodeCommand(nv);
		});
		
		// video frame extract command
		TextField txtVideoExtractCommand = new TextField(configuration.getVideoFrameExtractCommand());
		txtVideoExtractCommand.setMaxWidth(Double.MAX_VALUE);
		txtVideoExtractCommand.textProperty().addListener((obs, ov, nv) -> {
			configuration.setVideoFrameExtractCommand(nv);
		});
		
		// target mean volume
		Spinner<Double> spnTargetVolume = new Spinner<>(-100, 100, configuration.getTargetMeanVolume(), 1);
		spnTargetVolume.setEditable(true);
		spnTargetVolume.getValueFactory().setConverter(LastValueNumberStringConverter.forDouble((originalValueText) -> {
			Platform.runLater(() -> {
				spnTargetVolume.getEditor().setText(originalValueText);
			});
		}));
		spnTargetVolume.valueProperty().addListener((obs, ov, nv) -> {
			configuration.setTargetMeanVolume(nv);
		});
		
		// bible renumber
		ToggleSwitch tglBibleRenumberWarning = new ToggleSwitch();
		tglBibleRenumberWarning.setSelected(configuration.isRenumberBibleWarningEnabled());
		tglBibleRenumberWarning.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setRenumberBibleWarningEnabled(nv);
		});
		
		// bible reorder
		ToggleSwitch tglBibleReorderWarning = new ToggleSwitch();
		tglBibleReorderWarning.setSelected(configuration.isReorderBibleWarningEnabled());
		tglBibleReorderWarning.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setReorderBibleWarningEnabled(nv);
		});
		
		// debug mode
		ToggleSwitch tglDebugMode = new ToggleSwitch();
		tglDebugMode.setSelected(configuration.isDebugModeEnabled());
		tglDebugMode.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setDebugModeEnabled(nv);
		});

		Label lblGeneral = new Label(Translations.get("settings.general"));
		lblGeneral.getStyleClass().add(Styles.TITLE_3);
		Tile tleTheme = new Tile(Translations.get("settings.theme"), Translations.get("settings.theme.description"));
		tleTheme.setAction(new HBox(5, cmbTheme, btnReloadCss));
		tleTheme.setActionHandler(cmbTheme::requestFocus);
		Tile tleLocale = new Tile(Translations.get("settings.locale"), Translations.get("settings.locale.description"));
		tleLocale.setAction(new HBox(5, cmbLocales, btnRefreshLocales));
		tleLocale.setActionHandler(cmbLocales::requestFocus);
		Tile tleDebug = new Tile(Translations.get("settings.debug"), Translations.get("settings.debug.description"));
		tleDebug.setAction(tglDebugMode);
		tleDebug.setActionHandler(tglDebugMode::fire);
		VBox boxGeneral = new VBox(lblGeneral, new Separator(Orientation.HORIZONTAL), tleTheme, tleLocale, tleDebug);

		Label lblSlide = new Label(Translations.get("settings.slide"));
		lblSlide.getStyleClass().add(Styles.TITLE_3);
		Tile tleWaitForTransition = new Tile(Translations.get("settings.slide.waitForTransition"), Translations.get("settings.slide.waitForTransition.description"));
		tleWaitForTransition.setAction(tglWaitForTransition);
		tleWaitForTransition.setActionHandler(tglWaitForTransition::fire);
		Tile tlePlaceholderTransitionBehavior = new Tile(Translations.get("settings.slide.placeholderTransitionBehavior"), Translations.get("settings.slide.placeholderTransitionBehavior.description"));
		tlePlaceholderTransitionBehavior.setAction(cmbPlaceholderTransitionBehavior);
		tlePlaceholderTransitionBehavior.setActionHandler(cmbPlaceholderTransitionBehavior::requestFocus);
		VBox boxSlide = new VBox(lblSlide, new Separator(Orientation.HORIZONTAL), tleWaitForTransition, tlePlaceholderTransitionBehavior);
		
		Label lblBible = new Label(Translations.get("settings.bible"));
		lblBible.getStyleClass().add(Styles.TITLE_3);
		Tile tleBibleRenumberWarning = new Tile(Translations.get("settings.bible.renumberWarning"), Translations.get("settings.bible.renumberWarning.description"));
		tleBibleRenumberWarning.setAction(tglBibleRenumberWarning);
		tleBibleRenumberWarning.setActionHandler(tglBibleRenumberWarning::fire);
		Tile tleBibleReorderWarning = new Tile(Translations.get("settings.bible.reorderWarning"), Translations.get("settings.bible.reorderWarning.description"));
		tleBibleReorderWarning.setAction(tglBibleReorderWarning);
		tleBibleReorderWarning.setActionHandler(tglBibleReorderWarning::fire);
		VBox boxBible = new VBox(lblBible, new Separator(Orientation.HORIZONTAL), tleBibleRenumberWarning, tleBibleReorderWarning);

		Label lblMedia = new Label(Translations.get("settings.media"));
		lblMedia.getStyleClass().add(Styles.TITLE_3);
		Label lblMediaAudio = new Label(Translations.get("settings.media.audio"));
		lblMediaAudio.getStyleClass().add(Styles.TITLE_3);
		Label lblMediaVideo = new Label(Translations.get("settings.media.video"));
		lblMediaVideo.getStyleClass().add(Styles.TITLE_3);
		Tile tleTranscodeAudio = new Tile(Translations.get("settings.media.audioTranscode"), Translations.get("settings.media.audioTranscode.description"));
		tleTranscodeAudio.setAction(tglTranscodeAudio);
		tleTranscodeAudio.setActionHandler(tglTranscodeAudio::fire);
		Tile tleTranscodeAudioExt = new Tile(Translations.get("settings.media.audioTranscode.extension"), Translations.get("settings.media.audioTranscode.extension.description"));
		tleTranscodeAudioExt.setAction(txtAudioTranscodeExtension);
		tleTranscodeAudioExt.setActionHandler(txtAudioTranscodeExtension::requestFocus);
		Tile tleTranscodeAudioCommand = new Tile(Translations.get("settings.media.audioTranscode.command"), Translations.get("settings.media.audioTranscode.command.description"));
//		tleTranscodeAudioCommand.setAction(txtAudioTranscodeCommand);
		tleTranscodeAudioCommand.setActionHandler(txtAudioTranscodeCommand::requestFocus);
		Tile tleTranscodeVideo = new Tile(Translations.get("settings.media.videoTranscode"), Translations.get("settings.media.videoTranscode.description"));
		tleTranscodeVideo.setAction(tglTranscodeVideo);
		tleTranscodeVideo.setActionHandler(tglTranscodeVideo::fire);
		Tile tleTranscodeVideoExt = new Tile(Translations.get("settings.media.videoTranscode.extension"), Translations.get("settings.media.videoTranscode.extension.description"));
		tleTranscodeVideoExt.setAction(txtVideoTranscodeExtension);
		tleTranscodeVideoExt.setActionHandler(txtVideoTranscodeExtension::requestFocus);
		Tile tleTranscodeVideoCommand = new Tile(Translations.get("settings.media.videoTranscode.command"), Translations.get("settings.media.videoTranscode.command.description"));
//		tleTranscodeVideoCommand.setAction(txtVideoTranscodeCommand);
		tleTranscodeVideoCommand.setActionHandler(txtVideoTranscodeCommand::requestFocus);
		Tile tleVideoFrameExtractCommand = new Tile(Translations.get("settings.media.videoFrameExtract.command"), Translations.get("settings.media.videoFrameExtract.command.description"));
//		tleVideoFrameExtractCommand.setAction(txtVideoExtractCommand);
		tleVideoFrameExtractCommand.setActionHandler(txtVideoExtractCommand::requestFocus);
		Tile tleAdjustVolume = new Tile(Translations.get("settings.media.adjustVolume"), Translations.get("settings.media.adjustVolume.description"));
		tleAdjustVolume.setAction(tglAdjustVolume);
		tleAdjustVolume.setActionHandler(tglAdjustVolume::fire);
		Tile tleTargetVolume = new Tile(Translations.get("settings.media.targetVolume"), Translations.get("settings.media.targetVolume.description"));
		tleTargetVolume.setAction(spnTargetVolume);
		tleTargetVolume.setActionHandler(spnTargetVolume::requestFocus);
		VBox boxMedia = new VBox(
				lblMedia, 
				new Separator(Orientation.HORIZONTAL),
				tleAdjustVolume,
				tleTargetVolume);
		VBox boxMediaAudio = new VBox(
				lblMediaAudio, 
				new Separator(Orientation.HORIZONTAL), 
				tleTranscodeAudio,
				tleTranscodeAudioExt,
				tleTranscodeAudioCommand,
				txtAudioTranscodeCommand);
		VBox boxMediaVideo = new VBox(
				lblMediaVideo, 
				new Separator(Orientation.HORIZONTAL),
				tleTranscodeVideo,
				tleTranscodeVideoExt,
				tleTranscodeVideoCommand,
				txtVideoTranscodeCommand,
				tleVideoFrameExtractCommand,
				txtVideoExtractCommand);
		
		Label lblSettings = new Label(Translations.get("area.settings"));
		lblSettings.getStyleClass().add(Styles.TITLE_2);

		boxGeneral.getStyleClass().addAll(SETTINGS_PAGE_GROUP_CLASS);
		boxSlide.getStyleClass().addAll(SETTINGS_PAGE_GROUP_CLASS);
		boxBible.getStyleClass().addAll(SETTINGS_PAGE_GROUP_CLASS);
		boxMedia.getStyleClass().addAll(SETTINGS_PAGE_GROUP_CLASS);
		boxMediaAudio.getStyleClass().addAll(SETTINGS_PAGE_GROUP_CLASS);
		boxMediaVideo.getStyleClass().addAll(SETTINGS_PAGE_GROUP_CLASS);
		
		FlowPane flow = new FlowPane(Orientation.HORIZONTAL,
				boxGeneral, boxSlide, boxBible, boxMedia, boxMediaAudio, boxMediaVideo);
		flow.getStyleClass().addAll(SETTINGS_PAGE_FLOW_CLASS);
		
		ScrollPane scrLayout = new FastScrollPane(flow, 2.0);
		scrLayout.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrLayout.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrLayout.getStyleClass().addAll(SETTINGS_PAGE_SCROLL_CLASS);
		
		// JAVABUG (L) [workaround] FlowPane+ScrollPane doesn't work properly with the setFitToWidth method
		scrLayout.viewportBoundsProperty().addListener((obs, ov, nv) -> {
			flow.setPrefWrapLength(nv.getWidth());
			flow.setPrefWidth(nv.getWidth());
			flow.setMaxWidth(nv.getWidth());
			flow.setMinWidth(nv.getWidth());
			flow.requestLayout();
		});
		
		this.getStyleClass().add(SETTINGS_PAGE_CLASS);
		this.setCenter(scrLayout);
	}
}
