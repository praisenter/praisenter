package org.praisenter.ui;

import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.workspace.PlaceholderTransitionBehavior;
import org.praisenter.data.workspace.WorkspaceConfiguration;
import org.praisenter.ui.controls.FormField;
import org.praisenter.ui.controls.FormFieldGroup;
import org.praisenter.ui.controls.LastValueNumberStringConverter;
import org.praisenter.ui.themes.Theme;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.StringManipulator;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SettingsPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String SETTINGS_PANE_CLASS = "p-settings-pane";
	
	private final ObservableList<Theme> themes;
	private final ObservableList<Locale> locales;
	
	public SettingsPane(GlobalContext context) {
		WorkspaceConfiguration configuration = context.getWorkspaceConfiguration();
		
		// theme
		this.themes = FXCollections.observableArrayList(Theme.getAvailableThemes());
		ComboBox<Theme> cmbTheme = new ComboBox<>();
		Bindings.bindContent(cmbTheme.getItems(), this.themes);
		cmbTheme.setValue(Theme.getTheme(configuration.getThemeName()));
		
		Button btnRefreshThemes = new Button(Translations.get("refresh"));
		btnRefreshThemes.setOnAction(e -> {
			this.themes.setAll(Theme.getAvailableThemes());
		});
		
		cmbTheme.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				configuration.setThemeName(nv.getName());
			}
		});
		
		Button btnReloadCss = new Button(Translations.get("settings.theme.reload"));
		btnReloadCss.setOnAction(e -> {
			String url = cmbTheme.getValue().getCss();
			context.stage.getScene().getStylesheets().remove(url);
			context.stage.getScene().getStylesheets().add(url);
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
		CheckBox chkWaitForTransition = new CheckBox();
		chkWaitForTransition.setSelected(configuration.isWaitForTransitionsToCompleteEnabled());
		chkWaitForTransition.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setWaitForTransitionsToCompleteEnabled(nv);
		});
		
		// audio transcode enabled
		CheckBox chkTranscodeAudio = new CheckBox();
		chkTranscodeAudio.setSelected(configuration.isAudioTranscodingEnabled());
		chkTranscodeAudio.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setAudioTranscodingEnabled(nv);
		});
		
		// video transcode enabled
		CheckBox chkTranscodeVideo = new CheckBox();
		chkTranscodeVideo.setSelected(configuration.isVideoTranscodingEnabled());
		chkTranscodeVideo.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setVideoTranscodingEnabled(nv);
		});
		
		// volume adjust enabled
		CheckBox chkAdjustVolume = new CheckBox();
		chkAdjustVolume.setSelected(configuration.isVolumeAdjustmentEnabled());
		chkAdjustVolume.selectedProperty().addListener((obs, ov, nv) -> {
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
		CheckBox chkBibleRenumberWarning = new CheckBox();
		chkBibleRenumberWarning.setSelected(configuration.isRenumberBibleWarningEnabled());
		chkBibleRenumberWarning.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setRenumberBibleWarningEnabled(nv);
		});
		
		// bible reorder
		CheckBox chkBibleReorderWarning = new CheckBox();
		chkBibleReorderWarning.setSelected(configuration.isReorderBibleWarningEnabled());
		chkBibleReorderWarning.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setReorderBibleWarningEnabled(nv);
		});
		
		// debug mode
		CheckBox chkDebugMode = new CheckBox();
		chkDebugMode.setSelected(configuration.isDebugModeEnabled());
		chkDebugMode.selectedProperty().addListener((obs, ov, nv) -> {
			configuration.setDebugModeEnabled(nv);
		});
		
		VBox boxGeneral = new VBox(
				new FormField(Translations.get("settings.theme"), Translations.get("settings.theme.description"), cmbTheme, btnRefreshThemes, btnReloadCss),
				new FormField(Translations.get("settings.locale"), Translations.get("settings.locale.description"), cmbLocales, btnRefreshLocales),
				new FormField(Translations.get("settings.debug"), Translations.get("settings.debug.description"), chkDebugMode));
		FormFieldGroup pneGeneral = new FormFieldGroup(Translations.get("settings.general"), boxGeneral);
		
		VBox boxSlide = new VBox(
				new FormField(Translations.get("settings.slide.waitForTransition"), Translations.get("settings.slide.waitForTransition.description"), chkWaitForTransition),
				new FormField(Translations.get("settings.slide.placeholderTransitionBehavior"), Translations.get("settings.slide.placeholderTransitionBehavior.description"), cmbPlaceholderTransitionBehavior));
		FormFieldGroup pneSlide = new FormFieldGroup(Translations.get("settings.slide"), boxSlide);
		pneSlide.setExpanded(false);
		
		VBox boxMedia = new VBox(
				new FormField(Translations.get("settings.media.audioTranscode"), Translations.get("settings.media.audioTranscode.description"), chkTranscodeAudio),
				new FormField(Translations.get("settings.media.audioTranscode.extension"), Translations.get("settings.media.audioTranscode.extension.description"), txtAudioTranscodeExtension),
				new FormField(Translations.get("settings.media.audioTranscode.command"), Translations.get("settings.media.audioTranscode.command.description"), txtAudioTranscodeCommand),
				new FormField(Translations.get("settings.media.videoTranscode"), Translations.get("settings.media.videoTranscode.description"), chkTranscodeVideo),
				new FormField(Translations.get("settings.media.videoTranscode.extension"), Translations.get("settings.media.videoTranscode.extension.description"), txtVideoTranscodeExtension),
				new FormField(Translations.get("settings.media.videoTranscode.command"), Translations.get("settings.media.videoTranscode.command.description"), txtVideoTranscodeCommand),
				new FormField(Translations.get("settings.media.videoFrameExtract.command"), Translations.get("settings.media.videoFrameExtract.command.description"), txtVideoExtractCommand),
				new FormField(Translations.get("settings.media.adjustVolume"), Translations.get("settings.media.adjustVolume.description"), chkAdjustVolume),
				new FormField(Translations.get("settings.media.targetVolume"), Translations.get("settings.media.targetVolume.description"), spnTargetVolume));
		FormFieldGroup pneAV = new FormFieldGroup(Translations.get("settings.media"), boxMedia);
		pneAV.setExpanded(false);
		
		VBox boxBible = new VBox(
				new FormField(Translations.get("settings.bible.renumberWarning"), Translations.get("settings.bible.renumberWarning.description"), chkBibleRenumberWarning),
				new FormField(Translations.get("settings.bible.reorderWarning"), Translations.get("settings.bible.reorderWarning.description"), chkBibleReorderWarning));
		FormFieldGroup pneBible = new FormFieldGroup(Translations.get("settings.bible"), boxBible);
		pneBible.setExpanded(false);
		
		VBox layout = new VBox(
				pneGeneral,
				pneSlide,
				pneBible,
				pneAV);
		layout.getStyleClass().add(SETTINGS_PANE_CLASS);
		
		ScrollPane scrLayout = new ScrollPane(layout);
		scrLayout.setFitToWidth(true);
		scrLayout.setHbarPolicy(ScrollBarPolicy.NEVER);
		
		this.setCenter(scrLayout);
	}
}
