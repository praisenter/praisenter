package org.praisenter.slide;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Type adapter for {@link RenderableSlideComponent}s.
 * <p>
 * This adapter is used by JAXB to export/import {@link RenderableSlideComponent}s as {@link AbstractRenderableSlideComponent}s since
 * interfaces cannot be supported by JAXB.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class RenderableSlideComponentTypeAdapter extends XmlAdapter<AbstractRenderableSlideComponent, RenderableSlideComponent> {
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public AbstractRenderableSlideComponent marshal(RenderableSlideComponent v) throws Exception {
		return (AbstractRenderableSlideComponent)v;
	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public RenderableSlideComponent unmarshal(AbstractRenderableSlideComponent v) throws Exception {
		return v;
	}
}
