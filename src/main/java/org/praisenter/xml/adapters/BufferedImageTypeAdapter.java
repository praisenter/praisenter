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
package org.praisenter.xml.adapters;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

/**
 * Image type adapter for xml output.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class BufferedImageTypeAdapter extends XmlAdapter<String, BufferedImage> {
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(BufferedImage v) throws Exception {
		return getBase64ImageString(v);
	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public BufferedImage unmarshal(String v) throws Exception {
		return getBase64StringImage(v);
	}
	

	/**
	 * Converts the given image into a base64 encoded string.
	 * @param image the image
	 * @return String
	 * @throws IOException if an exception occurs during write
	 */
	private static final String getBase64ImageString(RenderedImage image) throws IOException {
		String result = null;
		try (ByteArrayOutputStream bo = new ByteArrayOutputStream();
			 Base64OutputStream b64o = new Base64OutputStream(bo, true, 0, null)) {
		
			ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
			ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
			jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			jpgWriteParam.setCompressionQuality(1.0f);
	
			ImageOutputStream outputStream = new MemoryCacheImageOutputStream(b64o); // For example implementations see below
			jpgWriter.setOutput(outputStream);
			IIOImage outputImage = new IIOImage(image, null, null);
			jpgWriter.write(null, outputImage, jpgWriteParam);
			jpgWriter.dispose();
			
			//ImageIO.write(image, "jpg", b64o);
			result = new String(bo.toByteArray());
		}
		return result;
	}
	
	/**
	 * Converts the given base 64 string into a BufferedImage object.
	 * @param string the base 64 string
	 * @return BufferedImage
	 * @throws IOException if an exception occurs reading the image data
	 */
	private static final BufferedImage getBase64StringImage(String string) throws IOException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes());
			 BufferedInputStream bis = new BufferedInputStream(bais);
			 Base64InputStream b64is = new Base64InputStream(bis, false, 0, null)) {
			return ImageIO.read(b64is);
		}
	}
	
}
