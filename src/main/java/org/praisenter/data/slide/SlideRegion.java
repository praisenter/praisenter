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
package org.praisenter.data.slide;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.praisenter.Watchable;
import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.graphics.Rectangle;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.graphics.SlideStroke;
import org.praisenter.data.slide.media.MediaObject;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Abstract implementation of the {@link SlideRegion} interface.
 * @author William Bittle
 * @version 3.0.0
 */
public class SlideRegion implements ReadOnlySlideRegion, Copyable, Identifiable {
	public static final double MIN_SIZE = 20;
	
	protected final ObjectProperty<UUID> id;
	protected final DoubleProperty x;
	protected final DoubleProperty y;
	protected final DoubleProperty width;
	protected final DoubleProperty height;
	protected final ObjectProperty<SlideStroke> border;
	protected final ObjectProperty<SlidePaint> background;
	protected final DoubleProperty opacity;

	protected final ReadOnlyStringWrapper name;
	
	/**
	 * Default constructor.
	 */
	public SlideRegion() {
		this.id = new SimpleObjectProperty<UUID>(UUID.randomUUID());
		this.name = new ReadOnlyStringWrapper();
		this.x = new SimpleDoubleProperty(0);
		this.y = new SimpleDoubleProperty(0);
		this.width = new SimpleDoubleProperty(100);
		this.height = new SimpleDoubleProperty(100);
		this.border = new SimpleObjectProperty<>();
		this.background = new SimpleObjectProperty<>();
		this.opacity = new SimpleDoubleProperty(1);
	}

	@Override
	public SlideRegion copy() {
		SlideRegion region = new SlideRegion();
		this.copyTo(region);
		return region;
	}
	
	protected void copyTo(SlideRegion region) {
		region.background.set(this.background.get().copy());
		region.border.set(this.border.get().copy());
		region.height.set(this.height.get());
		region.id.set(this.id.get());
		region.name.set(this.name.get());
		region.opacity.set(this.opacity.get());
		region.width.set(this.width.get());
		region.x.set(this.x.get());
		region.y.set(this.y.get());
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.identityEquals(obj);
	}
	
	@Override
	public int hashCode() {
		return this.id.get().hashCode();
	}
	
	@Override
	public boolean identityEquals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof SlideRegion) {
			SlideRegion region = (SlideRegion)other;
			return region.id.get().equals(this.id.get());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.name.get();
	}
	
	@Override
	@JsonProperty
	public UUID getId() {
		return this.id.get();
	}
	
	@JsonProperty
	public void setId(UUID id) {
		this.id.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> idProperty() {
		return this.id;
	}
	
	@Override
	public String getName() {
		return this.name.get();
	}
	
	@Override
	@Watchable(name = "name")
	public ReadOnlyStringProperty nameProperty() {
		return this.name.getReadOnlyProperty();
	}
	
	@Override
	@JsonProperty
	public double getX() {
		return this.x.get();
	}
	
	@JsonProperty
	public void setX(double x) {
		this.x.set(x);
	}
	
	@Override
	public DoubleProperty xProperty() {
		return this.x;
	}
	
	@Override
	@JsonProperty
	public double getY() {
		return this.y.get();
	}
	
	@JsonProperty
	public void setY(double y) {
		this.y.set(y);
	}
	
	@Override
	public DoubleProperty yProperty() {
		return this.y;
	}
	
	@Override
	@JsonProperty
	public double getWidth() {
		return this.width.get();
	}
	
	@JsonProperty
	public void setWidth(double width) {
		this.width.set(width);
	}
	
	@Override
	public DoubleProperty widthProperty() {
		return this.width;
	}
	
	@Override
	@JsonProperty
	public double getHeight() {
		return this.height.get();
	}
	
	@JsonProperty
	public void setHeight(double height) {
		this.height.set(height);
	}
	
	@Override
	public DoubleProperty heightProperty() {
		return this.height;
	}
	
	@Override
	public Rectangle getBounds() {
		return new Rectangle(this.x.get(), this.y.get(), this.width.get(), this.height.get());
	}
	
	@Override
	@JsonProperty
	public SlidePaint getBackground() {
		return this.background.get();
	}
	
	@JsonProperty
	public void setBackground(SlidePaint background) {
		this.background.set(background);
	}
	
	@Override
	@Watchable(name = "background")
	public ObjectProperty<SlidePaint> backgroundProperty() {
		return this.background;
	}
	
	@Override
	@JsonProperty
	public SlideStroke getBorder() {
		return this.border.get();
	}
	
	@JsonProperty
	public void setBorder(SlideStroke border) {
		this.border.set(border);
	}
	
	@Override
	@Watchable(name = "border")
	public ObjectProperty<SlideStroke> borderProperty() {
		return this.border;
	}
	
	@Override
	@JsonProperty
	public double getOpacity() {
		return this.opacity.get();
	}
	
	@JsonProperty
	public void setOpacity(double opacity) {
		this.opacity.set(opacity);
	}
	
	@Override
	@Watchable(name = "opacity")
	public DoubleProperty opacityProperty() {
		return this.opacity;
	}
	
	// other
	
//	@Override
//	public Rectangle getBounds() {
//		return new Rectangle(this.x.get(), this.y.get(), this.width.get(), this.height.get());
//	}
	
	public void adjust(double pw, double ph) {
		// adjust width/height
		this.width.set(Math.floor(this.width.get() * pw));
		this.height.set(Math.floor(this.height.get() * ph));
		
		// adjust positioning
		this.x.set(Math.ceil(this.x.get() * pw));
		this.y.set(Math.ceil(this.y.get() * ph));
	}
	
	public Rectangle resize(double dw, double dh) {
		// update
		this.width.set(this.width.get() + dw);
		this.height.set(this.height.get() + dh);
		
		// make sure we dont go too small width/height
		if (this.width.get() < MIN_SIZE) {
			this.width.set(MIN_SIZE);
		}
		if (this.height.get() < MIN_SIZE) {
			this.height.set(MIN_SIZE);
		}
		
		return new Rectangle(this.x.get(), this.y.get(), this.width.get(), this.height.get());
	}
	
	public void translate(double dx, double dy) {
		this.x.set(this.x.get() + dx);
		this.y.set(this.y.get() + dy);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#isMediaReferenced(java.util.UUID[])
	 */
	@Override
	public final boolean isMediaReferenced(UUID... ids) {
		Set<UUID> media = this.getReferencedMedia();
		for (UUID testId : ids) {
			if (media.contains(testId)) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getReferencedMedia()
	 */
	@Override
	public Set<UUID> getReferencedMedia() {
		Set<UUID> ids = new HashSet<UUID>();
		
		// check the background
		SlidePaint bg = this.background.get();
		if (bg != null && bg instanceof MediaObject) {
			MediaObject mo = (MediaObject)bg;
			if (mo.getMediaId() != null) {
				ids.add(mo.getMediaId());
			}
		}
		
		// check the slide stroke
		SlideStroke border = this.border.get();
		if (border != null && border.getPaint() instanceof MediaObject) {
			MediaObject mo = (MediaObject)border.getPaint();
			if (mo.getMediaId() != null) {
				ids.add(mo.getMediaId());
			}
		}
		return ids;
	}
}
