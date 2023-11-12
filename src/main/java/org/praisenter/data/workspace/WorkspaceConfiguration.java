package org.praisenter.data.workspace;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.data.Identifiable;
import org.praisenter.data.media.MediaConfiguration;
import org.praisenter.data.slide.SlideConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
@JsonTypeName(value = "workspace")
public final class WorkspaceConfiguration implements ReadOnlyWorkspaceConfiguration, MediaConfiguration, SlideConfiguration, Identifiable {
	public static final double POSITION_SIZE_UNSET = -1;
	public static final double DEFAULT_FONT_SIZE = 14;
	
	private final StringProperty format;
	private final StringProperty version;
	private final ObjectProperty<UUID> id;
	
	private final BooleanProperty renumberBibleWarningEnabled;
	private final BooleanProperty reorderBibleWarningEnabled;
	
	private final IntegerProperty thumbnailWidth;
	private final IntegerProperty thumbnailHeight;
	private final BooleanProperty audioTranscodingEnabled;
	private final BooleanProperty videoTranscodingEnabled;
	private final BooleanProperty volumeAdjustmentEnabled;
	private final StringProperty audioTranscodeExtension;
	private final StringProperty videoTranscodeExtension;
	private final StringProperty audioTranscodeCommand;
	private final StringProperty videoTranscodeCommand;
	private final StringProperty videoFrameExtractCommand;
	private final DoubleProperty targetMeanVolume;
	
	private final StringProperty languageTag;
	private final StringProperty themeName;
	private final StringProperty accentName;
	private final DoubleProperty applicationX;
	private final DoubleProperty applicationY;
	private final DoubleProperty applicationWidth;
	private final DoubleProperty applicationHeight;
	private final DoubleProperty applicationFontSize;
	private final BooleanProperty applicationMaximized;
	private final BooleanProperty debugModeEnabled;

	private final BooleanProperty waitForTransitionsToCompleteEnabled;
	private final ObjectProperty<PlaceholderTransitionBehavior> placeholderTransitionBehavior;
	
	private final ObservableList<Resolution> resolutions;
	private final ObservableList<Resolution> resolutionsReadOnly;
	private final ObservableList<DisplayConfiguration> displayConfigurations;
	private final ObservableList<DisplayConfiguration> displayConfigurationsReadOnly;
	
	public WorkspaceConfiguration() {
		this.format = new SimpleStringProperty(Constants.FORMAT_NAME);
		this.version = new SimpleStringProperty(Version.STRING);
		this.id = new SimpleObjectProperty<UUID>(UUID.randomUUID());
		
		this.renumberBibleWarningEnabled = new SimpleBooleanProperty(true);
		this.reorderBibleWarningEnabled = new SimpleBooleanProperty(true);
		
		this.thumbnailWidth = new SimpleIntegerProperty(Constants.THUMBNAIL_SIZE);
		this.thumbnailHeight = new SimpleIntegerProperty(Constants.THUMBNAIL_SIZE);
		this.audioTranscodingEnabled = new SimpleBooleanProperty(true);
		this.videoTranscodingEnabled = new SimpleBooleanProperty(true);
		this.volumeAdjustmentEnabled = new SimpleBooleanProperty(true);
		this.audioTranscodeExtension = new SimpleStringProperty(MediaConfiguration.DEFAULT_AUDIO_EXTENSION);
		this.videoTranscodeExtension = new SimpleStringProperty(MediaConfiguration.DEFAULT_VIDEO_EXTENSION);
		this.audioTranscodeCommand = new SimpleStringProperty(MediaConfiguration.DEFAULT_TRANSCODE_COMMAND);
		this.videoTranscodeCommand = new SimpleStringProperty(MediaConfiguration.DEFAULT_TRANSCODE_COMMAND);
		this.videoFrameExtractCommand = new SimpleStringProperty(MediaConfiguration.DEFAULT_VIDEO_FRAME_EXTRACT_COMMAND);
		this.targetMeanVolume = new SimpleDoubleProperty(MediaConfiguration.DEFAULT_TARGET_MEAN_VOLUME);
		
		this.languageTag = new SimpleStringProperty(null);
		this.themeName = new SimpleStringProperty();
		this.accentName = new SimpleStringProperty();
		this.applicationX = new SimpleDoubleProperty(POSITION_SIZE_UNSET);
		this.applicationY = new SimpleDoubleProperty(POSITION_SIZE_UNSET);
		this.applicationWidth = new SimpleDoubleProperty(POSITION_SIZE_UNSET);
		this.applicationHeight = new SimpleDoubleProperty(POSITION_SIZE_UNSET);
		this.applicationFontSize = new SimpleDoubleProperty(DEFAULT_FONT_SIZE);
		this.applicationMaximized = new SimpleBooleanProperty(false);
		this.debugModeEnabled = new SimpleBooleanProperty(false);

		this.waitForTransitionsToCompleteEnabled = new SimpleBooleanProperty();
		this.placeholderTransitionBehavior = new SimpleObjectProperty<>(PlaceholderTransitionBehavior.PLACEHOLDERS);
		
		this.resolutions = FXCollections.observableArrayList(Resolution.DEFAULT_RESOLUTIONS);
		this.resolutionsReadOnly = FXCollections.unmodifiableObservableList(this.resolutions);
		this.displayConfigurations = FXCollections.observableArrayList();
		this.displayConfigurationsReadOnly = FXCollections.unmodifiableObservableList(this.displayConfigurations);
	}

