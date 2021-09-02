package org.praisenter.data.workspace;           

import java.util.UUID;

import org.praisenter.data.Identifiable;
import org.praisenter.data.media.MediaConfiguration;
import org.praisenter.data.slide.SlideConfiguration;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;

public interface ReadOnlyWorkspaceConfiguration extends MediaConfiguration, SlideConfiguration, Identifiable {
	public String getFormat();
	public String getVersion();
	
	public UUID getPrimaryBibleId();
	public UUID getSecondaryBibleId();
	public boolean isRenumberBibleWarningEnabled();
	public boolean isReorderBibleWarningEnabled();
	
	public boolean isWaitForTransitionsToCompleteEnabled();

	public String getLanguageTag();
	public String getThemeName();
	public double getApplicationX();
	public double getApplicationY();
	public double getApplicationWidth();
	public double getApplicationHeight();
	public boolean isApplicationMaximized();
	public boolean isDebugModeEnabled();
	
	public ReadOnlyStringProperty formatProperty();
	public ReadOnlyStringProperty versionProperty();
	
	public ReadOnlyObjectProperty<UUID> primaryBibleIdProperty();
	public ReadOnlyObjectProperty<UUID> secondaryBibleIdProperty();
	public ReadOnlyBooleanProperty renumberBibleWarningEnabledProperty();
	public ReadOnlyBooleanProperty reorderBibleWarningEnabledProperty();
	
	public ReadOnlyIntegerProperty thumbnailWidthProperty();
	public ReadOnlyIntegerProperty thumbnailHeightProperty();
	public ReadOnlyBooleanProperty audioTranscodingEnabledProperty();
	public ReadOnlyBooleanProperty videoTranscodingEnabledProperty();
	public ReadOnlyBooleanProperty volumeAdjustmentEnabledProperty();
	public ReadOnlyStringProperty audioTranscodeExtensionProperty();
	public ReadOnlyStringProperty videoTranscodeExtensionProperty();
	public ReadOnlyStringProperty audioTranscodeCommandProperty();
	public ReadOnlyStringProperty videoTranscodeCommandProperty();
	public ReadOnlyStringProperty videoFrameExtractCommandProperty();
	public ReadOnlyDoubleProperty targetMeanVolumeProperty();
	
	public ReadOnlyBooleanProperty waitForTransitionsToCompleteEnabledProperty();
	
	public ReadOnlyStringProperty languageTagProperty();
	public ReadOnlyStringProperty themeNameProperty();
	public ReadOnlyDoubleProperty applicationXProperty();
	public ReadOnlyDoubleProperty applicationYProperty();
	public ReadOnlyDoubleProperty applicationWidthProperty();
	public ReadOnlyDoubleProperty applicationHeightProperty();
	public ReadOnlyBooleanProperty applicationMaximizedProperty();
	public ReadOnlyBooleanProperty debugModeEnabledProperty();
	
	public ObservableList<Display> getDisplaysUnmodifiable();
	public ObservableList<Resolution> getResolutionsUnmodifiable();
}
