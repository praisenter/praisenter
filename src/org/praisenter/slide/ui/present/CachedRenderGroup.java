/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.slide.ui.present;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.List;

import org.praisenter.preferences.Preferences;
import org.praisenter.slide.RenderableComponent;
import org.praisenter.slide.Slide;

/**
 * Represents a group of {@link RenderableComponent}s that are rendered together
 * to create one cached rendering. This saves a significant amount of time
 * during the rendering process of the entire {@link Slide}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class CachedRenderGroup implements RenderGroup {
	/** The list of components in this group */
	protected List<RenderableComponent> components;
	
	/** The cached rendering */
	protected BufferedImage image;
	
	/**
	 * Full constructor.
	 * @param components the components for this group.
	 * @param gc the graphics configuration
	 * @param w the slide width
	 * @param h the slide height
	 */
	public CachedRenderGroup(List<RenderableComponent> components, GraphicsConfiguration gc, int w, int h) {
		this.components = components;
		this.createCachedImage(gc, w, h);
	}
	
	/**
	 * Creates a cached image and renders the components to that image.
	 * @param gc the graphics configuration
	 * @param w the slide width
	 * @param h the slide height
	 */
	private void createCachedImage(GraphicsConfiguration gc, int w, int h) {
		this.image = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
		Graphics2D g = this.image.createGraphics();
		// use the configured quality
		Preferences preferences = Preferences.getInstance();
		g.setRenderingHints(preferences.getRenderingHints());
		// renders all the components to the given graphics
		for (RenderableComponent component : this.components) {
			component.render(g);
		}
		g.dispose();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.display.SlideComponentCache#render(java.awt.Graphics2D)
	 */
	public void render(Graphics2D g) {
		g.drawImage(this.image, 0, 0, null);
	}
}
