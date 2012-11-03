package org.praisenter.xml;

import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.praisenter.utilities.ImageUtilities;

/**
 * Image type adapter for xml output.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BufferedImageTypeAdapter extends XmlAdapter<String, BufferedImage> {
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(BufferedImage v) throws Exception {
		return ImageUtilities.getBase64ImageString(v);
	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public BufferedImage unmarshal(String v) throws Exception {
		return ImageUtilities.getBase64StringImage(v);
	}
}
