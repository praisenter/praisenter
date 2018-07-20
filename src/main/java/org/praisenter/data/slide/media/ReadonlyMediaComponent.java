package org.praisenter.data.slide.media;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.ReadonlySlideComponent;
import org.praisenter.data.slide.ReadonlySlideRegion;

import javafx.beans.property.ObjectProperty;

public interface ReadonlyMediaComponent extends ReadonlySlideComponent, ReadonlySlideRegion, Copyable, Identifiable {
	public MediaObject getMedia();
	
	public ObjectProperty<MediaObject> mediaProperty();
}
