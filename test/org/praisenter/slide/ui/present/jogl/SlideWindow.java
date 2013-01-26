package org.praisenter.slide.ui.present.jogl;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GLRunnable;

import org.apache.log4j.Logger;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerConfiguration;
import org.praisenter.media.MediaPlayerFactory;
import org.praisenter.media.PlayableMedia;
import org.praisenter.media.VideoMediaPlayerListener;
import org.praisenter.preferences.Preferences;
import org.praisenter.slide.NotificationSlide;
import org.praisenter.slide.RenderableComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.PlayableMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.ui.present.SlideSurface;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.Transition.Type;
import org.praisenter.transitions.TransitionAnimator;
import org.praisenter.utilities.ColorUtilities;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.awt.TextureRenderer;

/**
 * Represents a window that is used to display custom graphics.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideWindow implements VideoMediaPlayerListener, ActionListener, GLEventListener {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlideWindow.class);
	
	/** The device this display is for */
	protected GraphicsDevice device;

	/** The window used to display the Display */
	protected GLWindow dialog;

	/** True if the surface is visible */
	protected boolean visible;
	
	/** True if the window is always the device size */
	protected boolean fullScreen;
	
	/** True if the window is always on top of other windows */
	protected boolean overlay;
	
	////////////////////////////////////////////////////////////////////////////////////////////
	
	// current slide
	
	/** The current slide being displayed */
	protected Slide currentSlide;
	
	/** The current slide's background media player; can be null */
	protected MediaPlayer<?> currentBackgroundMediaPlayer;
	
	/** The current slide's list of media players (excluding the background media player) */
	protected List<MediaPlayer<?>> currentMediaPlayers;
	
	/** The current slide's renderer */
	protected SlideRenderer currentRenderer;
	
	// in-coming slide
	
	/** The incoming slide */
	protected Slide inSlide;
	
	/** The incoming slide's background media player; can be null */
	protected MediaPlayer<?> inBackgroundMediaPlayer;
	
	/** The incoming slide's list of media players (excluding the background media player) */
	protected List<MediaPlayer<?>> inMediaPlayers;
	
	/** The incoming slide's renderer */
	protected SlideRenderer inRenderer;
	
	// transitioning

	/** The transition to apply from display to display */
	protected TransitionAnimator animator;
	
	/** True if the out-going slide has playable media */
	protected boolean currentHasPlayableMedia;
	
	/** True if the in-coming slide has playable media */
	protected boolean inHasPlayableMedia;
	
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
		
		// setup the OpenGL capabilties
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(profile);
		caps.setBackgroundOpaque(false);
		
		// setup the dialog
		this.dialog = GLWindow.create(caps);
		this.dialog.setUndecorated(true);
		// don't allow focus to transfer to the dialog
//		this.dialog.setFocusable(false);
//		this.dialog.setFocusableWindowState(false);
//		this.dialog.setFocusTraversalKeysEnabled(false);
		// we need to enable per-pixel translucency if available
//		this.dialog.getRootPane().setOpaque(false);
		
		// get the device's default config
		GraphicsConfiguration gc = this.device.getDefaultConfiguration();
		// get its position and dimensions
		Rectangle r = gc.getBounds();
		
		// set the dialog location to the top left corner of the
		// target display device
		this.dialog.setPosition(r.x, r.y);
		
		// a full screen display window has its size set to the
		// height and width of the device
//		Dimension size = new Dimension(r.width, r.height);
		this.dialog.setSize(r.width, r.height);
//		this.dialog.setMinimumSize(size);
//		this.dialog.setPreferredSize(size);
		
		this.dialog.setAlwaysOnTop(overlay);
		this.dialog.setDefaultCloseOperation(WindowClosingMode.DO_NOTHING_ON_CLOSE);
		
		this.dialog.addGLEventListener(this);
		
		this.dialog.setVisible(true);
		
		this.image0 = null;
		this.image1 = null;
		
		this.currentSlide = null;
		this.currentBackgroundMediaPlayer = null;
		this.currentMediaPlayers = new ArrayList<>();
		this.currentHasPlayableMedia = false;
		
		this.inSlide = null;
		this.inBackgroundMediaPlayer = null;
		this.inMediaPlayers = new ArrayList<>();
		this.inHasPlayableMedia = false;
		
		this.animator = null;
		this.repaintIssued = false;
		
		this.clear = true;
		this.transitionComplete = true;
		this.transitionCompleteLock = new Object();
		this.transitionWaitThread = new TransitionWaitThread();
		this.transitionWaitThread.start();
	}
	
	/**
	 * Sends the new slide to this slide window using the given animator.
	 * @param slide the new slide to show
	 * @param animator the animator
	 */

	/**
	 * Clears this display window using the given animator.
	 * @param animator the animator
	 */

	
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
		
