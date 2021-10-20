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
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * Represents an exporter for {@link Slide}s stored within the application.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public interface SlideExporter {
	/**
	 * Exports the given slides to the given path.
	 * <p>
	 * The given path should be a normal file and preferably with a .zip extension.
	 * @param path the path to export to
	 * @param folder the folder in the zip to place the exported slides
	 * @param slides the slides to export
	 * @param shows the slide shows to export
	 * @throws IOException if an IO error occurs
	 */
	public abstract void execute(Path path, String folder, List<Slide> slides, List<SlideShow> shows) throws IOException;
	
	/**
	 * Exports the given slides to the given zip stream.
	 * @param stream the zip stream to export to
	 * @param folder the folder in the zip to place the exported slides
	 * @param slides the slides to export
	 * @param shows the slide shows to export
	 * @throws IOException if an IO error occurs
	 */
	public abstract void execute(ZipOutputStream stream, String folder, List<Slide> slides, List<SlideShow> shows) throws IOException;
}
