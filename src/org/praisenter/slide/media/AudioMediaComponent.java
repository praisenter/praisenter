package org.praisenter.slide.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.media.AbstractAudioMedia;
import org.praisenter.media.MediaPlayerListener;
import org.praisenter.slide.SlideComponent;
import org.praisenter.xml.MediaTypeAdapter;

/**
 * Represents a coponent that plays audio.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
// TODO add ability to fade out audio when a slide transitions
@XmlRootElement(name = "AudioMediaComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class AudioMediaComponent implements SlideComponent, MediaComponent<AbstractAudioMedia>, PlayableMediaComponent<AbstractAudioMedia>, MediaPlayerListener {
	/** The name of the component */
	@XmlElement(name = "Name", required = true, nillable = false)
	protected String name;

	/** The media */
	@XmlElement(name = "Media", required = true, nillable = false)
	@XmlJavaTypeAdapter(MediaTypeAdapter.class)
	protected AbstractAudioMedia media;
	
	/** True if looping is enabled */
	@XmlAttribute(name = "LoopEnabled", required = true)
	protected boolean loopEnabled;
	
	/** True if the audio should be muted */
	@XmlAttribute(name = "AudioMuted", required = true)
	protected boolean audioMuted;
	
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected AudioMediaComponent() {
		this(null, null);
	}
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param media the media
	 */
	public AudioMediaComponent(String name, AbstractAudioMedia media) {
		this.name = name;
		this.media = media;
	}
	
	/**
	 * Copy constructor.
	 * @param component the component to copy
	 */
	public AudioMediaComponent(AudioMediaComponent component) {
		this.name = component.name;
		this.media = component.media;
		this.loopEnabled = component.loopEnabled;
		this.audioMuted = component.audioMuted;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#copy()
	 */
	@Override
	public AudioMediaComponent copy() {
		return new AudioMediaComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#getName()
	 */
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
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
