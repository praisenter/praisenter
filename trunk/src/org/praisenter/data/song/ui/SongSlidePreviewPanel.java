package org.praisenter.data.song.ui;

import java.util.HashMap;
import java.util.Map;

import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.slide.SongSlide;
import org.praisenter.slide.SongSlideTemplate;
import org.praisenter.slide.ui.preview.InlineSlidePreviewPanel;

/**
 * Represents a panel that shows a preview of song slides.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongSlidePreviewPanel extends InlineSlidePreviewPanel {
	/** The version id */
	private static final long serialVersionUID = -6376569581892016128L;
	
	/** A mapping of the song parts to their respective slides */
	private Map<SongPartKey, SongSlide> map;
	
	/**
	 * Default constructor.
	 */
	public SongSlidePreviewPanel() {
		super(10, 5);
		this.map = new HashMap<SongPartKey, SongSlide>();
	}
	
	/**
	 * Sets the currently displayed song.
	 * @param song the song
	 * @param template the song template
	 */
	public void setSong(Song song, SongSlideTemplate template) {
		this.slides.clear();
		if (song != null && template != null) {
			for (SongPart part : song.getParts()) {
				SongSlide slide = template.createSlide();
				slide.setName(part.getName());
				slide.getTextComponent().setText(part.getText());
				slide.getTextComponent().setTextFont(slide.getTextComponent().getTextFont().deriveFont((float)part.getFontSize()));
				this.slides.add(slide);
				this.map.put(new SongPartKey(part.getType(), part.getIndex()), slide);
			}
		}
	}
	
	/**
	 * Returns the slide for the given song part.
	 * <p>
	 * Returns null if the slide does not exist.
	 * @param part the song part
	 * @return {@link SongSlide}
	 */
	public SongSlide getSlide(SongPart part) {
		SongPartKey key = new SongPartKey(part.getType(), part.getIndex());
		return this.getSlide(key);
	}
	
	/**
	 * Returns the slide for the given song part key.
	 * <p>
	 * Returns null if the slide does not exist.
	 * @param key the key
	 * @return {@link SongSlide}
	 */
	public SongSlide getSlide(SongPartKey key) {
		return this.map.get(key);
	}
}
