package org.praisenter.javafx.animation;

import java.text.Collator;
import java.util.UUID;

public final class AnimatedObject implements Comparable<AnimatedObject> {
	private static final Collator COLLATOR = Collator.getInstance();
	
	private final UUID objectId;
	private final AnimatedObjectType type;
	private final String name;
	
	public AnimatedObject(UUID objectId, AnimatedObjectType type, String name) {
		this.objectId = objectId;
		this.type = type;
		this.name = name;
	}
	
	@Override
	public int compareTo(AnimatedObject o) {
		return COLLATOR.compare(this.name, o.name);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public int hashCode() {
		return this.objectId.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof AnimatedObject) {
			AnimatedObject o = (AnimatedObject)obj;
			if (this.objectId.equals(o.objectId)) {
				return true;
			}
		}
		return false;
	}

	public UUID getObjectId() {
		return objectId;
	}

	public AnimatedObjectType getType() {
		return type;
	}

	public String getName() {
		return name;
	}
}
