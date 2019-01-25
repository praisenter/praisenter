package org.praisenter.ui.controls;

import javafx.scene.control.SpinnerValueFactory;

public final class IntegerSpinnerValueFactory extends SpinnerValueFactory<Integer> {
	private final int max;
	private final int min;
	private final int amountToStepBy;
	
	public IntegerSpinnerValueFactory(int min, int max) {
		this(min, max, min, 1);
	}
	
	public IntegerSpinnerValueFactory(int min, int max, int defaultValue) {
		this(min, max, defaultValue, 1);
	}
	
	public IntegerSpinnerValueFactory(int min, int max, int defaultValue, int amountToStepBy) {
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
		int value = this.getValue();
		if (value <= this.min) return;
		int diff = steps * this.amountToStepBy;
		value = value - diff;
		if (value <= this.min) value = this.min;
		this.setValue(value);
	}

	@Override
	public void increment(int steps) {
		int value = this.getValue();
		if (value >= this.max) return;
		int diff = steps * this.amountToStepBy;
		value = value + diff;
		if (value >= this.max) value = this.max;
		this.setValue(value);
	}

}
