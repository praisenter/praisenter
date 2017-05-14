package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.object.MediaObject;
import org.praisenter.utility.Scaling;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ObservableMediaComponent extends ObservableSlideComponent<MediaComponent> {

	private final ObjectProperty<MediaObject> media = new SimpleObjectProperty<MediaObject>();
	
	// nodes
	
	private final FillPane mediaNode;
	
	public ObservableMediaComponent(MediaComponent component, PraisenterContext context, SlideMode mode) {
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
	
	protected final void updateMedia() {
		MediaObject mo = this.media.get();
		this.mediaNode.setPaint(mo);
	}

	@Override
	protected void onSizeUpdate(double w, double h, Scaling scaling) {
		this.mediaNode.setSize(w, h);
	}
	
	@Override
	protected void onBorderUpdate(SlideStroke ss) {
		double r = ss != null ? ss.getRadius() : 0.0;
		this.mediaNode.setBorderRadius(r);
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
