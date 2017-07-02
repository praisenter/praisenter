package org.praisenter.javafx.slide.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.Pair;

public final class TimeFormatConverter {


	private static final List<Pair<String, String>> FORMAT_MAPPING;
	
	
	static {
		FORMAT_MAPPING = new ArrayList<Pair<String, String>>();
		FORMAT_MAPPING.add(new Pair<String, String>("YY", "%1$02d"));
		FORMAT_MAPPING.add(new Pair<String, String>("MM", "%2$02d"));
		FORMAT_MAPPING.add(new Pair<String, String>("DD", "%3$02d"));
		FORMAT_MAPPING.add(new Pair<String, String>("hh", "%4$02d"));
		FORMAT_MAPPING.add(new Pair<String, String>("mm", "%5$02d"));
		FORMAT_MAPPING.add(new Pair<String, String>("ss", "%6$02d"));
		FORMAT_MAPPING.add(new Pair<String, String>("Y", "%1$01d"));
		FORMAT_MAPPING.add(new Pair<String, String>("M", "%2$01d"));
		FORMAT_MAPPING.add(new Pair<String, String>("D", "%3$01d"));
		FORMAT_MAPPING.add(new Pair<String, String>("h", "%4$01d"));
		FORMAT_MAPPING.add(new Pair<String, String>("m", "%5$01d"));
		FORMAT_MAPPING.add(new Pair<String, String>("s", "%6$01d"));
	}
	
	public static final String getFormat(String format) {
		if (format != null && format.trim().length() > 0) {
			return toPattern(format);
		}
		return null;
	}
	
	public static final String toPattern(String format) {
		String fmt = format;
		for (Pair<String, String> pair : FORMAT_MAPPING) {
			fmt = fmt.replaceAll(pair.getKey(), Matcher.quoteReplacement(pair.getValue()));
		}
		return fmt;
	}
	
	public static final String fromPattern(String pattern) {
		String fmt = pattern;
		for (Pair<String, String> pair : FORMAT_MAPPING) {
			fmt = fmt.replaceAll(Pattern.quote(pair.getValue()), pair.getKey());
		}
		return fmt;
	}
}
