package org.praisenter.slide.text;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;

@XmlRootElement(name = "songTextComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class SongTextComponent extends AbstractTextPlaceholderComponent implements SlideRegion, SlideComponent, TextComponent, TextPlaceholderComponent {
	@XmlAttribute(name = "songId", required = false)
	UUID songId;
	
	@XmlAttribute(name = "verseName", required = false)
	String verseName;
	
	public UUID getSongId() {
		return songId;
	}

	public void setSongId(UUID songId) {
		this.songId = songId;
	}

	public String getVerseName() {
		return verseName;
	}

	public void setVerseName(String verseName) {
		this.verseName = verseName;
	}
}
