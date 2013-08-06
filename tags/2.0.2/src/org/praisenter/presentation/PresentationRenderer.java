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
package org.praisenter.presentation;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.praisenter.slide.RenderableComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.text.DateTimeComponent;

/**
 * Class used to render a slide using an image cache.
 * <p>
 * This class will attempt to group components into groups based on their
 * order and whether they are video.  Video components are what cause an issue
 * with rendering because they require constant updates, but the rendering
 * of some components is expensive and we dont want to do this every time
 * the video is updated.  This class will attempt to cache the rendering
 * of components that can be pre-rendered and when ready will composite
 * all the images together.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class PresentationRenderer {
	/** The background component */
	protected RenderLayer background;
	
	/** The component groups */
	protected List<RenderLayer> groups;
	
	/**
	 * Creates a new slide renderer for the given {@link Slide}.
	 * @param slide the slide to render
	 */
	public PresentationRenderer(Slide slide) {
		this.createGroups(slide);
	}
	
	/**
	 * Renders this slide to the given graphics object.
	 * @param g the graphics object to render to
	 * @param background true if the background should be rendered
	 */
	public void render(Graphics2D g, boolean background) {
		if (background) {
			this.background.render(g);
		}
		// render the groups in order
		for (int i = 0; i < this.groups.size(); i++) {
			RenderLayer group = this.groups.get(i);
			group.render(g);
		}
	}
	
	/**
	 * Method used to split the components by order and type and arranges
	 * them into groups for faster rendering with videos.  In the case of
	 * no videos, there will be only one group.
	 * @param slide the slide to render
	 */
	private void createGroups(Slide slide) {
		this.groups = new ArrayList<RenderLayer>();
		
		List<RenderableComponent> components = new ArrayList<>();
		
		RenderableComponent background = slide.getBackground();
		this.background = new RenderItem(background);
		
		// begin looping over the components and checking their types
		List<RenderableComponent> sComponents = slide.getComponents(RenderableComponent.class);
		for (RenderableComponent component : sComponents) {
			// check for video components
			if (component instanceof VideoMediaComponent) {
				// if we find one, see if we have any normal components queued up in the list
				if (components.size() > 0) {
					// then we need to stop and make a group with the current components
					RenderLayer group = new RenderGroup(components);
					this.groups.add(group);
					// create a new group for the next set of components
					components = new ArrayList<RenderableComponent>();
				}
				
				// then create a video component group for the video
				{
					RenderLayer group = new RenderItem(component);
					this.groups.add(group);
				}
			// otherwise check if its renderable at all
			} else if (component instanceof DateTimeComponent) {
				DateTimeComponent c = (DateTimeComponent)component;
				// see if its an updating date-time component
				if (c.isDateTimeUpdateEnabled()) {
					// add to its own group
					RenderLayer group = new RenderItem(component);
					this.groups.add(group);
				} else {
					// add as normal if it doesnt need to update
					components.add(component);
				}
			} else {
				// if so, then add it to the current list of components
				components.add(component);
			}
		}
		// create a group of the remaining components
		if (components.size() > 0) {
			RenderLayer group = new RenderGroup(components);
			this.groups.add(group);
		}
	}
}
