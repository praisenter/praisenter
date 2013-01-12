package org.praisenter.slide.ui.editor;

import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideThumbnail;

/**
 * Represents the result from slide editor.
 * <p>
 * A user can make modifications to a slide or template which update the slide and its thumbnail.
 * In addition, the user can cancel, save or save as a slide or template.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SlideEditorResult {
	/** The user's choice (cancel, save, save as) */
	protected SlideEditorOption choice;
	
	/** The updated slide; null on cancel */
	protected Slide slide;
	
	/** The updated thumbnail; null on cancel */
	protected SlideThumbnail thumbnail;
	
	/**
	 * Returns the user's choice: cancel, save, or save as.
	 * @return {@link SlideEditorOption}
	 */
	public SlideEditorOption getChoice() {
		return this.choice;
	}
	
	/**
	 * Returns the updated slide.
	 * <p>
	 * This will be null if the user canceled the action.
	 * @return {@link Slide}
	 */
	public Slide getSlide() {
		return this.slide;
	}
	
	/**
	 * Returns the updated slide thumbnail.
	 * <p>
	 * This will be null if the user canceled the action.
	 * @return {@link SlideThumbnail}
	 */
	public SlideThumbnail getThumbnail() {
		return this.thumbnail;
	}
}
