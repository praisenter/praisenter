package org.praisenter.data.slide.media;

import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.graphics.ScaleType;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.slide.effects.SlideColorAdjust;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadOnlyMediaObject extends SlidePaint, Copyable {
	public UUID getMediaId();
	public String getMediaName();
	public MediaType getMediaType();
	public ScaleType getScaleType();
	public boolean isLoopEnabled();
	public boolean isMuted();
	public SlideColorAdjust getColorAdjust();
	
	public ReadOnlyObjectProperty<UUID> mediaIdProperty();
	public ReadOnlyStringProperty mediaNameProperty();
	public ReadOnlyObjectProperty<MediaType> mediaTypeProperty();
	public ReadOnlyObjectProperty<ScaleType> scaleTypeProperty();
	public ReadOnlyBooleanProperty loopEnabledProperty();
	public ReadOnlyBooleanProperty mutedProperty();
	public ReadOnlyObjectProperty<SlideColorAdjust> colorAdjustProperty();
}
