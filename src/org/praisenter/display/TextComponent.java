package org.praisenter.display;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.praisenter.utilities.ColorUtilities;
import org.praisenter.utilities.FontManager;

/**
 * Represents a component that displays text.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
//TODO add text shadow (color, direction, width, visible)
public class TextComponent extends GraphicsComponent {
	/** The text */
	protected String text;
	
	/** The text color */
	protected Color textColor;
	
	/** The text font */
	protected Font textFont;
	
	/** The text alignment */
	protected TextAlignment textAlignment;
	
	/** The font scale type */
	protected FontScaleType textFontScaleType;
	
	/** True if this text should wrap */
	protected boolean textWrapped;
	
	/** The inner component padding */
	protected int padding;
	
	/** True if this text component is a placeholder for some text */
	protected boolean placeholder;
	
	// caching
	
	/** True if the cached text image needs to be updated */
	private boolean textUpdateRequired;
	
	/** The cached text image */
	private BufferedImage cachedTextImage;
	
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param text the text
	 * @param width the width of the component
	 * @param height the height of the component
	 * @param placeholder true if this component is a placeholder
	 */
	public TextComponent(String name, String text, int width, int height, boolean placeholder) {
		super(name, 0, 0, width, height);
		
		this.text = text;
		this.textColor = Color.BLACK;
		this.textFont = null;
		this.textAlignment = TextAlignment.CENTER;
		this.textFontScaleType = FontScaleType.REDUCE_SIZE_ONLY;
		this.textWrapped = true;
		this.padding = 5;
		this.placeholder = placeholder;
		
		this.textUpdateRequired = true;
		this.cachedTextImage = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.GraphicsComponent#renderComponent(java.awt.Graphics2D)
	 */
	@Override
	protected void renderComponent(Graphics2D graphics) {
		// render the super class stuff
		super.renderComponent(graphics);
		
		// render the text stuff
		int w = this.getTextWidth();
		int h = this.getTextHeight();
		Shape clip = graphics.getClip();
		// we need to set the clip here so that if the cachedImage is actually larger
		// than this component, that the text doesn't spill over
		graphics.setClip(this.padding, this.padding, w, h);
		// draw the image
		graphics.drawImage(this.cachedTextImage, this.padding, this.padding, null);
		// reset the clip
		graphics.setClip(clip);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.GraphicsComponent#invalidate()
	 */
	@Override
	public void invalidate() {
		super.invalidate();
		
		this.textUpdateRequired = true;
		this.cachedTextImage = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.GraphicsComponent#cacheImages(java.awt.GraphicsConfiguration, org.praisenter.display.BoundsChangeType)
	 */
	@Override
	protected void cacheImages(GraphicsConfiguration configuration, BoundsChangeType boundsChangeType) {
		// cache any super class stuff
		super.cacheImages(configuration, boundsChangeType);
		// cache the text stuff
		this.cacheTextImage(configuration, boundsChangeType);
	}
	
	/**
	 * Caches the text rendering.
	 * @param configuration the graphics configuration
	 * @param boundsChangeType the type of change to the bounds; null if no change
	 */
	protected void cacheTextImage(GraphicsConfiguration configuration, BoundsChangeType boundsChangeType) {
		boolean boundsUpdate = false;
		if (this.textFontScaleType == FontScaleType.NONE) {
			boundsUpdate = boundsChangeType == BoundsChangeType.INCREASED || boundsChangeType == BoundsChangeType.CHANGED;
		} else {
			boundsUpdate = boundsChangeType != null;
		}
		
		// see if we need to re-render the text image
		if (this.cachedTextImage == null || this.textUpdateRequired || boundsUpdate) {
			// compute the real width
			int rw = this.getTextWidth();
			int rh = this.getTextHeight();
			
			// generate the cached text image if its null or the bounds have increased
			if (this.cachedTextImage == null || boundsChangeType == BoundsChangeType.INCREASED || boundsChangeType == BoundsChangeType.CHANGED) {
				// this makes it to where we only re-create the cached text image when the bounds have increased
				this.cachedTextImage = configuration.createCompatibleImage(rw, rh, Transparency.TRANSLUCENT);
			}
			// get the image graphics
			Graphics2D ig = this.cachedTextImage.createGraphics();
			
			// set the render quality
			DisplayComponent.setRenderQuality(ig);
			
			// clear the image
			ig.setBackground(ColorUtilities.TRANSPARENT);
			ig.clearRect(0, 0, rw, rh);
			
			// set the font
			Font font = this.textFont;
			if (font == null) {
				// if the given font is null then use the default font
				font = FontManager.getDefaultFont();
			}
			
			// make sure the available width and height is greater
			// than zero before trying to fit the text
			if (rw > 0 && rh > 0 && this.text != null && !this.text.isEmpty()) {
				String text = this.text;
				// make sure the line break characters are correct
				text = text.replaceAll("(\\r\\n)|(\\r)", String.valueOf(TextRenderer.LINE_SEPARATOR));
				// check the font scaling method
				if (this.textFontScaleType == FontScaleType.REDUCE_SIZE_ONLY) {
					// check the wrap flag
					float size = 0.0f;
					if (this.textWrapped) {
						// get a scaled font size to fit the width and height but is maxed at the current font size
						size = TextRenderer.getFittingParagraphFontSize(font, ig.getFontRenderContext(), text, rw, rh);
					} else {
						// get a scaled font size to fit the entire line on one line
						size = TextRenderer.getFittingLineFontSize(font, ig.getFontRenderContext(), text, rw, rh);
					}
					ig.setFont(font.deriveFont(size));
				} else if (this.textFontScaleType == FontScaleType.BEST_FIT) {
					// check the wrap flag
					float size = 0.0f;
					if (this.textWrapped) {
						// get a scaled font size to fit the width and height but is maxed at the current font size
						size = TextRenderer.getFittingParagraphFontSize(font, Float.MAX_VALUE, ig.getFontRenderContext(), text, rw, rh);
					} else {
						// get a scaled font size to fit the entire line on one line
						size = TextRenderer.getFittingLineFontSize(font, Float.MAX_VALUE, ig.getFontRenderContext(), text, rw, rh);
					}
					ig.setFont(font.deriveFont(size));
				} else {
					ig.setFont(font);
				}
				
				// set the text color
				ig.setColor(this.textColor);
				if (this.textWrapped) {
					// render the text as a paragraph
					TextRenderer.renderParagraph(ig, text, this.textAlignment, 0, 0, rw);
				} else {
					// render the text as a line
					TextRenderer.renderLine(ig, text, this.textAlignment, 0, 0, rw);
				}
			}
			
			ig.dispose();
			this.textUpdateRequired = false;
		}
	}
	
	/**
	 * This returns the available width to render the text.
	 * @return int
	 */
	protected int getTextWidth() {
		return this.width - this.padding * 2 - 2;
	}
	
	/**
	 * This returns the available height to render the text.
	 * @return int
	 */
	protected int getTextHeight() {
		return this.height - this.padding * 2 - 2;
	}
	
	/**
	 * Returns the text of this text component.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * Sets the text of this component.
	 * @param text the text
	 */
	public void setText(String text) {
		this.text = text;
		this.textUpdateRequired = true;
		this.setDirty(true);
	}
	
	/**
	 * Returns the text color.
	 * @return Color
	 */
	public Color getTextColor() {
		return this.textColor;
	}
	
	/**
	 * Sets the text color.
	 * @param color the text color
	 */
	public void setTextColor(Color color) {
		this.textColor = color;
		this.textUpdateRequired = true;
		this.setDirty(true);
	}
	
	/**
	 * Returns the text font.
	 * @return Font
	 */
	public Font getTextFont() {
		return this.textFont;
	}
	
	/**
	 * Sets the text font.
	 * @param font the font
	 */
	public void setTextFont(Font font) {
		this.textFont = font;
		this.textUpdateRequired = true;
		this.setDirty(true);
	}
	
	/**
	 * Returns the text alignment.
	 * @return {@link TextAlignment}
	 */
	public TextAlignment getTextAlignment() {
		return this.textAlignment;
	}
	
	/**
	 * Sets the text alignment.
	 * @param alignment the alignment
	 */
	public void setTextAlignment(TextAlignment alignment) {
		this.textAlignment = alignment;
		this.textUpdateRequired = true;
		this.setDirty(true);
	}
	
	/**
	 * Returns the component padding.
	 * @return int
	 */
	public int getPadding() {
		return this.padding;
	}
	
	/**
	 * Sets the component padding.
	 * @param padding the padding
	 */
	public void setPadding(int padding) {
		this.padding = padding;
		this.textUpdateRequired = true;
		this.setDirty(true);
	}
	
	/**
	 * Returns the font scale type.
	 * @return {@link FontScaleType}
	 */
	public FontScaleType getTextFontScaleType() {
		return this.textFontScaleType;
	}
	
	/**
	 * Sets the font scale type.
	 * @param fontScaleType the font scale type
	 */
	public void setTextFontScaleType(FontScaleType fontScaleType) {
		this.textFontScaleType = fontScaleType;
		this.textUpdateRequired = true;
		this.setDirty(true);
	}
	
	/**
	 * Returns true if this text wraps.
	 * @return boolean
	 */
	public boolean isTextWrapped() {
		return this.textWrapped;
	}
	
	/**
	 * Sets the text to wrap inside this component.
	 * @param flag true to wrap the text
	 */
	public void setTextWrapped(boolean flag) {
		this.textWrapped = flag;
		this.textUpdateRequired = true;
		this.setDirty(true);
	}
	
	/**
	 * Returns true if this component is a placeholder.
	 * @return boolean
	 */
	public boolean isPlaceholder() {
		return this.placeholder;
	}
}
