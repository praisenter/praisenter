package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.object.MediaObject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ObservableMediaComponent extends ObservableSlideComponent<MediaComponent> {

	final ObjectProperty<MediaObject> media = new SimpleObjectProperty<MediaObject>();
	
	// nodes
	
	final FillPane mediaNode;
	
	public ObservableMediaComponent(MediaComponent component, ObservableSlideContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.media.set(component.getMedia());

		// setup nodes
		this.mediaNode = new FillPane(context, mode);
		
		// listen for changes
		this.media.addListener((obs, ov, nv) -> { 
			this.region.setMedia(nv); 
			updateMedia();
		});
		
		this.build();
	}
	
	protected void build() {
		updateMedia();
		
		super.build(this.mediaNode);
	}
	
	@Override
	void updateSize() {
		super.updateSize();
		
		int w = this.width.get();
		int h = this.height.get();
		this.mediaNode.setSize(w, h);
	}
	
	@Override
	void updateBorder() {
		super.updateBorder();
		
		SlideStroke ss = this.border.get();
		double r = ss != null ? ss.getRadius() : 0.0;
		this.mediaNode.setBorderRadius(r);
	}
	
	private void updateMedia() {
		MediaObject mo = this.media.get();
		this.mediaNode.setPaint(mo);
	}

	// playable stuff
	
	public void play() {
		super.play();
		this.mediaNode.play();
	}
	
	public void stop() {
		super.stop();
		this.mediaNode.stop();
	}
	
	public void dispose() {
		super.dispose();
		this.mediaNode.dispose();
	}
	
	// media
	
	public MediaObject getMedia() {
		return this.media.get();
	}
	
	public void setMedia(MediaObject media) {
		this.media.set(media);
	}
	
	public ObjectProperty<MediaObject> mediaProperty() {
		return this.media;
	}
}
