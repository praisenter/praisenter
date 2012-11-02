package org.praisenter.slide.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import org.praisenter.slide.GenericSlideComponent;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.utilities.FontManager;

/**
 * Represents a component that displays text.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
//TODO add text shadow (color, direction, width, visible)
public class TextComponent extends GenericSlideComponent implements SlideComponent, PositionedSlideComponent {
	// TODO change this to a AttributedString so we can support all kinds of string formatting (highlighting, super/sub scripts, etc)
	/** The text */
	protected String text;
	
	// TODO change this to a Paint to allow gradients and other paints
	/** The text color */
	protected Color textColor;
	
	/** The text font */
	protected Font textFont;
	
	/** The horizontal text alignment */
	protected HorizontalTextAlignment horizontalTextAlignment;
	
	/** The vertical text alignment */
	protected VerticalTextAlignment verticalTextAlignment; 
	
	/** The font scale type */
	protected FontScaleType textFontScaleType;
	
	/** True if this text should wrap */
	protected boolean textWrapped;
	
	/** The inner component padding */
	protected int padding;
	
	/**
	 * Minimal constructor.
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public TextComponent(int width, int height) {
		this(0, 0, width, height, null);
	}
	
	/**
	 * Optional constructor.
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public TextComponent(int x, int y, int width, int height) {
		this(x, y, width, height, null);
	}

	/**
	 * Optional constructor.
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @param text the text
	 */
	public TextComponent(int x, int y, int width, int height, String text) {
		super(x, y, width, height);
		this.text = text;
		this.textColor = Color.BLACK;
		this.textFont = null;
		this.horizontalTextAlignment = HorizontalTextAlignment.CENTER;
		this.verticalTextAlignment = VerticalTextAlignment.TOP;
		this.textFontScaleType = FontScaleType.REDUCE_SIZE_ONLY;
		this.textWrapped = true;
		this.padding = 5;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		// render the background/border
		super.render(g);
		// render the text
		this.renderText(g);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {
		// render the background/border
		super.renderPreview(g);
		// render the text
		this.renderText(g);
	}
	
	/**
	 * Renders the text to the given graphics object.
	 * @param g the graphics object to render to
	 */
	protected void renderText(Graphics2D g) {
		if (this.text != null && this.text.trim().length() > 0) {
			// compute the real width
			int rw = this.getTextWidth();
			int rh = this.getTextHeight();

			// set the font
			Font font = this.textFont;
			if (font == null) {
				// if the given font is null then use the default font
				font = FontManager.getDefaultFont();
			}
			
			// make sure the available width and height is greater
			// than zero before trying to fit the text
			if (rw > 0 && rh > 0) {
				// save the old font and color
				Font oFont = g.getFont();
				Color oColor = g.getColor();
				
				String text = this.text;
				// make sure the line break characters are correct
				text = text.replaceAll("(\\r\\n)|(\\r)", String.valueOf(TextRenderer.LINE_SEPARATOR));
				// get the text metrics
				TextMetrics metrics = null;
				// check the font scaling method
				if (this.textFontScaleType == FontScaleType.REDUCE_SIZE_ONLY) {
					// check the wrap flag
					if (this.textWrapped) {
						// get a scaled font size to fit the width and height but is maxed at the current font size
						metrics = TextRenderer.getFittingParagraphMetrics(font, g.getFontRenderContext(), text, rw, rh);
					} else {
						// get a scaled font size to fit the entire line on one line
						metrics = TextRenderer.getFittingLineMetrics(font, g.getFontRenderContext(), text, rw, rh);
					}
				} else if (this.textFontScaleType == FontScaleType.BEST_FIT) {
					// check the wrap flag
					if (this.textWrapped) {
						// get a scaled font size to fit the width and height but is maxed at the current font size
						metrics = TextRenderer.getFittingParagraphMetrics(font, Float.MAX_VALUE, g.getFontRenderContext(), text, rw, rh);
					} else {
						// get a scaled font size to fit the entire line on one line
						metrics = TextRenderer.getFittingLineMetrics(font, Float.MAX_VALUE, g.getFontRenderContext(), text, rw, rh);
					}
				} else {
					// get the bounds without modifying the font size
					TextBounds bounds = null;
					if (this.textWrapped) {
						bounds = TextRenderer.getParagraphBounds(text, font, g.getFontRenderContext(), rw);
					} else {
						bounds = TextRenderer.getLineBounds(font, g.getFontRenderContext(), text);
					}
					metrics = new TextMetrics(font.getSize2D(), bounds.width, bounds.height);
				}
				
				// see if we need to derive the font
				if (font.getSize2D() != metrics.fontSize) {
					g.setFont(font.deriveFont(metrics.fontSize));
				} else {
					g.setFont(font);
				}
				
				// vertical align top is easy, just render at y=0
				float y = 0;
				if (this.verticalTextAlignment == VerticalTextAlignment.CENTER) {
					y = ((float)rh - metrics.height) / 2.0f;
				} else if (this.verticalTextAlignment == VerticalTextAlignment.BOTTOM) {
					y = (float)rh - metrics.height;
				}
				
				// set the text color
				g.setColor(this.textColor);
				if (this.textWrapped) {
					// render the text as a paragraph
					TextRenderer.renderParagraph(g, text, this.horizontalTextAlignment, 0, y, rw);
				} else {
					// render the text as a line
					TextRenderer.renderLine(g, text, this.horizontalTextAlignment, 0, y, rw);
				}
				
				g.setColor(oColor);
				g.setFont(oFont);
			}
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
	}
	
	/**
	 * Returns the horizontal text alignment.
	 * @return {@link HorizontalTextAlignment}
	 */
	public HorizontalTextAlignment getHorizontalTextAlignment() {
		return this.horizontalTextAlignment;
	}
	
	/**
	 * Sets the horizontal text alignment.
	 * @param alignment the alignment
	 */
	public void setHorizontalTextAlignment(HorizontalTextAlignment alignment) {
		this.horizontalTextAlignment = alignment;
	}

	/**
	 * Returns the vertical text alignment.
	 * @return {@link VerticalTextAlignment}
	 */
	public VerticalTextAlignment getVerticalTextAlignment() {
		return this.verticalTextAlignment;
	}
	
	/**
	 * Sets the vertical text alignment.
	 * @param alignment the alignment
	 */
	public void setVerticalTextAlignment(VerticalTextAlignment alignment) {
		this.verticalTextAlignment = alignment;
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
	}
}
