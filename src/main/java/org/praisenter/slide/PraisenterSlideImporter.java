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
package org.praisenter.slide;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.praisenter.InvalidFormatException;
import org.praisenter.json.JsonIO;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * {@link SlideImporter} for the Praisenter slide format.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PraisenterSlideImporter implements SlideImporter {
	/* (non-Javadoc)
	 * @see org.praisenter.bible.BibleImporter#execute(java.lang.String, java.io.InputStream)
	 */
	@Override
	public List<Slide> execute(String fileName, InputStream stream) throws IOException, InvalidFormatException {
		List<Slide> slides = new ArrayList<Slide>();
		
		try {
			// make a copy to ensure the id is changed
			Slide slide = JsonIO.read(stream, Slide.class).copy(false);
			
			// update the import date
			if (slide instanceof BasicSlide) {
				((BasicSlide)slide).createdDate = Instant.now();
			}
			slides.add(slide);
		} catch (JsonProcessingException ex) {
			throw new InvalidFormatException("Failed to import file '" + fileName + "' as a Praisenter slide file.", ex);
		}
		
		return slides;
	}
}
