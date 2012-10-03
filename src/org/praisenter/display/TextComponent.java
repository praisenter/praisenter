package org.praisenter.display;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Represents a component that displays text.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
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
	// TODO move padding to graphics component
	protected int padding;
	// TODO add text shadow (color, direction, width, visible)
	/** True if this text component is a placeholder for some text */
	protected boolean placeholder;
	
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
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.DisplayComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D graphics) {
		boolean dirty = this.isDirty();
		
		// render the super class
		//	1. this will reset the dirty flag
		//	2. this will regenerate the cached image
		super.render(graphics);
		
		// see if the component has changed or the cached image is null
		if (dirty) {
			// get the graphics
			Graphics2D g2d = this.cachedImage.createGraphics();
			
			// setup the render quality to as high as possible
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			
			// get the x and y render locations
			int x = this.padding;
			int y = this.padding;
			
			// compute the real width
			int bw = this.width - 2;
			int bh = this.height - 2;
			int rw = bw - this.padding * 2;
			int rh = bh - this.padding * 2;
			
			// set the font
			Font font = this.textFont;
			if (font == null) {
				// if the given font is null then use the current font
				// on the given graphics object
				font = graphics.getFont();
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
						size = TextRenderer.getFittingParagraphFontSize(font, g2d.getFontRenderContext(), text, rw, rh);
					} else {
						// get a scaled font size to fit the entire line on one line
						size = TextRenderer.getFittingLineFontSize(font, g2d.getFontRenderContext(), text, rw, rh);
					}
					g2d.setFont(font.deriveFont(size));
				} else if (this.textFontScaleType == FontScaleType.BEST_FIT) {
					// check the wrap flag
					float size = 0.0f;
					if (this.textWrapped) {
						// get a scaled font size to fit the width and height but is maxed at the current font size
						size = TextRenderer.getFittingParagraphFontSize(font, Float.MAX_VALUE, g2d.getFontRenderContext(), text, rw, rh);
					} else {
						// get a scaled font size to fit the entire line on one line
						size = TextRenderer.getFittingLineFontSize(font, Float.MAX_VALUE, g2d.getFontRenderContext(), text, rw, rh);
					}
					g2d.setFont(font.deriveFont(size));
				} else {
					g2d.setFont(font);
				}
				
				// turn on text anti-aliasing
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				
				// we need to set the clip here so that if the cachedImage is actually larger
				// than this component, that the text doesn't spill over
				g2d.setClip(x, y, rw, rh);
				// set the text color
				g2d.setColor(this.textColor);
				if (this.textWrapped) {
					// render the text as a paragraph
					TextRenderer.renderParagraph(g2d, text, this.textAlignment, x, y, rw);
				} else {
					// render the text as a line
					TextRenderer.renderLine(g2d, text, this.textAlignment, x, y, rw);
				}
			}
			
			// let go of the graphics
			g2d.dispose();
			this.setDirty(false);
		}
		
		// check if we should render this component
		if (this.visible) {
			// draw the cached image
			graphics.drawImage(this.cachedImage, this.x, this.y, null);
		}
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
