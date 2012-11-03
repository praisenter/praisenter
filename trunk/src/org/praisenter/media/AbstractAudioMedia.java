package org.praisenter.media;

// FIXME finish; not sure what we need here yet
public abstract class AbstractAudioMedia extends AbstractMedia implements Media, PlayableMedia {
	public AbstractAudioMedia() {
		super(null, MediaType.AUDIO);
	}
}
