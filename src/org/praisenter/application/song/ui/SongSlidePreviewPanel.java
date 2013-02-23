/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.application.song.ui;

import java.util.HashMap;
import java.util.Map;

import org.praisenter.application.slide.ui.preview.InlineSlidePreviewPanel;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.slide.SongSlide;
import org.praisenter.slide.SongSlideTemplate;

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
				slide.setName(SongHelper.getPartName(part));
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
