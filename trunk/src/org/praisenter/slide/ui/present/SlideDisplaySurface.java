package org.praisenter.slide.ui.present;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerConfiguration;
import org.praisenter.media.MediaPlayerFactory;
import org.praisenter.media.PlayableMedia;
import org.praisenter.media.VideoMediaPlayerListener;
import org.praisenter.preferences.Preferences;
import org.praisenter.slide.RenderableSlideComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.media.PlayableMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.TransitionAnimator;
import org.praisenter.transitions.Transition.Type;

/**
 * Panel used for display on a selected device.
 * <p>
 * This panel accepts a {@link Slide} and renders it to a local buffered image.
 * From thereon, the image is used to render the panel.  If the underlying
 * display is updated, this panel will not update.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */

/**
 * There are a few cases which need to be handled:
 * 1. No slide present -> new slide
 * 2. Slide present -> new slide
 * 		a. Old slide & new slide have same video/image background (generic backgrounds too?) and smart transitions are enabled
 * 			i. In this case we need to retain the current background in position and only
 * 			   transition the other components on the slide
 *      b. Old slide & new slide have different backgrounds
 * 3. Slide present -> clear
 */


public class SlideDisplaySurface extends StandardSlideSurface implements VideoMediaPlayerListener {
	/** The version id */
	private static final long serialVersionUID = -944011695590655744L;
	
	// current slide
	
	/** The current slide being displayed */
	protected Slide currentSlide;
	
	protected MediaPlayer<?> currentBackgroundMediaPlayer;
	
	protected List<MediaPlayer<?>> currentMediaPlayers;
	
	protected SlideRenderer currentRenderer;
	
	// in-coming slide
	
	protected Slide inSlide;
	
	protected MediaPlayer<?> inBackgroundMediaPlayer;
	
	protected List<MediaPlayer<?>> inMediaPlayers;
	
	protected SlideRenderer inRenderer;
	
	// transitioning

	/** The transition to apply from display to display */
	protected TransitionAnimator animator;
	
	/** True if the out-going slide has playable media */
	protected boolean outHasPlayableMedia;
	
	/** True if the in-coming slide has playable media */
	protected boolean inHasPlayableMedia;
	
	/** True if the background should be transitioned */
	protected boolean transitionBackground;
	
	// caching
	
	/** The cached before image */
	protected BufferedImage image0;
	
	/** The cached current image */
	protected BufferedImage image1;
	
	/** True if the panel is clear */
	protected boolean clear;
	
	/** True if a repaint has been issued */
	protected boolean repaintIssued = false;
	
	/** True if the transition is complete */
	protected boolean transitionComplete = false;
	
