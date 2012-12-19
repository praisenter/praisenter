package org.praisenter.slide;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Type adapter for {@link RenderableComponent}s.
 * <p>
 * This adapter is used by JAXB to export/import {@link RenderableComponent}s as {@link AbstractRenderableComponent}s since
 * interfaces cannot be supported by JAXB.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class RenderableComponentTypeAdapter extends XmlAdapter<AbstractRenderableComponent, RenderableComponent> {
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public AbstractRenderableComponent marshal(RenderableComponent v) throws Exception {
		return (AbstractRenderableComponent)v;
	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public RenderableComponent unmarshal(AbstractRenderableComponent v) throws Exception {
		return v;
	}
}
