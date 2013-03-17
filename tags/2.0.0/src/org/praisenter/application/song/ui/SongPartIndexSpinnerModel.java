/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.application.song.ui;

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
