package org.praisenter.song;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.xml.adapters.PathXmlAdapter;

@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.NONE)
public final class SongMetadata {
	@XmlAttribute(name = "path")
	@XmlJavaTypeAdapter(value = PathXmlAdapter.class)
	Path path;
	
	@XmlAttribute(name = "dateAdded")
	final Date dateAdded;
	
	@XmlElement(name = "verse")
	final List<VerseMetadata> verses;
	
	public SongMetadata() {
		this.path = null;
		this.dateAdded = new Date();
		this.verses = new ArrayList<VerseMetadata>();
	}

	public SongMetadata(Date dateAdded) {
		this(dateAdded, new ArrayList<VerseMetadata>());
	}
	
	public SongMetadata(Date dateAdded, List<VerseMetadata> verses) {
		this.dateAdded = dateAdded;
		this.verses = verses != null ? verses : new ArrayList<VerseMetadata>();
	}

	public Path getPath() {
		return path;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public List<VerseMetadata> getVerses() {
		return verses;
	}
}
