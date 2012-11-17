package org.praisenter.slide.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
	/** True if looping is enabled */
	@XmlAttribute(name = "LoopEnabled", required = true)
	protected boolean loopEnabled;
	
	/** True if the audio should be muted */
	@XmlAttribute(name = "AudioMuted", required = true)
	protected boolean audioMuted;
	
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
			this.media = (AbstractAudioMedia)MediaLibrary.getMedia(component.media.getFile().getPath(), true);
		} catch (MediaException e) {
			throw new SlideComponentCopyException(e);
		}
		this.loopEnabled = component.loopEnabled;
		this.audioMuted = component.audioMuted;
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
		return -10;
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

	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.PlayableMediaComponent#setLoopEnabled(boolean)
	 */
	@Override
	public void setLoopEnabled(boolean loopEnabled) {
		this.loopEnabled = loopEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.PlayableMediaComponent#isLoopEnabled()
	 */
	@Override
	public boolean isLoopEnabled() {
		return this.loopEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.PlayableMediaComponent#isAudioMuted()
	 */
	public boolean isAudioMuted() {
		return this.audioMuted;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.PlayableMediaComponent#setAudioMuted(boolean)
	 */
	public void setAudioMuted(boolean audioMuted) {
		this.audioMuted = audioMuted;
	}
}
