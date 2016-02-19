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
package org.praisenter.utility;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for loading resources off of the classpath.
 * @author William Bittle
 * @version 3.0.0
 */
public final class ClasspathLoader {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** Hidden default constructor */
	private ClasspathLoader() {}
	
	/**
	 * Returns a new BufferedImage for the given path or null
	 * if the path was not found or was not a valid image.
	 * @param path the classpath path
	 * @return BufferedImage
	 */
	public static final BufferedImage getBufferedImage(String path) {
		try {
			URL url = ClasspathLoader.class.getResource(path);
			return ImageIO.read(url);
		} catch (Exception e) {
			LOGGER.warn("Failed to load path '" + path + "' from classpath.", e);
			return null;
		}
	}
	
	/**
	 * Returns a new JavaFX Image for the given path or null
	 * if the path was not found or was not a valid image.
	 * @param path the classpath path
	 * @return Image
	 */
	public static final Image getImage(String path) {
		return new Image(path, true);
	}
	
	/**
	 * Returns a new ImageIcon for the given path or null
	 * if the path was not found or was not a valid image.
	 * @param path the classpath path
	 * @return ImageIcon
	 */
	public static final ImageIcon getIcon(String path) {
		try {
			URL url = ClasspathLoader.class.getResource(path);
			BufferedImage image = ImageIO.read(url);
			return new ImageIcon(image);
		} catch (Exception e) {
			LOGGER.warn("Failed to load path '" + path + "' from classpath.", e);
			return null;
		}
	}
	
	/**
	 * Copies the file at the given classpath path to the given file system path.
	 * @param from the classpath path
	 * @param to the file system path
	 * @throws FileNotFoundException if the file was not found on the classpath
	 * @throws FileAlreadyExistsException if the file at the given system path already exists
	 * @throws IOException if an IO error occurs
	 */
	public static final void copy(String from, Path to) throws FileNotFoundException, FileAlreadyExistsException, IOException {
		// get the classpath resource
		try (InputStream is = ClasspathLoader.class.getResourceAsStream(from)) {
			// see if we found the classpath resource
			if (is == null) {
				throw new FileNotFoundException();
			}
			
			Files.copy(is, to);
		}
	}
}
