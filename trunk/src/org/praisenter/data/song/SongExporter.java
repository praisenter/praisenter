/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.data.song;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.praisenter.common.xml.XmlIO;
import org.praisenter.data.DataException;

/**
 * Class used to export a listing of songs.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.1
 */
public final class SongExporter {
	/** Hidden constructor */
	private SongExporter() {}
	
	/**
	 * Exports the given list of songs to the given file in the given format.
	 * @param file the file name and path to write to
	 * @param songs the list of songs to export
	 * @param format the format to export in
	 * @throws DataException thrown if an error occurs during export
	 */
	public static final void exportSongs(String file, List<Song> songs, SongFormat format) throws DataException {
		if (format == SongFormat.PRAISENTER) {
			// create the object to save
			SongList list = new SongList(songs);
			try {
				XmlIO.save(file, list);
			} catch (JAXBException | IOException e) {
				throw new DataException(e);
			}
		} else {
			throw new DataException("SongFormat [" + format + "] is not supported for export.");
		}
	}
}