//		Dimension size = new Dimension(slide.getWidth(), slide.getHeight());
		// set the size
		this.dialog.setSize(slide.getWidth(), slide.getHeight());
		this.dialog.setPosition(x, y);
	}

	/**
	 * Shows the given slide using the given animator.
	 * @param slide the slide to show
	 * @param animator the animator to use; can be null
	 */
	public void send(Slide slide, TransitionAnimator animator) {
		Preferences preferences = Preferences.getInstance();
		
		// always use a copy of the slide since it could be reused by 
		// the rest of the application, this shouldn't be a problem anyway
		// since the copy is really fast (mostly immutable objects)
		Slide copy = slide.copy();
		
		// see if we should wait for the existing transition before
		// sending this slide
		if (preferences.isWaitForTransitionEnabled()) {
			// see if the animation is still in progress
			synchronized (this.transitionCompleteLock) {
				if (!this.transitionComplete) {
					LOGGER.debug("Waiting on transition to complete. Queueing send.");
					// if so, then send the next animation to the present thread
					this.getTransitionWaitThread().send(copy, animator);
				} else {
					// if not, then just immediately being the next animation
					this.showSlide(copy, animator);
				}
			}
		} else {
			// if we don't need to wait, then just stop the old transition
			if (this.animator != null) {
				this.animator.stop();
			}
			
			// if the transition is not complete then complete it
			synchronized (this.transitionCompleteLock) {
				while (!this.transitionComplete) {
					try {
						this.transitionCompleteLock.wait();
					} catch (InterruptedException e) {
						LOGGER.warn("Interrupted while waiting for the transition to complete.");
						break;
					}
				}
			}
			
			// show the slide
			this.showSlide(copy, animator);
		}
	}
	
	/**
	 * Clears the current slide using the given animator.
	 * <p>
	 * This method will always allow one
	 * @param animator the animator to use; can be null
	 */
	public void clear(TransitionAnimator animator) {
		Preferences preferences = Preferences.getInstance();
		
		// see if we should wait for the existing transition before
		// sending this slide
		if (preferences.isWaitForTransitionEnabled()) {
			// see if the animation is still in progress
			synchronized (this.transitionCompleteLock) {
				if (!this.transitionComplete) {
					// if the transition is still in progress, see what type it is
					Transition transition = this.animator.getTransition();
					if (transition.getType() == Transition.Type.IN) {
						// if the current transition is an IN transition then wait until it
						// finishes then send the clear operation
						LOGGER.debug("Waiting on transition to complete. Queueing send.");
						// if so, then send the next animation to the present thread
						this.getTransitionWaitThread().clear(animator);
					} else {
						// otherwise, if the current transition is a clear transition
						// then just ignore it
						LOGGER.debug("Current transition is an out transition. This clear request will be ignored.");
						return;
					}
				} else {
					// if the current transition is complete
					if (!this.clear) {
						// we aren't already clear, so send the clear command
						this.clearSlide(animator);
					}
				}
			}
		} else {
			// if we don't need to wait, then just stop the old transition
			if (this.animator != null) {
				this.animator.stop();
			}
			
			// if the transition is not complete then complete it
			synchronized (this.transitionCompleteLock) {
				while (!this.transitionComplete) {
					try {
						this.transitionCompleteLock.wait();
					} catch (InterruptedException e) {
						LOGGER.warn("Interrupted while waiting for the transition to complete.");
						break;
					}
				}
			}
			
			// if the current transition is complete
			if (!this.clear) {
				// we aren't already clear, so send the clear command
				this.clearSlide(animator);
			}
		}
	}
	
	/**
	 * Shows the given slide using the given animator.
	 * @param slide the slide to show
	 * @param animator the animator to use; can be null
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void showSlide(Slide slide, TransitionAnimator animator) {
		Preferences preferences = Preferences.getInstance();
		
		GraphicsConfiguration gc = this.device.getDefaultConfiguration();
		
		// see if we have any playable media
		this.inSlide = slide;
		this.inRenderer = new SlideRenderer(slide, gc);
		this.inMediaPlayers.clear();
		this.transitionBackground = true;
		RenderableComponent background = slide.getBackground();
		List<PlayableMediaComponent<?>> playableMediaComponents = slide.getPlayableMediaComponents();
		this.inHasPlayableMedia = false;
		
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
					if (oC.getMedia().equals(nC.getMedia()) && oC.isVideoVisible() && nC.isVideoVisible()) {
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
					if (oC.getMedia().equals(nC.getMedia()) && oC.isImageVisible() && nC.isImageVisible()) {
						this.transitionBackground = !preferences.isSmartImageTransitionsEnabled();
						// if we transitioned the background in the last send, we need to make sure we
						// re-render the current slide without the background. if we don't do this
						// image0 still contains the background and it will appear as if we are
						// still transitioning the background
						SlideWindow.renderSlide(this.currentRenderer, false, this.image1);
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
		this.image0 = SlideWindow.validateOffscreenImage(this.image0, gc, slide.getWidth(), slide.getHeight());
		this.image1 = SlideWindow.validateOffscreenImage(this.image1, gc, slide.getWidth(), slide.getHeight());
		
		// render whats currently in image1 to image0
		// this saves the last display's rendering so we
		// can apply transitions
		SlideWindow.copyImage(this.image1, this.image0);
		
		// paint the display to the image
		SlideWindow.renderSlide(this.inRenderer, this.transitionBackground, this.image1);
		
		// make sure the transition is not null
		if (this.animator != null) {
			synchronized (this.transitionCompleteLock) {
				this.transitionComplete = false;
			}
			// start it
//			this.animator.start(this.dialog);
			// begin the media players
			if (this.inBackgroundMediaPlayer != null) {
				this.inBackgroundMediaPlayer.play();
			}
			for (MediaPlayer<?> player : this.inMediaPlayers) {
				player.play();
			}
		} else {
			synchronized (this.transitionCompleteLock) {
				this.onInTransitionComplete();
			}
			// begin the media players
			if (this.inBackgroundMediaPlayer != null) {
				this.inBackgroundMediaPlayer.play();
			}
			for (MediaPlayer<?> player : this.inMediaPlayers) {
				player.play();
			}
			this.update();
		}
	}
	
	/**
	 * Clears the slide using the given animator.
	 * @param animator the animator to use; can be null
	 */
	private void clearSlide(TransitionAnimator animator) {
		// set the transition
		this.animator = animator;

		// check the current slide background type
		if (this.currentSlide.getBackground() instanceof ImageMediaComponent
		 && Preferences.getInstance().isSmartImageTransitionsEnabled()) {
			// if the current slide background type is image and we have smart
			// image transitions enabled, its possible that image0 does not
			// contain the background. So we need to re-render the image
			// with the background to ensure the clear includes the background
			SlideWindow.renderSlide(this.currentRenderer, true, this.image0);
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
//			this.animator.start(this.dialog);
		} else {
			synchronized (this.transitionCompleteLock) {
				this.onOutTransitionComplete();
			}
			this.update();
		}
	}

	TextureRenderer tr = null;
	@Override
	public void init(GLAutoDrawable drawable) {
		// get the OpenGL context
		GL2 gl = drawable.getGL().getGL2();
		
		// set the matrix mode to projection
		gl.glMatrixMode(GL2.GL_PROJECTION);
		// initialize the matrix
		gl.glLoadIdentity();
		// set the view to a 2D view
		gl.glOrtho(0, this.device.getDisplayMode().getWidth(), this.device.getDisplayMode().getHeight(), 0, 0, 1);
		
		// switch to the model view matrix
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		// initialize the matrix
		gl.glLoadIdentity();
		
		// set the clear color to transparent
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// disable depth testing since we are working in 2D
		gl.glDisable(GL.GL_DEPTH_TEST);
		// we dont need lighting either
		gl.glDisable(GL2.GL_LIGHTING);
		
		// enable blending for translucency
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
				
		// set the swap interval to vertical-sync
		gl.setSwapInterval(1);
		
		this.tr = new TextureRenderer(this.device.getDisplayMode().getWidth(), this.device.getDisplayMode().getHeight(), true);
		this.tr.setSmoothing(true);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		// update the images if necessary
		if (this.currentHasPlayableMedia && this.currentSlide != null) {
			SlideWindow.renderSlide(this.currentRenderer, this.transitionBackground, this.image0);
		}
		if (this.inHasPlayableMedia && this.inSlide != null) {
			SlideWindow.renderSlide(this.inRenderer, this.transitionBackground, this.image1);
		}
		
		DisplayMode mode = this.device.getDisplayMode();
		int w = mode.getWidth();
		int h = mode.getHeight();
		
		Graphics2D g = this.tr.createGraphics();
		g.setBackground(ColorUtilities.TRANSPARENT);
		g.clearRect(0, 0, w, h);
		
		// render the background first
		if (!this.transitionBackground && this.currentSlide != null) {
			this.currentSlide.getBackground().render(g);
		}
		
		// render the animation
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
					g.drawImage(this.image0, 0, 0, null);
				} else {
					if (!this.clear) {
						this.onOutTransitionComplete();
					}
					// draw nothing
				}
			} else {
				transition.render((Graphics2D)g, this.image0, this.image1, this.animator.getPercentComplete());
			}
		} else {
			if (!this.clear) {
				g.drawImage(this.image0, 0, 0, null);
			}
		}
		g.dispose();
		
		this.tr.markDirty(0, 0, w, h);
		
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
//		TextureCoords coords = texture.getImageTexCoords();
		this.tr.beginOrthoRendering(w, h);
		this.tr.drawOrthoRect(0, 0, 0, 0, w, h);
		this.tr.endOrthoRendering();
