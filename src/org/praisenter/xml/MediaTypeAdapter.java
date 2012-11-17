package org.praisenter.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;

/**
 * Represents an XML adapter for {@link Media} objects.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MediaTypeAdapter extends XmlAdapter<String, Media> {
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Media v) throws Exception {
		return v.getFile().getPath();
	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Media unmarshal(String v) throws Exception {
		 return MediaLibrary.getMedia(v);
	}
}
