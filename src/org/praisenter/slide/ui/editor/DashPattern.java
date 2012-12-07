package org.praisenter.slide.ui.editor;

/**
 * Enumeration of standard dash/line patterns.
 * <p>
 * This enumeration is for ease of use, offering to the user a list of preset patterns
 * rather than them setting each individual property of a pattern.
 * <p>
 * When creating a stroke from a dash pattern, use the {@link #getDashLengths(float)}
 * method to get a dash length array scaled by the line width.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum DashPattern {
	/** A solid line */
	SOLID(null) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#getDashLengths(float)
		 */
		@Override
		public float[] getDashLengths(float lineWidth) {
			return null;
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#isEqual(float[], float)
		 */
		@Override
		protected boolean isEqual(float[] dashLengths, float lineWidth) {
			if (dashLengths == null || dashLengths.length == 0) {
				return true;
			}
			return false;
		}
	},
	
	/** A simple dot pattern */
	DOT(1.0f, 2.0f) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#getDashLengths(float)
		 */
		@Override
		public float[] getDashLengths(float lineWidth) {
			return new float[] { this.lengths[0], this.lengths[1] * lineWidth };
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#isEqual(float[], float)
		 */
		@Override
		protected boolean isEqual(float[] dashLengths, float lineWidth) {
			if (dashLengths == null || dashLengths.length != this.lengths.length) {
				return false;
			}
			// we dont apply scaling to the dots
			if (isEqual(this.lengths[0], dashLengths[0]) &&
				isEqual(this.lengths[1], dashLengths[1] / lineWidth)) {
				return true;
			}
			return false;
		}
	},
	
	/** A simple dash pattern */
	DASH(3.0f, 3.0f) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#getDashLengths(float)
		 */
		@Override
		public float[] getDashLengths(float lineWidth) {
			return new float[] { this.lengths[0] * lineWidth, this.lengths[1] * lineWidth };
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#isEqual(float[], float)
		 */
		@Override
		protected boolean isEqual(float[] dashLengths, float lineWidth) {
			if (dashLengths == null || dashLengths.length != this.lengths.length) {
				return false;
			}
			if (isEqual(this.lengths[0], dashLengths[0] / lineWidth) &&
				isEqual(this.lengths[1], dashLengths[1] / lineWidth)) {
				return true;
			}
			return false;
		}
	},
	
	/** A dash followed by a dot pattern */
	DASH_DOT(3.0f, 3.0f, 1.0f, 3.0f) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#getDashLengths(float)
		 */
		@Override
		public float[] getDashLengths(float lineWidth) {
			return new float[] { 
					this.lengths[0] * lineWidth, 
					this.lengths[1] * lineWidth,
					this.lengths[2],
					this.lengths[3] * lineWidth };
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#isEqual(float[], float)
		 */
		@Override
		protected boolean isEqual(float[] dashLengths, float lineWidth) {
			if (dashLengths == null || dashLengths.length != this.lengths.length) {
				return false;
			}
			if (isEqual(this.lengths[0], dashLengths[0] / lineWidth) &&
				isEqual(this.lengths[1], dashLengths[1] / lineWidth) &&
				isEqual(this.lengths[2], dashLengths[2]) &&
				isEqual(this.lengths[3], dashLengths[3] / lineWidth)) {
				return true;
			}
			return false;
		}
	},
	
	/** A longer dash pattern */
	LONG_DASH(8.0f, 3.0f) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#getDashLengths(float)
		 */
		@Override
		public float[] getDashLengths(float lineWidth) {
			return new float[] { 
					this.lengths[0] * lineWidth, 
					this.lengths[1] * lineWidth };
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#isEqual(float[], float)
		 */
		@Override
		protected boolean isEqual(float[] dashLengths, float lineWidth) {
			if (dashLengths == null || dashLengths.length != this.lengths.length) {
				return false;
			}
			if (isEqual(this.lengths[0], dashLengths[0] / lineWidth) &&
				isEqual(this.lengths[1], dashLengths[1] / lineWidth)) {
				return true;
			}
			return false;
		}
	},
	
	/** A longer dash dot pattern */
	LONG_DASH_DOT(8.0f, 3.0f, 1.0f, 3.0f) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#getDashLengths(float)
		 */
		@Override
		public float[] getDashLengths(float lineWidth) {
			return new float[] { 
					this.lengths[0] * lineWidth, 
					this.lengths[1] * lineWidth,
					this.lengths[2],
					this.lengths[3] * lineWidth };
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#isEqual(float[], float)
		 */
		@Override
		protected boolean isEqual(float[] dashLengths, float lineWidth) {
			if (dashLengths == null || dashLengths.length != this.lengths.length) {
				return false;
			}
			if (isEqual(this.lengths[0], dashLengths[0] / lineWidth) &&
				isEqual(this.lengths[1], dashLengths[1] / lineWidth) &&
				isEqual(this.lengths[2], dashLengths[2]) &&
				isEqual(this.lengths[3], dashLengths[3] / lineWidth)) {
				return true;
			}
			return false;
		}
	},
	
	/** A longer dash dot dot pattern */
	LONG_DASH_DOT_DOT(8.0f, 3.0f, 1.0f, 3.0f, 1.0f, 3.0f) {
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#getDashLengths(float)
		 */
		@Override
		public float[] getDashLengths(float lineWidth) {
			return new float[] { 
					this.lengths[0] * lineWidth, 
					this.lengths[1] * lineWidth,
					this.lengths[2],
					this.lengths[3] * lineWidth,
					this.lengths[4],
					this.lengths[5] * lineWidth};
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.slide.ui.editor.DashPattern#isEqual(float[], float)
		 */
		@Override
		protected boolean isEqual(float[] dashLengths, float lineWidth) {
			if (dashLengths == null || dashLengths.length != this.lengths.length) {
				return false;
			}
			if (isEqual(this.lengths[0], dashLengths[0] / lineWidth) &&
				isEqual(this.lengths[1], dashLengths[1] / lineWidth) &&
				isEqual(this.lengths[2], dashLengths[2]) &&
				isEqual(this.lengths[3], dashLengths[3] / lineWidth) &&
				isEqual(this.lengths[2], dashLengths[4]) &&
				isEqual(this.lengths[3], dashLengths[5] / lineWidth)) {
				return true;
			}
			return false;
		}
	};
	
	/** The dash lengths */
	public final float[] lengths;
	
	/**
	 * Minimal constructor.
	 * @param lengths the dash lengths
	 */
	private DashPattern(float... lengths) {
		this.lengths = lengths;
	}
	
	/**
	 * Returns the {@link DashPattern} for the given dash lengths and line width.
	 * <p>
	 * The line width must be supplied if the dash lengths are coming from a BasicStroke
	 * since the {@link #getDashLengths(float)} will scale the dash lengths by the
	 * line width.
	 * @param dashLengths the dash lengths
	 * @param lineWidth the line width
	 * @return {@link DashPattern}
	 */
	public static DashPattern getDashPattern(float[] dashLengths, float lineWidth) {
		for (DashPattern pattern : DashPattern.values()) {
			if (pattern.isEqual(dashLengths, lineWidth)) {
				return pattern;
			}
		}
		return DashPattern.SOLID;
	}
	
	/**
	 * Returns true if the given dash lengths are equal to this dash pattern.
	 * <p>
	 * The line width must be supplied if the dash lengths are coming from a BasicStroke
	 * since the {@link #getDashLengths(float)} will scale the dash lengths by the
	 * line width.
	 * @param dashLengths the dash lengths
	 * @param lineWidth the line width
	 * @return boolean
	 */
	protected abstract boolean isEqual(float[] dashLengths, float lineWidth);
	
	/**
	 * Returns a new dash lengths array for the given line width.
	 * @param lineWidth the line width
	 * @return float[]
	 */
	public abstract float[] getDashLengths(float lineWidth);
	
	/**
	 * Returns true if the given value is between the lower and upper bounds inclusive.
	 * @param value the value
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 * @return boolean
	 */
	private static final boolean isInRange(float value, float lowerBound, float upperBound) {
		if (value >= lowerBound && value <= upperBound) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the given values are equal within the range
	 * [test - 0.5, test + 0.5].
	 * @param value the value
	 * @param test the test value
	 * @return boolean
	 */
	private static final boolean isEqual(float value, float test) {
		return isInRange(value, test - 0.5f, test + 0.5f);
	}
	
//	public float[] getDashs(float width) {
//		float[] d = new float[this.lengths.length];
//		for (int i = 0; i < this.lengths.length; i++) {
//			if (this == DOT && i == 0) {
//				d[i] = this.lengths[i];
//			} else {
//				d[i] = this.lengths[i] * width;
//			}
//		}
//		return d;
//	}
}
