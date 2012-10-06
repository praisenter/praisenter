package org.praisenter.display;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.praisenter.utilities.ColorUtilities;
import org.praisenter.utilities.ImageUtilities;

/**
 * Represents a sized and positioned component that has a background.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class GraphicsComponent extends DisplayComponent {
	/** The x coordinate of this component */
	protected int x;
	
	/** The y coordinate of this component */
	protected int y;
	
	/** The width of this component */
	protected int width;
	
	/** The height of this component */
	protected int height;
	
	// color settings
	
	/** The color to use */
	protected Color backgroundColor;

	/** The color composite type */
	protected CompositeType backgroundColorCompositeType;
	
	/** True if the color is visible */
	protected boolean backgroundColorVisible;
	
	// TODO add multi-stop gradient
	// TODO add borders
	// TODO add effects (drop shadow)
	
	// image settings
	
	/** The image */
	protected BufferedImage backgroundImage;

	/** The image scale type */
	protected ScaleType backgroundImageScaleType;
	
	/** The scaling quality */
	protected ScaleQuality backgroundImageScaleQuality;

	/** True if the image is visible */
	protected boolean backgroundImageVisible;
	
	// TODO - VIDEO add video settings
	
	// cached info

	/** True if the original image has been converted */
	private boolean imageConverted;
	
	/** A scaled version of the image */
	private BufferedImage cachedScaledImage;
	
	/** True if the color has been changed */
	private boolean colorUpdateRequired;
	
	/** A cached version of the color */
	private BufferedImage cachedColorImage;
	
	/** The cached version of the entire composite */
	protected BufferedImage cachedCompositeImage;

	/** True if the bounds have changed */
	private BoundsChangeType boundsChangeType;
	
	/**
	 * Minimal constructor.
	 * @param name the name of this component
	 * @param width the width
	 * @param height the height
	 */
	public GraphicsComponent(String name, int width, int height) {
		this(name, 0, 0, width, height);
	}

	/**
	 * Full constructor.
	 * @param name the name of this component
	 * @param bounds the bounds
	 */
	public GraphicsComponent(String name, Bounds bounds) {
		this(name, bounds.x, bounds.y, bounds.w, bounds.h);
	}
	
	/**
	 * Full constructor.
	 * @param name the name of this component
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width
	 * @param height the height
	 */
	public GraphicsComponent(String name, int x, int y, int width, int height) {
		super(name);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.backgroundColor = Color.WHITE;
		this.backgroundColorCompositeType = CompositeType.UNDERLAY;
		this.backgroundColorVisible = false;
		
		this.backgroundImage = null;
		this.backgroundImageScaleType = ScaleType.NONUNIFORM;
		this.backgroundImageScaleQuality = ScaleQuality.BILINEAR;
		this.backgroundImageVisible = false;
		
		this.cachedScaledImage = null;
		this.cachedColorImage = null;
		this.cachedCompositeImage = null;
		this.imageConverted = false;
		this.boundsChangeType = null;
		this.colorUpdateRequired = true;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.DisplayComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D graphics) {
		// get the target device graphics configuration
		GraphicsConfiguration gc = DisplayComponent.getTargetGraphicsConfiguration();
		
		// cache any images
		this.cacheImages(gc, this.boundsChangeType);
		this.boundsChangeType = null;
		
		// make sure the cached image is created
		if (this.isDirty()) {
			// render to the cached image
			Graphics2D ig = this.cachedCompositeImage.createGraphics();
			
			// setup the rendering quality
			DisplayComponent.setRenderQuality(graphics);
			
			// render the component to the cached image
			this.renderComponent(ig);
			
			ig.dispose();
			// set the dirty flag
			this.setDirty(false);
		}
		
		// check visibility
		if (this.visible) {
			graphics.drawImage(this.cachedCompositeImage, this.x, this.y, null);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.DisplayComponent#invalidate()
	 */
	public void invalidate() {
		super.invalidate();
		this.cachedScaledImage = null;
		this.cachedColorImage = null;
		this.cachedCompositeImage = null;
		this.imageConverted = false;
		this.boundsChangeType = null;
		this.colorUpdateRequired = true;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.DisplayComponent#renderComponent(java.awt.Graphics2D)
	 */
	protected void renderComponent(Graphics2D graphics) {
		// clear the whole image using transparent
		graphics.setBackground(ColorUtilities.TRANSPARENT);
		graphics.clearRect(0, 0, this.cachedCompositeImage.getWidth(), this.cachedCompositeImage.getHeight());
		
		// check the color composite type
		if (this.backgroundColorVisible && this.backgroundColorCompositeType == CompositeType.UNDERLAY) {
			// render the color to the image first
			graphics.drawImage(this.cachedColorImage, 0, 0, null);
		}
		
		// render a scaled version of the image
		if (this.backgroundImageVisible && this.cachedScaledImage != null) {
			// center the image
			int iw = this.cachedScaledImage.getWidth();
			int ih = this.cachedScaledImage.getHeight();
			int x = (this.width - iw) / 2;
			int y = (this.height - ih) / 2;
			graphics.drawImage(this.cachedScaledImage, x,  y, null);
		}
		
		// check the color composite type
		if (this.backgroundColorVisible && this.backgroundColorCompositeType == CompositeType.OVERLAY) {
			// render the color to the image first
			graphics.drawImage(this.cachedColorImage, 0, 0, null);
		}
	}

	/**
	 * Caches any images needed for fast rendering.
	 * <p>
	 * Use the given graphics configuration to create compatible images for the target device.
	 * <p>
	 * Override this method to cache additional images before rendering.
	 * @param configuration the graphics configuration
	 * @param boundsChangeType the type of change to the bounds; null if no change
	 */
	protected void cacheImages(GraphicsConfiguration configuration, BoundsChangeType boundsChangeType) {
		// make sure the source image is compatible
		this.cacheConvertedImage(configuration);
		
		// make sure the color image is good
		this.cacheColorImage(configuration, boundsChangeType);
		
		// make sure the scaled image is good
		this.cacheScaledImage();
		
		// make sure the composite image is good
		this.cacheCompositeImage(configuration, boundsChangeType);
	}

	/**
	 * Converts the image background to the given graphics configuration compatible image.
	 * @param configuration the graphics configuration
	 */
	private void cacheConvertedImage(GraphicsConfiguration configuration) {
		// make sure the source image is compatible
		if (this.backgroundImage != null && !this.imageConverted) {
			// the image set here may or may not be in this device's best format
			// to avoid the cost of converting the image on each render, we go
			// ahead and convert the given image to an image of the same size
			// that is compatible with this display
			BufferedImage original = this.backgroundImage;
			this.backgroundImage = configuration.createCompatibleImage(original.getWidth(), original.getHeight(), original.getTransparency());
			// blit the original to the new one (this performs the color/data model conversion)
			Graphics2D ig = this.backgroundImage.createGraphics();
			// set the quality
			DisplayComponent.setRenderQuality(ig);
			// render the image
			ig.drawImage(original, 0, 0, null);
			ig.dispose();
			// set the converted flag
			this.imageConverted = true;
		}
	}
	
	/**
	 * Caches the color image using the given graphics configuration.
	 * @param configuration the graphics configuration
	 * @param boundsChangeType the type of change to the bounds; null if no change
	 */
	private void cacheColorImage(GraphicsConfiguration configuration, BoundsChangeType boundsChangeType) {
		// see if the background color is set (should always be) and that the cached
		// image for it has not be created
		
		// if the bounds have decreased there's nothing to do, but if its increased or changed we 
		// need to recreate the image bigger and re-render the color
		boolean updateBounds = boundsChangeType == BoundsChangeType.INCREASED || boundsChangeType == BoundsChangeType.CHANGED;
		
		// if the color was changed we only need to clear the current image and re-render the color
		if (this.backgroundColor != null && (this.cachedColorImage == null || updateBounds || this.colorUpdateRequired)) {
			int w = this.width;
			int h = this.height;
			// generate the cached color image if its null or the bounds have increased
			if (this.cachedColorImage == null || updateBounds) {
				// this makes it to where we only re-create the cached color image when the bounds have increased
				this.cachedColorImage = configuration.createCompatibleImage(w, h, this.backgroundColor.getTransparency());
			}
			// render the color to the image
			Graphics2D ig = this.cachedColorImage.createGraphics();
			// set the quality
			DisplayComponent.setRenderQuality(ig);
			// we only need to clear the image if its translucent
			if (this.backgroundColor.getTransparency() == Transparency.TRANSLUCENT) {
				// clear the image
				ig.setBackground(ColorUtilities.TRANSPARENT);
				ig.clearRect(0, 0, w, h);
			}
			ig.setColor(this.backgroundColor);
			// fill the whole image
			ig.fillRect(0, 0, w, h);
			ig.dispose();
			
			this.colorUpdateRequired = false;
		}
	}
	
	/**
	 * Caches a scaled version of the background image.
	 */
	private void cacheScaledImage() {
		// render a scaled version of the image
		if (this.backgroundImage != null && this.cachedScaledImage == null) {
			int w = this.width;
			int h = this.height;
			// now scale (these methods will return images with the same
			// color model as the one passed in)
			if (this.backgroundImageScaleType == ScaleType.UNIFORM) {
				this.cachedScaledImage = ImageUtilities.getUniformScaledImage(this.backgroundImage, w, h, this.backgroundImageScaleQuality.getQuality());
			} else if (this.backgroundImageScaleType == ScaleType.NONUNIFORM) {
				this.cachedScaledImage = ImageUtilities.getNonUniformScaledImage(this.backgroundImage, w, h, this.backgroundImageScaleQuality.getQuality());
			} else {
				// scaling type was none, so don't scale
				this.cachedScaledImage = this.backgroundImage;
			}
		}
	}

	/**
	 * Creates the composite image using the given graphics configuration.
	 * @param configuration the graphics configuration
	 * @param boundsChangeType the type of change to the bounds; null if no change
	 */
	private void cacheCompositeImage(GraphicsConfiguration configuration, BoundsChangeType boundsChangeType) {
		// check for increased or changed
		boolean updateBounds = boundsChangeType == BoundsChangeType.INCREASED || boundsChangeType == BoundsChangeType.CHANGED;
		// regenerate the image only if the bounds have changed
		if (this.cachedCompositeImage == null || updateBounds) {
			// create a compatible image
			this.cachedCompositeImage =  configuration.createCompatibleImage(this.width, this.height, Transparency.TRANSLUCENT);
		}
	}
	
	// general
	
	/**
	 * Translates this component by the given delta x and y.
	 * @param dx the delta x
	 * @param dy the delta y
	 */
	public void translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
		// moving the component does not invalidate
		// the cached info
	}
	
	/**
	 * Resizes this component.
	 * @param dw the delta width
	 * @param dh the delta height
	 */
	public void resize(int dw, int dh) {
		if (dw == 0 && dh == 0) return;
		// limit the minimum size by 50,50
		if (this.width + dw > 50)
			this.width += dw;
		if (this.height + dh > 50)
			this.height += dh;
		// only re-create the buffered image when the bounds are increased
		if (this.cachedCompositeImage != null) {
			int w = this.cachedCompositeImage.getWidth();
			int h = this.cachedCompositeImage.getHeight();
			
			BoundsChangeType wType = null;
			BoundsChangeType hType = null;
			
			if (this.width > w) {
				wType = BoundsChangeType.INCREASED;
			} else if (this.width < w) {
				wType = BoundsChangeType.DECREASED;
			}
			
			if (this.height > h) {
				hType = BoundsChangeType.INCREASED;
			} else if (this.height < h) {
				hType = BoundsChangeType.DECREASED;
			}
			
			if (wType == hType) {
				// if they are the same (both increased or decreased)
				this.boundsChangeType = wType;
			} else if (wType != null && hType == null) {
				// if one is null
				this.boundsChangeType = wType;
			} else if (hType != null && wType == null) {
				// if one is null
				this.boundsChangeType = hType;
			} else {
				// at this point they are not the same, but neither is null
				this.boundsChangeType = BoundsChangeType.CHANGED;
			}
			// the scaled image must be updated whenever the bounds are resized (not just when they are increased)
			this.cachedScaledImage = null;
		}
		this.setDirty(true);
	}
	
	/**
	 * Resizes this components width.
	 * @param dw the delta width
	 */
	public void resizeWidth(int dw) {
		this.resize(dw, 0);
	}
	
	/**
	 * Resizes this components height.
	 * @param dh the delta height
	 */
	public void resizeHeight(int dh) {
		this.resize(0, dh);
	}
	
	/**
	 * Sets the x coordinate of this component.
	 * @param x the x coordinate
	 */
	public void setX(int x) {
		this.x = x;
		// moving the component does not invalidate
		// the cached info
	}

	/**
	 * Sets the y coordinate of this component.
	 * @param y the y coordinate
	 */
	public void setY(int y) {
		this.y = y;
		// moving the component does not invalidate
		// the cached info
	}

	/**
	 * Sets the width of this component.
	 * @param width the width
	 */
	public void setWidth(int width) {
		// make sure we flag bounds increases
		if (this.width < width) {
			this.boundsChangeType = BoundsChangeType.INCREASED;
		} else if (this.width > width) {
			this.boundsChangeType = BoundsChangeType.DECREASED;
		}
		this.width = width;
		this.cachedScaledImage = null;
		this.setDirty(true);
	}
	
	/**
	 * Sets the height of this component.
	 * @param height the height
	 */
	public void setHeight(int height) {
		// make sure we flag bounds increases
		if (this.height < height) {
			this.boundsChangeType = BoundsChangeType.INCREASED;
		} else if (this.height > height) {
			this.boundsChangeType = BoundsChangeType.DECREASED;
		}
		this.height = height;
		this.cachedScaledImage = null;
		this.setDirty(true);
	}
	
	/**
	 * Returns a new rectangle object that encloses the bounds for this component.
	 * @return Rectangle
	 */
	public Rectangle getBounds() {
		return new Rectangle(this.x, this.y, this.width, this.height);
	}
	
	/**
	 * Returns the x coordinate.
	 * @return int
	 */
	public int getX() {
		return this.x;
	}
	
	/**
	 * Returns the y coordinate.
	 * @return int
	 */
	public int getY() {
		return this.y;
	}
	
	/**
	 * Returns the width.
	 * @return int
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns the height.
	 * @return int
	 */
	public int getHeight() {
		return this.height;
	}
	
	// color
	
	/**
	 * Returns the color.
	 * @return Color
	 */
	public Color getBackgroundColor() {
		return this.backgroundColor;
	}

	/**
	 * Sets the color.
	 * @param color the color
	 */
	public void setBackgroundColor(Color color) {
		this.backgroundColor = color;
		this.colorUpdateRequired = true;
		this.setDirty(true);
	}

	/**
	 * Returns the color composite type.
	 * @return {@link CompositeType}
	 */
	public CompositeType getBackgroundColorCompositeType() {
		return this.backgroundColorCompositeType;
	}
	
	/**
	 * Sets the color composite type.
	 * @param colorCompositeType the composite type
	 */
	public void setBackgroundColorCompositeType(CompositeType colorCompositeType) {
		this.backgroundColorCompositeType = colorCompositeType;
		this.setDirty(true);
	}
	
	/**
	 * Returns true if the color is visible.
	 * @return boolean
	 */
	public boolean isBackgroundColorVisible() {
		return this.backgroundColorVisible;
	}

	/**
	 * Sets the color's visibility.
	 * @param flag true if the color should be visible
	 */
	public void setBackgroundColorVisible(boolean flag) {
		this.backgroundColorVisible = flag;
		this.setDirty(true);
	}
	
	// image
	
	/**
	 * Returns the source image.
	 * @return BufferedImage
	 */
	public BufferedImage getBackgroundImage() {
		return this.backgroundImage;
	}
	
	/**
	 * Sets the source image.
	 * @param image the image
	 */
	public void setBackgroundImage(BufferedImage image) {
		this.backgroundImage = image;
		this.cachedScaledImage = null;
		this.cachedCompositeImage = null;
		this.imageConverted = false;
		this.setDirty(true);
	}

	/**
	 * Returns true if the image is visible.
	 * @return boolean
	 */
	public boolean isBackgroundImageVisible() {
		return this.backgroundImageVisible;
	}

	/**
	 * Sets the image's visibility.
	 * @param flag true if the image should be visible
	 */
	public void setBackgroundImageVisible(boolean flag) {
		this.backgroundImageVisible = flag;
		this.setDirty(true);
	}
	
	/**
	 * Returns the image scale type.
	 * @return {@link ScaleType}
	 */
	public ScaleType getBackgroundImageScaleType() {
		return this.backgroundImageScaleType;
	}
	
	/**
	 * Sets the image scale type.
	 * @param scaleType the scale type
	 */
	public void setBackgroundImageScaleType(ScaleType scaleType) {
		this.backgroundImageScaleType = scaleType;
		this.cachedScaledImage = null;
		this.setDirty(true);
	}
	
	/**
	 * Returns the image scaling quality.
	 * @return {@link ScaleQuality}
	 */
	public ScaleQuality getBackgroundImageScaleQuality() {
		return this.backgroundImageScaleQuality;
	}
	
	/**
	 * Returns the image scaling quality.
	 * @param scaleQuality the scale quality
	 */
	public void setBackgroundImageScaleQuality(ScaleQuality scaleQuality) {
		this.backgroundImageScaleQuality = scaleQuality;
		this.cachedScaledImage = null;
		this.setDirty(true);
	}
}
