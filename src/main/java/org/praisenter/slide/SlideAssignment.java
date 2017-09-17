package org.praisenter.slide;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class SlideAssignment implements Serializable {
	/** The serialization id */
	private static final long serialVersionUID = -4601551952701263378L;

	@JsonProperty
	private final UUID id;
	
	@JsonProperty
	private final UUID slideId;
	
	public SlideAssignment(UUID slideId) {
		this.id = UUID.randomUUID();
		this.slideId = slideId;
	}

	public UUID getId() {
		return this.id;
	}

	public UUID getSlideId() {
		return this.slideId;
	}
}
