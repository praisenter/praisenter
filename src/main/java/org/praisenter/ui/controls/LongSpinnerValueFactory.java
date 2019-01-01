package org.praisenter.ui.controls;

import javafx.scene.control.SpinnerValueFactory;

public final class LongSpinnerValueFactory extends SpinnerValueFactory<Long> {
	private final long max;
	private final long min;
	private final long amountToStepBy;
	
	public LongSpinnerValueFactory(long min, long max) {
		this(min, max, min, 1);
	}
	
	public LongSpinnerValueFactory(long min, long max, long defaultValue) {
		this(min, max, defaultValue, 1);
	}
	
	public LongSpinnerValueFactory(long min, long max, long defaultValue, long amountToStepBy) {
		if (min > max) throw new IllegalArgumentException("The min must be smaller than the max.");
		
		this.min = min;
		this.max = max;
		
		this.amountToStepBy = Math.abs(amountToStepBy);
		
		if (defaultValue < min) defaultValue = min;
		if (defaultValue > max) defaultValue = max;
		
		this.setValue(defaultValue);
	}
	
	@Override
	public void decrement(int steps) {
		long value = this.getValue();
		if (value <= this.min) return;
		long diff = steps * this.amountToStepBy;
		value = value - diff;
		if (value <= this.min) value = this.min;
		this.setValue(value);
	}

	@Override
	public void increment(int steps) {
		long value = this.getValue();
		if (value >= this.max) return;
		long diff = steps * this.amountToStepBy;
		value = value + diff;
		if (value >= this.max) value = this.max;
		this.setValue(value);
	}

}
