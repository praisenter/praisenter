package org.praisenter.slide.graphics;

public enum ScaleType {
	/** No scaling performed */
	NONE,
	
	/** Uniform scaling will scale the image to fit the bounds using the largest dimension */
	UNIFORM,
	
	/** Non-uniform scaling will scale the image to fit the bounds in both dimensions */
	NONUNIFORM;
	
	/**
	 * Returns a rectangle with the position and size based on the target width/height
	 * and this scale type.
	 * @param w the width
	 * @param h the height
	 * @param tw the target width
	 * @param th the target height
	 * @return Rectangle
	 * @since 2.0.1
	 */
	public Rectangle getScaledDimensions(int w, int h, int tw, int th) {
		// is the image a different size than the target width?
		if (w != tw || h != th) {
			// if so, lets get the scale factors
			double sw = (double)tw / (double)w;
			double sh = (double)th / (double)h;
			if (this == ScaleType.UNIFORM) {
				// if we want to scale uniformly we need to choose
				// the smallest scale factor
				if (sw < sh) {
					w = tw;
					h = (int)Math.ceil(sw * h);
				} else {
					w = (int)Math.ceil(sh * w);
					h = th;
				}
			} else if (this == ScaleType.NONUNIFORM) {
				// for non-uniform scaling we just use
				// the target width and height
				w = tw;
				h = th;
			}
			// center the image
			int x = (tw - w) / 2;
			int y = (th - h) / 2;
			
			return new Rectangle(x, y, w, h);
		} else {
			// if its the same size then dont do anything special
			return new Rectangle(0, 0, w, h);
		}
	}
}
