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
package org.praisenter.common.utilities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

/**
 * Utility class for loading and managing images.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ImageUtilities {
	/** Hidden default constructor */
	private ImageUtilities() {}
	
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
	 * Converts the given base 64 string into a BufferedImage object.
	 * @param string the base 64 string
	 * @return BufferedImage
	 * @throws IOException if an exception occurs reading the image data
	 */
	public static final BufferedImage getBase64StringImage(String string) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes());
		BufferedInputStream bis = new BufferedInputStream(bais);
		Base64InputStream b64is = new Base64InputStream(bis);
		return ImageIO.read(b64is);
	}
	
	/**
	 * Returns an image with the given image tiled onto it.
	 * @param image the image to tile
	 * @param gc the graphics configuration
	 * @param w the rectangle width
	 * @param h the rectangle height
	 * @return BufferedImage
	 */
	public static final BufferedImage getTiledImage(BufferedImage image, GraphicsConfiguration gc, int w, int h) {
		// create a new image of the right size
		BufferedImage tiled = gc.createCompatibleImage(w, h, Transparency.BITMASK);
		Graphics2D g2d = tiled.createGraphics();
		renderTiledImage(image, g2d, 0, 0, w, h);
		g2d.dispose();
		return tiled;
	}
	
	/**
	 * Tiles the given tile image onto the given surface starting from the given location and filling the given
	 * width and height. 
	 * @param tile the tile to render
	 * @param surface the surface to render to
	 * @param x the starting x position
	 * @param y the starting y position
	 * @param w the rendering width
	 * @param h the rendering height
	 */
	public static final void renderTiledImage(BufferedImage tile, Graphics surface, int x, int y, int w, int h) {
		int tw = tile.getWidth();
		int th = tile.getHeight();
		
		int cx = x;
		int cy = y;
		
		int xn = w / tw + 1;
		int yn = h / th + 1;
		
		surface.clipRect(x, y, w, h);
		// tile the image
		for (int i = 0; i < xn; i++) {
			for (int j = 0; j < yn; j++) {
				surface.drawImage(tile, cx, cy, null);
				cy += th;
			}
			cx += tw;
			cy = y;
		}
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
	 * Returns a drop shadow for the given rectangle specified by the given width and height.
	 * @param gc the graphics configuration
	 * @param w the rectangle width
	 * @param h the rectangle height
	 * @param sw the 
	 * @return BufferedImage
	 */
	public static final BufferedImage getDropShadowImage(GraphicsConfiguration gc, int w, int h, int sw) {
		return getDropShadowImage(gc, w, h, sw, Color.BLACK);
	}
	
	/**
	 * Returns a drop shadow for the given rectangle specified by the given width and height.
	 * @param gc the graphics configuration
	 * @param w the rectangle width
	 * @param h the rectangle height
	 * @param sw the 
	 * @param color the shadow color
	 * @return BufferedImage
	 */
	public static final BufferedImage getDropShadowImage(GraphicsConfiguration gc, int w, int h, int sw, Color color) {
		// create a new image of the right size
		BufferedImage image = gc.createCompatibleImage(w + 2 * sw, h + 2 * sw, Transparency.TRANSLUCENT);
					
		Graphics2D ig2d = image.createGraphics();
		ig2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ig2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		// render the shadow rectangle
		ig2d.setColor(color);
		ig2d.fillRect(sw, sw, w, h);
		ig2d.dispose();
		
		// perform the linear blur
		ConvolveOp op = ImageUtilities.getLinearBlurOp(sw);
		return op.filter(image, null);
	}
	
	/**
	 * Returns a linear blur ConvoleOp.
	 * @param size the blur size
	 * @return ConvolveOp
	 */
	public static final ConvolveOp getLinearBlurOp(int size) {
	    float[] data = new float[size * size];
	    float value = 1.0f / (float) (size * size);
	    for (int i = 0; i < data.length; i++) {
	        data[i] = value;
	    }
	    return new ConvolveOp(new Kernel(size, size, data));
	}
	
	
	/**
	 * Clears the target image and renders the source image to the target image.
	 * <p>
	 * This method uses a 100% transparent color to clear the target image.
	 * @param source the source image
	 * @param target the target image
	 */
	public static final void copyImage(BufferedImage source, BufferedImage target) {
		copyImage(source, target, ColorUtilities.TRANSPARENT);
	}
	
	/**
	 * Clears the target image and renders the source image to the target image.
	 * @param source the source image
	 * @param target the target image
	 * @param clearColor the clear color
	 */
	public static final void copyImage(BufferedImage source, BufferedImage target, Color clearColor) {
		Graphics2D tg2d = target.createGraphics();
		// clear the background
		tg2d.setBackground(clearColor);
		tg2d.clearRect(0, 0, target.getWidth(), target.getHeight());
		// for compatibility for offscreen images we need to do this
		tg2d.setColor(clearColor);
		tg2d.fillRect(0, 0, target.getWidth(), target.getHeight());
		// finally copy the image
		tg2d.drawImage(source, 0, 0, null);
		tg2d.dispose();
	}
	
	/**
	 * Clears the given image.
	 * @param image the image
	 */
	public static final void clearImage(BufferedImage image) {
		clearImage(image, ColorUtilities.TRANSPARENT);
	}
	
	/**
	 * Clears the given image using the given color.
	 * @param image the image
	 * @param clearColor the clear color
	 */
	public static final void clearImage(BufferedImage image, Color clearColor) {
		Graphics2D g2d = image.createGraphics();
		// clear the background
		g2d.setBackground(clearColor);
		g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
		// for compatibility for offscreen images we need to do this
		g2d.setColor(clearColor);
		g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		g2d.dispose();
	}
}
