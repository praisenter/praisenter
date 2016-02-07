package org.praisenter.javafx.slide;

import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;

public final class SlideScene {
	// the root element of the slide
	Region root;
	// for audio/video
	List<MediaPlayer> players;
	// for datetime component, not sure what else yet
	List<AnimationTimer> animations;
}
