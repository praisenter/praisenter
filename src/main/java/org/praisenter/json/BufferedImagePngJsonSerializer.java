package org.praisenter.json;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64OutputStream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BufferedImagePngJsonSerializer extends JsonSerializer<BufferedImage> {
	@Override
	public void serialize(BufferedImage value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		generator.writeString(getBase64ImageString(value));
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
			ImageIO.write(image, "png", b64o);
			result = new String(bo.toByteArray());
		}
		return result;
	}
}
