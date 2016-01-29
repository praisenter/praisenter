package org.praisenter.slide;

import java.awt.Dimension;

import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.graphics.LineStyle;

public interface SlideRegion {
	public abstract int getX();
	public abstract void setX(int x);
	public abstract int getY();
	public abstract void setY(int y);
	public abstract int getWidth();
	public abstract void setWidth(int width);
	public abstract int getHeight();
	public abstract void setHeight(int height);
	
	public abstract Fill getBackground();
	public abstract void setBackground(Fill background);
	public abstract LineStyle getBorder();
	public abstract void setBorder(LineStyle border);
	
	// other
	
	public abstract void adjust(double pw, double ph);
	public abstract void resize(int dw, int dh);
	
	// transition
	
	public abstract boolean isTransitionRequired(SlideRegion region);
}
