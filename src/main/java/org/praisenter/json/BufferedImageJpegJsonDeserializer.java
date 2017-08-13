package org.praisenter.json;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64InputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class BufferedImageJpegJsonDeserializer extends JsonDeserializer<BufferedImage> {
	@Override
	public BufferedImage deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		String data = parser.readValueAs(String.class);
		if (data != null) {
			try {
				return getBase64StringImage(data);
			} catch (Exception ex) {
				// TODO handle
			}
		}
		return null;
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
