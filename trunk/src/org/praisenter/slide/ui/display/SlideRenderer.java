package org.praisenter.slide.ui.display;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.praisenter.slide.RenderableSlideComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.media.VideoMediaComponent;

/**
 * Class used to render a slide using an image cache.
 * <p>
 * This class will attempt to group components into groups based on their
 * order and whether they video.  Video components are what cause an issue
 * with rendering because they require constant updates, yet the rendering
 * of some components is expensive and we dont want to do this every time
 * the video is updated.  This class will attempt to cache the rendering
 * of components that can be pre-rendered and when ready will composite
 * all the images together.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideRenderer {
	public static final RenderingHints BEST_QUALITY;
	static {
		Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
		map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		map.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		BEST_QUALITY = new RenderingHints(map);
	}
	
	protected Slide slide;
	protected GraphicsConfiguration gc;
	
	protected List<SlideComponentCache> groups;
	
	public SlideRenderer(Slide slide, GraphicsConfiguration gc) {
		this.slide = slide;
		this.gc = gc;
		this.groups = this.createGroups();
	}
	
	public void render(Graphics2D g) {
		// render the groups in order
		for (int i = 0; i < this.groups.size(); i++) {
			SlideComponentCache group = this.groups.get(i);
			group.render(g);
		}
	}
	
	private List<SlideComponentCache> createGroups() {
		List<SlideComponentCache> groups = new ArrayList<SlideComponentCache>();
		// ensure the slide components are in order
		this.slide.sortComponentsByOrder();
		
		List<RenderableSlideComponent> components = new ArrayList<>();
		BufferedImage image = this.gc.createCompatibleImage(this.slide.getWidth(), this.slide.getHeight(), Transparency.TRANSLUCENT);
		
		int w = this.slide.getWidth();
		int h = this.slide.getHeight();
		
		// begin looping over the components and checking their types
		int size = this.slide.getComponentCount();
		for (int i = 0; i < size; i++) {
			SlideComponent c = this.slide.getComponent(i);
			if (c instanceof VideoMediaComponent) {
				if (components.size() > 0) {
					// then we need to stop and make a group with the current items
					SlideComponentCache group = new SlideComponentCacheGroup(components, this.gc, w, h);
					groups.add(group);
					
					components = new ArrayList<>();
					image = this.gc.createCompatibleImage(this.slide.getWidth(), this.slide.getHeight(), Transparency.TRANSLUCENT);
				}
				
				{
					SlideComponentCache group = new VideoComponentCache((VideoMediaComponent)c);
					groups.add(group);
				}
			} else if (c instanceof RenderableSlideComponent) {
				components.add((RenderableSlideComponent)c);
			}
		}
		// create a group of the remaining components
		if (components.size() > 0) {
			SlideComponentCache group = new SlideComponentCacheGroup(components, this.gc, w, h);
			groups.add(group);
		}
		
		return groups;
	}
}
