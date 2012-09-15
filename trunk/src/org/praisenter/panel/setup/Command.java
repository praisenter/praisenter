package org.praisenter.panel.setup;

/**
 * Represents a user command.
 * <p>
 * Classes of this type are used to store state information for complex user
 * interface actions.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Command {
	/** The active flag */
	protected boolean active;
	
	/**
	 * Returns true if the command is currently active.
	 * @return boolean
	 */
	public synchronized boolean isActive() {
		return this.active;
	}
	
	/**
	 * Sets whether the command is active or not.
	 * @param flag true if the command should be flagged as active
	 */
	public synchronized void setActive(boolean flag) {
		this.active = flag;
	}
}
