package org.praisenter.ui.controls;

import java.util.function.Consumer;

import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;

public final class LastValueNumberStringConverter<E extends Number> extends StringConverter<E> {
	private E lastValue;
	private final StringConverter<E> converter;
	private final Consumer<String> onInvalid;

	public static LastValueNumberStringConverter<Double> forDouble() {
		return LastValueNumberStringConverter.forDouble(null);
	}
	
	public static LastValueNumberStringConverter<Double> forDouble(Consumer<String> onInvalid) {
		return new LastValueNumberStringConverter<>(new DoubleStringConverter(), onInvalid);
	}
	
	public static LastValueNumberStringConverter<Integer> forInteger() {
		return LastValueNumberStringConverter.forInteger(null);
	}
	
	public static LastValueNumberStringConverter<Integer> forInteger(Consumer<String> onInvalid) {
		return new LastValueNumberStringConverter<>(new IntegerStringConverter(), onInvalid);
	}
	
	public static LastValueNumberStringConverter<Long> forLong() {
		return LastValueNumberStringConverter.forLong(null);
	}
	
	public static LastValueNumberStringConverter<Long> forLong(Consumer<String> onInvalid) {
		return new LastValueNumberStringConverter<>(new LongStringConverter(), onInvalid);
	}
	
	private LastValueNumberStringConverter(StringConverter<E> converter, Consumer<String> onInvalid) {
		this.converter = converter;
		this.onInvalid = onInvalid;
	}
	
	@Override
	public String toString(E object) {
		this.lastValue = object;
		return converter.toString(object);
	}

	@Override
	public E fromString(String string) {
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
