package org.praisenter.javafx.slide;

import java.util.List;

import org.praisenter.slide.Slide;

import javafx.animation.AnimationTimer;
import javafx.animation.ParallelTransition;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;

public final class JavaFxSlide {
	// the slide that was converted
	Slide slide;
	
	// the background node
	Region background;
	
	// the contents of the slide
	List<JavaFxSlideComponent> components;
	
	// players for audio/video
	List<MediaPlayer> players;
	
	// any other playable animations
	ParallelTransition animations;
}
