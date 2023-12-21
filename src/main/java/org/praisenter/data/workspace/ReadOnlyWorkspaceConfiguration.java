package org.praisenter.data.workspace;           

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
	
	public boolean isRenumberBibleWarningEnabled();
	public boolean isReorderBibleWarningEnabled();
	
	public String getLanguageTag();
	public String getThemeName();
	public String getAccentName();
	public double getApplicationX();
	public double getApplicationY();
	public double getApplicationWidth();
	public double getApplicationHeight();
	public double getApplicationFontSize();
	public boolean isApplicationMaximized();
	public boolean isDebugModeEnabled();

	public boolean isWaitForTransitionsToCompleteEnabled();
	public PlaceholderTransitionBehavior getPlaceholderTransitionBehavior();
	
	public int getNDIFramesPerSecond();
	public boolean isNDIRenderOptimizationsEnabled();
	
	public ReadOnlyStringProperty formatProperty();
	public ReadOnlyStringProperty versionProperty();
	
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
	
	public ReadOnlyStringProperty languageTagProperty();
	public ReadOnlyStringProperty themeNameProperty();
	public ReadOnlyStringProperty accentNameProperty();
	public ReadOnlyDoubleProperty applicationXProperty();
	public ReadOnlyDoubleProperty applicationYProperty();
	public ReadOnlyDoubleProperty applicationWidthProperty();
	public ReadOnlyDoubleProperty applicationHeightProperty();
	public ReadOnlyDoubleProperty applicationFontSizeProperty();
	public ReadOnlyBooleanProperty applicationMaximizedProperty();
	public ReadOnlyBooleanProperty debugModeEnabledProperty();

	public ReadOnlyBooleanProperty waitForTransitionsToCompleteEnabledProperty();
	public ReadOnlyObjectProperty<PlaceholderTransitionBehavior> placeholderTransitionBehaviorProperty();
	
	public ReadOnlyIntegerProperty ndiFramesPerSecondProperty();
	public ReadOnlyBooleanProperty ndiRenderOptimizationsEnabledProperty();
	
	public ObservableList<? extends ReadOnlyResolution> getResolutionsUnmodifiable();
	public ObservableList<? extends ReadOnlyDisplayConfiguration> getDisplayConfigurationsUnmodifiable();
	
	public ReadOnlyDisplayConfiguration getDisplayConfigurationById(int id);
	public ReadOnlyDisplayConfiguration getPrimaryDisplayConfiguration();
}
