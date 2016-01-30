package org.praisenter.slide.graphics;

import org.praisenter.utility.Maf;

public final class SlidePaintStroke implements SlideStroke {
	final SlidePaint paint;
	final SlideStrokeStyle style;
	final double width;
	final double radius;
	
	private SlidePaintStroke() {
		// for jaxb
		this.paint = null;
		this.style = null;
		this.width = 0;
		this.radius = 0;
	}
	
	public SlidePaintStroke(SlidePaint paint, SlideStrokeStyle style, double width, double radius) {
		this.paint = paint;
		this.style = style;
		this.width = width;
		this.radius = radius;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlidePaintStroke) {
			SlidePaintStroke s = (SlidePaintStroke)obj;
			if (!this.paint.equals(s.paint) ||
				!this.style.equals(s.style) ||
				!Maf.equals(this.radius, s.radius) ||
				!Maf.equals(this.width, s.width)) {
				return false;
			}
			return true;
		}
		return false;
	}

	public SlidePaint getPaint() {
		return paint;
	}

	public SlideStrokeStyle getStyle() {
		return style;
	}

	public double getWidth() {
		return width;
	}

	public double getRadius() {
		return radius;
	}
}
