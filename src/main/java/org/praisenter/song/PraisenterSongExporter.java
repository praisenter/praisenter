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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.praisenter.xml.XmlIO;

// FEATURE (L) Allow writing of a zip file with multiple songs in it

/**
 * Class used to export a listing of songs in the current praisenter format.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PraisenterSongExporter implements SongExporter {
	/* (non-Javadoc)
	 * @see org.praisenter.song.SongExporter#write(java.nio.file.Path, java.util.List)
	 */
	@Override
	public void write(Path path, List<Song> songs) throws IOException, SongExportException {
		for (Song song : songs) {
			String title = song.getDefaultTitle();
			String variant = song.variant;
			
			// replace any non-alpha-numeric characters that might not be valid for a file name
			String name = title.replaceAll("\\W+", "") + "_" + variant.replaceAll("\\W+", "");
			Path file = path.resolve(name + ".xml");
			
			// see if it exists
			if (Files.exists(file)) {
				// append a UUID to the name
				file = path.resolve(name + "_" + UUID.randomUUID().toString().replaceAll("-", "") + ".xml");
			}
			
			try {
				XmlIO.save(file, song);
			} catch (JAXBException e) {
				throw new SongExportException(e);
			}
		}
	}
}
