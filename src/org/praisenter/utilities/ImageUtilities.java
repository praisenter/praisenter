package org.praisenter.utilities;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.codec.binary.Base64OutputStream;

/**
 * Utility class for loading and managing images.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImageUtilities {
	/**
	 * Returns a BufferedImage from the classpath given the path.
	 * @param path the path within the classpath
	 * @return BufferedImage
	 * @throws IOException thrown if an error occurs reading the file
	 */
	public static final BufferedImage getImageFromClassPath(String path) throws IOException {
		URL url = ImageUtilities.class.getResource(path);
		return ImageIO.read(url);
	}
	
	/**
	 * Returns an Icon from the classpath given the path.
	 * @param path the path within the classpath
	 * @return ImageIcon
	 * @throws IOException thrown if an error occurs reading the file
	 */
	public static final ImageIcon getIconFromClassPath(String path) throws IOException {
		BufferedImage bi = ImageUtilities.getImageFromClassPath(path);
		return new ImageIcon(bi);
	}
	
	/**
	 * Returns a BufferedImage from the classpath given the path.
	 * @param path the path within the classpath
	 * @return BufferedImage
	 */
	public static final BufferedImage getImageFromClassPathSuppressExceptions(String path) {
		try {
			return ImageUtilities.getImageFromClassPath(path);
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Returns an Icon from the classpath given the path.
	 * @param path the path within the classpath
	 * @return ImageIcon
	 */
	public static final ImageIcon getIconFromClassPathSuppressExceptions(String path) {
		try {
			BufferedImage bi = ImageUtilities.getImageFromClassPath(path);
			return new ImageIcon(bi);
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Converts the given image into a base64 encoded string.
	 * @param image the image
	 * @return String
	 * @throws IOException if an exception occurs during write
	 */
	public static final String getBase64ImageString(RenderedImage image) throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		Base64OutputStream b64o = new Base64OutputStream(bo);
		ImageIO.write(image, "png", b64o);
		return new String(bo.toByteArray());
	}
	
	/**
	 * Returns a non-uniformly scaled image of the given image.
	 * @param image the image to scale
	 * @param tw the target width
	 * @param th the target height
	 * @param quality the quality as defined by AffineTransformOp
	 * @return BufferedImage
	 */
	public static final BufferedImage getNonUniformScaledImage(BufferedImage image, int tw, int th, int quality) {
		// get the width/height
        int iw = image.getWidth();
        int ih = image.getHeight();
        
	    // get the scaling factors
	    double pw = (double)tw / (double)iw;
		double ph = (double)th / (double)ih;

	    // attempt to resize it
	    AffineTransformOp scale = new AffineTransformOp(AffineTransform.getScaleInstance(pw, ph), quality);
	    return scale.filter(image, null);
	}
	
	/**
	 * Returns a uniformly scaled image of the given image.
	 * @param image the image to scale
	 * @param tw the target width
	 * @param th the target height
	 * @param quality the quality as defined by AffineTransformOp
	 * @return BufferedImage
	 */
	public static final BufferedImage getUniformScaledImage(BufferedImage image, int tw, int th, int quality) {
		// get the width/height
        int iw = image.getWidth();
        int ih = image.getHeight();
        
	    // get the scaling factors
	    double pw = (double)tw / (double)iw;
		double ph = (double)th / (double)ih;
		
		// use uniform scaling
		double s = 1.0;
		if (pw < ph) {
			// the width scaling is more dramatic so use it
			s = pw;
		} else {
			s = ph;
		}
		
	    // attempt to resize it
	    AffineTransformOp scale = new AffineTransformOp(AffineTransform.getScaleInstance(s, s), quality);
	    return scale.filter(image, null);
	}
	
	/**
	 * Returns a non-uniformly scaled image of the given image.
	 * @param image the image to scale
	 * @param tw the target width
	 * @param th the target height
	 * @param quality the quality as defined by AffineTransformOp
	 * @param destination the destination image to draw the scaled image to
	 */
	public static final void drawNonUniformScaledImage(BufferedImage image, int tw, int th, int quality, BufferedImage destination) {
		// get the width/height
        int iw = image.getWidth();
        int ih = image.getHeight();
        
	    // get the scaling factors
	    double pw = (double)tw / (double)iw;
		double ph = (double)th / (double)ih;

	    // attempt to resize it
	    AffineTransformOp scale = new AffineTransformOp(AffineTransform.getScaleInstance(pw, ph), quality);
	    scale.filter(image, destination);
	}
	
	/**
	 * Returns a uniformly scaled image of the given image.
	 * @param image the image to scale
	 * @param tw the target width
	 * @param th the target height
	 * @param quality the quality as defined by AffineTransformOp
	 * @param destination the destination image to draw the scaled image to
	 */
	public static final void drawUniformScaledImage(BufferedImage image, int tw, int th, int quality, BufferedImage destination) {
		// get the width/height
        int iw = image.getWidth();
        int ih = image.getHeight();
        
	    // get the scaling factors
	    double pw = (double)tw / (double)iw;
		double ph = (double)th / (double)ih;
		
		// use uniform scaling
		double s = 1.0;
		if (pw < ph) {
			// the width scaling is more dramatic so use it
			s = pw;
		} else {
			s = ph;
		}
		
	    // attempt to resize it
	    AffineTransformOp scale = new AffineTransformOp(AffineTransform.getScaleInstance(s, s), quality);
	    scale.filter(image, destination);
	}
}
