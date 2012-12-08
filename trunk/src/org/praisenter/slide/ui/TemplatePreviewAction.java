package org.praisenter.slide.ui;

import org.praisenter.slide.Slide;
import org.praisenter.slide.Template;

/**
 * Stores the data required to execute a preview of the given template.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 * @param <E> the {@link Template} type
 */
public class TemplatePreviewAction<E extends Slide & Template> extends PreviewAction<E> {
	/** The template class */
	public Class<E> clazz;
	
	/**
	 * Minimal constructor.
	 * @param path the template path
	 * @param clazz the template type
	 */
	public TemplatePreviewAction(String path, Class<E> clazz) {
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
