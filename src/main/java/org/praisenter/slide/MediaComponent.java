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

	public MediaObject getMedia() {
		return media;
	}

	public void setMedia(MediaObject media) {
		this.media = media;
	}
}
