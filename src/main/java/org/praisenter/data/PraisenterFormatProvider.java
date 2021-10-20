/*
 * Copyright (c) 2015-2021 William Bittle  http://www.praisenter.org/
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
package org.praisenter.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.json.PraisenterFormat;
import org.praisenter.utility.MimeType;

/**
 * {@link PraisenterFormatProvider} for the Praisenter format.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class PraisenterFormatProvider<T> implements DataFormatProvider<T> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final Class<T> clazz;
	
	public PraisenterFormatProvider(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public boolean isSupported(Path path) {
		if (MimeType.JSON.check(path)) {
			try {
				PraisenterFormat format = JsonIO.getPraisenterFormat(path);
				if (format.is(this.clazz)) {
					return true;
				}
			} catch (Exception ex) {
				LOGGER.trace("Failed to determine if '" + path.toAbsolutePath() + "' was in the Praisenter json format.", ex);
			}
		}
		return false;
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		return MimeType.JSON.is(mimeType) || mimeType.toLowerCase().startsWith("text");
	}
	
	@Override
	public boolean isSupported(String resourceName, InputStream stream) throws IOException {
		if (stream == null) return false;
		if (!stream.markSupported()) {
			LOGGER.warn("Mark is not supported on the given input stream.");
		}
		try {
			PraisenterFormat format = JsonIO.getPraisenterFormat(stream);
			if (format != null && format.is(this.clazz)) {
				return true;
			}
		} catch (Exception ex) {
			LOGGER.trace("Failed to determine if '" + resourceName + "' was in the Praisenter json format.", ex);
		}
		return false;
	}
	
	@Override
	public List<DataReadResult<T>> read(Path path) throws IOException {
		List<DataReadResult<T>> results = new ArrayList<>();
		T data = JsonIO.read(path, this.clazz);
		results.add(new DataReadResult<T>(data));
		return results;
	}
	
	@Override
	public List<DataReadResult<T>> read(String resourceName, InputStream stream) throws IOException {
		T data = JsonIO.read(stream, this.clazz);
		List<DataReadResult<T>> results = new ArrayList<>();
		results.add(new DataReadResult<T>(data));
		return results;
	}
	
	@Override
	public void write(OutputStream stream, T data) throws IOException {
		JsonIO.write(stream, data);
	}
	
	@Override
	public void write(Path path, T data) throws IOException {
		JsonIO.write(path, data);
	}
}
