package org.praisenter.javafx.animation;

import java.text.Collator;

import javafx.scene.image.Image;

public final class AnimationOption implements Comparable<AnimationOption> {
	private static final Collator COLLATOR = Collator.getInstance();
	
	final int id;
	final String name;
	final Image image;
	
	public AnimationOption(int id, String name, Image image) {
		this.id = id;
		this.name = name;
		this.image = image;
	}

	@Override
	public int compareTo(AnimationOption o) {
		return this.id - o.id;
		//return COLLATOR.compare(this.name, o.name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof AnimationOption) {
			AnimationOption o = (AnimationOption)obj;
			return o.id == this.id;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Image getImage() {
		return image;
	}
}
