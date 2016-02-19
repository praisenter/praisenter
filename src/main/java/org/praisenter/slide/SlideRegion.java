package org.praisenter.slide;

import org.praisenter.slide.graphics.Rectangle;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;

public interface SlideRegion {
	static final int MIN_SIZE = 20;
	
	public abstract int getX();
	public abstract void setX(int x);
	public abstract int getY();
	public abstract void setY(int y);
	public abstract int getWidth();
	public abstract void setWidth(int width);
	public abstract int getHeight();
	public abstract void setHeight(int height);
	
	public abstract SlidePaint getBackground();
	public abstract void setBackground(SlidePaint background);
	public abstract SlideStroke getBorder();
	public abstract void setBorder(SlideStroke border);
	
	// other
	
	public abstract void adjust(double pw, double ph);
	public abstract Rectangle resize(int dw, int dh);
	public abstract void translate(int dx, int dy);
	
	// transition
	
	public abstract boolean isTransitionRequired(SlideRegion region);
	
	// copying
	
	public abstract SlideRegion copy();
}
