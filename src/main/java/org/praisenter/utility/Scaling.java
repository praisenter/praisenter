/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.utility;

/**
 * Represents a the values of scale operation.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Scaling {
	// input
	
	/** The original width */
	public final double originalWidth;
	
	/** The original height */
	public final double originalHeight;
	
	/** The target width */
	public final double targetWidth;
	
	/** The target height */
	public final double targetHeight;
	
	// output
	
	/** The uniform scale factor */
	public final double factor;
	
	/** The scale factor along the x */
	public final double factorX;
	
	/** The scale factor along the y */
	public final double factorY;
	
	/** The scale offset along the x */
	public final double x;
	
	/** The scale offset along the y */
	public final double y;
	
	/** The scaled width */
	public final double width;
	
	/** The scaled height */
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
