package org.praisenter.media;

public interface PlayableMediaListener {
	public void started();
	public void updated();
	public void paused();
	public void stopped();
	public void seeked();
}
