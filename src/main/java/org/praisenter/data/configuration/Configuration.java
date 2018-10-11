package org.praisenter.data.configuration;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.praisenter.Constants;
import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.Persistable;
import org.praisenter.data.json.InstantJsonDeserializer;
import org.praisenter.data.json.InstantJsonSerializer;
import org.praisenter.data.media.MediaConfiguration;
import org.praisenter.data.search.Indexable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
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
@JsonTypeName(value = "configuration")
public final class Configuration implements ReadOnlyConfiguration, MediaConfiguration, Indexable, Persistable, Copyable, Identifiable {
	public static final double POSITION_SIZE_UNSET = -1;
	
	private final StringProperty format;
	private final StringProperty version;
	private final ObjectProperty<UUID> id;
	private final StringProperty name;
	private final ObjectProperty<Instant> createdDate;
	private final ObjectProperty<Instant> modifiedDate;
	
	private final ObjectProperty<UUID> primaryBibleId;
	private final ObjectProperty<UUID> secondaryBibleId;
	private final BooleanProperty renumberBibleWarningEnabled;
	private final BooleanProperty reorderBibleWarningEnabled;
	
	private final IntegerProperty thumbnailWidth;
	private final IntegerProperty thumbnailHeight;
	private final BooleanProperty audioTranscodingEnabled;
	private final BooleanProperty videoTranscodingEnabled;
	private final StringProperty audioTranscodeExtension;
	private final StringProperty videoTranscodeExtension;
	private final StringProperty audioTranscodeCommand;
	private final StringProperty videoTranscodeCommand;
	private final StringProperty videoFrameExtractCommand;
	
	private final BooleanProperty waitForTransitionsToCompleteEnabled;
	
	private final StringProperty languageTag;
	private final StringProperty themeName;
	private final DoubleProperty applicationX;
	private final DoubleProperty applicationY;
	private final DoubleProperty applicationWidth;
	private final DoubleProperty applicationHeight;
	private final BooleanProperty applicationMaximized;
	private final BooleanProperty debugModeEnabled;
	
	private final ObservableList<Display> displays;
	private final ObservableList<Resolution> resolutions;
	
	public Configuration() {
		this.format = new SimpleStringProperty(Constants.FORMAT_NAME);
		this.version = new SimpleStringProperty(Constants.VERSION);
		this.id = new SimpleObjectProperty<UUID>(UUID.randomUUID());
		this.name = new SimpleStringProperty("Application Configuration");
		this.createdDate = new SimpleObjectProperty<Instant>(Instant.now());
		this.modifiedDate = new SimpleObjectProperty<Instant>(this.createdDate.get());
		
		this.primaryBibleId = new SimpleObjectProperty<>();
		this.secondaryBibleId = new SimpleObjectProperty<>();
		this.renumberBibleWarningEnabled = new SimpleBooleanProperty(true);
		this.reorderBibleWarningEnabled = new SimpleBooleanProperty(true);
		
		this.thumbnailWidth = new SimpleIntegerProperty(Constants.THUMBNAIL_SIZE);
		this.thumbnailHeight = new SimpleIntegerProperty(Constants.THUMBNAIL_SIZE);
		this.audioTranscodingEnabled = new SimpleBooleanProperty(true);
		this.videoTranscodingEnabled = new SimpleBooleanProperty(true);
		this.audioTranscodeExtension = new SimpleStringProperty(MediaConfiguration.DEFAULT_AUDIO_EXTENSION);
		this.videoTranscodeExtension = new SimpleStringProperty(MediaConfiguration.DEFAULT_VIDEO_EXTENSION);
		this.audioTranscodeCommand = new SimpleStringProperty(MediaConfiguration.DEFAULT_TRANSCODE_COMMAND);
		this.videoTranscodeCommand = new SimpleStringProperty(MediaConfiguration.DEFAULT_TRANSCODE_COMMAND);
		this.videoFrameExtractCommand = new SimpleStringProperty(MediaConfiguration.DEFAULT_VIDEO_FRAME_EXTRACT_COMMAND);
		
		this.waitForTransitionsToCompleteEnabled = new SimpleBooleanProperty(true);
		
		this.languageTag = new SimpleStringProperty(null);
		this.themeName = new SimpleStringProperty("default");
		this.applicationX = new SimpleDoubleProperty(POSITION_SIZE_UNSET);
		this.applicationY = new SimpleDoubleProperty(POSITION_SIZE_UNSET);
		this.applicationWidth = new SimpleDoubleProperty(POSITION_SIZE_UNSET);
		this.applicationHeight = new SimpleDoubleProperty(POSITION_SIZE_UNSET);
		this.applicationMaximized = new SimpleBooleanProperty(false);
		this.debugModeEnabled = new SimpleBooleanProperty(false);
		
		this.displays = FXCollections.observableArrayList();
		this.resolutions = FXCollections.observableArrayList(Resolution.DEFAULT_RESOLUTIONS);
	}

