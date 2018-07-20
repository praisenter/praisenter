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
package org.praisenter.data.slide.graphics;

import java.util.Arrays;

/**
 * Enumeration of standard dash/line patterns.
 * <p>
 * This enumeration is for ease of use, offering to the user a list of preset patterns
 * rather than them setting each individual property of a pattern.
 * <p>
 * When creating a stroke from a dash pattern, use the {@link #getScaledDashPattern(double)}
 * method to get a dash length array scaled by the line width.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public enum DashPattern {
	/** A solid line */
	SOLID() {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#getDashLengths(double)
		 */
		@Override
		public Double[] getScaledDashPattern(double lineWidth) {
			return lengths;
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#isEqual(java.lang.Double[])
		 */
		@Override
		protected boolean isEqual(Double[] dashLengths) {
			if (dashLengths == null || dashLengths.length == 0) {
				return true;
			}
			return false;
		}
	},
	
	/** A simple dot pattern */
	DOT(1.0, 2.0) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#getDashLengths(double)
		 */
		@Override
		public Double[] getScaledDashPattern(double lineWidth) {
			return new Double[] { this.lengths[0], this.lengths[1] * lineWidth };
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#isEqual(java.lang.Double[])
		 */
		@Override
		protected boolean isEqual(Double[] dashLengths) {
			if (dashLengths == null || dashLengths.length != this.lengths.length) {
				return false;
			}
			// we dont apply scaling to the dots
			if (isEqualTolerance(this.lengths[0], dashLengths[0]) &&
				isEqualTolerance(this.lengths[1], dashLengths[1])) {
				return true;
			}
			return false;
		}
	},
	
	/** A simple dash pattern */
	DASH(3.0, 3.0) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#getDashLengths(double)
		 */
		@Override
		public Double[] getScaledDashPattern(double lineWidth) {
			return new Double[] { this.lengths[0] * lineWidth, this.lengths[1] * lineWidth };
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#isEqual(java.lang.Double[])
		 */
		@Override
		protected boolean isEqual(Double[] dashLengths) {
			if (dashLengths == null || dashLengths.length != this.lengths.length) {
				return false;
			}
			if (isEqualTolerance(this.lengths[0], dashLengths[0]) &&
				isEqualTolerance(this.lengths[1], dashLengths[1])) {
				return true;
			}
			return false;
		}
	},
	
	/** A dash followed by a dot pattern */
	DASH_DOT(3.0, 3.0, 1.0, 3.0) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#getDashLengths(double)
		 */
		@Override
		public Double[] getScaledDashPattern(double lineWidth) {
			return new Double[] { 
					this.lengths[0] * lineWidth, 
					this.lengths[1] * lineWidth,
					this.lengths[2],
					this.lengths[3] * lineWidth };
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#isEqual(java.lang.Double[])
		 */
		@Override
		protected boolean isEqual(Double[] dashLengths) {
			if (dashLengths == null || dashLengths.length != this.lengths.length) {
				return false;
			}
			if (isEqualTolerance(this.lengths[0], dashLengths[0]) &&
				isEqualTolerance(this.lengths[1], dashLengths[1]) &&
				isEqualTolerance(this.lengths[2], dashLengths[2]) &&
				isEqualTolerance(this.lengths[3], dashLengths[3])) {
				return true;
			}
			return false;
		}
	},
	
	/** A longer dash pattern */
	LONG_DASH(8.0, 3.0) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#getDashLengths(double)
		 */
		@Override
		public Double[] getScaledDashPattern(double lineWidth) {
			return new Double[] { 
					this.lengths[0] * lineWidth, 
					this.lengths[1] * lineWidth };
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#isEqual(java.lang.Double[])
		 */
		@Override
		protected boolean isEqual(Double[] dashLengths) {
			if (dashLengths == null || dashLengths.length != this.lengths.length) {
				return false;
			}
			if (isEqualTolerance(this.lengths[0], dashLengths[0]) &&
				isEqualTolerance(this.lengths[1], dashLengths[1])) {
				return true;
			}
			return false;
		}
	},
	
	/** A longer dash dot pattern */
	LONG_DASH_DOT(8.0, 3.0, 1.0, 3.0) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#getDashLengths(double)
		 */
		@Override
		public Double[] getScaledDashPattern(double lineWidth) {
			return new Double[] { 
					this.lengths[0] * lineWidth, 
					this.lengths[1] * lineWidth,
					this.lengths[2],
					this.lengths[3] * lineWidth };
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#isEqual(java.lang.Double[])
		 */
		@Override
		protected boolean isEqual(Double[] dashLengths) {
			if (dashLengths == null || dashLengths.length != this.lengths.length) {
				return false;
			}
			if (isEqualTolerance(this.lengths[0], dashLengths[0]) &&
				isEqualTolerance(this.lengths[1], dashLengths[1]) &&
				isEqualTolerance(this.lengths[2], dashLengths[2]) &&
				isEqualTolerance(this.lengths[3], dashLengths[3])) {
				return true;
			}
			return false;
		}
	},
	
	/** A longer dash dot dot pattern */
	LONG_DASH_DOT_DOT(8.0, 3.0, 1.0, 3.0, 1.0, 3.0) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#getDashLengths(double)
		 */
		@Override
		public Double[] getScaledDashPattern(double lineWidth) {
			return new Double[] { 
					this.lengths[0] * lineWidth, 
					this.lengths[1] * lineWidth,
					this.lengths[2],
					this.lengths[3] * lineWidth,
					this.lengths[4],
					this.lengths[5] * lineWidth};
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.graphics.DashPattern#isEqual(java.lang.Double[])
		 */
		@Override
		protected boolean isEqual(Double[] dashLengths) {
			if (dashLengths == null || dashLengths.length != this.lengths.length) {
				return false;
			}
			if (isEqualTolerance(this.lengths[0], dashLengths[0]) &&
				isEqualTolerance(this.lengths[1], dashLengths[1]) &&
				isEqualTolerance(this.lengths[2], dashLengths[2]) &&
				isEqualTolerance(this.lengths[3], dashLengths[3]) &&
				isEqualTolerance(this.lengths[2], dashLengths[4]) &&
				isEqualTolerance(this.lengths[3], dashLengths[5])) {
				return true;
			}
			return false;
		}
	};
	
	/** The dash lengths */
	protected final Double[] lengths;
	
	/**
	 * Minimal constructor.
	 * @param lengths the dash lengths
	 */
	private DashPattern(Double... lengths) {
		this.lengths = lengths;
	}
	
	/**
	 * Returns the {@link DashPattern} for the given dash lengths.
	 * @param dashLengths the dash lengths
	 * @return {@link DashPattern}
	 */
	public static DashPattern getDashPattern(Double[] dashLengths) {
		for (DashPattern pattern : DashPattern.values()) {
			if (pattern.isEqual(dashLengths)) {
				return pattern;
			}
		}
		return DashPattern.SOLID;
	}
	
	/**
	 * Returns the unscaled dash pattern.
	 * @return Double[]
	 * @see #getScaledDashPattern(double)
	 */
	public Double[] getDashes() {
		return Arrays.copyOf(this.lengths, this.lengths.length);
	}
	
	/**
	 * Returns true if the given dash lengths are equal to this dash pattern.
	 * @param dashLengths the dash lengths
	 * @return boolean
	 */
	protected abstract boolean isEqual(Double[] dashLengths);
	
	/**
	 * Returns a new dash lengths array for the given line width.
	 * @param lineWidth the line width
	 * @return float[]
	 */
	public abstract Double[] getScaledDashPattern(double lineWidth);
	
	/**
	 * Returns true if the given value is between the lower and upper bounds inclusive.
	 * @param value the value
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 * @return boolean
	 */
	private static final boolean isInRange(double value, double lowerBound, double upperBound) {
		if (value >= lowerBound && value <= upperBound) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the given values are equal.
	 * @param value the value
	 * @param test the test value
	 * @return boolean
	 */
	private static final boolean isEqualTolerance(double value, double test) {
		return isInRange(value, test - 0.0001, test + 0.0001);
	}
}
