package org.praisenter.slide.media;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.media.AbstractAudioMedia;
import org.praisenter.slide.SlideComponent;

/**
 * Represents a coponent that plays audio.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "AudioMediaComponent")
public class AudioMediaComponent implements SlideComponent, MediaComponent<AbstractAudioMedia>, TimedMediaComponent<AbstractAudioMedia> {
	/** The media */
	@XmlElement(name = "Media", required = true, nillable = false)
	protected AbstractAudioMedia media;
	
	/**
	 * Minimal constructor.
	 * @param media the media
	 */
	public AudioMediaComponent(AbstractAudioMedia media) {
		this.media = media;
	}
	
	/**
	 * Copy constructor.
	 * @param component the component to copy
	 */
	public AudioMediaComponent(AudioMediaComponent component) {
		this.media = component.media;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#copy()
	 */
	@Override
	public AudioMediaComponent copy() {
		// TODO Auto-generated method stub
		return new AudioMediaComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.MediaComponent#getMedia()
	 */
	@Override
	public AbstractAudioMedia getMedia() {
		return this.media;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.MediaComponent#setMedia(org.praisenter.media.Media)
	 */
	@Override
	public void setMedia(AbstractAudioMedia media) {
		this.media = media;
	}
}
