package org.praisenter.javafx.transition;

import javafx.scene.layout.Region;
import javafx.util.Duration;

public abstract class ClipTransition extends CustomTransition {
	public ClipTransition(Region node, TransitionType type, Duration duration) {
		super(node, type, duration);
	}
}
