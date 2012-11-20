package org.praisenter.easings;

/**
 * Represents an abstract easing function.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractEasing implements Easing {
	/** The easing name */
	protected String name;
	
	/**
	 * Minimal constructor.
	 * @param name the easing name
	 */
	public AbstractEasing(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getName()
	 */
	public String getName() {
		return this.name;
	}
}
