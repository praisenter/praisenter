package org.praisenter.slide.ui;

import org.praisenter.slide.Slide;
import org.praisenter.slide.Template;

/**
 * Stores the data required to execute a preview of the given slide/template.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 * @param <E> the {@link Slide}/{@link Template} type
 */
public abstract class PreviewAction<E extends Slide> {
	/** The path to the slide/template */
	public String path;
	
	/**
	 * Minimal constructor.
	 * @param path the slide/template path
	 */
	public PreviewAction(String path) {
		this.path = path;
	}
	
	/**
	 * Returns the slide/template type.
	 * @return Class&lt;E&gt;
	 */
	public abstract Class<E> getSlideClass();
}
