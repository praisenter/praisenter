package org.praisenter.ui.controls;

import java.util.function.Consumer;

import javafx.util.StringConverter;

public final class IntegerStringConverter extends StringConverter<Integer> {
	private Integer lastValue;
	private final Consumer<String> onInvalid;
	
	public IntegerStringConverter(Consumer<String> onInvalid) {
		this.onInvalid = onInvalid;
	}
	
	@Override
	public String toString(Integer object) {
		this.lastValue = object;
		
		if (object == null) return "0";
		
		int value = object.intValue();
		
		return String.valueOf(value);
	}

	@Override
	public Integer fromString(String string) {
		if (string != null && !string.isEmpty()) {
			try {
				return Integer.parseInt(string);
			} catch (NumberFormatException ex) {
				
			}
		}
		this.onInvalid.accept(this.toString(this.lastValue));
		return this.lastValue;
	}
}
