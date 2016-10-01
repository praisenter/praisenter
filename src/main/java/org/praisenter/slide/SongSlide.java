package org.praisenter.slide;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

// TODO the song will also need to be exported with the slide if transported to another Praisenter instance 

@XmlRootElement(name = "songSlide")
@XmlAccessorType(XmlAccessType.NONE)
public final class SongSlide extends TemplatedSlide implements Slide, SlideRegion {
	// the properties describing the verse selected
	
	/** The song id */
	@XmlElement(name = "songId", required = false)
	UUID songId;
	
	/** The verse name */
	@XmlElement(name = "verse", required = false)
	String verse;
	
	// Note: the order should be preserved so we shouldn't have to know
	// which element applies to the primary, secondary, etc. we can
	// just assume it by index in the list
	// Note: if the list is empty, the default lyrics should be used
	@XmlElementWrapper(name = "lyricsets", required = false)
	@XmlElement(name = "lyrics", required = false)
	final List<SongSlideLyrics> lyrics;
	
	// constructors
	
	public SongSlide() {
		this(null);
	}
	
	public SongSlide(BasicSlide slide) {
		this(slide, null, null, null);
	}
	
	public SongSlide(BasicSlide slide, UUID songId, String verse, List<SongSlideLyrics> lyrics) {
		// set the template id
		super(getRootTemplateId(slide));
		// copy the slide to this slide
		if (slide != null) {
			slide.copy((Slide)this);
		}
		// set the song fields
		this.songId = songId;
		this.verse = verse;
		this.lyrics = lyrics != null ? new ArrayList<SongSlideLyrics>(lyrics) : new ArrayList<SongSlideLyrics>();
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.BasicSlide#copy()
	 */
	@Override
	public SongSlide copy() {
		return new SongSlide(this, this.songId, this.verse, this.lyrics);
	}
	
	// getter/setters
	
	public UUID getSongId() {
		return this.songId;
	}

	public void setSongId(UUID songId) {
		this.songId = songId;
	}

	public String getVerse() {
		return this.verse;
	}

	public void setVerse(String verse) {
		this.verse = verse;
	}

	public List<SongSlideLyrics> getLyrics() {
		return lyrics;
	}
}
