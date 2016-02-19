package org.praisenter.slide;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.object.MediaObject;

@XmlRootElement(name = "mediaComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class MediaComponent extends AbstractSlideComponent implements SlideRegion, SlideComponent {
	@XmlElement(name = "media", required = false)
	MediaObject media;

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#copy()
	 */
	@Override
	public MediaComponent copy() {
		MediaComponent comp = new MediaComponent();
		// copy the super classes stuff
		this.copy(comp);
		// a shallow copy should work here
		comp.setMedia(this.media);
		return comp;
	}
	
	public MediaObject getMedia() {
		return this.media;
	}

	public void setMedia(MediaObject media) {
		this.media = media;
	}
}
