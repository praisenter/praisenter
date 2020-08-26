package org.praisenter.data.song;

import java.util.Objects;
import java.util.UUID;

import org.praisenter.data.TextType;
import org.praisenter.data.bible.BibleReferenceVerse;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class SongReferenceVerse {
	@JsonProperty
	private final UUID songId;
	
	@JsonProperty
	private final UUID lyricsId;
	
	@JsonProperty
	private final String title;
	
	@JsonProperty
	private final String name;
	
	@JsonProperty
	private final String text;
	
//	@JsonProperty
//	private final int fontSize;

	SongReferenceVerse() {
		this.songId = null;
		this.lyricsId = null;
		this.title = null;
		this.name = null;
		this.text = null;
//		this.fontSize = Verse.USE_TEMPLATE_FONT_SIZE;
	}
	
	public SongReferenceVerse(UUID songId, UUID lyricsId, String title, String name, String text) {
		this.songId = songId;
		this.lyricsId = lyricsId;
		this.title = title;
		this.name = name;
		this.text = text;
//		this.fontSize = verse.fontSize;
	}
	
	public String getText(TextType type) {
		switch (type) {
			case TEXT:
				return this.text;
			case TITLE:
				return this.title;
			default:
				break;
		}
		return null;
	}
	
	public UUID getSongId() {
		return songId;
	}

	public UUID getLyricsId() {
		return lyricsId;
	}
	
	public String getTitle() {
		return title;
	}

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}

//	public int getFontSize() {
//		return fontSize;
//	}

}
