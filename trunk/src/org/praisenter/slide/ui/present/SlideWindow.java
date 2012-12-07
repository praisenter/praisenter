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
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideWindow {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlideWindow.class);
	
	/** The device this display is for */
	protected GraphicsDevice device;

	/** The window used to display the Display */
	protected JDialog dialog;

	/** True if the surface is visible */
	protected boolean visible;
	
	/** The rendering surface */
	protected SlideSurface surface;
	
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
		this.visible = false;
		this.fullScreen = fullScreen;
		this.overlay = overlay;
		
		// setup the dialog
		this.dialog = new JDialog();
		this.dialog.setUndecorated(true);
		// don't allow focus to transfer to the dialog
		this.dialog.setFocusable(false);
		this.dialog.setFocusableWindowState(false);
		this.dialog.setFocusTraversalKeysEnabled(false);
		// we need to enable per-pixel translucency if available
		this.dialog.getRootPane().setOpaque(false);
		
		// get the device's default config
		GraphicsConfiguration gc = this.device.getDefaultConfiguration();
		// get its position and dimensions
		Rectangle r = gc.getBounds();
		
		// set the dialog location to the top left corner of the
		// target display device
		this.dialog.setLocation(r.x, r.y);
		
		// a full screen display window has its size set to the
		// height and width of the device
		Dimension size = new Dimension(r.width, r.height);
		this.dialog.setMinimumSize(size);
		this.dialog.setPreferredSize(size);
		
		// setup the display surface
		Container container = this.dialog.getContentPane();
		container.setLayout(new BorderLayout());
		
		this.surface = new SlideSurface();
		container.add(this.surface, BorderLayout.CENTER);
		
		this.dialog.setAlwaysOnTop(overlay);
		
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
		if (!this.fullScreen) {
			this.setWindowSize(slide);
		}
		if (this.setVisible(true)) {
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
		if (this.setVisible(false)) {
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
		this.dialog.setMinimumSize(size);
		this.dialog.setPreferredSize(size);
		this.dialog.setLocation(x, y);
		
		// make sure the panel is resized to fit the layout
		this.dialog.pack();
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
			this.dialog.setBackground(new Color(0, 0, 0, 0));
			this.dialog.setVisible(true);
			LOGGER.info("Per-pixel translucency supported (best).");
		} else if (translucency == WindowTranslucency.TRANSLUCENT) {
			// no transition support but at least we can go ahead
			// and set the dialog to visible to save some time
			this.dialog.setOpacity(0.0f);
			this.dialog.setVisible(true);
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
	protected boolean setVisible(boolean flag) {
		WindowTranslucency translucency = this.getWindowTranslucency();
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(this.device);
		if (flag) {
			// showing the window depends on the translucency support
			if (translucency == WindowTranslucency.PERPIXEL_TRANSLUCENT) {
				// do nothing
			} else if (translucency == WindowTranslucency.TRANSLUCENT) {
				// see if the window is currently visible
				if (!this.visible) {
					// set the opacity to fully opaque
					// no transitions sadly
					this.dialog.setOpacity(1.0f);
				}
			} else {
				// see if the window is currently visible
				if (!this.visible) {
					// set the dialog to visible
					// no transitions sadly
					this.dialog.setVisible(true);
				}
			}
			// set the window visible flag
			this.visible = true;
			
			// if you re-send the display then make sure it goes on
			// top of all other windows
			this.dialog.toFront();
		} else {
			// hiding of the window depends on the translucency support
			if (translucency == WindowTranslucency.PERPIXEL_TRANSLUCENT) {
				// then we can just clear the surface
			} else if (translucency == WindowTranslucency.TRANSLUCENT) {
				// then we can set the opacity
				// no transitions sadly
				this.dialog.setOpacity(0.0f);
			} else {
				// then we have to set the window to not visible
				// no transitions sadly
				this.dialog.setVisible(false);
			}
			this.visible = false;
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
	 * Returns true if this window is currently visible.
	 * <p>
	 * The underlying surface may be visible but may have nothing
	 * rendered to it.  This method will only return true if there
	 * is something rendered to the surface.
	 * @return boolean
	 */
	public boolean isVisible() {
		return this.visible;
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
