package org.praisenter.song;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.TextType;

@XmlRootElement(name = "songReferenceVerse")
@XmlAccessorType(XmlAccessType.NONE)
public final class SongReferenceVerse {
	@XmlElement(name = "songId", required = false)
	private final UUID songId;
	
	@XmlElement(name = "title", required = false)
	private final String title;
	
	@XmlElement(name = "name", required = false)
	private final String name;
	
	@XmlElement(name = "text", required = false)
	private final String text;
	
	@XmlElement(name = "musicianText", required = false)
	private final String musicianText;

	@XmlElement(name = "fontSize", required = false)
	private final int fontSize;
	
	SongReferenceVerse() {
		// for jaxb
		this.songId = null;
		this.title = null;
		this.name = null;
		this.text = null;
		this.musicianText = null;
		this.fontSize = -1;
	}
	
	public SongReferenceVerse(UUID songId, String title, String name, int fontSize, String text, String musicianText) {
		this.songId = songId;
		this.title = title;
		this.name = name;
		this.fontSize = fontSize;
		this.text = text;
		this.musicianText = musicianText;
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

	public String getTitle() {
		return title;
	}

	public String getName() {
		return name;
	}

	public int getFontSize() {
		return fontSize;
	}

	public String getText() {
		return text;
	}

	public String getMusicianText() {
		return musicianText;
	}
}
