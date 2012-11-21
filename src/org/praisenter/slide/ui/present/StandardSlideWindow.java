package org.praisenter.slide.ui.present;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;

import org.praisenter.slide.Slide;
import org.praisenter.transitions.TransitionAnimator;

/**
 * Represents a standard display window for a standard display surface.
 * <p>
 * By default the rendering surface will be the size of the given GraphicsDevice and will
 * be invisible.  The mechanism for invisibility differs depending on the supported
 * WindowTranslucency.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class StandardSlideWindow extends SlideWindow<StandardSlideSurface> {
	/**
	 * Creates a new display window for the given device.
	 * @param device the device
	 */
	public StandardSlideWindow(GraphicsDevice device) {
		super(device);

		// get the device's default config
		GraphicsConfiguration gc = this.device.getDefaultConfiguration();
		// get its position and dimensions
		Rectangle r = gc.getBounds();
		System.out.println(r);
		// a full screen display window has its size set to the
		// height and width of the device
		Dimension size = new Dimension(r.width, r.height);
		this.dialog.setMinimumSize(size);
		this.dialog.setPreferredSize(size);
		
		// setup the display surface
		Container container = this.dialog.getContentPane();
		container.setLayout(new BorderLayout());
		
		this.surface = new SlideDisplaySurface();
		container.add(this.surface, BorderLayout.CENTER);
		
		// make sure the panel is resized to fit the layout
		this.dialog.pack();
		
		// prepare for display
		this.prepareForDisplay();
	}
	
	/**
	 * Sends the new slide to this slide window using the given animator.
	 * @param slide the new slide to show
	 * @param animator the animator
	 */
	public void send(Slide slide, TransitionAnimator animator) {
		if (this.setVisible(true)) {
			this.surface.send(slide, animator);
		} else {
			// if transitions aren't supported
			this.surface.send(slide);
		}
	}
	
	/**
	 * Clears this display window using the given animator.
	 * @param animator the animator
	 */
	public void clear(TransitionAnimator animator) {
		if (this.setVisible(false)) {
			this.surface.clear(animator);
		} else {
			// if transitions aren't supported
			this.surface.clear();
		}
	}
}
