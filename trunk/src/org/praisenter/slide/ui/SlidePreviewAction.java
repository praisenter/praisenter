package org.praisenter.slide.ui;

import org.praisenter.slide.Slide;

/**
 * Stores the data required to execute a preview of the given slide.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 * @param <E> the {@link Slide} type
 */
public class SlidePreviewAction<E extends Slide> extends PreviewAction<E> {
	/** The slide class type */
	public Class<E> clazz;
	
	/**
	 * Minimal constructor.
	 * @param path the slide path
	 * @param clazz the slide type
	 */
	public SlidePreviewAction(String path, Class<E> clazz) {
		super(path);
		this.clazz = clazz;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.PreviewAction#getSlideClass()
	 */
	@Override
	public Class<E> getSlideClass() {
		return clazz;
	}
}
