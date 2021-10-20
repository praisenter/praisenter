package org.praisenter.utility;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import javafx.scene.image.Image;

public final class ClasspathLoader {
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
			if (url == null) return null;
			return ImageIO.read(url);
		} catch (Exception e) {
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
			if (url == null) return null;
			BufferedImage image = ImageIO.read(url);
			return new ImageIcon(image);
		} catch (Exception e) {
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