	/**
	 * Default constructor.
	 */
	protected SlideDisplaySurface() {
		super();
		this.image0 = null;
		this.image1 = null;
		
		this.currentSlide = null;
		this.currentBackgroundMediaPlayer = null;
		this.currentMediaPlayers = new ArrayList<>();
		this.outHasPlayableMedia = false;
		
		this.inSlide = null;
		this.inBackgroundMediaPlayer = null;
		this.inMediaPlayers = new ArrayList<>();
		this.inHasPlayableMedia = false;
		
		this.animator = null;
		this.clear = true;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.presentation.StandardDisplaySurface#send(org.praisenter.slide.Slide)
	 */
	@Override
	public void send(Slide slide) {
		this.send(slide, null);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.presentation.StandardDisplaySurface#send(org.praisenter.slide.Slide, org.praisenter.transitions.TransitionAnimator)
	 */
	@Override
	public void send(Slide slide, TransitionAnimator animator) {
		if (this.currentSlide == slide || this.inSlide == slide) return;
		
		Preferences preferences = Preferences.getInstance();

		// first lets handle the case of the initial show
		this.stopPlayers();
		
		// see if we have any playable media
		this.inSlide = slide;
		this.inMediaPlayers.clear();
		this.inRenderer = new SlideRenderer(slide, getGraphicsConfiguration());
		this.transitionBackground = true;
		RenderableSlideComponent background = slide.getBackground();
		List<PlayableMediaComponent<?>> playableMediaComponents = slide.getPlayableMediaComponents();
		this.inHasPlayableMedia = false;
		
		// we will only NOT transition the background IF both slides have a video background component AND
		// they are the same video
		// check if there is a previous slide and that it has a background
		if (this.currentSlide != null && this.currentSlide.getBackground() != null) {
			// if so, then check if its background was a video
			if (this.currentSlide.getBackground() instanceof VideoMediaComponent) {
				// if so, then make sure the new background is also a video
				if (background instanceof VideoMediaComponent) {	
					// if both are video media components, we need to check if they are the same video
					VideoMediaComponent oC = (VideoMediaComponent)this.currentSlide.getBackground();
					VideoMediaComponent nC = (VideoMediaComponent)this.inSlide.getBackground();
					if (oC.getMedia().getFile().getPath().equals(nC.getMedia().getFile().getPath())) {
						// they are the same video, so we should not transition the background
						// we can attach the new media component as a listener to the current 
						// media player (to update its images as the video plays)
						this.currentBackgroundMediaPlayer.addMediaPlayerListener(nC);
						this.transitionBackground = preferences.isSmartTransitionsEnabled();
					}
				}
			}
		}
		
		// we need to create a player for each playable media component and attach them as listeners and this
		// surface as a listener
		// don't create a player for the IN slide if we aren't transitioning the background
		if (background instanceof VideoMediaComponent && this.transitionBackground) {
			VideoMediaComponent bg = (VideoMediaComponent)background;
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
		for (PlayableMediaComponent<?> component : playableMediaComponents) {
			if (component != background) {
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
		}
		
		
		// stop the old transition just in case it's still in progress
		if (this.animator != null) {
			this.animator.stop();
		}
//		if (!this.transitionComplete) {
			this.onTransitionComplete();
			
//		}
		
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
		SlideSurface.renderSlide(this.inRenderer, this.image1);
		
		// make sure the transition is not null
		if (this.animator != null) {
			// start it
			this.animator.start(this);
			// begin the media players
			if (this.inBackgroundMediaPlayer != null) {
				this.inBackgroundMediaPlayer.play();
			}
			for (MediaPlayer<?> player : this.inMediaPlayers) {
				player.play();
			}
		} else {
			this.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.StandardDisplaySurface#clear()
	 */
	@Override
	public void clear() {
		this.clear(null);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.StandardDisplaySurface#clear(org.praisenter.transitions.TransitionAnimator)
	 */
	@Override
	public void clear(TransitionAnimator animator) {
		if (!this.clear) {
			// stop the old transition just in case it's still in progress
			if (this.animator != null) {
				this.animator.stop();
			}
	
			// set the transition
			this.animator = animator;
			
			// make sure the transition is not null
			if (this.animator != null) {
				// render what's currently in image1 to image0
				// this saves the last display's rendering so we
				// can apply transitions
				Graphics2D tg2d = this.image0.createGraphics();
				tg2d.drawImage(this.image1, 0, 0, null);
				tg2d.dispose();
				
				SlideSurface.clearImage(this.image1);
				// start it
				this.animator.start(this);
			} else {
				SlideSurface.clearImage(this.image0);
				SlideSurface.clearImage(this.image1);
				this.clear = true;
				this.repaint();
			}
		}
	}
	
	@Override
	public void repaint() {
		this.repaintIssued = true;
		super.repaint();
	}
	
	@Override
	public void onVideoImage(BufferedImage image) {
		if (!this.repaintIssued) {
			this.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// update the images if necessary
		if (this.outHasPlayableMedia && this.currentSlide != null) {
			SlideSurface.renderSlide(this.currentRenderer, this.image0);
		}
		if (this.inHasPlayableMedia && this.inSlide != null) {
			SlideSurface.renderSlide(this.inRenderer, this.image1);
		}
		
		if (this.animator != null) {
			Transition transition = this.animator.getTransition();
			if (this.animator.isComplete()) {
				if (transition.getType() == Type.IN) {
					g.drawImage(this.image1, 0, 0, null);
				} else {
					if (!this.clear) {
						clearImage(this.image0);
						this.clear = true;
					}
				}
				if (!this.transitionComplete) {
					this.onTransitionComplete();
				}
			} else {
				transition.render((Graphics2D)g, this.image0, this.image1, this.animator.getPercentComplete());
			}
		} else {
			if (!this.clear) {
				g.drawImage(this.image1, 0, 0, null);
			}
		}
		
		this.repaintIssued = false;
	}
	
	private boolean isBackgroundTransitioning(Slide slide) {
		Preferences preferences = Preferences.getInstance();
		
		RenderableSlideComponent tbackground = slide.getBackground();
		if (tbackground == null) {
			// transition the background out since the new slide
			// has no background
			return true;
		}
		
		RenderableSlideComponent fBackground = null;
		if (this.inSlide != null) {
			fBackground = this.inSlide.getBackground();
		}
		this.inHasPlayableMedia = false;
		
		// to return that the background is not transitioning, we must have two video backgrounds
		// with the same media and smart transitions enabled
		if (fBackground != null && 
			fBackground instanceof VideoMediaComponent && 
			tbackground instanceof VideoMediaComponent && 
			preferences.isSmartTransitionsEnabled()) {
			// if both are video media components, we need to check if they are the same video
			VideoMediaComponent oC = (VideoMediaComponent)this.currentSlide.getBackground();
			VideoMediaComponent nC = (VideoMediaComponent)this.inSlide.getBackground();
			if (oC.getMedia().equals(nC.getMedia())) {
				// they are the same video, so we should not transition the background
				// we can attach the new media component as a listener to the current 
				// media player (to update its images as the video plays)
				this.currentBackgroundMediaPlayer.addMediaPlayerListener(nC);
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Called when a transition has completed.
	 */
	private void onTransitionComplete() {
		if (this.currentBackgroundMediaPlayer != null && this.transitionBackground) {
			// if we aren't transitioning the background, then we need to make sure
			// we keep the currently executing background media player
			this.currentBackgroundMediaPlayer.release();
			this.currentBackgroundMediaPlayer = null;
		}
		for (MediaPlayer<?> player : this.currentMediaPlayers) {
			player.release();
		}
		this.currentMediaPlayers.clear();
	}
	
	private void stopPlayers() {
		if (this.inBackgroundMediaPlayer != null) {
			// if we aren't transitioning the background, then we need to make sure
			// we keep the currently executing background media player
			this.inBackgroundMediaPlayer.release();
			this.inBackgroundMediaPlayer = null;
		}
		for (MediaPlayer<?> player : this.inMediaPlayers) {
			player.release();
		}
	}
}
