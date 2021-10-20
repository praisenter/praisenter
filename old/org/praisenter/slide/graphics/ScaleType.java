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
package org.praisenter.slide.graphics;

/**
 * Specifies a scaling method.
 * @author William Bittle
 * @version 3.0.0
 */
public enum ScaleType {
	/** No scaling performed */
	NONE,
	
	/** Uniform scaling will scale to fit the bounds using the largest dimension */
	UNIFORM,
	
	/** Non-uniform scaling will scale to fit the bounds in both dimensions */
	NONUNIFORM;
	
	/**
	 * Returns a rectangle with the position and size based on the target width/height
	 * and this scale type.
	 * @param w the width
	 * @param h the height
	 * @param tw the target width
	 * @param th the target height
	 * @return Rectangle
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
