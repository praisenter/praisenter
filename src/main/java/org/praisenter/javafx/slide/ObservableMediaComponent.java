/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.media.MediaComponent;
import org.praisenter.slide.media.MediaObject;
import org.praisenter.utility.Scaling;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents an observable {@link MediaComponent}.
 * @author Wiliam Bittle
 * @version 3.0.0
 */
public final class ObservableMediaComponent extends ObservableSlideComponent<MediaComponent> implements Playable {
	/** The media */
	private final ObjectProperty<MediaObject> media = new SimpleObjectProperty<MediaObject>();
	
	// nodes
	
	/** The node to display the media */
	private final FillPane mediaNode;
	
	/**
	 * Minimal constructor.
	 * @param component the media component
	 * @param context the context
	 * @param mode the slide mode
	 */
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
			updateName();
		});
		
		this.build(this.mediaNode);
	}
	
	protected void updateAll() {
		super.updateAll();
		this.updateMedia();
	}
	
	/**
	 * Updates the Java FX component when the media changes.
	 */
	protected final void updateMedia() {
		MediaObject mo = this.media.get();
		this.mediaNode.setPaint(mo);
		this.onMediaUpdate(mo);
	}
	
	/**
	 * Called after the media has been updated.
	 * @param media the new media
	 */
	protected void onMediaUpdate(MediaObject media) {}

	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#onSizeUpdate(double, double, org.praisenter.utility.Scaling)
	 */
	@Override
	protected void onSizeUpdate(double w, double h, Scaling scaling) {
		this.mediaNode.setSize(w, h);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#onBorderUpdate(org.praisenter.slide.graphics.SlideStroke)
	 */
	@Override
	protected void onBorderUpdate(SlideStroke ss) {
		double r = ss != null ? ss.getRadius() : 0.0;
		this.mediaNode.setBorderRadius(r);
	}
	
	// playable stuff
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#play()
	 */
	public void play() {
		super.play();
		this.mediaNode.play();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#stop()
	 */
	public void stop() {
		super.stop();
		this.mediaNode.stop();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#dispose()
	 */
	public void dispose() {
		super.dispose();
		this.mediaNode.dispose();
	}
	
	// media
	
	/**
	 * Returns the media.
	 * @return {@link MediaObject}
	 */
	public MediaObject getMedia() {
		return this.media.get();
	}
	
	/**
	 * Sets the media.
	 * @param media the media
	 */
	public void setMedia(MediaObject media) {
		this.media.set(media);
	}
	
	/**
	 * Returns the media property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<MediaObject> mediaProperty() {
		return this.media;
	}
}
