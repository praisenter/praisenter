package org.praisenter.javafx.slide;

import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.object.MediaObject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ObservableMediaComponent extends ObservableSlideComponent<MediaComponent> implements SlideRegion, SlideComponent {

	final ObjectProperty<MediaObject> media = new SimpleObjectProperty<MediaObject>();
	
	public ObservableMediaComponent(MediaComponent component) {
		super(component);
		this.media.set(component.getMedia());
	}
	
	// media
	
	public MediaObject getMedia() {
		return this.media.get();
	}
	
	public void setMedia(MediaObject media) {
		this.media.set(media);
		this.region.setMedia(media);
	}
	
	public ObjectProperty<MediaObject> mediaProperty() {
		return this.media;
	}

	@Override
	public ObservableMediaComponent copy() {
		throw new UnsupportedOperationException();
	}
}
