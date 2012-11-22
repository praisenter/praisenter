package org.praisenter.data.song.ui;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.preferences.Preferences;
import org.praisenter.preferences.SongPreferences;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideLibraryException;
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
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SongSlidePreviewPanel.class);
	
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
	 */
	public void setSong(Song song) {
		this.slides.clear();
		
		if (song != null) {
			Preferences preferences = Preferences.getInstance();
			SongPreferences sPreferences = preferences.getSongPreferences();
			
			// get the primary device
			Dimension displaySize = preferences.getPrimaryOrDefaultDeviceResolution();
			
			// get the bible slide template
			SongSlideTemplate template = null;
			String templatePath = sPreferences.getTemplate();
			if (templatePath != null && templatePath.trim().length() > 0) {
				try {
					template = SlideLibrary.getTemplate(templatePath, SongSlideTemplate.class);
				} catch (SlideLibraryException e) {
					LOGGER.error("Unable to load default song template [" + templatePath + "]: ", e);
				}
			}
			if (template == null) {
				// if its still null, then use the default template
				template = SongSlideTemplate.getDefaultTemplate(displaySize.width, displaySize.height);
			}
			
			// check the template size against the display size
			if (template.getWidth() != displaySize.width || template.getHeight() != displaySize.height) {
				// log a message and modify the template to fit
				LOGGER.warn("Template is not sized correctly for the primary display. Adjusing template.");
				template.adjustSize(displaySize.width, displaySize.height);
			}
			
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
