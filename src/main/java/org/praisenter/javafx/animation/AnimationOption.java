package org.praisenter.javafx.animation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.easing.Easing;

import javafx.scene.image.Image;

public final class AnimationOption implements Comparable<AnimationOption> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Image DEFAULT_IMAGE = new Image("/org/praisenter/resources/org.praisenter.slide.animation.Swap.png");
	
	final Class<?> type;
	final int order;
	final String name;
	final Image image;
	
	public AnimationOption(Class<?> type, int order) {
		this.type = type;
		this.order = order;
		this.name = Translations.get(type.getName());
		Image image = DEFAULT_IMAGE;
		try {
			image = new Image("/org/praisenter/resources/" + type.getName() + ".png");
		} catch (Exception e) {
			// TODO fix
			LOGGER.error(e);
		}
		this.image = image;
	}

	@Override
	public int compareTo(AnimationOption o) {
		return this.order - o.order;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof AnimationOption) {
			AnimationOption o = (AnimationOption)obj;
			return o.type == this.type;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public Class<?> getType() {
		return this.type;
	}
	
	public int getOrder() {
		return this.order;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Image getImage() {
		return this.image;
	}
}
