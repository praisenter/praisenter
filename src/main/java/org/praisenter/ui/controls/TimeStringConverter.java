package org.praisenter.ui.controls;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.StringConverter;

public final class TimeStringConverter extends StringConverter<Long> {
	private static final Pattern PATTERN = Pattern.compile("^([0-9]+)(:[0-9]+)?$");
	
	private Long lastValue;
	private final Consumer<String> onInvalid;
	
	public TimeStringConverter(Consumer<String> onInvalid) {
		this.onInvalid = onInvalid;
	}
	
	@Override
	public String toString(Long object) {
		this.lastValue = object;
		
		if (object == null) return "00:00";
		
		long value = object.longValue();
		
		if (value <= 0) return "00:00";
		
		long minutes = value / 60;
		long seconds = value % 60;
		
		return (minutes < 10 ? "0" : "") + String.valueOf(minutes) + ":" +
		       (seconds < 10 ? "0" : "") + String.valueOf(seconds);
	}

	@Override
	public Long fromString(String string) {
		if (string != null && !string.isEmpty()) {
			Matcher matcher = PATTERN.matcher(string);
			if (matcher.matches()) {
				String g0 = matcher.group(1);
				String g1 = matcher.group(2);
				
				long total = 0;
				if (g0 != null && !g0.isEmpty()) {
					try {
						total += Long.parseLong(g0);
					} catch (NumberFormatException ex) {
						
					}
				}
				if (g1 != null && !g1.isEmpty()) {
					total *= 60;
					try {
						total += Long.parseLong(g1.replaceAll(":", ""));
					} catch (NumberFormatException ex) {
						
					}
				}
				return total;
			}
		}
		
		this.onInvalid.accept(this.toString(this.lastValue));
		return this.lastValue;
	}
}
