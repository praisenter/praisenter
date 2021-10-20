package org.praisenter.utility;

public final class Scaling {
	// input
	
	public final double originalWidth;
	public final double originalHeight;
	public final double targetWidth;
	public final double targetHeight;
	
	// output
	
	public final double factor;
	public final double factorX;
	public final double factorY;
	public final double x;
	public final double y;
	public final double width;
	public final double height;
	
	private Scaling(
			double originalWidth,
			double originalHeight,
			double targetWidth,
			double targetHeight,
			double factor,
			double factorX,
			double factorY,
			double x, 
			double y,
			double width,
			double height) {
		this.originalWidth = originalWidth;
		this.originalHeight = originalHeight;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
		this.factor = factor;
		this.factorX = factorX;
		this.factorY = factorY;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Scaling[ow=").append(this.originalWidth).append(", ")
		.append("oh=").append(this.originalHeight).append(", ")
		.append("tw=").append(this.targetWidth).append(", ")
		.append("th=").append(this.targetHeight).append(", ")
		.append("f=").append(this.factor).append(", ")
		.append("fx=").append(this.factorX).append(", ")
		.append("fy=").append(this.factorY).append(", ")
		.append("x=").append(this.x).append(", ")
		.append("y=").append(this.y).append(", ")
		.append("w=").append(this.width).append(", ")
		.append("h=").append(this.height).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns a zero {@link Scaling} for the given target width and height.
	 * @param w the target with
	 * @param h the target height
	 * @return {@link Scaling}
	 */
	public static Scaling getNoScaling(double w, double h) {
		return new Scaling(w, h, w, h, 1, 1, 1, 0, 0, w, h);
	}
	
	/**
	 * Returns a uniform {@link Scaling} for the given parameters. 
	 * @param w the width
	 * @param h the height
	 * @param tw the target width
	 * @param th the target height
	 * @return {@link Scaling}
	 */
	public static Scaling getUniformScaling(double w, double h, double tw, double th) {
		return Scaling.getUniformScaling(w, h, tw, th, true, true);
	}
	
	/**
	 * Returns a uniform {@link Scaling} for the given parameters. 
	 * @param w the width
	 * @param h the height
	 * @param tw the target width
	 * @param th the target height
	 * @return {@link Scaling}
	 */
	public static Scaling getUniformScaling(double w, double h, double tw, double th, boolean scaleByWidth, boolean scaleByHeight) {
		double ow = w;
		double oh = h;
		
		// compute the scale factors
		double sw = tw / w;
		double sh = th / h;

		double factor;
		if (scaleByWidth && scaleByHeight) {
			// to scale uniformly we need to 
			// scale by the smallest factor
			if (sw < sh) {
				w = tw;
				h = sw * h;
				factor = sw;
			} else {
				w = sh * w;
				h = th;
				factor = sh;
			}
		} else if (scaleByWidth) {
			w = tw;
			h = sw * h;
			factor = sw;
		} else if (scaleByHeight) {
			w = sh * w;
			h = th;
			factor = sh;
		} else {
			return Scaling.getNoScaling(ow, oh);
		}
		
		// center the image
		double x = (tw - w) / 2.0;
		double y = (th - h) / 2.0;
		
		return new Scaling(ow, oh, tw, th, factor, sw, sh, x, y, w, h);
	}
}