//		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
//		gl.glBegin(GL2.GL_QUADS);
//			gl.glTexCoord2d(coords.left(), coords.top());
//			gl.glVertex2d(100, 100);
//			gl.glTexCoord2d(coords.right(), coords.top());
//			gl.glVertex2d(100, 200);
//			gl.glTexCoord2d(coords.right(), coords.bottom());
//			gl.glVertex2d(200, 200);
//			gl.glTexCoord2d(coords.left(), coords.bottom());
//			gl.glVertex2d(200, 100);
//		gl.glEnd();
		
//		texture.disable(gl);
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		this.tr.dispose();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.update();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.VideoMediaPlayerListener#onVideoImage(java.awt.image.BufferedImage)
	 */
	@Override
	public void onVideoImage(BufferedImage image) {
		this.update();
	}
	
	public void update() {
//		this.dialog.display();
		this.dialog.invoke(false, new GLRunnable() {
			@Override
			public boolean run(GLAutoDrawable arg0) {
				return false;
			}
		});
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
		
		this.inBackgroundMediaPlayer = null;
		this.inHasPlayableMedia = false;
		this.inRenderer = null;
		this.inSlide = null;
		
		synchronized (this.transitionCompleteLock) {
			this.transitionComplete = true;
			this.transitionCompleteLock.notifyAll();
		}
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
		
		this.currentBackgroundMediaPlayer = null;
		this.currentHasPlayableMedia = false;
		this.currentRenderer = null;
		this.currentSlide = null;
		
		this.clear = true;
		
		synchronized (this.transitionCompleteLock) {
			this.transitionComplete = true;
			this.transitionCompleteLock.notifyAll();
		}
	}
	
	// threading
	
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
		
		/** The queued slide */
		protected Slide slide;
		
		/** The queued animator */
		protected TransitionAnimator animator;
		
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
		 * Queues up the slide and animator for display.
		 * <p>
		 * The last send operation is used when its time for display.
		 * @param slide the slide to display
		 * @param animator the animator to display
		 */
		public void send(Slide slide, TransitionAnimator animator) {
			synchronized (this.queueLock) {
				this.queued = true;
				this.slide = slide;
				this.animator = animator;
				this.queueLock.notify();
			}
		}

		/**
		 * Queues up a clear operation.
		 * @param animator the animator to use to clear the display
		 */
		public void clear(TransitionAnimator animator) {
			synchronized (this.queueLock) {
				this.queued = true;
				this.animator = animator;
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
				// wait until there is something in the queue
				synchronized (this.queueLock) {
					while (!this.queued) {
						try {
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
							transitionCompleteLock.wait();
						} catch (InterruptedException e) {
							LOGGER.warn("Interrupted. Stopping thread gracefully.");
							return;
						}
					}
				}
				
				// execute the next transition
				synchronized (this.queueLock) {
					if (this.slide != null) {
						// perform the action
						showSlide(this.slide, this.animator);
					} else {
						clearSlide(this.animator);
					}
					this.queued = false;
					this.slide = null;
					this.animator = null;
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
		renderer.render(tg2d, renderBackground);
		tg2d.dispose();
	}
	
	/**
	 * Renders the output of the given {@link SlideRenderer} to the given image.
	 * @param renderer the renderer
	 * @param renderBackground true if the background of the slide should be rendered
	 * @param image the image to render to
	 */
	protected static final void renderBackground(Slide slide, BufferedImage image) {
		// paint the display to the image
		Graphics2D tg2d = image.createGraphics();
		// set the background color to 100% transparent so that it clears
		// the image when we call clearRect
		tg2d.setBackground(new Color(0, 0, 0, 0));
		tg2d.clearRect(0, 0, image.getWidth(), image.getHeight());
		slide.getBackground().render(tg2d);
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
		tg2d.drawImage(source, 0, 0, null);
		tg2d.dispose();
	}
	
	/**
	 * Validates the off-screen image is created and sized appropriately (fills the width/height of the given component).
	 * @param image the image to validate
	 * @param component the component to size to
	 * @return BufferedImage
	 */
	protected static final BufferedImage validateOffscreenImage(BufferedImage image, GraphicsConfiguration gc, int w, int h) {
		if (image == null || w != image.getWidth() || h != image.getHeight()) {
			image = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
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
		g2d.dispose();
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
