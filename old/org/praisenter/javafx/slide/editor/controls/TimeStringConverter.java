package org.praisenter.javafx.slide.editor.controls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.StringConverter;

public final class TimeStringConverter extends StringConverter<Long> {
	private final Pattern pattern = Pattern.compile("^([0-9]+)(:[0-9]+)?$");
	
	@Override
	public Long fromString(String string) {
		if (string != null && !string.isEmpty()) {
			Matcher matcher = pattern.matcher(string);
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
				return total != 0 ? total : -1l;
			}
		}
		return -1l;
	}
	@Override
	public String toString(Long object) {
		if (object == null) return "";
		long value = object.longValue();
		
		if (value == -1) return "";
		if (value == 0) return "00:00";
		
		long minutes = value / 60;
		long seconds = value % 60;
		
		return (minutes < 10 ? "0" : "") + String.valueOf(minutes) + ":" +
		       (seconds < 10 ? "0" : "") + String.valueOf(seconds);
	}
}
