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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.praisenter.animation.transitions.Transitions;
import org.praisenter.slide.AbstractPositionedSlide;
import org.praisenter.slide.Slide;

/**
 * Represents a window that is used to display custom graphics.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class PresentationWindow extends JDialog implements PresentationListener {
	/** The version id */
	private static final long serialVersionUID = 3385134636780286237L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(PresentationWindow.class);
	
	/** The device this display is for */
	protected GraphicsDevice device;

	/** The rendering surface */
	protected PresentationSurface surface;
	
	/** True if the window is always the device size */
	protected boolean fullScreen;
	
	/** True if the window is always on top of other windows */
	protected boolean overlay;
	
	// state
	
	/** The current state */
	protected PresentationState state;
		
	// send-wait-clear
	
	/** The queued send-wait-clear event if a current one is transitioning */
	protected SendWaitClearEvent queuedEvent;
	
	/** The wait timer for the send-wait-clear event */
	protected Timer waitTimer;
	
	/** The wait timer lock */
	protected Object waitTimerLock = new Object();
	
	/**
	 * Creates a new display window for the given device.
	 * @param device the device
	 * @param fullScreen true if the window should be full screen
	 * @param overlay true if the window should always be on top of other windows
	 */
	public PresentationWindow(GraphicsDevice device, boolean fullScreen, boolean overlay) {
		// simple assignments
		this.device = device;
		this.fullScreen = fullScreen;
		this.overlay = overlay;
		
		// setup the dialog
		this.setUndecorated(true);
		// don't allow focus to transfer to the dialog
		this.setAutoRequestFocus(false);
		this.setFocusable(false);
		this.setFocusableWindowState(false);
		this.setFocusTraversalKeysEnabled(false);
		
		WindowTranslucency translucency = this.getWindowTranslucency();
		if (translucency == WindowTranslucency.PERPIXEL_TRANSLUCENT) {
			// this is the best since all transitions will work
			this.setBackground(new Color(0, 0, 0, 0));
			LOGGER.info("Per-pixel translucency supported (best).");
		} else if (translucency == WindowTranslucency.TRANSLUCENT) {
			LOGGER.info("Only uniform translucency supported.");
		} else if (translucency == WindowTranslucency.PERPIXEL_TRANSPARENT) {
			LOGGER.info("Only per-pixel transparency supported (only shaped windows possible).");
		} else {
			LOGGER.info("Translucency/Transparency not supported.");
		}
		
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
		
		this.surface = new PresentationSurface();
		this.surface.addPresentListener(this);
		this.addWindowListener(this.surface);
		
		container.add(this.surface, BorderLayout.CENTER);
		
		this.setAlwaysOnTop(overlay);
		
		// make sure the panel is resized to fit the layout
		this.pack();
	}
	
	/**
	 * Executes the given event.
	 * @param event the event
	 */
	public void execute(SendEvent event) {
		// make sure its not a send-wait-clear event
		if (event instanceof SendWaitClearEvent) {
			this.execute((SendWaitClearEvent)event);
		} else {
			this.surface.execute(event);
		}
	}
	
	/**
	 * Executes the given event.
	 * @param event the event
	 */
	public void execute(SendWaitClearEvent event) {
		// make sure transitions are supported
		if (!Transitions.isTransitionSupportAvailable(this.device)) {
			// if transitions aren't supported, null out the animator
			event.animator = null;
			event.outAnimator = null;
		}
		
		// make sure we are listening to events on this window
		synchronized (this.waitTimerLock) {
			// make sure there is no queued event
			this.queuedEvent = null;
			
			// only do this if we have wait for transitions enabled and this window is not a fullscreen window
			if (!this.fullScreen && event.getConfiguration().isWaitForTransitionEnabled()) {
				// we need to check the position and size of the slide against the previous since they could
				// be different. If they are different the transitions will not work (what are we transitioning
				// at that point?). So instead, we need to end the current transition normally (just quickly)
				// and begin the new send
				boolean waitForCurrent = !this.isSizePositionEqual(event.slide);
				
				if (waitForCurrent) {
					LOGGER.trace("Size/Position not equal.");
					if ((this.state == PresentationState.IN || this.state == PresentationState.WAIT)) {
						// in either case, stop the current wait timer, set its initial delay to zero,
						// and execute it. In the case of the in transition, the timer will execute a 
						// clear event which will be queued. In the case of the wait event the clear event
						// will execute immediately. In both cases, when the clear event completes we
						// begin the queued event.
						if (this.state == PresentationState.IN) {
							LOGGER.trace("In transition executing. Setting the wait timer initial delay: 0.");
						} else {
							LOGGER.trace("Wait period in progress. Executing clear event.");
						}
						
						this.queuedEvent = event;
						
						if (this.waitTimer != null) {
							this.waitTimer.stop();
							this.waitTimer.setInitialDelay(0);
							this.waitTimer.start();
							// don't do anything else just yet
							return;
						}
						// otherwise the wait timer was not setup (this can happen if the current
						// event is just a send. In this case we need to just queue it up normally
						LOGGER.trace("Timer is null. Queueing send normally.");
					} else {
						// if the current state is CLEAR or OUT just queue the next send normally
						LOGGER.trace("Presentation state is CLEAR, OUT, or SHOWING. Queueing send normally.");
					}
				} else {
					LOGGER.trace("Size/Position equal. Queueing send normally.");
				}
			}
			
			this.surface.execute(event);
		}
		
	}
	
	/**
	 * Executes the given event.
	 * @param event the event
	 */
	public void execute(ClearEvent event) {
		// make sure transitions are supported
		if (!Transitions.isTransitionSupportAvailable(this.device)) {
			// if transitions aren't supported, null out the animator
			event.animator = null;
		}
		
		// perform the clear
		this.surface.execute(event);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.presentation.PresentationListener#inTransitionBegin(org.praisenter.presentation.SendEvent)
	 */
	@Override
	public void inTransitionBegin(SendEvent event) {
		this.state = PresentationState.IN;
		
		// we need to wait until the in transition begins before
		// we can move/resize the window since the event could
		// have been queued waiting on an existing event to finish
		if (!this.fullScreen) {
			this.setWindowSize(event.slide);
		}
		
		// its possible that when an out transition ends that there
		// is still an in transition in the queue.  In which case
		// we need to ensure the window is visible in this case
		if (!this.isVisible()) {
			this.setVisible(true);
		}
		
		// if you re-send the display then make sure it goes on
		// top of all other windows
		this.toFront();
		
		// stop the wait timer if its running
		synchronized (this.waitTimerLock) {
			if (this.waitTimer != null && this.waitTimer.isRunning()) {
				this.waitTimer.stop();
				LOGGER.trace("Wait timer stopped due to an 'In' transition beginning.");
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.presentation.PresentationListener#outTransitionBegin(org.praisenter.presentation.ClearEvent)
	 */
	@Override
	public void outTransitionBegin(ClearEvent event) {
		this.state = PresentationState.OUT;
		
		// stop the wait timer if its running
		synchronized (this.waitTimerLock) {
			if (this.waitTimer != null && this.waitTimer.isRunning()) {
				this.waitTimer.stop();
				LOGGER.trace("Wait timer stopped due to an 'Out' transition beginning.");
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.presentation.PresentationListener#eventDropped(org.praisenter.presentation.PresentationEvent)
	 */
	@Override
	public void eventDropped(PresentationEvent event) {
		// nothing to do here
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.presentation.PresentationListener#inTransitionComplete(org.praisenter.presentation.SendEvent)
	 */
	@Override
	public void inTransitionComplete(SendEvent event) {
		if (event instanceof SendWaitClearEvent) {
			this.state = PresentationState.WAIT;
			
			// begin the wait timer when the "in" transition completes
			final SendWaitClearEvent swcEvent = (SendWaitClearEvent)event;
			
			// setup the wait timer
			synchronized (this.waitTimerLock) {
				this.waitTimer = new Timer(swcEvent.waitPeriod, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// the wait timer should always be non null if the code gets here
						synchronized (waitTimerLock) {
							if (waitTimer != null) {
								// make sure the timer is ended (it should end anyway since
								// it has setRepeats(false), but for good measure
								waitTimer.stop();
							}
						}
						ClearEvent event = new ClearEvent(
								swcEvent.configuration,
								swcEvent.outAnimator);
						// once the wait period is up, then execute the clear operation
						execute(event);
						LOGGER.trace("Wait timer ended. Beginning clear event.");
					}
				});
				this.waitTimer.setRepeats(false);
				this.waitTimer.start();
			}
			
			LOGGER.trace("Wait timer started after an 'In' transition has completed.");
		} else {
			this.state = PresentationState.SHOWING;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.present.PresentListener#outTransitionComplete(org.praisenter.slide.ui.present.ClearEvent)
	 */
	@Override
	public void outTransitionComplete(ClearEvent event) {
		this.state = PresentationState.CLEAR;
		
		// when the out transition is complete, hide the window
		this.setVisible(false);
		
		// when an out transition ends we need to check if a queued event was stored
		if (this.queuedEvent != null) {
			SendWaitClearEvent qevent = this.queuedEvent;
			this.queuedEvent = null;
			this.execute(qevent);
			LOGGER.trace("Executed queued event.");
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
		if (slide instanceof AbstractPositionedSlide) {
			AbstractPositionedSlide ps = (AbstractPositionedSlide)slide;
			x += ps.getX();
			y += ps.getY();
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
	 * Returns true if the slide has the same position and size as this window.
	 * @param slide the slide
	 * @return boolean
	 */
	protected boolean isSizePositionEqual(Slide slide) {
		int sx = 0;
		int sy = 0;
		int sw = slide.getWidth();
		int sh = slide.getHeight();
		if (slide instanceof AbstractPositionedSlide) {
			AbstractPositionedSlide as = (AbstractPositionedSlide)slide;
			sx = as.getX();
			sy = as.getY();
		}
		
		int wx = this.getX();
		int wy = this.getY();
		// account for device offset
		Rectangle bounds = this.device.getDefaultConfiguration().getBounds();
		wx -= bounds.x;
		wy -= bounds.y;
		int ww = this.getWidth();
		int wh = this.getHeight();
		
		LOGGER.trace("Slide[" + sx + "," + sy + "," + sw + "," + sh + "] - Window[" + wx + "," + wy + "," + ww + "," + wh + "]");
		return sx == wx && sy == wy && sw == ww && sh == wh;
	}
	
	/**
	 * Returns the GraphicsDevice this {@link PresentationWindow} is displaying on.
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
