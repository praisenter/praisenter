package org.praisenter.slide;

import java.util.Comparator;

/**
 * Comparator used to sort {@link SlideComponent}s by their z-order.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideComponentOrderComparator implements Comparator<SlideComponent> {
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(SlideComponent o1, SlideComponent o2) {
		return o1.getOrder() - o2.getOrder();
	}
}
