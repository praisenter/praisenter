package org.praisenter.data.slide;

import java.util.Set;
import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.graphics.Rectangle;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.graphics.SlideStroke;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadOnlySlideRegion extends Copyable, Identifiable {
	public String getName();
	public double getWidth();
	public double getHeight();
	public SlidePaint getBackground();
	public SlideStroke getBorder();
	public double getOpacity();

	public ReadOnlyStringProperty nameProperty();
	public ReadOnlyDoubleProperty widthProperty();
	public ReadOnlyDoubleProperty heightProperty();
	public ReadOnlyObjectProperty<SlidePaint> backgroundProperty();
	public ReadOnlyObjectProperty<SlideStroke> borderProperty();
	public ReadOnlyDoubleProperty opacityProperty();
	
	// other

	public Rectangle getBounds();
	public boolean isMediaReferenced(UUID... ids);
	public Set<UUID> getReferencedMedia();
	public boolean hasPlayableMedia();
}
