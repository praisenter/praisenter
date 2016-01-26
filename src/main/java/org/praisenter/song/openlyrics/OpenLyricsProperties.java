package org.praisenter.song.openlyrics;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "properties")
@XmlAccessorType(XmlAccessType.FIELD)
public final class OpenLyricsProperties {
	@XmlElement(name = "title", required = false)
	@XmlElementWrapper(name = "titles", required = false)
	List<OpenLyricsTitle> titles;
	
	@XmlElement(name = "author", required = false)
	@XmlElementWrapper(name = "authors", required = false)
	List<OpenLyricsAuthor> authors;

	@XmlElement(name = "copyright", required = false)
	String copyright;
	
	@XmlElement(name = "ccliNo", required = false)
	int ccli;
	
	@XmlElement(name = "released", required = false)
	String released;
	
	@XmlElement(name = "transposition", required = false)
	int transposition;
	
	@XmlElement(name = "tempo", required = false)
	OpenLyricsTempo tempo;
	
	@XmlElement(name = "key", required = false)
	String key;
	
	@XmlElement(name = "variant", required = false)
	String variant;
	
	@XmlElement(name = "publisher", required = false)
	String publisher;
	
	@XmlElement(name = "version", required = false)
	String version;
	
	@XmlElement(name = "keywords", required = false)
	String keywords;
	
	@XmlElement(name = "verseOrder", required = false)
	String verseOrder;
	
	@XmlElement(name = "songbook", required = false)
	@XmlElementWrapper(name = "songbooks", required = false)
	List<OpenLyricsSongbook> songbooks;
	
	@XmlElement(name = "theme", required = false)
	@XmlElementWrapper(name = "themes", required = false)
	List<OpenLyricsTheme> themes;
	
	@XmlElement(name = "comment", required = false)
	@XmlElementWrapper(name = "comments", required = false)
	List<OpenLyricsComment> comments;

	public OpenLyricsProperties() {
		this.titles = new ArrayList<OpenLyricsTitle>();
		this.authors = new ArrayList<OpenLyricsAuthor>();
		this.songbooks = new ArrayList<OpenLyricsSongbook>();
		this.themes = new ArrayList<OpenLyricsTheme>();
		this.comments = new ArrayList<OpenLyricsComment>();
	}

	public List<OpenLyricsTitle> getTitles() {
		return titles;
	}

	public void setTitles(List<OpenLyricsTitle> titles) {
		this.titles = titles;
	}

	public List<OpenLyricsAuthor> getAuthors() {
		return authors;
	}

	public void setAuthors(List<OpenLyricsAuthor> authors) {
		this.authors = authors;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public int getCcli() {
		return ccli;
	}

	public void setCcli(int ccli) {
		this.ccli = ccli;
	}

	public String getReleased() {
		return released;
	}

	public void setReleased(String released) {
		this.released = released;
	}

	public int getTransposition() {
		return transposition;
	}

	public void setTransposition(int transposition) {
		this.transposition = transposition;
	}

	public OpenLyricsTempo getTempo() {
		return tempo;
	}

	public void setTempo(OpenLyricsTempo tempo) {
		this.tempo = tempo;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getVerseOrder() {
		return verseOrder;
	}

	public void setVerseOrder(String verseOrder) {
		this.verseOrder = verseOrder;
	}

	public List<OpenLyricsSongbook> getSongbooks() {
		return songbooks;
	}

	public void setSongbooks(List<OpenLyricsSongbook> songbooks) {
		this.songbooks = songbooks;
	}

	public List<OpenLyricsTheme> getThemes() {
		return themes;
	}

	public void setThemes(List<OpenLyricsTheme> themes) {
		this.themes = themes;
	}

	public List<OpenLyricsComment> getComments() {
		return comments;
	}

	public void setComments(List<OpenLyricsComment> comments) {
		this.comments = comments;
	}
}
