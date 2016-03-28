package org.praisenter.javafx.animation;

import java.text.Collator;
import java.util.UUID;

public final class AnimatableObject implements Comparable<AnimatableObject> {
	private static final Collator COLLATOR = Collator.getInstance();
	
	private final UUID objectId;
	private final String name;
	
	public AnimatableObject(UUID objectId, String name) {
		this.objectId = objectId;
		this.name = name;
	}
	
	@Override
	public int compareTo(AnimatableObject o) {
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
		if (obj instanceof AnimatableObject) {
			AnimatableObject o = (AnimatableObject)obj;
			if (this.objectId.equals(o.objectId)) {
				return true;
			}
		}
		return false;
	}

	public UUID getObjectId() {
		return objectId;
	}

	public String getName() {
		return name;
	}
}
