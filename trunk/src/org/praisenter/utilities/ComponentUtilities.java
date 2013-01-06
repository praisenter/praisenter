package org.praisenter.utilities;

import java.awt.Component;
import java.awt.Dimension;

/**
 * Class containing utility methods that deal with ui components.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ComponentUtilities {
	/**
	 * Sets the minimum size of the given components to the maximum size
	 * of the given components.
	 * @param components the components to size
	 */
	public static final void setMinimumSize(Component... components) {
		Dimension size = getMaximumSize(components);
		for (Component component : components) {
			component.setMinimumSize(size);
		}
	}
	
	/**
	 * Returns the maximum preferred size of the given components.
	 * @param components the components
	 * @return Dimension
	 */
	public static final Dimension getMaximumSize(Component... components) {
		Dimension size = new Dimension();
		for (Component component : components) {
			Dimension cSize = component.getPreferredSize();
			if (size.width < cSize.width) {
				size.width = cSize.width;
			}
			if (size.height < cSize.height) {
				size.height = cSize.height;
			}
		}
		return size;
	}
}
