package org.praisenter.slide;

import java.util.List;

import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.graphics.LineStyle;

public abstract class AbstractSlideRegion implements SlideRegion {

	int x;
	int y;
	int width;
	int height;
	
	LineStyle border;
	Fill background;
	
	@Override
	public int getX() {
		return this.x;
	}
	
	@Override
	public void setX(int x) {
		this.x = x;
	}
	
	@Override
	public int getY() {
		return this.y;
	}
	
	@Override
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public int getWidth() {
		return this.width;
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	
	@Override
	public int getHeight() {
		return this.height;
	}
	
	@Override
	public void setHeight(int height) {
		this.height = height;
	}
	
	@Override
	public Fill getBackground() {
		return this.background;
	}
	
	@Override
	public void setBackground(Fill background) {
		this.background = background;
	}
	
	@Override
	public LineStyle getBorder() {
		return this.border;
	}
	
	@Override
	public void setBorder(LineStyle border) {
		this.border = border;
	}
	
	@Override
	public void adjust(double pw, double ph) {
		// adjust width/height
		this.width = (int)Math.floor((double)this.width * pw);
		this.height = (int)Math.floor((double)this.height * ph);
		
		// adjust positioning
		this.x = (int)Math.ceil((double)this.x * pw);
		this.y = (int)Math.ceil((double)this.y * ph);
	}
	
	@Override
	public void resize(int dw, int dh) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isTransitionRequired(SlideRegion region) {
		if (region == null) return true;
		if (region == this) return false;
		
		// check the width and height
		if (this.width != region.getWidth() || this.height != region.getHeight()) {
			return true;
		}
		
		// check the backgrounds
		if (this.background != null && region.getBackground() != null) {
			// if both are visible then compare them
			if (this.backgroundFill != null && component.getBackgroundFill() != null) {
				if (!this.backgroundFill.equals(component.getBackgroundFill())) {
					// the background fills are not the same, so we must transition
					return true;
				}
			} else if (this.backgroundFill != null || component.getBackgroundFill() != null) {
				// if one background is not null, then we must transition
				return true;
			}
		} else if (this.backgroundVisible || component.isBackgroundVisible()) {
			// if both backgrounds are not visible but one is, then we must transition
			return true;
		}
		
		// otherwise they are visually the same so no transition is necessary
		return false;
	}
}
