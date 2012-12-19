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
		boolean r1 = o1 instanceof RenderableComponent;
		boolean r2 = o2 instanceof RenderableComponent;
		if (r1 && r2) {
			RenderableComponent rc1 = (RenderableComponent)o1;
			RenderableComponent rc2 = (RenderableComponent)o2;
			return rc1.getOrder() - rc2.getOrder();
		} else if (r1) {
			// the non-renderable slide component should be first in the list
			return 1;
		} else if (r2) {
			// the non-renderable slide component should be first in the list
			return -1;
		} else {
			// return they are equal because they have no ordering
			return 0;
		}
	}
}
