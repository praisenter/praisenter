package org.praisenter.ui.controls;

import java.util.function.Consumer;

import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

public final class LastValueDoubleStringConverter extends StringConverter<Double> {
	private Double lastValue;
	private final DoubleStringConverter converter = new DoubleStringConverter();
	private final Consumer<String> onInvalid;

	public LastValueDoubleStringConverter() {
		this(null);
	}
	
	public LastValueDoubleStringConverter(Consumer<String> onInvalid) {
		this.onInvalid = onInvalid;
	}
	
	@Override
	public String toString(Double object) {
		this.lastValue = object;
		return converter.toString(object);
	}

	@Override
	public Double fromString(String string) {
		if (string == null || string.isEmpty()) {
			if (this.onInvalid != null) {
				this.onInvalid.accept(this.converter.toString(this.lastValue));
			}
			return this.lastValue;
		}
		try {
			return converter.fromString(string);
		} catch (NumberFormatException ex) {
			if (this.onInvalid != null) {
				this.onInvalid.accept(this.converter.toString(this.lastValue));
			}
			return this.lastValue;
		}
	}

}
