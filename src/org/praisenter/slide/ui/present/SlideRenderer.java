package org.praisenter.slide.ui.present;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;
import java.util.List;

import org.praisenter.slide.RenderableComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.media.VideoMediaComponent;

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
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideRenderer {
	/** The slide to render */
	protected Slide slide;
	
	/** The graphics configuration to generate compatible images */
	protected GraphicsConfiguration gc;
	
	/** The background component */
	protected SlideComponentCache background;
	
	/** The component groups */
	protected List<SlideComponentCache> groups;
	
	/**
	 * Creates a new slide renderer for the given {@link Slide} and
	 * graphics configuration.
	 * @param slide the slide to render
	 * @param gc the graphics configuration
	 */
	public SlideRenderer(Slide slide, GraphicsConfiguration gc) {
		this.slide = slide;
		this.gc = gc;
		this.createGroups();
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
			SlideComponentCache group = this.groups.get(i);
			group.render(g);
		}
	}
	
	/**
	 * Method used to split the components by order and type and arranges
	 * them into groups for faster rendering with videos.  In the case of
	 * no videos, there will be only one group.
	 */
	private void createGroups() {
		this.groups = new ArrayList<SlideComponentCache>();
		
		List<RenderableComponent> components = new ArrayList<>();
		
		int w = this.slide.getWidth();
		int h = this.slide.getHeight();
		
		RenderableComponent background = this.slide.getBackground();
		this.background = new SlideComponentCacheItem(background);
		
		// begin looping over the components and checking their types
		List<RenderableComponent> sComponents = this.slide.getComponents(RenderableComponent.class);
		for (RenderableComponent component : sComponents) {
			// check for video components
			if (component instanceof VideoMediaComponent) {
				// if we find one, see if we have any normal components queued up in the list
				if (components.size() > 0) {
					// then we need to stop and make a group with the current components
					SlideComponentCache group = new SlideComponentCacheGroup(components, this.gc, w, h);
					this.groups.add(group);
					// create a new group for the next set of components
					components = new ArrayList<RenderableComponent>();
				}
				
				// then create a video component group for the video
				{
					SlideComponentCache group = new SlideComponentCacheItem((VideoMediaComponent)component);
					this.groups.add(group);
				}
			// otherwise check if its renderable at all
			} else {
				// if so, then add it to the current list of components
				components.add(component);
			}
		}
		// create a group of the remaining components
		if (components.size() > 0) {
			SlideComponentCache group = new SlideComponentCacheGroup(components, this.gc, w, h);
			this.groups.add(group);
		}
	}
}
