package org.praisenter.slide;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.praisenter.Constants;
import org.praisenter.json.InstantJsonDeserializer;
import org.praisenter.json.InstantJsonSerializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public final class SlideShow {
	/** The version of the slideshow format */
	public static final String CURRENT_VERSION = "1";
	
	/** The format (for format identification only) */
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	final String format;
	
	/** The slide format version */
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	final String version;

	/** The slide show id */
	@JsonProperty
	private UUID id;
	
	/** The date the slide show was created */
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	Instant createdDate;

	/** The date the slide show was last changed */
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	Instant lastModifiedDate;

	@JsonProperty
	String name;

	@JsonProperty
	boolean loop;

	@JsonProperty
	final List<UUID> slides;
	
	public SlideShow() {
		this(Constants.FORMAT_NAME, SlideShow.CURRENT_VERSION);
	}
	
	@JsonCreator
	private SlideShow(
			@JsonProperty("format") String format, 
			@JsonProperty("version") String version) {
		this.format = format;
		this.version = version;
		this.id = UUID.randomUUID();
		this.name = null;
		this.loop = true;
		this.slides = new ArrayList<UUID>();
	}
	
	public UUID getId() {
		return this.id;
	}
	
	public List<UUID> getSlides() {
		return this.slides;
	}
	
	public boolean loop() {
		return this.loop;
	}
	
	public void setLoop(boolean flag) {
		this.loop = flag;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Instant getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	public void setLastModifiedDate(Instant lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getFormat() {
		return this.format;
	}

	public String getVersion() {
		return this.version;
	}

	public Instant getCreatedDate() {
		return this.createdDate;
	}
}
