package org.praisenter.slide.graphics;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Type adapter for the {@link Fill} interface.
 * <p>
 * This adapter is used by JAXB to export/import {@link Fill}s as {@link AbstractFill}s since
 * interfaces cannot be supported by JAXB.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class FillTypeAdapter extends XmlAdapter<AbstractFill, Fill> {
	
	@Override
	public AbstractFill marshal(Fill v) throws Exception {
		return (AbstractFill)v;
	}
	
	@Override
	public Fill unmarshal(AbstractFill v) throws Exception {
		return v;
	}
}
