/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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
package org.praisenter.javafx.slide.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.Pair;

/**
 * A class containing methods for converting the time format that the user specifies into a printf format.
 * @author William Bittle
 * @version 3.0.0
 */
public final class TimeFormatConverter {
	/** The format mappings */
	private static final List<Pair<String, String>> FORMAT_MAPPING;
	
	static {
		// initialize the format mapping
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
	
	/** Hidden constructor */
	private TimeFormatConverter() {}
	
	/**
	 * Converts the user-defined format into the printf format.
	 * @param format the user-defined format
	 * @return String
	 */
	public static final String toPattern(String format) {
		if (format != null && format.trim().length() > 0) {
			String fmt = format;
			for (Pair<String, String> pair : FORMAT_MAPPING) {
				fmt = fmt.replaceAll(pair.getKey(), Matcher.quoteReplacement(pair.getValue()));
			}
			return fmt;
		}
		return null;
	}
	
	/**
	 * Converts the printf format to the user-defined format.
	 * @param pattern the printf format
	 * @return String
	 */
	public static final String fromPattern(String pattern) {
		if (pattern != null && pattern.trim().length() > 0) {
			String fmt = pattern;
			for (Pair<String, String> pair : FORMAT_MAPPING) {
				fmt = fmt.replaceAll(Pattern.quote(pair.getValue()), pair.getKey());
			}
			return fmt;
		}
		return null;
	}
}
