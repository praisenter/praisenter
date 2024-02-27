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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.json.PraisenterFormat;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.Streams;

/**
 * {@link PraisenterFormatProvider} for the Praisenter format.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public class PraisenterFormatProvider<T extends Persistable> implements ImportExportProvider<T> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	protected final Class<T> clazz;
	
	public PraisenterFormatProvider(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public boolean isSupported(Path path) {
		return this.isSupported(MimeType.get(path));
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		return MimeType.JSON.is(mimeType) || MimeType.ZIP.is(mimeType);
	}
	
	@Override
	public boolean isSupported(String name, InputStream stream) throws IOException {
		if (!stream.markSupported()) {
			LOGGER.warn("Mark is not supported on the given input stream.");
		}
		
		return this.isSupported(MimeType.get(stream, name));
	}
	
	@Override
	public void exp(PersistAdapter<T> adapter, OutputStream stream, T data) throws IOException {
		JsonIO.write(stream, data);
	}
	
	@Override
	public void exp(PersistAdapter<T> adapter, Path path, T data) throws IOException {
		JsonIO.write(path, data);
	}
	
	@Override
	public void exp(PersistAdapter<T> adapter, ZipArchiveOutputStream stream, T data) throws IOException {
		Path path = adapter.getPathResolver().getExportPath(data);
		ZipArchiveEntry entry = new ZipArchiveEntry(FilenameUtils.separatorsToUnix(path.toString()));
		stream.putArchiveEntry(entry);
		JsonIO.write(stream, data);
		stream.closeArchiveEntry();
	}
	
	@Override
	public DataImportResult<T> imp(PersistAdapter<T> adapter, Path path) throws IOException {
		DataImportResult<T> result = new DataImportResult<>();
		
		try (FileInputStream fis = new FileInputStream(path.toFile());
			 BufferedInputStream bis = new BufferedInputStream(fis);) {
			bis.mark(Integer.MAX_VALUE);
			
			LOGGER.trace("Checking stream for zip/json");
			if (MimeType.ZIP.check(path)) {
				LOGGER.trace("Reading as ZIP");
				// NOTE: Native java.util.zip package can't support zips 4GB or bigger or elements 2GB or bigger
		        try (ZipFile zipFile = ZipFile.builder().setPath(path).get()) {
		        	Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
		        	while (entries.hasMoreElements()) {
		        		ZipArchiveEntry entry = entries.nextElement();
		        		
		        		if (entry.isDirectory()) 
		        			continue;
		        		
		        		if (!zipFile.canReadEntryData(entry)) {
		        			LOGGER.warn("Unable to read entry '{}'. This is usually caused by encryption or an unsupported compression algorithm.", entry.getName());
		        			continue;
		        		}
		        		
						// wrap the stream in a stream that can reset
						BufferedInputStream zbis = new BufferedInputStream(zipFile.getInputStream(entry));
						zbis.mark(Integer.MAX_VALUE);
						
						LOGGER.trace("Checking zip entry '{}'", entry.getName());
						if (MimeType.JSON.check(zbis, entry.getName())) {
							zbis.reset();
							
							try {
								LOGGER.trace("Zip entry '{}' is a JSON file, reading and checking format", entry.getName());
								byte[] data = Streams.read(zbis);
								ByteArrayInputStream bais = new ByteArrayInputStream(data);
								PraisenterFormat format = JsonIO.getPraisenterFormat(bais);
								
								if (format != null && format.is(this.clazz)) {
									bais.reset();
									LOGGER.debug("Zip entry '{}' matches the format for '{}', attempting import", entry.getName(), this.clazz.getName());
									T item = JsonIO.read(bais, this.clazz);
									boolean isUpdate = adapter.upsert(item);
									if (isUpdate) {
										result.getUpdated().add(item);
									} else {
										result.getCreated().add(item);
									}
								}
							} catch (Exception ex) {
								LOGGER.trace("Failed to determine if '" + entry.getName() + "' was in the Praisenter json format.", ex);
							}
						}
					}
				}
			} else {
				LOGGER.trace("Reading as JSON");
				
				byte[] data = Streams.read(bis);
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				PraisenterFormat format = JsonIO.getPraisenterFormat(bais);
				
				if (format != null && format.is(this.clazz)) {
					LOGGER.debug("File '{}' matches the format for '{}', attempting import", path.getFileName(), this.clazz.getName());
					T item = JsonIO.read(bis, this.clazz);
					boolean isUpdate = adapter.upsert(item);
					if (isUpdate) {
						result.getUpdated().add(item);
					} else {
						result.getCreated().add(item);
					}
				}
			}
		}
		
		return result;
	}
}