	@Override
	public boolean identityEquals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof Configuration) {
			return this.id.get().equals(((Configuration)other).id.get());
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.identityEquals(obj);
	}
	
	@Override
	public int hashCode() {
		return this.id.get().hashCode();
	}
	
	@Override
	public String toString() {
		return this.name.get();
	}
	
	@Override
	public Configuration copy() {
		Configuration config = new Configuration();
		
		config.format.set(this.format.get());
		config.version.set(this.version.get());
		config.id.set(this.id.get());
		config.name.set(this.name.get());
		config.createdDate.set(this.createdDate.get());
		config.modifiedDate.set(this.modifiedDate.get());
		
		config.primaryBibleId.set(this.primaryBibleId.get());
		config.secondaryBibleId.set(this.secondaryBibleId.get());
		config.renumberBibleWarningEnabled.set(this.renumberBibleWarningEnabled.get());
		config.reorderBibleWarningEnabled.set(this.reorderBibleWarningEnabled.get());
		
		config.thumbnailWidth.set(this.thumbnailWidth.get());
		config.thumbnailHeight.set(this.thumbnailHeight.get());
		config.audioTranscodingEnabled.set(this.audioTranscodingEnabled.get());
		config.videoTranscodingEnabled.set(this.videoTranscodingEnabled.get());
		config.audioTranscodeExtension.set(this.audioTranscodeExtension.get());
		config.videoTranscodeExtension.set(this.videoTranscodeExtension.get());
		config.audioTranscodeCommand.set(this.audioTranscodeCommand.get());
		config.videoTranscodeCommand.set(this.videoTranscodeCommand.get());
		config.videoFrameExtractCommand.set(this.videoFrameExtractCommand.get());
		
		config.waitForTransitionsToCompleteEnabled.set(this.waitForTransitionsToCompleteEnabled.get());
		
		config.languageTag.set(this.languageTag.get());
		config.themeName.set(this.themeName.get());
		config.applicationX.set(this.applicationX.get());
		config.applicationY.set(this.applicationY.get());
		config.applicationWidth.set(this.applicationWidth.get());
		config.applicationHeight.set(this.applicationHeight.get());
		config.applicationMaximized.set(this.applicationMaximized.get());
		config.debugModeEnabled.set(this.debugModeEnabled.get());
		
		for (Display display : this.displays) {
			config.displays.add(display.copy());
		}
		
		for (Resolution resolution : this.resolutions) {
			config.resolutions.add(resolution.copy());
		}
		
		return config;
	}
	
	@Override
	public List<Document> index() {
		// not included in the index
		return null;
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
	public StringProperty formatProperty() {
		return this.format;
	}
	
	@Override
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	public String getVersion() {
		return this.version.get();
	}
	
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	void setVersion(String version) {
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
	void setId(UUID id) {
		this.id.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> idProperty() {
		return this.id;
	}
	
	@Override
	@JsonProperty
	public String getName() {
		return this.name.get();
	}
	
	@JsonProperty
	public void setName(String name) {
		this.name.set(name);
	}
	
	@Override
	public StringProperty nameProperty() {
		return this.name;
	}

	@Override
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	public Instant getCreatedDate() {
		return this.createdDate.get();
	}
	
	@JsonProperty
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	void setCreatedDate(Instant date) {
		this.createdDate.set(date);
	}
	
	@Override
	public ObjectProperty<Instant> createdDateProperty() {
		return this.createdDate;
	}

	@Override
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	public Instant getModifiedDate() {
		return this.modifiedDate.get();
	}
	
	@JsonProperty
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	void setModifiedDate(Instant date) {
		this.modifiedDate.set(date);
	}
	
	@Override
	public ObjectProperty<Instant> modifiedDateProperty() {
		return this.modifiedDate;
	}

	@Override
	@JsonProperty
	public UUID getPrimaryBibleId() {
		return this.primaryBibleId.get();
	}
	
	@JsonProperty
	public void setPrimaryBibleId(UUID id) {
		this.primaryBibleId.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> primaryBibleIdProperty() {
		return this.primaryBibleId;
	}

	@Override
	@JsonProperty
	public UUID getSecondaryBibleId() {
		return this.secondaryBibleId.get();
	}
	
	@JsonProperty
	public void setSecondaryBibleId(UUID id) {
		this.secondaryBibleId.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> secondaryBibleIdProperty() {
		return this.secondaryBibleId;
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
	public ObservableList<Display> getDisplaysUnmodifiable() {
		return FXCollections.unmodifiableObservableList(this.displays);
	}
	
	@JsonProperty
	public ObservableList<Display> getDisplays() {
		return this.displays;
	}
	
	@JsonProperty
	public void setDisplays(List<Display> displays) {
		this.displays.setAll(displays);
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
		return FXCollections.unmodifiableObservableList(this.resolutions);
	}
}