	@Override
	public boolean identityEquals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof WorkspaceConfiguration) {
			return this.id.get().equals(((WorkspaceConfiguration)other).id.get());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.id.get().hashCode();
	}
	
	@Override
	public DisplayConfiguration getDisplayConfigurationById(int id) {
		Optional<DisplayConfiguration> result = this.displayConfigurations.stream().filter(dc -> dc.getId() == id).findFirst();
		if (result.isPresent()) {
			return result.get();
		}
		DisplayConfiguration dc = new DisplayConfiguration();
		dc.setId(id);
		this.displayConfigurations.add(dc);
		return dc;
	}

	@Override
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	public String getFormat() {
		return this.format.get();
	}
	
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	void setFormat(String format) {
		this.format.set(format);
	}
	
	@Override
	public ReadOnlyStringProperty formatProperty() {
		return this.format;
	}
	
	@Override
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	public String getVersion() {
		return this.version.get();
	}
	
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	public void setVersion(String version) {
		this.version.set(version);
	}
	
	@Override
	public StringProperty versionProperty() {
		return this.version;
	}
	
	@Override
	@JsonProperty
	public UUID getId() {
		return this.id.get();
	}
	
	@JsonProperty
	public void setId(UUID id) {
		this.id.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> idProperty() {
		return this.id;
	}
	
	@Override
	@JsonProperty
	public boolean isRenumberBibleWarningEnabled() {
		return this.renumberBibleWarningEnabled.get();
	}
	
	@JsonProperty
	public void setRenumberBibleWarningEnabled(boolean enabled) {
		this.renumberBibleWarningEnabled.set(enabled);
	}
	
	@Override
	public BooleanProperty renumberBibleWarningEnabledProperty() {
		return this.renumberBibleWarningEnabled;
	}

	@Override
	@JsonProperty
	public boolean isReorderBibleWarningEnabled() {
		return this.reorderBibleWarningEnabled.get();
	}
	
	@JsonProperty
	public void setReorderBibleWarningEnabled(boolean enabled) {
		this.reorderBibleWarningEnabled.set(enabled);
	}
	
	@Override
	public BooleanProperty reorderBibleWarningEnabledProperty() {
		return this.reorderBibleWarningEnabled;
	}

	@Override
	@JsonProperty
	public int getThumbnailWidth() {
		return this.thumbnailWidth.get();
	}
	
	@JsonProperty
	public void setThumbnailWidth(int width) {
		this.thumbnailWidth.set(width);
	}
	
	@Override
	public IntegerProperty thumbnailWidthProperty() {
		return this.thumbnailWidth;
	}
	
	@Override
	@JsonProperty
	public int getThumbnailHeight() {
		return this.thumbnailHeight.get();
	}
	
	@JsonProperty
	public void setThumbnailHeight(int height) {
		this.thumbnailHeight.set(height);
	}
	
	@Override
	public IntegerProperty thumbnailHeightProperty() {
		return this.thumbnailHeight;
	}
	
	@Override
	@JsonProperty
	public boolean isAudioTranscodingEnabled() {
		return this.audioTranscodingEnabled.get();
	}
	
	@JsonProperty
	public void setAudioTranscodingEnabled(boolean enabled) {
		this.audioTranscodingEnabled.set(enabled);
	}
	
	@Override
	public BooleanProperty audioTranscodingEnabledProperty() {
		return this.audioTranscodingEnabled;
	}
	
	@Override
	@JsonProperty
	public boolean isVideoTranscodingEnabled() {
		return this.videoTranscodingEnabled.get();
	}
	
	@JsonProperty
	public void setVideoTranscodingEnabled(boolean enabled) {
		this.videoTranscodingEnabled.set(enabled);
	}
	
	@Override
	public BooleanProperty videoTranscodingEnabledProperty() {
		return this.videoTranscodingEnabled;
	}
	
	@Override
	@JsonProperty
	public boolean isVolumeAdjustmentEnabled() {
		return this.volumeAdjustmentEnabled.get();
	}
	
	@JsonProperty
	public void setVolumeAdjustmentEnabled(boolean enabled) {
		this.volumeAdjustmentEnabled.set(enabled);
	}
	
	@Override
	public BooleanProperty volumeAdjustmentEnabledProperty() {
		return this.volumeAdjustmentEnabled;
	}
	
	@Override
	@JsonProperty
	public String getAudioTranscodeExtension() {
		return this.audioTranscodeExtension.get();
	}
	
	@JsonProperty
	public void setAudioTranscodeExtension(String tag) {
		this.audioTranscodeExtension.set(tag);
	}
	
	@Override
	public StringProperty audioTranscodeExtensionProperty() {
		return this.audioTranscodeExtension;
	}
	
	@Override
	@JsonProperty
	public String getVideoTranscodeExtension() {
		return this.videoTranscodeExtension.get();
	}
	
	@JsonProperty
	public void setVideoTranscodeExtension(String tag) {
		this.videoTranscodeExtension.set(tag);
	}
	
	@Override
	public StringProperty videoTranscodeExtensionProperty() {
		return this.videoTranscodeExtension;
	}
	
	@Override
	@JsonProperty
	public String getAudioTranscodeCommand() {
		return this.audioTranscodeCommand.get();
	}
	
	@JsonProperty
	public void setAudioTranscodeCommand(String tag) {
		this.audioTranscodeCommand.set(tag);
	}
	
	@Override
	public StringProperty audioTranscodeCommandProperty() {
		return this.audioTranscodeCommand;
	}
	
	@Override
	@JsonProperty
	public String getVideoTranscodeCommand() {
		return this.videoTranscodeCommand.get();
	}
	
	@JsonProperty
	public void setVideoTranscodeCommand(String tag) {
		this.videoTranscodeCommand.set(tag);
	}
	
	@Override
	public StringProperty videoTranscodeCommandProperty() {
		return this.videoTranscodeCommand;
	}
	
	@Override
	@JsonProperty
	public String getVideoFrameExtractCommand() {
		return this.videoFrameExtractCommand.get();
	}
	
	@JsonProperty
	public void setVideoFrameExtractCommand(String tag) {
		this.videoFrameExtractCommand.set(tag);
	}
	
	@Override
	public StringProperty videoFrameExtractCommandProperty() {
		return this.videoFrameExtractCommand;
	}
	
	@Override
	@JsonProperty
	public double getTargetMeanVolume() {
		return this.targetMeanVolume.get();
	}
	
	@JsonProperty
	public void setTargetMeanVolume(double x) {
		this.targetMeanVolume.set(x);
	}
	
	@Override
	public DoubleProperty targetMeanVolumeProperty() {
		return this.targetMeanVolume;
	}
	
	@Override
	@JsonProperty
	public String getLanguageTag() {
		return this.languageTag.get();
	}
	
	@JsonProperty
	public void setLanguageTag(String tag) {
		this.languageTag.set(tag);
	}
	
	@Override
	public StringProperty languageTagProperty() {
		return this.languageTag;
	}

	@Override
	@JsonProperty
	public String getThemeName() {
		return this.themeName.get();
	}
	
	@JsonProperty
	public void setThemeName(String tag) {
		this.themeName.set(tag);
	}
	
	@Override
	public StringProperty themeNameProperty() {
		return this.themeName;
	}

	@Override
	@JsonProperty
	public String getAccentName() {
		return this.accentName.get();
	}
	
	@JsonProperty
	public void setAccentName(String name) {
		this.accentName.set(name);
	}
	
	@Override
	public StringProperty accentNameProperty() {
		return this.accentName;
	}

	@Override
	@JsonProperty
	public double getApplicationX() {
		return this.applicationX.get();
	}
	
	@JsonProperty
	public void setApplicationX(double x) {
		this.applicationX.set(x);
	}
	
	@Override
	public DoubleProperty applicationXProperty() {
		return this.applicationX;
	}

	@Override
	@JsonProperty
	public double getApplicationY() {
		return this.applicationY.get();
	}
	
	@JsonProperty
	public void setApplicationY(double y) {
		this.applicationY.set(y);
	}
	
	@Override
	public DoubleProperty applicationYProperty() {
		return this.applicationY;
	}

	@Override
	@JsonProperty
	public double getApplicationWidth() {
		return this.applicationWidth.get();
	}
	
	@JsonProperty
	public void setApplicationWidth(double width) {
		this.applicationWidth.set(width);
	}
	
	@Override
	public DoubleProperty applicationWidthProperty() {
		return this.applicationWidth;
	}

	@Override
	@JsonProperty
	public double getApplicationHeight() {
		return this.applicationHeight.get();
	}
	
	@JsonProperty
	public void setApplicationHeight(double height) {
		this.applicationHeight.set(height);
	}
	
	@Override
	public DoubleProperty applicationHeightProperty() {
		return this.applicationHeight;
	}
	
	@Override
	@JsonProperty
	public double getApplicationFontSize() {
		return this.applicationFontSize.get();
	}
	
	@JsonProperty
	public void setApplicationFontSize(double size) {
		this.applicationFontSize.set(size);
	}
	
	@Override
	public DoubleProperty applicationFontSizeProperty() {
		return this.applicationFontSize;
	}

	@Override
	@JsonProperty
	public boolean isApplicationMaximized() {
		return this.applicationMaximized.get();
	}
	
	@JsonProperty
	public void setApplicationMaximized(boolean maximized) {
		this.applicationMaximized.set(maximized);
	}
	
	@Override
	public BooleanProperty applicationMaximizedProperty() {
		return this.applicationMaximized;
	}

	@Override
	@JsonProperty
	public boolean isDebugModeEnabled() {
		return this.debugModeEnabled.get();
	}
	
	@JsonProperty
	public void setDebugModeEnabled(boolean enabled) {
		this.debugModeEnabled.set(enabled);
	}
	
	@Override
	public BooleanProperty debugModeEnabledProperty() {
		return this.debugModeEnabled;
	}

	@Override
	@JsonProperty
	public boolean isWaitForTransitionsToCompleteEnabled() {
		return this.waitForTransitionsToCompleteEnabled.get();
	}
	
	@JsonProperty
	public void setWaitForTransitionsToCompleteEnabled(boolean enabled) {
		this.waitForTransitionsToCompleteEnabled.set(enabled);
	}
	
	@Override
	public BooleanProperty waitForTransitionsToCompleteEnabledProperty() {
		return this.waitForTransitionsToCompleteEnabled;
	}
	
	@Override
	@JsonProperty
	public PlaceholderTransitionBehavior getPlaceholderTransitionBehavior() {
		return this.placeholderTransitionBehavior.get();
	}
	
	@JsonProperty
	public void setPlaceholderTransitionBehavior(PlaceholderTransitionBehavior placeholderTransitionBehavior) {
		this.placeholderTransitionBehavior.set(placeholderTransitionBehavior);
	}
	
	@Override
	public ObjectProperty<PlaceholderTransitionBehavior> placeholderTransitionBehaviorProperty() {
		return this.placeholderTransitionBehavior;
	}

	@JsonProperty
	public ObservableList<Resolution> getResolutions() {
		return this.resolutions;
	}
	
	@JsonProperty
	public void setResolutions(List<Resolution> resolutions) {
		this.resolutions.setAll(resolutions);
	}
	
	@Override
	public ObservableList<Resolution> getResolutionsUnmodifiable() {
		return this.resolutionsReadOnly;
	}
	
	@JsonProperty
	public ObservableList<DisplayConfiguration> getDisplayConfigurations() {
		return this.displayConfigurations;
	}
	
	@JsonProperty
	public void setDisplayConfigurations(List<DisplayConfiguration> displayConfigurations) {
		this.displayConfigurations.setAll(displayConfigurations);
	}
	
	@Override
	public ObservableList<DisplayConfiguration> getDisplayConfigurationsUnmodifiable() {
		return this.displayConfigurationsReadOnly;
	}
}
