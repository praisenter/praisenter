package org.praisenter.slide.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.media.AbstractAudioMedia;
import org.praisenter.media.MediaException;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaPlayerListener;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideComponentCopyException;
import org.praisenter.xml.MediaTypeAdapter;

/**
 * Represents a coponent that plays audio.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "AudioMediaComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class AudioMediaComponent implements SlideComponent, MediaComponent<AbstractAudioMedia>, PlayableMediaComponent<AbstractAudioMedia>, MediaPlayerListener {
	/** The media */
	@XmlElement(name = "Media", required = true, nillable = false)
	@XmlJavaTypeAdapter(MediaTypeAdapter.class)
	protected AbstractAudioMedia media;
	
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected AudioMediaComponent() {
		this((AbstractAudioMedia)null);
	}
	
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
	 * @throws SlideComponentCopyException thrown if the media cannot be copied
	 */
	public AudioMediaComponent(AudioMediaComponent component) throws SlideComponentCopyException {
		try {
			this.media = (AbstractAudioMedia)MediaLibrary.getMedia(component.media.getFileProperties().getFilePath(), true);
		} catch (MediaException e) {
			throw new SlideComponentCopyException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#copy()
	 */
	@Override
	public AudioMediaComponent copy() throws SlideComponentCopyException {
		return new AudioMediaComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#getOrder()
	 */
	@Override
	public int getOrder() {
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#setOrder(int)
	 */
	@Override
	public void setOrder(int order) {}
	
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
