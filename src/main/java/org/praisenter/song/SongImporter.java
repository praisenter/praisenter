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
package org.praisenter.song;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.praisenter.InvalidFormatException;

/**
 * Represents an importer of songs in a specified format.
 * @author William Bittle
 * @version 3.0.0
 */
public interface SongImporter {
	/**
	 * Reads the given stream and returns a list of songs in that stream.
	 * @param fileName the file name
	 * @param stream the stream containing the song(s)
	 * @return List&lt;{@link Song}&gt;
	 * @throws IOException if an IO error occurs
	 * @throws InvalidFormatException if the file or files are not in the expected format
	 */
	public abstract List<Song> execute(String fileName, InputStream stream) throws IOException, InvalidFormatException;
	
	/**
	 * Returns the locale for the given language.
	 * <p>
	 * The language should be in the BCP 47 format (xx-YY) using
	 * ISO-639 language codes and ISO-3166-1 country codes.
	 * <p>
	 * At this time variants and other designations not supported.
	 * @param language the language
	 * @return Locale
	 */
	public static Locale getLocale(String language) {
		// converts the language to a locale
		if (language != null && language.length() > 0) {
			String[] parts = language.split("[-]");
			if (parts.length == 1) {
				return new Locale(parts[0]);
			} else if (parts.length == 2) {
				return new Locale(parts[0], parts[1]);
			} else {
				return null;
			}
		}
		return null;
	}
}
