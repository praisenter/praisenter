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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.Rectangle;

import javax.swing.JDialog;

import org.apache.log4j.Logger;
import org.praisenter.slide.NotificationSlide;
import org.praisenter.slide.Slide;
import org.praisenter.transitions.TransitionAnimator;
import org.praisenter.transitions.Transitions;

/**
 * Represents a window that is used to display custom graphics.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SlideWindow extends JDialog {
	/** The version id */
	private static final long serialVersionUID = 3385134636780286237L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlideWindow.class);
	
	/** The device this display is for */
	protected GraphicsDevice device;

	/** The rendering surface */
	protected SlideSurface surface;
	
	/** True if the surface is visible */
	protected boolean visibleInternal;
	
	/** True if the window is always the device size */
	protected boolean fullScreen;
	
	/** True if the window is always on top of other windows */
	protected boolean overlay;
	
	/**
	 * Creates a new display window for the given device.
	 * @param device the device
	 * @param fullScreen true if the window should be full screen
	 * @param overlay true if the window should always be on top of other windows
	 */
	public SlideWindow(GraphicsDevice device, boolean fullScreen, boolean overlay) {
		// simple assignments
		this.device = device;
		this.fullScreen = fullScreen;
		this.overlay = overlay;
		this.visibleInternal = false;
		
		// setup the dialog
		this.setUndecorated(true);
		// don't allow focus to transfer to the dialog
		this.setFocusable(false);
		this.setFocusableWindowState(false);
		this.setFocusTraversalKeysEnabled(false);
		// we need to enable per-pixel translucency if available
		this.getRootPane().setOpaque(false);
		
		// get the device's default config
		GraphicsConfiguration gc = this.device.getDefaultConfiguration();
		// get its position and dimensions
		Rectangle r = gc.getBounds();
		
		// set the dialog location to the top left corner of the
		// target display device
		this.setLocation(r.x, r.y);
		
		// a full screen display window has its size set to the
		// height and width of the device
		Dimension size = new Dimension(r.width, r.height);
		this.setMinimumSize(size);
		this.setPreferredSize(size);
		
		// setup the display surface
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		
		this.surface = new SlideSurface();
		container.add(this.surface, BorderLayout.CENTER);
		
		this.setAlwaysOnTop(overlay);
		
		// make sure the panel is resized to fit the layout
		this.pack();
		
		// prepare for display
		this.prepareForDisplay();
	}
	
	/**
	 * Sends the new slide to this slide window using the given animator.
	 * @param slide the new slide to show
	 * @param animator the animator
	 */
	public void send(Slide slide, TransitionAnimator animator) {
		if (!this.fullScreen) {
			this.setWindowSize(slide);
		}
		if (this.setVisibleInternal(true)) {
			this.surface.send(slide, animator);
		} else {
			// if transitions aren't supported
			this.surface.send(slide, null);
		}
	}
	
	/**
	 * Clears this display window using the given animator.
	 * @param animator the animator
	 */
	public void clear(TransitionAnimator animator) {
		if (this.setVisibleInternal(false)) {
			this.surface.clear(animator);
		} else {
			// if transitions aren't supported
			this.surface.clear(null);
		}
	}
	
	/**
	 * Sets the window size to match the given slide size.
	 * @param slide the slide
	 */
	protected void setWindowSize(Slide slide) {
		// set the position
		GraphicsConfiguration gc = this.device.getDefaultConfiguration();
		// get its position and dimensions
		Rectangle r = gc.getBounds();
		int x = r.x;
		int y = r.y;
		
		// check for notification slide
		if (slide instanceof NotificationSlide) {
			NotificationSlide ns = (NotificationSlide)slide;
			x += ns.getX();
			y += ns.getY();
		}
		
		Dimension size = new Dimension(slide.getWidth(), slide.getHeight());
		// set the size
		this.setMinimumSize(size);
		this.setPreferredSize(size);
		this.setLocation(x, y);
		
		// make sure the panel is resized to fit the layout
		this.pack();
	}
	
	/**
	 * Returns the translucency support for this window.
	 * @return {@link WindowTranslucency}
	 */
	protected WindowTranslucency getWindowTranslucency() {
		if (this.device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
			return WindowTranslucency.PERPIXEL_TRANSLUCENT;
		} else if (this.device.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
			return WindowTranslucency.TRANSLUCENT;
		} else if (this.device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSPARENT)) {
			return WindowTranslucency.PERPIXEL_TRANSPARENT;
		} else {
			return null;
		}
	}
	
	/**
	 * This should be done at construction time of the {@link SlideWindow}.
	 * <p>
	 * This prepares the {@link SlideWindow} for display given the supported translucency of the
	 * device.
	 */
	protected void prepareForDisplay() {
		WindowTranslucency translucency = this.getWindowTranslucency();
		if (translucency == WindowTranslucency.PERPIXEL_TRANSLUCENT) {
			// this is the best since all transitions will work
			this.setBackground(new Color(0, 0, 0, 0));
			this.setVisible(true);
			LOGGER.info("Per-pixel translucency supported (best).");
		} else if (translucency == WindowTranslucency.TRANSLUCENT) {
			// no transition support but at least we can go ahead
			// and set the dialog to visible to save some time
			this.setOpacity(0.0f);
			this.setVisible(true);
			LOGGER.info("Only uniform translucency supported.");
		} else {
			// no support so don't show the dialog
			LOGGER.info("No translucency supported.");
		}
	}
	
	/**
	 * Sets the display window to visible or invisible.
	 * <p>
	 * Returns true if transitions are supported.
	 * @param flag true if the window should be visible
	 * @return boolean
	 */
	protected boolean setVisibleInternal(boolean flag) {
		WindowTranslucency translucency = this.getWindowTranslucency();
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(this.device);
		if (flag) {
			// showing the window depends on the translucency support
			if (translucency == WindowTranslucency.PERPIXEL_TRANSLUCENT) {
				// do nothing
			} else if (translucency == WindowTranslucency.TRANSLUCENT) {
				// see if the window is currently visible
				if (!this.visibleInternal) {
					// set the opacity to fully opaque
					// no transitions sadly
					this.setOpacity(1.0f);
				}
			} else {
				// see if the window is currently visible
				if (!this.visibleInternal) {
					// set the dialog to visible
					// no transitions sadly
					this.setVisible(true);
				}
			}
			// set the window visible flag
			this.visibleInternal = true;
			
			// if you re-send the display then make sure it goes on
			// top of all other windows
			this.toFront();
		} else {
			// hiding of the window depends on the translucency support
			if (translucency == WindowTranslucency.PERPIXEL_TRANSLUCENT) {
				// then we can just clear the surface
			} else if (translucency == WindowTranslucency.TRANSLUCENT) {
				// then we can set the opacity
				// no transitions sadly
				this.setOpacity(0.0f);
			} else {
				// then we have to set the window to not visible
				// no transitions sadly
				this.setVisible(false);
			}
			this.visibleInternal = false;
		}
		
		return transitionsSupported;
	}
	
	/**
	 * Returns the GraphicsDevice this {@link SlideWindow} is displaying on.
	 * @return GraphicsDevice
	 */
	public GraphicsDevice getDevice() {
		return this.device;
	}

	/**
	 * Returns true if this window is an overlay.
	 * <p>
	 * An overlay window is a window that will always be shown on top of
	 * other windows.
	 * @return boolean
	 */
	public boolean isOverlay() {
		return this.overlay;
	}
	
	/**
	 * Returns true if this window is a full screen window.
	 * <p>
	 * Windows that are NOT full screen will have their size adjusted
	 * to fit the size of the sent slides.
	 * @return boolean
	 */
	public boolean isFullScreen() {
		return this.fullScreen;
	}
}
