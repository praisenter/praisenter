package org.praisenter.media;

public abstract class AbstractAudioMedia extends AbstractMedia implements Media, PlayableMedia {
	public AbstractAudioMedia() {
		super(MediaType.AUDIO);
	}
}
