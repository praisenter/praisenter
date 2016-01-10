package org.praisenter.utility;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import javafx.scene.image.Image;

public final class ClasspathLoader {
	private ClasspathLoader() {}
	
	public static final BufferedImage getBufferedImage(String path) {
		try {
			URL url = ClasspathLoader.class.getResource(path);
			return ImageIO.read(url);
		} catch (IOException e) {
			return null;
		}
	}
	
	public static final Image getImage(String path) {
		return new Image(path, true);
	}
	
	public static final ImageIcon getIcon(String path) {
		try {
			URL url = ClasspathLoader.class.getResource(path);
			BufferedImage image = ImageIO.read(url);
			return new ImageIcon(image);
		} catch (IOException e) {
			return null;
		}
	}
}
