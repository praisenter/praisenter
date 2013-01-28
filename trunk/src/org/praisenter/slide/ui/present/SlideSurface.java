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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerConfiguration;
import org.praisenter.media.MediaPlayerFactory;
import org.praisenter.media.PlayableMedia;
import org.praisenter.media.VideoMediaPlayerListener;
import org.praisenter.preferences.Preferences;
import org.praisenter.slide.GenericComponent;
import org.praisenter.slide.RenderableComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.PlayableMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.Transition.Type;
import org.praisenter.transitions.TransitionAnimator;

/**
 * Surface for rendering slides using transitions.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SlideSurface extends JPanel implements VideoMediaPlayerListener, WindowListener {
	/** The version id */
	private static final long serialVersionUID = 957958229210490257L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlideSurface.class);
	
	/** The date/time update interval in milliseconds */
	private static final int CLOCK_UPDATE_INTERVAL = 250;
	
	// current slide
	
	/** The current slide being displayed */
	protected Slide currentSlide;
	
	/** The current slide's background media player; can be null */
	protected MediaPlayer<?> currentBackgroundMediaPlayer;
	
	/** The current slide's list of media players (excluding the background media player) */
	protected List<MediaPlayer<?>> currentMediaPlayers;
	
	/** The current slide's renderer */
	protected SlideRenderer currentRenderer;

	/** True if the current slide has playable media */
	protected boolean currentHasPlayableMedia;
	
	/** True if the current slide has an updating date time component */
	protected boolean currentHasUpdatingDateTime;
	
	// in-coming slide
	
	/** The incoming slide */
	protected Slide inSlide;
	
	/** The incoming slide's background media player; can be null */
	protected MediaPlayer<?> inBackgroundMediaPlayer;
	
	/** The incoming slide's list of media players (excluding the background media player) */
	protected List<MediaPlayer<?>> inMediaPlayers;
	
	/** The incoming slide's renderer */
	protected SlideRenderer inRenderer;

	/** True if the in-coming slide has playable media */
	protected boolean inHasPlayableMedia;
	
	/** True if the in-coming slide has an updating date time component */
	protected boolean inHasUpdatingDateTime;
	
	// transitioning

	/** The current event being processed */
	protected PresentEvent event;
	
	/** The transition to apply from display to display */
	protected TransitionAnimator animator;
	
	/** True if the background should be transitioned */
	protected boolean transitionBackground;
	
	// state
	
	/** The cached before image */
	protected BufferedImage image0;
	
	/** The cached current image */
	protected BufferedImage image1;
	
	/** True if the panel is clear */
	protected boolean clear;
	
	/** True if a repaint has been issued */
	protected boolean repaintIssued;
	
	/** True if the transition is complete */
	protected boolean transitionComplete;

	/** The transition complete lock */
	protected Object transitionCompleteLock;
	
	/** The transition wait thread */
	protected TransitionWaitThread transitionWaitThread;

	/** A timer for updating the date/time */
	protected Timer dateTimeTimer;
	
	/**
	 * Default constructor.
	 */
	protected SlideSurface() {
		super();
		this.setOpaque(false);
		this.setBackground(new Color(0, 0, 0, 0));
		
		this.image0 = null;
		this.image1 = null;
		
		this.currentSlide = null;
		this.currentBackgroundMediaPlayer = null;
		this.currentMediaPlayers = new ArrayList<>();
		this.currentHasPlayableMedia = false;
		this.currentHasUpdatingDateTime = false;
		
		this.inSlide = null;
		this.inBackgroundMediaPlayer = null;
		this.inMediaPlayers = new ArrayList<>();
		this.inHasPlayableMedia = false;
		this.inHasUpdatingDateTime = false;
		
		this.animator = null;
		this.repaintIssued = false;
		
		this.clear = true;
		this.transitionComplete = true;
		this.transitionCompleteLock = new Object();
		this.transitionWaitThread = new TransitionWaitThread();
		this.transitionWaitThread.start();
		
		this.dateTimeTimer = new Timer(0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		// every second
		this.dateTimeTimer.setDelay(CLOCK_UPDATE_INTERVAL);
	}
	
	/**
	 * Executes the given send event.
	 * @param event the event
	 */
	public void execute(SendEvent event) {
		Preferences preferences = Preferences.getInstance();
		
		// see if we should wait for the existing transition before
		// sending this slide
		if (preferences.isWaitForTransitionEnabled()) {
			// queue up the event
			TransitionWaitThread thread = this.getTransitionWaitThread();
			thread.queue(event);
		} else {
			// if we don't need to wait, then just stop the old transition
			if (this.animator != null) {
				this.animator.stop();
			}
			
			// if the transition is not complete then complete it
			synchronized (this.transitionCompleteLock) {
				if (!this.transitionComplete) {
					// if the transition didnt complete then
					// complete it
					this.onInTransitionComplete();
				}
			}
			
			// execute the event
			this.executeSendEvent(event);
		}
	}
	
	/**
	 * Executes the given clear event.
	 * @param event the event
	 */
	public void execute(ClearEvent event) {
		Preferences preferences = Preferences.getInstance();
		
		// see if we should wait for the existing transition before
		// sending this slide
		if (preferences.isWaitForTransitionEnabled()) {
			// queue up the event
			TransitionWaitThread thread = this.getTransitionWaitThread();
			thread.queue(event);
		} else {
			// if we don't need to wait, then just stop the old transition
			if (this.animator != null) {
				this.animator.stop();
			}
			
			// if the transition is not complete then complete it
			synchronized (this.transitionCompleteLock) {
				if (!this.transitionComplete) {
					this.onOutTransitionComplete();
				}
			}
			
			// if the current transition is complete
			if (!this.clear) {
				// we aren't already clear, so send the clear command
				this.executeClearEvent(event);
			} else {
				LOGGER.debug("Already clear. Dropping event (not notified).");
			}
		}
	}
	
	/**
	 * Executes a send event.
	 * @param event the send event
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void executeSendEvent(SendEvent event) {
		Preferences preferences = Preferences.getInstance();
		
		Slide slide = event.getSlide();
		TransitionAnimator animator = event.getAnimator();
		
		this.notifyInTransitionBegin(event);
		
		// see if we have any playable media
		this.inSlide = slide;
		this.inRenderer = new SlideRenderer(slide, getGraphicsConfiguration());
		this.inMediaPlayers.clear();
		this.transitionBackground = true;
		RenderableComponent background = slide.getBackground();
		List<PlayableMediaComponent<?>> playableMediaComponents = slide.getPlayableMediaComponents();
		this.inHasPlayableMedia = false;
		this.inHasUpdatingDateTime = this.hasUpdatingDateTimeComponent(slide);
		
		// we will only NOT transition the background IF both slides have a video background component AND
		// they are the same video
		// check if there is a previous slide and that it has a background
		if (this.currentSlide != null) {
			// check for video media components
			if (this.currentSlide.getBackground() instanceof VideoMediaComponent) {
				// see if the incoming slide background is also an video
				if (background instanceof VideoMediaComponent) {
					// if both are video media components, we need to check if they are the same video
					VideoMediaComponent oC = (VideoMediaComponent)this.currentSlide.getBackground();
					VideoMediaComponent nC = (VideoMediaComponent)slide.getBackground();
					if (!this.isTransitionRequired(oC, nC)) {
						// they are the same video (and both are visible), so we should not transition the background
						// we can attach the new media component as a listener to the current 
						// media player (to update its images as the video plays)
						this.currentBackgroundMediaPlayer.addMediaPlayerListener(nC);
						this.transitionBackground = !preferences.isSmartVideoTransitionsEnabled();
						this.inHasPlayableMedia = true;
					}
				}
			// check for image media components
			} else if (this.currentSlide.getBackground() instanceof ImageMediaComponent) {
				// see if the incoming slide background is also an image
				if (background instanceof ImageMediaComponent) {
					// if both are image media components, we need to check if they are the same image
					ImageMediaComponent oC = (ImageMediaComponent)this.currentSlide.getBackground();
					ImageMediaComponent nC = (ImageMediaComponent)slide.getBackground();
					if (!this.isTransitionRequired(oC, nC)) {
						this.transitionBackground = !preferences.isSmartImageTransitionsEnabled();
						// if we transitioned the background in the last send, we need to make sure we
						// re-render the current slide without the background. if we don't do this
						// image0 still contains the background and it will appear as if we are
						// still transitioning the background
						SlideSurface.renderSlide(this.currentRenderer, false, this.image1);
					} else {
						// if the images are not the same then we need to transition them, but we need
						// to make sure that the last time we transitioned, the background was present
						SlideSurface.renderSlide(this.currentRenderer, true, this.image1);
					}
				}
			}
		}
				
		// we need to create a player for each playable media component and attach them as listeners and this
		// surface as a listener
		// don't create a player for the IN slide if we aren't transitioning the background
		if (background instanceof VideoMediaComponent && this.transitionBackground) {
			VideoMediaComponent bg = (VideoMediaComponent)background;
			// make sure the video is visible
			if (bg.isVideoVisible()) {
				MediaPlayerFactory<?> factory = MediaLibrary.getMediaPlayerFactory(bg.getMedia().getClass());
				MediaPlayer player = factory.createMediaPlayer();
				player.setMedia(bg.getMedia());
				player.addMediaPlayerListener(bg);
				player.addMediaPlayerListener(this);
				
				MediaPlayerConfiguration conf = new MediaPlayerConfiguration();
				conf.setLoopEnabled(bg.isLoopEnabled());
				conf.setAudioMuted(bg.isAudioMuted());
				player.setConfiguration(conf);
				
				this.inBackgroundMediaPlayer = player;
				this.inHasPlayableMedia = true;
			}
		}
		for (PlayableMediaComponent<?> component : playableMediaComponents) {
			// check for non-visible video media
			if (component instanceof VideoMediaComponent) {
				VideoMediaComponent vc = (VideoMediaComponent)component;
				if (!vc.isVideoVisible()) {
					// if the video is not visible, then just skip this component
					continue;
				}
			}
			PlayableMedia media = component.getMedia();
			MediaPlayerFactory<?> factory = MediaLibrary.getMediaPlayerFactory(media.getClass());
			MediaPlayer player = factory.createMediaPlayer();
			player.setMedia(media);
			player.addMediaPlayerListener(component);
			player.addMediaPlayerListener(this);
			
			MediaPlayerConfiguration conf = new MediaPlayerConfiguration();
			conf.setLoopEnabled(component.isLoopEnabled());
			conf.setAudioMuted(component.isAudioMuted());
			player.setConfiguration(conf);
			
			this.inMediaPlayers.add(player);
			this.inHasPlayableMedia = true;
		}
		
		this.clear = false;
		this.animator = animator;
		this.repaintIssued = false;
		
		// make sure our offscreen images are still the correct size
		this.image0 = SlideSurface.validateOffscreenImage(this.image0, this);
		this.image1 = SlideSurface.validateOffscreenImage(this.image1, this);
		
		// render whats currently in image1 to image0
		// this saves the last display's rendering so we
		// can apply transitions
		SlideSurface.copyImage(this.image1, this.image0);
		
		// paint the display to the image
		SlideSurface.renderSlide(this.inRenderer, this.transitionBackground, this.image1);
		
		// make sure the transition is not null
		if (this.animator != null) {
			synchronized (this.transitionCompleteLock) {
				this.transitionComplete = false;
			}
		} else {
			// notify that the in transition has completed (since there wasn't one)
			synchronized (this.transitionCompleteLock) {
				this.onInTransitionComplete();
			}
		}
		
		// begin the media players
		if (this.inBackgroundMediaPlayer != null) {
			this.inBackgroundMediaPlayer.play();
		}
		for (MediaPlayer<?> player : this.inMediaPlayers) {
			player.play();
		}
		// begin the time update (if neccessary)
		if (this.inHasUpdatingDateTime || this.currentHasUpdatingDateTime) {
			if (!this.dateTimeTimer.isRunning()) {
				this.dateTimeTimer.start();
			}
		}
		
		if (this.animator != null) {
			// start it
			this.animator.start(this);
		} else {
			// refresh the display
			this.repaint();
		}
	}
	
	/**
	 * Executes a clear event.
	 * @param event the clear event
	 */
	private void executeClearEvent(ClearEvent event) {
		TransitionAnimator animator = event.getAnimator();
		
		this.notifyOutTransitionBegin(event);
		
		// set the transition
		this.animator = animator;
		
		// check the current slide background type
		if (this.currentSlide.getBackground() instanceof ImageMediaComponent
		 && Preferences.getInstance().isSmartImageTransitionsEnabled()) {
			// if the current slide background type is image and we have smart
			// image transitions enabled, its possible that image0 does not
			// contain the background. So we need to re-render the image
			// with the background to ensure the clear includes the background
			SlideSurface.renderSlide(this.currentRenderer, true, this.image0);
		}
		
		// on a clear operation we need to transition the background
		this.transitionBackground = true;
		this.repaintIssued = false;
		
		// make sure the transition is not null
		if (this.animator != null) {
			synchronized (this.transitionCompleteLock) {
				this.transitionComplete = false;
			}
			// start it
			this.animator.start(this);
		} else {
			synchronized (this.transitionCompleteLock) {
				this.onOutTransitionComplete();
			}
			this.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#repaint()
	 */
	@Override
	public void repaint() {
		// we need to handle repaint events from the transition timers
		// and any other system/Java generated repaints
		this.coalescingRepaint();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.VideoMediaPlayerListener#onVideoImage(java.awt.image.BufferedImage)
	 */
	@Override
	public void onVideoImage(BufferedImage image) {
		// we need to handle repaint events for any playing video
		this.coalescingRepaint();
	}
	
	/**
	 * Custom simple repaint coalescing to help reduce the number
	 * of repaint calls.
	 */
	private void coalescingRepaint() {
		if (!this.repaintIssued) {
			this.repaintIssued = true;
			super.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		// update the images if necessary
		if ((this.currentHasPlayableMedia || this.currentHasUpdatingDateTime) && this.currentSlide != null) {
			SlideSurface.renderSlide(this.currentRenderer, this.transitionBackground, this.image0);
		}
		if ((this.inHasPlayableMedia || this.inHasUpdatingDateTime) && this.inSlide != null) {
			SlideSurface.renderSlide(this.inRenderer, this.transitionBackground, this.image1);
		}
		
		if (!this.transitionBackground && this.currentSlide != null) {
			// then render the background of the slide
			this.currentSlide.getBackground().render(g2d);
		}
		
		if (this.animator != null) {
			Transition transition = this.animator.getTransition();
			if (this.animator.isComplete()) {
				if (transition.getType() == Type.IN) {
					// if the transition is not complete then complete it
					synchronized (this.transitionCompleteLock) {
						if (!this.transitionComplete) {
							this.onInTransitionComplete();
						}
					}
					g2d.drawImage(this.image0, 0, 0, null);
				} else {
					if (!this.clear) {
						this.onOutTransitionComplete();
					}
					// draw nothing
				}
			} else {
				transition.render(g2d, this.image0, this.image1, this.animator.getPercentComplete());
			}
		} else {
			if (!this.clear) {
				g2d.drawImage(this.image0, 0, 0, null);
			}
		}
		
		this.repaintIssued = false;
	}
	
	/**
	 * Called when an in transition has completed.
	 * <p>
	 * This method copies the state of the incoming slide to the current slide.
	 */
	private void onInTransitionComplete() {
		// copy the current image from the incoming slide to the
		// current slide image to avoid a small flicker
		copyImage(this.image1, this.image0);
		
		// see if the incoming slide has playable media
		if (this.inHasPlayableMedia) {
			// see if we were transitioning the background
			if (this.transitionBackground) {
				// if we were transitioning the background, then we need to release the current background
				// media player and set the current media player to the incoming media player
				if (this.currentBackgroundMediaPlayer != null) {
					this.currentBackgroundMediaPlayer.release();
				}
				this.currentBackgroundMediaPlayer = this.inBackgroundMediaPlayer;
			} else {
				// if we were NOT transitioning the background, then we need to do some house cleaning
				// remove the listener for the current slide
				if (this.currentSlide != null && this.currentSlide.getBackground() instanceof VideoMediaComponent) {
					// we attach a listener to the background media player for the current and incoming slide
					// when the transition is over we need to remove the current slide as a listener so that
					// we aren't sending events to it any more
					this.currentBackgroundMediaPlayer.removeMediaPlayerListener((VideoMediaComponent)this.currentSlide.getBackground());
					// there should be a maximum of 2 media player listeners per player
				}
			}
		}
		// release any current media players and flip the lists
		for (MediaPlayer<?> player : this.currentMediaPlayers) {
			player.release();
		}
		this.currentMediaPlayers.clear();
		// move the "in" media players to the "current" media players
		List<MediaPlayer<?>> players = this.currentMediaPlayers;
		this.currentMediaPlayers = this.inMediaPlayers;
		this.inMediaPlayers = players;
		
		this.currentRenderer = this.inRenderer;
		this.currentSlide = this.inSlide;
		this.currentHasPlayableMedia = this.inHasPlayableMedia;
		this.currentHasUpdatingDateTime = this.inHasUpdatingDateTime;
		
		this.inBackgroundMediaPlayer = null;
		this.inHasPlayableMedia = false;
		this.inHasUpdatingDateTime = false;
		this.inRenderer = null;
		this.inSlide = null;
		
		if (!this.currentHasUpdatingDateTime) {
			if (this.dateTimeTimer.isRunning()) {
				this.dateTimeTimer.stop();
			}
		}
		
		synchronized (this.transitionCompleteLock) {
			this.transitionComplete = true;
			this.transitionCompleteLock.notifyAll();
		}
		
		this.notifyInTransitionComplete();
		
		this.event = null;
		this.animator = null;
	}
	
	/**
	 * Called when an out transition has completed.
	 * <p>
	 * This method clears the state of the current slide.
	 */
	private void onOutTransitionComplete() {
		// clear the image
		clearImage(this.image0);
		clearImage(this.image1);
		
		// stop any media players
		if (this.currentBackgroundMediaPlayer != null) {
			this.currentBackgroundMediaPlayer.release();
		}
		for (MediaPlayer<?> player : this.currentMediaPlayers) {
			player.release();
		}
		this.currentMediaPlayers.clear();
		
		if (this.dateTimeTimer.isRunning()) {
			this.dateTimeTimer.stop();
		}
		
		this.currentBackgroundMediaPlayer = null;
		this.currentHasPlayableMedia = false;
		this.currentHasUpdatingDateTime = false;
		this.currentRenderer = null;
		this.currentSlide = null;
		
		this.clear = true;
		
		synchronized (this.transitionCompleteLock) {
			this.transitionComplete = true;
			this.transitionCompleteLock.notifyAll();
		}
		
		this.notifyOutTransitionComplete();
		
		this.event = null;
		this.animator = null;
	}

	/**
	 * Adds the given {@link PresentListener} to this surface.
	 * @param listener the listener
	 */
	public void addPresentListener(PresentListener listener) {
		if (this.containsPresentListener(listener)) {
			return;
		}
		this.listenerList.add(PresentListener.class, listener);
	}
	
	/**
	 * Returns true if the given listener has already been added.
	 * @param listener the listener
	 * @return boolean
	 */
	private boolean containsPresentListener(PresentListener listener) {
		PresentListener[] listeners = this.listenerList.getListeners(PresentListener.class);
		for (PresentListener l : listeners) {
			if (l == listener) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes the given {@link PresentListener} from this surface.
	 * @param listener the listener
	 */
	public void removePresentListener(PresentListener listener) {
		this.listenerList.remove(PresentListener.class, listener);
	}
	
	/**
	 * Notifies all {@link PresentListener}s of an in transition completing.
	 */
	private void notifyInTransitionComplete() {
		PresentListener[] listeners = this.getListeners(PresentListener.class);
		SendEvent event = (SendEvent)this.event;
		for (PresentListener listener : listeners) {
			listener.inTransitionComplete(event);
		}
	}
	
	/**
	 * Notifies all {@link PresentListener}s of an out transition completing.
	 */
	private void notifyOutTransitionComplete() {
		PresentListener[] listeners = this.getListeners(PresentListener.class);
		ClearEvent event = (ClearEvent)this.event;
		for (PresentListener listener : listeners) {
			listener.outTransitionComplete(event);
		}
	}
	
	/**
	 * Notifies all {@link PresentListener}s of an event being dropped.
	 * @param event the event that was dropped
	 */
	private void notifyEventDropped(PresentEvent event) {
		PresentListener[] listeners = this.getListeners(PresentListener.class);
		for (PresentListener listener : listeners) {
			listener.eventDropped(event);
		}
	}
	
	/**
	 * Notifies all {@link PresentListener}s of an in transition beginning.
	 * @param event the event that is beginning
	 */
	private void notifyInTransitionBegin(SendEvent event) {
		// store the event for later use in the end events
		this.event = event;
		PresentListener[] listeners = this.getListeners(PresentListener.class);
		for (PresentListener listener : listeners) {
			listener.inTransitionBegin(event);
		}
	}
	
	/**
	 * Notifies all {@link PresentListener}s of an out transition beginning.
	 * @param event the event that is beginning
	 */
	private void notifyOutTransitionBegin(ClearEvent event) {
		// store the event for later use in the end events
		this.event = event;
		PresentListener[] listeners = this.getListeners(PresentListener.class);
		for (PresentListener listener : listeners) {
			listener.outTransitionBegin(event);
		}
	}
	
	/**
	 * Returns true if a transition is required between the two components.
	 * @param mc1 the first component
	 * @param mc2 the second component
	 * @return boolean
	 */
	private boolean isTransitionRequired(VideoMediaComponent mc1, VideoMediaComponent mc2) {
		// the media must be the same
		if (mc1.isVideoVisible() && mc2.isVideoVisible()) {
			if (mc1.getMedia() != null && mc2.getMedia() != null) {
				if (!mc1.getMedia().equals(mc2.getMedia())) {
					// the media items are not the same so we have to transition
					return true;
				}
			} else if (mc1.getMedia() != null || mc2.getMedia() != null) {
				// one is not null
				return true;
			}
		} else if (mc1.isVideoVisible() || mc2.isVideoVisible()) {
			// one is visible and the other isn't, so we have to transition
			return true;
		}
		
		// test the scaling type
		if (mc1.getScaleType() != mc2.getScaleType()) {
			return true;
		}
		
		// if we made it here, then we need to check if the fill and border are the same
		return isTransitionRequired((GenericComponent)mc1, (GenericComponent)mc2);
	}
	
	/**
	 * Returns true if a transition is required between the two components.
	 * @param mc1 the first component
	 * @param mc2 the second component
	 * @return boolean
	 */
	private boolean isTransitionRequired(ImageMediaComponent mc1, ImageMediaComponent mc2) {
		// the media must be the same
		if (mc1.isImageVisible() && mc2.isImageVisible()) {
			if (mc1.getMedia() != null && mc2.getMedia() != null) {
				if (!mc1.getMedia().equals(mc2.getMedia())) {
					// the media items are not the same so we have to transition
					return true;
				}
			} else if (mc1.getMedia() != null || mc2.getMedia() != null) {
				// one is not null
				return true;
			}
		} else if (mc1.isImageVisible() || mc2.isImageVisible()) {
			// one is visible and the other isn't, so we have to transition
			return true;
		}
		
		// test the scaling type
		if (mc1.getScaleType() != mc2.getScaleType()) {
			return true;
		}
		
		// if we made it here, then we need to check if the fill and border are the same
		return isTransitionRequired((GenericComponent)mc1, (GenericComponent)mc2);
	}
	
	/**
	 * Returns true if a transition is required between the two components.
	 * @param c1 the first component
	 * @param c2 the second component
	 * @return boolean
	 */
	private boolean isTransitionRequired(GenericComponent c1, GenericComponent c2) {
		if (c1.isBackgroundVisible() && c2.isBackgroundVisible()) {
			if (c1.getBackgroundFill() != null && c2.getBackgroundFill() != null) {
				if (!c1.getBackgroundFill().equals(c2.getBackgroundFill())) {
					// the background fills are not the same, so we must transition
					return true;
				}
			} else if (c1.getBackgroundFill() != null || c2.getBackgroundFill() != null) {
				// one is not null
				return true;
			}
		} else if (c1.isBackgroundVisible() || c2.isBackgroundVisible()) {
			// one is visible and the other isn't, so we have to transition
			return true;
		}
		
		if (c1.isBorderVisible() && c2.isBorderVisible()) {
			// check the border fill
			if (c1.getBorderFill() != null && c2.getBorderFill() != null) {
				if (!c1.getBorderFill().equals(c2.getBorderFill())) {
					return true;
				}
			} else if (c1.getBorderFill() != null || c2.getBorderFill() != null) {
				// one is not null
				return true;
			}
			// check the line style
			if (c1.getBorderStyle() != null && c2.getBorderStyle() != null) {
				if (!c1.getBorderStyle().equals(c2.getBorderStyle())) {
					return true;
				}
			} else if (c1.getBorderStyle() != null || c2.getBorderStyle() != null) {
				// one is not null
				return true;
			}
		} else if (c1.isBorderVisible() || c2.isBorderVisible()) {
			// one is visible and the other isn't, so we have to transition
			return true;
		}
		
		// check the width/height and position
		if (c1.getWidth() != c2.getWidth() || c1.getHeight() != c2.getHeight()
		 || c1.getX() != c2.getX() || c1.getY() != c2.getY()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if the given slide has a {@link DateTimeComponent} that requires updates.
	 * @param slide the slide
	 * @return boolean
	 */
	private boolean hasUpdatingDateTimeComponent(Slide slide) {
		List<DateTimeComponent> components = slide.getComponents(DateTimeComponent.class);
		for (DateTimeComponent component : components) {
			if (component.isDateTimeUpdateEnabled()) {
				return true;
			}
		}
		return false;
	}
	
	// threading

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent e) {
		this.dateTimeTimer.stop();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent e) {}
	
	/**
	 * Returns the transition wait thread.
	 * <p>
	 * The thread may abruptly end if its interrupted during any wait operation.
	 * In which case, this method will detect if the thread has ended and start
	 * a new thread.
	 * <p>
	 * This method will always return a valid thread.
	 * @return {@link TransitionWaitThread}
	 */
	private TransitionWaitThread getTransitionWaitThread() {
		if (!this.transitionWaitThread.isAlive()) {
			this.transitionWaitThread = new TransitionWaitThread();
			this.transitionWaitThread.start();
		}
		return this.transitionWaitThread;
	}
	
	/**
	 * This thread will queue the latest send/clear request, waiting on the currently executing
	 * transition to complete.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class TransitionWaitThread extends Thread {
		/** True if a send/clear action has been queued */
		protected boolean queued;
		
		/** Lock for queueing and execution */
		protected Object queueLock;
		
		// data
		
		/** The queued event */
		protected PresentEvent event;
		
		/**
		 * Default constructor.
		 */
		public TransitionWaitThread() {
			super("TransitionWaitThread");
			this.setDaemon(true);
			this.queued = false;
			this.queueLock = new Object();
		}
		
		/**
		 * Queues up the event for display.
		 * <p>
		 * The last event queued is used when its time for display.
		 * The others are dropped.
		 * @param event the event to queue
		 */
		public void queue(PresentEvent event) {
			synchronized (this.queueLock) {
				TransitionAnimator eAnimator = event.getAnimator();
				// check the current animator
				if (animator != null && eAnimator != null) {
					// make sure the current animator is not an out transition
					Transition t1 = animator.getTransition();
					Transition t2 = eAnimator.getTransition();
					if (t1.getType() == Transition.Type.OUT && t2.getType() == Transition.Type.OUT) {
						// if the current transition is an out transition then just ignore it
						LOGGER.debug("Current transition is an out transition. Dropping given transition.");
						notifyEventDropped(event);
						return;
					}
				}
				if (this.event != null) {
					LOGGER.debug("Another event has been queued. Dropping current event.");
					notifyEventDropped(this.event);
				}
				LOGGER.debug("Queueing event.");
				this.queued = true;
				this.event = event;
				this.queueLock.notify();
			}
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// run forever
			while (true) {
				if (!isDisplayable()) {
					return;
				}
				// wait until there is something in the queue
				synchronized (this.queueLock) {
					while (!this.queued) {
						try {
							LOGGER.debug("Waiting on event.");
							this.queueLock.wait();
						} catch (InterruptedException e) {
							LOGGER.warn("Interrupted. Stopping thread gracefully.");
							return;
						}
					}
				}
				
				// wait until the current transition is complete
				synchronized (transitionCompleteLock) {
					while (!transitionComplete) {
						try {
							LOGGER.debug("Waiting on transition to complete. Queueing event.");
							transitionCompleteLock.wait();
						} catch (InterruptedException e) {
							LOGGER.warn("Interrupted. Stopping thread gracefully.");
							return;
						}
					}
				}
				
				// execute the next transition
				synchronized (this.queueLock) {
					if (this.event != null) {
						if (this.event instanceof SendEvent) {
							LOGGER.debug("Executing SendEvent.");
							executeSendEvent((SendEvent)this.event);
						} else if (this.event instanceof ClearEvent) {
							if (!clear) {
								LOGGER.debug("Executing ClearEvent.");
								executeClearEvent((ClearEvent)this.event);
							} else {
								// if we are already clear then just drop this event
								LOGGER.debug("Surface has already been cleared. Dropping event.");
								notifyEventDropped(this.event);
							}
						} else {
							LOGGER.warn("Unknown event type: " + this.event.getClass().getName());
						}
					}
					this.queued = false;
					this.event = null;
				}
			}
		}
	}
	
	// helper methods
	
	/**
	 * Renders the output of the given {@link SlideRenderer} to the given image.
	 * @param renderer the renderer
	 * @param renderBackground true if the background of the slide should be rendered
	 * @param image the image to render to
	 */
	protected static final void renderSlide(SlideRenderer renderer, boolean renderBackground, BufferedImage image) {
		// paint the display to the image
		Graphics2D tg2d = image.createGraphics();
		// set the background color to 100% transparent so that it clears
		// the image when we call clearRect
		tg2d.setBackground(new Color(0, 0, 0, 0));
		tg2d.clearRect(0, 0, image.getWidth(), image.getHeight());
		// for compatibility
		tg2d.setColor(new Color(0, 0, 0, 0));
		tg2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		renderer.render(tg2d, renderBackground);
		tg2d.dispose();
	}
	
	/**
	 * Clears the target image and renders the source image to the target image.
	 * @param source the source image
	 * @param target the target image
	 */
	protected static final void copyImage(BufferedImage source, BufferedImage target) {
		Graphics2D tg2d = target.createGraphics();
		tg2d.setBackground(new Color(0, 0, 0, 0));
		tg2d.clearRect(0, 0, target.getWidth(), target.getHeight());
		// for compatibility
		tg2d.setColor(new Color(0, 0, 0, 0));
		tg2d.fillRect(0, 0, target.getWidth(), target.getHeight());
		tg2d.drawImage(source, 0, 0, null);
		tg2d.dispose();
	}
	
	/**
	 * Validates the off-screen image is created and sized appropriately (fills the width/height of the given component).
	 * @param image the image to validate
	 * @param component the component to size to
	 * @return BufferedImage
	 */
	protected static final BufferedImage validateOffscreenImage(BufferedImage image, Component component) {
		Dimension size = component.getSize();
		if (image == null || size.width != image.getWidth() || size.height != image.getHeight()) {
			// FIXME SLIDE-TEMPLATE theres a problem here with position and sized slides in that if they are just one pixel width/height off the don't transition well
			// its also a problem when they are just completely different. maybe if they are different we should instead, transition them out then
			// transition the next in??? not real sure what to do here
			image = component.getGraphicsConfiguration().createCompatibleImage(size.width, size.height, Transparency.TRANSLUCENT);
		}
		return image;
	}
	
	/**
	 * Clears the given image.
	 * @param image the image
	 */
	protected static final void clearImage(BufferedImage image) {
		Graphics2D g2d = image.createGraphics();
		// set the background color to 100% transparent so that it clears
		// the image when we call clearRect
		g2d.setBackground(new Color(0, 0, 0, 0));
		g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
		// for compatibility
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		g2d.dispose();
	}
}
