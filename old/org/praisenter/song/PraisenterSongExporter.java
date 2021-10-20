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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.praisenter.Constants;
import org.praisenter.data.json.JsonIO;
import org.praisenter.utility.StringManipulator;

/**
 * Class used to export a listing of songs in the current praisenter format.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PraisenterSongExporter implements SongExporter {
	/* (non-Javadoc)
	 * @see org.praisenter.song.SongExporter#execute(java.nio.file.Path, java.util.List)
	 */
	@Override
	public void execute(Path path, List<Song> songs) throws IOException {
		Map<String, Integer> names = new HashMap<String, Integer>(); 
		try (FileOutputStream fos = new FileOutputStream(path.toFile());
			 ZipOutputStream zos = new ZipOutputStream(fos)) {
			for (Song song : songs) {
				int n = 1;
				String fileName = StringManipulator.toFileName(song.getDefaultTitle(), song.getId());
				// make sure it's unique
				if (names.containsKey(fileName)) {
					n = names.get(fileName);
					fileName += String.valueOf(++n);
				}
				names.put(fileName, n);
				Song copy = song.copy(true);
				ZipEntry entry = new ZipEntry(fileName + Constants.SONG_FILE_EXTENSION);
				zos.putNextEntry(entry);
				JsonIO.write(zos, copy);
				zos.closeEntry();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.song.SongExporter#execute(java.util.zip.ZipOutputStream, java.lang.String, java.util.List)
	 */
	@Override
	public void execute(ZipOutputStream stream, String folder, List<Song> songs) throws IOException {
		String root = "";
		if (folder != null) {
			root = folder + "/";
		}
		
		Map<String, Integer> names = new HashMap<String, Integer>();
		for (Song song : songs) {
			int n = 1;
			String fileName = StringManipulator.toFileName(song.getDefaultTitle(), song.getId());
			// make sure it's unique
			if (names.containsKey(fileName)) {
				n = names.get(fileName);
				fileName += String.valueOf(++n);
			}
			names.put(fileName, n);
			Song copy = song.copy(true);
			ZipEntry entry = new ZipEntry(root + fileName + Constants.SONG_FILE_EXTENSION);
			stream.putNextEntry(entry);
			JsonIO.write(stream, copy);
			stream.closeEntry();
		}
	}
}
