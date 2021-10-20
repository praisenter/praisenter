package org.praisenter.data.slide.media;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.ReadOnlySlideComponent;
import org.praisenter.data.slide.ReadOnlySlideRegion;

import javafx.beans.property.ObjectProperty;

public interface ReadOnlyMediaComponent extends ReadOnlySlideComponent, ReadOnlySlideRegion, Copyable, Identifiable {
	public MediaObject getMedia();
	
	public ObjectProperty<MediaObject> mediaProperty();
}
