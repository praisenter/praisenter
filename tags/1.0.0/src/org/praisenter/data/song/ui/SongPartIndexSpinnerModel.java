package org.praisenter.data.song.ui;

import javax.swing.SpinnerNumberModel;

/**
 * Custom spinner model for song part index.
 * <p>
 * Song parts should be unique by their type and index.  This custom model
 * enforces the uniqueness by the use of a excluded indices array.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongPartIndexSpinnerModel extends SpinnerNumberModel {
	/** The version id */
	private static final long serialVersionUID = -3982347423325565776L;
	
	/** The array of excluded indices */
	protected int[] excluded;
	
	/**
	 * Full constructor.
	 * @param index the current index
	 * @param excludedIndices the excluded indices; can be null
	 */
	public SongPartIndexSpinnerModel(int index, int[] excludedIndices) {
		super(index, 1, Integer.MAX_VALUE, 1);
		this.excluded = excludedIndices;
	}
	
	/**
	 * Sets the excluded indices array.
	 * @param excludedIndices the excluded indices
	 */
	public void setExcludedIndices(int[] excludedIndices) {
		this.excluded = excludedIndices;
	}
	
	/**
	 * Returns the excluded indices.
	 * @return int[]
	 */
	public int[] getExcludedIndices() {
		return this.excluded;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.SpinnerNumberModel#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		if (value != null && value instanceof Number) {
			int n = ((Number)value).intValue();
			if (this.isExcluded(n)) {
				// then do nothing but notify that we
				// didn't set the value
				this.fireStateChanged();
				return;
			}
		}
		
		super.setValue(value);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.SpinnerNumberModel#getNextValue()
	 */
	@Override
	public Object getNextValue() {
		// get the current value
		Object value = this.getValue();
		int s = this.getStepSize().intValue();
		int c = ((Number)value).intValue();
		// get the next value
		int n = c + s;
		if (value != null && value instanceof Number) {
			while (true) {
				if (this.isExcluded(n)) {
					n += s;
				} else {
					break;
				}
			}
		}
		
		return n;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.SpinnerNumberModel#getPreviousValue()
	 */
	@Override
	public Object getPreviousValue() {
		// get the current value
		Object value = this.getValue();
		int s = this.getStepSize().intValue();
		int c = ((Number)value).intValue();
		// get the previous value
		int n = c - s;
		if (value != null && value instanceof Number) {
			while (true) {
				// we cannot go past the minimum
				if (n < 1) {
					// if we do, then just return the current value
					n = c;
					break;
				} else if (this.isExcluded(n)) {
					n -= s;
				} else {
					break;
				}
			}
		}
		
		return n;
	}
	
	/**
	 * Returns true if the given number is in the excluded numbers list.
	 * @param n the number to test
	 * @return boolean
	 */
	private boolean isExcluded(int n) {
		if (this.excluded != null) {
			for (int i = 0; i < this.excluded.length; i++) {
				// test if the next value exists in the 
				// excluded list
				if (n == this.excluded[i]) {
					return true;
				}
			}
		}
		return false;
	}
}
