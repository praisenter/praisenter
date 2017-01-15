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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.utility.MimeType;
import org.praisenter.xml.XmlIO;

/**
 * A collection of slides that has been created in Praisenter.
 * <p>
 * Obtain a {@link SlideLibrary} instance by calling the {@link #open(Path)}
 * static method. Only one instance should be created for each path. Multiple instances
 * modifying the same path can have unexpected results and can show different sets of slides.
 * <p>
 * This class is intended to be thread safe within this application but can still contend
 * with other programs during disk operations.
 * <p>
 * Since the slides are not bound to any graphics framework, thumbnails must be produced by the
 * caller and given to the {@link SlideLibrary} for saving.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideLibrary {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	// static

	/** The extension to use for the song files */
	private static final String EXTENSION = ".xml";
	
	// instance
	
	/** The root path to the slide library */
	private final Path path;
	
	/** The slides */
	private final Map<UUID, Slide> slides;
	
	/**
	 * Sets up a new {@link SlideLibrary} at the given path.
	 * @param path the root path to the slide library
	 * @return {@link SlideLibrary}
	 * @throws IOException if an IO error occurs
	 */
	public static final SlideLibrary open(Path path) throws IOException {
		SlideLibrary sl = new SlideLibrary(path);
		sl.initialize();
		return sl;
	}
	
	/**
	 * Full constructor.
	 * @param path the path to maintain the slide library
	 * @param thumbnailGenerator the class used to generate thumbnails for slides
	 */
	private SlideLibrary(Path path) {
		this.path = path;
		this.slides = new HashMap<UUID, Slide>();
	}
	
	/**
	 * Performs the initialization required by the slide library.
	 * @throws IOException if an IO error occurs
	 */
	private void initialize() throws IOException {
		// verify paths exist
		Files.createDirectories(this.path);
		
		// index existing documents
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.path)) {
			for (Path file : stream) {
				// only open files
				if (Files.isRegularFile(file)) {
					// only open xml files
					if (MimeType.XML.check(file)) {
						try (InputStream is = Files.newInputStream(file)) {
							try {
								// read in the xml
								BasicSlide slide = XmlIO.read(is, BasicSlide.class);
								slide.setPath(file);
								
								// we can't attempt generating thumbnails at this time
								// since it would rely on the caller's systems to be in place already
								// for example, JavaFX
								
								this.slides.put(slide.getId(), slide);
							} catch (Exception e) {
								LOGGER.warn("Failed to load slide '" + file.toAbsolutePath().toString() + "'", e);
							}
						} catch (IOException ex) {
							LOGGER.warn("Failed to load slide '" + file.toAbsolutePath().toString() + "'", ex);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns all the slides in the library.
	 * @return List&lt;{@link Slide}&gt;
	 */
	public synchronized List<Slide> all() {
		return new ArrayList<Slide>(this.slides.values());
	}
	
	/**
	 * Returns the number of slides in the library.
	 * @return int
	 */
	public synchronized int size() {
		return this.slides.size();
	}
	
	/**
	 * Returns the slide for the given id.
	 * <p>
	 * Returns null if not found or id is null.
	 * @param id the slide id
	 * @return {@link Slide}
	 */
	public synchronized Slide get(UUID id) {
		if (id == null) return null;
		return this.slides.get(id);
	}
	
	/**
	 * Returns true if the given slide id is in the library.
	 * <p>
	 * Returns false if the id is null.
	 * @param id the slide id
	 * @return boolean
	 */
	public synchronized boolean contains(UUID id) {
		if (id == null) return false;
		return this.slides.containsKey(id);
	}

	/**
	 * Returns true if the given slide is in the library.
	 * <p>
	 * Returns false if the slide is null.
	 * @param slide the slide
	 * @return boolean
	 */
	public synchronized boolean contains(Slide slide) {
		if (slide == null || slide.getId() == null) return false;
		return this.slides.containsKey(slide.getId());
	}
	
	/**
	 * Adds the given slide to the slide library.
	 * @param stream the stream of the slide to add to the library
	 * @throws FileNotFoundException if the given path was not found
	 * @throws JAXBException if an error occurs writing the XML
	 * @throws IOException if an IO error occurs
	 * @return {@link Slide}
	 */
	public synchronized Slide add(InputStream stream) throws FileNotFoundException, JAXBException, IOException {
		if (stream == null) {
			return null;
		}
		
		// attempt to read the file
		Slide slide = XmlIO.read(stream, BasicSlide.class);
		slide.setPath(null);
		
		// add it to the library
		this.save(slide);
		
		return slide;
	}
	
	/**
	 * Adds the given slide to the slide library.
	 * @param path the path to the slide to add to the library
	 * @throws FileNotFoundException if the given path was not found
	 * @throws JAXBException if an error occurs writing the XML
	 * @throws IOException if an IO error occurs
	 * @return {@link Slide}
	 */
	public synchronized Slide add(Path path) throws FileNotFoundException, JAXBException, IOException {
		if (path == null) {
			return null;
		}
		
		// attempt to read the file
		Slide slide = XmlIO.read(path, BasicSlide.class);
		slide.setPath(null);
		
		// add it to the library
		this.save(slide);
		
		return slide;
	}
	
	/**
	 * Saves the given slide to the slide library.
	 * @param slide the slide to save
	 * @throws JAXBException if an error occurs writing the XML
	 * @throws IOException if an IO error occurs
	 */
	public synchronized void save(Slide slide) throws JAXBException, IOException {
		if (slide.getPath() == null) {
			String name = createFileName(slide);
			Path path = this.path.resolve(name + EXTENSION);
			// verify there doesn't exist a song with this name already
			if (Files.exists(path)) {
				// just use the UUID
				path = this.path.resolve(slide.getId().toString().replaceAll("-", "") + EXTENSION);
			}
			slide.setPath(path);
		}
		
		// save the song
		XmlIO.save(slide.getPath(), slide);
		
		// make sure the library is updated
		this.slides.put(slide.getId(), slide);
	}
	
	/**
	 * Removes the slide from the library.
	 * @param slide the slide to remove
	 * @throws IOException if and IO error occurs
	 */
	public synchronized void remove(Slide slide) throws IOException {
		if (slide == null) return;
		
		// delete the file
		Path path = slide != null ? slide.getPath() : null;
		if (path != null) {
			Files.deleteIfExists(path);
		}
		
		this.slides.remove(slide.getId());
	}
	
	/**
	 * Removes the slide with the given id from the library.
	 * @param id the slide id
	 * @throws IOException if and IO error occurs
	 */
	public synchronized void remove(UUID id) throws IOException {
		if (id == null) return;
		this.remove(this.get(id));
	}
	
	/**
	 * Creates a file name for the given slide name.
	 * @param slide the slide
	 * @return String
	 */
	public static final String createFileName(Slide slide) {
		String name = slide.getName();
		if (name == null) {
			// just use the id
			name = slide.getId().toString().replaceAll("-", "");
		}
		
		// truncate the name to certain length
		int max = Constants.MAX_FILE_NAME_CODEPOINTS - EXTENSION.length();
		if (name.length() > max) {
			LOGGER.warn("File name too long '{}', truncating.", name);
			name = name.substring(0, Math.min(name.length() - 1, max));
		}
		
		return name;
	}
}
